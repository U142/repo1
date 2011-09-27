package no.ums.pas.status;

import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Inhabitant;

public class StatusItemObject extends Inhabitant implements Comparable {
	private boolean m_b_isinited = false;
	public boolean get_isinited() { return m_b_isinited; }
	
	private int m_n_refno;
	private int m_n_item;
	private String m_sz_adrpk;
	private int m_n_date;
	private int m_n_time;
	private int m_n_status;
	private int m_n_tries;
	private int m_n_channel;
	private int m_n_pcid;
	private int m_n_seconds;
	private int m_n_changedate;
	private int m_n_changetime;
	private int m_n_latestdate;
	private int m_n_latesttime;
	//private Dimension m_dim_screencoor;
	private HouseItem m_parenthouse;
	
	public int get_refno() { return m_n_refno; }
	public int get_item()  { return m_n_item; }
	public String get_adrpk() { return m_sz_adrpk; }
	public int get_date() { return m_n_date; }
	public int get_time() { return m_n_time; }
	public int get_status() { return m_n_status; }
	public int get_tries() { return m_n_tries; }
	public int get_channel() { return m_n_channel; }
	public int get_pcid() { return m_n_pcid; }
	public int get_seconds() { return m_n_seconds; }
	public int get_changedate() { return m_n_changedate; }
	public int get_changetime() { return m_n_changetime; }
	public int get_latestdate() { return m_n_latestdate; }
	public int get_latesttime() { return m_n_latesttime; }
	public void set_parenthouse(HouseItem house) { m_parenthouse = house; }
	public HouseItem get_parenthouse() { return m_parenthouse; }
	public String toString() { return get_adrname(); }
	public int compareTo(Object obj) {
		//return ((Inhabitant)obj).get_adrname().compareTo(get_adrname());
		//return this.compareTo((StatusItemObject)obj);
		return get_adrname().compareTo(((StatusItemObject)obj).get_adrname());
	}
	protected static String EXPORT_HEADING;
	protected static String EXPORT_SEPARATOR;
	public static long EXPORT_FIELDS;
	public static void SetExportFields(long FIELDS, String sz_separator, boolean b_html_format) { 
		EXPORT_FIELDS = FIELDS; 
		EXPORT_SEPARATOR = sz_separator;
		createHeading(b_html_format); 
	}
	public static String getExportHeading() { return EXPORT_HEADING; }
	public static final long ADR_EXPORT_FIELD_REFNO	= 1 << 0;
	public static final long ADR_EXPORT_FIELD_ITEM	= 1 << 1;
	public static final long ADR_EXPORT_FIELD_ADRNAME	= 1 << 2;
	public static final long ADR_EXPORT_FIELD_BDATE	= 1 << 3;
	public static final long ADR_EXPORT_FIELD_POSTADDR= 1 << 4;
	public static final long ADR_EXPORT_FIELD_HOUSE	= 1 << 5;
	public static final long ADR_EXPORT_FIELD_POSTNO	= 1 << 6;
	public static final long ADR_EXPORT_FIELD_AREA	= 1 << 7;
	public static final long ADR_EXPORT_FIELD_PHONE	= 1 << 8;
	public static final long ADR_EXPORT_FIELD_MOBILE	= 1 << 9;
	public static final long ADR_EXPORT_FIELD_DATE	= 1 << 10;
	public static final long ADR_EXPORT_FIELD_TIME	= 1 << 11;
	public static final long ADR_EXPORT_FIELD_LATESTDATE = 1 << 12;
	public static final long ADR_EXPORT_FIELD_LATESTTIME = 1 << 13;
	public static final long ADR_EXPORT_FIELD_TRIES	= 1 << 14;
	public static final long ADR_EXPORT_FIELD_STATUS	= 1 << 15;
	public static final long ADR_EXPORT_FIELD_STATUSNAME = 1 << 16;
	public static final long ADR_EXPORT_FIELD_SECONDS = 1 << 17;
	public static final long ADR_EXPORT_FIELD_LAT 	= 1 << 18;
	public static final long ADR_EXPORT_FIELD_LON		= 1 << 19;
	public static final long ADR_EXPORT_FIELD_PCID	= 1 << 20;
	public static final long ADR_EXPORT_FIELD_CHANNEL = 1 << 21;


    public String exportString(StatusCodeList list)
	{
		String s_ret = getFields(EXPORT_FIELDS, list, false);
		/*s_ret = get_refno() + "	" + get_item() + "	" + get_adrname() + "	" + get_birthday_formatted() + 
				"	" + get_postaddr() + "	" + get_no() + "	" + get_postno() + "	" + get_postarea() + 
				"	" + get_number() + "	" + get_mobile() + 
				"	" + get_date() + "	" + get_time() + "	" + get_latestdate() + "	" + get_latesttime() +
				"	" + get_tries() + "	" + get_status() + "	" + list.get_statusname(get_status()) + "	" + get_seconds() + 
				"	" + get_lat() + "	" + get_lon() + "	" + get_pcid() + "	" + get_channel();*/
		
		return s_ret;
	}
	public String exportStringHtml(StatusCodeList list)
	{
		String s_ret = "<TR>";
		s_ret += getFields(EXPORT_FIELDS, list, true);
		s_ret += "</TR>";
		return s_ret;
	}
	
	protected String getFields(long n, StatusCodeList list, boolean b_htmlformat)
	{
		String sz_td = "";
		String sz_end = "";
		if(b_htmlformat)
		{
			sz_td = "<td>";
			sz_end = "</td>";
		} else
		{
			sz_end = EXPORT_SEPARATOR;
		}
		String ret = "";
		if((n & ADR_EXPORT_FIELD_REFNO) != 0)
			ret += sz_td + get_refno() + sz_end;
		if((n & ADR_EXPORT_FIELD_ITEM) != 0)
			ret += sz_td + get_item() + sz_end;
		if((n & ADR_EXPORT_FIELD_ADRNAME) != 0)
			ret += sz_td + get_adrname() + sz_end;
		if((n & ADR_EXPORT_FIELD_BDATE) != 0)
			ret += sz_td + get_birthday_formatted() + sz_end;
		if((n & ADR_EXPORT_FIELD_POSTADDR) != 0)
			ret += sz_td + get_postaddr() + sz_end;
		if((n & ADR_EXPORT_FIELD_HOUSE) != 0)
			ret += sz_td + get_no() + sz_end;
		if((n & ADR_EXPORT_FIELD_POSTNO) != 0)
			ret += sz_td + get_postno() + sz_end;
		if((n & ADR_EXPORT_FIELD_AREA) != 0)
			ret += sz_td + get_postarea() + sz_end;
		if((n & ADR_EXPORT_FIELD_PHONE) != 0)
			ret += sz_td + get_number() + sz_end;
		if((n & ADR_EXPORT_FIELD_MOBILE) != 0)
			ret += sz_td + get_mobile() + sz_end;
		if((n & ADR_EXPORT_FIELD_DATE) != 0)
			ret += sz_td + get_date() + sz_end;
		if((n & ADR_EXPORT_FIELD_TIME) != 0)
			ret += sz_td + get_time() + sz_end;
		if((n & ADR_EXPORT_FIELD_LATESTDATE) != 0)
			ret += sz_td + get_latestdate() + sz_end;
		if((n & ADR_EXPORT_FIELD_LATESTTIME) != 0)
			ret += sz_td + get_latesttime() + sz_end;
		if((n & ADR_EXPORT_FIELD_TRIES) != 0)
			ret += sz_td + get_tries() + sz_end;
		if((n & ADR_EXPORT_FIELD_STATUS) != 0)
			ret += sz_td + get_status() + sz_end;
		if((n & ADR_EXPORT_FIELD_STATUSNAME) != 0)
			ret += sz_td + list.get_statusname(get_status()) + sz_end;
		if((n & ADR_EXPORT_FIELD_SECONDS) != 0)
			ret += sz_td + get_seconds() + sz_end;
		if((n & ADR_EXPORT_FIELD_LAT) != 0)
			ret += sz_td + get_lat() + sz_end;
		if((n & ADR_EXPORT_FIELD_LON) != 0)
			ret += sz_td + get_lon() + sz_end;
		if((n & ADR_EXPORT_FIELD_PCID) != 0)
			ret += sz_td + get_pcid() + sz_end;
		if((n & ADR_EXPORT_FIELD_CHANNEL) != 0)
			ret += sz_td + get_channel() + sz_end;
		ret += "\r\n";
		return ret;
	}
	
	protected static void createHeading(boolean b_html_format)
	{
		String sz_td = "";
		String sz_end = "";
		if(b_html_format)
		{
			sz_td = "<td><b>";
			sz_end = "</b></td>";
		}
		else
			sz_end = EXPORT_SEPARATOR;
		
		EXPORT_HEADING = "";
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_REFNO) != 0)
			EXPORT_HEADING += sz_td + "Refno" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_ITEM) != 0)
			EXPORT_HEADING += sz_td + "Item" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_ADRNAME) != 0)
			EXPORT_HEADING += sz_td + "Name" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_BDATE) != 0)
			EXPORT_HEADING += sz_td + "Birth date" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_POSTADDR) != 0)
			EXPORT_HEADING += sz_td + "Address" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_HOUSE) != 0)
			EXPORT_HEADING += sz_td + "House" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_POSTNO) != 0)
			EXPORT_HEADING += sz_td + "Postno" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_AREA) != 0)
			EXPORT_HEADING += sz_td + "Area" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_PHONE) != 0)
			EXPORT_HEADING += sz_td + "Phone" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_MOBILE) != 0)
			EXPORT_HEADING += sz_td + "Mobile" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_DATE) != 0)
			EXPORT_HEADING += sz_td + "Date" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_TIME) != 0)
			EXPORT_HEADING += sz_td + "Time" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_LATESTDATE) != 0)
			EXPORT_HEADING += sz_td + "Latest Date" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_LATESTTIME) != 0)
			EXPORT_HEADING += sz_td + "Latest Time" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_TRIES) != 0)
			EXPORT_HEADING += sz_td + "Tries" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_STATUS) != 0)
			EXPORT_HEADING += sz_td + "Status Code" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_STATUSNAME) != 0)
			EXPORT_HEADING += sz_td + "Status" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_SECONDS) != 0)
			EXPORT_HEADING += sz_td + "Duration" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_LAT) != 0)
			EXPORT_HEADING += sz_td + "Latitude" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_LON) != 0)
			EXPORT_HEADING += sz_td + "Longitude" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_PCID) != 0)
			EXPORT_HEADING += sz_td + "PC ID" + sz_end;
		if((EXPORT_FIELDS & ADR_EXPORT_FIELD_CHANNEL) != 0)
			EXPORT_HEADING += sz_td + "Channel" + sz_end;
		EXPORT_HEADING += "\r\n";
	}
	
	public StatusItemObject() { 
		super();
		m_b_isinited = false; 
	}
	public void update(StatusItemObject obj) {
		m_n_refno	= obj.get_refno();
		m_sz_adrpk	= obj.m_sz_adrpk;
		m_n_date	= obj.m_n_date;
		m_n_time	= obj.m_n_time;
		m_n_status	= obj.m_n_status;
		m_n_tries	= obj.m_n_tries;
		m_n_channel	= obj.m_n_channel;
		m_n_pcid	= obj.m_n_pcid;
		m_n_seconds	= obj.m_n_seconds;
		m_n_changedate= obj.m_n_changedate;
		m_n_changetime= obj.m_n_changetime;
		m_n_latestdate= obj.m_n_latestdate;
		m_n_latesttime= obj.m_n_latesttime;
		m_n_vulnerable = obj.m_n_vulnerable;
	}	
	
	public StatusItemObject(int n_refno, int n_item, String sz_adrpk, double f_lon, double f_lat, String sz_adrname, String sz_postaddr,
		     String sz_postno, String sz_postarea, int n_date, int n_time, int n_status, String sz_number, int n_tries,
		     int n_channel, int n_pcid, int n_seconds, int n_changedate, int n_changetime, int n_latestdate, int n_latesttime,
		     int n_vulnerable)
	{
		super(sz_adrname, sz_postaddr, sz_postno, sz_postarea, sz_number, "", f_lon, f_lat, 0, -1, 0, "a", 1, 1, n_vulnerable);
		m_n_refno	= n_refno;
		m_n_item	= n_item;
		m_sz_adrpk	= sz_adrpk;
		//m_f_lon		= f_lon;
		//m_f_lat		= f_lat;
		m_n_date	= n_date;
		m_n_time	= n_time;
		m_n_status	= n_status;
		m_n_tries	= n_tries;
		m_n_channel	= n_channel;
		m_n_pcid	= n_pcid;
		m_n_seconds	= n_seconds;
		m_n_changedate	= n_changedate;
		m_n_changetime	= n_changetime;
		m_n_latestdate	= n_latestdate;
		m_n_latesttime	= n_latesttime;
		m_b_isinited = true;
	}
	public StatusItemObject(String n_refno, String n_item, String sz_adrpk, String f_lon, String f_lat, String sz_adrname, String sz_postaddr,
		     String sz_postno, String sz_postarea, String n_date, String n_time, String n_status, String sz_number, String n_tries,
		     String n_channel, String n_pcid, String n_seconds, String n_changedate, String n_changetime, String n_latestdate, String n_latesttime,
		     String n_vulnerable)
	{
		super(sz_adrname, sz_postaddr, sz_postno, sz_postarea, sz_number, "", 
			  new Double(f_lon).doubleValue(), new Double(f_lat).doubleValue(), 0, -1, 0, "a", 1, 1, new Integer(n_vulnerable));		
		m_n_refno	= new Integer(n_refno).intValue();
		m_n_item	= new Integer(n_item).intValue();
		m_sz_adrpk	= sz_adrpk;
		//m_f_lon		= new Double(f_lon).doubleValue();
		//m_f_lat		= new Double(f_lat).doubleValue();
		m_n_date	= new Integer(n_date).intValue();
		m_n_time	= new Integer(n_time).intValue();
		m_n_status	= new Integer(n_status).intValue();
		m_n_tries	= new Integer(n_tries).intValue();
		m_n_channel	= new Integer(n_channel).intValue();
		m_n_pcid	= new Integer(n_pcid).intValue();
		m_n_seconds	= new Integer(n_seconds).intValue();
		m_n_changedate	= new Integer(n_changedate).intValue();
		m_n_changetime	= new Integer(n_changetime).intValue();
		m_n_latestdate	= new Integer(n_latestdate).intValue();
		m_n_latesttime	= new Integer(n_latesttime).intValue();
		m_b_isinited = true;
	}	
	public StatusItemObject(String [] sz_values)
	{
		super(sz_values[5], sz_values[6], sz_values[7], sz_values[8], sz_values[12], "" /*mobile*/, 
			  new Double(sz_values[3]).doubleValue(), new Double(sz_values[4]).doubleValue(), 0, -1, 0, "S", 1, 1, sz_values[21]!=null?Integer.parseInt(sz_values[21]):0);
		m_n_refno	= new Integer(sz_values[0]).intValue();
		m_n_item	= new Integer(sz_values[1]).intValue();
		m_sz_adrpk	= sz_values[2];
		//m_f_lon		= new Double(sz_values[3]).doubleValue();
		//m_f_lat		= new Double(sz_values[4]).doubleValue();
		m_n_date	= new Integer(sz_values[9]).intValue();
		m_n_time	= new Integer(sz_values[10]).intValue();
		m_n_status	= new Integer(sz_values[11]).intValue();
		m_n_tries	= new Integer(sz_values[13]).intValue();
		m_n_channel	= new Integer(sz_values[14]).intValue();
		m_n_pcid	= new Integer(sz_values[15]).intValue();
		m_n_seconds	= new Integer(sz_values[16]).intValue();
		m_n_changedate	= new Integer(sz_values[17]).intValue();
		m_n_changetime	= new Integer(sz_values[18]).intValue();
		m_n_latestdate	= new Integer(sz_values[19]).intValue();
		m_n_latesttime	= new Integer(sz_values[20]).intValue();
		m_b_isinited = true;
	}		
		
}
