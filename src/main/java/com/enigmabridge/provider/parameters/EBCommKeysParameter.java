package com.enigmabridge.provider.parameters;

import com.enigmabridge.EBCommKeys;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Comm keys as parameters to the cipher.
 * Created by dusanklinec on 27.04.16.
 */
public class EBCommKeysParameter extends KeyParameter implements EBCipherParameters{
    protected EBCommKeys keys;

    public EBCommKeysParameter(byte[] key) {
        super(key);
        keys = new EBCommKeys(key);
    }

    public EBCommKeysParameter(byte[] key, int keyOff, int keyLen) {
        super(key, keyOff, keyLen);
        keys = new EBCommKeys(key, keyOff, keyLen);
    }

    public KeyParameter getEncKeyParameter(){
        return new KeyParameter(keys.getEncKey());
    }

    public KeyParameter getMacKeyparameter(){
        return new KeyParameter(keys.getMacKey());
    }

    public byte[] getEncKey() {
        return keys.getEncKey();
    }

    public byte[] getMacKey() {
        return keys.getMacKey();
    }

    public boolean areKeysOK() {
        return keys.areKeysOK();
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return keys.equals(o);
    }

    public byte[] getEncoded() {
        return keys.getEncoded();
    }
}
