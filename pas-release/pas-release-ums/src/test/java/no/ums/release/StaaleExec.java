package no.ums.release;

import no.ums.pas.entrypoint.ExecApp;
import no.ums.pas.ums.tools.Utils;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class StaaleExec {

    public static void main(String[] args) {
        ExecApp.main(new String[]{
                "-x$jnlp_codebase",
                "-shttps://secure.ums2.no/vb4utv/",
                "-whttp://192.168.3.157/WS/",
                "-p"
        });
    }
}
