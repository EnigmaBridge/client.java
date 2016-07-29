package com.enigmabridge.provider;

import com.enigmabridge.EBEngine;
import com.enigmabridge.provider.aes.AES;
import com.enigmabridge.provider.keystore.BC;
import com.enigmabridge.provider.keystore.PKCS12;
import com.enigmabridge.provider.rsa.RSA;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Testing implementation.
 * http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/HowToImplAProvider.html#Step6
 * http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html#KeySpecs
 *
 * Provider can be configurable with config file / EB config.
 * This approach is used in PKCS11 Provider - config file is passed in the constructor.
 * http://javaandcryptosmartcards.blogspot.cz/2009/05/java-and-cryptographic-smartcards-this.html
 *
 * Created by dusanklinec on 21.04.16.
 */
public class EnigmaProvider extends Provider implements ConfigurableProvider {

    public static final String PROVIDER_NAME = "EB";
    public static final String PROVIDER_DESC = "JCA/JCE provider for " + PROVIDER_NAME;
    public static final double VERSION = 0.1;

    // TODO: store EBEndpoint, ApiKey, Connection Settings and general info to the provider configuration.
    public static final ProviderConfiguration CONFIGURATION = new EBProviderConfigurationCrypto();

    private static final Map keyInfoConverters = new HashMap();

    private EBEngine engine;
    private EBProviderConfiguration config;

    public EnigmaProvider(EBEngine engine) {
        super(PROVIDER_NAME, VERSION, PROVIDER_DESC);
        config = EBProviderConfiguration.getInstance(engine);
        initProvider(engine);
    }

    public EnigmaProvider() {
        super(PROVIDER_NAME, VERSION, PROVIDER_DESC);
        config = EBProviderConfiguration.getInstance();
        initProvider(null);
    }

    public EnigmaProvider(String configFile) throws IOException {
        super(PROVIDER_NAME, VERSION, PROVIDER_DESC);
        config = EBProviderConfiguration.getInstance(configFile);
        initProvider(null);
    }

    public EnigmaProvider(InputStream configStream) throws IOException {
        super(PROVIDER_NAME, VERSION, PROVIDER_DESC);
        config = EBProviderConfiguration.getInstance(configStream);
        initProvider(null);
    }

    private void initProvider(EBEngine engine){
        if (engine == null){
            engine = new EBEngine();
        }

        this.engine = engine;
        AccessController.doPrivileged(new PrivilegedAction()
        {
            public Object run()
            {
                setup();
                return null;
            }
        });
    }

    private void setup()
    {
        // Register BC provider if not already registered.
        Security.addProvider(new BouncyCastleProvider());

        // Register our RSA engine.
        new RSA.Mappings().configure(this);

        // RSA keygen
        addAlgorithm("KeyPairGenerator.RSA", "com.enigmabridge.provider.EBKeyPairGenerator", true);
        addAlgorithm("Alg.Alias.KeyPairGenerator." + PKCSObjectIdentifiers.rsaEncryption, "RSA", true);
        addAlgorithm("Alg.Alias.KeyPairGenerator." + X509ObjectIdentifiers.id_ea_rsa, "RSA", true);
        addAlgorithm("Alg.Alias.KeyPairGenerator." + PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA", true);
        addAlgorithm("Alg.Alias.KeyPairGenerator." + PKCSObjectIdentifiers.id_RSASSA_PSS, "RSA", true);

        // AES
        new AES.Mappings().configure(this);

        // KeyStore
        new BC.Mappings().configure(this);
        new PKCS12.Mappings().configure(this);
    }

    /**
     * We have our own ServiceDescription implementation that overrides newInstance()
     */
    private static class MyService extends Service {
        private static final Class[] paramTypes = {EnigmaProvider.class};
        private static final Class[] paramTypesAlg = {EnigmaProvider.class, String.class};
        private final boolean withAlg;

        MyService(Provider provider, String type, String algorithm, String className) {
            super(provider, type, algorithm, className, null, null);
            this.withAlg = false;
        }

        MyService(Provider provider, String type, String algorithm, String className, boolean withAlg) {
            super(provider, type, algorithm, className, null, null);
            this.withAlg = withAlg;
        }

        public Object newInstance(Object param) throws NoSuchAlgorithmException {
            try {
                // get the Class object for the implementation class
                Class clazz;

                Provider provider = getProvider();
                ClassLoader loader = provider.getClass().getClassLoader();
                final String reqType = this.getType();
                if (loader == null) {
                    clazz = Class.forName(getClassName());
                } else {
                    clazz = loader.loadClass(getClassName());
                }

                if (withAlg){
                    // fetch the (Provider, String) constructor
                    Constructor cons = clazz.getConstructor(paramTypesAlg);
                    // invoke constructor and return the SPI object
                    Object obj = cons.newInstance(new Object[]{provider, getAlgorithm()});
                    return obj;

                } else {
                    // fetch the (Provider, String) constructor
                    Constructor cons = clazz.getConstructor(paramTypes);
                    // invoke constructor and return the SPI object
                    Object obj = cons.newInstance(new Object[]{provider});
                    return obj;
                }
            } catch (Exception e) {
                throw new NoSuchAlgorithmException("Could not instantiate service", e);
            }
        }
    }

    /**
     * Custom ServiceDescription class for Cipher objects. See supportsParameter() below
     */
    private static class MyCipherService extends MyService {
        MyCipherService(Provider provider, String type, String algorithm, String className) {
            super(provider, type, algorithm, className);
        }
        MyCipherService(Provider provider, String type, String algorithm, String className, boolean withAlg) {
            super(provider, type, algorithm, className, withAlg);
        }

        // we override supportsParameter() to let the framework know which
        // keys we can support. We support instances of MySecretKey, if they
        // are stored in our provider backend, plus SecretKeys with a RAW encoding.
        public boolean supportsParameter(Object obj) {
            if (!(obj instanceof EBUOKey)) {
                return false;
            }

            EBUOKey key = (EBUOKey)obj;
            if (!key.getAlgorithm().equals(getAlgorithm())) {
                return false;
            }

//            if (key instanceof EnigmaProviderOld.MySecretKey) {
//                EnigmaProviderOld.MySecretKey myKey = (EnigmaProviderOld.MySecretKey)key;
//                return myKey.provider == getProvider();
//            } else {
//                return "RAW".equals(key.getFormat());
//            }

            return true;
        }
    }

    public void setParameter(String parameterName, Object parameter)
    {
        synchronized (CONFIGURATION)
        {
            ((EBProviderConfigurationCrypto)CONFIGURATION).setParameter(parameterName, parameter);
        }
    }

    public boolean hasAlgorithm(String type, String name)
    {
        return containsKey(type + "." + name) || containsKey("Alg.Alias." + type + "." + name);
    }

    public void addAlgorithm(String key, String value){
        addAlgorithm(key, value, false);
    }

    public void addAlgorithm(String key, String value, boolean withAlg)
    {
        if (containsKey(key))
        {
            throw new IllegalStateException("duplicate provider key (" + key + ") found");
        }

        put(key, value);

        final String keyShort = key.replace("Alg.Alias.", "");
        final int dotIdx = keyShort.indexOf(".");
        if (dotIdx != -1){
            final String type = keyShort.substring(0, dotIdx);
            final String name = keyShort.substring(dotIdx+1);
            putService(new MyCipherService(this, type, name, value, withAlg));
        }
    }

    public void addAlgorithm(String type, ASN1ObjectIdentifier oid, String className)
    {
        addAlgorithm(type + "." + oid, className);
        addAlgorithm(type + ".OID." + oid, className);
    }

    public void addKeyInfoConverter(ASN1ObjectIdentifier oid, AsymmetricKeyInfoConverter keyInfoConverter)
    {
        keyInfoConverters.put(oid, keyInfoConverter);
    }

    public EBEngine getEngine() {
        return engine;
    }

    /**
     * Returns enigma provider from the JCA/JCE.
     * New one is created if not found in JCA/JCE.
     *
     * @return EnigmaProvider
     */
    public static EnigmaProvider getEnigmaProvider() {
        if (Security.getProvider(PROVIDER_NAME) != null)
        {
            return (EnigmaProvider) Security.getProvider(PROVIDER_NAME);
        }
        else
        {
            final EnigmaProvider enigmaProvider = new EnigmaProvider();
            Security.addProvider(enigmaProvider);

            return enigmaProvider;
        }
    }

    public JSONObject configureToJSON() {
        return engine.configureToJSON();
    }

    public String configureToURL() throws MalformedURLException {
        return engine.configureToURL();
    }

    public void configureFromJSON(String str) throws MalformedURLException {
        engine.configureFromJSON(str);
    }

    public void configureFromJSON(JSONObject obj) throws MalformedURLException {
        engine.configureFromJSON(obj);
    }

    public void configureFromURL(String url) throws MalformedURLException, UnsupportedEncodingException {
        engine.configureFromURL(url);
    }
}
