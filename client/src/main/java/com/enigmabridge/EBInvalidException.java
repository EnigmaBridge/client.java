package com.enigmabridge;

/**
 * Created by dusanklinec on 30.06.16.
 */
public class EBInvalidException extends EBEngineException {
    public EBInvalidException() {
    }

    public EBInvalidException(String message) {
        super(message);
    }

    public EBInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBInvalidException(Throwable cause) {
        super(cause);
    }

    public EBInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
