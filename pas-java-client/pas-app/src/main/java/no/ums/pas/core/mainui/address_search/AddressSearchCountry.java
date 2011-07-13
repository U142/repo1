package no.ums.pas.core.mainui.address_search;


public class AddressSearchCountry
{
	public AddressSearchCountry(int cc, String country)
	{
		n_cc = cc;
		sz_country = country;
	}
	public int n_cc;
	public String sz_country;
	@Override
	public int hashCode() {
		return Integer.valueOf(n_cc).hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return obj!=null && this.hashCode()==obj.hashCode();
	}
	@Override
	public String toString() {
		return sz_country;
	}
	
	
}
