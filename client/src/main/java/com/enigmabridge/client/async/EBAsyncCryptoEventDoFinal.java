package com.enigmabridge.client.async;

import java.lang.ref.WeakReference;

/**
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventDoFinal extends EBAsyncCryptoEvent {
    protected byte[] accumulated;

    public byte[] getAccumulated() {
        return accumulated;
    }

    protected void setAccumulated(byte[] accumulated) {
        this.accumulated = accumulated;
    }

    public EBAsyncCryptoEventDoFinal() {
    }

    public EBAsyncCryptoEventDoFinal(EBClientObjectAsync client) {
        super(client);
    }

    public EBAsyncCryptoEventDoFinal(EBClientObjectAsync client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventDoFinal(EBClientObjectAsync client, Object discriminator, byte[] resultBuffer) {
        super(client, discriminator, resultBuffer);
    }

    public EBAsyncCryptoEventDoFinal(byte[] resultBuffer) {
        super(resultBuffer);
    }
}
