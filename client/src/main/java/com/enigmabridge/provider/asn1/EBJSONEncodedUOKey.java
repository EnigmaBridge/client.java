package com.enigmabridge.provider.asn1;

import com.enigmabridge.EBJSONSerializable;
import org.bouncycastle.asn1.*;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Enumeration;

/**
 * JSON encoded ASN.1 string.
 *
 * Created by dusanklinec on 18.07.16.
 */
public class EBJSONEncodedUOKey extends ASN1Object {
    protected JSONObject jsonObject;
    protected BigInteger version;

    public EBJSONEncodedUOKey(EBJSONSerializable ebjsonSerializable) {
        jsonObject = ebjsonSerializable.toJSON(null);
    }

    public static EBJSONEncodedUOKey getInstance(
            ASN1TaggedObject obj,
            boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static EBJSONEncodedUOKey getInstance(
            Object obj)
    {
        if (obj instanceof EBJSONEncodedUOKey)
        {
            return (EBJSONEncodedUOKey)obj;
        }

        if (obj != null)
        {
            return new EBJSONEncodedUOKey(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    private EBJSONEncodedUOKey(
            ASN1Sequence seq)
    {
        Enumeration e = seq.getObjects();

        BigInteger v = ((ASN1Integer)e.nextElement()).getValue();
        if (v.intValue() != 0 && v.intValue() != 1)
        {
            throw new IllegalArgumentException("wrong version");
        }

        version = v;
        jsonObject = new JSONObject(((DERUTF8String)e.nextElement()).getString());
    }

    /**
     * This outputs the key in PKCS1v2 format.
     *
     * <pre>
     *      JSONEncodedKey ::= SEQUENCE {
     *                          version Version,
     *                          json STRING
     *                      }
     *
     *      Version ::= INTEGER { (0) }
     * </pre>
     * <p>
     */
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new ASN1Integer(0));                       // version
        v.add(new DERUTF8String(jsonObject.toString()));

        return new DERSequence(v);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }
}
