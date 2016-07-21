package com.enigmabridge.comm.retry;

/**
 * Exception thrown when calling sync job.
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryFailedException extends Exception {
    protected Object error;
    protected EBRetry retry;

    public EBRetryFailedException() {
    }

    public EBRetryFailedException(String message) {
        super(message);
    }

    public EBRetryFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBRetryFailedException(Throwable cause) {
        super(cause);
    }

    public EBRetryFailedException(Object error, EBRetry retry) {
        this.error = error;
        this.retry = retry;
    }

    public EBRetryFailedException(String message, Object error, EBRetry retry) {
        super(message);
        this.error = error;
        this.retry = retry;
    }

    public EBRetryFailedException(String message, Throwable cause, Object error, EBRetry retry) {
        super(message, cause);
        this.error = error;
        this.retry = retry;
    }

    public EBRetryFailedException(Throwable cause, Object error, EBRetry retry) {
        super(cause);
        this.error = error;
        this.retry = retry;
    }

    public Object getError() {
        return error;
    }

    public EBRetry getRetry() {
        return retry;
    }
}
