package com.enigmabridge.comm;

import com.enigmabridge.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Fetches import public keys from EB.
 * Created by dusanklinec on 02.05.16.
 */
public class EBGetPubKeyCall extends EBAPICall implements EBResponseParser{
    private static final Logger LOG = LoggerFactory.getLogger(EBGetPubKeyCall.class);

    public static final String FIELD_RESULT = "result";
    public static final String FIELD_CERTIFICATE = "certificate";
    public static final String FIELD_ID = "id";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_KEY = "key";

    protected EBGetPubKeyResponse pkResponse;

    /**
     * Separate abstract builder, chain from EBApiCall broken on purpose, restrict setters of this builder, e.g. callFunction.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBGetPubKeyCall, B extends AbstractBuilder> {
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

        public B setUo(UserObjectInfo uo){
            final T obj = getObj();
            obj.setUo(uo);

            if (uo != null){
                if (uo.getApiKey() != null && obj.getApiKey() == null){
                    obj.setApiKey(uo.getApiKey());
                }
                if (uo.getEndpointInfo() != null && obj.getEndpoint() == null){
                    obj.setEndpoint(uo.getEndpointInfo());
                }
                if(uo.getConnectionSettings() != null && obj.getSettings() == null){
                    obj.setSettings(uo.getConnectionSettings());
                }
            }
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

    public static class Builder extends AbstractBuilder<EBGetPubKeyCall, Builder> {
        private final EBGetPubKeyCall child = new EBGetPubKeyCall();

        @Override
        public EBGetPubKeyCall getObj() {
            return child;
        }

        @Override
        public EBGetPubKeyCall build() {
            if (child.getApiKey() == null){
                throw new NullPointerException("ApiKey is null");
            }

            if (child.getEndpoint() == null){
                throw new NullPointerException("Endpoint info is null");
            }

            child.setCallFunction("GetImportPublicKey");
            return child;
        }

        @Override
        public Builder getThisBuilder() {
            return this;
        }
    }

    /**
     * Builds request data.
     */
    public void build() throws IOException {
        this.buildApiBlock(getApiKey(), 0);

        // Build raw request.
        rawRequest = new EBRawRequest();
        if (settings != null) {
            rawRequest.setMethod(settings.getMethod());
        }

        // Build request - headers.
        rawRequest.setBody(null);
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
    public EBGetPubKeyResponse doRequest() throws IOException, EBCorruptedException {
        if (apiBlock == null){
            build();
        }

        this.connector = engine.getConMgr().getConnector(this.endpoint);
        this.connector.setEndpoint(this.endpoint);
        this.connector.setSettings(this.settings);
        this.connector.setRawRequest(rawRequest);

        LOG.trace("Going to call request...");
        this.rawResponse = this.connector.request();

        // Empty response to parse data to.
        pkResponse = new EBGetPubKeyResponse();
        pkResponse.setRawResponse(rawResponse);
        if (!rawResponse.isSuccessful()){
            LOG.info("Response was not successful: " + rawResponse.toString());
            return pkResponse;
        }

        // Parse process data response.
        final EBResponseParserBase parser = new EBResponseParserBase();
        parser.setSubParser(this);
        parser.parseResponse(new JSONObject(rawResponse.getBody()), pkResponse, null);

        // Return connector.
        engine.getConMgr().doneWithConnector(connector);
        this.connector = null;

        return pkResponse;
    }

    @Override
    public EBResponse parseResponse(JSONObject data, EBResponse resp, EBResponseParserOptions options) throws EBCorruptedException {
        /**
         * Response:
         * {"function":"GetImportPublicKey","result":[
         * {"certificate":null,"id":263,"type":"rsa","key":"81 00 03 01 00 01 82 01 00 e1 e0 6b 76 f9 7b cd 82 7c 98 cc 3b 41 a8 50 40 cc dc 61 cf 72 58 14 fd b9 e9 5f 53 06 29 12 e9 39 b1 3c f1 ce 27 d0 7b 44 78 57 7a 20 9c ff db de a2 90 29 19 c0 87 08 8f 85 d5 ed 1d 0b 0c dc ef d8 23 b6 49 71 4f 69 95 31 d9 b8 10 08 af 63 5e a9 79 67 82 fe 3c 40 3c 0e 5d e2 15 58 78 06 f3 0e 16 09 4d a0 16 05 89 e9 80 1c ba f4 0e 63 fd 2d 72 cb 85 cb 7f c1 9a 37 7b 0f a9 2e 7d 90 8e 6a 69 aa bc 4c 5b a2 2d 32 e5 58 7e 0e d8 12 b4 c1 62 66 84 98 fd e5 54 08 93 c1 c0 88 41 51 60 93 93 d8 cc cd ee 3e eb 88 ae 91 24 32 16 b2 26 92 73 f9 a5 23 b9 5c cf e5 b1 f9 e5 4f d2 4f 73 77 a2 ab d7 c6 43 9e c4 60 97 c4 70 1e 58 c2 49 33 02 2d 43 8b 77 67 3c 30 0e a6 81 e4 73 d2 46 18 f9 79 40 3d a6 79 dd 5c 3c e0 b7 4c 16 a9 5c 96 47 40 7c 2c dc 11 3b 92 75 44 ec d8 c6 95 "},
         * {"certificate":null,"id":264,"type":"rsa","key":"81 00 03 01 00 01 82 01 00 e1 e0 6b 76 f9 7b cd 82 7c 98 cc 3b 41 a8 50 40 cc dc 61 cf 72 58 14 fd b9 e9 5f 53 06 29 12 e9 39 b1 3c f1 ce 27 d0 7b 44 78 57 7a 20 9c ff db de a2 90 29 19 c0 87 08 8f 85 d5 ed 1d 0b 0c dc ef d8 23 b6 49 71 4f 69 95 31 d9 b8 10 08 af 63 5e a9 79 67 82 fe 3c 40 3c 0e 5d e2 15 58 78 06 f3 0e 16 09 4d a0 16 05 89 e9 80 1c ba f4 0e 63 fd 2d 72 cb 85 cb 7f c1 9a 37 7b 0f a9 2e 7d 90 8e 6a 69 aa bc 4c 5b a2 2d 32 e5 58 7e 0e d8 12 b4 c1 62 66 84 98 fd e5 54 08 93 c1 c0 88 41 51 60 93 93 d8 cc cd ee 3e eb 88 ae 91 24 32 16 b2 26 92 73 f9 a5 23 b9 5c cf e5 b1 f9 e5 4f d2 4f 73 77 a2 ab d7 c6 43 9e c4 60 97 c4 70 1e 58 c2 49 33 02 2d 43 8b 77 67 3c 30 0e a6 81 e4 73 d2 46 18 f9 79 40 3d a6 79 dd 5c 3c e0 b7 4c 16 a9 5c 96 47 40 7c 2c dc 11 3b 92 75 44 ec d8 c6 95 "}]
         * ,"status":"9000","statusdetail":"(OK)SW_STAT_OK","version":"1.0"}
         */
        if (data == null || !data.has(FIELD_RESULT)){
            throw new EBCorruptedException("Message corrupted");
        }

        if (resp == null){
            resp = new EBGetPubKeyResponse();
        }

        final EBGetPubKeyResponse resp2ret = (EBGetPubKeyResponse) resp;
        final JSONArray results = data.getJSONArray(FIELD_RESULT);
        final int resLen = results.length();

        int index;
        for (index = 0; index < resLen; ++index) {
            JSONObject cur = results.getJSONObject(index);
            EBImportPubKey cKey = new EBImportPubKey();

            if (!cur.has(FIELD_ID) || !cur.has(FIELD_KEY)) {
                LOG.debug("Invalid current import key field, ID||Key is missing");
                continue;
            }

            cKey.setId(EBUtils.getAsInteger(cur, FIELD_ID, 10));
            cKey.setType(EBUtils.getAsStringOrNull(cur, FIELD_TYPE));

            if (cur.has(FIELD_CERTIFICATE)){
                final String certStr = EBUtils.getAsStringOrNull(cur, FIELD_CERTIFICATE);
                if (certStr != null){
                    cKey.setCertificateRaw(EBUtils.hex2byte(certStr.replaceAll("\\s","")));
                }
            }

            if (cur.has(FIELD_KEY)){
                final String keyStr = EBUtils.getAsStringOrNull(cur, FIELD_KEY);
                if (keyStr != null){
                    cKey.setKeyRaw(EBUtils.hex2byte(keyStr.replaceAll("\\s","")));
                }
            }

            resp2ret.getImportKeys().add(cKey);
        }

        return resp2ret;
    }

    @Override
    public EBResponseParser getSubParser() {
        return null;
    }

}
