package no.ums.log.swing;

import com.google.common.base.Charsets;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LogRecordCollector extends Handler {
    public static final LogRecordModel MODEL = new LogRecordModel();
    private static final Logger umsLog = Logger.getLogger("no.ums");
    private static LogMailSender logMailSender = new LogMailSender() {
        @Override
        public boolean sendMail(String id, String content) {
            System.out.printf("Dummy mail:\n%s\nID: %s\n", content, id);
            return true;
        }
    };

    // Findbugs reports an error here, as the can potentially fail when using OpenJDK.
    // As we only deploy to the Oracle JDK, we can ignore this warning.
    // http://findbugs.sourceforge.net/bugDescriptions.html#LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE")
    public static void install(@Nullable LogMailSender logMailSender, final boolean enableDebugLogging) {
        umsLog.setUseParentHandlers(false);
        umsLog.setLevel(Level.FINEST);
        umsLog.addHandler(new LogRecordCollector());
        if (logMailSender != null) {
            LogRecordCollector.logMailSender = logMailSender;
        }

        // We log everything, so no need for the parent to log our messages
        umsLog.addHandler(new Handler() {

            {
                setLevel((enableDebugLogging) ? Level.ALL : Level.WARNING);
                setFormatter(new UmsLogFormat(true));
            }
            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record)) {
                    if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                        System.err.println(getFormatter().format(record));
                    } else {
                        System.out.println(getFormatter().format(record));
                    }
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
        });
    }

    @Override
    public void publish(LogRecord record) {
        MODEL.add(record);
    }

    @Override
    public void flush() {
        // Noop
    }

    @Override
    public void close() throws SecurityException {
        // Noop
    }

    public static boolean sendMail() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        final List<LogRecord> allRecords = MODEL.getAllRecords();
        int lastThrowable = allRecords.size();
        while (lastThrowable > 0 && allRecords.get(lastThrowable-1).getLevel().intValue() < Level.SEVERE.intValue()) {
            lastThrowable--;
        }
        // If there are no severe messages, include all log statements
        if (lastThrowable == 0) {
            lastThrowable = allRecords.size();
        }
        final LogRecord lastRecord = allRecords.get(lastThrowable - 1);
        final long startTime = lastRecord.getMillis() - TimeUnit.SECONDS.toMillis(10);
        final String id;
        if (lastRecord.getThrown() != null) {
            final CRC32 crc32 = new CRC32();
            final StackTraceElement[] stackTrace = lastRecord.getThrown().getStackTrace();
            for (int i=0; i<Math.min(5, stackTrace.length); i++) {
                crc32.update(stackTrace[i].getClassName().getBytes(Charsets.UTF_8));
                crc32.update(stackTrace[i].getMethodName().getBytes(Charsets.UTF_8));
                crc32.update(stackTrace[i].getLineNumber());
            }
            id = Integer.toString((int) crc32.getValue(), Character.MAX_RADIX);
        } else {
            id = "";
        }
        for (LogRecord logRecord : allRecords.subList(0, lastThrowable)) {
            // Only include logging statements from the last 10 seconds
            if (logRecord.getMillis() > startTime) {
                pw.printf("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%tS %-6s %s\n\t%s\n", logRecord.getMillis(), logRecord.getLevel(), logRecord.getLoggerName(), logRecord.getMessage());
                final Throwable throwable = logRecord.getThrown();
                if (throwable != null) {
                    throwable.printStackTrace(pw);
                }
            }
        }
        pw.close();
        return logMailSender.sendMail(id, sw.toString());
    }

}
