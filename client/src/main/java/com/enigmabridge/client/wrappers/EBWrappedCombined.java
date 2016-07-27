package com.enigmabridge.client.wrappers;

import com.enigmabridge.client.EBCommonCryptoBase;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.SignatureException;

/**
 * Created by dusanklinec on 26.07.16.
 */
public class EBWrappedCombined extends EBCommonCryptoBase {
    /**
     * If UO represents cipher, it is initialized here.
     */
    protected EBWrappedCipher cipher;

    /**
     * If UO represents MAC, it is initialized here.
     */
    protected EBWrappedMac mac;

    /**
     * If UO represents Signature, it is initialized here.
     */
    protected EBWrappedSignature signature;

    public EBWrappedCombined() {
    }

    public EBWrappedCombined(EBWrappedCipher cipher) {
        this.cipher = cipher;
    }

    public EBWrappedCombined(EBWrappedMac mac) {
        this.mac = mac;
    }

    public EBWrappedCombined(EBWrappedSignature signature) {
        this.signature = signature;
    }

    @Override
    public int update(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws SignatureException, ShortBufferException {
        checkInit();
        if (cipher != null){
            return cipher.update(buffer, offset, length, bufferOut, offsetOut);
        } else if (mac != null){
            return mac.update(buffer, offset, length, bufferOut, offsetOut);
        } else if (signature != null){
            return signature.update(buffer, offset, length, bufferOut, offsetOut);
        } else {
            throw new IllegalStateException("No usable crypto object");
        }
    }

    @Override
    public byte[] update(byte[] buffer, int offset, int length) throws SignatureException {
        checkInit();
        if (cipher != null){
            return cipher.update(buffer, offset, length);
        } else if (mac != null){
            return mac.update(buffer, offset, length);
        } else if (signature != null){
            return signature.update(buffer, offset, length);
        } else {
            throw new IllegalStateException("No usable crypto object");
        }
    }

    @Override
    public byte[] doFinal(byte[] buffer, int offset, int length) throws BadPaddingException, IllegalBlockSizeException, SignatureException {
        checkInit();
        if (cipher != null){
            return cipher.doFinal(buffer, offset, length);
        } else if (mac != null){
            return mac.doFinal(buffer, offset, length);
        } else if (signature != null){
            return signature.doFinal(buffer, offset, length);
        } else {
            throw new IllegalStateException("No usable crypto object");
        }
    }

    @Override
    public int doFinal(byte[] buffer, int offset, int length, byte[] bufferOut, int offsetOut) throws BadPaddingException, IllegalBlockSizeException, SignatureException, ShortBufferException {
        checkInit();
        if (cipher != null){
            return cipher.doFinal(buffer, offset, length, bufferOut, offsetOut);
        } else if (mac != null){
            return mac.doFinal(buffer, offset, length, bufferOut, offsetOut);
        } else if (signature != null){
            return signature.doFinal(buffer, offset, length, bufferOut, offsetOut);
        } else {
            throw new IllegalStateException("No usable crypto object");
        }
    }

    @Override
    public boolean verify(byte[] buffer, int offset, int length) throws SignatureException {
        checkInit();
        if (cipher != null){
            return cipher.verify(buffer, offset, length);
        } else if (mac != null){
            return mac.verify(buffer, offset, length);
        } else if (signature != null){
            return signature.verify(buffer, offset, length);
        } else {
            throw new IllegalStateException("No usable crypto object");
        }
    }

    public void checkInit(){
        int code = 0;
        code += cipher      == null ? 0 : 1;
        code += mac         == null ? 0 : 1;
        code += signature   == null ? 0 : 1;
        if (code != 1){
            throw new IllegalStateException("Exactly one primitive can be configured");
        }
    }

    public EBWrappedCipher getCipher() {
        return cipher;
    }

    public EBWrappedMac getMac() {
        return mac;
    }

    public EBWrappedSignature getSignature() {
        return signature;
    }
}
