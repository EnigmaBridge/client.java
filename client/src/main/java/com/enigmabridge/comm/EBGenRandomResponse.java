package com.enigmabridge.comm;

/**
 * Generate randomness response.
 * Created by dusanklinec on 06.05.16.
 */
public class EBGenRandomResponse extends EBResponse {
    protected byte[] data;

    public static abstract class ABuilder<T extends EBGenRandomResponse, B extends ABuilder>
            extends EBResponse.ABuilder<T,B>
    {
        public B setData(byte[] data) {
            getObj().setData(data);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends ABuilder<EBGenRandomResponse, Builder> {
        private final EBGenRandomResponse parent = new EBGenRandomResponse();

        @Override
        public EBGenRandomResponse.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBGenRandomResponse getObj() {
            return parent;
        }

        @Override
        public EBGenRandomResponse build() {
            return parent;
        }
    }

    public byte[] getData() {
        return data;
    }

    protected void setData(byte[] data) {
        this.data = data;
    }
}
