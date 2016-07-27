package com.enigmabridge.client.async;

/**
 * Basic implementation of the listener interface. Has empty logic.
 * User can anonymously extend this class to get simpler code.
 *
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoListenerBase implements EBAsyncCryptoListener {
    @Override
    public void onUpdateSuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventUpdate evt) {

    }

    @Override
    public void onDoFinalSuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventDoFinal evt) {

    }

    @Override
    public void onVerifySuccess(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventVerify evt) {

    }

    @Override
    public void onFail(EBClientObjectAsyncSimple clientObject, EBAsyncCryptoEventFail evt) {

    }
}
