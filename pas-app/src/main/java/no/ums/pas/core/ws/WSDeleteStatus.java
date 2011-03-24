package no.ums.pas.core.ws;

import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.core.Variables;
import no.ums.ws.common.UDELETESTATUSREQUEST;
import no.ums.ws.common.UDELETESTATUSRESPONSE;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.status.PasStatus;

public class WSDeleteStatus extends WSThread {

	IDeleteStatus icallback;
	long n_refno = 0;
	
	public WSDeleteStatus(long n_refno, IDeleteStatus icallback)
	{
		super(null);
		this.icallback = icallback;
		this.n_refno = n_refno;
	}
	
	public interface IDeleteStatus
	{
		public void Complete(long refno, UDELETESTATUSRESPONSE response);
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
		try
		{
			URL wsdl = new URL(vars.WSDL_PASSTATUS);
			QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");

			ULOGONINFO logon = new ULOGONINFO();
			WSFillLogoninfo.fill(logon, Variables.getUserInfo());
			UDELETESTATUSREQUEST req = new UDELETESTATUSREQUEST();
			req.setLRefno(n_refno);
			UDELETESTATUSRESPONSE response = new PasStatus(wsdl, service).getPasStatusSoap12().deleteStatus(logon, req);
			icallback.Complete(n_refno, response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			icallback.Complete(n_refno, UDELETESTATUSRESPONSE.ERROR);
		}
	}

}
