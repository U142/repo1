
package no.ums.pas.importer.gis;

import java.util.ArrayList;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;
import no.ums.pas.maps.defines.NavStruct;

public class GISFilterList extends ArrayList<GISFilterRecord> {

    private static final Log log = UmsLog.getLogger(GISList.class);

    public static final long serialVersionUID = 1;
    public NavStruct m_bounds = new NavStruct();
    public NavStruct GetBounds() { return m_bounds; }
    public GISFilterRecord get_gisrecord(int n) { return (GISFilterRecord)get(n); }
    public void add_gisrecord(GISFilterRecord obj) { add(obj); }
    public GISFilterRecord addColumn(String [] sz,String importType) {
        GISFilterRecord ret = null;
        try {
            if("import_addr_street".equals(importType)){
                ret = new GISFilterRecord(sz);
            } else if("import_addr_CUNorway".equals(importType)) {
                ret = new GISRecordCun(sz);
            }else if("import_addr_CUSweden".equals(importType)) {
                ret = new GISRecordCus(sz);
            }else if("import_addr_VABanken".equals(importType)) {
                    ret = new GISRecordVAB(sz);
            }else{
                ret = new GISFilterRecord(sz);
            }

            add(ret);
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
        }
        return ret;
    }

    /**
     * @Todo :Change inhabitantList
     * @param r
     */
    public void fill()
    {
       // List<UGisImportResultLine> lines = r.getList().getUGisImportResultLine();
        //if(r.getList().getUGisImportResultLine().size() > 500)
        //  return;
       // for (UGisImportResultLine rl : lines) {
         //   GISFilterRecord gr = new GISFilterRecord(rl.getMunicipalid(), rl.getStreetid(), rl.getHouseno(), rl.getLetter());
        GISFilterRecord gr= new GISFilterRecord("1102", "1100", "4", "");
        
         //   List<UAddress> al = rl.getList().getList().getUAddress();
         //   for (UAddress a : al) {
                Inhabitant in = new Inhabitant("11058092", "Siam Thai Massage & Spa AS", "A.O. Anfindsens gate 4",
                        Integer.toString(4) , "", "4307", "Sandnes",
                        Integer.toString(1102), "0", "97946764", "97946764", 5.74345064163208,
                                58.86420822143555, 111, 9, 1, -1,
                        1100, "O", 1, 1, 0);
                gr.add_inhabitant(in);
                //if (5.74345064163208 < m_bounds._lbo)
                    m_bounds._lbo = 5.74345064163208;
               // if (5.74345064163208 > m_bounds._rbo)
                    m_bounds._rbo = 5.74345064163208;
             //   if (58.86420822143555 < m_bounds._bbo)
                    m_bounds._bbo = 58.86420822143555;
             //   if (58.86420822143555 > m_bounds._ubo)
                    m_bounds._ubo = 58.86420822143555;
            

            //List<UAddressBasics> bl = rl.getList().getListBasics().getUAddressBasics();
            //for (UAddressBasics a : bl) {
              /*  InhabitantBasics inhab = new InhabitantBasics(a.getKondmid(), a.getLat(), a.getLon(), a.getHasfixed(), a.getHasmobile(), a.getBedrift(), a.getArrayindex(), a.getHasdisabled());
                //inhab.init(a.getKondmid(), null, null, null, null, null, null, null, null, null, null, a.getLat(), a.getLon(), 0, 0, a.getBedrift(), 0, 0, null, a.getHasfixed(), a.getHasmobile());
                gr.add_inhabitant(inhab);
                if (a.getLat() < m_bounds._lbo)
                    m_bounds._lbo = a.getLat();
                if (a.getLat() > m_bounds._rbo)
                    m_bounds._rbo = a.getLat();
                if (a.getLon() < m_bounds._bbo)
                    m_bounds._bbo = a.getLon();
                if (a.getLon() > m_bounds._ubo)
                    m_bounds._ubo = a.getLon();*/
            


            add_gisrecord(gr);
        }
    

    /**
     * @Todo :Change inhabitantList
     * @param r
     */
   /* public void fill(List<no.ums.ws.common.parm.UGisImportResultLine> lines)
    {
        //List<UGisImportResultLine> lines = r.getList().getUGisImportResultLine();
        //if(r.getList().getUGisImportResultLine().size() > 500)
        //  return;
        for (no.ums.ws.common.parm.UGisImportResultLine rl : lines) {
            GISRecord gr = null;
            if(rl.isPropertyField())
            {
                gr = new GISRecordProperty(rl.getMunicipalid(), rl.getGnr(), rl.getBnr(), rl.getFnr(), rl.getSnr(),
                        rl.getNamefilter1(), rl.getNamefilter2(), rl.getNLinenumber());
            }
            else
            {
                gr = new GISRecord(rl.getMunicipalid(), rl.getStreetid(), rl.getHouseno(), rl.getLetter(),
                        rl.getNamefilter1(), rl.getNamefilter2(), rl.getNLinenumber());
            }

            List<no.ums.ws.common.parm.UAddress> al = rl.getList().getList().getUAddress();
            for (no.ums.ws.common.parm.UAddress a : al) {
                Inhabitant in = new Inhabitant(a.getKondmid(), a.getName(), a.getAddress(),
                        Integer.toString(a.getHouseno()), a.getLetter(), a.getPostno(), a.getPostarea(),
                        Integer.toString(a.getRegion()), a.getBday(), a.getNumber(), a.getMobile(), a.getLat(),
                        a.getLon(), a.getGno(), a.getBno(), a.getBedrift(), -1,
                        a.getStreetid(), a.getXycode(), a.getHasfixed(), a.getHasmobile(),
                        a.getHasdisabled());
                gr.add_inhabitant(in);
                if (a.getLat() < m_bounds._lbo)
                    m_bounds._lbo = a.getLat();
                if (a.getLat() > m_bounds._rbo)
                    m_bounds._rbo = a.getLat();
                if (a.getLon() < m_bounds._bbo)
                    m_bounds._bbo = a.getLon();
                if (a.getLon() > m_bounds._ubo)
                    m_bounds._ubo = a.getLon();
            }

            List<no.ums.ws.common.parm.UAddressBasics> bl = rl.getList().getListBasics().getUAddressBasics();
            for (no.ums.ws.common.parm.UAddressBasics a : bl) {
                InhabitantBasics inhab = new InhabitantBasics(a.getKondmid(), a.getLat(), a.getLon(), a.getHasfixed(), a.getHasmobile(), a.getBedrift(), a.getArrayindex(), a.getHasdisabled());
                //inhab.init(a.getKondmid(), null, null, null, null, null, null, null, null, null, null, a.getLat(), a.getLon(), 0, 0, a.getBedrift(), 0, 0, null, a.getHasfixed(), a.getHasmobile());
                gr.add_inhabitant(inhab);
                if (a.getLat() < m_bounds._lbo)
                    m_bounds._lbo = a.getLat();
                if (a.getLat() > m_bounds._rbo)
                    m_bounds._rbo = a.getLat();
                if (a.getLon() < m_bounds._bbo)
                    m_bounds._bbo = a.getLon();
                if (a.getLon() > m_bounds._ubo)
                    m_bounds._ubo = a.getLon();
            }

            add_gisrecord(gr);
        }
    }
*/
 /*   public void fillProperty(UGisImportResultsByStreetId r)
    {

        List<UGisImportResultLine> lines = r.getList().getUGisImportResultLine();
        //if(r.getList().getUGisImportResultLine().size() > 500)
        //  return;
        for (UGisImportResultLine rl : lines) {
            //String m_sz_municipal, String m_sz_gnr, String m_sz_bnr, String m_sz_fnr, String m_sz_snr, String sz_name1, String sz_name2, int lineno
            GISRecord gr = new GISRecordProperty(rl.getMunicipalid(), rl.getGnr(), rl.getBnr(), rl.getFnr(), rl.getSnr(),
                    rl.getNamefilter1(), rl.getNamefilter2(), rl.getNLinenumber());
            List<UAddress> al = rl.getList().getList().getUAddress();
            for (UAddress a : al) {
                Inhabitant in = new Inhabitant(a.getKondmid(), a.getName(), a.getAddress(),
                        Integer.toString(a.getHouseno()), a.getLetter(), a.getPostno(), a.getPostarea(),
                        Integer.toString(a.getRegion()), a.getBday(), a.getNumber(), a.getMobile(), a.getLat(),
                        a.getLon(), a.getGno(), a.getBno(),
                        a.getBedrift(), -1,
                        a.getStreetid(), a.getXycode(), a.getHasfixed(), a.getHasmobile(),
                        a.getHasdisabled());
                gr.add_inhabitant(in);
                if (a.getLat() < m_bounds._lbo)
                    m_bounds._lbo = a.getLat();
                if (a.getLat() > m_bounds._rbo)
                    m_bounds._rbo = a.getLat();
                if (a.getLon() < m_bounds._bbo)
                    m_bounds._bbo = a.getLon();
                if (a.getLon() > m_bounds._ubo)
                    m_bounds._ubo = a.getLon();
            }

            List<UAddressBasics> bl = rl.getList().getListBasics().getUAddressBasics();
            for (UAddressBasics a : bl) {
                InhabitantBasics inhab = new InhabitantBasics(a.getKondmid(), a.getLat(), a.getLon(), a.getHasfixed(), a.getHasmobile(), a.getBedrift(), a.getArrayindex(), a.getHasdisabled());
                //inhab.init(a.getKondmid(), null, null, null, null, null, null, null, null, null, null, a.getLat(), a.getLon(), 0, 0, a.getBedrift(), 0, 0, null, a.getHasfixed(), a.getHasmobile());
                gr.add_inhabitant(inhab);
                if (a.getLat() < m_bounds._lbo)
                    m_bounds._lbo = a.getLat();
                if (a.getLat() > m_bounds._rbo)
                    m_bounds._rbo = a.getLat();
                if (a.getLon() < m_bounds._bbo)
                    m_bounds._bbo = a.getLon();
                if (a.getLon() > m_bounds._ubo)
                    m_bounds._ubo = a.getLon();
            }


            add_gisrecord(gr);
        }
    }*/
    
    public Object clone() {
        return super.clone();
    }

    public void recalcBounds() {
        // Reset before calculating in case of deselect
        setDefaultBounds();

        for (int i=0; i < this.size(); i++) {
            GISFilterRecord gisrecord = this.get_gisrecord(i);
            if (gisrecord.get_inhabitants().size() > 0) {
                // All should have the same point so just need the first
                InhabitantBasics inhabitant = gisrecord.get_inhabitant(0);
                // The lat and lon has to be switched here for some reason
                if (inhabitant.get_lon() < m_bounds._lbo)
                    m_bounds._lbo = inhabitant.get_lon();
                if (inhabitant.get_lon() > m_bounds._rbo)
                    m_bounds._rbo = inhabitant.get_lon();
                if (inhabitant.get_lat() < m_bounds._bbo)
                    m_bounds._bbo = inhabitant.get_lat();
                if (inhabitant.get_lat() > m_bounds._ubo)
                    m_bounds._ubo = inhabitant.get_lat();
            }

        }
    }
    
    public GISFilterList() {
        super();
        setDefaultBounds();
    }

    private void setDefaultBounds() {
        m_bounds._lbo = 360;
        m_bounds._rbo = -360;
        m_bounds._ubo = -360;
        m_bounds._bbo = 360;
    }
}

