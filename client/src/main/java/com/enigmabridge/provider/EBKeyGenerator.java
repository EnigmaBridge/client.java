package com.enigmabridge.provider;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBEngineReference;
import com.enigmabridge.UserObjectType;
import com.enigmabridge.create.Constants;
import com.enigmabridge.create.EBUOGetTemplateRequest;
import com.enigmabridge.provider.specs.EBCreateUOTemplateSpec;
import com.enigmabridge.provider.specs.EBSymmetricKeyGenParameterSpec;
import com.enigmabridge.provider.specs.EBSymmetricKeyGenTypes;

import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Symmetric key generator. Key is stored in the EB, non-extractable.
 *
 * Created by dusanklinec on 06.07.16.
 */
public class EBKeyGenerator extends KeyGeneratorSpi {
    protected final EnigmaProvider provider;
    protected SecureRandom random;
    protected EBEngine engine;

    protected final String algorithm;
    protected int keySize = 128;
    protected EBUOGetTemplateRequest getTemplateRequest;
    protected EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    protected EBSymmetricKeyCreator keyCreator;

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

        if (spec instanceof EBEngineReference){
            final EBEngine lEngine = ((EBEngineReference) spec).getEBEngine();
            if (lEngine != null){
                this.engine = lEngine;
            }
        }

        if (this.engine == null && this.provider != null){
            this.engine = this.provider.getEngine();
        }

        if (spec instanceof EBCreateUOTemplateSpec){
            this.getTemplateRequest = ((EBCreateUOTemplateSpec) spec).getTemplateRequest();
        }

        if (this.random == null && engine != null){
            this.random = engine.getRnd();
        }

        if (this.random == null){
            this.random = new SecureRandom();
        }

        if (spec instanceof EBSymmetricKeyGenParameterSpec){
            this.keySize = ((EBSymmetricKeyGenParameterSpec) spec).getKeySize();
            this.keyType = ((EBSymmetricKeyGenParameterSpec) spec).getKeyType();
            this.checkKeySize(this.keySize, spec);

            keyCreator = new EBSymmetricKeyCreator.Builder()
                    .setEngine(this.engine)
                    .setRandom(this.random)
                    .setGetTemplateRequest(getTemplateRequest)
                    .build();

        } else {
            throw new InvalidAlgorithmParameterException("AlgorithmParameterSpec not supported");
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

    protected SecretKey engineGenerateKey() {
        if ("AES".equalsIgnoreCase(algorithm)){
            // AES key will be derived using this seed.
            final byte[] appKeySeed = new byte[16];
            random.nextBytes(appKeySeed);

            keyCreator
                    .setUoTypeFunction(UserObjectType.TYPE_PLAINAES)
                    .setAppKey(appKeySeed)
                    .setAppKeyGeneration(Constants.GENKEY_ENROLL_DERIVED);

            return keyCreator.engineGenerateKey();

        } else {
            throw new ProviderException("AES algorithm supported only.");
        }
    }
}
