package no.ums.log;

import java.util.logging.Level;
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

    public static Log getLogger(String src) {
        final Logger log = Logger.getLogger(src);
        return new Log() {
            @Override
            public void debug(String msg, Object... args) {
                log(Level.FINE, msg, args);
            }

            @Override
            public void info(String msg, Object... args) {
                log(Level.INFO, msg, args);
            }

            @Override
            public void warn(String msg, Object... args) {
                log(Level.WARNING, msg, args);
            }

            @Override
            public void error(String msg, Object... args) {
                log(Level.SEVERE, msg, args);
            }

            private void log(Level level, String msg, Object[] args) {
                if (log.isLoggable(level)) {
                    final Object last = (args.length > 0) ? args[args.length-1] : null;
                    if (last instanceof Throwable) {
                        log.log(level, String.format(msg, args), (Throwable) last);
                    } else {
                        log.log(level, String.format(msg, args));
                    }
                }
            }

        };
    }

}
