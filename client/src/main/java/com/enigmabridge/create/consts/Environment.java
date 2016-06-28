package com.enigmabridge.create.consts;

/**
 * Created by dusanklinec on 28.06.16.
 */
public enum Environment {
    DEV("dev"),
    TEST("test"),
    PROD("prod");

    private final String text;

    /**
     * @param text
     */
    private Environment(final String text) {
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
