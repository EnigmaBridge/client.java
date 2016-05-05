package com.enigmabridge;

import com.enigmabridge.comm.EBCommUtils;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.comm.EBProcessDataResponse;
import com.enigmabridge.misc.EBTestingUtils;
import com.enigmabridge.provider.EnigmaProvider;
import com.enigmabridge.provider.rsa.EBRSAPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.PublicKey;
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

    // RSA UOs comm keys
    private EBCommKeys ckRSA;

    // Enigma crypto provider.
    private static EnigmaProvider provider;

    /**
     * EB JCA/JCE provider
     */
    private EnigmaProvider ebProvider;

    public EBEnigmaProviderIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Adding Enigma as a security provider.
        provider = new EnigmaProvider();
        Security.addProvider(provider);

        // Do not forget to add BouncyCastle provider.
        Security.addProvider(new BouncyCastleProvider());

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
        endpoint = new EBEndpointInfo(EBTestingUtils.CONNECTION_STRING);

        settings = new EBConnectionSettings()
                .setMethod(EBCommUtils.METHOD_POST);

        defaultSettings = new EBSettingsBase.Builder()
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setConnectionSettings(settings)
                .build();

        ckRSA = new EBCommKeys()
                .setEncKey("1234567890123456789012345678901234567890123456789012345678901234")
                .setMacKey("2224262820223456789012345678901234567890123456789012345678901234");
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testCall() throws Exception {
        // Load key public parts.
        final BigInteger exp = BigInteger.valueOf(EBTestingUtils.RSA2k_PUB_EXP);
        final BigInteger mod = new BigInteger(EBUtils.hex2byte(EBTestingUtils.RSA2k_MODULUS));
        final PublicKey rsa2kPubkey = EBTestingUtils.createRSAPublicKey2k();
        final int bitLength = mod.bitLength();

        // Create UOKey
        final UserObjectKeyBase key = new UserObjectKeyBase.Builder()
                .setSettings(defaultSettings)
                .setUoid(EBTestingUtils.UOID_RSA2k_KNOWN)
                .setUserObjectType(0x1)
                .setCommKeys(ckRSA)
                .setAlgorithm("RSA")
                .setKeyLength(mod.bitLength())
                .setKeyType(UserObjectKeyType.PRIVATE)
                .build();

        LOG.debug("UO: " + key.toJSON(null).toString());

        // Create Java RSA key - will be done with key specs.
        final EBRSAPrivateKey rsa2kPrivKey = new EBRSAPrivateKey.Builder()
                .setPublicExponent(exp)
                .setModulus(mod)
                .setUo(key)
                .setEngine(engine)
                .build();

        final Cipher rsa = Cipher.getInstance("RSA", provider);
        rsa.init(Cipher.DECRYPT_MODE, rsa2kPrivKey);

        // Encrypt test vector
        // Test RSA_DEC(1) == 1 as (1^d) mod N = 1
        final String input  = "01";
        final byte[] bytes = rsa.doFinal(EBUtils.hex2byte(input));

        LOG.info("DONE");

    }
}
