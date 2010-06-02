package no.ums.pas.pluginbase;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;


import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.*;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.send.SendOptionToolbar;

import org.jvnet.substance.*;


public class PAS_Scripting extends PasScriptingInterface
{
	
	public PAS_Scripting()
	{
		super();		
		System.out.println("PAS_Scripting loaded");
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
		//m_menu_gps.add(m_item_gps_epsilon);
		//m_item_gps_epsilon.add(m_item_gps_epsilon_slider);
		
		menu.get_status().add(menu.get_item_status_updates());

		return true;
	}
	
	@Override
	public boolean onAddPASComponents(PAS p)
	{
		p.getContentPane().add(p.get_mappane(), BorderLayout.CENTER);
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
	public boolean onSetAppTitle(PAS pas, String s)
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
			onSetAppTitle(pas, "");
			pas.get_eastcontent().remove_tab(EastContent.PANEL_TAS_);
			pas.get_mainmenu().enable_mapsite(true);
			break;
		case 4: //TAS
			pas.get_mainmenu().setTASMode(true);
			//pas.setAppTitle("UMS - " + PAS.l("main_tas_appname"));
			onSetAppTitle(pas, "UMS - " + PAS.l("main_tas_appname"));
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
	
	
		
}