package no.ums.pas.pluginbase;


import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


import javax.swing.*;
import javax.xml.ws.soap.SOAPFaultException;

import no.ums.pas.*;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.dataexchange.MailCtrl;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.Logon;
import no.ums.pas.core.logon.LogonDialog;
import no.ums.pas.core.logon.LogonInfo;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.LogonDialog.LogonPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.*;
import no.ums.pas.core.themes.UMSTheme;
import no.ums.pas.core.themes.UMSTheme.THEMETYPE;
import no.ums.pas.core.ws.WSGetSystemMessages;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.ums.errorhandling.Error;

import org.geotools.data.ows.Layer;
import org.jvnet.substance.*;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;


public class PAS_Scripting extends PasScriptingInterface
{
	
	public PAS_Scripting()
	{
		super();		
		System.out.println("PAS_Scripting loaded");
	}
	
	





	@Override
	public boolean onAfterPowerUp(LogonDialog dlg, WSRESULTCODE ws) {
		if(ws==WSRESULTCODE.OK)
			dlg.setTitle(PAS.l("logon_heading") + " - " + PAS.l("logon_ws_active"));
		else
			dlg.setTitle(PAS.l("logon_heading") + " - " + PAS.l("logon_ws_inactive"));
		return true;
	}

	@Override
	public boolean onBeforeLogon()
	{
		return true;
	}
	
	@Override
	public boolean onAfterLogon()
	{
		return true;
	}
	@Override
	public boolean onShowMainWindow()
	{
		return true;
	}
	@Override 
	public boolean onAddSendOptionToolbar(SendOptionToolbar toolbar)
	{
		return true;
	}
	
	@Override
	public boolean onTest()
	{
		return true;
	}
	
	@Override
	public boolean onAddMainMenuButtons(MainMenu menu)
	{
		menu.set_gridconst(0, 0, 15, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_selectmenu().get_bar(), menu.m_gridconst);

		menu.set_gridconst(0, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_pan(), menu.m_gridconst);
		menu.set_gridconst(1, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_zoom(), menu.m_gridconst);
		menu.set_gridconst(2, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_search(), menu.m_gridconst);
		menu.set_gridconst(3, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_houseeditor(), menu.m_gridconst);
		menu.set_gridconst(4, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_showhousedetails(), menu.m_gridconst);
		menu.set_gridconst(5, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_zoom_to_world(), menu.m_gridconst);
		
		menu.set_gridconst(6, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_combo_mapsite(), menu.m_gridconst);
		return true;
	}
	
	@Override
	public boolean onAddMainSelectMenu(MainMenuBar menu)
	{
		menu.add(menu.get_menu_file());
		menu.add(menu.get_menu_navigate());
		menu.add(menu.get_view());
		menu.add(menu.get_menu_config());
		menu.add(menu.get_status());
		//add(m_menu_gps);
		menu.add(menu.get_parm());
		menu.add(menu.get_dept());
		menu.add(menu.get_menu_layout());
		
		menu.get_menu_layout().add(menu.get_menu_skins());
		menu.get_menu_layout().add(menu.get_menu_themes());
		menu.get_menu_layout().add(menu.get_menu_watermarks());
		JMenu menu_colors = new JMenu("Colors");
		menu.get_menu_layout().add(menu_colors);
		JMenuItem item_color;
		item_color = new JMenuItem("Foreground");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_foreground");
		menu_colors.add(item_color);
		item_color = new JMenuItem("Background");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_background");
		menu_colors.add(item_color);			
		item_color = new JMenuItem("Watermark");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_watermark");
		menu_colors.add(item_color);			
		item_color = new JMenuItem("Mid");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_mid");
		menu_colors.add(item_color);
		item_color = new JMenuItem("Dark");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_dark");
		menu_colors.add(item_color);
		item_color = new JMenuItem("Extra Light");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_extra_light");
		menu_colors.add(item_color);
		item_color = new JMenuItem("Light");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_light");
		menu_colors.add(item_color);
		item_color = new JMenuItem("Ultra Dark");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_ultra_dark");
		menu_colors.add(item_color);
		item_color = new JMenuItem("Ultra Light");
		item_color.addActionListener(menu.get_actionlistener());
		item_color.setActionCommand("act_change_color_ultra_light");
		menu_colors.add(item_color);
		
		//m_menu_layout.setFont(PAS.f().getMenuFont());
		
		menu.get_menu_file().add(menu.get_item_new_sending());
		menu.get_menu_file().add(menu.get_item_new_project());
		menu.get_menu_file().add(menu.get_item_close_project());
		menu.get_menu_file().addSeparator();
		menu.get_menu_file().add(menu.get_item_fileimport());
		menu.get_menu_file().add(menu.get_item_file_print_map());
		menu.get_menu_file().add(menu.get_item_file_save_map());
		menu.get_menu_file().add(menu.get_item_exit());

		menu.get_menu_navigate().add(menu.get_item_navigate_pan());
		menu.get_menu_navigate().add(menu.get_item_navigate_zoom());
		menu.get_menu_navigate().add(menu.get_item_navigate_search());
		
		menu.get_view().add(menu.get_item_view_showpolygon());
		menu.get_view().add(menu.get_item_view_showhouses());
		//m_menu_view.add(m_item_view_statuscodes);
		menu.get_view().add(menu.get_item_view_searchpinpoint());
		
		menu.get_menu_config().add(menu.get_item_show_settings());
		if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4)
			menu.get_menu_config().add(menu.get_item_messagelib());
			//m_menu_config.add(m_item_messagelib); 
		//m_menu_config.add(m_item_save_settings);
		
		menu.get_status().add(menu.get_item_status_open());
		menu.get_status().add(menu.get_item_status_export());
		
		menu.get_fleetcontrol().add(menu.get_item_gps_new());
		menu.get_fleetcontrol().add(menu.get_item_gps_open());
		menu.get_fleetcontrol().add(menu.get_item_gps_updates());
		menu.get_fleetcontrol().add(menu.get_item_gps_trail_minutes());
		
		menu.get_parm().add(menu.get_item_parm_start());
		menu.get_parm().add(menu.get_item_parm_refresh());
		menu.get_parm().add(menu.get_item_parm_close());
		
		menu.get_menu_help().add(menu.get_item_help_about());

		//m_menu_gps.add(m_item_gps_epsilon);
		//m_item_gps_epsilon.add(m_item_gps_epsilon_slider);
		
		menu.get_status().add(menu.get_item_status_updates());

		return true;
	}
	
	@Override
	public boolean onAddPASComponents(PAS p)
	{
		p.add(p.get_mappane(), BorderLayout.CENTER);
		//p.add(p.get_maplayeredpane(), BorderLayout.CENTER);
		p.add(p.get_mainmenu(), BorderLayout.NORTH);
		p.add(p.get_southcontent(), BorderLayout.SOUTH);
		p.add(p.get_eastcontent(), BorderLayout.EAST);	

		return true;
	}
	
	@Override
	public boolean onSetInitialMapBounds(Navigation nav, UserInfo ui)
	{
		switch(ui.get_current_department().get_pas_rights())
		{
		case 4: //TAS
			ui.set_nav_init(Navigation.NAV_WORLD);
			break;
		}
		nav.setNavigation(ui.get_nav_init());

		return true;
	}
	
	@Override 
	public boolean onStartParm()
	{

		new Thread("PARM Start thread")
		{
			public void run()
			{
				try
				{
					long start = System.currentTimeMillis();
					PAS.get_pas().waitForFirstMap();

					System.out.println("Waited " + (System.currentTimeMillis() - start) / 1000 + " seconds for map to load");
					if(PAS.get_pas().get_parmcontroller()!=null)
						return;
				}
				catch(Exception err)
				{
					err.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable()
				//new Thread()
				{
					public void run()
					{
						PAS.get_pas().init_parmcontroller();
						PAS.get_pas().get_parmcontroller().setExpandedNodes();
						PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_PARM_);
					}
				});
			}
		}.start();
		return true;
	}
	
	@Override
	public boolean onCloseParm()
	{
		PAS.get_pas().close_parm(false);
		return true;
	}
	
	@Override
	public boolean onRefreshParm()
	{
		PAS.get_pas().get_parmcontroller().getUpdateXML().saveProject();
		return true;
	}
	@Override
	public boolean onSetAppTitle(PAS pas, String s, final UserInfo userinfo)
	{
		if(s.length()==0)
			s = PAS.l("common_app_title");
		pas.setMainTitle(s + " - " + pas.get_sitename());
		pas.setTitle(pas.getMainTitle() + "        " + PAS.l("projectdlg_project")+ " - " + PAS.l("projectdlg_no_project")); //+ m_sz_sitename);		
		return true;
	}
	@Override
	public boolean onDepartmentChanged(PAS pas)
	{
		if(!pas.get_rightsmanagement().read_parm()) {
			pas.get_mainmenu().get_selectmenu().get_bar().get_parm().setEnabled(false);
			if(PAS.isParmOpen())//if(PAS.get_pas().get_parmcontroller() != null)
				pas.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "act_close_parm"));
		}
		else if(pas.get_rightsmanagement().read_parm() && pas.get_userinfo().get_current_department().get_pas_rights() != 4) {
			if(pas.get_parmcontroller()==null && pas.get_settings().parm()) {
				pas.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_start_parm"));
			}	
			pas.get_mainmenu().get_selectmenu().get_bar().get_parm().setEnabled(true);
		}
		if(!pas.get_rightsmanagement().read_fleetcontrol()) {
			pas.get_mainmenu().get_selectmenu().get_bar().get_fleetcontrol().setEnabled(false);
		}
		else
			pas.get_mainmenu().get_selectmenu().get_bar().get_fleetcontrol().setEnabled(true);
		
		if(pas.get_rightsmanagement().cansend() || pas.get_rightsmanagement().cansimulate()) {
			pas.get_mainmenu().get_selectmenu().get_bar().showNewProject(true); //get_file_new_project().setEnabled(true);
			pas.get_mainmenu().get_selectmenu().get_bar().showNewSending(true);//get_file_new_sending().setEnabled(true);
			pas.get_mainmenu().get_selectmenu().get_bar().showFileImport(true);//get_file_import().setEnabled(true);
		}
		else {
			pas.get_mainmenu().get_selectmenu().get_bar().showNewProject(false); //get_file_new_project().setEnabled(false);
			pas.get_mainmenu().get_selectmenu().get_bar().showNewSending(false); //.setEnabled(false);
			pas.get_mainmenu().get_selectmenu().get_bar().showFileImport(false);//get_file_import().setEnabled(false);
			pas.close_active_project(true, true);
		}
		switch(pas.get_userinfo().get_current_department().get_pas_rights())
		{
		case 0:
			//none
			System.exit(0);
			break;
		case 1:
		case 2:
			pas.get_mainmenu().setTASMode(false);
			//pas.setAppTitle("");
			onSetAppTitle(pas, "", PAS.get_pas().get_userinfo());
			pas.get_eastcontent().remove_tab(EastContent.PANEL_TAS_);
			pas.get_mainmenu().enable_mapsite(true);
			break;
		case 4: //TAS
			pas.get_mainmenu().setTASMode(true);
			//pas.setAppTitle("UMS - " + PAS.l("main_tas_appname"));
			onSetAppTitle(pas, "UMS - " + PAS.l("main_tas_appname"), PAS.get_pas().get_userinfo());
			pas.get_eastcontent().InitTAS();
			pas.get_eastcontent().flip_to(EastContent.PANEL_TAS_);
			pas.get_mainmenu().enable_mapsite(false);
			break;
		}
	
		
		if(pas.get_rightsmanagement().status())
			pas.get_mainmenu().get_selectmenu().get_bar().get_status().setEnabled(true);
		else
			pas.get_mainmenu().get_selectmenu().get_bar().get_status().setEnabled(false);	
		
		//if(pas.get_rightsmanagement().houseeditor()>=1)
		pas.get_mainmenu().setHouseeditorEnabled((pas.get_rightsmanagement().houseeditor()>=1 ? true : false));
		return true;
	}

	

	@Override
	public InfoPanel onCreateInfoPanel() {
		InfoPanel panel = new InfoPanel();
		panel.doInit();
		return panel;
	}


	@Override
	public boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel) {
		tab.addTab(PAS.l("main_infotab_title"), null,
				panel,
				//sp,
				PAS.l("main_infotab_title_tooltip"));
		return true;
	}



	@Override
	public LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader) {
		try
		{
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);	
			SubstanceOfficeBlue2007LookAndFeel laf = new SubstanceOfficeBlue2007LookAndFeel();
			UIManager.setLookAndFeel(laf);
			return laf;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}
	
	

	@Override
	protected boolean onGetInitialUIDefaults() {
		if(uidefaults_initial==null)
			uidefaults_initial = (UIDefaults)UIManager.getDefaults().clone();
		return true;
	}


	@Override
	public boolean onSetUserLookAndFeel(final Settings settings, final UserInfo userinfo) {
		try
		{
			//SubstanceLookAndFeel.setCurrentTheme(SubstanceTheme.getTheme(m_settings.getThemeClassName()));
			//ClassLoader loader = new ClassLoader(); //getSystemResource(m_settings.getThemeClassName());
			//SubstanceTheme activeTheme = new SubstanceMixTheme(m_settings.getThemeClassName());
			        //new SubstancePurpleTheme(),
			        //new SubstanceBarbyPinkTheme()).saturate(0.1);
			//SubstanceLookAndFeel.setCurrentTheme(activeTheme);

			//Substance 3.3
			////
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						//UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel");
						boolean b;
						//b = SubstanceLookAndFeel.setSkin(m_settings.getSkinClassName());
						b = SubstanceLookAndFeel.setCurrentTheme(settings.getThemeClassName());
					
						//themeChanged();
						
						//active_theme = (UMSTheme)SubstanceLookAndFeel.getTheme();
						//active_theme = new UMSTheme(THEMETYPE.SIMPLE);
						//theme = theme.saturate(0.1, true);
						//theme = theme.tint(0.15);
						//theme = theme.hueShift(0.2);
						//theme = theme.shade(0.1);
						
						
						//b = SubstanceLookAndFeel.setCurrentTheme(active_theme);
						
						
						
						b = SubstanceLookAndFeel.setCurrentButtonShaper(settings.getButtonShaperClassname());
						if(settings.getGradientClassname()!=null && settings.getGradientClassname().length() > 0)
							b = SubstanceLookAndFeel.setCurrentGradientPainter(settings.getGradientClassname());
						else
							b = SubstanceLookAndFeel.setCurrentGradientPainter("org.jvnet.substance.painter.GlassGradientPainter");
						if(settings.getTitlePainterClassname()!=null && settings.getTitlePainterClassname().length() > 0)
							b = SubstanceLookAndFeel.setCurrentTitlePainter(settings.getTitlePainterClassname());
						else
							b = SubstanceLookAndFeel.setCurrentTitlePainter("org.jvnet.substance.title.Glass3DTitlePainter");
						b = SubstanceLookAndFeel.setCurrentWatermark(settings.getWatermarkClassName());
						
					}
					catch(Exception e)
					{
						
					}

				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	
	

	@Override
	public boolean onUserChangedLookAndFeel(Settings settings) {
		try
		{
			
			//String szname = UIManager.getLookAndFeel().getLayoutStyle().getClass().getName();
			//m_settings.setThemeClassName(szname);
			String szname = UIManager.getLookAndFeel().getClass().getName();
			
			szname = SubstanceLookAndFeel.getCurrentWatermark().getClass().getName();
			settings.setWatermarkClassName(szname);

			szname = SubstanceLookAndFeel.getCurrentButtonShaper().getClass().getName();
			settings.setButtonShaperClassName(szname);
			
			szname = SubstanceLookAndFeel.getCurrentGradientPainter().getClass().getName();
			settings.setGradientClassname(szname);
			
			szname = SubstanceLookAndFeel.getCurrentTitlePainter().getClass().getName();
			settings.setTitlePainterClassname(szname);
			//SubstanceLookAndFeel.getCurrentDecorationPainter().getClass().getName();
			//m_settings.setTitlePainterClassname(szname);
			
			szname = SubstanceLookAndFeel.getTheme().getClass().getName();
			settings.setThemeClassName(szname);
		
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}







	@Override
	public ImageIcon onLoadAppIcon() {
		try
		{
			return no.ums.pas.ums.tools.ImageLoader.load_icon("pas_appicon_16.png");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public boolean onBeforeLoadMap(Settings settings) {
		return true;
	}


	@Override
	public boolean onWmsLayerListLoaded(List<Layer> layers, ArrayList<String> check) {
		return false;
	}

	@Override
	public List<String> onSendErrorMessages(String concat_errorlist, MailAccount account, ActionListener callback) {
		List<String> arr_adr = new ArrayList<String>();
		arr_adr.add("mh@ums.no");
		arr_adr.add("sa@ums.no");		
		MailCtrl mc = new MailCtrl(account.get_helo(),account.get_mailserver(),account.get_port(),account.get_displayname(),account.get_mailaddress(),arr_adr, callback,"PAS error", concat_errorlist);
		return arr_adr;
	}

	@Override
	public boolean onSoapFaultException(UserInfo info, SOAPFaultException e) {
		int idx1 = e.getLocalizedMessage().indexOf(">")+2;
		int idx2 = e.getLocalizedMessage().indexOf(":", idx1);
		String sz_class = e.getLocalizedMessage().substring(idx1, idx2);
		if(sz_class.equals("com.ums.UmsCommon.USessionExpiredException"))
		{
			return onSessionTimedOutException(info);
		}
		return false;
	}


	@Override
	protected boolean onSessionTimedOutException(UserInfo info) {
		try
		{
			PAS.get_pas().setEnabled(false);
			//PAS.get_pas().setVisible(false);
			info.set_session_active(false);
			if(!PAS.APP_EXIT)
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						try
						{
							ClassLoader classloader = PAS.get_pas().getClass().getClassLoader();
							Class cl = classloader.loadClass("no.ums.pas.pluginbase.defaults.DisabledLookAndFeel");
							LookAndFeel laf = (LookAndFeel)cl.newInstance();
							UIManager.setLookAndFeel(laf);
							SwingUtilities.updateComponentTreeUI(PAS.get_pas());
							onSetAppTitle(PAS.get_pas(), " [SESSION TIMED OUT]", PAS.get_pas().get_userinfo());
						}
						catch(Exception err)
						{
							
						}
					}
				});
				Logon logon = new Logon(PAS.get_pas(), new LogonInfo(PAS.get_pas().get_settings().getUsername(),
						PAS.get_pas().get_settings().getCompany()), 
						PAS.get_pas().get_settings().getLanguage(),
						true);
				if(!logon.isLoggedOn())
					System.exit(0);
	
				if(logon.get_userinfo()==null) {
					System.exit(0);
				}
				PAS.get_pas().get_userinfo().set_sessionid(logon.get_userinfo().get_sessionid());
				PAS.pasplugin.onSessionRenewed(PAS.get_pas().get_userinfo());
			}
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}


	@Override
	public boolean onSessionRenewed(final UserInfo ui) {
		ui.set_session_active(true);
		//onUserChangedLookAndFeel(PAS.get_pas().get_settings());
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				PAS.get_pas().setEnabled(true);
				//PAS.get_pas().setVisible(true);
				//onSetInitialLookAndFeel(ui.getClass().getClassLoader());
				onSetUserLookAndFeel(PAS.get_pas().get_settings(), ui);
				onSetAppTitle(PAS.get_pas(), "", ui);
				PAS.get_pas().toFront();
			}
		});
		return true;
	}


	@Override
	public boolean onStartSystemMessageThread(final ActionListener callback, final int n_interval_msec) {
		new Thread("PAS System Messages") {
			public void run()
			{
				while(!PAS.APP_EXIT)
				{
					try
					{
						onExecAskForSystemMessage(callback);
						Thread.sleep(n_interval_msec);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}.start();
		return true;
	}

	@Override
	public boolean onExecAskForSystemMessage(ActionListener callback) {
		WSGetSystemMessages msg = new WSGetSystemMessages(callback);
		msg.runNonThreaded();
		return true;
	}

	@Override
	public boolean onHelpAbout() {
		return false;
	}

	@Override
	public boolean onTrainingMode(boolean b) {
		System.out.println("TrainingMode=" + b);
		return true;
	}
	
	/**
	 * Function to determine if a user has activated training mode
	 * @param ui UserInfo struct may be used to determine if it's training mode
	 * @return true if user is in training mode
	 */
	@Override
	protected boolean IsInTrainingMode(final UserInfo userinfo)
	{
		//boolean cansend = (userinfo.get_current_department().get_userprofile().get_send() >= 1);
		//return !cansend;
		return PAS.TRAINING_MODE;
	}


	@Override
	public boolean onLogonAddControls(LogonPanel p) {
		p.add_controls();
		return true;
	}

	@Override
	public boolean onCustomizeLogonDlg(LogonDialog dlg) {
		System.out.println("onCustomizeLogonDlg");
		return true;
	}

	@Override
	public boolean onPaintMenuBarExtras(JMenuBar bar, Graphics g) {
		return true;
	}

	@Override
	public boolean onMapCalcNewCoords(Navigation nav, PAS p) {
		p.get_statuscontroller().calcHouseCoords();
		p.get_housecontroller().calcHouseCoords();
		if(p.get_statuscontroller().get_sendinglist()!=null) {
			for(int i=0; i < p.get_statuscontroller().get_sendinglist().size(); i++) {
				try {
					if(p.get_statuscontroller().get_sendinglist().get_sending(i).get_shape()!=null)
						p.get_statuscontroller().get_sendinglist().get_sending(i).get_shape().calc_coortopix(nav);
				} catch(Exception e) {
					
				}
			}
		}
		
		try
		{
			for(int i=0; i < p.get_sendcontroller().get_sendings().size(); i++)
			{
				try
				{
					p.get_sendcontroller().get_sendings().get(i).get_sendproperties().calc_coortopix();
				}
				catch(Exception e)
				{
					
				}
				
			}
		}
		catch(Exception e)
		{
			
		}
		p.get_gpscontroller().calcGpsCoords();	
		if(p.get_parmcontroller()!=null)
			p.get_parmcontroller().calc_coortopix();
		if(p.get_eastcontent().get_taspanel()!=null)
			p.get_eastcontent().get_taspanel().calc_coortopix();
		try
		{
			DeptArray depts = p.get_userinfo().get_departments();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).CalcCoorRestrictionShapes();
			}
			List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).calc_coortopix(p.get_navigation());
			}
			//get_pas().get_userinfo().get_current_department().CalcCoorRestrictionShapes();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			p.get_mappane().get_active_shape().calc_coortopix(PAS.get_pas().get_navigation());
		}
		catch(Exception e)
		{
			
		}
		return true;
	}







	@Override
	public boolean onMapDrawLayers(Navigation nav, Graphics g, PAS p) {
		try
		{
			
			DeptArray depts = p.get_userinfo().get_departments();
			//depts.ClearCombinedRestrictionShapelist();
			//depts.CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
			//depts.test();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).drawRestrictionShapes(g, nav);
			}
			List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).draw(g, nav, false, true, false, null, true, true, 2, false);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(p.get_parmcontroller()!=null)
			p.get_parmcontroller().drawLayers(g);
		try {
			p.get_sendcontroller().draw_polygons(g, PAS.get_pas().get_mappane().get_current_mousepos());
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			p.get_mappane().get_active_shape().draw(g, nav, false, false, true, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 1, false);
		} catch(Exception e) { }
		if(p.get_mainmenu().get_selectmenu().get_bar().get_show_houses())
			p.get_housecontroller().drawItems(g);
		try {
			p.get_mappane().draw_pinpoint(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			p.get_mappane().draw_adredit(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(p.get_mappane().get_mode()==MapFrame.MAP_MODE_HOUSEEDITOR_) {
				switch(p.get_mappane().get_submode()) {
					case MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR:
					case MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR:
						p.get_mappane().draw_moveinhab_text(g);
						break;
				}
						
			}
		} catch(Exception e) { }
		try {
			p.get_statuscontroller().drawItems(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		//get_pas().get_mappane().drawOnEvents(m_gfx_buffer);
		try {
			p.get_gpscontroller().drawItems(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			p.get_housecontroller().draw_details(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(p.get_eastcontent().get_taspanel()!=null)
			{
				p.get_eastcontent().get_taspanel().drawItems((Graphics2D)g);
				p.get_eastcontent().get_taspanel().drawLog((Graphics2D)g);
			}
		} catch(Exception e) { 
			e.printStackTrace();
		}
		return true;
	}


	@Override
	public boolean onFrameResize(JFrame f, ComponentEvent e) {
		return true;
	}

	
	
		
}