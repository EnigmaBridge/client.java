package com.enigmabridge.comm;

import com.enigmabridge.EBUtils;
import com.enigmabridge.UserObjectInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * EB ProcessData response
 *
 * Created by dusanklinec on 27.04.16.
 */
public class EBProcessDataResponseParser extends EBResponseParserBase{
    private static final Logger LOG = LoggerFactory.getLogger(EBProcessDataResponseParser.class);

    protected UserObjectInfo uo;

    /**
     * Parse EB response
     *
     * @param data - json response
     * @param resp - response object to put data to.
     * @param options
     * @returns request unwrapped response.
     */
    public EBResponse parseResponse(JSONObject data, EBResponse resp, EBResponseParserOptions options) throws EBCorruptedException{
        if (resp == null){
            resp = new EBProcessDataResponse();
        }

        this.parseCommonHeaders(resp, data);
        if (!resp.isCodeOk()){
            LOG.debug("Error in processing, status: %s, message: %s", resp.getStatusCode(), resp.getStatusDetail());
            return resp;
        }

        final EBProcessDataResponse pdResp = (EBProcessDataResponse) resp;
        final String resultBuffer = resp.getResult();
        final byte[] baResult = EBUtils.hex2byte(resultBuffer.substring(0, resultBuffer.indexOf('_')));

        short offset = 0;
        final short plainLen = EBCommUtils.getShort(baResult, offset);
        offset += 2;
        final byte[] plainBytes = plainLen <= 0 ? null : Arrays.copyOfRange(baResult, offset, offset+plainLen);
        offset += plainLen;

        // Decrypt and verify.
        final EBProcessDataCipher cipher = EBProcessDataCipher.initCipher(false, uo.getCommKeys());
        final byte[] decryptedData = cipher.processBuffer(baResult, offset, baResult.length - offset);

        short decOffset = 0;
        if (decryptedData[decOffset++] != EBCommUtils.OUT_DATA_FLAG){
            throw new EBCorruptedException("Given data packet is not a response (flag mismatch)");
        }

        // Get user object.
        pdResp.setUserObjectId(EBCommUtils.getInt(decryptedData, 1));
        decOffset+=4;

        // Get nonce, mangled.
        EBCommUtils.demangleNonce(decryptedData, decOffset, EBCommUtils.PROCESSDATA_FRESHNESS_NONCE_LENGTH);
        pdResp.setNonce(Arrays.copyOfRange(decryptedData, decOffset, decOffset+EBCommUtils.PROCESSDATA_FRESHNESS_NONCE_LENGTH));
        decOffset+=EBCommUtils.PROCESSDATA_FRESHNESS_NONCE_LENGTH;

        // Response = plainData + decryptedData.
        pdResp.setProtectedData(Arrays.copyOfRange(decryptedData, decOffset, decryptedData.length));
        pdResp.setPlainData(plainBytes);

        // If parsing function is already set, use it.
        if (subParser != null){
            return subParser.parseResponse(data, pdResp, options);
        }

        return pdResp;
    }

    public UserObjectInfo getUo() {
        return uo;
    }

    public void setUo(UserObjectInfo uo) {
        this.uo = uo;
    }
}
