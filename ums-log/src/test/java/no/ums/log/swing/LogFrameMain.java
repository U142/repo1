package no.ums.log.swing;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LogFrameMain {

    static Log log = UmsLog.getLogger(LogFrameMain.class);

    public static void main(String args[]) throws IOException {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        LogRecordCollector.install();
        LogFrame.install();
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        log.debug("Test message");
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                switch ((int) (5 * Math.random())) {
                    case 0:
                        log.debug("Test message");
                        break;
                    case 1:
                        log.info("Info message");
                        break;
                    case 2:
                        log.warn("Warning message");
                        break;
                    default:
                        log.error("Error message", new Exception());
                }
            }
        }, 1, 1, TimeUnit.SECONDS);

    }
}
