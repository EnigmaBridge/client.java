package com.enigmabridge;

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
}
