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

    public String toString(){
        return String.format("ProcessDataResponse{statusCode=0x%4X, statusDetail=[%s], userObjectId: 0x%08X, function: [%s], " +
                        "nonce: [%s], protectedData: [%s], plainData: [%s]",
                this.statusCode,
                this.statusDetail,
                this.userObjectId,
                this.function,
                EBUtils.byte2hex(this.nonce),
                EBUtils.byte2hex(this.protectedData),
                EBUtils.byte2hex(this.plainData)
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

    EBProcessDataResponse setPlainData(byte[] plainData) {
        this.plainData = plainData;
        return this;
    }

    EBProcessDataResponse setProtectedData(byte[] protectedData) {
        this.protectedData = protectedData;
        return this;
    }

    EBProcessDataResponse setUserObjectId(long userObjectId) {
        this.userObjectId = userObjectId;
        return this;
    }

    EBProcessDataResponse setNonce(byte[] nonce) {
        this.nonce = nonce;
        return this;
    }
}
