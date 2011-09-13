package no.ums.pas.send;

import no.ums.pas.core.dataexchange.soap.SoapExecAlert;
import no.ums.pas.core.dataexchange.soap.SoapExecAlert.SnapAlertResults;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.send.sendpanels.SendingResultsView;

public class OpenSendingResultsView
{
	public static void main(String [] args)
	{
		try
		{
			UserInfo ui = new UserInfo();
			SnapAlertResults sar = new SoapExecAlert("0", "SIMULATE", ui).newSnapAlertResult();
			SendingResultsView srv = new SendingResultsView(null, false);
			srv.getAnswer(sar);
			srv.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}