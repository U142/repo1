package no.ums.pas.plugins.centric;

import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.pluginbase.PAS_Scripting;
import javax.swing.*;
import org.jvnet.substance.*;
import no.ums.pas.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import no.ums.pas.send.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.logon.*;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.*;
import no.ums.pas.maps.defines.*;


public class plugin_Centric extends PAS_Scripting
{
	public plugin_Centric()
	{
		super();
		System.out.println("plugin_Centric loaded");
	}
	
	@Override
	protected void setSubPluginNames() {
		System.out.println("***Using Plugins (plugin_Centric)***");
		System.out.println((this.plugin_AddressSearch = "no.ums.pas.plugins.centric.CentricAddressSearch"));
	}

	@Override
	public boolean onBeforeLogon()
	{
		super.onBeforeLogon();
		new DisclaimerDialog();

		return true;
	}
	
	
	@Override
	public boolean onAfterLogon()
	{
		super.onAfterLogon();
		try
		{
			//SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.SaharaSkin");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	@Override
	public boolean onShowMainWindow()
	{
		super.onShowMainWindow();
		/*new Thread()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						Thread.sleep(10000);
						System.out.println("TRALLALA");
					}
				}
				catch(Exception e)
				{
					
				}
			}
		}.start();*/
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
		
		JButton btn_goto_restriction = new JButton("Home");
		btn_goto_restriction.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		btn_goto_restriction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				PAS.get_pas().actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape().get(0).getFullBBox(),ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			}
		});
		menu.set_gridconst(3, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(btn_goto_restriction, menu.m_gridconst);

		return true;
	}
	
	@Override
	public boolean onAddMainSelectMenu(MainMenuBar menu)
	{
		super.onAddMainSelectMenu(menu);
		menu.remove(menu.get_menu_navigate());
		menu.remove(menu.get_dept());
		menu.remove(menu.get_menu_layout());
		menu.remove(menu.get_parm());
		menu.get_status().remove(menu.get_item_status_export());
		menu.get_status().remove(menu.get_item_status_updates());
		menu.get_view().remove(menu.get_item_view_showhouses());
		menu.get_item_view_showhouses().setSelected(false);
		//menu.set_show_houses_invoke(false);
		return true;
	}
	
	@Override 
	public boolean onAddSendOptionToolbar(SendOptionToolbar toolbar)
	{
		toolbar.show_buttons(
				SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_|
				SendOptionToolbar.BTN_SENDINGTYPE_ELLIPSE_|
				SendOptionToolbar.BTN_OPEN_|
				SendOptionToolbar.TXT_RECIPIENTTYPES_|
				SendOptionToolbar.BTN_ADRTYPES_NOFAX_, 
				false);
		return super.onAddSendOptionToolbar(toolbar);
	}
	
	@Override
	public boolean onAddPASComponents(PAS p)
	{
		System.out.println("onAddPASComponents");
		p.getContentPane().add(p.get_mappane(), BorderLayout.CENTER);
		p.add(p.get_mainmenu(), BorderLayout.NORTH);
		p.add(p.get_southcontent(), BorderLayout.SOUTH);
		p.add(p.get_eastcontent(), BorderLayout.WEST);
		//p.getContentPane().setLayout(new GridBagLayout());
		/*DefaultPanel panel = new DefaultPanel() {
			@Override
			public void actionPerformed(ActionEvent e) { }
			public void init() { }
			public void add_controls() { }
		};
		panel.set_gridconst(0,0,2,1);
		panel.add(p.get_mainmenu(), panel.get_gridconst());
		panel.set_gridconst(0,1,1,1);
		panel.add(p.get_mappane(), panel.get_gridconst());
		panel.set_gridconst(1,1,1,1);
		panel.add(p.get_eastcontent(), panel.get_gridconst());
		//panel.set_gridconst(0,2,2,1);
		//panel.add(p.get_southcontent(), panel.get_gridconst());
		
		p.getContentPane().add(panel, BorderLayout.CENTER);*/
		
		return true;
	}
	
	@Override
	public boolean onSetInitialMapBounds(Navigation nav, UserInfo ui)
	{
		nav.setNavigation(ui.get_departments().get_combined_restriction_shape().get(0).getFullBBox());
		return true;
	}
	
	@Override 
	public boolean onStartParm()
	{
		System.out.println("onStartParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onCloseParm()
	{
		System.out.println("onCloseParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onRefreshParm()
	{
		System.out.println("onRefreshParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onDepartmentChanged(PAS pas)
	{
		super.onDepartmentChanged(pas);
		//PAS.get_pas().setAppTitle("UMS/Centric - " + pas.get_userinfo().get_current_department().get_deptid());
		return true;
	}
	
	@Override
	public boolean onSetAppTitle(PAS pas, String s)
	{
		System.out.println("onSetAppTitle");
		pas.setMainTitle("UMS/Centric Burger Alert - " + pas.get_userinfo().get_current_department().get_deptid());
		pas.setTitle(pas.getMainTitle());
		return true;
	}

	@Override
	public InfoPanel onCreateInfoPanel() {
		InfoPanel panel = new CentricInfoPanel();
		panel.doInit();
		return panel;
	}
	
	
	


}