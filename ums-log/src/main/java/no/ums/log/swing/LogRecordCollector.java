package no.ums.log.swing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LogRecordCollector extends Handler {
    public static final LogRecordModel MODEL = new LogRecordModel();
    private static final Logger umsLog = Logger.getLogger("no.ums");

    // Findbugs reports an error here, as the can potentially fail when using OpenJDK.
    // As we only deploy to the Oracle JDK, we can ignore this warning.
    // http://findbugs.sourceforge.net/bugDescriptions.html#LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE")
    public static void install() {
        umsLog.addHandler(new LogRecordCollector());
        umsLog.setLevel(Level.FINEST);
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLoggerName().startsWith("no")) {
            MODEL.add(record);
        }
    }

    @Override
    public void flush() {
        // Noop
    }

    @Override
    public void close() throws SecurityException {
        // Noop
    }

}
