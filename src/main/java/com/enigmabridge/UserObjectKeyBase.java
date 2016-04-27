package com.enigmabridge;

/**
 * Basic implementation of the UserObjectKey.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class UserObjectKeyBase implements UserObjectKey {
    protected UserObjectInfo uo;
    protected String algorithm;
    protected int keyLength;
    UserObjectKeyType keyType;

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
    public long getUserObjectType() {
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
}
