package com.enigmabridge.client.wrappers;

import com.enigmabridge.client.EBCommonCryptoBase;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Created by dusanklinec on 26.07.16.
 */
public class EBWrappedSignature extends EBCommonCryptoBase {
    protected Signature signature;

    public EBWrappedSignature() {
    }

    public EBWrappedSignature(Signature signature) {
        this.signature = signature;
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws SignatureException, ShortBufferException {
        signature.update(buffer, offset, length);
        return 0;
    }

    @Override
    public byte[] update(byte[] buffer, int offset, int length) throws SignatureException {
        signature.update(buffer, offset, length);
        return null;
    }

    @Override
    public byte[] doFinal(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        if (buffer != null){
            signature.update(buffer, offset, length);
        }

        return signature.sign();
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        if (buffer != null){
            signature.update(buffer, offset, length);
        }

        return signature.sign(bufferOut, offsetOut, Integer.MAX_VALUE);
    }

    @Override
    public boolean verify(byte[] buffer, int offset, int length) throws SignatureException {
        return signature.verify(buffer, offset, length);
    }

    public Signature getSignature() {
        return signature;
    }
}
