package com.enigmabridge.client.async;

/**
 * Used when async crypto task was cancelled.
 *
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCancelledException extends Exception {
    public EBAsyncCancelledException() {
    }

    public EBAsyncCancelledException(String message) {
        super(message);
    }

    public EBAsyncCancelledException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBAsyncCancelledException(Throwable cause) {
        super(cause);
    }
}
