package no.ums.pas.core.webdata;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.dataexchange.HttpPostForm;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.InputStream;


public abstract class XMLThread extends Thread
{
	boolean m_b_zipped = false;
	boolean m_b_issearching;
	private LoadingFrame m_loadingframe = null;
	private PAS m_pas;
	private String m_sz_loadtext;
	protected ActionListener m_callbackclass;
	protected ActionListener get_callback() { return m_callbackclass; }
	protected String m_sz_callback_action;
	public ActionListener get_callback_class() { return m_callbackclass; }
	public String get_callback_action() { return m_sz_callback_action; }
	protected void set_callbackclass(ActionListener a) { m_callbackclass = a; }
	protected void set_callbackaction(String s) { m_sz_callback_action = s; }
	
	public PAS get_pas() { return m_pas; }
	public HTTPReq get_http() {
		if(m_http_req==null)
			return new HTTPReq(PAS.get_pas().get_sitename());
		else
			return m_http_req;
	}//return get_pas().get_httpreq(); }
	public String get_url() { return m_sz_url; }
	private String m_sz_url;
	private String m_file_path = null;
	public void setXmlPath(String xmlpath){ m_file_path = xmlpath; }
	JFrame m_parent_frame;
	LoadingPanel m_loadingpanel = null;
	public LoadingPanel get_loadingpanel() { return m_loadingpanel; }
	public String get_loadtext() { return m_sz_loadtext; }
	private HTTPReq m_http_req;
	private InputStream m_is = null;
	protected HttpPostForm m_form = null;
	private int m_n_httpcode = 200;
	public int get_httpcode() { return m_n_httpcode; }
	private boolean m_b_failed = false;
	public boolean getFailed() { return m_b_failed; }
	
	LoadingFrame get_loadingframe() { return m_loadingframe; }
	JFrame get_parentframe() { return m_parent_frame; }
	protected void onDownloadFinishedFailed() {
		Error.getError().addError("HTTP Error", "HTTP error detected, check your network connection", 2, 1);
		m_b_failed = true;
		parseDoc(null);
		onDownloadFinished();
	}
	
	public XMLThread(int n_pri, String sz_url, HTTPReq http_req) {
		this(n_pri, PAS.get_pas(), sz_url, null, null, "", false, http_req);
	}
	
	public XMLThread(int n_pri, PAS pas, String sz_url, JFrame parent_frame, 
					LoadingPanel loader, String sz_loadtext, boolean b_zipped, HTTPReq http_req)
	{
		super("XMLThread");
		m_pas = pas;
		m_http_req = http_req;
		m_loadingpanel = loader;
		m_parent_frame = parent_frame;
		m_sz_url = sz_url;
		m_sz_loadtext = sz_loadtext;
		this.setPriority(n_pri);
		if(get_parentframe()!=null)
			m_loadingframe = new LoadingFrame(get_loadtext(), PAS.get_pas().get_mappane());//, parent_frame);
		m_b_zipped = b_zipped;
	}
	public XMLThread(HttpPostForm is, int n_pri, PAS pas, JFrame parent_frame, 
					LoadingPanel loader, String sz_loadtext, boolean b_zipped, HTTPReq http_req) {
		this(n_pri, pas, "", parent_frame, loader, sz_loadtext, b_zipped, http_req);
		//m_is = is;
		m_form = is;
	}
	public void set_httpreq(HTTPReq http_req) {
		m_http_req = http_req;
	}
	private void started() { m_b_issearching = true; }
	private void stopped() 
	{ 
		m_b_issearching = false; 
	}
	public boolean get_issearching() { return m_b_issearching; }
	
	public void run()
	{
		started();
		if(m_form!=null) {
			try {
				if(PAS.get_pas().get_userinfo()!=null) {
					m_form.setParameter("xuid", PAS.get_pas().get_userinfo().get_userid());
					m_form.setParameter("xcid", PAS.get_pas().get_userinfo().get_compid());
					m_form.setParameter("xpwd", PAS.get_pas().get_userinfo().get_passwd());
					m_form.setParameter("xdep", new Integer(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()));
					m_form.setParameter("xupk", PAS.get_pas().get_userinfo().get_userpk());
					m_form.setParameter("xcpk", new Integer(PAS.get_pas().get_userinfo().get_comppk()).toString());
				}
				m_is = m_form.post();
				//System.out.println(new BufferedReader(new InputStreamReader(m_is)).readLine());
			} catch(Exception e) {
				m_n_httpcode = 500;
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("XMLThread","Exception in run",e,1);
				onDownloadFinishedFailed();
				return;
			}
		} else {
			try {
				if(PAS.get_pas().get_userinfo()!=null) {
					if(m_sz_url.charAt(m_sz_url.length()-1)!='?')
						m_sz_url += "&";
					m_sz_url += "xuid=" + PAS.get_pas().get_userinfo().get_userid();
					m_sz_url += "&xcid=" + PAS.get_pas().get_userinfo().get_compid();
					m_sz_url += "&xpwd=" + PAS.get_pas().get_userinfo().get_passwd();
					m_sz_url += "&xdep=" + PAS.get_pas().get_userinfo().get_current_department().get_deptpk();
					m_sz_url += "&xupk=" + PAS.get_pas().get_userinfo().get_userpk();
					m_sz_url += "&xcpk=" + PAS.get_pas().get_userinfo().get_comppk();
				}
			} catch(Exception e) {
				m_n_httpcode = 500;
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("XMLThread","Exception in run",e,1);
				onDownloadFinishedFailed();
				return;
			}
		}
		
		if(get_parentframe()!=null)
			m_loadingframe.start_and_show();
		//MsgBox message = new  MsgBox(new java.awt.Frame(""), sz_url, true);
		Document doc;
		doc = null;
		//doc = new parm.xml.XmlReader().getXmlDocument(m_file_path);
		
		if(m_is==null) {
			String sz_url = get_pas().get_sitename() + get_url(); //+ "?l_refno=" + m_n_refno;//"PAS_getstatusitems.asp?l_refno=" + m_n_refno;
			try {
				doc = get_http().get_xml(sz_url, m_b_zipped, get_loadingpanel());
			} catch(Exception e) {
				m_n_httpcode = 500;
				onDownloadFinishedFailed();
				return;
			}
			if(get_http().session_reconnected())
				System.out.println("Session ended, you were automatically reconnected");
				//javax.swing.JOptionPane.showMessageDialog(null, "Session ended, you were automatically reconnected");
		}
		else {
			try {
				doc = get_http().get_xml(m_is, m_b_zipped, get_loadingpanel());
			} catch(Exception e) {
				m_n_httpcode = 500;
				onDownloadFinishedFailed();
				return;
			}
			if(get_http().session_reconnected())
				System.out.println("Session ended, you were automatically reconnected");				
				//javax.swing.JOptionPane.showMessageDialog(null, "Session ended, you were automatically reconnected");
		}
		//if(m_file_path != null)
		//	new XmlWriter().writeXMLFile(doc, m_file_path);
		
		if(doc==null)
		{
			m_n_httpcode = 500;
			onDownloadFinishedFailed();
			return;
			//parseDoc(doc);
			//error
		}
		else {
			try {
				PAS.get_pas().get_drawthread().set_suspended(true);
			} catch(Exception e) {
				Error.getError().addError("XMLThread","Exception in run",e,1);
			}
			try {
				if(get_loadingpanel()!=null)
					get_loadingpanel().lock_overallstage();
				parseDoc(doc);
			} catch(Exception e1) {
				Error.getError().addError("XMLThread","Exception in run",e1,1);
				try {
					PAS.get_pas().get_drawthread().set_suspended(false);
				} catch(Exception e2) {
					System.out.println(e2.getMessage());
					e2.printStackTrace();
					Error.getError().addError("XMLThread","Exception in run",e2,1);
				}
				//PAS.get_pas().add_event("", e);
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
		}
		if(get_parentframe()!=null)
			if(m_loadingframe!=null)
				m_loadingframe.stop_and_hide();
		if(get_loadingpanel()!=null)
			get_loadingpanel().reset_progress();
		stopped();
		try {
			PAS.get_pas().get_drawthread().set_suspended(false);
		} catch(Exception e) {
			Error.getError().addError("XMLThread","Exception in run",e,1);
		}
	}
	abstract public void parseDoc(Document doc);
	abstract public void onDownloadFinished();
}