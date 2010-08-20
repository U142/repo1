package no.ums.pas.core.logon;

import java.util.ArrayList;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.maps.defines.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.pas.*;
import java.util.*;


public class UserInfo extends Object {
	private String m_sz_name;
	private String m_sz_surname;
	private String m_sz_userid;
	private String m_sz_compid;
	private String m_sz_passwd;
	private String m_sz_sessionid;
	private boolean m_b_session_active = false;
	private int m_n_comppk;
	private String m_sz_userpk;
	private int m_n_default_deptpk;
	private String m_sz_sitename;
	NavStruct m_nav_init = null;
	public void set_session_active(boolean b) { m_b_session_active = b; }
	public boolean is_session_active() { return m_b_session_active; }
	public String get_userid() { return m_sz_userid; }
	public String get_compid() { return m_sz_compid; }
	public String get_userpk() { return m_sz_userpk; }
	public int  get_comppk() { return m_n_comppk; }
	public String get_realname() { return m_sz_name + " " + m_sz_surname; }
	public NavStruct get_nav_init() { return m_nav_init; }
	public void set_nav_init(NavStruct n) { m_nav_init = n; }
	public String get_passwd() { return m_sz_passwd; }
	public String get_sessionid() { return m_sz_sessionid; }
	private DeptArray m_departments;
	private DeptInfo m_default_dept = null;
	public DeptArray get_departments() { return m_departments; }
	public int get_default_deptpk() { return m_n_default_deptpk; }
	public DeptInfo get_default_dept() { return m_default_dept; }
	public void set_passwd(String sz_passwd) { m_sz_passwd = sz_passwd; }
	public void set_sessionid(String sz_sessionid) { m_sz_sessionid = sz_sessionid; }
	public String get_sitename() { return m_sz_sitename; }
	public void set_sitename(String sz_site) { m_sz_sitename = sz_site; }
	private MailAccount m_account;
	public MailAccount get_mailaccount() {
		if(m_account==null)
			m_account = new MailAccount();
		return m_account; 
	}
	public void set_mailaccount(MailAccount ac) { m_account = ac; }
	private ArrayList<NSLookup> m_arr_nslookup = new ArrayList<NSLookup>();
	public ArrayList<NSLookup> get_nslookup() { return m_arr_nslookup; }
	public void add_nslookup(NSLookup ns) { m_arr_nslookup.add(ns); }
	public NSLookup get_nslookup(int i) { return (NSLookup)get_nslookup().get(i); } 
	
	private DeptInfo m_current_dept;
	public DeptInfo get_current_department() { return m_current_dept; }
	public void set_current_department(DeptInfo dept) { m_current_dept = dept; }
	
	public UserInfo(String n_userpk, int n_comppk, String sz_userid, String sz_compid,
			String sz_name, String sz_surname, String sessionid) {
		m_sz_userid = sz_userid;
		m_sz_compid = sz_compid;
		m_n_comppk  = n_comppk;
		m_sz_userpk  = n_userpk;
		m_sz_name = sz_name;
		m_sz_surname = sz_surname;
		m_sz_sessionid = sessionid;
		m_departments = new DeptArray();
	}
	/*public UserInfo(String [] s) {
		//"l_userpk", "l_comppk", "sz_userid", "sz_compid", "sz_name", "sz_surname" 
		m_sz_userpk = s[0];
		m_n_comppk 	= new Integer(s[1]).intValue();
		m_sz_userid = s[2];
		m_sz_compid = s[3];
		m_sz_name   = s[4];
		m_sz_surname= s[5];
		m_departments = new DeptArray();
	}*/
	public UserInfo(UserInfo info) {
		m_sz_userid = info.m_sz_userid;
		m_sz_compid = info.m_sz_compid;
		m_n_comppk  = info.m_n_comppk;
		m_sz_userpk = info.m_sz_userpk;
		m_nav_init  = info.m_nav_init;
		m_sz_passwd = info.m_sz_passwd;
		m_sz_name   = info.m_sz_name;
		m_sz_surname= info.m_sz_surname;
		m_sz_sessionid = info.m_sz_sessionid;
		m_departments = new DeptArray();
		m_departments.m_combined_shapestruct_list = info.get_departments().get_combined_restriction_shape();
		/*m_departments = (DeptArray)info.m_departments.clone();*/
		for(int i=0; i < info.get_departments().size(); i++) {
			m_departments.add(info.get_departments().get(i));
			m_default_dept = info.get_default_dept();
			m_current_dept = info.get_default_dept();
			m_n_default_deptpk = info.get_default_deptpk();
		}
	}
	/*void add_department(int n_deptpk, String sz_deptid, String sz_stdcc, double l_bo, double r_bo, double u_bo, double b_bo, boolean b_default_dept,
							int n_deptpri, int n_maxalloc) {
		NavStruct nav_init = null;
		if(l_bo!=0 && r_bo!=0 && u_bo!=0 && b_bo!=0)
			nav_init = new NavStruct(l_bo, r_bo, u_bo, b_bo);
		DeptInfo dept = get_departments().add(n_deptpk, sz_deptid, sz_stdcc, nav_init, b_default_dept, n_deptpri, n_maxalloc);
		if(b_default_dept)
		{
			m_n_default_deptpk = n_deptpk;
			m_nav_init = nav_init;
			m_default_dept = dept;
		}
	}*/
	/*
	 "l_deptpk", "sz_deptid", "sz_stdcc", "lbo", "rbo", "ubo", "bbo", "f_default", "l_deptpri", "l_maxalloc", 
	 "sz_userprofilename", "sz_userprofiledesc", "l_status", "l_newsending", "l_parm", "l_fleetcontrol", "l_cellbroadcast",
	 "l_houseeditor", "l_addresstypes", "sz_defaultnumber"
	 */
	
	public void add_department(int l_deptpk, String sz_deptid, String sz_stdcc, float lbo, float rbo, float ubo, float bbo,
					boolean f_default, int l_deptpri, int l_maxalloc, String sz_userprofilename, 
					String sz_userprofiledesc, int l_status, int l_newsending, int l_parm, int l_fleetcontrol,
					int l_cellbroadcast, int l_houseeditor, long l_addresstypes, String sz_defaultnumber,
					List<UMunicipalDef> municipals, int l_pas, ArrayOfUShape restriction_shapes)
	{
		try {
		NavStruct nav_init = new NavStruct(lbo, rbo, ubo, bbo);
		UserProfile m_userprofile = new UserProfile(sz_userprofilename, sz_userprofiledesc, l_fleetcontrol,
									l_parm, l_status, l_newsending, l_cellbroadcast, l_houseeditor,
									l_addresstypes);
		DeptInfo dept = get_departments().add(l_deptpk, sz_deptid, sz_stdcc, nav_init, f_default, l_deptpri, 
										l_maxalloc, sz_defaultnumber, m_userprofile, municipals, l_pas, 
										restriction_shapes);
		
		if(f_default)
		{
			m_n_default_deptpk = l_deptpk;
			if(l_pas==4) //TAS show world map
				m_nav_init = new NavStruct(-150, 160, 89, -89);
			else
				m_nav_init = nav_init;
			m_default_dept = dept;
			this.set_current_department(dept);
		}
		} catch(Exception e) { System.out.println("ninja"); }
	}
			
	/*void add_department(String [] s) {
		NavStruct nav_init = null;
		try {
			double l_bo = new Double(s[3]).doubleValue();
			double r_bo = new Double(s[4]).doubleValue();
			double u_bo = new Double(s[5]).doubleValue();
			double b_bo = new Double(s[6]).doubleValue();
			if(l_bo!=0 && r_bo!=0 && u_bo!=0 && b_bo!=0)
				nav_init = new NavStruct(l_bo, r_bo, u_bo, b_bo);
		} catch(Exception e) {
			
		}
		int n_deptpk = new Integer(s[0]).intValue();
		String sz_deptid = s[1];
		String sz_stdcc = s[2];
		boolean b_default_dept = (s[7].equals("1") ? true : false);
		int n_deptpri = new Integer(s[8]).intValue();
		int n_maxalloc = new Integer(s[9]).intValue();
//		userprofile
		String sz_profilename = s[10];
		String sz_profiledesc = s[11];
		int n_status = new Integer(s[12]).intValue();
		int n_newsending = new Integer(s[13]).intValue();
		int n_parm = new Integer(s[14]).intValue();
		int n_fleetcontrol = new Integer(s[15]).intValue();
		int n_cellbroadcast = new Integer(s[16]).intValue();
		int n_houseeditor = new Integer(s[17]).intValue();
		int n_addresstypes = new Integer(s[18]).intValue();
		String sz_defaultnumber = s[19];
		
		UserProfile m_userprofile = new UserProfile(sz_profilename, sz_profiledesc, n_fleetcontrol, n_parm, n_status, n_newsending, n_cellbroadcast, n_houseeditor, n_addresstypes);
		
		DeptInfo dept = get_departments().add(n_deptpk, sz_deptid, sz_stdcc, nav_init, b_default_dept, n_deptpri, n_maxalloc, sz_defaultnumber, m_userprofile);
		if(b_default_dept)
		{
			m_n_default_deptpk = n_deptpk;
			m_nav_init = nav_init;
			m_default_dept = dept;
			this.set_current_department(dept);
		}		
	}*/
	
	public class NSLookup extends Object {
		private String m_sz_domain;
		private String m_sz_ip;
		private String m_sz_lastdatetime;
		private String m_sz_location;
		private Boolean m_b_success;
		
		public String get_domain() { return m_sz_domain; }
		public String get_ip() { return m_sz_ip; }
		public String get_location() { return m_sz_location; }
		public String get_lastdatetime() { return TextFormat.format_datetime(m_sz_lastdatetime); }
		public Boolean get_success() { return m_b_success; }
		public String toString() { return get_domain(); }
		public String get_ssuccess() { 
			if(m_b_success.booleanValue())
				return PAS.l("logon_success");
			else
				return PAS.l("logon_failed"); 
		}
		
		public NSLookup(String sz_domain, String sz_ip, long lastdatetime, String sz_location, boolean b_success)
		{
			super();
			m_sz_domain = sz_domain;
			m_sz_ip = sz_ip;
			m_sz_lastdatetime = new Long(lastdatetime).toString();
			m_sz_location = sz_location;
			m_b_success = b_success;
			add_nslookup(this);
		}
		
		public NSLookup(String [] sz) {
			super();
			init(sz);
			add_nslookup(this);
		}
		void init(String [] sz) {
			try {
				m_sz_domain = sz[0];
				m_sz_ip		= sz[1];
				m_sz_lastdatetime = sz[2];
				m_sz_location	  = sz[3];
				m_b_success = (new Integer(sz[4]).intValue()==1 ? new Boolean(true) : new Boolean(false));
			} catch(Exception e) {
				Error.getError().addError("Error initializing", "Could not create NSLookup record", e, 1);
			}
		}
	}
}
