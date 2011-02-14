package no.ums.log.swing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LogRecordCollector extends Handler {
    public static final LogRecordModel MODEL = new LogRecordModel();

    public static void install() {
        try {
            final Properties config = new Properties();
            config.put("handlers", LogRecordCollector.class.getName());
            config.put(".level", Level.FINEST.getName());
            final StringWriter writer = new StringWriter();
            config.store(writer, "");
            LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(writer.toString().getBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to install new logging configuration", e);
        }
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
