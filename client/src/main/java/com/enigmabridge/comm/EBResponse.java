package com.enigmabridge.comm;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Base EB response.
 *
 * Created by dusanklinec on 27.04.16.
 */
public class EBResponse implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * Parsed status code. 0x9000 = OK.
     * #output
     */
    protected short statusCode = -1;

    /**
     * Parsed status detail.
     * #output
     */
    protected String statusDetail;

    /**
     * Function name extracted from the request.
     */
    protected String function;

    /**
     * Raw result of the call.
     * Usually processed by child classes.
     */
    protected Object result;

    /**
     * Raw response as a reference.
     */
    protected EBRawResponse rawResponse;

    public EBResponse() {
    }

    public EBResponse(JSONObject result, short statusCode, String statusDetail, String function) {
        this.result = result;
        this.statusCode = statusCode;
        this.statusDetail = statusDetail;
        this.function = function;
    }

    public static abstract class ABuilder<T extends EBResponse, B extends ABuilder> {
        public B setStatusCode(short statusCode) {
            getObj().setStatusCode(statusCode);
            return getThisBuilder();
        }

        public B setStatusDetail(String statusDetail) {
            getObj().setStatusDetail(statusDetail);
            return getThisBuilder();
        }

        public B setFunction(String function) {
            getObj().setFunction(function);
            return getThisBuilder();
        }

        public B setResult(Object result) {
            getObj().setResult(result);
            return getThisBuilder();
        }

        public B setRawResponse(EBRawResponse rawResponse) {
            getObj().setRawResponse(rawResponse);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends ABuilder<EBResponse, Builder> {
        private final EBResponse parent = new EBResponse();

        @Override
        public EBResponse.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBResponse getObj() {
            return parent;
        }

        @Override
        public EBResponse build() {
            return parent;
        }
    }

    /**
     * Returns true if after parsing, code is OK.
     * @return boolean, true if code is SW_STAT_OK
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

    protected void setStatusCode(short statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    protected void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getFunction() {
        return function;
    }

    protected void setFunction(String function) {
        this.function = function;
    }

    public Object getResult() {
        return result;
    }

    protected void setResult(Object result) {
        this.result = result;
    }

    public EBRawResponse getRawResponse() {
        return rawResponse;
    }

    protected void setRawResponse(EBRawResponse rawResponse) {
        this.rawResponse = rawResponse;
    }
}
