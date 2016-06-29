package com.enigmabridge.comm;

import org.json.JSONObject;

/**
 * Base response parser.
 * Created by dusanklinec on 28.04.16.
 */
public interface EBResponseParser {
    EBResponse.ABuilder parseResponse(JSONObject data, EBResponse.ABuilder resp, EBResponseParserOptions options) throws EBCorruptedException;
    EBResponseParser getSubParser();
}
