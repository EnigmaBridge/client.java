package com.enigmabridge;

import com.enigmabridge.create.Constants;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Encapsulates allowed operations with user object and another misc info.
 *
 * Created by dusanklinec on 05.05.16.
 */
public class UserObjectType implements Serializable{
    public static final long INVALID_TYPE = -1;
    protected static final int TYPE_MASK = 0xffff;

    // Request type from the lower bytes.
    public static final int TYPE_INVALID = -1;
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

    // For forward compatibility we store UO type in the composite field
    // as EB can add another bit field in the future.
    protected long typeValue = 0;

    public static abstract class AbstractBuilder<T extends UserObjectType, B extends AbstractBuilder> {
        public B setUoTypeFunction(int type) {
            getObj().setUoTypeFunction(type);
            return getThisBuilder();
        }

        public B setAppKeyGenerationType(int gen) {
            if (gen < 0 || gen > Constants.GENKEY_ENROLL_DERIVED){
                throw new IllegalArgumentException("Illegal argument for app key generation type");
            }
            getObj().setAppKeyGenerationType(gen);
            return getThisBuilder();
        }

        public B setComKeyGenerationType(int gen) {
            if (gen != Constants.GENKEY_LEGACY_ENROLL_RANDOM && gen != Constants.GENKEY_CLIENT){
                throw new IllegalArgumentException("Illegal argument for comm key generation type");
            }
            getObj().setComKeyGenerationType(gen);
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

    public UserObjectType(int function, int appKeyClientGenerated, int comKeyClientGenerated){
        setUoTypeFunction(function);
        setAppKeyGenerationType(appKeyClientGenerated);
        setComKeyGenerationType(comKeyClientGenerated);
    }

    protected void setUoTypeFunction(int function){
        if ((function & TYPE_MASK) != function){
            throw new IllegalArgumentException("Illegal function argument");
        }

        this.typeValue &= ~TYPE_MASK;
        this.typeValue |= function & TYPE_MASK;
    }

    protected void setAppKeyGenerationType(int gen){
        if ((gen & 0x7) != gen){
            throw new IllegalArgumentException("Illegal function argument");
        }

        this.typeValue &= ~(0x7 << 21);
        this.typeValue |=   gen << 21;
    }

    protected void setComKeyGenerationType(int gen){
        if ((gen & 0x1) != gen){
            throw new IllegalArgumentException("Illegal function argument");
        }

        this.typeValue &= ~(0x1 << 20);
        this.typeValue |=   gen << 20;
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
        return (int) (typeValue & TYPE_MASK);
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
        return getUoTypeFunctionString(getUoTypeFunction());
    }

    /**
     * Returns algorithm for given UO.
     * May Return AES, RSA, ... null if undefined for this user object.
     * @return key algorithm, if key
     */
    public String getAlgorithm(){
        switch (getUoTypeFunction()){
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
        switch (getUoTypeFunction()){
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
        switch (getUoTypeFunction()){
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

    /**
     * Returns true if the UO represents encryption key.
     * @return true if encryption key
     */
    public boolean isCipherObject(boolean forEncryption){
        switch (getUoTypeFunction()){
            case TYPE_PLAINAES:
            case TYPE_RSA1024ENCRYPT_NOPAD:
            case TYPE_RSA2048ENCRYPT_NOPAD:
                return forEncryption;
            case TYPE_PLAINAESDECRYPT:
            case TYPE_RSA1024DECRYPT_NOPAD:
            case TYPE_RSA2048DECRYPT_NOPAD:
                return !forEncryption;
            default:
                throw new EBInvalidException("UO does not represent encryption");
        }
    }

    /**
     * Returns true if the UO represents encryption key.
     * @return true if encryption key
     */
    public boolean isEncryptionObject(){
        return isCipherObject(true);
    }

    /**
     * Returns true if the UO represents decryption key.
     * @return true if decryption key
     */
    public boolean isDecryptionObject(){
        return isCipherObject(false);
    }

    /**
     * If UO type represents encryption this function returns UO type function
     * representing inversion encryption operation than current one.
     *
     * @return inversion cipher operation to the current one. TYPE_INVALID on error
     */
    public int getInversionUoTypeFunction(){
        switch (getUoTypeFunction()){
            case TYPE_PLAINAES:
                return TYPE_PLAINAESDECRYPT;
            case TYPE_PLAINAESDECRYPT:
                return TYPE_PLAINAES;
            case TYPE_RSA1024DECRYPT_NOPAD:
                return TYPE_RSA1024ENCRYPT_NOPAD;
            case TYPE_RSA1024ENCRYPT_NOPAD:
                return TYPE_RSA1024DECRYPT_NOPAD;
            case TYPE_RSA2048DECRYPT_NOPAD:
                return TYPE_RSA2048ENCRYPT_NOPAD;
            case TYPE_RSA2048ENCRYPT_NOPAD:
                return TYPE_RSA2048DECRYPT_NOPAD;
            default:
                return TYPE_INVALID;
        }
    }

    public static long getValue(int function, int appKeyClientGenerated, int comKeyClientGenerated){
        long type = function & TYPE_MASK;
        if (comKeyClientGenerated != Constants.GENKEY_LEGACY_ENROLL_RANDOM && comKeyClientGenerated != Constants.GENKEY_CLIENT){
            throw new IllegalArgumentException("Illegal argument for comm key generation type");
        }

        if (appKeyClientGenerated < 0 || appKeyClientGenerated > Constants.GENKEY_ENROLL_DERIVED){
            throw new IllegalArgumentException("Illegal argument for app key generation type");
        }

        type |= (appKeyClientGenerated & 0x7) << 21;
        type |= (comKeyClientGenerated & 0x1) << 20;
        return type;
    }

    public int getComKeyGenerationType() {
        return (int) ((typeValue >> 20) & 0x1);
    }

    public int getAppKeyGenerationType() {
        return (int) ((typeValue >> 21) & 0x7);
    }

    public long getValue() {
        return typeValue;
    }

    protected void setValue(long value){
        this.typeValue = value;
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
        return "{" + Long.toHexString(getValue()) + "}";
    }

    /**
     * Returns either TYPE_RSA2048DECRYPT_NOPAD or TYPE_RSA1024DECRYPT_NOPAD depending on the bitLength of the modulus.
     *
     * @param modulus modulus of the RSA private key
     * @return TYPE_RSA2048DECRYPT_NOPAD or TYPE_RSA1024DECRYPT_NOPAD
     */
    public static int getRSADecryptFunctionFromModulus(BigInteger modulus){
        // 1048 is on purpose, not exact 1024, what if implementation generated modulus of bitLength 1025?
        return modulus.bitLength() > 1048 ? UserObjectType.TYPE_RSA2048DECRYPT_NOPAD : UserObjectType.TYPE_RSA1024DECRYPT_NOPAD;
    }
}
