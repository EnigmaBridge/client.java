package com.enigmabridge.comm;

import com.enigmabridge.EBUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Response on getPubKeyCall
 * Created by dusanklinec on 02.05.16.
 */
public class EBGetPubKeyResponse extends EBResponse {
    List<EBImportPubKey> importKeys;

    public List<EBImportPubKey> getImportKeys() {
        if (importKeys == null){
            importKeys = new LinkedList<EBImportPubKey>();
        }
        return importKeys;
    }

    public void setImportKeys(List<EBImportPubKey> importKeys) {
        this.importKeys = importKeys;
    }

    public String toString(){
        return String.format("EBGetPubKeyResponse{statusCode=0x%4X, statusDetail=[%s], function: [%s], " +
                        "keys: %s",
                this.statusCode,
                this.statusDetail,
                this.function,
                Arrays.deepToString(getImportKeys().toArray())
        );
    }
}
