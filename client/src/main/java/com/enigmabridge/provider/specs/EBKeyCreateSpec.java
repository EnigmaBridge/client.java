package com.enigmabridge.provider.specs;

import com.enigmabridge.EBEngineReference;

import java.security.spec.KeySpec;

/**
 * Created by dusanklinec on 14.07.16.
 */
public interface EBKeyCreateSpec extends EBKeySpec, EBEngineReference, EBCreateUOTemplateSpec {
    KeySpec getUnderlyingSpec();
}
