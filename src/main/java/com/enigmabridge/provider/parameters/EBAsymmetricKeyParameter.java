package com.enigmabridge.provider.parameters;

import com.enigmabridge.*;
import com.enigmabridge.comm.EBConnectionSettings;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * Cipher parameter internally used in the provider.
 * For asymmetric ciphers.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBAsymmetricKeyParameter extends AsymmetricKeyParameter
        implements EBCipherParameters, UserObjectKey, EBEngineReference, EBOperationConfigurationReference
{
    protected UserObjectKey uo;
    protected EBEngine ebEngine;
    protected EBOperationConfiguration ebOperationConfig;

    public EBAsymmetricKeyParameter(boolean privateKey) {
        super(privateKey);
    }

    public EBAsymmetricKeyParameter(boolean privateKey, UserObjectKey uo) {
        super(privateKey);
        this.uo = uo;
    }

    public EBAsymmetricKeyParameter(boolean privateKey, UserObjectKey uo, EBEngine ebEngine, EBOperationConfiguration ebOperationConfig) {
        super(privateKey);
        this.uo = uo;
        this.ebEngine = ebEngine;
        this.ebOperationConfig = ebOperationConfig;
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

    @Override
    public EBEngine getEBEngine() {
        return ebEngine;
    }

    @Override
    public EBOperationConfiguration getOperationConfiguration() {
        return ebOperationConfig;
    }
}
