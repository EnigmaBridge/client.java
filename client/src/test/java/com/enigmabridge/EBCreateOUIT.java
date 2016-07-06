package com.enigmabridge;

import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.create.*;
import com.enigmabridge.misc.EBTestingUtils;
import com.enigmabridge.provider.EnigmaProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.security.SecureRandom;
import java.security.Security;

/**
 * Created by dusanklinec on 06.07.16.
 */
public class EBCreateOUIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBCreateOUIT.class);

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

    // RSA UOs comm keys
    private EBCommKeys ckRSA;

    public EBCreateOUIT() {

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

        defaultSettings = new EBSettingsBase.Builder()
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setConnectionSettings(settings)
                .build();

        engine.setDefaultSettings(defaultSettings);

        ckRSA = new EBCommKeys()
                .setEncKey("1234567890123456789012345678901234567890123456789012345678901234")
                .setMacKey("2224262820223456789012345678901234567890123456789012345678901234");

        // Do not forget to add BouncyCastle provider.
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAES() throws Exception {
        final SecureRandom rand = new SecureRandom();
        final byte[] appKey = new byte[16];
        final byte[] encKey = new byte[32];
        final byte[] macKey = new byte[32];

        rand.nextBytes(appKey);
        rand.nextBytes(encKey);
        rand.nextBytes(macKey);

        EBUOGetTemplateRequest req = new EBUOGetTemplateRequest();
        req.setType(UserObjectType.TYPE_PLAINAES);
        req.setGenerationCommKey(Constants.GENKEY_CLIENT);
        req.setGenerationAppKey(Constants.GENKEY_CLIENT);

        EBCreateUOSimpleCall callBld = new EBCreateUOSimpleCall.Builder()
                .setEngine(engine)
                .setEndpoint(new EBEndpointInfo(endpoint.getScheme(), endpoint.getHostname(), 11182))
                .setRequest(req)
                .addKey(new EBUOTemplateKey(Constants.KEY_APP, appKey))
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_ENC, encKey))
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_MAC, macKey))
                .build();

        final EBCreateUOResponse response = callBld.create();

        LOG.info("DONE");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA() throws Exception {
        final SecureRandom rand = new SecureRandom();
        final byte[] encKey = new byte[32];
        final byte[] macKey = new byte[32];

        rand.nextBytes(encKey);
        rand.nextBytes(macKey);

        EBUOGetTemplateRequest req = new EBUOGetTemplateRequest();
        req.setType(UserObjectType.TYPE_RSA2048DECRYPT_NOPAD);
        req.setGenerationCommKey(Constants.GENKEY_CLIENT);
        req.setGenerationAppKey(Constants.GENKEY_ENROLL_RANDOM);

        EBCreateUOSimpleCall callBld = new EBCreateUOSimpleCall.Builder()
                .setEngine(engine)
                .setEndpoint(new EBEndpointInfo(endpoint.getScheme(), endpoint.getHostname(), 11182))
                .setRequest(req)
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_ENC, encKey))
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_MAC, macKey))
                .build();

        final EBCreateUOResponse response = callBld.create();

        LOG.info("DONE");
    }
}
