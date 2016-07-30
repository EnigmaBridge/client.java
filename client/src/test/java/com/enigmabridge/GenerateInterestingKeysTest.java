package com.enigmabridge;

import com.enigmabridge.misc.KpSizes;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.security.*;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.*;

/**
 * Created by dusanklinec on 29.07.16.
 */
public class GenerateInterestingKeysTest {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateInterestingKeysTest.class);

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

    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"benchmark"}, enabled = true) //, timeOut = 100000
    public void testCreate() throws Exception {
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "BC");
        final Set<KpSizes> keys = new HashSet<KpSizes>();
        final Map<KpSizes, Integer> keysSize = new HashMap<KpSizes, Integer>();

        kpGen.initialize(2048);
        for(int i = 0; i < 1024; i++){
            final KeyPair keyPair = kpGen.generateKeyPair();
            final RSAPrivateCrtKeySpec privSpec = kFact.getKeySpec(keyPair.getPrivate(), RSAPrivateCrtKeySpec.class);
            final KpSizes kpSize = new KpSizes(privSpec);

            final boolean added = keys.add(kpSize);
            if (added){
                keysSize.put(kpSize, 1);
            } else {
                keysSize.put(kpSize, keysSize.get(kpSize) + 1);
            }

            LOG.debug(String.format("Idx: %02d, size: %02d", i, keys.size()));
            if ((i % 20) == 0){
                dumpKeys(keys, keysSize);
            }
        }

        dumpKeys(keys, keysSize);
        LOG.info("DONE");
    }

    private void dumpKeys(Collection<KpSizes> keys, Map<KpSizes, Integer> counts){
        for(KpSizes key : keys){
            key.setCounter(counts.get(key));
            LOG.debug("Key: " + key + "\n");
        }
    }

}
