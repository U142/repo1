package no.ums.pas.ums.tools;

import org.junit.Test;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class UMSSecurityTest {

    @Test
    public void testEncrypt() throws Exception {
        System.out.println(Utils.encrypt("mh123mh123"));
    }
}
