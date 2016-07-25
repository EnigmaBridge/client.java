package com.enigmabridge.comm;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/**
 * Encrypt-then-MAC cipher.
 * Not implemented yet, do not use...
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBProcessDataCipherEngine implements AEADBlockCipher {
    private static final byte nTAG = 0x0;

    private static final byte hTAG = 0x1;

    private static final byte cTAG = 0x2;

    private BlockCipher cipher;

    private boolean forEncryption;

    private int blockSize;

    private Mac mac;

    private byte[] nonceMac;
    private byte[] associatedTextMac;
    private byte[] macBlock;

    private int macSize;
    private byte[] bufBlock;
    private int bufOff;

    private boolean cipherInitialized;
    private byte[] initialAssociatedText;

    /**
     * Constructor that accepts an instance of a block cipher engine.
     *
     * @param cipher the engine to use
     */
    public EBProcessDataCipherEngine(BlockCipher cipher, Mac mac)
    {
        blockSize = cipher.getBlockSize();
        macSize = mac.getMacSize();

        this.mac = mac;
        macBlock = new byte[macSize];
        associatedTextMac = new byte[macSize];
        nonceMac = new byte[macSize];
        this.cipher = cipher;
    }

    public String getAlgorithmName()
    {
        return cipher.getAlgorithmName() + "/PDT";
    }

    public BlockCipher getUnderlyingCipher()
    {
        return cipher;
    }

    public int getBlockSize()
    {
        return cipher.getBlockSize();
    }

    public void init(boolean forEncryption, CipherParameters params)
            throws IllegalArgumentException
    {
        this.forEncryption = forEncryption;

        byte[] nonce;
        CipherParameters keyParam;

        if (params instanceof AEADParameters)
        {
            AEADParameters param = (AEADParameters)params;

            nonce = param.getNonce();
            initialAssociatedText = param.getAssociatedText();
            macSize = param.getMacSize() / 8;
            keyParam = param.getKey();
        }
        else if (params instanceof ParametersWithIV)
        {
            ParametersWithIV param = (ParametersWithIV)params;

            nonce = param.getIV();
            initialAssociatedText = null;
            macSize = mac.getMacSize() / 2;
            keyParam = param.getParameters();
        }
        else
        {
            throw new IllegalArgumentException("invalid parameters passed to EAX");
        }

        bufBlock = new byte[forEncryption ? blockSize : (blockSize + macSize)];

        byte[] tag = new byte[blockSize];

        // Key reuse implemented in CBC mode of underlying CMac
        mac.init(keyParam);

        tag[blockSize - 1] = nTAG;
        mac.update(tag, 0, blockSize);
        mac.update(nonce, 0, nonce.length);
        mac.doFinal(nonceMac, 0);

        // Same BlockCipher underlies this and the mac, so reuse last key on cipher
        cipher.init(true, new ParametersWithIV(null, nonceMac));

        reset();
    }

    private void initCipher()
    {
        if (cipherInitialized)
        {
            return;
        }

        cipherInitialized = true;

        mac.doFinal(associatedTextMac, 0);

        byte[] tag = new byte[blockSize];
        tag[blockSize - 1] = cTAG;
        mac.update(tag, 0, blockSize);
    }

    private void calculateMac()
    {
        byte[] outC = new byte[blockSize];
        mac.doFinal(outC, 0);

        for (int i = 0; i < macBlock.length; i++)
        {
            // TODO: in enc-then-mac we do not use this. just outC.
            macBlock[i] = (byte)(nonceMac[i] ^ associatedTextMac[i] ^ outC[i]);
        }
    }

    public void reset()
    {
        reset(true);
    }

    private void reset(
            boolean clearMac)
    {
        cipher.reset(); // TODO Redundant since the mac will reset it?
        mac.reset();

        bufOff = 0;
        Arrays.fill(bufBlock, (byte)0);

        if (clearMac)
        {
            Arrays.fill(macBlock, (byte)0);
        }

        byte[] tag = new byte[blockSize];
        tag[blockSize - 1] = hTAG;
        mac.update(tag, 0, blockSize);

        cipherInitialized = false;

        if (initialAssociatedText != null)
        {
            processAADBytes(initialAssociatedText, 0, initialAssociatedText.length);
        }
    }

    public void processAADByte(byte in)
    {
        if (cipherInitialized)
        {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        mac.update(in);
    }

    public void processAADBytes(byte[] in, int inOff, int len)
    {
        if (cipherInitialized)
        {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        mac.update(in, inOff, len);
    }

    public int processByte(byte in, byte[] out, int outOff)
            throws DataLengthException
    {
        initCipher();

        return process(in, out, outOff);
    }

    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
            throws DataLengthException
    {
        initCipher();

        if (in.length < (inOff + len))
        {
            throw new DataLengthException("Input buffer too short");
        }

        int resultLen = 0;

        for (int i = 0; i != len; i++)
        {
            resultLen += process(in[inOff + i], out, outOff + resultLen);
        }

        return resultLen;
    }

    public int doFinal(byte[] out, int outOff)
            throws IllegalStateException, InvalidCipherTextException
    {
        initCipher();

        int extra = bufOff;
        byte[] tmp = new byte[bufBlock.length];

        bufOff = 0;

        if (forEncryption)
        {
            if (out.length < (outOff + extra + macSize))
            {
                throw new OutputLengthException("Output buffer too short");
            }

            if (extra != blockSize){
                throw new InvalidCipherTextException("Unaligned input - input need to be padded to cipher block size.");
            }

            // Encrypt the final block
            // TODO: use Cipher object, with padding added...
            cipher.processBlock(bufBlock, 0, tmp, 0);

            System.arraycopy(tmp, 0, out, outOff, extra);

            // TODO: calculate MAC on padded data.
            mac.update(tmp, 0, extra);

            calculateMac();

            System.arraycopy(macBlock, 0, out, outOff + extra, macSize);

            reset(false);

            return extra + macSize;
        }
        else
        {
            if (extra < macSize)
            {
                throw new InvalidCipherTextException("data too short");
            }
            if (out.length < (outOff + extra - macSize))
            {
                throw new OutputLengthException("Output buffer too short");
            }
            if (extra > macSize)
            {
                mac.update(bufBlock, 0, extra - macSize);

                cipher.processBlock(bufBlock, 0, tmp, 0);

                System.arraycopy(tmp, 0, out, outOff, extra - macSize);
            }

            calculateMac();

            if (!verifyMac(bufBlock, extra - macSize))
            {
                throw new InvalidCipherTextException("mac check in EAX failed");
            }

            reset(false);

            return extra - macSize;
        }
    }

    public byte[] getMac()
    {
        byte[] mac = new byte[macSize];

        System.arraycopy(macBlock, 0, mac, 0, macSize);

        return mac;
    }

    public int getUpdateOutputSize(int len)
    {
        int totalData = len + bufOff;
        if (!forEncryption)
        {
            if (totalData < macSize)
            {
                return 0;
            }
            totalData -= macSize;
        }
        return totalData - totalData % blockSize;
    }

    public int getOutputSize(int len)
    {
        int totalData = len + bufOff;

        if (forEncryption)
        {
            return totalData + macSize;
        }

        return totalData < macSize ? 0 : totalData - macSize;
    }

    private int process(byte b, byte[] out, int outOff)
    {
        bufBlock[bufOff++] = b;

        // TODO: optimize this, do function call only on block encryption.
        if (bufOff == bufBlock.length)
        {
            if (out.length < (outOff + blockSize))
            {
                throw new OutputLengthException("Output buffer is too short");
            }
            // TODO Could move the processByte(s) calls to here
//            initCipher();

            int size;

            if (forEncryption)
            {
                // Process one block from the bufBlock. It is exactly the size of the block.
                size = cipher.processBlock(bufBlock, 0, out, outOff);

                // MAC the whole ciphertext.
                mac.update(out, outOff, blockSize);
            }
            else
            {
                // MAC only the first part of the bufBlock.
                mac.update(bufBlock, 0, blockSize);

                // processBlock from the BlockCipher processes only one block, leaving the second one untouched - mac is there.
                size = cipher.processBlock(bufBlock, 0, out, outOff);
            }

            bufOff = 0;
            if (!forEncryption)
            {
                // If decrypting, we don't know when the last MAC block is present in the input data.
                // From this reason we are keeping potential mac block in the bufBlock all the time.
                // Now one decryption operation was performed, thus macSize part is moved to the beginning
                // of the bufBlock.
                System.arraycopy(bufBlock, blockSize, bufBlock, 0, macSize);
                bufOff = macSize;
            }

            return size;
        }

        return 0;
    }

    private boolean verifyMac(byte[] mac, int off)
    {
        int nonEqual = 0;

        // Constant time comparison - important for timing attacks prevention.
        for (int i = 0; i < macSize; i++)
        {
            nonEqual |= (macBlock[i] ^ mac[off + i]);
        }

        return nonEqual == 0;
    }
}
