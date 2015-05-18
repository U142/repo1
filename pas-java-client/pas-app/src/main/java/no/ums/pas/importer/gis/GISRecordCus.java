
//Fastighetsnyckel



package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;

import java.util.ArrayList;


public class GISRecordCus extends GISFilterRecord {

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
  

    //Property stuff
    private String m_sz_fastighetsnyckel;
    private int m_lineno = -1;
    public String get_municipal() { return m_sz_municipal; }
   
    public int get_lineno() { return m_lineno; }

    public String getM_sz_fastighetsnyckel() {
        return m_sz_fastighetsnyckel;
    }
   
    public GISRecordCus() {
        super();
        m_inhabitants = new ArrayList<InhabitantBasics>(0);
    }


    public GISRecordCus(String[] sz_info) {
        super();
        set_propertyinfo(sz_info);
        m_inhabitants = new ArrayList<InhabitantBasics>(0);
    }


    public GISRecordCus(String m_sz_municipal, String m_sz_fastighetsnyckel) {
        super();
        this.m_sz_municipal = m_sz_municipal;
        this.m_sz_fastighetsnyckel = m_sz_fastighetsnyckel;
       
      
        m_inhabitants = new ArrayList<InhabitantBasics>(0);
    }
    public GISRecordCus(String m_sz_municipal, String m_sz_fastighetsnyckel, int lineno) {
        this(m_sz_municipal,m_sz_fastighetsnyckel);
        m_lineno = lineno;
    }


    protected void set_propertyinfo(String [] sz_propertyinfo) {
        m_sz_municipal  = sz_propertyinfo[0];
        m_sz_fastighetsnyckel =  sz_propertyinfo[1];
       
       
    }

}