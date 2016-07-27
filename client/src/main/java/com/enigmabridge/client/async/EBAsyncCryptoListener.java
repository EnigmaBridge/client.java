package com.enigmabridge.client.async;

/**
 * Listener for async crypto operations.
 * Caller should implement the callbacks, will be notified on async change.
 *
 * Created by dusanklinec on 27.07.16.
 */
public interface EBAsyncCryptoListener {
    void onUpdateSuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventUpdate evt);
    void onDoFinalSuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventDoFinal evt);
    void onVerifySuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventVerify evt);
    void onFail(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventFail evt);
}
