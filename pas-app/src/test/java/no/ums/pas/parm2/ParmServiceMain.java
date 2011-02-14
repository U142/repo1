package no.ums.pas.parm2;

import no.ums.ws.parm.*;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ParmServiceMain {

    public static void main(String[] args) {
        ParmSoap parm = new Parm().getParmSoap12();
        String sessionId = parm.login("mh", "ums", "mh123,1");
        System.out.println(sessionId);
        final long start = System.currentTimeMillis();
        DataResultOfListOfPaObject result = parm.getPaObjects(sessionId, 0);
        System.out.println(System.currentTimeMillis()-start);
        System.out.println(result.isValid());
        if (result.isValid()) {
            System.out.println(result.getValue().getPaObject().size());
            for (PaObject paObject : result.getValue().getPaObject()) {
                System.out.println(paObject.getObjectpk()+" "+paObject.getName());
            }
        }
        parm.logout(sessionId);

    }
}
