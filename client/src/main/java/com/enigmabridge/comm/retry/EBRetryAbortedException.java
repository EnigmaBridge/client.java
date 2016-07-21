package com.enigmabridge.comm.retry;

/**
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryAbortedException extends EBRetryException {
    protected Object error;
    protected EBRetry retry;

    public EBRetryAbortedException(Object error, EBRetry retry) {
        this.error = error;
        this.retry = retry;
    }

    public EBRetryAbortedException(String message, Object error, EBRetry retry) {
        super(message);
        this.error = error;
        this.retry = retry;
    }

    public EBRetryAbortedException(String message, Throwable cause, Object error, EBRetry retry) {
        super(message, cause);
        this.error = error;
        this.retry = retry;
    }

    public EBRetryAbortedException(Throwable cause, Object error, EBRetry retry) {
        super(cause);
        this.error = error;
        this.retry = retry;
    }

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

    public Object getError() {
        return error;
    }

    public EBRetry getRetry() {
        return retry;
    }
}
