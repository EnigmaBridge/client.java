package com.enigmabridge.client.async;

import java.lang.ref.WeakReference;

/**
 * Object passed to the listener on crypto operation finished.
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEvent {
    /**
     * Client which started this call.
     */
    protected WeakReference<EBClientObjectAsync> clientObject;

    /**
     * Discriminator object, provided by the caller to differentiate between doFinal() calls. Null by default.
     */
    protected Object discriminator;

    /**
     * Result buffer from the operation.
     */
    protected byte[] resultBuffer;

    /**
     * Contains valid value for verify() call.
     */
    protected Boolean resultVerify;

    public EBAsyncCryptoEvent() {
    }

    public EBAsyncCryptoEvent(EBClientObjectAsync clientObject) {
        this.clientObject = new WeakReference<EBClientObjectAsync>(clientObject);
    }

    public EBAsyncCryptoEvent(EBClientObjectAsync clientObject, Object discriminator) {
        this.clientObject = new WeakReference<EBClientObjectAsync>(clientObject);
        this.discriminator = discriminator;
    }

    public EBAsyncCryptoEvent(EBClientObjectAsync clientObject, Object discriminator, byte[] resultBuffer) {
        this.clientObject = new WeakReference<EBClientObjectAsync>(clientObject);
        this.discriminator = discriminator;
        this.resultBuffer = resultBuffer;
    }

    public EBAsyncCryptoEvent(EBClientObjectAsync clientObject, Object discriminator, Boolean resultVerify) {
        this.clientObject = new WeakReference<EBClientObjectAsync>(clientObject);
        this.discriminator = discriminator;
        this.resultVerify = resultVerify;
    }

    public EBAsyncCryptoEvent(byte[] resultBuffer) {
        this.resultBuffer = resultBuffer;
    }

    public EBAsyncCryptoEvent(Boolean resultVerify) {
        this.resultVerify = resultVerify;
    }

    // Getters

    public EBClientObjectAsync getClientObject() {
        return clientObject != null ? clientObject.get() : null;
    }

    public Object getDiscriminator() {
        return discriminator;
    }

    public byte[] getResultBuffer() {
        return resultBuffer;
    }

    public Boolean getResultVerify() {
        return resultVerify;
    }

    // Setters

    protected EBAsyncCryptoEvent setClientObject(EBClientObjectAsync clientObject) {
        this.clientObject = new WeakReference<EBClientObjectAsync>(clientObject);
        return this;
    }

    protected EBAsyncCryptoEvent setDiscriminator(Object discriminator) {
        this.discriminator = discriminator;
        return this;
    }

    protected EBAsyncCryptoEvent setResultBuffer(byte[] resultBuffer) {
        this.resultBuffer = resultBuffer;
        return this;
    }

    protected EBAsyncCryptoEvent setResultVerify(Boolean resultVerify) {
        this.resultVerify = resultVerify;
        return this;
    }
}
