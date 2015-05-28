package no.ums.pas;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.RightsManagement;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.StatusPanel;
import no.ums.pas.core.menus.OtherActions;
import no.ums.pas.core.menus.ViewOptions;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.menus.defines.SubstanceMenuItem;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.ws.WSSaveUI;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.*;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.ULBACOUNTRY;
import no.ums.ws.common.parm.UPASUISETTINGS;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//Substance 5.2
//import org.jvnet.substance.api.SubstanceSkin;

public class PASActions implements ActionListener {

    private static final Log log = UmsLog.getLogger(PASActions.class);
    private Set<ArrayList<HouseItem>> selectedHouses = new HashSet<ArrayList<HouseItem>>();

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
		}
		else if("act_set_predefinedarea_closed".equals(e.getActionCommand())) {
			PAS.setPredefinedAreaOpen(false);//m_b_predefinedArea_open = false;
		}
		else if("act_set_theme".equals(e.getActionCommand())) {
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
		else if("act_initialize_default_values".equals(e.getActionCommand())) {
		}
		else if("act_loadmap".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().load_map(true);
			PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(Variables.getNavigation(), ActionEvent.ACTION_PERFORMED, "act_maploaded"));
			//PAS.get_pas().kickRepaint();
		}
		else if("act_download_houses".equals(e.getActionCommand())) {
			PAS.get_pas().download_houses();
			if(PAS.get_pas().get_eastcontent()!=null)
				PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(no.ums.pas.core.controllers.HouseController.HOUSE_DOWNLOAD_IN_PROGRESS_, ActionEvent.ACTION_PERFORMED, "act_download_houses"));
		}
		else if("act_download_houses_report".equals(e.getActionCommand())) {
			//PAS.get_pas().get_eastcontent().actionPerformed(e);
			Integer i = (Integer)e.getSource();
			try
			{
				if(PAS.get_pas().get_eastcontent().get_infopanel()!=null)
					PAS.get_pas().get_eastcontent().get_infopanel().set_housesdownload_status(i);
			} catch(Exception err)
			{
				
			}
			PAS.get_pas().kickRepaint();

		}
		else if("act_setzoom".equals(e.getActionCommand())) {
			PAS.get_pas().get_mapproperties().set_zoom(((Double)e.getSource()).intValue());
		}
		else if("act_map_goto_point".equals(e.getActionCommand())) {
			NavPoint p = (NavPoint)e.getSource();
			Variables.getNavigation().exec_adrsearch(p.get_lon(), p.get_lat(), p.get_zoom());
		}
		else if("act_map_goto_area".equals(e.getActionCommand())) {
			NavStruct nav = (NavStruct)e.getSource();
			if(Variables.getNavigation().setNavigation(nav))
				PAS.get_pas().get_mappane().load_map(true);
			PAS.get_pas().kickRepaint();
		}
		else if("act_show_world".equals(e.getActionCommand())) {
			//NavStruct nav = new NavStruct(-150, 150, 80, -80);
			//if(Variables.getNavigation().setNavigation(Navigation.NAV_WORLD))
			//	PAS.get_pas().get_mappane().load_map(true);
			Variables.getMapFrame().getMapModel().setZoom(2);
			Variables.getMapFrame().getMapModel().setTopLeft(new LonLat(-180, 90));
			PAS.get_pas().kickRepaint();
		}
		else if("act_center_all_polygon_sendings".equals(e.getActionCommand())) {
			try {
				NavStruct nav_total = new NavStruct();
				for(SendObject so : Variables.getSendController().get_sendings())
				{
					ShapeStruct ss = so.get_sendproperties().get_shapestruct();
					NavStruct nav = ss.calc_bounds();
					if(nav!=null && ss.hasValidBounds())
					{
						nav_total = nav_total.appendTo(nav);
					}
				}
				if(nav_total.isSet())
				{
					actionPerformed(new ActionEvent(nav_total, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));						
				}

			} catch(Exception err) {
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
				Error.getError().addError("PAS", "Error centering all polygon sendings", err, Error.SEVERITY_ERROR);
			}
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
						PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_POLY);
						PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
						break;
					case SendProperties.SENDING_TYPE_PAINT_RESTRICTION_AREA_:
						PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.PAINT_RESTRICTIONAREA);
						PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
						break;
					case SendProperties.SENDING_TYPE_CIRCLE_:
						PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_ELLIPSE);
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
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
				Error.getError().addError("PAS", "Error activating drawmode", err, Error.SEVERITY_ERROR);
			}
		}
		else if("act_set_active_shape".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().actionPerformed(e);
		}
		else if("act_activate_parm_drawmode".equals(e.getActionCommand())) {
			ShapeStruct s = (ShapeStruct)e.getSource();
			if(s.getClass().equals(PolygonStruct.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_POLY);
			else if(s.getClass().equals(EllipseStruct.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_ELLIPSE);
			else if(s.getClass().equals(GISShape.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_POLY);
			else if(s.getClass().equals(UnknownShape.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.ASSIGN_EPICENTRE);
			PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		}
		else if("act_activate_area_drawmode".equals(e.getActionCommand())) {
			ShapeStruct s = (ShapeStruct)e.getSource();
			if(s.getClass().equals(PolygonStruct.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_POLY);
			else if(s.getClass().equals(EllipseStruct.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_ELLIPSE);
			//else if(s.getClass().equals(GISShape.class))
				//PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_POLY);
			else if(s.getClass().equals(UnknownShape.class))
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.ASSIGN_EPICENTRE);
			PAS.get_pas().get_mappane().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		}
		else if("act_mousemoved".equals(e.getActionCommand())) {
		}
		else if("act_search_houses".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					PAS.get_pas().get_statuscontroller().search_houses(-99999, false, (MapPointLL)e.getSource());					
				}
			});
		}
		else if("act_set_pinpoint".equals(e.getActionCommand())) {
			MapPointLL ll = (MapPointLL)e.getSource();
			PAS.get_pas().get_mappane().set_pinpoint(ll);
            ViewOptions.TOGGLE_SEARCHPOINTS.setSelected(true);
            ViewOptions.TOGGLE_SEARCHPOINTS.setSelected(true);
		}
		else if("act_show_searchpinpoint".equals(e.getActionCommand())) {
            ViewOptions.TOGGLE_SEARCHPOINTS.setSelected(((Boolean)e.getSource()).booleanValue());
            PAS.get_pas().kickRepaint();
		}
		else if("act_invoke_project".equals(e.getActionCommand())) {
			if(PAS.get_pas().get_current_project()==null) {
				PAS.get_pas().invoke_project(true);
			}
		}
		else if("act_send_scenario".equals(e.getActionCommand())) {
			if(e.getSource().getClass().equals(AlertVO.class)) {
				try {
					AlertVO avo = ((AlertVO)e.getSource()).clone();
					PAS.get_pas().get_sendcontroller().insert_alert_sending(avo);
					if(avo.getM_shape()!=null)
					{
						NavStruct nav = avo.getM_shape().calc_bounds();
						if(nav!=null && avo.getM_shape().hasValidBounds())
						{
							actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
						}
					}
				} catch(Exception ex) { Error.getError().addError("PASActions", "Generate sending from Alert failed", ex, Error.SEVERITY_ERROR); }
			}
			else if(e.getSource().getClass().equals(EventVO.class)) {
				try {
					EventVO evo = ((EventVO)e.getSource()).clone();
					PAS.get_pas().get_sendcontroller().insert_event_sending(evo);
					NavStruct nav_total = new NavStruct();
					for(Object o : evo.getAlertListe())
					{
						AlertVO avo = (AlertVO)o;
						NavStruct nav = avo.getM_shape().calc_bounds();
						if(nav!=null && avo.getM_shape().hasValidBounds())
						{
							nav_total = nav_total.appendTo(nav);
						}
					}
					if(nav_total.isSet())
					{
						actionPerformed(new ActionEvent(nav_total, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));						
					}
				} catch(Exception ex) { Error.getError().addError("PASActions", "Generate sending from Event failed", ex, Error.SEVERITY_ERROR); }
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
				PAS.get_pas().get_sendcontroller().add_sending((SendObject)e.getSource(), false);
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
				PAS.get_pas().get_sendcontroller().add_sending(obj, true, false);
			}
		}
		else if("act_project_saved".equals(e.getActionCommand())) {
			if(!PAS.get_pas().get_keep_sendings()) {
				PAS.get_pas().get_sendcontroller().resetActiveProject();
			}
			PAS.get_pas().set_keep_sendings(false);
			log.debug("Project saved");
			Project source = (Project)e.getSource();
			PAS.get_pas().activateProject(source);
		}
		else if("act_project_activate".equals(e.getActionCommand())) {
			PAS.get_pas().m_statuscontroller = PAS.pasplugin.onCreateStatusController();
			Variables.setStatusController(PAS.get_pas().m_statuscontroller);
			PAS.get_pas().get_eastcontent().reloadStatusPanel(false);
			if(!PAS.get_pas().get_keep_sendings())
			{
				PAS.get_pas().get_sendcontroller().resetActiveProject();
			}
			PAS.get_pas().set_keep_sendings(false);
			log.debug("Project Opened");
			if(e.getSource() instanceof Project)
			{
				Project source = (Project)e.getSource();
				PAS.get_pas().activateProject(source);	
				PAS.get_pas().get_eastcontent().ensure_added(EastContent.PANEL_STATUS_LIST);
			}
			
		}
		else if("act_eastcontent_pane_opened".equals(e.getActionCommand())) {
			if(e.getSource().getClass().equals(StatusPanel.class)) {
				if(!PAS.get_pas().get_current_project().get_projectpk().equals(PAS.get_pas().get_statuscontroller().get_current_projectpk()))
				{
                    //PAS.get_pas().close_active_project(true, false);
					PAS.get_pas().set_keep_sendings(true);
                    PAS.pasplugin.onOpenProject(PAS.get_pas().get_current_project(), -1);
                    PAS.get_pas().get_statuscontroller().retrieve_statusitems(PAS.get_pas(), PAS.get_pas().get_current_project().get_projectpk(), -1, true);
				}
			}
		}
		else if("act_no_project".equals(e.getActionCommand())) {
			PAS.get_pas().setTitle(PAS.get_pas().get_maintitle() + "        " + e.getSource().toString());
		}

		else if("act_cancel_openproject".equals(e.getActionCommand())) {
			
		}
		else if("act_set_ellipse_center".equals(e.getActionCommand()) || "act_set_polygon_ellipse_center".equals(e.getActionCommand())) {
			try {
				//get_sendcontroller().get_activesending().get_sendproperties().typecast_ellipse().get_ellipse().set_ellipse(get_navigation(), p, p);
				PAS.get_pas().get_mappane().actionPerformed(e);
			} catch(Exception err) {
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
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
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
				Error.getError().addError("PAS", "Error setting ellipse corner", err, Error.SEVERITY_ERROR);
			}				
		}
		else if("act_set_ellipse_complete".equals(e.getActionCommand())) {
			
		}
		else if("act_enable_houseeditor".equals(e.getActionCommand())) {
			PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_HOUSEEDITOR_);
		}
		else if("act_houseeditor_newadr".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.HOUSEEDITOR);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_NEWPOS);
		}
		else if("act_ready_for_coor_assignment_private".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().setCurrentInhabitant((Inhabitant)e.getSource());
			PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.HOUSEEDITOR);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR);
		}
		else if("act_ready_for_coor_assignment_company".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().setCurrentInhabitant((Inhabitant)e.getSource());
			PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.HOUSEEDITOR);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR);			
		}
		else if("act_houseeditor_setcoor_none".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.HOUSEEDITOR);
			PAS.get_pas().get_mappane().set_submode(MapFrame.MAP_HOUSEEDITOR_SET_COOR_NONE);
		}
		else if("act_close_houseeditor".equals(e.getActionCommand())) {
			//get_eastcontent().rem_houseeditor();
			//m_houseeditor = null;
		}
		else if("set_houseeditor_coor".equals(e.getActionCommand())) {
			PAS.get_pas().get_eastcontent().get_houseeditor().reinit((MapPoint)e.getSource(), PAS.get_pas().get_mappane().get_mouseoverhouse());			
		}
        else if("set_selected_house".equals(e.getActionCommand())) {
            // Add the house to a sending list, or remove from a sending list

            ArrayList<HouseItem> mouseoverhouse = PAS.get_pas().get_mappane().get_mouseoverhouse();
            if (mouseoverhouse != null) {
                if(selectedHouses.contains(mouseoverhouse)) {
                    selectedHouses.remove(mouseoverhouse);
                } else {
                    selectedHouses.add(mouseoverhouse);
                }
            }

            //TODO: Move this to the proper location
            //Create gis listtest
            GISList gisList = Utils.convertHouseItemsToGisList(selectedHouses);
            System.out.println(gisList);

            //TODO: Move this to the proper location
            Variables.getSendController().get_activesending().get_sendproperties().typecast_gis().set_gislist(gisList);
            Variables.getSendController().get_activesending().get_sendproperties().set_shapestruct(new GISShape(gisList));

        }
        else if("act_clear_selected_houses".equals(e.getActionCommand())) {
            selectedHouses.clear();
        }
		else if("act_restart_parm".equals(e.getActionCommand())) {
			PAS.get_pas().close_parm(false);
			PAS.setParmOpen(false);//parm_open = false;
            OtherActions.PARM_START.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_start_parm"));
		}
		else if("act_maildelivery_success".equals(e.getActionCommand())) {
			log.debug("MailCtrl reported success (Code: " + (Integer) e.getSource() + ")");
		}
		else if("act_maildelivery_failed".equals(e.getActionCommand())) {
			log.debug("MailCtrl reported failure (Error: " + (Integer) e.getSource() + ")");
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
			//if(PAS.isParmOpen())
			//	PAS.get_pas().close_parm(false);
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
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					PAS.pasplugin.onDepartmentChanged(PAS.get_pas());
				}
			});
			//actionPerformed(new ActionEvent(dept, ActionEvent.ACTION_PERFORMED, "act_dept_changed"));
			// La til denne for å enten ta vare på eller slette sendingene ved department bytte
			if(PAS.get_pas().get_current_project()!=null) {
				//PAS.get_pas().close_active_project(true, true);
				actionPerformed(new ActionEvent(PAS.get_pas().get_current_project(), ActionEvent.ACTION_PERFORMED, "act_project_saved"));
				// Close project here (hopefully removes the tab)
				//PAS.get_pas().close_active_project(true, true);
				//PAS.get_pas().get_eastcontent().remove_tab(EastContent.PANEL_SENDING_);
			}

            try {
                UserInfo ui = (UserInfo)PAS.get_pas().get_userinfo().clone();
                ui.set_nav_init(PAS.get_pas().get_userinfo().get_current_department().get_nav_init());
                PAS.pasplugin.onSetInitialMapBounds(Variables.getNavigation(), ui);
            }
            catch(Exception err) {
                log.warn(err.getMessage(), err);
            }

			PAS.get_pas().get_mappane().load_map(true);
		}
		else if("act_dept_changed".equals(e.getActionCommand())) {
			deptChanged();
		}
		else if("act_export_alert_polygon".equals(e.getActionCommand())) {
			//AlertVO [] a = { (AlertVO)e.getSource() };
			AlertVO [] a = (AlertVO[])e.getSource();
			new no.ums.pas.importer.SosiExport(a);
		}
		else if("act_predefined_areas_changed".equals(e.getActionCommand()))
		{
			for(SendObject sending : PAS.get_pas().get_sendcontroller().get_sendings())
			{
				sending.get_toolbar().refreshAreaList();
			}
		}//act_predefined_filters_changed
		else if("act_predefined_filters_changed".equals(e.getActionCommand()))
        {
            for(SendObject sending : PAS.get_pas().get_sendcontroller().get_sendings())
            {
                sending.get_toolbar().refreshAddressFilterList();
            }
        }
		  else if("act_map_goto_filter".equals(e.getActionCommand())) {
	            NavStruct nav = (NavStruct)e.getSource();
	            if(Variables.getNavigation().setNavigation(nav))
	                PAS.get_pas().get_mappane().load_map(true);
	            PAS.get_pas().kickRepaint();
	        }
	}
	
	public void deptChanged() {
		PAS.pasplugin.onDepartmentChanged(PAS.get_pas());
	}


}


