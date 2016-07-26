package com.enigmabridge.comm;

import com.enigmabridge.EBEngineException;
import com.enigmabridge.EBLogger;
import com.enigmabridge.EBUtils;
import com.enigmabridge.UserObjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Builder of the ProcessData body.
 * Created by dusanklinec on 27.04.16.
 */
public class EBProcessDataRequestBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataRequestBuilder.class);

    public static final int NONCE_LENGTH = 8;

    protected UserObjectInfo uoInfo;
    protected String requestType;
    protected byte[] nonce;

    protected EBLogger logger;

    /**
     * Builds EB request.
     *
     * @param requestData - bitArray with userdata to perform operation on (will be encrypted, MAC protected)
     * @return request
     * @throws IOException io
     * @throws EBEngineException ex
     */
    public EBProcessDataRequest build(byte[] requestData) throws IOException, EBEngineException {
        return build(null, requestData, 0, requestData == null ? 0 : requestData.length);
    }

    /**
     * Builds EB request.
     *
     * @param requestData - bitArray with userdata to perform operation on (will be encrypted, MAC protected)
     * @param offset - offset to start with request data
     * @param length - number of bytes to read from request data
     * @return request
     * @throws IOException io
     * @throws EBEngineException ex
     */
    public EBProcessDataRequest build(byte[] requestData, int offset, int length) throws IOException, EBEngineException {
        return build(null, requestData, offset, length);
    }

    /**
     * Builds EB request.
     *
     * @param plainData - bitArray of the plaintext data.
     * @param requestData - bitArray with userdata to perform operation on (will be encrypted, MAC protected)
     * @return request
     * @throws IOException io
     * @throws EBEngineException ex
     */
    public EBProcessDataRequest build(byte[] plainData, byte[] requestData) throws IOException, EBEngineException {
        return build(plainData, requestData, 0, requestData == null ? 0 : requestData.length);
    }

    /**
     * Builds EB request.
     *
     * @param plainData - bitArray of the plaintext data.
     * @param requestData - bitArray with userdata to perform operation on (will be encrypted, MAC protected)
     * @param requestDataOffset - offset to start with request data
     * @param requestDataLength - number of bytes to read from request data
     * @return request
     * @throws IOException io
     * @throws EBEngineException ex
     */
    public EBProcessDataRequest build(byte[] plainData, byte[] requestData, int requestDataOffset, int requestDataLength)
            throws IOException, EBEngineException
    {
        if (nonce == null){
            nonce = EBCommUtils.genProcessDataNonce();
        }

        // Plain data is empty for now.
        final int plainDataLength = plainData == null ? 0 : plainData.length;
        final int bufferSize = 1  // Request 0x1f flag
                + EBCommUtils.OBJECTID_LENGTH
                + EBCommUtils.UO_SECTION_SIZE_LENGTH
                + plainDataLength
                + EBCommUtils.PROCESSDATA_FRESHNESS_NONCE_LENGTH
                + requestDataLength
                + EBCommUtils.AES_BLOCK_LEN
                + EBCommUtils.APDU_MAC_AES_LENGTH;
        byte[] inDataWithUOID = new byte[bufferSize];
        short offset = 0;

        offset = EBCommUtils.setShort(inDataWithUOID, offset, (short)plainDataLength); // Offset of data protected by comm keys
        if (plainData != null) {
            System.arraycopy(plainData, 0, inDataWithUOID, offset, plainDataLength);
            offset += plainDataLength;
        }

        // Input data flag
        short commOffset = offset;
        inDataWithUOID[offset++] = EBCommUtils.IN_DATA_FLAG;

        // User Object ID
        offset = EBCommUtils.setInt(inDataWithUOID, offset, (int)uoInfo.getUoid());    // UO ID inside protected blob

        // Freshness nonce
        System.arraycopy(nonce, 0, inDataWithUOID, offset, nonce.length);
        offset += nonce.length;

        // Request data
        System.arraycopy(requestData, requestDataOffset, inDataWithUOID, offset, requestDataLength);
        offset += requestDataLength;

        //this.log('ProcessData function input PDIN (0x1f | <UOID-4B> | <nonce-8B> | data | pkcs#7padding) : ' + h.fromBits(baBuff) + "; len: " + ba.bitLength(baBuff));

        final EBProcessDataCipher cipher = EBProcessDataCipher.initCipher(true, uoInfo.getCommKeys());
        final int processed = cipher.processBuffer(
                inDataWithUOID, (int)commOffset, (int)offset - commOffset,
                inDataWithUOID, (int)commOffset);

        final String requestBase = getRequestBase(EBUtils.byte2hex(inDataWithUOID, 0, commOffset+processed));
        return new EBProcessDataRequest(requestBase, null, uoInfo, nonce);
    }

    protected String getRequestBase(String hexCodedRequest){
        //return "Packet0_" + requestType + "_" + hexCodedRequest;
        return hexCodedRequest;
    }

    public UserObjectInfo getUoInfo() {
        return uoInfo;
    }

    public EBProcessDataRequestBuilder setUoInfo(UserObjectInfo uoInfo) {
        this.uoInfo = uoInfo;
        return this;
    }

    public String getRequestType() {
        return requestType;
    }

    public EBProcessDataRequestBuilder setRequestType(String requestType) {
        this.requestType = requestType;
        return this;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public EBProcessDataRequestBuilder setNonce(byte[] nonce) {
        this.nonce = nonce;
        return this;
    }

    public EBLogger getLogger() {
        return logger;
    }

    public EBProcessDataRequestBuilder setLogger(EBLogger logger) {
        this.logger = logger;
        return this;
    }

    protected void log(String x) {
// TODO:
//        if (console && console.log){
//            console.log(x);
//        }
//
//        if (this.logger){
//            this.logger(x);
//        }
    }
}
