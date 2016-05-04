package com.enigmabridge.comm;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBJSONSerializable;
import com.enigmabridge.EBUtils;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * Misc connection preferences for connectors.
 * Created by dusanklinec on 26.04.16.
 */
public class EBConnectionSettings implements Serializable, EBJSONSerializable {
    public static final long serialVersionUID = 1L;
    public static final String FIELD_CONNECT_TIMEOUT = "connectTimeout";
    public static final String FIELD_READ_TIMEOUT = "readTimeout";
    public static final String FIELD_WRITE_TIMEOUT = "writeTimeout";
    public static final String FIELD_HTTP_METHOD = "httpMethod";
    public static final String FIELD_TRUST = "trust";

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

    public EBConnectionSettings() {
    }

    public EBConnectionSettings(JSONObject json) {
        fromJSON(json);
    }

    protected void fromJSON(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        if (json.has(FIELD_CONNECT_TIMEOUT)){
            connectTimeoutMilli = EBUtils.getAsInteger(json, FIELD_CONNECT_TIMEOUT, 10);
        }

        if (json.has(FIELD_READ_TIMEOUT)){
            readTimeoutMilli = EBUtils.getAsInteger(json, FIELD_READ_TIMEOUT, 10);
        }

        if (json.has(FIELD_WRITE_TIMEOUT)){
            writeTimeoutMilli = EBUtils.getAsInteger(json, FIELD_WRITE_TIMEOUT, 10);
        }

        if (json.has(FIELD_HTTP_METHOD)){
            method = EBUtils.getAsStringOrNull(json, FIELD_HTTP_METHOD);
        }

        if (json.has(FIELD_TRUST)){
            setTrust(new EBAdditionalTrust(json.getJSONObject(FIELD_TRUST)));
        }
    }

    @Override
    public JSONObject toJSON(JSONObject json) {
        if (json == null){
            json = new JSONObject();
        }

        json.put(FIELD_CONNECT_TIMEOUT, getConnectTimeoutMilli());
        json.put(FIELD_READ_TIMEOUT, getReadTimeoutMilli());
        json.put(FIELD_WRITE_TIMEOUT, getWriteTimeoutMilli());
        json.put(FIELD_HTTP_METHOD, getMethod());

        if (getTrust() != null){
            json.put(FIELD_TRUST, getTrust().toJSON(null));
        }

        return json;
    }

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
