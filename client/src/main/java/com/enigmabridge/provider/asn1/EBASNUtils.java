package com.enigmabridge.provider.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Created by dusanklinec on 18.07.16.
 */
public class EBASNUtils {

    /** PKCS#1: EB constants */
    public static final ASN1ObjectIdentifier pkcs_eb                    = new ASN1ObjectIdentifier("2.1.14");
    public static final ASN1ObjectIdentifier pkcs_uo                    = pkcs_eb.branch("1");

    public static final ASN1ObjectIdentifier eb_uo                      = pkcs_uo.branch("1");
    public static final ASN1ObjectIdentifier eb_uoKey                   = pkcs_uo.branch("2");
    public static final ASN1ObjectIdentifier eb_rsa                     = pkcs_uo.branch("3");
    public static final ASN1ObjectIdentifier eb_rsa_pub                 = eb_rsa.branch("1");
    public static final ASN1ObjectIdentifier eb_rsa_priv                = eb_rsa.branch("2");
    public static final ASN1ObjectIdentifier eb_aes                     = pkcs_uo.branch("4");

    public static byte[] getEncodedUOKey(AlgorithmIdentifier algId, ASN1Encodable privKey)
    {
        try
        {
            EBEncodableUOKey info = new EBEncodableUOKey(algId, privKey.toASN1Primitive());

            return getEncodedUOKey(info);
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public static byte[] getEncodedUOKey(EBEncodableUOKey info)
    {
        try
        {
            return info.getEncoded(ASN1Encoding.DER);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
