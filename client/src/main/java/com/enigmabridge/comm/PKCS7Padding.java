package com.enigmabridge.comm;

import javax.crypto.BadPaddingException;

/**
 * PKCS7 padding
 * Created by dusanklinec on 30.06.16.
 */
public class PKCS7Padding {

    public static int pad(byte[] buffer, int dataOffset, int dataLen, int blockLength) {
        byte paddValue = (byte) (blockLength - (byte) (dataLen % blockLength));
        for (int i = (dataOffset + dataLen); i < (dataOffset + dataLen + paddValue); i++) {
            buffer[i] = paddValue;
        }
        dataLen += paddValue;

        return dataLen;
    }

    public static int unpad(byte[] buffer, int dataOffset, int dataLen, int blockLength) throws BadPaddingException {
        if ((short) (dataLen % blockLength) != 0) {
            throw new BadPaddingException("Wrong data length");
        }
        else {
            short lastOffset = (short) ((short) (dataOffset + dataLen) - 1);
            byte paddValue = buffer[lastOffset];

            if (paddValue > blockLength) {
                throw new BadPaddingException("Value error");
            }
            if (paddValue <= (byte) 0) {
                throw new BadPaddingException("Value error");
            }

            for (short i = lastOffset; i > (short) (lastOffset - paddValue); i--) {
                if (buffer[i] != paddValue) {
                    throw new BadPaddingException("Invalid padding");
                }
                dataLen--;
            }
        }

        return dataLen;
    }
}
