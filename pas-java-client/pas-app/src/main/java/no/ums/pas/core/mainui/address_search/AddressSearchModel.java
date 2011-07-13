package no.ums.pas.core.mainui.address_search;

import java.util.List;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.mainui.address_search.AddressSearchCountry;

import org.jdesktop.beansbinding.AbstractBean;

public class AddressSearchModel extends AbstractBean {

    private static final Log log = UmsLog.getLogger(AddressSearchModel.class);

	private String Address;
	private String House;
	private String Postno;
	private String Place;
	private String Region;
	private Object Country;
	
	
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		String oldValue = this.Address;
		Address = address;
		update("Address", oldValue, address);
	}
	public String getHouse() {
		return House;
	}
	public void setHouse(String house) {
		String oldValue = this.House;
		House = house;
		update("House", oldValue, house);
	}
	public String getPostno() {
		return Postno;
	}
	public void setPostno(String postno) {
		String oldValue = this.Postno;
		Postno = postno;
		update("Postno", oldValue, postno);
	}
	public String getPlace() {
		return Place;
	}
	public void setPlace(String place) {
		String oldValue = this.Place;
		Place = place;
		update("Place", oldValue, place);
	}
	public String getRegion() {
		return Region;
	}
	public void setRegion(String region) {
		String oldValue = this.Region;
		Region = region;
		update("Region", oldValue, region);
	}
	public Object getCountry() {
		return Country;
	}
	public void setCountry(Object country) {
		if(country instanceof AddressSearchCountry)
		{
			Object oldValue = this.Country;
			Country = country;
			update("Country", oldValue, country);
			log.debug(country + " " + ((AddressSearchCountry)country).n_cc);
		}
	}
	
}
