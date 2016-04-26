package com.enigmabridge.provider.parameters;

import com.enigmabridge.EBUOReference;
import com.enigmabridge.UserObjectInfo;
import com.enigmabridge.UserObjectKey;
import com.enigmabridge.UserObjectKeyType;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * Created by dusanklinec on 26.04.16.
 */
public class EBAsymmetricKeyParameter extends AsymmetricKeyParameter implements EBCipherParameters, UserObjectKey {
    protected UserObjectKey uo;

    public EBAsymmetricKeyParameter(boolean privateKey) {
        super(privateKey);
    }

    @Override
    public UserObjectInfo getUserObjectInfo() {
        return uo.getUserObjectInfo();
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
}
