package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;

import java.util.ArrayList;


public class GISRecord extends Object {

    private static final Log log = UmsLog.getLogger(GISRecord.class);

	private ArrayList<InhabitantBasics> m_inhabitants;
	public ArrayList<InhabitantBasics> get_inhabitants() { return m_inhabitants; }
	public InhabitantBasics get_inhabitant(int n) { return get_inhabitants().get(n); }
	public void add_inhabitant(InhabitantBasics obj) { get_inhabitants().add(obj); }
	public Inhabitant add_inhabitant(String [] sz) {
		try {
			Inhabitant inhab = new Inhabitant(sz);
			get_inhabitants().add(inhab);
			return inhab;
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public int get_inhabitantcount() { return get_inhabitants().size(); }

	private String m_sz_municipal;
	private String m_sz_streetid;
	private String m_sz_houseno;
	private String m_sz_letter;
	private String m_sz_name1;
	private String m_sz_name2;
	private int m_lineno = -1;
	public String get_municipal() { return m_sz_municipal; }
	public String get_streetid() { return m_sz_streetid; }
	public String get_houseno() { return m_sz_houseno; }
	public String get_letter() { return m_sz_letter; }
	public String get_name1() { return m_sz_name1; }
	public String get_name2() { return m_sz_name2; }
	public int get_lineno() { return m_lineno; }
	
	public GISRecord(String [] sz_streetinfo) {
		super();
		set_streetinfo(sz_streetinfo);
		m_inhabitants = new ArrayList<InhabitantBasics>(0);
	}
	
	public GISRecord(String m_sz_municipal, String m_sz_streetid, String m_sz_houseno, String m_sz_letter, String sz_name1, String sz_name2) {
		super();
		this.m_sz_municipal = m_sz_municipal;
		this.m_sz_streetid = m_sz_streetid;
		this.m_sz_houseno = m_sz_houseno;
		this.m_sz_letter = m_sz_letter;
		this.m_sz_name1 = sz_name1;
		this.m_sz_name2 = sz_name2;
		m_inhabitants = new ArrayList<InhabitantBasics>(0);
	}
	public GISRecord(String sz_municipal, String sz_streetid, String sz_houseno, String sz_letter, String sz_name1, String sz_name2, int lineno) {
		this(sz_municipal, sz_streetid, sz_houseno, sz_letter, sz_name1, sz_name2);
		m_lineno = lineno;
	}
	
	protected void set_streetinfo(String [] sz_streetinfo) {
		m_sz_municipal	= sz_streetinfo[0];
		m_sz_streetid	= sz_streetinfo[1];
		m_sz_houseno	= sz_streetinfo[2];
		m_sz_letter		= sz_streetinfo[3];
		m_sz_name1		= sz_streetinfo[4];
		m_sz_name2		= sz_streetinfo[5];
	}
}