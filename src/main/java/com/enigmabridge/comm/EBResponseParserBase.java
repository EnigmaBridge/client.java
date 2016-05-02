package com.enigmabridge.comm;

import org.json.JSONObject;

/**
 * Parser for EB response.
 * Created by dusanklinec on 27.04.16.
 */
public class EBResponseParserBase implements EBResponseParser{
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_STATUS_DETAIL = "statusdetail";
    public static final String FIELD_FUNCTION = "function";
    public static final String FIELD_RESULT = "result";

    protected EBResponseParser subParser;

    /**
     * Parses common JSON headers from the response, e.g., status, to the provided message.
     * @param resp
     * @param data
     * @returns {eb.comm.response}
     */
    public EBResponse parseCommonHeaders(EBResponse resp, JSONObject data){
        if (data == null || !data.has(FIELD_STATUS) || !data.has(FIELD_FUNCTION)){
            throw new IllegalArgumentException();
        }

        // Build new response message.
        resp.setStatusCode(EBCommUtils.getAsInteger(data, FIELD_STATUS, 16));
        resp.setStatusDetail(EBCommUtils.getAsStringOrNull(data, FIELD_STATUS_DETAIL));
        resp.setFunction(EBCommUtils.tryGetAsString(data, FIELD_FUNCTION));
        resp.setResult(EBCommUtils.tryGetAsString(data, FIELD_RESULT));
        return resp;
    }

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
            resp = new EBResponse();
        }

        this.parseCommonHeaders(resp, data);

        // If parsing function is already set, use it.
        if (subParser != null){
            return subParser.parseResponse(data, resp, options);
        }

        return resp;
    }

    public EBResponseParser getSubParser() {
        return subParser;
    }

    public void setSubParser(EBResponseParser subParser) {
        this.subParser = subParser;
    }
}
