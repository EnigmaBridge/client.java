package com.enigmabridge.comm;

/**
 * Misc connection preferences for connectors.
 * Created by dusanklinec on 26.04.16.
 */
public class EBConnectionSettings {
    /**
     * Timeout for reading data in milliseconds.
     */
    protected int timeoutMilliseconds = 30000;

    public int getTimeoutMilliseconds() {
        return timeoutMilliseconds;
    }

    public void setTimeoutMilliseconds(int timeoutMilliseconds) {
        this.timeoutMilliseconds = timeoutMilliseconds;
    }
}
