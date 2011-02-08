package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.ParmPanel;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.tas.TasPanel;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.theme.SubstanceTheme;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

//import org.jvnet.substance.api.SubstanceSkin;
//Substance 3.3
//Substance 5.2
//import org.jvnet.substance.api.SubstanceColorSchemeBundle;
//import org.jvnet.substance.theme.SubstanceTheme;


public class EastContent extends JPanel implements ActionListener, ComponentListener {
	public static final long serialVersionUID = 1;
	public static final int PANEL_INFO_ = 1;
	public static final int PANEL_GPS_LIST_ = 2;
	public static final int PANEL_STATUS_LIST = 3;
	public static final int PANEL_GPSEVENTS_LIST = 4;
	public static final int PANEL_SENDING_ = 5;
	public static final int PANEL_HOUSEEDITOR_ = 6;
	public static final int PANEL_PARM_ = 7;
	public static final int PANEL_TAS_ = 8;
	
	public static int CURRENT_PANEL = PANEL_INFO_;

	PAS m_pas;
	private GridBagLayout	m_gridlayout;
	private GridBagConstraints m_gridconst;
	/*private LoadingPanel m_status_loadingpanel;
	private LoadingPanel m_gps_loadingpanel;*/
	public LoadingPanel get_status_loadingpanel() { return null; }
	public LoadingPanel get_gps_loadingpanel() { return null; }
	private int m_n_panels = 0;
	private int inc_panels() { return m_n_panels++; }
	protected EastTabbedPane m_tabbedpane;
	public EastTabbedPane get_tabbedpane() { return m_tabbedpane; }
	public void setIndexZero() {
		m_tabbedpane.setSelectedIndex(0);
	}
	private InfoPanel m_infopanel;
	private SendingPanel m_sendingpanel;
	private GPSPanel m_gpspanel;
	private StatusPanel m_statuspanel = null;
	private GPSEventPanel m_gpseventpanel;
	private HouseEditorDlg m_houseeditor = null;
	private ImageIcon m_icon_close = null;
	private TasPanel m_taspanel = null;
	
	PAS get_pas() { return m_pas; }
	public StatuscodeFrame get_statuscodeframe() { return get_pas().get_statuscontroller().get_statuscodeframe(); }
	public InhabitantFrame get_inhabitantframe() { return get_pas().get_inhabitantframe(); }
	GPSFrame get_gpsframe() { return get_pas().get_gpsframe(); }
	
	public InfoPanel get_infopanel() { return m_infopanel; }
	public GPSPanel get_gpspanel() { return m_gpspanel; }
	public StatusPanel get_statuspanel() { return m_statuspanel; }
	public GPSEventPanel get_gpseventpanel() { return m_gpseventpanel; }
	public SendingPanel get_sendingpanel() { return m_sendingpanel;}
	public HouseEditorDlg get_houseeditor() { return m_houseeditor; }
	public TasPanel get_taspanel() { return m_taspanel; }
	
	
	public ParmPanel get_parm() {
		if(PAS.get_pas().get_parmcontroller()!=null)
			return PAS.get_pas().get_parmcontroller().get_parmpanel();
		else
			return null;
	}
	
	public EastContent(PAS pas)
	{
		final int n_infopanel_height = 850; // Hardkodet pga layout og at scrollbaren skal fungere 
		final int n_infopanel_width = 400;
		m_pas = pas;
		m_icon_close = new ImageIcon(ImageLoader.load_icon("no.gif").getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH));


		//SwingUtilities.invokeLater(new Runnable() {
		//	public void run()
			{
				Dimension dim_panelsize = new Dimension(100,100);//new Dimension(get_pas().get_eastwidth(), get_pas().get_mappane().getHeight());
				m_infopanel	= PAS.pasplugin.onCreateInfoPanel(); //new InfoPanel(new Dimension(n_infopanel_width, 200));
				reloadStatusPanel(false);
				m_gpspanel		= new GPSPanel(get_pas(), dim_panelsize);
				m_gpseventpanel = new GPSEventPanel(get_pas(), dim_panelsize);
				m_sendingpanel  = new SendingPanel(dim_panelsize);
				m_sendingpanel.doInit();
				m_tabbedpane = new EastTabbedPane();
				try { 
					if(m_infopanel!=null)
						m_houseeditor	= new HouseEditorDlg(m_infopanel, get_pas(), get_pas().get_pasactionlistener(), null, null); //PAS.get_pas().get_mappane().get_mouseoverhouse());
				} catch (Exception e) {
					// TODO: handle exception
				}
				m_taspanel = null;
				//this.setPreferredSize(new Dimension(get_pas().get_eastwidth()-50, 500));
				m_gridlayout = new GridBagLayout();//GridLayout(0, 2, 50, 20);
				m_gridconst  = new GridBagConstraints();
				setLayout(m_gridlayout);
				
				
				PAS.pasplugin.onAddInfoTab(m_tabbedpane, m_infopanel);
				/*m_tabbedpane.addTab(PAS.l("main_infotab_title"), null,
									m_infopanel,
									//sp,
									PAS.l("main_infotab_title_tooltip"));*/
				/*m_tabbedpane.addTab("Status", null,
									m_statuspanel,
									"Status");
				m_tabbedpane.addTab("Fleet Control", null,
									m_gpspanel,
									"Fleet Control");
				m_tabbedpane.addTab("GPS Events", null,
									m_gpseventpanel,
									"GPS Events");*/
				m_tabbedpane.setPreferredSize(new Dimension(get_pas().get_eastwidth(), get_pas().get_mappane().getHeight()-50));
				setBounds(0, 0, get_pas().get_eastwidth(), get_pas().get_mappane().getHeight());
				//m_status_loadingpanel = new LoadingPanel("Status thread idle...", new Dimension(getWidth()/2, 20));
				//m_gps_loadingpanel = new LoadingPanel("GPS thread idle...", new Dimension(getWidth()/2, 20));
				prepare_controls();
				add_controls();
			}
		//});
		//setBackground(Color.lightGray);
		this.addContainerListener(new PanelListener(get_pas(), this));
		try
		{
			if(m_houseeditor!=null)
				m_houseeditor.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
			if(m_gpspanel!=null)
				m_gpspanel.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
			if(m_gpseventpanel!=null)
				m_gpseventpanel.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
			if(get_parm()!=null)
			{
				get_parm().putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
			}
		}
		catch(Exception e)
		{
			
		}
		m_tabbedpane.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				tabChanged();
			}
		});
		addComponentListener(this);

	}
	
	protected void tabChanged()
	{
		PAS.pasplugin.onEastContentTabClicked(EastContent.this, m_tabbedpane);		
	}
	
	public void InitTAS()
	{
		try
		{
			if(m_taspanel==null)
				m_taspanel = new TasPanel(this);
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "Failed to init TAS", e, Error.SEVERITY_ERROR);
		}
	}
	public void UninitTAS()
	{
		if(m_taspanel!=null)
		{
			//m_taspanel.uninit();
			m_taspanel.uninit();
			m_taspanel = null;
		}
			
	}
	
	/*due to new statuscontroller*/
	public void reloadStatusPanel(boolean AddToTab) {
		if(m_statuspanel!=null) {
			//m_tabbedpane.remove(m_statuspanel);
			remove_tab(PANEL_STATUS_LIST);
		}		
		Dimension dim_panelsize = new Dimension(get_pas().get_eastwidth(), get_pas().get_mappane().getHeight());
		m_statuspanel	= new StatusPanel(get_pas(), dim_panelsize);
		try
		{
			//m_statuspanel.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, Boolean.TRUE);
			//m_statuspanel.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
		}
		catch(Exception e)
		{
			
		}
		/*if(AddToTab)
			m_tabbedpane.add(m_statuspanel);*/
		m_statuspanel.revalidate();
		if(AddToTab) {
			ensure_added(PANEL_STATUS_LIST);
		}
		
	}
		
	
	
	public void actionPerformed(ActionEvent e) {
		if("act_mousemove".equals(e.getActionCommand())) {
			//int n = get_tabbedpane().getSelectedIndex();
			//Component c = get_tabbedpane().getSelectedComponent();
			//if(get_tabbedpane().getComponentAt(get_tabbedpane().getSelectedIndex()).equals(get_infopanel()))
			if(get_infopanel()!=null)
			{
				if(get_tabbedpane().getSelectedComponent().equals(get_infopanel()))
					get_infopanel().actionPerformed(e);
			}
		}
		else if("act_maploaded".equals(e.getActionCommand())) {
			if(get_infopanel()!=null)
				get_infopanel().actionPerformed(e);
		}
		else if("act_download_houses_report".equals(e.getActionCommand())) {
			if(get_infopanel()!=null)
				get_infopanel().actionPerformed(e);
		}
		else if(e.getSource().equals(get_taspanel())) //show loader
		{
			try
			{
				final int n_idx = get_tabindex(PANEL_TAS_);
				if(n_idx>=0)
				{
					if("act_loading".equals(e.getActionCommand()))
					{
						SwingUtilities.invokeLater(new Runnable(){
							public void run()
							{
								m_tabbedpane.setIconAt(n_idx, no.ums.pas.ums.tools.ImageLoader.load_and_scale_icon("find_16.png", 15, 15));								
							}
						});
					}
					else if("act_loading_finished".equals(e.getActionCommand()))
					{
						SwingUtilities.invokeLater(new Runnable(){
							public void run()
							{
								m_tabbedpane.setIconAt(n_idx, no.ums.pas.ums.tools.ImageLoader.load_and_scale_icon("blank.png", 15, 15));								
							}
						});
					}
				}
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	protected class EastTabbedPane extends JTabbedPane implements ComponentListener, ChangeListener {
		public static final long serialVersionUID = 1;
		EastTabbedPane() {
			super();
			//super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			//CloseTpUI.m_icon_close = m_icon_close;
			//setUI((CloseTpUI) CloseTpUI.createUI(this));
			addComponentListener(this);
			addChangeListener(this);
		}
		public void componentResized(ComponentEvent e) {
			if(getWidth()<=0 || getHeight()<=0)
			{
				EastContent.this.componentResized(e);
				return;
			}

			revalidate();
		}
		public void componentHidden(ComponentEvent e) { }
		public void componentMoved(ComponentEvent e) { }
		public void componentShown(ComponentEvent e) {
			
			
		}
		public void stateChanged(ChangeEvent e) {
			//Report to PAS actions that status-pane is opened. If active project is opened without status, then it should be opened automatically now
			if(m_statuspanel!=null) {
				try {
					if(this.getSelectedComponent().equals(m_statuspanel))
					{
						PAS.get_pas().actionPerformed(new ActionEvent(m_statuspanel, ActionEvent.ACTION_PERFORMED, "act_eastcontent_pane_opened"));
						/*int index = find_component(m_statuspanel);
						if(index!=-1)
							get_tabbedpane().setTabComponentAt(index, m_lbl_status);*/
					}
				} catch(Exception err) {
					
				}

			}

		}
	}
	
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=1 || getHeight()<=1)
			return;
		//SwingUtilities.invokeLater(new Runnable() {
		//	public void run()
		try
		{
			resize(new Dimension(getWidth(), getHeight()));
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		//});
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) {
		
	}	
	public void resize(Dimension d) {
		if(d.height<=0 || d.width<=0)
			return;
		revalidate();
		m_tabbedpane.setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_tabbedpane.revalidate();
		//m_status_loadingpanel.setPreferredSize(new Dimension((getWidth()-2)/2, 20));
		//m_gps_loadingpanel.setPreferredSize(new Dimension((getWidth()-2)/2, 20));
		//m_status_loadingpanel.revalidate();
		//m_gps_loadingpanel.revalidate();
		
		//m_sendingpanel.getScrollPane().setPreferredSize(new Dimension(getWidth(), getHeight()));
		
		m_statuspanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_statuspanel.revalidate();
		
		m_gpspanel.revalidate();
		m_gpspanel.get_gpsframe().revalidate();
		m_gpspanel.get_gpsframe().get_gpssearchpanel().revalidate();
		m_gpspanel.get_gpsframe().get_panel().revalidate();
		
		
		// Kommentert bort pga scrollbar
		if(m_infopanel!=null)
		{
			m_infopanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
			m_infopanel.revalidate();
		}
		
		if(m_houseeditor!=null)
		{
			m_houseeditor.setPreferredSize(new Dimension(getWidth(), getHeight()));
			m_houseeditor.revalidate();
		}
		
		if(m_taspanel!=null)
		{
			m_taspanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
			m_taspanel.revalidate();
		}
		
		if(get_parm()!=null)
			get_parm().setPreferredSize(new Dimension(getWidth(), getHeight()));
		//repaint();
	}
	
	//Substance 3.3
	public void setSubstanceTheme(SubstanceTheme substance) {
		SubstanceLookAndFeel.setCurrentTheme(substance);
		//SubstanceLookAndFeel.setSkin(substance);
		updateUI();
	}
	
	//Substance 5.2
	/*public void setSubstanceTheme(SubstanceColorSchemeBundle cols) {
	}*/
	
	
	/*public void updateUI() {
		super.updateUI();
	}*/

	void prepare_controls()
	{
		get_statuscodeframe().setPreferredSize(get_statuscodeframe().get_dim());
		get_inhabitantframe().setPreferredSize(get_inhabitantframe().get_dim());
		//get_status_loadingpanel().setPreferredSize(get_status_loadingpanel().get_dimension());
		//get_gps_loadingpanel().setPreferredSize(get_gps_loadingpanel().get_dimension());
		
	}
	public void flip_to(int n_leaf) {
		ensure_added(n_leaf);
		try {
			switch(n_leaf) {
				case PANEL_INFO_:
					if(m_infopanel!=null)
						get_tabbedpane().setSelectedComponent(m_infopanel);
					break;
				case PANEL_GPS_LIST_:
					get_tabbedpane().setSelectedComponent(m_gpspanel);
					break;
				case PANEL_STATUS_LIST:
					get_tabbedpane().setSelectedComponent(m_statuspanel);
					break;
				case PANEL_GPSEVENTS_LIST:
					get_tabbedpane().setSelectedComponent(m_gpseventpanel);
					break;
				case PANEL_SENDING_:
					get_tabbedpane().setSelectedComponent(m_sendingpanel.getScrollPane());
					break;
				case PANEL_HOUSEEDITOR_:
					if(m_houseeditor!=null)
						get_tabbedpane().setSelectedComponent(m_houseeditor);
					break;
				case PANEL_PARM_:
					get_tabbedpane().setSelectedComponent(get_parm());
					//componentResized(null);
					break;
				case PANEL_TAS_:
					if(get_taspanel()!=null)
						get_tabbedpane().setSelectedComponent(get_taspanel());
					break;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("EastContent","Exception in flip_to",e,1);
		}
	}
	
	
	public synchronized void ensure_added(int n_leaf) {
		try
		{
			switch(n_leaf) {
				case PANEL_INFO_:
					if(find_component(m_infopanel)==-1)
						get_tabbedpane().addTab(PAS.l("main_infotab_title"), null, m_infopanel, PAS.l("main_infotab_title_tooltip"));
					break;
				case PANEL_STATUS_LIST:
					if(find_component(m_statuspanel)==-1)
					{
						get_tabbedpane().addTab(PAS.l("main_statustab_title"), null, m_statuspanel, PAS.l("main_statustab_title_tooltip"));
					}
					break;
				case PANEL_GPS_LIST_:
					if(find_component(m_gpspanel)==-1)
						get_tabbedpane().addTab(PAS.l("main_fleetcontroltab_title"), null, m_gpspanel, PAS.l("main_fleetcontroltab_title_tooltip"));
					ensure_added(EastContent.PANEL_GPSEVENTS_LIST);
					break;
				case PANEL_GPSEVENTS_LIST:
					if(find_component(m_gpseventpanel)==-1)
						get_tabbedpane().addTab(PAS.l("main_gpseventstab_title"), null, m_gpseventpanel, PAS.l("main_gpseventstab_title_tooltip"));
					break;
				case PANEL_SENDING_:
					if(find_component(m_sendingpanel.getScrollPane())==-1)
						get_tabbedpane().addTab(PAS.l("main_sendingtab_title"), null, m_sendingpanel.getScrollPane(), PAS.l("main_sendingtab_title_tooltip"));
					break;
				case PANEL_HOUSEEDITOR_:
					if(find_component(m_houseeditor)==-1)
						get_tabbedpane().addTab(PAS.l("main_houseeditortab_title"), null, m_houseeditor, PAS.l("main_houseeditortab_title_tooltip"));
					break;
				case PANEL_PARM_:
					if(find_component(get_parm())==-1) {
						get_tabbedpane().addTab(PAS.l("main_parmtab_title"), null, get_parm(), PAS.l("main_parmtab_title_tooltip"));
					}
					break;
				case PANEL_TAS_:
					if(get_taspanel()!=null)
					{
						if(find_component(get_taspanel())==-1) {
							get_tabbedpane().addTab(PAS.l("main_tas_title") + " (" + CountryCodes.getCountryByCCode(PAS.get_pas().get_userinfo().get_current_department().get_stdcc()) + ")", null, get_taspanel(), PAS.l("main_tas_title"));
						}
					}
			}
		}
		catch(Exception e)
		{
			
		}
		
	}
    public void updateUI() {
        //super.setUI(new CloseTpUI());
        super.updateUI();
    }
	
	

	
	
	
	protected int find_component(Component c) {
		if(c==null)
			return -1;
		Component arr [] = get_tabbedpane().getComponents();
		for(int i=0; i < arr.length; i++) {
			if(arr[i].equals(c))
				return i;
		}
		return -1;
	}
	public int get_tabindex(int ID) {
		int i = -1;
		switch(ID) {
			case PANEL_SENDING_:
				i = get_tabbedpane().indexOfComponent(m_sendingpanel.getScrollPane());
				break;
			case PANEL_TAS_:
				i = get_tabbedpane().indexOfComponent(m_taspanel);
				break;
		}
		return i;
	}
	public Component get_tab(int ID) {
		Component tab = null;
		switch(ID) {
			case PANEL_INFO_:
				tab = m_infopanel;
				break;
			case PANEL_GPS_LIST_:
				tab = m_gpspanel;
				break;
			case PANEL_STATUS_LIST:
				tab = m_statuspanel;
				break;
			case PANEL_GPSEVENTS_LIST:
				tab = m_gpseventpanel;
				break;
			case PANEL_SENDING_:
				tab = m_sendingpanel.getScrollPane();
				break;
			case PANEL_HOUSEEDITOR_:
				tab = m_houseeditor;
				break;
			case PANEL_PARM_:
				tab = get_parm();
				//componentResized(null);
				break;
			case PANEL_TAS_:
				tab = get_taspanel();
				break;
		}
		return tab;
	}
	protected void clear_panel(int ID) {
		Component panel = get_tab(ID);
		
	}
	public void remove_tab(int ID) {
		switch(ID) {
			case PANEL_PARM_:
				get_tabbedpane().remove(get_parm());
				break;
			case PANEL_SENDING_:
				get_tabbedpane().remove(get_sendingpanel().getScrollPane());
				break;
			case PANEL_STATUS_LIST:
				try
				{
					m_statuspanel.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_rem_all"));
					get_tabbedpane().remove(get_statuspanel());
				}
				catch(Exception e)
				{
					
				}
				break;
			case PANEL_TAS_:
				try
				{
					get_tabbedpane().remove(get_taspanel());
					UninitTAS();
				}
				catch(Exception e)
				{
					
				}
				break;
		}
	}
	public void set_tabtext(int ID, String sz_text) {
		//m_sendingpanel.setName(sz_text);
		//get_tabbedpane().setTitleAt(get_tabindex(ID), sz_text);
		get_tabbedpane().setTitleAt(get_tabindex(ID), sz_text);
	}
	/*
		m_tabbedpane.addTab("Info", null,
							m_infopanel,
                			"Info");
		m_tabbedpane.addTab("Status", null,
							m_statuspanel,
							"Status");
		m_tabbedpane.addTab("Fleet Control", null,
							m_gpspanel,
							"Fleet Control");
		m_tabbedpane.addTab("GPS Events", null,
							m_gpseventpanel,
							"GPS Events");*/
	
	void add_controls()
	{
		int x_width = 4;
		m_gridconst.fill = GridBagConstraints.BOTH;
		//set_gridconst(0, inc_panels(), x_width/2, 1, GridBagConstraints.NORTHWEST);
		//add(get_gps_loadingpanel());
		//m_gridlayout.setConstraints(get_gps_loadingpanel(), m_gridconst);
		//set_gridconst(x_width/2, 0, x_width/2, 1, GridBagConstraints.NORTHWEST);
		//add(get_status_loadingpanel());
		//m_gridlayout.setConstraints(get_status_loadingpanel(), m_gridconst);
		//inc_panels();
		
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
		add(get_tabbedpane(), m_gridconst);
		
		/*hide both label and lists*/
		get_pas().get_gpsframe().set_visible(false);
		get_statuscodeframe().set_visible(false);
		get_pas().get_inhabitantframe().set_visible(false);		
	}
	void set_gridconst(int n_x, int n_y, int n_width, int n_height, int n_pos)
	{
		m_gridconst.gridx = n_x;
		m_gridconst.gridy = n_y;
		m_gridconst.gridwidth = n_width;
		m_gridconst.gridheight = n_height;
		//m_gridconst.weightx = 15;
		m_gridconst.anchor = n_pos; //GridBagConstraints.NORTHWEST;
		//m_gridconst.ipadx = 20;
	}	
	void add_panel(JPanel panel)
	{
	}
	void add_component(Component comp)
	{
		set_gridconst(0,inc_panels(),1,1, GridBagConstraints.NORTHWEST);
		add(comp);
		m_gridlayout.setConstraints(comp, m_gridconst);
	}

}





class CloseTpUI extends BasicTabbedPaneUI {
	public static ImageIcon m_icon_close = null;
	CloseTpUI() {
		super();
	}
	public void update(Graphics g, JComponent c) {
		System.out.println("update");
		super.update(g, c);
	}
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
	}
	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
		System.out.println("calculateTabWidth");
		return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 20;
	}
	protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect){
		System.out.println("paintTab");
		super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
		Rectangle tabRect = rects[tabIndex]; //the black rectangle
		int selectedIndex = tabPane.getSelectedIndex();
		boolean isSelected = selectedIndex == tabIndex;
		boolean isOver = selectedIndex == tabIndex;
		int BUTTONSIZE = 16;
		int WIDTHDELTA = 4;

		int dx = tabRect.x + tabRect.width - BUTTONSIZE - WIDTHDELTA;
		int dy = (tabRect.y + tabRect.height) / 2 - 6;
		if (isOver || isSelected) {
		}
		paintCloseIcon(g, dx, dy, isOver);
	}
	protected void paintCloseIcon(Graphics g, int x, int y, boolean bIsOver) {
		System.out.println("paintCloseIcon");
		try {
			if(m_icon_close!=null)
				this.paintIcon(g, x, y, m_icon_close, new Rectangle(16,16), true);
		} catch(Exception e) {
			
		}
		//g.drawImage(m_icon_close.getImage(), x, y, null);
	}
	  public static ComponentUI createUI(JComponent c) {
	    return new CloseTpUI();
	  }
}



