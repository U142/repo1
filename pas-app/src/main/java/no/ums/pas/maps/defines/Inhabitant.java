package no.ums.pas.maps.defines;

import no.ums.pas.send.SendController;

/*
String[] sz_itemattr = { "dmid", "name", "adr", "no", "lt", "post", "area", "reg", "bday", "pno", 
						 "lon", "lat", "gno", "bno" };
**/
public class Inhabitant extends InhabitantBasics implements Cloneable {
	
	@Override
	public Inhabitant clone()
	{
		try
		{
			return (Inhabitant)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
		/*Inhabitant i = new Inhabitant();
		i.set_adrname(this.get_adrname());
		i.set_adrtype(this.get_adrtype());
		i.set_birthday(this.get_birthday());
		i.set_birthday_formatted(this.get_birthday_formatted());
		i.set_bnumber(this.get_bnumber());
		i.set_deptpk(this.get_deptpk());
		i.set_dim_screencoor(this.get_dim_screencoor());
		i.set_gnumber(this.get_gnumber());
		i.set_hitpercent(this.get_hitpercent());
		i.set_included(this.get_included());
		i.set_kondmid(this.get_kondmid());
		i.set_lat(this.get_lat());
		i.set_letter(this.get_letter());
		i.set_lon(this.get_lon());
		i.set_mobile(this.get_mobile());
		i.set_namesearch_col(this.get_namesearch_col());
		i.set_no(this.get_no());
		i.set_number(this.get_number());
		i.set_parenthouse(this.get_parenthouse());
		i.set_postaddr(this.get_postaddr());
		i.set_postarea(this.get_postarea());
		i.set_postno(this.get_postno());
		i.set_quality(this.get_quality());
		i.set_region(this.get_region());
		i.set_streetid(this.get_streetid());
		return i;*/
	}
	
	public Inhabitant() {
		super();
	}
	public Inhabitant(String [] values) {
		init(values[0], values[1], values[2], values[3], values[4], values[5], values[6],
			   values[7], values[8], values[9], values[16], (values[10].equals("") ? 0.0 : new Double(values[10]).doubleValue()), 
			   (values[11].equals("") ? 0.0 : new Double(values[11]).doubleValue()), new Integer(values[12]).intValue(),
			   new Integer(values[13]).intValue(), new Integer(values[14]).intValue(), new Integer(values[15]).intValue(), 
			   new Integer(values[17]).intValue(), new String(values[18]), new Integer(values[19]).intValue(), new Integer(values[20]).intValue());
	}
	public Inhabitant(String sz_adrname, String sz_postaddr, String sz_postno, String sz_postarea, 
		   String sz_number, String sz_mobile, double f_lon, double f_lat, int n_adrtype, int n_deptpk, 
		   int n_streetid, String sz_quality, int n_hasfixed, int n_hasmobile) {
		init("", sz_adrname, sz_postaddr, "", "", sz_postno, sz_postarea, "", "", sz_number, sz_mobile, f_lon, f_lat, 0, 0, 
				n_adrtype, n_deptpk, n_streetid, sz_quality, n_hasfixed, n_hasmobile);
	}
	public Inhabitant(String sz_kondmid, String sz_adrname, String sz_postaddr, String sz_no, String sz_letter,
		   String sz_postno, String sz_postarea, String sz_region, String sz_birthday, String sz_number, String sz_mobile, 
		   double f_lon, double f_lat, int n_gnumber, int n_bnumber, int n_adrtype, int n_deptpk, int n_streetid, 
		   String sz_quality, int n_hasfixed, int n_hasmobile) {
	init(sz_kondmid, sz_adrname, sz_postaddr, sz_no, sz_letter, sz_postno, sz_postarea, 
		 sz_region, sz_birthday, sz_number, sz_mobile, f_lon, f_lat, n_gnumber, n_bnumber, n_adrtype, n_deptpk, 
		 n_streetid, sz_quality, n_hasfixed, n_hasmobile);
	}
	public String toString() {
		return get_adrname();
	}
	public Inhabitant(double f_lon, double f_lat) {
		init("", "", "", "", "", "", "", "", "", "", "", f_lon, f_lat, 0, 0, 0, 0, 0, "a", 1, 1);
	}
	public void init(String sz_kondmid, String sz_adrname, String sz_postaddr, String sz_no, String sz_letter,
		   String sz_postno, String sz_postarea, String sz_region, String sz_birthday, String sz_number, String sz_mobile, 
		   double f_lon, double f_lat, int n_gnumber, int n_bnumber, int n_adrtype, int n_deptpk, int n_streetid, 
		   String sz_quality, int n_hasfixed, int n_hasmobile) {
		m_sz_kondmid	= sz_kondmid;
		if(sz_adrname!=null)
			m_sz_adrname	= sz_adrname;
		else
			m_sz_adrname	= "";
		m_sz_postaddr	= sz_postaddr;
		m_sz_no			= sz_no;
		m_sz_letter		= sz_letter;
		m_sz_postno		= sz_postno;
		m_sz_postarea	= sz_postarea;
		m_sz_region		= sz_region;
		m_sz_birthday	= sz_birthday;
		m_sz_number		= sz_number;
		m_sz_mobile		= sz_mobile;
		m_f_lon			= f_lon;
		m_f_lat			= f_lat;
		m_n_gnumber		= n_gnumber;
		m_n_bnumber		= n_bnumber;
		m_n_streetid	= n_streetid;
		m_n_hasfixed	= n_hasfixed;
		m_n_hasmobile	= n_hasmobile;
		if(n_adrtype==10 || n_adrtype==12) //manual address (private/mobile)
			m_n_inhabitant_type = INHABITANT_PRIVATE;
		else if(n_adrtype==11) //manual address (company)
			m_n_inhabitant_type = INHABITANT_COMPANY;
		else
			m_n_inhabitant_type = n_adrtype;
		
		if(sz_quality==null)
			sz_quality = "";
		if(sz_quality.length() > 0) {
			m_c_quality		= sz_quality.charAt(0);
		} else {
			m_c_quality = 'a';
		}
		format_birthday();
		/*if(n_adrtype==0) n_adrtype = SendController.ADR_TYPES_PRIVATE_;
		else if(n_adrtype==1) n_adrtype = SendController.ADR_TYPES_COMPANY_;
		//else if(n_adrtype==2) n_adrtype = Controller.ADR_TYPES_MOBILE_;
		//else if(n_adrtype==3) n_adrtype = Controller.ADR_TYPES_FAX_;
		else if(n_adrtype==10) n_adrtype = SendController.ADR_TYPES_PRIVATE_MOVED_; //a user moved it
		else if(n_adrtype==11) n_adrtype = SendController.ADR_TYPES_COMPANY_MOVED_; //a user moved it
		else if(n_adrtype==12) n_adrtype = SendController.ADR_TYPES_MOBILE_MOVED_; //a user moved it
		
		m_n_adrtype		= n_adrtype;*/
		m_n_adrtype = 0;
		if(n_adrtype==0 && n_hasfixed==1) m_n_adrtype |= SendController.SENDTO_FIXED_PRIVATE;
		if(n_adrtype==0 && n_hasmobile==1) m_n_adrtype |= SendController.SENDTO_MOBILE_PRIVATE;
		if(n_adrtype==1 && n_hasfixed==1) m_n_adrtype |= SendController.SENDTO_FIXED_COMPANY;
		if(n_adrtype==1 && n_hasmobile==1) m_n_adrtype |= SendController.SENDTO_MOBILE_COMPANY;
		if(n_adrtype==0 && (n_hasfixed==0 && n_hasmobile==0)) m_n_adrtype = SendController.SENDTO_NOPHONE_PRIVATE;
		if(n_adrtype==1 && (n_hasfixed==0 && n_hasmobile==0)) m_n_adrtype = SendController.SENDTO_NOPHONE_COMPANY;
		if(n_adrtype==10 || n_adrtype==12) m_n_adrtype = SendController.SENDTO_MOVED_RECIPIENT_PRIVATE;
		if(n_adrtype==11) m_n_adrtype = SendController.SENDTO_MOVED_RECIPIENT_COMPANY;
		
		m_n_deptpk		= n_deptpk;
		if(m_n_deptpk >= 0)
			set_useredited();
	}
	
	protected void format_birthday() {
		if(m_sz_birthday != null) {
			if(m_sz_birthday.length() == 8) {
				m_sz_birthday_formatted = m_sz_birthday.substring(6, 8) + "." + m_sz_birthday.substring(4, 6) + "." +  m_sz_birthday.substring(0, 4);
			} else {
				m_sz_birthday_formatted = "";
			}
		}
	}
	public void set_birthday_formatted(String s) {
		if(s.length() == 10) {
			m_sz_birthday = s.substring(6,10) + s.substring(3,5) + s.substring(0,2);
			m_sz_birthday_formatted = s;
		} else {
			m_sz_birthday = "";
			m_sz_birthday_formatted = "";
		}
	}
	
	
	
	//private String m_sz_kondmid;
	private String m_sz_adrname;
	private String m_sz_postaddr;
	private String m_sz_no = "0";
	private String m_sz_letter;
	private String m_sz_postno;
	private String m_sz_postarea;
	private String m_sz_region = "0";
	private String m_sz_birthday = "0";
	private String m_sz_birthday_formatted;
	private String m_sz_number;
	private String m_sz_mobile;
	private int m_n_deptpk;
	private int m_n_gnumber;
	private int m_n_bnumber;
	//private int m_n_inhabitant_type; //private or company
	private int m_n_streetid = 0;
	private char m_c_quality = 'a';
	//private int m_n_hasfixed = 0;
	//private int m_n_hasmobile = 0;
	private int m_n_namesearch_col = -1; //FOR GIS-import
	private double m_n_hit_percent = 0; //For GIS-import
	public void set_namesearch_col(int n) { m_n_namesearch_col = n; }
	public int get_namesearch_col() { return m_n_namesearch_col; }
	public void set_hitpercent(double n) { m_n_hit_percent = n; }
	public double get_hitpercent() { return m_n_hit_percent; }
	
	
	
	public String get_adrname() { return m_sz_adrname; }
	public String get_postaddr() { return m_sz_postaddr; }
	public String get_no() { return m_sz_no; }
	public String get_letter() { return m_sz_letter; }
	public String get_postno() { return m_sz_postno; }
	public String get_postarea() { return m_sz_postarea; }
	public String get_region() { return m_sz_region; }
	public String get_birthday() { return m_sz_birthday; }
	public String get_number() { return m_sz_number; }
	public String get_mobile() { return m_sz_mobile; } 
	public int get_gnumber() { return m_n_gnumber; }
	public int get_bnumber() { return m_n_bnumber; }
	public int get_deptpk() { return m_n_deptpk; }
	public int get_streetid() { return m_n_streetid; }
	public String get_birthday_formatted() { return m_sz_birthday_formatted; }
	public char get_quality() { return m_c_quality; }
	
	public void set_adrname(String s) { m_sz_adrname = s; }
	public void set_postaddr(String s) { m_sz_postaddr = s; }
	public void set_no(String s) {
		//check for letter
		int n_letterstart = not_numeric_at(s);
		if(n_letterstart >= 0) {
			m_sz_no = s.substring(0, n_letterstart);
			set_letter(s.substring(n_letterstart, s.length()));
		}
		else
			m_sz_no = s;
	}
	protected String m_sz_numbers = "0123456789";
	protected int not_numeric_at(String s) {
		for(int i=0; i < s.length(); i++) {
			if(m_sz_numbers.indexOf(s.charAt(i))==-1)
				return i;
		}
		return -1;
	}
	public void set_letter(String s) { m_sz_letter = s; }
	public void set_postno(String s) { m_sz_postno = s; }
	public void set_postarea(String s) { m_sz_postarea = s; }
	public void set_region(String s) { m_sz_region = s; }
	public void set_birthday(String s) { m_sz_birthday = s; format_birthday(); }
	public void set_number(String s) { m_sz_number = s; }
	public void set_mobile(String s) { m_sz_mobile = s; }
	public void set_gnumber(int n) { m_n_gnumber = n; }
	public void set_bnumber(int n) { m_n_bnumber = n; }
	public void set_adrtype(int n) { m_n_adrtype = n; }
	public void set_deptpk(int n) { m_n_deptpk = n; }
	public void set_streetid(int n) { m_n_streetid = n; }
	public void set_quality(char c) { m_c_quality = c; }
	
	//double m_f_lon = 0, m_f_lat = 0;
	
	

}
