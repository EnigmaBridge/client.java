package com.enigmabridge.comm.retry;

/**
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryCancelledException extends EBRetryException {
    public EBRetryCancelledException() {
    }

    public EBRetryCancelledException(String message) {
        super(message);
    }

    public EBRetryCancelledException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBRetryCancelledException(Throwable cause) {
        super(cause);
    }
}
