package no.ums.pas.core.logon;

import no.ums.pas.PAS;
import no.ums.pas.core.ws.WSLogon;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.Timeout;
import no.ums.ws.common.UNSLOOKUP;
import no.ums.ws.common.parm.UDEPARTMENT;
import no.ums.ws.common.parm.UPASLOGON;
import no.ums.ws.common.parm.UPASUISETTINGS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/*executes dialog*/
public class Logon implements ActionListener {
	private PAS m_pas;
	private PAS get_pas() { return m_pas; }
	UserInfo m_info = null;
	LogonInfo m_logoninfo = null;
	LogonInfo m_initinfo = null;
	UPASUISETTINGS m_pasui_settings = null;
	public UserInfo get_userinfo() { return m_info; }
	private void set_last_error(String sz_error) { m_sz_error = sz_error; }
	public String get_last_error() { return m_sz_error; }
	public UPASUISETTINGS get_uisettings() { return m_pasui_settings; }
	String m_sz_error;
	LogonDialog dlg = null;
	boolean m_b_isloggedon = false;
	public boolean isLoggedOn() { return m_b_isloggedon; }
	private void setLoggedOn() { m_b_isloggedon = true; }
	private int m_n_logontries = 1;
	private int m_n_max_tries = 3;
	public void setMaxTries(int n)
	{
		m_n_max_tries = n;
	}
	private boolean m_b_cantryagain = true;
	private boolean m_b_doexit = false;
	private String wantedlanguage = "";
	public LogonInfo getLogonInfo() { return m_logoninfo; }
	private boolean b_request_newsession = false;
	private boolean bAutoLogon = false;
	
	public int getLogonTries() {
		return m_n_logontries;
	}
	public int getMaxLogonTries() {
		return m_n_max_tries;
	}
	
	public boolean canTryAgain() { 
		return m_b_cantryagain;
	}
	public boolean triesExceeded() {
		if(m_n_logontries >= m_n_max_tries)
			return true;
		return false; 		
	}
	private void incLogonTries() {
		m_n_logontries++;
		if(triesExceeded())
			m_b_cantryagain = false;
	}
	
	public Logon(PAS pas, LogonInfo info, String language, boolean b_request_newsession) {
		m_pas = pas;
		m_logoninfo = info;
		m_initinfo = info;
		wantedlanguage = language;
		this.b_request_newsession = b_request_newsession;
		bAutoLogon = info.isAutoLogonReady();
		start();
	}
	
	public synchronized void set_response(boolean b_quit) {
		m_b_doexit = b_quit;
		this.notify();
	}
	

	private synchronized UserInfo start() {

		LogonInfo info = null;
		
		final Logon temp = this;
		if(dlg==null)
		{
			try
			{
				java.awt.EventQueue.invokeLater(new Runnable() {
				public void run()
				{
					dlg = new LogonDialog(temp, get_pas(), true, m_initinfo, wantedlanguage, b_request_newsession);
					PAS.pasplugin.onCustomizeLogonDlg(dlg);
				}
				});
			}
			catch(Exception e)
			{
				
			}
		}

		
		if(!bAutoLogon)
		{
			
			try
			{  
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run()
					{
						dlg.restart();
						dlg.setVisible(true);
					}
				});
			}
			catch(Exception e)
			{
				
			}
			
			try {
				wait();
			} catch(InterruptedException e) {
				
			} catch(Exception e) {
				
			}
			if(m_b_doexit)
				return null;
			if(!dlg.get_logonproc_start()) { //window closed for exit
				return null;
			}
			info = dlg.get_logoninfo();
			m_logoninfo = dlg.get_logoninfo();
			dlg.setLoading(true);
		}
		else
			info = m_logoninfo;
		b_results_ready = false;
		
		WSLogon proc = new WSLogon(this, info.get_userid(), info.get_compid(), info.get_passwd());
		proc.start();
		Timeout timer = new Timeout(60, 50); //10 second timeout, 50 msec wait interval
		//while(!proc.getResponded() && !timer.timer_exceeded())
		while(!b_results_ready && !timer.timer_exceeded())
		{
			try {
				timer.inc_timer();
				Thread.sleep(timer.get_msec_interval());
				//timer.inc_timer();
			} catch(InterruptedException e) { 
			}
		}
		if(!bAutoLogon)
			dlg.setLoading(false);
		bAutoLogon = false;
		
		
		if(timer.timer_exceeded()) {
			set_last_error(Localization.l("error_logon_request_timed_out"));
			if(canTryAgain()) {
				dlg.set_errortext(get_last_error());
				incLogonTries();
				return start();
			}
			else {
				//dlg.componentHidden(null);
				dlg.setVisible(true);
				dlg.set_errortext(get_last_error());
				//JOptionPane.showInternalMessageDialog(dlg, "Logon failed, quitting...", "Error", JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		else if(proc.hasError())
		{
			set_last_error(proc.get_last_error());
			dlg.set_errortext(get_last_error());
			if(canTryAgain())
				return start();
		}
		/*else if(proc.get_httpcode()!=HttpURLConnection.HTTP_OK) {
			set_last_error("Error: HTTP Error. Please check your internet connection and try again");
			dlg.set_errortext(get_last_error());
			if(canTryAgain()) {
				return start();
			}
		}*/
		else if(m_info==null) {
			set_last_error("Error: " + proc.get_last_error());
			switch(proc.getReason())
			{
			case BLOCKED_BY_ADMIN:
				dlg.set_errortext(Localization.l("logon_error_user_blocked"));
				break;
			case NONE:
				dlg.set_errortext(String.format(Localization.l("error_logon_invalid_userinfo_format"), getLogonTries(), getMaxLogonTries())); //"Invalid user-information (try " + getLogonTries() + " / " + getMaxLogonTries() + ")");
				break;
			case REACHED_RETRY_LIMIT:
				dlg.set_errortext(Localization.l("logon_error_user_retry_limit"));
				break;
			}
			m_logoninfo = null;
			if(canTryAgain()) {
				incLogonTries();
				return start();
			}
			else {
				//dlg.componentHidden(null);
				dlg.setVisible(true);
				//JOptionPane.showInternalMessageDialog(dlg, "Logon failed, quitting...", "Error", JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		try {
			Thread.sleep(1000);
			//Thread.sleep(5000);
		} catch(Exception e) { }
		setLoggedOn();
		//update the hashed password. It should now be hash(hashedpassword+onetimekey)
		get_userinfo().set_passwd(proc.getGeneratedPassword());

		//get_userinfo().set_passwd(info.get_passwd());
		//get_userinfo().set_sessionid(info.get_sessionid());
		dlg.setVisible(false);
		//dlg.setTitle(get_last_error());
		return m_info;
	}
	
	boolean b_results_ready = false;
	
	public void actionPerformed(ActionEvent e)
	{
		if("act_logon".equals(e.getActionCommand()))
		{
			UPASLOGON l = (UPASLOGON)e.getSource();
			if(!l.isFGranted())
			{
				//m_info = new UserInfo("0", 0, l.getSzUserid(),l.getSzCompid(),"", "");
				m_info = null;
				b_results_ready = true;
				
				return;
			}
			
			m_info = new UserInfo(Long.toString(l.getLUserpk()), l.getLComppk(), l.getSzUserid(),
					l.getSzCompid(), l.getSzName(), l.getSzSurname(), l.getSessionid(), l.getSzOrganization(),
					l.getDefaultOadc().getLType(), l.getDefaultOadc().getSzValue());
			m_info.set_session_active(true);
			m_pasui_settings = l.getUisettings();
			if(!b_request_newsession)
			{
				List<UDEPARTMENT> depts = l.getDepartments().getUDEPARTMENT();
				for(int i=0; i < depts.size(); i++)
				{
					UDEPARTMENT d = depts.get(i);
					m_info.add_department(d.getLDeptpk(), d.getSzDeptid(), d.getSzStdcc(), d.getLbo(), d.getRbo(), 
							d.getUbo(), d.getBbo(), d.isFDefault(), d.getLDeptpri(), d.getLMaxalloc(), 
							d.getSzUserprofilename(), d.getSzUserprofiledesc(), d.getLStatus(), 
							d.getLNewsending(), d.getLParm(), d.getLFleetcontrol(), d.getLLba(), 
							d.getLHouseeditor(), d.getLAddresstypes(), d.getSzDefaultnumber(), 
							d.getMunicipals().getUMunicipalDef(), d.getLPas(), d.getLLangpk(), 
							d.getFSms(), d.getRestrictionShapes(),
							d.getLMessagelib(),
							d.getLPhonebook(),
							d.getLLists(),
							d.getLModules(),
							d.getLProfiles(),
							d.getLPhonebookShare(),
							d.getLListsShare(),
							d.getLModulesShare(),
							d.getLProfilesShare(),
							d.getLPbName(),
							d.getLPbPhone(),
							d.getLPbPin(),
							d.getLPbLocality(),
							d.getLLocality(),
							d.getLSched(),
							d.getLSchedRetry(),
							d.getLSchedCancel(),
							d.getLSchedPause(),
							d.getLSchedShare(),
							d.getLMessagelibShare(),
							d.getFSuperuser(),
							d.getLResource(),
							d.getLEmail());
							
				}
				//m_info.get_departments().CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
				m_info.get_departments().CreateCombinedRestrictionShape();
				List<UNSLOOKUP> ns = l.getNslookups().getUNSLOOKUP();
				for(int i=0; i < ns.size(); i++)
				{
					UNSLOOKUP n = ns.get(i);
					UserInfo.NSLookup temp = m_info.new NSLookup(n.getSzDomain(),n.getSzIp(),n.getLLastdatetime(),
																n.getSzLocation(),n.isFSuccess());
					PAS.get_pas().add_event("NS Added: " + temp.get_domain(), null);
				}
				dlg.fillNSInfo();
			}
			b_results_ready = true;
		}
	}
	

	

}

