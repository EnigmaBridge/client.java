package com.enigmabridge.comm.retry;

import com.enigmabridge.EBJSONSerializable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Very simple base retry implementation.
 * Ideally, new retry should be associated with the job. No reuse.
 *
 * Created by dusanklinec on 21.07.16.
 */
public class EBRetry<Result, Error> implements EBCallback<Result,Error>, EBJSONSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(EBRetry.class);
    protected static final String FIELD_STRATEGY_TYPE = "strategy";
    protected static final String FIELD_STRATEGY_DATA = "strategyConf";

    // Current number of attempts
    protected int attempts = 0;
    protected boolean logFails = false;

    // Retry strategy to use.
    protected EBRetryStrategy retryStrategy = new EBRetryStrategySimple(0);

    // All listeners for async runs
    protected final List<EBRetryListener<Result, Error>> listeners = new LinkedList<EBRetryListener<Result, Error>>();

    // Current job being executed
    protected EBRetryJob<Result, Error> job;

    // Did job already signalized success / fail?
    protected volatile boolean signalized = false;
    protected volatile boolean running = false;
    protected volatile boolean cancel = false;
    protected volatile boolean abort = false;

    // Milliseconds for waiting.
    protected volatile long waitingUntilMilli;

    // State from the last signalization
    protected boolean lastWasSuccess = false;
    protected boolean startedAsBlocking = false;
    protected Result lastResult;
    protected Error lastError;

    public EBRetry() {
    }

    public EBRetry(EBRetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    public EBRetry(int maxAttempts, EBRetryJob<Result, Error> job) {
        this.retryStrategy = new EBRetryStrategySimple(maxAttempts);
        this.job = job;
    }

    public EBRetry(EBRetryStrategy retryStrategy, EBRetryJob<Result, Error> job) {
        this.retryStrategy = retryStrategy;
        this.job = job;
    }

    public EBRetry(EBRetryJob<Result, Error> job) {
        this.job = job;
    }

    public EBFuture<Result, Error> runAsync(EBRetryJob<Result, Error> job){
        this.job = job;
        return runAsync();
    }

    /**
     * Runs the job asynchronously.
     */
    public EBFuture<Result, Error> runAsync(){
        startedAsBlocking = false;
        reset();
        runAsyncInternal();

        return new EBFuture<Result, Error>() {
            @Override
            public boolean isRunning() {
                return running;
            }

            @Override
            public boolean isDone() {
                return !running;
            }

            @Override
            public void cancel() {
                cancel = true;
            }

            @Override
            public void runNow() {
                waitingUntilMilli = 0;
            }
        };
    }

    protected void runAsyncInternal(){
        signalized = false;
        running = true;
        job.runAsync(this);
    }

    /**
     * Blocking version of the run.
     */
    public Result runSync() throws EBRetryException {
        reset();

        running = true;
        startedAsBlocking = true;
        for(int i = 0; retryStrategy.shouldContinue() && !cancel && !abort; i++){
            // Ask strategy if we are going to wait.
            final long waitMilli = retryStrategy.getWaitMilli();
            waitingUntilMilli = waitMilli >= 0 ? System.currentTimeMillis() + waitMilli : 0;

            // Signalize the job it is about to retry.
            // Job can read waiting until milli or adjust it.
            if (i > 0){
                job.onRetry(this);
            }

            // Wait. If waitingUntilMilli is reset or modified, no waiting is done here.
            while(!cancel && !abort && waitingUntilMilli > 0 && System.currentTimeMillis() < waitingUntilMilli){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new EBRetryFailedException("Interrupted", e);
                }
            }

            // Run the async job. Result will be signalized to the onSuccess, onFail callbacks on completion.
            if (!cancel && !abort) {
                runAsyncInternal();
            }

            // Wait until tasks signalizes the result.
            // If task is actually blocking, it signalizes result before returning control
            // thus no waiting is made.
            while(!signalized && !cancel && !abort){
                try {
                    Thread.sleep(0, 100);
                } catch (InterruptedException e) {
                    throw new EBRetryFailedException("Waiting interrupted", e);
                }
            }

            // Success? break. On fail, loop repeats.
            if (lastWasSuccess){
                break;
            }
        }

        if (abort){
            throw new EBRetryAbortedException(lastError, this);
        }
        if (cancel){
            throw new EBRetryCancelledException("Cancelled");
        }

        // If job is actually blocking, it signalized callback before exiting thus lets check if it is so.
        if (lastWasSuccess && !cancel){
            return lastResult;
        } else {
            throw new EBRetryFailedException(lastError, this);
        }
    }

    /**
     * Job calls this callback.
     * @param result result of the job
     */
    @Override
    public void onSuccess(Result result) {
        signalized = true;
        lastWasSuccess = true;
        lastResult = result;
        lastError = null;
        running = false;
        retryStrategy.onSuccess();
        notifyListenerSuccess(result);
    }

    /**
     * Job calls this callback.
     * @param error error causing the job to fail
     * @param abort if true abort the call.
     */
    @Override
    public void onFail(Error error, boolean abort) {
        signalized = true;
        lastWasSuccess = false;
        lastError = error;
        lastResult = null;
        attempts += 1;
        running = false;
        retryStrategy.onFail();

        if (abort){
            this.abort = true;
            notifyListenerFailed(error);
            return;
        }

        if (logFails){
            LOG.debug("Fail: " + error);
        } else {
            LOG.trace("Fail: " + error);
        }

        if (retryStrategy.shouldContinue() || cancel){
            notifyListenerFailed(error);

        } else if (!startedAsBlocking) {
            // Future-Extension: Notifier can be provided. NotifyMeIn(milli). for now - wait in separate thread.
            // Ask strategy if we are going to wait.
            final long waitMilli = retryStrategy.getWaitMilli();
            waitingUntilMilli = waitMilli >= 0 ? System.currentTimeMillis() + waitMilli : 0;

            // Signalize the job it is about to retry.
            // Job can read waiting until milli or adjust it.
            job.onRetry(this);

            // Start new waiting thread, signalizing onWaitFinished when done.
            new WaitThread(this).start();
        }
    }

    /**
     * Called in async run - when waiting was finished.
     */
    public void onWaitFinished() {
        if (cancel){
            notifyListenerFailed(null);
            return;
        }

        runAsyncInternal();
    }

    public int getAttempts() {
        return attempts;
    }

    public void reset(){
        attempts = 0;
        signalized = false;
        lastWasSuccess = false;
        lastResult = null;
        lastError = null;
        running = false;
        cancel = false;
        abort = false;
        retryStrategy.reset();
    }

    public void addListener(EBRetryListener<Result, Error> listener) {
        listeners.add(listener);
    }

    public void removeListener(EBRetryListener<Result, Error> listener) {
        listeners.remove(listener);
    }

    public void setJob(EBRetryJob<Result, Error> job) {
        this.job = job;
    }

    public long getWaitingUntilMilli() {
        return waitingUntilMilli;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public boolean isAborted() {
        return abort;
    }

    @Override
    public JSONObject toJSON(JSONObject json) {
        if (json == null){
            json = new JSONObject();
        }

        json.put(FIELD_STRATEGY_TYPE, retryStrategy.getName());
        json.put(FIELD_STRATEGY_DATA, retryStrategy.toJSON(null));
        return json;
    }

    public void fromJSON(JSONObject json){
        if (!json.has(FIELD_STRATEGY_TYPE)){
            retryStrategy = new EBRetryStrategySimple(0);
            return;
        }

        final String type = json.getString(FIELD_STRATEGY_TYPE);
        final JSONObject config = json.has(FIELD_STRATEGY_DATA) ? json.getJSONObject(FIELD_STRATEGY_DATA) : null;
        retryStrategy = EBRetryStrategyFactory.getByName(type, config);
    }

    public boolean isLogFails() {
        return logFails;
    }

    public void setLogFails(boolean logFails) {
        this.logFails = logFails;
    }

    protected void notifyListenerSuccess(Result result){
        for (EBRetryListener<Result, Error> listener : listeners) {
            listener.onSuccess(result, this);
        }
    }

    protected void notifyListenerFailed(Error error){
        for (EBRetryListener<Result, Error> listener : listeners) {
            listener.onFail(error, this);
        }
    }

    /**
     * Simple notify me in thread.
     */
    public static class WaitThread extends Thread {
        protected EBRetry retry;

        public WaitThread(EBRetry retry) {
            this.retry = retry;
        }

        @Override
        public void run() {
            // Wait. If waitingUntilMilli is reset or modified, no waiting is done here.
            while(!retry.isCancelled()
                    && retry.getWaitingUntilMilli() > 0
                    && System.currentTimeMillis() < retry.getWaitingUntilMilli())
            {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted", e);
                }
            }

            retry.onWaitFinished();
        }
    }
}
