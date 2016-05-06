package com.enigmabridge.misc;

import com.enigmabridge.EBUtils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Misc utilities & constants for testing.
 * Created by dusanklinec on 04.05.16.
 */
public class EBTestingUtils {
    public static final String CONNECTION_STRING = "https://site2.enigmabridge.com:11180";
    public static final String API_KEY = "TETS_API";

    public static final long UOID_RANDOM = 0x77ee;
    public static final long UOID_AES = 0xEE01L;
    public static final long UOID_RSA1k= 0x7654L;
    public static final long UOID_RSA2k= 0x9876L;

    public static final long UOID_RSA1k_KNOWN= 0x10;
    public static final long UOID_RSA2k_KNOWN= 0x11;

    public static final long RSA1k_PUB_EXP = 65537;
    public static final long RSA2k_PUB_EXP = 65537;

    public static final String RSA1k_MODULUS = "00c4bdbd7a912dca46e045e5787fc9bc43e7de4bc86155b3b4e39b1533ea3e2a9fafafe63389657f4ff971e2d0cf636bed5d2f183eac03eb63ad37591055cbf91fdd848652fcc1bbd01c74e01e53337337a2d749a1f07be7f19fce450d551955ce4e36ccc4d143f623544ca17bb42dbfeabc08fef6bbeefc68be8971caa3463d87";
    public static final String RSA2k_MODULUS = "008bc31e1e1986665f2cea471cb5a3315c733672b3ce621b0a1dff711523ef2d0d79df1eca52d2222b7331fe1bd79605a12fb9c9f00e2a34bc7e4e773128816cb6930aab8cd8a7fbd4d50e0b98fa7001e6a5eada763d4eebe103616ab54be0bd0d01dcc25787d36ec86852cd7c2bedd8fcdd0647c7ba4c54df19ccb0848a1e4023967b6c12817f42d6b94bb9b97d994d02eba018bf57878cf16c186674626482947de05258696274a7ab33ab30519c609ac9e360ced6b26932325a3702056382c01bd377cc1e9e19fdb99ebb2369ee46de17b30ce26dc03acb2c16e8472e026249a249dea63dee8811cbf3329d80dfbb4d11ec71267e8c1a64a3d576520f30d901";

    public static PublicKey createRSAPublicKey(BigInteger modulus, BigInteger publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);

        // Create a key factory
        KeyFactory factory = KeyFactory.getInstance("RSA");

        // Create the RSA private and public keys
        return factory.generatePublic(spec);
    }

    public static PublicKey createRSAPublicKey1k() throws InvalidKeySpecException, NoSuchAlgorithmException {
        final BigInteger exp = BigInteger.valueOf(EBTestingUtils.RSA1k_PUB_EXP);
        final BigInteger mod = new BigInteger(EBUtils.hex2byte(EBTestingUtils.RSA1k_MODULUS));
        return createRSAPublicKey(mod, exp);
    }

    public static PublicKey createRSAPublicKey2k() throws InvalidKeySpecException, NoSuchAlgorithmException {
        final BigInteger exp = BigInteger.valueOf(EBTestingUtils.RSA2k_PUB_EXP);
        final BigInteger mod = new BigInteger(EBUtils.hex2byte(EBTestingUtils.RSA2k_MODULUS));
        return createRSAPublicKey(mod, exp);
    }
}
