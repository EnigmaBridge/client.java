package com.enigmabridge.provider.rsa;

/**
 * Non-extractable RSA private key in EB.
 * All private key operations need to be done in EB. Key does not leave EB.
 * Conforms to general Java Key interface.
 *
 * Inspiration: bcprov-jdk15on-1.54-sources.jar!/org/bouncycastle/jcajce/provider/asymmetric/rsa/BCRSAPrivateKey.java
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAPrivateKey extends EBRSAKey {
    static final long serialVersionUID = 1;

    public static class Builder extends EBRSAKey.AbstractBuilder<EBRSAPrivateKey, Builder> {
        private final EBRSAPrivateKey parent = new EBRSAPrivateKey();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBRSAPrivateKey getObj() {
            return parent;
        }

        @Override
        public EBRSAPrivateKey build() {
            return parent;
        }
    }
}
