package com.enigmabridge.comm;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBEngineException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Process data cipher.
 * Encrypt-then-MAC.
 *
 * AES-256/CBC/PKCS7 + HMAC/AES-256/CBC
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBProcessDataCipher {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataCipher.class);

    public static final String PROCESS_DATA_CIPHER = "ProcessDataV1";
    public static final byte[] ZERO_IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    protected boolean forEncryption;
    protected Cipher enc;
    protected Mac mac;
    protected int macSize;

    public EBProcessDataCipher(boolean forEncryption, Cipher enc, Mac mac) {
        this.forEncryption = forEncryption;
        this.enc = enc;
        this.mac = mac;
        this.macSize = mac.getMacSize();
    }

    /**
     * Creates a new cipher instance with given keys.
     * @param forEncryption
     * @param keys
     * @return
     */
    public static EBProcessDataCipher initCipher(boolean forEncryption, EBCommKeys keys) throws EBEngineException {
        try {
            // Initialize cipher
            final Cipher enc = Cipher.getInstance("AES/CBC/PKCS5Padding");

            try {
                enc.init(
                        forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
                        new SecretKeySpec(keys.getEncKey(), "AES"),
                        new IvParameterSpec(ZERO_IV));

            } catch(InvalidKeyException ex){
                // We are using AES-256 which is not allowed by default in Java.
                // You may need to install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
                // http://stackoverflow.com/questions/6481627/java-security-illegal-key-size-or-default-parameters
                if (keys.getEncKey() != null && keys.getEncKey().length == 32){
                    final String msg = "Invalid key exception - install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files";

                    LOG.error(msg);
                    throw new EBEngineException(msg, ex);
                } else {
                    throw ex;
                }
            }

            // Initialize CBC MAC.
            // There is no AES-CBC-MAC in the BouncyCastle JCA/JCE provider.
            // We need to use BouncyCastle directly for this.
            final BlockCipher cipher = new AESLightEngine();
            final CBCBlockCipherMac mac = new CBCBlockCipherMac(cipher, cipher.getBlockSize() * 8);
            mac.init(new KeyParameter(keys.getMacKey()));

            return new EBProcessDataCipher(forEncryption, enc, mac);

        } catch (NoSuchAlgorithmException e) {
            throw new EBEngineException(e);
        } catch (NoSuchPaddingException e) {
            throw new EBEngineException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new EBEngineException(e);
        } catch (InvalidKeyException e) {
            throw new EBEngineException(e);
        }
    }

    protected void checkValid(){
        if (mac == null || enc == null){
            throw new IllegalStateException("Cipher not initialized");
        }
    }

    /**
     * Gets output buffer size needed for the operation depending on the size of the input.
     * @param inputSize
     * @return
     */
    public int getOutputBufferSize(int inputSize){
        final int cipherBlockSize = enc.getBlockSize();
        int outBuffSize = 0;
        if (forEncryption){
            outBuffSize = inputSize - (inputSize % cipherBlockSize) + cipherBlockSize + macSize;
        } else {
            outBuffSize = inputSize - macSize;
        }

        return outBuffSize;
    }

    /**
     * Used to add data to MAC.
     * @param input
     * @param inputOffset
     * @param length
     */
    public void add2mac(byte[] input, int inputOffset, int length){
        mac.update(input, inputOffset, length);
    }

    /**
     * Performs cipher operation on the given input data.
     * In case of invalid MAC, exception is thrown.
     *
     * @param input
     * @param inputOffset
     * @return
     * @throws EBCryptoException
     */
    public byte[] processBuffer(byte[] input, int inputOffset, int length) throws EBCryptoException {
        checkValid();
        int outBuffSize = getOutputBufferSize(length);

        byte[] output = new byte[outBuffSize];
        final int processed = processBuffer(input, inputOffset, length, output, 0);
        if(processed != outBuffSize){
            byte[] ret = new byte[processed];
            System.arraycopy(output, 0, ret, 0, processed);
            return ret;
        }

        return output;
    }

    /**
     * Performs cipher operation on the given input data.
     * In case of invalid MAC, exception is thrown.
     *
     * @param input
     * @param inputOffset
     * @param inputLength
     * @param output
     * @param outputOffset
     * @return
     */
    public int processBuffer(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws EBCryptoException
    {
        checkValid();
        try {
            if (this.forEncryption){
                if (output.length < (outputOffset + inputOffset + macSize))
                {
                    throw new EBCryptoException(new ShortBufferException("Output buffer too short"));
                }

                int written = enc.doFinal(input, inputOffset, inputLength, output, outputOffset);
                mac.update(output, outputOffset, written);
                written += mac.doFinal(output, outputOffset + written);

                return written;

            } else {
                if (inputLength < macSize)
                {
                    throw new EBCryptoException(new InvalidCipherTextException("data too short"));
                }
                if (output.length < (outputOffset + inputLength - macSize))
                {
                    throw new EBCryptoException(new ShortBufferException("Output buffer too short"));
                }

                byte[] computedMac = new byte[macSize];
                mac.update(input, inputOffset, inputLength - macSize);
                mac.doFinal(computedMac, 0);

                // Process rest of data, without last MAC block.
                int written = enc.doFinal(input, inputOffset, inputLength - macSize, output, outputOffset);

                if (!verifyMac(computedMac, input, inputOffset + inputLength - macSize)){
                    throw new BadPaddingException("Invalid MAC");
                }

                return written;
            }
        } catch (ShortBufferException e) {
            throw new EBCryptoException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EBCryptoException(e);
        } catch (BadPaddingException e) {
            throw new EBCryptoException(e);
        }
    }

    private boolean verifyMac(byte[] computedMac, byte[] mac, int off)
    {
        int nonEqual = 0;

        // Constant time comparison - important for timing attacks prevention.
        for (int i = 0; i < macSize; i++)
        {
            nonEqual |= (computedMac[i] ^ mac[off + i]);
        }

        return nonEqual == 0;
    }

    public Cipher getEnc() {
        return enc;
    }

    public Mac getMac() {
        return mac;
    }

    public boolean isForEncryption() {
        return forEncryption;
    }
}
