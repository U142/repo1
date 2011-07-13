package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UWeatherReportResults;
import no.ums.ws.pas.UWeatherResult;
import no.ums.ws.pas.UWeatherSearch;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class WSWeatherReport extends WSThread
{
    private static final Log log = UmsLog.getLogger(WSWeatherReport.class);

	String action;
	UWeatherSearch params;
	UWeatherReportResults results;
	public void setParams(UWeatherSearch w) { params = w; }
	public UWeatherReportResults getResults() { return results; }
	public UWeatherResult getResult(int n)
	{
		try
		{
			return getResults().getResults().getUWeatherResult().get(0);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public WSWeatherReport(ActionListener callback, String act, UWeatherSearch params)
	{
		super(callback);
		this.action = act;
		this.params = params;
		//start();
	}
	
	public static String getWindIcon(float f) {
		if(f > 337.5 || f <= 22.5)
			return "N.gif";
		else if(f > 22.5 && f <= 67.5)
			return "NE.gif";
		else if(f > 67.5 && f <= 112.5)
			return "E.gif";
		else if(f > 112.5 && f <= 157.5)
			return "SE.gif";
		else if(f > 157.5 && f <= 202.5)
			return "S.gif";
		else if(f > 202.5 && f <= 247.5)
			return "SW.gif";
		else if(f > 247.5 && f <= 292.5)
			return "W.gif";
		else if(f > 292.5 && f <= 337.5)
			return "NW.gif";
		else
			return null;
	}


	@Override
	public void onDownloadFinished() {
		if(m_callback!=null && results!=null)
			m_callback.actionPerformed(new ActionEvent(results, ActionEvent.ACTION_PERFORMED, action));
	}
	
	@Override
	public void call() throws Exception
	{
		ULOGONINFO logon = new ULOGONINFO();
		no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
		logon.setLComppk(ui.get_comppk());
		logon.setLDeptpk(ui.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(ui.get_userpk()));
		logon.setSzCompid(ui.get_compid());
		logon.setSzDeptid(ui.get_current_department().get_deptid());
		logon.setSzUserid(ui.get_userid());
		logon.setSzPassword(ui.get_passwd());
		logon.setSzStdcc(ui.get_current_department().get_stdcc());
		logon.setSessionid(ui.get_sessionid());
		java.net.URL wsdl;
		try
		{	
			wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			//wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			
			
			results = new Pasws(wsdl, service).getPaswsSoap12().getWeatherReport(logon, params);

		}
		catch(Exception e)
		{
			//Error.getError().addError("Error", "An error occured when downloading weather report", e, 1);
			log.debug(e.getMessage());
			throw e;
		}
		finally
		{
			//onDownloadFinished();
		}
		
	}
	@Override
	protected String getErrorMessage() {
		return "An error occured when downloading weather report";
	}
}