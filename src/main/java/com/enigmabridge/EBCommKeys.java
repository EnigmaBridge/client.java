package com.enigmabridge;

import com.enigmabridge.comm.EBProcessDataCipher;
import com.enigmabridge.comm.EBProcessDataUtils;
import org.bouncycastle.crypto.CipherParameters;
import sun.security.util.Length;

import java.io.Serializable;
import java.util.Arrays;
import javax.crypto.SecretKey;

/**
 * EB communication keys.
 * Created by dusanklinec on 26.04.16.
 */
public class EBCommKeys implements SecretKey, CipherParameters, Length, Serializable{
    public static final long serialVersionUID = 1L;
    public static final int ENC_KEY_LEN = 32;
    public static final int MAC_KEY_LEN = 32;

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

    /**
     * Constructor initializes each key separately.
     * @param encKey
     * @param macKey
     */
    public EBCommKeys(byte[] encKey, byte[] macKey) {
        this.encKey = encKey;
        this.macKey = macKey;
    }

    /**
     * Initializes comm keys from the encoded form.
     * @param encoded
     */
    public EBCommKeys(byte[] encoded) {
        initFromEncoded(encoded, 0, encoded.length);
    }

    public EBCommKeys(byte[] encoded, int keyOff, int keyLen){
        initFromEncoded(encoded, keyOff, keyLen);
    }

    private void initFromEncoded(byte[] encoded, int keyOff, int keyLen){
        if (keyLen != ENC_KEY_LEN + MAC_KEY_LEN){
            throw new IllegalArgumentException("Invalid encoded form");
        }

        encKey = new byte[ENC_KEY_LEN];
        macKey = new byte[MAC_KEY_LEN];
        System.arraycopy(encoded, keyOff,             encKey, 0, ENC_KEY_LEN);
        System.arraycopy(encoded, keyOff+ENC_KEY_LEN, macKey, 0, MAC_KEY_LEN);
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
        this.encKey = EBUtils.hex2byte(encKey);
        return this;
    }

    public EBCommKeys setMacKey(String macKey) {
        this.macKey = EBUtils.hex2byte(macKey);
        return this;
    }

    /**
     * Returns true if commkeys are initialized correctly.
     * @return
     */
    public boolean areKeysOK(){
        return encKey != null && macKey != null && encKey.length == ENC_KEY_LEN && macKey.length == MAC_KEY_LEN;
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

    @Override
    public String getAlgorithm() {
        return EBProcessDataCipher.PROCESS_DATA_CIPHER;
    }

    @Override
    public String getFormat() {
        return "RAW";
    }

    @Override
    public byte[] getEncoded() {
        final int encLen = encKey.length;
        final int macLen = macKey.length;
        final byte[] bytes = new byte[encLen + macLen];
        System.arraycopy(encKey, 0, bytes, 0, encLen);
        System.arraycopy(macKey, 0, bytes, encLen, macLen);
        return bytes;
    }

    @Override
    public int length() {
        return encKey.length + macKey.length;
    }
}
