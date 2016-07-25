package com.enigmabridge;

import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.create.*;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKey;
import com.enigmabridge.create.misc.EBRSAPrivateCrtKeyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;

/**
 * Creates UserObject keys in the EB.
 *
 * Created by dusanklinec on 13.07.16.
 */
public class UserObjectKeyCreator {
    private static final Logger LOG = LoggerFactory.getLogger(UserObjectKeyCreator.class);

    protected SecureRandom random;
    protected EBEngine engine;

    protected UserObjectType uoType;
    protected EBUOGetTemplateRequest getTemplateRequest;

    protected EBCommKeys commKeys;
    protected EBUOTemplateKey appKey;

    protected EBCreateUOResponse lastResponse;

    public static abstract class AbstractBuilder<T extends UserObjectKeyCreator, B extends AbstractBuilder>
    {
        public B setRandom(SecureRandom random) {
            getObj().setRandom(random);
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine) {
            getObj().setEngine(engine);
            return getThisBuilder();
        }

        public B setGetTemplateRequest(EBUOGetTemplateRequest getTemplateRequest) {
            getObj().setGetTemplateRequest(getTemplateRequest);
            return getThisBuilder();
        }

        public B setUoType(UserObjectType uoType) {
            getObj().setUoType(uoType);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<UserObjectKeyCreator, UserObjectKeyCreator.Builder> {
        private final UserObjectKeyCreator parent = new UserObjectKeyCreator();

        @Override
        public UserObjectKeyCreator.Builder getThisBuilder() {
            return this;
        }

        @Override
        public UserObjectKeyCreator getObj() {
            return parent;
        }

        @Override
        public UserObjectKeyCreator build() {
            final UserObjectKeyCreator obj = getObj();

            // Engine has to be set.
            if (obj.engine == null){
                throw new NullPointerException("Engine has to be set");
            }

            // At least one type has to be set
            if (obj.uoType == null && obj.getTemplateRequest == null){
                throw new NullPointerException("At least one of the uoType, getTemplateRequest has to be set");
            }

            // Empty request -> create a new one
            if (obj.getTemplateRequest == null){
                obj.getTemplateRequest = new EBUOGetTemplateRequest().setType(obj.uoType);
            }

            // SecureRandom
            if (obj.random == null){
                obj.setRandom(obj.engine.getRnd());
            }

            if (obj.random == null){
                obj.setRandom(new SecureRandom());
            }

            return parent;
        }
    }

    // Build logic
    public UserObjectKeyCreator setCommKeys(byte[] encKey, byte[] macKey){
        this.commKeys = new EBCommKeys(encKey, macKey);
        return this;
    }

    public UserObjectKeyCreator setCommKeys(EBCommKeys commKeys){
        this.commKeys = commKeys;
        return this;
    }

    public UserObjectKeyCreator setAppKeyGeneration(int appKeyGeneration){
        this.getTemplateRequest.setGenerationAppKey(appKeyGeneration);
        return this;
    }

    public UserObjectKeyCreator setUoTypeFunction(int typeFunction){
        this.getTemplateRequest.setTypeFunction(typeFunction);
        return this;
    }

    public UserObjectKeyCreator setAppKey(byte[] appKey){
        this.appKey = new EBUOTemplateKey(Constants.KEY_APP, appKey);
        return this;
    }

    // Sugar for easy setting of objects
    // Later maybe move to anoher builder class, after more objects emerge.

    /**
     * Adds the symmetric key from the specifications (e.g., AES).
     * For secret key it is needed to set setAppKeyGeneration, setUoTypeFunction.
     * We don't know which mode user wants to use (e.g., derive, client = import).
     *
     * @param keySpec secret key spec to import
     * @return this
     */
    public UserObjectKeyCreator setAppKey(SecretKeySpec keySpec){
        this.appKey = new EBUOTemplateKey(keySpec);
        return this;
    }

    /**
     * Sets RSA private CRT key wrapper for import.
     * Serializes it to the appKey for import, sets setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param wrapper RSA private key wrapper.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(EBRSAPrivateCrtKeyWrapper wrapper){
        this.appKey = new EBUOTemplateKeyRSA(wrapper);
        setAppKeyGeneration(Constants.GENKEY_CLIENT);
        setRSADecryptFunctionFromModulus(wrapper.getModulus());
        return this;
    }

    /**
     * Sets RSA private CRT key spec for import.
     * Serializes it to the appKey for import, sets setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param spec RSA private key spec.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(RSAPrivateCrtKeySpec spec){
        return setAppKey(spec, null);
    }

    /**
     * Sets RSA private CRT key spec for import.
     * Serializes it to the appKey for import, sets setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param spec RSA private key spec.
     * @param e public exponent, unused. for easier calling if user is unsure whether CRT or not.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(RSAPrivateCrtKeySpec spec, BigInteger e){
        return setAppKey(new EBRSAPrivateCrtKeyWrapper(spec));
    }

    /**
     * Sets RSA private CRT key for import.
     * Serializes it to the appKey for import, sets setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param key RSA private key spec.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(RSAPrivateCrtKey key){
        return setAppKey(key, null);
    }

    /**
     * Sets RSA private CRT key for import.
     * Serializes it to the appKey for import, sets setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param key RSA private key spec.
     * @param e public exponent, unused. for easier calling if user is unsure whether CRT or not.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(RSAPrivateCrtKey key, BigInteger e){
        return setAppKey(new EBRSAPrivateCrtKeyWrapper(key));
    }

    /**
     * Sets RSA private key spec for import.
     * Only CRT keys are allowed to import thus this one needs to be converted to CRT key.
     * Such conversion may take some time and may not succeed. If conversion fails, RuntimeException is thrown.
     *
     * Key is then serialized to the appKey for import, setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param spec RSA private key spec.
     * @param e public exponent, required.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(RSAPrivateKeySpec spec, BigInteger e){
        return setAppKey(new EBRSAPrivateCrtKeyWrapper(new EBRSAPrivateCrtKey(spec, e)));
    }

    /**
     * Sets RSA private key for import.
     * Only CRT keys are allowed to import thus this one needs to be converted to CRT key.
     * Such conversion may take some time and may not succeed. If conversion fails, RuntimeException is thrown.
     *
     * Key is then serialized to the appKey for import, setAppKeyGeneration(client), calls
     * appropriate setUoTypeFunction.
     *
     * @param key RSA private key.
     * @param e public exponent, required.
     * @return this
     */
    public UserObjectKeyCreator setAppKey(RSAPrivateKey key, BigInteger e){
        return setAppKey(new EBRSAPrivateCrtKeyWrapper(new EBRSAPrivateCrtKey(key, e)));
    }

    protected void setRSADecryptFunctionFromModulus(BigInteger modulus){
        if (modulus == null){
            throw new NullPointerException("Empty modulus");
        }

        setUoTypeFunction(UserObjectType.getRSADecryptFunctionFromModulus(modulus));
    }

    /**
     * Creates a new Use Object Key from the input values.
     * @return builder for new user object key
     */
    public UserObjectKeyBase.Builder create() throws IOException {
        final EBEndpointInfo endpoint = engine.getEnrollmentEndpoint();
        final EBUOGetTemplateRequest req = this.getTemplateRequest;

        if (commKeys == null){
            commKeys = EBCommKeys.generate(random);
        }

        // Force remaining parameters to default values.
        req.setGenerationBillingKey(Constants.GENKEY_LEGACY_ENROLL_RANDOM);
        req.setFormat(1);
        req.setProtocol(1);

        EBCreateUOSimpleCall.Builder callBld = new EBCreateUOSimpleCall.Builder()
                .setEngine(engine)
                .setEndpoint(endpoint)
                .setRequest(req)
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_ENC, commKeys.getEncKey()))
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_MAC, commKeys.getMacKey()));

        if (appKey != null){
            callBld.addKey(appKey);
        }

        final EBCreateUOSimpleCall createCall = callBld.build();
        try {
            final EBCreateUOResponse response = createCall.create();
            if (!response.isCodeOk()){
                if (EBDevSettings.shouldLogFailedCreateUO()){
                    LOG.debug("Failed createUO: " + createCall.getCreateRequest());
                }
                throw new EBEngineException("Could not create UO - response: " + response.toString());
            }

            // Create UOKey
            final UserObjectKeyBase.Builder keyBld = new UserObjectKeyBase.Builder()
                    .setUoid(response.getHandle().getUoId())
                    .setUserObjectType(response.getHandle().getUoType().getValue())
                    .setCommKeys(new EBCommKeys(commKeys))
                    .setKeyLength(response.getHandle().getUoType().keyLength());

            lastResponse = response;
            return keyBld;

        } catch (IOException e) {
            throw new IOException("Could not create UO", e);
        } catch (EBCorruptedException e) {
            throw new EBEngineException("Could not create UO", e);
        }
    }

    // Setters

    protected void setRandom(SecureRandom random) {
        this.random = random;
    }

    protected void setEngine(EBEngine engine) {
        this.engine = engine;
    }

    public void setGetTemplateRequest(EBUOGetTemplateRequest getTemplateRequest) {
        if (getTemplateRequest == null){
            throw new NullPointerException("Template request cannot be null");
        }

        this.getTemplateRequest = getTemplateRequest;
    }

    public void setUoType(UserObjectType uoType) {
        if (getTemplateRequest != null) {
            this.getTemplateRequest.setType(uoType);
        }
        this.uoType = uoType;
    }

    // Getters

    public SecureRandom getRandom() {
        return random;
    }

    public EBEngine getEngine() {
        return engine;
    }

    public EBUOGetTemplateRequest getGetTemplateRequest() {
        return getTemplateRequest;
    }

    public UserObjectType getUoType() {
        return UserObjectType.valueOf(getTemplateRequest.getType());
    }

    public EBCreateUOResponse getLastResponse() {
        return lastResponse;
    }
}
