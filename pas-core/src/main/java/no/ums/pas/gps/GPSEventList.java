package no.ums.pas.gps;

import java.util.ArrayList;

public class GPSEventList extends ArrayList<GPSEvent> {
	public static final long serialVersionUID = 1;
	
	private String m_sz_eventpk_filter = "0";
	public String get_eventpk_filter() { return m_sz_eventpk_filter; }
	public void set_eventpk_filter(String s) { m_sz_eventpk_filter = s; }
	
	public GPSEventList() {
		super();
	}
	/*public void add(GPSEvent obj) {
		add(obj);
	}*/
	public GPSEvent add(String [] sz) {
		return add(sz[0], sz[1], sz[2], sz[3], sz[4], sz[5], sz[6], sz[7],
				sz[8], sz[9], sz[10], sz[11], sz[12], sz[13], sz[14]);
	}
	public GPSEvent add(String sz_eventpk, String sz_objectpk, String sz_cmd, String sz_dir,
			String sz_param1, String sz_param2, String sz_sparam1, String sz_sparam2,
			String sz_answered, String sz_pri, String sz_date, String sz_time,
			String sz_updatedate, String sz_updatetime, String sz_msgpk) {
		GPSEvent event = null;
		try {
			event = new GPSEvent(sz_eventpk, sz_objectpk, sz_cmd, sz_dir,
								sz_param1, sz_param2, sz_sparam1, sz_sparam2, sz_answered,
								sz_pri, sz_date, sz_time, sz_updatedate, sz_updatetime,
								sz_msgpk);
			System.out.println("new GPSevent added");
			super.add(event);
			set_eventpk_filter(event.get_eventpk());
			return event;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return event;
	}
}