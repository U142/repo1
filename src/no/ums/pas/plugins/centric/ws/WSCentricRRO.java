package no.ums.pas.plugins.centric.ws;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.parm.CBALERTPOLYGON;
import no.ums.ws.parm.admin.ParmAdmin;

public class WSCentricRRO extends WSThread {

	//CBMESSAGE_FIELDS ret;
	String action;
	public WSCentricRRO(ActionListener callback, String act) {
		super(callback);
		action = act;
	}

	@Override
	public void OnDownloadFinished() {
		/*if(m_callback != null)
			m_callback.actionPerformed(new ActionEvent(ret, ActionEvent.ACTION_PERFORMED, action));*/
	}

	@Override
	public void call() throws Exception {
		URL wsdl = new URL(vars.WSDL_PARMADMIN);
		QName service = new QName("http://ums.no/ws/parm/", "parmws");
		
		//ret = new ParmAdmin(wsdl, service).getParmAdminSoap12().getMessageFields();
		
	}

	@Override
	protected String getErrorMessage() {
		return "Could not get CB message fields";
	}

}
