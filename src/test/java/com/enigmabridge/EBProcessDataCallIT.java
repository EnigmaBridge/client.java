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

    private final EBEngine engine = new EBEngine();
    private final String apiKey = "TEST_API";
    private EBEndpointInfo endpoint;
    private EBAdditionalTrust trust;

    public EBProcessDataCallIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
        endpoint = new EBEndpointInfo("https://site2.enigmabridge.com:11180");
        trust = new EBAdditionalTrust(true, true, null);
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

            final EBCommKeys ck = new EBCommKeys()
                    .setEncKey("e134567890123456789012345678901234567890123456789012345678901234")
                    .setMacKey("e224262820223456789012345678901234567890123456789012345678901234");

            final UserObjectInfoBase uo = new UserObjectInfoBase.Builder()
                    .setUoid(0xEE01L)
                    .setUserObjectType(1)
                    .setApiKey(apiKey)
                    .setEndpointInfo(endpoint)
                    .setCommKeys(ck)
                    .build();

            final EBProcessDataCall call = new EBProcessDataCall.Builder()
                    .setEngine(engine)
                    .setSettings(settings)
                    .setUo(uo)
                    .setProcessFunction(EBRequestTypes.PLAINAES)
                    .build();

            final EBProcessDataResponse response = call.doRequest(EBUtils.hex2byte("6bc1bee22e409f96e93d7e117393172a"));
            assertNotNull(response, "Response is null");
            assertNotNull(response.getRawResponse(), "Raw response is null");
            assertTrue(response.isCodeOk(), "Response code is not OK");
            assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");
            assertEquals(response.getProtectedData(), EBUtils.hex2byte("95c6bb9b6a1c3835f98cc56087a03e82"));

        } catch (Exception ex){
            LOG.error("Exception in get import keys call", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBProcessDataCallIT::testAES ## END ###");
    }
}
