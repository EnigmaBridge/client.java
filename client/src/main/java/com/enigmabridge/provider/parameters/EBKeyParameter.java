package com.enigmabridge.provider.parameters;

import com.enigmabridge.*;
import com.enigmabridge.comm.EBConnectionSettings;
import com.enigmabridge.provider.EBUOKey;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * KeyParameter is used as a base key holder for symmetric ciphers in the BouncyCastle.
 * Its a class, unfortunately, not an interface, thus we have to extend KeyParameter in order
 * to preserve compatibility with base classes (BaseBlockCipher).
 *
 * BlockCiphers are initialized with CipherParameter interface, which is either
 *  - KeyParameter
 *  - ParametersWithIv: IV + CipherParameter (KeyParameter)
 *  - AEADParameters: aead + CipherParameter (KeyParameter)
 *
 * Created by dusanklinec on 12.07.16.
 */
public class EBKeyParameter<T extends EBUOKey> extends KeyParameter implements EBCipherParameters, EBUOKey
{
    protected static final byte[] EMPTY_KEY = new byte[0];
    protected T uoKey;

    public EBKeyParameter(T uoKey) {
        super(EMPTY_KEY);
        this.uoKey = uoKey;
    }

    @Override
    public byte[] getKey()
    {
        throw new UnsupportedOperationException("This operation is not supported on opaque keys");
    }

    @Override
    public UserObjectKey getUserObjectKey() {
        return uoKey.getUserObjectKey();
    }

    @Override
    public UserObjectInfo getUserObjectInfo() {
        return uoKey.getUserObjectInfo();
    }

    @Override
    public long getUoid() {
        return uoKey.getUoid();
    }

    @Override
    public String getApiKey() {
        return uoKey.getApiKey();
    }

    @Override
    public EBCommKeys getCommKeys() {
        return uoKey.getCommKeys();
    }

    @Override
    public UserObjectType getUserObjectType() {
        return uoKey.getUserObjectType();
    }

    @Override
    public EBEndpointInfo getEndpointInfo() {
        return uoKey.getEndpointInfo();
    }

    @Override
    public String getAlgorithm() {
        return uoKey.getAlgorithm();
    }

    @Override
    public UserObjectKeyType getKeyType() {
        return uoKey.getKeyType();
    }

    @Override
    public int length() {
        return uoKey.length();
    }

    @Override
    public EBConnectionSettings getConnectionSettings() {
        return uoKey.getConnectionSettings();
    }

    @Override
    public EBEngine getEBEngine() {
        return uoKey.getEBEngine();
    }

    @Override
    public EBOperationConfiguration getOperationConfiguration() {
        return uoKey.getOperationConfiguration();
    }

    @Override
    public String getFormat() {
        return uoKey.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return uoKey.getEncoded();
    }

    public T getUoKey(){
        return uoKey;
    }
}

