package no.ums.pas.core.ws;

import java.awt.event.*;
import java.net.URL;

import javax.xml.namespace.QName;


import no.ums.pas.PAS;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.ums.errorhandling.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.pas.*;



public class WSClosestGAB extends WSThread
{	
	String action;
	UMapPoint params;
	Inhabitant results;
	public WSClosestGAB(ActionListener callback, String act, UMapPoint p)
	{
		super(callback);
		this.action = act;
		this.params = p;
		start();
	}
	
	@Override
	public void OnDownloadFinished() {
		if(m_callback!=null && results!=null)
			m_callback.actionPerformed(new ActionEvent(results, ActionEvent.ACTION_PERFORMED, action));
	}
	
	@Override
	public void run()
	{
		no.ums.ws.pas.ObjectFactory of = new no.ums.ws.pas.ObjectFactory();
		no.ums.ws.pas.ULOGONINFO logon = of.createULOGONINFO();
		no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
		logon.setLComppk(ui.get_comppk());
		logon.setLDeptpk(ui.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(ui.get_userpk()));
		logon.setSzCompid(ui.get_compid());
		logon.setSzDeptid(ui.get_current_department().get_deptid());
		logon.setSzUserid(ui.get_userid());
		logon.setSzPassword(ui.get_passwd());
		logon.setSzStdcc(ui.get_current_department().get_stdcc());
		java.net.URL wsdl;
		try
		{	
			wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			//wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			
			
			UGabResultFromPoint temp = (UGabResultFromPoint)new Pasws(wsdl, service).getPaswsSoap12().getNearestGABFromPoint(logon, params);
			results = new Inhabitant(temp.getLon(), temp.getLat());
			results.set_postaddr(temp.getName());
			results.set_no(temp.getNo());
			results.set_postarea(temp.getRegion());

		}
		catch(Exception e)
		{
			Error.getError().addError("Error", "An error occured while searching for nearest building", e, 1);
		}
		finally
		{
			OnDownloadFinished();
		}
		
	}	
}