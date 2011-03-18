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
		if(obj instanceof AddressSearchCountry)
		{
			return this.n_cc == ((AddressSearchCountry)obj).n_cc;//this.hashCode()==((AddressSearchCountry)obj).hashCode();
		}
		else if(obj instanceof Integer)
		{
			return this.n_cc==((Integer)obj);
		}
		return false;
	}
	@Override
	public String toString() {
		return sz_country;
	}
	
	
}
