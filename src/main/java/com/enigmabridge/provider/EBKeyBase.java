package com.enigmabridge.provider;

import com.enigmabridge.*;
import com.enigmabridge.comm.EBConnectionSettings;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Base class for all EB keys.
 * Created by dusanklinec on 26.04.16.
 */
public abstract class EBKeyBase implements EBUOKey {
    public static final String FORMAT_RAW = "RAW";
    public static final String FORMAT_X509 = "X.509";
    public static final String FORMAT_PKCS8 = "PKCS#8";
    public static final String FORMAT_JSON = "JSON";

    protected UserObjectKeyBase uo;
    protected EBEngine ebEngine;
    protected EBOperationConfiguration ebOperationConfig;

    protected boolean tokenObject = true;
    protected boolean sensitive = true;
    protected boolean extractable = false;

    public static abstract class AbstractBuilder<T extends EBKeyBase, B extends AbstractBuilder> {
        public B setUo(UserObjectKeyBase uo) {
            getObj().setUo(uo);
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine) {
            getObj().setEbEngine(engine);
            return getThisBuilder();
        }

        public B setOperationConfig(EBOperationConfiguration cfg){
            getObj().setEbOperationConfig(cfg);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    @Override
    public UserObjectInfo getUserObjectInfo() {
        return uo;
    }

    @Override
    public UserObjectKey getUserObjectKey() {
        return uo;
    }

    @Override
    public String getFormat() {
        // TODO:
        return null;
    }

    @Override
    public byte[] getEncoded() {
        // TODO:
        return null;
    }

    @Override
    public int length() {
        return uo.length();
    }

    @Override
    public UserObjectKeyType getKeyType() {
        return uo.getKeyType();
    }

    @Override
    public long getUoid() {
        return uo.getUoid();
    }

    @Override
    public String getApiKey() {
        return uo.getApiKey();
    }

    @Override
    public EBCommKeys getCommKeys() {
        return uo.getCommKeys();
    }

    @Override
    public int getUserObjectType() {
        return uo.getUserObjectType();
    }

    @Override
    public EBEndpointInfo getEndpointInfo() {
        return uo.getEndpointInfo();
    }

    @Override
    public EBConnectionSettings getConnectionSettings() {
        return null;
    }

    @Override
    public String getAlgorithm() {
        return uo.getAlgorithm();
    }

    @Override
    public EBEngine getEBEngine() {
        return ebEngine;
    }

    @Override
    public EBOperationConfiguration getOperationConfiguration() {
        return ebOperationConfig;
    }

    // Protected setters
    protected void setUo(UserObjectKeyBase uo) {
        this.uo = uo;
    }

    protected void setEbEngine(EBEngine ebEngine) {
        this.ebEngine = ebEngine;
    }

    protected void setEbOperationConfig(EBOperationConfiguration ebOperationConfig) {
        this.ebOperationConfig = ebOperationConfig;
    }

    protected void setTokenObject(boolean tokenObject) {
        this.tokenObject = tokenObject;
    }

    protected void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    protected void setExtractable(boolean extractable) {
        this.extractable = extractable;
    }
}
