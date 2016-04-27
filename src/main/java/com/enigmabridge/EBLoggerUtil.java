package com.enigmabridge;

/**
 * Created by dusanklinec on 27.04.16.
 */
public class EBLoggerUtil {
    public void setLogLevel(EBLogger logger, int level) {
        if (logger == null) return;
        logger.setLogLevel(level);
    }

    /**
     * Get the current log level
     * @return the log level
     */
    public int getLogLevel(EBLogger logger) {
        if (logger == null) return -1;
        return logger.getLogLevel();
    }

    /**
     * Log verbose
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void v(EBLogger logger, String tag, String msg) {
        if (logger == null) return;
        logger.v(tag, msg);
    }

    /**
     * Log verbose
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void v(EBLogger logger, String tag, String msg, Throwable tr) {
        if (logger == null) return;
        logger.v(tag, msg, tr);
    }

    public void vf(EBLogger logger, String tag, final String msg, final Object... args) {
        if (logger == null) return;
        logger.vf(tag, msg, args);
    }

    public void vf(EBLogger logger, String tag, final Throwable tr, final String msg, final Object... args) {
        if (logger == null) return;
        logger.vf(tag, tr, msg, args);
    }

    /**
     * Log debug
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void d(EBLogger logger, String tag, String msg) {
        if (logger == null) return;
        logger.d(tag, msg);
    }

    /**
     * Log debug
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void d(EBLogger logger, String tag, String msg, Throwable tr) {
        if (logger == null) return;
        logger.d(tag, msg, tr);
    }

    public void df(EBLogger logger, String tag, final String msg, final Object... args) {
        if (logger == null) return;
        logger.df(tag, msg, args);
    }

    public void df(EBLogger logger, String tag, final Throwable tr, final String msg, final Object... args) {
        if (logger == null) return;
        logger.df(tag, tr, msg, args);
    }

    /**
     * Log info
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void i(EBLogger logger, String tag, String msg) {
        if (logger == null) return;
        logger.i(tag, msg);
    }

    /**
     * Log info
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void i(EBLogger logger, String tag, String msg, Throwable tr) {
        if (logger == null) return;
        logger.i(tag, msg, tr);
    }

    public void inf(EBLogger logger, String tag, final String msg, final Object... args) {
        if (logger == null) return;
        logger.inf(tag, msg, args);
    }

    public void inf(EBLogger logger, String tag, final Throwable tr, final String msg, final Object... args) {
        if (logger == null) return;
        logger.inf(tag, tr, msg, args);
    }

    /**
     * Log warning
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void w(EBLogger logger, String tag, String msg) {
        if (logger == null) return;
        logger.w(tag, msg);
    }

    /**
     * Log warning
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void w(EBLogger logger, String tag, String msg, Throwable tr) {
        if (logger == null) return;
        logger.w(tag, msg, tr);
    }

    public void wf(EBLogger logger, String tag, final String msg, final Object... args) {
        if (logger == null) return;
        logger.wf(tag, msg, args);
    }

    public void wf(EBLogger logger, String tag, final Throwable tr, final String msg, final Object... args) {
        if (logger == null) return;
        logger.wf(tag, tr, msg, args);
    }

    /**
     * Log error
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void e(EBLogger logger, String tag, String msg) {
        if (logger == null) return;
        logger.e(tag, msg);
    }

    /**
     * Log error
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void e(EBLogger logger, String tag, String msg, Throwable tr) {
        if (logger == null) return;
        logger.e(tag, msg, tr);
    }

    public void ef(EBLogger logger, String tag, final String msg, final Object... args) {
        if (logger == null) return;
        logger.ef(tag, msg, args);
    }

    public void ef(EBLogger logger, String tag, final Throwable tr, final String msg, final Object... args) {
        if (logger == null) return;
        logger.ef(tag, tr, msg, args);
    }
}
