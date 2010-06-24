package no.ums.pas.core.ws;

import java.awt.event.*;
import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.PAS;
import no.ums.ws.pas.Pasws;



public class WSPowerup extends WSThread
{
	public WSPowerup(ActionListener callback)
	{
		super(callback);
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
			new Pasws(wsdl, service).getPaswsSoap12().powerup();
			setResult(WSRESULTCODE.OK);
			//OnDownloadFinished();			
		}
		catch(Exception e)
		{
			setResult(WSRESULTCODE.FAILED);
			throw e;
		}
		finally
		{
		}
	}
	
	@Override
	protected String getErrorMessage() {
		return "PowerUp failed";
	}

	@Override
	public void OnDownloadFinished()
	{
		if(m_callback!=null)
			m_callback.actionPerformed(new ActionEvent(getResult(), ActionEvent.ACTION_PERFORMED, "act_powerup"));
	}

}