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

    public static void install() {
        Logger umsLog = Logger.getLogger("no.ums");
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
