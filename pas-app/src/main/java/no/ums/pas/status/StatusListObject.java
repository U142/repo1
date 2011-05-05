package no.ums.pas.status;

import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.TooltipItem;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.mainui.SearchPanelStatusList;
import no.ums.pas.core.project.Project;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.common.UDeleteStatusResponse;

public class StatusListObject extends Object implements TooltipItem {

	private Project m_project = new Project();
	
	private int m_n_refno;
	private int m_n_sendingtype;
	private int m_n_totitem;
	private int m_n_altjmp;
	private int m_n_createdate;
	private int m_n_createtime;
	private String m_sz_sendingname;
	private int m_n_sendingstatus;
	private int m_n_group;
	private int m_n_type;
	private int m_n_deptpk;
	private String m_sz_deptid;
	private int m_n_simulation;
	private boolean m_b_marked_as_cancelled;
	private String m_sz_messagetext;
	
	public Project get_project() { return m_project; }
	public int get_refno() { return m_n_refno; }
	public int get_sendingtype() { return m_n_sendingtype; }
	public int get_totitem() { return m_n_totitem; }
	public int get_altjmp() { return m_n_altjmp; }
	public int get_createdate() { return m_n_createdate; }
	public int get_createtime() { return m_n_createtime; }
	public String get_sendingname() { return m_sz_sendingname; }
	public int get_sendingstatus() { return m_n_sendingstatus; }
	public int get_group() { return m_n_group; }
	public int get_type() { return m_n_type; }
	public int get_deptpk() { return m_n_deptpk; }
	public String get_deptid() { return m_sz_deptid; }
	public String toString() { return get_sendingname(); }//return new Integer(get_refno()).toString(); }
	public int get_simulation() { return m_n_simulation; }
	public boolean isMarkedAsCancelled() { return m_b_marked_as_cancelled; }
	public String get_messagetext() { return m_sz_messagetext; }
	
	public String getChannel()
	{
		if(get_type()==1) {
            return Localization.l("main_status_channel_voice");
        }
		else if(get_type()==2) {
            return Localization.l("main_status_channel_sms");
        }
		else if(get_type()==3) {
			return Localization.l("main_status_channel_email");
		}
		else if(get_type()==4) {
            return Localization.l("main_status_channel_lba");
        }
		else if(get_type()==5) {
            return Localization.l("main_status_channel_tas");
        }
		return "Unknown";
	}
	public String getSimulationText()
	{
		String ret = "Unknown";
		switch(get_simulation())
		{
		case 0:
			ret = Localization.l("common_live");
			break;
		case 1:
			ret = Localization.l("common_simulated");
			break;
		case 2:
			ret = Localization.l("common_silent");
			break;
		}
		return ret;
	}

	public String getStatusText()
	{
		String sz_statustext = "Unknown";
		if(get_type()==4 || get_type()==5)//LBA or TAS
			sz_statustext = LBASEND.LBASTATUSTEXT(get_sendingstatus());
		else
			sz_statustext = TextFormat.get_statustext_from_code(get_sendingstatus(), get_altjmp(), isMarkedAsCancelled());
		return sz_statustext;
	}
	
	public StatusListObject(int n_refno, int n_sendingtype, int n_totitem, int  n_altjmp, int n_createdate, int n_createtime, 
			 String sz_sendingname, int n_sendingstatus, int n_group, int n_type, int n_deptpk, String sz_deptid, String sz_projectpk,
			 String sz_projectname, String sz_createtimestamp, String sz_updatetimestamp, int simulation, 
			 int n_project_owner_deptpk, int n_project_owner_userpk, boolean bMarkedAsCancelled, String sz_messagetext)
	{
		m_n_refno	= n_refno;
		m_n_sendingtype	= n_sendingtype;
		m_n_totitem	= n_totitem;
		m_n_altjmp	= n_altjmp;
		m_n_createdate	= n_createdate;
		m_n_createtime	= n_createtime;
		m_sz_sendingname= sz_sendingname;
		m_n_sendingstatus= n_sendingstatus;
		m_n_group	= n_group;
		m_n_type	= n_type;
		m_n_deptpk	= n_deptpk;
		m_sz_deptid	= sz_deptid;
		m_n_simulation = simulation;
		m_b_marked_as_cancelled = bMarkedAsCancelled;
		get_project().set_projectpk(sz_projectpk);
		get_project().set_projectname(sz_projectname);
		get_project().set_createtimestamp(sz_createtimestamp);
		get_project().set_updatetimestamp(sz_updatetimestamp);
		get_project().set_saved();
		get_project().setOwnerDeptpk(n_project_owner_deptpk);
		get_project().setOwnerUserpk(n_project_owner_userpk);
		m_sz_messagetext = sz_messagetext;
	}
	public StatusListObject(String [] sz_values)
	{
		this(new Integer(sz_values[0]).intValue(), new Integer(sz_values[1]).intValue(), new Integer(sz_values[2]).intValue(),
				new Integer(sz_values[3]).intValue(), new Integer(sz_values[4]).intValue(), new Integer(sz_values[5]).intValue(),
				sz_values[6], new Integer(sz_values[7]).intValue(), new Integer(sz_values[8]).intValue(), 
				new Integer(sz_values[9]).intValue(), new Integer(sz_values[10]).intValue(), sz_values[11], sz_values[12],
				sz_values[13], sz_values[14], sz_values[15], Integer.valueOf(sz_values[16]),
				Integer.valueOf(sz_values[17]), Integer.valueOf(sz_values[18]), Boolean.valueOf(sz_values[19]), sz_values[20]);
		/*m_n_refno	= new Integer(sz_values[0]).intValue();
		m_n_sendingtype	= new Integer(sz_values[1]).intValue();
		m_n_totitem	= new Integer(sz_values[2]).intValue();
		m_n_altjmp	= new Integer(sz_values[3]).intValue();
		m_n_createdate	= new Integer(sz_values[4]).intValue();
		m_n_createtime	= new Integer(sz_values[5]).intValue();
		m_sz_sendingname= sz_values[6];
		m_n_sendingstatus= new Integer(sz_values[7]).intValue();
		m_n_group	= new Integer(sz_values[8]).intValue();
		m_n_type	= new Integer(sz_values[9]).intValue();
		m_n_deptpk	= new Integer(sz_values[10]).intValue();
		m_sz_deptid	= sz_values[11];*/
	}
	public String get_groupdesc() {
		switch(get_group())
		{
			case 0:
			case 1:
				return "N/A";
			case 2:
				return "Square";
			case 3:
				return "Polygon";
			case 4:
				return "Import";
			case 5:
				return "TAS";
			case 8:
				return "Ellipse";
			case 9:
				return "Municipal";
		}
		return "N/A";
	}
	
	public boolean HasFinalStatus()
	{
		boolean b_ret = false;
		switch(get_type())
		{
		case 1: //voice
		case 2: //sms
		case 3: //email
			if(get_sendingstatus() < 0 || get_sendingstatus()>=7)
				b_ret = true;
			break;
		case 4: //lba
		case 5: //tas
			b_ret = LBASEND.HasFinalStatus(get_sendingstatus());
			break;
		}
		return b_ret;
	}
	
	public boolean HasErrorStatus()
	{
		boolean b_ret = false;
		if(!HasFinalStatus())
			return false;
		switch(get_type())
		{
		case 1: //voice
		case 2: //sms
		case 3: //email
			if(get_sendingstatus() <= 0 || get_sendingstatus()>7)
				b_ret = true;
			break;
		case 4: //lba
		case 5: //tas
			b_ret = LBASEND.HasErrorStatus(get_sendingstatus());
			break;
		}		
		return b_ret;
	}
	
	public UDeleteStatusResponse statusMayBeDeleted()
	{
		//check if user is member of dept and that membership allows to delete (status>=3)
		boolean b_continue = false;
		for(DeptInfo di : Variables.getUserInfo().get_departments())
		{
			if(di.get_deptpk()==get_deptpk())
			{
				if(di.get_userprofile().get_status()<3)
				{
					return UDeleteStatusResponse.FAILED_USER_RESTRICTED;
				}
				else
				{
					b_continue = true; //the user have rights to delete
				}
			}
		}
		if(!b_continue)
		{
			return UDeleteStatusResponse.FAILED_USER_RESTRICTED;
		}
		return (HasFinalStatus() ? UDeleteStatusResponse.OK : UDeleteStatusResponse.FAILED_SENDING_STILL_ACTIVE);
	}
	@Override
	public String toTooltipString() {
		return "";
	}
	@Override
	public String toTooltipString(int at_column) {
		if(at_column==SearchPanelStatusList.DELETE_COLUMN)
		{
			switch(statusMayBeDeleted())
			{
			case OK:
				return Localization.l("common_delete");
			case ERROR:
				return Localization.l("main_status_delete_general_error");
			case FAILED_SENDING_STILL_ACTIVE:
				return Localization.l("main_status_delete_sending_active");
			case FAILED_USER_RESTRICTED:
				return Localization.l("main_status_delete_restricted_by_profile");
			case PROJECT_USER_RESTRICTED:
				return Localization.l("main_status_delete_restricted_by_ownership");
			}
		}
		return null;
	}

}