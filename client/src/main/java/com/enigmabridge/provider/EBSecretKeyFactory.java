package com.enigmabridge.provider;

import com.enigmabridge.EBEngine;
import com.enigmabridge.EBURLConfig;
import com.enigmabridge.UserObjectKeyType;
import com.enigmabridge.UserObjectType;
import com.enigmabridge.create.Constants;
import com.enigmabridge.provider.asn1.EBASNUtils;
import com.enigmabridge.provider.asn1.EBEncodableUOKey;
import com.enigmabridge.provider.asn1.EBJSONEncodedUOKey;
import com.enigmabridge.provider.specs.EBConfigurationUOKeySpec;
import com.enigmabridge.provider.specs.EBJSONEncodedUOKeySpec;
import com.enigmabridge.provider.specs.EBSecretKeySpec;
import com.enigmabridge.provider.specs.EBSymmetricKeyGenTypes;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * SecretKeyFactory translates between KeySpecs and Keys.
 * In our case it helps to createUO from the existing key - function very similar to KeyGenerator.
 *
 * Reversed process - creating KeySpecs from the Key is not possible as the keys are not extractable.
 *
 * Created by dusanklinec on 12.07.16.
 */
public class EBSecretKeyFactory extends SecretKeyFactorySpi {
    protected final EnigmaProvider provider;
    protected final EBSymmetricKeyCreator.Builder keyCreatorBld = new EBSymmetricKeyCreator.Builder();

    protected EBSymmetricKeyCreator keyCreator;
    protected EBEngine engine;
    protected SecureRandom random;
    private EBSymmetricKeyGenTypes keyType = EBSymmetricKeyGenTypes.BOTH;

    private static final String FIELD_SYMMETRIC_KEY = "symKey";

    public EBSecretKeyFactory(EnigmaProvider provider) {
        this.provider = provider;
    }

    public EBSecretKeyFactory(EnigmaProvider provider, String algorithm) {
        this.provider = provider;
    }

    @Override
    protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
        if(keySpec == null) {
            throw new InvalidKeySpecException("KeySpec must not be null");

        } else if(keySpec instanceof EBSecretKeySpec){
            final EBSecretKeySpec ebSpec = (EBSecretKeySpec) keySpec;
            initFromSpecs(ebSpec);

            if (ebSpec.getTemplateRequest() != null){
                keyCreatorBld.setGetTemplateRequest(ebSpec.getTemplateRequest());
            }

        } else if(keySpec instanceof SecretKeySpec) {
            initFromSpecs(null);

        } else if (keySpec instanceof PKCS8EncodedKeySpec) {
            initFromSpecs(null);

            final PKCS8EncodedKeySpec p8 = (PKCS8EncodedKeySpec) keySpec;
            return fromPkcs8Encoded(p8);

        } else if (keySpec instanceof EBJSONEncodedUOKeySpec){
            final JSONObject json = ((EBJSONEncodedUOKeySpec) keySpec).getJson();
            try {
                final EBSymmetricKey tmpKey = new EBSymmetricKey.Builder()
                        .setEngine(engine)
                        .setJson(json)
                        .build();

                if (tmpKey.getKeyType() != UserObjectKeyType.SECRET){
                    throw new InvalidKeySpecException("Key type is invalid: " + tmpKey.getKeyType());
                }

                return tmpKey;

            } catch (IOException e) {
                throw new InvalidKeySpecException("Key could not be parsed", e);
            }

        } else if (keySpec instanceof EBConfigurationUOKeySpec){
            final String configLine = ((EBConfigurationUOKeySpec) keySpec).getConfigLine();
            try {
                final EBURLConfig config = new EBURLConfig.Builder().setURLConfig(configLine).build();
                final JSONObject keyJson = config.getElement(FIELD_SYMMETRIC_KEY);

                final EBSymmetricKey tmpKey = new EBSymmetricKey.Builder()
                        .setEngine(engine)
                        .setJson(keyJson)
                        .build();

                if (tmpKey.getKeyType() != UserObjectKeyType.SECRET){
                    throw new InvalidKeySpecException("Key type is invalid: " + tmpKey.getKeyType());
                }

                return tmpKey;

            } catch (IOException e) {
                throw new InvalidKeySpecException("Key could not be parsed", e);
            }

        } else {
            throw new InvalidKeySpecException("Unsupported spec: " + keySpec.getClass().getName());
        }

        final SecretKeySpec secKeySpec = (SecretKeySpec) keySpec;
        final String algorithm = secKeySpec.getAlgorithm();
        if (!"AES".equalsIgnoreCase(algorithm)){
            throw new InvalidKeySpecException("Unsupported key: " + algorithm);
        }

        keyCreatorBld.setEngine(this.engine)
                .setRandom(this.random)
                .setKeyType(keyType)
                .setUoType(UserObjectType.OBJ_PLAINAES);

        keyCreator = keyCreatorBld.build();
        keyCreator
                .setUoTypeFunction(UserObjectType.TYPE_PLAINAES)
                .setAppKey(secKeySpec.getEncoded())
                .setAppKeyGeneration(Constants.GENKEY_CLIENT);

        return keyCreator.engineGenerateKey();
    }

    protected void initFromSpecs(final EBSecretKeySpec ebSpec){
        if (ebSpec != null && ebSpec.getEBEngine() != null){
            this.engine = ebSpec.getEBEngine();
        }

        if (this.engine == null){
            this.engine = provider.getEngine();
        }

        if (this.random == null && this.engine != null){
            this.random = this.engine.getRnd();
        }

        if (this.random == null){
            this.random = new SecureRandom();
        }

        if (ebSpec != null){
            this.keyType = ebSpec.getKeyType();
        }
    }

    protected SecretKey fromPkcs8Encoded(PKCS8EncodedKeySpec p8) throws InvalidKeySpecException {
        try {
            final EBEncodableUOKey keyInfo = EBEncodableUOKey.getInstance(p8.getEncoded());
            final ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();

            if (EBASNUtils.eb_aes.equals(algOid)) {
                final EBJSONEncodedUOKey encKey = EBJSONEncodedUOKey.getInstance(keyInfo.parsePrivateKey());

                return new EBSymmetricKey.Builder()
                        .setEngine(engine)
                        .setAsn(encKey)
                        .build();

            } else {
                throw new InvalidKeySpecException("algorithm identifier " + algOid + " in key not recognised");
            }
        }catch (IOException io){
            throw new InvalidKeySpecException("IOException when parsing key from PKCS8 encoded form", io);
        }

    }

    @Override
    protected KeySpec engineGetKeySpec(SecretKey secretKey, Class aClass) throws InvalidKeySpecException {
        // JSON export of the object.
        if (secretKey instanceof EBSymmetricKey){
            final EBSymmetricKey ebKey = (EBSymmetricKey) secretKey;

            if(EBJSONEncodedUOKeySpec.class.isAssignableFrom(aClass)){
                return new EBJSONEncodedUOKeySpec(ebKey.toJSON(null, true));

            } else if(EBConfigurationUOKeySpec.class.isAssignableFrom(aClass)){
                try {
                    return new EBConfigurationUOKeySpec(new EBURLConfig.Builder()
                            .setFromEngine(engine)
                            .addElement(ebKey, FIELD_SYMMETRIC_KEY)
                            .build()
                            .toString());

                } catch (MalformedURLException e) {
                    throw new InvalidKeySpecException("Exception in generating specs", e);
                }

            } else {
                throw new UnsupportedOperationException("EB provider does not allow to extract keys");
            }
        }

        throw new UnsupportedOperationException("EB provider does not allow to extract keys");
    }

    @Override
    protected SecretKey engineTranslateKey(SecretKey secretKey) throws InvalidKeyException {
        if (secretKey instanceof SecretKeySpec) {
            try {
                return engineGenerateSecret((SecretKeySpec) secretKey);
            } catch (InvalidKeySpecException e) {
                throw new InvalidKeyException("Cannot translate provided key", e);
            }

        } else {
            throw new UnsupportedOperationException("Unsupported key translation");
        }
    }
}
