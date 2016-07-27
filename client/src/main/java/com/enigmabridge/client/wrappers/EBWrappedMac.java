package com.enigmabridge.client.wrappers;

import com.enigmabridge.client.EBCommonCryptoBase;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import java.security.SignatureException;

/**
 * Created by dusanklinec on 26.07.16.
 */
public class EBWrappedMac extends EBCommonCryptoBase {
    protected Mac mac;

    public EBWrappedMac() {
    }

    public EBWrappedMac(Mac mac) {
        this.mac = mac;
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws SignatureException, ShortBufferException {
        mac.update(buffer, offset, length);
        return 0;
    }

    @Override
    public byte[] update(byte[] buffer, int offset, int length) throws SignatureException {
        mac.update(buffer, offset, length);
        return null;
    }

    @Override
    public byte[] doFinal(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        if (buffer != null){
            mac.update(buffer, offset, length);
        }

        return mac.doFinal();
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        if (buffer != null){
            mac.update(buffer, offset, length);
        }

        mac.doFinal(bufferOut, offsetOut);
        return mac.getMacLength();
    }

    @Override
    public boolean verify(byte[] buffer, int offset, int length) throws SignatureException {
        throw new UnsupportedOperationException("Not a signature object");
    }

    public Mac getMac() {
        return mac;
    }
}
