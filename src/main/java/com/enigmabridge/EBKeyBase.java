package com.enigmabridge;

/**
 * Base class for all EB keys.
 * Created by dusanklinec on 26.04.16.
 */
public abstract class EBKeyBase implements EBUOKey{
    public static final String FORMAT_RAW = "RAW";
    public static final String FORMAT_X509 = "X.509";
    public static final String FORMAT_PKCS8 = "PKCS#8";

    protected UserObjectKeyBase uo;

    protected boolean tokenObject = true;
    protected boolean sensitive = true;
    protected boolean extractable = false;

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
    public long getUserObjectType() {
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
}
