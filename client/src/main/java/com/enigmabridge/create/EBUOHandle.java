package com.enigmabridge.create;

import com.enigmabridge.UserObjectType;

/**
 * User object handle.
 * Util method.
 *
 * Created by dusanklinec on 01.07.16.
 */
public class EBUOHandle {
    protected String apiKey;
    protected long uoId;
    protected UserObjectType uoType;

    public EBUOHandle() {
    }

    public EBUOHandle(String apiKey, long uoId, long uoType) {
        this.apiKey = apiKey;
        this.uoId = uoId;
        this.uoType = new UserObjectType(uoType);
    }

    // Setters

    public EBUOHandle setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public EBUOHandle setUoId(long uoId) {
        this.uoId = uoId;
        return this;
    }

    public EBUOHandle setUoType(long uoType) {
        this.uoType = new UserObjectType(uoType);
        return this;
    }

    // Getters

    public String getApiKey() {
        return apiKey;
    }

    public long getUoId() {
        return uoId;
    }

    public UserObjectType getUoType() {
        return uoType;
    }

    public String getHandle(){
        return String.format("%s00%08x00%08x", apiKey, uoId, uoType.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBUOHandle that = (EBUOHandle) o;

        if (uoId != that.uoId) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        return uoType != null ? uoType.equals(that.uoType) : that.uoType == null;

    }

    @Override
    public int hashCode() {
        int result = apiKey != null ? apiKey.hashCode() : 0;
        result = 31 * result + (int) (uoId ^ (uoId >>> 32));
        result = 31 * result + (uoType != null ? uoType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getHandle();
    }
}
