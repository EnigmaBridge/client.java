package com.enigmabridge.comm;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBImportPubKey that = (EBImportPubKey) o;

        if (id != that.id) return false;
        if (!Arrays.equals(certificateRaw, that.certificateRaw)) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return Arrays.equals(keyRaw, that.keyRaw);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + Arrays.hashCode(certificateRaw);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(keyRaw);
        return result;
    }

    @Override
    public String toString() {
        return "EBImportPubKey{" +
                "id=" + id +
                ", certificateRaw=" + Arrays.toString(certificateRaw) +
                ", type='" + type + '\'' +
                ", keyRaw=" + Arrays.toString(keyRaw) +
                '}';
    }
}
