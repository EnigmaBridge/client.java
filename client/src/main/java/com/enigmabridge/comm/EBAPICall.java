package com.enigmabridge.comm;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBEngine;
import com.enigmabridge.EBSettings;
import com.enigmabridge.UserObjectInfo;
import com.enigmabridge.create.EBCreateUtils;

/**
 * Base API call to the EB service.
 * Created by dusanklinec on 27.04.16.
 */
public class EBAPICall {
    protected EBConnectionSettings settings;
    protected EBEndpointInfo endpoint;
    protected String apiKey;

    protected String apiVersion = "1.0";
    protected String callFunction;
    protected UserObjectInfo uo;

    protected byte[] nonce;
    protected String apiBlock;
    protected EBRawRequest rawRequest;
    protected EBRawResponse rawResponse;

    protected EBConnector connector;
    protected EBResponseParser responseParser;

    protected EBEngine engine;

    public static abstract class AbstractBuilder<T extends EBAPICall, B extends AbstractBuilder> {
        public B setEndpoint(EBEndpointInfo a) {
            getObj().setEndpoint(a);
            return getThisBuilder();
        }

        public B setSettings(EBConnectionSettings b) {
            getObj().setSettings(b);
            return getThisBuilder();
        }

        public B setApiKey(String apiKey){
            getObj().setApiKey(apiKey);
            return getThisBuilder();
        }

        public B setApiVersion(String apiVersion){
            getObj().setApiVersion(apiVersion);
            return getThisBuilder();
        }

        public B setUo(UserObjectInfo uo){
            getObj().setUo(uo);
            return getThisBuilder();
        }

        public B setCallFunction(String callFunction){
            getObj().setCallFunction(callFunction);
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine){
            getObj().setEngine(engine);
            final EBSettings settings = engine == null ? null : engine.getDefaultSettings();
            if (settings != null){
                if (settings.getApiKey() != null && getObj().getApiKey() == null){
                    getObj().setApiKey(settings.getApiKey());
                }
                if (settings.getEndpointInfo() != null && getObj().getEndpoint() == null){
                    getObj().setEndpoint(settings.getEndpointInfo());
                }
                if(settings.getConnectionSettings() != null && getObj().getSettings() == null){
                    getObj().setSettings(settings.getConnectionSettings());
                }
            }
            return getThisBuilder();
        }

        public B setNonce(byte[] nonce){
            getObj().setNonce(nonce);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBAPICall, Builder> {
        private final EBAPICall parent = new EBAPICall();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBAPICall getObj() {
            return parent;
        }

        @Override
        public EBAPICall build() {
            return parent;
        }
    }

    /**
     * Returns response parser when is needed. May lazily initialize parser.
     * Override point.
     *
     * @return Response parser to use
     */
    public EBResponseParser getResponseParser(){
        return new EBResponseParserBase();
    }

    /**
     * Returns respone object to be used by the response parser.
     * Enables to specify a subclass of the original response class.
     */
    public EBResponse getResponseObject(){
        return new EBResponse();
    }

    /**
     * Builds API key token.
     * Consists of apiKey and low4B identifier.
     * Result is returned and set to the property.
     */
    public String buildApiBlock(){
        return buildApiBlock(null, null, null);
    }

    /**
     * Builds API key token.
     * Consists of apiKey and low4B identifier.
     * Can be specified by parameters or currently set values are set.
     * Result is returned and set to the property.
     *
     * @param apiKey API key string
     * @param uoId  integer or hex-coded string.
     * @param uoType  user object type. Integer or hex-coded string.
     */
    public String buildApiBlock(String apiKey, Long uoId, Long uoType){
        String apiKeyToUser = apiKey == null ? this.getApiKey() : apiKey;
        long uoIdr = uoId   == null ? this.getUo().getUoid() : uoId;
        long type  = uoType == null ? this.getUo().getUserObjectType().getValue() : uoType;

        this.apiBlock = EBCreateUtils.getUoHandle(apiKeyToUser, uoIdr, type);
        return this.apiBlock;
    }

    public String getRequestMethod(){
        if (settings == null && rawRequest == null){
             return null;
        }

        if (settings != null && settings.getMethod() != null){
            return settings.getMethod();
        }

        if (rawRequest != null){
            return rawRequest.getMethod();
        }

        return null;
    }

    /**
     * Returns true if HTTP POST method should be used for this service call.
     * @return true if HTTP method is GET
     */
    public boolean isMethodGet(){
        final String method = getRequestMethod();
        return (method == null && EBCommUtils.METHOD_DEFAULT.equals(EBCommUtils.METHOD_GET))
                || "POST".equalsIgnoreCase(method);
    }

    /**
     * Returns true if HTTP POST method should be used for this service call.
     * @return true if HTTP method is POST
     */
    public boolean isMethodPost(){
        final String method = getRequestMethod();
        return (method == null && EBCommUtils.METHOD_DEFAULT.equals(EBCommUtils.METHOD_POST))
                || "POST".equalsIgnoreCase(method);
    }

//    /**
//     * Returns raw EB request for raw socket transport method.
//     * For debugging and verification.
//     *
//     * @returns {string}
//     */
//    TODO: getSocketRequest: function(){
//        this._socketRequest = {};
//        $.extend(true, this._socketRequest, this.reqHeader || {});
//        $.extend(true, this._socketRequest, this.reqBody || {});
//        return this._socketRequest;
//    },

    public byte[] getNonce() {
        if (nonce == null){
            nonce = EBCommUtils.genProcessDataNonce();
        }
        return nonce;
    }

    protected void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    public EBConnectionSettings getSettings() {
        return settings;
    }

    protected EBAPICall setSettings(EBConnectionSettings settings) {
        this.settings = settings; // TODO: should clone own copy?
        return this;
    }

    public EBEndpointInfo getEndpoint() {
        return endpoint;
    }

    protected EBAPICall setEndpoint(EBEndpointInfo endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    protected void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    protected void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public UserObjectInfo getUo() {
        return uo;
    }

    protected void setUo(UserObjectInfo uo) {
        this.uo = uo;
    }

    public String getCallFunction() {
        return callFunction;
    }

    protected void setCallFunction(String callFunction) {
        this.callFunction = callFunction;
    }

    public EBEngine getEngine() {
        return engine;
    }

    protected void setEngine(EBEngine engine) {
        this.engine = engine;
    }
}
