package com.enigmabridge.client.async;

import java.lang.ref.WeakReference;

/**
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventFail extends EBAsyncCryptoEvent {
    /**
     * Exception caused the failure.
     */
    protected Exception exception;

    public EBAsyncCryptoEventFail() {
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsync client) {
        super(client);
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsync client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventFail(Exception exception) {
        this.exception = exception;
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsync client, Exception exception) {
        super(client);
        this.exception = exception;
    }

    public EBAsyncCryptoEventFail(EBClientObjectAsync client, Object discriminator, Exception exception) {
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
