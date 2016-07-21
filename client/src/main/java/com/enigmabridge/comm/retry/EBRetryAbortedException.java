package com.enigmabridge.comm.retry;

/**
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryAbortedException extends EBRetryException {
    public EBRetryAbortedException() {
    }

    public EBRetryAbortedException(String message) {
        super(message);
    }

    public EBRetryAbortedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBRetryAbortedException(Throwable cause) {
        super(cause);
    }
}
