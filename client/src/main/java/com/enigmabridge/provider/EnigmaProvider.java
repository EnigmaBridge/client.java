package com.enigmabridge.provider;

import com.enigmabridge.EBEngine;
import com.enigmabridge.provider.rsa.RSA;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
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
    public static final ProviderConfiguration CONFIGURATION = new EBProviderConfiguration();

    private static final Map keyInfoConverters = new HashMap();

    private EBEngine engine;

    public EnigmaProvider(EBEngine engine) {
        super(PROVIDER_NAME, VERSION, PROVIDER_DESC);
        initProvider(engine);
    }

    public EnigmaProvider() {
        super(PROVIDER_NAME, VERSION, PROVIDER_DESC);
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
        // Register our RSA engine.
        new RSA.Mappings().configure(this);
    }

    /**
     * We have our own ServiceDescription implementation that overrides newInstance()
     */
    private static class MyService extends Service {
        //private static final Class[] paramTypes = {Provider.class, String.class};
        private static final Class[] paramTypes = {};

        MyService(Provider provider, String type, String algorithm, String className) {
            super(provider, type, algorithm, className, null, null);
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
                // fetch the (Provider, String) constructor
                Constructor cons = clazz.getConstructor(paramTypes);
                // invoke constructor and return the SPI object
                //Object obj = cons.newInstance(new Object[] {provider, getAlgorithm()});
                Object obj = cons.newInstance();
                return obj;
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
            ((EBProviderConfiguration)CONFIGURATION).setParameter(parameterName, parameter);
        }
    }

    public boolean hasAlgorithm(String type, String name)
    {
        return containsKey(type + "." + name) || containsKey("Alg.Alias." + type + "." + name);
    }

    public void addAlgorithm(String key, String value)
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
            putService(new MyCipherService(this, type, name, value));
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
}
