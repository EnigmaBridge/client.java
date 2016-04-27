package com.enigmabridge;

/**
 * Created by dusanklinec on 27.04.16.
 */
public interface EBLogger {
    /**
     * Change current logging level
     * @param level new log level 1 <= level <= 6
     */
    public void setLogLevel(int level);

    /**
     * Get the current log level
     * @return the log level
     */
    public int getLogLevel();

    /**
     * Log verbose
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void v(String tag, String msg);

    /**
     * Log verbose
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void v(String tag, String msg, Throwable tr);

    public void vf(String tag, final String msg, final Object... args);

    public void vf(String tag, final Throwable tr, final String msg, final Object... args);

    /**
     * Log debug
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void d(String tag, String msg);

    /**
     * Log debug
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void d(String tag, String msg, Throwable tr);

    public void df(String tag, final String msg, final Object... args);

    public void df(String tag, final Throwable tr, final String msg, final Object... args);

    /**
     * Log info
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void i(String tag, String msg);

    /**
     * Log info
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void i(String tag, String msg, Throwable tr);

    public void inf(String tag, final String msg, final Object... args);

    public void inf(String tag, final Throwable tr, final String msg, final Object... args);

    /**
     * Log warning
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void w(String tag, String msg);

    /**
     * Log warning
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void w(String tag, String msg, Throwable tr);

    public void wf(String tag, final String msg, final Object... args);

    public void wf(String tag, final Throwable tr, final String msg, final Object... args);

    /**
     * Log error
     * @param tag Tag for this log
     * @param msg Msg for this log
     */
    public void e(String tag, String msg);

    /**
     * Log error
     * @param tag Tag for this log
     * @param msg Msg for this log
     * @param tr Error to serialize in log
     */
    public void e(String tag, String msg, Throwable tr);

    public void ef(String tag, final String msg, final Object... args);

    public void ef(String tag, final Throwable tr, final String msg, final Object... args);
}
