package com.enigmabridge.client;

import com.enigmabridge.client.wrappers.EBWrappedCipher;
import com.enigmabridge.client.wrappers.EBWrappedCombined;
import com.enigmabridge.client.wrappers.EBWrappedMac;
import com.enigmabridge.client.wrappers.EBWrappedSignature;

import javax.crypto.*;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Object initialized with particular user object.
 *
 * Created by dusanklinec on 26.07.16.
 */
public class EBClientObject implements EBCommonCrypto {
    protected EBClient client;
    protected EBWrappedCombined cryptoWrapper;

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

    public EBWrappedCipher getCipher() {
        return cryptoWrapper.getCipher();
    }

    public EBWrappedMac getMac() {
        return cryptoWrapper.getMac();
    }

    public EBWrappedSignature getSignature() {
        return cryptoWrapper.getSignature();
    }

    public Cipher getRawCipher() {
        return cryptoWrapper.getCipher() != null ? cryptoWrapper.getCipher().getCipher() : null;
    }

    public Mac getRawMac() {
        return cryptoWrapper.getMac() != null ? cryptoWrapper.getMac().getMac() : null;
    }

    public Signature getRawSignature() {
        return cryptoWrapper.getSignature() != null ? cryptoWrapper.getSignature().getSignature() : null;
    }

    protected void checkInit(){
        cryptoWrapper.checkInit();
    }
}
