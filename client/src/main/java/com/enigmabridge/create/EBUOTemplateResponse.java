package com.enigmabridge.create;

import com.enigmabridge.EBUtils;
import com.enigmabridge.comm.EBResponse;

import java.util.Arrays;
import java.util.List;

/**
 * Response on GetUO template request
 * Created by dusanklinec on 28.06.16.
 */
public class EBUOTemplateResponse extends EBResponse {
    private long objectId;
    private int version;            //<integer>,
    private int protocol;           //<integer>,
    private long encryptionOffset;  //<decimal_number>,
    private long flagOffset;        //<decimal_number>,
    private long policyOffset;      //<decimal_number>,
    private long scriptOffset;      //<decimal_number,

    private List<EBUOTemplateKeyOffset> keyOffsets;
    private byte[] template;
    private byte[] templateHs;
    private List<EBUOTemplateImportKey> importKeys;
    private String authorization;

    public static abstract class ABuilder<T extends EBUOTemplateResponse, B extends ABuilder>
            extends EBResponse.ABuilder<T,B>
    {
        public B setObjectId(long objectId) {
            getObj().setObjectId(objectId);
            return getThisBuilder();
        }

        public B setVersion(int version) {
            getObj().setVersion(version);
            return getThisBuilder();
        }

        public B setProtocol(int protocol) {
            getObj().setProtocol(protocol);
            return getThisBuilder();
        }

        public B setEncryptionOffset(long encryptionOffset) {
            getObj().setEncryptionOffset(encryptionOffset);
            return getThisBuilder();
        }

        public B setFlagOffset(long flagOffset) {
            getObj().setFlagOffset(flagOffset);
            return getThisBuilder();
        }

        public B setPolicyOffset(long policyOffset) {
            getObj().setPolicyOffset(policyOffset);
            return getThisBuilder();
        }

        public B setScriptOffset(long scriptOffset) {
            getObj().setScriptOffset(scriptOffset);
            return getThisBuilder();
        }

        public B setKeyOffsets(List<EBUOTemplateKeyOffset> keyOffsets) {
            getObj().setKeyOffsets(keyOffsets);
            return getThisBuilder();
        }

        public B setTemplate(byte[] template) {
            getObj().setTemplate(template);
            return getThisBuilder();
        }

        public B setTemplateHs(byte[] templateHs) {
            getObj().setTemplateHs(templateHs);
            return getThisBuilder();
        }

        public B setImportKeys(List<EBUOTemplateImportKey> importKeys) {
            getObj().setImportKeys(importKeys);
            return getThisBuilder();
        }

        public B setAuthorization(String authorization) {
            getObj().setAuthorization(authorization);
            return getThisBuilder();
        }


        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends ABuilder<EBUOTemplateResponse, Builder> {
        private final EBUOTemplateResponse parent = new EBUOTemplateResponse();

        @Override
        public EBUOTemplateResponse.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBUOTemplateResponse getObj() {
            return parent;
        }

        @Override
        public EBUOTemplateResponse build() {
            return parent;
        }
    }

    // Setters

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setEncryptionOffset(long encryptionOffset) {
        this.encryptionOffset = encryptionOffset;
    }

    public void setFlagOffset(long flagOffset) {
        this.flagOffset = flagOffset;
    }

    public void setPolicyOffset(long policyOffset) {
        this.policyOffset = policyOffset;
    }

    public void setScriptOffset(long scriptOffset) {
        this.scriptOffset = scriptOffset;
    }

    public void setKeyOffsets(List<EBUOTemplateKeyOffset> keyOffsets) {
        this.keyOffsets = keyOffsets;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    public void setTemplateHs(byte[] templateHs) {
        this.templateHs = templateHs;
    }

    public void setImportKeys(List<EBUOTemplateImportKey> importKeys) {
        this.importKeys = importKeys;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    // Getters

    public long getObjectId() {
        return objectId;
    }

    public int getVersion() {
        return version;
    }

    public int getProtocol() {
        return protocol;
    }

    public long getEncryptionOffset() {
        return encryptionOffset;
    }

    public long getFlagOffset() {
        return flagOffset;
    }

    public long getPolicyOffset() {
        return policyOffset;
    }

    public long getScriptOffset() {
        return scriptOffset;
    }

    public List<EBUOTemplateKeyOffset> getKeyOffsets() {
        return keyOffsets;
    }

    public byte[] getTemplate() {
        return template;
    }

    public byte[] getTemplateHs() {
        return templateHs;
    }

    public List<EBUOTemplateImportKey> getImportKeys() {
        return importKeys;
    }

    public String getAuthorization() {
        return authorization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBUOTemplateResponse that = (EBUOTemplateResponse) o;

        if (objectId != that.objectId) return false;
        if (version != that.version) return false;
        if (protocol != that.protocol) return false;
        if (encryptionOffset != that.encryptionOffset) return false;
        if (flagOffset != that.flagOffset) return false;
        if (policyOffset != that.policyOffset) return false;
        if (scriptOffset != that.scriptOffset) return false;
        if (keyOffsets != null ? !keyOffsets.equals(that.keyOffsets) : that.keyOffsets != null) return false;
        if (!Arrays.equals(template, that.template)) return false;
        if (!Arrays.equals(templateHs, that.templateHs)) return false;
        if (importKeys != null ? !importKeys.equals(that.importKeys) : that.importKeys != null) return false;
        return authorization != null ? authorization.equals(that.authorization) : that.authorization == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + version;
        result = 31 * result + protocol;
        result = 31 * result + (int) (encryptionOffset ^ (encryptionOffset >>> 32));
        result = 31 * result + (int) (flagOffset ^ (flagOffset >>> 32));
        result = 31 * result + (int) (policyOffset ^ (policyOffset >>> 32));
        result = 31 * result + (int) (scriptOffset ^ (scriptOffset >>> 32));
        result = 31 * result + (keyOffsets != null ? keyOffsets.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(template);
        result = 31 * result + Arrays.hashCode(templateHs);
        result = 31 * result + (importKeys != null ? importKeys.hashCode() : 0);
        result = 31 * result + (authorization != null ? authorization.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EBUOTemplateResponse{" +
                "objectId=" + objectId +
                ", version=" + version +
                ", protocol=" + protocol +
                ", encryptionOffset=" + encryptionOffset +
                ", flagOffset=" + flagOffset +
                ", policyOffset=" + policyOffset +
                ", scriptOffset=" + scriptOffset +
                ", keyOffsets=" + keyOffsets +
                ", template=" + EBUtils.byte2hex(template) +
                ", templateHs=" + EBUtils.byte2hex(templateHs) +
                ", importKeys=" + importKeys +
                ", authorization='" + authorization + '\'' +
                "} " + super.toString();
    }
}
