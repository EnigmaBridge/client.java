package com.enigmabridge.comm;

import com.enigmabridge.EBUtils;

/**
 * EB ProcessData() response
 *
 * Created by dusanklinec on 27.04.16.
 */
public class EBProcessDataResponse extends EBResponse {
    /**
     * Plain data parsed from the response.
     * Nor MACed neither encrypted.
     * @output
     */
    protected byte[] plainData;

    /**
     * Protected data parsed from the response.
     * Protected by MAC, encrypted in transit.
     * @output
     */
    protected byte[] protectedData;

    /**
     * USerObjectID parsed from the response.
     * Ingeter, 4B.
     */
    protected long userObjectId;

    /**
     * Nonce parsed from the RAW response.
     */
    protected byte[] nonce;

    public static abstract class ABuilder<T extends EBProcessDataResponse, B extends ABuilder>
            extends EBResponse.ABuilder<T,B>
    {
        protected B setPlainData(byte[] plainData) {
            getObj().setPlainData(plainData);
            return getThisBuilder();
        }

        protected B setProtectedData(byte[] protectedData) {
            getObj().setProtectedData(protectedData);
            return getThisBuilder();
        }

        protected B setUserObjectId(long userObjectId) {
            getObj().setUserObjectId(userObjectId);
            return getThisBuilder();
        }

        protected B setNonce(byte[] nonce) {
            getObj().setNonce(nonce);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends ABuilder<EBProcessDataResponse, Builder> {
        private final EBProcessDataResponse parent = new EBProcessDataResponse();

        @Override
        public EBProcessDataResponse.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBProcessDataResponse getObj() {
            return parent;
        }

        @Override
        public EBProcessDataResponse build() {
            return parent;
        }
    }

    public String toString(){
        return String.format("ProcessDataResponse{statusCode=0x%4X, statusDetail=[%s], userObjectId: 0x%08X, function: [%s], " +
                        "nonce: [%s], protectedData: [%s], plainData: [%s]",
                this.statusCode,
                this.statusDetail,
                this.userObjectId,
                this.function,
                this.nonce          == null ? "null" : EBUtils.byte2hex(this.nonce),
                this.protectedData  == null ? "null" : EBUtils.byte2hex(this.protectedData),
                this.plainData      == null ? "null" : EBUtils.byte2hex(this.plainData)
        );
    }

    public byte[] getPlainData() {
        return plainData;
    }

    public byte[] getProtectedData() {
        return protectedData;
    }

    public long getUserObjectId() {
        return userObjectId;
    }

    public byte[] getNonce() {
        return nonce;
    }

    protected EBProcessDataResponse setPlainData(byte[] plainData) {
        this.plainData = plainData;
        return this;
    }

    protected EBProcessDataResponse setProtectedData(byte[] protectedData) {
        this.protectedData = protectedData;
        return this;
    }

    protected EBProcessDataResponse setUserObjectId(long userObjectId) {
        this.userObjectId = userObjectId;
        return this;
    }

    protected EBProcessDataResponse setNonce(byte[] nonce) {
        this.nonce = nonce;
        return this;
    }
}
