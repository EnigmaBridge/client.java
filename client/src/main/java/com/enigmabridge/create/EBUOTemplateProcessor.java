package com.enigmabridge.create;

import com.enigmabridge.EBCommKeys;
import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBException;
import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.comm.EBProcessDataCall;
import com.enigmabridge.comm.EBProcessDataCipher;
import com.enigmabridge.comm.PKCS7Padding;
import org.bouncycastle.crypto.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
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

    public EBUOTemplateProcessor() {
    }

    public EBUOTemplateProcessor(EBUOTemplateResponse template, List<EBUOTemplateKey> keys) {
        this.template = template;
        this.keys = keys;
    }

    /**
     * Builds user object from the template & provided keys.
     */
    public void build(){
        // Template to fill in.
        byte[] tplSrc = this.template.getTemplate();
        byte[] tpl = new byte[tplSrc.length + 4*32];
        System.arraycopy(tplSrc, 0, tpl, 0, tplSrc.length);

        // Fill in template keys
        fillInKeys(tpl);

        // Encrypt.
        final int encOffset = (int)template.getEncryptionOffset();
        if ((encOffset % 7) != 0){
            throw new EBInvalidException("Encryption offset position has to be byte aligned");
        }

        if (rand == null){
            rand = new SecureRandom();
        }

        byte[] tek = new byte[32];
        byte[] tmk = new byte[32];
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
            encLen = aes.doFinal(tpl, encOffset, tpl.length - encOffset, tpl, encOffset);

        } catch (ShortBufferException ex){
            throw new EBCryptoException("ShortBufferException - should not happen", ex);
        } catch (BadPaddingException e) {
            throw new EBCryptoException("BadPaddingException - should not happen", e);
        } catch (IllegalBlockSizeException e) {
            throw new EBCryptoException("IllegalBlockSizeException - should not happen", e);
        }

        // Pad for MAC - scheme design.
        paddedLen = PKCS7Padding.pad(tpl, 0, encOffset + encLen, 16);

        // MAC.
        mac.update(tpl, 0, paddedLen);
        macLen = mac.doFinal(tpl, paddedLen);
        encryptedTplLen = paddedLen + macLen;
    }

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

        return template;
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
