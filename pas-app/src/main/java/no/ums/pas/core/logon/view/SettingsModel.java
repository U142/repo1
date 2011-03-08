package no.ums.pas.core.logon.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jdesktop.beansbinding.AbstractBean;

public class SettingsModel extends AbstractBean {
	/*
	private final PropertyChangeSupport prop = new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		prop.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		prop.removePropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		prop.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		prop.removePropertyChangeListener(propertyName, listener);
	}
*/

	private String username = "";
	private String companyid = "";
	private Object lbaupdate;
	private int n_lbaupdate_selecteditem = -1;
	
	public String getUsername() {
		return username;
	}
	public int getN_lbaupdate_selecteditem() {
		return n_lbaupdate_selecteditem;
	}
	public void setN_lbaupdate_selecteditem(int n_lbaupdate_selecteditem) {
		this.n_lbaupdate_selecteditem = n_lbaupdate_selecteditem;
	}
	public String getCompanyid() {
		return companyid;
	}
	public void setCompanyid(String companyid) {
		Object oldValue = this.getCompanyid();
		this.companyid = companyid;
		update("companyid", oldValue, companyid);
		System.out.println("companyid=" + companyid);
	}
	public void setUsername(String username) {
		Object oldValue = this.getUsername();
		this.username = username;
		update("username", oldValue, username);
		System.out.println("username=" + username);
	}
	public void setLbaupdate(Object lbaupdate) {
		Object oldValue = this.getLbaupdate();
		this.lbaupdate = lbaupdate;
		update("lbaupdate", oldValue, lbaupdate);
		System.out.println("lbaupdate=" + lbaupdate);
	}
	public Object getLbaupdate() {
		return lbaupdate;
	}
	
	

}
