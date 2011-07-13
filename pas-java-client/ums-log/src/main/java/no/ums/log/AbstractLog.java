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
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractLog<L> implements Log {

    private final L trace, debug, info, warn, error;

    protected AbstractLog(L trace, L debug, L info, L warn, L error) {
        this.trace = trace;
        this.debug = debug;
        this.info = info;
        this.warn = warn;
        this.error = error;
    }

    @Override
    public void trace(@Nonnull String msg, Object... args) {
        log(trace, msg, args);
    }

    @Override
    public void trace(Object value) {
        log(trace, value);
    }

    @Override
    public void debug(@Nonnull String msg, Object... args) {
        log(debug, msg, args);
    }

    @Override
    public void debug(Object value) {
        log(debug, value);
    }

    @Override
    public void info(@Nonnull String msg, Object... args) {
        log(info, msg, args);
    }

    @Override
    public void info(Object value) {
        log(info, value);
    }

    @Override
    public void warn(@Nonnull String msg, Object... args) {
        log(warn, msg, args);
    }

    @Override
    public void warn(Object value) {
        log(warn, value);
    }

    @Override
    public void error(@Nonnull String msg, Object... args) {
        log(error, msg, args);
    }

    @Override
    public void error(Object value) {
        log(error, value);
    }

    private void log(L level, Object value) {
        if (value == null) {
            log(level, "NULL", new Object[0]);
        } else {
            log(level, value.toString(), new Object[0]);
        }
    }

    private void log(L level, String msg, Object[] args) {
        if (isLoggable(level)) {
            final Object last = (args.length > 0) ? args[args.length - 1] : null;
            if (last instanceof Throwable) {
                logImpl(level, String.format(msg!=null ? msg : "<NO TEXT>", args), (Throwable) last);
            } else {
                logImpl(level, String.format(msg!=null ? msg : "<NO TEXT>", args));
            }
        }
    }

    protected abstract void logImpl(L level, String message);

    protected abstract void logImpl(L level, String message, Throwable throwable);

    protected abstract boolean isLoggable(L level);
}
