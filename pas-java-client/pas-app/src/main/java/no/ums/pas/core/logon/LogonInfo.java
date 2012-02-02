package no.ums.pas.core.logon;



/*struct class containing username, compid and password*/
public class LogonInfo {
	private String m_sz_userid;
	private String m_sz_compid;
	private String m_sz_passwd;
	private String m_sz_sessionid;
	private String m_sz_language;
	public boolean isAutoLogonReady()
	{
		return (m_sz_userid!=null && m_sz_userid.length()>0 &&
			m_sz_compid!=null && m_sz_compid.length()>0 &&
			m_sz_passwd!=null && m_sz_passwd.length()>0);
	}
	public String get_userid() { return m_sz_userid; }
	public String get_compid() { return m_sz_compid; }
	public String get_passwd() { return m_sz_passwd; }
	public String get_sessionid() { return m_sz_sessionid; }
	public String get_language() { return m_sz_language; }
	public void set_userid(String s) { m_sz_userid = s; }
	public void set_compid(String s) { m_sz_compid = s; }
	public void set_language(String s) { m_sz_language = s; }
	
	public LogonInfo(String sz_userid, String sz_compid, 
			String sz_passwd, String sz_language) {
		m_sz_userid = sz_userid;
		m_sz_compid = sz_compid;
		m_sz_passwd = sz_passwd;
		m_sz_language = sz_language;
	}
	public LogonInfo(String sz_userid, String sz_compid) {
		m_sz_userid = sz_userid;
		m_sz_compid = sz_compid;
	}
}