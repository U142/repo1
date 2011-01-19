package no.ums.pas.status;

import no.ums.pas.PAS;
import no.ums.pas.core.project.Project;

public class StatusListObject extends Object {

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
	public String toString() { return new Integer(get_refno()).toString(); }
	public String getChannel()
	{
		if(get_type()==1)
			return PAS.l("main_status_channel_voice");
		else if(get_type()==2)
			return PAS.l("main_status_channel_sms");
		else if(get_type()==4)
			return PAS.l("main_status_channel_lba");
		else if(get_type()==5)
			return PAS.l("main_status_channel_tas");
		return "Unknown";
	}

	
	public StatusListObject(int n_refno, int n_sendingtype, int n_totitem, int  n_altjmp, int n_createdate, int n_createtime, 
			 String sz_sendingname, int n_sendingstatus, int n_group, int n_type, int n_deptpk, String sz_deptid, String sz_projectpk,
			 String sz_projectname, String sz_createtimestamp, String sz_updatetimestamp)
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
		get_project().set_projectpk(sz_projectpk);
		get_project().set_projectname(sz_projectname);
		get_project().set_createtimestamp(sz_createtimestamp);
		get_project().set_updatetimestamp(sz_updatetimestamp);
		get_project().set_saved();
	}
	public StatusListObject(String [] sz_values)
	{
		this(new Integer(sz_values[0]).intValue(), new Integer(sz_values[1]).intValue(), new Integer(sz_values[2]).intValue(),
				new Integer(sz_values[3]).intValue(), new Integer(sz_values[4]).intValue(), new Integer(sz_values[5]).intValue(),
				sz_values[6], new Integer(sz_values[7]).intValue(), new Integer(sz_values[8]).intValue(), 
				new Integer(sz_values[9]).intValue(), new Integer(sz_values[10]).intValue(), sz_values[11], sz_values[12],
				sz_values[13], sz_values[14], sz_values[15]);
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
}