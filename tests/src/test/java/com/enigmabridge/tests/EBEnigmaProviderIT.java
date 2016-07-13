package com.enigmabridge.tests;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBEngine;
import com.enigmabridge.EBSettingsBase;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.create.Constants;
import com.enigmabridge.create.EBUOGetTemplateRequest;
import com.enigmabridge.misc.EBTestingUtils;
import com.enigmabridge.provider.EnigmaProvider;
import com.enigmabridge.provider.specs.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import static org.testng.Assert.assertEquals;

/**
 * Basic tests of Enigma Bridge crypto provider.
 * Created by dusanklinec on 04.05.16.
 */
public class EBEnigmaProviderIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBEnigmaProviderIT.class);

    // General engine - common
    private final EBEngine engine = new EBEngine();

    // TEST API key
    private final String apiKey = EBTestingUtils.API_KEY;

    // Testing endpoint
    private EBEndpointInfo endpoint;

    // Default settings - POST method
    private EBConnectionSettings settings;

    // EBSettings - defaults.
    private EBSettingsBase defaultSettings;

    // Enigma crypto provider.
    private static EnigmaProvider provider;

    public EBEnigmaProviderIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
        endpoint = new EBEndpointInfo(EBTestingUtils.CONNECTION_STRING);
        settings = new EBConnectionSettings();

        defaultSettings = new EBSettingsBase.Builder()
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setConnectionSettings(settings)
                .build();

        engine.setDefaultSettings(defaultSettings);

        // Adding Enigma as a security provider.
        provider = new EnigmaProvider(engine);
        Security.addProvider(provider);

        // Do not forget to add BouncyCastle provider.
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA() throws Exception {
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "EB");
        final EBRSAKeyGenParameterSpec keySpec = new EBRSAKeyGenParameterSpec(2048).setTplReq(tplReq);

        kpGen.initialize(keySpec);
        final KeyPair keyPair = kpGen.generateKeyPair();

        // Generate random input to encrypt.
        final SecureRandom rand = new SecureRandom();
        final byte[] testInput = new byte[32];
        rand.nextBytes(testInput);

        // Encrypt.
        final Cipher rsaEnc = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaEnc.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        final byte[] ciphertext = rsaEnc.doFinal(testInput);

        // Decrypt
        final Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        final byte[] decrypted = rsa.doFinal(ciphertext);

        // Test
        assertEquals(decrypted, testInput, "RSAdecrypt(RSAencrypt(x)) != x");
        LOG.info("DONE");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAES() throws Exception {
        final SecureRandom rand = new SecureRandom();

        // Create AES keys, for encryption & decryption.
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final KeyGenerator kGen = KeyGenerator.getInstance("AES", "EB");
        final EBSymmetricKeyGenParameterSpec keySpec = new EBSymmetricKeyGenParameterSpec(128)
                .setTplReq(tplReq)
                .setKeyType(EBSymmetricKeyGenTypes.BOTH);

        kGen.init(keySpec);
        final SecretKey secretKey = kGen.generateKey();

        // Very simple 1 block test.
        final byte[] simpleInput = new byte[16];
        rand.nextBytes(simpleInput);

        // Encrypt.
        final Cipher aesEncSimple = Cipher.getInstance("AES/ECB/NoPadding", "EB");
        aesEncSimple.init(Cipher.ENCRYPT_MODE, secretKey);
        final byte[] ciphertextSimple = aesEncSimple.doFinal(simpleInput);

        // Decrypt
        final Cipher aesDecSimple = Cipher.getInstance("AES/ECB/NoPadding", "EB");
        aesDecSimple.init(Cipher.DECRYPT_MODE, secretKey);
        final byte[] decryptedSimple = aesDecSimple.doFinal(ciphertextSimple);

        // Test
        assertEquals(decryptedSimple, simpleInput, "AESdecrypt(AESencrypt(x)) != x");

        // More complex test - CBC mode, more blocks.
        // Generate random input to encrypt.
        final byte[] testInput = new byte[24];
        final byte[] iv = new byte[16];
        rand.nextBytes(testInput);
        rand.nextBytes(iv);

        // Encrypt.
        final Cipher aesEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING", "EB");
        aesEnc.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        final byte[] ciphertext = aesEnc.doFinal(testInput);

        // Decrypt
        final Cipher aesDec = Cipher.getInstance("AES/CBC/PKCS5PADDING", "EB");
        aesDec.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        final byte[] decrypted = aesDec.doFinal(ciphertext);

        // Test
        assertEquals(decrypted, testInput, "AESdecrypt(AESencrypt(x)) != x");
        LOG.info("DONE");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAESFactory() throws Exception {
        final SecureRandom rand = new SecureRandom();

        // Create AES keys, for encryption & decryption.
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final SecretKeyFactory kFact = SecretKeyFactory.getInstance("AES", "EB");

        final byte[] aesKey = new byte[16];
        rand.nextBytes(aesKey);

        final EBSecretKeySpec spec = new EBSecretKeySpec(aesKey, "AES", engine);
        spec.setKeyType(EBSymmetricKeyGenTypes.BOTH);
        spec.setTplReq(tplReq);

        // Method accepts SecretKeySpec (simple one) or EBSecretKeySpec
        final SecretKey key = kFact.generateSecret(spec);

        // Bouncy castle secret
        final SecretKeySpec bcKey = new SecretKeySpec(aesKey, "AES");

        // Very simple 1 block test.
        final byte[] simpleInput = new byte[16];
        rand.nextBytes(simpleInput);

        // Encrypt.
        final Cipher aesEncSimple = Cipher.getInstance("AES/ECB/NoPadding", "EB");
        aesEncSimple.init(Cipher.ENCRYPT_MODE, key);
        final byte[] ciphertextSimple = aesEncSimple.doFinal(simpleInput);

        // Decrypt
        final Cipher aesDecSimple = Cipher.getInstance("AES/ECB/NoPadding", "BC");
        aesDecSimple.init(Cipher.DECRYPT_MODE, bcKey);
        final byte[] decryptedSimple = aesDecSimple.doFinal(ciphertextSimple);

        // Test
        assertEquals(decryptedSimple, simpleInput, "AESdecrypt(AESencrypt(x)) != x");

        // More complex test - CBC mode, more blocks, inverted operation.
        // Generate random input to encrypt.
        final byte[] testInput = new byte[24];
        final byte[] iv = new byte[16];
        rand.nextBytes(testInput);
        rand.nextBytes(iv);

        // Encrypt.
        final Cipher aesEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
        aesEnc.init(Cipher.ENCRYPT_MODE, bcKey, new IvParameterSpec(iv));
        final byte[] ciphertext = aesEnc.doFinal(testInput);

        // Decrypt
        final Cipher aesDec = Cipher.getInstance("AES/CBC/PKCS5PADDING", "EB");
        aesDec.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        final byte[] decrypted = aesDec.doFinal(ciphertext);

        // Test
        assertEquals(decrypted, testInput, "AESdecrypt(AESencrypt(x)) != x");
        LOG.info("DONE");
    }
}
