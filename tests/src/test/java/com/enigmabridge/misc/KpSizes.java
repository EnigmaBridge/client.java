package com.enigmabridge.misc;

import java.math.BigInteger;
import java.security.spec.RSAPrivateCrtKeySpec;

/**
 * Created by dusanklinec on 29.07.16.
 */
public class KpSizes {
    protected RSAPrivateCrtKeySpec repr;

    // Aux
    protected int modulusSize;
    protected int privateExponentSize;
    protected int publicExponentSize;
    protected int primePSize;
    protected int primeQSize;
    protected int primeExponentPSize;
    protected int primeExponentQSize;
    protected int crtCoefficientSize;

    public KpSizes(RSAPrivateCrtKeySpec repr) {
        this.repr = repr;

        modulusSize = byteLen(repr.getModulus());
        privateExponentSize = byteLen(repr.getPrivateExponent());
        publicExponentSize = byteLen(repr.getPublicExponent());
        primePSize = byteLen(repr.getPrimeP());
        primeQSize = byteLen(repr.getPrimeQ());
        primeExponentPSize = byteLen(repr.getPrimeExponentP());
        primeExponentQSize = byteLen(repr.getPrimeExponentQ());
        crtCoefficientSize = byteLen(repr.getCrtCoefficient());
    }

    protected int byteLen(BigInteger bi) {
        return (bi.bitLength() + 7) / 8;
    }

    protected int byteLen(int bits) {
        return (bits + 7) / 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KpSizes kpSizes = (KpSizes) o;

        if (modulusSize != kpSizes.modulusSize) return false;
        if (privateExponentSize != kpSizes.privateExponentSize) return false;
        if (publicExponentSize != kpSizes.publicExponentSize) return false;
        if (primePSize != kpSizes.primePSize) return false;
        if (primeQSize != kpSizes.primeQSize) return false;
        if (primeExponentPSize != kpSizes.primeExponentPSize) return false;
        if (primeExponentQSize != kpSizes.primeExponentQSize) return false;
        return crtCoefficientSize == kpSizes.crtCoefficientSize;

    }

    @Override
    public int hashCode() {
        int result = modulusSize;
        result = 31 * result + privateExponentSize;
        result = 31 * result + publicExponentSize;
        result = 31 * result + primePSize;
        result = 31 * result + primeQSize;
        result = 31 * result + primeExponentPSize;
        result = 31 * result + primeExponentQSize;
        result = 31 * result + crtCoefficientSize;
        return result;
    }

    public String specString() {
        return "\n/* n=*/hex2bigInt(\"" + repr.getModulus().toString(16) + "\"),"
                + "\n/* e=*/hex2bigInt(\"" + repr.getPublicExponent().toString(16) + "\"),"
                + "\n/* d=*/hex2bigInt(\"" + repr.getPrivateExponent().toString(16) + "\"),"
                + "\n/* p=*/hex2bigInt(\"" + repr.getPrimeP().toString(16) + "\"),"
                + "\n/* q=*/hex2bigInt(\"" + repr.getPrimeQ().toString(16) + "\"),"
                + "\n/*eP=*/hex2bigInt(\"" + repr.getPrimeExponentP().toString(16) + "\"),"
                + "\n/*eQ=*/hex2bigInt(\"" + repr.getPrimeExponentQ().toString(16) + "\"),"
                + "\n/*PQ=*/hex2bigInt(\"" + repr.getCrtCoefficient().toString(16) + "\")";
    }

    @Override
    public String toString() {
        return "KpSizes{" +
                "n=" + modulusSize +
                ", e=" + publicExponentSize +
                ", d=" + privateExponentSize +
                ", p=" + primePSize +
                ", q=" + primeQSize +
                ", expP=" + primeExponentPSize +
                ", expQ=" + primeExponentQSize +
                ", pqInv=" + crtCoefficientSize +
                ", repr=" + specString() +
                '}';
    }
}
