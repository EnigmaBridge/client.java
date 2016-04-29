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

    public int getConnectTimeoutMilli() {
        return connectTimeoutMilli;
    }

    public void setConnectTimeoutMilli(int connectTimeoutMilli) {
        this.connectTimeoutMilli = connectTimeoutMilli;
    }

    public int getReadTimeoutMilli() {
        return readTimeoutMilli;
    }

    public void setReadTimeoutMilli(int readTimeoutMilli) {
        this.readTimeoutMilli = readTimeoutMilli;
    }

    public int getWriteTimeoutMilli() {
        return writeTimeoutMilli;
    }

    public void setWriteTimeoutMilli(int writeTimeoutMilli) {
        this.writeTimeoutMilli = writeTimeoutMilli;
    }
}
