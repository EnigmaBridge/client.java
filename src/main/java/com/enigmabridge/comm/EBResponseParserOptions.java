package com.enigmabridge.comm;

import java.util.Map;

/**
 * Parser options.
 * Created by dusanklinec on 28.04.16.
 */
public interface EBResponseParserOptions extends Map {
    EBResponseParser getParentParser();
}
