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
	private int m_n_sms;
	private int m_n_email;

	private int messagelib;
	private int phonebook;
	private int lists;
	private int modules;
	private int profiles;
	private int phonebookShare;
	private int listsShare;
	private int modulesShare;
	private int profilesShare;
	private int phonebookName;
	private int phonebookPhone;
	private int phonebookPin;
	private int phonebookLocality;
	private int locality;
	private int sched;
	private int schedRetry;
	private int schedCancel;
	private int schedPause;
	private int schedShare;
	private int messagelibShare;
	private int superuser;
	private int resource;
	
	public String get_name() { return m_sz_name; }
	public String get_description() { return m_sz_description; }
	public int get_fleetcontrol_rights() { return m_n_fleetcontrol; }
	public int get_parm_rights() { return m_n_parm; }
	public int get_status() { return m_n_status; }
	public int get_send() { return m_n_send; }
	public int get_houseeditor() { return m_n_houseeditor; }
	public long get_addresstypes() { return m_n_addresstypes; }
	
	public int get_sms() { return m_n_sms; }
	public int get_email() { return m_n_email; }
	
	public int get_cellbroadcast_rights() { return m_n_cellbroadcast; }
	public int get_weather() { return m_n_weather; }
	public RightsManagement get_rights_management() { return m_right_management; }
	
	public UserProfile(String name, String description, int fleetcontrol, 
					int parm, int status, int send, int cellbroadcast, 
					int houseeditor, long addresstypes, int sms,
					 int l_messagelib, int l_phonebook, int l_lists, int l_modules, int l_profiles,
					 int l_phonebook_share, int l_lists_share, int l_modules_share, int l_profiles_share,
					 int l_pb_name, int l_pb_phone, int l_pb_pin, int l_pb_locality, int l_locality,
					 int l_sched, int l_sched_retry, int l_sched_cancel, int l_sched_pause,
					 int l_sched_share, int l_messagelib_share, int l_superuser, int l_resource,
					 int l_email) {
		m_sz_name = name;
		m_sz_description = description;
		m_n_fleetcontrol = fleetcontrol;
		m_n_parm = parm;
		m_n_status = status;
		m_n_send = send;
		m_n_cellbroadcast = cellbroadcast;
		m_n_houseeditor = houseeditor;
		m_n_addresstypes = addresstypes;
		m_n_sms = sms;
		m_n_email = l_email;
		m_right_management = new RightsManagement(this);
		m_n_weather = 0; // TODO fiks denne
		
		setMessagelib(l_messagelib);
		setPhonebook(l_phonebook);
		setLists(l_lists);
		setModules(l_modules);
		setProfiles(l_profiles);
		setPhonebookShare(l_phonebook_share);
		setListsShare(l_lists_share);
		setModulesShare(l_modules_share);
		setProfilesShare(l_profiles_share);
		setPhonebookName(l_pb_name);
		setPhonebookPhone(l_pb_phone);
		setPhonebookPin(l_pb_pin);
		setPhonebookLocality(l_pb_locality);
		setLocality(l_locality);
		setSched(l_sched);
		setSchedRetry(l_sched_retry);
		setSchedCancel(l_sched_cancel);
		setSchedPause(l_sched_pause);
		setSchedShare(l_sched_share);
		setMessagelibShare(l_messagelib_share);
		setSuperuser(l_superuser);
		setResource(l_resource);
	}
	public int getMessagelib() {
		return messagelib;
	}
	public void setMessagelib(int messagelib) {
		this.messagelib = messagelib;
	}
	public int getPhonebook() {
		return phonebook;
	}
	public void setPhonebook(int phonebook) {
		this.phonebook = phonebook;
	}
	public int getLists() {
		return lists;
	}
	public void setLists(int lists) {
		this.lists = lists;
	}
	public int getModules() {
		return modules;
	}
	public void setModules(int modules) {
		this.modules = modules;
	}
	public int getProfiles() {
		return profiles;
	}
	public void setProfiles(int profiles) {
		this.profiles = profiles;
	}
	public boolean canReadProfiles()
	{
		return getProfiles()>=1;
	}
	public int getPhonebookShare() {
		return phonebookShare;
	}
	public void setPhonebookShare(int phonebookShare) {
		this.phonebookShare = phonebookShare;
	}
	public int getListsShare() {
		return listsShare;
	}
	public void setListsShare(int listsShare) {
		this.listsShare = listsShare;
	}
	public int getModulesShare() {
		return modulesShare;
	}
	public void setModulesShare(int modulesShare) {
		this.modulesShare = modulesShare;
	}
	public int getProfilesShare() {
		return profilesShare;
	}
	public void setProfilesShare(int profilesShare) {
		this.profilesShare = profilesShare;
	}
	public int getPhonebookName() {
		return phonebookName;
	}
	public void setPhonebookName(int phonebookName) {
		this.phonebookName = phonebookName;
	}
	public int getPhonebookPhone() {
		return phonebookPhone;
	}
	public void setPhonebookPhone(int phonebookPhone) {
		this.phonebookPhone = phonebookPhone;
	}
	public int getPhonebookPin() {
		return phonebookPin;
	}
	public void setPhonebookPin(int phonebookPin) {
		this.phonebookPin = phonebookPin;
	}
	public int getPhonebookLocality() {
		return phonebookLocality;
	}
	public void setPhonebookLocality(int phonebookLocality) {
		this.phonebookLocality = phonebookLocality;
	}
	public int getLocality() {
		return locality;
	}
	public void setLocality(int locality) {
		this.locality = locality;
	}
	public int getSched() {
		return sched;
	}
	public void setSched(int sched) {
		this.sched = sched;
	}
	public int getSchedRetry() {
		return schedRetry;
	}
	public void setSchedRetry(int schedRetry) {
		this.schedRetry = schedRetry;
	}
	public int getSchedCancel() {
		return schedCancel;
	}
	public void setSchedCancel(int schedCancel) {
		this.schedCancel = schedCancel;
	}
	public int getSchedPause() {
		return schedPause;
	}
	public void setSchedPause(int schedPause) {
		this.schedPause = schedPause;
	}
	public int getSchedShare() {
		return schedShare;
	}
	public void setSchedShare(int schedShare) {
		this.schedShare = schedShare;
	}
	public int getMessagelibShare() {
		return messagelibShare;
	}
	public void setMessagelibShare(int messagelibShare) {
		this.messagelibShare = messagelibShare;
	}
	public int getSuperuser() {
		return superuser;
	}
	public void setSuperuser(int superuser) {
		this.superuser = superuser;
	}
	public int getResource() {
		return resource;
	}
	public void setResource(int resource) {
		this.resource = resource;
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
