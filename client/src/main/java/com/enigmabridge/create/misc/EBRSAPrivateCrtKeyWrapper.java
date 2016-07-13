package com.enigmabridge.create.misc;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;

/**
 * Provides javacard functionality on top of the existing RSAPrivateCrtKey.
 *
 * Created by dusanklinec on 13.07.16.
 */
public class EBRSAPrivateCrtKeyWrapper implements RSAPrivateCrtKey {
    protected final RSAPrivateCrtKey key;

    public EBRSAPrivateCrtKeyWrapper(RSAPrivateCrtKey key) {
        this.key = key;
    }

    public short getP(byte[] buffer, short offset) {
        return getNum(key.getPrimeP(), buffer, offset);
    }

    public short getQ(byte[] buffer, short offset) {
        return getNum(key.getPrimeQ(), buffer, offset);
    }

    public short getDP1(byte[] buffer, short offset) {
        return getNum(key.getPrimeExponentP(), buffer, offset);
    }

    public short getDQ1(byte[] buffer, short offset) {
        return getNum(key.getPrimeExponentQ(), buffer, offset);
    }

    public short getPQ(byte[] buffer, short offset) {
        return getNum(key.getModulus(), buffer, offset);
    }

    protected short getNum(BigInteger num, byte[] buffer, short offset){
        final byte[] bytes = num.toByteArray();
        System.arraycopy(bytes, 0, buffer, offset, buffer.length);
        return (short) buffer.length;
    }

    @Override
    public BigInteger getPublicExponent() {
        return key.getPublicExponent();
    }

    @Override
    public BigInteger getPrimeP() {
        return key.getPrimeP();
    }

    @Override
    public BigInteger getPrimeQ() {
        return key.getPrimeQ();
    }

    @Override
    public BigInteger getPrimeExponentP() {
        return key.getPrimeExponentP();
    }

    @Override
    public BigInteger getPrimeExponentQ() {
        return key.getPrimeExponentQ();
    }

    @Override
    public BigInteger getCrtCoefficient() {
        return key.getCrtCoefficient();
    }

    @Override
    public BigInteger getPrivateExponent() {
        return key.getPrivateExponent();
    }

    @Override
    public String getAlgorithm() {
        return key.getAlgorithm();
    }

    @Override
    public String getFormat() {
        return key.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return key.getEncoded();
    }

    @Override
    public BigInteger getModulus() {
        return key.getModulus();
    }
}
