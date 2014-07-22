package no.ums.pas.area.voobjects;

import java.util.ArrayList;

import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.map.MapPanel;
import no.ums.pas.parm.voobjects.AlertVO;

/**
 * @author sachinn
 */
public class AreaVO extends AlertVO implements Cloneable{
		
	public AreaVO() {
		//fjåått
	}

	public AreaVO(String alertpk, String parent, String name, int addresstypes, MapPanel map, Navigation nav) {
		super(alertpk,parent,name,addresstypes,map,nav);
	}
	
	public AreaVO(String alertpk, String parent, String name, String description, int profilepk, String schedpk, String oadc, int validity,
			int addresstypes, String timestamp, ShapeStruct shape) {
		super(alertpk, parent, name, description, profilepk, schedpk, oadc, validity,
			addresstypes, timestamp, shape);
	}
	
	public AreaVO(String alertpk, String parent, String name, String description, int profilepk, String schedpk, String oadc, int validity,
			int addresstypes, String timestamp, ShapeStruct shape, String[] local_language, String[] international_language, Area area, String strCBOadc, ArrayList<Object> CBMessages) {
		super(alertpk, parent, name, description, profilepk, schedpk, oadc, validity,
				addresstypes, timestamp, shape, local_language, international_language, area, strCBOadc, CBMessages);
	}
	
	public AreaVO(String strAlertpk, String strParent, String strName, int addresstypes) {
		super(strAlertpk, strParent, strName, addresstypes);
	}
		

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AreaVO areaVO = (AreaVO) o;
        return !(getPk() != null ? !getPk().equals(areaVO.getPk()) : areaVO.getPk() != null);
    }

    @Override
    public int hashCode() {
        return getPk() != null ? getPk().hashCode() : 0;
    }

    /*public String toString() {
		String ret;
		if(getLocked() == 1) {
			ret = strName + String.format(" [%s]", Localization.l("main_parm_alert_lba_locked"));
		}
		else
			ret = strName;
		if(!hasValidAreaIDFromCellVision())
		{
			if(sz_lba_areaid!=null)
			{
				if(sz_lba_areaid.equals("-2"))
					ret += String.format(" [%s]", Localization.l("main_parm_alert_lba_error_text"));
				else if(sz_lba_areaid.equals("0"))
					ret += " [" + Localization.l("main_parm_alert_lba_waiting_text") + "]";
					
				b_size_dirty = true;
			}			
		}
		return ret;
	}*/
    
    public String toString() {
		return this.getName();
	}

	@Override
	public boolean hasValidPk() {
		if(getPk().startsWith("a"))
			return true;
		return false;
	}
}
