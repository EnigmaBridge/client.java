package com.enigmabridge.provider.rsa;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBOperationConfiguration;
import com.enigmabridge.UserObjectInfoBase;
import com.enigmabridge.UserObjectKeyBase;
import com.enigmabridge.provider.EBKeyBase;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

/**
 * General RSA key, common for private and public parts.
 * Conforms to general Java Key interface.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAKey extends EBKeyBase implements PrivateKey, RSAKey {
    static final long serialVersionUID = 1;

    /**
     * If null for encryption operation -> operation fails.
     * If null for decryption operation -> operation runs without blinding.
     */
    protected BigInteger publicExponent;
    protected BigInteger modulus;

    public static abstract class AbstractBuilder<T extends EBRSAKey, B extends AbstractBuilder> extends EBKeyBase.AbstractBuilder<T,B>{
        public B setModulus(BigInteger mod) {
            getObj().setModulus(mod);
            return getThisBuilder();
        }

        public B setPublicExponent(BigInteger e) {
            getObj().setPublicExponent(e);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBRSAKey, Builder> {
        private final EBRSAKey parent = new EBRSAKey();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBRSAKey getObj() {
            return parent;
        }

        @Override
        public EBRSAKey build() {
            return parent;
        }
    }

    @Override
    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPublicExponent() {
        return publicExponent;
    }

    protected void setModulus(BigInteger modulus) {
        this.modulus = modulus;
    }

    protected void setPublicExponent(BigInteger publicExponent) {
        this.publicExponent = publicExponent;
    }
}
