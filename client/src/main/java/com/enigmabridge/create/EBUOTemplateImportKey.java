package com.enigmabridge.create;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBSettingsBase;
import com.enigmabridge.comm.EBConnectionSettings;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Asymmetric public import key - for encryption for smartcard.
 *
 * {"id": <string>, "type":<"rsa2048"|"rsa1024">, "publickey": <string-serialized public key> },
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBUOTemplateImportKey {
    private long id;
    private String type;
    private byte[] publicKey;

    public static abstract class AbstractBuilder<T extends EBUOTemplateImportKey, B extends EBUOTemplateImportKey.AbstractBuilder> {
        public B setId(long id) {
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

    protected void setId(long id) {
        this.id = id;
    }

    protected void setType(String type) {
        this.type = type;
    }

    protected void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    // Getters

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
