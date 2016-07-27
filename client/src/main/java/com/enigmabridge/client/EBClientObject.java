package com.enigmabridge.client;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.SignatureException;

/**
 * Object initialized with particular user object.
 *
 * Created by dusanklinec on 26.07.16.
 */
public class EBClientObject implements EBCommonCrypto {
    protected EBClient client;
    protected EBCommonCrypto cryptoWrapper;

    // Common crypto

    @Override
    public byte[] update(byte[] buffer) throws SignatureException {
        return cryptoWrapper.update(buffer);
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut) throws SignatureException, ShortBufferException {
        return cryptoWrapper.update(buffer, offset, length, bufferOut);
    }

    @Override
    public byte[] doFinal(byte[] buffer) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return cryptoWrapper.doFinal(buffer);
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return cryptoWrapper.doFinal(buffer, offset, length, bufferOut);
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws SignatureException, ShortBufferException {
        return cryptoWrapper.update(buffer, offset, length, bufferOut, offsetOut);
    }

    @Override
    public byte[] processData(byte[] buffer) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return cryptoWrapper.processData(buffer);
    }

    @Override
    public byte[] processData(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return cryptoWrapper.processData(buffer, offset, length);
    }

    @Override
    public int processData(byte[] buffer, int offset, int length, byte[] bufferOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return cryptoWrapper.processData(buffer, offset, length, bufferOut);
    }

    @Override
    public byte[] update(byte[] buffer, int offset, int length) throws SignatureException {
        return cryptoWrapper.update(buffer, offset, length);
    }

    @Override
    public int processData(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return cryptoWrapper.processData(buffer, offset, length, bufferOut, offsetOut);
    }

    @Override
    public byte[] doFinal(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        return cryptoWrapper.doFinal(buffer, offset, length);
    }

    @Override
    public boolean verify(byte[] buffer) throws SignatureException {
        return cryptoWrapper.verify(buffer);
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        return cryptoWrapper.doFinal(buffer, offset, length, bufferOut, offsetOut);
    }

    @Override
    public boolean verify(byte[] buffer, int offset, int length) throws SignatureException {
        return cryptoWrapper.verify(buffer, offset, length);
    }

    protected void checkInit(){

    }

    public EBClientObject setClient(EBClient client) {
        this.client = client;
        return this;
    }

    public EBClientObject setCryptoWrapper(EBCommonCrypto cryptoWrapper) {
        this.cryptoWrapper = cryptoWrapper;
        return this;
    }
}
