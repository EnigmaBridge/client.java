package com.enigmabridge.create;

/**
 * EB Create utils.
 *
 * Created by dusanklinec on 01.07.16.
 */
public class EBCreateUtils {

    /**
     * Generates UO handle.
     *
     * @param apiKey API KEY to access EB
     * @param uoId user object ID
     * @param uoType user object type - composite
     * @return UO handle string
     */
    public static String getUoHandle(String apiKey, long uoId, long uoType){
        // TEST_API 00 00000013 00 00a00004
        return String.format("%s00%08x00%08x", apiKey, uoId, uoType);
    }

    /**
     * Generates UO handle object from string.
     *
     * @param handleString handle string
     * @return handle object
     */
    public static EBUOHandle getHandleObj(String handleString){
        final int len = handleString.length();
        if (len < 19){
            throw new IllegalArgumentException("handle string is too short");
        }

        final String apiKey = handleString.substring(0, len-2-8-2-8);
        final String uoIdStr = handleString.substring(len-2-8-2-8, len-8-2);
        final String uoTypeStr = handleString.substring(len-8, len);

        return new EBUOHandle(
                apiKey,
                Long.parseLong(uoIdStr, 16),
                Long.parseLong(uoTypeStr, 16)
        );
    }


}
