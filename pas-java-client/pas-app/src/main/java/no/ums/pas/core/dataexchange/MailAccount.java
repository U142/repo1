package no.ums.pas.core.dataexchange;

//import com.roxes.win32.*;

public class MailAccount {
	private String m_sz_account;
	private String m_sz_mailserver = "";
	private String m_sz_displayname;
	private String m_sz_mailaddress = "";
	private String m_sz_helo;
	private int m_n_port = 25;
	private boolean m_b_autodetected = false;
	
	public String get_accountname() { return m_sz_account; }
	public String get_mailserver() { return m_sz_mailserver; }
	public String get_displayname() { return m_sz_displayname; }
	public String get_mailaddress() { return m_sz_mailaddress; } 
	public boolean get_autodetected() { return m_b_autodetected; }
	public String get_helo() { return m_sz_helo; }
	public int get_port() { return m_n_port; } 
	
	public void set_accountname(String s) { m_sz_account = s; }
	public void set_mailserver(String s) { m_sz_mailserver = s; }
	public void set_displayname(String s) { m_sz_displayname = s; }
	private void set_helo(String s) { m_sz_helo = s; }
	public void set_port(int i) { m_n_port = i; }
	public void set_autodetected(boolean b) { m_b_autodetected = b; }
	public void set_mailaddress(String s) { 
		m_sz_mailaddress = s;
		int n_found = get_mailaddress().indexOf("@");
		if(n_found > 0){
			set_helo(get_mailaddress().substring(n_found+1));
		}
	
	}
	
	public MailAccount() {
		//m_b_autodetected = autodetect();
	}
	/*protected boolean autodetect() {
		try {
			Registry reg = new Registry(Registry.HKEY_CURRENT_USER, "software\\microsoft\\internet account manager");
			if(reg.exists()) {
				set_accountname((String)reg.getValue("default mail account"));
				//log.debug("MailAccount = " + m_sz_account);
				try {
					reg = new Registry(Registry.HKEY_CURRENT_USER, "software\\microsoft\\internet account manager\\accounts\\" + m_sz_account);
					if(reg.exists()) {
						set_mailserver((String)reg.getValue("SMTP Server"));
						set_mailaddress((String)reg.getValue("SMTP Email Address"));
						set_displayname((String)reg.getValue("SMTP Display Name"));
						log.debug(get_accountname());
						log.debug(get_mailserver());
						log.debug(get_displayname());
						log.debug(get_helo());
						log.debug(get_mailaddress());

						return true;
					}
				} catch(Exception e) {
					log.debug(e.getMessage());
				}
			}
		}
		catch(Exception e) {
			log.debug(e.getMessage());
		}

		return false;
	}*/
}