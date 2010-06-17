package no.ums.pas.core.ws;

import java.awt.event.*;
import java.util.UUID;

import javax.xml.ws.soap.SOAPFaultException;

import no.ums.pas.PAS;
import no.ums.pas.ums.errorhandling.Error;

public abstract class WSThread extends Thread
{
	public static String GenJobId()
	{
		return UUID.randomUUID().toString();
	}
	ActionListener m_callback;
	String sz_cb_cmd;
	public void set_callbackcmd(String sz)
	{
		sz_cb_cmd = sz;
	}
	public WSThread(ActionListener callback)
	{
		m_callback = callback;
	}
	public void run()
	{
		try
		{
			call();
		}
		catch(SOAPFaultException e)
		{
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"), getErrorMessage(), e, Error.SEVERITY_ERROR);
			PAS.pasplugin.onSoapFaultException(PAS.get_pas().get_userinfo(), e);
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), getErrorMessage(), e, Error.SEVERITY_ERROR);			
		}
		finally
		{
			OnDownloadFinished();
		}
	}
	protected abstract String getErrorMessage();
	public abstract void OnDownloadFinished();
	public abstract void call() throws Exception;
	
	public void runNonThreaded()
	{
		run();
	}
}