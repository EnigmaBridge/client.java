package com.enigmabridge.create;

import com.enigmabridge.EBUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Create UO request
 *
 * Created by dusanklinec on 30.06.16.
 */
public class EBCreateUORequest implements Serializable {
    private long objectId;
    private long objectType;
    private byte[] object;
    private String importKeyId;
    private String authorization;
    private String contextparams;

    // Setters

    public EBCreateUORequest setObjectId(long objectId) {
        this.objectId = objectId;
        return this;
    }

    public EBCreateUORequest setObject(byte[] object) {
        this.object = object;
        return this;
    }

    public EBCreateUORequest setImportKeyId(String importKeyId) {
        this.importKeyId = importKeyId;
        return this;
    }

    public EBCreateUORequest setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public EBCreateUORequest setContextparams(String contextparams) {
        this.contextparams = contextparams;
        return this;
    }

    public EBCreateUORequest setObjectType(long objectType) {
        this.objectType = objectType;
        return this;
    }

    // Getters

    public long getObjectId() {
        return objectId;
    }

    public byte[] getObject() {
        return object;
    }

    public String getImportKeyId() {
        return importKeyId;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getContextparams() {
        return contextparams;
    }

    public long getObjectType() {
        return objectType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBCreateUORequest that = (EBCreateUORequest) o;

        if (objectId != that.objectId) return false;
        if (!Arrays.equals(object, that.object)) return false;
        if (importKeyId != null ? !importKeyId.equals(that.importKeyId) : that.importKeyId != null) return false;
        if (authorization != null ? !authorization.equals(that.authorization) : that.authorization != null)
            return false;
        if (contextparams != null ? !contextparams.equals(that.contextparams) : that.contextparams != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + Arrays.hashCode(object);
        result = 31 * result + (importKeyId != null ? importKeyId.hashCode() : 0);
        result = 31 * result + (authorization != null ? authorization.hashCode() : 0);
        result = 31 * result + (contextparams != null ? contextparams.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EBCreateUORequest{" +
                "objectId=" + objectId +
                ", objectType=" + objectType +
                ", object=" + EBUtils.byte2hexNullable(object) +
                ", importKeyId='" + importKeyId + '\'' +
                ", authorization='" + authorization + '\'' +
                ", contextparams='" + contextparams + '\'' +
                '}';
    }
}
