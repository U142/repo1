package no.ums.pas.parm2.views;

import no.ums.pas.parm2.ParmUiService;
import no.ums.pas.parm2.ParmUiServiceSoapImpl;
import no.ums.ws.parm.PaObject;
import no.ums.ws.parm.Parm;
import no.ums.ws.parm.ParmSoap;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class RootFolderViewRun {
    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
        final Parm parm = new Parm();
        parm.setExecutor(service);
        final ParmSoap parmSoap12 = parm.getParmSoap12();
        final String sessionId = parmSoap12.login("mh", "ums", "mh123,1");
        final ParmUiService parmService = new ParmUiServiceSoapImpl(sessionId, parmSoap12, service);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new RootFolder(parmService));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}


