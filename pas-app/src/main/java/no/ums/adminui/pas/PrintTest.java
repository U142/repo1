package no.ums.adminui.pas;

import no.ums.pas.Draw;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.ums.tools.PrintCtrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ServiceLoader;

public class PrintTest extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lat,lon;
	private PolygonStruct shape;
	
	public static void main(String[] args) {
		new PrintTest();
	}
	
	public PrintTest() {
		
		//Variables.DRAW.create_image();
		//Variables.MAPPANE.load_map();
		//Variables.MAPPANE.kickRepaint();
		System.out.println("line 93");
		//add(Variables.MAPPANE);
		JButton btn_save = new JButton("Save");
		btn_save.addActionListener(this);
		btn_save.setActionCommand("act_save");
		Container cont = getContentPane();
		cont.setLayout(new FlowLayout());
		cont.add(btn_save);
		System.out.println("line 99");
		
		
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
		
		
		Variables.getNavigation().setNavigation(5.3353, 7.2271, 53.466, 52.2176);
		Variables.getDraw().set_mappane(Variables.getMapFrame());
		
			     
		
		Variables.getMapFrame().setAllOverlays();
		//Variables.NAVIGATION.setNavigation(5.3353, 7.2271, 53.466, 52.2176);
		//Variables.DRAW.set_mappane(Variables.MAPPANE);
		
		
		//Må alltid kjøre calc før tegning (hvor navigation er endret pga map)		
		Variables.getMapFrame().load_map();
		Variables.getDraw().create_image();
		Variables.getMapFrame().kickRepaint();
		JLabel img = new JLabel();
		img.setIcon(new ImageIcon(Variables.getDraw().get_buff_image()));
		cont.add(img);
		setSize(800,600);
		this.setVisible(true);
		
		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		// gets parameters from applet tag
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("act_loadmap")) {
			
		}
		else if(e.getActionCommand().equals("act_save")) {
			PrintCtrl print = new PrintCtrl(this,this);
			print.print();
		}
	}
}
