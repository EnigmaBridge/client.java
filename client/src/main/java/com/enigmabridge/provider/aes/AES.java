package com.enigmabridge.provider.aes;

import com.enigmabridge.provider.BaseBlockCipher;
import com.enigmabridge.provider.*;
import com.enigmabridge.provider.asn1.EBASNUtils;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.*;

import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/**
 * AES cipher.
 * Provider registration &amp; wrappers.
 *
 * Created by dusanklinec on 11.07.16.
 */
public class AES {

    // AES + standard block modes
    public static class ECB
            extends BaseBlockCipher
    {
        public ECB(final EnigmaProvider provider)
        {
            super(new BlockCipherProvider()
            {
                public BlockCipher get()
                {
                    return new AESEngine(provider);
                }
            });
        }
    }

    public static class CBC
            extends BaseBlockCipher
    {
        public CBC(EnigmaProvider provider)
        {
            super(new CBCBlockCipher(new AESEngine(provider)), 128);
        }
    }

    static public class CFB
            extends BaseBlockCipher
    {
        public CFB(EnigmaProvider provider)
        {
            super(new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(provider), 128)), 128);
        }
    }

    static public class OFB
            extends BaseBlockCipher
    {
        public OFB(EnigmaProvider provider)
        {
            super(new BufferedBlockCipher(new OFBBlockCipher(new AESEngine(provider), 128)), 128);
        }
    }

    static public class GCM
            extends BaseBlockCipher
    {
        public GCM(EnigmaProvider provider)
        {
            super(new GCMBlockCipher(new AESEngine(provider)));
        }
    }

    static public class CCM
            extends BaseBlockCipher
    {
        public CCM(EnigmaProvider provider)
        {
            super(new CCMBlockCipher(new AESEngine(provider)), false, 16);
        }
    }

    public static class AESCMAC
            extends BaseMac
    {
        public AESCMAC(EnigmaProvider provider)
        {
            super(new CMac(new AESEngine(provider)));
        }
    }

    public static class AESGMAC
            extends BaseMac
    {
        public AESGMAC(EnigmaProvider provider)
        {
            super(new GMac(new GCMBlockCipher(new AESEngine(provider))));
        }
    }

    public static class Poly1305
            extends BaseMac
    {
        public Poly1305(EnigmaProvider provider)
        {
            super(new org.bouncycastle.crypto.macs.Poly1305(new AESEngine(provider)));
        }
    }

    public static class Poly1305KeyGen
            extends BaseKeyGenerator
    {
        public Poly1305KeyGen(EnigmaProvider provider)
        {
            super("Poly1305-AES", 256, new Poly1305KeyGenerator());
        }
    }

    static public class Wrap
            extends BaseWrapCipher
    {
        public Wrap(EnigmaProvider provider)
        {
            super(new AESWrapEngine(provider));
        }
    }

    public static class RFC3211Wrap
            extends BaseWrapCipher
    {
        public RFC3211Wrap(EnigmaProvider provider)
        {
            super(new RFC3211WrapEngine(new AESEngine(provider)), 16);
        }
    }

    public static class RFC5649Wrap
            extends BaseWrapCipher
    {
        public RFC5649Wrap(EnigmaProvider provider)
        {
            super(new RFC5649WrapEngine(new AESEngine(provider)));
        }
    }

    // KeyGenerator
    public static class KeyGen
            extends EBKeyGenerator
    {
        public KeyGen(EnigmaProvider provider)
        {
            this(provider, 192);
        }

        public KeyGen(EnigmaProvider provider, int keySize)
        {
            super(provider, "AES");
        }
    }

    public static class KeyGen128
            extends KeyGen
    {
        public KeyGen128(EnigmaProvider provider)
        {
            super(provider, 128);
        }
    }

    public static class KeyGen192
            extends KeyGen
    {
        public KeyGen192(EnigmaProvider provider)
        {
            super(provider, 192);
        }
    }

    public static class KeyGen256
            extends KeyGen
    {
        public KeyGen256(EnigmaProvider provider)
        {
            super(provider, 256);
        }
    }

    // SecretKeyFactory
    public static class KeyFact
            extends EBSecretKeyFactory
    {
        public KeyFact(EnigmaProvider provider)
        {
            super(provider, "AES");
        }
    }

    // Parameters
    public static class AlgParamGen
            extends BaseAlgorithmParameterGenerator
    {
        protected void engineInit(
                AlgorithmParameterSpec genParamSpec,
                SecureRandom random)
                throws InvalidAlgorithmParameterException
        {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }

        protected AlgorithmParameters engineGenerateParameters()
        {
            byte[]  iv = new byte[16];

            if (random == null)
            {
                random = new SecureRandom();
            }

            random.nextBytes(iv);

            AlgorithmParameters params;

            try
            {
                params = createParametersInstance("AES");
                params.init(new IvParameterSpec(iv));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }

            return params;
        }
    }

    public static class AlgParamGenCCM
            extends BaseAlgorithmParameterGenerator
    {
        protected void engineInit(
                AlgorithmParameterSpec genParamSpec,
                SecureRandom random)
                throws InvalidAlgorithmParameterException
        {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }

        protected AlgorithmParameters engineGenerateParameters()
        {
            byte[]  iv = new byte[12];

            if (random == null)
            {
                random = new SecureRandom();
            }

            random.nextBytes(iv);

            AlgorithmParameters params;

            try
            {
                params = createParametersInstance("CCM");
                params.init(new CCMParameters(iv, 12).getEncoded());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }

            return params;
        }
    }

    public static class AlgParamGenGCM
            extends BaseAlgorithmParameterGenerator
    {
        protected void engineInit(
                AlgorithmParameterSpec genParamSpec,
                SecureRandom random)
                throws InvalidAlgorithmParameterException
        {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }

        protected AlgorithmParameters engineGenerateParameters()
        {
            byte[]  nonce = new byte[12];

            if (random == null)
            {
                random = new SecureRandom();
            }

            random.nextBytes(nonce);

            AlgorithmParameters params;

            try
            {
                params = createParametersInstance("GCM");
                params.init(new GCMParameters(nonce, 12).getEncoded());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }

            return params;
        }
    }

    public static class AlgParams
            extends IvAlgorithmParameters
    {
        protected String engineToString()
        {
            return "AES IV";
        }
    }

    public static class AlgParamsGCM
            extends BaseAlgorithmParameters
    {
        private GCMParameters gcmParams;

        protected void engineInit(AlgorithmParameterSpec paramSpec)
                throws InvalidParameterSpecException
        {
            if (GcmSpecUtil.isGcmSpec(paramSpec))
            {
                gcmParams = GcmSpecUtil.extractGcmParameters(paramSpec);
            }
            else
            {
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramSpec.getClass().getName());
            }
        }

        protected void engineInit(byte[] params)
                throws IOException
        {
            gcmParams = GCMParameters.getInstance(params);
        }

        protected void engineInit(byte[] params, String format)
                throws IOException
        {
            if (!isASN1FormatString(format))
            {
                throw new IOException("unknown format specified");
            }

            gcmParams = GCMParameters.getInstance(params);
        }

        protected byte[] engineGetEncoded()
                throws IOException
        {
            return gcmParams.getEncoded();
        }

        protected byte[] engineGetEncoded(String format)
                throws IOException
        {
            if (!isASN1FormatString(format))
            {
                throw new IOException("unknown format specified");
            }

            return gcmParams.getEncoded();
        }

        protected String engineToString()
        {
            return "GCM";
        }

        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec)
                throws InvalidParameterSpecException
        {
            if (paramSpec == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(paramSpec))
            {
                if (GcmSpecUtil.gcmSpecExists())
                {
                    return GcmSpecUtil.extractGcmSpec(gcmParams.toASN1Primitive());
                }
                return new IvParameterSpec(gcmParams.getNonce());
            }
            if (paramSpec == IvParameterSpec.class)
            {
                return new IvParameterSpec(gcmParams.getNonce());
            }

            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + paramSpec.getName());
        }
    }

    public static class AlgParamsCCM
            extends BaseAlgorithmParameters
    {
        private CCMParameters ccmParams;

        protected void engineInit(AlgorithmParameterSpec paramSpec)
                throws InvalidParameterSpecException
        {
            if (GcmSpecUtil.isGcmSpec(paramSpec))
            {
                ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(paramSpec));
            }
            else
            {
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramSpec.getClass().getName());
            }
        }

        protected void engineInit(byte[] params)
                throws IOException
        {
            ccmParams = CCMParameters.getInstance(params);
        }

        protected void engineInit(byte[] params, String format)
                throws IOException
        {
            if (!isASN1FormatString(format))
            {
                throw new IOException("unknown format specified");
            }

            ccmParams = CCMParameters.getInstance(params);
        }

        protected byte[] engineGetEncoded()
                throws IOException
        {
            return ccmParams.getEncoded();
        }

        protected byte[] engineGetEncoded(String format)
                throws IOException
        {
            if (!isASN1FormatString(format))
            {
                throw new IOException("unknown format specified");
            }

            return ccmParams.getEncoded();
        }

        protected String engineToString()
        {
            return "CCM";
        }

        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec)
                throws InvalidParameterSpecException
        {
            if (paramSpec == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(paramSpec))
            {
                if (GcmSpecUtil.gcmSpecExists())
                {
                    return GcmSpecUtil.extractGcmSpec(ccmParams.toASN1Primitive());
                }
                return new IvParameterSpec(ccmParams.getNonce());
            }
            if (paramSpec == IvParameterSpec.class)
            {
                return new IvParameterSpec(ccmParams.getNonce());
            }

            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + paramSpec.getName());
        }
    }

    // Mappings - provider registration
    public static class Mappings
            extends SymmetricAlgorithmProvider
    {
        private static final String PREFIX = AES.class.getName();

        /**
         * These three got introduced in some messages as a result of a typo in an
         * early document. We don't produce anything using these OID values, but we'll
         * read them.
         */
        private static final String wrongAES128 = "2.16.840.1.101.3.4.2";
        private static final String wrongAES192 = "2.16.840.1.101.3.4.22";
        private static final String wrongAES256 = "2.16.840.1.101.3.4.42";

        public Mappings()
        {

        }

        public void configure(ConfigurableProvider provider)
        {
            provider.addAlgorithm("AlgorithmParameters.AES", PREFIX + "$AlgParams");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + wrongAES128, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + wrongAES192, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + wrongAES256, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CBC, "AES");

            provider.addAlgorithm("AlgorithmParameters.GCM", PREFIX + "$AlgParamsGCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");

            provider.addAlgorithm("AlgorithmParameters.CCM", PREFIX + "$AlgParamsCCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");

            provider.addAlgorithm("AlgorithmParameterGenerator.AES", PREFIX + "$AlgParamGen");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + wrongAES128, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + wrongAES192, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + wrongAES256, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CBC, "AES");

            provider.addAlgorithm("Cipher.AES", PREFIX + "$ECB");
            provider.addAlgorithm("Alg.Alias.Cipher." + wrongAES128, "AES");
            provider.addAlgorithm("Alg.Alias.Cipher." + wrongAES192, "AES");
            provider.addAlgorithm("Alg.Alias.Cipher." + wrongAES256, "AES");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher.AESWRAP", PREFIX + "$Wrap");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap, "AESWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap, "AESWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap, "AESWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher.AESKW", "AESWRAP");

            provider.addAlgorithm("Cipher.AESRFC3211WRAP", PREFIX + "$RFC3211Wrap");
            provider.addAlgorithm("Cipher.AESRFC5649WRAP", PREFIX + "$RFC5649Wrap");

            provider.addAlgorithm("AlgorithmParameterGenerator.CCM", PREFIX + "$AlgParamGenCCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");

            provider.addAlgorithm("Cipher.CCM", PREFIX + "$CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_CCM, "CCM");

            provider.addAlgorithm("AlgorithmParameterGenerator.GCM", PREFIX + "$AlgParamGenGCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");

            provider.addAlgorithm("Cipher.GCM", PREFIX + "$GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_GCM, "GCM");

            provider.addAlgorithm("KeyGenerator.AES", PREFIX + "$KeyGen");
            provider.addAlgorithm("KeyGenerator." + wrongAES128, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator." + wrongAES192, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator." + wrongAES256, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator.AESWRAP", PREFIX + "$KeyGen");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_GCM, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_GCM, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_GCM, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CCM, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CCM, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CCM, PREFIX + "$KeyGen256");

            // SecretKeyFactory
            provider.addAlgorithm("SecretKeyFactory.AES", PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory." + wrongAES128, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory." + wrongAES192, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory." + wrongAES256, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory.AESWRAP", PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_wrap, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_wrap, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_wrap, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_GCM, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_GCM, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_GCM, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes128_CCM, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes192_CCM, PREFIX + "$KeyFact");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.id_aes256_CCM, PREFIX + "$KeyFact");

            provider.addAlgorithm("SecretKeyFactory", EBASNUtils.eb_aes, PREFIX + "$KeyFact");

            provider.addAlgorithm("Mac.AESCMAC", PREFIX + "$AESCMAC");

            addGMacAlgorithm(provider, "AES", PREFIX + "$AESGMAC", PREFIX + "$KeyGen128");
            addPoly1305Algorithm(provider, "AES", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
        }

        /**
         * PBE AES. Not supported for now.
         *
         * @param provider
         */
        public void configurePbe(ConfigurableProvider provider){
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");

            provider.addAlgorithm("Cipher.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC128");
            provider.addAlgorithm("Cipher.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC192");
            provider.addAlgorithm("Cipher.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC256");
            provider.addAlgorithm("Cipher.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC128");
            provider.addAlgorithm("Cipher.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC192");
            provider.addAlgorithm("Cipher.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC256");

            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-CBC-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-CBC-BC","PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-CBC-BC","PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-CBC-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-CBC-BC","PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-CBC-BC","PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND128BITAES-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-BC","PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-BC","PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-BC","PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-BC","PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-CBC-BC","PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-CBC-BC","PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-CBC-BC","PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND128BITAES-BC","PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND192BITAES-BC","PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND256BITAES-BC","PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-BC","PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-BC","PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-BC","PBEWITHSHA256AND256BITAES-CBC-BC");

            provider.addAlgorithm("Cipher.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
            provider.addAlgorithm("Cipher.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
            provider.addAlgorithm("Cipher.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");

            provider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And128BitAESCBCOpenSSL");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And192BitAESCBCOpenSSL");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And256BitAESCBCOpenSSL");

            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd128BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd192BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd256BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And128BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And192BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And256BitAESBC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND128BITAES-CBC-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND192BITAES-CBC-BC","PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND256BITAES-CBC-BC","PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND128BITAES-CBC-BC","PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND192BITAES-CBC-BC","PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND256BITAES-CBC-BC","PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-CBC-BC","PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-CBC-BC","PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-CBC-BC","PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-BC","PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-BC","PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-BC","PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");

            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND128BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND192BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND256BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND128BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND192BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND256BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND128BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND192BITAES-CBC-BC","PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND256BITAES-CBC-BC","PKCS12PBE");

            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
        }
    }
}
