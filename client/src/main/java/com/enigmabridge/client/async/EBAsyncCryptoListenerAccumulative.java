package com.enigmabridge.client.async;

import com.enigmabridge.EBUtils;

import java.util.LinkedList;

/**
 * Listener accumulating onUpdate() calls to the accumulator buffer.
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoListenerAccumulative extends EBAsyncCryptoListenerBase {
    protected final LinkedList<byte[]> updates = new LinkedList<byte[]>();

    protected byte[] accumulated;

    @Override
    public void onUpdateSuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventUpdate evt) {
        super.onUpdateSuccess(clientObject, evt);
        if (evt == null){
            return;
        }

        final byte[] buffer = evt.getResultBuffer();
        if (buffer == null || buffer.length == 0){
            return;
        }

        updates.add(buffer);
    }

    @Override
    public void onDoFinalSuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventDoFinal evt) {
        super.onDoFinalSuccess(clientObject, evt);

        final byte[] buffer = evt.getResultBuffer();
        if (buffer != null && buffer.length > 0){
            updates.add(buffer);
        }

        accumulated = updates.size() > 0 ? EBUtils.concatByteArrays(updates) : null;
        evt.setAccumulated(accumulated);

        updates.clear();
    }

    @Override
    public void onVerifySuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventVerify evt) {
        super.onVerifySuccess(clientObject, evt);
        updates.clear();
    }

    @Override
    public void onFail(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventFail evt) {
        super.onFail(clientObject, evt);
        updates.clear();
    }

    public byte[] getAccumulated() {
        return accumulated;
    }
}
