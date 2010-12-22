package no.ums.adminui.pas;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;


import sun.management.snmp.util.JvmContextFactory;

import no.ums.pas.Draw;
import no.ums.pas.MAPDraw;
import no.ums.pas.core.variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.MapLoader;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.ws.pas.UPASUISETTINGS;

public class MapImageDownload extends JApplet implements ActionListener {
	
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
		
		variables.NAVIGATION = new Navigation(this,applet_width,applet_height);
		variables.DRAW = new Draw(this,Thread.NORM_PRIORITY,applet_width,applet_height);
		variables.MAPPANE = new MapFrameAdmin(applet_width, applet_height, variables.DRAW, variables.NAVIGATION, new HTTPReq("http://vb4utv"), true);
		Settings m_settings = new Settings();
		vars.init(getParameter("w"));
		String OVERRIDE_WMS_SITE = getParameter("mapinfo");
		
		UPASUISETTINGS ui = new UPASUISETTINGS();
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
		variables.SETTINGS = m_settings;
		
		//MapLoader maploader = new MapLoader(this, new HTTPReq("http://vb4utv"));
		
		
		//variables.DRAW.get_buff_image();
		//variables.MAPPANE.initialize();
		//variables.MAPPANE.SetIsLoading(false, "map");
		
		//Container contentpane = getContentPane();
		//contentpane.setLayout(new FlowLayout());
		//contentpane.add(variables.MAPPANE);
		//add(variables.MAPPANE);
		variables.MAPPANE.setVisible(true);
		
		variables.MAPPANE.setAllOverlays();
		//variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
		variables.DRAW.set_mappane(variables.MAPPANE);
		
		lat = getParameter("lat");
		lon = getParameter("lon");
		System.out.println("lat: " + lat);
		System.out.println("lon: " + lon);
		
		String[] clat = lat.split("\\|");
		String[] clon = lon.split("\\|");
		
		shape = new PolygonStruct(variables.NAVIGATION.getDimension());
		variables.MAPPANE.set_active_shape(shape);
		
		for(int i=0;i<clat.length;++i) {
			shape.add_coor(Double.parseDouble(clon[i].replace(',', '.')),Double.parseDouble(clat[i].replace(',', '.')));
		}
		
		shape.set_fill_color(Color.BLUE);
		
		NavStruct nav = shape.typecast_polygon().calc_bounds();
	     
		
		variables.MAPPANE.setAllOverlays();
		//variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
		//variables.DRAW.set_mappane(variables.MAPPANE);
		
		
		//Må alltid kjøre calc før tegning (hvor navigation er endret pga map)
		variables.NAVIGATION.setNavigation(nav);
		variables.MAPPANE.load_map();
		shape.calc_coortopix(variables.NAVIGATION);
		variables.DRAW.create_image();
		variables.MAPPANE.kickRepaint();
		variables.MAPPANE.save_map(variables.DRAW.get_buff_image());
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("act_loadmap")) {
			
		}
		else if(e.getActionCommand().equals("act_save")) {
			variables.NAVIGATION = new Navigation(this,640,480);
			variables.DRAW = new Draw(this,Thread.NORM_PRIORITY,640,480);
			variables.MAPPANE = new MapFrameAdmin(640, 480, variables.DRAW, variables.NAVIGATION, new HTTPReq("http://vb4utv"), true);
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
			variables.SETTINGS = m_settings;
			
			//MapLoader maploader = new MapLoader(this, new HTTPReq("http://vb4utv"));
			
			//variables.DRAW.get_buff_image();
			//variables.MAPPANE.initialize();
			//variables.MAPPANE.SetIsLoading(false, "map");
			
			//Container contentpane = getContentPane();
			//contentpane.setLayout(new FlowLayout());
			//contentpane.add(variables.MAPPANE);
			//add(variables.MAPPANE);
			variables.MAPPANE.setVisible(true);
			
			variables.MAPPANE.setAllOverlays();
			//variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
			variables.DRAW.set_mappane(variables.MAPPANE);
			
			lat = getParameter("lat");
			lon = getParameter("lon");
			System.out.println("lat: " + lat);
			System.out.println("lon: " + lon);
			
			String[] clat = lat.split("\\|");
			String[] clon = lon.split("\\|");
			
			shape = new PolygonStruct(variables.NAVIGATION.getDimension());
			variables.MAPPANE.set_active_shape(shape);
			
			for(int i=0;i<clat.length;++i) {
				shape.add_coor(Double.parseDouble(clon[i].replace(',', '.')),Double.parseDouble(clat[i].replace(',', '.')));
			}
			
			shape.set_fill_color(Color.BLUE);
			
			NavStruct nav = shape.typecast_polygon().calc_bounds();
		     
			
			variables.MAPPANE.setAllOverlays();
			//variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
			//variables.DRAW.set_mappane(variables.MAPPANE);
			
			
			//Må alltid kjøre calc før tegning (hvor navigation er endret pga map)
			variables.NAVIGATION.setNavigation(nav);
			variables.MAPPANE.load_map();
			shape.calc_coortopix(variables.NAVIGATION);
			variables.DRAW.create_image();
			variables.MAPPANE.kickRepaint();
			variables.MAPPANE.save_map(variables.DRAW.get_buff_image());
		}
	}
}
