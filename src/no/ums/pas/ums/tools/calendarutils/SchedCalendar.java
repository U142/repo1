package no.ums.pas.ums.tools.calendarutils;

import java.util.Calendar;

/*
 * Find dates of the next n_days from now
 */
public class SchedCalendar extends Object {
	public class Day {
		private int m_n_date;
		public int get_date() { return m_n_date; }
		public Day(int n_date) {
			m_n_date = n_date;
		}
		public Day(int n_year, int n_month, int n_day) {
			String sz_date = "";
			sz_date += n_year;
			if(n_month<10)
				sz_date += "0";
			sz_date += n_month;
			if(n_day<10)
				sz_date += "0";
			sz_date += n_day;
			m_n_date = new Integer(sz_date).intValue();
		}
		public String toString() { 
			String sz_ret = new Integer(m_n_date).toString();
			//return sz_ret;
			return sz_ret.substring(6,8) + "." + sz_ret.substring(4,6) + "." + sz_ret.substring(0, 4);
		}
	}
	
	private Calendar m_calendar;
	private Day m_day [];
	private String m_hour [];
	private String m_minute [];
	
	public Day[] get_days() { return m_day; }
	public String [] get_hours() { return m_hour; }
	public String [] get_minutes() { return m_minute; }
	public SchedCalendar(int n_days) {
		m_calendar = Calendar.getInstance();
		m_day = new Day[n_days];
		int n_year, n_month, n_day;
		for(int i=0; i < m_day.length; i++) {
			n_year = m_calendar.get(Calendar.YEAR);
			n_month = m_calendar.get(Calendar.MONTH) + 1;
			n_day = m_calendar.get(Calendar.DAY_OF_MONTH);
			//m_day[i] = new Day(new Integer(new String(new Integer(n_year).toString()) + new String(new Integer(n_month).toString()) + new String(new Integer(n_day).toString()).toString()).intValue());
			m_day[i] = new Day(n_year, n_month, n_day);
			m_calendar.add(Calendar.DATE, 1);
		}
		m_hour = new String [] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" };
		m_minute= new String [] { "00", "10", "20", "30", "40", "50" };
	}
}