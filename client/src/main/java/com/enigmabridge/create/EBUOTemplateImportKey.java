package com.enigmabridge.create;

import java.util.Arrays;

/**
 * Asymmetric public import key - for encryption for smartcard.
 *
 * {"id": <string>, "type":<"rsa2048"|"rsa1024">, "publickey": <string-serialized public key> },
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBUOTemplateImportKey {
    private String id;
    private String type;
    private byte[] publicKey;

    public static abstract class AbstractBuilder<T extends EBUOTemplateImportKey, B extends EBUOTemplateImportKey.AbstractBuilder> {
        public B setId(String id) {
            getObj().setId(id);
            return getThisBuilder();
        }

        public B setType(String type) {
            getObj().setType(type);
            return getThisBuilder();
        }

        public B setPublicKey(byte[] publicKey) {
            getObj().setPublicKey(publicKey);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBUOTemplateImportKey, EBUOTemplateImportKey.Builder> {
        private final EBUOTemplateImportKey parent = new EBUOTemplateImportKey();

        @Override
        public EBUOTemplateImportKey.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBUOTemplateImportKey getObj() {
            return parent;
        }

        @Override
        public EBUOTemplateImportKey build() {
            return parent;
        }
    }

    // Setters

    protected void setId(String id) {
        this.id = id;
    }

    protected void setType(String type) {
        this.type = type;
    }

    protected void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBUOTemplateImportKey that = (EBUOTemplateImportKey) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return Arrays.equals(publicKey, that.publicKey);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(publicKey);
        return result;
    }

    @Override
    public String toString() {
        return "EBUOTemplateImportKey{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", publicKey=" + Arrays.toString(publicKey) +
                '}';
    }
}
