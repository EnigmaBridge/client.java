package com.enigmabridge.provider;

import com.enigmabridge.*;
import com.enigmabridge.create.Constants;
import com.enigmabridge.create.EBUOGetTemplateRequest;
import com.enigmabridge.provider.specs.EBSymmetricKeyGenTypes;

import javax.crypto.SecretKey;
import java.security.ProviderException;
import java.security.SecureRandom;

/**
 * EBSymmetricKey factory.
 * Symmetric key usually needs two UOs to perform both encryption and decryption (one for one operation).
 *
 * Created by dusanklinec on 13.07.16.
 */
public class EBSymmetricKeyCreator {
    protected final UserObjectKeyCreator.Builder uoKeyCreatorBld = new UserObjectKeyCreator.Builder();

    protected EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;
    protected UserObjectKeyCreator uoKeyCreator;

    private EBCommKeys commKeysEnc;
    private EBCommKeys commKeysDec;

    public static abstract class AbstractBuilder<T extends EBSymmetricKeyCreator, B extends EBSymmetricKeyCreator.AbstractBuilder>
    {
        public B setRandom(SecureRandom random) {
            getObj().uoKeyCreatorBld.setRandom(random);
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine) {
            getObj().uoKeyCreatorBld.setEngine(engine);
            return getThisBuilder();
        }

        public B setGetTemplateRequest(EBUOGetTemplateRequest getTemplateRequest) {
            getObj().uoKeyCreatorBld.setGetTemplateRequest(getTemplateRequest);
            return getThisBuilder();
        }

        public B setUoType(UserObjectType uoType) {
            getObj().uoKeyCreatorBld.setUoType(uoType);
            return getThisBuilder();
        }

        public B setKeyType(EBSymmetricKeyGenTypes keyType){
            getObj().keyType = keyType;
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBSymmetricKeyCreator, EBSymmetricKeyCreator.Builder> {
        private final EBSymmetricKeyCreator parent = new EBSymmetricKeyCreator();

        @Override
        public EBSymmetricKeyCreator.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBSymmetricKeyCreator getObj() {
            return parent;
        }

        @Override
        public EBSymmetricKeyCreator build() {
            final EBSymmetricKeyCreator obj = getObj();

            // Engine has to be set.
            obj.uoKeyCreator = obj.uoKeyCreatorBld.build();

            return parent;
        }
    }

    // Build logic
    public EBSymmetricKeyCreator setCommKeys(boolean forEncryption, byte[] encKey, byte[] macKey){
        if (forEncryption) {
            this.commKeysEnc = new EBCommKeys(encKey, macKey);
        } else {
            this.commKeysDec = new EBCommKeys(encKey, macKey);
        }
        return this;
    }

    public EBSymmetricKeyCreator setCommKeys(boolean forEncryption, EBCommKeys commKeys){
        if (forEncryption) {
            this.commKeysEnc = commKeys;
        } else {
            this.commKeysDec = commKeys;
        }
        return this;
    }

    public EBSymmetricKeyCreator setAppKey(byte[] appKey){
        uoKeyCreator.setAppKey(appKey);
        return this;
    }

    public EBSymmetricKeyCreator setAppKeyGeneration(int appKeyGeneration){
        this.uoKeyCreator.setAppKeyGeneration(appKeyGeneration);
        return this;
    }

    public EBSymmetricKeyCreator setUoTypeFunction(int typeFunction){
        this.uoKeyCreator.setUoTypeFunction(typeFunction);
        return this;
    }

    /**
     * Generates UO key in the EB with randomly generated comm keys.
     *
     * @param forEncryption true if this should be encryption key
     * @return EBSymmetricKey.Builder
     */
    protected EBSymmetricKey.Builder generateKey(boolean forEncryption){
        uoKeyCreator.setCommKeys(getCommKeys(forEncryption));
        final UserObjectType uoType = uoKeyCreator.getUoType();
        final int functionType = uoType.isCipherObject(forEncryption) ?
                uoType.getUoTypeFunction() :
                uoType.getInversionUoTypeFunction();

        uoKeyCreator.setUoTypeFunction(functionType);
        try {
            final UserObjectKeyBase.Builder keyBld = uoKeyCreator.create();
            final EBSymmetricKey.Builder bld = new EBSymmetricKey.Builder()
                    .setUo(keyBld.build())
                    .setEngine(uoKeyCreator.getEngine());

            return bld;

        } catch (Exception e) {
            throw new ProviderException("Create Symmetric key failed", e);
        }
    }

    public SecretKey engineGenerateKey() {
        EBSymmetricKey.Builder decKey = null;
        EBSymmetricKey.Builder encKey = null;

        // Create decrypt.
        if (keyType == EBSymmetricKeyGenTypes.BOTH || keyType == EBSymmetricKeyGenTypes.DECRYPT){
            decKey = generateKey(false);
        }

        // Create encrypt.
        if (keyType == EBSymmetricKeyGenTypes.BOTH || keyType == EBSymmetricKeyGenTypes.ENCRYPT){
            encKey = generateKey(true);
        }

        // If pair was generated - chain them.
        if (keyType == EBSymmetricKeyGenTypes.BOTH){
            decKey.setInversionKey(encKey);
            final EBSymmetricKey decKeyFinal = decKey.build();

            encKey.setInversionKey(decKeyFinal);
            return decKeyFinal;

        } else if (keyType == EBSymmetricKeyGenTypes.DECRYPT){
            return decKey.build();

        } else {
            return encKey.build();
        }
    }

    // Getters

    protected EBCommKeys getCommKeys(boolean forEncryption){
        return forEncryption ? commKeysEnc : commKeysDec;
    }

    public EBSymmetricKeyGenTypes getKeyType() {
        return keyType;
    }
}
