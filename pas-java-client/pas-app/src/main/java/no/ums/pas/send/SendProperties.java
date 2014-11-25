package no.ums.pas.send;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.AreaController;
import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.soap.SoapExecAlert;
import no.ums.pas.core.dataexchange.soap.SoapExecAlert.SnapAlertResults;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.WSAdrcount;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.cap.CapConfigView;
import no.ums.pas.send.cellbroadcast.CBMessage;
import no.ums.pas.send.sendpanels.Sending_Cell_Broadcast_text;
import no.ums.pas.send.sendpanels.Sending_SMS_Broadcast_text;
import no.ums.pas.status.StatusCode;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.common.ArrayOfLBACCode;
import no.ums.ws.common.LBACCode;
import no.ums.ws.common.LBALanguage;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.UMapBounds;
import no.ums.ws.common.parm.ArrayOfLBALanguage;
import no.ums.ws.common.parm.ArrayOfLong;
import no.ums.ws.common.parm.ArrayOfString;
import no.ums.ws.common.parm.BBSENDNUM;
import no.ums.ws.common.parm.ULocationBasedAlert;
import no.ums.ws.common.parm.UMAPSENDING;
import no.ums.ws.common.parm.UMapSendingCapFields;
import no.ums.ws.common.parm.UTESTSENDING;
import no.ums.ws.parm.AlertResultLine;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.ObjectFactory;
import no.ums.ws.parm.Parmws;
import no.ums.ws.parm.UAdrCount;

import javax.xml.namespace.QName;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public abstract class SendProperties extends Object {

    private static final Log log = UmsLog.getLogger(SendProperties.class);

	public static final int SENDING_TYPE_POLYGON_ = 0;
	public static final int SENDING_TYPE_HOUSESELECT_ = 1;
	public static final int SENDING_TYPE_SQUARE_ = 2;
	public static final int SENDING_TYPE_GROUPS_ = 3;
	public static final int SENDING_TYPE_SINGLEADR_ = 4;
	public static final int SENDING_TYPE_GEMINI_GNRBNR_ = 5;
	public static final int SENDING_TYPE_GEMINI_STREETCODE_ = 6;
	public static final int SENDING_TYPE_ADRLIST_ = 7;
	public static final int SENDING_TYPE_CIRCLE_ = 8;
	public static final int SENDING_TYPE_MUNICIPAL_ = 9;
	public static final int SENDING_TYPE_TAS_COUNTRY_ = 10;
	public static final int SENDING_TYPE_PAINT_RESTRICTION_AREA_ = 11;
	public static final int SENDING_TYPE_POLYGONAL_ELLIPSE_ = 12;
    public static final int SENDING_TYPE_HOUSESELECT_ALERT_ = 13;

	
	//public static final int SENDING_TYPE_RESEND_ = 9;
	
	private int m_n_refno;
	private String m_sz_sendingname = "";
	private String m_sz_description = "";
	private BBProfile m_bbprofile = null;
	private int m_n_profilepk = -1;
	private SchedDateTime m_sched = null;
	private boolean m_b_usesched = false;
	protected SoundFileArray m_soundfiles = null;
	private OADC m_oadc = null;
	private String m_sz_oadc;
	private int m_n_validity;
	private BBSchedProfile m_schedprofile = null;
	private BBSchedProfile m_1try_schedprofile = null;
	private int m_n_sendingtype;
	private String m_sz_projectpk = "";
	private int m_n_simulation = 0;
	private boolean m_b_isresend = false;
	private int m_n_resend_refno = -1;
	private Set<StatusCode> m_arr_resend_status = new LinkedHashSet<StatusCode>();
	private int m_n_maxchannels = 1;
	private int m_n_requesttype = 0;
	private int m_n_sendchannels = 0; //0 is both, 1 is voice, 2 is sms
	private String m_sz_sms_message;
	private String m_sz_sms_oadc;
	private int m_n_duration; // centric
	private int m_n_channel; // centric
	
	private String sendTestType = null;
	private int reshedVal = 0;
	public String getSendTestType() {
		return sendTestType;
	}
	public void setSendTestType(String sendTestType) {
		this.sendTestType = sendTestType;
	}
	
	public void addResendStatus(StatusCode sz_code) {
        m_arr_resend_status.add(sz_code);
	}
	public void remResendStatus(StatusCode sz_code) {
		m_arr_resend_status.remove(sz_code);
	}
	
	public abstract boolean PerformAdrCount(ActionListener l, String act);
	
	public abstract void calc_coortopix();

	public BBProfile get_bbprofile() { return m_bbprofile; }
	public SchedDateTime get_sched() { return m_sched; }
	public OADC get_oadc() { return m_oadc; }
	public int get_validity() { return m_n_validity; }
	public BBSchedProfile get_schedprofile() { return m_schedprofile; }
	public void set_schedprofile(BBSchedProfile sched) { m_schedprofile = sched; }
	public BBSchedProfile get_1try_schedprofile() { return m_1try_schedprofile; }
	public void set_1try_schedprofile(BBSchedProfile sched) { m_1try_schedprofile = sched; }
	public int get_sendingtype() { return m_n_sendingtype; }
	protected SendOptionToolbar parent;
	protected AreaController parentAreaController;
	protected SendObject get_sendobject() { return parent.get_parent(); }
	public void set_simulation(int n) { m_n_simulation = n; }
	public int get_simulation() { return m_n_simulation; }
	public int get_profilepk() { return m_n_profilepk; }
	public void set_profilepk(int n_profilepk) { m_n_profilepk = n_profilepk; }
	public String get_oadc_number() { return m_sz_oadc; }
	public void set_oadc_number(String sz_oadc) { m_sz_oadc = sz_oadc; }
	public String m_sz_schedprofilepk;
	public void set_schedprofilepk(String sz_pk) { m_sz_schedprofilepk = sz_pk; }
	public String get_schedprofilepk() { return m_sz_schedprofilepk; }
	public boolean get_isresend() { return m_b_isresend; }
	public int get_resend_refno() { return m_n_resend_refno; }
	public void set_maxchannels(int n) { m_n_maxchannels = n; }
	public int get_maxchannels() { return m_n_maxchannels; }
	public void set_requesttype(int n) { m_n_requesttype = n; }
	public int get_requesttype() { return m_n_requesttype; }
	public void set_sendchannels(int n) { m_n_sendchannels = n; }
	public int get_sendchannels() { return m_n_sendchannels; }
	
	public void set_bbprofile(BBProfile profile) { m_bbprofile = profile; }
	public void set_scheddatetime(SchedDateTime sched) { m_sched = sched; }
	public void set_sendingname(String sz_name, String sz_description) {
		m_sz_sendingname = sz_name!=null ? sz_name : "";
		m_sz_description = sz_description!=null ? sz_description : "";
		parent.set_sendingname(m_sz_sendingname, sz_description);
		if(get_shapestruct()!=null)
		{
			get_shapestruct().shapeName = m_sz_sendingname;
		}
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
	private Sending_Cell_Broadcast_text m_cell_broadcast_text;
	public Sending_Cell_Broadcast_text get_cell_broadcast_text() { return m_cell_broadcast_text; }
	public void set_cell_broadcast_text(Sending_Cell_Broadcast_text scbt) { m_cell_broadcast_text = scbt; }
	private Sending_SMS_Broadcast_text m_sms_broadcast_text;
	public Sending_SMS_Broadcast_text get_sms_broadcast_text() { return m_sms_broadcast_text; }
	public void set_sms_broadcast_text(Sending_SMS_Broadcast_text ssbt) { m_sms_broadcast_text = ssbt; }
	public void set_sms_broadcast_message(String message) { m_sz_sms_message = message; }
	public String get_sms_broadcast_message() { return m_sz_sms_message; }
	public void set_sms_broadcast_oadc(String sms_oadc) { m_sz_sms_oadc = sms_oadc; }
	public String get_sms_broadcast_oadc() { return m_sz_sms_oadc; }
	public ShapeStruct get_shapestruct() { return m_shape; }
	public void set_shapestruct(ShapeStruct s) { 
		m_shape = s; 
	}
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
	public SendPropertiesMunicipal typecast_municipal() { return (SendPropertiesMunicipal)this; }
	public SendPropertiesTAS typecast_tas() { return (SendPropertiesTAS)this; }
	public Col get_default_color() { return m_col_default; }
	
	private String sz_params[] = new String[] { "sz_sendingname", "l_profilepk", "l_scheddate", "l_schedtime", 
												"sz_oadc", "l_validity", "l_reschedprofile", "l_sendingtype", "l_refno",
												"n_wavfiles", "l_retries", "l_interval", "l_canceltime", "l_canceldate",
												"l_pausetime", "l_pauseinterval", "l_addresstypes", "l_comppk", "l_deptpk",
												"l_deptpri", "l_bo", "r_bo", "u_bo", "b_bo", "l_projectpk", "b_simulation",
												"b_resend", "l_resend_refno", "szStatus", "sz_cell_broadcast_localtext",
												"sz_cell_broadcast_internationaltext", "l_cell_broadcast_area", "sz_cb_oadc", "l_maxchannels"};
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
		//m_sz_sms_oadc = PAS.get_pas().get_userinfo().get_current_department().get_defaultnumber();
		m_sz_sms_oadc = PAS.get_pas().get_userinfo().get_default_oadc();
		if(PAS.get_pas() != null)
			m_n_maxchannels = PAS.get_pas().get_userinfo().get_current_department().get_maxalloc();
	}
	
	public SendProperties(int n_sendingtype, AreaController parent, Col col_default) {
		super();
		this.parentAreaController = parent;
		m_col_default = col_default;
		m_n_sendingtype = n_sendingtype;
		m_soundfiles = new SoundFileArray();
		m_sched = new SchedDateTime();
		//m_sz_sms_oadc = PAS.get_pas().get_userinfo().get_current_department().get_defaultnumber();
		m_sz_sms_oadc = PAS.get_pas().get_userinfo().get_default_oadc();
		if(PAS.get_pas() != null)
			m_n_maxchannels = PAS.get_pas().get_userinfo().get_current_department().get_maxalloc();
	}

	public void CopyCommons(SendProperties sp)
	{
		m_arr_resend_status = sp.m_arr_resend_status;
		m_b_isresend = sp.m_b_isresend;
		m_b_usesched = sp.m_b_usesched;
		m_bbprofile = sp.m_bbprofile;
		m_cell_broadcast_text = sp.m_cell_broadcast_text;
		m_col_default = sp.m_col_default;
		m_n_channel = sp.m_n_channel;
		m_n_duration = sp.m_n_duration;
		m_n_maxchannels = sp.m_n_maxchannels;
		m_n_profilepk = sp.m_n_profilepk;
		m_n_refno = sp.m_n_refno;
		m_n_requesttype = sp.m_n_requesttype;
		m_n_resend_refno = sp.m_n_resend_refno;
		m_n_sendchannels = sp.m_n_sendchannels;
		//m_n_sendingtype = sp.m_n_sendingtype;
		m_n_simulation = sp.m_n_simulation;
		m_n_validity = sp.m_n_validity;
		m_oadc = sp.m_oadc;
		m_sched = sp.m_sched;
		m_schedprofile = sp.m_schedprofile;
		m_sms_broadcast_text = sp.m_sms_broadcast_text;
		m_soundfiles = sp.m_soundfiles;
		m_sz_description = sp.m_sz_description;
		m_sz_last_error = sp.m_sz_last_error;
		m_sz_oadc = sp.m_sz_oadc;
		m_sz_projectpk = sp.m_sz_projectpk;
		m_sz_schedprofilepk = sp.m_sz_schedprofilepk;
		m_sz_sendingname = sp.m_sz_sendingname;
		m_sz_sms_message = sp.m_sz_sms_message;
		m_sz_sms_oadc = sp.m_sz_sms_oadc;
		parent = sp.parent;
		sz_params = sp.sz_params;
		sz_vals = sp.sz_vals;
	}
	public void create_common_paramvals(int n_sendingtype) {
		sz_vals = new String[sz_params.length];
		try {
			sz_vals[0] = get_sendingname();
			sz_vals[1] = Integer.toString(get_bbprofile().get_profilepk());
			if(n_sendingtype==SENDING_TYPE_ADRLIST_) { //skip resched
				sz_vals[2] = "-1";
				sz_vals[3] = "-1";
			} else {
				sz_vals[2] = Integer.toString(get_sched().get_scheddate());
				sz_vals[3] = Integer.toString(get_sched().get_schedtime());
			}
			sz_vals[4] = get_oadc().get_number();
			sz_vals[5] = Integer.toString(get_validity());
			sz_vals[6] = get_schedprofile().get_reschedpk();
			sz_vals[7] = Integer.toString(n_sendingtype);
			sz_vals[8] = Integer.toString(get_refno());
			sz_vals[9] = Integer.toString(get_bbprofile().get_soundfiles().size());
			sz_vals[10]= Integer.toString(get_schedprofile().get_retries());
			sz_vals[11]= Integer.toString(get_schedprofile().get_interval());
			sz_vals[12]= Integer.toString(get_schedprofile().get_canceltime());
			sz_vals[13]= Integer.toString(get_schedprofile().get_canceldate());
			sz_vals[14]= Integer.toString(get_schedprofile().get_pausetime());
			sz_vals[15]= Integer.toString(get_schedprofile().get_pauseinterval());
			sz_vals[16]= Integer.toString(get_addresstypes());
			sz_vals[17]= Integer.toString(PAS.get_pas().get_userinfo().get_comppk());
			//sz_vals[18]= new String(new Integer(PAS.get_pas().get_userinfo().get_default_dept().get_deptpk()).toString());
			//sz_vals[19]= new String(new Integer(PAS.get_pas().get_userinfo().get_default_dept().get_deptpri()).toString());
			sz_vals[18]= new String(new Integer(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()).toString());
			sz_vals[19]= new String(new Integer(PAS.get_pas().get_userinfo().get_current_department().get_deptpri()).toString());
			sz_vals[20]= String.valueOf(Variables.getNavigation().getHeaderLBO());
			sz_vals[21]= String.valueOf(Variables.getNavigation().getHeaderRBO());
			sz_vals[22]= String.valueOf(Variables.getNavigation().getHeaderUBO());
			sz_vals[23]= String.valueOf(Variables.getNavigation().getHeaderBBO());
			sz_vals[24]= get_projectpk();
			sz_vals[25]=  new Integer(get_simulation()).toString();//(get_simulation() ? "1" : "0");
			sz_vals[26]= (get_isresend() ? "1" : "0");
			sz_vals[27]= String.valueOf(get_resend_refno());
			
			final String sz_statusstring;
            final StringBuilder statusstring = new StringBuilder();
			if(get_isresend()) {
                for (StatusCode statusCode : m_arr_resend_status) {
                    statusstring.append(", ").append(statusCode.get_code());
				}
                sz_statusstring = statusstring.substring(2);
			} else {
				sz_statusstring = "0";
			}
			sz_vals[28] = sz_statusstring;
//			if(parent.get_cell_broadcast_text().isSelected() || parent.get_cell_broadcast_voice().isSelected()) {
			if(parent.getChkLocationBased().isSelected() || parent.get_cell_broadcast_voice().isSelected()) {
				//sz_vals[29]= new String(parent.get_parent().get_sendwindow().get_cell_broadcast_text().get_txt_localtext().getText());
				//sz_vals[30]= new String(parent.get_parent().get_sendwindow().get_cell_broadcast_text().get_txt_internationaltext().getText());
				sz_vals[29]= "";
				sz_vals[30]= "";
				sz_vals[31]= String.valueOf(((Area) parent.get_parent().get_sendwindow().get_cell_broadcast_text().get_combo_area().getSelectedItem()).get_id());
				sz_vals[32]= parent.get_parent().get_sendwindow().get_cell_broadcast_text().get_txt_oadc_text().getText();
			} else {
				sz_vals[29]= "";
				sz_vals[30]= "";
				sz_vals[31]= "";
				sz_vals[32]= "";
			}
			sz_vals[33] = String.valueOf(get_maxchannels());
			
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR create_paramvals() - " + e.getMessage(), e);
			Error.getError().addError("SendProperties","Exception in create_common_paramvals",e,1);
		}
		for(int i=0; i < get_params().length; i++) {
			PAS.get_pas().add_event(get_params()[i] + " = " + get_vals()[i], null);
		}		
	}

	
	protected boolean populate_common(UMAPSENDING s, ULOGONINFO logon, UMapBounds bounds)
	{
		
		UserInfo info = PAS.get_pas().get_userinfo();
		logon.setLComppk(info.get_comppk());
		logon.setLDeptpk(info.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(info.get_userpk()));
		logon.setSzCompid(info.get_compid());
		logon.setSzDeptid(info.get_current_department().get_deptid());
		logon.setSzPassword(info.get_passwd());
		logon.setLDeptpri(info.get_current_department().get_deptpri());
		logon.setSessionid(info.get_sessionid());
		logon.setSzStdcc(info.get_current_department().get_stdcc());
		
		s.setLogoninfo(logon);
		s.setMapbounds(bounds);
		s.setNAddresstypes(get_addresstypes());
		
		if(((get_addresstypes() & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
				(get_addresstypes() & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 ||
				(get_addresstypes() & SendController.SENDTO_FIXED_COMPANY) > 0 ||
				(get_addresstypes() & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 ||
				(get_addresstypes() & SendController.SENDTO_FIXED_PRIVATE) > 0 ||
				(get_addresstypes() & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0 ||
				(get_addresstypes() & SendController.SENDTO_MOBILE_COMPANY) > 0 ||
				(get_addresstypes() & SendController.SENDTO_MOBILE_PRIVATE) > 0 ||
				(get_addresstypes() & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0 ||
				(get_addresstypes() & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0 ||
				(get_addresstypes() & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0 ||
				(get_addresstypes() & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0))	{
			s.setNCanceldate(get_schedprofile().get_canceldate());
			s.setNCanceltime(get_schedprofile().get_canceltime());
			s.setNInterval(get_schedprofile().get_interval());
			s.setNPauseinterval(get_schedprofile().get_pauseinterval());
			s.setNPausetime(get_schedprofile().get_pausetime());
			s.setNRetries(get_schedprofile().get_retries());
			s.setNReschedpk(new Long(get_schedprofile().get_reschedpk()).longValue());
			
			if(get_bbprofile() != null) {				
				s.setNDynvoc(get_bbprofile().get_soundfiles().size());
				s.setNProfilepk(get_bbprofile().get_profilepk());
			}
			s.setNMaxchannels(get_maxchannels());
		//	s.setNMaxchannels(info.get_current_department().get_maxalloc());
			
			BBSENDNUM sn = new BBSENDNUM();
			if(get_oadc()!=null && get_oadc().get_number()!=null)
				sn.setSzNumber(get_oadc().get_number());
			else
				sn.setSzNumber("23500801");
			s.setOadc(sn);
			s.setNSendChannels(get_sendchannels());
		}
		
		s.setNScheddate(get_sched().get_scheddate());
		s.setNSchedtime(get_sched().get_schedtime());
		s.setNValidity(get_validity());
		s.setNSendingtype(get_sendingtype());
		String szFunction = "simulate";
		switch(get_simulation())
		{
		case 0:
			szFunction = "live";
			break;
		case 1:
			szFunction = "simulate";
			break;
		case 2:
			szFunction = "silent";
			break;
		}
		s.setSzFunction(szFunction);//(get_simulation() ? "simulate" : "live"));
		s.setNProjectpk(new Long(get_projectpk()).longValue());
		s.setNRefno(get_refno());
		s.setSzLbaOadc("");
		s.setSzSendingname(get_sendingname());
		s.setBResend(get_isresend());
		s.setNResendRefno(get_resend_refno());
		
		ArrayOfLong resendStatus = new ArrayOfLong();
		if(get_isresend()) {
            for (StatusCode statusCode : m_arr_resend_status) {
                resendStatus.getLong().add((long) statusCode.get_code());
            }
		}
		//log.debug("TAS i sendproperties objid: " + System.identityHashCode(this));
		
		if(resendStatus.getLong().size() == 0)
			log.debug("No statuscodes");
		
		s.setResendStatuscodes(resendStatus);
		
		ULocationBasedAlert lba = new ULocationBasedAlert();
		if(get_sms_broadcast_text()!=null)
		{
			String oadc = get_sms_broadcast_text().get_txt_oadc_text().getText();
			String msg = get_sms_broadcast_text().get_txt_messagetext().getText();
			int exp = get_sms_broadcast_text().get_expiryMinutes();
			boolean allow_response = get_sms_broadcast_text().get_allow_response().isSelected();
			if(oadc.equals("") && s.getNSendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_)
				s.setSzSmsOadc("NULL");
			else
				s.setSzSmsOadc(oadc);
			s.setSzSmsMessage(msg);
			s.setNSmsExpirytimeMinutes(exp) ;//String.valueOf(exp));
			//s.set
		}
		if(get_cell_broadcast_text()!=null)
		{
			lba.setLAlertpk("0");
			ArrayOfLBALanguage lang = new ArrayOfLBALanguage();
			for(int i=0; i < get_cell_broadcast_text().getMessages().size(); i++)
			{
				CBMessage msg = (CBMessage)get_cell_broadcast_text().getMessages().get(i);
				LBALanguage lbalang = new LBALanguage();
				lbalang.setSzCbOadc(msg.getCboadc());
				lbalang.setSzName(msg.getMessageName());
				lbalang.setSzOtoa("1");
				lbalang.setSzText(msg.getMessage());
				ArrayOfLBACCode lbaccode = new ArrayOfLBACCode();
				for(int j=0; j < msg.getCcodes().size(); j++)
				{
					LBACCode ccode = new LBACCode();
					ccode.setCcode(msg.getCcodes().get(j).getCCode());
					lbaccode.getLBACCode().add(ccode);
				}
				lbalang.setMCcodes(lbaccode);
				
				lang.getLBALanguage().add(lbalang);
			}
			lba.setMLanguages(lang);
			lba.setNExpiryMinutes(get_cell_broadcast_text().get_expiryMinutes());
			lba.setNRequesttype(get_cell_broadcast_text().getRequestType());
		}
		s.setMLba(lba);

		
		UMapSendingCapFields cap = new UMapSendingCapFields();
		cap.setLanguage(System.getProperty("user.language"));
		cap.setSource(String.format("%s %s/%s/%s", "Operator", logon.getSzCompid(), logon.getSzDeptid(), logon.getSzUserid()));
		if(get_sms_broadcast_text()!=null)
		{
			cap.setHeadline(s.getSzSmsMessage());
		}
		//s.setCapFields(new CapConfigView().edit(cap));
		s.setCapFields(null);
		
		
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
		return this.send();
	}
	public boolean send_test(int l_refno, ArrayList<String> arr_numberlist,String sendTestType, int reshedVal) {
		set_refno(l_refno);
		setSendTestType(sendTestType);
		this.reshedVal = reshedVal;
		return this.send_test(arr_numberlist);
	}
	protected boolean send_test(ArrayList<String> arr_numberlist) {
		try
		{
			UTESTSENDING sending = new UTESTSENDING();
			ULOGONINFO logon = new ULOGONINFO();
			UMapBounds bounds = new UMapBounds();
			
			populate_common(sending, logon, bounds);
			//0 for both
			//1 for voice
			//2 for sms
			if("voice".equals(sendTestType)) 
			{
				sending.setNSendChannels(1);
				if(reshedVal==1)
				{
					sending.setNCanceldate(get_1try_schedprofile().get_canceldate());
					sending.setNCanceltime(get_1try_schedprofile().get_canceltime());
					sending.setNInterval(get_1try_schedprofile().get_interval());
					sending.setNPauseinterval(get_1try_schedprofile().get_pauseinterval());
					sending.setNPausetime(get_1try_schedprofile().get_pausetime());
					sending.setNRetries(get_1try_schedprofile().get_retries());
					sending.setNReschedpk(new Long(get_1try_schedprofile().get_reschedpk()).longValue());
				}
			}
			else if("sms".equals(sendTestType)) 
			{
				sending.setNSendChannels(2);
			}
			
			sending.setBResend(false);
			sending.setNResendRefno(0);
			sending.setNScheddate(0);
			sending.setNSchedtime(0);
			sending.setSzFunction("live"); //always set test sendings to live
			ArrayOfString list = new ArrayOfString();
			for(int i=0; i < arr_numberlist.size(); i++)
			{
				list.getString().add(arr_numberlist.get(i));
			}
			sending.setNumbers(list);
			URL wsdl = new URL(vars.WSDL_EXTERNALEXEC); //"http://localhost/WS/ExternalExec.asmx?WSDL");
			//URL wsdl = new java.net.URL(PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execTestSending(sending);
			parse_sendingresults(response, false);

			return true;
		}
		catch(Exception e)
		{
			Error.getError().addError("Error sending test message", e.getMessage(), e, 1);
		}
		
		return false;
	}
	protected void parse_sendingresults(ExecResponse ar, boolean b_openstatus_question)
	{
		try
		{
			SnapAlertResults res = new SoapExecAlert("0", "0", null).newSnapAlertResult();
			if(ar.getLExecpk()!=null)
				res.l_execpk = ar.getLExecpk();
			else
				res.l_execpk = "0";
			if(ar.getLProjectpk()!=null)
				res.setProjectPk(ar.getLProjectpk());
			else
				res.setProjectPk("");
			
			if(ar.getSzFunction()!=null)
				res.sz_sendfunction = ar.getSzFunction();
			else
				res.sz_sendfunction = "";
			
			if(ar.getArrAlertresults()!=null && ar.getArrAlertresults().getAlertResultLine()!=null)
			{
				Iterator it = ar.getArrAlertresults().getAlertResultLine().iterator();
				while(it.hasNext())
				{
					try
					{
						AlertResultLine line = (AlertResultLine)it.next();
						res.addExecAlertResult(Long.toString(line.getLAlertpk()), line.getSzName(), Integer.toString(line.getLRefno()), line.getSzResult(), line.getSzText(), line.getSzExtendedInfo());
					}
					catch(Exception e)
					{
						Error.getError().addError("Error parsing results", e.getLocalizedMessage(), e, 1);
						log.warn(e.getMessage(), e);					
					}
				}
			}
			PAS.get_pas().get_sendcontroller().showSnapResults(res, b_openstatus_question);
			// Denne skal lukkes kun ved resend
			if(m_b_isresend)
				parent.get_parent().get_sendwindow().dispose();
		}
		catch(Exception e)
		{
			Error.getError().addError("Error parsing results", e.getLocalizedMessage(), e, 1);
			log.warn(e.getMessage(), e);
		}
	}
	
	protected boolean _ExecAdrCount(UMAPSENDING m, ActionListener l, String act)
	{
		try
		{
			ULOGONINFO logon = new ULOGONINFO();
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
			m.setLogoninfo(logon);
			m.setNAddresstypes(get_addresstypes());
			WSAdrcount ws = new WSAdrcount(l, act, m);
			ws.start();
			return true;
		}
		catch(Exception e)
		{
			Error.getError().addError("Error in address count", e.getMessage(), e, 1);
			return false;
		}
	}
	
	abstract protected boolean send();
}