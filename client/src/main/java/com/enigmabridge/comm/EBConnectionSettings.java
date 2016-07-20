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

    public static final int DEFAULT_CONNECT_TIMEOUT_MILLI = 60000;
    public static final int DEFAULT_READ_TIMEOUT_MILLI = 60000;
    public static final int DEFAULT_WRITE_TIMEOUT_MILLI = 60000;

    /**
     * Timeout for connecting to the endpoint in milliseconds.
     */
    protected int connectTimeoutMilli = DEFAULT_CONNECT_TIMEOUT_MILLI;

    /**
     * Timeout for reading data from the endpoint.
     */
    protected int readTimeoutMilli = DEFAULT_READ_TIMEOUT_MILLI;

    /**
     * Timeout for writing data to the endpoint.
     */
    protected int writeTimeoutMilli = DEFAULT_WRITE_TIMEOUT_MILLI;

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

        connectTimeoutMilli = json.has(FIELD_CONNECT_TIMEOUT) ?
                EBUtils.getAsInteger(json, FIELD_CONNECT_TIMEOUT, 10) :
                DEFAULT_CONNECT_TIMEOUT_MILLI;

        readTimeoutMilli = json.has(FIELD_READ_TIMEOUT) ?
                EBUtils.getAsInteger(json, FIELD_READ_TIMEOUT, 10) :
                DEFAULT_READ_TIMEOUT_MILLI;

        writeTimeoutMilli = json.has(FIELD_WRITE_TIMEOUT) ?
                EBUtils.getAsInteger(json, FIELD_WRITE_TIMEOUT, 10) :
                DEFAULT_WRITE_TIMEOUT_MILLI;

        method = json.has(FIELD_HTTP_METHOD) ?
                EBUtils.getAsStringOrNull(json, FIELD_HTTP_METHOD) :
                EBCommUtils.METHOD_DEFAULT;

        if (json.has(FIELD_TRUST)){
            setTrust(new EBAdditionalTrust(json.getJSONObject(FIELD_TRUST)));
        }
    }

    @Override
    public JSONObject toJSON(JSONObject json) {
        if (json == null){
            json = new JSONObject();
        }

        if (getConnectTimeoutMilli() != DEFAULT_CONNECT_TIMEOUT_MILLI){
            json.put(FIELD_CONNECT_TIMEOUT, getConnectTimeoutMilli());
        }

        if (getReadTimeoutMilli() != DEFAULT_READ_TIMEOUT_MILLI){
            json.put(FIELD_READ_TIMEOUT, getReadTimeoutMilli());
        }

        if (getWriteTimeoutMilli() != DEFAULT_WRITE_TIMEOUT_MILLI){
            json.put(FIELD_WRITE_TIMEOUT, getWriteTimeoutMilli());
        }

        if (!EBCommUtils.METHOD_DEFAULT.equals(getMethod())){
            json.put(FIELD_HTTP_METHOD, getMethod());
        }

        if (getTrust() != null){
            json.put(FIELD_TRUST, getTrust().toJSON(null));
        }

        return json;
    }

    @Override
    public String toString() {
        return "EBConnectionSettings{" +
                "connectTimeoutMilli=" + connectTimeoutMilli +
                ", readTimeoutMilli=" + readTimeoutMilli +
                ", writeTimeoutMilli=" + writeTimeoutMilli +
                ", method='" + method + '\'' +
                ", trust=" + trust +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBConnectionSettings that = (EBConnectionSettings) o;

        if (connectTimeoutMilli != that.connectTimeoutMilli) return false;
        if (readTimeoutMilli != that.readTimeoutMilli) return false;
        if (writeTimeoutMilli != that.writeTimeoutMilli) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        return trust != null ? trust.equals(that.trust) : that.trust == null;

    }

    @Override
    public int hashCode() {
        int result = connectTimeoutMilli;
        result = 31 * result + readTimeoutMilli;
        result = 31 * result + writeTimeoutMilli;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (trust != null ? trust.hashCode() : 0);
        return result;
    }

    public EBConnectionSettings copy(){
        final EBConnectionSettings n = new EBConnectionSettings();
        n.method = this.method;
        n.writeTimeoutMilli = this.writeTimeoutMilli;
        n.readTimeoutMilli = this.readTimeoutMilli;
        n.connectTimeoutMilli = this.connectTimeoutMilli;
        n.trust = this.trust == null ? null : this.trust.copy();

        return n;
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
