package com.enigmabridge;

import com.enigmabridge.utils.FieldWrapper;
import org.hjson.JsonValue;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.util.Collection;

/**
 * Misc EB utils, general purpose.
 * Created by dusanklinec on 27.04.16.
 */
public class EBUtils {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String sanitizeHexString(String hex, boolean removeWhitespaces){
        if (hex == null){
            return null;
        }

        if (removeWhitespaces){
            hex = hex.replaceAll("\\s","");
        }

        if ((hex.length() & 1) == 1){
            hex = "0" + hex;
        }

        return hex;
    }

    public static byte[] hex2byte(String hex){
        return DatatypeConverter.parseHexBinary(sanitizeHexString(hex, false));
    }

    public static byte[] hex2byte(String hex, boolean removeWhitespaces){
        return DatatypeConverter.parseHexBinary(sanitizeHexString(hex, removeWhitespaces));
    }

    public static String byte2hex(byte[] bytes){
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static String byte2hexNullable(byte[] bytes){
        if (bytes == null){
            return "";
        }

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

    public static String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }

    /**
     * Tries to extract json parameter as an integer.
     * @param json target
     * @param key field name
     * @return extracted boolean
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
     * @param json target
     * @param key field name
     * @return extracted string
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
     * @param json target
     * @param key field name
     * @return extracted string
     * @throws JSONException - if the JSON object doesn't contain the item or is malformed
     */
    public static String tryGetAsString(JSONObject json, String key) throws JSONException {
        return json.getString(key);
    }

    /**
     * Tries to extract json parameter as an integer.
     * @param json target
     * @param key field name
     * @param radix radix for string / int conversion
     * @return extracted integer
     * @throws JSONException - if the JSON object doesn't contain the item or is malformed
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
     * @param json target
     * @param key field name
     * @param radix radix for string / int conversion
     * @return extracted long
     * @throws JSONException - if the JSON object doesn't contain the item or is malformed
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

    public static JSONObject mergeInto(JSONObject src, JSONObject dst){
        if (src == null){
            throw new NullPointerException("Source cannot be null");
        } else if (dst == null || dst.length() == 0){
            return src;
        }

        for(String key : JSONObject.getNames(dst)) {
            src.put(key, dst.get(key));
        }

        return src;
    }

    public static JSONObject mergeObjects(JSONObject a, JSONObject b){
        if (a == null && b != null){
            return b;
        } else if (a != null && b == null){
            return a;
        } else if (a == null && b == null){
            return null;
        }

        final JSONObject merged = new JSONObject(a, JSONObject.getNames(a));
        for(String key : JSONObject.getNames(b)) {
            merged.put(key, b.get(key));
        }

        return merged;
    }

    public static String absorbSettingIfSet(JSONObject obj, String key, String defaultValue){
        if (obj == null || !obj.has(key)){
            return defaultValue;
        }

        return obj.getString(key);
    }

    public static int absorbSettingIfSet(JSONObject obj, String key, int defaultValue){
        if (obj == null || !obj.has(key)){
            return defaultValue;
        }

        return obj.getInt(key);
    }


    public static long absorbSettingIfSet(JSONObject obj, String key, long defaultValue){
        if (obj == null || !obj.has(key)){
            return defaultValue;
        }

        return obj.getLong(key);
    }

    public static boolean absorbFieldValue(JSONObject obj, String key, FieldWrapper<Object> wrapper){
        if (obj == null || !obj.has(key)){
            return false;
        }

        wrapper.setValue(obj.get(key));
        return true;
    }

    public static boolean absorbStringFieldValue(JSONObject obj, String key, FieldWrapper<String> wrapper){
        if (obj == null || !obj.has(key)){
            return false;
        }

        wrapper.setValue(obj.getString(key));
        return true;
    }

    public static boolean absorbIntFieldValue(JSONObject obj, String key, FieldWrapper<Integer> wrapper){
        if (obj == null || !obj.has(key)){
            return false;
        }

        wrapper.setValue(obj.getInt(key));
        return true;
    }

    public static boolean absorbLongFieldValue(JSONObject obj, String key, FieldWrapper<Long> wrapper){
        if (obj == null || !obj.has(key)){
            return false;
        }

        wrapper.setValue(obj.getLong(key));
        return true;
    }

    public static byte[] concatByteArrays(Collection<byte[]> bytes){
        if (bytes.size() == 0)
            return null;
        if (bytes.size() == 1)
            return bytes.iterator().next();

        int resultSize = 0;
        for(byte[] sub : bytes){
            resultSize += sub.length;
        }

        int offset = 0;
        final byte[] result = new byte[resultSize];
        for(byte[] sub : bytes){
            System.arraycopy(sub, 0, result, offset, sub.length);
            offset += sub.length;
        }

        return result;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static JSONObject parseJSON(String json){
        return new JSONObject(JsonValue.readHjson(json).toString());
    }
}
