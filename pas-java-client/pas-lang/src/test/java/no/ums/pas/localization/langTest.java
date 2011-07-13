package no.ums.pas.localization;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class langTest {

    private final Mockery context = new Mockery();

    @Test
    public void testConstruction() throws Exception {
        assertThat(new lang(new Locale("no", "NO"), false, null).l("common_seconds"), notNullValue());
    }

    @Test
    public void testUnkonwn() throws Exception {
        final lang.LangError langError = context.mock(lang.LangError.class);
        context.checking(new Expectations() {{
            one(langError).logMissing(with(Expectations.<Locale>anything()), with(Expectations.<String>anything()), with(Expectations.<String>anything()));
        }});

        assertThat(new lang(new Locale("no", "NO"), false, langError).l("test_key_that_will_always_be_missing_in_no"), notNullValue());
        context.assertIsSatisfied();
    }
}
