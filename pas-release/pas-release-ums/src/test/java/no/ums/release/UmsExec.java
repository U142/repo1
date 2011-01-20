package no.ums.release;

import no.ums.pas.entrypoint.ExecApp;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class UmsExec {

    public static void main(String[] args) {
        ExecApp.main(new String[]{
                "-x$jnlp_codebase",
                "-shttps://secure.ums2.no/vb4utv/",
                "-whttps://secure.ums2.no/PAS/ws_pasutv/WS/",
                "-p"
        });
    }
}
