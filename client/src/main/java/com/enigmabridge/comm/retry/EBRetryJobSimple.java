package com.enigmabridge.comm.retry;

/**
 * Created by dusanklinec on 21.07.16.
 */
public abstract class EBRetryJobSimple<Result, Error> implements EBRetryJob<Result, Error> {
    /**
     * Retry manager.
     */
    protected EBRetry<Result, Error> retry;

    public EBRetryJobSimple() {
    }

    public EBRetryJobSimple(EBRetry<Result, Error> retry) {
        this.retry = retry;
    }

    @Override
    public void onRetry(EBRetry<Result, Error> retry) {
        // Nothing to do.
    }

    public EBRetry<Result, Error> getRetry() {
        return retry;
    }
}
