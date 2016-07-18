package com.enigmabridge.provider.rsa;

import com.enigmabridge.provider.asn1.EBASNUtils;
import com.enigmabridge.provider.asn1.EBJSONEncodedUOKey;
import org.bouncycastle.asn1.ASN1Sequence;
import org.json.JSONObject;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA public key in EB.
 * Conforms to general Java Key interface.
 *
 * Inspiration: bcprov-jdk15on-1.54-sources.jar!/org/bouncycastle/jcajce/provider/asymmetric/rsa/BCRSAPublicKey.java
 * Created by dusanklinec on 26.04.16.
 */
public class EBRSAPublicKey extends EBRSAKey implements RSAPublicKey {
    static final long serialVersionUID = 1;

    public static class Builder extends EBRSAKey.AbstractBuilder<EBRSAPublicKey, Builder> {
        private final EBRSAPublicKey parent = new EBRSAPublicKey();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBRSAPublicKey getObj() {
            return parent;
        }

        @Override
        public EBRSAPublicKey build() {
            return parent;
        }
    }

    public static EBRSAPublicKey getInstance(
            Object obj) throws IOException
    {
        if (obj instanceof EBRSAPublicKey)
        {
            return (EBRSAPublicKey)obj;
        }

        if (obj != null)
        {
            return new EBRSAPublicKey(EBJSONEncodedUOKey.getInstance(ASN1Sequence.getInstance(obj)));
        }

        return null;
    }

    public EBRSAPublicKey() {
    }

    public EBRSAPublicKey(JSONObject json) throws IOException {
        super(json);
    }

    public EBRSAPublicKey(EBJSONEncodedUOKey key) throws IOException {
        super(key);
    }

    @Override
    public byte[] getEncoded() {
        return super.getEncoded(EBASNUtils.eb_rsa_pub);
    }
}
