package com.enigmabridge.provider.specs;

/**
 * Keys to generate for symmetric key operations.
 * EB supports symmetric key operations while encryption and decryption is a different operation
 * with different key handle.
 *
 * Created by dusanklinec on 11.07.16.
 */
public enum EBSymmetricKeyGenTypes {
    ENCRYPT,
    DECRYPT,
    BOTH
}
