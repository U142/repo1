package no.ums.adminui.pas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.parm.admin.ArrayOfPAOBJECT;
import no.ums.ws.parm.admin.PAOBJECT;
import no.ums.ws.parm.admin.ParmAdmin;
import no.ums.ws.parm.admin.ULOGONINFO;
import no.ums.ws.parm.admin.UPolypoint;
import no.ums.ws.pas.ArrayOfUDEPARTMENT;
import no.ums.ws.pas.ArrayOfUShape;
import no.ums.ws.pas.PASHAPETYPES;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.RESTRICTIONTYPE;
import no.ums.ws.pas.UDEPARTMENT;
import no.ums.ws.pas.UNSLOOKUP;
import no.ums.ws.pas.UPASLOGON;
import no.ums.ws.pas.UPASUISETTINGS;
import no.ums.ws.pas.status.UPolygon;
import no.ums.ws.pas.tas.ULBACOUNTRY;

import org.jvnet.substance.SubstanceLookAndFeel;

import no.ums.adminui.pas.ws.WSGetRestrictionShapes;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.pluginbase.PAS_Scripting;
import no.ums.pas.pluginbase.PluginLoader;
import sun.java2d.pipe.SpanClipRenderer;


import no.ums.pas.core.variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.Logon;
import no.ums.pas.core.logon.LogonInfo;
import no.ums.pas.core.logon.RightsManagement;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.SettingsGUI;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.logon.UserInfo.NSLookup;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.StatusPanel;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.menus.defines.SubstanceMenuItem;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.ws.WSLogon;
import no.ums.pas.core.ws.WSSaveUI;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavPoint;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.UnknownShape;
import no.ums.pas.MAPDraw;
import no.ums.pas.PAS;
import no.ums.pas.PASActions;
import no.ums.pas.PASDraw;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.SendPropertiesPolygon;
import no.ums.pas.send.messagelibrary.MessageLibDlg;
import no.ums.pas.send.sendpanels.SendWindow.BtnPane;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.pas.importer.*;
import no.ums.pas.maps.*;
import no.ums.pas.maps.defines.*;

public class MapApplet extends JApplet implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MapFrameAdmin m_mappane;
	public AdminDraw m_drawthread;
	public Navigation m_navigation;
	public Image m_image;
	public String coors;
	public SendPropertiesPolygon sp;
	public UserInfo m_info;
	private ULOGONINFO logoninfo;
	private LogonInfo info;
	public static String OVERRIDE_WMS_SITE = null;
	public int applet_width;
	public int applet_height;
	
	public void init() {
		try {
			System.setSecurityManager(null);
		}
		catch(Exception e) {
			
		}
		
	}
	
	public void kickRepaint()
	{
	}
	
	public void start() {
		
		vars.init(getParameter("w"));
		PAS.setLocale("en","GB");
		//info = new LogonInfo("mh","ums","a8a5dce8b728e1b62dac48f0c2550bc1b3ce3c28fb686d376868a1ecc6aa1661258ff9ac095924fc146d8e226966db7ee271e2832de42d589f53b62c6ca4c8b5","GB");
		//WSLogon proc = new WSLogon(this, info.get_userid(), info.get_compid(), info.get_passwd());
		
		no.ums.ws.pas.ULOGONINFO logon = new no.ums.ws.pas.ULOGONINFO();
		/*
		String session = "9235035e-f6f8-413c-b921-059f78f8516c";
		logon.setLDeptpk(1);
		logon.setLComppk(1);
		logon.setLUserpk(7);
		logon.setSzPassword("614b5c970633ec4ac2ee96f98f6fdeb04e4fb0e0b13dc9401b674bb8c4a41ee96b67ce39491a716776ca81a4b58a7b47434aef0195c90241856fe065a476adcb");
		logon.setSessionid(session);
		*/
		applet_height = Integer.parseInt(getParameter("applet_height"));
		applet_width = Integer.parseInt(getParameter("applet_width"));
		logon.setLDeptpk(Integer.parseInt(getParameter("deptid")));
		logon.setLComppk(Integer.parseInt(getParameter("compid")));
		logon.setLUserpk(Long.parseLong(getParameter("userid")));
		logon.setSzPassword(getParameter("password"));
		logon.setSessionid(getParameter("session"));
		OVERRIDE_WMS_SITE = getParameter("mapinfo");
		
		m_info = new UserInfo(logon.getSzUserid(), logon.getLComppk(),logon.getSzUserid(), logon.getSzCompid(), "", "", logon.getSessionid(), "");
		Settings m_settings = new Settings();
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
	
		WSGetRestrictionShapes ting = new WSGetRestrictionShapes(this, "act_logon", logon, PASHAPETYPES.PADEPARTMENTRESTRICTION);
		resize(applet_width,applet_height);
		m_navigation = new Navigation(this,applet_width,applet_height);
		variables.NAVIGATION = m_navigation;
		
		try {
			ting.run();
			//PAS.pasplugin = new PAS_Scripting();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void afterLogon() {
		
		m_drawthread = new AdminDraw(null,Thread.NORM_PRIORITY,applet_width,applet_height);
		variables.DRAW = m_drawthread;
		
		
	}
	
	private void afterAfterLogon() {
		SendController m_sendcontroller = new SendController();
		//m_drawthread.set_sendcontroller(m_sendcontroller);
		variables.SENDCONTROLLER = m_sendcontroller;
		
		//m_drawthread.set_mappane(m_mappane);
		JPanel pnl_buttons = new JPanel();
		JButton btn_zoom = new JButton("Zoom");
		JButton btn_pan = new JButton("Pan");
		JButton btn_draw = new JButton("Draw");
		JButton btn_put = new JButton("Put");
		
		pnl_buttons.add(btn_zoom);
		pnl_buttons.add(btn_pan);
		pnl_buttons.add(btn_draw);
		pnl_buttons.add(btn_put);
		
		btn_zoom.addActionListener( new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				m_mappane.set_mode(MapFrame.MAP_MODE_ZOOM);
				//m_mappane.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		}});
		
		btn_pan.addActionListener( new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				m_mappane.set_mode(MapFrame.MAP_MODE_PAN);
				//m_mappane.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		}});
		
		btn_draw.addActionListener( new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
			if(e.getSource().getClass().equals(JButton.class)){
				m_mappane.set_mode(MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA);
				if(variables.SENDCONTROLLER.get_activesending() == null) {
					SendObject so = new SendObject("New sending", SendProperties.SENDING_TYPE_PAINT_RESTRICTION_AREA_, 0, this, m_navigation);
					variables.SENDCONTROLLER.set_activesending(so);
					variables.SENDCONTROLLER.add_sending(so);
					sp = new SendPropertiesPolygon(new PolygonStruct(new Dimension(applet_width,applet_height)),new SendOptionToolbar(so,this,0), new Col());
					so.set_sendproperties(sp);
				}
				else
					sp = variables.SENDCONTROLLER.get_activesending().get_sendproperties().typecast_poly();
										
				m_mappane.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
			}
		}});
		
		btn_put.addActionListener( new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				variables.MAPPANE.get_active_shape();
				no.ums.ws.pas.UPolygon shape = new no.ums.ws.pas.UPolygon();
				PolygonStruct polygon = variables.MAPPANE.get_active_shape().typecast_polygon();
				shape.setColAlpha(polygon.get_fill_color().getAlpha());
				shape.setColBlue(polygon.get_fill_color().getBlue());
				shape.setColGreen(polygon.get_fill_color().getGreen());
				shape.setColRed(polygon.get_fill_color().getRed());
				no.ums.ws.pas.UPolypoint p;
				for(int i=0;i<polygon.get_coors_lat().size();++i) {
					p = new no.ums.ws.pas.UPolypoint();
					p.setLat(polygon.get_coors_lat().get(i));
					p.setLon(polygon.get_coors_lon().get(i));
					shape.getPolypoint().add(p);
				}
				ArrayOfUShape arrShape = new ArrayOfUShape();
				arrShape.getUShape().add(shape);
				variables.USERINFO.get_current_department().get_restriction_shapes();
				variables.USERINFO.add_department(variables.USERINFO.get_default_dept().get_deptpk(),
						variables.USERINFO.get_default_dept().get_deptid(), variables.USERINFO.get_default_dept().get_defaultnumber(),
						(float)variables.USERINFO.get_default_dept().get_nav_init()._lbo,
						(float)variables.USERINFO.get_default_dept().get_nav_init()._rbo,
						(float)variables.USERINFO.get_default_dept().get_nav_init()._ubo, 
						(float)variables.USERINFO.get_default_dept().get_nav_init()._bbo,
						false, 3, 150, "fjols", "", 1, 1, 1, 1, 1, 1, (long)1, "", null, 2, arrShape);
				
				
				variables.USERINFO.get_current_department().CalcCoorRestrictionShapes();
				//m_mappane.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		}});
		
		Container contentpane = getContentPane();
		contentpane.setLayout(new FlowLayout());
		//contentpane.add(pnl_buttons, BorderLayout.PAGE_START);
		contentpane.add(m_mappane, BorderLayout.PAGE_END);
		contentpane.setSize(applet_width, applet_height);
		//add(m_mappane);
		//m_image = m_mappane.m_maploader.load_map(m_navigation.getNavLBO(), m_navigation.getNavRBO(), m_navigation.getNavUBO(), m_navigation.getNavBBO(), this.getSize(), 0, "By");
		//m_drawthread.setRepaint(m_image);
		//m_drawthread.setNeedRepaint();
		//JOptionPane.showMessageDialog(this, "Is succes: " + m_drawthread.isImgpaintSuccess());
		m_mappane.addActionListener(this);
		m_mappane.set_mode(MapFrame.MAP_MODE_ZOOM);
		//m_mappane.SetIsLoading(false, "map");
		//put("38");
	}
	
	private void add_controls(){
		
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
			
		final ActionEvent event = e;
			
		if("act_logon".equals(e.getActionCommand()))
		{
			
			boolean b_results_ready;
			ArrayOfUDEPARTMENT wsdept = (ArrayOfUDEPARTMENT)e.getSource();
			
			afterLogon();
			
			List<UDEPARTMENT> depts = wsdept.getUDEPARTMENT();
			for(int i=0; i < depts.size(); i++)
			{
				UDEPARTMENT d = depts.get(i);
				
				m_info.add_department(d.getLDeptpk(), d.getSzDeptid(), d.getSzStdcc(), d.getLbo(), d.getRbo(), 
					d.getUbo(), d.getBbo(), d.isFDefault(), d.getLDeptpri(), d.getLMaxalloc(), 
					d.getSzUserprofilename(), d.getSzUserprofiledesc(), d.getLStatus(), 
					d.getLNewsending(), d.getLParm(), d.getLFleetcontrol(), d.getLLba(), 
					d.getLHouseeditor(), d.getLAddresstypes(), d.getSzDefaultnumber(), d.getMunicipals().getUMunicipalDef(), d.getLPas(), d.getRestrictionShapes());
			}


			m_info.get_departments().CreateCombinedRestrictionShape();
			if(m_info.get_departments().get_combined_restriction_shape() != null) {
				List<ShapeStruct> list = m_info.get_departments().get_combined_restriction_shape();
				
				for(int i=0;i<list.size();++i) {
					
				}
			}
			variables.USERINFO = m_info;
			variables.NAVIGATION.setNavigation(new NavStruct(2.042989900708198, 8.180480787158013, 52.76231045722961, 51.548939180374144),false);
			m_mappane = new MapFrameAdmin(applet_width, applet_height, variables.DRAW, variables.NAVIGATION, new HTTPReq("http://vb4utv"), true);
			variables.MAPPANE = m_mappane;
			m_mappane.load_map();
			m_drawthread.set_mappane(m_mappane);
			
			m_mappane.initialize();
			add(m_mappane);
			b_results_ready = true;
			afterAfterLogon();
		}
		else if("act_download_houses".equals(e.getActionCommand())){
			variables.DRAW.set_neednewcoors(true);
			variables.DRAW.set_need_imageupdate();
		}
		else if("act_repaint".equals(e.getActionCommand())) {
				PAS.get_pas().kickRepaint();
			}
			else if("act_exit_application".equals(e.getActionCommand())) {
				PAS.get_pas().exit_application();
			}
			else if("act_initialize_default_values".equals(e.getActionCommand())) {
			}
			else if("act_loadmap".equals(e.getActionCommand())) {
				m_mappane.load_map(true);
				//PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(variables.NAVIGATION, ActionEvent.ACTION_PERFORMED, "act_maploaded"));
				//PAS.get_pas().kickRepaint();
			}
			else if("act_setzoom".equals(e.getActionCommand())) {
				//PAS.get_pas().get_mapproperties().set_zoom(((Double)e.getSource()).intValue());
			}
			else if("act_map_goto_point".equals(e.getActionCommand())) {
				NavPoint p = (NavPoint)e.getSource();
				m_navigation.exec_adrsearch(p.get_lon(), p.get_lat(), p.get_zoom());
			}
			else if("act_map_goto_area".equals(e.getActionCommand())) {
				NavStruct nav = (NavStruct)e.getSource();
				m_navigation.setNavigation(nav);
				m_mappane.load_map(true);
				PAS.get_pas().kickRepaint();
			}
			else if("act_show_world".equals(e.getActionCommand())) {
				//NavStruct nav = new NavStruct(-150, 150, 80, -80);
				m_navigation.setNavigation(Navigation.NAV_WORLD);
				m_mappane.load_map(true);
				PAS.get_pas().kickRepaint();
			}
			else if("act_center_all_polygon_sendings".equals(e.getActionCommand())) {
				try {
					PAS.get_pas().get_drawthread().set_suspended(true);
					SendObject obj;
					ArrayList<ShapeStruct> polygons = new ArrayList<ShapeStruct>();
					for(int i=0; i < PAS.get_pas().get_sendcontroller().get_sendings().size(); i++) {
						obj = (SendObject)PAS.get_pas().get_sendcontroller().get_sendings().get(i);
						//obj.get_sendproperties().typecast_poly().get_polygon();
						polygons.add(obj.get_sendproperties().get_shapestruct());
					}
					try {
						NavStruct nav = CommonFunc.calc_bounds(polygons.toArray());
						if(nav!=null)
							actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
					} catch(Exception err) {
						
					}
				} catch(Exception err) {
					System.out.println(err.getMessage());
					err.printStackTrace();
					Error.getError().addError("PAS", "Error centering all polygon sendings", err, Error.SEVERITY_ERROR);
				}
				PAS.get_pas().get_drawthread().set_suspended(false);
			}
			else if("act_toggle_houseselect".equals(e.getActionCommand())) {
//				MapFrameActionHandler.ActionHouseSelect h = (MapFrameActionHandler.ActionHouseSelect)e.getSource();
				PAS.get_pas().get_mainmenu().toggle_houseselect(true, true);
			}
			/* POLYGON EDITOR */
			else if("act_add_polypoint".equals(e.getActionCommand())) {
				m_mappane.actionPerformed(e);
			}		
			else if("act_rem_polypoint".equals(e.getActionCommand())) {
				m_mappane.actionPerformed(e);
			}
			else if("act_mouse_rightclick".equals(e.getActionCommand())) {
				m_mappane.actionPerformed(e);
			}		
			else if("act_mousesnap".equals(e.getActionCommand())) {
				m_mappane.actionPerformed(e);
			}		
			else if("act_mousemove".equals(e.getActionCommand())) {
				try
				{
					m_mappane.actionPerformed(e);
					PAS.get_pas().get_eastcontent().actionPerformed(e);
				}
				catch(Exception err)
				{
					
				}

			}		
			else if("act_check_mousesnap".equals(e.getActionCommand())) { //executed by thread in MapFrame when mouse is idle
				int n_x, n_y;
				n_x = ((MapPoint)e.getSource()).get_x();
				n_y = ((MapPoint)e.getSource()).get_y();			
				
				switch(m_mappane.get_mode()) {
					/*case MapFrame.MAP_MODE_SENDING_POLY:
						try {
							PolySnapStruct p = PAS.get_pas().get_sendcontroller().snap_to_point(new Point(n_x, n_y), 20);
							if(p==null && PAS.get_pas().get_parmcontroller()!=null) {
								p = PAS.get_pas().get_parmcontroller().snap_to_point(new Point(n_x, n_y), 20);
							}
							if(p != null) { //do snap
								SnapMouseEvent mouseevent = new SnapMouseEvent(PAS.get_pas(), 0, System.currentTimeMillis(), 16, p.p().x, p.p().y, 0, false);
								try {
									m_mappane.get_actionhandler().mouseMoved(mouseevent);
									m_mappane.set_current_snappoint(p);
								} catch(Exception err) {
									System.out.println(err.getMessage());
									err.printStackTrace();
									Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
								}
								//get_pas().get_mappane().robot_movecursor(p);
							} else {
								m_mappane.set_current_snappoint(null);
							}
						} catch(Exception err) {
							System.out.println(err.getMessage());
							err.printStackTrace();
							Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
						}	
						break;*/
					default:
						//PAS.get_pas().get_housecontroller().check_mouseover(n_x, n_y);
						//java.util.ArrayList<Object> arr = PAS.get_pas().get_housecontroller().get_found_houses();
						//if(arr.size()>0)
						//	arr = arr;
						//if(arr!=null)
						//	m_mappane.actionPerformed(new ActionEvent(arr, ActionEvent.ACTION_PERFORMED, "act_onmouseover_houses"));
						break;
				}
			}
			else if("act_start_repaint_cycling".equals(e.getActionCommand())) {
				//used for alert-statuses
				PAS.get_pas().execRepaintCycler(((Boolean)e.getSource()).booleanValue());
			}
			else if("act_activate_drawmode".equals(e.getActionCommand())) {
				try {
					switch(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_sendingtype()) {
						case SendProperties.SENDING_TYPE_POLYGON_:
							m_mappane.set_mode(MapFrame.MAP_MODE_SENDING_POLY);
							m_mappane.actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
							break;
						case SendProperties.SENDING_TYPE_CIRCLE_:
							m_mappane.set_mode(MapFrame.MAP_MODE_SENDING_ELLIPSE);
							m_mappane.actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
							break;
						case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
							break;
						case SendProperties.SENDING_TYPE_MUNICIPAL_:
							//m_mappane.set_mode()
							//m_mappane.actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
							break;
					}
				} catch(Exception err) {
					System.out.println(err.getMessage());
					err.printStackTrace();
					Error.getError().addError("PAS", "Error activating drawmode", err, Error.SEVERITY_ERROR);
				}
			}
			else if("act_set_active_shape".equals(e.getActionCommand())) {
				m_mappane.actionPerformed(e);
			}
			else if("act_activate_parm_drawmode".equals(e.getActionCommand())) {
				ShapeStruct s = (ShapeStruct)e.getSource();
				if(s.getClass().equals(PolygonStruct.class))
					m_mappane.set_mode(MapFrame.MAP_MODE_SENDING_POLY);
				else if(s.getClass().equals(EllipseStruct.class))
					m_mappane.set_mode(MapFrame.MAP_MODE_SENDING_ELLIPSE);
				else if(s.getClass().equals(UnknownShape.class))
					m_mappane.set_mode(MapFrame.MAP_MODE_ASSIGN_EPICENTRE);
				m_mappane.actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
			}
			else if("act_mousemoved".equals(e.getActionCommand())) {
			}
			/*else if("act_mousesnap_house".equals(e.getActionCommand())) {
				java.util.ArrayList arr = get_housecontroller().get_found_houses();
				if(arr!=null)
					get_mappane().get_actionhandler().actionPerformed(new ActionEvent(arr, ActionEvent.ACTION_PERFORMED, "act_onmouseover_houses"));
			}*/
			else if("act_search_houses".equals(e.getActionCommand())) {
				PAS.get_pas().get_statuscontroller().search_houses((Dimension)e.getSource());
			}
			else if("act_set_pinpoint".equals(e.getActionCommand())) {
				MapPointLL ll = (MapPointLL)e.getSource();
				m_mappane.set_pinpoint(ll);
				m_mappane.set_drawpinpoint(true);
				PAS.get_pas().get_mainmenu().actionPerformed(new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_force_searchpinpoint"));
			}
			else if("act_show_searchpinpoint".equals(e.getActionCommand())) {
				m_mappane.set_drawpinpoint(((Boolean)e.getSource()).booleanValue());
				PAS.get_pas().kickRepaint();
			}
			else if("act_invoke_project".equals(e.getActionCommand())) {
				if(PAS.get_pas().get_current_project()==null) {
					PAS.get_pas().invoke_project(true);
				}
			}
			else if("act_send_scenario".equals(e.getActionCommand())) {
				if(e.getSource().getClass().equals(AlertVO.class)) {
					PAS.get_pas().get_sendcontroller().insert_alert_sending((AlertVO)e.getSource());
				}
				else if(e.getSource().getClass().equals(EventVO.class)) {
					PAS.get_pas().get_sendcontroller().insert_event_sending((EventVO)e.getSource());
				}
			}
			else if("act_send_tas".equals(e.getActionCommand())) {
				ULBACOUNTRY c = (ULBACOUNTRY)e.getSource();
				PAS.get_pas().get_sendcontroller().insert_tas_sending(c);
			}
			else if("act_send_tas_multiple".equals(e.getActionCommand())) {
				List<ULBACOUNTRY> l = (List<ULBACOUNTRY>)e.getSource();
				PAS.get_pas().get_sendcontroller().insert_tas_multiple_sending(l);
			}
			else if("act_add_sending".equals(e.getActionCommand())) {
				if(e.getSource().getClass().equals(SendObject.class)) {
					PAS.get_pas().get_sendcontroller().add_sending((SendObject)e.getSource());
				}
			}
			else if("act_exec_snapsending".equals(e.getActionCommand())) {
				//PARM har sendt et objekt (Alert/Event) for snap sending via SOAP
				new Thread() {
					public void run()
					{
						PAS.get_pas().get_sendcontroller().execSoapSnapSendings(e.getSource(), "live");
					}
				}.start();
			}
			else if("act_exec_snapsimulation".equals(e.getActionCommand())) {
				//PARM har sendt et objekt (Alert/Event) for snap sending via SOAP
				new Thread() {
					public void run()
					{
						PAS.get_pas().get_sendcontroller().execSoapSnapSendings(e.getSource(), "simulate");
					}
				}.start();
				
			}
			else if("act_exec_snaptest".equals(e.getActionCommand())) {
				new Thread() {
					public void run()
					{
						PAS.get_pas().get_sendcontroller().execSoapSnapSendings(e.getSource(), "test");
					}
				}.start();
			}
			else if("act_sosi_parsing_complete".equals(e.getActionCommand())) {
				ArrayList<SendObject> sendings_found = (ArrayList<SendObject>)e.getSource();
				for(int i=0; i < sendings_found.size(); i++) {
					SendObject obj = (SendObject)sendings_found.get(i);
					PAS.get_pas().get_sendcontroller().add_sending(obj);
				}
			}
			else if("act_project_saved".equals(e.getActionCommand())) {
				if(!PAS.get_pas().get_keep_sendings()) {
					PAS.get_pas().get_sendcontroller().resetActiveProject();
				}
				PAS.get_pas().set_keep_sendings(false);
				System.out.println("Project saved");
				Project source = (Project)e.getSource();
				PAS.get_pas().activateProject(source);
			}
			else if("act_ready_for_coor_assignment_private".equals(e.getActionCommand())) {
				m_mappane.setCurrentInhabitant((Inhabitant)e.getSource());
				m_mappane.set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
				m_mappane.set_submode(MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR);
			}
			else if("act_ready_for_coor_assignment_company".equals(e.getActionCommand())) {
				m_mappane.setCurrentInhabitant((Inhabitant)e.getSource());
				m_mappane.set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
				m_mappane.set_submode(MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR);			
			}
			else if("act_houseeditor_setcoor_none".equals(e.getActionCommand())) {
				m_mappane.set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
				m_mappane.set_submode(MapFrame.MAP_HOUSEEDITOR_SET_COOR_NONE);
			}
			else if("act_close_houseeditor".equals(e.getActionCommand())) {
				//get_eastcontent().rem_houseeditor();
				//m_houseeditor = null;
			}
			else if("set_houseeditor_coor".equals(e.getActionCommand())) {
				PAS.get_pas().get_eastcontent().get_houseeditor().reinit((MapPoint)e.getSource(), m_mappane.get_mouseoverhouse());			
			}
			else if("act_maildelivery_success".equals(e.getActionCommand())) {
				System.out.println("MailCtrl reported success (Code: " + (Integer)e.getSource() + ")");
			}
			else if("act_maildelivery_failed".equals(e.getActionCommand())) {
				System.out.println("MailCtrl reported failure (Error: " + (Integer)e.getSource() + ")");
			}
			else if("act_messagelib".equals(e.getActionCommand()))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						new MessageLibDlg(PAS.get_pas());
						
					}
				});
			}
			
			else if("act_visualsettings_downloaded".equals(e.getActionCommand())) {
				UPASUISETTINGS ui = (UPASUISETTINGS)e.getSource();
				if(ui.isInitialized())
				{
					PAS.setLocale(ui.getSzLanguageid());
					//PAS.get_pas().setVisualSettings(ui);
					//PAS.get_pas().initSubstance();
				}
			}
			else if("act_save_settingsobject".equals(e.getActionCommand())) {
				//m_settings = (Settings)e.getSource();
				PAS.get_pas().set_settings((Settings)e.getSource());
				actionPerformed(new ActionEvent(e,ActionEvent.ACTION_PERFORMED,"act_save_settings"));
				//JOptionPane.showMessageDialog(null, "Settings saved");
			}
			else if("act_save_settings".equals(e.getActionCommand())) {
				try
				{
					//new XmlWriter().saveSettings();
					new WSSaveUI(this).start();
				}
				catch(Exception err)
				{
					
				}
			}
			else if("act_export_alert_polygon".equals(e.getActionCommand())) {
				//AlertVO [] a = { (AlertVO)e.getSource() };
				AlertVO [] a = (AlertVO[])e.getSource();
				new SosiExport(a);
			}
			
		}
		
	public void put(String id) {
		PolygonStruct s = null; 
		for(int i=0;i<variables.USERINFO.get_departments().size();++i){
			DeptInfo deptinfo = (DeptInfo)m_info.get_departments().get(i);
			if(deptinfo.get_deptpk() == Integer.parseInt(id)) { 
				List<ShapeStruct> rshapes = deptinfo.get_restriction_shapes();
				s = rshapes.get(0).typecast_polygon();
				break;
			}
			
		}
		
		if(variables.SENDCONTROLLER.get_activesending() == null) {
			SendObject so = new SendObject("New sending", SendProperties.SENDING_TYPE_PAINT_RESTRICTION_AREA_, 0, this, m_navigation);
			variables.SENDCONTROLLER.set_activesending(so);
			variables.SENDCONTROLLER.add_sending(so);
			sp = new SendPropertiesPolygon(s,new SendOptionToolbar(so,this,0), new Col());
			so.set_sendproperties(sp);
		}
		else {
			if(variables.SENDCONTROLLER.get_activesending().get_sendproperties().get_shapestruct().isObsolete())
				variables.SENDCONTROLLER.get_activesending().get_sendproperties().get_shapestruct().setHidden(true);
			variables.SENDCONTROLLER.get_activesending().get_sendproperties().set_shapestruct(s);
		}
		if(sp.get_shapestruct().isObsolete())
			sp.get_shapestruct().setHidden(false);
		sp.get_shapestruct().set_fill_color(Color.BLUE);
		variables.MAPPANE.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		variables.MAPPANE.set_mode(MapFrame.MAP_MODE_PAN);

		//variables.MAPPANE.setAllOverlaysDirty();
		//variables.DRAW.setNeedRepaint();
		variables.MAPPANE.kickRepaint();
		//variables.MAPPANE.load_map(true);
		
		
	}
	public void store(String name) {
		variables.MAPPANE.get_active_shape();
		no.ums.ws.pas.UPolygon shape = new no.ums.ws.pas.UPolygon();
		PolygonStruct polygon = variables.MAPPANE.get_active_shape().typecast_polygon();
		shape.setColAlpha(polygon.get_fill_color().getAlpha());
		shape.setColBlue(polygon.get_fill_color().getBlue());
		shape.setColGreen(polygon.get_fill_color().getGreen());
		shape.setColRed(polygon.get_fill_color().getRed());
		no.ums.ws.pas.UPolypoint p;
		for(int i=0;i<polygon.get_coors_lat().size();++i) {
			p = new no.ums.ws.pas.UPolypoint();
			p.setLat(polygon.get_coors_lat().get(i));
			p.setLon(polygon.get_coors_lon().get(i));
			shape.getPolypoint().add(p);
		}
		ArrayOfUShape arrShape = new ArrayOfUShape();
		arrShape.getUShape().add(shape);
		variables.USERINFO.get_current_department().get_restriction_shapes();
		variables.USERINFO.add_department(variables.USERINFO.get_default_dept().get_deptpk(),
				name, variables.USERINFO.get_default_dept().get_defaultnumber(),
				(float)variables.USERINFO.get_default_dept().get_nav_init()._lbo,
				(float)variables.USERINFO.get_default_dept().get_nav_init()._rbo,
				(float)variables.USERINFO.get_default_dept().get_nav_init()._ubo, 
				(float)variables.USERINFO.get_default_dept().get_nav_init()._bbo,
				false, 3, 150, "fjols", "", 1, 1, 1, 1, 1, 1, (long)1, "", null, 2, arrShape);
		
		
		variables.USERINFO.get_current_department().CalcCoorRestrictionShapes();
	}
	public void generateRestrictionShape(String[] coors) {
	
	}
	private PolygonStruct parseCoors(String coors){
		String[] l = coors.split("€");
		
		PolygonStruct s = new PolygonStruct(new Dimension(m_mappane.get_dimension().width,m_mappane.get_dimension().height));
		//PolygonStruct s = new PolygonStruct(new Dimension(640,480));
		
		String[] clat = l[0].split("\\|");
		String[] clon = l[1].split("\\|");
		
		for(int i=0;i<clat.length;++i) {
			s.add_coor(Double.parseDouble(clon[i].replace(',', '.')),Double.parseDouble(clat[i].replace(',', '.')));
		}
		return s;
	}
	public void addRestriction(String coors) {
		PolygonStruct s = parseCoors(coors);
		m_info.get_departments().m_combined_shapestruct_list.add(s);
		m_info.get_departments().CreateCombinedRestrictionShape();
		//set restriction shape
	}
	public void pan() {
		m_mappane.set_mode(MapFrame.MAP_MODE_PAN);
		//m_mappane.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
	}
	public void zoom() {
		m_mappane.set_mode(MapFrame.MAP_MODE_ZOOM);
	}
	public void draw() {
		m_mappane.set_mode(MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA);
		if(variables.SENDCONTROLLER.get_activesending() == null) {
			SendObject so = new SendObject("New sending", SendProperties.SENDING_TYPE_PAINT_RESTRICTION_AREA_, 0, this, m_navigation);
			variables.SENDCONTROLLER.set_activesending(so);
			variables.SENDCONTROLLER.add_sending(so);
			sp = new SendPropertiesPolygon(new PolygonStruct(new Dimension(applet_width,applet_height)),new SendOptionToolbar(so,this,0), new Col());
			so.set_sendproperties(sp);
		}
		else
			sp = variables.SENDCONTROLLER.get_activesending().get_sendproperties().typecast_poly();
		
		sp.set_color(Color.BLUE);
								
		m_mappane.actionPerformed(new ActionEvent(sp.get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
	}
	public String get(){
		ArrayList<Double> lat = m_mappane.get_active_shape().typecast_polygon().get_coors_lat();
		ArrayList<Double> lon = m_mappane.get_active_shape().typecast_polygon().get_coors_lon();
		String coors = "";
		
		for(int i=0;i<lat.size();++i) {
			coors += lat.get(i).toString();
			if(i+1<lat.size())
				coors += "|";
		}
		if(coors.length()>0)
			coors+="€";
		for(int i=0;i<lon.size();++i) {
			coors += lon.get(i).toString();
			if(i+1<lon.size())
				coors += "|";
		}
		
		
		return coors;
	}
	
	public boolean logon(String userid, String company, String password) {
		return true;
	}
}
 