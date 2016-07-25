package com.enigmabridge.comm;

import com.enigmabridge.*;
import com.enigmabridge.comm.retry.*;
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

                final UserObjectType uot = uo.getUserObjectType();
                final String fction = uot == null ? null : uot.getUoTypeFunctionString();
                if (fction != null){
                    obj.setProcessFunction(fction);
                }
            }

            return getThisBuilder();
        }

        public B setKey(UserObjectKey key){
            final T obj = getObj();
            getThisBuilder().setUo(key);

            if (key != null){
                if (key instanceof EBEngineReference && obj.getEngine() == null){
                    getThisBuilder().setEngine(((EBEngineReference) key).getEBEngine());
                }

                final UserObjectType uot = key.getUserObjectType();
                final String fction = uot == null ? null : uot.getUoTypeFunctionString();
                if (fction != null){
                    obj.setProcessFunction(fction);
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

            if (child.getProcessFunction() == null){
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
        build(requestData, 0, requestData == null ? 0 : requestData.length);
    }

    /**
     * Builds request data.
     * @param requestData raw data to ProcessData().
     * @param offset - offset to start with request data
     * @param length - number of bytes to read from request data
     */
    public void build(byte[] requestData, int offset, int length) throws IOException {
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

        pdRequest = pdRequestBuilder.build(requestData, offset, length);

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
     * Performs request to the remote endpoint with built request.
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBProcessDataResponse doRequest() throws IOException, EBCorruptedException {
        return doRequest(null);
    }

    /**
     * Performs request to the remote endpoint with built request.
     *
     * @param requestData - data to build request with
     * @return process data response
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBProcessDataResponse doRequest(byte[] requestData) throws IOException, EBCorruptedException {
        return doRequest(requestData, 0, requestData == null ? 0 : requestData.length);
    }

    /**
     * Performs request to the remote endpoint with built request.
     *
     * @param requestData - data to build request with
     * @param offset - offset to start with request data
     * @param length - number of bytes to read from request data
     * @return process data response
     * @throws IOException
     * @throws EBCorruptedException
     */
    public EBProcessDataResponse doRequest(final byte[] requestData, final int offset, final int length) throws IOException, EBCorruptedException {
        if (settings == null || settings.getRetryStrategyApplication() == null){
            return doRequestInternal(requestData, offset, length);
        }

        final EBRetryStrategy strategy = settings.getRetryStrategyApplication().copy();

        // New retry mechanism
        final EBRetry<EBProcessDataResponse, Throwable> ebRetry = new EBRetry<EBProcessDataResponse, Throwable>(strategy);

        // Define retry job
        ebRetry.setJob(new EBRetryJobSimpleSafeThrErr<EBProcessDataResponse>() {
            @Override
            public void runAsyncNoException(EBCallback<EBProcessDataResponse, Throwable> callback) throws Throwable {
                try {
                    final EBProcessDataResponse ebResponse = doRequestInternal(requestData, offset, length);

                    // Inspect the code
                    if (ebResponse.isCodeOk()) {
                        callback.onSuccess(ebResponse);
                        return;
                    }

                    // Some error codes may be recoverable on retry.
                    final short statusCode = ebResponse.getStatusCode();
                    final boolean isRecoverable = statusCode == EBCommStatus.ERROR_CLASS_ERR_CHECK_ERRORS_6f;
                    callback.onFail(new EBRetryJobErrorThr(new EBCryptoException("Invalid response: " + ebResponse)), !isRecoverable);

                } catch(IOException exception) {
                    callback.onFail(new EBRetryJobErrorThr(exception), false);
                }
            }
        });

        // Start the job synchronously.
        try {
            return ebRetry.runSync();

        } catch (EBRetryFailedException e) {
            final Object error = e.getError();
            rethrowProcessDataError(error);

            throw new IOException("ProcessData failed", e);

        } catch(EBRetryAbortedException e) {
            final Object error = e.getError();
            rethrowProcessDataError(error);

            throw new IOException("ProcessData aborted", e);

        } catch (EBRetryException e){
            throw new IOException("Fatal request error", e);
        }
    }

    protected EBProcessDataResponse doRequestInternal(byte[] requestData, int offset, int length) throws IOException, EBCorruptedException {
        if (apiBlock == null && requestData == null){
            throw new IllegalArgumentException("Call was not built with request data, cannot build now - no data");
        } else if (requestData != null){
            build(requestData, offset, length);
        }

        this.connector = engine.getConMgr().getConnector(this.endpoint);
        this.connector.setEndpoint(this.endpoint);
        this.connector.setSettings(this.settings);
        this.connector.setRawRequest(rawRequest);

        LOG.trace("Going to call request...");
        this.rawResponse = this.connector.request();

        // Empty response to parse data to.
        EBProcessDataResponse.Builder builder = new EBProcessDataResponse.Builder();
        builder.setRawResponse(rawResponse);

        if (!rawResponse.isSuccessful()){
            LOG.info("Response was not successful: " + rawResponse.toString());
            pdResponse = builder.build();
            return pdResponse;
        }

        // Parse process data response.
        pdResponseParser = new EBProcessDataResponseParser();
        pdResponseParser.setUo(getUo());
        pdResponseParser.parseResponse(new JSONObject(rawResponse.getBody()), builder, null);

        // Return connector.
        engine.getConMgr().doneWithConnector(connector);
        this.connector = null;

        pdResponse = builder.build();
        return pdResponse;
    }

    /**
     * Returns process data output in one call.
     * @return result of process data call
     * @throws IOException
     * @throws EBCorruptedException
     */
    public byte[] processData() throws IOException, EBCorruptedException {
        return processData(null);
    }

    /**
     * Returns process data output in one call.
     * @param input buffer to process
     * @return result of process data call
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

    protected void rethrowProcessDataError(Object t) throws IOException, EBCorruptedException {
        if (t instanceof EBCorruptedException){
            throw (EBCorruptedException)t;
        } else if (t instanceof IOException){
            throw (IOException)t;
        }
    }
}
