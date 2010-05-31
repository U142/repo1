package no.ums.pas.core.ws;

import java.awt.event.*;
import java.net.URL;

import javax.xml.namespace.QName;


import no.ums.pas.PAS;
import no.ums.pas.send.AddressCount;
import no.ums.pas.send.SendController;
import no.ums.ws.parm.*;



public class WSAdrcount extends WSThread
{
	AddressCount ret = null;
	UMAPSENDING m_sending;
	String action;
	public WSAdrcount(ActionListener callback, String act, UMAPSENDING sending)
	{
		super(callback);
		action = act;
		m_sending =  sending;
	}

	@Override
	public void OnDownloadFinished() {
		if(m_callback!=null && ret!=null)
			m_callback.actionPerformed(new ActionEvent(ret, ActionEvent.ACTION_PERFORMED, action));

	}

	@Override
	public void run() {
		try
		{
			URL wsdl = new URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			
			m_sending.setNAddresstypes(m_sending.getNAddresstypes());
			ULOGONINFO l = new ULOGONINFO();
			l.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			l.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			l.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			l.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			l.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
			UAdrCount a = new Parmws(wsdl, service).getParmwsSoap12().getAdrCount(l, m_sending);
			ret = new AddressCount(a.getNPrivateFixed(),
									a.getNCompanyFixed(),
									a.getNPrivateMobile(),
									a.getNCompanyMobile(),
									a.getNPrivateSms(),
									a.getNCompanySms(),
									a.getNPrivateNonumber(),
									a.getNCompanyNonumber(),
									a.getNPrivateFax()+a.getNCompanyFax(),
									a.getNDuplicates());
									
		}
		catch(Exception e)
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("Error", "Address Count Failed", e, 1);
		}
		finally
		{
			OnDownloadFinished();
		}
	}
}