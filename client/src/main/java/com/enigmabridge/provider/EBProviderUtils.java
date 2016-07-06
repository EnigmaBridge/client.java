package com.enigmabridge.provider;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by dusanklinec on 06.07.16.
 */
public class EBProviderUtils {
    public static PublicKey createRSAPublicKey(BigInteger modulus, BigInteger publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);

        // Create a key factory
        KeyFactory factory = KeyFactory.getInstance("RSA");

        // Create the RSA private and public keys
        return factory.generatePublic(spec);
    }
}
