package com.enigmabridge.comm;

import com.enigmabridge.EBEndpointInfo;
import okhttp3.*;

import java.io.IOException;
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
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Request settings.
     */
    protected EBConnectionSettings settings;

    protected EBEndpointInfo endpoint;

    protected EBRawRequest rawRequest;

    /**
     * Do the request, performs real service call.
     */
    public EBRawResponse request() throws IOException {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (settings != null) {
            clientBuilder
                    .connectTimeout(settings.getConnectTimeoutMilli(), TimeUnit.MILLISECONDS)
                    .writeTimeout(settings.getWriteTimeoutMilli(), TimeUnit.MILLISECONDS)
                    .readTimeout(settings.getReadTimeoutMilli(), TimeUnit.MILLISECONDS);
        }

        final OkHttpClient client = clientBuilder.build();

        final HttpUrl url = HttpUrl.parse("").newBuilder()
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
        if (EBRawRequest.METHOD_GET.equals(method)){
            // Nothing to do really.

        } else if (EBRawRequest.METHOD_POST.equals(method)){
            final RequestBody body = RequestBody.create(JSON, rawRequest.getBody());
            requestBuilder.post(body);

        } else {
            throw new IllegalArgumentException("Unknown request method: " + method);
        }

        final Request request = requestBuilder.build();

        // Do the call.
        final long timeStart = System.currentTimeMillis();
        final Response response = client.newCall(request).execute();

        final EBRawResponse ebResponse = new EBRawResponse();
        ebResponse.setHttpCode(response.code())
                .setBody(response.body().string())
                .setBodyBytes(response.body().bytes())
                .setResponseTime(System.currentTimeMillis() - timeStart)
                .setSuccessful(response.isSuccessful());

        return ebResponse;
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
