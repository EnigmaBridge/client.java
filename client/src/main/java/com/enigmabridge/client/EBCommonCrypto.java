package com.enigmabridge.client;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.SignatureException;

/**
 * Interface for common crypto operations.
 * Unifies Cipher, Mac, Signature.
 *
 * Created by dusanklinec on 26.07.16.
 */
public interface EBCommonCrypto {
    byte[] update(byte[] buffer) throws SignatureException;

    int    update(byte[] buffer, int offset, int length, byte[] bufferOut) throws SignatureException, ShortBufferException;

    int    update(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws SignatureException, ShortBufferException;

    byte[] update(byte[] buffer, int offset, int length) throws SignatureException;

    byte[] doFinal(byte[] buffer) throws BadPaddingException, IllegalBlockSizeException, SignatureException;

    byte[] doFinal(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException;

    int    doFinal(byte[] buffer, int offset, int length, byte[] bufferOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException;

    int    doFinal(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException;

    byte[] processData(byte[] buffer) throws BadPaddingException, IllegalBlockSizeException, SignatureException;

    byte[] processData(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException;

    int    processData(byte[] buffer, int offset, int length, byte[] bufferOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException;

    int    processData(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException;

    boolean verify(byte[] buffer) throws SignatureException;

    boolean verify(byte[] buffer, int offset, int length) throws SignatureException;
}
