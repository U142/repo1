package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASLOGON;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;



public class WSLogoff extends WSThread
{
	protected String sz_username, sz_companyid, sz_password;
	UPASLOGON ret;
	protected boolean b_responded = false;
	public boolean getResponded() { return b_responded;}
	String sz_last_error = "";
	public boolean hasError() { return (sz_last_error.length() > 0 ? true : false); }
	public String get_last_error() { return sz_last_error; }
	public WSLogoff(ActionListener callback, UserInfo info)
	{
		super(callback);
		b_responded = false;
		this.sz_username = info.get_userid();
		this.sz_companyid = info.get_compid();
		this.sz_password = info.get_passwd();
		start();
	}
	
	@Override
	public void call() throws Exception
	{
		try
		{
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/Pas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			ULOGONINFO l = new ULOGONINFO();
			l.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			l.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			l.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			l.setJobid(WSThread.GenJobId());
			l.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
			l.setLAltservers(0);
			l.setLComppk(0);
			l.setLDeptpk(0);
			l.setLPriserver(0);
			l.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			l.setSzStdcc("");
			l.setLDeptpri(3);
			l.setSzDeptid("");
			
			boolean b = new Pasws(wsdl, service).getPaswsSoap12().pasLogoff(l);
		}
		catch(Exception e)
		{
			//no.ums.pas.ums.errorhandling.Error.getError().addError("Logoff script Failed", "An error occured in logoff", e, 1);
			sz_last_error = e.getMessage();
			ret = new UPASLOGON();
			ret.setFGranted(false);
			throw e;
		}
		finally
		{
			b_responded = true;
			//onDownloadFinished();
		}
	}
	@Override
	protected String getErrorMessage() {
		return "An error occured in logoff";
	}
	@Override
	public void onDownloadFinished()
	{
		if(m_callback!=null && ret!=null)
			m_callback.actionPerformed(new ActionEvent(ret, ActionEvent.ACTION_PERFORMED, "act_logon"));
		
	}
}