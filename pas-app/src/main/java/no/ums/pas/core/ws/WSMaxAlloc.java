package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UMAXALLOC;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WSMaxAlloc extends WSThread
{
	public class MaxAlloc extends Object {
		private String _sz_projectpk;
		private String _sz_refno;
		private String _sz_maxalloc;
		private boolean _b_haserror = false;
		public boolean has_error() { return _b_haserror; }
		public String get_projectpk() { return _sz_projectpk; }
		public String get_refno() { return _sz_refno; }
		public String get_maxalloc() { return _sz_maxalloc; }
		
		public MaxAlloc(String sz_projectpk, String sz_refno, String sz_maxalloc) {
			_sz_projectpk	= sz_projectpk;
			_sz_refno		= sz_refno;
			_sz_maxalloc	= sz_maxalloc;
			init();
		}
		public void init() {
			if(new Integer(_sz_maxalloc).intValue() < 0)
				_b_haserror = true;
		}
	}	
	
	UMAXALLOC maxalloc_request;
	UMAXALLOC maxalloc_return;
	String m_action;
	
	public WSMaxAlloc(UMAXALLOC max, ActionListener callback, String action)
	{
		super(callback);
		maxalloc_request = max;
		m_callback = callback;
		m_action = action;
	}
	
	public void call() throws Exception
	{
		try
		{
			no.ums.ws.pas.ObjectFactory of = new no.ums.ws.pas.ObjectFactory();
			no.ums.ws.pas.ULOGONINFO logon = of.createULOGONINFO();
			//WSFillLogoninfo.fill(logon, PAS.get_pas().get_userinfo());
			no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
			logon.setLComppk(ui.get_comppk());
			logon.setLDeptpk(ui.get_current_department().get_deptpk());
			logon.setLUserpk(Long.parseLong(ui.get_userpk()));
			logon.setSzCompid(ui.get_compid());
			logon.setSzDeptid(ui.get_current_department().get_deptid());
			logon.setSzUserid(ui.get_userid());
			logon.setSzPassword(ui.get_passwd());
			logon.setSessionid(ui.get_sessionid());
			java.net.URL wsdl;
			try
			{			
				wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
				//wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL"); 
				QName service = new QName("http://ums.no/ws/pas/", "pasws");
				maxalloc_return = new Pasws(wsdl, service).getPaswsSoap12().setMaxAlloc(logon, maxalloc_request);
				
			} catch(Exception e)
			{
				maxalloc_return = new UMAXALLOC();
				maxalloc_return.setNProjectpk(maxalloc_request.getNProjectpk());
				maxalloc_return.setNRefno(maxalloc_request.getNRefno());
				maxalloc_return.setNMaxalloc(-1);
				//return ;
				throw e;
			}
			//onDownloadFinished();
			
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	protected String getErrorMessage() {
		return "Failed to set max alloc";
	}

	@Override
	public void onDownloadFinished()
	{
		if(m_callback!=null)
			m_callback.actionPerformed(new ActionEvent(maxalloc_return, ActionEvent.ACTION_PERFORMED, m_action));
	}
	
}