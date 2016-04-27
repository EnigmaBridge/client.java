package com.enigmabridge.comm;

/**
 * All known request types.
 * Created by dusanklinec on 27.04.16.
 */
public enum EBRequestTypes {
    PLAINAES("PLAINAES"),
    RSA1024("RSA1024"),
    RSA2048("RSA2048"),
    AUTH_NEWUSERCTX("AUTH_NEWUSERCTX"),
    AUTH_HOTP("AUTH_HOTP"),
    AUTH_PASSWD("AUTH_PASSWD"),
    AUTH_UPDATEUSERCTX("AUTH_UPDATEUSERCTX");

    private final String text;

    /**
     * @param text
     */
    private EBRequestTypes(final String text) {
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
