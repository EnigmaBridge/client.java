package com.enigmabridge.provider.parameters;

import com.enigmabridge.*;
import com.enigmabridge.comm.EBConnectionSettings;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * Created by dusanklinec on 26.04.16.
 */
public class EBAsymmetricKeyParameter extends AsymmetricKeyParameter implements EBCipherParameters, UserObjectKey {
    protected UserObjectKey uo;

    public EBAsymmetricKeyParameter(boolean privateKey) {
        super(privateKey);
    }

    public EBAsymmetricKeyParameter(boolean privateKey, UserObjectKey uo) {
        super(privateKey);
        this.uo = uo;
    }

    @Override
    public UserObjectKey getUserObjectKey() {
        return uo.getUserObjectKey();
    }

    @Override
    public UserObjectInfo getUserObjectInfo() {
        return uo.getUserObjectInfo();
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
    public String getAlgorithm() {
        return uo.getAlgorithm();
    }

    @Override
    public UserObjectKeyType getKeyType() {
        return uo.getKeyType();
    }

    @Override
    public int length() {
        return uo.length();
    }

    @Override
    public EBConnectionSettings getConnectionSettings() {
        return uo.getConnectionSettings();
    }
}
