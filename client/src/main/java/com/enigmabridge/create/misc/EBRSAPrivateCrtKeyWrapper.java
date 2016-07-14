package com.enigmabridge.create.misc;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;

/**
 * Provides javacard functionality on top of the existing RSAPrivateCrtKey.
 *
 * Created by dusanklinec on 13.07.16.
 */
public class EBRSAPrivateCrtKeyWrapper implements RSAPrivateCrtKey {
    protected final RSAPrivateCrtKey key;
    protected final RSAPrivateCrtKeySpec keySpec;
    protected final RSAPrivateKey ebKey;

    public EBRSAPrivateCrtKeyWrapper(RSAPrivateCrtKey key) {
        this.key = key;
        this.keySpec = null;
        this.ebKey = null;
    }

    public EBRSAPrivateCrtKeyWrapper(RSAPrivateCrtKeySpec key) {
        this.key = null;
        this.keySpec = key;
        this.ebKey = null;
    }

    public EBRSAPrivateCrtKeyWrapper(RSAPrivateKey ebKey) {
        this.key = null;
        this.keySpec = null;
        this.ebKey = ebKey;
    }

    public short getP(byte[] buffer, short offset) {
        return getNum(getPrimeP(), buffer, offset);
    }

    public short getQ(byte[] buffer, short offset) {
        return getNum(getPrimeQ(), buffer, offset);
    }

    public short getDP1(byte[] buffer, short offset) {
        return getNum(getPrimeExponentP(), buffer, offset);
    }

    public short getDQ1(byte[] buffer, short offset) {
        return getNum(getPrimeExponentQ(), buffer, offset);
    }

    public short getPQ(byte[] buffer, short offset) {
        return getNum(getCrtCoefficient(), buffer, offset);
    }

    protected short getNum(BigInteger num, byte[] buffer, short offset){
        final byte[] bytes = num.toByteArray();
        System.arraycopy(bytes, 0, buffer, offset, bytes.length);
        return (short) bytes.length;
    }

    @Override
    public BigInteger getPublicExponent() {
        if (key != null){
            return key.getPublicExponent();
        } else if (ebKey != null){
            return ebKey.getPublicExponent();
        } else {
            return keySpec.getPublicExponent();
        }
    }

    @Override
    public BigInteger getPrimeP() {
        if (key != null){
            return key.getPrimeP();
        } else if (ebKey != null){
            return ebKey.getPrime1();
        } else {
            return keySpec.getPrimeP();
        }
    }

    @Override
    public BigInteger getPrimeQ() {
        if (key != null){
            return key.getPrimeQ();
        } else if (ebKey != null){
            return ebKey.getPrime2();
        } else {
            return keySpec.getPrimeQ();
        }
    }

    @Override
    public BigInteger getPrimeExponentP() {
        if (key != null){
            return key.getPrimeExponentP();
        } else if (ebKey != null){
            return ebKey.getExponent1();
        } else {
            return keySpec.getPrimeExponentP();
        }
    }

    @Override
    public BigInteger getPrimeExponentQ() {
        if (key != null){
            return key.getPrimeExponentQ();
        } else if (ebKey != null){
            return ebKey.getExponent2();
        } else {
            return keySpec.getPrimeExponentQ();
        }
    }

    @Override
    public BigInteger getCrtCoefficient() {
        if (key != null){
            return key.getCrtCoefficient();
        } else if (ebKey != null){
            return ebKey.getCoefficient();
        } else {
            return keySpec.getCrtCoefficient();
        }
    }

    @Override
    public BigInteger getPrivateExponent() {
        if (key != null){
            return key.getPrivateExponent();
        } else if (ebKey != null){
            return ebKey.getPrivateExponent();
        } else {
            return keySpec.getPrivateExponent();
        }
    }

    @Override
    public BigInteger getModulus() {
        if (key != null){
            return key.getModulus();
        } else if (ebKey != null){
            return ebKey.getModulus();
        } else {
            return keySpec.getModulus();
        }
    }

    @Override
    public String getAlgorithm() {
        return key != null ? key.getAlgorithm() : "RSA";
    }

    @Override
    public String getFormat() {
        return key != null ? key.getFormat() : null;
    }

    @Override
    public byte[] getEncoded() {
        return key != null ? key.getEncoded() : null;
    }
}
