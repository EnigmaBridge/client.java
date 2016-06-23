package com.enigmabridge;

import com.enigmabridge.comm.*;
import com.enigmabridge.misc.EBTestingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Simple test to obtain import public keys from the EB.
 * Created by dusanklinec on 02.05.16.
 */
public class EBGetPubKeyCallIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBGetPubKeyCallIT.class);

    public EBGetPubKeyCallIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testCall() throws Exception {
        LOG.trace("### UT ## EBGetPubKeyCallIT::testCall ## BEGIN ###");

        try {
            final EBEngine engine = new EBEngine();
            final EBEndpointInfo endpoint = new EBEndpointInfo(EBTestingUtils.CONNECTION_STRING);
            final String apiKey = EBTestingUtils.API_KEY;

            final EBConnectionSettings settings = new EBConnectionSettings()
                    .setMethod(EBCommUtils.METHOD_GET);

            final EBGetPubKeyCall call = new EBGetPubKeyCall.Builder()
                   .setApiKey(apiKey)
                   .setEndpoint(endpoint)
                   .setEngine(engine)
                   .setSettings(settings)
                   .build();

            final EBGetPubKeyResponse response = call.doRequest();
            assertNotNull(response, "Response is null");
            assertNotNull(response.getRawResponse(), "Raw response is null");
            assertTrue(response.isCodeOk(), "Response code is not OK");
            assertTrue(response.getRawResponse().isSuccessful(), "HTTP request was not successful");

            final List<EBImportPubKey> keys = response.getImportKeys();
            assertNotNull(keys, "Keys are null");
            assertTrue(!keys.isEmpty(), "Key list is empty");
            for(EBImportPubKey key : keys){
                LOG.trace(key.toString());
            }

        } catch (Exception ex){
            LOG.error("Exception in get import keys call", ex);
            assertTrue(false);
        }

        LOG.trace("### UT ## EBGetPubKeyCallIT::testCall ## END ###");
    }
}

