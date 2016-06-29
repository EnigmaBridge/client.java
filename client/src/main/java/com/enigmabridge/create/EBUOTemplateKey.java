package com.enigmabridge.create;

import com.enigmabridge.EBUtils;

import java.util.Arrays;

/**
 * UO key used when creating a new UO - template filler uses it.
 * Created by dusanklinec on 29.06.16.
 */
public class EBUOTemplateKey {
    protected String type;
    protected byte[] key;

    public EBUOTemplateKey(String type, byte[] key) {
        this.type = type;
        this.key = key;
    }

    public EBUOTemplateKey() {
    }

    public String getType() {
        return type;
    }

    public byte[] getKey() {
        return key;
    }

    public EBUOTemplateKey setType(String type) {
        this.type = type;
        return this;
    }

    public EBUOTemplateKey setKey(byte[] key) {
        this.key = key;
        return this;
    }

    public EBUOTemplateKey setKey(String key) {
        this.key = EBUtils.hex2byte(key, true);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBUOTemplateKey that = (EBUOTemplateKey) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return Arrays.equals(key, that.key);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(key);
        return result;
    }

    @Override
    public String toString() {
        return "EBUOTemplateKey{" +
                "type='" + type + '\'' +
                ", key=" + EBUtils.byte2hex(key) +
                '}';
    }
}
