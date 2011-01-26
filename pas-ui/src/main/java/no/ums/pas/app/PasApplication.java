package no.ums.pas.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

import no.ums.pas.ui.LogonDialog;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.PaswsSoap;
import org.jdesktop.application.Application;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PasApplication extends Application {

    public static PasApplication getInstance() {
        return Application.getInstance(PasApplication.class);
    }

    private Pasws pasws;

    public PasApplication() {
        try {
            QName service = new QName("http://ums.no/ws/pas/", "pasws");
            pasws = new Pasws(new URL("https://secure.ums2.no/PAS/ws_pasutv/WS/PAS.asmx?WSDL"), service);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Illegal URL specified", ex);
        }
    }

    public PaswsSoap getPasws() {
        return pasws.getPaswsSoap12();
    }

    @Override
    protected void startup() {
        new LogonDialog().setVisible(true);
    }

    public static void main(String[] args) {
        launch(PasApplication.class, args);
    }
}
