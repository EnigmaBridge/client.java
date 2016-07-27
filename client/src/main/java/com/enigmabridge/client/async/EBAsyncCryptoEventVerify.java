package com.enigmabridge.client.async;

import java.lang.ref.WeakReference;

/**
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventVerify extends EBAsyncCryptoEvent {
    public EBAsyncCryptoEventVerify() {
    }

    public EBAsyncCryptoEventVerify(EBClientObjectAsync client) {
        super(client);
    }

    public EBAsyncCryptoEventVerify(EBClientObjectAsync client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventVerify(EBClientObjectAsync client, Object discriminator, Boolean resultVerify) {
        super(client, discriminator, resultVerify);
    }

    public EBAsyncCryptoEventVerify(Boolean resultVerify) {
        super(resultVerify);
    }
}
