package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;
import com.enigmabridge.create.EBUOGetTemplateRequest;

import java.security.spec.AlgorithmParameterSpec;

/**
 * Key generation spec for EB.
 *
 * Created by dusanklinec on 13.07.16.
 */
public class EBSymmetricKeyGenParameterSpec implements AlgorithmParameterSpec, EBEngineReference, EBCreateUOTemplateSpec {
    /**
     * Basic EB engine / configuration.
     */
    private final EBEngine engine;

    /**
     * Desired key size for key to be generated.
     */
    private final int keySize;

    /**
     * Override point for creating new objects.
     * If both objects are to be created (enc, dec), the same template request is used for both.
     */
    private EBUOGetTemplateRequest tplReq;

    /**
     * Generate key only for one symmetric operation (i.e., encryption, decryption) or both.
     */
    private EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    public EBSymmetricKeyGenParameterSpec(int keySize) {
        this.keySize = keySize;
        this.engine = null;
    }

    public EBSymmetricKeyGenParameterSpec(int keySize, EBEngine engine) {
        this.keySize = keySize;
        this.engine = engine;
    }

    @Override
    public EBEngine getEBEngine() {
        return engine;
    }

    public int getKeySize() {
        return keySize;
    }

    public EBSymmetricKeyGenParameterSpec setTplReq(EBUOGetTemplateRequest tplReq) {
        this.tplReq = tplReq;
        return this;
    }

    @Override
    public EBUOGetTemplateRequest getTemplateRequest() {
        return tplReq;
    }

    public EBSymmetricKeyGenTypes getKeyType() {
        return keyType;
    }

    public EBSymmetricKeyGenParameterSpec setKeyType(EBSymmetricKeyGenTypes keyType) {
        this.keyType = keyType;
        return this;
    }
}
