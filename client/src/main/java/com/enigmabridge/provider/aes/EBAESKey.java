package com.enigmabridge.provider.aes;

import com.enigmabridge.UserObjectType;
import com.enigmabridge.provider.EBKeyBase;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * Symmetric secret key is used for both encryption and decryption operation,
 * while EB separates these two operations and separate EB keys (same app key)
 * are used.
 *
 * We can do either a) encapsulate both encrypt and decrypt keys and use appropriate
 * one on init, or b) link inversion key to this one.
 *
 * Created by dusanklinec on 05.07.16.
 */
public class EBAESKey extends EBKeyBase implements SecretKey {
    static final long serialVersionUID = 1;

    // Key for inversion operation.
    protected EBAESKey inversionKey;

    public boolean isEncryptionKey(){
        final int function = getUserObjectType().getUoTypeFunction();
        if (function != UserObjectType.TYPE_PLAINAES && function != UserObjectType.TYPE_PLAINAESDECRYPT){
            throw new IllegalStateException("Key is not for AES");
        }

        return function == UserObjectType.TYPE_PLAINAES;
    }

    public boolean isDecryptionKey(){
        return !isEncryptionKey();
    }

    public EBAESKey getInversionKey() {
        return inversionKey;
    }
}
