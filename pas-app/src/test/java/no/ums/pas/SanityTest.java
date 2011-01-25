package no.ums.pas;

import no.ums.ws.pas.Pasws;
import org.junit.Test;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class SanityTest {

    @Test
    public void verifyOneTimeKeyWorks() {
        new Pasws().getPaswsSoap12().getOneTimeKey();
    }
}
