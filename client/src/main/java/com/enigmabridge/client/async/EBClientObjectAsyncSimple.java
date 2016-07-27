package com.enigmabridge.client.async;

import com.enigmabridge.EBEngine;
import com.enigmabridge.client.EBClient;
import com.enigmabridge.client.wrappers.EBWrappedCombined;
import com.enigmabridge.comm.EBConnectionSettings;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.SignatureException;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * Simple async object, does not support incremental updates.
 * Very simple logic, no internal queueing.
 * doFinal requests are not guaranteed to return in the order they were accepted.
 *
 * Created by dusanklinec on 27.07.16.
 */
public class EBClientObjectAsyncSimple {
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
    protected Future<EBAsyncCryptoEvent> future;
    protected EBClientObjectAsync.ObjectTask currentlyRunningTask;

    /**
     * Listeners for async job completion.
     */
    protected final LinkedList<EBAsyncCryptoListener> listeners = new LinkedList<EBAsyncCryptoListener>();

    /**
     * Object assigned by caller to discriminate between independent doFinal() calls.
     * It is passed in the event back to the caller in the callback.
     * It can be changed only before first update() call or after fresh init / doFinal (resets state).
     */
    protected Object discriminator;

    /**
     * The last final event produced.
     */
    protected volatile EBAsyncCryptoEventDoFinal lastFinalEvent;
    protected volatile EBAsyncCryptoEvent lastEvent;

    public static abstract class AbstractBuilder<T extends EBClientObjectAsyncSimple, B extends AbstractBuilder> {
        public B setClient(EBClient client){
            getObj().client = client;
            return getThisBuilder();
        }

        public B setCryptoWrapper(EBWrappedCombined wrapper){
            getObj().cryptoWrapper = wrapper;
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBClientObjectAsyncSimple, EBClientObjectAsyncSimple.Builder> {
        private final EBClientObjectAsyncSimple child = new EBClientObjectAsyncSimple();

        @Override
        public EBClientObjectAsyncSimple getObj() {
            return child;
        }

        @Override
        public EBClientObjectAsyncSimple build() {
            return child;
        }

        @Override
        public EBClientObjectAsyncSimple.Builder getThisBuilder() {
            return this;
        }
    }

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelReturn = false;
        if (future != null){
            cancelReturn = future.cancel(mayInterruptIfRunning);
        }

        return cancelReturn;
    }

    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    public boolean isDone() {
        return future != null && future.isDone();
    }

    public EBAsyncCryptoEvent get() throws InterruptedException, ExecutionException {
        if (future != null){
            return future.get();
        }

        return null;
    }

    public EBAsyncCryptoEvent get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (future != null) {
            return future.get(timeout, unit);
        }

        return null;
    }

    // Bookkeeping

    public synchronized void addListener(EBAsyncCryptoListener listener){
        listeners.add(listener);
    }

    public synchronized void removeListener(EBAsyncCryptoListener listener){
        listeners.remove(listener);
    }

    // Async interface for data processing.

    public synchronized Future<EBAsyncCryptoEvent> doFinal(byte[] buffer){
        return doFinal(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized Future<EBAsyncCryptoEvent> doFinal(byte[] buffer, int offset, int length){
        currentlyRunningTask = new EBClientObjectAsync.ObjectTaskDoFinal(this, discriminator, buffer, offset, length);
        future = client.getExecutorService().submit(currentlyRunningTask);
        return future;
    }

    public synchronized Future<EBAsyncCryptoEvent> processData(byte[] buffer){
        return doFinal(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized Future<EBAsyncCryptoEvent> processData(byte[] buffer, int offset, int length){
        return doFinal(buffer, offset, length);
    }

    public synchronized Future<EBAsyncCryptoEvent> verify(byte[] buffer) {
        return verify(buffer, 0, buffer == null ? 0 : buffer.length);
    }

    public synchronized Future<EBAsyncCryptoEvent> verify(byte[] buffer, int offset, int length) {
        currentlyRunningTask = new EBClientObjectAsync.ObjectTaskVerify(this, discriminator, buffer, offset, length);
        future = client.getExecutorService().submit(currentlyRunningTask);
        return future;
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

        lastEvent = event;
        if (wasFinal){
            lastFinalEvent = (EBAsyncCryptoEventDoFinal) event;
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
    }

    /**
     * Represents current operation to be called.
     * update() / doFinal()
     */
    static abstract class ObjectTask implements Callable<EBAsyncCryptoEvent> {
        protected byte[] buffer;
        protected int offset;
        protected int length;

        protected Object discriminator;
        protected EBClientObjectAsyncSimple parent;

        public ObjectTask(EBClientObjectAsyncSimple parent, Object discriminator, byte[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            this.discriminator = discriminator;
            this.parent = parent;
        }

        public ObjectTask(EBClientObjectAsyncSimple parent, byte[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            this.parent = parent;
        }

        public void setDiscriminator(Object discriminator) {
            this.discriminator = discriminator;
        }

        public Object getDiscriminator() {
            return discriminator;
        }

        @Override
        public EBAsyncCryptoEvent call() throws Exception {
            // This code runs in service executor.
            // Do the process operation.
            try {
                final EBAsyncCryptoEvent event = process();

                // Handle the result to the client.
                parent.onTaskFinished(this, event);
                return event;

            } catch(Exception e){
                final EBAsyncCryptoEventFail failEvent = new EBAsyncCryptoEventFail(parent, discriminator, e);
                parent.onTaskFinished(this, failEvent);
                return failEvent;
            }
        }

        public abstract EBAsyncCryptoEvent process();
    }

    /**
     * Task used to call update() on sync crypto primitive.
     */
    static class ObjectTaskUpdate extends ObjectTask {
        public ObjectTaskUpdate(EBClientObjectAsyncSimple parent, byte[] buffer, int offset, int length) {
            super(parent, buffer, offset, length);
        }

        public ObjectTaskUpdate(EBClientObjectAsyncSimple parent, Object discriminator, byte[] buffer, int offset, int length) {
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
        public ObjectTaskDoFinal(EBClientObjectAsyncSimple parent, byte[] buffer, int offset, int length) {
            super(parent, buffer, offset, length);
        }

        public ObjectTaskDoFinal(EBClientObjectAsyncSimple parent, Object discriminator, byte[] buffer, int offset, int length) {
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
        public ObjectTaskVerify(EBClientObjectAsyncSimple parent, byte[] buffer, int offset, int length) {
            super(parent, buffer, offset, length);
        }

        public ObjectTaskVerify(EBClientObjectAsyncSimple parent, Object discriminator, byte[] buffer, int offset, int length) {
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

    public Object getDiscriminator() {
        return discriminator;
    }

    public EBAsyncCryptoEventDoFinal getLastFinalEvent() {
        return lastFinalEvent;
    }

    public EBAsyncCryptoEvent getLastEvent() {
        return lastEvent;
    }
}
