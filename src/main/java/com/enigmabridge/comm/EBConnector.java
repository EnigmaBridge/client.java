package com.enigmabridge.comm;

import com.enigmabridge.EBEndpointInfo;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Connector to EB endpoint. It enables to call raw requests on the EB API.
 * Wraps HTTP query library, processes connection settings. Can e.g., perform multiple attempts before failure.
 * Lowest EB level of abstraction for remote calls.
 *
 * In future this may provide outputStream
 * Created by dusanklinec on 26.04.16.
 */
public class EBConnector {
    private static final Logger LOG = LoggerFactory.getLogger(EBConnector.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Default trust object, with letsencrypt certificates included
     */
    public static final EBAdditionalTrust DEFAULT_TRUST = new EBAdditionalTrust(true, true, null);

    /**
     * Request settings.
     */
    protected EBConnectionSettings settings;

    protected EBEndpointInfo endpoint;

    protected EBRawRequest rawRequest;

    /**
     * OkHttp call, for cancellation.
     */
    private Call call;

    /**
     * Do the request, performs real service call.
     */
    public EBRawResponse request() throws IOException {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        boolean trustInstalled = false;

        if (settings != null) {
            clientBuilder
                    .connectTimeout(settings.getConnectTimeoutMilli(), TimeUnit.MILLISECONDS)
                    .writeTimeout(settings.getWriteTimeoutMilli(), TimeUnit.MILLISECONDS)
                    .readTimeout(settings.getReadTimeoutMilli(), TimeUnit.MILLISECONDS);

            final EBAdditionalTrust trust = settings.getTrust();
            if (trust != null){
                trust.install(clientBuilder);
                trustInstalled = true;
            }
        }

        // We are using mainly letsencrypt and if there is no trust object provided, we initialize a default one.
        if (!trustInstalled){
            DEFAULT_TRUST.install(clientBuilder);
        }

        final OkHttpClient client = clientBuilder.build();

        final HttpUrl url = new HttpUrl.Builder()
                .scheme(endpoint.getScheme())
                .host(endpoint.getHostname())
                .port(endpoint.getPort())
                .addPathSegments(rawRequest.getQuery())
                .build();

        final Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .header("User-Agent", "EBClient.java")
                .addHeader("Accept", "application/json; q=0.5");

        final String method = rawRequest.getMethod();
        if (EBCommUtils.METHOD_GET.equals(method)){
            // Nothing to do really.

        } else if (EBCommUtils.METHOD_POST.equals(method)){
            final RequestBody body = RequestBody.create(JSON, rawRequest.getBody());
            requestBuilder.post(body);

        } else {
            throw new IllegalArgumentException("Unknown request method: " + method);
        }

        final Request request = requestBuilder.build();

        // Do the call.
        final long timeStart = System.currentTimeMillis();
        call = client.newCall(request);

        // Synchronous call.
        final Response response = call.execute();
        final ResponseBody body = response.body();
        final byte[] respBytes = body.bytes();
        String respString = null;
        try {
            final Charset charset = body.contentType() == null ? null : body.contentType().charset();
            respString = new String(respBytes, charset == null ? "UTF-8" : charset.name());
        } catch(RuntimeException e){
            LOG.error("Exception in converting response bytes to string", e);
        }

        final EBRawResponse ebResponse = new EBRawResponse();
        ebResponse.setHttpCode(response.code())
                .setBodyBytes(respBytes)
                .setBody(respString)
                .setResponseTime(System.currentTimeMillis() - timeStart)
                .setSuccessful(response.isSuccessful());

        return ebResponse;
    }

    public void cancel(){
        if (call!=null){
            call.cancel();
        }
    }

    public EBConnectionSettings getSettings() {
        return settings;
    }

    public EBConnector setSettings(EBConnectionSettings settings) {
        this.settings = settings;
        return this;
    }

    public EBEndpointInfo getEndpoint() {
        return endpoint;
    }

    public EBConnector setEndpoint(EBEndpointInfo endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public EBRawRequest getRawRequest() {
        return rawRequest;
    }

    public EBConnector setRawRequest(EBRawRequest rawRequest) {
        this.rawRequest = rawRequest;
        return this;
    }
}
