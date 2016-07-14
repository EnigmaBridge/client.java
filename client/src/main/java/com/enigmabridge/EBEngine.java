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

    /**
     * Shared secure random instance
     */
    protected SecureRandom rnd;

    /**
     * Default EB settings for using EB service.
     */
    protected EBSettings defaultSettings;

    /**
     * Endpoint for enrollment.
     */
    protected EBEndpointInfo enrollmentEndpoint;

    public EBConnectorManager getConMgr() {
        if (conMgr == null){
            conMgr = new EBConnectorManager();
        }
        return conMgr;
    }

    public SecureRandom getRnd() {
        if (rnd == null){
            rnd = new SecureRandom();
        }
        return rnd;
    }

    public EBSettings getDefaultSettings() {
        return defaultSettings;
    }

    public void setDefaultSettings(EBSettings defaultSettings) {
        this.defaultSettings = defaultSettings;
    }

    public void setEnrollmentEndpoint(EBEndpointInfo enrollmentEndpoint) {
        this.enrollmentEndpoint = enrollmentEndpoint;
    }

    public EBEndpointInfo getEnrollmentEndpoint() {
        if (enrollmentEndpoint == null){
            final EBEndpointInfo endpoint = getDefaultSettings().getEndpointInfo();
            if (endpoint != null){
                return endpoint.copy().setPort(EBEndpointInfo.DEFAULT_ENROLLMENT_PORT);
            }
        }

        return enrollmentEndpoint;
    }
}
