package com.enigmabridge;

import com.enigmabridge.comm.EBConnectorManager;

import java.security.SecureRandom;

/**
 * Global object for performing EB requests.
 * Created by dusanklinec on 27.04.16.
 */
public class EBEngine {
    /**
     * Connection manager
     */
    protected EBConnectorManager conMgr;

    protected SecureRandom rnd;

    public EBConnectorManager getConMgr() {
        if (conMgr == null){
            conMgr = new EBConnectorManager();
        }
        return conMgr;
    }

    public SecureRandom getRnd() {
        return rnd;
    }
}
