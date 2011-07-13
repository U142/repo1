package no.ums.adminui.pas;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.Draw;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapImageDownload extends JApplet implements ActionListener {

    private static final Log log = UmsLog.getLogger(MapImageDownload.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lat,lon;
	private PolygonStruct shape;
	
	public void init() {
		try {
			System.setSecurityManager(null);
		}
		catch(Exception e) {
			
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		// gets parameters from applet tag
		int applet_width;
		int applet_height;
		applet_height = Integer.parseInt(getParameter("applet_height"));
		applet_width = Integer.parseInt(getParameter("applet_width"));
		
		Variables.setNavigation(new Navigation(this, applet_width, applet_height));
		Variables.setDraw(new Draw(this, Thread.NORM_PRIORITY, applet_width, applet_height));
		Variables.setMapFrame(new MapFrameAdmin(applet_width, applet_height, Variables.getDraw(), Variables.getNavigation(), new HTTPReq("http://vb4utv"), true));
		Settings m_settings = new Settings();
		vars.init(getParameter("w"));
		String OVERRIDE_WMS_SITE = getParameter("mapinfo");
		
		if(OVERRIDE_WMS_SITE.toLowerCase().equals("default"))
		{
			m_settings.setMapServer(MAPSERVER.DEFAULT);
		}
		else
		{
			String [] arr = OVERRIDE_WMS_SITE.split(";");
			if(arr!=null && arr.length>=3)
			{
				m_settings.setMapServer(MAPSERVER.WMS);
				//ui.setSzWmsSite(arr[0]);
				m_settings.setWmsSite(arr[0]);
				m_settings.setSelectedWmsFormat(arr[1]);
				m_settings.setSelectedWmsLayers(arr[2]);
				//ui.setSzWmsFormat(arr[1]);
				//ui.setSzWmsLayers(arr[2]);
				if(arr.length>=4)
					m_settings.setWmsEpsg(arr[3]);
				else
					m_settings.setWmsEpsg("4326"); //default to lon/lat WGS84
				if(arr.length>=5)
					m_settings.setWmsUsername(arr[4]);
				else
					m_settings.setWmsUsername("");
				if(arr.length>=6)
					m_settings.setWmsPassword(arr[5]);
				else
					m_settings.setWmsPassword("");
				/*m_settings.setWmsSite(ui.getSzWmsSite());
				m_settings.setWmsUsername(ui.getSzWmsUsername());
				m_settings.setSelectedWmsLayers(ui.getSzWmsLayers());
				m_settings.setSelectedWmsFormat(ui.getSzWmsFormat());
				m_settings.setWmsPassword(ui.getSzWmsPassword());*/
			}
		}
		Variables.setSettings(m_settings);
		
		//MapLoader maploader = new MapLoader(this, new HTTPReq("http://vb4utv"));
		
		
		//Variables.DRAW.get_buff_image();
		//Variables.MAPPANE.initialize();
		//Variables.MAPPANE.SetIsLoading(false, "map");
		
		//Container contentpane = getContentPane();
		//contentpane.setLayout(new FlowLayout());
		//contentpane.add(Variables.MAPPANE);
		//add(Variables.MAPPANE);
		Variables.getMapFrame().setVisible(true);
		
		Variables.getMapFrame().setAllOverlays();
		//Variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
		Variables.getDraw().set_mappane(Variables.getMapFrame());
		
		lat = getParameter("lat");
		lon = getParameter("lon");
		log.debug("lat: " + lat);
		log.debug("lon: " + lon);
		
		String[] clat = lat.split("\\|");
		String[] clon = lon.split("\\|");
		
		shape = new PolygonStruct(Variables.getNavigation().getDimension());
		Variables.getMapFrame().set_active_shape(shape);
		
		for(int i=0;i<clat.length;++i) {
			shape.add_coor(Double.parseDouble(clon[i].replace(',', '.')),Double.parseDouble(clat[i].replace(',', '.')));
		}
		
		shape.set_fill_color(Color.BLUE);
		
		NavStruct nav = shape.typecast_polygon().calc_bounds();
	     
		
		Variables.getMapFrame().setAllOverlays();
		//Variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
		//Variables.DRAW.set_mappane(Variables.MAPPANE);
		
		
		//Må alltid kjøre calc før tegning (hvor navigation er endret pga map)
		Variables.getNavigation().setNavigation(nav);
		Variables.getMapFrame().load_map();
		shape.calc_coortopix(Variables.getNavigation());
		Variables.getDraw().create_image();
		Variables.getMapFrame().kickRepaint();
		Variables.getMapFrame().save_map(Variables.getDraw().get_buff_image());
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("act_loadmap")) {
			
		}
		else if(e.getActionCommand().equals("act_save")) {
			Variables.setNavigation(new Navigation(this, 640, 480));
			Variables.setDraw(new Draw(this, Thread.NORM_PRIORITY, 640, 480));
			Variables.setMapFrame(new MapFrameAdmin(640, 480, Variables.getDraw(), Variables.getNavigation(), new HTTPReq("http://vb4utv"), true));
			Settings m_settings = new Settings();
			vars.init("https://secure.ums2.no/centricadminws/WS/");
			
			m_settings.setWmsSite("http://192.168.3.135/mapguide2010/mapagent/mapagent.fcgi");//
			m_settings.setMapServer(MAPSERVER.DEFAULT);
			String [] arr = "http://192.168.3.135/mapguide2010/mapagent/mapagent.fcgi;image/png;Gemeentekaart2009/Layers/MunicipalityBorder_LatLon,Gemeentekaart2009/Layers/Road_LatLon,Gemeentekaart2009/Layers/River_LatLon,Gemeentekaart2009/Layers/CityPoint_LatLon,Gemeentekaart2009/Layers/CityArea_LatLon,Gemeentekaart2009/Layers/CityPoint,Gemeentekaart2009/Layers/River,Gemeentekaart2009/Layers/MunicipalityBorder,Gemeentekaart2009/Layers/Background,Gemeentekaart2009/Layers/CityArea,Gemeentekaart2009/Layers/Road".split(";");
			if(arr!=null && arr.length>=3)
			{
				m_settings.setWmsSite(arr[0]);
				m_settings.setSelectedWmsFormat(arr[1]);
				m_settings.setSelectedWmsLayers(arr[2]);
				if(arr.length>=4)
					m_settings.setWmsUsername(arr[3]);
				if(arr.length>=5)
					m_settings.setWmsPassword(arr[4]);
			}
			Variables.setSettings(m_settings);
			
			//MapLoader maploader = new MapLoader(this, new HTTPReq("http://vb4utv"));
			
			//Variables.DRAW.get_buff_image();
			//Variables.MAPPANE.initialize();
			//Variables.MAPPANE.SetIsLoading(false, "map");
			
			//Container contentpane = getContentPane();
			//contentpane.setLayout(new FlowLayout());
			//contentpane.add(Variables.MAPPANE);
			//add(Variables.MAPPANE);
			Variables.getMapFrame().setVisible(true);
			
			Variables.getMapFrame().setAllOverlays();
			//Variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
			Variables.getDraw().set_mappane(Variables.getMapFrame());
			
			lat = getParameter("lat");
			lon = getParameter("lon");
			log.debug("lat: " + lat);
			log.debug("lon: " + lon);
			
			String[] clat = lat.split("\\|");
			String[] clon = lon.split("\\|");
			
			shape = new PolygonStruct(Variables.getNavigation().getDimension());
			Variables.getMapFrame().set_active_shape(shape);
			
			for(int i=0;i<clat.length;++i) {
				shape.add_coor(Double.parseDouble(clon[i].replace(',', '.')),Double.parseDouble(clat[i].replace(',', '.')));
			}
			
			shape.set_fill_color(Color.BLUE);
			
			NavStruct nav = shape.typecast_polygon().calc_bounds();
		     
			
			Variables.getMapFrame().setAllOverlays();
			//Variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
			//Variables.DRAW.set_mappane(Variables.MAPPANE);
			
			
			//Må alltid kjøre calc før tegning (hvor navigation er endret pga map)
			Variables.getNavigation().setNavigation(nav);
			Variables.getMapFrame().load_map();
			shape.calc_coortopix(Variables.getNavigation());
			Variables.getDraw().create_image();
			Variables.getMapFrame().kickRepaint();
			Variables.getMapFrame().save_map(Variables.getDraw().get_buff_image());
		}
	}
}
