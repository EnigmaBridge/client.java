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
    public static final String FIELD_SETTINGS = "settings";

    /**
     * User object handle.
     */
    protected long uoid = -1;

    /**
     * Type of the user object.
     * Required for API token build.
     */
    protected long userObjectType = -1;

    /**
     * Communication keys.
     */
    protected EBCommKeys commKeys = new EBCommKeys();

    /**
     * Settings required for UO use.
     */
    protected EBSettingsBase settings;

    public static abstract class AbstractBuilder<T extends UserObjectInfoBase, B extends AbstractBuilder> {
        public B setUoid(long a) {
            getObj().setUoid(a);
            return getThisBuilder();
        }

        public B setUserObjectType(long b) {
            getObj().setUserObjectType(b);
            return getThisBuilder();
        }

        public B setCommKeys(EBCommKeys ck){
            getObj().setCommKeys(ck);
            return getThisBuilder();
        }

        public B setSettings(EBSettings settings){
            if (settings.getApiKey() != null){
                getObj().setApiKey(settings.getApiKey());
            }
            if (settings.getEndpointInfo() != null){
                getObj().setEndpointInfo(settings.getEndpointInfo());
            }
            if (settings.getConnectionSettings() != null){
                getObj().setConnectionSettings(settings.getConnectionSettings());
            }
            return getThisBuilder();
        }

        public B useSettings(EBSettingsBase settings){
            getObj().setSettings(settings);
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

    public static class Builder extends AbstractBuilder<UserObjectInfoBase, Builder> {
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
        this.settings = new EBSettingsBase();
        this.settings.setApiKey(apiKey);
    }

    public UserObjectInfoBase(long uoid, byte[] encKey, byte[] macKey, String apiKey, EBEndpointInfo endpointInfo) {
        this.uoid = uoid;
        this.commKeys.encKey = encKey;
        this.commKeys.macKey = macKey;
        this.settings = new EBSettingsBase();
        this.settings.setApiKey(apiKey);
        this.settings.setEndpointInfo(endpointInfo);
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

        // UO ID
        setUoid(EBUtils.getAsLong(json, FIELD_UOID, 16));

        // UO type
        final Long uotype = EBUtils.tryGetAsLong(json, FIELD_UOTYPE, 16);
        setUserObjectType(uotype == null ? -1 : uotype);

        // Comm keys
        setCommKeys(new EBCommKeys(json.getJSONObject(FIELD_COMMKEYS)));

        // EB Settings
        if (json.has(FIELD_SETTINGS)){
            setSettings(new EBSettingsBase(json.getJSONObject(FIELD_SETTINGS)));
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
        json.put(FIELD_COMMKEYS, getCommKeys() == null ? null : getCommKeys().toJSON(null));
        json.put(FIELD_SETTINGS, getSettings() == null ? null : getSettings().toJSON(null));
        return json;
    }

    @Override
    public String toString() {
        return "UserObjectInfoBase{" +
                "uoid=" + uoid +
                ", userObjectType=" + userObjectType +
                ", commKeys=" + commKeys +
                ", settings=" + settings +
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
        return settings != null ? settings.equals(that.settings) : that.settings == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (uoid ^ (uoid >>> 32));
        result = 31 * result + (int) (userObjectType ^ (userObjectType >>> 32));
        result = 31 * result + (commKeys != null ? commKeys.hashCode() : 0);
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        return result;
    }

    public long getUoid() {
        return uoid;
    }

    public long getUserObjectType() {
        return userObjectType;
    }

    public EBCommKeys getCommKeys() {
        return commKeys;
    }

    public String getApiKey() {
        return getSettings() == null ? null : getSettings().apiKey;
    }

    public EBEndpointInfo getEndpointInfo() {
        return getSettings() == null ? null : getSettings().endpointInfo;
    }

    public EBConnectionSettings getConnectionSettings() {
        return getSettings() == null ? null : getSettings().connectionSettings;
    }

    public byte[] getEncKey() {
        return commKeys.encKey;
    }

    public byte[] getMacKey() {
        return commKeys.macKey;
    }

    public EBSettingsBase getSettings() {
        return settings;
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

    protected void setCommKeys(EBCommKeys commKeys) {
        this.commKeys = commKeys;
    }

    protected void setUserObjectType(long userObjectType) {
        this.userObjectType = userObjectType;
    }

    protected UserObjectInfoBase setApiKey(String apiKey) {
        if (getSettings() == null){
            settings = new EBSettingsBase();
        }
        getSettings().setApiKey(apiKey);
        return this;
    }

    protected UserObjectInfoBase setEndpointInfo(EBEndpointInfo endpointInfo) {
        if (getSettings() == null){
            settings = new EBSettingsBase();
        }
        getSettings().setEndpointInfo(endpointInfo);
        return this;
    }

    protected UserObjectInfoBase setConnectionSettings(EBConnectionSettings connectionSettings) {
        if (getSettings() == null){
            settings = new EBSettingsBase();
        }
        getSettings().setConnectionSettings(connectionSettings);
        return this;
    }

    protected void setSettings(EBSettingsBase settings) {
        this.settings = settings;
    }
}
