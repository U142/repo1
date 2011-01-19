package no.ums.pas.pluginbase;

import no.ums.pas.entrypoint.ExecApp;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class DefaultPasScriptingTest {

    @Test
    public void testDefaultPluginIsLoaded() {
        assertThat(ExecApp.loadPlugin(), is(DefaultPasScripting.class));
    }

}
