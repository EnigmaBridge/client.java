package com.enigmabridge.client.async;

import com.enigmabridge.client.EBClient;
import com.enigmabridge.client.wrappers.EBWrappedCombined;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.SignatureException;
import java.util.concurrent.*;

/**
 * Created by dusanklinec on 26.07.16.
 */
public class EBClientObjectAsync {
    /**
     * Client handle.
     */
    protected EBClient client;

    /**
     * Wrapped crypto object, synchronous.
     */
    protected EBWrappedCombined cryptoWrapper;

    /**
     * Future object representing this task in the executor.
     */
    protected Future future;
    protected final Object futureLock = new Object();

    /**
     * Queue of async task, enqueued for processing in executor from client.
     */
    protected final ConcurrentLinkedQueue<ObjectTask> jobQueue = new ConcurrentLinkedQueue<ObjectTask>();

    /**
     * Object assigned by caller to discriminate between independent doFinal() calls.
     * It is passed in the event back to the caller in the callback.
     * It can be changed only before first update() call or after fresh init / doFinal (resets state).
     */
    protected Object discriminator;

    /**
     * If true, doFinal returns also accumulated buffers from update() calls.
     */
    protected Boolean accumulative = null;

    /**
     * Flag indicating if settings can be changed to the object.
     * It is only after initialization OR after doFinal() which resets the state back to init.
     */
    protected boolean clearForSettingsChange = true;

    // TODO: INIT.

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
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

    // Bookkeeping

    public synchronized void addListener(EBClientListener listener){
        // TODO:
    }

    public synchronized void removeListener(EBClientListener listener){
        // TODO:
    }

    // Async interface for data processing.

    public synchronized void update(byte[] buffer){
        update(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized void update(byte[] buffer, int offset, int length){
        clearForSettingsChange = false;
        final ObjectTaskUpdate task = new ObjectTaskUpdate(this, discriminator, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();
    }

    public synchronized void doFinal(byte[] buffer){
        doFinal(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized void doFinal(byte[] buffer, int offset, int length){
        processData(buffer, offset, length);
    }

    public synchronized void processData(byte[] buffer){
        processData(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized void processData(byte[] buffer, int offset, int length){
        final ObjectTaskDoFinal task = new ObjectTaskDoFinal(this, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();

        clearForSettingsChange = true;
    }

    public synchronized void verify(byte[] buffer) {
        verify(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized void verify(byte[] buffer, int offset, int length) {
        final ObjectTaskVerify task = new ObjectTaskVerify(this, buffer, offset, length);
        jobQueue.add(task);
        checkQueue();

        clearForSettingsChange = true;
    }

    // Task management.

    protected synchronized void checkQueue(){
        // TODO: check queue, start new task if nothing is running
    }

    /**
     * Called by task when computation finishes.
     *
     * @param task task triggering the call
     * @param event result of the computation
     */
    protected synchronized void onTaskFinished(ObjectTask task, EBAsyncCryptoEvent event){
        // TODO: task finished, do something.
        final boolean wasFail = event instanceof EBAsyncCryptoEventFail;
        final boolean wasFinal = event instanceof EBAsyncCryptoEventDoFinal;
        final boolean wasUpdate = event instanceof EBAsyncCryptoEventUpdate;
    }

    /**
     * Represents current operation to be called.
     * update() / doFinal()
     */
    static abstract class ObjectTask implements Runnable {
        protected byte[] buffer;
        protected int offset;
        protected int length;

        protected Object discriminator;
        protected EBClientObjectAsync parent;

        public ObjectTask(EBClientObjectAsync parent, Object discriminator, byte[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            this.discriminator = discriminator;
            this.parent = parent;
        }

        public ObjectTask(EBClientObjectAsync parent, byte[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            this.parent = parent;
        }

        public void setDiscriminator(Object discriminator) {
            this.discriminator = discriminator;
        }

        @Override
        public void run() {
            // This code runs in service executor.
            // Do the process operation.
            try {
                final EBAsyncCryptoEvent event = process();

                // Handle the result to the client.
                parent.onTaskFinished(this, event);
            } catch(Exception e){
                parent.onTaskFinished(this, new EBAsyncCryptoEventFail(parent, discriminator, e));
            }
        }

        public abstract EBAsyncCryptoEvent process();
    }

    /**
     * Task used to call update() on sync crypto primitive.
     */
    static class ObjectTaskUpdate extends ObjectTask {
        public ObjectTaskUpdate(EBClientObjectAsync parent, byte[] buffer, int offset, int length) {
            super(parent, buffer, offset, length);
        }

        public ObjectTaskUpdate(EBClientObjectAsync parent, Object discriminator, byte[] buffer, int offset, int length) {
            super(parent, discriminator, buffer, offset, length);
        }

        @Override
        public EBAsyncCryptoEvent process() {
            try {
                final byte[] update = parent.getCryptoWrapper().update(buffer, offset, length);
                return new EBAsyncCryptoEventUpdate(parent, discriminator, update);

            } catch (SignatureException e) {
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            } catch (Exception e){
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            }
        }
    }

    /**
     * Task used to call doFinal() on sync crypto primitive.
     */
    static class ObjectTaskDoFinal extends ObjectTask {
        public ObjectTaskDoFinal(EBClientObjectAsync parent, byte[] buffer, int offset, int length) {
            super(parent, buffer, offset, length);
        }

        public ObjectTaskDoFinal(EBClientObjectAsync parent, Object discriminator, byte[] buffer, int offset, int length) {
            super(parent, discriminator, buffer, offset, length);
        }

        @Override
        public EBAsyncCryptoEvent process() {
            try {
                final byte[] update = parent.getCryptoWrapper().doFinal(buffer, offset, length);
                return new EBAsyncCryptoEventDoFinal(parent, discriminator, update);

            } catch (SignatureException e) {
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            } catch (IllegalBlockSizeException e) {
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            } catch (BadPaddingException e) {
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            } catch (Exception e){
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            }
        }
    }

    /**
     * Task used to call verify() on sync crypto primitive.
     */
    static class ObjectTaskVerify extends ObjectTask {
        public ObjectTaskVerify(EBClientObjectAsync parent, byte[] buffer, int offset, int length) {
            super(parent, buffer, offset, length);
        }

        public ObjectTaskVerify(EBClientObjectAsync parent, Object discriminator, byte[] buffer, int offset, int length) {
            super(parent, discriminator, buffer, offset, length);
        }

        @Override
        public EBAsyncCryptoEvent process() {
            try {
                final boolean update = parent.getCryptoWrapper().verify(buffer, offset, length);
                return new EBAsyncCryptoEventVerify(parent, discriminator, update);

            } catch (SignatureException e) {
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            } catch (Exception e){
                return new EBAsyncCryptoEventFail(parent, discriminator, e);
            }
        }
    }

    // Getters

    public EBClient getClient() {
        return client;
    }

    public EBWrappedCombined getCryptoWrapper() {
        return cryptoWrapper;
    }

    protected Future getFuture() {
        return future;
    }

    protected Object getFutureLock() {
        return futureLock;
    }

    protected ConcurrentLinkedQueue<ObjectTask> getJobQueue() {
        return jobQueue;
    }

    public Object getDiscriminator() {
        return discriminator;
    }
}
