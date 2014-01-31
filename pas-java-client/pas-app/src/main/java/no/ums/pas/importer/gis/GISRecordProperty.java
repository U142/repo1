package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;

import java.util.ArrayList;


public class GISRecordProperty extends GISRecord {

    private static final Log log = UmsLog.getLogger(GISRecordProperty.class);

    private ArrayList<InhabitantBasics> m_inhabitants;
    public ArrayList<InhabitantBasics> get_inhabitants() {
        return m_inhabitants;
    }
    public InhabitantBasics get_inhabitant(int n) {
        return get_inhabitants().get(n);
    }
    public void add_inhabitant(InhabitantBasics obj) {
        get_inhabitants().add(obj);
    }
    public int get_inhabitantcount() {
        return get_inhabitants().size();
    }

    private String m_sz_municipal;
    private String m_sz_name1;
    private String m_sz_name2;

    //Property stuff
    private String m_sz_gnr;
    private String m_sz_bnr;
    private String m_sz_fnr;
    private String m_sz_snr;

    private int m_lineno = -1;
    public String get_municipal() { return m_sz_municipal; }
    public String get_name1() { return m_sz_name1; }
    public String get_name2() { return m_sz_name2; }
    public int get_lineno() { return m_lineno; }



    public String getM_sz_gnr() {
        return m_sz_gnr;
    }

    public String getM_sz_bnr() {
        return m_sz_bnr;
    }

    public String getM_sz_fnr() {
        return m_sz_fnr;
    }

    public String getM_sz_snr() {
        return m_sz_snr;
    }

    public GISRecordProperty() {
        super();
        m_inhabitants = new ArrayList<InhabitantBasics>(0);
    }


    public GISRecordProperty(String[] sz_info) {
        super();
        set_propertyinfo(sz_info);
        m_inhabitants = new ArrayList<InhabitantBasics>(0);
    }


    public GISRecordProperty(String m_sz_municipal, String m_sz_gnr, String m_sz_bnr, String m_sz_fnr, String m_sz_snr, String sz_name1, String sz_name2) {
        super();
        this.m_sz_municipal = m_sz_municipal;
        this.m_sz_gnr = m_sz_gnr;
        this.m_sz_bnr = m_sz_bnr;
        this.m_sz_fnr = m_sz_fnr;
        this.m_sz_snr = m_sz_snr;
        this.m_sz_name1 = sz_name1;
        this.m_sz_name2 = sz_name2;
        m_inhabitants = new ArrayList<InhabitantBasics>(0);
    }
    public GISRecordProperty(String m_sz_municipal, String m_sz_gnr, String m_sz_bnr, String m_sz_fnr, String m_sz_snr, String sz_name1, String sz_name2, int lineno) {
        this(m_sz_municipal,m_sz_gnr,m_sz_bnr,m_sz_fnr,m_sz_snr,sz_name1, sz_name2);
        m_lineno = lineno;
    }


    protected void set_propertyinfo(String [] sz_propertyinfo) {
        m_sz_municipal	= sz_propertyinfo[0];
        m_sz_gnr        =  sz_propertyinfo[1];
        m_sz_bnr        = sz_propertyinfo[2];
        m_sz_fnr        = sz_propertyinfo[3];
        m_sz_snr        = sz_propertyinfo[4];
        m_sz_name1		= sz_propertyinfo[5];
        m_sz_name2		= sz_propertyinfo[6];
    }

}