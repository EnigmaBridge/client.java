package com.enigmabridge.client.async;

/**
 * Event used when update() call returns successfully.
 *
 * Created by dusanklinec on 27.07.16.
 */
public class EBAsyncCryptoEventUpdate extends EBAsyncCryptoEvent {
    public EBAsyncCryptoEventUpdate() {
    }

    public EBAsyncCryptoEventUpdate(EBClientObjectAsyncSimple client) {
        super(client);
    }

    public EBAsyncCryptoEventUpdate(EBClientObjectAsyncSimple client, Object discriminator) {
        super(client, discriminator);
    }

    public EBAsyncCryptoEventUpdate(EBClientObjectAsyncSimple client, Object discriminator, byte[] resultBuffer) {
        super(client, discriminator, resultBuffer);
    }

    public EBAsyncCryptoEventUpdate(byte[] resultBuffer) {
        super(resultBuffer);
    }
}
