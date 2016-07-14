package com.enigmabridge.create.misc;

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

    public EBRSAPrivateCrtKeyWrapper(RSAPrivateCrtKey key) {
        this.key = key;
        this.keySpec = null;
    }

    public EBRSAPrivateCrtKeyWrapper(RSAPrivateCrtKeySpec key) {
        this.key = null;
        this.keySpec = key;
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
        return getNum(getModulus(), buffer, offset);
    }

    protected short getNum(BigInteger num, byte[] buffer, short offset){
        final byte[] bytes = num.toByteArray();
        System.arraycopy(bytes, 0, buffer, offset, buffer.length);
        return (short) buffer.length;
    }

    @Override
    public BigInteger getPublicExponent() {
        return key != null ? key.getPublicExponent() : keySpec.getPublicExponent();
    }

    @Override
    public BigInteger getPrimeP() {
        return key != null ? key.getPrimeP() : keySpec.getPrimeP();
    }

    @Override
    public BigInteger getPrimeQ() {
        return key != null ? key.getPrimeQ() : keySpec.getPrimeQ();
    }

    @Override
    public BigInteger getPrimeExponentP() {
        return key != null ? key.getPrimeExponentP() : keySpec.getPrimeExponentP();
    }

    @Override
    public BigInteger getPrimeExponentQ() {
        return key != null ? key.getPrimeExponentQ() : keySpec.getPrimeExponentQ();
    }

    @Override
    public BigInteger getCrtCoefficient() {
        return key != null ? key.getCrtCoefficient() : keySpec.getCrtCoefficient();
    }

    @Override
    public BigInteger getPrivateExponent() {
        return key != null ? key.getPrivateExponent() : keySpec.getPrivateExponent();
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

    @Override
    public BigInteger getModulus() {
        return key != null ? key.getModulus() : keySpec.getModulus();
    }
}
