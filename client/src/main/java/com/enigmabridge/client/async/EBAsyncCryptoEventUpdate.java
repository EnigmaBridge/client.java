package com.enigmabridge.client.async;

import java.lang.ref.WeakReference;

/**
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventUpdate extends EBAsyncCryptoEvent {
    public EBAsyncCryptoEventUpdate() {
    }

    public EBAsyncCryptoEventUpdate(EBClientObjectAsync client) {
        super(client);
    }

    public EBAsyncCryptoEventUpdate(EBClientObjectAsync client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventUpdate(EBClientObjectAsync client, Object discriminator, byte[] resultBuffer) {
        super(client, discriminator, resultBuffer);
    }

    public EBAsyncCryptoEventUpdate(byte[] resultBuffer) {
        super(resultBuffer);
    }
}
