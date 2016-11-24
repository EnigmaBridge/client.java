package com.enigmabridge.registration;

import com.enigmabridge.comm.EBResponse;
import org.json.JSONObject;

/**
 * General response for registration calls.
 * Created by dusanklinec on 24.11.16.
 */
public class EBRegistrationBaseResponse extends EBResponse {
    /**
     * Root of the JSON response.
     */
    protected JSONObject root;

    protected String nonce;
    protected Integer version;
    protected Long timestamp;

    /**
     * Generic builder for further extending.
     * @param <T>
     * @param <B>
     */
    public static abstract class ABuilder<T extends EBRegistrationBaseResponse, B extends ABuilder>
            extends EBResponse.ABuilder<T,B>
    {
        public B setRoot(JSONObject root) {
            getObj().setRoot(root);
            return getThisBuilder();
        }

        public B setNonce(String nonce) {
            getObj().setNonce(nonce);
            return getThisBuilder();
        }

        public B setTimestamp(Long timestamp) {
            getObj().setTimestamp(timestamp);
            return getThisBuilder();
        }

        public B setVersion(Integer version) {
            getObj().setVersion(version);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    /**
     * Final builder for building this particular class.
     */
    public static class Builder extends ABuilder<EBRegistrationBaseResponse, Builder> {
        private final EBRegistrationBaseResponse parent = new EBRegistrationBaseResponse();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBRegistrationBaseResponse getObj() {
            return parent;
        }

        @Override
        public EBRegistrationBaseResponse build() {
            return parent;
        }
    }

    // Protected setters.

    protected void setRoot(JSONObject root) {
        this.root = root;
    }

    protected void setNonce(String nonce) {
        this.nonce = nonce;
    }

    protected void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    protected void setVersion(Integer version) {
        this.version = version;
    }

    // General getters

    public JSONObject getRoot() {
        return root;
    }

    public String getNonce() {
        return nonce;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Integer getVersion() {
        return version;
    }
}
