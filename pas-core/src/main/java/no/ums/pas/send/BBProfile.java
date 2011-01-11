package no.ums.pas.send;

import no.ums.pas.sound.SoundFile;

public class BBProfile extends Object {
	private int m_sz_profilepk;
	private String m_sz_name;
	private int m_n_deptpk;
	private String m_sz_reschedpk = "-1";
	private boolean m_b_sharing;
	private SoundFileArray m_soundfiles = null;
	public int get_profilepk() { return m_sz_profilepk; }
	public String get_profilename() { return m_sz_name; }
	public int get_deptpk() { return m_n_deptpk; }
	public String get_reschedpk() { return m_sz_reschedpk; }
	public boolean isShared() { return m_b_sharing; }
	public SoundFileArray get_soundfiles() { return m_soundfiles; }
	public String toString() { return get_profilename() + " (" + get_soundfiles().size() + ")"; }
	public BBProfile(int sz_profilepk, String sz_name, int n_deptpk, String sz_reschedpk, boolean b_sharing) {
		super();
		m_sz_profilepk	= sz_profilepk;
		m_sz_name		= sz_name;
		m_n_deptpk		= n_deptpk;
		if(sz_reschedpk.equals(""))
			m_sz_reschedpk = "-1";
		else
			m_sz_reschedpk	= sz_reschedpk;
		m_b_sharing		= b_sharing;
		m_soundfiles = new SoundFileArray();
	}
	public BBProfile(String [] sz_values) {
		this(new Integer(sz_values[0]).intValue(), sz_values[1], new Integer(sz_values[2]).intValue(), 
			 sz_values[3], (new Integer(sz_values[4]).intValue() == 1 ? true : false));
	}
	public void add_soundfile(SoundFile file) {
		m_soundfiles.add(file);
	}
}
