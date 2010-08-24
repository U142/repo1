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

import org.opengis.filter.expression.Add;

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

public class MapImageDownload extends JApplet implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lat,lon;
	private PolygonStruct shape;
	
	public void init() {
		new MapImageDownload();
	}
	
	public MapImageDownload() {
		this.setSize(new Dimension(640,480));
		JPanel pnl_map = new JPanel();
		add(pnl_map);
		variables.NAVIGATION = new Navigation(this,640,480);
		variables.DRAW = new Draw(this,Thread.NORM_PRIORITY,640,480);
		variables.MAPPANE = new MapFrameAdmin(640, 480, variables.DRAW, variables.NAVIGATION, new HTTPReq("http://vb4utv"), true);
		Settings m_settings = new Settings();
		vars.init("http://localhost/WS/");
		
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
		
		MapLoader maploader = new MapLoader(this, new HTTPReq("http://vb4utv"));
		
		
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
		variables.DRAW.create_image();
		variables.MAPPANE.load_map();
		//variables.MAPPANE.kickRepaint();
		
		//add(variables.MAPPANE);
		JButton btn_save = new JButton("Save");
		btn_save.addActionListener(this);
		btn_save.setActionCommand("act_save");
		add(btn_save);
		//JPanel pnl_ting = new JPanel();
		//pnl_ting.add(btn_save);
		//contentpane.add(pnl_ting);
		//this.resize(800, 600);
		//this.repaint();
		//this.setVisible(true);
		
		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		// gets parameters from applet tag
		lat = getParameter("lat");
		lon = getParameter("lon");
		
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

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("act_loadmap")) {
			
		}
		else if(e.getActionCommand().equals("act_save")) {
			variables.MAPPANE.save_map(variables.DRAW.get_buff_image());
		}
	}
}
