package com.enigmabridge.provider.rsa;

import com.enigmabridge.provider.EBKeyBase;
import com.enigmabridge.provider.asn1.EBASNUtils;
import com.enigmabridge.provider.asn1.EBJSONEncodedUOKey;
import org.bouncycastle.asn1.ASN1Sequence;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

/**
 * General RSA key, common for private and public parts.
 * Conforms to general Java Key interface.
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAKey extends EBKeyBase implements PrivateKey, RSAKey {
    static final long serialVersionUID = 1;

    /**
     * If null for encryption operation -&gt; operation fails.
     * If null for decryption operation -&gt; operation runs without blinding.
     */
    protected BigInteger publicExponent;
    protected BigInteger modulus;

    private static final String FIELD_MODULUS = "modulus";
    private static final String FIELD_PUBLIC_EXPONENT = "publicExponent";

    public static abstract class AbstractBuilder<T extends EBRSAKey, B extends AbstractBuilder> extends EBKeyBase.AbstractBuilder<T,B>{
        public B setModulus(BigInteger mod) {
            getObj().setModulus(mod);
            return getThisBuilder();
        }

        public B setPublicExponent(BigInteger e) {
            getObj().setPublicExponent(e);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBRSAKey, Builder> {
        private final EBRSAKey parent = new EBRSAKey();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBRSAKey getObj() {
            return parent;
        }

        @Override
        public EBRSAKey build() {
            return parent;
        }
    }

    public static EBRSAKey getInstance(
            Object obj) throws IOException
    {
        if (obj instanceof EBRSAKey)
        {
            return (EBRSAKey)obj;
        }

        if (obj != null)
        {
            return new EBRSAKey(EBJSONEncodedUOKey.getInstance(ASN1Sequence.getInstance(obj)));
        }

        return null;
    }

    public EBRSAKey() {
    }

    public EBRSAKey(JSONObject json) throws IOException {
        super(json);
    }

    public EBRSAKey(EBJSONEncodedUOKey key) throws IOException {
        super(key);
    }

    /**
     * Initializes object form the JSON.
     * @param json
     * @throws MalformedURLException
     */
    @Override
    protected void fromJSON(JSONObject json) throws IOException {
        super.fromJSON(json);
        if (json == null)
        {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        if (json.has(FIELD_MODULUS)){
            modulus = new BigInteger(json.getString(FIELD_MODULUS), 16);

            if (json.has(FIELD_PUBLIC_EXPONENT)){
                publicExponent = new BigInteger(json.getString(FIELD_PUBLIC_EXPONENT), 16);
            }
        }
    }

    /**
     * Serializes to JSON.
     * @param json where to serialize / null
     * @return JSONObject
     */
    @Override
    public JSONObject toJSON(JSONObject json) {
        json = super.toJSON(json);
        if (json == null){
            json = new JSONObject();
        }

        if (modulus != null){
            json.put(FIELD_MODULUS, modulus.toString(16));
            if (publicExponent != null){
                json.put(FIELD_PUBLIC_EXPONENT, publicExponent.toString(16));
            }
        }
        return json;
    }

    @Override
    public byte[] getEncoded() {
        return super.getEncoded(EBASNUtils.eb_rsa);
    }

    @Override
    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPublicExponent() {
        return publicExponent;
    }

    protected void setModulus(BigInteger modulus) {
        this.modulus = modulus;
    }

    protected void setPublicExponent(BigInteger publicExponent) {
        this.publicExponent = publicExponent;
    }

    @Override
    public String toString() {
        return "EBRSAKey{" +
                "publicExponent=" + publicExponent +
                ", modulus=" + modulus +
                "} " + super.toString();
    }
}
