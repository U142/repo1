package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.ObjectFactory;
import no.ums.ws.parm.Parmws;

import javax.xml.namespace.QName;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSTasResend extends WSThread {

	private int l_refno;
	private int l_operator;
	ExecResponse response;
	
	public WSTasResend(ActionListener callback, int refno, int operator)
	{
		super(callback);
		l_refno = refno;
		l_operator = operator;
		start();
		
	}

	@Override
	public void onDownloadFinished() {
		//if(m_callback!=null)
			//m_callback.actionPerformed(new ActionEvent(response, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
	}

	@Override
	public void call() throws Exception{
		try
		{
			ULOGONINFO logon = new ULOGONINFO();
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
			logon.setJobid(WSThread.GenJobId());
			logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());

			URL wsdl = new URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Tas.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/Tas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			response = new Parmws(wsdl, service).getParmwsSoap12().tasResend(l_refno, l_operator, logon);
			
			
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"),"Error in TAS", e, Error.SEVERITY_ERROR);
			throw e;
		}
		finally
		{
			//onDownloadFinished();
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error in TAS";
	}
}