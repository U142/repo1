package no.ums.pas.parm.alert;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.ParmController;
import no.ums.pas.core.Variables;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.core.ws.WSSendSettings;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.send.SendPropertiesEllipse;
import no.ums.pas.send.SendPropertiesGIS;
import no.ums.pas.send.SendPropertiesPolygon;
import no.ums.pas.send.sendpanels.SendWindow;
import no.ums.pas.send.sendpanels.Sending_Cell_Broadcast_text;
import no.ums.pas.send.sendpanels.Sending_SMS_Broadcast_text;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Utils;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;



public class AlertWindow extends SendWindow implements ActionListener, ChangeListener, WindowListener {
	public static final long serialVersionUID = 1;
    private static final Log log = UmsLog.getLogger(AlertWindow.class);
	//private String m_sz_sendingid;
	//public String get_sendingid() { return m_sz_sendingid; }
	private AlertController m_alert;
	private Sending_Settings_Parm_Alert m_alert_settings;
	private Sending_Cell_Broadcast_text m_cell_broadcast_text_panel;
	public Sending_Cell_Broadcast_text get_cell_broadcast_text() { return m_cell_broadcast_text_panel; }
	public Sending_Settings_Parm_Alert get_alert_settings() { return this.m_alert_settings; }
	private Sending_Send_Parm_Alert m_alert_send;
	public Sending_Send_Parm_Alert get_alert_send() { return this.m_alert_send; }
	private boolean edit;
	private ActionEvent map_panel_event;
	
	public int get_sendingid() {
		return get_sendobject().get_toolbar().get_sendingid();
	}
	
	public void createAlert() {
		EventVO event = null;
		ParmController parmctrl = PAS.get_pas().get_parmcontroller();
		Object object = parmctrl.getObjectFromTree();
		if (parmctrl.checkObject(object, EventVO.class)) {
			event = (EventVO) object;
		}
		try {
			m_alert.createNewAlert(PAS.get_pas().get_parmcontroller().getHighestTemp(),event,new PolygonStruct(parmctrl.getMapSize()));
			m_alert.getPanelToolbar().set_addresstypes((int)Variables.getSettings().getN_newsending_autochannel());
			m_alert.getPanelToolbar().populateABASPanelData((int)Variables.getSettings().getAddressTypes(),false);
            // Please don't use the code below as an example off how things should be done.
            // Prefer initing a model with the desired value instead, that the UI should listen to.
            switch (Variables.getSettings().getN_autoselect_shapetype()) {
                case SendOptionToolbar.BTN_SENDINGTYPE_ELLIPSE_:
                    m_alert.getPanelToolbar().get_radio_ellipse().doClick();
                    break;
                case SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_:
                    m_alert.getPanelToolbar().get_radio_municipal().doClick();
                    break;
                case SendOptionToolbar.BTN_SENDINGTYPE_POLYGONAL_ELLIPSE_:
                    m_alert.getPanelToolbar().get_radio_polygonal_ellipse().doClick();
                    break;
                case SendOptionToolbar.BTN_SENDINGTYPE_POLYGON_:
                    m_alert.getPanelToolbar().get_radio_polygon().doClick();
                    break;
                case SendOptionToolbar.BTN_OPEN_:
                    m_alert.getPanelToolbar().get_btn_open().doClick();
            }

			m_alert.getPanelToolbar().initSelections();
		} catch(Exception e){
            Error.getError().addError(Localization.l("common_error"),"Error while creating alert",e,1);
		}
	}
	public void editAlert(AlertController ac) {
		try {
			ac.editAlert(ac.getAlert(),PAS.get_pas().get_parmcontroller(),(DefaultMutableTreeNode)((DefaultMutableTreeNode)PAS.get_pas().get_parmcontroller().getTreeCtrl().getSelPath().getLastPathComponent()).getParent());
		}catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"),"Error in edit alert",e,Error.SEVERITY_ERROR);
		}
	}
	
	public boolean load_background() {
		try {
			//imgbg = ImageLoader.load_icon("preparescenario.png").getImage();
			//imgbg = ImageLoader.makeTransparent(imgbg, 0.55f);
			return true;
		} catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"), "Could not load scenario background image", e, Error.SEVERITY_WARNING);
			return false;
		}
	}
	
	public AlertWindow(SendObject obj, AlertController ac) {
		super(PAS.get_pas().get_sendcontroller());
		this.setIconImage(PAS.get_pas().getIconImage());

        try {
            this.setAlwaysOnTop(true);
        } catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"),"Exception in SendWindow",e,1);
        }

		edit = false;
		m_sendobject = obj;
		load_background();
		//register window with toolbar

				
		setLayout(new BorderLayout());
		int n_width = 770, n_height = 620;//height changed after adding predefined area combo box
		Dimension d = Utils.screendlg_upperleft(n_width, n_height);
		
		setBounds(d.width, d.height, n_width, n_height);
		m_loader = new LoadingPanel("", new Dimension(100, 20), false);
		reset_comstatus();
		
		if(ac == null){
            this.setTitle(Localization.l("main_parm_alert_dlg_new"));
			m_alert = new AlertController(PAS.get_pas().get_parmcontroller(), Variables.getNavigation(), this);
			createAlert();
		} else {
            this.setTitle(Localization.l("main_parm_alert_dlg_edit"));
			m_alert = ac;
			editAlert(m_alert);
			edit = true;
		}
		
		m_alert_settings = new Sending_Settings_Parm_Alert(this);
		m_alert_send = new Sending_Send_Parm_Alert(this);
		m_cell_broadcast_text_panel = new Sending_Cell_Broadcast_text(PAS.get_pas(),this);
		m_sms_broadcast_text_panel = new Sending_SMS_Broadcast_text(PAS.get_pas(), this);
		m_tabbedpane = new JTabbedPane();
		m_tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        m_tabbedpane.addTab(Localization.l("main_parm_alert_dlg_area_and_recipients"), null,
							m_alert.getGui(),
                Localization.l("main_parm_alert_dlg_area_and_recipients_tooltip"));
        m_tabbedpane.addTab(Localization.l("main_sending_settings"), null,
				m_alert_settings,
                Localization.l("main_sending_settings_tooltip"));
		
		m_alert.getPanelToolbar().setReportAddressChanges(this);

        m_tabbedpane.addTab(Localization.l("main_sending_finalize_heading"), null,
							m_alert_send,
                Localization.l("main_sending_finalize_heading_tooltip"));

        m_btn_next = new JButton(Localization.l("common_wizard_next"));
        m_btn_back = new JButton(Localization.l("common_wizard_back"));
        m_btn_simulation = new JButton(Localization.l("main_sending_simulate"));
		m_btn_next.setPreferredSize(new Dimension(100, 20));
		m_btn_back.setPreferredSize(new Dimension(100, 20));
		m_btn_next.setActionCommand("act_next");
		m_btn_back.setActionCommand("act_back");
		m_btn_simulation.setPreferredSize(new Dimension(100, 20));
		m_btn_simulation.setActionCommand("act_send_simulation");
		m_btn_simulation.setVisible(false);
		m_btn_next.addActionListener(this);
		m_btn_back.addActionListener(this);
		m_btn_simulation.addActionListener(this);
		m_txt_comstatus.setPreferredSize(new Dimension(200, 16));
		add_controls();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// Dette er for å aktivere cell broadcast tab'en
			if(m_alert.getAlert() != null && m_alert.getAlert().getArea() != null) {
				if((m_alert.getAlert().getAddresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT) {
//					m_alert.getPanelToolbar().get_cell_broadcast_text().setSelected(true);
//					actionPerformed(new ActionEvent(m_alert.getPanelToolbar().get_cell_broadcast_text(), ActionEvent.ACTION_PERFORMED, "act_button_pressed"));
					m_alert.getPanelToolbar().getChkLocationBased().setSelected(true);
					actionPerformed(new ActionEvent(m_alert.getPanelToolbar().getChkLocationBased(), ActionEvent.ACTION_PERFORMED, "act_button_pressed"));
				}
			}
			if(m_alert.getAlert()!=null) {
				if((m_alert.getAlert().getAddresstypes() & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE) {
					m_alert.getPanelToolbar().get_cell_broadcast_voice().setSelected(true);
					actionPerformed(new ActionEvent(m_alert.getPanelToolbar().get_cell_broadcast_voice(), ActionEvent.ACTION_PERFORMED, "act_button_pressed"));
				}
			}
		actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
		m_alert.setParent(this);
		PAS.get_pas().get_mappane().addActionListener(this);
		m_alert.getPanelToolbar().get_radio_ellipse().addActionListener(this);
		m_alert.getPanelToolbar().get_radio_polygon().addActionListener(this);
		super.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(n_width, n_height));
		if(m_alert.getAlert() != null && (m_alert.getAlert().getLocked() == 1 || !PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_rights_management().write_parm())) {
			m_alert.enableInput(false);
			m_alert_settings.enableInput(false);
			m_cell_broadcast_text_panel.enableInput(false);
			m_sms_broadcast_text_panel.enableInput(false);
			m_alert_send.enableInput(false);
		}
		m_tabbedpane.addChangeListener(this);
		addWindowListener(this);

	}
	protected void add_controls() {
		init();
	}
	/*public void set_comstatus(String sz_text) {
		m_txt_comstatus.setText(PAS.l("main_sending_settings_dl_status") + sz_text);
	}
	public void reset_comstatus() {
		set_comstatus("Idle...");
	}
	public void start_download_settings() {
		set_comstatus("Downloading settings...");
		get_loader().set_totalitems(0, "Loading settings...");
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_settings_startload");
		actionPerformed(e);
	}*/
	public void close() {
		setVisible(false);
	}
	protected void init() {
		//SoundRecorderPanel rec = new SoundRecorderPanel(get_sendcontroller());
		//add(rec, BorderLayout.CENTER);
		add(m_tabbedpane, BorderLayout.CENTER);
		m_btnpane.add(get_loader());
		m_btnpane.add_button(m_txt_comstatus);
		m_btnpane.add_button(m_btn_back);
		m_btnpane.add_button(m_btn_next);
		m_btnpane.add_button(m_btn_simulation);
		add(m_btnpane, BorderLayout.SOUTH);
		setVisible(true);
		set_next_text();
		start_download_settings();
		//ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_adrcount");
		//actionPerformed(e);
		//this.pack();
	}
	public void remove_old_filepanes() {
		if(m_files==null)
			return;
		for(int i=0; i < m_files.length; i++) {
			m_tabbedpane.remove(m_tabbedpane.indexOfComponent(m_files[i]));
		}
	}
	
	public void set_next_text() {
//		System.out.println("inside AlertWindow set_next_text called...");
		if(m_tabbedpane.getSelectedIndex() == m_tabbedpane.getTabCount()-1) {
            m_btn_next.setText(Localization.l("common_save"));
			
			if(PAS.get_pas().get_rightsmanagement().write_parm())
				m_btn_next.setEnabled(true);
			else
				m_btn_next.setEnabled(false);
			
			m_btn_next.setActionCommand("act_save");
			m_btn_simulation.setVisible(false);
			if(m_alert.getAlert() != null && m_alert.getAlert().getLocked() == 1)
				m_btn_next.setEnabled(false);
		} else {
            m_btn_next.setText(Localization.l("common_wizard_next"));
			m_btn_next.setActionCommand("act_next");
			m_btn_next.setEnabled(true);
			m_btn_simulation.setVisible(false);
		}
		if(m_tabbedpane.getSelectedIndex() == 0) {
			m_btn_back.setEnabled(false);
		}
		else {
			m_btn_back.setEnabled(true);
		}
	}
	
	public boolean has_shape() { // Has either gis, polygon or ellipse shape and can continue
		boolean shape = true;
		//if(PAS.get_pas().get_mappane().get_active_shape().getType() == ShapeStruct.SHAPE_ELLIPSE)
		//if(m_alert.get_m_edit_shape().getType()==ShapeStruct.SHAPE_ELLIPSE)
		//if(m_alert.getSendObj().get_sendproperties().get_sendingtype()==ShapeStruct.SHAPE_ELLIPSE)
		if(m_alert.getSendObj().get_sendproperties().getClass().equals(SendPropertiesEllipse.class))
		{
			try
			{
				if(PAS.get_pas().get_mappane().get_active_shape().typecast_ellipse().get_center() == null) {
					shape = false;
				}
			}
			catch(Exception e)
			{
			
			}
		}
		//else if(PAS.get_pas().get_mappane().get_active_shape().getType() == ShapeStruct.SHAPE_POLYGON) 
		//else if(m_alert.get_m_edit_shape().getType()==ShapeStruct.SHAPE_POLYGON)
		//else if(m_alert.getSendObj().get_sendproperties().get_sendingtype()==ShapeStruct.SHAPE_POLYGON)
		else if(m_alert.getSendObj().get_sendproperties().getClass().equals(SendPropertiesPolygon.class))
		{
			try
			{
				if(PAS.get_pas().get_mappane().get_active_shape().typecast_polygon().get_size() < 3) {
					shape = false;
				}
			}
			catch(Exception e)
			{
			
			}
		}
		//else if(PAS.get_pas().get_mappane().get_active_shape().getType() == ShapeStruct.SHAPE_GISIMPORT)
		//else if(m_alert.get_m_edit_shape().getType()==ShapeStruct.SHAPE_GISIMPORT)
		//else if(m_alert.getSendObj().get_sendproperties().get_sendingtype()==ShapeStruct.SHAPE_GISIMPORT)
		else if(m_alert.getSendObj().get_sendproperties().getClass().equals(SendPropertiesGIS.class))
		{
			if(m_alert.getSendObj().get_sendproperties().typecast_gis().get_gislist().size() > 0)
				shape = true;
			else
				shape = false;
			ShapeStruct ss = PAS.get_pas().get_mappane().get_active_shape();
			//JOptionPane.showMessageDialog(this, "GIS");
		}
		if(!shape){
			disableNext();
		}
		
		return shape;
	}
	
	private void disableNext() {
//		System.out.println("inside AlertWindow disableNext called..");
		if(m_tabbedpane!=null && m_alert!=null)
		{
			for(int i=m_tabbedpane.indexOfComponent(m_alert.getGui())+1;i<m_tabbedpane.getTabCount();i++)
				m_tabbedpane.setEnabledAt(i, false);
			
			if(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()).equals(m_alert.getGui()))
				m_btn_next.setEnabled(false);
			else
				m_btn_next.setEnabled(true);
		}
		//m_btn_next.setEnabled(true);
	}
	
	
	
	private JFrame get_frame() {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
		p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
		frame.setLocation(p);
		frame.setVisible(true);
		//frame.setAlwaysOnTop(true);
		return frame;
	}
	
	/**
	 * 
	 * @return if voice is disabled, return true. if voice is enabled, if soundfiles in profile<=0 return true
	 */
	private boolean can_external_exec() {
		if(m_alert.getAlert()==null)
			return false;
		boolean hasVoice = hasVoice(m_alert.getPanelToolbar().get_addresstypes());
		boolean soundfiles = (m_alert_settings.get_current_profile().get_soundfiles().size()<=0);
		return (hasVoice && soundfiles) || !hasVoice;
		//return (!hasVoice(m_alert.getPanelToolbar().get_addresstypes()) || m_alert_settings.get_current_profile().get_soundfiles().size()>1);
		
	}
	
	private void verify_external_exec() {
		/*if(m_alert_settings.get_current_profile().get_soundfiles().size() > 0){
			m_alert_send.get_chk_execute_remote().setSelected(false);
			m_alert_send.get_chk_execute_remote().setEnabled(false);
		}
		else
			if(m_alert.getAlert() != null && m_alert.getAlert().getLocked() == 0)
				m_alert_send.get_chk_execute_remote().setEnabled(true);*/
		if(m_alert!=null && m_alert.getAlert() != null) {
			m_alert_send.get_chk_execute_remote().setSelected(m_alert.getAlert().getLocked()>0);
			boolean b = can_external_exec() && (m_alert.getAlert().getLocked() == 0);
			m_alert_send.get_chk_execute_remote().setEnabled(b);
		}
		else if(m_alert_send!=null)
			m_alert_send.get_chk_execute_remote().setEnabled(true);

		//to permanantly disable remote execute checkbox
		m_alert_send.get_chk_execute_remote().setEnabled(false);
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
//		System.out.println("inside AlertWindow actionPerformed called..ActionCommand="+e.getActionCommand());
		if("act_next".equals(e.getActionCommand())) {
			boolean proceed = true;
			
			if(m_tabbedpane.getSelectedComponent().equals(m_cell_broadcast_text_panel) && !m_cell_broadcast_text_panel.defaultLanguage()) {
				showSpecifyLanguage();
				return;
			}
			
			if(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()).equals(m_alert.getGui())) {
				proceed = has_shape();
			}
			
			if(proceed && m_tabbedpane.getSelectedIndex() < m_tabbedpane.getTabCount()-1) {
				if(!m_tabbedpane.isEnabledAt(m_tabbedpane.getSelectedIndex()+1))
					m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex()+1, true);
				
				m_tabbedpane.setSelectedComponent(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()+1));
				
			}
			
			if(!proceed) {
				JFrame frame = get_frame();
                JOptionPane.showMessageDialog(this, Localization.l("main_parm_alert_dlg_paint_or_open"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				frame.dispose();
			}
			//JOptionPane.showMessageDialog(this, "rols: " + m_tabbedpane.getSelectedComponent().toString() + m_alert.getGui().toString());
			
			//set_next_text();
		} else if("act_back".equals(e.getActionCommand())) {
			if(m_tabbedpane.getSelectedIndex() > 0) {
				m_tabbedpane.setSelectedComponent(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()-1));
				if(m_tabbedpane.getSelectedIndex() == 0)
					m_btn_next.setEnabled(true);
			}
			//set_next_text();
		} else if("act_adrcount".equals(e.getActionCommand())) {
			m_btn_next.setEnabled(false);
            m_loader.start_progress(0, Localization.l("main_sending_address_count"));
			m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_alert_settings), false);		
		} else if("act_settings_startload".equals(e.getActionCommand())) {
			m_btn_next.setEnabled(false);
			//m_xmlsendsettings = new XMLSendSettings(Thread.NORM_PRIORITY, PAS.get_pas(), "PAS_getsendoptions.asp?l_comppk=" + PAS.get_pas().get_userinfo().get_comppk() + "&l_deptpk=" + PAS.get_pas().get_userinfo().get_current_department().get_deptpk(), m_alert_settings, PAS.get_pas().get_httpreq(), get_loader());
			//m_xmlsendsettings.start();	//connection will report to Sending_settings when done. Sending_settings will report back here
			m_xmlsendsettings = new WSSendSettings(m_alert_settings);
			m_xmlsendsettings.start();
		} else if("act_settings_loaded".equals(e.getActionCommand())) {
			get_loader().get_progress().setIndeterminate(false);
            get_loader().set_starttext(Localization.l("common_finished"));
			m_btn_next.setEnabled(true);
			m_btn_simulation.setEnabled(false);
//			this.disableNext();
			//m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_alert_settings), true);
			if(edit)
				set_alert_values();
			
			verify_external_exec();
			//m_alert_send.get_chk_execute_remote().setEnabled(hasVoice(m_alert.getPanelToolbar().get_addresstypes()) && m_alert_settings.get_current_profile().get_soundfiles().size()>1);
			//if(m_alert_settings.get_current_profile().get_soundfiles().size()>1)
			//	m_alert_send.get_chk_execute_remote().setEnabled(true);
			//else
			//	m_alert_send.get_chk_execute_remote().setEnabled(false);
				
		} else if("act_settings_changed".equals(e.getActionCommand())) {
			verify_external_exec();
			//m_alert_send.get_chk_execute_remote().setEnabled(hasVoice(m_alert.getPanelToolbar().get_addresstypes()) && m_alert_settings.get_current_profile().get_soundfiles().size()>1);
			//if(m_alert_settings.get_current_profile().get_soundfiles().size()>1)
			//	m_alert_send.get_chk_execute_remote().setEnabled(true);
			//else
			//	m_alert_send.get_chk_execute_remote().setEnabled(true);
		} else if("act_send".equals(e.getActionCommand())) {
			get_sendobject().get_sendproperties().set_simulation(0);
			if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Confirm live sending to " + get_addresscount().get_total() + " recipients", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				m_send.actionPerformed(e);
			}
		} else if("act_save".equals(e.getActionCommand())) {
			boolean ready = true;
			
			if((hasBlocklist(m_alert.getPanelToolbar().get_addresstypes()) && m_alert.getPanelToolbar().get_addresstypes()-SendController.SENDTO_USE_NOFAX_COMPANY < 1)||
					(!hasBlocklist(m_alert.getPanelToolbar().get_addresstypes()) && m_alert.getPanelToolbar().get_addresstypes() < 1) ||
					(!hasRecipientTypeAndChannel(m_alert.getPanelToolbar().get_addresstypes()) && isABAS(m_alert.getPanelToolbar().get_addresstypes()))
					) {
				JFrame frame = get_frame();
				if(isABAS(m_alert.getPanelToolbar().get_addresstypes()))
					JOptionPane.showMessageDialog(this, Localization.l("main_parm_alert_dlg_specify_recipient_type_abas"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				else
					JOptionPane.showMessageDialog(this, Localization.l("main_parm_alert_dlg_specify_recipient_type"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				frame.dispose();
				m_tabbedpane.setSelectedComponent(m_alert.getGui());
				ready = false;
			}
			if(ready && this.hasSMS(m_alert.getPanelToolbar().get_addresstypes())) {
				if(m_sms_broadcast_text_panel.get_txt_messagetext().getText().length() < 1) {
					ready = false;
					// Please enter a message
					JFrame frame = get_frame();
                    JOptionPane.showMessageDialog(frame, Localization.l("main_sending_warning_empty_sms"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					m_tabbedpane.setSelectedComponent(m_sms_broadcast_text_panel);
				}	
			}
			if(ready && get_alert_settings().get_sending_name().getText().length() < 1) {
				JFrame frame = get_frame();
                JOptionPane.showMessageDialog(this, Localization.l("main_sending_sendingname_mandatory"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				frame.dispose();
				m_tabbedpane.setSelectedComponent(m_alert_settings);
				ready = false;
			}
//			if(ready && m_alert.getPanelToolbar().get_cell_broadcast_text().isSelected() && m_cell_broadcast_text_panel.validateFields() != null) {
			if(ready && m_alert.getPanelToolbar().getChkLocationBased().isSelected() && m_cell_broadcast_text_panel.validateFields() != null) {
				JFrame frame = get_frame();
				JOptionPane.showMessageDialog(frame,m_cell_broadcast_text_panel.validateFields());
				frame.dispose();
				m_tabbedpane.setSelectedComponent(m_cell_broadcast_text_panel);
				ready = false;
			}
			if(ready && m_alert.getParent().hasSMS(m_alert.getPanelToolbar().get_addresstypes())) {
				if(m_alert.getParent().m_sms_broadcast_text_panel.get_txt_messagetext().getText().length() < 1) {
					ready = false;
					JFrame frame = get_frame();
                    JOptionPane.showMessageDialog(frame, Localization.l("main_sending_warning_empty_sms"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					m_alert.getParent().m_tabbedpane.setSelectedComponent(m_alert.getParent().m_sms_broadcast_text_panel);
				}
				else if (m_alert.getParent().m_sms_broadcast_text_panel.get_txt_oadc_text().getText().length() < 1) {
					ready = false;
					JFrame frame = get_frame();
                    JOptionPane.showMessageDialog(frame, Localization.l("main_sending_warning_empty_sms_oadc"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					m_alert.getParent().m_tabbedpane.setSelectedComponent(m_alert.getParent().m_sms_broadcast_text_panel);
				}
			}
			if(ready) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)PAS.get_pas().get_parmcontroller().getTreeCtrl().getSelPath().getLastPathComponent();
				
				if((m_alert.getAlert()==null && !node.getUserObject().getClass().equals(EventVO.class))) { 
					// I have to check that m_alert is the one selected
					TreePath tp = PAS.get_pas().get_parmcontroller().getTreeCtrl().find(PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree(),
							(ParmVO)m_alert.getAlertParent());
					if(tp != null) {
						PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree().setSelectionPath(tp);
					}
					else {
						JFrame frame = get_frame();
						JOptionPane.showMessageDialog(frame,new String("Parent is not an Event, please make sure that the selected item in the tree structure is an Event"),"Parent is not an Event", JOptionPane.ERROR_MESSAGE);
						frame.dispose();
						ready = false;
					}
				} else if(m_alert.getAlert()!=null && !node.getUserObject().equals(m_alert.getAlert())){
					TreePath tp = PAS.get_pas().get_parmcontroller().getTreeCtrl().find(PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree(),
							(ParmVO)m_alert.getAlertParent());
					if(tp != null) {
						PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree().setSelectionPath(tp);
					}
					else {
						JFrame frame = get_frame();
						JOptionPane.showMessageDialog(frame,new String("Parent is not an Event, please make sure that the selected item in the tree structure is an Event"),"Parent is not an Event", JOptionPane.ERROR_MESSAGE);
						frame.dispose();
						ready = false;
					}
				}
				if(ready && m_alert.getAlert()!=null) {
					if(!node.getUserObject().equals(m_alert.getAlert())) {
						TreePath tp = PAS.get_pas().get_parmcontroller().getTreeCtrl().find(PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree(),
								(ParmVO)m_alert.getAlertParent());
						
						if(tp != null) {
							PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree().setSelectionPath(tp);
						}
						else {
							JFrame frame = get_frame();
							JOptionPane.showMessageDialog(frame,new String("The selected parent is different from your original selection, please make sure that the selected item in the tree structure is correct"),"Parent differs from original", JOptionPane.ERROR_MESSAGE);
							frame.dispose();
							ready = false;
						}
					}
				}else if(ready && !((ParmVO)node.getUserObject()).getPk().equals(((ParmVO)m_alert.getAlertParent()).getPk())) {
					// Finn noden i treet
					TreePath tp = PAS.get_pas().get_parmcontroller().getTreeCtrl().find(PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree(),
							(ParmVO)m_alert.getAlertParent());
					if(tp != null) {
						PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().getTree().setSelectionPath(tp);
					}
					else {
						JFrame frame = get_frame();
						JOptionPane.showMessageDialog(frame,new String("The selected parent is different from your original selection, please make sure that the selected item in the tree structure is correct"),"Parent differs from original", JOptionPane.ERROR_MESSAGE);
						frame.dispose();
						ready = false;
					}
				}
				if(ready) {
					//ParmVO
					AlertVO alert = m_alert.storeAlert(this);
					if(PAS.g_n_parmversion < 2)
						PAS.get_pas().get_parmcontroller().addToObjectList(alert);
					PAS.get_pas().get_parmcontroller().actionPerformed(new ActionEvent(m_alert,ActionEvent.ACTION_PERFORMED,"act_store_alert"));
					this.dispose();
				}
				//else {
					//JOptionPane.showMessageDialog(this,new String("Parent is not an Event, please make sure that the selected item in the tree structure is an Event"),"Parent is not an Event", JOptionPane.ERROR_MESSAGE);
					//JOptionPane.showMessageDialog(this,new String("Parent differs from original selection, please select or start"),"Parent is not an Event", JOptionPane.ERROR_MESSAGE);
				//}
			}
		} else if("act_schedprofile_changed".equals(e.getActionCommand()) || "act_profile_changed".equals(e.getActionCommand())) {
			/*if(m_alert_settings.get_current_profile().get_soundfiles().size()>1)
				m_alert_send.get_chk_execute_remote().setEnabled(false);
			else
				m_alert_send.get_chk_execute_remote().setEnabled(true);*/
			verify_external_exec();
		} else if("act_set_addresstypes".equals(e.getActionCommand())) { //callback from toolbar
			if(m_alert==null)
				return;
			int tmp = m_alert.getPanelToolbar().get_addresstypes();
//			System.out.println("inside AlertWindow m_alert.getPanelToolbar().get_addresstypes()="+tmp);
			if(hasSMS(tmp))
			{
                m_tabbedpane.insertTab(Localization.l("main_sending_sms_heading"), null,
						m_sms_broadcast_text_panel,
                        Localization.l("main_sending_sms_heading_tooltip"),
						m_tabbedpane.getTabCount()-1); //m_tabbedpane.indexOfComponent(m_tabbedpane.getTabComponentAt(m_tabbedpane.getTabCount()-1)));//m_alert.getGui())+1);

				for(int i=m_tabbedpane.indexOfComponent(m_sms_broadcast_text_panel);i<m_tabbedpane.getTabCount();i++) {
					m_tabbedpane.setEnabledAt(i, false);
					log.debug("sms skal være disabled");
				}
				
			}
			else
			{
				if(m_tabbedpane.indexOfComponent(m_sms_broadcast_text_panel) >= 0)
					m_tabbedpane.removeTabAt(m_tabbedpane.indexOfComponent(m_sms_broadcast_text_panel));
			}

			if((m_alert.getPanelToolbar().get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT ||
					(m_alert.getPanelToolbar().get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE) {

                m_tabbedpane.insertTab(Localization.l("main_status_locationbased_alert"), null,
						m_cell_broadcast_text_panel,
                        Localization.l("main_sending_lba_tooltip"),
						m_tabbedpane.getTabCount()-1);
						//m_tabbedpane.indexOfComponent(m_alert.getGui())+1);

				for(int i=m_tabbedpane.indexOfComponent(m_cell_broadcast_text_panel)+1;i<m_tabbedpane.getTabCount();i++)
					m_tabbedpane.setEnabledAt(i, false);
				
				has_shape();
				m_cell_broadcast_text_panel.setSendingType(m_alert.getPanelToolbar().get_addresstypes());
				if(m_alert.getAlert() != null && m_alert.getAlert().getArea() != null) {
					m_cell_broadcast_text_panel.setMessages(m_alert.getAlert().getCBMessages());
					m_cell_broadcast_text_panel.get_cbx_messages().setSelectedIndex(-1);
					m_cell_broadcast_text_panel.get_combo_area().setSelectedItem(m_alert.getAlert().getArea());
					if(m_alert.getAlert().getCBOadc() != null && m_alert.getAlert().getCBOadc().length()>0)
						m_cell_broadcast_text_panel.get_txt_oadc_text().setText(m_alert.getAlert().getCBOadc());
				}
				// Hvis den ikke har voice, så fjern alle unødvendige ting i settings
				int n_adrtypes = m_alert.getPanelToolbar().get_addresstypes();
				/*if(((n_adrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
							(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 ||
							(n_adrtypes & SendController.SENDTO_FIXED_COMPANY) > 0 ||
							(n_adrtypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 ||
							(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE) > 0 ||
							(n_adrtypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0 ||
							(n_adrtypes & SendController.SENDTO_MOBILE_COMPANY) > 0 ||
							(n_adrtypes & SendController.SENDTO_MOBILE_PRIVATE) > 0 ||
							(n_adrtypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0 ||
							(n_adrtypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0 ||
							(n_adrtypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0 ||
							(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0))*/
				if(hasVoice(n_adrtypes))
					m_alert_settings.toggleVoiceSettings(true);
				else
					m_alert_settings.toggleVoiceSettings(false);
			} else {
				m_tabbedpane.remove(m_cell_broadcast_text_panel);
				m_alert_settings.toggleVoiceSettings(true);
			}
		}
		if("act_add_polypoint".equals(e.getActionCommand()) || "act_rem_polypoint".equals(e.getActionCommand()))
		{
			try
			{
				PolygonStruct poly = PAS.get_pas().get_mappane().get_active_shape().typecast_polygon();
				if(poly.get_size()>2)
				{
					//pnlTop.getTxaDescription().setText("ROFL");
					m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex()+1, true);
					m_btn_next.setEnabled(true);
				}
				else
					has_shape();
					//m_btn_next.setEnabled(false);
			}
			catch(Exception err)
			{
				
			}
		}
		if("act_set_ellipse_corner".equals(e.getActionCommand())) {
			try {
				EllipseStruct ellipse = PAS.get_pas().get_mappane().get_active_shape().typecast_ellipse();
				if(ellipse.get_center() != null && ellipse.get_corner() != null) {
					m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex()+1, true);
					m_btn_next.setEnabled(true);
				}
				else
					m_btn_next.setEnabled(false);
					
			} catch(Exception err) {
				
			}
		}
		if("act_sendingtype_ellipse".equals(e.getActionCommand()) || "act_sendingtype_polygon".equals(e.getActionCommand())) {
			disableNext();
		}
		/*if(e.getSource().equals(m_alert.getPanelToolbar().get_cell_broadcast_text()) || e.getSource().equals(m_alert.getPanelToolbar().get_cell_broadcast_voice())) {
			if(m_alert.getPanelToolbar().get_cell_broadcast_text().isSelected() || m_alert.getPanelToolbar().get_cell_broadcast_voice().isSelected()) {
				m_tabbedpane.insertTab("Location Based Alert", null, 
						m_cell_broadcast_text_panel,
						"Configure Location Based Alert",
						m_tabbedpane.indexOfComponent(m_alert.getGui())+1);
				
				//int n_adrtypes = m_alert.getPanelToolbar().get_addresstypes();
				//m_cell_broadcast_text_panel.setSendingType(m_alert.getPanelToolbar().get_addresstypes());
				if(m_alert.getAlert() != null && m_alert.getAlert().getArea() != null) {
					//m_cell_broadcast_text_panel.get_txt_localtext().setText(m_alert.getAlert().getLocalLanguage()[1]);
					//m_cell_broadcast_text_panel.set_size_label(m_alert.getAlert().getLocalLanguage()[1], m_cell_broadcast_text_panel.get_lbl_localsize());
					//m_cell_broadcast_text_panel.get_txt_internationaltext().setText(m_alert.getAlert().getInternationalLanguage()[1]);
					m_cell_broadcast_text_panel.setMessages(m_alert.getAlert().getCBMessages());
					m_cell_broadcast_text_panel.get_cbx_messages().setSelectedIndex(-1);
					//m_cell_broadcast_text_panel.set_size_label(m_alert.getAlert().getInternationalLanguage()[1], m_cell_broadcast_text_panel.get_lbl_internationalsize());
					m_cell_broadcast_text_panel.get_combo_area().setSelectedItem(m_alert.getAlert().getArea());
					if(m_alert.getAlert().getCBOadc() != null && m_alert.getAlert().getCBOadc().length()>0)
						m_cell_broadcast_text_panel.get_txt_oadc_text().setText(m_alert.getAlert().getCBOadc());
					
				}
			} else
				m_tabbedpane.remove(m_cell_broadcast_text_panel);
		}*/
	}
	
	public void set_values() {
		try {
			get_sendobject().get_sendproperties().set_bbprofile(m_alert_settings.get_current_profile());
			get_sendobject().get_sendproperties().set_refno(m_alert_send.get_refno());
			get_sendobject().get_sendproperties().set_scheddatetime(m_alert_settings.get_scheddatetime());
			get_sendobject().get_sendproperties().set_oadc(m_alert_settings.get_current_oadc());
			get_sendobject().get_sendproperties().set_sendingname(m_alert_settings.get_sendingname(), "Alert");
			get_sendobject().get_sendproperties().set_schedprofile(m_alert_settings.get_current_schedprofile());
			get_sendobject().get_sendproperties().set_validity(m_alert_settings.get_current_validity());
			if(get_sendcontroller().get_pas().get_current_project()!=null)
				get_sendobject().get_sendproperties().set_projectpk(get_sendcontroller().get_pas().get_current_project().get_projectpk());
			else
				get_sendobject().get_sendproperties().set_projectpk("");
			if(m_cell_broadcast_text_panel != null)
				get_sendobject().get_sendproperties().set_requesttype(m_cell_broadcast_text_panel.getRequestType());
			else
				get_sendobject().get_sendproperties().set_requesttype(0);
			get_sendobject().get_sendproperties().set_maxchannels(m_alert_settings.getMaxChannels());
			
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR set_values() " + e.getMessage(), e);
            Error.getError().addError(Localization.l("common_error"),"SendWindow Exception in set_values",e,1);
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		if(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()).equals(m_alert_send))
				verify_external_exec();
		if(((m_alert.getSendObj().get_toolbar().get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0) && 
				((componentIndex(m_cell_broadcast_text_panel)<componentIndex(m_tabbedpane.getSelectedComponent())) 
						|| m_tabbedpane.getSelectedComponent().equals(m_alert_send)) &&
				!m_cell_broadcast_text_panel.defaultLanguage()) {
			showSpecifyLanguage();
			try
			{
				m_tabbedpane.setSelectedComponent(m_cell_broadcast_text_panel);
			}
			catch(Exception ex) {}
			finally { }
		}
		
		if(hasVoice(m_alert.getPanelToolbar().get_addresstypes()))
			m_alert_settings.toggleVoiceSettings(true);
		else
			m_alert_settings.toggleVoiceSettings(false);
		
		if(hasSMS(m_alert.getPanelToolbar().get_addresstypes()))
		{
			int sms_index = -1;
			for(int i=0;i<m_tabbedpane.getTabCount();++i) {
				if(m_sms_broadcast_text_panel == m_tabbedpane.getComponentAt(i)) {
					sms_index = i;
				}
			}
			
			if(sms_index < m_tabbedpane.getSelectedIndex()) {
				if(!m_alert_send.checkSMSInput()) {
					return;
				}
			}
		}
		if((((hasBlocklist(m_alert.getPanelToolbar().get_addresstypes()) && m_alert.getPanelToolbar().get_addresstypes()-SendController.SENDTO_USE_NOFAX_COMPANY < 1)||
				(!hasBlocklist(m_alert.getPanelToolbar().get_addresstypes()) && m_alert.getPanelToolbar().get_addresstypes() < 1)
				|| (!hasRecipientTypeAndChannel(m_alert.getPanelToolbar().get_addresstypes()) && isABAS(m_alert.getPanelToolbar().get_addresstypes()))
				) && m_tabbedpane.getSelectedComponent().getClass() != m_alert.getGui().getClass())
				) {
			JFrame frame = get_frame();
			if(isABAS(m_alert.getPanelToolbar().get_addresstypes()))
				JOptionPane.showMessageDialog(this, Localization.l("main_parm_alert_dlg_specify_recipient_type_abas"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, Localization.l("main_parm_alert_dlg_specify_recipient_type"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			frame.dispose();
			m_tabbedpane.setSelectedComponent(m_alert.getGui());
			return;
		}
		
		int alert_name_index = -1;
		for(int i=0;i<m_tabbedpane.getTabCount();++i) {
			if(m_alert_settings == m_tabbedpane.getComponentAt(i)) {
				alert_name_index = i;
			}
		}
		
		if(alert_name_index < m_tabbedpane.getSelectedIndex()) {
			if(m_alert_settings.get_sending_name().getText().length() < 1) {
				JFrame frame = get_frame();
                JOptionPane.showMessageDialog(this, Localization.l("main_sending_sendingname_mandatory"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				frame.dispose();
				m_tabbedpane.setSelectedComponent(m_alert_settings);
				return;
			}
		}
		
		/*
		if(m_tabbedpane.getSelectedComponent().equals(m_alert.getGui())) {
			ShapeStruct m_edit_shape = null;
			MainController main = m_alert.get_main();
			
			if(m_alert.getAlert().getM_shape()==null) {
				//default to polygon
				m_edit_shape = (new PolygonStruct(main.getMapNavigation().getDimension()));
			}
			else {
				try {
					m_edit_shape = (ShapeStruct)m_alert.getAlert().getM_shape().clone();
				} catch(Exception ex) {
					log.debug(ex.getMessage());
					log.warn(ex.getMessage(), ex);
					Error.getError().addError("AlertController","Exception in editAlert",ex,1);
				}
			}
			try {
				m_alert.getPanelToolbar().setActiveShape(m_edit_shape);
			} catch(Exception ex){
				Error.getError().addError("AlertController","Exception in editAlert",ex,1);
			}
			main.setDrawMode(m_edit_shape);
			
		}
		else
		{
			PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_PAN);
		}*/

		if(m_cell_broadcast_text_panel != null && m_cell_broadcast_text_panel.m_popup != null) {
			m_cell_broadcast_text_panel.get_lbl_localsize().setToolTipText(null);
			m_cell_broadcast_text_panel.m_popup.hide();
		}
		if(m_sms_broadcast_text_panel != null && m_sms_broadcast_text_panel.m_popup != null) {
			m_sms_broadcast_text_panel.get_lbl_localsize().setToolTipText(null);
			m_sms_broadcast_text_panel.m_popup.hide();
		}
		if(m_tabbedpane.getSelectedComponent().equals(m_sms_broadcast_text_panel))
			m_sms_broadcast_text_panel.set_size_label(m_sms_broadcast_text_panel.get_txt_messagetext().getText(), m_sms_broadcast_text_panel.get_lbl_localsize());
		else if(m_tabbedpane.getSelectedComponent().equals(m_cell_broadcast_text_panel))
			m_cell_broadcast_text_panel.set_size_label(m_cell_broadcast_text_panel.get_txt_messagetext().getText(), m_cell_broadcast_text_panel.get_lbl_localsize());
		has_shape();
		set_next_text();
	}
	public void windowActivated(WindowEvent e) {
		//PAS.get_pas().add_event("Window activated");
		//get_sendcontroller().set_activesending(get_sendobject());
		//get_sendobject().get_toolbar().setActive();
		//send_activationevent(true);
		
		//if(PAS.get_pas().get_parmcontroller().getMapShape() != m_alert.getAlert().getM_shape()) {
		
		if(m_alert.getAlert() != null) {
			if(m_alert.get_m_edit_shape() != PAS.get_pas().get_parmcontroller().get_shape()) {
				ShapeStruct shape = PAS.get_pas().get_parmcontroller().get_shape();
				PAS.get_pas().get_parmcontroller().clearDrawQueue();
				PAS.get_pas().get_parmcontroller().setSelectedAlert(m_alert.getAlert());
				PAS.get_pas().get_parmcontroller().setDrawMode(shape);
			}
		}
		has_shape();
		//} else {
			
		//}
	}
	public void windowClosed(WindowEvent e) {
		//send activation event to toolbar
		if(m_alert != null) {
			PAS.get_pas().add_event("windowClosed", null);
			send_activationevent(false);
			PAS.get_pas().get_parmcontroller().updateShape(null);
			if(m_alert.getAlert() != null) {
				PAS.get_pas().get_parmcontroller().setFilled(m_alert.getAlert().getM_shape());
			}
			
			m_tabbedpane = null;
			m_cell_broadcast_text_panel = null;
			m_addresspanel = null;
			m_addresscount = null;
			m_alert = null;
			m_alert_send = null;
			m_alert_settings = null;
			//PAS.get_pas().get_mappane().set_prev_mode();
			PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.PAN);
		}
	}
	public void windowClosing(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) {
		//windowClosed(e);
	}
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { 
		//send deactivation event to toolbar
		windowActivated(e);
	}
	public void send_activationevent(boolean b) {
		ActionEvent ae;
		if(b) {
			ae = new ActionEvent(get_sendobject(), ActionEvent.ACTION_PERFORMED, "act_sendwindow_activated");
		}
		else {
			ae = new ActionEvent(get_sendobject(), ActionEvent.ACTION_PERFORMED, "act_sendwindow_closed");
		}
		
		SendOptionToolbar toolbar = m_alert.getPanelToolbar();
		toolbar.actionPerformed(ae);
	}
	class BtnPane extends JPanel {
		public static final long serialVersionUID = 1;
		BtnPane() {
			super();
			setLayout(new FlowLayout());
		}
		void add_button(Component c) {
			this.add(c);
		}
	}
	private void set_alert_values() {
		m_alert_settings.set_selected_profile(m_alert.getAlert().getProfilepk());
		m_alert_settings.set_selected_schedprofile(m_alert.getAlert().getSchedpk());
		m_alert_settings.set_selected_oadc(m_alert.getAlert().getOadc());
		m_alert_settings.set_selected_validity(m_alert.getAlert().getValidity());
		m_alert_settings.set_name(m_alert.getAlert().getName());
		m_alert_settings.setMaxChannels(m_alert.getAlert().getMaxChannels());
		m_alert_settings.setRequestType(m_alert.getAlert().getRequestType());
		m_alert.getGui().getTxtDescription().setText(m_alert.getAlert().getDescription());
		if(m_alert.getAlert().getLocked()==0)
			m_alert_send.get_chk_execute_remote().setSelected(false);
		else if(m_alert.getAlert().getLocked()==1)
			m_alert_send.get_chk_execute_remote().setSelected(true);

		//to permanantly disable remote execute checkbox
		m_alert_send.get_chk_execute_remote().setEnabled(false);

		m_alert_settings.get_parent().get_sms_broadcast_text().get_txt_messagetext().setText(m_alert.getAlert().get_sms_message());
		m_alert_settings.get_parent().get_sms_broadcast_text().get_txt_oadc_text().setText(m_alert.getAlert().get_sms_oadc());
		m_alert_settings.get_parent().get_sms_broadcast_text().setExpiry(m_alert.getAlert().get_LBAExpiry());
		m_alert_settings.get_parent().get_cell_broadcast_text().set_expiryMinutes(m_alert.getAlert().get_LBAExpiry());
		m_alert_settings.get_parent().get_cell_broadcast_text().set_requesttype(m_alert.getAlert().getRequestType());
	}
}











