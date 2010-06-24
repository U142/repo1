package no.ums.pas.plugins.centric;

import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.pluginbase.PAS_Scripting;
import javax.swing.*;
import javax.xml.ws.soap.SOAPFaultException;

import org.geotools.data.ows.Layer;
import org.jvnet.substance.*;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import no.ums.pas.*;

import java.awt.*;

import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import no.ums.pas.send.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.logon.*;
import no.ums.pas.core.logon.LogonDialog.LogonPanel;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.mainui.GeneralPanel;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.*;
import no.ums.pas.core.themes.UMSTheme;
import no.ums.pas.core.themes.UMSTheme.THEMETYPE;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.maps.MapFrame;
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
		//menu.set_gridconst(2, 1, 1, 1, GridBagConstraints.NORTHWEST);
		//menu.add(menu.get_btn_search(), menu.m_gridconst);
		
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

		
		JButton btn_draw_polygon = new JButton("Draw Polygon");
		btn_draw_polygon.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		btn_draw_polygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				PAS.get_pas().get_mappane().set_active_shape(new PolygonStruct(null));
				PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
				PAS.get_pas().repaint();
			}
		});
		menu.set_gridconst(4, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(btn_draw_polygon, menu.m_gridconst);

		JButton btn_draw_ellipse = new JButton("Draw Ellipse");
		btn_draw_ellipse.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		btn_draw_ellipse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				PAS.get_pas().get_mappane().set_active_shape(new PolygonStruct(null));
				PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON);
				PAS.get_pas().repaint();
			}
		});
		menu.set_gridconst(5, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(btn_draw_ellipse, menu.m_gridconst);
		
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
		menu.remove(menu.get_status());
		menu.get_menu_file().remove(menu.get_item_new_sending());
		menu.get_menu_file().remove(menu.get_item_file_print_map());
		menu.get_menu_file().remove(menu.get_item_file_save_map());
		menu.get_menu_file().remove(menu.get_item_fileimport());
		menu.get_status().remove(menu.get_item_status_export());
		menu.get_status().remove(menu.get_item_status_updates());
		menu.get_view().remove(menu.get_item_view_showhouses());
		menu.get_item_view_showhouses().setSelected(false);
		menu.add(menu.get_menu_help());
		menu.get_menu_config().add(menu.get_item_training_mode());

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
	public boolean onAddPASComponents(final PAS p)
	{
		System.out.println("onAddPASComponents");
		p.add(p.get_mappane(), BorderLayout.CENTER);
		p.add(p.get_mainmenu(), BorderLayout.NORTH);
		p.add(p.get_southcontent(), BorderLayout.SOUTH);
		p.add(p.get_eastcontent(), BorderLayout.EAST);
		//p.setJMenuBar(p.get_mainmenu().get_selectmenu().get_bar());

		//p.setJMenuBar(p.get_mainmenu().get_selectmenu().get_bar());
		/*DefaultPanel panel = new DefaultPanel() {
			
			@Override
			public void init() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void add_controls() {				
				set_gridconst(0, 0, 2, 1, GridBagConstraints.WEST);
				add(p.get_mainmenu(), get_gridconst());
				
				set_gridconst(0, 1, 1, 1);
				add(p.get_mappane(), get_gridconst());
				
				set_gridconst(1, 1, 1, 1);
				add(p.get_eastcontent(), get_gridconst());				
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		panel.add_controls();
		p.getContentPane().add(panel, GridBagConstraints.CENTER);*/


		
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
		//return super.onStartParm();
		System.out.println("onStartParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onCloseParm()
	{
		//return super.onCloseParm();
		System.out.println("onCloseParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onRefreshParm()
	{
		//return super.onRefreshParm();
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
	public boolean onSetAppTitle(PAS pas, String s, UserInfo userinfo)
	{
		boolean trainingmode = IsInTrainingMode(userinfo);
		System.out.println("onSetAppTitle");
		pas.setMainTitle(
				"UMS/Centric Burger Alert - " + 
				pas.get_userinfo().get_current_department().get_deptid() + 
				(trainingmode ? "  [TRAINING MODE] " : " ") + s);
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
		//return super.onLoadAppIcon();
		return no.ums.pas.ums.tools.ImageLoader.load_icon("no/ums/pas/plugins/centric/", "alert-icon.png", getClass().getClassLoader());
		/*try
		{
			//return new ImageIcon(getClass().getClassLoader().getResource("no/ums/pas/plugins/centric/alert-icon.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}*/
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
				cl = (Class<LookAndFeel>)classloader.loadClass("no.ums.pas.pluginbase.defaults.DefaultWindowsLookAndFeel"); //"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				if(uidefaults_initial!=null)
				{
					ArrayList<Object> defaults = new ArrayList<Object>();
					Enumeration<Object> keys = uidefaults_initial.keys();
					while(keys.hasMoreElements()) {
						Object key = keys.nextElement();
						defaults.add(key);
						defaults.add(uidefaults_initial.get(key));
					}
					UIManager.getDefaults().putDefaults(defaults.toArray());
				}
				//cl = (Class<LookAndFeel>)classloader.loadClass(UIManager.getCrossPlatformLookAndFeelClassName());
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
			onGetInitialUIDefaults();
			/*if(IsInTrainingMode(userinfo))
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
			}*/
			onSetInitialLookAndFeel(this.getClass().getClassLoader());

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
	

	@Override
	public boolean onSoapFaultException(UserInfo info, SOAPFaultException e) {
		return super.onSoapFaultException(info, e);
	}

	@Override
	protected boolean onSessionTimedOutException(UserInfo info) {
		return super.onSessionTimedOutException(info);
	}

	@Override
	public boolean onHelpAbout() {
		
		JOptionPane.showMessageDialog(PAS.get_pas(), "Blablabla...", "About NL Alert", JOptionPane.INFORMATION_MESSAGE);
		return super.onHelpAbout();
	}

	@Override
	public boolean onTrainingMode(boolean b) {
		if(b)
		{
			onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
		}
		else
		{
			onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
		}
		PAS.get_pas().repaint();
		//onSetUserLookAndFeel(PAS.get_pas().get_settings(), PAS.get_pas().get_userinfo());
		return super.onTrainingMode(b);
	}

	
	
	@Override
	public boolean onAfterPowerUp(LogonDialog dlg, WSRESULTCODE ws) {
		if(ws==WSRESULTCODE.OK)
			dlg.set_errortext(PAS.l("logon_ws_active"), false);
		else
			dlg.set_errortext(PAS.l("logon_ws_inactive"));
		return true;
	}

	@Override
	public boolean onLogonAddControls(LogonPanel p) {
		int verticalspacing = 10;
		p.m_gridconst.fill = GridBagConstraints.HORIZONTAL;
		p.m_gridconst.anchor = GridBagConstraints.CENTER;
		

		p.set_gridconst(3,p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getLblCompId(), p.m_gridconst);
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getCompId(), p.m_gridconst);

		p.add_spacing(p.DIR_VERTICAL, verticalspacing);

		p.set_gridconst(3, p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getLblUserId(), p.m_gridconst);
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getUserId(), p.m_gridconst);

		p.add_spacing(p.DIR_VERTICAL, verticalspacing);

		p.set_gridconst(3,p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getLblPasswd(), p.m_gridconst);
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getPasswd(), p.m_gridconst);

		p.add_spacing(p.DIR_VERTICAL, verticalspacing);
		
		
		p.set_gridconst(3,p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getBtnSubmit(), p.m_gridconst);			

		JButton btn_cancel = new JButton(PAS.l("common_cancel"));
		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(btn_cancel, p.m_gridconst);			
		
		
		p.set_gridconst(0,p.inc_panels(),7,1, GridBagConstraints.CENTER);
		p.add(p.getLblError(), p.m_gridconst);
		
		
		
		//p.set_gridconst(0,p.inc_panels(),7,1);
		//p.add(p.getNSList(), p.m_gridconst);
		return true;

	}

	@Override
	public boolean onCustomizeLogonDlg(LogonDialog dlg) {
		dlg.setSize(new Dimension(350,250));
		dlg.get_logonpanel().getCompId().setEditable(false);
		dlg.get_logonpanel().getCompId().setText("UMS");
		dlg.get_logonpanel().getBtnSubmit().setText(PAS.l("common_ok"));

		/*dlg.get_logonpanel().getNSList().setVisible(false);
		dlg.get_logonpanel().getCompId().setEditable(false);
		dlg.get_logonpanel().getCompId().setText("UMS");
		dlg.get_logonpanel().getLblLanguage().setVisible(false);
		dlg.get_logonpanel().getLanguageCombo().setVisible(false);
		dlg.get_logonpanel().getLblUserId().setPreferredSize(new Dimension(150, 30));*/
		return super.onCustomizeLogonDlg(dlg);
	}

	@Override
	public boolean onPaintMenuBarExtras(JMenuBar bar, Graphics g) {
		//MARK LIVE/TRAINING MODE
		g.setFont(UIManager.getFont("InternalFrame.titleFont"));

		String str = "LIVE";
		if(IsInTrainingMode(PAS.get_pas().get_userinfo()))
			str = "TRAINING MODE";
		int strwidth = g.getFontMetrics().stringWidth(str);
		int x = bar.getWidth()/2 - strwidth/2;
		int y = bar.getHeight()/2-9;
		int w = strwidth;
		int h = bar.getHeight()/2+5;
		g.setColor(new Color(230, 100, 100, 250));
		g.fillRoundRect(x-5, y, w+10, h, 2, 2);
		g.setColor(Color.black);
		g.drawRoundRect(x-5, y, w+10, h, 2, 2);
		g.drawString(str, x, h);
		
		//HELPDESK
		g.setColor(Color.black);
		str = "Helpdesk: 0123456789";
		strwidth = g.getFontMetrics().stringWidth(str); 
		x = bar.getWidth() - strwidth - 20;
		w = strwidth;
		//g.drawRoundRect(x-5, y, w+10, h, 2, 2);
		g.drawString(str, x, h);
		return super.onPaintMenuBarExtras(bar, g);
	}

	@Override
	public boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel) {
		return true;
	}

	@Override
	public boolean onMapCalcNewCoords(Navigation nav, PAS p) {
		//return super.onMapCalcNewCoords(nav, p);
		p.get_statuscontroller().calcHouseCoords();
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
		try {
			p.get_mappane().get_active_shape().draw(g, nav, false, false, true, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 1, false);
		} catch(Exception e) { }
		try {
			p.get_mappane().draw_pinpoint(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }

		return true;
	}
	
	


}