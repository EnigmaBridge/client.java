package com.enigmabridge.comm;

import com.enigmabridge.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ProcessData() caller.
 * Created by dusanklinec on 27.04.16.
 */
public class EBProcessDataCall extends EBAPICall {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataCall.class);

    /**
     * ProcessData function.
     * PLAINAES, RSA1024, RSA2048, ...
     */
    protected String processFunction;

    protected EBProcessDataRequest pdRequest;
    protected EBProcessDataRequestBuilder pdRequestBuilder;
    protected EBProcessDataResponse pdResponse;
    protected EBProcessDataResponseParser pdResponseParser;

    /**
     * Separate abstract builder, chain from EBApiCall broken on purpose, restrict setters of this builder, e.g. callFunction.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBProcessDataCall, B extends AbstractBuilder> {
        public B setEndpoint(EBEndpointInfo a) {
            getObj().setEndpoint(a);
            return getThisBuilder();
        }

        public B setSettings(EBConnectionSettings b) {
            getObj().setSettings(b);
            return getThisBuilder();
        }

        public B setProcessFunction(String b) {
            getObj().setProcessFunction(b);
            return getThisBuilder();
        }

        public B setProcessFunction(EBRequestTypes b) {
            getObj().setProcessFunction(b.toString());
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

        public B setUo(UserObjectInfo uo){
            final T obj = getObj();
            obj.setUo(uo);

            if (uo != null){
                getThisBuilder().setSettings(uo);
            }
            return getThisBuilder();
        }

        public B setKey(UserObjectKey key){
            final T obj = getObj();
            getThisBuilder().setUo(key);

            if (key != null){
                if (key instanceof EBEngineReference && obj.getEngine() == null){
                    obj.setEngine(((EBEngineReference) key).getEBEngine());
                }

                final EBRequestTypes tmpReqType = obj.getRequestTypeForKey(key);
                if (tmpReqType != null && obj.getProcessFunction() == null){
                    obj.setProcessFunction(tmpReqType.toString());
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

    public static class Builder extends AbstractBuilder<EBProcessDataCall, Builder> {
        private final EBProcessDataCall child = new EBProcessDataCall();

        @Override
        public EBProcessDataCall getObj() {
            return child;
        }

        @Override
        public EBProcessDataCall build() {
            // Check if UO is set
            if (child.getUo() == null){
                throw new NullPointerException("UO is null");
            }

            if (child.getApiKey() == null){
                throw new NullPointerException("ApiKey is null");
            }

            if (child.getEndpoint() == null){
                throw new NullPointerException("Endpoint info is null");
            }

            if (child.processFunction == null){
                throw new NullPointerException("Process function is null");
            }

            child.setCallFunction("ProcessData");
            return child;
        }

        @Override
        public Builder getThisBuilder() {
            return this;
        }
    }

    /**
     * Builds request data.
     * @param requestData raw data to ProcessData().
     */
    public void build(byte[] requestData) throws IOException {
        this.buildApiBlock(null, null);

        // Build raw request.
        rawRequest = new EBRawRequest();
        if (settings != null) {
            rawRequest.setMethod(settings.getMethod());
        }

        // Build request - body.
        pdRequestBuilder = new EBProcessDataRequestBuilder()
                .setNonce(getNonce())
                .setRequestType(getProcessFunction())
                .setUoInfo(getUo());

        pdRequest = pdRequestBuilder.build(requestData);

        // Build request - headers.
        if (isMethodPost()){
            // POST
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("data", pdRequest.getRequest());
            rawRequest.setBody(jsonBody.toString());
            rawRequest.setQuery(String.format("%s/%s/%s/%s",
                    this.apiVersion,
                    this.apiBlock,
                    this.callFunction,
                    EBUtils.byte2hex(this.getNonce())
                    ));
        } else {
            // GET
            rawRequest.setBody(null);
            rawRequest.setQuery(String.format("%s/%s/%s/%s/%s",
                    this.apiVersion,
                    this.apiBlock,
                    this.callFunction,
                    EBUtils.byte2hex(this.getNonce()),
                    pdRequest.getRequest()
                    ));
        }
    }

    /**
     * Determines a ProcessData() request type for the given key.
     * @param key
     * @return
     */
    public EBRequestTypes getRequestTypeForKey(UserObjectKey key){
        final String algorithm = key.getAlgorithm();
        final int bitLength = key.length();
        if ("AES".equalsIgnoreCase(algorithm)){
            return EBRequestTypes.PLAINAES;
        }

        if ("RSA".equalsIgnoreCase(algorithm)){
            if (bitLength == 1024){
                return EBRequestTypes.RSA1024;
            } else if (bitLength == 2048){
                return EBRequestTypes.RSA2048;
            }
        }

        return null;
    }

    /**
     * Performs request to the remote endpoint with built request.
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBProcessDataResponse doRequest() throws IOException, EBCorruptedException {
        return doRequest(null);
    }

    /**
     * Performs request to the remote endpoint with built request.
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBProcessDataResponse doRequest(byte[] requestData) throws IOException, EBCorruptedException {
        if (apiBlock == null && requestData == null){
            throw new IllegalArgumentException("Call was not built with request data, cannot build now - no data");
        } else if (requestData != null){
            build(requestData);
        }

        this.connector = engine.getConMgr().getConnector(this.endpoint);
        this.connector.setEndpoint(this.endpoint);
        this.connector.setSettings(this.settings);
        this.connector.setRawRequest(rawRequest);

        LOG.trace("Going to call request...");
        this.rawResponse = this.connector.request();

        // Empty response to parse data to.
        pdResponse = new EBProcessDataResponse();
        pdResponse.setRawResponse(rawResponse);

        if (!rawResponse.isSuccessful()){
            LOG.info("Response was not successful: " + rawResponse.toString());
            return pdResponse;
        }

        // Parse process data response.
        pdResponseParser = new EBProcessDataResponseParser();
        pdResponseParser.setUo(getUo());
        pdResponseParser.parseResponse(new JSONObject(rawResponse.getBody()), pdResponse, null);

        // Return connector.
        engine.getConMgr().doneWithConnector(connector);
        this.connector = null;

        return pdResponse;
    }

    /**
     * Returns process data output in one call.
     * @return
     * @throws IOException
     * @throws EBCorruptedException
     */
    public byte[] processData() throws IOException, EBCorruptedException {
        return processData(null);
    }

    /**
     * Returns process data output in one call.
     * @param input
     * @return
     * @throws IOException
     * @throws EBCorruptedException
     */
    public byte[] processData(byte[] input) throws IOException, EBCorruptedException {
        if (input != null){
            this.build(input);
        }

        doRequest();
        return pdResponse.getProtectedData();
    }

    // Getters & Setters.
    public String getProcessFunction() {
        return processFunction;
    }

    protected void setProcessFunction(String processFunction) {
        this.processFunction = processFunction;
    }
}
