package no.ums.adminui.pas;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;
import javax.swing.JPanel;

import org.opengis.filter.expression.Add;

import no.ums.pas.Draw;
import no.ums.pas.core.variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.MapLoader;
import no.ums.pas.maps.defines.Navigation;

public class MapImageDownload extends JApplet implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
		new MapImageDownload();
	}
	
	public MapImageDownload() {
		JPanel pnl_map = new JPanel();
		add(pnl_map);
		variables.NAVIGATION = new Navigation(this,640,480);
		variables.DRAW = new Draw(this,Thread.NORM_PRIORITY,640,480);
		variables.MAPPANE = new MapFrameAdmin(640, 480, variables.DRAW, variables.NAVIGATION, new HTTPReq("http://vb4utv"), true);
		Settings m_settings = new Settings();
		
		m_settings.setWmsSite("http://192.168.3.135/mapguide2010/mapagent/mapagent.fcgi");//
		m_settings.setMapServer(MAPSERVER.WMS);
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
		maploader.load_map_wms(5.3353, 7.2271, 53.466, 52.2176, new Dimension(640,480),variables.SETTINGS.getWmsSite()); 
		variables.DRAW.setMapImage(variables.MAPPANE.get_image());
		variables.DRAW.set_mappane(variables.MAPPANE);
		//m_drawthread.setRepaint(m_image);
		//variables.DRAW.set_neednewcoors(true);
		//variables.DRAW.set_need_imageupdate();
		variables.MAPPANE.initialize();
		variables.MAPPANE.SetIsLoading(false, "map");
		//m_mappane.repaint();
		//m_mappane.validate();
		add(variables.MAPPANE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("act_loadmap")) {
			
		}
	}
}
