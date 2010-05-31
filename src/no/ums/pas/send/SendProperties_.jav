package Send;

import PAS.*;
import UMS.ErrorHandling.Error;
import UMS.Tools.*;
import java.awt.*;
import java.util.*;
import Core.DataExchange.*;
import Maps.Defines.*;
import Core.Controllers.*;
import Status.StatusCode;

public abstract class SendProperties extends Object {
	public static final int SENDING_TYPE_POLYGON_ = 0;
	public static final int SENDING_TYPE_HOUSESELECT_ = 1;
	public static final int SENDING_TYPE_SQUARE_ = 2;
	public static final int SENDING_TYPE_GROUPS_ = 3;
	public static final int SENDING_TYPE_SINGLEADR_ = 4;
	public static final int SENDING_TYPE_GEMINI_GNRBNR_ = 5;
	public static final int SENDING_TYPE_GEMINI_STREETCODE_ = 6;
	public static final int SENDING_TYPE_ADRLIST_ = 7;
	public static final int SENDING_TYPE_CIRCLE_ = 8;
	//public static final int SENDING_TYPE_RESEND_ = 9;
	
	private int m_n_refno;
	private String m_sz_sendingname = "";
	private String m_sz_description = "";
	private BBProfile m_bbprofile = null;
	private int m_n_profilepk = -1;
	private SchedDateTime m_sched = null;
	private boolean m_b_usesched = false;
	private SoundFileArray m_soundfiles = null;
	private OADC m_oadc = null;
	private String m_sz_oadc;
	private int m_n_validity;
	private BBSchedProfile m_schedprofile = null;
	private int m_n_sendingtype;
	private String m_sz_projectpk = "";
	private boolean m_b_simulation = false;
	private boolean m_b_isresend = false;
	private int m_n_resend_refno = -1;
	private ArrayList m_arr_resend_status = new ArrayList();
	public void addResendStatus(StatusCode sz_code) {
		boolean b_found = false;
		for(int i=0; i < m_arr_resend_status.size(); i++) {
			if(m_arr_resend_status.get(i).toString().equals(sz_code)) {
				b_found = true;
				break;
			}
		}
		if(!b_found) {
			m_arr_resend_status.add(sz_code);
		}
	}
	public void remResendStatus(StatusCode sz_code) {
		m_arr_resend_status.remove(sz_code);
	}
	
	
	public BBProfile get_bbprofile() { return m_bbprofile; }
	public SchedDateTime get_sched() { return m_sched; }
	public OADC get_oadc() { return m_oadc; }
	public int get_validity() { return m_n_validity; }
	public BBSchedProfile get_schedprofile() { return m_schedprofile; }
	public void set_schedprofile(BBSchedProfile sched) { m_schedprofile = sched; }
	public int get_sendingtype() { return m_n_sendingtype; }
	protected SendOptionToolbar parent;
	protected SendObject get_sendobject() { return parent.get_parent(); }
	public void set_simulation(boolean b) { m_b_simulation = b; }
	public boolean get_simulation() { return m_b_simulation; }
	public int get_profilepk() { return m_n_profilepk; }
	public void set_profilepk(int n_profilepk) { m_n_profilepk = n_profilepk; }
	public String get_oadc_number() { return m_sz_oadc; }
	public void set_oadc_number(String sz_oadc) { m_sz_oadc = sz_oadc; }
	public String m_sz_schedprofilepk;
	public void set_schedprofilepk(String sz_pk) { m_sz_schedprofilepk = sz_pk; }
	public String get_schedprofilepk() { return m_sz_schedprofilepk; }
	public boolean get_isresend() { return m_b_isresend; }
	public int get_resend_refno() { return m_n_resend_refno; }
	
	public void set_bbprofile(BBProfile profile) { m_bbprofile = profile; }
	public void set_scheddatetime(SchedDateTime sched) { m_sched = sched; }
	public void set_sendingname(String sz_name, String sz_description) {
		m_sz_sendingname = sz_name;
		m_sz_description = sz_description;
		parent.set_sendingname(m_sz_sendingname, sz_description);
	}
	public String get_sendingname() { return m_sz_sendingname; }
	public String get_description() { return m_sz_description; }
	public void set_usesched(boolean b_use) { m_b_usesched = b_use; }
	public boolean get_usesched() { return m_b_usesched; }
	public void set_refno(int n_refno) { m_n_refno = n_refno; }
	public int get_refno() { return m_n_refno; }
	public void set_oadc(OADC oadc) { m_oadc = oadc; }
	public void set_validity(int n_days) { m_n_validity = n_days; }
	private String m_sz_last_error;
	public String get_last_error() { return m_sz_last_error; }
	public int get_addresstypes() { return parent.get_addresstypes(); }
	public void set_projectpk(String sz_projectpk) { m_sz_projectpk = sz_projectpk; }
	public String get_projectpk() { return m_sz_projectpk; }
	private ShapeStruct m_shape;
	public ShapeStruct get_shapestruct() { return m_shape; }
	public void set_shapestruct(ShapeStruct s) { m_shape = s; }
	public void set_resend(int n_refno) {
		m_b_isresend = true;
		m_n_resend_refno = n_refno;
	}
	
	public int get_addresstypes_bitwise() {
		/*
	public static final int ADR_TYPES_PRIVATE_ = 1;
	public static final int ADR_TYPES_COMPANY_ = 2;
	public static final int ADR_TYPES_MOBILE_  = 4;
	public static final int ADR_TYPES_FAX_     = 8;
	public static final int ADR_TYPES_SHOW_ALL_ = ADR_TYPES_PRIVATE_ | ADR_TYPES_COMPANY_ | ADR_TYPES_MOBILE_ | ADR_TYPES_FAX_;
	public int ADR_TYPES_SHOW_ = ADR_TYPES_SHOW_ALL_;
		 */
		/*int ret = 0;
		switch(parent.get_addresstypes()) {
			case 0:
				ret = Controller.ADR_TYPES_PRIVATE_ | Controller.ADR_TYPES_COMPANY_;
				break;
			case 1:
				ret = Controller.ADR_TYPES_PRIVATE_;
				break;
			case 2:
				ret = Controller.ADR_TYPES_COMPANY_;
				break;
			case 3:
				ret = Controller.ADR_TYPES_MOBILE_;
				break;
			case 4:
				ret = Controller.ADR_TYPES_PRIVATE_ | Controller.ADR_TYPES_MOBILE_;
				break;
			case 5:
				ret = Controller.ADR_TYPES_COMPANY_ | Controller.ADR_TYPES_MOBILE_;
				break;
			case 6:
				ret = Controller.ADR_TYPES_SHOW_ALL_;
				break;
		}*/
		return parent.get_addresstypes();
	}
	protected void set_last_error(String sz_error) { m_sz_last_error = sz_error; }
	public SendOptionToolbar get_toolbar() { return parent; }
	protected Col m_col_default;
	public SendPropertiesPolygon typecast_poly() { return (SendPropertiesPolygon)this; }
	public SendPropertiesGIS typecast_gis() { return (SendPropertiesGIS)this; }
	public SendPropertiesEllipse typecast_ellipse() { return (SendPropertiesEllipse)this; }
	public Col get_default_color() { return m_col_default; }
	
	private String sz_params[] = new String[] { "sz_sendingname", "l_profilepk", "l_scheddate", "l_schedtime", 
												"sz_oadc", "l_validity", "l_reschedprofile", "l_sendingtype", "l_refno",
												"n_wavfiles", "l_retries", "l_interval", "l_canceltime", "l_canceldate",
												"l_pausetime", "l_pauseinterval", "l_addresstypes", "l_comppk", "l_deptpk",
												"l_deptpri", "l_bo", "r_bo", "u_bo", "b_bo", "l_projectpk", "b_simulation",
												"b_resend", "l_resend_refno", "szStatus"};
	private String sz_vals[];
	public String [] get_params() { return sz_params; }
	public String [] get_vals() { return sz_vals; }
	//public void set_date(int n_date) { m_sched.set_date(n_date); }
	//public void set_time(int n_time) { m_sched.set_time(n_time); }
		
	public SendProperties(int n_sendingtype, SendOptionToolbar parent, Col col_default) {
		super();
		this.parent = parent;
		m_col_default = col_default;
		m_n_sendingtype = n_sendingtype;
		m_soundfiles = new SoundFileArray();
		m_sched = new SchedDateTime();
	}
	public void create_common_paramvals(int n_sendingtype) {
		sz_vals = new String[sz_params.length];
		try {
			sz_vals[0] = new String(get_sendingname());
			sz_vals[1] = new String(new Integer(get_bbprofile().get_profilepk()).toString());
			if(n_sendingtype==SENDING_TYPE_ADRLIST_) { //skip resched
				sz_vals[2] = new String("-1");
				sz_vals[3] = new String("-1");				
			} else {
				sz_vals[2] = new String(new Integer(get_sched().get_scheddate()).toString());
				sz_vals[3] = new String(new Integer(get_sched().get_schedtime()).toString());
			}
			sz_vals[4] = new String(get_oadc().get_number());
			sz_vals[5] = new String(new Integer(get_validity()).toString());
			sz_vals[6] = new String(get_schedprofile().get_reschedpk());
			sz_vals[7] = new String(new Integer(n_sendingtype).toString());
			sz_vals[8] = new String(new Integer(get_refno()).toString());
			sz_vals[9] = new String(new Integer(get_bbprofile().get_soundfiles().size()).toString());
			sz_vals[10]= new String(new Integer(get_schedprofile().get_retries()).toString());
			sz_vals[11]= new String(new Integer(get_schedprofile().get_interval()).toString());
			sz_vals[12]= new String(new Integer(get_schedprofile().get_canceltime()).toString());
			sz_vals[13]= new String(new Integer(get_schedprofile().get_canceldate()).toString());
			sz_vals[14]= new String(new Integer(get_schedprofile().get_pausetime()).toString());
			sz_vals[15]= new String(new Integer(get_schedprofile().get_pauseinterval()).toString());
			sz_vals[16]= new String(new Integer(get_addresstypes()).toString());
			sz_vals[17]= new String(new Integer(PAS.get_pas().get_userinfo().get_comppk()).toString());
			sz_vals[18]= new String(new Integer(PAS.get_pas().get_userinfo().get_default_dept().get_deptpk()).toString());
			sz_vals[19]= new String(new Integer(PAS.get_pas().get_userinfo().get_default_dept().get_deptpri()).toString());
			sz_vals[20]= new String(PAS.get_pas().get_navigation().getHeaderLBO().toString());
			sz_vals[21]= new String(PAS.get_pas().get_navigation().getHeaderRBO().toString());
			sz_vals[22]= new String(PAS.get_pas().get_navigation().getHeaderUBO().toString());
			sz_vals[23]= new String(PAS.get_pas().get_navigation().getHeaderBBO().toString());
			sz_vals[24]= new String(get_projectpk());
			sz_vals[25]= new String((get_simulation() ? "1" : "0"));
			sz_vals[26]= new String((get_isresend() ? "1" : "0"));
			sz_vals[27]= new String(new Integer(get_resend_refno()).toString());
			String sz_statusstring = "";
			if(get_isresend()) {
				for(int i=0; i < m_arr_resend_status.size(); i++) {
					if(i>0)
						sz_statusstring += ", ";
					try {
						sz_statusstring += new Integer(((StatusCode)m_arr_resend_status.get(i)).get_code()).toString();
					} catch(Exception e) {
						System.out.println("Error adding statuscode for resend. " + e.getMessage());
					}
				}
			} else {
				sz_statusstring = "";
			}
			sz_vals[28] = sz_statusstring;
			
			
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR create_paramvals() - " + e.getMessage(), e);
			Error.getError().addError("SendProperties","Exception in create_common_paramvals",e,1);
		}
		for(int i=0; i < get_params().length; i++) {
			PAS.get_pas().add_event(get_params()[i] + " = " + get_vals()[i], null);
		}		
	}
	protected boolean populate_common(HttpPostForm http) {
		//try {
			for(int i=0; i < get_params().length; i++) {
				try {
					http.setParameter(get_params()[i], get_vals()[i]);
				} catch(Exception e) {
					System.out.println("Error populating parameter " + i);
					Error.getError().addError("SendProperties","Exception in populate_common",e,1);
					return false;
				}
			}
		/*} catch(java.io.IOException e) {
			return false;
		}*/
		return true;
	}
	protected boolean populate_adrlist(HttpPostForm http, ArrayList arr_numbers) {
		try {
			for(int i=0; i < arr_numbers.size(); i++) {
				http.setParameter("sz_number_" + i, arr_numbers.get(i));
			}
			http.setParameter("n_num_numbers", new Integer(arr_numbers.size()).toString());
		} catch(java.io.IOException e) {
			Error.getError().addError("SendProperties","Exception in populate_adrlist",e,1);
			return false;
		}
		return true;
	}
	
	abstract public void set_adrinfo(Object adr);
	abstract public void draw(Graphics g, Point mousepos);
	abstract public PolySnapStruct snap_to_point(Point p1, int n_max_distance);
	abstract public boolean goto_area();
	abstract public boolean can_lock();
	abstract public void set_color(Color col);
	abstract public Color get_color();
	
	
	public boolean send(int l_refno) {
		set_refno(l_refno);
		this.create_common_paramvals(this.get_sendingtype());
		//PAS.get_pas().add_event("Sending " + get_refno(), null);
		
		return this.send();
	}
	public boolean send_test(int l_refno, ArrayList arr_numberlist) {
		set_refno(l_refno);
		this.create_common_paramvals(SENDING_TYPE_ADRLIST_);
		//PAS.get_pas().add_event("Sending testmessage " + get_refno(), null);
		
		return this.send_test(arr_numberlist);
	}
	protected boolean send_test(ArrayList arr_numberlist) {
		HttpPostForm http = null;
		try {
			http = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_send.asp");
		} catch(java.io.IOException e) {
			Error.getError().addError("SendProperties","Exception in send_test",e,1);
			return false;
		}
		if(!populate_common(http))
			return false;
		if(!populate_adrlist(http, arr_numberlist))
			return false;
		try {
			http.post();
		} catch(java.io.IOException e) {
			Error.getError().addError("SendProperties","Exception in send_test",e,1);
			return false;
		}
		return true;
	}
	
	abstract protected boolean send();
}