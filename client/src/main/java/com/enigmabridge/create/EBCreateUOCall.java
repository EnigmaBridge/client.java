package com.enigmabridge.create;

import com.enigmabridge.*;
import com.enigmabridge.comm.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Create UO call.
 * EB template needs to be prepared when calling this
 *
 * Created by dusanklinec on 30.06.16.
 */
public class EBCreateUOCall extends EBAPICall implements EBResponseParser {
    private static final Logger LOG = LoggerFactory.getLogger(EBCreateUOCall.class);
    public static final String FIELD_DATA = "data";

    protected EBCreateUORequest pkRequest;
    protected EBCreateUOResponse pkResponse;

    /**
     * Separate abstract builder, chain from EBApiCall broken on purpose, restrict setters of this builder, e.g. callFunction.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBCreateUOCall, B extends EBCreateUOCall.AbstractBuilder> {
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

    public static class Builder extends EBCreateUOCall.AbstractBuilder<EBCreateUOCall, EBCreateUOCall.Builder> {
        private final EBCreateUOCall child = new EBCreateUOCall();

        @Override
        public EBCreateUOCall getObj() {
            return child;
        }

        @Override
        public EBCreateUOCall build() {
            if (child.getApiKey() == null){
                throw new NullPointerException("ApiKey is null");
            }

            if (child.getEndpoint() == null){
                throw new NullPointerException("Endpoint info is null");
            }

            child.setCallFunction("CreateUserObject");
            return child;
        }

        @Override
        public EBCreateUOCall.Builder getThisBuilder() {
            return this;
        }
    }

    /**
     * Builds request data.
     */
    public void build(EBCreateUORequest request) throws IOException {
        this.buildApiBlock(getApiKey(), 0);

        // Build raw request.
        rawRequest = new EBRawRequest();
        if (settings != null) {
            rawRequest.setMethod(settings.getMethod());
        }

        this.pkRequest = request;

        // Build request - body.
        final JSONObject jreq = new JSONObject();
        final JSONObject jdat = new JSONObject();
        jreq.put("data", jdat);

        jdat.put("objectid",        Long.toHexString(request.getObjectId()));
        jdat.put("object",          EBUtils.byte2hex(request.getObject()));
        jdat.put("authorization",   request.getAuthorization());
        jdat.put("importKeyId",     request.getImportKeyId());
        jdat.put("contextparams",   request.getContextparams());

        // Build the rest of the request - headers.
        rawRequest.setBody(jreq.toString());
        rawRequest.setQuery(String.format("%s/%s/%s/%s",
                this.apiVersion,
                this.apiBlock,
                this.callFunction,
                EBUtils.byte2hex(this.getNonce())
        ));
    }

    /**
     * Performs request to the remote endpoint with built request.
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBCreateUOResponse doRequest(EBCreateUORequest request) throws IOException, EBCorruptedException {
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
        final EBCreateUOResponse.Builder builder = new EBCreateUOResponse.Builder();
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
        if (data == null || !data.has(FIELD_DATA)){
            throw new EBCorruptedException("Message corrupted");
        }

        if (resp == null){
            resp = new EBGetPubKeyResponse.Builder();
        }

        if (!resp.getObj().isCodeOk()){
            LOG.debug(String.format("Error in processing, status: %04X, message: %s",
                    (long)resp.getObj().getStatusCode(), resp.getObj().getStatusDetail()));
            return resp;
        }

        final EBCreateUOResponse.ABuilder resp2ret = (EBCreateUOResponse.ABuilder) resp;
        final JSONObject res = data.getJSONObject(FIELD_DATA);

        resp2ret.setHandle(res.getString("uoi"));

        // Certificate?
        if (res.has("certificate")){
            resp2ret.setCertificate(EBUtils.hex2byte(res.getString("certificate"), true));
        }

        // TODO: certificate chain processing.
        return resp2ret;
    }

    @Override
    public EBResponseParser getSubParser() {
        return null;
    }

}
