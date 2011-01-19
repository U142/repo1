package no.ums.pas.status;

import java.util.ArrayList;

public class StatusCodeList extends ArrayList<StatusCode> {
	public static final long serialVersionUID = 1;
	public StatusCodeList() {
		super();
	}
	public void _add(StatusCode obj) {
		check_existing(obj);
	}
	private void __add(StatusCode obj) {
		super.add(obj);
	}
	public void check_existing(StatusCode obj) {
		StatusCode current;
		boolean b_add = true;
		for(int i=0; i < this.size(); i++) {
			current = _get(i);
			if(current.get_code() == obj.get_code()) {
				//super.set(i, obj);
				current.update(obj);
				b_add = false;
				return ;
			}
		}
		if(b_add)
			__add(obj);
	}
	public StatusCode _get(int i) {
		return (StatusCode)super.get(i);
	}
	
	public String get_statusname(int statuscode) {
		StatusCode current;
		for(int i=0; i < this.size(); i++) {
			current = _get(i);
			if(current.get_code() == statuscode) {
				return current.get_status();
			}
		}
		return null;
	}
}