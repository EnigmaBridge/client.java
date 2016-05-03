package com.enigmabridge.comm;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

/**
 * Misc COMM utils.
 * Created by dusanklinec on 27.04.16.
 */
public class EBCommUtils {
    public static final byte  IN_DATA_FLAG                          = (byte) 0x1f;
    public static final byte  OUT_DATA_FLAG                         = (byte) 0xf1;

    public final static byte OBJECTID_LENGTH =                     (byte) 0x04;
    public final static byte UO_KEY_TYPE_LENGTH = (short) 2;
    public final static byte UO_KEY_SIZE_LENGTH = (short) 2;
    public final static byte UO_SECTION_SIZE_LENGTH = (short) 2;
    public final static byte PROCESSDATA_FRESHNESS_NONCE_LENGTH     = (byte) 0x08;
    public final static byte AES_BLOCK_LEN =                       (byte) 0x10;
    public final static byte APDU_MAC_AES_LENGTH =                 (byte) 0x10;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DEFAULT = METHOD_POST;

    /**
     * Converts mangled nonce value to the original one in ProcessData response.
     * ProcessData response has nonce return value response_nonce[i] = request_nonce[i] + 0x1
     *
     * @param nonce
     * @param offset
     * @param len
     * @return
     */
    public static void demangleNonce(byte[] nonce, int offset, int len){
        int i = 0;
        for(i=offset; i<offset+len; i++){
            nonce[i]-=1;
        }
    }

    /**
     * Generates a new nonce for ProcessData request.
     * @return nonce
     */
    public static byte[] genProcessDataNonce(){
        final SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[EBProcessDataRequestBuilder.NONCE_LENGTH];
        random.nextBytes(nonce);
        return nonce;
    }

    public static int getInt(byte[] buffer, int offset) {
        return ByteBuffer.wrap(buffer, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public static long getLong(byte[] buffer, int offset) {
        return ByteBuffer.wrap(buffer, offset, 8).order(ByteOrder.BIG_ENDIAN).getLong();
    }

    public static short getShort(byte[] buffer, int offset) {
        return ByteBuffer.wrap(buffer, offset, 2).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    public static short setShort(byte[] buffer, int offset, short value) {
        buffer[offset] = (byte) (value >> 8 & 0xff);
        buffer[offset + 1] = (byte) (value & 0xff);
        return (short) (offset + 2); // size of short == 2
    }

    /**
     * @param buffer - the main buffer where we will store the integer value.
     * @param offset - index from which the integer value will be stored.
     * @param value  - the integer value to add to the buffer
     * @return returns the new index into the buffer.
     */
    public static short setInt(byte[] buffer, int offset, int value) {

        buffer[offset] = (byte) (value >> 24 & 0xff);
        buffer[offset + 1] = (byte) (value >> 16 & 0xff);
        buffer[offset + 2] = (byte) (value >> 8 & 0xff);
        buffer[offset + 3] = (byte) (value & 0xff);
        return (short) (offset + 4); // size of int == 4
    }

    public static short setLong(byte[] buffer, int offset, long value) {
        buffer[offset] = (byte) (value >> 56 & 0xff);
        buffer[offset + 1] = (byte) (value >> 48 & 0xff);
        buffer[offset + 2] = (byte) (value >> 40 & 0xff);
        buffer[offset + 3] = (byte) (value >> 32 & 0xff);
        buffer[offset + 4] = (byte) (value >> 24 & 0xff);
        buffer[offset + 5] = (byte) (value >> 16 & 0xff);
        buffer[offset + 6] = (byte) (value >> 8 & 0xff);
        buffer[offset + 7] = (byte) (value & 0xff);
        return (short) (offset + 8); // size of long == 8
    }

}
