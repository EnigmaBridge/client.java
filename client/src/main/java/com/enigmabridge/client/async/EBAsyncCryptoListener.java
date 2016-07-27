package com.enigmabridge.client.async;

/**
 * Listener for async crypto operations.
 * Caller should implement the callbacks, will be notified on async change.
 *
 * Created by dusanklinec on 27.07.16.
 */
public interface EBAsyncCryptoListener {
    void onUpdateSuccess(EBAsyncCryptoEventUpdate evt);
    void onDoFinalSuccess(EBAsyncCryptoEventDoFinal evt);
    void onVerifySuccess(EBAsyncCryptoEventVerify evt);
    void onFail(EBAsyncCryptoEventFail evt);
}
