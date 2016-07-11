package com.enigmabridge.tests;

import com.enigmabridge.EBEndpointInfo;
import com.enigmabridge.EBEngine;
import com.enigmabridge.EBSettingsBase;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.create.Constants;
import com.enigmabridge.create.EBUOGetTemplateRequest;
import com.enigmabridge.misc.EBTestingUtils;
import com.enigmabridge.provider.EnigmaProvider;
import com.enigmabridge.provider.specs.EBRSAKeyGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import javax.crypto.Cipher;
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
    public void testRSAKeyPair() throws Exception {
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
}
