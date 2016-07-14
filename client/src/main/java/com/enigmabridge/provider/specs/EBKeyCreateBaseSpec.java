package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngine;
import com.enigmabridge.create.EBUOGetTemplateRequest;

import java.security.spec.KeySpec;

/**
 * KeySpec used with our Factory / Generator classes when want to provide more detailed configuration
 * (engine, tplReq).
 *
 * Created by dusanklinec on 14.07.16.
 */
public class EBKeyCreateBaseSpec implements EBKeyCreateSpec {
    /**
     * Underlying specs.
     */
    protected final KeySpec spec;

    /**
     * Basic EB engine / configuration.
     */
    private EBEngine engine;

    /**
     * Override point for creating new objects.
     * If both objects are to be created (enc, dec), the same template request is used for both.
     */
    private EBUOGetTemplateRequest tplReq;

    public EBKeyCreateBaseSpec(KeySpec spec) {
        this.spec = spec;
    }

    @Override
    public KeySpec getUnderlyingSpec() {
        return spec;
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
