package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UBBMESSAGE;
import no.ums.ws.pas.UBBMESSAGELISTFILTER;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WSMessageLibEdit extends WSThread
{
	protected UBBMESSAGE m_msg;
	public WSMessageLibEdit(ActionListener callback, UBBMESSAGE msg)
	{
		super(callback);
		m_msg = msg;
	}

	@Override
	public void onDownloadFinished() {
		m_callback.actionPerformed(new ActionEvent(m_msg, ActionEvent.ACTION_PERFORMED, "act_messagelib_saved"));
	}

	@Override
	public void call() throws Exception{
		no.ums.ws.pas.ObjectFactory of = new no.ums.ws.pas.ObjectFactory();
		no.ums.ws.pas.ULOGONINFO logon = of.createULOGONINFO();
		no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
		logon.setLComppk(ui.get_comppk());
		logon.setLDeptpk(ui.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(ui.get_userpk()));
		logon.setSzCompid(ui.get_compid());
		logon.setSzDeptid(ui.get_current_department().get_deptid());
		logon.setSzUserid(ui.get_userid());
		logon.setSzPassword(ui.get_passwd());
		logon.setSessionid(ui.get_sessionid());
		UBBMESSAGELISTFILTER filter = of.createUBBMESSAGELISTFILTER();
		//filter.setNTimefilter(n_servertimestamp);
		java.net.URL wsdl;
		try
		{			
			wsdl = new java.net.URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			m_msg = new Pasws(wsdl, service).getPaswsSoap12().insertMessageLibrary(logon, m_msg);
			//list = new Pasws(wsdl, service).getPaswsSoap12().getMessageLibrary(logon, filter);
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"), "Error saving message library", e, Error.SEVERITY_ERROR);
			m_msg.setBValid(false);
			//list = new UBBMESSAGELIST();
			//list.setNServertimestamp(-1);
		}
		finally
		{
			//onDownloadFinished();
		}		
	}

	@Override
	protected String getErrorMessage() {
		return "Error saving message library";
	}
}