package com.enigmabridge.provider;

import com.enigmabridge.UserObjectType;

import javax.crypto.SecretKey;
import java.security.spec.KeySpec;

/**
 * Symmetric secret key is used for both encryption and decryption operation,
 * while EB separates these two operations and separate EB keys (same app key)
 * are used.
 *
 * We can do either a) encapsulate both encrypt and decrypt keys and use appropriate
 * one on init, or b) link inversion key to this one.
 *
 * Created by dusanklinec on 05.07.16.
 */
public class EBSymmetricKey extends EBKeyBase implements SecretKey, KeySpec {
    static final long serialVersionUID = 1;

    // Key for inversion operation.
    protected EBSymmetricKey inversionKey;

    public static abstract class AbstractBuilder<T extends EBSymmetricKey, B extends EBSymmetricKey.AbstractBuilder>
            extends EBKeyBase.AbstractBuilder<T,B>
    {
        public B setInversionKey(EBSymmetricKey ikey) {
            getObj().setInversionKey(ikey);
            return getThisBuilder();
        }

        public B setInversionKey(EBSymmetricKey.Builder ikey) {
            getObj().setInversionKey(ikey.getObj());
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBSymmetricKey.AbstractBuilder<EBSymmetricKey, EBSymmetricKey.Builder> {
        private final EBSymmetricKey parent = new EBSymmetricKey();

        @Override
        public EBSymmetricKey.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBSymmetricKey getObj() {
            return parent;
        }

        @Override
        public EBSymmetricKey build() {
            return parent;
        }
    }

    public boolean isEncryptionKey(){
        return getUserObjectType().isEncryptionObject();
    }

    public boolean isDecryptionKey(){
        return getUserObjectType().isDecryptionObject();
    }

    public EBSymmetricKey getInversionKey() {
        return inversionKey;
    }

    protected EBSymmetricKey setInversionKey(EBSymmetricKey inversionKey) {
        this.inversionKey = inversionKey;
        return this;
    }
}
