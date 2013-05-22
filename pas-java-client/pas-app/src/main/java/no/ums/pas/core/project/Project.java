package no.ums.pas.core.project;

import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.TooltipItem;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.localization.Localization;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.common.UDeleteStatusResponse;

import java.awt.SystemColor;
import java.util.ArrayList;

public class Project extends Object implements TooltipItem {
	
	
	private UDeleteStatusResponse may_be_deleted = UDeleteStatusResponse.ERROR;
	private int m_n_active_sendings = 0;
	private int m_n_inactive_sendings = 0;
	
	private int m_n_owner_deptpk;
	private int m_n_owner_userpk;
	public void setOwnerDeptpk(int n)
	{
		m_n_owner_deptpk = n;
	}
	public void setOwnerUserpk(int n)
	{
		m_n_owner_userpk = n;
	}
	public int getOwnerDeptpk() {
		return m_n_owner_deptpk;
	}
	public int getOwnerUserpk() {
		return m_n_owner_userpk;
	}
	private String m_sz_projectpk = "-1";
	private String m_sz_projectname;
	private String m_sz_createtimestamp;
	private String m_sz_updatetimestamp;
	private int m_n_createdate = 0;
	private int m_n_createtime = 0;
	private int m_n_updatedate = 0;
	private int m_n_updatetime = 0;
	private boolean m_b_issaved = false;
	public void setMayOrNotBeDeleted() { 
		//check if user have 3+ in status rights in owner department
		boolean b_continue = false;
		for(DeptInfo di : Variables.getUserInfo().get_departments())
		{
			if(di.get_deptpk()==getOwnerDeptpk())
			{
				if(di.get_userprofile().get_status()<3)
				{
					may_be_deleted = UDeleteStatusResponse.PROJECT_USER_RESTRICTED;
					return;
				}
				else
				{
					b_continue = true; //we know the user have rights to delete
				}
			}
		}
		if(!b_continue)
		{
			may_be_deleted = UDeleteStatusResponse.PROJECT_USER_RESTRICTED;
			return;
		}
		//check if all sendings are complete AND user have delete-access to them all
		for(StatusListObject slo : get_status_sendings())
		{
			if(slo.statusMayBeDeleted() != UDeleteStatusResponse.OK)
			{
				may_be_deleted = slo.statusMayBeDeleted();
				return;
			}
		}
		may_be_deleted = UDeleteStatusResponse.OK;
	}
	public UDeleteStatusResponse canProjectBeDeleted() { 

		return may_be_deleted; 
	}
	public int getNumberOfActiveSendings() {
		return m_n_active_sendings;
	}
	public int getNumberOfInactiveSendings() {
		return m_n_inactive_sendings; 
	}
	private void incNumberOfActiveSendings() {
		++m_n_active_sendings;
	}
	private void incNumberOfInactiveSendings() {
		++m_n_inactive_sendings;
	}
	
	private ArrayList<StatusListObject> m_status_sendings = new ArrayList<StatusListObject>();
	public ArrayList<StatusListObject> get_status_sendings() { 
		return m_status_sendings; 
	}
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return !(m_sz_projectpk != null ? !m_sz_projectpk.equals(project.m_sz_projectpk) : project.m_sz_projectpk != null);

    }

    @Override
    public int hashCode() {
        return m_sz_projectpk != null ? m_sz_projectpk.hashCode() : 0;
    }

    public boolean add_status_sending(StatusListObject obj) {

		//if(!m_status_sendings.contains(obj)) {
			m_status_sendings.add(obj); 
			if(!obj.HasFinalStatus())
				incNumberOfActiveSendings();
			else
				incNumberOfInactiveSendings();
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
	
	
	@Override
	public String toTooltipString(int at_column) {
		if(at_column == ProjectDlg.DELETE_COLUMN)
		{
			switch(canProjectBeDeleted())
			{
			case OK:
				return Localization.l("common_delete");
			case ERROR:
				return "Can not be deleted";
			case FAILED_SENDING_STILL_ACTIVE:
				return "Sending still active";
			case FAILED_USER_RESTRICTED:
				return "Restricted by profile";
			case PROJECT_USER_RESTRICTED:
				return "Restricted by ownership";
			}
		}
		return toTooltipString();
	}
	@Override
	public String toTooltipString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		//sb.append("<b><font size=3>");
		//sb.append(get_projectname());
		//sb.append("</b>");
		sb.append("<table CELLPADDING=5>");
		//headings
		//String bgcolor = "#" + Integer.toHexString(SystemColor.controlLtHighlight.getRGB()).substring(2);
		//sb.append("<tr bgcolor=");
		//sb.append(bgcolor);
		//sb.append(">");
		sb.append("<tr>");
		sb.append("<td><b>");
		sb.append(Localization.l("common_owner"));
		sb.append("</b></td><td><b>");
		sb.append(Localization.l("common_refno"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_sendingname"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_channel"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_mode"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_items"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_type"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_created"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_time"));
		sb.append("</td><td><b>");
		sb.append(Localization.l("common_sendingstatus"));
		sb.append("</td>");
		sb.append("</tr>");
		for(StatusListObject slo : get_status_sendings())
		{
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(slo.get_deptid());
			sb.append("</td><td>");
			sb.append(slo.get_refno());
			sb.append("</td><td>");
			sb.append(slo.get_sendingname());
			sb.append("</td><td>");
			sb.append(slo.getChannel());
			sb.append("</td><td>");
			sb.append(slo.getSimulationText());
			sb.append("</td><td>");
			//Localization.l("common_items"), 
			//Localization.l("common_type"), 
			sb.append(slo.get_totitem());
			sb.append("</td><td>");
			sb.append(slo.get_groupdesc());
			sb.append("</td><td>");
			sb.append(TextFormat.format_date(slo.get_createdate()));
			sb.append("</td><td>");
			sb.append(TextFormat.format_time(slo.get_createtime(),4));
			sb.append("</td>");
			if(!slo.HasFinalStatus()) //not finished nor error
			{
				sb.append("<td>");
			}
			else
			{
				sb.append((slo.HasErrorStatus() ? "<td color=red>" : "<td color=green>"));
			}
			sb.append(slo.getStatusText());
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("</html>");
		
		return sb.toString();
	}
	
}