package no.ums.pas.core.logon.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.jdesktop.beansbinding.AbstractBean;

public class SettingsModel extends AbstractBean {

	public SettingsModel()
	{ 
		setMapSiteDefault(true);
		setMapSiteWms(true);
	}
	
	private String username = "";
	private String companyid = "";
	private Object lbaupdate;
	private boolean autoStartParm = false;
	private boolean mapSiteDefault = true;
	private boolean mapSiteWms = false;
	private String wmsUrl = "";
	private String wmsUsername = "";
	private String wmsPassword = "";
	private String wmsImageFormat = "";
	private List<String> wmsImageFormatElements;
	private long newSendingAutoChannel = 0;
	private int newSendingAutoShape = 0;
	private Object oNewSendingAutoShape;
	
	private int panMode = 0;
	private int zoomMode = 1;
	
	private boolean panByDrag;
	private boolean panByClick;
	private boolean zoomFromCenter;
	private boolean zoomFromCorner;
	


	private String emailDisplayName = "";
	private String emailAddress = "";
	private String emailServer = "";
	
	
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
	public boolean getMapSiteDefault() {
		return mapSiteDefault;
	}
	public void setMapSiteDefault(boolean mapSiteDefault) {
		boolean oldValue = this.getMapSiteDefault();
		this.mapSiteDefault = mapSiteDefault;
		update("mapSiteDefault", oldValue, mapSiteDefault);
		System.out.println("MapSiteDefault="+mapSiteDefault);
	}
	public boolean getMapSiteWms() {
		return mapSiteWms;
	}
	public void setMapSiteWms(boolean mapSiteWms) {
		boolean oldValue = this.getMapSiteWms();
		this.mapSiteWms = mapSiteWms;
		update("mapSiteWms", oldValue, mapSiteWms);
		System.out.println("MapSiteWms="+mapSiteWms);
	}
	public String getWmsUrl() {
		return wmsUrl;
	}
	public void setWmsUrl(String wmsUrl) {
		String oldValue = this.getWmsUrl();
		this.wmsUrl = wmsUrl;
		update("wmsUrl", oldValue, wmsUrl);
		System.out.println("WMS-URL="+wmsUrl);
	}
	public String getWmsUsername() {
		return wmsUsername;
	}
	public void setWmsUsername(String wmsUsername) {
		String oldValue = this.getWmsUsername();
		this.wmsUsername = wmsUsername;
		update("wmsUsername", oldValue, wmsUsername);
		System.out.println(wmsUsername);
	}
	public String getWmsPassword() {
		return wmsPassword;
	}
	public void setWmsPassword(String wmsPassword) {
		String oldValue = this.getWmsPassword();
		this.wmsPassword = wmsPassword;
		update("wmsPassword", oldValue, wmsPassword);
		System.out.println(wmsPassword);
	}
	public String getWmsImageFormat() {
		return wmsImageFormat;
	}
	public void setWmsImageFormat(String wmsImageFormat) {
		String oldValue = this.getWmsImageFormat();
		this.wmsImageFormat = wmsImageFormat;
		update("wmsImageFormat", oldValue, wmsImageFormat);
		System.out.println(wmsImageFormat);
	}
	public List<String> getWmsImageFormatElements() {
		return wmsImageFormatElements;
	}
	public void setWmsImageFormatElements(List<String> wmsImageFormatElements) {
		this.wmsImageFormatElements = wmsImageFormatElements;
	}
	public int getPanMode() {
		return panMode;
	}
	public int getZoomMode() {
		return zoomMode;
	}
	
	public boolean getPanByDrag() {
		return panByDrag;
	}
	public void setPanByDrag(boolean panByDrag) {
		boolean oldValue = this.panByDrag;
		this.panByDrag = panByDrag;
		update("panByDrag", oldValue, panByDrag);
		setPanMode(0);
	}
	private void setZoomMode(int zoomMode) {
		this.zoomMode = zoomMode;
		System.out.println("Zoommode="+zoomMode);
	}
	public void setPanMode(int panMode) {
		this.panMode = panMode;		
		System.out.println("Panmode="+panMode);
	}
	public boolean getPanByClick() {
		return panByClick;
	}
	public void setPanByClick(boolean panByClick) {
		boolean oldValue = this.panByClick;
		this.panByClick = panByClick;
		update("panByClick", oldValue, panByClick);
		setPanMode(1);
	}
	public boolean getZoomFromCenter() {
		return zoomFromCenter;
	}
	public void setZoomFromCenter(boolean zoomFromCenter) {
		boolean oldValue = this.zoomFromCenter;
		this.zoomFromCenter = zoomFromCenter;
		update("zoomFromCenter", oldValue, zoomFromCenter);
		setZoomMode(0);
	}
	public boolean getZoomFromCorner() {
		return zoomFromCorner;
	}
	public void setZoomFromCorner(boolean zoomFromCorner) {
		boolean oldValue = this.zoomFromCorner;
		this.zoomFromCorner = zoomFromCorner;
		update("zoomFromCorner", oldValue, zoomFromCorner);
		setZoomMode(1);
	}
	
	public String getEmailDisplayName() {
		return emailDisplayName;
	}
	public void setEmailDisplayName(String emailDisplayName) {
		String oldValue = this.emailDisplayName;
		this.emailDisplayName = emailDisplayName;
		update("emailDisplayName", oldValue, emailDisplayName);
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		String oldValue = this.emailAddress;
		this.emailAddress = emailAddress;
		update("emailAddress", oldValue, emailAddress);
	}
	public String getEmailServer() {
		return emailServer;
	}
	public void setEmailServer(String emailServer) {
		String oldValue = this.emailServer;
		this.emailServer = emailServer;
		update("emailServer", oldValue, emailServer);
	}
	public long getNewSendingAutoChannel() {
		return newSendingAutoChannel;
	}
	public void setNewSendingAutoChannel(long newSendingAutoChannel) {
		this.newSendingAutoChannel = newSendingAutoChannel;
	}
	public int getNewSendingAutoShape() {
		return newSendingAutoShape;
	}
	public void setNewSendingAutoShape(int newSendingAutoShape) {
		this.newSendingAutoShape = newSendingAutoShape;
	}
	public Object getoNewSendingAutoShape() {
		return oNewSendingAutoShape;
	}
	public void setoNewSendingAutoShape(Object oNewSendingAutoShape) {
		this.oNewSendingAutoShape = oNewSendingAutoShape;
	}
	
	

}
