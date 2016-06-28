package com.enigmabridge.create.consts;

/**
 * Created by dusanklinec on 28.06.16.
 */
public enum MaxTps {
    _1("one"),
    _10("ten"),
    _20("twenty"),
    _50("fifty"),
    _100("one_hundred"),
    _200("two_hundred"),
    _500("five_hundred"),
    _1000("one_thousand"),
    _2000("two_thousand"),
    _5000("five_thousand"),
    _10000("ten_thousand"),
    _50000("fifty_thousand"),
    _100000("hundred_thousand"),
    UNLIMITED("unlimited");

    private final String text;

    /**
     * @param text
     */
    private MaxTps(final String text) {
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
