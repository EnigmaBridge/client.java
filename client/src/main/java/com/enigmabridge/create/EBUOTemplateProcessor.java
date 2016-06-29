package com.enigmabridge.create;

import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBException;
import com.enigmabridge.EBInvalidException;
import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.comm.EBProcessDataCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        byte[] template = this.template.getTemplate();

        // Fill in template keys
        template = fillInKeys(template);

        // TODO: encrypt.
        

    }

    protected byte[] fillInKeys(byte[] template){
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
}
