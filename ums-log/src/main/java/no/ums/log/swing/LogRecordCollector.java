package no.ums.log.swing;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        public void sendMail(String id, String content) {
            System.out.printf("Dummy mail:\n%s\nID: %s\n", content, id);
        }
    };

    // Findbugs reports an error here, as the can potentially fail when using OpenJDK.
    // As we only deploy to the Oracle JDK, we can ignore this warning.
    // http://findbugs.sourceforge.net/bugDescriptions.html#LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE")
    public static void install(@Nullable LogMailSender logMailSender) {
        umsLog.addHandler(new LogRecordCollector());
        umsLog.setLevel(Level.FINEST);
        if (logMailSender != null) {
            LogRecordCollector.logMailSender = logMailSender;
        }
    }

    @Override
    public void publish(LogRecord record) {
//        if (record.getLoggerName().startsWith("no")) {
            MODEL.add(record);
//        }
    }

    @Override
    public void flush() {
        // Noop
    }

    @Override
    public void close() throws SecurityException {
        // Noop
    }

    public static void sendMail() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String id = "";
        final List<LogRecord> allRecords = MODEL.getAllRecords();
        int lastThrowable = allRecords.size();
        while (lastThrowable > 1 && allRecords.get(lastThrowable-1).getLevel().intValue() < Level.SEVERE.intValue()) {
            lastThrowable--;
        }
        for (LogRecord logRecord : allRecords.subList(0, lastThrowable)) {
            pw.printf("%s %-6s %s\n\t%s\n", df.format(new Date(logRecord.getMillis())), logRecord.getLevel(), logRecord.getLoggerName(), logRecord.getMessage());
            final Throwable throwable = logRecord.getThrown();
            if (throwable != null) {
                throwable.printStackTrace(pw);
                final CRC32 crc32 = new CRC32();
                final StackTraceElement[] stackTrace = throwable.getStackTrace();
                for (int i=0; i<Math.min(5, stackTrace.length); i++) {
                    crc32.update(stackTrace[i].getClassName().getBytes(Charsets.UTF_8));
                    crc32.update(stackTrace[i].getMethodName().getBytes(Charsets.UTF_8));
                    crc32.update(stackTrace[i].getLineNumber());
                }
                id = Integer.toString((int) crc32.getValue(), Character.MAX_RADIX);

            }
        }
        pw.close();
        logMailSender.sendMail(id, sw.toString());
    }
}
