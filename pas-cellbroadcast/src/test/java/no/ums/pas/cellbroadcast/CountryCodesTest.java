package no.ums.pas.cellbroadcast;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class CountryCodesTest {

    @Test
    public void testInstantiation() throws Exception {
        assertThat(CountryCodes.INSTANCE, notNullValue());
    }
    
    @Test
    public void testCyclicReference() {
        for (CCode code : CountryCodes.getCountryCodes()) {
            assertThat(CountryCodes.getCountryByCCode(code.getCCode()), is(code));
        }
    }

    @Test
    public void testDoubleZeroPrefix() {
        assertThat(CountryCodes.getCountryByCCode("47"), is(CountryCodes.getCountryByCCode("0047")));
    }
}
