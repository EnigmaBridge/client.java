package com.enigmabridge.comm;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBEngine;
import com.enigmabridge.UserObjectInfo;

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

    public static abstract class AbstractEBAPICallBuilder<T extends EBAPICall, B extends AbstractEBAPICallBuilder> {
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

    public static class Builder extends AbstractEBAPICallBuilder<EBAPICall, Builder> {
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
     * @returns {*}
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
        return buildApiBlock(null, null);
    }

    /**
     * Builds API key token.
     * Consists of apiKey and low4B identifier.
     * Can be specified by parameters or currently set values are set.
     * Result is returned and set to the property.
     *
     * @param apiKey API key string
     * @param low4B  integer or hex-coded string.
     */
    public String buildApiBlock(String apiKey, Integer low4B){
        String apiKeyToUser = apiKey == null ? this.getApiKey() : apiKey;
        int low4b = low4B == null ? (int)this.getUo().getUoid() : low4B;

        this.apiBlock = String.format("%s%010x", apiKey, low4b);
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

    public boolean isMethodGet(){
        final String method = getRequestMethod();
        return method == null || "GET".equalsIgnoreCase(method);
    }

    public boolean isMethodPost(){
        final String method = getRequestMethod();
        return method == null || "POST".equalsIgnoreCase(method);
    }

//    /**
//     * Returns raw EB request for raw socket transport method.
//     * For debugging & verification.
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
        this.settings = settings;
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
