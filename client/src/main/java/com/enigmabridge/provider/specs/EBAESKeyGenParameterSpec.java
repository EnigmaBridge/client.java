package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;
import com.enigmabridge.create.EBUOGetTemplateRequest;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * AES key generation spec for EB.
 * In future maybe more settings - encrypt, decrypt, both.
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBAESKeyGenParameterSpec implements AlgorithmParameterSpec, EBEngineReference, EBCreateUOTemplateSpec {
    private final EBEngine engine;
    private final int keySize;
    private EBUOGetTemplateRequest tplReq;

    private EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    public EBAESKeyGenParameterSpec(int keySize) {
        this.keySize = keySize;
        this.engine = null;
    }

    public EBAESKeyGenParameterSpec(int keySize, EBEngine engine) {
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

    public EBAESKeyGenParameterSpec setTplReq(EBUOGetTemplateRequest tplReq) {
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

    public EBAESKeyGenParameterSpec setKeyType(EBSymmetricKeyGenTypes keyType) {
        this.keyType = keyType;
        return this;
    }
}
