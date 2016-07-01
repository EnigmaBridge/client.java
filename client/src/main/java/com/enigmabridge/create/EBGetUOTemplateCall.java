package com.enigmabridge.create;

import com.enigmabridge.*;
import com.enigmabridge.comm.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Requests template for creating a new UO.
 * Created by dusanklinec on 29.06.16.
 */
public class EBGetUOTemplateCall extends EBAPICall implements EBResponseParser {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataCall.class);
    public static final String FIELD_DATA = "data";

    protected EBUOGetTemplateRequest pkRequest;
    protected EBUOTemplateResponse pkResponse;

    /**
     * Separate abstract builder, chain from EBApiCall broken on purpose, restrict setters of this builder, e.g. callFunction.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBGetUOTemplateCall, B extends EBGetUOTemplateCall.AbstractBuilder> {
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

    public static class Builder extends EBGetUOTemplateCall.AbstractBuilder<EBGetUOTemplateCall, EBGetUOTemplateCall.Builder> {
        private final EBGetUOTemplateCall child = new EBGetUOTemplateCall();

        @Override
        public EBGetUOTemplateCall getObj() {
            return child;
        }

        @Override
        public EBGetUOTemplateCall build() {
            if (child.getApiKey() == null){
                throw new NullPointerException("ApiKey is null");
            }

            if (child.getEndpoint() == null){
                throw new NullPointerException("Endpoint info is null");
            }

            child.setCallFunction("GetUserObjectTemplate");
            return child;
        }

        @Override
        public EBGetUOTemplateCall.Builder getThisBuilder() {
            return this;
        }
    }

    /**
     * Builds request data.
     */
    public void build(EBUOGetTemplateRequest request) throws IOException {
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

        jdat.put("format",      String.valueOf(request.getFormat()));        //<integer, starting with 1>,
        jdat.put("protocol",    String.valueOf(request.getProtocol()));      //<integer, starting with 1>,
        jdat.put("type",        String.valueOf(request.getType()));        //<32bit integer>,
        jdat.put("environment", request.getEnvironment()); // shows whether the UO should be for production (live), test (pre-production testing), or dev (development)
        jdat.put("maxtps",      request.getMaxtps()); // maximum guaranteed TPS
        jdat.put("core",        request.getCore()); // how many cards have UO loaded permanently
        jdat.put("persistence", request.getPersistence()); // once loaded onto card, how long will the UO stay there without use (this excludes the "core")
        jdat.put("priority",    request.getPriority()); // this defines a) priority when the server capacity is fully utilised and it also defines how quickly new copies of UO are installed (pre-empting icreasing demand)
        jdat.put("separation",  request.getSeparation()); // "complete" = only one UO can be loaded on a smartcard at one one time
        jdat.put("bcr",         request.getBcr());      // "yes" will ensure the UO is replicated to provide high availability for any possible service disruption
        jdat.put("unlimited",   request.getUnlimited());
        jdat.put("clientiv",    request.getClientiv()); //  if "yes", we expect the data starts with an IV to initialize decryption of data - this is for communication security
        jdat.put("clientdiv",   request.getClientdiv()); // if "yes", we expect the data starting with a diversification 16B for communication keys
        jdat.put("resource",    request.getResource());
        jdat.put("credit",      String.valueOf(request.getCredit())); // <1-32767>, a limit a seed card can provide to the EB service

        final JSONObject jgen = new JSONObject();
        jgen.put("commkey", request.getGenerationCommKey());
        jgen.put("billingkey", request.getGenerationBillingKey());
        jgen.put("appkey", request.getGenerationAppKey());
        jdat.put("generation", jgen);

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
    public EBUOTemplateResponse doRequest(EBUOGetTemplateRequest request) throws IOException, EBCorruptedException {
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
        final EBUOTemplateResponse.Builder builder = new EBUOTemplateResponse.Builder();
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

        final EBUOTemplateResponse.ABuilder resp2ret = (EBUOTemplateResponse.ABuilder) resp;
        final JSONObject res = data.getJSONObject(FIELD_DATA);

        resp2ret.setObjectId(EBUtils.getAsLong(res, "objectid", 16));
        resp2ret.setVersion(EBUtils.getAsInteger(res, "version", 10));
        resp2ret.setProtocol(EBUtils.getAsInteger(res, "protocol", 10));

        resp2ret.setEncryptionOffset(EBUtils.getAsLong(res, "encryptionoffset", 10));
        resp2ret.setFlagOffset(EBUtils.getAsLong(res, "flagoffset", 10));
        resp2ret.setPolicyOffset(EBUtils.getAsLong(res, "policyoffset", 10));
        resp2ret.setScriptOffset(EBUtils.getAsLong(res, "scriptoffset", 10));

        resp2ret.setTemplate(EBUtils.hex2byte(res.getString("template"), true));
        resp2ret.setTemplateHs(EBUtils.hex2byte(res.getString("templatehs"), true));
        resp2ret.setAuthorization(EBUtils.tryGetAsString(res, "authorization"));

        List<EBUOTemplateKeyOffset> offsets = new LinkedList<EBUOTemplateKeyOffset>();
        final JSONArray keyOffsets = res.getJSONArray("keyoffsets");
        for(int idx=0, len=keyOffsets.length(); idx < len; ++idx){
            final JSONObject offset = keyOffsets.getJSONObject(idx);

            final EBUOTemplateKeyOffset.Builder offBld = new EBUOTemplateKeyOffset.Builder();
            offBld.setType(offset.getString("type"));
            offBld.setOffset(EBUtils.getAsLong(offset, "offset", 10));
            offBld.setLength(EBUtils.getAsLong(offset, "length", 10));

            if (offset.has("tlvtype")) {
                offBld.setTlvtype(EBUtils.getAsInteger(offset, "tlvtype", 10));
            }

            offsets.add(offBld.build());
        }

        resp2ret.setKeyOffsets(offsets);

        List<EBUOTemplateImportKey> keys = new LinkedList<EBUOTemplateImportKey>();
        final JSONArray importKeys = res.getJSONArray("importkeys");
        for(int idx=0, len=importKeys.length(); idx < len; ++idx){
            final JSONObject key = importKeys.getJSONObject(idx);

            final EBUOTemplateImportKey.Builder keyBld = new EBUOTemplateImportKey.Builder();
            keyBld.setId(key.getString("id"));
            keyBld.setType(key.getString("type"));
            keyBld.setPublicKey(EBUtils.hex2byte(key.getString("publickey"), true));

            keys.add(keyBld.build());
        }

        resp2ret.setImportKeys(keys);

        return resp2ret;
    }

    @Override
    public EBResponseParser getSubParser() {
        return null;
    }

}
