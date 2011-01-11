package no.ums.pas.ums.tools;

public class Timeout {
	int m_n_msec = 0;
	int m_n_max_msec;
	int m_n_msec_interval;
	boolean b_force_exit = false;
	public int get_msec_interval() { return m_n_msec_interval; }
	public double get_wait_secondsD() { return m_n_max_msec/1000; }
	public int get_wait_secondsI() { return m_n_max_msec/1000; }
	public int get_waited() { return m_n_msec; }
	public int get_percent() { return (int)((((double)m_n_msec)/((double)(m_n_max_msec))) * 100); }
	public int get_remaining_seconds() { return (m_n_max_msec - m_n_msec) / 1000; }
	public void ForceExit() { b_force_exit = true; }
	public Timeout(int n_maxseconds, int n_msec_interval) {
		m_n_max_msec = n_maxseconds * 1000;
		m_n_msec_interval = n_msec_interval;
	}
	public void inc_timer() {
		m_n_msec += m_n_msec_interval;
	}
	public boolean timer_exceeded() { 
		return ((m_n_msec > m_n_max_msec) || b_force_exit ? true : false); 
	}
}