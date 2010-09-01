package no.ums.pas.core.menus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import no.ums.pas.*;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.menus.defines.*;
import no.ums.pas.ums.errorhandling.Error;

import org.jvnet.substance.*;
//import org.jvnet.substance.skin.SkinInfo;
//import org.jvnet.substance.theme.*;
//import org.jvnet.substance.skin.*;
import org.jvnet.substance.skin.SkinInfo;
import org.jvnet.substance.watermark.SubstanceWatermark;
//import org.jvnet.substance.api.SubstanceSkin;

import java.util.Map;
import java.util.Iterator;

//Substance 3.3
import org.jvnet.substance.watermark.WatermarkInfo;
import org.jvnet.substance.skin.SubstanceSkin;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.theme.ThemeInfo;


//Substance 5.2
//import org.jvnet.substance.api.SubstanceSkin;


public class MainSelectMenu extends JPanel implements ActionListener, ComponentListener
{
	public static final long serialVersionUID = 1;
	protected static final String DISABLE = "disable";
	protected static final String ENABLE = "enable";
	private MainMenuBar m_menubar;
	private PAS m_pas;

	PAS get_pas() { return m_pas; }
	public MainMenuBar get_bar() { return m_menubar; }
	public boolean get_view_polygon() { return get_bar().m_item_view_showpolygon.getState(); }
	public boolean get_view_houses() { return get_bar().m_item_view_showhouses.getState(); }
	public boolean get_view_statuscodes() { return get_bar().m_item_view_statuscodes.getState(); }
	public boolean get_view_searchpinpoint() { return get_bar().m_item_view_searchpinpoint.getState(); }
	public void enable_viewstatuscodes(boolean b_enable) { get_bar().m_item_view_statuscodes.setEnabled(b_enable); }
	public void enableStatusExport(boolean b)
	{
		get_bar().m_item_status_export.setEnabled(b);
	}

	private ActionListener m_actionlistener;
	
	public class MainMenuBar extends JMenuBar 
	{
		public static final long serialVersionUID = 1;
		protected JMenu m_menu_file;
		protected JMenu m_menu_config;
		protected JMenu m_menu_view;
		protected JMenu m_menu_navigate;
		protected JMenu m_menu_status;
		protected JMenu m_menu_gps;
		protected JMenu m_menu_parm;
		protected JMenu m_menu_departments;
		protected JMenu m_menu_help;

		protected JMenu m_menu_layout;
		protected JMenu m_menu_themes;
		protected JMenu m_menu_watermarks;
		protected JMenu m_menu_skins;
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			PAS.pasplugin.onPaintMenuBarExtras(this, g);
		}

		public ActionListener get_actionlistener() { return m_actionlistener; }
		
		//public JMenuItem get_file_new_sending() { return m_item_new_sending; }
		//public JMenuItem get_file_new_project() { return m_item_new_project; }
		//public JMenuItem get_file_import() { return m_item_fileimport; }
		public JMenu get_menu_file() { return m_menu_file; }
		public JMenu get_menu_config() { return m_menu_config; }
		public JMenu get_menu_navigate() { return m_menu_navigate; }
		public JMenu get_menu_layout() { return m_menu_layout; }
		public JMenu get_menu_themes() { return m_menu_themes; }
		public JMenu get_menu_watermarks() { return m_menu_watermarks; }
		public JMenu get_menu_skins() { return m_menu_skins; }
		public JMenu get_status() { return m_menu_status; }
		public JMenu get_menu_help() { return m_menu_help; }
		public JMenuItem get_fleetcontrol() { return m_menu_gps; }
		public JMenuItem get_parm() { return m_menu_parm; }
		public JMenuItem get_dept() { return m_menu_departments; }
		public JMenuItem get_view() { return m_menu_view; }
		
		
		public JMenuItem get_item_fileimport()
		{
			return m_item_fileimport;
		}
		public JMenuItem get_item_file_print_map()
		{
			return m_item_file_print_map;
		}
		public JMenuItem get_item_file_save_map()
		{
			return m_item_file_save_map;
		}
		public JMenuItem get_item_exit()
		{
			return m_item_exit;
		}
		public JMenuItem get_item_navigate_pan()
		{
			return m_item_navigate_pan;
		}
		public JMenuItem get_item_navigate_zoom()
		{
			return m_item_navigate_zoom;
		}
		public JMenuItem get_item_navigate_search()
		{
			return m_item_navigate_search;
		}
		public JMenuItem get_item_view_showpolygon()
		{
			return m_item_view_showpolygon;
		}
		public JMenuItem get_item_view_showhouses()
		{
			return m_item_view_showhouses;
		}
		public JMenuItem get_item_view_searchpinpoint()
		{
			return m_item_view_searchpinpoint;
		}
		public JMenuItem get_item_show_settings()
		{
			return m_item_show_settings;
		}
		public JMenuItem get_item_messagelib()
		{
			return m_item_messagelib;
		}
		public JMenuItem get_item_training_mode()
		{
			return m_item_training_mode;
		}
		public JMenuItem get_item_status_open()
		{
			return m_item_status_open;
		}
		public JMenuItem get_item_status_export()
		{
			return m_item_status_export;
		}
		public JMenuItem get_item_gps_new()
		{
			return m_item_gps_new;
		}
		public JMenuItem get_item_gps_open()
		{
			return m_item_gps_open;
		}
		public JMenuItem get_item_gps_updates()
		{
			return m_item_gps_updates;
		}
		public JMenuItem get_item_gps_trail_minutes()
		{
			return m_item_gps_trail_minutes;
		}
		public JMenuItem get_item_parm_start()
		{
			return m_item_parm_start;
		}
		public JMenuItem get_item_parm_refresh()
		{
			return m_item_parm_refresh;
		}
		public JMenuItem get_item_parm_close()
		{
			return m_item_parm_close;
		}
		public JMenuItem get_item_status_updates()
		{
			return m_item_status_updates;
		}
		
		public JMenuItem get_item_help_about()
		{
			return m_item_help_about;
		}
		public JMenuItem get_item_contact_information()
		{
			return m_item_contact_information;
		}
		public JMenuItem get_item_address_book()
		{
			return m_item_address_book;
		}
		//public JMenuItem get_search() { return m_item_navigate_search; }
		
		protected JMenuItem m_item_new_sending;
		public JMenuItem get_item_new_sending()
		{
			return m_item_new_sending;
		}
		protected JMenuItem m_item_new_project;
		public JMenuItem get_item_new_project()
		{
			return m_item_new_project;
		}
		protected JMenuItem m_item_close_project;
		public JMenuItem get_item_close_project()
		{
			return m_item_close_project;
		}
		
		protected JMenuItem m_item_file_print_map;
		protected JMenuItem m_item_file_save_map;
		protected JMenuItem m_item_fileimport;
		protected JMenuItem m_item_exit;
		
		protected JMenuItem m_item_navigate_pan;
		protected JMenuItem m_item_navigate_zoom;
		protected JMenuItem m_item_navigate_search;
		
		protected JCheckBoxMenuItem m_item_view_showpolygon;
		//private JCheckBoxMenuItem m_item_view_showstatushouses;
		protected JCheckBoxMenuItem m_item_view_showhouses;
		protected JCheckBoxMenuItem m_item_view_statuscodes;
		protected JCheckBoxMenuItem m_item_view_searchpinpoint;
		
		protected JMenuItem m_item_show_settings;
		protected JMenuItem m_item_messagelib;
		protected JCheckBoxMenuItem m_item_training_mode;
		//private JMenuItem m_item_save_settings;
		
		protected JMenuItem m_item_status_open;
		protected JMenuItem m_item_status_export;
		
		protected JMenuItem m_item_gps_new;
		protected JMenuItem m_item_gps_open;
		
		protected JMenuItem m_item_parm_start;
		protected JMenuItem m_item_parm_refresh;
		protected JMenuItem m_item_parm_close;
		
		protected JMenuItem m_item_help_about;
		protected JMenuItem m_item_address_book;
		protected JMenuItem m_item_contact_information;

		
		//private JMenu m_item_gps_epsilon;
		//private SliderMenuItem m_item_gps_epsilon_slider;
		protected JMenu m_item_gps_trail_minutes;
		protected JMenu m_item_gps_updates;
		protected JMenu m_item_status_updates;
		protected RadioItemList m_item_gps_trail_checklist;
		protected CheckItem [] m_item_gps_trail_minutes_check;
		
		protected RadioItemList m_item_departments_checklist;
		protected CheckItem [] m_item_departments_check;
		
		//private SubstanceMenuItem m_themes[];
		protected SubstanceMenuItemList m_themes;
		protected SubstanceMenuItemList m_watermarks;
		protected SubstanceMenuItemList m_skins;
		
		protected RadioItemList m_item_gps_updateseconds_checklist;
		protected CheckItem [] m_item_gps_updateseconds_check;
		protected RadioItemList m_item_gps_updatemethod_checklist;
		protected CheckItem [] m_item_gps_updatemethod_check;
		
		protected RadioItemList m_item_status_updatemethod_checklist;
		protected CheckItem [] m_item_status_updatemethod_check;
		protected RadioItemList m_item_status_updateseconds_checklist;
		protected CheckItem [] m_item_status_updateseconds_check;
		
		public RadioItemList get_gpstrail_checklist() { return m_item_gps_trail_checklist; }
		public RadioItemList get_gpsupdateseconds_checklist() { return m_item_gps_updateseconds_checklist; }
		public RadioItemList get_gps_updatemethod() { return m_item_gps_updatemethod_checklist; }
		public RadioItemList get_statusupdateseconds_checklist() { return m_item_status_updateseconds_checklist; }
		public RadioItemList get_status_updatemethod() { return m_item_status_updatemethod_checklist; }
		public SubstanceMenuItemList get_theme_menu() { return m_themes; }
		public SubstanceMenuItemList get_watermarks_menu() { return m_watermarks; }
		public SubstanceMenuItemList get_skin_menu() { return m_skins; }
		
		
		
		public void set_searchpinpoint(boolean b) {
			m_item_view_searchpinpoint.setSelected(b);
		}
		
		public void set_status_autoupdate_invoke(boolean b) {
			if(!b) {
				m_item_status_updatemethod_check[0].doClick();
			} else {
				m_item_status_updatemethod_check[1].doClick();
			}
		}
		public void set_gps_autoupdate_invoke(boolean b) {
			if(!b) {
				m_item_gps_updatemethod_check[0].doClick();
			} else {
				m_item_gps_updatemethod_check[1].doClick();
			}
		}
		public void set_show_houses_invoke(boolean b) {
			m_item_view_showhouses.setState(b);
			set_show_houses();
			/*if(b) {
				PAS.get_pas().download_houses();
			}
			PAS.get_pas().kickRepaint();*/
		}
		public void set_show_houses() {
			if(get_show_houses())
				PAS.get_pas().download_houses();
			PAS.get_pas().kickRepaint();
		}
		public boolean get_show_houses() {
			return m_item_view_showhouses.isSelected();
		}
		
		public MainMenuBar()
		{
			super();
			m_menu_file = new JMenu(PAS.l("mainmenu_file"));
			m_menu_navigate = new JMenu(PAS.l("mainmenu_navigation"));
			m_menu_view = new JMenu(PAS.l("mainmenu_view"));
			m_menu_config = new JMenu(PAS.l("mainmenu_settings"));
			m_menu_status = new JMenu(PAS.l("mainmenu_status"));
			m_menu_gps		= new JMenu(PAS.l("mainmenu_fleetcontrol"));
			m_menu_parm		= new JMenu(PAS.l("mainmenu_parm"));
			m_menu_departments = new JMenu(PAS.l("mainmenu_departments"));
			m_menu_help = new JMenu(PAS.l("mainmenu_help"));
			m_menu_layout = new JMenu(PAS.l("mainmenu_layout"));
			m_menu_themes	= new JMenu(PAS.l("mainmenu_layout_themes"));
			m_menu_watermarks = new JMenu(PAS.l("mainmenu_layout_watermarks"));
			m_menu_skins = new JMenu(PAS.l("mainmenu_layout_skins"));
			
			m_item_new_sending = new JMenuItem(PAS.l("mainmenu_file_newsending"));
			m_item_new_project = new JMenuItem(PAS.l("mainmenu_file_project"));
			m_item_close_project = new JMenuItem(PAS.l("mainmenu_file_project_close"));
			m_item_close_project.setEnabled(false);
			m_item_fileimport = new JMenuItem(PAS.l("mainmenu_file_import"));
			m_item_file_print_map = new JMenuItem(PAS.l("mainmenu_file_printmap"));
			m_item_file_save_map = new JMenuItem(PAS.l("mainmenu_file_savemap"));
			m_item_exit = new JMenuItem(PAS.l("mainmenu_file_quit"));
			
			m_item_navigate_pan = new JMenuItem(PAS.l("mainmenu_navigation_pan"));
			m_item_navigate_zoom = new JMenuItem(PAS.l("mainmenu_navigation_zoom"));
			m_item_navigate_search = new JMenuItem(PAS.l("mainmenu_navigation_search"));
			
			m_item_view_showpolygon = new JCheckBoxMenuItem(PAS.l("mainmenu_view_show_statusshape"), true);
			//m_item_view_showstatushouses = new JCheckBoxMenuItem("Show status houses", true);
			m_item_view_showhouses = new JCheckBoxMenuItem(PAS.l("mainmenu_view_show_houses"), true);
			m_item_view_statuscodes = new JCheckBoxMenuItem(PAS.l("mainmenu_view_show_statuscodes"), true);
			m_item_view_searchpinpoint = new JCheckBoxMenuItem(PAS.l("mainmenu_view_show_search_pinpoint"), true);
			
			m_item_show_settings = new JMenuItem(PAS.l("mainmenu_settings_show"));
			m_item_messagelib = new JMenuItem(PAS.l("main_sending_audio_type_library"));
			//m_item_save_settings = new JMenuItem("Save settings");
			
			m_item_status_open = new JMenuItem(PAS.l("mainmenu_status_open"));
			m_item_status_export = new JMenuItem(PAS.l("mainmenu_status_export"));
			
			m_item_gps_new = new JMenuItem(PAS.l("mainmenu_fleetcontrol_newobject"));
			m_item_gps_open = new JMenuItem(PAS.l("mainmenu_fleetcontrol_download_mapobjects"));
			m_item_gps_trail_minutes = new JMenu(PAS.l("mainmenu_fleetcontrol_trail_time"));
			m_item_gps_updates = new JMenu(PAS.l("mainmenu_fleetcontrol_updates"));
			
			m_item_parm_start = new JMenuItem(PAS.l("mainmenu_parm_start"));
			m_item_parm_refresh = new JMenuItem(PAS.l("common_refresh"));
			m_item_parm_close = new JMenuItem(PAS.l("common_close"));
			
			m_item_help_about = new JMenuItem(PAS.l("mainmenu_help_about"));
			m_item_training_mode = new JCheckBoxMenuItem(PAS.l("mainmenu_trainingmode"));
			m_item_address_book = new JMenuItem(PAS.l("common_address_book"));
			m_item_contact_information = new JMenuItem(PAS.l("common_contact_information"));
			
			//m_item_gps_epsilon = new JMenu("Point epsilon");
			//m_item_gps_epsilon_slider = new SliderMenuItem(get_pas(), "");
			
			m_item_status_updates = new JMenu(PAS.l("mainmenu_status_updates"));
			
			String sz_seconds = PAS.l("common_seconds");
			String sz_minute = PAS.l("common_minute");
			String sz_minutes = PAS.l("common_minutes");
			String sz_hour = PAS.l("common_hour");
			String sz_hours = PAS.l("common_hours");
			m_item_gps_trail_minutes_check = new CheckItem[] { 
													new CheckItem(String.format("10 %s", sz_minutes), new Integer(10), true),
													new CheckItem(String.format("20 %s", sz_minutes), new Integer(20), false), 
													new CheckItem(String.format("30 %s", sz_minutes), new Integer(30), false), 
													new CheckItem(String.format("1 %s", sz_hour), new Integer(60), false), 
													new CheckItem(String.format("2 %s", sz_hours), new Integer(120), false), 
													new CheckItem(String.format("12 %s", sz_hours), new Integer(720), false), 
													new CheckItem(String.format("24 %s", sz_hours), new Integer(1440), false), 
													new CheckItem(PAS.l("mainmenu_fleetcontrol_updates_no_time_filter"), new Integer(-1), false)
											 };
			
			m_item_gps_updateseconds_check = new CheckItem[] {
													new CheckItem(String.format("5 %s", sz_seconds), new Integer(5), false),
													new CheckItem(String.format("10 %s", sz_seconds), new Integer(10), true),
													new CheckItem(String.format("20 %s", sz_seconds), new Integer(20), false),
													new CheckItem(String.format("30 %s", sz_seconds), new Integer(30), false),
													new CheckItem(String.format("1 %s", sz_minute), new Integer(60), false),
													new CheckItem(String.format("5 %s", sz_minutes), new Integer(60*5), false)
											 };
			m_item_status_updateseconds_check = new CheckItem[] {
												new CheckItem(String.format("5 %s", sz_seconds), new Integer(5), true),
												new CheckItem(String.format("10 %s", sz_seconds), new Integer(10), false),
												new CheckItem(String.format("20 %s", sz_seconds), new Integer(20), false),
												new CheckItem(String.format("30 %s", sz_seconds), new Integer(30), false),
												new CheckItem(String.format("1 %s", sz_minute), new Integer(60), false),
												new CheckItem(String.format("5 %s", sz_minutes), new Integer(60*5), false)
											};
			
			m_item_gps_updatemethod_check = new CheckItem[] {
												new CheckItem(PAS.l("mainmenu_status_updates_manual"), new String("manual"), (get_pas().get_gpscontroller().get_autoupdate() ? false : true)),
												new CheckItem(PAS.l("mainmenu_status_updates_every"), new String("auto"), (get_pas().get_gpscontroller().get_autoupdate() ? true : false))
											};
			m_item_status_updatemethod_check = new CheckItem[] {
												new CheckItem(PAS.l("mainmenu_status_updates_manual"), new String("manual"), (get_pas().get_statuscontroller().get_autoupdate() ? false : true)),
												new CheckItem(PAS.l("mainmenu_status_updates_every"), new String("auto"), (get_pas().get_statuscontroller().get_autoupdate() ? true : false))
											};
			
			
			m_item_departments_check		= new CheckItem[PAS.get_pas().get_userinfo().get_departments().size()];
			for(int i=0; i < PAS.get_pas().get_userinfo().get_departments().size(); i++) {
				try
				{
					m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
																PAS.get_pas().get_userinfo().get_departments().get(i),
																PAS.get_pas().get_userinfo().get_departments().get(i).equals(PAS.get_pas().get_userinfo().get_default_dept()) ? true : false);//(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).isDefaultDept() ? true : false));
				}
				catch(Exception e)
				{
					m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
							PAS.get_pas().get_userinfo().get_departments().get(i),
							false);			
				}
			}
			m_item_departments_checklist = new RadioItemList(get_pas(), m_item_departments_check, 0, 
															m_menu_departments, m_actionlistener, "act_change_department");	
			

			m_item_gps_trail_checklist = new RadioItemList(get_pas(), m_item_gps_trail_minutes_check, 0, 
											m_item_gps_trail_minutes, m_actionlistener, "act_gps_trail_minutes");	
			m_item_gps_updatemethod_checklist = new RadioItemList(get_pas(), m_item_gps_updatemethod_check, 0,
													m_item_gps_updates, m_actionlistener, "act_gps_updatemethod");
			m_item_gps_updates.insertSeparator(m_item_gps_updatemethod_check.length);
			m_item_gps_updateseconds_checklist = new RadioItemList(get_pas(), m_item_gps_updateseconds_check, 0, 
													m_item_gps_updates, m_actionlistener, "act_gps_updateseconds");
			
			m_item_status_updatemethod_checklist = new RadioItemList(get_pas(), m_item_status_updatemethod_check, 0,
													m_item_status_updates, m_actionlistener, "act_status_updatemethod");
			m_item_status_updates.insertSeparator(m_item_status_updatemethod_check.length);
			m_item_status_updateseconds_checklist = new RadioItemList(get_pas(), m_item_status_updateseconds_check, 0,
														m_item_status_updates, m_actionlistener, "act_status_updateseconds");
			if(!get_pas().get_gpscontroller().get_autoupdate())
				m_item_gps_updateseconds_checklist.enable_all(false);
			if(!get_pas().get_statuscontroller().get_autoupdate())
				m_item_status_updateseconds_checklist.enable_all(false);

						
			int i=0;

			try
			{
				//Substance 3.3
				Map<String,ThemeInfo> themes = SubstanceLookAndFeel.getAllThemes();
	            SubstanceImageCreator ic = new SubstanceImageCreator();
				SubstanceMenuItem theme_items [] = new SubstanceMenuItem[themes.size()];
				for (Iterator iterator = themes.entrySet().iterator(); iterator.hasNext();) { 
				    Map.Entry entry = (Map.Entry) iterator.next(); 
				    ThemeInfo info = (ThemeInfo)entry.getValue();
				    try {
				    	Class themeClass = Class.forName(info.getClassName());
			            SubstanceTheme themeInstance = (SubstanceTheme) themeClass.newInstance();
	
			            theme_items[i] = new SubstanceMenuItem(info.getDisplayName(), themeInstance, SubstanceImageCreator.getThemeIcon(themeInstance));
			            
				    } catch(Exception e) {
				    	Error.getError().addError("MainSelectMenu","Exception in MainMenuBar",e,1);
				    	continue;
				    }
				    i++;
				}
				m_themes = new SubstanceMenuItemList(get_pas(), theme_items, 0, m_menu_themes, m_actionlistener, "act_set_theme", 14);

				i=0;
				Map<String, WatermarkInfo> wm = SubstanceLookAndFeel.getAllWatermarks();
				SubstanceMenuItem wm_items [] = new SubstanceMenuItem[wm.size()];
				for(Iterator iterator = wm.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, WatermarkInfo> entry = (Map.Entry<String, WatermarkInfo>)iterator.next();
					WatermarkInfo info = (WatermarkInfo)entry.getValue();
					try
					{
						Class themeClass = Class.forName(info.getClassName());
						SubstanceWatermark wmInstance = (SubstanceWatermark)themeClass.newInstance();
						wm_items[i] = new SubstanceMenuItem(info.getDisplayName(), wmInstance, SubstanceImageCreator.getWatermarkIcon(wmInstance));
					}
					catch(Exception e)
					{
						
					}
					i++;
				}
				m_watermarks = new SubstanceMenuItemList(get_pas(), wm_items, 0, m_menu_watermarks, m_actionlistener, "act_set_watermark", 0);
				
				i=0;
				Map<String,SkinInfo> skins = SubstanceLookAndFeel.getAllSkins();
				SubstanceMenuItem sk_items [] = new SubstanceMenuItem[skins.size()];
				for(Iterator iterator = skins.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, SkinInfo> entry = (Map.Entry<String, SkinInfo>)iterator.next();
					SkinInfo info = entry.getValue();
					try
					{
						Class skinClass = Class.forName(info.getClassName());
						SubstanceSkin skInstance = (SubstanceSkin)skinClass.newInstance();
						sk_items[i] = new SubstanceMenuItem(info.getDisplayName(), skInstance, null);
					}
					catch(Exception e)
					{
						
					}
					i++;
				}
				m_skins = new SubstanceMenuItemList(get_pas(), sk_items, 0, m_menu_skins, m_actionlistener, "act_set_skin", 0);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			
			init();
			showNewSending(true);
			showNewProject(true);
			showCloseProject(true);
			showFileImport(true);
			showPrintMap(true);
			showSaveMap(true);
			showExit(true);
			showFileMenu(true);
			
			showNavigationMenu(true);
			showNavZoom(true);
			showNavPan(true);
			showNavSearch(true);
		}
		public void showNewSending(boolean b)
		{
			m_item_new_sending.setVisible((Boolean)UIManager.get("m_item_new_sending") && b);
		}
		public void showNewProject(boolean b)
		{
			m_item_new_project.setVisible((Boolean)UIManager.get("m_item_new_project") && b);
		}
		public void showCloseProject(boolean b)
		{
			m_item_close_project.setVisible((Boolean)UIManager.get("m_item_close_project") && b);
			
		}
		public void showFileImport(boolean b)
		{
			m_item_fileimport.setVisible((Boolean)UIManager.get("m_item_fileimport") && b);			
		}
		public void showPrintMap(boolean b)
		{
			m_item_file_print_map.setVisible((Boolean)UIManager.get("m_item_file_print_map") && b);			
		}
		public void showSaveMap(boolean b)
		{
			m_item_file_save_map.setVisible((Boolean)UIManager.get("m_item_file_save_map") && b);			
		}
		public void showExit(boolean b)
		{
			m_item_exit.setVisible((Boolean)UIManager.get("m_item_exit") && b);						
		}
		public void showFileMenu(boolean b)
		{
			m_menu_file.setVisible((Boolean)UIManager.get("m_menu_file") && b);
		}
		
		public void showNavigationMenu(boolean b)
		{
			m_menu_navigate.setVisible((Boolean)UIManager.get("m_menu_navigate") && b);
		}
		public void showNavZoom(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_navigate_zoom") && b;
			m_item_navigate_zoom.setVisible(show);
			m_actionlistener.actionPerformed(new ActionEvent(show, ActionEvent.ACTION_PERFORMED, "act_show_zoom_btn"));
		}
		public void showNavPan(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_navigate_pan") && b;
			m_item_navigate_pan.setVisible(show);
			m_actionlistener.actionPerformed(new ActionEvent(new Boolean(show), ActionEvent.ACTION_PERFORMED, "act_show_pan_btn"));
		}
		public void showNavSearch(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_navigate_search") && b;
			m_item_navigate_search.setVisible(show);
			m_actionlistener.actionPerformed(new ActionEvent(new Boolean(show), ActionEvent.ACTION_PERFORMED, "act_show_search_btn"));
		}
		public void showHouseEditor(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_house_editor") && b;
			m_actionlistener.actionPerformed(new ActionEvent(new Boolean(show), ActionEvent.ACTION_PERFORMED, "act_show_house_editor"));			
		}
		public void showHouseSelect(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_house_select") && b;
			m_actionlistener.actionPerformed(new ActionEvent(new Boolean(show), ActionEvent.ACTION_PERFORMED, "act_show_house_select"));			
		}
		public void showMapSelection(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_map_select") && b;
			m_actionlistener.actionPerformed(new ActionEvent(new Boolean(show), ActionEvent.ACTION_PERFORMED, "act_show_map_select"));			
		}
		public void showViewMenu(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_menu_view") && b;
			m_menu_view.setVisible(show);
		}
		public void showViewStatusShape(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_view_showpolygon") && b;
			m_item_view_showpolygon.setVisible(show);			
		}
		public void showViewHouses(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_view_showhouses") && b;
			m_item_view_showhouses.setVisible(show);
		}
		public void showViewSearchPinpoint(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_view_searchpinpoint") && b;
			m_item_view_searchpinpoint.setVisible(show);
		}
		public void showSettingsMenu(boolean b)
		{			
			Boolean show = (Boolean)UIManager.get("m_menu_config") && b;
			m_menu_config.setVisible(show);
		}
		public void showSettingsShowSettings(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_show_settings") && b;
			m_item_show_settings.setVisible(show);
		}
		public void showSettingsMessageLib(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_messagelib") && b;
			m_item_messagelib.setVisible(show);
		}
		public void showStatusMenu(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_menu_status") && b;
			m_menu_status.setVisible(show);
		}
		public void showStatusOpenStatus(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_status_open") && b;
			m_item_status_open.setVisible(show);
		}
		public void showStatusExportStatus(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_status_export") && b;
			m_item_status_export.setVisible(show);
		}
		public void showStatusUpdates(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_status_updates") && b;
			m_item_status_updates.setVisible(show);
		}
		public void showParmMenu(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_menu_parm") && b;
			m_menu_parm.setVisible(show);			
		}
		public void showParmStart(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_parm_start") && b;
			m_item_parm_start.setVisible(show);			
		}
		public void showParmRefresh(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_parm_refresh") && b;
			m_item_parm_refresh.setVisible(show);			
			
		}
		public void showParmClose(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_parm_close") && b;
			m_item_parm_close.setVisible(show);	
		}
		public void showDepartmentMenu(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_menu_departments") && b;
			m_menu_departments.setVisible(show);
		}
		public void showLayoutMenu(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_menu_layout") && b;
			m_menu_layout.setVisible(show);			
		}
		
		void init()
		{
			PAS.pasplugin.onAddMainSelectMenu(this);
			m_item_new_sending.setActionCommand("act_new_sending");
			m_item_new_project.setActionCommand("act_new_project");
			m_item_close_project.setActionCommand("act_close_project");
			m_item_fileimport.setActionCommand("act_fileimport");
			m_item_file_print_map.setActionCommand("act_print_map");
			m_item_file_save_map.setActionCommand("act_save_map");
			m_item_exit.setActionCommand("act_exit_application");
			
			m_item_navigate_pan.setActionCommand("act_pan");
			m_item_navigate_zoom.setActionCommand("act_zoom");
			m_item_navigate_search.setActionCommand("act_search");
			
			m_item_view_showpolygon.setActionCommand("act_togglepolygon");
			m_item_view_showhouses.setActionCommand("act_toggle_showhouses");
			m_item_view_statuscodes.setActionCommand("act_view_statuscodes");
			m_item_view_searchpinpoint.setActionCommand("act_show_searchpinpoint");
			
			m_item_show_settings.setActionCommand("act_show_settings");
			m_item_messagelib.setActionCommand("act_messagelib");
			//m_item_save_settings.setActionCommand("act_save_settings");
			
			m_item_status_open.setActionCommand("act_statusopen");
			m_item_status_export.setActionCommand("act_statusexport");
			m_item_gps_open.setActionCommand("act_gps_open");
			m_item_gps_new.setActionCommand("act_gps_new");
			
			m_item_parm_start.setActionCommand("act_start_parm");
			m_item_parm_refresh.setActionCommand("act_refresh_parm");
			m_item_parm_close.setActionCommand("act_close_parm");
			
			m_item_help_about.setActionCommand("act_help_about");
			m_item_training_mode.setActionCommand("act_trainingmode");
			m_item_address_book.setActionCommand("act_address_book");
			m_item_contact_information.setActionCommand("act_show_contact_information");
		}

	}

	
	public MainSelectMenu(PAS pas, ActionListener actionlistener)
	{
		super();
		m_pas = pas;
		//setSize(get_pas().get_eastwidth() + get_pas().get_mappane().getWidth(), 40);//get_pas().get_eastwidth() + 
		try {
			//setBounds(0, 0, getWidth(), 40);
			//setPreferredSize(new Dimension(get_pas().get_eastwidth() + get_pas().get_mappane().get_dimension().width, 40));
			//setMinimumSize(new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE));
			//setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		} catch(Exception e) {
			PAS.get_pas().add_event("MainSelectMenu setBounds() " + e.getMessage(), e);
			Error.getError().addError(PAS.l("common_error"),"Exception in MainSelectMenu",e,1);
		}
		
		m_actionlistener = actionlistener;
		prepare_controls();
		//setBackground(Color.white);
		enable_viewstatuscodes(false);
		enableStatusExport(false);
		revalidate();
		doLayout();
		addComponentListener(this);
		
	}
	void prepare_controls()
	{
	        m_menubar = new MainMenuBar();
	        //m_menubar.setPreferredSize(new Dimension(m_pas.get_mappane().getWidth() + m_pas.get_eastwidth(), 20)); //+ m_pas.get_eastwidth()
	        //m_menubar.init();
	        init();
	}
	void init()
	{
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
		{
			return;
		}
		//System.out.println(getWidth() + " , " + getHeight());

		setBounds(0,0,getWidth(),getHeight());
		m_menubar.setPreferredSize(new Dimension(getWidth(), 25));
		m_menubar.setMinimumSize(new Dimension(500, 25));
		m_menubar.revalidate();
		//m_menubar.doLayout();
		//setBounds(0, 0, getWidth()-20, 20);
		//setPreferredSize(new Dimension(getWidth()-20, 20));
		//m_menubar.setBounds(0, 0, getWidth(), getHeight());
		//m_menubar.setPreferredSize(new Dimension(getWidth(), getHeight()));
		//revalidate();
		//m_menubar.revalidate();
		//m_menubar.doLayout();
		//System.out.println("Resized menu " + getWidth());
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }			
}