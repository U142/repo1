package no.ums.pas.plugins.centric.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.common.BBPROJECT;
import no.ums.ws.common.cb.CBPROJECTSTATUSREQUEST;
import no.ums.ws.common.cb.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.PasStatus;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSCentricStatus extends WSThread {

    private static final Log log = UmsLog.getLogger(WSCentricStatus.class);

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
			log.warn(e.getMessage(), e);
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error getting CB status";
	}

}
