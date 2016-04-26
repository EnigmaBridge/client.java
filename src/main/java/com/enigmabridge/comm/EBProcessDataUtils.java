package com.enigmabridge.comm;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBEngineException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Process data utils.
 * Created by dusanklinec on 26.04.16.
 */
public class EBProcessDataUtils {
    public static final String PROCESS_DATA_CIPHER = "ProcessDataV1";
    public static final byte[] ZERO_IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    /**
     * Creates a new cipher instance with given keys.
     * @param forEncryption
     * @param keys
     * @return
     */
    public static EBProcessDataCipherHolder initCipher(boolean forEncryption, EBCommKeys keys) throws EBEngineException {
        try {
            // Initialize cipher
            final Cipher enc = Cipher.getInstance("AES/CBC/PKCS5Padding");
            enc.init(
                    forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keys.getEncKey(), "AES"),
                    new IvParameterSpec(ZERO_IV));

            // Initialize CBC MAC.
            // There is no AES-CBC-MAC in the BouncyCastle provider.
            final BlockCipher cipher = new AESLightEngine();
            final CBCBlockCipherMac mac = new CBCBlockCipherMac(cipher, cipher.getBlockSize() * 8);
            mac.init(new KeyParameter(keys.getMacKey()));

            return new EBProcessDataCipherHolder(forEncryption, enc, mac);

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

    public static byte[] processBuffer(EBProcessDataCipherHolder cipher, byte[] buffer){
        //TODO: implement.
        return null;
    }
}
