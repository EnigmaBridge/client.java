package com.enigmabridge;

import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Basic implementation of the UserObjectKey.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class UserObjectKeyBase extends UserObjectInfoBase implements UserObjectKey {
    public static final String FIELD_ALGORITHM = "algorithm";
    public static final String FIELD_KEY_LENGTH = "keyLen";
    public static final String FIELD_KEY_TYPE = "keyType";

    protected String algorithm;
    protected int keyLength;
    protected UserObjectKeyType keyType;

    public static abstract class AbstractBuilder<T extends UserObjectKeyBase, B extends AbstractBuilder>
    extends UserObjectInfoBase.AbstractBuilder<T,B>
    {
        public B setAlgorithm(String algorithm) {
            getObj().setAlgorithm(algorithm);
            return getThisBuilder();
        }

        public B setKeyLength(int keyLen) {
            getObj().setKeyLength(keyLen);
            return getThisBuilder();
        }

        public B setKeyType(UserObjectKeyType keyType){
            getObj().setKeyType(keyType);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<UserObjectKeyBase, Builder> {
        private final UserObjectKeyBase parent = new UserObjectKeyBase();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public UserObjectKeyBase getObj() {
            return parent;
        }

        @Override
        public UserObjectKeyBase build() {
            return parent;
        }
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public int length() {
        return keyLength;
    }

    @Override
    public UserObjectKeyType getKeyType() {
        return keyType;
    }

    @Override
    public UserObjectInfo getUserObjectInfo() {
        return this;
    }

    @Override
    public UserObjectKey getUserObjectKey() {
        return this;
    }

    /**
     * Initializes object form the JSON.
     * @param json
     * @throws MalformedURLException
     */
    @Override
    protected void fromJSON(JSONObject json) throws MalformedURLException {
        super.fromJSON(json);
        if (json == null
                || !json.has(FIELD_ALGORITHM)
                || !json.has(FIELD_KEY_LENGTH)
                || !json.has(FIELD_KEY_TYPE))
        {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        setAlgorithm(EBUtils.getAsStringOrNull(json, FIELD_ALGORITHM));
        setKeyLength(EBUtils.getAsInteger(json, FIELD_KEY_LENGTH, 10));
        setKeyType(UserObjectKeyType.valueOf(EBUtils.getAsStringOrNull(json, FIELD_KEY_TYPE)));
    }

    /**
     * Serializes to JSON.
     * @param json
     * @return
     */
    @Override
    public JSONObject toJSON(JSONObject json) {
        json = super.toJSON(json);
        if (json == null){
            json = new JSONObject();
        }

        json.put(FIELD_ALGORITHM, this.getAlgorithm());
        json.put(FIELD_KEY_LENGTH, this.length());
        json.put(FIELD_KEY_TYPE, this.getKeyType().toString());
        return json;
    }

    // Protected setters - object can be created only with builders.
    protected void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    protected void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    protected void setKeyType(UserObjectKeyType keyType) {
        this.keyType = keyType;
    }
}
