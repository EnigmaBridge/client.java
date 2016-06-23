package com.enigmabridge.comm;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBEngineException;
import com.enigmabridge.provider.parameters.EBCommKeysParameter;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * CipherSpi for process data cipher.
 * Enables stream processing of the input data for both encryption and decryption.
 * Idea: Extend CipherSpi so it can be exported by a crypto provider and
 * obtained as a Cipher instance. This can be used for CipherInputStream, CipherOutputStream.
 *
 * For decryption case the stream processing is not trivial - we don't know which last block is the MAC block,
 * which must not be passed for decryption to the decryption cipher object. Thus we are keeping always 2 blocks in
 * the decryption mode in memory, processing them as they fill to the max capacity.
 *
 * This class combines both CipherSpi and AEADBlockCipher interface from BouncyCastle.
 * Refactoring may be needed for a better fit to BouncyCastle architecture. Maybe wrapping
 * by BaseBlockCipher.
 *
 * Warning: Not tested yet.
 * Created by dusanklinec on 26.04.16.
 */
public class EBProcessDataCipherSpi extends CipherSpi implements AEADBlockCipher {
    private EBProcessDataCipher cipherHolder;

    protected boolean forEncryption;
    protected Cipher cipher;
    protected org.bouncycastle.crypto.Mac mac;

    // Cipher state parameters.
    private int blockSize;
    private byte[] macBlock;

    private int macSize;
    private byte[] bufBlock;
    private int bufOff;

    private boolean cipherInitialized;
    private byte[] initialAssociatedText;

    @Override
    protected void engineSetMode(String s) throws NoSuchAlgorithmException {
        String md = Strings.toUpperCase(s);

        if (md.equals("NONE") || md.equals("ECB")) {
            return;
        }

        if (md.equals("1")) {
            return;
        } else if (md.equals("2")) {
            return;
        }

        throw new NoSuchAlgorithmException("can't support mode " + s);
    }

    @Override
    protected void engineSetPadding(String pad) throws NoSuchPaddingException {
        if (pad != null && !pad.isEmpty() && !pad.equalsIgnoreCase("NOPADDING")){
            throw new NoSuchPaddingException(pad + " Cannot be applied");
        }
    }

    @Override
    protected int engineGetBlockSize() {
        return cipher.getBlockSize();
    }

    @Override
    protected int engineGetOutputSize(int len) {
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

    @Override
    protected byte[] engineGetIV() {
        return EBProcessDataCipher.ZERO_IV;
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override
    protected void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        engineInitImpl(i, key);
    }

    @Override
    protected void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInitImpl(i, key);
    }

    @Override
    protected void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInitImpl(i, key);
    }

    protected void engineInitImpl(int i, Key key) throws InvalidKeyException {
        if (i != Cipher.ENCRYPT_MODE && i != Cipher.DECRYPT_MODE){
           throw new InvalidKeyException("Invalid mode with the key");
        }

        if (!(key instanceof EBCommKeys)){
            throw new InvalidKeyException("Invalid key, use EBCommKeys");
        }

        try {
            this.cipherHolder = EBProcessDataCipher.initCipher(i == Cipher.ENCRYPT_MODE, (EBCommKeys) key);
            this.cipher = this.cipherHolder.getEnc();
            this.mac = this.cipherHolder.getMac();
            this.blockSize = cipher.getBlockSize();
            this.macSize = mac.getMacSize();

        } catch (EBEngineException e) {
            throw new InvalidKeyException(e);
        }
    }

    @Override
    protected byte[] engineUpdate(byte[] input,
                                  int inputOffset,
                                  int inputLen) {

        byte[] output = new byte[2*getUpdateOutputSize(inputLen)];
        int ret = processBytes(input, inputOffset, inputLen, output, 0);
        return output;
    }

    @Override
    protected int engineUpdate(byte[] input,
                               int inputOffset,
                               int inputLen,
                               byte[] output,
                               int outputOffset) throws ShortBufferException {

        return processBytes(input, inputOffset, inputLen, output, outputOffset);
    }

    @Override
    protected byte[] engineDoFinal(byte[] input,
                                   int inputOffset,
                                   int inputLen) throws IllegalBlockSizeException, BadPaddingException {

        byte[] output = new byte[2*getUpdateOutputSize(inputLen)];
        int ret = processBytes(input, inputOffset, inputLen, output, 0);
        try {
            ret += doFinal(output, ret);
        } catch(InvalidCipherTextException e){
            throw new IllegalBlockSizeException(e.getMessage());
        }

        return output;
    }

    @Override
    protected int engineDoFinal(byte[] input,
                                int inputOffset,
                                int inputLen,
                                byte[] output,
                                int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {

        int ret = processBytes(input, inputOffset, inputLen, output, outputOffset);
        try {
            ret += doFinal(output, outputOffset + ret);
        } catch(InvalidCipherTextException e){
            throw new IllegalBlockSizeException(e.getMessage());
        }
        return ret;
    }

    // AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters params)
            throws IllegalArgumentException
    {
        this.forEncryption = forEncryption;
        byte[] iv = EBProcessDataCipher.ZERO_IV;
        CipherParameters keyParam;

        if (params instanceof AEADParameters)
        {
            AEADParameters param = (AEADParameters)params;

            initialAssociatedText = param.getAssociatedText();
            macSize = param.getMacSize() / 8;
            keyParam = param.getKey();
        }
        else if (params instanceof ParametersWithIV)
        {
            ParametersWithIV param = (ParametersWithIV)params;

            initialAssociatedText = null;
            macSize = mac.getMacSize() / 2;
            iv = param.getIV();
            keyParam = param.getParameters();
        }
        else
        {
            throw new IllegalArgumentException("invalid parameters passed to " + getAlgorithmName());
        }

        if (!(keyParam instanceof EBCommKeysParameter))
        {
            throw new IllegalArgumentException("No key parameters");
        }

        bufBlock = new byte[forEncryption ? blockSize : (blockSize + macSize)];

        // Key reuse implemented in CBC mode of underlying CMac
        mac.init(((EBCommKeysParameter) keyParam).getMacKeyparameter());

        // Cipher initialization.
        try {
            cipher.init(
                    forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
                    new SecretKeySpec(((EBCommKeysParameter) keyParam).getEncKey(), cipher.getAlgorithm()),
                    new IvParameterSpec(iv)
            );
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }

        reset();
    }

    @Override
    public String getAlgorithmName() {
        return "PDT";
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return null;
    }

    private void initCipher()
    {
        if (cipherInitialized)
        {
            return;
        }

        cipherInitialized = true;
    }

    private void calculateMac()
    {
        byte[] outC = new byte[blockSize];
        mac.doFinal(outC, 0);

        for (int i = 0; i < macBlock.length; i++)
        {
            macBlock[i] = outC[i];
        }
    }

    public void reset()
    {
        reset(true);
    }

    private void reset(
            boolean clearMac)
    {
        try {
            cipher.doFinal();
        } catch (Exception e) {

        }

        mac.reset();

        bufOff = 0;
        Arrays.fill(bufBlock, (byte)0);

        if (clearMac)
        {
            Arrays.fill(macBlock, (byte)0);
        }

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

        try {
            return process(in, out, outOff);
        } catch (ShortBufferException e) {
            throw new DataLengthException("Buffer too short");
        }
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
        try
        {
            for (int i = 0; i != len; i++)
            {
                resultLen += process(in[inOff + i], out, outOff + resultLen);
            }
        }
        catch (ShortBufferException e) {
            throw new DataLengthException("Buffer too short");
        }

        return resultLen;
    }

    public int doFinal(byte[] out, int outOff)
            throws IllegalStateException, InvalidCipherTextException
    {
        initCipher();

        int extra = bufOff;
        byte[] tmp = new byte[2*bufBlock.length];

        bufOff = 0;

        if (forEncryption)
        {
            if (out.length < (outOff + extra + macSize))
            {
                throw new OutputLengthException("Output buffer too short");
            }

            if ((extra % blockSize) != 0){
                throw new InvalidCipherTextException("Unaligned input - input need to be padded to cipher block size.");
            }

            // Encrypt the final block
            int finalProduced = 0;
            try {
                finalProduced = cipher.doFinal(bufBlock, 0, extra, tmp, 0);
            } catch (ShortBufferException e) {
                throw new InvalidCipherTextException("Short buffer", e);
            } catch (IllegalBlockSizeException e) {
                throw new InvalidCipherTextException("Invalid block size", e);
            } catch (BadPaddingException e) {
                throw new InvalidCipherTextException("Bad padding", e);
            }

            System.arraycopy(tmp, 0, out, outOff, finalProduced);

            mac.update(tmp, 0, finalProduced);

            calculateMac();

            System.arraycopy(macBlock, 0, out, outOff + finalProduced, macSize);

            reset(false);

            return finalProduced + macSize;
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

            int finalProduced = 0;
            if (extra > macSize)
            {
                mac.update(bufBlock, 0, extra - macSize);
                try {
                    finalProduced = cipher.doFinal(bufBlock, 0, extra - macSize, tmp, 0);
                } catch (ShortBufferException e) {
                    throw new InvalidCipherTextException("Short buffer", e);
                } catch (IllegalBlockSizeException e) {
                    throw new InvalidCipherTextException("Invalid block size", e);
                } catch (BadPaddingException e) {
                    throw new InvalidCipherTextException("Bad padding", e);
                }

                System.arraycopy(tmp, 0, out, outOff, finalProduced);
            }

            calculateMac();

            if (!verifyMac(bufBlock, extra - macSize))
            {
                throw new InvalidCipherTextException("mac check in EAX failed");
            }

            reset(false);

            return finalProduced;
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

    private int process(byte b, byte[] out, int outOff) throws ShortBufferException {
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
                size = cipher.update(bufBlock, 0, bufBlock.length, out, outOff);

                // MAC the whole ciphertext.
                mac.update(out, outOff, size);
            }
            else
            {
                // MAC only the first part of the bufBlock.
                mac.update(bufBlock, 0, blockSize);

                // processBlock from the BlockCipher processes only one block, leaving the second one untouched - mac is there.
                size = cipher.update(bufBlock, 0, blockSize, out, outOff);
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
