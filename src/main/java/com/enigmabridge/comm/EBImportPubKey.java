package com.enigmabridge.comm;

/**
 * Represents single import public key.
 * Created by dusanklinec on 02.05.16.
 */
public class EBImportPubKey {
    protected int id;
    protected byte[] certificateRaw;
    protected String type;
    protected byte[] keyRaw;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getCertificateRaw() {
        return certificateRaw;
    }

    public void setCertificateRaw(byte[] certificateRaw) {
        this.certificateRaw = certificateRaw;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getKeyRaw() {
        return keyRaw;
    }

    public void setKeyRaw(byte[] keyRaw) {
        this.keyRaw = keyRaw;
    }
}
