package com.enigmabridge.create;

import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBInvalidException;
import com.enigmabridge.EBUtils;

import javax.crypto.spec.SecretKeySpec;
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

    public EBUOTemplateKey(SecretKeySpec spec) {
        this.type = Constants.KEY_APP;
        this.key = spec.getEncoded();
    }

    public EBUOTemplateKey() {
    }

    public String getType() {
        return type;
    }

    public byte[] getKey() {
        return key;
    }

    /**
     * Serializes the key to the given buffer.
     *
     * @param buffer buffer to encode key to.
     * @param offset offset to the buffer to place the first byte of the key, in bits.
     * @param length length of the space for the key, in bits.
     * @return number of bits written
     */
    public long encodeTo(byte[] buffer, long offset, long length){
        // For symmetric key the size has to match strictly.
        if (length != key.length*8) {
            throw new EBCryptoException("Invalid key size, exp: " + length
                    + ", given: " + (key.length*8)
                    + ", type: " + type);
        }

        // Byte aligned keys are supported for now.
        if ((offset & 7) != 0){
            throw new EBInvalidException("Key position has to be byte aligned, type: " + type);
        }

        // Encode to the buffer.
        for(int idx=0, len=key.length; idx < len; ++idx){
            buffer[(int)(offset/8) + idx] = key[idx];
        }

        return length;
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
