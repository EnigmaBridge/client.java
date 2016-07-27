package com.enigmabridge.client;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBSettings;
import com.enigmabridge.EBSettingsBase;
import com.enigmabridge.EBURLConfig;
import com.enigmabridge.client.async.EBClientObjectAsync;
import com.enigmabridge.client.async.EBClientObjectAsyncSimple;
import com.enigmabridge.client.wrappers.EBWrappedCombined;
import com.enigmabridge.comm.EBConnectionSettings;
import org.json.JSONObject;

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

    public EBClientObject init(String urlConfig) throws MalformedURLException, UnsupportedEncodingException {
        return init(new EBURLConfig(urlConfig));
    }

    public EBClientObject init(JSONObject json){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(json);
        return new EBClientObject()
            .setClient(this)
            .setCryptoWrapper(wrapper);
    }

    public EBClientObject init(EBURLConfig urlConfig){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(urlConfig);
        return new EBClientObject()
                .setClient(this)
                .setCryptoWrapper(wrapper);
    }

    // Simple async API

    public EBClientObjectAsyncSimple initAsyncSimpleFromJSON(String json){
        return initAsyncSimple(new JSONObject(json));
    }

    public EBClientObjectAsyncSimple initAsyncSimple(String urlConfig) throws MalformedURLException, UnsupportedEncodingException {
        return initAsyncSimple(new EBURLConfig(urlConfig));
    }

    public EBClientObjectAsyncSimple initAsyncSimple(JSONObject json){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(json);
        return new EBClientObjectAsyncSimple.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    public EBClientObjectAsyncSimple initAsyncSimple(EBURLConfig urlConfig) {
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(urlConfig);
        return new EBClientObjectAsyncSimple.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    // Complex async API

    public EBClientObjectAsync initAsyncFromJSON(String json){
        return initAsync(new JSONObject(json));
    }

    public EBClientObjectAsync initAsync(String urlConfig) throws MalformedURLException, UnsupportedEncodingException {
        return initAsync(new EBURLConfig(urlConfig));
    }

    public EBClientObjectAsync initAsync(JSONObject json){
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(json);
        return new EBClientObjectAsync.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    public EBClientObjectAsync initAsync(EBURLConfig urlConfig) {
        final EBWrappedCombined wrapper = getCryptoWrapperInstance(urlConfig);
        return new EBClientObjectAsync.Builder()
                .setClient(this)
                .setCryptoWrapper(wrapper)
                .build();
    }

    // Crypto wrapper factory

    protected EBWrappedCombined getCryptoWrapperInstance(JSONObject json){
        return null; // TODO: xxx
    }

    protected EBWrappedCombined getCryptoWrapperInstance(EBURLConfig urlConfig){
        return null; // TODO: xxx
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
