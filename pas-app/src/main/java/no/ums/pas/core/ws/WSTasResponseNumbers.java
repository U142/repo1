package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.tas.*;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

public class WSTasResponseNumbers extends WSThread {

	List<UTASRESPONSENUMBER> m_responsenumbers;
	
	public WSTasResponseNumbers(ActionListener callback) {
		super(callback);
	}

	@Override
	public void onDownloadFinished() {
		if(m_callback != null)
			m_callback.actionPerformed(new ActionEvent(m_responsenumbers, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
	}

	@Override
	public void call() throws Exception {
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

			URL wsdl = new URL(vars.WSDL_TAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Tas.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/Tas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/tas", "tasws");
			
			ArrayOfUTASRESPONSENUMBER arr = new Tasws(wsdl, service).getTaswsSoap12().getResponseNumbers(logon);
			m_responsenumbers = arr.getUTASRESPONSENUMBER();
		}
		catch(Exception e)
		{
			//Error.getError().addError(PAS.l("common_error"),"Error in getting TAS response numbers", e, Error.SEVERITY_ERROR);
			m_responsenumbers = new ArrayOfUTASRESPONSENUMBER().getUTASRESPONSENUMBER();
			throw e;
		}
		finally
		{
			//onDownloadFinished();
		}
		
	}

	@Override
	protected String getErrorMessage() {
		return "Error in getting TAS response numbers";
	}

}
