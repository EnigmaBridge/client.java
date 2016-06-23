package com.enigmabridge.comm;

/**
 * All known request types.
 * Created by dusanklinec on 27.04.16.
 */
public enum EBRequestType {
    PLAINAES("PLAINAES"),
    RSA1024("RSA1024"),
    RSA2048("RSA2048"),
    AUTH_NEWUSERCTX("AUTH_NEWUSERCTX"),
    AUTH_HOTP("AUTH_HOTP"),
    AUTH_PASSWD("AUTH_PASSWD"),
    AUTH_UPDATEUSERCTX("AUTH_UPDATEUSERCTX"),
    HMAC("HMAC"),
    SCRAMBLE("SCRAMBLE"),
    ENSCRAMBLE("ENSCRAMBLE"),
    FP192SIGN("FP192SIGN"),
    TOKENIZE("TOKENIZE"),
    DETOKENIZE("DETOKENIZE"),
    TOKENIZEWRAP("TOKENIZEWRAP"),
    RANDOMDATA("RANDOMDATA");

    private final String text;

    /**
     * @param text
     */
    private EBRequestType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
