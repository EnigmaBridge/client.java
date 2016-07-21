package com.enigmabridge.comm.retry;

/**
 * Created by dusanklinec on 21.07.16.
 */
public interface EBRetryListener<Result, Error> {
    /**
     * Called by job on success.
     * @param result
     */
    void onSuccess(Result result, EBRetry<Result, Error> retry);

    /**
     * Called by job on fail.
     * @param error
     */
    void onFail(Error error, EBRetry<Result, Error> retry);
}
