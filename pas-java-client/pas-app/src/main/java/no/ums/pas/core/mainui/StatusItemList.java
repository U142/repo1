package no.ums.pas.core.mainui;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.status.StatusItemObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class StatusItemList extends TreeSet<Inhabitant> {

    private static final Log log = UmsLog.getLogger(StatusItemList.class);

	public static final long serialVersionUID = 1;
	public StatusItemList(Comparator c) {
		super(c);
	}
	public void _add(StatusItemObject obj) {
		check_existing(obj);
	}
	protected void __add(int i, StatusItemObject obj) {
		super.add(obj);
	}
	void check_existing(StatusItemObject obj) {
		try {
			StatusItemObject find = find(obj.get_refno(), obj.get_item());
			if(find==null) {
				super.add(obj);
			}
			else {
				find.update(obj);
			}
		} catch(Exception e) { 
			log.warn(e.getMessage(), e);
		}
	}
	
	StatusItemObject find(int n_refno, int n_item) {
		for(Inhabitant currentInhab : this){
			StatusItemObject current = (StatusItemObject)currentInhab;
			if(n_refno == current.get_refno() && n_item == current.get_item())
				return current;
		}
		return null;
	}
}