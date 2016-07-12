package com.enigmabridge.provider.parameters;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBOperationConfiguration;
import com.enigmabridge.UserObjectKey;
import com.enigmabridge.UserObjectKeyType;

import java.math.BigInteger;

/**
 * EB RSA parameters.
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAKeyParameter extends EBAsymmetricKeyParameter {
    /**
     * Modulus & public exponents are required for blinding.
     */
    protected BigInteger modulus;
    protected BigInteger publicExponent;

    public EBRSAKeyParameter(boolean privateKey, UserObjectKey uo) {
        super(privateKey, uo);
    }

    public EBRSAKeyParameter(boolean privateKey, UserObjectKey uo, BigInteger modulus, BigInteger publicExponent) {
        super(privateKey, uo);
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    public EBRSAKeyParameter(boolean privateKey, UserObjectKey uo, EBEngine ebEngine, EBOperationConfiguration ebOperationConfig, BigInteger modulus, BigInteger publicExponent) {
        super(privateKey, uo, ebEngine, ebOperationConfig);
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    /**
     * Returns true if blinding is possible with given parameters.
     * @return
     */
    public boolean canBlind(){
        return modulus != null
                && publicExponent != null
                && !modulus.equals(BigInteger.ZERO)
                && !publicExponent.equals(BigInteger.ZERO);
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPublicExponent() {
        return publicExponent;
    }
}
