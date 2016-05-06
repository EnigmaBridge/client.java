package com.enigmabridge.comm;

/**
 * Generate randomness response.
 * Created by dusanklinec on 06.05.16.
 */
public class EBGenRandomResponse extends EBResponse {
    protected byte[] data;

    public byte[] getData() {
        return data;
    }

    void setData(byte[] data) {
        this.data = data;
    }
}
