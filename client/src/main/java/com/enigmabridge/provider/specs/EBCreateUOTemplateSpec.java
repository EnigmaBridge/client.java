package com.enigmabridge.provider.specs;

import com.enigmabridge.create.EBUOGetTemplateRequest;

/**
 * Key gen spec providing template request object.
 * Template request can specify advanced UO options.
 * User might want to specify some of them by passing prepared template request.
 *
 * Created by dusanklinec on 11.07.16.
 */
public interface EBCreateUOTemplateSpec {
    EBUOGetTemplateRequest getTemplateRequest();
}
