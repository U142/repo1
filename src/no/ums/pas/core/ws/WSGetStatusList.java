package no.ums.pas.core.ws;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;


import no.ums.pas.PAS;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.pas.ObjectFactory;
import no.ums.ws.pas.status.*;
//import no.ums.ws.pas.*;

public class WSGetStatusList extends WSThread
{
	private ArrayList<StatusListObject> m_arr_statusobjects;
	
	public WSGetStatusList(ActionListener callback)
	{
		super(callback);
	}

	@Override
	public void OnDownloadFinished() {
		if(m_arr_statusobjects==null)
		{
			m_arr_statusobjects = new ArrayList<StatusListObject>();
		}
		try{
			m_callback.actionPerformed(new ActionEvent(m_arr_statusobjects, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
		}
		catch(Exception e) { Error.getError().addError("XMLGetStatusList","Exception in onDownloadFinished",e,1); }			
		
	}

	@Override
	public void call() throws Exception {
		no.ums.ws.pas.status.ObjectFactory of = new no.ums.ws.pas.status.ObjectFactory();
		no.ums.ws.pas.status.ULOGONINFO logon = of.createULOGONINFO();
		WSFillLogoninfo.fill(logon, PAS.get_pas().get_userinfo());
		try
		{
			URL wsdl = new URL(vars.WSDL_PASSTATUS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PasStatus.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/PasStatus.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");
			//new ParmAdmin(wsdl, service).getParmAdminSoap12().updateParm(bytes, logon, xmlFilename, polyFileName)
			UStatusListResults response = new PasStatus(wsdl, service).getPasStatusSoap12().getStatusList(logon);
			m_arr_statusobjects = new ArrayList<StatusListObject>();
			
			for(int i=0; i < response.getList().getUStatusListItem().size(); i++)
			{
				UStatusListItem item = response.getList().getUStatusListItem().get(i);
				StatusListObject obj = new StatusListObject(item.getNRefno(), item.getNSendingtype(), item.getNTotitem(), 
						item.getNAltjmp(), item.getNCreatedate(), item.getNCreatetime(), item.getSzSendingname(),
						item.getNSendingstatus(), item.getNGroup(), item.getNType(), item.getNDeptpk(),
						item.getSzDeptid(), new Long(item.getNProjectpk()).toString(), item.getSzProjectname(), new Long(item.getNCreatetimestamp()).toString(),
						new Long(item.getNUpdatetimestamp()).toString());
				m_arr_statusobjects.add(obj);
			}
			//OnDownloadFinished();
		}
		catch(Exception e)
		{
			throw e;
		}
		/*catch(SOAPFaultException e)
		{
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"), "Unable to open statuslist", e, Error.SEVERITY_ERROR);
			PAS.pasplugin.onSoapFaultException(e);
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "Unable to open statuslist", e, Error.SEVERITY_ERROR);			
		}
		finally
		{
			OnDownloadFinished();
		}*/
		
	}

	@Override
	protected String getErrorMessage() {
		return "Unable to open statuslist";
	}

}