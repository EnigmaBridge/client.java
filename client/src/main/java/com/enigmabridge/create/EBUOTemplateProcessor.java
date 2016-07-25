package com.enigmabridge.create;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.EBCommUtils;
import com.enigmabridge.comm.EBProcessDataCipher;
import com.enigmabridge.comm.PKCS7Padding;
import org.bouncycastle.crypto.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
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

    /**
     * List of all keys actually used. If no key was provided, we may have generated some.
     */
    protected final List<EBUOTemplateKey> templateKeysUsed = new LinkedList<EBUOTemplateKey>();

    protected byte[] tek;
    protected byte[] tmk;

    public EBUOTemplateProcessor() {
    }

    public EBUOTemplateProcessor(EBUOTemplateResponse template, List<EBUOTemplateKey> keys) {
        this.template = template;
        this.keys = keys;
    }

    /**
     * Builds user object from the template &amp; provided keys.
     * @return byte array with filled in template
     */
    public byte[] build(){
        if (rand == null){
            rand = new SecureRandom();
        }

        // Template to fill in.
        byte[] tplSrc = this.template.getTemplate();
        byte[] tpl = new byte[tplSrc.length + 4*32];
        System.arraycopy(tplSrc, 0, tpl, 0, tplSrc.length);

        // Fill in template keys
        fillInKeys(tpl);

        // Set bits accordingly
        resetBits(tpl, this.template.getFlagOffset());

        // Encrypt (AES) + MAC.
        final int encOffset = (int)template.getEncryptionOffset();
        if ((encOffset & 7) != 0){
            throw new EBInvalidException("Encryption offset position has to be byte aligned: " + encOffset);
        }

        int encryptedTplLen = encryptAndMac(tpl, encOffset/8, tplSrc.length - encOffset/8);

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
        roff = EBCommUtils.setShort(result, roff, (short)encryptedTplLen);
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
            final String type = offset.getType();
            EBUOTemplateKey keyToUse = keyMap.get(type);

            if (keyToUse == null){
                //TODO: if key was set client generated, take care it was really set
                //LOG.debug("Key not found: " + type);

                // Comm keys
                if (Constants.KEY_COMM_ENC.equalsIgnoreCase(type) || Constants.KEY_COMM_MAC.equalsIgnoreCase(type)){
                    final byte[] tmpKey = new byte[(int) (offset.getLength()/8)];
                    rand.nextBytes(tmpKey);

                    keyToUse = new EBUOTemplateKey(type, tmpKey);
                }

                // Comm keys next - if was not generated by the client, generate randomly so it is not security vulnerability.
                if (Constants.KEY_COMM_ENC_NEXT.equalsIgnoreCase(type) || Constants.KEY_COMM_MAC_NEXT.equalsIgnoreCase(type)){
                    final byte[] tmpKey = new byte[(int) (offset.getLength()/8)];
                    rand.nextBytes(tmpKey);

                    keyToUse = new EBUOTemplateKey(type, tmpKey);
                }

                // Billing - is client? Generate new one.
                if (Constants.KEY_BILLING.equalsIgnoreCase(type)){
                    final byte[] tmpKey = new byte[(int) (offset.getLength()/8)];
                    rand.nextBytes(tmpKey);

                    keyToUse = new EBUOTemplateKey(type, tmpKey);
                }

                if (keyToUse == null){
                    continue;
                }
            }

            // Encode key to the provided buffer.
            keyToUse.encodeTo(template, offset.getOffset(), offset.getLength());

            // Store key used to the list. If the key was generated now, user may need it later (e.g., comm keys, billing key).
            getTemplateKeysUsed().add(keyToUse);
        }
    }

    /**
     * Resets particular key gen bits in the template to finish the template.
     * @param template template to set
     * @param flagOffset offset to flags field in the template, provided by the server
     */
    protected void resetBits(byte[] template, long flagOffset){
        // Reset comm key flag - generated by client.
        // 0x8 position, in short. flagOffset points to MSB byte of the short.
        template[(int) (flagOffset/8 + 1)] &= ~0x8;

        // Reset app key flag - generated by client?
        boolean appKeyClientGenerated = false;
        for(EBUOTemplateKey key : keys){
            if (Constants.KEY_APP.equalsIgnoreCase(key.getType())){
                appKeyClientGenerated = true;
            }
        }

        // App key has 0x10.
        if (appKeyClientGenerated){
            template[(int) (flagOffset/8 + 1)] &= ~0x10;
        }
    }

    /**
     * Protects given part of the buffer.
     * Encryption is performed in the buffer, so it has to be long enough.
     *
     * @param tpl buffer to protect
     * @param offset offset to start, in bytes.
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
        if (!key.getType().startsWith("rsa")){
            throw new EBInvalidException("Only RSA keys are supported by now");
        }

        // TAG|len-2B|value. 81 = exponent, 82 = modulus
        byte keyVal[] = key.getPublicKey();
        final RSAPublicKeySpec keySpec = EBCreateUtils.readSerializedRSAPublicKey(keyVal);

        try {
            final KeyFactory rsaFact = KeyFactory.getInstance("RSA", "BC");
            return rsaFact.generatePublic(keySpec);

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

    public List<EBUOTemplateKey> getTemplateKeysUsed() {
        return templateKeysUsed;
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
