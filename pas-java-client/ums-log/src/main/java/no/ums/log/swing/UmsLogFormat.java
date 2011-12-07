package no.ums.log.swing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class UmsLogFormat extends Formatter {

    private static final String LOG_FORMAT_STRING = "%1$tH:%1$tM:%1$tS,%tL %-6s [%-25s] %s";
    private static final int MAX_LOGGER_NAME_LENGTH = 25;

    private final boolean shrinkLoggerName;

    public UmsLogFormat(final boolean shrinkLoggerName) {
        this.shrinkLoggerName = shrinkLoggerName;
    }

    @Override
    public String format(final LogRecord record) {
        String logger = record.getLoggerName();
        while (shrinkLoggerName && logger.length() > MAX_LOGGER_NAME_LENGTH && logger.indexOf('.') != -1) {
            logger = logger.substring(logger.indexOf('.') + 1);
        }

        final long time = record.getMillis();
        if (record.getThrown() == null) {
            return String.format(LOG_FORMAT_STRING, time, record.getLevel().getName(), logger, record.getMessage());
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.printf(LOG_FORMAT_STRING, time, record.getLevel().getName(), logger, record.getMessage());
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
    }
}
