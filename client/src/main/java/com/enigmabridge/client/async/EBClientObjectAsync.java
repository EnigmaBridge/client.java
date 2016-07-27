package com.enigmabridge.client.async;

import java.util.concurrent.*;

/**
 * Async client object supports incremental update() call and preserves the order of commands.
 * Only one submitted task is running at the time.
 *
 * Created by dusanklinec on 26.07.16.
 */
public class EBClientObjectAsync extends EBClientObjectAsyncSimple {
    /**
     * Queue of async task, enqueued for processing in executor from client.
     */
    protected final ConcurrentLinkedQueue<ObjectTask> jobQueue = new ConcurrentLinkedQueue<ObjectTask>();

    /**
     * Flag indicating if settings can be changed to the object.
     * It is only after initialization OR after doFinal() which resets the state back to init.
     */
    protected boolean clearForSettingsChange = true;

    // TODO: INIT.

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelReturn = false;
        if (future != null){
            cancelReturn = future.cancel(mayInterruptIfRunning);
        }

        return cancelReturn;
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public Object get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    // Async interface for data processing.

    public synchronized void update(byte[] buffer, int offset, int length){
        clearForSettingsChange = false;
        final ObjectTaskUpdate task = new ObjectTaskUpdate(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();
    }

    public synchronized void processData(byte[] buffer, int offset, int length){
        final ObjectTaskDoFinal task = new ObjectTaskDoFinal(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();

        clearForSettingsChange = true;
    }

    public synchronized void verify(byte[] buffer, int offset, int length) {
        final ObjectTaskVerify task = new ObjectTaskVerify(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();

        clearForSettingsChange = true;
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
        final ObjectTask polledTask = jobQueue.poll();
        if (!polledTask.equals(task)){
            throw new IllegalStateException("Finished task was not on the top of the queue, should not happen");
        }

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

    /**
     * Removes tasks from the current job. If the current task fails, all next updates & final need to me removed
     * from the queue.
     *
     * Examples of the queue transformation:
     *
     * update, update, update, final, update -> update
     * final, update -> update
     */
    protected synchronized void removeRemainingTasks(){
        // TODO:
    }

    // Getters

    protected ConcurrentLinkedQueue<ObjectTask> getJobQueue() {
        return jobQueue;
    }
}
