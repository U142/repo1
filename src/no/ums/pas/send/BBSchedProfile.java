package no.ums.pas.send;

//"l_reschedpk", "sz_name", "l_deptpk", "sz_deptid", "l_retries", "l_interval", "l_canceltime", 
//"l_canceldate", "l_pausetime", "l_pauseinterval", "sharing"
public class BBSchedProfile extends Object {
	private String m_sz_reschedpk;
	private String m_sz_name;
	private int m_n_deptpk;
	private String m_sz_deptid;
	private int m_n_retries = -1;
	private int m_n_interval = -1;
	private int m_n_canceldate = -1;
	private int m_n_canceltime = -1;
	private int m_n_pausetime = -1;
	private int m_n_pauseinterval = -1;
	private boolean m_b_sharing;
	public String get_reschedpk() { return m_sz_reschedpk; }
	public String get_name() { return m_sz_name; }
	public int get_retries() { return m_n_retries; }
	public int get_canceldate() { return m_n_canceldate; }
	public int get_canceltime() { return m_n_canceltime; }
	public int get_pausetime() { return m_n_pausetime; }
	public int get_pauseinterval() { return m_n_pauseinterval; }
	public int get_interval() { return m_n_interval; }
	public String toString() { return get_name(); }
	public BBSchedProfile(String sz_reschedpk, String sz_name, int n_deptpk, String sz_deptid, int n_retries, int n_interval, 
				   int n_canceltime, int n_canceldate, int n_pausetime, int n_pauseinterval, boolean b_sharing) {
		m_sz_reschedpk	= sz_reschedpk;
		m_sz_name		= sz_name;
		m_n_deptpk		= n_deptpk;
		m_sz_deptid		= sz_deptid;
		m_n_retries		= n_retries;
		m_n_interval	= n_interval;
		m_n_canceltime	= n_canceltime;
		m_n_canceldate	= n_canceldate;
		m_n_pausetime	= n_pausetime;
		m_n_pauseinterval= n_pauseinterval;
		m_b_sharing		= b_sharing;
	}
	public BBSchedProfile(String [] vals) {
		this(vals[0], vals[1], new Integer(vals[2]).intValue(), vals[3], new Integer(vals[4]).intValue(), 
			 new Integer(vals[5]).intValue(), new Integer(vals[6]).intValue(), new Integer(vals[7]).intValue(),
			 new Integer(vals[8]).intValue(), new Integer(vals[9]).intValue(), (new Integer(vals[10]).intValue()==1 ? true : false));
	}
}