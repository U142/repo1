package no.ums.pas.area.voobjects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.core.ws.GISFilterList;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.map.MapPanel;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.ws.addressfilters.AddressFilterInfo;
import no.ums.ws.addressfilters.AddressType;
import no.ums.ws.addressfilters.FILTEROPERATION;
import no.ums.ws.addressfilters.UAddressList;

/**
 * @author sachinn
 */
public class AddressFilterInfoVO extends AlertVO implements Cloneable {
	
    protected String filterName;
	protected String deptId;
    protected String description;
    protected FILTEROPERATION filterOp;
	protected String creationTime;
    protected AddressType addressType;
    protected List <AddressFilterInfo> addressForFilterlist;
    protected GISFilterList gisFilterList=new GISFilterList();
    
    protected XMLGregorianCalendar creatTime;
    protected int filterId;
   // protected String filterDesc;
	
	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/*public String getFilterDesc() {
		return filterDesc;
	}

	public void setFilterDesc(String filterDesc) {
		this.filterDesc = filterDesc;
	}*/

	public UAddressList getAddresses() {
		return addresses;
	}

	public void setAddresses(UAddressList addresses) {
		this.addresses = addresses;
	}

	
	private UAddressList addresses = new UAddressList();
		
	public AddressFilterInfoVO() {
		//fjåått
	}

	public AddressFilterInfoVO(String alertpk, String parent, String name, int addresstypes, MapPanel map, Navigation nav) {
		super(alertpk,parent,name,addresstypes,map,nav);
	}
	
	public AddressFilterInfoVO(String alertpk, String parent, String name, String description, int profilepk, String schedpk, String oadc, int validity,
			int addresstypes, String timestamp, ShapeStruct shape) {
		super(alertpk, parent, name, description, profilepk, schedpk, oadc, validity,
			addresstypes, timestamp, shape);
	}
	
	public AddressFilterInfoVO(String alertpk, String parent, String name, String description, int profilepk, String schedpk, String oadc, int validity,
			int addresstypes, String timestamp, ShapeStruct shape, String[] local_language, String[] international_language, Area area, String strCBOadc, ArrayList<Object> CBMessages) {
		super(alertpk, parent, name, description, profilepk, schedpk, oadc, validity,
				addresstypes, timestamp, shape, local_language, international_language, area, strCBOadc, CBMessages);
	}
	
	public AddressFilterInfoVO(String strAlertpk, String strParent, String strName, int addresstypes) {
		super(strAlertpk, strParent, strName, addresstypes);
	}
		

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressFilterInfoVO AddressFilterInfoVO = (AddressFilterInfoVO) o;
        return !(getPk() != null ? !getPk().equals(AddressFilterInfoVO.getPk()) : AddressFilterInfoVO.getPk() != null);
    }

    @Override
    public int hashCode() {
        return getPk() != null ? getPk().hashCode() : 0;
    }


    
    public String toString() {
		return this.getFilterName();
	}

	@Override
	public boolean hasValidPk() {
		if(getPk().startsWith("a"))
			return true;
		return false;
	}

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FILTEROPERATION getfilterOp() {
		return filterOp;
	}

	public void setParmop(FILTEROPERATION filterOp) {
		this.filterOp = filterOp;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	
 
	  public void setCreatTime(XMLGregorianCalendar value) {
	        this.creatTime = value;
	    }

	public XMLGregorianCalendar getCreatTime() {
		return creatTime;
	}

	

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	public List<AddressFilterInfo> getAddressForFilterlist() {
		return addressForFilterlist;
	}

	public void setAddressForFilterlist(List<AddressFilterInfo> addressForFilterlist) {
		this.addressForFilterlist = addressForFilterlist;
	}

	public int getFilterId() {
		return filterId;
	}

	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}

	public GISFilterList getGisFilterList() {
		return gisFilterList;
	}

	public void setGisFilterList(GISFilterList gisFilterList) {
		this.gisFilterList = gisFilterList;
	}

 
}
