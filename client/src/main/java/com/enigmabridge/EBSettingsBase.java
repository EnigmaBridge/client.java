package com.enigmabridge;

import com.enigmabridge.comm.EBConnectionSettings;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * Basic EB settings for UO operation.
 * Created by dusanklinec on 04.05.16.
 */
public class EBSettingsBase implements EBSettings, Serializable {
    public static final long serialVersionUID = 1L;
    public static final String FIELD_APIKEY = "apiKey";
    public static final String FIELD_ENDPOINT = "endpoint";
    public static final String FIELD_SETTINGS = "settings";

    /**
     * API key for using EB service.
     */
    protected String apiKey;

    /**
     * Connection string to the EB endpoint
     * https://site1.enigmabridge.com:11180
     */
    protected EBEndpointInfo endpointInfo;

    /**
     * Connection settings for UO operation.
     */
    protected EBConnectionSettings connectionSettings;

    public static abstract class AbstractBuilder<T extends EBSettingsBase, B extends AbstractBuilder> {
        public B setApiKey(String apiKey){
            getObj().setApiKey(apiKey);
            return getThisBuilder();
        }

        public B setEndpointInfo(EBEndpointInfo info){
            getObj().setEndpointInfo(info);
            return getThisBuilder();
        }

        public B setConnectionSettings(EBConnectionSettings cs){
            getObj().setConnectionSettings(cs);
            return getThisBuilder();
        }

        public B setJson(JSONObject json) throws MalformedURLException {
            getObj().fromJSON(json);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBSettingsBase, Builder> {
        private final EBSettingsBase parent = new EBSettingsBase();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBSettingsBase getObj() {
            return parent;
        }

        @Override
        public EBSettingsBase build() {
            return parent;
        }
    }

    public EBSettingsBase() {
    }

    public EBSettingsBase(String apiKey, EBEndpointInfo endpointInfo, EBConnectionSettings connectionSettings) {
        this.apiKey = apiKey;
        this.endpointInfo = endpointInfo;
        this.connectionSettings = connectionSettings;
    }

    public EBSettingsBase(JSONObject json) throws MalformedURLException {
        fromJSON(json);
    }

    /**
     * Initializes object form the JSON.
     * @param json
     * @throws MalformedURLException
     */
    protected void fromJSON(JSONObject json) throws MalformedURLException {
        if (json == null
                || !json.has(FIELD_APIKEY)
                || !json.has(FIELD_ENDPOINT))
        {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        setApiKey(EBUtils.getAsStringOrNull(json, FIELD_APIKEY));

        final String endpointStr = EBUtils.getAsStringOrNull(json, FIELD_ENDPOINT);
        if (endpointStr != null){
            setEndpointInfo(new EBEndpointInfo(endpointStr));
        }

        if (json.has(FIELD_SETTINGS)){
            setConnectionSettings(new EBConnectionSettings(json.getJSONObject(FIELD_SETTINGS)));
        }
    }

    /**
     * Serializes to JSON.
     * @param json
     * @return
     */
    public JSONObject toJSON(JSONObject json){
        if (json == null){
            json = new JSONObject();
        }

        json.put(FIELD_APIKEY, this.getApiKey());
        json.put(FIELD_ENDPOINT, getEndpointInfo() == null ? null : getEndpointInfo().getConnectionString());
        json.put(FIELD_SETTINGS, getConnectionSettings() == null ? null : getConnectionSettings().toJSON(null));
        return json;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public EBEndpointInfo getEndpointInfo() {
        return endpointInfo;
    }

    @Override
    public EBConnectionSettings getConnectionSettings() {
        return connectionSettings;
    }

    // Protected setters.
    protected void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected void setEndpointInfo(EBEndpointInfo endpointInfo) {
        this.endpointInfo = endpointInfo;
    }

    protected void setConnectionSettings(EBConnectionSettings connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    @Override
    public String toString() {
        return "EBSettingsBase{" +
                "apiKey='" + apiKey + '\'' +
                ", endpointInfo=" + endpointInfo +
                ", connectionSettings=" + connectionSettings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBSettingsBase that = (EBSettingsBase) o;

        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        if (endpointInfo != null ? !endpointInfo.equals(that.endpointInfo) : that.endpointInfo != null) return false;
        return connectionSettings != null ? connectionSettings.equals(that.connectionSettings) : that.connectionSettings == null;

    }

    @Override
    public int hashCode() {
        int result = apiKey != null ? apiKey.hashCode() : 0;
        result = 31 * result + (endpointInfo != null ? endpointInfo.hashCode() : 0);
        result = 31 * result + (connectionSettings != null ? connectionSettings.hashCode() : 0);
        return result;
    }
}
