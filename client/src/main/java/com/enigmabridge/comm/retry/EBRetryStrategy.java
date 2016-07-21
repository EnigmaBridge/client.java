package com.enigmabridge.comm.retry;

import com.enigmabridge.EBJSONSerializable;

/**
 * EB retry strategy interface.
 *
 * Created by dusanklinec on 21.07.16.
 */
public interface EBRetryStrategy extends EBJSONSerializable {
    String getName();

    void onFail();
    void onSuccess();
    void reset();
    boolean shouldContinue();
    long getWaitMilli();

    EBRetryStrategy copy();
}
