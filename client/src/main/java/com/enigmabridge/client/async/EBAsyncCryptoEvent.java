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
    protected WeakReference<EBClientObjectAsyncSimple> clientObject;

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

    public EBAsyncCryptoEvent(EBClientObjectAsyncSimple clientObject) {
        this.clientObject = new WeakReference<EBClientObjectAsyncSimple>(clientObject);
    }

    public EBAsyncCryptoEvent(EBClientObjectAsyncSimple clientObject, Object discriminator) {
        this.clientObject = new WeakReference<EBClientObjectAsyncSimple>(clientObject);
        this.discriminator = discriminator;
    }

    public EBAsyncCryptoEvent(EBClientObjectAsyncSimple clientObject, Object discriminator, byte[] resultBuffer) {
        this.clientObject = new WeakReference<EBClientObjectAsyncSimple>(clientObject);
        this.discriminator = discriminator;
        this.resultBuffer = resultBuffer;
    }

    public EBAsyncCryptoEvent(EBClientObjectAsyncSimple clientObject, Object discriminator, Boolean resultVerify) {
        this.clientObject = new WeakReference<EBClientObjectAsyncSimple>(clientObject);
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

    public EBClientObjectAsyncSimple getClientObject() {
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

    protected EBAsyncCryptoEvent setClientObject(EBClientObjectAsyncSimple clientObject) {
        this.clientObject = new WeakReference<EBClientObjectAsyncSimple>(clientObject);
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
