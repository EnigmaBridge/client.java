package com.enigmabridge.provider.aes;

import com.enigmabridge.EBCryptoException;
import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.comm.EBProcessDataCall;
import com.enigmabridge.comm.EBProcessDataResponse;
import com.enigmabridge.create.EBUOHandle;
import com.enigmabridge.provider.EBSymmetricKey;
import com.enigmabridge.provider.EBUOKey;
import com.enigmabridge.provider.EnigmaProvider;
import com.enigmabridge.provider.parameters.EBKeyParameter;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

import java.io.IOException;

/**
 * Simple AES engine using EB call to process single AES block.
 * EB uses internally CBC mode with zero IV for PLAINAES, PLAINAESDECRYPT UserObjects.
 * In this default setting, we can perform arbitrary AES operation with arbitrary block cipher mode but at the cost
 * each block has to be transferred separately.
 *
 * Created by dusanklinec on 12.07.16.
 */
public class AESEngine implements BlockCipher
{
    private EnigmaProvider provider;
    private boolean     forEncryption;
    private EBSymmetricKey aesKey;
    private static final int BLOCK_SIZE = 16;

    /**
     * default constructor - 128 bit block size.
     */
    public AESEngine()
    {
    }

    public AESEngine(EnigmaProvider provider)
    {
        this.provider = provider;
    }

    /**
     * initialise an AES cipher.
     *
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(
            boolean           forEncryption,
            CipherParameters params)
    {
        if (params instanceof EBKeyParameter)
        {
            final EBKeyParameter ebParam = (EBKeyParameter) params;
            final EBUOKey uoKey = ebParam.getUoKey();

            if (!(uoKey instanceof EBSymmetricKey) || !"AES".equalsIgnoreCase(uoKey.getAlgorithm())){
                throw new IllegalArgumentException("AES expects AES key");
            }

            final EBSymmetricKey tmpAesKey = (EBSymmetricKey)uoKey;
            if (isMatchingKey(forEncryption, tmpAesKey)){
                this.aesKey = tmpAesKey;

            } else {
                // Is inversion key available?
                final EBSymmetricKey inversionKey = tmpAesKey.getInversionKey();
                if (inversionKey == null){
                    throw new IllegalArgumentException("AES key for "
                            + (forEncryption ? "encryption" : "decryption")
                            + " is not provided");

                } else if (!isMatchingKey(forEncryption, inversionKey)){
                    throw new IllegalArgumentException("Inversion key is still not matching - keys are corrupted");

                } else {
                    this.aesKey = inversionKey;
                }
            }

            this.forEncryption = forEncryption;
            return;
        }

        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    protected boolean isMatchingKey(boolean forEncryption, EBSymmetricKey key){
        return (forEncryption && key.isEncryptionKey()) || (!forEncryption && key.isDecryptionKey());
    }

    public String getAlgorithmName()
    {
        return "AES";
    }

    public int getBlockSize()
    {
        return BLOCK_SIZE;
    }

    public int processBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
    {
        if (this.aesKey == null)
        {
            throw new IllegalStateException("AES engine not initialised");
        }

        if ((inOff + (32 / 2)) > in.length)
        {
            throw new DataLengthException("input buffer too short");
        }

        if ((outOff + (32 / 2)) > out.length)
        {
            throw new OutputLengthException("output buffer too short");
        }

        final EBProcessDataCall call = new EBProcessDataCall.Builder()
                .setKey(aesKey)
                .build();

        try {
            final EBProcessDataResponse response = call.doRequest(in, inOff, BLOCK_SIZE);
            if (!response.isCodeOk()){
                throw new EBCryptoException("Server returned invalid response");
            }

            final byte[] respData = response.getProtectedData();
            System.arraycopy(respData, 0, out, outOff, BLOCK_SIZE);

        } catch (IOException e) {
            throw new EBCryptoException("ProcessData failed for: " + new EBUOHandle(aesKey.getUserObjectInfo()), e);
        } catch (EBCorruptedException e) {
            throw new EBCryptoException("ProcessData failed for: " + new EBUOHandle(aesKey.getUserObjectInfo()), e);
        }

        return BLOCK_SIZE;
    }

    public void reset()
    {
    }

}