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
    protected static final int REQ_PLAINAES = 1;
    protected static final int REQ_RSA1024 = 2;
    protected static final int REQ_RSA2048 = 3;
    protected static final int REQ_AUTH_NEWUSERCTX = 4;
    protected static final int REQ_AUTH_HOTP = 5;
    protected static final int REQ_AUTH_PASSWD = 6;
    protected static final int REQ_AUTH_UPDATEUSERCTX = 7;
    protected static final int REQ_HMAC = 8;
    protected static final int REQ_SCRAMBLE = 9;
    protected static final int REQ_ENSCRAMBLE = 10;
    protected static final int REQ_FP192SIGN = 11;
    protected static final int REQ_TOKENIZE = 12;
    protected static final int REQ_DETOKENIZE = 13;
    protected static final int REQ_TOKENIZEWRAP = 14;
    protected static final int REQ_RANDOMDATA = 15;

    public static final UserObjectType TYPE_PLAINAES = UserObjectType.valueOf(REQ_PLAINAES);
    public static final UserObjectType TYPE_RSA1024 = UserObjectType.valueOf(REQ_RSA1024);
    public static final UserObjectType TYPE_RSA2048 = UserObjectType.valueOf(REQ_RSA2048);
    public static final UserObjectType TYPE_RANDOM = UserObjectType.valueOf(REQ_RANDOMDATA);

    /**
     * Serialized information goes here.
     */
    protected long backingBuffer = INVALID_TYPE;
    public static final UserObjectType INVALID = UserObjectType.valueOf(INVALID_TYPE);

    public static abstract class AbstractBuilder<T extends UserObjectType, B extends AbstractBuilder> {
        String alg;
        int keyLen;

        public B setRequestType(EBRequestType a) {
            getObj().backingBuffer = getCodeFromRequestType(a);
            return getThisBuilder();
        }

        public B setAlgorithm(String alg) {
            this.alg = alg;
            return getThisBuilder();
        }

        public B setKeyLen(int keyLen){
            this.keyLen = keyLen;
            return getThisBuilder();
        }

        public B setType(long buffer){
            getObj().backingBuffer = buffer;
            return getThisBuilder();
        }

        public T build(){
            if (getObj().backingBuffer == INVALID_TYPE){
                if ("AES".equals(alg) && keyLen == 256){
                    getObj().backingBuffer = getCodeFromRequestType(EBRequestType.PLAINAES);
                } else if ("RSA".equals(alg) && keyLen == 1024){
                    getObj().backingBuffer = getCodeFromRequestType(EBRequestType.RSA1024);
                } else if ("RSA".equals(alg) && keyLen == 2048){
                    getObj().backingBuffer = getCodeFromRequestType(EBRequestType.RSA2048);
                }
            }

            if (getObj().backingBuffer == INVALID_TYPE){
                throw new IllegalArgumentException("Invalid type");
            }

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

    UserObjectType(long backingBuffer) {
        this.backingBuffer = backingBuffer;
    }

    UserObjectType(EBRequestType type) {
        this.backingBuffer = getCodeFromRequestType(type);
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
     * Builder from the backing buffer.
     * @param type
     * @return
     */
    public static UserObjectType valueOf(EBRequestType type){
        return new UserObjectType(type);
    }

    /**
     * Returns request type encoded in the UO.
     * TODO: more requests for one UOType?
     * @return
     */
    public EBRequestType getRequestType(){
        if (backingBuffer == INVALID_TYPE){
            return null;
        }

        return getRequestTypeFromCode((int) (backingBuffer & TYPE_MASK));
    }

    /**
     * Returns algorithm for given UO.
     * May Return AES, RSA, ... null if undefined for this user object.
     * @return
     */
    public String getAlgorithm(){
        final EBRequestType requestType = getRequestType();
        switch (requestType){
            case PLAINAES:
                return "AES";
            case RSA1024:
            case RSA2048:
                return "RSA";
            default:
                return null;
        }
    }

    /**
     * Returns key type, if represents key.
     * @return
     */
    public UserObjectKeyType getKeyType(){
        final EBRequestType requestType = getRequestType();
        switch (requestType){
            case PLAINAES:
                return UserObjectKeyType.SECRET;
            case RSA1024:
            case RSA2048:
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
        final EBRequestType requestType = getRequestType();
        switch (requestType){
            case PLAINAES:
                return 256;
            case RSA1024:
                return 1024;
            case RSA2048:
                return 2048;
            default:
                return 0;
        }
    }

    public long getBackingBuffer() {
        return backingBuffer;
    }

    void setBackingBuffer(long backingBuffer) {
        this.backingBuffer = backingBuffer;
    }

    /**
     * Serializes to JSON.
     * @param parent
     * @param key
     * @return
     */
    public Object toJSON(JSONObject parent, String key){
        if (parent == null){
            return getBackingBuffer();
        }

        parent.put(key, getBackingBuffer());
        return getBackingBuffer();
    }

    public static UserObjectType fromJSON(JSONObject parent, String key){
        if (parent == null || !parent.has(key)){
            return null;
        }

        return valueOf(parent.getLong(key));
    }

    private static EBRequestType getRequestTypeFromCode(int code){
        switch(code){
            case REQ_PLAINAES:
                return EBRequestType.PLAINAES;
            case REQ_RSA1024:
                return EBRequestType.RSA1024;
            case REQ_RSA2048:
                return EBRequestType.RSA2048;
            case REQ_AUTH_NEWUSERCTX:
                return EBRequestType.AUTH_NEWUSERCTX;
            case REQ_AUTH_HOTP:
                return EBRequestType.AUTH_HOTP;
            case REQ_AUTH_PASSWD:
                return EBRequestType.AUTH_PASSWD;
            case REQ_AUTH_UPDATEUSERCTX:
                return EBRequestType.AUTH_UPDATEUSERCTX;
            case REQ_HMAC:
                return EBRequestType.HMAC;
            case REQ_SCRAMBLE:
                return EBRequestType.SCRAMBLE;
            case REQ_ENSCRAMBLE:
                return EBRequestType.ENSCRAMBLE;
            case REQ_FP192SIGN:
                return EBRequestType.FP192SIGN;
            case REQ_TOKENIZE:
                return EBRequestType.TOKENIZE;
            case REQ_DETOKENIZE:
                return EBRequestType.DETOKENIZE;
            case REQ_TOKENIZEWRAP:
                return EBRequestType.TOKENIZEWRAP;
            case REQ_RANDOMDATA:
                return EBRequestType.RANDOMDATA;
            default: return null;
        }
    }

    private static int getCodeFromRequestType(EBRequestType request){
        switch(request){
            case PLAINAES:
                return REQ_PLAINAES;
            case RSA1024:
                return REQ_RSA1024;
            case RSA2048:
                return REQ_RSA2048;
            case AUTH_NEWUSERCTX:
                return REQ_AUTH_NEWUSERCTX;
            case AUTH_HOTP:
                return REQ_AUTH_HOTP;
            case AUTH_PASSWD:
                return REQ_AUTH_PASSWD;
            case AUTH_UPDATEUSERCTX:
                return REQ_AUTH_UPDATEUSERCTX;
            case HMAC:
                return REQ_HMAC;
            case SCRAMBLE:
                return REQ_SCRAMBLE;
            case ENSCRAMBLE:
                return REQ_ENSCRAMBLE;
            case FP192SIGN:
                return REQ_FP192SIGN;
            case TOKENIZE:
                return REQ_TOKENIZE;
            case DETOKENIZE:
                return REQ_DETOKENIZE;
            case TOKENIZEWRAP:
                return REQ_TOKENIZEWRAP;
            case RANDOMDATA:
                return REQ_RANDOMDATA;
            default: return -1;
        }
    }

    @Override
    public String toString() {
        return "{" + backingBuffer + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserObjectType that = (UserObjectType) o;

        return backingBuffer == that.backingBuffer;

    }

    @Override
    public int hashCode() {
        return (int) (backingBuffer ^ (backingBuffer >>> 32));
    }
}
