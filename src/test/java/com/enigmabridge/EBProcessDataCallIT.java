package com.enigmabridge;

import com.enigmabridge.comm.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.security.Security;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test for basic ProcessData() calls.
 * Created by dusanklinec on 03.05.16.
 */
public class EBProcessDataCallIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataCallIT.class);

    // General engine - common
    private final EBEngine engine = new EBEngine();

    // TEST API key
    private final String apiKey = "TEST_API";

    // Testing endpoint
    private EBEndpointInfo endpoint;

    // Trust object for accepting Let's Encrypt certificates
    private EBAdditionalTrust trust;

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
        endpoint = new EBEndpointInfo("https://site2.enigmabridge.com:11180");
        trust = new EBAdditionalTrust(true, true, null);

        ckAES = new EBCommKeys()
                .setEncKey("e134567890123456789012345678901234567890123456789012345678901234")
                .setMacKey("e224262820223456789012345678901234567890123456789012345678901234");

        ckRSA = new EBCommKeys()
                .setEncKey("1234567890123456789012345678901234567890123456789012345678901234")
                .setMacKey("2224262820223456789012345678901234567890123456789012345678901234");
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAES() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testAES ## BEGIN ###");

        try {
            final EBConnectionSettings settings = new EBConnectionSettings()
                    .setMethod(EBCommUtils.METHOD_GET)
                    .setTrust(trust);

            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(0xEE01L)
                    .setUserObjectType(1)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ckAES)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .setProcessFunction(EBRequestTypes.PLAINAES)
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
                    .setProcessFunction(EBRequestTypes.PLAINAES)
                    .build();

            final EBProcessDataResponse response2 = call2.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            testResponse(response2);
            assertEquals(response2.getProtectedData(), EBUtils.hex2byte("95c6bb9b6a1c3835f98cc56087a03e82"));

            // Test not-padded input data.
            final EBProcessDataResponse response3 = call2.doRequest(EBUtils.hex2byte("00"));
            assertNotNull(response3, "Response is null");
            assertNotNull(response3.getRawResponse(), "Raw response is null");
            assertTrue(response3.getRawResponse().isSuccessful(), "HTTP request was not successful");
            assertEquals(response3.getStatusCode(), EBCommStatus.ERROR_CLASS_ERR_CHECK_ERRORS_6f, "Status code does not match");

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(PLAINAES, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testAES ## END ###");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA1k() throws Exception {
        LOG.trace("### UT ## EBProcessDataCallIT::testRSA1k ## BEGIN ###");

        try {
            final EBConnectionSettings settings = new EBConnectionSettings()
                    .setMethod(EBCommUtils.METHOD_POST)
                    .setTrust(trust);

            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(0x7654L)
                    .setUserObjectType(1)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ckRSA)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .setProcessFunction(EBRequestTypes.PLAINAES)
                    .build();

            // Test RSA_DEC(1) == 1 as (1^d) mod N = 1
            final String input  = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001";
            final String output = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001";
            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte(input));
            testResponse(response);
            assertEquals(response.getProtectedData(), EBUtils.hex2byte(output));

            // Test RSA_DEC(0) == 0
            final String input2  = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
            final String output2 = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
            final EBProcessDataResponse response2 = call.doRequest(EBUtils.hex2byte(input2));
            testResponse(response2);
            assertEquals(response2.getProtectedData(), EBUtils.hex2byte(output2));

            // Test RSA_DEC(knownInput) == knownOutput
            final String input3  = "1122334455667788112233445566778811223344556677881122334455667788112233445566778811223344556677881122334455667788112233445566778811223344556677881122334455667788112233445566778811223344556677881122334455667788112233445566778811223344556677881122334455667788";
            final String output3 = "04ba15d03874c5bee4f5a6ef8e90bc422bb2ee347ef119d39368d3277ab5204ca68db721c0ef59906c2fa56986a83bdc0987dd4f7c1e1f46d0e577c9af04d5ab2ca019358e9d17604443b626700bbf7ad5efd052340864c6374800ec6d835fd00de320e40e6b21a2dfe9e3d995f8135d63801b7c7932e18069a8f91080b06ca8";
            final EBProcessDataResponse response3 = call.doRequest(EBUtils.hex2byte(input3));
            testResponse(response3);
            assertEquals(response3.getProtectedData(), EBUtils.hex2byte(output3));

        } catch (Exception ex){
            LOG.error("Exception in ProcessData(RSA1024, data)", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testRSA1k ## END ###");
    }

    /**
     * Basic test on the EB response.
     * @param response
     */
    private void testResponse(EBResponse response){
        assertNotNull(response, "Response is null");
        assertNotNull(response.getRawResponse(), "Raw response is null");
        assertTrue(response.isCodeOk(), "Response code is not OK");
        assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");
    }
}
