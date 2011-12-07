package no.ums.log;

import javax.annotation.Nonnull;

/**
 * Abstract implementation off the Log interface.
 * <p/>
 * This abstract logger takes a type parameter for the type off level
 * used by the implementing log system. The constructor takes in the 4
 * levels needed for implementation, and will call one off the logImpl methods
 * if the level is loggable with the finished message.
 *
 * @param <L> Log level type
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractLog<L> implements Log {

    private final L trace, debug, info, warn, error;

    /**
     * Creates a new AbstractLog, with the given level values.
     *
     * @param trace trace level value
     * @param debug debug level value
     * @param info  info level value
     * @param warn  warn level value
     * @param error error level value
     */
    protected AbstractLog(final L trace, final L debug, final L info, final L warn, final L error) {
        this.trace = trace;
        this.debug = debug;
        this.info = info;
        this.warn = warn;
        this.error = error;
    }

    @Override
    public final void trace(@Nonnull final String msg, final Object... args) {
        log(trace, msg, args);
    }

    @Override
    public final void trace(final Object value) {
        log(trace, value);
    }

    @Override
    public final void debug(@Nonnull final String msg, final Object... args) {
        log(debug, msg, args);
    }

    @Override
    public final void debug(final Object value) {
        log(debug, value);
    }

    @Override
    public final void info(@Nonnull final String msg, final Object... args) {
        log(info, msg, args);
    }

    @Override
    public final void info(final Object value) {
        log(info, value);
    }

    @Override
    public final void warn(@Nonnull final String msg, final Object... args) {
        log(warn, msg, args);
    }

    @Override
    public final void warn(final Object value) {
        log(warn, value);
    }

    @Override
    public final void error(@Nonnull final String msg, final Object... args) {
        log(error, msg, args);
    }

    @Override
    public final void error(final Object value) {
        log(error, value);
    }

    private void log(final L level, final Object value) {
        if (value == null) {
            log(level, "NULL", new Object[0]);
        } else {
            log(level, value.toString(), new Object[0]);
        }
    }

    private void log(final L level, final String msg, final Object[] args) {
        if (isLoggable(level)) {
            final Object last = (args.length > 0) ? args[args.length - 1] : null;
            if (last instanceof Throwable) {
                logImpl(level, String.format(msg != null ? msg : "<NO TEXT>", args), (Throwable) last);
            } else {
                logImpl(level, String.format(msg != null ? msg : "<NO TEXT>", args));
            }
        }
    }

    /**
     * Logs a message off the given level.
     * <p/>
     * The Level is one off the levels provided in the constructor. This method will get called
     * after {@link #isLoggable(Object)} is called to check that the statement should get logged.
     *
     * @param level   off the message
     * @param message message being logged
     */
    protected abstract void logImpl(L level, String message);

    /**
     * Logs a message off the given level with an exception.
     * <p/>
     * Works like {@link #logImpl(Object, String)}, but used when there is also an exception
     * to log.
     *
     * @param level     off the message
     * @param message   message being logged
     * @param throwable exception being logged
     */
    protected abstract void logImpl(L level, String message, Throwable throwable);

    /**
     * Checks if the given level should be logged.
     *
     * @param level being checked for logging
     * @return true if messages should be logged at this level, false otherwise.
     */
    protected abstract boolean isLoggable(L level);
}
