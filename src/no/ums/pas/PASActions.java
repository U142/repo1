package no.ums.pas;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import no.ums.pas.core.variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.RightsManagement;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.SettingsGUI;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.StatusPanel;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.menus.defines.SubstanceMenuItem;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.ws.WSSaveUI;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavPoint;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.UnknownShape;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.SnapMouseEvent;
import no.ums.pas.send.messagelibrary.MessageLibDlg;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.pas.UPASUISETTINGS;
import no.ums.ws.pas.tas.ULBACOUNTRY;

import org.jvnet.substance.SubstanceLookAndFeel;
//Substance 5.2
//import org.jvnet.substance.api.SubstanceSkin;

public class PASActions implements ActionListener {

	//private boolean parm_open = false;
	
	public PASActions() {
		
	}
	
	public void actionPerformed(final ActionEvent e) {
		final ActionEvent event = e;
		
		if(PAS.get_pas().resizeWaitingTimer != null && e.getSource().equals(PAS.get_pas().resizeWaitingTimer))
		{
			PAS.get_pas().resizeWaitingTimer.stop();
			PAS.get_pas().resizeWaitingTimer = null;
			PAS.get_pas().applyResize(true);
		}
		else if("act_set_parm_closed".equals(e.getActionCommand())) {
			PAS.setParmOpen(false);//parm_open = false;
		}else if("act_set_theme".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					SubstanceMenuItem item = ((SubstanceMenuItem)event.getSource());
					//Substance 3.3
					SubstanceLookAndFeel.setCurrentTheme(item.get_theme());
					
					//Substance 5.2
					//SubstanceLookAndFeel.setSkin(new SubstanceSkin());
					
					PAS.get_pas().kickRepaint();
					//PAS.get_pas().setSubstanceTheme(item.get_skin());
					//PAS.get_pas().get_eastcontent().setSubstanceTheme(item.get_skin());
					//PAS.get_pas().get_mainmenu().get_searchframe().initUI();
				}
			});
		}
		else if("act_set_watermark".equals(e.getActionCommand()))
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					SubstanceMenuItem item = ((SubstanceMenuItem)event.getSource());
					//Substance 3.3
					SubstanceLookAndFeel.setCurrentWatermark(item.get_watermark());
					
					PAS.get_pas().kickRepaint();
				}
			});
		}
		else if("act_set_skin".equals(e.getActionCommand()))
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					SubstanceMenuItem item = ((SubstanceMenuItem)event.getSource());
					SubstanceLookAndFeel.setSkin(item.get_skin());
				}
			});
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
			PAS.get_pas().get_mappane().load_map(true);
			PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(variables.NAVIGATION, ActionEvent.ACTION_PERFORMED, "act_maploaded"));
			//PAS.get_pas().kickRepaint();
		}
		else if("act_download_houses".equals(e.getActionCommand())) {
			PAS.get_pas().download_houses();
			if(PAS.get_pas().get_eastcontent()!=null)
				PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(new Integer(no.ums.pas.core.controllers.HouseController.HOUSE_DOWNLOAD_IN_PROGRESS_), ActionEvent.ACTION_PERFORMED, "act_download_houses"));
		}
		else if("act_download_houses_report".equals(e.getActionCommand())) {
			//PAS.get_pas().get_eastcontent().actionPerformed(e);
			Integer i = (Integer)e.getSource();
			try
			{
				if(PAS.get_pas().get_eastcontent().get_infopanel()!=null)
					PAS.get_pas().get_eastcontent().get_infopanel().set_housesdownload_status(i.intValue());
			} catch(Exception err)
			{
				
			}

		}
		else if("act_setzoom".equals(e.getActionCommand())) {
			PAS.get_pas().get_mapproperties().set_zoom(((Double)e.getSource()).intValue());
		}
		else if("act_map_goto_point".equals(e.getActionCommand())) {
			NavPoint p = (NavPoint)e.getSource();
			variables.NAVIGATION.exec_adrsearch(p.get_lon(), p.get_lat(), p.get_zoom());
		}
		else if("act_map_goto_area".equals(e.getActionCommand())) {
			NavStruct nav = (NavStruct)e.getSource();
			variables.NAVIGATION.setNavigation(nav);
			PAS.get_pas().get_mappane().load_map(true);
			PAS.get_pas().kickRepaint();
		}
		else if("act_show_world".equals(e.getActionCommand())) {
			//NavStruct nav = new NavStruct(-150, 150, 80, -80);
			variables.NAVIGATION.setNavigation(Navigation.NAV_WORLD);
			PAS.get_pas().get_mappane().load_map(true);
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
//			MapFrameActionHandler.ActionHouseSelect h = (MapFrameActionHandler.ActionHouseSelect)e.getSource();
			PAS.get_pas().get_mainmenu().toggle_houseselect(true, true);
		}
		/* POLYGON EDITOR */
		else if("act_add_polypoint".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().actionPerformed(e);
		}		
		else if("act_rem_polypoint".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().actionPerformed(e);
		}
		else if("act_mouse_rightclick".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().actionPerformed(e);
		}		
		else if("act_mousesnap".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().actionPerformed(e);
		}		
		else if("act_mousemove".equals(e.getActionCommand())) {
			try
			{
				PAS.get_pas().get_mappane().actionPerformed(e);
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
			
			switch(PAS.get_pas().get_mappane().get_mode()) {
				/*case MapFrame.MAP_MODE_SENDING_POLY:
					try {
						PolySnapStruct p = PAS.get_pas().get_sendcontroller().snap_to_point(new Point(n_x, n_y), 20);
						if(p==null && PAS.get_pas().get_parmcontroller()!=null) {
							p = PAS.get_pas().get_parmcontroller().snap_to_point(new Point(n_x, n_y), 20);
						}
						if(p != null) { //do snap
							SnapMouseEvent mouseevent = new SnapMouseEvent(PAS.get_pas(), 0, System.currentTimeMillis(), 16, p.p().x, p.p().y, 0, false);
							try {
								PAS.get_pas().get_mappane().get_actionhandler().mouseMoved(mouseevent);
								PAS.get_pas().get_mappane().set_current_snappoint(p);
							} catch(Exception err) {
								System.out.println(err.getMessage());
								err.printStackTrace();
								Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
							}
							//get_pas().get_mappane().robot_movecursor(p);
						} else {
							PAS.get_pas().get_mappane().set_current_snappoint(null);
						}
					} catch(Exception err) {
						System.out.println(err.getMessage());
						err.printStackTrace();
						Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
					}	
					break;*/
				default:
					PAS.get_pas().get_housecontroller().check_mouseover(n_x, n_y);
					java.util.ArrayList<Object> arr = PAS.get_pas().get_housecontroller().get_found_houses();
					if(arr.size()>0)
						arr = arr;
					if(arr!=null)
						PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(arr, ActionEvent.ACTION_PERFORMED, "act_onmouseover_houses"));
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
						PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
						PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
						break;
					case SendProperties.SENDING_TYPE_PAINT_RESTRICTION_AREA_:
						PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA);
						PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
						break;
					case SendProperties.SENDING_TYPE_CIRCLE_:
						PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_ELLIPSE);
						PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
						break;
					case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
						break;
					case SendProperties.SENDING_TYPE_MUNICIPAL_:
						//PAS.get_pas().get_mappane().set_mode()
						//PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
						break;
				}
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("PAS", "Error activating drawmode", err, Error.SEVERITY_ERROR);
			}
		}
		else if("act_set_active_shape".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().actionPerformed(e);
		}
		else if("act_activate_parm_drawmode".equals(e.getActionCommand())) {
			ShapeStruct s = (ShapeStruct)e.getSource();
			if(s.getClass().equals(PolygonStruct.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
			else if(s.getClass().equals(EllipseStruct.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_ELLIPSE);
			else if(s.getClass().equals(UnknownShape.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_ASSIGN_EPICENTRE);
			PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
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
			PAS.get_pas().get_mappane().set_pinpoint(ll);
			PAS.get_pas().get_mappane().set_drawpinpoint(true);
			PAS.get_pas().get_mainmenu().actionPerformed(new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_force_searchpinpoint"));
		}
		else if("act_show_searchpinpoint".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().set_drawpinpoint(((Boolean)e.getSource()).booleanValue());
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
			new Thread("Snap sending LIVE thread") {
				public void run()
				{
					PAS.get_pas().get_sendcontroller().execSoapSnapSendings(e.getSource(), "live");
				}
			}.start();
		}
		else if("act_exec_snapsimulation".equals(e.getActionCommand())) {
			//PARM har sendt et objekt (Alert/Event) for snap sending via SOAP
			new Thread("Snap sending SIMULATE thread") {
				public void run()
				{
					PAS.get_pas().get_sendcontroller().execSoapSnapSendings(e.getSource(), "simulate");
				}
			}.start();
			
		}
		else if("act_exec_snaptest".equals(e.getActionCommand())) {
			new Thread("Snap sending TEST thread") {
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
		else if("act_project_activate".equals(e.getActionCommand())) {
			PAS.get_pas().m_statuscontroller = PAS.pasplugin.onCreateStatusController();
			variables.STATUSCONTROLLER = PAS.get_pas().m_statuscontroller;
			PAS.get_pas().get_eastcontent().reloadStatusPanel(false);
			PAS.get_pas().get_sendcontroller().resetActiveProject();
			PAS.get_pas().set_keep_sendings(false);
			System.out.println("Project Opened");
			Project source = (Project)e.getSource();
			PAS.get_pas().activateProject(source);	
			PAS.get_pas().get_eastcontent().ensure_added(EastContent.PANEL_STATUS_LIST);
			
		}
		else if("act_eastcontent_pane_opened".equals(e.getActionCommand())) {
			if(e.getSource().getClass().equals(StatusPanel.class)) {
				if(!PAS.get_pas().get_current_project().get_projectpk().equals(PAS.get_pas().get_statuscontroller().get_current_projectpk()))
				{
					new Thread("Get statusitems thread")
					{
						public void run()
						{
							PAS.get_pas().get_statuscontroller().retrieve_statusitems(PAS.get_pas(), PAS.get_pas().get_current_project().get_projectpk(), -1, true);
						}
					}.start();
				}
			}
		}
		else if("act_no_project".equals(e.getActionCommand())) {
			PAS.get_pas().setTitle(PAS.get_pas().get_maintitle() + "        " + e.getSource().toString());
		}
//		else if("act_set_new_active_project".equals(e.getActionCommand())) {
//			// Skal sette nytt prosjekt, må 
//			//Project.this.
//			Project source = (Project)e.getSource();
//		}
		else if("act_cancel_openproject".equals(e.getActionCommand())) {
			
		}
		else if("act_set_ellipse_center".equals(e.getActionCommand()) || "act_set_polygon_ellipse_center".equals(e.getActionCommand())) {
			try {
				//get_sendcontroller().get_activesending().get_sendproperties().typecast_ellipse().get_ellipse().set_ellipse(get_navigation(), p, p);
				PAS.get_pas().get_mappane().actionPerformed(e);
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("PAS", "Error setting ellipse center", err, Error.SEVERITY_ERROR);
			}
			PAS.get_pas().kickRepaint();
		}
		else if("act_set_ellipse_corner".equals(e.getActionCommand()) || "act_set_polygon_ellipse_corner".equals(e.getActionCommand())) {
			try {
				//get_sendcontroller().get_activesending().get_sendproperties().typecast_ellipse().get_ellipse().set_ellipse_corner(get_navigation(), p);
				PAS.get_pas().get_mappane().actionPerformed(e);
				PAS.get_pas().kickRepaint();
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("PAS", "Error setting ellipse corner", err, Error.SEVERITY_ERROR);
			}				
		}
		else if("act_set_ellipse_complete".equals(e.getActionCommand())) {
			
		}
		else if("act_enable_houseeditor".equals(e.getActionCommand())) {
			PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_HOUSEEDITOR_);
		}
		else if("act_houseeditor_newadr".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_NEWPOS);
		}
		else if("act_ready_for_coor_assignment_private".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().setCurrentInhabitant((Inhabitant)e.getSource());
			PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR);
		}
		else if("act_ready_for_coor_assignment_company".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().setCurrentInhabitant((Inhabitant)e.getSource());
			PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR);			
		}
		else if("act_houseeditor_setcoor_none".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_COOR_NONE);
		}
		else if("act_close_houseeditor".equals(e.getActionCommand())) {
			//get_eastcontent().rem_houseeditor();
			//m_houseeditor = null;
		}
		else if("set_houseeditor_coor".equals(e.getActionCommand())) {
			PAS.get_pas().get_eastcontent().get_houseeditor().reinit((MapPoint)e.getSource(), PAS.get_pas().get_mappane().get_mouseoverhouse());			
		}
		else if("act_start_parm".equals(e.getActionCommand())) {
			if(PAS.get_pas().get_parmcontroller()==null && PAS.isParmOpen() == false) {
				//parm_open = true;
				//PAS.get_pas().m_parmcontroller = new ParmController(get_sitename(), get_userinfo());
				PAS.setParmOpen(PAS.pasplugin.onStartParm());
			}
		}
		else if("act_restart_parm".equals(e.getActionCommand())) {
			PAS.get_pas().close_parm(false);
			PAS.setParmOpen(false);//parm_open = false;
			actionPerformed(new ActionEvent(new String(""), ActionEvent.ACTION_PERFORMED, "act_start_parm"));
		}
		else if("act_refresh_parm".equals(e.getActionCommand())) {
			PAS.pasplugin.onRefreshParm();
			//PAS.get_pas().get_parmcontroller().getUpdateXML().saveProject();
		}
		else if("act_close_parm".equals(e.getActionCommand())) {
			PAS.pasplugin.onCloseParm();
			//PAS.get_pas().close_parm(false);
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
		else if("act_show_settings".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					SettingsGUI sGui = new SettingsGUI(PAS.get_pas());
					if(PAS.get_pas().get_settings()!= null) {
						if(PAS.get_pas().get_settings().getUsername().length()<1)
							sGui.getM_txt_username().setText(PAS.get_pas().get_userinfo().get_userid());
						else
							sGui.getM_txt_username().setText(PAS.get_pas().get_settings().getUsername());
						if(PAS.get_pas().get_settings().getCompany().length()<1)
							sGui.getM_txt_company().setText(PAS.get_pas().get_userinfo().get_compid());
						else
							sGui.getM_txt_company().setText(PAS.get_pas().get_settings().getCompany());
						if(PAS.get_pas().get_settings().parm())
							sGui.getM_chk_start_parm().setSelected(true);
						else
							sGui.getM_chk_start_parm().setSelected(false);
						/*if(PAS.get_pas().get_settings().fleetcontrol())
							sGui.getM_chk_start_fleetcontrol().setSelected(true);
						else
							sGui.getM_chk_start_fleetcontrol().setSelected(false);*/
						sGui.getM_txt_lba_refresh().setText(String.valueOf(PAS.get_pas().get_settings().getLbaRefresh()));
						//if(PAS.get_pas().get_settings().getMapServer())
						sGui.setMapServer(PAS.get_pas().get_settings().getMapServer());
						sGui.setWmsUser(PAS.get_pas().get_settings().getWmsUsername());
						sGui.setWmsPassword(PAS.get_pas().get_settings().getWmsPassword());
						sGui.setWmsSite(PAS.get_pas().get_settings().getWmsSite());
						if(PAS.get_pas().get_settings().getPanByDrag())
							sGui.getM_btn_pan_by_drag().doClick();
						else
							sGui.getM_btn_pan_by_click().doClick();
					}
					if(PAS.get_pas().get_userinfo().get_mailaccount() != null) {
						sGui.getM_txt_mail_displayname().setText(PAS.get_pas().get_userinfo().get_mailaccount().get_displayname());
						sGui.getM_txt_mail_address().setText(PAS.get_pas().get_userinfo().get_mailaccount().get_mailaddress());
						sGui.getM_txt_mail_outgoing().setText(PAS.get_pas().get_userinfo().get_mailaccount().get_mailserver());
					}
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
		else if("act_change_department".equals(e.getActionCommand())) {
			/*
			if(PAS.get_pas().get_parmcontroller() != null)
				parm_open = true;
			else
				parm_open = false;*/
			if(PAS.isParmOpen())
				PAS.get_pas().close_parm(false);
			CheckItem dept = (CheckItem)e.getSource();
			PAS.get_pas().get_userinfo().set_current_department((DeptInfo)dept.get_value());
			PAS.get_pas().get_eastcontent().get_infopanel().actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo(), ActionEvent.ACTION_PERFORMED, "act_update_accountinfo"));
			PAS.get_pas().set_rightsmanagement(new RightsManagement(PAS.get_pas().get_userinfo().get_current_department().get_userprofile()));
			/* @ Update max channels on department change
			 * 
			 */
			//if(PAS.get_pas().get_eastcontent().get_statuspanel() != null) { // Update max channels
			//	 for(int i=0;i<PAS.get_pas().get_eastcontent().get_tabbedpane().size();++i)
			//}
			actionPerformed(new ActionEvent(dept, ActionEvent.ACTION_PERFORMED, "act_dept_changed"));
			// La til denne for å enten ta vare på eller slette sendingene ved department bytte
			if(PAS.get_pas().get_current_project()!=null) {
				//PAS.get_pas().close_active_project(true, true);
				actionPerformed(new ActionEvent(PAS.get_pas().get_current_project(), ActionEvent.ACTION_PERFORMED, "act_project_saved"));
				// Close project here (hopefully removes the tab)
				//PAS.get_pas().close_active_project(true, true);
				//PAS.get_pas().get_eastcontent().remove_tab(EastContent.PANEL_SENDING_);
			}
			/*switch(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights())
			{
			case 4: //TAS
				actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo().get_current_department().get_nav_init(), ActionEvent.ACTION_PERFORMED, "act_show_world"));
				break;
			default: 
				if(PAS.get_pas().get_userinfo().get_current_department().get_nav_init()!=null)
					actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo().get_current_department().get_nav_init(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			}*/
			PAS.pasplugin.onSetInitialMapBounds(variables.NAVIGATION, PAS.get_pas().get_userinfo());
			PAS.get_pas().get_mappane().load_map(false);
			/*
			// Denne skal oppdatere weatherdata
			try
			{
				//SwingUtilities.invokeLater(new Runnable() {
				//	public void run()
					{
						try 
						{
							if(!parm_open)
								actionPerformed(new ActionEvent(dept, ActionEvent.ACTION_PERFORMED, "act_start_parm"));
							PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(variables.NAVIGATION, ActionEvent.ACTION_PERFORMED, "act_maploaded"));
						}
						catch(Exception err) {}
					}
				//});
			}catch(Exception err) { }
			*/
		}
		else if("act_dept_changed".equals(e.getActionCommand())) {
			deptChanged();
		}
		else if("act_export_alert_polygon".equals(e.getActionCommand())) {
			//AlertVO [] a = { (AlertVO)e.getSource() };
			AlertVO [] a = (AlertVO[])e.getSource();
			new no.ums.pas.importer.SosiExport(a);
		}
	}
	
	public void deptChanged() {
		PAS.pasplugin.onDepartmentChanged(PAS.get_pas());
		PAS.pasplugin.onSetUserLookAndFeel(PAS.get_pas().get_settings(), PAS.get_pas().get_userinfo());

		
		/*if(!PAS.get_pas().get_rightsmanagement().read_parm()) {
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().get_parm().setEnabled(false);
			if(PAS.isParmOpen())//if(PAS.get_pas().get_parmcontroller() != null)
				PAS.get_pas().actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "act_close_parm"));
		}
		else if(PAS.get_pas().get_rightsmanagement().read_parm() && PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() != 4) {
			if(PAS.get_pas().get_parmcontroller()==null && PAS.get_pas().get_settings().parm()) {
				actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_start_parm"));
			}	
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().get_parm().setEnabled(true);
		}
		if(!PAS.get_pas().get_rightsmanagement().read_fleetcontrol()) {
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().get_fleetcontrol().setEnabled(false);
		}
		else
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().get_fleetcontrol().setEnabled(true);
		
		if(PAS.get_pas().get_rightsmanagement().cansend() || PAS.get_pas().get_rightsmanagement().cansimulate()) {
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().showNewProject(true); //get_file_new_project().setEnabled(true);
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().showNewSending(true);//get_file_new_sending().setEnabled(true);
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().showFileImport(true);//get_file_import().setEnabled(true);
		}
		else {
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().showNewProject(false); //get_file_new_project().setEnabled(false);
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().showNewSending(false); //.setEnabled(false);
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().showFileImport(false);//get_file_import().setEnabled(false);
			PAS.get_pas().close_active_project(true, true);
		}
		switch(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights())
		{
		case 0:
			//none
			System.exit(0);
			break;
		case 1:
		case 2:
			PAS.get_pas().get_mainmenu().setTASMode(false);
			PAS.get_pas().setAppTitle("");
			PAS.get_pas().get_eastcontent().remove_tab(EastContent.PANEL_TAS_);
			PAS.get_pas().m_mainmenu.enable_mapsite(true);
			break;
		case 4: //TAS
			PAS.get_pas().get_mainmenu().setTASMode(true);
			PAS.get_pas().setAppTitle("UMS - " + PAS.l("main_tas_appname"));
			PAS.get_pas().get_eastcontent().InitTAS();
			PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_TAS_);
			PAS.get_pas().m_mainmenu.enable_mapsite(false);
			break;
		}
	
		
		if(PAS.get_pas().get_rightsmanagement().status())
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().get_status().setEnabled(true);
		else
			PAS.get_pas().m_mainmenu.get_selectmenu().get_bar().get_status().setEnabled(false);	
		
		//if(PAS.get_pas().get_rightsmanagement().houseeditor()>=1)
		PAS.get_pas().m_mainmenu.setHouseeditorEnabled((PAS.get_pas().get_rightsmanagement().houseeditor()>=1 ? true : false));
		*/
	}	
}


