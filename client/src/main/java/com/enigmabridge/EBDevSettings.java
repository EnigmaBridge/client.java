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
}
