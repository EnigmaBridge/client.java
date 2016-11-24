package com.enigmabridge.registration;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBEngine;
import com.enigmabridge.EBSettings;
import com.enigmabridge.EBUtils;
import com.enigmabridge.comm.*;
import com.enigmabridge.create.EBUOTemplateImportKey;
import com.enigmabridge.create.EBUOTemplateKeyOffset;
import com.enigmabridge.create.EBUOTemplateResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.enigmabridge.EBUtils.absorbIfNonNull;

/**
 * Base request for registration calls
 * Created by dusanklinec on 29.06.16.
 */
public class EBRegistrationBaseCall extends EBAPICall implements EBResponseParser {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataCall.class);
    public static final String FIELD_DATA = "data";
    public static final String FIELD_RESULT = "result";
    public static final String FIELD_STATUS = "status";

    public static final String FIELD_NONCE = "nonce";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_FUNCTION = "function";
    public static final String FIELD_ENVIRONMENT = "environment";

    public static final String CLIENT_SUFFIX = "/api/v1/client";
    public static final String API_KEY_SUFFIX = "/api/v1/apikey";

    protected int regVersion = 1;
    protected String environment;

    protected EBRegistrationBaseRequest pkRequest;
    protected EBRegistrationBaseResponse pkResponse;

    /**
     * Separate abstract builder, chain from EBApiCall broken on purpose, restrict setters of this builder, e.g. callFunction.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBRegistrationBaseCall, B extends EBRegistrationBaseCall.AbstractBuilder> {
        public B setEndpoint(EBEndpointInfo a) {
            getObj().setEndpoint(a);
            return getThisBuilder();
        }

        public B setSettings(EBConnectionSettings b) {
            getObj().setSettings(b);
            return getThisBuilder();
        }

        public B setSettings(EBSettings settings) {
            if (settings.getApiKey() != null){
                getObj().setApiKey(settings.getApiKey());
            }
            if (settings.getEndpointInfo() != null){
                getObj().setEndpoint(settings.getEndpointInfo());
            }
            if (settings.getConnectionSettings() != null){
                getObj().setSettings(settings.getConnectionSettings());
            }
            return getThisBuilder();
        }

        public B setApiKey(String apiKey){
            getObj().setApiKey(apiKey);
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine){
            getObj().setEngine(engine);

            // Take registration endpoint preferably if configured via engine
            if (engine != null && engine.getEndpointRegistration() != null){
                getObj().setEndpoint(engine.getEndpointRegistration());
            }

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

        public B setEnvironment(String environment){
            getObj().setEnvironment(environment);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBRegistrationBaseCall.AbstractBuilder<EBRegistrationBaseCall, EBRegistrationBaseCall.Builder> {
        private final EBRegistrationBaseCall child = new EBRegistrationBaseCall();

        @Override
        public EBRegistrationBaseCall getObj() {
            return child;
        }

        @Override
        public EBRegistrationBaseCall build() {
            if (child.getEndpoint() == null){
                throw new NullPointerException("Endpoint info is null");
            }

            return child;
        }

        @Override
        public EBRegistrationBaseCall.Builder getThisBuilder() {
            return this;
        }
    }

    /**
     * Builds request data.
     */
    public void build(EBRegistrationBaseRequest request) throws IOException {
        // Build raw request for the call, manually.
        rawRequest = new EBRawRequest();
        if (settings != null) {
            rawRequest.setMethod(settings.getMethod());
        }

        this.pkRequest = request;

        // Build request - body.
        final JSONObject jreq = new JSONObject();
        jreq.put(FIELD_NONCE, EBUtils.byte2hex(getNonce()));
        jreq.put(FIELD_VERSION, regVersion);
        jreq.put(FIELD_FUNCTION, getCallFunction());
        jreq.put(FIELD_ENVIRONMENT, environment);

        // Merge settings from the request body
        if (request != null){
            absorbIfNonNull(jreq, FIELD_VERSION, request.getRegVersion());
            absorbIfNonNull(jreq, FIELD_FUNCTION, request.getFunction());
            absorbIfNonNull(jreq, FIELD_ENVIRONMENT, request.getEnvironment());
            final JSONObject reqRoot = request.getRoot();
            if (reqRoot != null) {
                EBUtils.mergeInto(jreq, reqRoot);
            }

            rawRequest.setPath(request.getSuffix());
        }

        // Auth header
        rawRequest.addHeader("X-Auth-Token", "public");

        // Build the rest of the request - headers.
        rawRequest.setBody(jreq.toString());

        // the default URL suffix is the client one.
        if (rawRequest.getPath() == null){
            rawRequest.setPath(CLIENT_SUFFIX);
        }
    }

    /**
     * Creates the response class instance - for overloading.
     * @return
     */
    public EBRegistrationBaseResponse.ABuilder getResponseBuilder(){
        return new EBRegistrationBaseResponse.Builder();
    }

    /**
     * Performs request to the remote endpoint with built request.
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBRegistrationBaseResponse doRequest(EBRegistrationBaseRequest request) throws IOException, EBCorruptedException {
        if (apiBlock == null || request != null){
            build(request);
        }

        this.connector = engine.getConMgr().getConnector(this.endpoint);
        this.connector.setEndpoint(this.endpoint);
        this.connector.setSettings(this.settings);
        this.connector.setRawRequest(rawRequest);

        LOG.trace("Going to call request...");
        this.rawResponse = this.connector.request();

        // Empty response to parse data to.
        final EBRegistrationBaseResponse.ABuilder builder = getResponseBuilder();
        builder.setRawResponse(rawResponse);

        if (!rawResponse.isSuccessful()){
            LOG.info("Response was not successful: " + rawResponse.toString());
            pkResponse = builder.build();
            return pkResponse;
        }

        // Parse process data response.
        final EBResponseParserBase parser = new EBResponseParserBase();
        parser.setSubParser(this);
        parser.parseResponse(new JSONObject(rawResponse.getBody()), builder, null);

        // Return connector.
        engine.getConMgr().doneWithConnector(connector);
        this.connector = null;

        pkResponse = builder.build();
        return pkResponse;
    }

    @Override
    public EBResponse.ABuilder parseResponse(JSONObject data, EBResponse.ABuilder resp, EBResponseParserOptions options) throws EBCorruptedException {
        if (data == null || !data.has(FIELD_STATUS)){
            throw new EBCorruptedException("Message corrupted");
        }

        if (resp == null){
            resp = new EBRegistrationBaseResponse.Builder();
        }

        if (!resp.getObj().isCodeOk()){
            LOG.debug(String.format("Error in processing, status: %04X, message: %s",
                    (long)resp.getObj().getStatusCode(), resp.getObj().getStatusDetail()));
            return resp;
        }

        final EBRegistrationBaseResponse.ABuilder resp2ret = (EBRegistrationBaseResponse.ABuilder) resp;

        if (data.has(FIELD_VERSION)) {
            resp2ret.setVersion(EBUtils.getAsInteger(data, FIELD_VERSION, 10));
        }

        if (data.has("error")){
            resp2ret.setStatusDetail(EBUtils.getAsStringOrNull(data, "error"));
        }

        if (data.has("timestamp")){
            resp2ret.setTimestamp(EBUtils.tryGetAsLong(data, "timestamp", 10));
        }

        if (data.has(FIELD_NONCE)){
            resp2ret.setNonce(EBUtils.getAsStringOrNull(data, FIELD_NONCE));
        }

        return resp2ret;
    }

    @Override
    public EBResponseParser getSubParser() {
        return null;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
