package no.ums.adminui.pas.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.common.PASHAPETYPES;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.ArrayOfUDEPARTMENT;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSGetRestrictionShapes extends WSThread {

    private static final Log log = UmsLog.getLogger(WSGetRestrictionShapes.class);

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
	public void onDownloadFinished() {
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
			log.warn(e.getMessage(), e);
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error getting restrictionshapes";
	}

}
