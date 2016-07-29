package com.enigmabridge;

import com.enigmabridge.misc.KpSizes;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.security.*;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    @Test(groups = {"benchmark"}, enabled = false) //, timeOut = 100000
    public void testCreate() throws Exception {
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "BC");
        final Set<KpSizes> keys = new HashSet<KpSizes>();

        kpGen.initialize(1024);
        for(int i = 0; i < 16000; i++){
            final KeyPair keyPair = kpGen.generateKeyPair();
            final RSAPrivateCrtKeySpec privSpec = kFact.getKeySpec(keyPair.getPrivate(), RSAPrivateCrtKeySpec.class);
            keys.add(new KpSizes(privSpec));
            LOG.debug(String.format("Idx: %02d, size: %02d", i, keys.size()));

            if ((i % 20) == 0){
                dumpKeys(keys);
            }
        }

        dumpKeys(keys);
        LOG.info("DONE");
    }

    private void dumpKeys(Collection<KpSizes> keys){
        for(KpSizes key : keys){
            LOG.debug("Key: " + key + "\n");
        }
    }

}
