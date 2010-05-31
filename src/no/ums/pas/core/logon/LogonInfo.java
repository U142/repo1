package no.ums.pas.core.logon;



/*struct class containing username, compid and password*/
public class LogonInfo {
	private String m_sz_userid;
	private String m_sz_compid;
	private String m_sz_passwd;
	private String m_sz_language;
	public String get_userid() { return m_sz_userid; }
	public String get_compid() { return m_sz_compid; }
	public String get_passwd() { return m_sz_passwd; }
	public String get_language() { return m_sz_language; }
	
	public LogonInfo(String sz_userid, String sz_compid, String sz_passwd, String sz_language) {
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