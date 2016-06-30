package com.enigmabridge.create;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBEngine;
import com.enigmabridge.EBSettings;
import com.enigmabridge.UserObjectInfo;
import com.enigmabridge.comm.EBAPICall;
import com.enigmabridge.comm.EBConnectionSettings;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple wrapper for creating User Objects.
 * Performs 2 steps in one
 *  1. fetch corresponding template
 *  2. import initialized template
 *
 * Created by dusanklinec on 30.06.16.
 */
public class EBCreateUOSimpleCall {
    // Builders for sub requests / calls.
    protected final EBGetUOTemplateCall.Builder getTemplateCallBld = new EBGetUOTemplateCall.Builder();
    protected final EBCreateUOCall.Builder createUOcallBld = new EBCreateUOCall.Builder();

    // Sub requests constructed after building is finished.
    protected EBGetUOTemplateCall getTemplateCall = null;
    protected EBCreateUOCall createUOcall = null;

    // Template request. Can be passed directly, or default one is constructed.
    protected EBUOGetTemplateRequest tplRequest = null;

    // Keys to set to UO.
    protected List<EBUOTemplateKey> keys = null;

    /**
     * Separate abstract builder, chain from EBApiCall broken on purpose, restrict setters of this builder, e.g. callFunction.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBCreateUOSimpleCall, B extends EBCreateUOSimpleCall.AbstractBuilder> {
        protected boolean endpointSet = false;
        protected boolean settingsSet = false;
        protected boolean apiKeySet = false;

        public B setEndpoint(EBEndpointInfo a) {
            endpointSet = true;
            getObj().getTemplateCallBld.setEndpoint(a);
            getObj().createUOcallBld.setEndpoint(a);
            return getThisBuilder();
        }

        public B setSettings(EBConnectionSettings b) {
            settingsSet = true;
            getObj().getTemplateCallBld.setSettings(b);
            getObj().createUOcallBld.setSettings(b);
            return getThisBuilder();
        }

        public B setApiKey(String apiKey){
            apiKeySet = true;
            getObj().getTemplateCallBld.setApiKey(apiKey);
            getObj().createUOcallBld.setApiKey(apiKey);
            return getThisBuilder();
        }

        public B setSettings(EBSettings settings) {
            if (settings.getApiKey() != null){
                getThisBuilder().setApiKey(settings.getApiKey());
            }
            if (settings.getEndpointInfo() != null){
                getThisBuilder().setEndpoint(settings.getEndpointInfo());
            }
            if (settings.getConnectionSettings() != null){
                getThisBuilder().setSettings(settings.getConnectionSettings());
            }
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine){
            getObj().getTemplateCallBld.setEngine(engine);
            getObj().createUOcallBld.setEngine(engine);

            final EBSettings settings = engine == null ? null : engine.getDefaultSettings();
            if (settings != null){
                if (settings.getApiKey() != null && !apiKeySet){
                    getThisBuilder().setApiKey(settings.getApiKey());
                }
                if (settings.getEndpointInfo() != null && !endpointSet){
                    getThisBuilder().setEndpoint(settings.getEndpointInfo());
                }
                if(settings.getConnectionSettings() != null && !settingsSet){
                    getThisBuilder().setSettings(settings.getConnectionSettings());
                }
            }
            return getThisBuilder();
        }

        public B setRequest(EBUOGetTemplateRequest request){
            getObj().setTplRequest(request);
            return getThisBuilder();
        }

        public B setKeys(List<EBUOTemplateKey> keys){
            getObj().setKeys(keys);
            return getThisBuilder();
        }

        public B addKey(EBUOTemplateKey key){
            getObj().getKeys().add(key);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBCreateUOSimpleCall.AbstractBuilder<EBCreateUOSimpleCall, EBCreateUOSimpleCall.Builder> {
        private final EBCreateUOSimpleCall child = new EBCreateUOSimpleCall();

        @Override
        public EBCreateUOSimpleCall getObj() {
            return child;
        }

        @Override
        public EBCreateUOSimpleCall build() {
            if (!apiKeySet || !endpointSet){
                throw new NullPointerException("Call was not initialized with endpoint settings");
            }

            child.getTemplateCall = child.getTemplateCallBld.build();
            child.createUOcall = child.createUOcallBld.build();
            return child;
        }

        @Override
        public EBCreateUOSimpleCall.Builder getThisBuilder() {
            return this;
        }
    }

    protected EBCreateUOSimpleCall setTplRequest(EBUOGetTemplateRequest tplRequest) {
        this.tplRequest = tplRequest;
        return this;
    }

    protected EBCreateUOSimpleCall setKeys(List<EBUOTemplateKey> keys) {
        this.keys = keys;
        return this;
    }

    public EBUOGetTemplateRequest getTplRequest() {
        return tplRequest;
    }

    public List<EBUOTemplateKey> getKeys() {
        if (this.keys == null){
            this.keys = new LinkedList<EBUOTemplateKey>();
        }
        return keys;
    }
}
