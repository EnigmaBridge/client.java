package com.enigmabridge.provider.rsa;

import com.enigmabridge.EBKeyBase;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

/**
 * General RSA key, common for private and public parts.
 * Conforms to general Java Key interface.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAKey extends EBKeyBase implements PrivateKey, RSAKey {
    static final long serialVersionUID = 1;

    @Override
    public BigInteger getModulus() {
        return null;
    }

}
