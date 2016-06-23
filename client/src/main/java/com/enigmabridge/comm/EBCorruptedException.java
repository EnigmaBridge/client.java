package com.enigmabridge.comm;

import com.enigmabridge.EBException;

/**
 * Created by dusanklinec on 28.04.16.
 */
public class EBCorruptedException extends EBException {
    public EBCorruptedException() {
    }

    public EBCorruptedException(String message) {
        super(message);
    }

    public EBCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBCorruptedException(Throwable cause) {
        super(cause);
    }

    public EBCorruptedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
