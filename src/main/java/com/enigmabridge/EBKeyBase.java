package com.enigmabridge;

import com.enigmabridge.EBUOKey;
import com.enigmabridge.UserObjectInfo;

/**
 * Base class for all EB keys.
 * Created by dusanklinec on 26.04.16.
 */
public abstract class EBKeyBase implements EBUOKey{
    public static final String FORMAT_RAW = "RAW";
    public static final String FORMAT_X509 = "X.509";
    public static final String FORMAT_PKCS8 = "PKCS#8";

    protected UserObjectInfo uo;
    protected String algorithm;
    protected int keyLength;

    protected boolean tokenObject = true;
    protected boolean sensitive = true;
    protected boolean extractable = false;

    @Override
    public UserObjectInfo getUserObjectInfo() {
        return uo;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
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
        return keyLength;
    }
}
