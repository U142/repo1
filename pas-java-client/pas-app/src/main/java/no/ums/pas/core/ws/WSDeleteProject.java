package no.ums.pas.core.ws;

import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.Variables;
import no.ums.ws.common.UDeleteProjectRequest;
import no.ums.ws.common.UDeleteProjectResponse;
import no.ums.ws.common.UDeleteStatusResponse;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.status.PasStatus;

public class WSDeleteProject extends WSThread {

    private static final Log log = UmsLog.getLogger(WSDeleteProject.class);

	IDeleteProject icallback;
	long projectpk;
	
	public WSDeleteProject(long projectpk, IDeleteProject callback)
	{
		super(null);
		this.icallback = callback;
		this.projectpk = projectpk;
	}
	
	public interface IDeleteProject
	{
		public void Complete(long projectpk, UDeleteProjectResponse response);
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
		UDeleteProjectResponse response = new UDeleteProjectResponse();
		try
		{
			URL wsdl = new URL(vars.WSDL_PASSTATUS);
			QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");
			ULOGONINFO logon = new ULOGONINFO();
			WSFillLogoninfo.fill(logon, Variables.getUserInfo());
			UDeleteProjectRequest req = new UDeleteProjectRequest();
			req.setLProjectpk(projectpk);
			response = new PasStatus(wsdl, service).getPasStatusSoap12().deleteProject(logon, req);
			icallback.Complete(projectpk, response);
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
			icallback.Complete(projectpk, response);
		}
	}

}
