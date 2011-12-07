package no.ums.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory to create Log instances.
 */
public final  class UmsLog {

    private UmsLog() {
        // Cannot construct outside this package.
    }

    public static Log getLogger(final Class<?> src) {
        return getLogger(src.getName());
    }

    public static Log getLogger(final String src) {
        return new JavaLoggerLog(src);
    }

    private static final class JavaLoggerLog extends AbstractLog<Level> {

        private final Logger log;

        public JavaLoggerLog(final String src) {
            super(Level.FINEST, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE);
            log = Logger.getLogger(src);
        }

        @Override
        protected void logImpl(final Level level, final String message) {
            log.log(level, message);
        }

        @Override
        protected void logImpl(final Level level, final String message, final Throwable throwable) {
            log.log(level, message, throwable);
        }

        @Override
        protected boolean isLoggable(final Level level) {
            return log.isLoggable(level);
        }
    }
}
