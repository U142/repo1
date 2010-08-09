package no.ums.pas.plugins.centric.ws;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.PAS;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.parm.CBALERTPOLYGON;
import no.ums.ws.parm.CBMESSAGELIST;
import no.ums.ws.parm.CBOPERATION;
import no.ums.ws.parm.CBOPERATIONBASE;
import no.ums.ws.parm.CBSENDBASE;
import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.parm.Parmws;
import no.ums.ws.parm.ULOGONINFO;
import no.ums.ws.parm.admin.ParmAdmin;

public class WSCentricSend extends WSThread {

	private String action;
	private CBOPERATIONBASE cbsb;
	private CBSENDINGRESPONSE cbsr;
	
	public WSCentricSend(ActionListener callback, String action, CBOPERATIONBASE cbsb) {
		super(callback);
		this.cbsb = cbsb;
		this.action = action;
	}

	@Override
	public void OnDownloadFinished() {
		if(m_callback!=null && cbsr!=null)
			m_callback.actionPerformed(new ActionEvent(cbsr, ActionEvent.ACTION_PERFORMED, action));
		else
			m_callback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_error"));
	}

	@Override
	public void call() throws Exception {
		URL wsdl = new URL(vars.WSDL_EXTERNALEXEC);
		QName service = new QName("http://ums.no/ws/parm/", "parmws");
		
		ULOGONINFO l = new ULOGONINFO();
		l.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		l.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		l.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		l.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		l.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
		l.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		
		cbsr = new Parmws(wsdl, service).getParmwsSoap12().execCBOperation(l, cbsb);
	}

	@Override
	protected String getErrorMessage() {
		return "Error sending CB message";
	}

}
