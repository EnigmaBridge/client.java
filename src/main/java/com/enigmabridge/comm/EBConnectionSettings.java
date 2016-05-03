package com.enigmabridge.comm;

/**
 * Misc connection preferences for connectors.
 * Created by dusanklinec on 26.04.16.
 */
public class EBConnectionSettings {
    /**
     * Timeout for connecting to the endpoint in milliseconds.
     */
    protected int connectTimeoutMilli = 30000;

    /**
     * Timeout for reading data from the endpoint.
     */
    protected int readTimeoutMilli = 30000;

    /**
     * Timeout for writing data to the endpoint.
     */
    protected int writeTimeoutMilli = 30000;

    /**
     * Method used for the API call.
     */
    protected String method = EBCommUtils.METHOD_DEFAULT;

    /**
     * Custom trust roots for SSL/TLS.
     */
    protected EBAdditionalTrust trust;

    public int getConnectTimeoutMilli() {
        return connectTimeoutMilli;
    }

    public EBConnectionSettings setConnectTimeoutMilli(int connectTimeoutMilli) {
        this.connectTimeoutMilli = connectTimeoutMilli;
        return this;
    }

    public int getReadTimeoutMilli() {
        return readTimeoutMilli;
    }

    public EBConnectionSettings setReadTimeoutMilli(int readTimeoutMilli) {
        this.readTimeoutMilli = readTimeoutMilli;
        return this;
    }

    public int getWriteTimeoutMilli() {
        return writeTimeoutMilli;
    }

    public EBConnectionSettings setWriteTimeoutMilli(int writeTimeoutMilli) {
        this.writeTimeoutMilli = writeTimeoutMilli;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public EBConnectionSettings setMethod(String method) {
        this.method = method;
        return this;
    }

    public EBAdditionalTrust getTrust() {
        return trust;
    }

    public EBConnectionSettings setTrust(EBAdditionalTrust trust) {
        this.trust = trust;
        return this;
    }
}
