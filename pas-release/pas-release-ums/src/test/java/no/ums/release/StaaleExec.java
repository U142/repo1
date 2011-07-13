package no.ums.release;

import no.ums.pas.entrypoint.ExecApp;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class StaaleExec {

    public static void main(String[] args) {
        ExecApp.main(new String[]{
                "-umh",
                "-cums",
                "-wmh123mh123",
                "-x$jnlp_codebase",
                "-shttps://secure.ums2.no/vb4utv/",
                "-whttp://192.168.3.157/pas/WS/",
                "-p"
        });
    }
}
