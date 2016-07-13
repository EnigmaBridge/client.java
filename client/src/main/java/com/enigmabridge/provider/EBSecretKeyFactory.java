package com.enigmabridge.provider;

import com.enigmabridge.*;
import com.enigmabridge.create.Constants;
import com.enigmabridge.provider.specs.EBSecretKeySpec;
import com.enigmabridge.provider.specs.EBSymmetricKeyGenTypes;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * SecretKeyFactory translates between KeySpecs and Keys.
 * In our case it helps to createUO from the existing key - function very similar to KeyGenerator.
 *
 * Reversed process - creating KeySpecs from the Key is not possible as the keys are not extractable.
 *
 * Created by dusanklinec on 12.07.16.
 */
public class EBSecretKeyFactory extends SecretKeyFactorySpi {
    protected final EnigmaProvider provider;
    protected final EBSymmetricKeyCreator.Builder keyCreatorBld = new EBSymmetricKeyCreator.Builder();

    protected EBSymmetricKeyCreator keyCreator;
    protected EBEngine engine;
    protected SecureRandom random;
    private EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    public EBSecretKeyFactory(EnigmaProvider provider) {
        this.provider = provider;
    }

    public EBSecretKeyFactory(EnigmaProvider provider, String algorithm) {
        this.provider = provider;
    }

    @Override
    protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
        if(keySpec == null) {
            throw new InvalidKeySpecException("KeySpec must not be null");

        } else if(keySpec instanceof EBSecretKeySpec){
            final EBSecretKeySpec ebSpec = (EBSecretKeySpec) keySpec;
            initFromSpecs(ebSpec);

            if (ebSpec.getTemplateRequest() != null){
                keyCreatorBld.setGetTemplateRequest(ebSpec.getTemplateRequest());
            }

        } else if(keySpec instanceof SecretKeySpec) {
            initFromSpecs(null);

        } else {

            throw new InvalidKeySpecException("Unsupported spec: " + keySpec.getClass().getName());
        }

        final SecretKeySpec secKeySpec = (SecretKeySpec) keySpec;
        final String algorithm = secKeySpec.getAlgorithm();
        if (!"AES".equalsIgnoreCase(algorithm)){
            throw new InvalidKeySpecException("Unsupported key: " + algorithm);
        }

        keyCreatorBld.setEngine(this.engine)
                .setRandom(this.random)
                .setKeyType(keyType)
                .setUoType(UserObjectType.OBJ_PLAINAES);

        keyCreator = keyCreatorBld.build();
        keyCreator
                .setUoTypeFunction(UserObjectType.TYPE_PLAINAES)
                .setAppKey(secKeySpec.getEncoded())
                .setAppKeyGeneration(Constants.GENKEY_CLIENT);

        return keyCreator.engineGenerateKey();
    }

    protected void initFromSpecs(final EBSecretKeySpec ebSpec){
        if (ebSpec != null && ebSpec.getEBEngine() != null){
            this.engine = ebSpec.getEBEngine();
        }

        if (this.engine == null){
            this.engine = provider.getEngine();
        }

        if (this.random == null && this.engine != null){
            this.random = this.engine.getRnd();
        }

        if (this.random == null){
            this.random = new SecureRandom();
        }

        if (ebSpec != null){
            this.keyType = ebSpec.getKeyType();
        }
    }

    @Override
    protected KeySpec engineGetKeySpec(SecretKey secretKey, Class aClass) throws InvalidKeySpecException {
        throw new UnsupportedOperationException("EB provider does not allow to extract keys");
    }

    @Override
    protected SecretKey engineTranslateKey(SecretKey secretKey) throws InvalidKeyException {
        if (secretKey instanceof SecretKeySpec) {
            try {
                return engineGenerateSecret((SecretKeySpec) secretKey);
            } catch (InvalidKeySpecException e) {
                throw new InvalidKeyException("Cannot translate provided key", e);
            }

        } else {
            throw new UnsupportedOperationException("Unsupported key translation");
        }
    }
}
