package com.enigmabridge.comm;

import org.json.JSONObject;

/**
 * Base EB response.
 *
 * Created by dusanklinec on 27.04.16.
 */
public class EBResponse {
    /**
     * Parsed status code. 0x9000 = OK.
     * @output
     */
    short statusCode = -1;

    /**
     * Parsed status detail.
     * @output
     */
    String statusDetail;

    /**
     * Function name extracted from the request.
     */
    String function;

    /**
     * Raw result of the call.
     * Usually processed by child classes.
     */
    Object result;

    /**
     * Raw response as a reference.
     */
    EBRawResponse rawResponse;

    public EBResponse() {
    }

    public EBResponse(JSONObject result, short statusCode, String statusDetail, String function) {
        this.result = result;
        this.statusCode = statusCode;
        this.statusDetail = statusDetail;
        this.function = function;
    }

    /**
     * Returns true if after parsing, code is OK.
     * @returns {boolean}
     */
    public boolean isCodeOk(){
        return this.statusCode == EBCommStatus.SW_STAT_OK;
    }

    public String toString(){
        return String.format("Response{statusCode=0x%4X, statusDetail=[%s], function: [%s], result: [%s]}",
                this.statusCode,
                this.statusDetail,
                this.function,
                this.result
        );
    }

    public short getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(short statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public EBRawResponse getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(EBRawResponse rawResponse) {
        this.rawResponse = rawResponse;
    }
}
