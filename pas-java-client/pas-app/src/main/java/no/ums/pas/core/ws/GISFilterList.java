package no.ums.pas.core.ws;

import java.util.ArrayList;
import java.util.List;

import no.ums.ws.addressfilters.AddressAssociatedWithFilter;

public class GISFilterList extends ArrayList<AddressAssociatedWithFilter> {
	
	public void fill(List<AddressAssociatedWithFilter> list) {
		addAll(list);
	}

}
