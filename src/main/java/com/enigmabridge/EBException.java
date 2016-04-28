package com.enigmabridge;

/**
 * Created by dusanklinec on 28.04.16.
 */
public class EBException extends Exception {
    public EBException() {
    }

    public EBException(String message) {
        super(message);
    }

    public EBException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBException(Throwable cause) {
        super(cause);
    }

    public EBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
