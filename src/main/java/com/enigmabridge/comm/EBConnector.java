package com.enigmabridge.comm;

/**
 * Connector to EB endpoint. It enables to call requests on the EB API.
 *
 * In future this may provide outputStream
 * Created by dusanklinec on 26.04.16.
 */
public class EBConnector {
    /**
     * Request settings.
     */
    protected EBConnectionSettings settings;



    public EBConnectionSettings getSettings() {
        return settings;
    }

    public EBConnector setSettings(EBConnectionSettings settings) {
        this.settings = settings;
        return this;
    }
}
