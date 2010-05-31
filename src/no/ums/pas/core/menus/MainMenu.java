package no.ums.pas.core.menus;

import java.awt.*;

import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

import java.awt.event.*;

import no.ums.pas.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.mainui.*;
import no.ums.pas.core.menus.defines.*;
import no.ums.pas.core.themes.ThemeColorComponent;
import no.ums.pas.importer.*;
import no.ums.pas.maps.*;
import no.ums.pas.maps.defines.*;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;

import org.jvnet.substance.SubstanceLookAndFeel;

//Substance 3.3
import org.jvnet.substance.SubstanceButtonBorder;
import org.jvnet.substance.SubstanceImageCreator;
import org.jvnet.substance.theme.SubstanceTheme;


//substance 5.2
//import org.jvnet.substance.utils.SubstanceImageCreator;


class MapSiteCombo extends JComboBox {
	public static final long serialVersionUID = 1;
	MapSiteCombo(MapSite [] map_site) {
		super(map_site);
	}
}

public class MainMenu extends DefaultPanel implements ComponentListener //implements ActionListener //, EventListener //MenuListener //, EventListener
{
	public static final long serialVersionUID = 1;
	protected static Color ACTIVE_COLOR;
	protected static Color INACTIVE_COLOR;
	
	public static int BTN_SIZE_WIDTH = 100;
	public static int BTN_SIZE_HEIGHT = 20;
	
	private JButton 	m_btn_zoom;
	private JButton 	m_btn_pan;

	private JButton 	m_btn_search;
	private JButton		m_btn_houseeditor;
	private JButton		m_btn_showhousedetails;
	private JButton		m_btn_zoom_to_world;
	private ButtonGroup m_group_navigation;
	private MapSiteCombo m_combo_mapsite;
	private SearchFrame m_searchframe = null;
	private MainSelectMenu m_selectmenu;

	public JButton get_btn_zoom()
	{ 
		return m_btn_zoom;
	}
	public JButton get_btn_pan()
	{
		return m_btn_pan;
	}
	public JButton get_btn_search()
	{
		return m_btn_search;
	}
	public JButton get_btn_houseeditor()
	{
		return m_btn_houseeditor;
	}
	public JButton get_btn_showhousedetails()
	{
		return m_btn_showhousedetails;
	}
	public JButton get_btn_zoom_to_world()
	{
		return m_btn_zoom_to_world;
	}
	public ButtonGroup get_btn_group_navigation()
	{
		return m_group_navigation;
	}
	public MapSiteCombo get_combo_mapsite()
	{
		return m_combo_mapsite;
	}
	
	public void clickMapMode(int mapmode, boolean b)
	{
		switch(mapmode)
		{
		case MapFrame.MAP_MODE_PAN:
		case MapFrame.MAP_MODE_PAN_BY_DRAG:
			m_btn_pan.setSelected(b);
			m_btn_zoom.setSelected(!b);
			break;
		case MapFrame.MAP_MODE_ZOOM:
			m_btn_zoom.setSelected(b);
			m_btn_pan.setSelected(!b);
			break;
		}
	}
	
	public void enable_mapsite(boolean enable) { m_combo_mapsite.setVisible(enable); }
	
	public void setHouseeditorEnabled(boolean b) {
		m_btn_houseeditor.setEnabled(b);
	}

	public MainSelectMenu get_selectmenu() { return m_selectmenu; }
	
	public void setTASMode(boolean b)
	{
		try
		{
			//m_btn_search.setVisible(!b);
			m_selectmenu.get_bar().showNavSearch(!b);
			//m_btn_houseeditor.setVisible(!b);
			m_selectmenu.get_bar().showHouseEditor(!b);
			m_selectmenu.get_bar().showHouseSelect(!b);
			m_selectmenu.get_bar().showMapSelection(true);
			//m_btn_showhousedetails.setVisible(!b);
			m_btn_zoom_to_world.setVisible(b);
			//m_selectmenu.get_bar().get_parm().setVisible(!b);
			m_selectmenu.get_bar().showFileImport(!b);//get_file_import().setVisible(!b);
			m_selectmenu.get_bar().get_fleetcontrol().setVisible(!b);
			//m_selectmenu.get_bar().get_view().setVisible(!b);
			m_selectmenu.get_bar().showNewSending(!b);//get_file_new_sending().setVisible(!b);
			m_selectmenu.get_bar().showNavSearch(!b);//get_search().setVisible(!b);
			m_selectmenu.get_bar().showViewMenu(!b);
			m_selectmenu.get_bar().showViewStatusShape(!b);
			m_selectmenu.get_bar().showViewSearchPinpoint(!b);
			m_selectmenu.get_bar().showViewHouses(!b);
			
			m_selectmenu.get_bar().showSettingsMenu(true);
			m_selectmenu.get_bar().showSettingsShowSettings(true);
			m_selectmenu.get_bar().showSettingsMessageLib(true);
			m_selectmenu.get_bar().showStatusMenu(true);
			m_selectmenu.get_bar().showStatusOpenStatus(true);
			m_selectmenu.get_bar().showStatusExportStatus(true);
			m_selectmenu.get_bar().showStatusUpdates(true);
			m_selectmenu.get_bar().showParmMenu(true);
			m_selectmenu.get_bar().showParmStart(true);
			m_selectmenu.get_bar().showParmRefresh(true);
			m_selectmenu.get_bar().showParmClose(true);
			m_selectmenu.get_bar().showDepartmentMenu(true);
			m_selectmenu.get_bar().showLayoutMenu(true);
		}
		catch(Exception e)
		{
			
		}
		
		
	}
	
	//private String[] m_mapsites = { "Scandinavia", "Germany", "Norway", "Oslo" };
	private MapSite [] m_mapsites = { 
									  //new MapSite("Scandinavia", 0, "Mow1"),
									  //new MapSite("Scand. rural", 0, "Mow2"),
									  new MapSite("By", 0, "By"),
									  new MapSite("Normal", 0, "Normal"),
									  //new MapSite("Scand mow3", 0, "Mow3"),
									  //new MapSite("Germany", 1, ""),
									  //new MapSite("Norway", 2, ""),
									  //new MapSite("Oslo", 3, ""),
									  new MapSite("Denmark 1", 0, "Mow12"),
									  new MapSite("Denmark 2", 0, "Mow22") };
									  //new MapSite("Denmark 3", 0, "Mow32"),
									  //new MapSite("Denmark 4", 0, "Mow42") };
	
	String m_sz_current_action;
	String m_sz_prev_action = "act_pan";
	
	public MainMenu(PAS pas)
	{
		super();
		m_searchframe = new SearchFrame();
		m_sz_current_action = "";
		
		//setSize(get_pas().get_mappane().get_dimension().width + get_pas().get_eastwidth(), 40);
		setBounds(0,0,/*get_pas().get_mappane().get_dimension().width + get_pas().get_eastwidth()*/getWidth(), 41);
		prepare_controls();
		add_controls();
		doLayout();
		revalidate();
		setAlignmentX(JComponent.LEFT_ALIGNMENT);
		m_selectmenu.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		m_selectmenu.get_bar().setAlignmentX(JComponent.LEFT_ALIGNMENT);
		addComponentListener(this);
		//setBackground(new Color(191, 191, 191)); //Color.white);		
		
	}
	public void componentResized(ComponentEvent e) {
		
		if(getWidth()<=0 || getHeight()<=0)
		{
			super.componentResized(e);
			return;
		}
			
		int n_width = getWidth();
		setBounds(0, 0, getWidth(), getHeight());
		m_selectmenu.setBounds(0, 0, getWidth(), 22);//new Dimension(getWidth(), 20));
		//m_selectmenu.revalidate();
		//m_selectmenu.setPreferredSize(new Dimension(getWidth(), 18));
		//m_selectmenu.get_bar().setPreferredSize(new Dimension(getWidth(), 18));
		//System.out.println("Menu resized to " + getWidth() + ", " + getHeight());
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }		
	
	public String get_current_action() { return m_sz_current_action; }
	public String get_prev_action() { return m_sz_prev_action; }
	public SearchFrame get_searchframe() { return m_searchframe; }
	
	void prepare_controls()
	{
		m_group_navigation = new ButtonGroup();
	        m_btn_pan = new JButton(PAS.l("mainmenu_navigation_pan"));
	        m_btn_pan.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_pan.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_pan.setMnemonic('p');
	        m_btn_pan.setActionCommand(ENABLE);
	        m_btn_pan.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));
	        //m_btn_pan.setFont(PAS.f().getMenuFont());

	        
	        m_btn_zoom = new JButton(PAS.l("mainmenu_navigation_zoom"));
	        m_btn_zoom.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_zoom.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_zoom.setMnemonic('z');
	        m_btn_zoom.setActionCommand(ENABLE);
	        m_btn_zoom.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));	
	        m_group_navigation.add(m_btn_pan);
	        m_group_navigation.add(m_btn_zoom);
	        //m_btn_zoom.setFont(PAS.f().getMenuFont());

	        m_btn_search = new JButton(PAS.l("mainmenu_navigation_search"));
	        m_btn_search.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_search.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_search.setMnemonic('s');
	        m_btn_search.setActionCommand(ENABLE);
	        m_btn_search.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));	
	        //m_btn_search.setFont(PAS.f().getMenuFont());

	        m_btn_houseeditor = new JButton(PAS.l("mainmenu_house_editor"));
	        m_btn_houseeditor.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_houseeditor.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_houseeditor.setMnemonic('e');
	        m_btn_houseeditor.setActionCommand(ENABLE);
	        m_btn_houseeditor.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));
	        //m_btn_houseeditor.setFont(PAS.f().getMenuFont());

	        m_btn_showhousedetails = new JButton(PAS.l("mainmenu_house_select"));
	        m_btn_showhousedetails.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_showhousedetails.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_showhousedetails.setMnemonic('h');
	        m_btn_showhousedetails.setActionCommand(ENABLE);
	        m_btn_showhousedetails.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));					
	        //m_btn_showhousedetails.setFont(PAS.f().getMenuFont());

	        m_btn_zoom_to_world = new JButton(PAS.l("common_pas_zoom_world"));
	        m_btn_zoom_to_world.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_zoom_to_world.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_zoom_to_world.setMnemonic('w');
	        m_btn_zoom_to_world.setActionCommand(ENABLE);
	        m_btn_zoom_to_world.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));
	        
			m_combo_mapsite = new MapSiteCombo(m_mapsites);
	        m_combo_mapsite.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));		        
	        m_combo_mapsite.setActionCommand(ENABLE);
	        
	        m_selectmenu = new MainSelectMenu(PAS.get_pas(), this);
	        m_selectmenu.init();

	}
	
	public void enableUglandPortrayal(final boolean b)
	{
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					m_combo_mapsite.setEnabled(b);					
				}
			});
		}
		catch(Exception e)
		{
			
		}
	}

	public void add_controls()
	{
		//m_gridconst.fill = GridBagConstraints.HORIZONTAL;
		PAS.pasplugin.onAddMainMenuButtons(this);
	}
	
	
	public void init()
	{
		m_btn_pan.setActionCommand("act_pan");
		m_btn_pan.addActionListener(this);
		m_btn_zoom.setActionCommand("act_zoom");
		m_btn_zoom.addActionListener(this);
		m_btn_search.setActionCommand("act_search");
		m_btn_search.addActionListener(this);
		m_btn_houseeditor.setActionCommand("act_houseeditor");
		m_btn_houseeditor.addActionListener(this);
		m_btn_showhousedetails.setActionCommand("act_houseselect");
		m_btn_showhousedetails.addActionListener(this);
		m_btn_zoom_to_world.setActionCommand("act_show_world");
		m_btn_zoom_to_world.addActionListener(PAS.get_pas().get_pasactionlistener());
		m_combo_mapsite.setActionCommand("act_mapsite");
		m_combo_mapsite.addActionListener(this);
				
		try {
			for(int i=0; i < m_selectmenu.get_bar().getMenuCount(); i++) {
				JMenu menu = m_selectmenu.get_bar().getMenu(i);
				if(menu!=null) {
					for(int j=0; j < menu.getItemCount(); j++)
					{
						try {
							if(m_selectmenu.get_bar().getMenu(i).getItem(j).getActionListeners().length == 0)
								m_selectmenu.get_bar().getMenu(i).getItem(j).addActionListener(this);
						}catch(Exception e) {
							//Error.getError().addError("MainMenu","Exception in init",e,1);
						}
					}
				}
			}
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR menu.init() " + e.getMessage(), e);
			PAS.get_pas().printStackTrace(e.getStackTrace());
			Error.getError().addError("MainMenu","Exception in init",e,1);
		}
		setVisible(true);	
		reset_buttons_foreground();
		
		try {
			//String sz_url = get_pas().get_sitename() + "java_pas/images/edit.gif";
			//URL url_cur = new URL(sz_url);
			
		} catch(Exception e_url) { 
			PAS.get_pas().add_event("Error loading cursor: " + e_url.getMessage(), e_url);
			Error.getError().addError("MainMenu","Exception in init",e_url,1);
		}
		
	}
	void set_prevaction(String sz_act) { m_sz_prev_action = sz_act; }
	void set_action(String sz_act) { m_sz_current_action = sz_act; }
	void revoke_action() { 
		if(m_sz_prev_action!=null)
			set_action(m_sz_prev_action);
	}
	
	public void set_pan() {
		set_pan("act_pan");
	}
	private void set_pan(String sz_command)
	{
		PAS.get_pas().get_mappane().set_cursor(new Cursor(Cursor.HAND_CURSOR)); //setCursor(new Cursor(Cursor.HAND_CURSOR));
		m_sz_current_action = sz_command;		
		PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_PAN_BY_DRAG);
		reset_buttons_foreground();
		//change_buttoncolor(m_btn_pan, true);
	}
	private void set_draw(String sz_command) {
		//PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
		activate_drawmode();
		reset_buttons_foreground();
	}
	private void activate_drawmode() {
		PAS.get_pas().actionPerformed(new ActionEvent(new String(""), ActionEvent.ACTION_PERFORMED, "act_activate_drawmode"));
	}
	// Byttet fra private til public
	public void reset_buttons_foreground()
	{
		change_buttoncolor(m_btn_pan, false);
		change_buttoncolor(m_btn_zoom, false);
		change_buttoncolor(m_btn_houseeditor, false);
		change_buttoncolor(m_btn_showhousedetails, false);
		switch(PAS.get_pas().get_mappane().get_mode())
		{
			case MapFrame.MAP_MODE_PAN:
			case MapFrame.MAP_MODE_PAN_BY_DRAG:
				change_buttoncolor(m_btn_pan, true);
				break;
			case MapFrame.MAP_MODE_ZOOM:
				change_buttoncolor(m_btn_zoom, true);
				break;
			case MapFrame.MAP_MODE_HOUSESELECT:
				change_buttoncolor(m_btn_showhousedetails, true);
				break;
			case MapFrame.MAP_MODE_HOUSEEDITOR_:
				change_buttoncolor(m_btn_houseeditor, true);
				break;
		}
	}
	private void set_zoom(String sz_command)
	{
		PAS.get_pas().get_mappane().set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		m_sz_current_action = sz_command;		
		PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_ZOOM);
		reset_buttons_foreground();
		//change_buttoncolor(m_btn_zoom, true);
	}
	private void set_houseeditor(String sz_command) {
		PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_HOUSEEDITOR_);
		PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_enable_houseeditor"));
		reset_buttons_foreground();
	}
	private void set_mapsite()
	{
		//m_pas.get_mappane().set_mapsite(m_combo_mapsite.getSelectedIndex());
		MapSite site = (MapSite)m_combo_mapsite.getSelectedItem();
		PAS.get_pas().get_mappane().set_mapsite(site);
		//PAS.get_pas().get_mappane().load_map();
		PAS.get_pas().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_loadmap"));
	}
	private void set_search()
	{
		//if(m_searchframe==null)
		//	m_searchframe = new SearchFrame(get_pas());
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				//get_searchframe().initUI();
				get_searchframe().activate();		
				get_searchframe().toFront();
			}
		});
	}
	private void set_statusopen()
	{
		PAS.get_pas().load_status();
	}
	
	private void export_status()
	{
		PAS.get_pas().get_statuscontroller().export_status();
	}
	
	private void toggle_viewpolygon()
	{
		PAS.get_pas().get_statuscontroller().toggle_viewpolygon(m_selectmenu.get_view_polygon());
	}
	
	private void toggle_viewstatuscodes()
	{
		PAS.get_pas().get_statuscontroller().get_statuscodeframe().setVisible(m_selectmenu.get_view_statuscodes());
	}
	
	public synchronized void toggle_houseselect(boolean b_activate, boolean b_foreign_source)
	{
		//get_pas().add_event("Houseselect activate=" + b_activate);
		if(b_activate)
		{
			PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_HOUSESELECT);
		}	
		else
		{
			if(b_foreign_source)
			{
				PAS.get_pas().get_mappane().set_prev_mode();
			}
		}
		reset_buttons_foreground();
		//change_buttoncolor(m_btn_showhousedetails, b_activate);
	}
	public synchronized void change_buttoncolor(Component button, boolean b_active)
	{
		if(b_active)
		{
			INACTIVE_COLOR = ((JButton)button).getForeground();
			((JButton)button).setHorizontalTextPosition(JButton.RIGHT);
			((JButton)button).setHorizontalAlignment(JButton.CENTER);
			
			//Substance 3.3
			((JButton)button).setForeground(SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor());
			((JButton)button).setIcon(SubstanceImageCreator.getArrowIcon(9, 9, 1, 3, SubstanceLookAndFeel.getTheme()));//getStripe(15, SubstanceLookAndFeel.getActiveColorScheme().getMidColor())));
			
			//Substance 5.2
			//((JButton)button).setForeground(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraDarkColor());
			//((JButton)button).setIcon(SubstanceImageCreator.getArrowIcon(9, 9, 1, 3, SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme()));//getStripe(15, SubstanceLookAndFeel.getActiveColorScheme().getMidColor())));
		}
		else
		{
			((JButton)button).setForeground(INACTIVE_COLOR);
			((JButton)button).setIcon(null);
		}
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		set_prevaction(m_sz_current_action);
		if ("act_pan".equals(e.getActionCommand())) { set_pan(e.getActionCommand()); }
		else if ("act_zoom".equals(e.getActionCommand())) { set_zoom(e.getActionCommand()); }
		else if ("act_mapsite".equals(e.getActionCommand())) { set_mapsite(); }
		else if ("act_houseeditor".equals(e.getActionCommand())) {
			set_houseeditor(e.getActionCommand()); 
		}
		else if("act_search".equals(e.getActionCommand())) { set_search(); }
		else if("act_statusopen".equals(e.getActionCommand())) { set_statusopen(); }
		else if("act_statusexport".equals(e.getActionCommand())) { export_status(); }
		else if("act_togglepolygon".equals(e.getActionCommand())) { toggle_viewpolygon(); }
		else if("act_view_statuscodes".equals(e.getActionCommand())) { toggle_viewstatuscodes(); }
		else if("act_houseselect".equals(e.getActionCommand())) { toggle_houseselect(true, false); }
		else if("act_print_map".equals(e.getActionCommand())) { PAS.get_pas().print_map(); }
		else if("act_save_map".equals(e.getActionCommand())) { PAS.get_pas().save_map(); }
		else if("act_toggle_showhouses".equals(e.getActionCommand())) {
			get_selectmenu().get_bar().set_show_houses();
		}
		else if("act_gps_open".equals(e.getActionCommand())) { 
			PAS.get_pas().get_gpscontroller().start_download(false);
			PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_GPS_LIST_);		
			get_selectmenu().get_bar().set_gps_autoupdate_invoke(true);
		}
		else if("act_gps_trail_minutes".equals(e.getActionCommand())) { 
			int n_value = new Integer(((CheckItem)e.getSource()).get_value().toString()).intValue();
			PAS.get_pas().get_gpscontroller().set_trail_minutes(n_value);
			PAS.get_pas().add_event("GPS trail set to " + ((CheckItem)e.getSource()).getText() + " (" + ((CheckItem)e.getSource()).get_value() + " minutes)", null);
		}
		else if("act_gps_updatemethod".equals(e.getActionCommand())) {
			CheckItem item = (CheckItem)e.getSource();
			if(item.get_value().toString().equals("manual")) {
				get_selectmenu().get_bar().get_gpsupdateseconds_checklist().enable_all(false);
				PAS.get_pas().get_gpscontroller().set_autoupdate(false);
				PAS.get_pas().add_event("GPS: Manual updates activated", null);
			} else if(item.get_value().toString().equals("auto")) {
				get_selectmenu().get_bar().get_gpsupdateseconds_checklist().enable_all(true);
				PAS.get_pas().get_gpscontroller().set_autoupdate(true);
				PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_GPS_LIST_);
				PAS.get_pas().add_event("GPS: Automatic updates activated", null);
			}
		}
		else if("act_gps_new".equals(e.getActionCommand())) {
			PAS.get_pas().get_gpscontroller().reg_mapobj(null);
		}
		else if("act_gps_updateseconds".equals(e.getActionCommand())) {
			CheckItem item = ((CheckItem)e.getSource());
			PAS.get_pas().get_gpscontroller().set_autoupdate_seconds(((Integer)item.get_value()).intValue());
			PAS.get_pas().add_event("GPS: Automatic updates set to every " + item.getText(), null);
		}
		else if("act_status_updatemethod".equals(e.getActionCommand())) {
			CheckItem item = (CheckItem)e.getSource();
			if(item.get_value().toString().equals("manual")) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						get_selectmenu().get_bar().get_statusupdateseconds_checklist().enable_all(false);
						PAS.get_pas().get_statuscontroller().set_autoupdate(false);
						PAS.get_pas().add_event("Status: Manual updates activated", null);
					}
				});
			} else if(item.get_value().toString().equals("auto")) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						get_selectmenu().get_bar().get_statusupdateseconds_checklist().enable_all(true);
						PAS.get_pas().get_statuscontroller().set_autoupdate(true);
						PAS.get_pas().add_event("Status: Automatic updates activated", null);
					}
				});
			}
		}
		else if("act_status_updateseconds".equals(e.getActionCommand())) {
			CheckItem item = ((CheckItem)e.getSource());
			PAS.get_pas().get_statuscontroller().set_autoupdate_seconds(((Integer)item.get_value()).intValue());
			PAS.get_pas().add_event("Status: Automatic updates set to every " + item.getText(), null);
		}
		else if("act_new_sending".equals(e.getActionCommand())) {
			ActionEvent action = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, e.getActionCommand());
			//m_pas.get_mappane().set_cursor(new Cursor(Cursor.CUSTOM_CURSOR));
			try {
				//PAS.get_pas().get_mappane().set_cursor(m_cursor_draw);
				PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
			} catch(Exception err) {
				PAS.get_pas().add_event("Error set_cursor() " + err.getMessage(), null);
				Error.getError().addError("MainMenu","Exception in actionPerformed",err,1);
			}
			try {
				PAS.get_pas().get_sendcontroller().actionPerformed(action);
				if(PAS.get_pas().get_parmcontroller()!=null) {
					PAS.get_pas().get_parmcontroller().clearDrawQueue();
					PAS.get_pas().get_parmcontroller().setFilled(null);
				}
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("MainMenu","Exception in actionPerformed",err,1);
			}
		}
		else if("act_new_project".equals(e.getActionCommand())) {
			PAS.get_pas().invoke_project(false);
		}
		else if("act_close_project".equals(e.getActionCommand())) {
			PAS.get_pas().close_active_project(true, true);
		}
		else if("act_draw".equals(e.getActionCommand())) {
			set_draw(e.getActionCommand());
		}
		else if("act_set_theme".equals(e.getActionCommand())) {
			//ActionEvent event = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, e.getActionCommand());
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_set_skin".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_set_watermark".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_exit_application".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_fileimport".equals(e.getActionCommand())) {
			SendObject obj = PAS.get_pas().get_sendcontroller().create_new_sending();
			new ImportPolygon(obj.get_toolbar(), "act_polygon_imported", false, PAS.get_pas());
		}
		else if("act_show_searchpinpoint".equals(e.getActionCommand())) {
			e.setSource(new Boolean(((JCheckBoxMenuItem)e.getSource()).isSelected()));
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_force_searchpinpoint".equals(e.getActionCommand())) {
			get_selectmenu().get_bar().set_searchpinpoint(((Boolean)e.getSource()).booleanValue());
		}
		else if("act_refresh_parm".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_start_parm".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_close_parm".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_show_settings".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_messagelib".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_save_settings".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_change_department".equals(e.getActionCommand())) {
			if(PAS.get_pas().get_sendcontroller().get_sendings().size() > 0) {
				Object[] options = { PAS.l("common_discard_sendings"), PAS.l("common_keep_sendings") };
	//			System.out.println(JOptionPane.showInputDialog(PAS.get_pas(), "Do you want to close current project <" + m_current_project.get_projectname() + ">", "New project", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]));
				Object input = JOptionPane.showInputDialog(PAS.get_pas(), PAS.l("mainmenu_department_onchange"), PAS.l("mainmenu_department_change"), JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if(input != null) {
					if(input.equals(PAS.l("common_keep_sendings"))) {
						PAS.get_pas().set_keep_sendings(true);
						System.out.println("m_keep_sendings=" + PAS.get_pas().get_keep_sendings());
					}
					PAS.get_pas().actionPerformed(e);
				}
				else {
					return;
					//m_selectmenu.get_bar().get_dept()
				}
			}
			else {
				if(JOptionPane.showConfirmDialog(PAS.get_pas(), PAS.l("mainmenu_department_onchange"), PAS.l("mainmenu_department_change"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
					PAS.get_pas().actionPerformed(e);
				}
			}
		}
		//navigation buttons are linked to the main menu
		else if("act_show_zoom_btn".equals(e.getActionCommand()))
		{
			m_btn_zoom.setVisible((Boolean)e.getSource());
		}
		else if("act_show_pan_btn".equals(e.getActionCommand()))
		{
			m_btn_pan.setVisible((Boolean)e.getSource());
		}
		else if("act_show_search_btn".equals(e.getActionCommand()))
		{
			m_btn_search.setVisible((Boolean)e.getSource());
		}
		else if("act_show_house_editor".equals(e.getActionCommand()))
		{
			m_btn_houseeditor.setVisible((Boolean)e.getSource());
		}
		else if("act_show_house_select".equals(e.getActionCommand()))
		{
			m_btn_showhousedetails.setVisible((Boolean)e.getSource());
		}
		else if("act_show_map_select".equals(e.getActionCommand()))
		{
			m_combo_mapsite.setVisible((Boolean)e.getSource());
		}
		else if("act_change_color_foreground".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_FOREGROUND);
		}
		else if("act_change_color_background".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_BACKGROUND);
		}
		else if("act_change_color_watermark".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_WATERMARK);
		}
		else if("act_change_color_mid".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_MID);			
		}
		else if("act_change_color_dark".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_DARK);						
		}
		else if("act_change_color_extra_light".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_EXTRA_LIGHT);						
		}
		else if("act_change_color_light".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_LIGHT);						
		}
		else if("act_change_color_ultra_dark".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_ULTRA_DARK);						
		}
		else if("act_change_color_ultra_light".equals(e.getActionCommand()))
		{
			PAS.active_theme.editColor(ThemeColorComponent.COL_ULTRA_LIGHT);						
		}
		else
		{
			PAS.get_pas().get_mappane().set_cursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		}
	} 	
	/*class MenubarListener implements MenuListener
	{
		MenubarListener()
		{
		}
		public void menuSelected(javax.swing.event.MenuEvent e)
		{
			//if("act_pan".equals(e.getActionCommand())) { set_pan("act_pan"); }
		}
		public void menuDeselected(javax.swing.event.MenuEvent e)
		{
		}
		public void menuCanceled(javax.swing.event.MenuEvent e)
		{
		}
		
	}*/

}