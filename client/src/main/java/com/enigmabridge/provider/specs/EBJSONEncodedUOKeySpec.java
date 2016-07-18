package com.enigmabridge.provider.specs;

import org.json.JSONObject;

import java.security.spec.KeySpec;

/**
 * Support key factory from serialized objects.
 *
 * Created by dusanklinec on 18.07.16.
 */
public class EBJSONEncodedUOKeySpec implements KeySpec {
    protected final JSONObject json;

    public EBJSONEncodedUOKeySpec(JSONObject json) {
        this.json = json;
    }

    public JSONObject getJson() {
        return json;
    }
}
