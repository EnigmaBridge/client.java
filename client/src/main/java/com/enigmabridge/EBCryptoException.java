package com.enigmabridge;

/**
 * Created by dusanklinec on 27.04.16.
 */
public class EBCryptoException extends EBEngineException {
    public EBCryptoException() {
    }

    public EBCryptoException(String message) {
        super(message);
    }

    public EBCryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBCryptoException(Throwable cause) {
        super(cause);
    }

    public EBCryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
