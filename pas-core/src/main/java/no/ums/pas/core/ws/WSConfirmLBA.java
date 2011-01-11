package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.parm.ObjectFactory;
import no.ums.ws.parm.Parmws;
import no.ums.ws.parm.UConfirmJobResponse;
import no.ums.ws.parm.ULOGONINFO;

import javax.xml.namespace.QName;

public class WSConfirmLBA
{
	private int refno;
	private String jobid;
	private boolean confirm;
	
	public WSConfirmLBA(int n_refno, String sz_jobid, boolean b_confirm)
	{
		refno = n_refno;
		jobid = sz_jobid;
		confirm = b_confirm;
		new ConfirmThread().start();
	}
	
	public class ConfirmThread extends Thread
	{
		public ConfirmThread()
		{
			super("WSConfirmLBA");
		}
		public void run()
		{
			java.net.URL wsdl;
			try
			{			
				wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
				//wsdl = new java.net.URL("http://localhost/WS/ExternalExec.asmx?WSDL"); 
			} catch(Exception e)
			{
				return ;
			}
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			ObjectFactory factory = new no.ums.ws.parm.ObjectFactory();
			ULOGONINFO logon = factory.createULOGONINFO();
			UserInfo info = PAS.get_pas().get_userinfo();
			logon.setLComppk(info.get_comppk());
			logon.setLDeptpk(info.get_current_department().get_deptpk());
			logon.setLUserpk(Long.parseLong(info.get_userpk()));
			logon.setSzCompid(info.get_compid());
			logon.setSzDeptid(info.get_current_department().get_deptid());
			logon.setSzPassword(info.get_passwd());
			logon.setSessionid(info.get_sessionid());
			
			try
			{
				Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
				//UConfirmJobResponse response = myService.getParmwsSoap12().confirmJob(logon, refno, jobid, confirm);
				UConfirmJobResponse response = myService.getParmwsSoap12().confirmJob20(logon, refno, confirm);
				if(response.getResultcode()<0)
				{
					//ERROR occured
					//Error.getError().addError(PAS.l("common_error"), "Could not confirm or cancel the sending", "", Error.SEVERITY_ERROR);
				}
			}
			catch(Exception e)
			{
				Error.getError().addError(PAS.l("common_error"), "Could not confirm or cancel the sending", e, Error.SEVERITY_ERROR);
			}
		}
	}
}