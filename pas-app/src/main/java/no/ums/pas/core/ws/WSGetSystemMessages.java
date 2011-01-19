package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UBBNEWSLISTFILTER;
import no.ums.ws.pas.USYSTEMMESSAGES;

import javax.xml.namespace.QName;
import java.awt.event.ActionListener;
import java.net.URL;

public class WSGetSystemMessages extends WSThread
{
	USYSTEMMESSAGES ret;
	long n_dbtimestamp = 0;
	public USYSTEMMESSAGES getSystemMessages() { return ret; }
	
	public WSGetSystemMessages(ActionListener callback)
	{
		super(callback);
	}
	@Override
	public void call() throws Exception {
		try
		{
			no.ums.ws.pas.ObjectFactory of = new no.ums.ws.pas.ObjectFactory();
			no.ums.ws.pas.ULOGONINFO logon = of.createULOGONINFO();
			logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			logon.setJobid("0");
			logon.setLAltservers(0);
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLDeptpri(0);
			logon.setLPriserver(0);
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			logon.setSzDeptid("");
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
			logon.setSzStdcc("");
			URL wsdl = new URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			ret = new Pasws(wsdl, service).getPaswsSoap12().getSystemMessages(logon, n_dbtimestamp, UBBNEWSLISTFILTER.IN_BETWEEN_START_END);
			n_dbtimestamp = ret.getNews().getLTimestampDb();
			
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error accessing system messages from server";
	}

	@Override
	public void onDownloadFinished() {
		//System.out.println("System messages downloaded");
	}
	
}