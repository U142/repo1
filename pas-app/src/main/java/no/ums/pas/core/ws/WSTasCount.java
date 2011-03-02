package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.common.ULBACOUNTRY;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.tas.*;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

public class WSTasCount extends WSThread
{
	UTASREQUEST result;
	List<ULBACOUNTRY> m_countries;
	public WSTasCount(ActionListener callback, List<ULBACOUNTRY> countries)
	{
		super(callback);
		m_countries = countries;
	}
	@Override
	public void call() throws Exception
	{
		try
		{
			ULOGONINFO logon = new ULOGONINFO();
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
			logon.setJobid(WSThread.GenJobId());
			logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());

			URL wsdl = new URL(vars.WSDL_TAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Tas.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/Tas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/tas", "tasws");
			ArrayOfULBACOUNTRY arr = new ArrayOfULBACOUNTRY();
			for(int i=0; i < m_countries.size(); i++)
			{
				arr.getULBACOUNTRY().add(m_countries.get(i));
			}
			result = new Tasws(wsdl, service).getTaswsSoap12().getAdrCount(logon, arr); //new Pasws(wsdl, service).getPaswsSoap12().uCreateProject(logon, projectrequest);
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"),"Error in TAS Count Request", e, Error.SEVERITY_ERROR);
			result = new UTASREQUEST();
			result.setBSuccess(false);
			throw e;
		}
		finally
		{
			//onDownloadFinished();
		}

	}
	@Override
	protected String getErrorMessage() {
		return "Error in TAS Count Request";
	}
	@Override
	public void onDownloadFinished()
	{
		m_callback.actionPerformed(new ActionEvent(result, ActionEvent.ACTION_PERFORMED, "act_tascount_finished"));
	}
}