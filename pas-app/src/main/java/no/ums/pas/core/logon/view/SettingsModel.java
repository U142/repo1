package no.ums.pas.core.logon.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jdesktop.beansbinding.AbstractBean;

public class SettingsModel extends AbstractBean {

	private String username = "";
	private String companyid = "";
	private Object lbaupdate;
	private boolean autoStartParm = false;
	private int mapSite;
	private String wmsUrl;
	private String wmsUsername;
	private String wmsPassword;
	private String wmsImageFormat;
	private int panMode;
	private int zoomMode;
	private String emailDisplayName;
	private String emailAddress;
	private String emailServer;
	
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
	public boolean getAutoStartParm() {
		return autoStartParm;
	}
	public void setAutoStartParm(boolean autoStartParm) {
		boolean oldValue = this.getAutoStartParm();
		this.autoStartParm = autoStartParm;
		update("autoStartParm", oldValue, autoStartParm);
		System.out.println("AutoStart: Parm="+autoStartParm);
	}
	public int getMapSite() {
		return mapSite;
	}
	public void setMapSite(int mapSite) {
		this.mapSite = mapSite;
	}
	public String getWmsUrl() {
		return wmsUrl;
	}
	public void setWmsUrl(String wmsUrl) {
		this.wmsUrl = wmsUrl;
	}
	public String getWmsUsername() {
		return wmsUsername;
	}
	public void setWmsUsername(String wmsUsername) {
		this.wmsUsername = wmsUsername;
	}
	public String getWmsPassword() {
		return wmsPassword;
	}
	public void setWmsPassword(String wmsPassword) {
		this.wmsPassword = wmsPassword;
	}
	public String getWmsImageFormat() {
		return wmsImageFormat;
	}
	public void setWmsImageFormat(String wmsImageFormat) {
		this.wmsImageFormat = wmsImageFormat;
	}
	public int getPanMode() {
		return panMode;
	}
	public void setPanMode(int panMode) {
		this.panMode = panMode;
	}
	public int getZoomMode() {
		return zoomMode;
	}
	public void setZoomMode(int zoomMode) {
		this.zoomMode = zoomMode;
	}
	public String getEmailDisplayName() {
		return emailDisplayName;
	}
	public void setEmailDisplayName(String emailDisplayName) {
		this.emailDisplayName = emailDisplayName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getEmailServer() {
		return emailServer;
	}
	public void setEmailServer(String emailServer) {
		this.emailServer = emailServer;
	}
	
	

}
