package com.enigmabridge.comm;

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
    int statusCode;

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
    String result;

    public EBResponse() {
    }

    public EBResponse(String result, int statusCode, String statusDetail, String function) {
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
