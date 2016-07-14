package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;
import com.enigmabridge.create.EBUOGetTemplateRequest;

import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;

/**
 * SecretKeySpec, extended version, capable of storing EB options for import.
 *
 * Created by dusanklinec on 13.07.16.
 */
public class EBSecretKeySpec extends SecretKeySpec implements EBKeyCreateSpec {
    /**
     * Basic EB engine / configuration.
     */
    private final EBEngine engine;

    /**
     * Override point for creating new objects.
     * If both objects are to be created (enc, dec), the same template request is used for both.
     */
    private EBUOGetTemplateRequest tplReq;

    /**
     * Generate key only for one symmetric operation (i.e., encryption, decryption) or both.
     */
    private EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    public EBSecretKeySpec(SecretKeySpec spec) {
        super(spec.getEncoded(), spec.getAlgorithm());
        engine = null;
    }

    public EBSecretKeySpec(SecretKeySpec spec, EBEngine engine) {
        super(spec.getEncoded(), spec.getAlgorithm());
        this.engine = engine;
    }

    public EBSecretKeySpec(byte[] bytes, String s) {
        super(bytes, s);
        engine = null;
    }

    public EBSecretKeySpec(byte[] bytes, int i, int i1, String s) {
        super(bytes, i, i1, s);
        engine = null;
    }

    public EBSecretKeySpec(byte[] bytes, String s, EBEngine engine) {
        super(bytes, s);
        this.engine = engine;
    }

    @Override
    public EBEngine getEBEngine() {
        return engine;
    }

    public EBSecretKeySpec setTplReq(EBUOGetTemplateRequest tplReq) {
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

    public EBSecretKeySpec setKeyType(EBSymmetricKeyGenTypes keyType) {
        this.keyType = keyType;
        return this;
    }

    @Override
    public KeySpec getUnderlyingSpec() {
        return this;
    }
}
