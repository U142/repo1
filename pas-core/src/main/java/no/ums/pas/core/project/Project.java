package no.ums.pas.core.project;

import no.ums.pas.status.StatusListObject;

import java.util.ArrayList;

public class Project extends Object {
	
	private String m_sz_projectpk = "-1";
	private String m_sz_projectname;
	private String m_sz_createtimestamp;
	private String m_sz_updatetimestamp;
	private int m_n_createdate = 0;
	private int m_n_createtime = 0;
	private int m_n_updatedate = 0;
	private int m_n_updatetime = 0;
	private boolean m_b_issaved = false;
	private ArrayList<StatusListObject> m_status_sendings = new ArrayList<StatusListObject>();
	public ArrayList<StatusListObject> get_status_sendings() { return m_status_sendings; }
	public int get_num_sendings() { 
		ArrayList<StatusListObject> temp = new ArrayList<StatusListObject>();
		for(int i=0;i<m_status_sendings.size();++i) {
			boolean exists = false;
			for(int j=0;j<temp.size();j++) {
				if(m_status_sendings.get(i).get_refno() == temp.get(j).get_refno())
					exists = true;
			}
			if(!exists) {
				temp.add(m_status_sendings.get(i));
			}
		}
		return temp.size(); 
	}
	public boolean equals(Object o) {
		try {
			if(this.get_projectpk().equals(((Project)o).get_projectpk()))
				return true;
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public boolean add_status_sending(StatusListObject obj) { 
		//if(!m_status_sendings.contains(obj)) {
			m_status_sendings.add(obj); 
		//	return true;
		//}
		return true;
	}
	

	
	public void set_projectpk(String sz) { m_sz_projectpk = sz; }
	public void set_projectname(String sz) { m_sz_projectname = sz; }
	public void set_createtimestamp(String sz) {
		m_sz_createtimestamp = sz;
		m_n_createdate = datepart(sz);
		m_n_createtime = timepart(sz);
	}
	public void set_updatetimestamp(String sz) {
		m_sz_updatetimestamp = sz;
		m_n_updatedate = datepart(sz);
		m_n_updatetime = timepart(sz);
	}
	private int datepart(String ts) {
		if(ts.length()!=14)
			return -1;
		return new Integer(ts.substring(0, 8)).intValue();
	}
	private int timepart(String ts) {
		if(ts.length()!=14)
			return -1;
		return new Integer(ts.substring(8, 14)).intValue();
	}
	public String toString() { return get_projectpk(); }
	
	public String get_projectpk() { return m_sz_projectpk; }
	public String get_projectname() { return m_sz_projectname; }
	public String get_createtimestamp() { return m_sz_createtimestamp; }
	public String get_updatetimestamp() { return m_sz_updatetimestamp; }
	public int get_createdate() { return m_n_createdate; }
	public int get_createtime() { return m_n_createtime; }
	public int get_updatedate() { return m_n_updatedate; }
	public int get_updatetime() { return m_n_updatetime; }
	public boolean is_saved() { return m_b_issaved; }
	public void set_saved() { m_b_issaved = true; }
	
	public Project() {
		super();
	}
	
}