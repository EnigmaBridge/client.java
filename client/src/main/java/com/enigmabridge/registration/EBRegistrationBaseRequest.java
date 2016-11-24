package com.enigmabridge.registration;

import com.enigmabridge.comm.EBRawRequest;
import com.enigmabridge.create.consts.Environment;
import org.json.JSONObject;

/**
 * Base registration request - contains request root JSON object.
 * Created by dusanklinec on 24.11.16.
 */
public class EBRegistrationBaseRequest extends EBRawRequest {
    /**
     * Root JSON object with the request body.
     * Can be filled in manually but in general top-level setters should take care
     * about properly filling this in.
     */
    protected JSONObject root;

    /**
     * Version field in the registration request.
     */
    protected Integer regVersion = 1;

    /**
     * Registration function name to call.
     */
    protected String function;

    /**
     * Environment scope definition of the call.
     */
    protected String environment = Environment.DEV.toString();

    public EBRegistrationBaseRequest() {
    }

    public JSONObject getRoot() {
        return root;
    }

    public void setRoot(JSONObject root) {
        this.root = root;
    }

    public int getRegVersion() {
        return regVersion;
    }

    public void setRegVersion(int regVersion) {
        this.regVersion = regVersion;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getSuffix(){
        return this.getPath();
    }

    public void setSuffix(String prefix){
        this.setPath(prefix);
    }

}
