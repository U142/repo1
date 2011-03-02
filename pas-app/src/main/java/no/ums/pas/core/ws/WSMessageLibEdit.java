package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.common.UBBMESSAGE;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;

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
		java.net.URL wsdl;
		try
		{			
			wsdl = new java.net.URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			m_msg = new Pasws(wsdl, service).getPaswsSoap12().insertMessageLibrary(logon, m_msg);
		}
		catch(Exception e)
		{
			m_msg.setBValid(false);
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error saving message library";
	}
}