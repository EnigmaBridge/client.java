package com.enigmabridge.provider.specs;

import java.security.spec.KeySpec;

/**
 * Support for key spec such as
 * {@literal https://site2.enigmabridge.com:11180/apiKey=API_TEST&uoid=28&uotype=b00004}
 *
 * Created by dusanklinec on 18.07.16.
 */
public class EBConfigurationUOKeySpec implements KeySpec {
    protected final String configLine;

    public EBConfigurationUOKeySpec(String configLine) {
        this.configLine = configLine;
    }

    public String getConfigLine() {
        return configLine;
    }
}
