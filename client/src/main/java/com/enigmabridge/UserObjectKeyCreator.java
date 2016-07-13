package com.enigmabridge;

import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.create.*;
import com.enigmabridge.provider.EBSymmetricKey;

import java.io.IOException;
import java.security.ProviderException;
import java.security.SecureRandom;

/**
 * Creates UserObject keys in the EB.
 *
 * Created by dusanklinec on 13.07.16.
 */
public class UserObjectKeyCreator {
    private SecureRandom random;
    private EBEngine engine;

    private UserObjectType uoType;
    private EBUOGetTemplateRequest getTemplateRequest;

    private EBCommKeys commKeys;
    private byte[] appKey;

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

    public UserObjectKeyCreator setAppKey(byte[] appKey){
        this.appKey = appKey;
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

    public UserObjectKeyBase.Builder create() {
        final EBEndpointInfo endpoint = engine.getDefaultSettings().getEndpointInfo();
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
                .setEndpoint(new EBEndpointInfo(endpoint.getScheme(), endpoint.getHostname(), 11182))
                .setRequest(req)
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_ENC, commKeys.getEncKey()))
                .addKey(new EBUOTemplateKey(Constants.KEY_COMM_MAC, commKeys.getMacKey()));

        if (appKey != null){
            callBld.addKey(new EBUOTemplateKey(Constants.KEY_APP, appKey));
        }

        final EBCreateUOSimpleCall createCall = callBld.build();
        try {
            final EBCreateUOResponse response = createCall.create();

            // Create UOKey
            final UserObjectKeyBase.Builder keyBld = new UserObjectKeyBase.Builder()
                    .setUoid(response.getHandle().getUoId())
                    .setUserObjectType(response.getHandle().getUoType().getValue())
                    .setCommKeys(new EBCommKeys(commKeys))
                    .setKeyLength(response.getHandle().getUoType().keyLength()/8);

            return keyBld;

        } catch (IOException e) {
            throw new EBEngineException("Could not create UO", e);
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
}
