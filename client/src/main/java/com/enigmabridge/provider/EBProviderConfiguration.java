package com.enigmabridge.provider;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBUtils;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provider configuration, provided by the caller either by passing config files or direct arguments.
 *
 * Created by dusanklinec on 29.07.16.
 */
public class EBProviderConfiguration {
    protected JSONObject jsonRoot;

    /**
     * Default configuration instance
     * @return configuration
     */
    public static EBProviderConfiguration getInstance(){
        return new EBProviderConfiguration();
    }

    /**
     * Parses the input configuration.
     * By default it is considered as a file name.
     * If The string starts with "--" it is considered as JSON config.
     *
     * @param input input
     * @return config
     */
    public static EBProviderConfiguration getInstance(String input) throws IOException {
        String jsonInput = null;
        if (input.startsWith("--")){
            jsonInput = input.substring(2);

        } else {
            final FileInputStream fis = new FileInputStream(input);
            fis.close();

            jsonInput = EBUtils.convertStreamToString(fis);
        }

        return getInstance(EBUtils.parseJSON(jsonInput));
    }

    /**
     * Parses the input configuration.
     * By default it is considered as a file name.
     * If The string starts with "--" it is considered as JSON config.
     *
     * @param input input
     * @return config
     */
    public static EBProviderConfiguration getInstance(InputStream input) throws IOException {
        return getInstance(EBUtils.convertStreamToString(input));
    }

    /**
     * Constructs configuration directly from the json object.
     * @param jsonObject jsonObject with configuration
     * @return config
     */
    public static EBProviderConfiguration getInstance(JSONObject jsonObject) {
        return new EBProviderConfiguration(jsonObject);
    }

    /**
     * Extracts configuration from the engine.
     *
     * @param engine engine to process
     * @return config
     */
    public static EBProviderConfiguration getInstance(EBEngine engine) {
        return new EBProviderConfiguration(engine);
    }

    public EBProviderConfiguration() {
    }

    public EBProviderConfiguration(JSONObject jsonRoot) {
        fromJSON(jsonRoot);
    }

    public EBProviderConfiguration(EBEngine engine) {
        fromEngine(engine);
    }

    protected void fromJSON(JSONObject json){
        this.jsonRoot = json;
        // TODO: implement
    }

    protected void fromEngine(EBEngine engine){
        this.jsonRoot = null;
        // TODO: implement
    }

    public JSONObject getJsonRoot() {
        return jsonRoot;
    }
}
