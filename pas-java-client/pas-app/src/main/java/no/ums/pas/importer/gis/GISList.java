package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.ws.pas.UAddress;
import no.ums.ws.pas.UAddressBasics;
import no.ums.ws.pas.UGisImportResultLine;
import no.ums.ws.pas.UGisImportResultsByStreetId;

import java.util.ArrayList;
import java.util.List;

public class GISList extends ArrayList<GISRecord> {

    private static final Log log = UmsLog.getLogger(GISList.class);

	public static final long serialVersionUID = 1;
	public NavStruct m_bounds = new NavStruct();
	public NavStruct GetBounds() { return m_bounds; }
	public GISRecord get_gisrecord(int n) { return (GISRecord)get(n); }
	public void add_gisrecord(GISRecord obj) { add(obj); }
	public GISRecord addColumn(String [] sz,String importType) {
		GISRecord ret = null;
		try {
            if("Street".equals(importType)){
                ret = new GISRecord(sz);
            } else if("StreetApartment".equals(importType)) {
                ret = new GISRecordApartmentID(sz);
            }else{
                ret = new GISRecordProperty(sz);
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
	public void fill(UGisImportResultsByStreetId r)
	{
		List<UGisImportResultLine> lines = r.getList().getUGisImportResultLine();
		//if(r.getList().getUGisImportResultLine().size() > 500)
		//	return;
        for (UGisImportResultLine rl : lines) {
            GISRecord gr = new GISRecord(rl.getMunicipalid(), rl.getStreetid(), rl.getHouseno(), rl.getLetter(),
                    rl.getNamefilter1(), rl.getNamefilter2(), rl.getNLinenumber());
            List<UAddress> al = rl.getList().getList().getUAddress();
            for (UAddress a : al) {
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
	}
    public void fillProperty(UGisImportResultsByStreetId r)
    {

        List<UGisImportResultLine> lines = r.getList().getUGisImportResultLine();
        //if(r.getList().getUGisImportResultLine().size() > 500)
        //	return;
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
    }
	
	public Object clone() {
		return super.clone();
	}
	
	
	public GISList() {
		super();
		m_bounds._lbo = 360;
		m_bounds._rbo = -360;
		m_bounds._ubo = -360;
		m_bounds._bbo = 360;
	}
}

