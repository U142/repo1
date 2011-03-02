package no.ums.pas.core.ws;

import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.PAS;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.MDSOADCDEFAULT;
import no.ums.ws.pas.Pasws;

public class WSSetDefaultOadc extends WSThread
{
	private String oadc;
	private MDSOADCDEFAULT ret = null;
	public MDSOADCDEFAULT getReturnValue() { return ret; }
	public WSSetDefaultOadc(String oadc)
	{
		super(null);
		this.oadc = oadc;
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

	@Override
	public void onDownloadFinished() {
		
	}

	@Override
	public void call() throws Exception {
		ULOGONINFO logon = new ULOGONINFO();
		logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		try
		{
			URL wsdl = new URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			ret = new Pasws(wsdl, service).getPaswsSoap12().setDefaultOadc(logon, oadc);
        	PAS.get_pas().get_userinfo().set_default_oadc(getReturnValue().getSzValue());
        	PAS.get_pas().get_userinfo().set_default_oadc_type(getReturnValue().getLType());
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
		}

	}
}
