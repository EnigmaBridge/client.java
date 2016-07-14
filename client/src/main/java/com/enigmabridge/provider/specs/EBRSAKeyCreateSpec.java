package com.enigmabridge.provider.specs;

import java.math.BigInteger;
import java.security.spec.KeySpec;

/**
 * Key import spec for non-CRT RSA keys, they require public modulus for CRT computation.
 *
 * Created by dusanklinec on 14.07.16.
 */
public class EBRSAKeyCreateSpec extends EBKeyCreateBaseSpec {
    protected final BigInteger publicExponent;

    public EBRSAKeyCreateSpec(KeySpec spec) {
        super(spec);
        publicExponent = null;
    }

    public EBRSAKeyCreateSpec(KeySpec spec, BigInteger publicExponent) {
        super(spec);
        this.publicExponent = publicExponent;
    }

    public BigInteger getPublicExponent() {
        return publicExponent;
    }
}
