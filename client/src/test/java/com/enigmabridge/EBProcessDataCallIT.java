package com.enigmabridge;

import com.enigmabridge.comm.*;
import com.enigmabridge.misc.EBTestingUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;

import static org.testng.Assert.*;

/**
 * Test for basic ProcessData() calls.
 * Created by dusanklinec on 03.05.16.
 */
public class EBProcessDataCallIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataCallIT.class);

    // General engine - common
    private EBEngine engine;

    // TEST API key
    private final String apiKey = EBTestingUtils.API_KEY;

    // Testing endpoint
    private EBEndpointInfo endpoint;

    // Trust object for accepting Let's Encrypt certificates
    private EBAdditionalTrust trust;

    // Default settings - POST method
    private EBConnectionSettings settings;

    // EBSettings - defaults.
    private EBSettingsBase defaultSettings;

    // AES UOs comm keys
    private EBCommKeys ckAES;

    // RSA UOs comm keys
    private EBCommKeys ckRSA;

    public EBProcessDataCallIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Do not forget to add BouncyCastle provider.
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
        endpoint = new EBEndpointInfo(EBTestingUtils.CONNECTION_STRING);

        trust = new EBAdditionalTrust(true, true);

        settings = new EBConnectionSettings()
                .setMethod(EBCommUtils.METHOD_POST)
                .setTrust(trust);

        defaultSettings = new EBSettingsBase.Builder()
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setConnectionSettings(settings)
                .build();

        ckAES = new EBCommKeys()
                .setEncKey("e134567890123456789012345678901234567890123456789012345678901234")
                .setMacKey("e224262820223456789012345678901234567890123456789012345678901234");

        ckRSA = new EBCommKeys()
                .setEncKey("1234567890123456789012345678901234567890123456789012345678901234")
                .setMacKey("2224262820223456789012345678901234567890123456789012345678901234");

        engine = new EBEngine();
        engine.setDefaultSettings(defaultSettings);
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testInvalidUo() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testInvalidUo ## BEGIN ###");

        try {
            final EBConnectionSettings settings = new EBConnectionSettings()
                    .setMethod(EBCommUtils.METHOD_GET)
                    .setTrust(trust);

            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(0xFFFFL)
                    .setUserObjectType(1)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ckAES)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .build();

            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            assertNotNull(response, "Response is null");
            assertNotNull(response.getRawResponse(), "Raw response is null");
            assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");
            assertEquals(response.getStatusCode(), EBCommStatus.SW_STAT_INVALID_USER_OBJECT_ID, "Invalid UO should be detected");

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(PLAINAES, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testInvalidUo ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAESInvalidMac() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testAESInvalidMac ## BEGIN ###");

        try {
            final EBConnectionSettings settings = new EBConnectionSettings()
                    .setMethod(EBCommUtils.METHOD_GET)
                    .setTrust(trust);

            final EBCommKeys ck = new EBCommKeys()
                    .setEncKey("e134567890123456789012345678901234567890123456789012345678901234")
                    .setMacKey("f224262820223456789012345678901234567890123456789012345678901234");

            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(EBTestingUtils.UOID_AES)
                    .setUserObjectType(UserObjectType.OBJ_PLAINAES)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ck)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .build();

            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            assertNotNull(response, "Response is null");
            assertNotNull(response.getRawResponse(), "Raw response is null");
            assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");
            assertEquals(response.getStatusCode(), EBCommStatus.SW_WRONG_MAC_DATA, "Wrong MAC should be detected");

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(PLAINAES, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testAESInvalidMac ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAESInvalidPadding() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testAESInvalidPadding ## BEGIN ###");

        try {
            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(EBTestingUtils.UOID_AES)
                    .setUserObjectType(UserObjectType.OBJ_PLAINAES)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ckAES)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .build();

            // Test not-padded input data.
            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte("00"));
            assertNotNull(response, "Response is null");
            assertNotNull(response.getRawResponse(), "Raw response is null");
            assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");
            assertEquals(response.getStatusCode(), EBCommStatus.ERROR_CLASS_ERR_CHECK_ERRORS_6f, "Status code does not match");

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(PLAINAES, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testAESInvalidPadding ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAESDefaultSettings() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testAESDefaultSettings ## BEGIN ###");

        try {
            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(EBTestingUtils.UOID_AES)
                    .setUserObjectType(UserObjectType.OBJ_PLAINAES)
                    .setCommKeys(ckAES)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setUo(uo)
                    .setEngine(engine)
                    .build();

            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            testResponse(response);
            assertEquals(response.getProtectedData(), EBUtils.hex2byte("95c6bb9b6a1c3835f98cc56087a03e82"));

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(PLAINAES, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testAESDefaultSettings ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAES() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testAES ## BEGIN ###");

        try {
            final EBConnectionSettings settings = new EBConnectionSettings()
                    .setMethod(EBCommUtils.METHOD_GET)
                    .setTrust(trust);

            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(EBTestingUtils.UOID_AES)
                    .setUserObjectType(1)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ckAES)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .build();

            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            testResponse(response);
            assertEquals(response.getProtectedData(), EBUtils.hex2byte("95c6bb9b6a1c3835f98cc56087a03e82"));

            // Try with POST method.
            settings.setMethod(EBCommUtils.METHOD_POST);
            final EBProcessDataCall call2 = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .build();

            final EBProcessDataResponse response2 = call2.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            testResponse(response2);
            assertEquals(response2.getProtectedData(), EBUtils.hex2byte("95c6bb9b6a1c3835f98cc56087a03e82"));

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(PLAINAES, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testAES ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA1kBasic() throws Exception {
        basicRSATest(EBTestingUtils.UOID_RSA1k, 1024);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA2kBasic() throws Exception {
        basicRSATest(EBTestingUtils.UOID_RSA2k, 2048);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA1kKnownBasic() throws Exception {
        basicRSATest(EBTestingUtils.UOID_RSA1k_KNOWN, 1024);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA2kKnownBasic() throws Exception {
        basicRSATest(EBTestingUtils.UOID_RSA2k_KNOWN, 2048);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA1kKnown() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testRSA1known ## BEGIN ###");

        final BigInteger mod = new BigInteger(EBUtils.hex2byte(EBTestingUtils.RSA1k_MODULUS));
        final PublicKey pub = EBTestingUtils.createRSAPublicKey1k();
        try {
            knownRSATest(EBTestingUtils.UOID_RSA1k_KNOWN, mod, pub);

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(RSA1024, data)", ex);
            assertTrue(false, ex.getMessage());
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testRSA1known ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA2kKnown() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testRSA1known ## BEGIN ###");

        final BigInteger mod = new BigInteger(EBUtils.hex2byte(EBTestingUtils.RSA2k_MODULUS));
        final PublicKey pub = EBTestingUtils.createRSAPublicKey2k();
        try {
            knownRSATest(EBTestingUtils.UOID_RSA2k_KNOWN, mod, pub);

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(RSA1024, data)", ex);
            assertTrue(false, ex.getMessage());
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testRSA1known ## END ###");
    }

    /**
     * Extended remote RSA decryption test when public part of the key is known.
     * Static pattern and random patterns are tested added.
     *
     * @param uoid
     * @param mod
     * @param pub
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     * @throws EBCorruptedException
     */
    private void knownRSATest(long uoid, BigInteger mod, PublicKey pub) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, EBCorruptedException {
        final int bitLength = mod.bitLength();
        final Cipher rsaEnc = Cipher.getInstance("RSA");
        rsaEnc.init(Cipher.ENCRYPT_MODE, pub);

        final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                .setUoid(uoid)
                .setUserObjectType(1)
                .setSettings(defaultSettings)
                .setCommKeys(ckRSA)
                .build();

        final EBProcessDataCall call = new EBProcessDataCall.Builder()
                .setEngine(engine)
                .setUo(uo)
                .build();

        // Test RSA_DEC(knownInput) == knownOutput
        final byte[] output3 = EBUtils.hex2byte("01 23 45 67 89 ff ff ff", true);
        final byte[] input3 = rsaEnc.doFinal(output3);

        final EBProcessDataResponse response3 = call.doRequest(input3);
        testResponse(response3);
        assertEquals(PKCS1Padding.unpad(response3.getProtectedData(), mod.bitLength()), output3);

        // Test with random input, 3 times.
        byte[] output4 = new byte[mod.bitLength()/8-20];
        for(int i=0; i<3; i++){
            // Generate random input
            engine.getRnd().nextBytes(output4);
            // Encrypt it with public key
            final byte[] input4 = rsaEnc.doFinal(output4);

            // Call remote decryption
            final EBProcessDataResponse response4 = call.doRequest(input4);
            testResponse(response4);
            assertEquals(PKCS1Padding.unpad(response4.getProtectedData(), mod.bitLength()), output4);
        }
    }

    /**
     * Performs very simple RSA OU test.
     * @param uoid
     * @throws IOException
     * @throws EBCorruptedException
     */
    private void basicRSATest(long uoid, int bitLength) throws IOException, EBCorruptedException {
        final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                .setUoid(uoid)
                .setUserObjectType(1)
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setCommKeys(ckRSA)
                .build();

        final EBProcessDataCall call = new EBProcessDataCall.Builder()
                .setEngine(engine)
                .setSettings(settings)
                .setUo(uo)
                .build();

        // Test RSA_DEC(1) == 1 as (1^d) mod N = 1
        final String input  = EBUtils.repeat("00", bitLength/8-1) + "01";
        final String output = EBUtils.repeat("00", bitLength/8-1) + "01";
        final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte(input));
        testResponse(response);
        assertEquals(response.getProtectedData(), EBUtils.hex2byte(output));

        // Test RSA_DEC(0) == 0
        final String input2  = EBUtils.repeat("00", bitLength/8);
        final String output2 = EBUtils.repeat("00", bitLength/8);
        final EBProcessDataResponse response2 = call.doRequest(EBUtils.hex2byte(input2));
        testResponse(response2);
        assertEquals(response2.getProtectedData(), EBUtils.hex2byte(output2));

        // Test RSA_DEC(2) != 2
        final String input3  = EBUtils.repeat("00", bitLength/8-1) + "02";
        final String output3 = EBUtils.repeat("00", bitLength/8-1) + "02";
        final EBProcessDataResponse response3 = call.doRequest(EBUtils.hex2byte(input3));
        testResponse(response3);
        assertNotEquals(response3.getProtectedData(), EBUtils.hex2byte(output2));
    }

    /**
     * Basic test on the EB response.
     * @param response
     */
    private void testResponse(EBResponse response){
        assertNotNull(response, "Response is null");
        assertNotNull(response.getRawResponse(), "Raw response is null");
        assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");
        assertTrue(response.isCodeOk(), "Response code is not OK: " + response.getStatusCode());
    }
}
