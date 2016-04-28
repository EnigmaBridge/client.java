package com.enigmabridge.comm;

import org.json.JSONObject;

/**
 * Base response parser.
 * Created by dusanklinec on 28.04.16.
 */
public interface EBResponseParser {
    EBResponse parse(JSONObject data, EBResponse resp, EBResponseParserOptions options) throws EBCorruptedException;
}
