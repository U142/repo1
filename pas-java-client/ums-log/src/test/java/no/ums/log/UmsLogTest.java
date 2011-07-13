package no.ums.log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UmsLogTest {

    @Test
    public void testConstruction() {
        // Only needed to achieve 100% coverage
        new UmsLog();
    }
    
    @Test
    public void testGetLogger() {
        assertThat(UmsLog.getLogger(UmsLogTest.class), notNullValue());
    }


}
