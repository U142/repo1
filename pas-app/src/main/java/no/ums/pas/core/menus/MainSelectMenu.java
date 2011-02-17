package no.ums.pas.core.menus;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.menus.defines.RadioItemList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

//import org.jvnet.substance.skin.SkinInfo;
//import org.jvnet.substance.theme.*;
//import org.jvnet.substance.skin.*;
//import org.jvnet.substance.api.SubstanceSkin;
//Substance 3.3


//Substance 5.2
//import org.jvnet.substance.api.SubstanceSkin;


public class MainSelectMenu extends JPanel implements ActionListener, ComponentListener
{

    private static final Log log = UmsLog.getLogger(MainSelectMenu.class);

	public static final long serialVersionUID = 1;
	protected static final String DISABLE = "disable";
	protected static final String ENABLE = "enable";
	private MainMenuBar m_menubar;
	private PAS m_pas;

	PAS get_pas() { return m_pas; }
	public MainMenuBar get_bar() { return m_menubar; }

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

//		protected JCheckBoxMenuItem m_item_view_showpolygon;
		//private JCheckBoxMenuItem m_item_view_showstatushouses;
//		protected JCheckBoxMenuItem m_item_view_showhouses;
//		protected JCheckBoxMenuItem m_item_view_statuscodes;
//		protected JCheckBoxMenuItem m_item_view_searchpinpoint;
		
		protected JMenuItem m_item_show_settings;
		protected JMenuItem m_item_messagelib;
		protected JCheckBoxMenuItem m_item_training_mode;
		//private JMenuItem m_item_save_settings;
		
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

			m_item_show_settings = new JMenuItem(PAS.l("mainmenu_settings_show"));
			m_item_messagelib = new JMenuItem(PAS.l("main_sending_audio_type_library"));
			//m_item_save_settings = new JMenuItem("Save settings");
			
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
													new CheckItem(String.format("10 %s", sz_minutes), 10, true),
													new CheckItem(String.format("20 %s", sz_minutes), 20, false),
													new CheckItem(String.format("30 %s", sz_minutes), 30, false),
													new CheckItem(String.format("1 %s", sz_hour), 60, false),
													new CheckItem(String.format("2 %s", sz_hours), 120, false),
													new CheckItem(String.format("12 %s", sz_hours), 720, false),
													new CheckItem(String.format("24 %s", sz_hours), 1440, false),
													new CheckItem(PAS.l("mainmenu_fleetcontrol_updates_no_time_filter"), -1, false)
											 };
			
			m_item_gps_updateseconds_check = new CheckItem[] {
													new CheckItem(String.format("5 %s", sz_seconds), 5, false),
													new CheckItem(String.format("10 %s", sz_seconds), 10, true),
													new CheckItem(String.format("20 %s", sz_seconds), 20, false),
													new CheckItem(String.format("30 %s", sz_seconds), 30, false),
													new CheckItem(String.format("1 %s", sz_minute), 60, false),
													new CheckItem(String.format("5 %s", sz_minutes), 60 * 5, false)
											 };
			m_item_status_updateseconds_check = new CheckItem[] {
												new CheckItem(String.format("5 %s", sz_seconds), 5, true),
												new CheckItem(String.format("10 %s", sz_seconds), 10, false),
												new CheckItem(String.format("20 %s", sz_seconds), 20, false),
												new CheckItem(String.format("30 %s", sz_seconds), 30, false),
												new CheckItem(String.format("1 %s", sz_minute), 60, false),
												new CheckItem(String.format("5 %s", sz_minutes), 60 * 5, false)
											};
			
			m_item_gps_updatemethod_check = new CheckItem[] {
												new CheckItem(PAS.l("mainmenu_status_updates_manual"), "manual", (!get_pas().get_gpscontroller().get_autoupdate())),
												new CheckItem(PAS.l("mainmenu_status_updates_every"), "auto", (get_pas().get_gpscontroller().get_autoupdate()))
											};
			m_item_status_updatemethod_check = new CheckItem[] {
												new CheckItem(PAS.l("mainmenu_status_updates_manual"), "manual", (!get_pas().get_statuscontroller().get_autoupdate())),
												new CheckItem(PAS.l("mainmenu_status_updates_every"), "auto", (get_pas().get_statuscontroller().get_autoupdate()))
											};
			
			
			updateDeptSelection(false);
			

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
			
			init();
			showFileMenu(true);
			
			showNavigationMenu(true);
		}
		
		public void updateDeptSelection(boolean current_dept) {
			if(m_item_departments_checklist != null)
				m_item_departments_checklist.clear();
			if(m_menu_departments != null)
				m_menu_departments.removeAll();
			m_item_departments_check = new CheckItem[PAS.get_pas().get_userinfo().get_departments().size()];
			for(int i=0; i < PAS.get_pas().get_userinfo().get_departments().size(); i++) {
				try
				{
					if(current_dept) {
						m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
								PAS.get_pas().get_userinfo().get_departments().get(i),
								PAS.get_pas().get_userinfo().get_departments().get(i).equals(PAS.get_pas().get_userinfo().get_current_department()) ? true : false);//(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).isDefaultDept() ? true : false));
					}
					else {
						m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
																PAS.get_pas().get_userinfo().get_departments().get(i),
																PAS.get_pas().get_userinfo().get_departments().get(i).equals(PAS.get_pas().get_userinfo().get_default_dept()) ? true : false);//(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).isDefaultDept() ? true : false));
					}
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
		}

        public void showFileMenu(boolean b)
		{
			m_menu_file.setVisible((Boolean)UIManager.get("m_menu_file") && b);
		}
		
		public void showNavigationMenu(boolean b)
		{
			m_menu_navigate.setVisible((Boolean) UIManager.get("m_menu_navigate") && b);
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

			m_item_show_settings.setActionCommand("act_show_settings");
			m_item_messagelib.setActionCommand("act_messagelib");
			//m_item_save_settings.setActionCommand("act_save_settings");
			
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

		m_actionlistener = actionlistener;
		prepare_controls();
		//setBackground(Color.white);
        StatusActions.EXPORT.setEnabled(false);
        revalidate();
		doLayout();
		addComponentListener(this);
		
	}
	void prepare_controls()
	{
	        m_menubar = new MainMenuBar();
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
		setBounds(0,0,getWidth(),getHeight());
		m_menubar.setPreferredSize(new Dimension(getWidth(), 25));
		m_menubar.setMinimumSize(new Dimension(500, 25));
		m_menubar.revalidate();

	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }			
}