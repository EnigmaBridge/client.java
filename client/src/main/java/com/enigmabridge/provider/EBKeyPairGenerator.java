package com.enigmabridge.provider;


import com.enigmabridge.*;
import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.create.*;
import com.enigmabridge.provider.rsa.EBRSAPrivateKey;
import com.enigmabridge.provider.specs.EBCreateUOTemplateSpec;
import sun.security.rsa.RSAKeyFactory;

import java.io.IOException;
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
    private EBUOGetTemplateRequest getTemplateRequest;

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
                final EBEngine lEngine = ((EBEngineReference) spec).getEBEngine();
                if (lEngine != null){
                    this.engine = lEngine;
                }
            }

            if (spec instanceof EBCreateUOTemplateSpec){
                this.getTemplateRequest = ((EBCreateUOTemplateSpec) spec).getTemplateRequest();
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
        if("RSA".equals(this.algorithm)) {
            if (keySize != 1024 && keySize != 2048){
                throw new InvalidAlgorithmParameterException("RSA1024 and RSA2048 are supported");
            }

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
            final SecureRandom rand = new SecureRandom();
            final byte[] encKey = new byte[32];
            final byte[] macKey = new byte[32];
            rand.nextBytes(encKey);
            rand.nextBytes(macKey);

            final int uoTypeFunction = this.keySize == 1024 ? UserObjectType.TYPE_RSA1024DECRYPT_NOPAD : UserObjectType.TYPE_RSA2048DECRYPT_NOPAD;
            final EBEndpointInfo endpoint = engine.getDefaultSettings().getEndpointInfo();
            EBUOGetTemplateRequest req = this.getTemplateRequest != null ? this.getTemplateRequest : new EBUOGetTemplateRequest();
            req.setType(uoTypeFunction);
            req.setGenerationCommKey(Constants.GENKEY_CLIENT);
            req.setGenerationAppKey(Constants.GENKEY_ENROLL_RANDOM);

            // Force remaining parameters to default values.
            req.setGenerationBillingKey(Constants.GENKEY_LEGACY_ENROLL_RANDOM);
            req.setFormat(1);
            req.setProtocol(1);

            EBCreateUOSimpleCall callBld = new EBCreateUOSimpleCall.Builder()
                    .setEngine(engine)
                    .setEndpoint(new EBEndpointInfo(endpoint.getScheme(), endpoint.getHostname(), 11182))
                    .setRequest(req)
                    .addKey(new EBUOTemplateKey(Constants.KEY_COMM_ENC, encKey))
                    .addKey(new EBUOTemplateKey(Constants.KEY_COMM_MAC, macKey))
                    .build();

            try {
                final EBCreateUOResponse response = callBld.create();

                // Load key public parts.
                final byte[] publicKey = response.getPublicKey();
                final RSAPublicKeySpec pubKeySpec = EBCreateUtils.readSerializedRSAPublicKey(publicKey);
                final KeyFactory rsaFact = KeyFactory.getInstance("RSA");
                final PublicKey rsa2kPubkey = rsaFact.generatePublic(pubKeySpec);


                // Create UOKey
                final UserObjectKeyBase key = new UserObjectKeyBase.Builder()
                        .setUoid(response.getHandle().getUoId())
                        .setUserObjectType(response.getHandle().getUoType().getValue())
                        .setCommKeys(new EBCommKeys(encKey, macKey))
                        .build();

                // Create Java RSA key - will be done with key specs.
                final EBRSAPrivateKey rsa2kPrivKey = new EBRSAPrivateKey.Builder()
                        .setPublicExponent(pubKeySpec.getPublicExponent())
                        .setModulus(pubKeySpec.getModulus())
                        .setUo(key)
                        .setEngine(engine)
                        .build();

                return new KeyPair(rsa2kPubkey, rsa2kPrivKey);

            } catch (IOException e) {
                throw new ProviderException("Create RSA key failed", e);
            } catch (EBCorruptedException e) {
                throw new ProviderException("RSA keys are supported only", e);
            } catch (InvalidKeySpecException e) {
                throw new ProviderException("Cannot create RSA pub key", e);
            } catch (NoSuchAlgorithmException e) {
                throw new ProviderException("Cannot create RSA pub key", e);
            }

        } else {
            throw new ProviderException("RSA keys are supported only");
        }
    }
}

