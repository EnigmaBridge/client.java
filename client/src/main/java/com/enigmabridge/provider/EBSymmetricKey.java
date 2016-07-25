package com.enigmabridge.provider;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBJSONSerializable;
import com.enigmabridge.provider.asn1.EBASNUtils;
import com.enigmabridge.provider.asn1.EBJSONEncodedUOKey;
import org.bouncycastle.asn1.ASN1Sequence;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.spec.KeySpec;

/**
 * Symmetric secret key is used for both encryption and decryption operation,
 * while EB separates these two operations and separate EB keys (same app key)
 * are used.
 *
 * We can do either a) encapsulate both encrypt and decrypt keys and use appropriate
 * one on init, or b) link inversion key to this one.
 *
 * Created by dusanklinec on 05.07.16.
 */
public class EBSymmetricKey extends EBKeyBase implements EBJSONSerializable, SecretKey, KeySpec {
    static final long serialVersionUID = 1;

    // Key for inversion operation.
    protected EBSymmetricKey inversionKey;

    private static final String FIELD_INVERSION_KEY = "inversionKey";

    public static abstract class AbstractBuilder<T extends EBSymmetricKey, B extends EBSymmetricKey.AbstractBuilder>
            extends EBKeyBase.AbstractBuilder<T,B>
    {
        public B setInversionKey(EBSymmetricKey ikey) {
            getObj().setInversionKey(ikey);
            return getThisBuilder();
        }

        public B setInversionKey(EBSymmetricKey.Builder ikey) {
            getObj().setInversionKey(ikey.getObj());
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBSymmetricKey.AbstractBuilder<EBSymmetricKey, EBSymmetricKey.Builder> {
        private final EBSymmetricKey parent = new EBSymmetricKey();

        @Override
        public EBSymmetricKey.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBSymmetricKey getObj() {
            return parent;
        }

        @Override
        public EBSymmetricKey build() {
            return parent;
        }
    }

    public static EBSymmetricKey getInstance(
            Object obj) throws IOException
    {
        if (obj instanceof EBSymmetricKey)
        {
            return (EBSymmetricKey)obj;
        }

        if (obj != null)
        {
            return new EBSymmetricKey(EBJSONEncodedUOKey.getInstance(ASN1Sequence.getInstance(obj)));
        }

        return null;
    }

    public EBSymmetricKey() {
    }

    public EBSymmetricKey(JSONObject json) throws IOException {
        super(json);
    }

    public EBSymmetricKey(EBJSONEncodedUOKey key) throws IOException {
        super(key);
    }

    /**
     * Initializes object form the JSON.
     * @param json
     * @throws MalformedURLException
     */
    protected void fromJSON(JSONObject json) throws IOException {
        fromJSON(json, true);
    }

    /**
     * Initializes object form the JSON.
     * @param json
     * @throws MalformedURLException
     */
    protected void fromJSON(JSONObject json, boolean includeInversion) throws IOException {
        if (json == null)
        {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        super.fromJSON(json);
        if (includeInversion && json.has(FIELD_INVERSION_KEY)){
            inversionKey = new EBSymmetricKey();
            inversionKey.fromJSON(json.getJSONObject(FIELD_INVERSION_KEY), false);
            inversionKey.setInversionKey(this);
            inversionKey.setEbEngine(getEBEngine(), false);
        }
    }

    /**
     * Serializes to JSON.
     * @param json
     * @return
     */
    @Override
    public JSONObject toJSON(JSONObject json) {
        return toJSON(json, true);
    }

    /**
     * Serializes to JSON.
     * @param json
     * @return
     */
    public JSONObject toJSON(JSONObject json, boolean includeInversion) {
        if (json == null){
            json = new JSONObject();
        }

        super.toJSON(json);
        if (includeInversion && inversionKey != null) {
            json.put(FIELD_INVERSION_KEY, inversionKey.toJSON(null, false));
        }
        return json;
    }

    @Override
    public byte[] getEncoded() {
        return super.getEncoded(EBASNUtils.eb_aes);
    }

    public boolean isEncryptionKey(){
        return getUserObjectType().isEncryptionObject();
    }

    public boolean isDecryptionKey(){
        return getUserObjectType().isDecryptionObject();
    }

    public EBSymmetricKey getInversionKey() {
        return inversionKey;
    }

    protected EBSymmetricKey setInversionKey(EBSymmetricKey inversionKey) {
        this.inversionKey = inversionKey;
        return this;
    }

    @Override
    protected void setEbEngine(EBEngine ebEngine) {
        setEbEngine(ebEngine, true);
    }

    protected void setEbEngine(EBEngine ebEngine, boolean setToInversion) {
        super.setEbEngine(ebEngine);
        if (setToInversion && this.inversionKey != null){
            this.inversionKey.setEbEngine(ebEngine, false);
        }
    }
}
