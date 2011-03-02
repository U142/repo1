package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.core.project.Project;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UPROJECTREQUEST;
import no.ums.ws.pas.UPROJECTRESPONSE;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSProject extends WSThread
{
	String sz_callback;
	UPROJECTREQUEST projectrequest = null; 
	UPROJECTRESPONSE projectresponse = null;
	public WSProject(UPROJECTREQUEST projectrequest, ActionListener callback, String sz_callback)
	{
		super(callback);
		this.sz_callback = sz_callback;
		this.projectrequest = projectrequest;
	}
	@Override
	public void onDownloadFinished() {
		try
		{
			if(projectresponse==null)
			{
				projectresponse = new UPROJECTRESPONSE();
				projectresponse.setNProjectpk(-1);
			}
			else
			{
				Project project = new Project();
				project.set_createtimestamp(Long.toString(projectresponse.getNCreatedtimestamp()));
				project.set_projectname(projectresponse.getSzName());
				project.set_projectpk(Long.toString(projectresponse.getNProjectpk()));
				project.set_updatetimestamp(Long.toString(projectresponse.getNUpdatedtimestamp()));
				m_callback.actionPerformed(new ActionEvent(project, ActionEvent.ACTION_PERFORMED, sz_callback));
				PAS.pasplugin.onOpenProject(project, -1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void call() throws Exception
	{	
		ULOGONINFO logon = new ULOGONINFO();
		logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		try
		{
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/Pas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			projectresponse = new Pasws(wsdl, service).getPaswsSoap12().uCreateProject(logon, projectrequest);
			
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"), "Error saving project", e, Error.SEVERITY_ERROR);
		}
		finally
		{
			//onDownloadFinished();
		}

	}
	@Override
	protected String getErrorMessage() {
		return "Error saving project";
	}
}
