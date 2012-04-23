package no.ums.pas.plugins.centric.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.cb.CBMESSAGEFIELDS;
import no.ums.ws.parm.Parmws;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class WSCentricRRO extends WSThread {

    private static final Log log = UmsLog.getLogger(WSCentricRRO.class);

	CBMESSAGEFIELDS ret;
	String action;
	public WSCentricRRO(ActionListener callback, String act) {
		super(callback);
		action = act;
	}

	@Override
	public void onDownloadFinished() {
		if(m_callback != null)
			m_callback.actionPerformed(new ActionEvent(ret, ActionEvent.ACTION_PERFORMED, action));
	}

	@Override
	public void call() throws Exception {
		try
		{
			URL wsdl = new URL(vars.WSDL_EXTERNALEXEC);
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			
			ULOGONINFO logon = new ULOGONINFO();
			UserInfo i = PAS.get_pas().get_userinfo();
			logon.setLComppk(i.get_comppk());
			logon.setLDeptpk(i.get_current_department().get_deptpk());
			logon.setLUserpk(new Long(i.get_userpk()));
			logon.setSessionid(i.get_sessionid());
			logon.setSzPassword(i.get_passwd());
			//ret = new ParmAdmin(wsdl, service).getParmAdminSoap12().getMessageFields();
			ret = new Parmws(wsdl, service).getParmwsSoap12().getCBSendingFields(logon);

		}
		catch(Exception e)
		{
            log.error(e.getMessage(),e);
			throw e;
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Could not get CB message fields";
	}

}
