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
import java.security.*;

import static org.testng.Assert.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by dusanklinec on 27.07.16.
 */
public class EBProcessDataMassIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataMassIT.class);

    // General engine - common
    private EBEngine engine;

    // TEST API key
    private final String apiKey = EBTestingUtils.API_KEY;

    // Testing endpoint
    private EBEndpointInfo endpoint;

    // Default settings - POST method
    private EBConnectionSettings settings;

    // EBSettings - defaults.
    private EBSettingsBase defaultSettings;

    // AES UOs comm keys
    private EBCommKeys ckAES;

    // RSA UOs comm keys
    private EBCommKeys ckRSA;

    public EBProcessDataMassIT() {

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

        settings = new EBConnectionSettings();

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
    public void testAESManyTimes() throws Exception {
        LOG.trace("### UT ## EBProcessDataMassIT::testAESManyTimes ## BEGIN ###");

        final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                .setUoid(EBTestingUtils.UOID_AES)
                .setUserObjectType(UserObjectType.OBJ_PLAINAES)
                .setCommKeys(ckAES)
                .build();

        // Independent test
        for(int testIdx = 0; testIdx < 100; testIdx++) {
            // Generate random input.
            final int length = 16 * (1 + engine.getRnd().nextInt(16));
            byte[] plaintext = new byte[length];
            engine.getRnd().nextBytes(plaintext);

            // Repeat same plaintext several times to test hypothesis it is independent on input data.
            for(int repIdx=0; repIdx < 10; repIdx++){
                final EBProcessDataCall call = new EBProcessDataCall.Builder()
                        .setUo(uo)
                        .setEngine(engine)
                        .build();

                EBProcessDataResponse response = null;

                boolean logFail = false;
                Exception exception = null;

                try {
                    response = call.doRequest(plaintext);
                } catch(Exception e){
                    exception = e;
                    logFail = true;
                }

                logFail |= response == null
                        || !response.isCodeOk()
                        || response.getProtectedData() == null
                        || response.getProtectedData().length == 0;

                if (logFail){
                    LOG.debug(String.format("Anomaly, test=%d, rep=%d,\n" +
                                    "  length=%04d B, input=%s\n" +
                                    "  raw request:  [%s]\n" +
                                    "  raw response: [%s]",
                            testIdx, repIdx,
                            length, EBUtils.byte2hex(plaintext),
                            call.getRawRequest(),
                            call.getRawResponse()
                            ), exception);
                }
            }
        }

        LOG.trace("### UT ## EBProcessDataMassIT::testAESManyTimes ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA2kBasic() throws Exception {
        basicRSATest(EBTestingUtils.UOID_RSA2k, 2048);
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
        assertTrue(response.isCodeOk(), "Response code is not OK");
    }
}
