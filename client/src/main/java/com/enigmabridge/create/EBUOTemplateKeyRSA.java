package com.enigmabridge.create;

import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.EBCommUtils;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKeyWrapper;

/**
 * Template APP key, holding RSA key.
 * RSA
 * Created by dusanklinec on 15.07.16.
 */
public class EBUOTemplateKeyRSA extends EBUOTemplateKey {
    protected final EBRSAPrivateCrtKeyWrapper wrapper;

    /**
     * Constructs TemplateKey from the RSA wrapper.
     * @param wrapper RSA key wrapper
     */
    public EBUOTemplateKeyRSA(EBRSAPrivateCrtKeyWrapper wrapper) {
        this.wrapper = wrapper;
        this.type = Constants.KEY_APP;
    }

    @Override
    public byte[] getKey() {
        return EBCreateUtils.exportPrivateKeyUOStyle(wrapper);
    }

    @Override
    public long encodeTo(byte[] buffer, long offset, long length) {
        // Byte aligned keys are supported for now.
        if ((offset & 7) != 0){
            throw new EBInvalidException("Key position has to be byte aligned, type: " + type);
        }

        // Warning: the length in current form indicates length of the RSA key, not the buffer.
        EBCreateUtils.exportPrivateKeyUOStyle(buffer, (short)(offset/8), (short)(length/8), wrapper);
        return length;
    }

    @Override
    public EBUOTemplateKey setKey(byte[] key) {
        throw new UnsupportedOperationException("RSA key cannot be set in this way");
    }

    @Override
    public EBUOTemplateKey setKey(String key) {
        throw new UnsupportedOperationException("RSA key cannot be set in this way");
    }
}
