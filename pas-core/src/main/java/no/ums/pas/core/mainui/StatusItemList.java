package no.ums.pas.core.mainui;

import java.util.ArrayList;

import no.ums.pas.status.*;

public class StatusItemList extends ArrayList<Object> {
	public static final long serialVersionUID = 1;
	public StatusItemList() {
		super();
	}
	public void _add(StatusItemObject obj) {
		check_existing(obj);
	}
	protected void __add(int i, StatusItemObject obj) {
		super.add(obj);
	}
	void check_existing(StatusItemObject obj) {
		try {
			//int n_item = obj.get_item();
			//if(super.size() < n_item) {
			StatusItemObject find = find(obj.get_refno(), obj.get_item());
			if(find==null) {
				//super.ensureCapacity(n_item);
				//PAS.get_pas().add_event("added l_item " + n_item);
				super.add(obj);
			}
			else {
				//PAS.get_pas().add_event("updated l_item " + n_item);				
				//_get(n_item + 1).update(obj);
				//System.out.println("Find n_item=" + n_item);
				//find(n_item).update(obj);
				find.update(obj);
			}
				//super.set(n_item - 1, obj);
		} catch(Exception e) { 
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	StatusItemObject _get(int i) {
		return (StatusItemObject)super.get(i);
	}
	StatusItemObject find(int n_refno, int n_item) {
		for(int i=0; i < size(); i++) {
			if(n_refno == _get(i).get_refno() && n_item == _get(i).get_item())
				return _get(i);
		}
		return null;
	}
}