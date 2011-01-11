package no.ums.pas.ums.tools.calendarutils;

public class DateTime {
	int m_n_date = 0;
	int m_n_time = 0;
	public int get_date() { return m_n_date; }
	public int get_time() { return m_n_time; }
	public DateTime(int n_date, int n_time) {
		m_n_date = n_date;
		m_n_time = n_time;
	}
}