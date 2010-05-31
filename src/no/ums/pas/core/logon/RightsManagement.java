package no.ums.pas.core.logon;

public class RightsManagement {
	
	private UserProfile m_userprofile;
	
	public RightsManagement(UserProfile userprofile) {
		m_userprofile = userprofile;
	}
	
	public boolean read_parm() {
		if(m_userprofile.get_parm_rights() >= 1)
			return true;
		else
			return false;
	}
	
	public boolean write_parm() {
		if(m_userprofile.get_parm_rights() >= 2)
			return true;
		else
			return false;
	}
	
	public boolean delete_parm() {
		if(m_userprofile.get_parm_rights() >= 3)
			return true;
		else
			return false;
	}
	
	public boolean read_fleetcontrol() {
		if(m_userprofile.get_fleetcontrol_rights() >= 1)
			return true;
		else
			return false;
	}
	
	public boolean write_fleetcontrol() {
		if(m_userprofile.get_fleetcontrol_rights() >= 2)
			return true;
		else
			return false;
	}
	
	public boolean delete_fleetcontrol() {
		if(m_userprofile.get_fleetcontrol_rights() >= 3)
			return true;
		else
			return false;
	}

	public boolean cansend() {
		//if(m_userprofile.get_send() >= 2)
		if((m_userprofile.get_send() & 2) ==2)
			return true;
		else
			return false;
	}
	public boolean cansimulate() {
		if((m_userprofile.get_send() & 1) == 1)
			return true;
		else
			return false;
	}
	

	public boolean status() {
		if(m_userprofile.get_status() >= 1)
			return true;
		else
			return false;
	}
	
	public boolean weather() {
		if(m_userprofile.get_weather() == 1)
			return true;
		else
			return false;
	}
	
	public long addresstypes() { 
		return m_userprofile.get_addresstypes();
	}
	
	public int houseeditor() {
		return m_userprofile.get_houseeditor();
	}
	
	public boolean cell_broadcast() {
//		if(m_userprofile.get_cellbroadcast_rights() >= 1)
//			return true;
//		else
//			return false;
		return true;
	}
}
