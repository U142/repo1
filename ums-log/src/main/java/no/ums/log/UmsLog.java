package no.ums.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Factory to create Log instances.
 */
public class UmsLog {

    UmsLog() {
        // Cannot construct outside this package.
    }

    public static Log getLogger(Class<?> src) {
        return getLogger(src.getName());
    }

    public static Log getLogger(final String src) {
        return new JavaLoggerLog(src);
    }

    private static final class JavaLoggerLog extends AbstractLog<Level> {

        final Logger log;

        public JavaLoggerLog(String src) {
            super(Level.FINEST, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE);
            log = Logger.getLogger(src);
        }

        @Override
        protected void logImpl(Level level, String message) {
            log.log(level, message);
        }

        @Override
        protected void logImpl(Level level, String message, Throwable throwable) {
            log.log(level, message, throwable);
        }

        @Override
        protected boolean isLoggable(Level level) {
            return log.isLoggable(level);
        }
    }
}
