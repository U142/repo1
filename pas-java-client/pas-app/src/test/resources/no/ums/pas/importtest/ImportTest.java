package no.ums.pas.importtest;

import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.PaswsSoap;
import org.junit.Test;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ImportTest {

    @Test
    public void testImport() throws Exception {
        PaswsSoap paswsSoap12 = new Pasws().getPaswsSoap12();
    }
}
