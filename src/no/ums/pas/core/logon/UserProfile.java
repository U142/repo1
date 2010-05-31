package no.ums.pas.core.logon;

public class UserProfile {
	
	private String m_sz_name;
	private String m_sz_description;
	private int m_n_fleetcontrol;
	private int m_n_parm;
	private int m_n_cellbroadcast;
	private int m_n_status;
	private int m_n_send;
	private int m_n_weather;
	private RightsManagement m_right_management;
	private int m_n_houseeditor;
	private long m_n_addresstypes;
	
	public String get_name() { return m_sz_name; }
	public String get_description() { return m_sz_description; }
	public int get_fleetcontrol_rights() { return m_n_fleetcontrol; }
	public int get_parm_rights() { return m_n_parm; }
	public int get_status() { return m_n_status; }
	public int get_send() { return m_n_send; }
	public int get_houseeditor() { return m_n_houseeditor; }
	public long get_addresstypes() { return m_n_addresstypes; }
	
	public int get_cellbroadcast_rights() { return m_n_cellbroadcast; }
	public int get_weather() { return m_n_weather; }
	public RightsManagement get_rights_management() { return m_right_management; }
	
	public UserProfile(String name, String description, int fleetcontrol, int parm, int status, int send, int cellbroadcast, int houseeditor, long addresstypes) {
		m_sz_name = name;
		m_sz_description = description;
		m_n_fleetcontrol = fleetcontrol;
		m_n_parm = parm;
		m_n_status = status;
		m_n_send = send;
		m_n_cellbroadcast = cellbroadcast;
		m_n_houseeditor = houseeditor;
		m_n_addresstypes = addresstypes;
		m_right_management = new RightsManagement(this);
		m_n_weather = 1; // TODO fiks denne
		
	}
	
	/*public UserProfile() {
		m_sz_name = "Rolseprofil";
		m_sz_description = "Dette er bare en testprofil";
		m_n_fleetcontrol = 1;
		m_n_parm = 1;
		m_n_status = 1;
		m_n_send = 2;
		m_n_cellbroadcast = 1;
		m_right_management = new RightsManagement(this);
		m_n_weather = 1;
	}*/

}
