package no.ums.log;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JavaLoggerTest {

    private static final List<LogRecord> records = new ArrayList<LogRecord>();

    public static class TestLogHandler extends Handler {

        @Override
        public void publish(LogRecord record) {
            records.add(record);
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


    @Before
    public void addAppender() throws IOException {
        records.clear();
        final Properties config = new Properties();
        config.put("handlers", TestLogHandler.class.getName());
        config.put(".level", Level.FINEST.getName());
        final StringWriter writer = new StringWriter();
        config.store(writer, "");
        java.util.logging.LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(writer.toString().getBytes()));
    }

    @Test
    public void testDebug()  {
        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.debug("Test %s", "debug");
        assertThat(records.size(), equalTo(1));
        assertThat(records.get(0).getLevel(), equalTo(Level.FINE));
        assertThat(records.get(0).getMessage(), equalTo("Test debug"));
    }

    @Test
    public void testInfo()  {
        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.info("Test %s", "info");
        assertThat(records.size(), equalTo(1));
        assertThat(records.get(0).getLevel(), equalTo(Level.INFO));
        assertThat(records.get(0).getMessage(), equalTo("Test info"));
    }

    @Test
    public void testWarning()  {
        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.warn("Test %s", "warning");
        assertThat(records.size(), equalTo(1));
        assertThat(records.get(0).getLevel(), equalTo(Level.WARNING));
        assertThat(records.get(0).getMessage(), equalTo("Test warning"));
    }

    @Test
    public void testError()  {
        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.error("Test %s", "error");
        assertThat(records.size(), equalTo(1));
        assertThat(records.get(0).getLevel(), equalTo(Level.SEVERE));
        assertThat(records.get(0).getMessage(), equalTo("Test error"));
    }
    
    @Test
    public void testException() {
        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.error("Test", new Exception());
        assertThat(records.get(0).getThrown(), notNullValue());
    }

    @Test
    public void testLogSimpleMessage() {
        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.info("Just a simple message");
        assertThat(records.size(), equalTo(1));
    }

    @Test
    public void testRepeatedCallsReturnSameLogger() {
        assertThat(Logger.getLogger(JavaLoggerTest.class.getName()), is(Logger.getLogger(JavaLoggerTest.class.getName())));
    }

    @Test
    public void testLoggingLevelDisabled() throws IOException {
        // Change the level off the logger
        Logger.getLogger(JavaLoggerTest.class.getName()).setLevel(Level.SEVERE);

        final Log log = UmsLog.getLogger(JavaLoggerTest.class);
        log.debug("Just a simple message");
        assertThat(records.size(), equalTo(0));
    }
}
