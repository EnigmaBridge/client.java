package com.enigmabridge.client;

import com.enigmabridge.*;
import com.enigmabridge.client.async.EBClientObjectAsync;
import com.enigmabridge.client.async.EBClientObjectAsyncSimple;
import com.enigmabridge.client.wrappers.EBWrappedCombined;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.provider.EBKeyBase;
import com.enigmabridge.provider.EBSecretKeyFactory;
import com.enigmabridge.provider.EBSymmetricKey;
import com.enigmabridge.provider.rsa.EBRSAPrivateKey;
import com.enigmabridge.provider.rsa.KeyFactorySpi;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.*;

/**
 * High-level interface for EnigmaBridge.
 *
 * Created by dusanklinec on 25.07.16.
 */
public class EBClient {
    /**
     * Number of workers in the thread pool executor.
     */
    protected int workerCount = 1;

    /**
     * Engine with default settings.
     */
    protected EBEngine engine;

    /**
     * Executor service being used.
     */
    protected ExecutorService executorService;

    /**
     * Builder
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBClient, B extends EBClient.AbstractBuilder> {
        public B setEngine(EBEngine a) {
            getObj().setEngine(a);
            return getThisBuilder();
        }

        public B setWorkerCount(int a) {
            getObj().setWorkerCount(a);
            return getThisBuilder();
        }

        public B setExecutorService(ExecutorService a) {
            getObj().setExecutorService(a);
            return getThisBuilder();
        }

        public B setDefaultSettings(EBConnectionSettings settings){
            getObj().setDefaultSettings(settings);
            return getThisBuilder();
        }

        public B setClient(EBClient client){
            getObj().setExecutorService(client.getExecutorService());
            getObj().setWorkerCount(client.getWorkerCount());
            getObj().setEngine(client.getEngine());
            return getThisBuilder();
        }

        public EBEngine getEngine(){
            return getObj().getEngine();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBClient, EBClient.Builder> {
        private final EBClient child = new EBClient();

        @Override
        public EBClient getObj() {
            return child;
        }

        @Override
        public EBClient build() {
            child.build();
            return child;
        }

        @Override
        public Builder getThisBuilder() {
            return this;
        }
    }

    /**
     * Returns new client instance with default settings.
     *
     * @return EBClient
     */
    public static EBClient getInstance(){
        return new Builder().build();
    }

    /**
     * Constructs client state
     */
    protected void build(){
        if (this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(this.workerCount);
        } else if (this.executorService instanceof ThreadPoolExecutor){
            this.workerCount = ((ThreadPoolExecutor) this.executorService).getPoolSize();
        }
    }

    // Simple sync API

    public EBClientObject initFromJSON(String json){
        return init(new JSONObject(json));
    }

    public EBClientObject init(String urlConfig) throws IOException {
        return init(new EBURLConfig(urlConfig));
    }

    public EBClientObject init(JSONObject json){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(json);
        return new EBClientObject()
            .setClient(this)
            .setCryptoWrapper(wrapper);
    }

    public EBClientObject init(EBURLConfig urlConfig) throws IOException {
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(urlConfig);
        return new EBClientObject()
                .setClient(this)
                .setCryptoWrapper(wrapper);
    }

    public EBClientObject init(EBWrappedCombined wrapper) throws IOException {
        return new EBClientObject()
                .setClient(this)
                .setCryptoWrapper(wrapper);
    }

    // Simple async API

    public EBClientObjectAsyncSimple initAsyncSimpleFromJSON(String json){
        return initAsyncSimple(new JSONObject(json));
    }

    public EBClientObjectAsyncSimple initAsyncSimple(String urlConfig) throws IOException {
        return initAsyncSimple(new EBURLConfig(urlConfig));
    }

    public EBClientObjectAsyncSimple initAsyncSimple(JSONObject json){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(json);
        return new EBClientObjectAsyncSimple.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    public EBClientObjectAsyncSimple initAsyncSimple(EBURLConfig urlConfig) throws IOException {
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(urlConfig);
        return new EBClientObjectAsyncSimple.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    public EBClientObjectAsyncSimple initAsyncSimple(EBWrappedCombined wrapper) throws IOException {
        return new EBClientObjectAsyncSimple.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    // Complex async API

    public EBClientObjectAsync initAsyncFromJSON(String json){
        return initAsync(new JSONObject(json));
    }

    public EBClientObjectAsync initAsync(String urlConfig) throws IOException {
        return initAsync(new EBURLConfig(urlConfig));
    }

    public EBClientObjectAsync initAsync(JSONObject json){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(json);
        return new EBClientObjectAsync.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    public EBClientObjectAsync initAsync(EBURLConfig urlConfig) throws IOException {
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(urlConfig);
        return new EBClientObjectAsync.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    public EBClientObjectAsync initAsync(EBWrappedCombined wrapper) throws IOException {
        return new EBClientObjectAsync.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    // Crypto wrapper factory

    public EBWrappedCombined getCryptoWrapperInstance(JSONObject json){
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public EBWrappedCombined getCryptoWrapperInstance(EBURLConfig urlConfig) throws IOException {
        // Try to find EB provider serialized keys. Can be directly used with cipher.
        // Symmetric key. AES, for now.
        final JSONObject symmetricJson = urlConfig.getElement(EBSecretKeyFactory.FIELD_SYMMETRIC_KEY);
        if (symmetricJson != null){
            final EBSymmetricKey tmpKey = new EBSymmetricKey.Builder()
                    .setEngine(engine)
                    .setJson(symmetricJson)
                    .build();
            // TODO: continue
            return null;
        }

        // Asymmetric key, RSA. Can be used to encrypt (locally), decrypt(in EB), sign, verify.
        final JSONObject rsaJson = urlConfig.getElement(KeyFactorySpi.FIELD_RSA_PRIVATE);
        if (rsaJson != null){
            final EBRSAPrivateKey tmpKey = new EBRSAPrivateKey.Builder()
                    .setEngine(engine)
                    .setJson(rsaJson)
                    .build();
            // TODO: continue
            return null;
        }

        // General key
        final JSONObject uoJson = urlConfig.getElement(EBKeyBase.FIELD_UO);
        if (uoJson != null){
            // Try UserObjectKey at first
            try {
                final UserObjectKeyBase tmpKey = new UserObjectKeyBase.Builder()
                        .setEndpointInfo(urlConfig.getEndpointInfo())
                        .setApiKey(urlConfig.getApiKey())
                        .setConnectionSettings(urlConfig.getConnectionSettings())
                        .setJson(uoJson)
                        .build();
                // TODO: continue;
                return null;

            } catch(Exception e){

            }

            // Try general UO
            try {
                final UserObjectInfoBase tmpKey = new UserObjectInfoBase.Builder()
                        .setEndpointInfo(urlConfig.getEndpointInfo())
                        .setApiKey(urlConfig.getApiKey())
                        .setConnectionSettings(urlConfig.getConnectionSettings())
                        .setJson(uoJson)
                        .build();
                // TODO: continue;
                return null;

            } catch(Exception e){

            }
        }

        throw new IllegalArgumentException("Unrecognized user object");
    }

    // Getters

    public int getWorkerCount() {
        return workerCount;
    }

    public EBEngine getEngine() {
        return engine;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    // Setters

    protected EBClient setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
        return this;
    }

    protected EBClient setEngine(EBEngine engine) {
        this.engine = engine;
        return this;
    }

    protected EBClient setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    protected EBClient setDefaultSettings(EBConnectionSettings settings) {
        if (this.engine == null){
            this.engine = new EBEngine();
        }

        EBSettings oldSettings = this.engine.getDefaultSettings();
        if (oldSettings == null){
            this.engine.setDefaultSettings(new EBSettingsBase());
        }

        EBSettingsBase bs = new EBSettingsBase.Builder()
                .setSettings(oldSettings)
                .setConnectionSettings(settings.copy())
                .build();

        this.engine.setDefaultSettings(bs);
        return this;
    }
}
