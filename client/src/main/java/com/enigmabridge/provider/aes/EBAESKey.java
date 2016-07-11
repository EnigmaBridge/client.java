package com.enigmabridge.provider.aes;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBOperationConfiguration;
import com.enigmabridge.UserObjectKeyBase;
import com.enigmabridge.UserObjectType;
import com.enigmabridge.provider.EBKeyBase;
import com.enigmabridge.provider.rsa.EBRSAKey;
import com.enigmabridge.provider.rsa.EBRSAPrivateKey;

import javax.crypto.Cipher;
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
public class EBAESKey extends EBKeyBase implements SecretKey, KeySpec {
    static final long serialVersionUID = 1;

    // Key for inversion operation.
    protected EBAESKey inversionKey;

    public static abstract class AbstractBuilder<T extends EBAESKey, B extends EBAESKey.AbstractBuilder>
            extends EBKeyBase.AbstractBuilder<T,B>
    {
        public B setInversionKey(EBAESKey ikey) {
            getObj().setInversionKey(ikey);
            return getThisBuilder();
        }

        public B setInversionKey(EBAESKey.Builder ikey) {
            getObj().setInversionKey(ikey.getObj());
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBAESKey.AbstractBuilder<EBAESKey, EBAESKey.Builder> {
        private final EBAESKey parent = new EBAESKey();

        @Override
        public EBAESKey.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBAESKey getObj() {
            return parent;
        }

        @Override
        public EBAESKey build() {
            return parent;
        }
    }

    public boolean isEncryptionKey(){
        final int function = getUserObjectType().getUoTypeFunction();
        if (function != UserObjectType.TYPE_PLAINAES && function != UserObjectType.TYPE_PLAINAESDECRYPT){
            throw new IllegalStateException("Key is not for AES");
        }

        return function == UserObjectType.TYPE_PLAINAES;
    }

    public boolean isDecryptionKey(){
        return !isEncryptionKey();
    }

    public EBAESKey getInversionKey() {
        return inversionKey;
    }

    protected EBAESKey setInversionKey(EBAESKey inversionKey) {
        this.inversionKey = inversionKey;
        return this;
    }
}
