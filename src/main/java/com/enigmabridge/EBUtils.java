package com.enigmabridge;

import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;

/**
 * Misc EB utils, general purpose.
 * Created by dusanklinec on 27.04.16.
 */
public class EBUtils {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static byte[] hex2byte(String hex){
        return DatatypeConverter.parseHexBinary(hex);
    }

    public static byte[] hex2byte(String hex, boolean removeWhitespaces){
        return DatatypeConverter.parseHexBinary(removeWhitespaces ? hex.replaceAll("\\s","") : hex);
    }

    public static String byte2hex(byte[] bytes){
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static String byte2hex(byte[] bytes, int offset, int length){
        StringBuilder r = new StringBuilder((length-offset) * 2);
        for (int i = offset; i < offset+length; i++){
            byte b = bytes[i];
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    /**
     * Tries to extract json parameter as an integer.
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static Boolean tryGetAsBoolean(JSONObject json, String key) throws JSONException {
        final Object obj = json.get(key);
        if (obj == null){
            return null;
        }

        if(!obj.equals(Boolean.FALSE) && (!(obj instanceof String) || !((String)obj).equalsIgnoreCase("false"))) {
            if(!obj.equals(Boolean.TRUE) && (!(obj instanceof String) || !((String)obj).equalsIgnoreCase("true"))) {
                final Integer asInt = tryGetAsInteger(json, key, 10);
                if (asInt == null){
                    return null;
                }

                return asInt!=0;

            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Tries to extract json parameter as a string.
     * If parameter is not present or is not a string, null is returned.
     *
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static String getAsStringOrNull(JSONObject json, String key) {
        if (!json.has(key)){
            return null;
        }

        try {
            return json.getString(key);
        } catch(JSONException e){
            return null;
        }
    }

    /**
     * Tries to extract json parameter as an string.
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static String tryGetAsString(JSONObject json, String key) throws JSONException {
        return json.getString(key);
    }

    /**
     * Tries to extract json parameter as an integer.
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static Integer tryGetAsInteger(JSONObject json, String key, int radix) throws JSONException {
        final Object obj = json.get(key);

        if (obj instanceof String){
            try {
                return Integer.parseInt((String) obj, radix);
            } catch(Exception e){
                return null;
            }
        }

        try {
            return obj instanceof Number ? ((Number) obj).intValue() : (int) json.getDouble(key);
        } catch(Exception e){
            return null;
        }
    }

    /**
     * Tries to extract json parameter as a long.
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static Long tryGetAsLong(JSONObject json, String key, int radix) throws JSONException {
        final Object obj = json.get(key);

        if (obj instanceof String){
            try {
                return Long.parseLong((String) obj, radix);
            } catch(Exception e){
                return null;
            }
        }

        try {
            return obj instanceof Number ? ((Number) obj).longValue() : (long) json.getDouble(key);
        } catch(Exception e){
            return null;
        }
    }

    public static long getAsLong(JSONObject json, String key, int radix) throws JSONException {
        final Long toret = tryGetAsLong(json, key, radix);
        if (toret == null) {
            throw new JSONException("JSONObject[" + key + "] not found.");
        }

        return toret;
    }

    public static int getAsInteger(JSONObject json, String key, int radix) throws JSONException {
        final Integer toret = tryGetAsInteger(json, key, radix);
        if (toret == null) {
            throw new JSONException("JSONObject[" + key + "] not found.");
        }

        return toret;
    }

    public static boolean getAsBoolean(JSONObject json, String key) throws JSONException {
        final Boolean toret = tryGetAsBoolean(json, key);
        if (toret == null) {
            throw new JSONException("JSONObject[" + key + "] not found.");
        }

        return toret;
    }
}
