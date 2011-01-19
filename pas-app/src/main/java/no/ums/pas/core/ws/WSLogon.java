package no.ums.pas.core.ws;

import no.ums.ws.pas.BBUSERBLOCKREASONS;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.ULOGONINFO;
import no.ums.ws.pas.UPASLOGON;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;



public class WSLogon extends WSThread
{
	protected String sz_username, sz_companyid, sz_password;
	UPASLOGON ret;
	BBUSERBLOCKREASONS reason = BBUSERBLOCKREASONS.NONE;
	public BBUSERBLOCKREASONS getReason() { return reason; }
	protected boolean b_responded = false;
	public boolean getResponded() { return b_responded;}
	String sz_last_error = "";
	public boolean hasError() { return (sz_last_error.length() > 0 ? true : false); }
	public String get_last_error() { return sz_last_error; }
	public WSLogon(ActionListener callback,
				String sz_username, String sz_companyid, String sz_password)
	{
		super(callback);
		b_responded = false;
		this.sz_username = sz_username;
		this.sz_companyid = sz_companyid;
		this.sz_password = sz_password;
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
			l.setSzUserid(sz_username);
			l.setSzCompid(sz_companyid);
			l.setSzPassword(sz_password);
			System.out.println("Sending \"" + l.getSzUserid() + "\" \"" + l.getSzCompid() + "\"");
			l.setJobid(WSThread.GenJobId());
			l.setLAltservers(0);
			l.setLComppk(0);
			l.setLDeptpk(0);
			l.setLPriserver(0);
			l.setLUserpk(0);
			l.setSzStdcc("");
			l.setLDeptpri(3);
			l.setSzDeptid("");
			
			ret = new Pasws(wsdl, service).getPaswsSoap12().pasLogon(l);
			reason = ret.getReason();
		}
		catch(Exception e)
		{
			//no.ums.pas.ums.errorhandling.Error.getError().addError("Logon script Failed", "An error occured in logon", e, 1);
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
		return "An error occured in logon";
	}
	@Override
	public void onDownloadFinished()
	{
		if(m_callback!=null && ret!=null)
			m_callback.actionPerformed(new ActionEvent(ret, ActionEvent.ACTION_PERFORMED, "act_logon"));
		
	}
}