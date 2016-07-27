package com.enigmabridge.client.async;

import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * Async client object supports incremental update() call and preserves the order of commands.
 * Only one submitted task is running at the time.
 *
 * Created by dusanklinec on 26.07.16.
 */
public class EBClientObjectAsync extends EBClientObjectAsyncSimple implements Future<EBAsyncCryptoEvent> {
    /**
     * Queue of async task, enqueued for processing in executor from client.
     */
    protected final ConcurrentLinkedQueue<ObjectTask> jobQueue = new ConcurrentLinkedQueue<ObjectTask>();

    /**
     * Flag indicating if settings can be changed to the object.
     * It is only after initialization OR after doFinal() which resets the state back to init.
     */
    protected boolean clearForSettingsChange = true;

    /**
     * Cancellation for async task.
     */
    protected volatile boolean cancelled = false;

    /**
     * Cancels all running jobs, the whole queue.
     * After cancellation no more jobs can be accepted as it would leave us in the possible dangerous state
     * w.r.t. race conditions (cancel, add new task, callback from old task, callback from task after cancellation)
     *
     * @param mayInterruptIfRunning {@see Future.cancel()}
     * @return true/false
     */
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelReturn = false;

        // If there is some running / enqueued task, cancel it.
        if (future != null){
            cancelReturn = future.cancel(mayInterruptIfRunning);
        }

        // One way switch. After cancel no more tasks can be submitted.
        cancelled = true;

        // Copy of the worker queue, empty the queue.
        final LinkedList<ObjectTask> queueCopy = new LinkedList<ObjectTask>(jobQueue);
        jobQueue.clear();

        // Notify listeners about fail - cancelled.
        for(ObjectTask task : queueCopy) {
            for (EBAsyncCryptoListener listener : listeners) {
                listener.onFail(this, new EBAsyncCryptoEventFail(this, task.getDiscriminator(), new EBAsyncCancelledException()));
            }
        }

        return cancelReturn;
    }

    /**
     * isCancelled => no more tasks can be submitted, everything got aborted.
     *
     * @return true if jobs were cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * isDone == there are no more jobs in the queue.
     *
     * @return true if no more jobs
     */
    public boolean isDone() {
        return jobQueue.isEmpty();
    }

    /**
     * Waits until the whole queue is processed
     *
     * @return last event
     * @throws InterruptedException waiting thread was interrupted
     * @throws ExecutionException job aborted by throwing an exception
     */
    public EBAsyncCryptoEvent get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, TimeUnit.DAYS);
        }catch (TimeoutException e){
            // Should not happen
            return null;
        }
    }

    /**
     * Waits until the whole job queue gets processed.
     * Returns the last processed event.
     *
     * @param timeout timeout value
     * @param unit timeout TimeUnit
     * @return last event
     * @throws InterruptedException waiting thread was interrupted
     * @throws ExecutionException job aborted by throwing an exception
     * @throws TimeoutException given waiting time value was reached, job was not finished till then
     */
    public EBAsyncCryptoEvent get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final long milliWait = unit.toMillis(timeout);
        final long milliStart = System.currentTimeMillis();
        while(!this.jobQueue.isEmpty()){
            Thread.sleep(1);
            if (milliWait >= 0 && (milliStart + milliWait) < System.currentTimeMillis()){
                throw new TimeoutException();
            }
        }

        return getLastEvent();
    }

    // Async interface for data processing.

    /**
     * Incremental API for crypto operation.
     *
     * @param buffer buffer to process
     * @return future
     */
    public synchronized Future<EBAsyncCryptoEvent> update(byte[] buffer){
        return update(buffer, 0, buffer != null ? buffer.length : 0);
    }

    /**
     * Incremental API for crypto operation.
     *
     * @param buffer buffer to process
     * @param offset offset where to start the processing
     * @param length number of bytes to process in buffer starting at offset
     * @return future
     */
    public synchronized Future<EBAsyncCryptoEvent> update(byte[] buffer, int offset, int length){
        if (cancelled){
            throw new RuntimeException("Async cancelled");
        }

        clearForSettingsChange = false;
        final ObjectTaskUpdate task = new ObjectTaskUpdate(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();
        return this;
    }

    public synchronized Future<EBAsyncCryptoEvent> doFinal(byte[] buffer, int offset, int length){
        if (cancelled){
            throw new RuntimeException("Async cancelled");
        }

        final ObjectTaskDoFinal task = new ObjectTaskDoFinal(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();

        clearForSettingsChange = true;
        return this;
    }

    public synchronized Future<EBAsyncCryptoEvent> verify(byte[] buffer, int offset, int length) {
        if (cancelled){
            throw new RuntimeException("Async cancelled");
        }

        final ObjectTaskVerify task = new ObjectTaskVerify(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();

        clearForSettingsChange = true;
        return this;
    }

    // Task management.

    protected synchronized void checkQueue(){
        // Something is running, do nothing.
        if (currentlyRunningTask != null){
            return;
        }
        if (jobQueue.isEmpty()){
            return;
        }

        // Just peek the task. Will be removed when finished.
        final ObjectTask newTask = jobQueue.peek();
        currentlyRunningTask = newTask;
        future = client.getExecutorService().submit(newTask);
    }

    /**
     * Called by task when computation finishes.
     *
     * @param task task triggering the call
     * @param event result of the computation
     */
    protected synchronized void onTaskFinished(ObjectTask task, EBAsyncCryptoEvent event){
        final boolean wasFail = event instanceof EBAsyncCryptoEventFail;
        final boolean wasFinal = event instanceof EBAsyncCryptoEventDoFinal;
        final boolean wasUpdate = event instanceof EBAsyncCryptoEventUpdate;
        final boolean wasVerify = event instanceof EBAsyncCryptoEventVerify;

        // Remove the task from the queue. There is max 1 task running from the queue all the time.
        final ObjectTask polledTask = jobQueue.peek();
        if (polledTask == null || !polledTask.equals(task)){
            // Task was cancelled / removed. It was cancelled before.
            checkQueue();
            return;
        }

        lastEvent = event;
        if (wasFinal){
            lastFinalEvent = (EBAsyncCryptoEventDoFinal) event;
        }

        // Remove now, we have observers on the queue, so remove after lastX is set.
        jobQueue.poll();

        // If it was fail for update() remove all next updates and doFinals()
        if (wasFail && task instanceof ObjectTaskUpdate){
            boolean needCleaning = true;
            do{
                // Remove next task anyway. This one was update, the next one will be either update or finalize/verify.
                // Remove all tasks up to the last finalize/verify (inclusive).
                final ObjectTask nextTask = jobQueue.poll();
                needCleaning = nextTask instanceof ObjectTaskUpdate;
            } while(needCleaning);
        }

        // Trigger listeners.
        for (EBAsyncCryptoListener listener : listeners){
            if (wasFail){
                listener.onFail(this, (EBAsyncCryptoEventFail)event);
            } else if (wasUpdate){
                listener.onUpdateSuccess(this, (EBAsyncCryptoEventUpdate)event);
            } else if (wasFinal){
                listener.onDoFinalSuccess(this, (EBAsyncCryptoEventDoFinal)event);
            } else if (wasVerify){
                listener.onVerifySuccess(this, (EBAsyncCryptoEventVerify)event);
            } else {
                throw new IllegalStateException("Unrecognized event");
            }
        }

        // Check the queue again.
        checkQueue();
    }

    // Getters

    protected ConcurrentLinkedQueue<ObjectTask> getJobQueue() {
        return jobQueue;
    }
}
