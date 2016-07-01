package com.enigmabridge.create;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBException;
import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.*;
import org.bouncycastle.crypto.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Processing template returned by the server.
 * Generates user object for import.
 *
 * Created by dusanklinec on 29.06.16.
 */
public class EBUOTemplateProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(EBUOTemplateProcessor.class);

    protected EBUOTemplateResponse template;
    protected List<EBUOTemplateKey> keys;
    protected SecureRandom rand;

    /**
     * Import key used in the last build.
     */
    protected EBUOTemplateImportKey keyUsed;

    protected byte[] tek;
    protected byte[] tmk;

    public EBUOTemplateProcessor() {
    }

    public EBUOTemplateProcessor(EBUOTemplateResponse template, List<EBUOTemplateKey> keys) {
        this.template = template;
        this.keys = keys;
    }

    /**
     * Builds user object from the template & provided keys.
     */
    public byte[] build(){
        // Template to fill in.
        byte[] tplSrc = this.template.getTemplate();
        byte[] tpl = new byte[tplSrc.length + 4*32];
        System.arraycopy(tplSrc, 0, tpl, 0, tplSrc.length);

        // Fill in template keys
        fillInKeys(tpl);

        // Encrypt (AES) + MAC.
        final int encOffset = (int)template.getEncryptionOffset();
        if ((encOffset % 7) != 0){
            throw new EBInvalidException("Encryption offset position has to be byte aligned");
        }

        if (rand == null){
            rand = new SecureRandom();
        }

        int encryptedTplLen = encryptAndMac(tpl, encOffset, tplSrc.length - encOffset);

        // RSA encryption: UOID-4B | TEK | TMK
        keyUsed = getBestImportKey();
        if (keyUsed == null){
            throw new EBInvalidException("No supported import key found");
        }

        byte[] encPlain = new byte[32+32+4];
        EBCommUtils.setInt(encPlain, 0, (int)template.getObjectId());
        System.arraycopy(tek, 0, encPlain, 4,    tek.length);
        System.arraycopy(tmk, 0, encPlain, 32+4, tmk.length);

        final byte[] importEncrypted = encryptImportKey(keyUsed, encPlain);

        // Final template: 0xa1 | len-2B | RSA-ENC-BLOB | 0xa2 | len-2B | encrypted-maced-template
        final byte[] result = new byte[1+2+importEncrypted.length + 1+2+encryptedTplLen];
        int roff = 0;

        result[roff] = (byte)0xa1; roff+=1;
        roff = EBCommUtils.setShort(result, roff, (short)importEncrypted.length);
        System.arraycopy(importEncrypted, 0, result, roff, importEncrypted.length);
        roff += importEncrypted.length;

        result[roff] = (byte)0xa2; roff+=1;
        roff = EBCommUtils.setShort(result, roff, (short)importEncrypted.length);
        System.arraycopy(tpl, 0, result, roff, encryptedTplLen);

        return result;
    }

    /**
     * Fills given keys to the template
     * @param template byte template to fill in
     */
    protected void fillInKeys(byte[] template){
        // Map keyType -> key.
        final List<EBUOTemplateKey> keys = getKeys();
        final Map<String, EBUOTemplateKey> keyMap = new HashMap<String, EBUOTemplateKey>();
        for(EBUOTemplateKey key : keys){
            keyMap.put(key.getType(), key);
        }

        // Fill in template keys
        final List<EBUOTemplateKeyOffset> keyOffsets = this.template.getKeyOffsets();
        for(EBUOTemplateKeyOffset offset : keyOffsets){
            final EBUOTemplateKey key = keyMap.get(offset.getType());
            if (key == null){
                LOG.debug("Key not found: " + offset.getType());
                continue;
            }

            final byte[] keyVal = key.getKey();
            if (keyVal.length*8 != offset.getLength()){
                throw new EBCryptoException("Invalid key size, exp: " + offset.getLength() + ", given: " + keyVal.length*8);
            }

            final long cOffset = offset.getOffset();
            if ((cOffset & 7) != 0){
                throw new EBInvalidException("Key position has to be byte aligned");
            }

            for(int idx=0, len=(int)offset.getLength(); idx < len; ++idx){
                template[(int)cOffset + idx] = keyVal[idx];
            }
        }
    }

    /**
     * Protects given part of the buffer.
     * Encryption is performed in the buffer, so it has to be long enough.
     *
     * @param tpl buffer to protect
     * @param offset offset to start
     * @param length length from the offset to protect
     * @return length of the result.
     */
    protected int encryptAndMac(byte[] tpl, int offset, int length){
        tek = new byte[32];
        tmk = new byte[32];
        rand.nextBytes(tek);
        rand.nextBytes(tmk);

        final EBProcessDataCipher ebCipher = EBProcessDataCipher.initCipher(true, new EBCommKeys(tek, tmk));
        final Cipher aes = ebCipher.getEnc();
        final Mac mac = ebCipher.getMac();
        int encLen = 0;
        int paddedLen = 0;
        int macLen = 0;
        int encryptedTplLen = 0;

        try {
            encLen = aes.doFinal(tpl, offset, length, tpl, offset);

        } catch (ShortBufferException ex){
            throw new EBCryptoException("ShortBufferException - should not happen", ex);
        } catch (BadPaddingException e) {
            throw new EBCryptoException("BadPaddingException - should not happen", e);
        } catch (IllegalBlockSizeException e) {
            throw new EBCryptoException("IllegalBlockSizeException - should not happen", e);
        }

        // Pad for MAC - scheme design.
        paddedLen = PKCS7Padding.pad(tpl, 0, offset + encLen, 16);

        // MAC.
        mac.update(tpl, 0, paddedLen);
        macLen = mac.doFinal(tpl, paddedLen);
        encryptedTplLen = paddedLen + macLen;
        return encryptedTplLen;
    }

    /**
     * Protects plain buffer with the given import key.
     *
     * @param key import key to use
     * @param plain plain buffer to protect/encrypt
     * @return byte result of the operation, new buffer.
     */
    protected byte[] encryptImportKey(EBUOTemplateImportKey key, byte[] plain) {
        final Key rsaPubKey = readSerializedRSAPublicKey(key);

        try {
            final Cipher rsaCipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPubKey);
            return rsaCipher.doFinal(plain);

        } catch (NoSuchAlgorithmException e) {
            throw new EBCryptoException("No RSA cipher available", e);
        } catch (InvalidKeyException e) {
            throw new EBCryptoException("RSA key cannot be used", e);
        } catch (NoSuchPaddingException e) {
            throw new EBCryptoException("Illegal padding", e);
        } catch (BadPaddingException e) {
            throw new EBCryptoException("Bad padding", e);
        } catch (IllegalBlockSizeException e) {
            throw new EBCryptoException("Illegal block size", e);
        } catch (NoSuchProviderException e) {
            throw new EBCryptoException("Crypto provider not found", e);
        }
    }

    /**
     * Reads serialized import public key, builds Java JCA JCE key that can be used with encryption.
     * @param key RSA public key to deserialize.
     * @return java crypto key
     */
    protected Key readSerializedRSAPublicKey(EBUOTemplateImportKey key){
        // Read serialized import public key.
        BigInteger exp = null;
        BigInteger mod = null;
        if (!key.getType().startsWith("rsa")){
            throw new EBInvalidException("Only RSA keys are supported by now");
        }

        // TAG|len-2B|value. 81 = exponent, 82 = modulus
        byte keyVal[] = key.getPublicKey();
        byte tmpDat[] = null;
        int tag, len, pos, ln = keyVal.length;
        for(pos = 0; pos < ln;){
            tag = keyVal[pos];  pos += 1;
            len = EBCommUtils.getShort(keyVal, pos); pos += 2;
            switch(tag){
                case 0x81:
                    tmpDat = new byte[len];
                    System.arraycopy(keyVal, pos, tmpDat, 0, len);

                    exp = new BigInteger(tmpDat);
                    break;

                case 0x82:
                    tmpDat = new byte[len];
                    System.arraycopy(keyVal, pos, tmpDat, 0, len);

                    mod = new BigInteger(tmpDat);
                    break;
                default:
                    break;
            }

            pos += len;
        }

        if (exp == null || mod == null){
            throw new EBInvalidException("RSA public key is malformed");
        }

        try {
            final KeyFactory rsaFact = KeyFactory.getInstance("RSA", "BC");
            final RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(mod, exp);

            return rsaFact.generatePublic(pubKeySpec);

        } catch (NoSuchAlgorithmException e) {
            throw new EBCryptoException("No RSA key factory", e);
        } catch (InvalidKeySpecException e) {
            throw new EBCryptoException("Invalid RSA public key", e);
        } catch (NoSuchProviderException e) {
            throw new EBCryptoException("Crypto provider not found", e);
        }
    }

    /**
     * Returns the best import key - strongest supported.
     * @return EBUOTemplateKey
     */
    protected EBUOTemplateImportKey getBestImportKey(){
        EBUOTemplateImportKey rsa1024 = null;
        EBUOTemplateImportKey rsa2048 = null;
        final List<EBUOTemplateImportKey> keys = getTemplate().getImportKeys();

        for(EBUOTemplateImportKey key : keys){
            if ("rsa1024".equalsIgnoreCase(key.getType())){
                rsa1024 = key;
            }
            if ("rsa2048".equalsIgnoreCase(key.getType())){
                rsa2048 = key;
            }
        }

        return rsa2048 != null ? rsa2048 : rsa1024;
    }

    public EBUOTemplateResponse getTemplate() {
        return template;
    }

    public List<EBUOTemplateKey> getKeys() {
        if (keys == null){
            keys = new LinkedList<EBUOTemplateKey>();
        }
        return keys;
    }

    public SecureRandom getRand() {
        return rand;
    }

    public EBUOTemplateImportKey getKeyUsed() {
        return keyUsed;
    }

    public EBUOTemplateProcessor setTemplate(EBUOTemplateResponse template) {
        this.template = template;
        return this;
    }

    public EBUOTemplateProcessor setKeys(List<EBUOTemplateKey> keys) {
        this.keys = keys;
        return this;
    }

    public EBUOTemplateProcessor addKey(EBUOTemplateKey key) {
        getKeys().add(key);
        return this;
    }

    public EBUOTemplateProcessor setRand(SecureRandom rand) {
        this.rand = rand;
        return this;
    }
}
