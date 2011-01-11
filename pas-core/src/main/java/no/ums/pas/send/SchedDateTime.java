package no.ums.pas.send;

public class SchedDateTime extends Object {
	private int m_n_scheddate = -1; //now + days in to the future
	private int m_n_schedtime = -1;
	private int m_n_hour = 0;
	private int m_n_minute = 0;
	public int get_scheddate() { return m_n_scheddate; }
	public int get_schedtime() { return m_n_schedtime; }
	public void set_date(int n_date) { m_n_scheddate = n_date; }
	//public void set_time(int n_time) { m_n_schedtime = n_time; }
	public void set_hour(int n_hour) { 
		m_n_hour = n_hour;
		create_time();
	}
	public void set_minute(int n_minute) {
		m_n_minute = n_minute;
		create_time();
	}
	private void create_time() {
		String sz_time;
		if(m_n_hour < 10)
			sz_time = "0" + m_n_hour;
		else
			sz_time = "" + m_n_hour;
		if(m_n_minute < 10)
			sz_time += "0" + m_n_minute;
		else
			sz_time += "" + m_n_minute;
		m_n_schedtime = new Integer(sz_time).intValue();
	}
	public SchedDateTime() {
		super();
	}
}
