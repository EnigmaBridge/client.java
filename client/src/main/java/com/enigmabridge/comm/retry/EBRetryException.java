package com.enigmabridge.comm.retry;

/**
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryException extends Exception {
    public EBRetryException() {
    }

    public EBRetryException(String message) {
        super(message);
    }

    public EBRetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBRetryException(Throwable cause) {
        super(cause);
    }
}
