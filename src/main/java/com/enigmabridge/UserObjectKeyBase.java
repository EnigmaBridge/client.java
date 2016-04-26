package com.enigmabridge;

/**
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
    public UserObjectInfo getUserObjectInfo() {
        return uo;
    }

    @Override
    public UserObjectKeyType getKeyType() {
        return keyType;
    }
}
