package com.enigmabridge.create;

import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.EBCommUtils;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKey;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKeyWrapper;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
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

    public static short exportPrivateKeyUOStyle(byte[] buffer, short baseOffset, short length, RSAPrivateCrtKey privKey) {
        return exportPrivateKeyUOStyle(buffer, baseOffset, length, privKey, (byte) 0);
    }

    public static short exportPrivateKeyUOStyle(byte[] buffer, short baseOffset, short length, RSAPrivateKey privKey, BigInteger e, byte spareBytes) {
        return exportPrivateKeyUOStyle(buffer, baseOffset, length,
                new EBRSAPrivateCrtKeyWrapper(new EBRSAPrivateCrtKey(privKey, e)),
                spareBytes);
    }

    public static short exportPrivateKeyUOStyle(byte[] buffer, short baseOffset, short length, RSAPrivateCrtKey privKey, byte spareBytes) {
        return exportPrivateKeyUOStyle(buffer, baseOffset, length, new EBRSAPrivateCrtKeyWrapper(privKey), spareBytes);
    }

    public static byte[] exportPrivateKeyUOStyle(EBRSAPrivateCrtKeyWrapper privKey) {
        return exportPrivateKeyUOStyle(privKey, (byte) 0);
    }

    public static byte[] exportPrivateKeyUOStyle(EBRSAPrivateCrtKeyWrapper privKey, byte spareBytes) {
        final int baseLen = privKey.getModulus().bitLength()/8;
        final int buffLen = baseLen * 6;
        byte[] buffer = new byte[buffLen];
        short usedBytes = exportPrivateKeyUOStyle(buffer, (short) 0, (short) -1, privKey, spareBytes);

        byte[] realBuffer = new byte[usedBytes];
        System.arraycopy(buffer, 0, realBuffer, 0, usedBytes);

        return realBuffer;
    }

    public static short exportPrivateKeyUOStyle(
            byte[] buffer,
            short baseOffset,
            short length,
            EBRSAPrivateCrtKeyWrapper privKey,
            byte spareBytes)
    {
        short tempOffset = baseOffset;

        // Separate parts of key
        short keyBaseOffset = tempOffset;
        tempOffset += EBCommUtils.UO_KEY_SIZE_LENGTH;   // make space for part length value, set later
        short partLen = privKey.getDP1(buffer, tempOffset);
        // If there is leading zero (some software lib keys make it),
        // then export again shifted by -1 (will put leading zero into
        // TLV length segment where it will be overwritten just after)
        if (buffer[tempOffset] == (byte) 0) {
            partLen = privKey.getDP1(buffer, (short) (tempOffset - 1));
            partLen--; // don't count leading zero
        }
        EBCommUtils.setShort(buffer, (short) (tempOffset - EBCommUtils.UO_KEY_SIZE_LENGTH), partLen);
        tempOffset += partLen;
        tempOffset += EBCommUtils.UO_KEY_SIZE_LENGTH;   // make space for part length value, set later
        partLen = privKey.getDQ1(buffer, tempOffset);
        if (buffer[tempOffset] == (byte) 0) { // Check for leading zero (see reason above)
            partLen = privKey.getDQ1(buffer, (short) (tempOffset - 1));
            partLen--; // don't count leading zero
        }
        EBCommUtils.setShort(buffer, (short) (tempOffset - EBCommUtils.UO_KEY_SIZE_LENGTH), partLen);
        tempOffset += partLen;
        tempOffset += EBCommUtils.UO_KEY_SIZE_LENGTH;   // make space for part length value, set later
        partLen = privKey.getP(buffer, tempOffset);
        if (buffer[tempOffset] == (byte) 0) { // Check for leading zero (see reason above)
            partLen = privKey.getP(buffer, (short) (tempOffset - 1));
            partLen--; // don't count leading zero
        }
        EBCommUtils.setShort(buffer, (short) (tempOffset - EBCommUtils.UO_KEY_SIZE_LENGTH), partLen);
        tempOffset += partLen;
        tempOffset += EBCommUtils.UO_KEY_SIZE_LENGTH;   // make space for part length value, set later
        partLen = privKey.getQ(buffer, tempOffset);
        if (buffer[tempOffset] == (byte) 0) { // Check for leading zero (see reason above)
            partLen = privKey.getQ(buffer, (short) (tempOffset - 1));
            partLen--; // don't count leading zero
        }
        EBCommUtils.setShort(buffer, (short) (tempOffset - EBCommUtils.UO_KEY_SIZE_LENGTH), partLen);
        tempOffset += partLen;
        tempOffset += EBCommUtils.UO_KEY_SIZE_LENGTH;   // make space for part length value, set later
        partLen = privKey.getPQ(buffer, tempOffset);
        if (buffer[tempOffset] == (byte) 0) { // Check for leading zero (see reason above)
            partLen = privKey.getPQ(buffer, (short) (tempOffset - 1));
            partLen--; // don't count leading zero
        }
        EBCommUtils.setShort(buffer, (short) (tempOffset - EBCommUtils.UO_KEY_SIZE_LENGTH), partLen);
        tempOffset += partLen;

        // Add spare bytes if required (used as compensation for tmaple objects generated later)
        tempOffset += spareBytes;

        // Not enough room for the RSA key
        if (length > 0 && length < (tempOffset - baseOffset)) {
            throw new EBInvalidException("Key buffer is too short.");
        }

        // Return overall length that was inserted
        return (short) (tempOffset - baseOffset);
    }

}
