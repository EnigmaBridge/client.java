package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;
import com.enigmabridge.create.EBUOGetTemplateRequest;

import java.math.BigInteger;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * RSA key generation spec for EB.
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBRSAKeyGenParameterSpec extends RSAKeyGenParameterSpec implements EBEngineReference, EBCreateUOTemplateSpec {
    private final EBEngine engine;
    private EBUOGetTemplateRequest tplReq;

    /**
     * Constructs a new <code>RSAParameterSpec</code> object from the
     * given keysize. Public exponent is 65537.
     *
     * @param keysize        the modulus size (specified in number of bits)
     */
    public EBRSAKeyGenParameterSpec(int keysize) {
        super(keysize, F4);
        engine = null;
    }

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

    public EBRSAKeyGenParameterSpec(int keysize, EBEngine engine) {
        super(keysize, F4);
        this.engine = engine;
    }

    public EBRSAKeyGenParameterSpec setTplReq(EBUOGetTemplateRequest tplReq) {
        this.tplReq = tplReq;
        return this;
    }

    @Override
    public EBEngine getEBEngine() {
        return engine;
    }

    @Override
    public EBUOGetTemplateRequest getTemplateRequest() {
        return tplReq;
    }
}
