package no.ums.log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public void debug(@Nonnull String msg, Object ... args);

    /**
     * This will log the to string value off the provided value
     * @param value to log
     */
    public void debug(@Nullable Object value);

    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void info(@Nonnull String msg, Object ... args);
    /**
     * @see #debug(Object)
     * @param value to log
     */
    public void info(@Nullable Object value);
    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void warn(@Nonnull String msg, Object ... args);
    /**
     * @see #debug(Object)
     * @param value to log
     */
    public void warn(@Nullable Object value);

    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void error(@Nonnull String msg, Object ... args);
    /**
     * @see #debug(Object)
     * @param value to log
     */
    public void error(@Nullable Object value);

    /**
     * @see #debug(String, Object...)
     * @param msg Message to log
     * @param args message parameters
     */
    public void trace(@Nonnull String msg, Object ... args);
    /**
     * @see #debug(Object)
     * @param value to log
     */
    public void trace(@Nullable Object value);

}
