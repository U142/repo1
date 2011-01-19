package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.pas.tas.ObjectFactory;
import no.ums.ws.pas.tas.Tasws;
import no.ums.ws.pas.tas.ULOGONINFO;
import no.ums.ws.pas.tas.UTASUPDATES;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class WSTas extends WSThread
{
	//ArrayOfULBACONTINENT m_continentlist;
	UTASUPDATES m_updates;
	long n_timefilter = 0;
	long n_timefilter_requestlog = 0;
	public WSTas(ActionListener callback, long timefilter, long timefilter_requestlog)
	{
		super(callback);
		n_timefilter = timefilter;
		n_timefilter_requestlog = timefilter_requestlog;
		start();
	}

	@Override
	public void onDownloadFinished() {
		if(m_callback!=null)
			m_callback.actionPerformed(new ActionEvent(m_updates, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
	}

	@Override
	public void call() throws Exception {
		try
		{
			ObjectFactory of = new ObjectFactory();
			ULOGONINFO logon = of.createULOGONINFO();
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
			m_updates = new Tasws(wsdl, service).getTaswsSoap12().getContinentsAndCountries(logon, n_timefilter, n_timefilter_requestlog);//new Pasws(wsdl, service).getPaswsSoap12().uCreateProject(logon, projectrequest);
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"),"Error in TAS", e, Error.SEVERITY_ERROR);
			m_updates = new UTASUPDATES(); //new ArrayOfULBACONTINENT();
			throw e;
		}
		finally
		{
			//onDownloadFinished();
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error in TAS";
	}
}