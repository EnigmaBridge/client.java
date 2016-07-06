package com.enigmabridge.create;

import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.EBCommUtils;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * EB Create utils.
 *
 * Created by dusanklinec on 01.07.16.
 */
public class EBCreateUtils {

    /**
     * Generates UO handle.
     *
     * @param apiKey API KEY to access EB
     * @param uoId user object ID
     * @param uoType user object type - composite
     * @return UO handle string
     */
    public static String getUoHandle(String apiKey, long uoId, long uoType){
        // TEST_API 00 00000013 00 00a00004
        return String.format("%s00%08x00%08x", apiKey, uoId, uoType);
    }

    /**
     * Generates UO handle object from string.
     *
     * @param handleString handle string
     * @return handle object
     */
    public static EBUOHandle getHandleObj(String handleString){
        final int len = handleString.length();
        if (len < 19){
            throw new IllegalArgumentException("handle string is too short");
        }

        final String apiKey = handleString.substring(0, len-2-8-2-8);
        final String uoIdStr = handleString.substring(len-2-8-2-8, len-8-2);
        final String uoTypeStr = handleString.substring(len-8, len);

        return new EBUOHandle(
                apiKey,
                Long.parseLong(uoIdStr, 16),
                Long.parseLong(uoTypeStr, 16)
        );
    }

    /**
     * Reads serialized import public key, builds key spec (transparent key representation - modulus + exponent).
     *
     * @param keyVal RSA public key to deserialize.
     * @return key spec
     */
    public static RSAPublicKeySpec readSerializedRSAPublicKey(byte[] keyVal){
        // Read serialized import public key.
        BigInteger exp = null;
        BigInteger mod = null;

        // TAG|len-2B|value. 81 = exponent, 82 = modulus
        byte tmpDat[] = null;
        int tag, len, pos, ln = keyVal.length;
        for(pos = 0; pos < ln;){
            tag = keyVal[pos];  pos += 1;
            len = EBCommUtils.getShort(keyVal, pos); pos += 2;
            switch(tag){
                case (byte)0x81:
                    tmpDat = new byte[len];
                    System.arraycopy(keyVal, pos, tmpDat, 0, len);

                    exp = new BigInteger(1, tmpDat);
                    break;

                case (byte)0x82:
                    tmpDat = new byte[len];
                    System.arraycopy(keyVal, pos, tmpDat, 0, len);

                    mod = new BigInteger(1, tmpDat);
                    break;
                default:
                    break;
            }

            pos += len;
        }

        if (exp == null || mod == null){
            throw new EBInvalidException("RSA public key is malformed");
        }

        return new RSAPublicKeySpec(mod, exp);
    }


}
