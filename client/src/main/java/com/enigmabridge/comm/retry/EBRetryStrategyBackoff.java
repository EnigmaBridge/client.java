package com.enigmabridge.comm.retry;

import org.json.JSONObject;

/**
 * TODO: implement backoff strategy.
 *
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryStrategyBackoff extends EBRetryStrategySimple {
    public static final String NAME = "backoff";

    public EBRetryStrategyBackoff(int maxAttempts) {
        super(maxAttempts);
    }

    public EBRetryStrategyBackoff() {
    }

    public EBRetryStrategyBackoff(JSONObject json) {
        super(json);
    }

    @Override
    public EBRetryStrategy copy() {
        return super.copy();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
