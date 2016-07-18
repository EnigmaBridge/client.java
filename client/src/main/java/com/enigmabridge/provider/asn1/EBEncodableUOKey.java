package com.enigmabridge.provider.asn1;


import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.io.IOException;

/**
 * Wrapping PrivateKeyInfo for encoding of EB UO keys to the PKCS8 format.
 * Used for KeyStore (KS) serialization - PKCS12.
 *
 * Created by dusanklinec on 18.07.16.
 */
public class EBEncodableUOKey extends PrivateKeyInfo {
    public EBEncodableUOKey(AlgorithmIdentifier algId, ASN1Encodable privateKey) throws IOException {
        super(algId, privateKey);
    }

    public EBEncodableUOKey(AlgorithmIdentifier algId, ASN1Encodable privateKey, ASN1Set attributes) throws IOException {
        super(algId, privateKey, attributes);
    }

    public EBEncodableUOKey(ASN1Sequence seq) {
        super(seq);
    }

    public static EBEncodableUOKey getInstance(
            Object  obj)
    {
        if (obj instanceof EBEncodableUOKey)
        {
            return (EBEncodableUOKey)obj;
        }
        else if (obj != null)
        {
            return new EBEncodableUOKey(ASN1Sequence.getInstance(obj));
        }

        return null;
    }
}
