package no.ums.log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * UMS Logging facade.
 * <p/>
 * This is a single log, retrieved from {@link UmsLog}
 */
public interface Log {

    /**
     * This will log a single message.
     * <p/>
     * The message can be a formatted string, as expected by {@link java.io.PrintStream#printf(String, Object...)}.
     * Additionally, if the last argument is an exception, it will get logged as well.
     *
     * @param msg  Message to log
     * @param args message parameters
     */
    void debug(@Nonnull String msg, Object... args);

    /**
     * This will log the to string value off the provided value.
     *
     * @param value to log
     */
    void debug(@Nullable Object value);

    /**
     * @param msg  Message to log
     * @param args message parameters
     * @see #debug(String, Object...)
     */
    void info(@Nonnull String msg, Object... args);

    /**
     * @param value to log
     * @see #debug(Object)
     */
    void info(@Nullable Object value);

    /**
     * @param msg  Message to log
     * @param args message parameters
     * @see #debug(String, Object...)
     */
    void warn(@Nonnull String msg, Object... args);

    /**
     * @param value to log
     * @see #debug(Object)
     */
    void warn(@Nullable Object value);

    /**
     * @param msg  Message to log
     * @param args message parameters
     * @see #debug(String, Object...)
     */
    void error(@Nonnull String msg, Object... args);

    /**
     * @param value to log
     * @see #debug(Object)
     */
    void error(@Nullable Object value);

    /**
     * @param msg  Message to log
     * @param args message parameters
     * @see #debug(String, Object...)
     */
    void trace(@Nonnull String msg, Object... args);

    /**
     * @param value to log
     * @see #debug(Object)
     */
    void trace(@Nullable Object value);

}
