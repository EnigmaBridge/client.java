package com.enigmabridge.provider;


import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;
import sun.security.rsa.RSAKeyFactory;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;

/**
 * Asymmetric key pair generator.
 * Generates keys in EB.
 *
 * Created by dusanklinec on 27.06.16.
 */
public class EBKeyPairGenerator extends KeyPairGeneratorSpi {
    private final EnigmaProvider provider;
    private final String algorithm;

    private int keySize;
    private AlgorithmParameterSpec params;
    private BigInteger rsaPublicExponent;
    private SecureRandom random;
    private EBEngine engine;

    public EBKeyPairGenerator(EnigmaProvider provider, String algorithm)  {
        this.rsaPublicExponent = RSAKeyGenParameterSpec.F4;
        this.provider = provider;
        this.algorithm = algorithm;
        this.engine = provider.getEngine();
        this.initialize(1024, (SecureRandom)null);
    }

    public void initialize(int keySize, SecureRandom random) {
        try {
            this.checkKeySize(keySize, (AlgorithmParameterSpec)null);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }

        if (random == null){
            random = engine.getRnd();
        }

        this.keySize = keySize;
        this.params = null;
        this.random = random;
    }

    public void initialize(AlgorithmParameterSpec spec, SecureRandom random) throws InvalidAlgorithmParameterException {
        int keySize;
        if(this.algorithm.equals("RSA")) {
            if(!(spec instanceof RSAKeyGenParameterSpec)) {
                throw new InvalidAlgorithmParameterException("RSAKeyGenParameterSpec required for RSA");
            }

            RSAKeyGenParameterSpec rsaSpec = (RSAKeyGenParameterSpec)spec;
            keySize = rsaSpec.getKeysize();
            this.checkKeySize(keySize, rsaSpec);
            this.keySize = keySize;
            this.params = null;
            this.rsaPublicExponent = rsaSpec.getPublicExponent();

            if (spec instanceof EBEngineReference){
                this.engine = ((EBEngineReference) spec).getEBEngine();
            }

        } else {

            throw new ProviderException("Unknown algorithm: " + this.algorithm);
        }

        if (random == null){
            random = engine.getRnd();
        }

        this.random = random;
    }

    private void checkKeySize(int keySize, AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if(this.algorithm.equals("RSA")) {
            BigInteger var3 = this.rsaPublicExponent;
            if(params != null) {
                var3 = ((RSAKeyGenParameterSpec)params).getPublicExponent();
            }

            try {
                RSAKeyFactory.checkKeyLengths(keySize, var3, 512, 65536);
            } catch (InvalidKeyException var5) {
                throw new InvalidAlgorithmParameterException(var5.getMessage());
            }
        } else if(keySize < 512) {
            throw new InvalidAlgorithmParameterException("Key size must be at least 512 bit");
        }
    }

    public KeyPair generateKeyPair() {
        if(this.algorithm.equals("RSA")) {
            // TODO: generate.

        } else {

            throw new ProviderException("RSA keys are supported only");
        }

        return null;
    }
}

