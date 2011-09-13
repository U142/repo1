package no.ums.pas.localization;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class DegreeSignTest {

    @Test
    public void testDegreeSign() throws Exception {
        // Issue PAS-1062
        assertThat(new lang(Locale.ENGLISH, false, lang.NO_ERROR).l("common_degree_sign"), equalTo("\u00b0"));
        assertThat(new lang(new Locale("no", "NO"), false, lang.NO_ERROR).l("common_degree_sign"), equalTo("\u00b0"));
        assertThat(new lang(new Locale("nl", "NL"), false, lang.NO_ERROR).l("common_degree_sign"), equalTo("\u00b0"));
    }
}
