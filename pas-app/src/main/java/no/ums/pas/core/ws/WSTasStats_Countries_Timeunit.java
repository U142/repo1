package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.pas.tas.*;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class WSTasStats_Countries_Timeunit extends WSThread
{
	ULBASTATISTICSFILTER filter;
	ArrayOfULBACOUNTRYSTATISTICS results;
	
	public WSTasStats_Countries_Timeunit(ActionListener callback, ULBASTATISTICSFILTER filter)
	{
		super(callback);
		this.filter = filter;
	}

	@Override
	public void onDownloadFinished() {
		if(results!=null)
			m_callback.actionPerformed(new ActionEvent(results, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
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
			QName service = new QName("http://ums.no/ws/pas/tas", "tasws");
			
			results = new Tasws(wsdl, service).getTaswsSoap12().getStatsCountriesPerTimeunit(logon, filter);
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"),"Error in TAS Count Request", e, Error.SEVERITY_ERROR);
			results = new ArrayOfULBACOUNTRYSTATISTICS();
			throw e;
		}
		finally
		{
			//onDownloadFinished();
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error in TAS Stats Countries Timeunit";
	}
	
}