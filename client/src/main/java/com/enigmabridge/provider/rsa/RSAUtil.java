package com.enigmabridge.provider.rsa;

import com.enigmabridge.provider.parameters.EBRSAKeyParameter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

/**
 * utility class for converting java.security RSA objects into their
 * org.bouncycastle.crypto counterparts.
 */
public class RSAUtil
{
    public static final ASN1ObjectIdentifier[] rsaOids =
    {
        PKCSObjectIdentifiers.rsaEncryption,
        X509ObjectIdentifiers.id_ea_rsa,
        PKCSObjectIdentifiers.id_RSAES_OAEP,
        PKCSObjectIdentifiers.id_RSASSA_PSS
    };

    public static boolean isRsaOid(
        ASN1ObjectIdentifier algOid)
    {
        for (int i = 0; i != rsaOids.length; i++)
        {
            if (algOid.equals(rsaOids[i]))
            {
                return true;
            }
        }

        return false;
    }

    public static EBRSAKeyParameter generatePublicKeyParameter(
        EBRSAKey key)
    {
        return new EBRSAKeyParameter(
                false,
                key.getUserObjectKey(),
                key.getEBEngine(),
                key.getOperationConfiguration(),
                key.getModulus(),
                key.getPublicExponent()
        );
    }

    public static EBRSAKeyParameter generatePrivateKeyParameter(
        EBRSAKey key)
    {
        return new EBRSAKeyParameter(
                true,
                key.getUserObjectKey(),
                key.getEBEngine(),
                key.getOperationConfiguration(),
                key.getModulus(),
                key.getPublicExponent()
        );
    }
}
