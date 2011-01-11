package no.ums.pas.core.ws;

import java.awt.event.*;
import java.net.URL;

import javax.xml.namespace.QName;


import no.ums.pas.PAS;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.ums.errorhandling.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.pas.*;



public class WSHouseEditor extends WSThread
{
	public class SaveGABResult extends Object {
		private boolean _b_success = false;
		private String _sz_kondmid = "";
		private String _sz_errortext = "";
		public void set_kondmid(String s) { _sz_kondmid = s; }
		public void set_errortext(String s) { _sz_errortext = s; }
		public String get_kondmid() { return _sz_kondmid; }
		public String get_errortext() { return _sz_errortext; }
		public boolean is_success() { return _b_success; }
		
		public SaveGABResult(String kondmid, String error) {
			set_kondmid(kondmid);
			set_errortext(error);
			if(!get_kondmid().equals("-1"))
				_b_success = true;
		}
	}
	String action;
	Inhabitant m_inhab;
	HOUSEEDITOROPERATION m_operation;
	String sz_error = "";
	
	public WSHouseEditor(ActionListener callback, String act, Inhabitant inhab, HOUSEEDITOROPERATION operation)
	{
		super(callback);
		action = act;
		m_inhab = inhab;
		m_operation = operation;
		start();
	}

	@Override
	public void OnDownloadFinished() {
		if(m_callback!=null && m_inhab!=null)
			m_callback.actionPerformed(new ActionEvent(new SaveGABResult(m_inhab.get_kondmid().toString(), sz_error), ActionEvent.ACTION_PERFORMED, action));
			//m_callback.actionPerformed(new ActionEvent(m_inhab, ActionEvent.ACTION_PERFORMED, action));

	}
	
	@Override
	public void call() throws Exception
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
		logon.setSessionid(ui.get_sessionid());
		java.net.URL wsdl;
		try
		{	
			UAddress inadr = new UAddress();
			inadr.setKondmid(m_inhab.get_kondmid());
			inadr.setName(m_inhab.get_adrname());
			inadr.setBday(m_inhab.get_birthday());
			inadr.setNumber(m_inhab.get_number());
			inadr.setAddress(m_inhab.get_postaddr());
			if(m_inhab.get_no().compareTo("")==0)
				inadr.setHouseno(-1);
			else
				inadr.setHouseno(new Integer(m_inhab.get_no()));
			inadr.setLetter(m_inhab.get_letter());
			inadr.setPostno(m_inhab.get_postno());
			inadr.setPostarea(m_inhab.get_postarea());
			inadr.setGno(m_inhab.get_gnumber());
			inadr.setBno(m_inhab.get_bnumber());
			inadr.setImportid(logon.getLDeptpk());
			if(m_inhab.get_region().length()>0)
				inadr.setMunicipalid(m_inhab.get_region());
			else
				inadr.setMunicipalid("0");
			inadr.setStreetid(m_inhab.get_streetid());
			inadr.setLon(m_inhab.get_lon());
			inadr.setLat(m_inhab.get_lat());
			inadr.setBedrift(m_inhab.get_inhabitanttype());
			inadr.setMobile(m_inhab.get_mobile());
			wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			//wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			
			
			UAddress adr = new Pasws(wsdl, service).getPaswsSoap12().houseEditor(inadr, logon, m_operation);
			m_inhab.set_kondmid(adr.getKondmid());
		}
		catch(Exception e)
		{
			//sz_error = e.getMessage();
			//Error.getError().addError("Error occured", "Error in House Editor", e, 1);
			throw e;
		}
		/*finally
		{
			OnDownloadFinished();
		}*/
		
	}

	@Override
	protected String getErrorMessage() {
		return "Error in House Editor";
	}
}