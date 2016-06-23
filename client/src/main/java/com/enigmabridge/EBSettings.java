package com.enigmabridge;

import com.enigmabridge.comm.EBConnectionSettings;

/**
 * Settings required for using EB service.
 * UserObjects have these settings associated.
 *
 * Created by dusanklinec on 04.05.16.
 */
public interface EBSettings {
    /**
     * Returns API key to EB API access.
     * API key can be shared among several UOs.
     * Not UO specific.
     * @return API key
     */
    String getApiKey();

    /**
     * EB Endpoint identification.
     * Not UO specific.
     * @return endpoint info
     */
    EBEndpointInfo getEndpointInfo();

    /**
     * Returns connection settings for the EB call.
     * @return connection settings
     */
    EBConnectionSettings getConnectionSettings();
}
