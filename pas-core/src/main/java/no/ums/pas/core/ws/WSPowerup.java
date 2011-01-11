package no.ums.pas.core.ws;

import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UPOWERUPRESPONSE;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;



public class WSPowerup extends WSThread
{
	UPOWERUPRESPONSE resp = null;
	public UPOWERUPRESPONSE getResponse() { return resp; }
	
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
			resp = new Pasws(wsdl, service).getPaswsSoap12().powerup();
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
			m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_powerup"));
	}

}