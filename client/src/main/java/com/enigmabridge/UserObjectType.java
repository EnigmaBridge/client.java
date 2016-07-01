package com.enigmabridge;

import com.enigmabridge.comm.EBRequestType;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Encapsulates allowed operations with user object and another misc info.
 *
 * Created by dusanklinec on 05.05.16.
 */
public class UserObjectType implements Serializable{
    public static final long INVALID_TYPE = -1;
    protected static final int TYPE_MASK = 0xffff;

    // Request type from the lower bytes.
    public static final int TYPE_HMAC = 0x0001;
    public static final int TYPE_SCRAMBLE = 0x0002;
    public static final int TYPE_ENSCRAMBLE = 0x0003;
    public static final int TYPE_PLAINAES = 0x0004;
    public static final int TYPE_RSA1024DECRYPT_NOPAD = 0x0005;
    public static final int TYPE_RSA2048DECRYPT_NOPAD = 0x0006;
    public static final int TYPE_EC_FP192SIGN = 0x0007;
    public static final int TYPE_AUTH_HOTP = 0x0008;
    public static final int TYPE_AUTH_NEW_USER_CTX = 0x0009;
    public static final int TYPE_AUTH_PASSWORD = 0x000a;
    public static final int TYPE_AUTH_UPDATE_USER_CTX = 0x000b;
    public static final int TYPE_TOKENIZE = 0x000c;
    public static final int TYPE_DETOKENIZE = 0x000d;
    public static final int TYPE_TOKENIZEWRAP = 0x000e;
    public static final int TYPE_PLAINAESDECRYPT = 0x000f;
    public static final int TYPE_RANDOMDATA = 0x0010;
    public static final int TYPE_CREATENEWUO = 0x0011;
    public static final int TYPE_RSA1024ENCRYPT_NOPAD = 0x0012;
    public static final int TYPE_RSA2048ENCRYPT_NOPAD = 0x0013;

    public static final UserObjectType OBJ_PLAINAES = UserObjectType.valueOf(TYPE_PLAINAES);
    public static final UserObjectType OBJ_PLAINAESDECRYPT = UserObjectType.valueOf(TYPE_PLAINAESDECRYPT);
    public static final UserObjectType OBJ_RSA1024 = UserObjectType.valueOf(TYPE_RSA1024DECRYPT_NOPAD);
    public static final UserObjectType OBJ_RSA2048 = UserObjectType.valueOf(TYPE_RSA2048DECRYPT_NOPAD);
    public static final UserObjectType OBJ_RANDOM = UserObjectType.valueOf(TYPE_RANDOMDATA);
    public static final UserObjectType INVALID = UserObjectType.valueOf(INVALID_TYPE);

    protected int uoType;
    protected boolean appKeyClientGenerated = false;
    protected boolean comKeyClientGenerated = false;

    public static abstract class AbstractBuilder<T extends UserObjectType, B extends AbstractBuilder> {
        public B setUoTypeFunction(int type) {
            getObj().uoType = type;
            return getThisBuilder();
        }

        public B setAppKeyClientGenerated(boolean gen) {
            getObj().appKeyClientGenerated = gen;
            return getThisBuilder();
        }

        public B setComKeyClientGenerated(boolean gen) {
            getObj().comKeyClientGenerated = gen;
            return getThisBuilder();
        }

        public B setUoType(long buffer){
            getObj().setValue(buffer);
            return getThisBuilder();
        }

        public T build(){
            return getObj();
        }

        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<UserObjectType, Builder> {
        private final UserObjectType parent = new UserObjectType();

        @Override
        public Builder getThisBuilder() {
            return this;
        }

        @Override
        public UserObjectType getObj() {
            return parent;
        }

        @Override
        public UserObjectType build() {
            super.build();
            return parent;
        }
    }

    UserObjectType() {
    }

    public UserObjectType(long serialized) {
        setValue(serialized);
    }

    public UserObjectType(int function, boolean appKeyClientGenerated, boolean comKeyClientGenerated){
        this.uoType = function;
        this.appKeyClientGenerated = appKeyClientGenerated;
        this.comKeyClientGenerated = comKeyClientGenerated;
    }

    /**
     * Builder from the backing buffer.
     * @param buffer
     * @return
     */
    public static UserObjectType valueOf(long buffer){
        return new UserObjectType(buffer);
    }

    /**
     * Returns request type encoded in the UO.
     *
     * @return uo type function
     */
    public int getUoTypeFunction(){
        return uoType;
    }

    public static String getUoTypeFunctionString(int uoType){
        switch(uoType) {
            case TYPE_HMAC:
                return "HMAC";
            case TYPE_SCRAMBLE:
                return "SCRAMBLE";
            case TYPE_ENSCRAMBLE:
                return "ENSCRAMBLE";
            case TYPE_PLAINAES:
                return "PLAINAES";
            case TYPE_RSA1024DECRYPT_NOPAD:
                return "RSA1024DECRYPT_NOPAD";
            case TYPE_RSA2048DECRYPT_NOPAD:
                return "RSA2048DECRYPT_NOPAD";
            case TYPE_EC_FP192SIGN:
                return "EC_FP192SIGN";
            case TYPE_AUTH_HOTP:
                return "AUTH_HOTP";
            case TYPE_AUTH_NEW_USER_CTX:
                return "AUTH_NEW_USER_CTX";
            case TYPE_AUTH_PASSWORD:
                return "AUTH_PASSWORD";
            case TYPE_AUTH_UPDATE_USER_CTX:
                return "AUTH_UPDATE_USER_CTX";
            case TYPE_TOKENIZE:
                return "TOKENIZE";
            case TYPE_DETOKENIZE:
                return "DETOKENIZE";
            case TYPE_TOKENIZEWRAP:
                return "TOKENIZEWRAP";
            case TYPE_PLAINAESDECRYPT:
                return "PLAINAESDECRYPT";
            case TYPE_RANDOMDATA:
                return "RANDOMDATA";
            case TYPE_CREATENEWUO:
                return "CREATENEWUO";
            case TYPE_RSA1024ENCRYPT_NOPAD:
                return "RSA1024ENCRYPT_NOPAD";
            case TYPE_RSA2048ENCRYPT_NOPAD:
                return "RSA2048ENCRYPT_NOPAD";
            default:
                return "PROCESSDATA";
        }
    }

    public String getUoTypeFunctionString(){
        return getUoTypeFunctionString(uoType);
    }

    /**
     * Returns algorithm for given UO.
     * May Return AES, RSA, ... null if undefined for this user object.
     * @return key algorithm, if key
     */
    public String getAlgorithm(){
        switch (uoType){
            case TYPE_PLAINAES:
            case TYPE_PLAINAESDECRYPT:
                return "AES";
            case TYPE_RSA1024DECRYPT_NOPAD:
            case TYPE_RSA1024ENCRYPT_NOPAD:
            case TYPE_RSA2048DECRYPT_NOPAD:
            case TYPE_RSA2048ENCRYPT_NOPAD:
                return "RSA";
            default:
                return null;
        }
    }

    /**
     * Returns key type, if represents key.
     * @return key type (secret/public/private)
     */
    public UserObjectKeyType getKeyType(){
        switch (uoType){
            case TYPE_PLAINAES:
            case TYPE_PLAINAESDECRYPT:
                return UserObjectKeyType.SECRET;
            case TYPE_RSA1024DECRYPT_NOPAD:
            case TYPE_RSA1024ENCRYPT_NOPAD:
            case TYPE_RSA2048DECRYPT_NOPAD:
            case TYPE_RSA2048ENCRYPT_NOPAD:
                return UserObjectKeyType.PRIVATE;
            default:
                return null;
        }
    }

    /**
     * Returns key length in bits if the UO represents cipher key.
     * @return key lenght in bits, 0 if not applicable.
     */
    public int keyLength(){
        switch (uoType){
            case TYPE_PLAINAES:
            case TYPE_PLAINAESDECRYPT:
                return 256;
            case TYPE_RSA1024DECRYPT_NOPAD:
            case TYPE_RSA1024ENCRYPT_NOPAD:
                return 1024;
            case TYPE_RSA2048DECRYPT_NOPAD:
            case TYPE_RSA2048ENCRYPT_NOPAD:
                return 2048;
            default:
                return 0;
        }
    }

    public static long getValue(int function, boolean appKeyClientGenerated, boolean comKeyClientGenerated){
        long type = function;
        type |= appKeyClientGenerated ? 1L<<21 : 0;
        type |= comKeyClientGenerated ? 1L<<20 : 0;
        return type;
    }

    public long getValue() {
        return getValue(uoType, appKeyClientGenerated, comKeyClientGenerated);
    }

    protected void setValue(long value){
        uoType = (int) (value & TYPE_MASK);
        appKeyClientGenerated = (value & (1L << 21)) > 0;
        comKeyClientGenerated = (value & (1L << 20)) > 0;
    }

    /**
     * Serializes to JSON.
     * @param parent
     * @param key
     * @return
     */
    public Object toJSON(JSONObject parent, String key){
        if (parent == null){
            return getValue();
        }

        parent.put(key, getValue());
        return getValue();
    }

    public static UserObjectType fromJSON(JSONObject parent, String key){
        if (parent == null || !parent.has(key)){
            return null;
        }

        return valueOf(parent.getLong(key));
    }

    @Override
    public String toString() {
        return "{" + getValue() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserObjectType that = (UserObjectType) o;

        if (uoType != that.uoType) return false;
        if (appKeyClientGenerated != that.appKeyClientGenerated) return false;
        return comKeyClientGenerated == that.comKeyClientGenerated;

    }

    @Override
    public int hashCode() {
        int result = uoType;
        result = 31 * result + (appKeyClientGenerated ? 1 : 0);
        result = 31 * result + (comKeyClientGenerated ? 1 : 0);
        return result;
    }
}
