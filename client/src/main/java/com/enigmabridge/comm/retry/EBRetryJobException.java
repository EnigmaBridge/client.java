package com.enigmabridge.comm.retry;

/**
 * General exception used as a cause that job failed, if there is no other exception cause.
 * Used as an error cause with conjunction to EBRetryFailedException.
 *
 * Created by dusanklinec on 25.07.16.
 */
public class EBRetryJobException extends Exception {
    /**
     * Error object - result of the job that is interpreted as an error.
     */
    Object error;

    public EBRetryJobException() {
    }

    public EBRetryJobException(String message) {
        super(message);
    }

    public EBRetryJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBRetryJobException(Throwable cause) {
        super(cause);
    }

    public EBRetryJobException(Object error) {
        this.error = error;
    }

    public EBRetryJobException(String message, Object error) {
        super(message);
        this.error = error;
    }

    public EBRetryJobException(String message, Throwable cause, Object error) {
        super(message, cause);
        this.error = error;
    }

    public EBRetryJobException(Throwable cause, Object error) {
        super(cause);
        this.error = error;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
