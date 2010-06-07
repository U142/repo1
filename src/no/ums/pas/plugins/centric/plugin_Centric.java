package no.ums.pas.plugins.centric;

import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.pluginbase.PAS_Scripting;
import javax.swing.*;

import org.geotools.data.ows.Layer;
import org.jvnet.substance.*;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import no.ums.pas.*;

import java.awt.*;

import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import no.ums.pas.send.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.logon.*;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.mainui.GeneralPanel;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.*;
import no.ums.pas.core.themes.UMSTheme;
import no.ums.pas.core.themes.UMSTheme.THEMETYPE;
import no.ums.pas.maps.WMSLayerSelectorPanel;
import no.ums.pas.maps.defines.*;


public class plugin_Centric extends PAS_Scripting
{
	WMSLayerSelectorPanel wms_layer_selector = new WMSLayerSelectorPanel();

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
		boolean b = new DisclaimerDialog().isConfirmed();
		if(!b)
		{
			System.out.println("User denied Disclaimer");
			System.exit(0);
		}
		System.out.println("User accepted Disclaimer");
		return b;
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
		//menu.remove(menu.get_menu_navigate());
		//menu.remove(menu.get_dept());
		menu.remove(menu.get_menu_layout());
		//menu.remove(menu.get_parm());
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
		p.add(p.get_mappane(), BorderLayout.CENTER);
		p.add(p.get_mainmenu(), BorderLayout.NORTH);
		p.add(p.get_southcontent(), BorderLayout.SOUTH);
		p.add(p.get_eastcontent(), BorderLayout.EAST);
		//p.get_mappane().add(wms_layer_selector, BorderLayout.WEST);
		//wms_layer_selector.setVisible(false);
		
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
		return super.onStartParm();
		//System.out.println("onStartParm - PARM is invalid in this plugin");
		//return false;
	}
	
	@Override
	public boolean onCloseParm()
	{
		return super.onCloseParm();
		//System.out.println("onCloseParm - PARM is invalid in this plugin");
		//return false;
	}
	
	@Override
	public boolean onRefreshParm()
	{
		return super.onRefreshParm();
		//System.out.println("onRefreshParm - PARM is invalid in this plugin");
		//return false;
	}
	
	@Override
	public boolean onDepartmentChanged(PAS pas)
	{
		super.onDepartmentChanged(pas);
		//PAS.get_pas().setAppTitle("UMS/Centric - " + pas.get_userinfo().get_current_department().get_deptid());
		return true;
	}
	
	@Override
	public boolean onSetAppTitle(PAS pas, String s, UserInfo userinfo)
	{
		boolean trainingmode = _IsTrainingMode(userinfo);
		System.out.println("onSetAppTitle");
		pas.setMainTitle(
				"UMS/Centric Burger Alert - " + 
				pas.get_userinfo().get_current_department().get_deptid() + 
				(trainingmode ? "  [TRAINING MODE] " : ""));
		pas.setTitle(pas.getMainTitle());
		return true;
	}

	@Override
	public InfoPanel onCreateInfoPanel() {
		InfoPanel panel = new CentricInfoPanel();
		panel.doInit();
		return panel;
	}

	@Override
	public ImageIcon onLoadAppIcon() {
		return super.onLoadAppIcon();
	}

	@Override
	public LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader) {
		try
		{
			Class<LookAndFeel> cl = null;
			switch(operating_system)
			{
			case MAC:
				cl = (Class<LookAndFeel>)classloader.loadClass("javax.swing.plaf.mac.MacLookAndFeel");
				break;
			case UNIX:
				cl = (Class<LookAndFeel>)classloader.loadClass("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				break;
			case WIN:
				cl = (Class<LookAndFeel>)classloader.loadClass("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				break;
			}
			LookAndFeel laf = (LookAndFeel)cl.newInstance();
			UIManager.setLookAndFeel(laf);
			JDialog.setDefaultLookAndFeelDecorated(false);
			JFrame.setDefaultLookAndFeelDecorated(false);
			if(PAS.get_pas()!=null)
				SwingUtilities.updateComponentTreeUI(PAS.get_pas());
			return laf;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{//default to crossplatform LAF

			System.out.println("Loading cross platform LAF");
			Class cl = classloader.loadClass(UIManager.getCrossPlatformLookAndFeelClassName());
			LookAndFeel laf = (LookAndFeel)cl.newInstance();
			UIManager.setLookAndFeel(laf);
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);	
			SwingUtilities.updateComponentTreeUI(PAS.get_pas());

			return laf;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean onSetUserLookAndFeel(Settings settings, final UserInfo userinfo) {
		try
		{
			if(_IsTrainingMode(userinfo))
			{
				ClassLoader classloader = settings.getClass().getClassLoader();
				Class cl = classloader.loadClass("no.ums.pas.plugins.centric.TrainingLookAndFeel");
				LookAndFeel laf = (LookAndFeel)cl.newInstance();
				UIManager.setLookAndFeel(laf);
				SwingUtilities.updateComponentTreeUI(PAS.get_pas());
			}
			else
			{
				onSetInitialLookAndFeel(this.getClass().getClassLoader());
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	

	@Override
	public boolean onUserChangedLookAndFeel(Settings settings) {
		return false;
	}

	@Override
	public boolean onBeforeLoadMap(Settings settings) {
		/*if(settings.getMapServer()==MAPSERVER.WMS)
		{
			wms_layer_selector.setVisible(true);
		}
		else
			wms_layer_selector.setVisible(false);*/
		return true;
	}

	@Override
	public boolean onWmsLayerListLoaded(List<Layer> layers, ArrayList<String> check) {
		//wms_layer_selector.populate(layers, check);
		return true;
	}
	
	/**
	 * Centric specific function to determine if a user is logged on in training mode
	 * @param ui UserInfo struct used to determine if it's training mode
	 * @return
	 */
	private boolean _IsTrainingMode(final UserInfo userinfo)
	{
		boolean cansend = (userinfo.get_current_department().get_userprofile().get_send() >= 1);
		return !cansend;
	}
	
	


}