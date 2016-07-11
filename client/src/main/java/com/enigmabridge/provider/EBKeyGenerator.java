package com.enigmabridge.provider;

import com.enigmabridge.*;
import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.create.*;
import com.enigmabridge.provider.aes.EBAESKey;
import com.enigmabridge.provider.rsa.EBRSAPrivateKey;
import com.enigmabridge.provider.specs.EBAESKeyGenParameterSpec;
import com.enigmabridge.provider.specs.EBCreateUOTemplateSpec;
import com.enigmabridge.provider.specs.EBSymmetricKeyGenTypes;

import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Symmetric key generator. Key is stored in the EB, non-extractable.
 *
 * Created by dusanklinec on 06.07.16.
 */
public class EBKeyGenerator extends KeyGeneratorSpi {
    private final EnigmaProvider provider;
    private SecureRandom random;
    private EBEngine engine;

    private final String algorithm;
    private int keySize = 128;
    private EBUOGetTemplateRequest getTemplateRequest;
    private EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    public EBKeyGenerator(EnigmaProvider provider, String algorithm)  {
        if (!"AES".equalsIgnoreCase(algorithm)){
            throw new ProviderException("Only AES is supported for now");
        }

        this.provider = provider;
        this.algorithm = algorithm;
        this.engine = provider.getEngine();
        this.setDefaultKeySize();
    }

    private void setDefaultKeySize() {
        this.keySize = 128;
    }

    protected void engineInit(SecureRandom rand) {
        this.setDefaultKeySize();
        if (rand != null) {
            this.random = rand;
        }
    }

    protected void engineInit(AlgorithmParameterSpec spec, SecureRandom rand) throws InvalidAlgorithmParameterException {
        if (rand != null){
            this.random = rand;
        }

        if (this.random == null){
            this.random = engine.getRnd();
        }

        if (this.random == null){
            this.random = new SecureRandom();
        }

        if (spec instanceof EBAESKeyGenParameterSpec){
            this.keySize = ((EBAESKeyGenParameterSpec) spec).getKeySize();
            this.keyType = ((EBAESKeyGenParameterSpec) spec).getKeyType();

        } else {
            throw new InvalidAlgorithmParameterException("AlgorithmParameterSpec not supported");
        }

        if (spec instanceof EBEngineReference){
            final EBEngine lEngine = ((EBEngineReference) spec).getEBEngine();
            if (lEngine != null){
                this.engine = lEngine;
            }
        }

        if (spec instanceof EBCreateUOTemplateSpec){
            this.getTemplateRequest = ((EBCreateUOTemplateSpec) spec).getTemplateRequest();
        }
    }

    protected void engineInit(int keySize, SecureRandom random) {
        try {
            this.checkKeySize(keySize, (AlgorithmParameterSpec)null);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }

        if (random != null){
            this.random = random;
        }

        if (this.random == null){
            this.random = engine.getRnd();
        }

        if (this.random == null){
            this.random = new SecureRandom();
        }

        this.keySize = keySize;
        this.random = random;
    }

    private void checkKeySize(int keySize, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException{
        if ("AES".equalsIgnoreCase(this.algorithm) && keySize != 128){
            throw new InvalidAlgorithmParameterException("AES supports only 128bit key");
        }
    }

    /**
     * Generates AES key in the EB with randomly generated comm keys.
     * appKeySeed is used to derive main AES app key. This protects application as only EB knows actual key.
     *
     * @param forEncryption
     * @param appKeySeed
     * @return
     */
    protected EBAESKey.Builder engineGenerateAESKey(boolean forEncryption, byte[] appKeySeed){
        final byte[] encKey = new byte[32];
        final byte[] macKey = new byte[32];
        random.nextBytes(encKey);
        random.nextBytes(macKey);

        return engineGenerateAESKey(forEncryption, encKey, macKey, appKeySeed);
    }

    /**
     * Generates AES key in the EB.
     * appKeySeed is used to derive main AES app key. This protects application as only EB knows actual key.
     *
     * @param forEncryption
     * @param encKey
     * @param macKey
     * @param appKeySeed
     * @return
     */
    protected EBAESKey.Builder engineGenerateAESKey(boolean forEncryption, byte[] encKey, byte[] macKey, byte[] appKeySeed){
        // Create decrypt.
        final EBEndpointInfo endpoint = engine.getDefaultSettings().getEndpointInfo();
        final int uoTypeFunction = forEncryption ? UserObjectType.TYPE_PLAINAES : UserObjectType.TYPE_PLAINAESDECRYPT;

        EBUOGetTemplateRequest req = this.getTemplateRequest != null ? this.getTemplateRequest : new EBUOGetTemplateRequest();
        req.setType(uoTypeFunction);
        req.setGenerationCommKey(Constants.GENKEY_CLIENT);
        req.setGenerationAppKey(Constants.GENKEY_ENROLL_DERIVED);

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

            // Create UOKey
            final UserObjectKeyBase key = new UserObjectKeyBase.Builder()
                    .setUoid(response.getHandle().getUoId())
                    .setUserObjectType(response.getHandle().getUoType().getValue())
                    .setCommKeys(new EBCommKeys(encKey, macKey))
                    .build();

            final EBAESKey.Builder bld = new EBAESKey.Builder()
                    .setUo(key)
                    .setEngine(engine);

            return bld;

        } catch (IOException e) {
            throw new ProviderException("Create AES key failed", e);
        } catch (EBCorruptedException e) {
            throw new ProviderException("AES keys are supported only", e);
        }
    }

    protected SecretKey engineGenerateKey() {
        if ("AES".equalsIgnoreCase(algorithm)){
            // AES key will be derived using this seed.
            final byte[] appKeySeed = new byte[16];
            random.nextBytes(appKeySeed);

            EBAESKey.Builder decKey = null;
            EBAESKey.Builder encKey = null;

            // Create decrypt.
            if (keyType == EBSymmetricKeyGenTypes.BOTH || keyType == EBSymmetricKeyGenTypes.DECRYPT){
                decKey = engineGenerateAESKey(false, appKeySeed);
            }

            // Create encrypt.
            if (keyType == EBSymmetricKeyGenTypes.BOTH || keyType == EBSymmetricKeyGenTypes.ENCRYPT){
                encKey = engineGenerateAESKey(false, appKeySeed);
            }

            // If pair was generated - chain them.
            if (keyType == EBSymmetricKeyGenTypes.BOTH){
                decKey.setInversionKey(encKey);
                final EBAESKey decKeyFinal = decKey.build();

                encKey.setInversionKey(decKeyFinal);
                return decKeyFinal;
            } else if (keyType == EBSymmetricKeyGenTypes.DECRYPT){
                return decKey.build();
            } else {
                return encKey.build();
            }

        } else {
            throw new ProviderException("AES algorithm supported only.");
        }
    }
}
