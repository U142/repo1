package no.ums.release;

import no.ums.pas.entrypoint.ExecApp;
import no.ums.pas.ums.tools.Utils;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class StaaleExec {

    public static void main(String[] args) {
        System.out.println(Utils.encrypt("mh123mh123"));
        ExecApp.main(new String[]{
                "-x$jnlp_codebase",
                "-shttps://secure.ums2.no/vb4utv/",
                "-whttp://localhost:8080/WS/",
                "-p"
        });
    }
}
