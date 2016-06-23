package com.enigmabridge.comm;

import com.enigmabridge.EBEndpointInfo;

/**
 * EB Connector manager. Manages cache/pool of connectors for given endpoints.
 * Created by dusanklinec on 26.04.16.
 */
public class EBConnectorManager {
    // TODO: implement

    /**
     * Builds a new connector to the given EB endpoint.
     * It may reuse already opened connector to the EB endpoint from the pool.
     *
     * @param endpoint Endpoint to construct connector to.
     * @return
     */
    public EBConnector getConnector(EBEndpointInfo endpoint){
        // TODO: implement.
        return new EBConnector().setEndpoint(endpoint);
    }

    /**
     * Called when caller is done with the connector.
     * It is returned to the pool of connectors for given endpoint.
     * @return
     */
    public EBConnectorManager doneWithConnector(EBConnector connector){
        // TODO: implement.
        return this;
    }
}
