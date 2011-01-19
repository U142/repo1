package no.ums.pas.plugins.centric.ws;

import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.pas.status.BBPROJECT;
import no.ums.ws.pas.status.CBPROJECTSTATUSREQUEST;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.PasStatus;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSCentricStatus extends WSThread {

	private String action;
	private CBPROJECTSTATUSREQUEST cbpreq;
	private CBPROJECTSTATUSRESPONSE cbpres;
	
	public WSCentricStatus(ActionListener callback, String action, CBPROJECTSTATUSREQUEST request) {
		super(callback);
		this.cbpreq = request;
		this.action = action;
	}
	

	@Override
	public void onDownloadFinished() {
		if(cbpres==null)
		{
			cbpres = new CBPROJECTSTATUSRESPONSE();
			cbpres.setLDbTimestamp(0);
			BBPROJECT p = new BBPROJECT();
			p.setLProjectpk("-1");
			cbpres.setProject(p);
		}
		
		if(m_callback!=null && cbpres!=null) {
			//cbpreq.setLTimefilter(cbpres.getLDbTimestamp());
			m_callback.actionPerformed(new ActionEvent(cbpres, ActionEvent.ACTION_PERFORMED, action));

		}
		
	}
	
	


	@Override
	public void call() throws Exception {
		try
		{
			URL wsdl = new URL(vars.WSDL_PASSTATUS);
			QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");
					
			cbpres = new PasStatus(wsdl, service).getPasStatusSoap12().getCBStatus(cbpreq);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error getting CB status";
	}

}
