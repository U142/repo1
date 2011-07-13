package no.ums.pas.ums.tools;

import no.ums.pas.localization.Localization;

// Is used for expiry time on lbas and sms
public class ExpiryMins {
	private String m_s;
	public ExpiryMins(String s) {
		m_s = s;
	}
	public String get_minutes() { return m_s; }
	public String toString() {
		if(m_s.length() > 0) {
			int minutes = Integer.valueOf(m_s);
			if(minutes >= 1440) {
                return (minutes/60)/24 + " " + Localization.l("common_days_maybe");//" day(s)";
            }
			else if(minutes >= 60) {
                return minutes/60 + " " + Localization.l("common_hours_maybe");
            }
			else {
                return m_s + " " + Localization.l("common_minutes_maybe");
            }
		}
		else
			return "";
			
	}
}