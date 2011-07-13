package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.errorhandling.Error;

import javax.xml.ws.soap.SOAPFaultException;
import java.awt.event.ActionListener;
import java.util.UUID;

public abstract class WSThread extends Thread
{

    private static final Log log = UmsLog.getLogger(WSThread.class);

	public static enum WSRESULTCODE
	{
		NOT_SET,
		OK,
		SOAPFAULT,
		FAILED,
	};
	public static String GenJobId()
	{
		return UUID.randomUUID().toString();
	}
	protected ActionListener m_callback;
	String sz_cb_cmd;
	protected WSRESULTCODE result = WSRESULTCODE.NOT_SET;
	protected void setResult(WSRESULTCODE c) { result = c; }
	public WSRESULTCODE getResult() { return result; }
	
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
			log.warn(e.getMessage(), e);
			boolean b = PAS.pasplugin.onSoapFaultException(PAS.get_pas().get_userinfo(), e);
			if(!b) {
                Error.getError().addError(Localization.l("common_error"), getErrorMessage(), e, Error.SEVERITY_ERROR);
            }

		}
		catch(Exception e)
		{
            Error.getError().addError(Localization.l("common_error"), getErrorMessage(), e, Error.SEVERITY_ERROR);
		}
		finally
		{
			onDownloadFinished();
		}
	}
	protected abstract String getErrorMessage();
	public abstract void onDownloadFinished();
	public abstract void call() throws Exception;
	
	public void runNonThreaded()
	{
		run();
	}
}