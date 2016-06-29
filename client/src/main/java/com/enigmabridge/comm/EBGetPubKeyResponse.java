package com.enigmabridge.comm;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Response on getPubKeyCall
 * Created by dusanklinec on 02.05.16.
 */
public class EBGetPubKeyResponse extends EBResponse {
    protected List<EBImportPubKey> importKeys;

    public List<EBImportPubKey> getImportKeys() {
        if (importKeys == null){
            importKeys = new LinkedList<EBImportPubKey>();
        }
        return importKeys;
    }

    public static abstract class ABuilder<T extends EBGetPubKeyResponse, B extends ABuilder>
            extends EBResponse.ABuilder<T,B>
    {
        public B setImportKeys(List<EBImportPubKey> importKeys) {
            getObj().setImportKeys(importKeys);
            return getThisBuilder();
        }

        public B addImportKey(EBImportPubKey importKey) {
            getObj().getImportKeys().add(importKey);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends ABuilder {
        private final EBGetPubKeyResponse parent = new EBGetPubKeyResponse();

        @Override
        public EBGetPubKeyResponse.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBGetPubKeyResponse getObj() {
            return parent;
        }

        @Override
        public EBGetPubKeyResponse build() {
            return parent;
        }
    }

    protected void setImportKeys(List<EBImportPubKey> importKeys) {
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
