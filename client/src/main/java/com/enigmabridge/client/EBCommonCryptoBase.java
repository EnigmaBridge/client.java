package com.enigmabridge.client;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.SignatureException;

/**
 * Created by dusanklinec on 26.07.16.
 */
public abstract class EBCommonCryptoBase implements EBCommonCrypto {

    @Override
    public byte[] update(byte[] buffer) throws SignatureException {
        return update(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut) throws SignatureException, ShortBufferException {
        return update(buffer, offset, length, bufferOut, 0);
    }

    @Override
    public byte[] doFinal(byte[] buffer) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return doFinal(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return doFinal(buffer, offset, length, bufferOut, 0);
    }

    @Override
    public byte[] processData(byte[] buffer) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return doFinal(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    @Override
    public byte[] processData(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return doFinal(buffer, offset, length);
    }

    @Override
    public int processData(byte[] buffer, int offset, int length, byte[] bufferOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return doFinal(buffer, offset, length, bufferOut, 0);
    }

    @Override
    public int processData(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return doFinal(buffer, offset, length, bufferOut, offsetOut);
    }

    @Override
    public boolean verify(byte[] buffer) throws SignatureException {
        return verify(buffer, 0, buffer == null ? 0 : buffer.length);
    }
}
