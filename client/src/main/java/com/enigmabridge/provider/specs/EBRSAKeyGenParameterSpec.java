package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;

import java.math.BigInteger;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * RSA key generation spec for EB.
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBRSAKeyGenParameterSpec extends RSAKeyGenParameterSpec implements EBEngineReference {
    private final EBEngine engine;

    /**
     * Constructs a new <code>RSAParameterSpec</code> object from the
     * given keysize and public-exponent value.
     *
     * @param keysize        the modulus size (specified in number of bits)
     * @param publicExponent the public exponent
     */
    public EBRSAKeyGenParameterSpec(int keysize, BigInteger publicExponent) {
        super(keysize, publicExponent);
        engine = null;
    }

    public EBRSAKeyGenParameterSpec(int keysize, BigInteger publicExponent, EBEngine engine) {
        super(keysize, publicExponent);
        this.engine = engine;
    }

    @Override
    public EBEngine getEBEngine() {
        return engine;
    }
}
