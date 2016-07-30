package com.enigmabridge.tests;

import com.enigmabridge.*;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.comm.retry.EBRetryStrategySimple;
import com.enigmabridge.create.Constants;
import com.enigmabridge.create.EBUOGetTemplateRequest;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKeyWrapper;
import com.enigmabridge.misc.EBTestingUtilsIT;
import com.enigmabridge.misc.KpSizes;
import com.enigmabridge.provider.EnigmaProvider;
import com.enigmabridge.provider.specs.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Basic tests of Enigma Bridge crypto provider.
 * Created by dusanklinec on 04.05.16.
 * TODO: implement serialized from JSON factory.
 */
public class EBEnigmaProviderIT {
    private static final Logger LOG = LoggerFactory.getLogger(EBEnigmaProviderIT.class);

    // General engine - common
    private final EBEngine engine = new EBEngine();

    // TEST API key
    private final String apiKey = EBTestingUtilsIT.API_KEY;

    // Testing endpoint
    private EBEndpointInfo endpoint;

    // Default settings - POST method
    private EBConnectionSettings settings;

    // EBSettings - defaults.
    private EBSettingsBase defaultSettings;

    // Enigma crypto provider.
    private static EnigmaProvider provider;

    public EBEnigmaProviderIT() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod(alwaysRun = true, groups = {"integration"})
    public void setUpMethod() throws Exception {
        endpoint = new EBEndpointInfo(EBTestingUtilsIT.CONNECTION_STRING);
        settings = new EBConnectionSettings();
        settings.setRetryStrategyNetwork(new EBRetryStrategySimple(3));
        settings.setRetryStrategyApplication(new EBRetryStrategySimple(3));

        defaultSettings = new EBSettingsBase.Builder()
                .setApiKey(apiKey)
                .setEndpointInfo(endpoint)
                .setConnectionSettings(settings)
                .build();

        engine.setDefaultSettings(defaultSettings);

        // Adding Enigma as a security provider.
        provider = new EnigmaProvider(engine);
        Security.addProvider(provider);

        // Do not forget to add BouncyCastle provider.
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterMethod(alwaysRun = true, groups = {"integration"}, enabled = false)
    public void tearDownMethod() throws Exception {
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSA() throws Exception {
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "EB");
        final EBRSAKeyGenParameterSpec keySpec = new EBRSAKeyGenParameterSpec(2048).setTplReq(tplReq);

        kpGen.initialize(keySpec);
        final KeyPair keyPair = kpGen.generateKeyPair();

        // Test
        testRSAKeys(keyPair.getPublic(), keyPair.getPrivate(), null);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSAFactory() throws Exception {
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        // At first, generate local RSA keys.
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048);
        final KeyPair keyPair = kpGen.generateKeyPair();

        // Use EB factory to import existing RSA key to EB.
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "EB");
        final KeyFactory kFactBc = KeyFactory.getInstance("RSA", "BC");

        // At first extract CRT key specs from the locally generated private key
        final RSAPrivateCrtKeySpec keySpec = kFactBc.getKeySpec(keyPair.getPrivate(), RSAPrivateCrtKeySpec.class);

        // Convert specs to the EB stored key.
        final PrivateKey ebPrivate = kFact.generatePrivate(keySpec);

        // test
        testRSAKeys(keyPair.getPublic(), ebPrivate, keyPair);

        // Get key spec - JSON encoded
        final EBJSONEncodedUOKeySpec jsonSpec = kFact.getKeySpec(ebPrivate, EBJSONEncodedUOKeySpec.class);

        // And back
        final PrivateKey keyFromJson = kFact.generatePrivate(jsonSpec);
        testRSAKeys(keyPair.getPublic(), keyFromJson, keyPair);

        // Config encoded
        final EBConfigurationUOKeySpec urlSpec = kFact.getKeySpec(ebPrivate, EBConfigurationUOKeySpec.class);
        LOG.debug("Config: " + urlSpec.getConfigLine());

        // Try to decode config line
        final PrivateKey keyFromUrl = kFact.generatePrivate(urlSpec);
        testRSAKeys(keyPair.getPublic(), keyFromUrl, keyPair);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSAFactoryTranslation() throws Exception {
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        // At first, generate local RSA keys.
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048);
        final KeyPair keyPair = kpGen.generateKeyPair();

        // Use EB factory to import existing RSA key to EB.
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "EB");
        final PrivateKey ebPrivate = (PrivateKey) kFact.translateKey(keyPair.getPrivate());

        // Test.
        testRSAKeys(keyPair.getPublic(), ebPrivate, keyPair);
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testRSAKeyStore() throws Exception {
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        // At first, generate local RSA keys.
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048);
        final KeyPair keyPair = kpGen.generateKeyPair();

        // Use EB factory to import existing RSA key to EB.
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "EB");
        final PrivateKey ebPrivate = (PrivateKey) kFact.translateKey(keyPair.getPrivate());

        // Serialize to keyStore.
        final KeyStore ks = KeyStore.getInstance("BKS", "EB");
        final String ksAlias = "rsa";
        final String ksPassword = "changeit";

        // We need self signed certificate for KS store for PrivateKey.
        final X509Certificate x509Certificate = EBTestingUtilsIT.generateCertificate(keyPair);

        ks.load(null, ksPassword.toCharArray());
        ks.setKeyEntry(ksAlias, ebPrivate, ksPassword.toCharArray(), new Certificate[]{x509Certificate});

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ks.store(bos, ksPassword.toCharArray());
        bos.close();
        final byte[] ksBytes = bos.toByteArray();

        // Open keystore again.
        final KeyStore ks2 = KeyStore.getInstance("BKS", "EB");
        ks2.load(new ByteArrayInputStream(ksBytes), ksPassword.toCharArray());
        final Key ebPrivateNew = ks2.getKey(ksAlias, ksPassword.toCharArray());

        // Get public part from the private
        final RSAPublicKeySpec pubKeySpec = kFact.getKeySpec(ebPrivateNew, RSAPublicKeySpec.class);
        final PublicKey ebNewPubKey = kFact.generatePublic(pubKeySpec);

        testRSAKeys(keyPair.getPublic(), (PrivateKey) ebPrivateNew, keyPair);
        testRSAKeys(ebNewPubKey, (PrivateKey) ebPrivateNew, keyPair);
        testRSAKeys(ebNewPubKey, keyPair.getPrivate(), keyPair);
    }

    @Test(groups = {"integration"}, enabled = true) //, timeOut = 100000
    public void testRSAAtypicalKeys2048() throws Exception {
        final Collection<RSAPrivateCrtKeySpec> testKeys = EBTestingUtilsIT.getTestImportKeys2048();
        testRSAAtypicalKeys(testKeys);
    }

    @Test(groups = {"integration"}, enabled = true) //, timeOut = 100000
    public void testRSAAtypicalKeys1024() throws Exception {
        final Collection<RSAPrivateCrtKeySpec> testKeys = EBTestingUtilsIT.getTestImportKeys1024();
        testRSAAtypicalKeys(testKeys);
    }

    protected void testRSAAtypicalKeys(Collection<RSAPrivateCrtKeySpec> testKeys) throws Exception {
        // Use EB factory to import existing RSA key to EB.
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "EB");
        final KeyFactory kFactBc = KeyFactory.getInstance("RSA", "BC");
        final List<RSAPrivateCrtKeySpec> brokenKeys = new LinkedList<RSAPrivateCrtKeySpec>();
        byte[] keyBuff = new byte[4096*6];

        // Test keys locally - no typos?
        for(RSAPrivateCrtKeySpec keySpec : testKeys){
            // Convert specs to the EB stored key - import happens
            final PrivateKey privKey = kFactBc.generatePrivate(keySpec);
            final PublicKey pubKey = kFactBc.generatePublic(EBTestingUtilsIT.getPubKeySpec(keySpec));
            short len = EBTestingUtilsIT.ExportPrivateKey(keyBuff, (short)0, new EBRSAPrivateCrtKeyWrapper(keySpec));
            LOG.debug(EBUtils.byte2hex(keyBuff, 0, len));

            // test
            testRSAKeys(pubKey, privKey, new KeyPair(pubKey, kFactBc.generatePrivate(keySpec)));
        }

        // Test keys with EB.
        int importErrors = 0;
        int testErrors = 0;
        for(RSAPrivateCrtKeySpec keySpec : testKeys){
            final PublicKey pubKey = kFactBc.generatePublic(EBTestingUtilsIT.getPubKeySpec(keySpec));
            PrivateKey ebPrivate = null;

            // Convert specs to the EB stored key - import happens
            try {
                ebPrivate = kFact.generatePrivate(keySpec);

            } catch(Exception e){
                importErrors+=1;
                brokenKeys.add(keySpec);
                LOG.error("Exception during key import: " + ebPrivate, e);
                continue;
            }

            // test
            try {
                testRSAKeys(pubKey, ebPrivate, new KeyPair(pubKey, kFactBc.generatePrivate(keySpec)));
            }catch(Exception e){
                testErrors += 1;
                brokenKeys.add(keySpec);
                LOG.error("Exception during key test: " + ebPrivate, e);
                continue;
            }
        }

        if (!brokenKeys.isEmpty()){
            for(RSAPrivateCrtKeySpec key : brokenKeys){
                LOG.debug("Broken keys: " + new KpSizes(key));
            }
        }
        
        assertEquals(importErrors, 0, "Some keys were not imported successfully");
        assertEquals(testErrors, 0, "Some keys were imported but not worked properly");
    }

    @Test(groups = {"integration"}, enabled = false) //, timeOut = 100000
    public void testRSAMany() throws Exception {
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        // At first, generate local RSA keys.
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048);
        final KeyPair keyPair = kpGen.generateKeyPair();

        // Use EB factory to import existing RSA key to EB.
        final KeyFactory kFact = KeyFactory.getInstance("RSA", "EB");
        final PrivateKey ebPrivate = (PrivateKey) kFact.translateKey(keyPair.getPrivate());

        // Test.
        for (int i = 0; i < 512; i++) {
            testRSAKeys(keyPair.getPublic(), ebPrivate, keyPair);
        }
    }

    /**
     * Performs simple identity tests DEC(ENC(X)) == X for given key pairs.
     *
     * @param pub public key
     * @param priv private key
     * @param locallyGeneratedKeyPair keypair if generated locally, for logging.
     * @throws Exception exception
     */
    protected void testRSAKeys(PublicKey pub, PrivateKey priv, KeyPair locallyGeneratedKeyPair) throws Exception {
        // Generate random input to encrypt.
        final SecureRandom rand = new SecureRandom();
        final byte[] testInput = new byte[32];

        for(int i=0; i<5; i++) {
            rand.nextBytes(testInput);
            byte[] ciphertext = null;
            byte[] decrypted = null;

            try {
                final Cipher rsaEnc = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                rsaEnc.init(Cipher.ENCRYPT_MODE, pub);
                ciphertext = rsaEnc.doFinal(testInput);

                // Decrypt
                final Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                rsa.init(Cipher.DECRYPT_MODE, priv);
                decrypted = rsa.doFinal(ciphertext);

                // Test
                assertEquals(decrypted, testInput, "RSAdecrypt(RSAencrypt(x)) != x");
            } catch (Exception t){
                LOG.debug(String.format("Exception in RSA operation. " +
                                "testInput[%s], ciphertext[%s], decrypted[%s], " +
                                "pubKey: [%s], privKey: [%s]",
                        EBUtils.byte2hexNullable(testInput),
                        EBUtils.byte2hexNullable(ciphertext),
                        EBUtils.byte2hexNullable(decrypted),
                        pub,
                        priv
                ));

                if (locallyGeneratedKeyPair != null){
                    LOG.debug(String.format("Locally generated keys. Public[%s], Private[%s]",
                            locallyGeneratedKeyPair.getPublic(),
                            locallyGeneratedKeyPair.getPrivate()));
                }
                throw t;
            }
        }
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAES() throws Exception {
        final SecureRandom rand = new SecureRandom();

        // Create AES keys, for encryption & decryption.
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final KeyGenerator kGen = KeyGenerator.getInstance("AES", "EB");
        final EBSymmetricKeyGenParameterSpec keySpec = new EBSymmetricKeyGenParameterSpec(128)
                .setTplReq(tplReq)
                .setKeyType(EBSymmetricKeyGenTypes.BOTH);

        kGen.init(keySpec);
        final SecretKey secretKey = kGen.generateKey();

        // Test
        testAESKey(secretKey);
    }

    protected void testAESKey(Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        final SecureRandom rand = new SecureRandom();

        // Very simple 1 block test.
        final byte[] simpleInput = new byte[16];
        rand.nextBytes(simpleInput);

        // Encrypt.
        final Cipher aesEncSimple = Cipher.getInstance("AES/ECB/NoPadding", "EB");
        aesEncSimple.init(Cipher.ENCRYPT_MODE, key);
        final byte[] ciphertextSimple = aesEncSimple.doFinal(simpleInput);

        // Decrypt
        final Cipher aesDecSimple = Cipher.getInstance("AES/ECB/NoPadding", "EB");
        aesDecSimple.init(Cipher.DECRYPT_MODE, key);
        final byte[] decryptedSimple = aesDecSimple.doFinal(ciphertextSimple);

        // Test
        assertEquals(decryptedSimple, simpleInput, "AESdecrypt(AESencrypt(x)) != x");

        // More complex test - CBC mode, more blocks.
        // Generate random input to encrypt.
        final byte[] testInput = new byte[24];
        final byte[] iv = new byte[16];
        rand.nextBytes(testInput);
        rand.nextBytes(iv);

        // Encrypt.
        final Cipher aesEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING", "EB");
        aesEnc.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        final byte[] ciphertext = aesEnc.doFinal(testInput);

        // Decrypt
        final Cipher aesDec = Cipher.getInstance("AES/CBC/PKCS5PADDING", "EB");
        aesDec.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        final byte[] decrypted = aesDec.doFinal(ciphertext);

        // Test
        assertEquals(decrypted, testInput, "AESdecrypt(AESencrypt(x)) != x");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAESFactory() throws Exception {
        final SecureRandom rand = new SecureRandom();

        // Create AES keys, for encryption & decryption.
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final SecretKeyFactory kFact = SecretKeyFactory.getInstance("AES", "EB");

        final byte[] aesKey = new byte[16];
        rand.nextBytes(aesKey);

        final EBSecretKeySpec spec = new EBSecretKeySpec(aesKey, "AES", engine);
        spec.setKeyType(EBSymmetricKeyGenTypes.BOTH);
        spec.setTplReq(tplReq);

        // Method accepts SecretKeySpec (simple one) or EBSecretKeySpec
        final SecretKey key = kFact.generateSecret(spec);

        // Bouncy castle secret
        final SecretKeySpec bcKey = new SecretKeySpec(aesKey, "AES");

        // Test factory.
        testAESFactoryKeys(key, bcKey);

        // Get key spec - JSON encoded
        final EBJSONEncodedUOKeySpec jsonSpec = (EBJSONEncodedUOKeySpec) kFact.getKeySpec(key, EBJSONEncodedUOKeySpec.class);

        // And back
        final SecretKey keyFromJson = kFact.generateSecret(jsonSpec);
        testAESFactoryKeys(keyFromJson, bcKey);

        // Config encoded
        final EBConfigurationUOKeySpec urlSpec = (EBConfigurationUOKeySpec) kFact.getKeySpec(key, EBConfigurationUOKeySpec.class);

        // Try to decode config line
        final SecretKey keyFromUrl = kFact.generateSecret(urlSpec);
        testAESFactoryKeys(keyFromUrl, bcKey);
    }

    /**
     * Test encryption and decryption of AES encryption keys generated with factory method.
     * One is generated for EB, another one directly with AES key. Operations should be compatible between each other
     * (encryption performed with EB should match decryption performed locally and vice versa).
     *
     * @param ebKey EB key
     * @param bcKey local key
     *
     * @throws NoSuchPaddingException - if the padding scheme is unknown
     * @throws NoSuchAlgorithmException - encryption algorithm is incorrect
     * @throws NoSuchProviderException - crypto provider name is not registered
     * @throws BadPaddingException - if the message padding is incorrect
     * @throws IllegalBlockSizeException - encryption block size is wrong
     * @throws InvalidKeyException - key value is invalid
     * @throws InvalidAlgorithmParameterException - encryption parameters are incorrect
     */
    protected void testAESFactoryKeys(Key ebKey, Key bcKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        final SecureRandom rand = new SecureRandom();

        // Very simple 1 block test.
        final byte[] simpleInput = new byte[16];
        rand.nextBytes(simpleInput);

        // Encrypt.
        final Cipher aesEncSimple = Cipher.getInstance("AES/ECB/NoPadding", "EB");
        aesEncSimple.init(Cipher.ENCRYPT_MODE, ebKey);
        final byte[] ciphertextSimple = aesEncSimple.doFinal(simpleInput);

        // Decrypt
        final Cipher aesDecSimple = Cipher.getInstance("AES/ECB/NoPadding", "BC");
        aesDecSimple.init(Cipher.DECRYPT_MODE, bcKey);
        final byte[] decryptedSimple = aesDecSimple.doFinal(ciphertextSimple);

        // Test
        assertEquals(decryptedSimple, simpleInput, "AESdecrypt(AESencrypt(x)) != x");

        // More complex test - CBC mode, more blocks, inverted operation.
        // Generate random input to encrypt.
        final byte[] testInput = new byte[24];
        final byte[] iv = new byte[16];
        rand.nextBytes(testInput);
        rand.nextBytes(iv);

        // Encrypt.
        final Cipher aesEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
        aesEnc.init(Cipher.ENCRYPT_MODE, bcKey, new IvParameterSpec(iv));
        final byte[] ciphertext = aesEnc.doFinal(testInput);

        // Decrypt
        final Cipher aesDec = Cipher.getInstance("AES/CBC/PKCS5PADDING", "EB");
        aesDec.init(Cipher.DECRYPT_MODE, ebKey, new IvParameterSpec(iv));
        final byte[] decrypted = aesDec.doFinal(ciphertext);

        // Test
        assertEquals(decrypted, testInput, "AESdecrypt(AESencrypt(x)) != x");
    }

    @Test(groups = {"integration"}) //, timeOut = 100000
    public void testAESKeyStore() throws Exception {
        final SecureRandom rand = new SecureRandom();

        // Create AES keys, for encryption & decryption.
        final EBUOGetTemplateRequest tplReq = new EBUOGetTemplateRequest();
        tplReq.setEnvironment(Constants.ENV_DEV);

        final SecretKeyFactory kFact = SecretKeyFactory.getInstance("AES", "EB");

        final byte[] aesKey = new byte[16];
        rand.nextBytes(aesKey);

        final EBSecretKeySpec spec = new EBSecretKeySpec(aesKey, "AES", engine);
        spec.setKeyType(EBSymmetricKeyGenTypes.BOTH);
        spec.setTplReq(tplReq);

        // Method accepts SecretKeySpec (simple one) or EBSecretKeySpec
        final SecretKey key = kFact.generateSecret(spec);

        // Bouncy castle secret
        final SecretKeySpec bcKey = new SecretKeySpec(aesKey, "AES");

        // Test factory.
        testAESFactoryKeys(key, bcKey);

        // Serialize to keyStore.
        final KeyStore ks = KeyStore.getInstance("BKS", "EB");
        final String ksAlias = "aes";
        final String ksPassword = "changeit";

        ks.load(null, ksPassword.toCharArray());
        ks.setKeyEntry(ksAlias, key, ksPassword.toCharArray(), new Certificate[]{});

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ks.store(bos, ksPassword.toCharArray());
        bos.close();
        final byte[] ksBytes = bos.toByteArray();

        // Open keystore again.
        final KeyStore ks2 = KeyStore.getInstance("BKS", "EB");
        ks2.load(new ByteArrayInputStream(ksBytes), ksPassword.toCharArray());
        final Key aesKeyNew = ks2.getKey(ksAlias, ksPassword.toCharArray());

        // Test factory.
        testAESFactoryKeys(aesKeyNew, bcKey);
    }
}
