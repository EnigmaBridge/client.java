package com.enigmabridge.client.wrappers;

import com.enigmabridge.client.EBCommonCryptoBase;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.SignatureException;

/**
 * Created by dusanklinec on 26.07.16.
 */
public class EBWrappedCipher extends EBCommonCryptoBase {
    protected Cipher cipher;

    public EBWrappedCipher() {
    }

    public EBWrappedCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public byte[] update(byte[] buffer, int offset, int length) throws SignatureException {
        return cipher.update(buffer, offset, length);
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws SignatureException, ShortBufferException {
        return cipher.update(buffer, offset, length, bufferOut, offsetOut);
    }

    @Override
    public byte[] doFinal(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return cipher.doFinal(buffer, offset, length);
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return cipher.doFinal(buffer, offset, length, bufferOut, offsetOut);
    }

    @Override
    public boolean verify(byte[] buffer, int offset, int length) throws SignatureException {
        throw new UnsupportedOperationException("Not a signature object");
    }

    public Cipher getCipher() {
        return cipher;
    }
}
