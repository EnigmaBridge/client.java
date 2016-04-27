package com.enigmabridge;

import com.enigmabridge.comm.EBConnectorManager;

/**
 * Global object for performing EB requests.
 * Created by dusanklinec on 27.04.16.
 */
public class EBEngine {
    /**
     * Connection manager
     */
    protected EBConnectorManager conMgr;
    
    public EBConnectorManager getConMgr() {
        return conMgr;
    }
}
