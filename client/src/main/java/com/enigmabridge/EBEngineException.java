package com.enigmabridge;

/**
 * Created by dusanklinec on 26.04.16.
 */
public class EBEngineException extends RuntimeException {
    public EBEngineException() {
    }

    public EBEngineException(String message) {
        super(message);
    }

    public EBEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBEngineException(Throwable cause) {
        super(cause);
    }

    public EBEngineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
