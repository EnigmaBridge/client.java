package com.enigmabridge.provider;

import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * EB provider helper.
 * Wraps crypto provider calls (e.g., Cipher.getInstance()) preferring EB crypto provider.
 *
 * If no such * exists exception is thrown, it fall backs to BouncyCastle provider.
 *
 * Created by dusanklinec on 18.07.16.
 */
public class EBJcaJceHelper extends ProviderJcaJceHelper {
    protected final ProviderJcaJceHelper bcHelper = new BCJcaJceHelper();

    public static Provider getEBProvider()
    {
        if (Security.getProvider("EB") != null)
        {
            return Security.getProvider("EB");
        }
        else
        {
            return new EnigmaProvider();
        }
    }

    public static Provider getBCProvider()
    {
        if (Security.getProvider("BC") != null)
        {
            return Security.getProvider("BC");
        }
        else
        {
            return new BouncyCastleProvider();
        }
    }

    public EBJcaJceHelper()
    {
        super(getEBProvider());
    }

    @Override
    public Cipher createCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            return super.createCipher(algorithm);
        } catch (Exception e){
            return bcHelper.createCipher(algorithm);
        }
    }

    @Override
    public Mac createMac(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createMac(algorithm);
        } catch (Exception e){
            return bcHelper.createMac(algorithm);
        }
    }

    @Override
    public KeyAgreement createKeyAgreement(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createKeyAgreement(algorithm);
        } catch (Exception e){
            return bcHelper.createKeyAgreement(algorithm);
        }
    }

    @Override
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createAlgorithmParameterGenerator(algorithm);
        } catch (Exception e){
            return bcHelper.createAlgorithmParameterGenerator(algorithm);
        }
    }

    @Override
    public AlgorithmParameters createAlgorithmParameters(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createAlgorithmParameters(algorithm);
        } catch (Exception e){
            return bcHelper.createAlgorithmParameters(algorithm);
        }
    }

    @Override
    public KeyGenerator createKeyGenerator(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createKeyGenerator(algorithm);
        } catch (Exception e){
            return bcHelper.createKeyGenerator(algorithm);
        }
    }

    @Override
    public KeyFactory createKeyFactory(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createKeyFactory(algorithm);
        } catch (Exception e){
            return bcHelper.createKeyFactory(algorithm);
        }
    }

    @Override
    public SecretKeyFactory createSecretKeyFactory(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createSecretKeyFactory(algorithm);
        } catch (Exception e){
            return bcHelper.createSecretKeyFactory(algorithm);
        }
    }

    @Override
    public KeyPairGenerator createKeyPairGenerator(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createKeyPairGenerator(algorithm);
        } catch (Exception e){
            return bcHelper.createKeyPairGenerator(algorithm);
        }
    }

    @Override
    public MessageDigest createDigest(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createDigest(algorithm);
        } catch (Exception e){
            return bcHelper.createDigest(algorithm);
        }
    }

    @Override
    public Signature createSignature(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createSignature(algorithm);
        } catch (Exception e){
            return bcHelper.createSignature(algorithm);
        }
    }

    @Override
    public CertificateFactory createCertificateFactory(String algorithm) throws CertificateException {
        try {
            return super.createCertificateFactory(algorithm);
        } catch (Exception e){
            return bcHelper.createCertificateFactory(algorithm);
        }
    }
}
