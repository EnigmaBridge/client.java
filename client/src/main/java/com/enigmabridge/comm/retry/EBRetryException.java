package com.enigmabridge.comm.retry;

/**
 * Root exception for all retry based exceptions.
 *
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryException extends Exception {
    /**
     * Error indicated in onFail() notify.
     * Mostly it is an exception that caused job to fail.
     */
    protected Object error;

    /**
     * Retry mechanism throwing this exception.
     */
    protected EBRetry retry;

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

    public EBRetryException(Object error, EBRetry retry) {
        this.error = error;
        this.retry = retry;
    }

    public EBRetryException(String message, Object error, EBRetry retry) {
        super(message);
        this.error = error;
        this.retry = retry;
    }

    public EBRetryException(String message, Throwable cause, Object error, EBRetry retry) {
        super(message, cause);
        this.error = error;
        this.retry = retry;
    }

    public EBRetryException(Throwable cause, Object error, EBRetry retry) {
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

    public void setError(Object error) {
        this.error = error;
    }

    public void setRetry(EBRetry retry) {
        this.retry = retry;
    }
}
