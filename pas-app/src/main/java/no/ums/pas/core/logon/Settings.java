package no.ums.pas.core.logon;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import no.ums.pas.core.logon.WmsLayer;

public class Settings {
	public enum MAPSERVER
	{
		DEFAULT,
		WMS,
	}

	private boolean parm;
	private boolean fleetcontrol;
	private String username;
	private String company;
	private boolean windowFullscreen;
	private int lbarefresh;
	private int windowHeight;
	private int windowWidth;
	private int xpos;
	private int ypos;
	private int gis_download_details_threshold;
	private String sz_skin_classname;
	private String sz_theme_classname;
	private String sz_watermark_classname;
	private String sz_buttonshaper_classname;
	private String sz_gradientpainter_classname;
	private String sz_titlepainter_classname;
	private MAPSERVER n_mapserver;
	private String sz_wms_site;
	private List<WmsLayer> m_wms_layers = new ArrayList<WmsLayer>();
	private List<String> m_selected_wms_layers = new ArrayList<String>();
	private String sz_wms_format;
	private String sz_wms_username;
	private String sz_wms_password;
	private String sz_wms_epsg;
	private boolean b_pan_by_drag;
	private boolean b_zoom_from_center;
	private String sz_languageid; //en_EN, no_NO
	private Rectangle rect_messagelib_dlg;
	List<Object> messagelib_exploded_nodes;
	private long n_newsending_autochannel = 0;
	private int n_autoselect_shapetype = 0;
	
	public Settings() {
		username = "";
		company = "";
		parm = false;
		fleetcontrol = false;
		windowHeight = 0;
		windowWidth = 0;
		xpos = 0;
		ypos = 0;
		sz_skin_classname = "";
		sz_theme_classname = "";
		sz_watermark_classname = "";
		gis_download_details_threshold = 500;
		n_mapserver = MAPSERVER.DEFAULT;
		sz_wms_site = "http://";
		sz_wms_format = "";
		b_pan_by_drag = false;
		sz_languageid = "en_GB";
		sz_wms_username = "";
		sz_wms_password = "";
		rect_messagelib_dlg = new Rectangle(0,0,0,0);
		messagelib_exploded_nodes = null;
		b_zoom_from_center = true;
	}
	
	public static String getLanguageID(int n_language)
	{
		String sz_languageid = "";
		switch(n_language)
		{
		case 1: //nor
			sz_languageid = "no_NO";
			break;
		case 2: //eng
			sz_languageid = "en_GB";
			break;
		case 3: //deu
			sz_languageid = "de_DE";
			break;
		case 5: //sve
			sz_languageid = "sv_SE";
			break;
		case 6: //dan
			sz_languageid = "da_DK";
			break;
		case 7: //ned
			sz_languageid = "nl_NL";
			break;
		default:
			sz_languageid = "en_EN";
			break;
			
		}
		return sz_languageid;
	}
	
	public Settings(String username, String company, boolean parm, 
					boolean fleetcontrol, int lbarefresh, 
					MAPSERVER n_mapserver, String sz_wms_site, 
					List<String> layers, String sz_wms_format, 
					boolean b_pan_by_drag, boolean b_zoom_from_center, String sz_language, 
					String sz_wms_username, String sz_wms_password) {
		this.username = username;
		this.company = company;
		this.parm = parm;
		this.fleetcontrol = fleetcontrol;
		this.lbarefresh = lbarefresh;
		this.n_mapserver = n_mapserver;
		this.sz_wms_site = sz_wms_site;
		//this.m_wms_layers = selected_layers;
		//setWmsLayers(layers);
		
		this.sz_wms_format = sz_wms_format;
		this.b_pan_by_drag = b_pan_by_drag;
		this.sz_languageid = sz_language;
		this.sz_wms_username = sz_wms_username;
		this.sz_wms_password = sz_wms_password;
		this.sz_wms_epsg = "4326";
		rect_messagelib_dlg = new Rectangle(0,0,0,0);
		messagelib_exploded_nodes = null;

	}
	
	public long getN_newsending_autochannel() {
		return n_newsending_autochannel;
	}

	public void setN_newsending_autochannel(long n_newsending_autochannel) {
		this.n_newsending_autochannel = n_newsending_autochannel;
	}

	public int getN_autoselect_shapetype() {
		return n_autoselect_shapetype;
	}

	public void setN_autoselect_shapetype(int n_autoselect_shapetype) {
		this.n_autoselect_shapetype = n_autoselect_shapetype;
	}

	public boolean getPanByDrag() {
		return b_pan_by_drag;
	}
	public void setPanByDrag(boolean b) {
		b_pan_by_drag = b;
	}
	public boolean getZoomFromCenter() {
		return b_zoom_from_center;
	}

	public void setZoomFromCenter(boolean b_zoom_from_center) {
		this.b_zoom_from_center = b_zoom_from_center;
	}

	public MAPSERVER getMapServer() {
		return n_mapserver;
	}
	public void setMapServer(MAPSERVER m) {
		this.n_mapserver = m;
	}
	public String getWmsSite() {
		return this.sz_wms_site;
	}
	public void setWmsSite(String s) {
		this.sz_wms_site = s;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public boolean fleetcontrol() {
		return fleetcontrol;
	}

	public void setFleetcontrol(boolean fleetcontrol) {
		this.fleetcontrol = fleetcontrol;
	}

	public boolean parm() {
		return parm;
	}

	public void setParm(boolean parm) {
		this.parm = parm;
	}

	public int getLbaRefresh() {
		return lbarefresh;
	}
	
	public void setLbaRefresh(int lbarefresh) {
		this.lbarefresh = lbarefresh;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isWindowFullscreen() {
		return windowFullscreen;
	}

	public void setWindowFullscreen(boolean windowFullscreen) {
		this.windowFullscreen = windowFullscreen;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public String getThemeClassName() {
		return sz_theme_classname;
	}
	public String getSkinClassName() {
		return sz_skin_classname;
	}
	public String getWatermarkClassName() {
		return sz_watermark_classname;
	}
	public String getButtonShaperClassname() {
		return sz_buttonshaper_classname;
	}
	public String getGradientClassname() {
		return sz_gradientpainter_classname;
	}
	public String getTitlePainterClassname() {
		return sz_titlepainter_classname;
	}
	public void setThemeClassName(String sz) {
		sz_theme_classname = sz;
	}
	public void setSkinClassName(String sz) {
		sz_skin_classname = sz;
	}
	public void setWatermarkClassName(String sz) {
		sz_watermark_classname = sz;
	}
	public void setButtonShaperClassName(String sz) {
		sz_buttonshaper_classname = sz;
	}
	public void setGradientClassname(String sz) {
		sz_gradientpainter_classname = sz;
	}
	public void setTitlePainterClassname(String sz) {
		sz_titlepainter_classname = sz;
	}
	public void setGisDownloadDetailThreshold(int n)
	{
		gis_download_details_threshold = n;
	}
	public int getGisDownloadDetailThreshold()
	{
		return gis_download_details_threshold;
	}
	public void setSelectedWmsLayers(List<String> l)
	{
		m_selected_wms_layers = l;
	}
	public void setWmsLayers(List<WmsLayer> l)
	{
		m_wms_layers = l;
		String s = "";
		for(WmsLayer layer : l)
		{
			s+=layer.toString();
			s+=",";
		}
		setSelectedWmsLayers(s);
	}
	public void setWmsLayers(String s)
	{
		setSelectedWmsLayers(s);
	}
	public void setSelectedWmsLayers(String s)
	{
		String [] l = s.split(",");
		m_wms_layers.clear();
		m_selected_wms_layers.clear();
		for(int i=0; i < l.length; i++)
		{
			if(l[i].length()==0)
				continue;
			//try to split layer=[false,true]
			int idx = l[i].indexOf("=");
			if(idx>0)
			{
				String layername = l[i].substring(0, idx);
				boolean checked = Boolean.parseBoolean(l[i].substring(idx+1));
				m_wms_layers.add(new WmsLayer(layername, checked));
				if(checked)
					m_selected_wms_layers.add(layername);
			}
			else //compatability
			{
				m_wms_layers.add(new WmsLayer(l[i], true));
				m_selected_wms_layers.add(l[i]);
			}
		}
	}
	public List<WmsLayer> getWmsLayers()
	{
		return m_wms_layers;
	}
	public List<String> getSelectedWmsLayers()
	{
		return m_selected_wms_layers;
	}
	public void setSelectedWmsFormat(String s)
	{
		sz_wms_format = s;
	}
	public String getSelectedWmsFormat()
	{
		return sz_wms_format;
	}
	public void setWmsUsername(String s)
	{
		if(s != null)
			sz_wms_username = (s.trim().length()==0 ? "" : s);
		else
			sz_wms_username = "";
	}
	public String getWmsUsername()
	{
		return sz_wms_username;
	}
	public void setWmsPassword(String s)
	{
		if(s != null)
			sz_wms_password = (s.trim().length()==0 ? "" : s);
		else
			sz_wms_password = "";
	}
	public String getWmsPassword()
	{
		return sz_wms_password;
	}
	public void setWmsEpsg(String s)
	{
		sz_wms_epsg = s;
	}
	public String getWmsEpsg()
	{
		return sz_wms_epsg;
	}
	public String getLanguage()
	{
		return sz_languageid;
	}
	public void setLanguage(String sz_language)
	{
		sz_languageid = sz_language;
	}
	public Rectangle getRectMessageLibDlg()
	{
		return rect_messagelib_dlg;
	}
	public void setRectMessageLibDlg(Rectangle r)
	{
		rect_messagelib_dlg = r;
	}
	public List<Object> getMessageLibExplodedNodes()
	{
		return messagelib_exploded_nodes;
	}
	public void setMessageLibExplodedNodes(List<Object> nodes)
	{
		messagelib_exploded_nodes = nodes;
	}
}
