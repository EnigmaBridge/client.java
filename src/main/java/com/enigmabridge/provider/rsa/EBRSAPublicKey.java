package com.enigmabridge.provider.rsa;

import com.enigmabridge.EBKeyBase;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;

/**
 * RSA public key in EB.
 * Conforms to general Java Key interface.
 *
 * Inspiration: bcprov-jdk15on-1.54-sources.jar!/org/bouncycastle/jcajce/provider/asymmetric/rsa/BCRSAPublicKey.java
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAPublicKey extends EBRSAKey {
    static final long serialVersionUID = 1;

    @Override
    public BigInteger getModulus() {
        return null;
    }

}
