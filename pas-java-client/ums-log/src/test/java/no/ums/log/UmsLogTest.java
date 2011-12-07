package no.ums.log;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UmsLogTest {

    @Test
    public void testGetLogger() {
        assertThat(UmsLog.getLogger(UmsLogTest.class), notNullValue());
    }


}
