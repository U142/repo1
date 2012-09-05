package no.ums.pas.plugins.centric.ws;

import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.cb.CBPROJECTSTATUSREQUEST;
import no.ums.ws.common.cb.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.PasStatus;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.net.URL;

/**
 * @author Svein Anfinnsen <sa@ums.no>
 */
public class WSCentricStatusTest {

	@Ignore
    @Test
    public void testGetCBStatus() throws Exception {
        CBPROJECTSTATUSREQUEST cbsreq = new CBPROJECTSTATUSREQUEST();
        CBPROJECTSTATUSRESPONSE cbpres;

        ULOGONINFO l = new ULOGONINFO();
        l.setLComppk(1);
        l.setLDeptpk(3);
        l.setLUserpk(4);
        l.setSzPassword("3d62f18bc25235bf17a1972ce9c6a6dbd0985fe51384b62a30f31dc002644ada647b072e79c3b5a1f076106b7410466c28d6c34a9b4b6a2b7408dd2ce402f168");
        l.setSzStdcc("0047");
        l.setSessionid("58a4d9b7-87ae-4805-aa9f-8a98cfb0");

        cbsreq.setLProjectpk(571);
        cbsreq.setLogon(l);

        //To change body of created methods use File | Settings | File Templates.
        URL wsdl = new URL("http://secure.ums2.no/pas/ws_NLAlert/ws/passtatus.asmx?wsdl");
        QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");

        cbpres = new PasStatus(wsdl, service).getPasStatusSoap12().getCBStatus(cbsreq);
    }
}
