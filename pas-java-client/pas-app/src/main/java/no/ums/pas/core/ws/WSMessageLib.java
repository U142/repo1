package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.ws.common.UBBMESSAGELIST;
import no.ums.ws.common.UBBMESSAGELISTFILTER;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WSMessageLib extends WSThread
{

    private static final Log log = UmsLog.getLogger(WSMessageLib.class);

	UBBMESSAGELIST list;
	long n_servertimestamp = 0;
	public WSMessageLib(ActionListener callback, long n_timestamp)
	{
		super(callback);
		n_servertimestamp = n_timestamp;
	}

	@Override
	public void onDownloadFinished() {
		m_callback.actionPerformed(new ActionEvent(list, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
	}

	@Override
	public void call() throws Exception {
		ULOGONINFO logon = new ULOGONINFO();
		no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
		logon.setLComppk(ui.get_comppk());
		logon.setLDeptpk(ui.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(ui.get_userpk()));
		logon.setSzCompid(ui.get_compid());
		logon.setSzDeptid(ui.get_current_department().get_deptid());
		logon.setSzUserid(ui.get_userid());
		logon.setSzPassword(ui.get_passwd());
		logon.setSessionid(ui.get_sessionid());
		UBBMESSAGELISTFILTER filter = new UBBMESSAGELISTFILTER();
		filter.setNTimefilter(n_servertimestamp);
		java.net.URL wsdl;
		try
		{			
			wsdl = new java.net.URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			list = new Pasws(wsdl, service).getPaswsSoap12().getMessageLibrary(logon, filter);
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"), "Error opening Message Library", e, Error.SEVERITY_ERROR);
			list = new UBBMESSAGELIST();
			list.setNServertimestamp(-1);
			log.warn(e.getMessage(), e);
		}
		finally
		{
			//onDownloadFinished();
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error opening Message Library";
	}
}