package com.enigmabridge;

/**
 * Debugging and dev settings.
 *
 * Created by dusanklinec on 25.07.16.
 */
public class EBDevSettings {
    /**
     * If true createUO logs the request that caused the error.
     * Should be turned off in the production mode.
     *
     * @return true if failed UO logging should be enabled.
     */
    public static boolean shouldLogFailedCreateUO(){
        return true;
    }

    /**
     * If true processData call logs request and response that caused
     * 6f00 error.
     *
     * @return true if log request and response
     */
    public static boolean shouldLog6f00RequestResponse(){
        return true;
    }
}
