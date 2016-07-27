package com.enigmabridge.client.async;

/**
 * Event used when any of the crypto API fails.
 *
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventFail extends EBAsyncCryptoEvent {
    /**
     * Exception caused the failure.
     */
    protected Exception exception;

    public EBAsyncCryptoEventFail() {
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsyncSimple client) {
        super(client);
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsyncSimple client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventFail(Exception exception) {
        this.exception = exception;
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsyncSimple client, Exception exception) {
        super(client);
        this.exception = exception;
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsyncSimple client, Object discriminator, Exception exception) {
        super(client, discriminator);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
