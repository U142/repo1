
package no.ums.pas.importer.gis;

import java.util.ArrayList;
import java.util.List;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.ws.addressfilters.ArrayOfUAddress;
import no.ums.ws.addressfilters.UAddress;
import no.ums.ws.addressfilters.UAddressBasics;
import no.ums.ws.addressfilters.UAddressList;

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
    public void fill(UAddressList list) {
        List<UAddress> addList = list.getList().getUAddress();
        GISFilterRecord gr = new GISFilterRecord();
        for (UAddress r1 : addList) {
            gr = new GISFilterRecord(r1.getMunicipalid(), Integer.toString(r1
                    .getStreetid()), Integer.toString(r1.getHouseno()),
                    r1.getLetter());
            // lat & lon values are interchanged in order to point to correct
            // location
            Inhabitant in = new Inhabitant(r1.getKondmid(), r1.getName(),
                    r1.getAddress(), Integer.toString(r1.getHouseno()),
                    r1.getLetter(), r1.getPostno(), r1.getPostarea(),
                    Integer.toString(r1.getRegion()), r1.getBday(),
                    r1.getNumber(), r1.getMobile(), r1.getLat(), r1.getLon(),
                    r1.getGno(), r1.getBno(), r1.getFno(), r1.getSno(),
                    r1.getBedrift(), -1, r1.getStreetid(), r1.getXycode(),
                    r1.getHasfixed(), r1.getHasmobile(), r1.getHasdisabled());
            gr.add_inhabitant(in);
            if (r1.getLat() < m_bounds._lbo)
                m_bounds._lbo = r1.getLat();
            if (r1.getLat() > m_bounds._rbo)
                m_bounds._rbo = r1.getLat();
            if (r1.getLon() < m_bounds._bbo)
                m_bounds._bbo = r1.getLon();
            if (r1.getLon() > m_bounds._ubo)
                m_bounds._ubo = r1.getLon();
              }

        List<UAddressBasics> basicList = list.getListBasics()
                .getUAddressBasics();
        for (UAddressBasics a1 : basicList) {
            InhabitantBasics inhab = new InhabitantBasics(a1.getKondmid(),
                    a1.getLat(), a1.getLon(), a1.getHasfixed(),
                    a1.getHasmobile(), a1.getBedrift(), a1.getArrayindex(),
                    a1.getHasdisabled());
            gr.add_inhabitant(inhab);
            if (a1.getLat() < m_bounds._lbo)
                m_bounds._lbo = a1.getLat();
            if (a1.getLat() > m_bounds._rbo)
                m_bounds._rbo = a1.getLat();
            if (a1.getLon() < m_bounds._bbo)
                m_bounds._bbo = a1.getLon();
            if (a1.getLon() > m_bounds._ubo)
                m_bounds._ubo = a1.getLon();
        }
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

