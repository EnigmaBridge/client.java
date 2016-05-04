package com.enigmabridge;

import com.enigmabridge.comm.EBConnectionSettings;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;

/**
 * Holder represents user object and all required parameters for using it.
 *
 * Created by dusanklinec on 21.04.16.
 */
public class UserObjectInfoBase implements UserObjectInfo, EBJSONSerializable {
    public static final long serialVersionUID = 1L;
    public static final String FIELD_UOID = "uoid";
    public static final String FIELD_UOTYPE = "uotype";
    public static final String FIELD_COMMKEYS = "commKeys";
    public static final String FIELD_APIKEY = "apiKey";
    public static final String FIELD_ENDPOINT = "endpoint";
    public static final String FIELD_SETTINGS = "settings";

    /**
     * User object handle.
     */
    protected long uoid;

    /**
     * Type of the user object.
     * Required for API token build.
     */
    protected int userObjectType;

    /**
     * Communication keys.
     */
    protected EBCommKeys commKeys = new EBCommKeys();

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

    public static abstract class AbstractUOBaseBuilder<T extends UserObjectInfoBase, B extends AbstractUOBaseBuilder> {
        public B setUoid(long a) {
            getObj().setUoid(a);
            return getThisBuilder();
        }

        public B setUserObjectType(int b) {
            getObj().setUserObjectType(b);
            return getThisBuilder();
        }

        public B setApiKey(String apiKey){
            getObj().setApiKey(apiKey);
            return getThisBuilder();
        }

        public B setEndpointInfo(EBEndpointInfo info){
            getObj().setEndpointInfo(info);
            return getThisBuilder();
        }

        public B setCommKeys(EBCommKeys ck){
            getObj().setCommKeys(ck);
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

    public static class Builder extends AbstractUOBaseBuilder<UserObjectInfoBase, Builder> {
        private final UserObjectInfoBase parent = new UserObjectInfoBase();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public UserObjectInfoBase getObj() {
            return parent;
        }

        @Override
        public UserObjectInfoBase build() {
            return parent;
        }
    }

    public UserObjectInfoBase() {
    }

    public UserObjectInfoBase(long uoid) {
        this.uoid = uoid;
    }

    public UserObjectInfoBase(long uoid, byte[] encKey, byte[] macKey) {
        this.uoid = uoid;
        this.commKeys.encKey = encKey;
        this.commKeys.macKey = macKey;
    }

    public UserObjectInfoBase(long uoid, byte[] encKey, byte[] macKey, String apiKey) {
        this.uoid = uoid;
        this.commKeys.encKey = encKey;
        this.commKeys.macKey = macKey;
        this.apiKey = apiKey;
    }

    public UserObjectInfoBase(long uoid, byte[] encKey, byte[] macKey, String apiKey, EBEndpointInfo endpointInfo) {
        this.uoid = uoid;
        this.commKeys.encKey = encKey;
        this.commKeys.macKey = macKey;
        this.apiKey = apiKey;
        this.endpointInfo = endpointInfo;
    }

    /**
     * Builds UserObjectInfoBase from serialized form.
     * @param encoded byte representation of the object
     * @return new object loaded from byte representation
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static UserObjectInfoBase build(byte[] encoded) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = null;
        ObjectInput in = null;
        try {
            bis = new ByteArrayInputStream(encoded);
            in = new ObjectInputStream(bis);
            return (UserObjectInfoBase) in.readObject();

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Serializes object to the byte array
     * @return encoded object representation.
     * @throws IOException
     */
    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            return bos.toByteArray();

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Builds user object from string representation.
     * @param json
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static UserObjectInfoBase build(JSONObject json) throws IOException {
        final UserObjectInfoBase b = new UserObjectInfoBase();
        b.fromJSON(json);
        return b;
    }

    /**
     * Initializes object form the JSON.
     * @param json
     * @throws MalformedURLException
     */
    protected void fromJSON(JSONObject json) throws MalformedURLException {
        if (json == null
                || !json.has(FIELD_UOID)
                || !json.has(FIELD_COMMKEYS))
        {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        setUoid(EBUtils.getAsLong(json, FIELD_UOID, 16));

        final Integer uotype = EBUtils.tryGetAsInteger(json, FIELD_UOTYPE, 16);
        setUserObjectType(uotype == null ? -1 : uotype);

        setApiKey(EBUtils.getAsStringOrNull(json, FIELD_APIKEY));

        final String endpointStr = EBUtils.getAsStringOrNull(json, FIELD_ENDPOINT);
        if (endpointStr != null){
            setEndpointInfo(new EBEndpointInfo(endpointStr));
        }

        setCommKeys(new EBCommKeys(json.getJSONObject(FIELD_COMMKEYS)));

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

        json.put(FIELD_UOID, this.getUoid());
        json.put(FIELD_UOTYPE, this.getUserObjectType());
        json.put(FIELD_APIKEY, this.getApiKey());
        json.put(FIELD_ENDPOINT, getEndpointInfo() == null ? null : getEndpointInfo().getConnectionString());
        json.put(FIELD_COMMKEYS, getCommKeys() == null ? null : getCommKeys().toJSON(null));
        json.put(FIELD_SETTINGS, getConnectionSettings() == null ? null : getConnectionSettings().toJSON(null));
        return json;
    }

    @Override
    public String toString() {
        return "UserObjectInfoBase{" +
                "uoid=" + uoid +
                ", userObjectType=" + userObjectType +
                ", commKeys=" + commKeys +
                ", apiKey='" + apiKey + '\'' +
                ", endpointInfo=" + endpointInfo +
                ", connectionSettings=" + connectionSettings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserObjectInfoBase that = (UserObjectInfoBase) o;

        if (uoid != that.uoid) return false;
        if (userObjectType != that.userObjectType) return false;
        if (commKeys != null ? !commKeys.equals(that.commKeys) : that.commKeys != null) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        if (endpointInfo != null ? !endpointInfo.equals(that.endpointInfo) : that.endpointInfo != null) return false;
        return connectionSettings != null ? connectionSettings.equals(that.connectionSettings) : that.connectionSettings == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (uoid ^ (uoid >>> 32));
        result = 31 * result + userObjectType;
        result = 31 * result + (commKeys != null ? commKeys.hashCode() : 0);
        result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
        result = 31 * result + (endpointInfo != null ? endpointInfo.hashCode() : 0);
        result = 31 * result + (connectionSettings != null ? connectionSettings.hashCode() : 0);
        return result;
    }

    public long getUoid() {
        return uoid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public EBCommKeys getCommKeys() {
        return commKeys;
    }

    public int getUserObjectType() {
        return userObjectType;
    }

    public EBEndpointInfo getEndpointInfo() {
        return endpointInfo;
    }

    public EBConnectionSettings getConnectionSettings() {
        return connectionSettings;
    }

    public byte[] getEncKey() {
        return commKeys.encKey;
    }

    public byte[] getMacKey() {
        return commKeys.macKey;
    }

    // Protected setters - object can be created only with builders.
    protected UserObjectInfoBase setUoid(long uoid) {
        this.uoid = uoid;
        return this;
    }

    protected UserObjectInfoBase setEncKey(byte[] encKey) {
        this.commKeys.encKey = encKey;
        return this;
    }

    protected UserObjectInfoBase setMacKey(byte[] macKey) {
        this.commKeys.macKey = macKey;
        return this;
    }

    protected UserObjectInfoBase setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    protected void setCommKeys(EBCommKeys commKeys) {
        this.commKeys = commKeys;
    }

    protected void setUserObjectType(int userObjectType) {
        this.userObjectType = userObjectType;
    }

    protected UserObjectInfoBase setEndpointInfo(EBEndpointInfo endpointInfo) {
        this.endpointInfo = endpointInfo;
        return this;
    }

    protected UserObjectInfoBase setConnectionSettings(EBConnectionSettings connectionSettings) {
        this.connectionSettings = connectionSettings;
        return this;
    }
}
