package com.enigmabridge.comm.retry;

/**
 * Job error if error = throwable. Special case, although quite common.
 *
 * Created by dusanklinec on 25.07.16.
 */
public class EBRetryJobErrorThrowable extends EBRetryJobError<Throwable> {
    public EBRetryJobErrorThrowable(Throwable throwable) {
        super(throwable, throwable);
    }
}
