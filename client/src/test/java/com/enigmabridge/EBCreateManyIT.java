package com.enigmabridge;

import com.enigmabridge.comm.EBCommStatus;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.create.*;
import com.enigmabridge.misc.EBTestingUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.RSAPublicKeySpec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dusanklinec on 15.07.16.
 */
public class EBCreateManyIT {
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

    private SecureRandom rand;

    private String lastHandle;
    private final AtomicInteger createdUOCalls = new AtomicInteger(0);
    private final AtomicInteger createdUOSuccess = new AtomicInteger(0);

    public EBCreateManyIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
        rand = new SecureRandom();
        endpoint = new EBEndpointInfo(EBTestingUtils.CONNECTION_STRING);
        settings = new EBConnectionSettings();

        defaultSettings = new EBSettingsBase.Builder()
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setConnectionSettings(settings)
                .build();

        engine.setDefaultSettings(defaultSettings);

        // Do not forget to add BouncyCastle provider.
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"benchmark"}) //, timeOut = 100000
    public void testCreate() throws Exception {
        createdUOCalls.set(0);
        createdUOSuccess.set(0);

        final int threads = 10;
        final int objectsToCreate = 5 * 1000;

        final ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for(int i = 0; i < threads; i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while(createdUOCalls.incrementAndGet() < objectsToCreate){
                        createObject();
                        if ((createdUOCalls.intValue() % 10) == 0){
                            LOG.debug(String.format("Created %d, success %d, handle: %s",
                                    createdUOCalls.intValue(), createdUOSuccess.intValue(), lastHandle));
                        }
                    }
                }
            });
        }

        while(createdUOCalls.get() < objectsToCreate){
            Thread.sleep(50);
        }

        LOG.info("DONE");
    }

    protected void createObject(){
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

        try {
            final EBCreateUOResponse response = callBld.create();
            if (response.isCodeOk()) {
                createdUOSuccess.incrementAndGet();
                lastHandle = response.getHandle().toString();
            } else if (response.getStatusCode() == EBCommStatus.ERROR_CLASS_ERR_CHECK_ERRORS_6f) {
                LOG.debug("6f00: " + callBld.getCreateRequest());
            }
        } catch(Exception e){
            //LOG.error("Create failed: " + e.getMessage(), e);
        }
    }
}
