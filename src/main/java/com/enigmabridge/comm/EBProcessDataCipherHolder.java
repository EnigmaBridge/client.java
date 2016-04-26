package com.enigmabridge.comm;

import org.bouncycastle.crypto.Mac;

import javax.crypto.Cipher;


/**
 * Holder for Process data cipher & mac.
 * Created by dusanklinec on 26.04.16.
 */
public class EBProcessDataCipherHolder {
    protected boolean forEncryption;
    protected Cipher enc;
    protected Mac mac;

    public EBProcessDataCipherHolder() {
    }

    public EBProcessDataCipherHolder(boolean forEncryption, Cipher enc, Mac mac) {
        this.forEncryption = forEncryption;
        this.enc = enc;
        this.mac = mac;
    }

    public Cipher getEnc() {
        return enc;
    }

    public void setEnc(Cipher enc) {
        this.enc = enc;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public boolean isForEncryption() {
        return forEncryption;
    }

    public void setForEncryption(boolean forEncryption) {
        this.forEncryption = forEncryption;
    }
}
