package no.ums.log;

/**
 * UMS Logging facade.
 *
 * This is a single log, retrieved from {@link UmsLog}
 *
 */
public interface Log {

    /**
     * This will log a single message.
     *
     * The message can be a formatted string, as expected by {@link java.io.PrintStream#printf(String, Object...)}.
     * Additionally, if the last argument is an exception, it will get logged as well.
     *
     * @param msg Message to log
     * @param args message parameters
     */
    public void debug(String msg, Object ... args);
    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void info(String msg, Object ... args);
    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void warn(String msg, Object ... args);

    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void error(String msg, Object ... args);

}
