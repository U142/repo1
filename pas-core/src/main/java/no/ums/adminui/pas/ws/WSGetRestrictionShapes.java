package no.ums.adminui.pas.ws;

import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.pas.ArrayOfUDEPARTMENT;
import no.ums.ws.pas.PASHAPETYPES;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.ULOGONINFO;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSGetRestrictionShapes extends WSThread {

	private String action;
	private ULOGONINFO logon;
	private PASHAPETYPES type;
	private ArrayOfUDEPARTMENT res;
	
	public WSGetRestrictionShapes(ActionListener callback, String action, ULOGONINFO logon, PASHAPETYPES type) {
		super(callback);
		this.logon = logon;
		this.type = type;
		this.action = action;
	}
	

	@Override
	public void OnDownloadFinished() {
		if(res==null)
		{
			res = new ArrayOfUDEPARTMENT();
			
		}
		
		if(m_callback!=null && res!=null) {
			//cbpreq.setLTimefilter(cbpres.getLDbTimestamp());
			m_callback.actionPerformed(new ActionEvent(res, ActionEvent.ACTION_PERFORMED, action));

		}
		
	}

	@Override
	public void call() throws Exception {
		try
		{
			URL wsdl = new URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			res = new Pasws(wsdl, service).getPaswsSoap12().getRestrictionShapes(logon, type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error getting restrictionshapes";
	}

}
