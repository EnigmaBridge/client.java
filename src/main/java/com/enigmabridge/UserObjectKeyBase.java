package com.enigmabridge;

import com.enigmabridge.comm.EBConnectionSettings;

/**
 * Basic implementation of the UserObjectKey.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class UserObjectKeyBase implements UserObjectKey {
    protected UserObjectInfo uo;
    protected String algorithm;
    protected int keyLength;
    protected UserObjectKeyType keyType;

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public int length() {
        return keyLength;
    }

    @Override
    public UserObjectKeyType getKeyType() {
        return keyType;
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
    public UserObjectInfo getUserObjectInfo() {
        return uo;
    }

    @Override
    public UserObjectKey getUserObjectKey() {
        return this;
    }

    @Override
    public EBConnectionSettings getConnectionSettings() {
        return uo.getConnectionSettings();
    }

    protected void setUo(UserObjectInfo uo) {
        this.uo = uo;
    }

    protected void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    protected void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    protected void setKeyType(UserObjectKeyType keyType) {
        this.keyType = keyType;
    }
}
