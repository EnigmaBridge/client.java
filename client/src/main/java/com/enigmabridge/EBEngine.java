package com.enigmabridge;

import com.enigmabridge.comm.EBConnectorManager;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.SecureRandom;

/**
 * Global object for performing EB requests.
 * Created by dusanklinec on 27.04.16.
 */
public class EBEngine {
    public static final String FIELD_SETTINGS = "settings";
    public static final String FIELD_ENDPOINT_PROCESS = "endpointProcess";
    public static final String FIELD_ENDPOINT_ENROLLMENT = "endpointEnroll";
    public static final String FIELD_ENDPOINT_REGISTER = "endpointRegister";

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
     * Contains retry strategies, API_KEY, process data endpoint.
     */
    protected EBSettings defaultSettings;

    /**
     * Endpoint for enrollment.
     */
    protected EBEndpointInfo endpointEnrollment;

    /**
     * Endpoint for registration
     */
    protected EBEndpointInfo endpointRegistration;

    /**
     * Creates an empty engine
     */
    public EBEngine()  {

    }

    /**
     * Constructs a default engine with common configuration.
     * @return Engine with default settings.
     */
    public static EBEngine defaultEngine() {
        final EBEngine e = new EBEngine();
        e.rnd = new SecureRandom();
        try {
            e.defaultSettings = new EBSettingsBase.Builder()
                    .setEndpointInfo(new EBEndpointInfo("https://site2.enigmabridge.com:11180"))
                    .build();
            e.endpointEnrollment = new EBEndpointInfo("https://site2.enigmabridge.com:11182");
            e.endpointRegistration = new EBEndpointInfo("https://hut6.enigmabridge.com:8445");
        } catch (MalformedURLException urlEx){
            throw new RuntimeException("Predefined endpoints failed", urlEx);
        }

        return e;
    }

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

    public void setEndpointEnrollment(EBEndpointInfo endpointEnrollment) {
        this.endpointEnrollment = endpointEnrollment;
    }

    public void setEndpointRegistration(EBEndpointInfo endpointRegistration) {
        this.endpointRegistration = endpointRegistration;
    }

    public EBEndpointInfo getEndpointEnrollment() {
        if (endpointEnrollment == null){
            final EBEndpointInfo endpoint = getDefaultSettings().getEndpointInfo();
            if (endpoint != null){
                return endpoint.copy().setPort(EBEndpointInfo.DEFAULT_ENROLLMENT_PORT);
            }
        }

        return endpointEnrollment;
    }

    public EBEndpointInfo getEndpointRegistration() {
        return endpointRegistration;
    }

    public JSONObject configureToJSON(){
        return configureToJSON(null, true);
    }

    /**
     * Serializes the configuration to the JSON.
     * Expands settings to the root + adds remaining endpoints.
     * @param json object where to deploy the configuration. If null a new one is created
     * @param compatible if true the compatible JSON format is used, otherwise the more structured one.
     * @return JSONObject with the configuration
     */
    public JSONObject configureToJSON(JSONObject json, boolean compatible){
        if (json == null){
            json = new JSONObject();
        }

        final EBSettingsBase.Builder builder = new EBSettingsBase.Builder().setSettings(getDefaultSettings());

        // Compatible mode (simple): Serialize settings to the root
        // Extended mode: use proper hierarchy (more safe and extensible)
        if (compatible) {
            builder.build().toJSON(json);
        } else {
            json.put(FIELD_SETTINGS, builder.build().toJSON(null));
        }

        json.put(FIELD_ENDPOINT_ENROLLMENT, endpointEnrollment == null ? null : endpointEnrollment);
        json.put(FIELD_ENDPOINT_REGISTER, endpointRegistration == null ? null : endpointRegistration);

        return json;
    }

    public String configureToURL() throws MalformedURLException {
        final EBURLConfig.Builder urlCfgBld = new EBURLConfig.Builder()
                .setFromSettings(getDefaultSettings())
                .addElement(this.endpointEnrollment, FIELD_ENDPOINT_ENROLLMENT)
                .addElement(this.endpointRegistration, FIELD_ENDPOINT_REGISTER);

        return urlCfgBld
                .build()
                .toString();
    }

    public void configureFromJSON(String str) throws MalformedURLException {
        configureFromJSON(new JSONObject(str));
    }

    public void configureFromJSON(JSONObject obj) throws MalformedURLException {
        this.defaultSettings = new EBSettingsBase.Builder()
                .setJson(obj)
                .build();

        final String endpointEnrollStr = EBUtils.getAsStringOrNull(obj, FIELD_ENDPOINT_ENROLLMENT);
        if (endpointEnrollStr != null){
            setEndpointEnrollment(new EBEndpointInfo(endpointEnrollStr));
        }

        final String endpointRegStr = EBUtils.getAsStringOrNull(obj, FIELD_ENDPOINT_REGISTER);
        if (endpointRegStr != null){
            setEndpointRegistration(new EBEndpointInfo(endpointRegStr));
        }

        // Compatibility with other clients - process data
        final String endpointProcessStr = EBUtils.getAsStringOrNull(obj, FIELD_ENDPOINT_PROCESS);
        if (endpointRegStr != null){
            this.defaultSettings = new EBSettingsBase.Builder()
                    .setSettings(defaultSettings)
                    .setEndpointInfo(new EBEndpointInfo(endpointProcessStr))
                    .build();
        }
    }

    public void configureFromURL(String url) throws MalformedURLException, UnsupportedEncodingException {
        final EBURLConfig urlCfg = new EBURLConfig.Builder()
                .setURLConfig(url)
                .build();

        this.defaultSettings = urlCfg;
        final Object endpointEnrollObj = urlCfg.getElementObj(FIELD_ENDPOINT_ENROLLMENT);
        if (endpointEnrollObj != null){
            final String endpointEnroll = (String) endpointEnrollObj;
            setEndpointEnrollment(new EBEndpointInfo(endpointEnroll));
        }

        final Object endpointRegObj = urlCfg.getElementObj(FIELD_ENDPOINT_REGISTER);
        if (endpointRegObj != null){
            final String endpointRegister = (String) endpointRegObj;
            setEndpointRegistration(new EBEndpointInfo(endpointRegister));
        }
    }

    @Override
    public String toString() {
        return "EBEngine{" +
                "conMgr=" + conMgr +
                ", rnd=" + rnd +
                ", defaultSettings=" + defaultSettings +
                ", endpointEnrollment=" + endpointEnrollment +
                ", endpointRegistration=" + endpointRegistration +
                '}';
    }
}
