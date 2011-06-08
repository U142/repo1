package no.ums.pas.core.menus;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults.AdressTblListener;
import no.ums.pas.core.mainui.address_search.AddressSearchCtrl;
import no.ums.pas.core.mainui.address_search.SearchFrame;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.themes.ThemeColorComponent;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.MapSite;
import no.ums.pas.ums.errorhandling.Error;
import org.jvnet.substance.SubstanceImageCreator;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

//Substance 3.3


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
    private static final Log log = UmsLog.getLogger(MainMenu.class);

	public static final long serialVersionUID = 1;
	protected Color INACTIVE_COLOR;
	
	public static final int BTN_SIZE_WIDTH = 120;
	public static final int BTN_SIZE_HEIGHT = 20;
	
	private JButton 	m_btn_zoom;
	private JButton 	m_btn_pan;

	private JButton 	m_btn_search;
	private JButton		m_btn_houseeditor;
	private JButton		m_btn_showhousedetails;
	private JButton		m_btn_zoom_to_world;
	private JButton		m_btn_navigate_home;
	private ButtonGroup m_group_navigation;
	private MapSiteCombo m_combo_mapsite;
	//private SearchFrame m_searchframe = null;
	private AddressSearchCtrl m_addresssearch = null;
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
	public JButton get_btn_navigate_home()
	{
		return m_btn_navigate_home;
	}
	public ButtonGroup get_btn_group_navigation()
	{
		return m_group_navigation;
	}
	public MapSiteCombo get_combo_mapsite()
	{
		return m_combo_mapsite;
	}

    public void enable_mapsite(boolean enable) { m_combo_mapsite.setVisible(enable); }
	
	public void setHouseeditorEnabled(boolean b) {
		m_btn_houseeditor.setEnabled(b);
	}

	public MainSelectMenu get_selectmenu() { return m_selectmenu; }
	
	public void setTASMode(boolean b)
	{
        NavigateActions.SEARCH.setEnabled(!b);

        m_selectmenu.get_bar().showHouseEditor(!b);
        m_selectmenu.get_bar().showHouseSelect(false);
        m_selectmenu.get_bar().showMapSelection(true);

        m_btn_zoom_to_world.setVisible(b);

        ViewOptions.TOGGLE_HOUSES.setEnabled(!b);
        ViewOptions.TOGGLE_POLYGON.setEnabled(!b);
        ViewOptions.TOGGLE_SEARCHPOINTS.setEnabled(!b);
        ViewOptions.TOGGLE_STATUSCODES.setEnabled(!b);
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

    public MainMenu(PAS pas)
	{
		super();
		//m_searchframe = new SearchFrame();
		m_addresssearch = new AddressSearchCtrl();

		//setSize(get_pas().get_mappane().get_dimension().width + get_pas().get_eastwidth(), 40);
		int w = getWidth();
		setBounds(0,0,/*get_pas().get_mappane().get_dimension().width + get_pas().get_eastwidth()*/w, 41);
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
	
	
	@Override
	public int getWantedHeight() {
		return 50;
	}
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		if(w<=0 || h<=0)
		{
			super.componentResized(e);
			return;
		}
			
		setBounds(0, 0, w, h);
		m_selectmenu.setBounds(0, 0, w, 22);//new Dimension(getWidth(), 20));
		m_selectmenu.revalidate();
		//m_selectmenu.setPreferredSize(new Dimension(getWidth(), 18));
		//m_selectmenu.get_bar().setPreferredSize(new Dimension(getWidth(), 18));
		//log.debug("Menu resized to " + getWidth() + ", " + getHeight());
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }		
	
	//public SearchFrame get_searchframe() { return m_searchframe; }
	public AddressSearchCtrl get_searchframe() { return m_addresssearch; }
	void prepare_controls()
	{
		m_group_navigation = new ButtonGroup();
	        m_btn_pan = new JButton(NavigateActions.PAN);
	        m_btn_pan.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_pan.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_pan.setMnemonic('p');
	        m_btn_pan.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));


	        m_btn_zoom = new JButton(NavigateActions.ZOOM);
	        m_btn_zoom.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_zoom.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_zoom.setMnemonic('z');
	        m_btn_zoom.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));
	        m_group_navigation.add(m_btn_pan);
	        m_group_navigation.add(m_btn_zoom);
	        
	        m_btn_navigate_home = new JButton(NavigateActions.MAP_GOTO_HOME);
	        m_btn_navigate_home.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_navigate_home.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_navigate_home.setMnemonic('z');
	        m_btn_navigate_home.setActionCommand(ENABLE);
	        m_btn_navigate_home.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));	
	        

	        m_btn_search = new JButton(NavigateActions.SEARCH);
	        m_btn_search.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_search.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_search.setMnemonic('s');
	        m_btn_search.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));
	        //m_btn_search.setFont(PAS.f().getMenuFont());

        m_btn_houseeditor = new JButton(Localization.l("mainmenu_house_editor"));
	        m_btn_houseeditor.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_houseeditor.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_houseeditor.setMnemonic('e');
	        m_btn_houseeditor.setActionCommand(ENABLE);
	        m_btn_houseeditor.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));
	        //m_btn_houseeditor.setFont(PAS.f().getMenuFont());

        m_btn_showhousedetails = new JButton(Localization.l("mainmenu_house_select"));
	        m_btn_showhousedetails.setVerticalTextPosition(AbstractButton.CENTER);
	        m_btn_showhousedetails.setHorizontalTextPosition(AbstractButton.LEFT);
	        m_btn_showhousedetails.setMnemonic('h');
	        m_btn_showhousedetails.setActionCommand(ENABLE);
	        m_btn_showhousedetails.setPreferredSize(new Dimension(BTN_SIZE_WIDTH, BTN_SIZE_HEIGHT));					
	        //m_btn_showhousedetails.setFont(PAS.f().getMenuFont());

        m_btn_zoom_to_world = new JButton(Localization.l("common_pas_zoom_world"));
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

	public void set_pan()
	{
		PAS.get_pas().get_mappane().set_cursor(new Cursor(Cursor.HAND_CURSOR)); //setCursor(new Cursor(Cursor.HAND_CURSOR));
		PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.PAN_BY_DRAG);
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
		
		// For native GUI
		m_btn_pan.setSelected(false);
		m_btn_zoom.setSelected(false);
		m_btn_houseeditor.setSelected(false);
		m_btn_showhousedetails.setSelected(false);
		
		switch(PAS.get_pas().get_mappane().get_mode())
		{
			case PAN:
				m_btn_pan.setSelected(true);
			case PAN_BY_DRAG:
				change_buttoncolor(m_btn_pan, true);
				m_btn_pan.setSelected(true);
				break;
			case ZOOM:
				change_buttoncolor(m_btn_zoom, true);
				m_btn_zoom.setSelected(true);
				break;
			case HOUSESELECT:
				change_buttoncolor(m_btn_showhousedetails, true);
				m_btn_showhousedetails.setSelected(true);
				break;
			case HOUSEEDITOR:
				change_buttoncolor(m_btn_houseeditor, true);
				m_btn_houseeditor.setSelected(true);
				break;
		}
	}
	private void set_houseeditor(String sz_command) {
		PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.HOUSEEDITOR);
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

	private void set_statusopen()
	{
		PAS.get_pas().load_status();
	}
	
	private void export_status()
	{
		PAS.get_pas().get_statuscontroller().export_status();
	}
	
	public synchronized void toggle_houseselect(boolean b_activate, boolean b_foreign_source)
	{
		//get_pas().add_event("Houseselect activate=" + b_activate);
		if(b_activate)
		{
			PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.HOUSESELECT);
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
		if ("act_mapsite".equals(e.getActionCommand())) { set_mapsite(); }
		else if ("act_houseeditor".equals(e.getActionCommand())) {
			set_houseeditor(e.getActionCommand()); 
		}
		else if("act_statusopen".equals(e.getActionCommand())) { set_statusopen(); }
		else if("act_statusexport".equals(e.getActionCommand())) { export_status(); }
		else if("act_houseselect".equals(e.getActionCommand())) { toggle_houseselect(true, false); }
		else if("act_gps_new".equals(e.getActionCommand())) {
			PAS.get_pas().get_gpscontroller().reg_mapobj(null);
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
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.SENDING_POLY);
			} catch(Exception err) {
				PAS.get_pas().add_event("Error set_cursor() " + err.getMessage(), null);
				Error.getError().addError("MainMenu","Exception in actionPerformed",err,1);
			}
			try {
				PAS.get_pas().get_sendcontroller().actionPerformed(action);
				if(PAS.get_pas().get_parmcontroller()!=null) {
					//PAS.get_pas().get_parmcontroller().clearDrawQueue();
					//PAS.get_pas().get_parmcontroller().setFilled(null);
				}
			} catch(Exception err) {
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
				Error.getError().addError("MainMenu","Exception in actionPerformed",err,1);
			}
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
		else if("act_show_searchpinpoint".equals(e.getActionCommand())) {
			e.setSource(new Boolean(((JCheckBoxMenuItem)e.getSource()).isSelected()));
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_save_settings".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_change_department".equals(e.getActionCommand())) {
			if(PAS.get_pas().get_sendcontroller().get_sendings().size() > 0) {
                Object[] options = {Localization.l("common_discard_sendings"), Localization.l("common_keep_sendings")};
	//			log.debug(JOptionPane.showInputDialog(PAS.get_pas(), "Do you want to close current project <" + m_current_project.get_projectname() + ">", "New project", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]));
                Object input = JOptionPane.showInputDialog(PAS.get_pas(), Localization.l("mainmenu_department_onchange"), Localization.l("mainmenu_department_change"), JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if(input != null) {
                    if(input.equals(Localization.l("common_keep_sendings"))) {
						PAS.get_pas().set_keep_sendings(true);
						log.debug("m_keep_sendings=" + PAS.get_pas().get_keep_sendings());
					}
					PAS.get_pas().actionPerformed(e);
				}
				else {
					return;
					//m_selectmenu.get_bar().get_dept()
				}
			}
			else {
                if(JOptionPane.showConfirmDialog(PAS.get_pas(), Localization.l("mainmenu_department_onchange"), Localization.l("mainmenu_department_change"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
					PAS.get_pas().actionPerformed(e);
				}
			}
			//get_selectmenu().get_bar().m_item_departments_checklist.set_default(new CheckItem(PAS.get_pas().get_userinfo().get_current_department().toString(), 
			//PAS.get_pas().get_userinfo().get_current_department(),true));
			//get_selectmenu().get_bar().updateDeptSelection(true);
			get_selectmenu().get_bar().setDepartment(Variables.getUserInfo().get_current_department());
			//System.out.print(get_selectmenu().get_bar().m_item_departments_checklist.toString());
			
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
		else if("act_trainingmode".equals(e.getActionCommand()))
		{
			AbstractButton a = (AbstractButton)e.getSource();
			boolean b = a.getModel().isSelected();
			PAS.TRAINING_MODE = b;
			PAS.pasplugin.onTrainingMode(b);
		}
		else if("act_address_book".equals(e.getActionCommand()))
		{
			PAS.pasplugin.onOpenAddressBook();
		}
		else
		{
			PAS.get_pas().get_mappane().set_cursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		}
	}

    public void clickMapMode(MapFrame.MapMode mapmode, boolean b) {
        switch (mapmode) {
            case PAN:
            case PAN_BY_DRAG:
                m_btn_pan.setSelected(b);
                m_btn_zoom.setSelected(!b);
                break;
            case ZOOM:
                m_btn_zoom.setSelected(b);
                m_btn_pan.setSelected(!b);
                break;
        }
    }

}