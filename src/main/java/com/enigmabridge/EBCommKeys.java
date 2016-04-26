package com.enigmabridge;

import java.io.Serializable;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 * EB communication keys.
 * Created by dusanklinec on 26.04.16.
 */
public class EBCommKeys implements Serializable{
    public static final long serialVersionUID = 1L;

    /**
     * AES-256-CBC end-to-end encryption key.
     */
    protected byte[] encKey;

    /**
     * HMAC-AES-256-CBC key for end-to-end HMAC.
     */
    protected byte[] macKey;

    public EBCommKeys() {
    }

    public EBCommKeys(byte[] encKey, byte[] macKey) {
        this.encKey = encKey;
        this.macKey = macKey;
    }

    public byte[] getEncKey() {
        return encKey;
    }

    public EBCommKeys setEncKey(byte[] encKey) {
        this.encKey = encKey;
        return this;
    }

    public byte[] getMacKey() {
        return macKey;
    }

    public EBCommKeys setMacKey(byte[] macKey) {
        this.macKey = macKey;
        return this;
    }

    public EBCommKeys setEncKey(String encKey) {
        this.encKey = DatatypeConverter.parseHexBinary(encKey);
        return this;
    }

    public EBCommKeys setMacKey(String macKey) {
        this.macKey = DatatypeConverter.parseHexBinary(macKey);
        return this;
    }

    public boolean areKeysOK(){
        return encKey != null && macKey != null && encKey.length == 32 && macKey.length == 32;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBCommKeys that = (EBCommKeys) o;

        if (!Arrays.equals(encKey, that.encKey)) return false;
        return Arrays.equals(macKey, that.macKey);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(encKey);
        result = 31 * result + Arrays.hashCode(macKey);
        return result;
    }

    @Override
    public String toString() {
        return "EBCommKeys{" +
                "encKey=" + Arrays.toString(encKey) +
                ", macKey=" + Arrays.toString(macKey) +
                '}';
    }
}