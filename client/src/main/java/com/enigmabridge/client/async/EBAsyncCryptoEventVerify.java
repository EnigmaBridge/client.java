package com.enigmabridge.client.async;

/**
 * Event used when verify() call returns successfully.
 *
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventVerify extends EBAsyncCryptoEvent {
    public EBAsyncCryptoEventVerify() {
    }

    public EBAsyncCryptoEventVerify(EBClientObjectAsyncSimple client) {
        super(client);
    }

    public EBAsyncCryptoEventVerify(EBClientObjectAsyncSimple client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventVerify(EBClientObjectAsyncSimple client, Object discriminator, Boolean resultVerify) {
        super(client, discriminator, resultVerify);
    }

    public EBAsyncCryptoEventVerify(Boolean resultVerify) {
        super(resultVerify);
    }
}
