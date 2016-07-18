package com.enigmabridge.provider.rsa;

import com.enigmabridge.provider.asn1.EBASNUtils;
import com.enigmabridge.provider.asn1.EBJSONEncodedUOKey;
import org.bouncycastle.asn1.ASN1Sequence;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Non-extractable RSA private key in EB.
 * All private key operations need to be done in EB. Key does not leave EB.
 * Conforms to general Java Key interface.
 *
 * Inspiration: bcprov-jdk15on-1.54-sources.jar!/org/bouncycastle/jcajce/provider/asymmetric/rsa/BCRSAPrivateKey.java
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAPrivateKey extends EBRSAKey {
    static final long serialVersionUID = 1;

    public static class Builder extends EBRSAKey.AbstractBuilder<EBRSAPrivateKey, Builder> {
        private final EBRSAPrivateKey parent = new EBRSAPrivateKey();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBRSAPrivateKey getObj() {
            return parent;
        }

        @Override
        public EBRSAPrivateKey build() {
            return parent;
        }
    }

    public EBRSAPrivateKey() {
    }

    public EBRSAPrivateKey(JSONObject json) throws IOException {
        super(json);
    }

    public EBRSAPrivateKey(EBJSONEncodedUOKey key) throws IOException {
        super(key);
    }

    public static EBRSAPrivateKey getInstance(
            Object obj) throws IOException
    {
        if (obj instanceof EBRSAPrivateKey)
        {
            return (EBRSAPrivateKey)obj;
        }

        if (obj != null)
        {
            return new EBRSAPrivateKey(EBJSONEncodedUOKey.getInstance(ASN1Sequence.getInstance(obj)));
        }

        return null;
    }

    @Override
    public byte[] getEncoded() {
        return super.getEncoded(EBASNUtils.eb_rsa_priv);
    }
}
