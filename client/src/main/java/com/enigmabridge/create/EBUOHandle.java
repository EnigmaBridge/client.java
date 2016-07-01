package com.enigmabridge.create;

/**
 * User object handle.
 * Util method.
 *
 * Created by dusanklinec on 01.07.16.
 */
public class EBUOHandle {
    protected String apiKey;
    protected long uoId;
    protected long uoType;

    public EBUOHandle() {
    }

    public EBUOHandle(String apiKey, long uoId, long uoType) {
        this.apiKey = apiKey;
        this.uoId = uoId;
        this.uoType = uoType;
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
        this.uoType = uoType;
        return this;
    }

    // Getters

    public String getApiKey() {
        return apiKey;
    }

    public long getUoId() {
        return uoId;
    }

    public long getUoType() {
        return uoType;
    }

    public String getHandle(){
        return String.format("%s00%08x00%08x", apiKey, uoId, uoType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBUOHandle that = (EBUOHandle) o;

        if (uoId != that.uoId) return false;
        if (uoType != that.uoType) return false;
        return apiKey != null ? apiKey.equals(that.apiKey) : that.apiKey == null;

    }

    @Override
    public int hashCode() {
        int result = apiKey != null ? apiKey.hashCode() : 0;
        result = 31 * result + (int) (uoId ^ (uoId >>> 32));
        result = 31 * result + (int) (uoType ^ (uoType >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return getHandle();
    }
}
