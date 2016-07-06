package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * AES key generation spec for EB.
 * In future maybe more settings - encrypt, decrypt, both.
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBAESKeyGenParameterSpec implements AlgorithmParameterSpec, EBEngineReference {
    private final EBEngine engine;
    private final int keySize;

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
}
