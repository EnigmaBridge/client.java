package com.enigmabridge.provider.rsa;

import com.enigmabridge.*;
import com.enigmabridge.create.Constants;
import com.enigmabridge.create.EBCreateUOResponse;
import com.enigmabridge.create.EBCreateUtils;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKey;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKeyWrapper;
import com.enigmabridge.provider.EnigmaProvider;
import com.enigmabridge.provider.specs.EBKeyCreateSpec;
import com.enigmabridge.provider.specs.EBRSAKeyCreateSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

/**
 * KeyFactorySpi - for RSA private keys import to EB.
 * Created by dusanklinec on 13.07.16.
 */
public class KeyFactorySpi
        extends BaseKeyFactorySpi
{
    protected final EnigmaProvider provider;
    protected EBEngine engine;
    protected SecureRandom random;

    protected final UserObjectKeyCreator.Builder keyCreatorBld = new UserObjectKeyCreator.Builder();
    protected UserObjectKeyCreator keyCreator;

    public KeyFactorySpi(EnigmaProvider provider)
    {
        this.provider = provider;
    }

    /**
     * KeySpec extraction is supported only from local types.
     * PublicKey spec is supported for EB types.
     * RSAPrivateKeySpec for EBRSAKey throws InvalidKeySpecException
     *
     * @param key key to process
     * @param spec specs class to produce
     * @return spec
     * @throws InvalidKeySpecException
     */
    protected KeySpec engineGetKeySpec(
            Key key,
            Class spec)
            throws InvalidKeySpecException
    {
        if (spec.isAssignableFrom(RSAPrivateKeySpec.class) && key instanceof EBRSAKey)
        {
            throw new InvalidKeySpecException("EBRSAPrivate key is not extractable");
        }
        else if (spec.isAssignableFrom(RSAPublicKeySpec.class) && key instanceof EBRSAKey)
        {
            final EBRSAKey k = (EBRSAKey) key;

            return new RSAPublicKeySpec(k.getModulus(), k.getPublicExponent());
        }
        else if (spec.isAssignableFrom(RSAPublicKeySpec.class) && key instanceof RSAPublicKey)
        {
            RSAPublicKey k = (RSAPublicKey)key;

            return new RSAPublicKeySpec(k.getModulus(), k.getPublicExponent());
        }
        else if (spec.isAssignableFrom(RSAPrivateKeySpec.class) && key instanceof java.security.interfaces.RSAPrivateKey)
        {
            java.security.interfaces.RSAPrivateKey k = (java.security.interfaces.RSAPrivateKey)key;

            return new RSAPrivateKeySpec(k.getModulus(), k.getPrivateExponent());
        }
        else if (spec.isAssignableFrom(RSAPrivateCrtKeySpec.class) && key instanceof RSAPrivateCrtKey)
        {
            RSAPrivateCrtKey k = (RSAPrivateCrtKey)key;

            return new RSAPrivateCrtKeySpec(
                    k.getModulus(), k.getPublicExponent(),
                    k.getPrivateExponent(),
                    k.getPrimeP(), k.getPrimeQ(),
                    k.getPrimeExponentP(), k.getPrimeExponentQ(),
                    k.getCrtCoefficient());
        }

        return super.engineGetKeySpec(key, spec);
    }

    /**
     * Key translation is not yet implemented.
     *
     * @param key key to translate
     * @return translated key
     * @throws InvalidKeyException
     */
    protected Key engineTranslateKey(
            Key key)
            throws InvalidKeyException
    {
        // For EB key - just change engine.
        try {
            if (key instanceof EBRSAKey)
            {
                final EBRSAKey k = (EBRSAKey) key;

                final UserObjectKeyBase keyBase = new UserObjectKeyBase.Builder()
                        .setUserObjectKeyCopy(k.getUserObjectKey())
                        .build();

                final EBRSAKey.AbstractBuilder kBld = key instanceof EBRSAPublicKey ?
                        new EBRSAPublicKey.Builder() :
                        new EBRSAPrivateKey.Builder();

                return kBld
                        .setModulus(k.getModulus())
                        .setPublicExponent(k.getPublicExponent())
                        .setEngine(provider.getEngine())
                        .setUo(keyBase)
                        .setOperationConfig(k.getOperationConfiguration() == null ? null : k.getOperationConfiguration().copy())
                        .build();
            }

            // For keyspec?
            else if (key instanceof RSAPrivateCrtKey){
                return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper((RSAPrivateCrtKey)key));
            }
            else if (key instanceof RSAPrivateCrtKeySpec){
                return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper((RSAPrivateCrtKeySpec)key));
            }
            else if (key instanceof RSAPrivateKey) {
                return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper((RSAPrivateKey)key));
            }
            else if ("PKCS#8".equalsIgnoreCase(key.getFormat())){
                return generatePrivate(PrivateKeyInfo.getInstance(key.getEncoded()), null);
            }

        } catch (MalformedURLException e) {
            throw new InvalidKeyException("Cannot translate the key", e);
        } catch (IOException e) {
            throw new InvalidKeyException("Cannot translate the key", e);
        } catch (InvalidKeySpecException e) {
            throw new InvalidKeyException("Cannot translate the key", e);
        }

        throw new InvalidKeyException("key type unknown");
    }

    /**
     * Worker method creating RSA private key from the wrapper.
     *
     * @param wrapper RSA private key wrapper
     * @return PrivateKey EB handle
     * @throws InvalidKeySpecException
     */
    protected PrivateKey engineGeneratePrivate(
            EBRSAPrivateCrtKeyWrapper wrapper)
            throws InvalidKeySpecException
    {
        final int uoFunction = UserObjectType.getRSADecryptFunctionFromModulus(wrapper.getModulus());
        keyCreatorBld.setEngine(this.engine)
                .setRandom(this.random)
                .setUoType(new UserObjectType(uoFunction,
                        Constants.GENKEY_CLIENT,
                        Constants.GENKEY_CLIENT
                ));

        keyCreator = keyCreatorBld.build();
        keyCreator
                .setAppKey(wrapper)
                .setUoTypeFunction(uoFunction)
                .setAppKeyGeneration(Constants.GENKEY_CLIENT);
        try {
            final UserObjectKeyBase.Builder keyBld = keyCreator.create();
            final EBCreateUOResponse response = keyCreator.getLastResponse();

            // Load key public parts.
            final RSAPublicKeySpec pubKeySpec = EBCreateUtils.readSerializedRSAPublicKey(response.getPublicKey());

            // Create Java RSA key - will be done with key specs.
            final EBRSAPrivateKey rsa2kPrivKey = new EBRSAPrivateKey.Builder()
                    .setPublicExponent(pubKeySpec.getPublicExponent())
                    .setModulus(pubKeySpec.getModulus())
                    .setUo(keyBld.build())
                    .setEngine(engine)
                    .build();

            return rsa2kPrivKey;

        }  catch (IOException e) {
            throw new ProviderException("Create RSA key failed", e);
        }
    }

    protected void initFromSpec(EBKeyCreateSpec ebSpec)
    {
        if (ebSpec != null && ebSpec.getEBEngine() != null){
            this.engine = ebSpec.getEBEngine();
        }

        if (this.engine == null){
            this.engine = provider.getEngine();
        }

        if (this.random == null && this.engine != null){
            this.random = this.engine.getRnd();
        }

        if (this.random == null){
            this.random = new SecureRandom();
        }

        if (ebSpec != null && ebSpec.getTemplateRequest() != null){
            keyCreatorBld.setGetTemplateRequest(ebSpec.getTemplateRequest());
        }
    }

    protected PrivateKey engineGeneratePrivate(
            KeySpec keySpec)
            throws InvalidKeySpecException
    {
        BigInteger publicExponent = null;

        // For non-crt key public modulus is needed.
        if (keySpec instanceof EBRSAKeyCreateSpec)
        {
            publicExponent = ((EBRSAKeyCreateSpec) keySpec).getPublicExponent();
        }

        // If our specs are passed, initialize from it.
        if (keySpec instanceof EBKeyCreateSpec)
        {
            final EBKeyCreateSpec createSpec = (EBKeyCreateSpec) keySpec;
            initFromSpec(createSpec);

            keySpec = createSpec.getUnderlyingSpec();
        }
        else
        {
            initFromSpec(null);
        }

        if (keySpec instanceof PKCS8EncodedKeySpec)
        {
            try
            {
                return generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)keySpec).getEncoded()), publicExponent);
            }
            catch (Exception e)
            {
                //
                // in case it's just a RSAPrivateKey object... -- openSSL produces these
                //
                try
                {
                    return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper(
                            RSAPrivateKey.getInstance(((PKCS8EncodedKeySpec)keySpec).getEncoded())));
                }
                catch (Exception ex)
                {
                    throw new ExtendedInvalidKeySpecException("unable to process key spec: " + e.toString(), e);
                }
            }
        }
        else if (keySpec instanceof RSAPrivateCrtKeySpec)
        {
            return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper((RSAPrivateCrtKeySpec)keySpec));
        }
        else if (keySpec instanceof RSAPrivateKeySpec)
        {
            if (publicExponent != null)
            {
                return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper(new EBRSAPrivateCrtKey((RSAPrivateKeySpec)keySpec, publicExponent)));
            }
            else
            {
                throw new InvalidKeySpecException("Cannot accept KeySpec type: " + keySpec.getClass().getName() + ", public modulus is missing");
            }
        }

        throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
    }

    protected PublicKey engineGeneratePublic(
            KeySpec keySpec)
            throws InvalidKeySpecException
    {
        // Public key import not yet supported.
        return super.engineGeneratePublic(keySpec);
    }

    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo)
            throws IOException
    {
        return generatePrivate(keyInfo, null);
    }

    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo, BigInteger publicExponent)
            throws IOException
    {
        ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();

        if (org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil.isRsaOid(algOid))
        {
            RSAPrivateKey rsaPrivKey = RSAPrivateKey.getInstance(keyInfo.parsePrivateKey());

            if (rsaPrivKey.getCoefficient().intValue() != 0)
            {
                try
                {
                    return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper(rsaPrivKey));
                }
                catch (InvalidKeySpecException e)
                {
                    throw new IOException("Conversion exception", e);
                }
            }
            else if (rsaPrivKey.getPublicExponent().intValue() != 0 || publicExponent != null)
            {
                try
                {
                    return engineGeneratePrivate(new EBRSAPrivateCrtKeyWrapper(new EBRSAPrivateCrtKey(
                            publicExponent != null ? publicExponent : rsaPrivKey.getPublicExponent(),
                            rsaPrivKey.getPrivateExponent(),
                            rsaPrivKey.getModulus()
                    )));
                }
                catch (InvalidKeySpecException e)
                {
                    throw new IOException("Conversion exception", e);
                }
            }
            else
            {
                throw new IOException("Cannot convert non-CRT private key without public modulus");
            }
        }
        else
        {
            throw new IOException("algorithm identifier " + algOid + " in key not recognised");
        }
    }

    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo)
            throws IOException
    {
        throw new IOException("algorithm identifier " + keyInfo.getAlgorithm() + " in key not recognised");
    }
}

