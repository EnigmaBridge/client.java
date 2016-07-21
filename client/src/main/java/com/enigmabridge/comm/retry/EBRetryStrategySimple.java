package com.enigmabridge.comm.retry;

import com.enigmabridge.EBUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple retry strategy, with threshold, no waiting.
 *
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetryStrategySimple implements EBRetryStrategy {
    protected int maxAttempts;
    protected int attempts = 0;

    public static final String NAME = "simple";
    protected static final String FIELD_MAX_ATTEMPTS = "maxAttempts";

    public EBRetryStrategySimple(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public EBRetryStrategySimple() {
    }

    public EBRetryStrategySimple(JSONObject json) {
        fromJSON(json);
    }

    @Override
    public void onFail() {
        attempts += 1;
    }

    @Override
    public void onSuccess() {
        //
    }

    @Override
    public void reset() {
        attempts = 0;
    }

    @Override
    public boolean shouldContinue() {
        return maxAttempts < 0 || attempts < maxAttempts;
    }

    @Override
    public long getWaitMilli() {
        return -1;
    }

    @Override
    public JSONObject toJSON(JSONObject json) {
        if (json == null){
            json = new JSONObject();
        }

        json.put(FIELD_MAX_ATTEMPTS, maxAttempts);
        return json;
    }

    protected void fromJSON(JSONObject json) throws JSONException {
        if (json == null){
            return;
        }

        if (json.has(FIELD_MAX_ATTEMPTS)){
            maxAttempts = EBUtils.getAsInteger(json, FIELD_MAX_ATTEMPTS, 10);
        }
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
