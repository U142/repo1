package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.LightPanel;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.core.ws.WSSendSettings;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.AddressCount;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.SendPropertiesTAS;
import no.ums.pas.send.TTSLang;
import no.ums.pas.sound.SoundFile;
import no.ums.pas.sound.SoundlibFile;
import no.ums.pas.status.StatusCode;
import no.ums.pas.status.StatusCodeList;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.status.StatusSending.ResendPanel;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.Utils;
import no.ums.pas.ums.tools.calendarutils.SchedCalendar.Day;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Calendar;



public class SendWindow extends JDialog implements ActionListener, ChangeListener, WindowListener {
	public static final long serialVersionUID = 1;

	SendController m_sendcontroller;
	public SendController get_sendcontroller() { return m_sendcontroller; }
	protected JTabbedPane m_tabbedpane;
	protected Sending_AddressResend m_resendpanel;
	protected Sending_AddressPanel m_addresspanel;
	protected Sending_Settings m_settings;
	protected Sending_Cell_Broadcast_text m_cell_broadcast_text_panel;
	protected Sending_SMS_Broadcast_text m_sms_broadcast_text_panel;
	public Sending_Cell_Broadcast_text get_cell_broadcast_text() { return m_cell_broadcast_text_panel; }
	public Sending_SMS_Broadcast_text get_sms_broadcast_text() { return m_sms_broadcast_text_panel; }
	protected Sending_Files m_files[] = null;
	protected int m_n_files = 0;
	protected Sending_Send m_send = null;
	public Sending_Send get_sending_send() { return m_send; }
	protected int m_n_fileindex = 2;
	protected SendObject m_sendobject;
	protected JButton m_btn_next;
	protected JButton m_btn_back;
	protected JButton m_btn_simulation;
	protected JButton m_btn_silent;
	protected StdTextLabel m_txt_comstatus = new StdTextLabel("");
	protected LoadingPanel m_loader = null;
	public LoadingPanel get_loader() { return m_loader; }
	protected BtnPane m_btnpane = new BtnPane();
	//protected XMLSendSettings m_xmlsendsettings;
	protected WSSendSettings m_xmlsendsettings;
	public WSSendSettings get_settingsloader() { return m_xmlsendsettings; }
	public SendObject get_sendobject() { return m_sendobject; }
	public int get_num_files() { return m_n_files; }
	public Sending_Files[] get_files() { return m_files; }
	public JButton get_btn_next() { return m_btn_next; }
	protected ArrayList<TTSLang> m_ttslang = null;
	public void set_tts(ArrayList<TTSLang> tts) { m_ttslang = tts; }
	protected ArrayList<SoundlibFile> m_txtlib = null;
	protected ArrayList<SoundlibFile> m_soundlib = null;
	protected ArrayList<SoundlibFile> m_smstemplates = null;
	public void set_txtlib(ArrayList<SoundlibFile> txt) { m_txtlib = txt; }
	public void set_soundlib(ArrayList<SoundlibFile> snd) { m_soundlib = snd; }
	public void set_smstemplates(ArrayList<SoundlibFile> sms) { 
		m_smstemplates = sms;
		if(get_sms_broadcast_text()!=null)
		{
			get_sms_broadcast_text().FillSMSTemplates();
		}
	}
	public ArrayList<TTSLang> get_tts() { return m_ttslang; }
	public ArrayList<SoundlibFile> get_txtlib() { return m_txtlib; }
	public ArrayList<SoundlibFile> get_soundlib() { return m_soundlib; }
	public ArrayList<SoundlibFile> get_smstemplates() { return m_smstemplates; }
	protected AddressCount m_addresscount = null;
	public AddressCount get_addresscount() { return m_addresscount; }
	protected Image imgbg = null;
	public Image get_bgimg() { return imgbg; }
	public JTabbedPane get_tabbedpane() { return m_tabbedpane; }

	//private String m_sz_sendingid;
	//public String get_sendingid() { return m_sz_sendingid; }
	protected void generate_sendingid() {
	}
	public int get_sendingid() {
		return get_sendobject().get_toolbar().get_sendingid();
	}
	
	
	public SendWindow(SendController controller) {
		//super(controller.get_pas());
		super();
	}
	
	public boolean load_background() {
		try {
			//imgbg = ImageLoader.load_icon("warninglivesending.png").getImage();
			//imgbg = ImageLoader.makeTransparent(imgbg, 0.55f);
			return true;
		} catch(Exception e) {
			Error.getError().addError("Error in ImageLoader", "Could not load warning background image", e, Error.SEVERITY_WARNING);
			return false;
		}
	}
	public boolean hasBlocklist(int n_adrtypes) {
		if((n_adrtypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0 ||
		   (n_adrtypes & SendController.SENDTO_USE_NOFAX_DEPARTMENT) > 0 ||
		   (n_adrtypes & SendController.SENDTO_USE_NOFAX_GLOBAL) > 0)
			return true;
		return false;
	}
	public boolean hasSMS(int n_adrtypes)
	{
		if(doSendSMS() && 
				((n_adrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
				(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_COMPANY) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_PRIVATE) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0) ||
				(n_adrtypes & SendController.SENDTO_TAS_SMS) > 0)
			return true;
		return false;
	}
	public boolean hasLBA(int n_adrtypes)
	{
		if((n_adrtypes & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0 ||
			(n_adrtypes & SendController.SENDTO_CELL_BROADCAST_VOICE) > 0)
		{
			return true;
		}
		return false;
	}
	public boolean hasVoice(int n_adrtypes)
	{
		if(/*doSendVoice() &&*/ 
				((n_adrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
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
				(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0))	
			return true;
		return false;
	}
	public boolean hasVoicePrivate(int n_adrtypes)
	{
		if(doSendVoice() && 
				(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 ||
				(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0)
			return true;
		return false;
	}
	public boolean hasVoiceCompany(int n_adrtypes)
	{
		if(doSendVoice() && 
				((n_adrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
				(n_adrtypes & SendController.SENDTO_FIXED_COMPANY) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0))
			return true;
		return false;
	}
	public boolean hasMobilePrivate(int n_adrtypes)
	{
		if(doSendSMS() && 
				((n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_PRIVATE) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0 || 
				(n_adrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0 ||
				(n_adrtypes & SendController.SENDTO_MOBILE_PRIVATE) > 0))
			
			return true;
		return false;		
	}
	public boolean hasMobileCompany(int n_adrtypes)
	{
		if(doSendSMS() && 
				((n_adrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_COMPANY) > 0 ||
				(n_adrtypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 ||
				(n_adrtypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0 || 
				(n_adrtypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0 ||
				(n_adrtypes & SendController.SENDTO_MOBILE_COMPANY) > 0))


			return true;
		return false;

	}
	public boolean doSendSMS()
	{
		try
		{
			if(m_sendobject==null)
				return true;
			if(m_sendobject.get_sendproperties()==null)
				return true;
			if(m_sendobject.get_sendproperties().get_sendchannels() == 0 ||
				m_sendobject.get_sendproperties().get_sendchannels() == 2)
				return true;
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return true;
		}
			
	}
	public boolean doSendVoice()
	{
		try
		{
			if(m_sendobject.get_sendproperties().get_sendchannels() == 0 ||
					m_sendobject.get_sendproperties().get_sendchannels() == 1)
				return true;
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
	
	public SendWindow(SendController controller, SendObject obj, 
			Sending_Cell_Broadcast_text cb_info,
			Sending_SMS_Broadcast_text sms_info) {
		//super(controller.get_pas());
		super();
		this.setIconImage(PAS.get_pas().getIconImage());

		PAS.get_pas().get_statuscontroller().set_pause(true);
		m_sendobject = obj;
		//register window with toolbar
		m_sendobject.set_sendwindow(this);
		//generate_sendingid();
		load_background();
		try {
			this.setAlwaysOnTop(true);
			//this.setAlwaysOnTop(false);
			//this.setModal(false);
		} catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"),"Exception in SendWindow",e,1);
		}

		setWindowTitle(m_sendobject.get_sendproperties().get_sendingname());
		setLayout(new BorderLayout());
		m_sendcontroller = controller;
		int n_width = 770, n_height = 550;
		//setBounds((controller.get_pas().getWidth() / 2) - (n_width/2), (controller.get_pas().getHeight() / 2) - (n_height/2), n_width, n_height);
		Dimension d = Utils.screendlg_upperleft(n_width, n_height);
		setBounds(d.width, d.height, n_width, n_height);
		
		if(!obj.get_sendproperties().get_isresend()) {
			switch(obj.get_sendproperties().get_sendingtype()) {
				case SendProperties.SENDING_TYPE_POLYGON_:
				case SendProperties.SENDING_TYPE_POLYGONAL_ELLIPSE_:
					m_addresspanel = new Sending_AddressPanelPolygon(controller.get_pas(), this);
					break;
				case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
					m_addresspanel = new Sending_AddressPanelGIS(controller.get_pas(), this);
					break;
				case SendProperties.SENDING_TYPE_CIRCLE_:
					m_addresspanel = new Sending_AddressPanelEllipse(controller.get_pas(), this);
					break;
				case SendProperties.SENDING_TYPE_MUNICIPAL_:
					m_addresspanel = new Sending_AddressPanelPolygon(controller.get_pas(), this);
					break;
				case SendProperties.SENDING_TYPE_TAS_COUNTRY_:
					m_addresspanel = new Sending_AddressPanelTas(controller.get_pas(), this);
					break;
			}
		}
		m_loader = new LoadingPanel("", new Dimension(100, 20), false);
		reset_comstatus();

		
	
		
		m_settings = new Sending_Settings(controller.get_pas(), this);
		
		if(cb_info == null)
			m_cell_broadcast_text_panel = new Sending_Cell_Broadcast_text(controller.get_pas(), this);
		else {
			m_cell_broadcast_text_panel = cb_info;
			m_cell_broadcast_text_panel.set_parent(this);
		}
		if(sms_info == null)
			m_sms_broadcast_text_panel = new Sending_SMS_Broadcast_text(controller.get_pas(), this);
		else {
			m_sms_broadcast_text_panel = sms_info;
			m_sms_broadcast_text_panel.set_parent(this);
		}
		//if((get_sendobject().get_toolbar().get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE)
		//	m_cell_broadcast_text_panel.m_panel_messages.setVisible(false);
		m_cell_broadcast_text_panel.setSendingType(get_sendobject().get_toolbar().get_addresstypes());
		m_sms_broadcast_text_panel.setSendingType(get_sendobject().get_toolbar().get_addresstypes());

		this.get_sendobject().get_sendproperties().set_cell_broadcast_text(m_cell_broadcast_text_panel);
		this.get_sendobject().get_sendproperties().set_sms_broadcast_text(m_sms_broadcast_text_panel);
		
		m_send = new Sending_Send(controller.get_pas(), this);
		m_tabbedpane = new JTabbedPane();
		m_tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		if(obj.get_sendproperties().get_isresend()) {
			m_addresscount = new AddressCount();
			// Her må jeg hente StatusController.get_items
			ArrayList<Object> items = controller.get_pas().get_statuscontroller().get_items(); // denne listen inneholder StatusItemObjects
			StatusCodeList mainList = controller.get_pas().get_statuscontroller().get_statuscodes();
			StatusCodeList list = new StatusCodeList();
			
			if(obj.get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
				SendPropertiesTAS tas = (SendPropertiesTAS)obj.get_sendproperties();
				StatusCode sc;
				for(int i=0;i<tas.getSmsInStats().size();++i) {
					sc = new StatusCode(tas.getSmsInStats().get(i).getLAnswercode(),tas.getSmsInStats().get(i).getSzDescription(),false,tas.getSmsInStats().get(i).getLCount(),false);
					/*
					for(int j=0;j<list.size();++j) {
						if(list.get(j).get_code() == sc.get_code())
							list.get(j).set_current_count(sc.get_current_count() + list.get(j).get_current_count());
					}*/
					list._add(sc);
				}
			}
			
			for(int i=0;i<items.size();i++) {
				StatusItemObject item = (StatusItemObject)items.get(i);
				int count = 0;
				
				if(item.get_refno() == obj.get_sendproperties().get_resend_refno()) {
					StatusItemObject tempItem;
					for(int j=0; j<=i; j++) {
						tempItem = ((StatusItemObject)items.get(j));
						if(tempItem.get_status() == item.get_status() && tempItem.get_refno() == item.get_refno())
							count++;
					}
					list._add(new StatusCode(item.get_status(), mainList.get_statusname(item.get_status()), false, count, false));
				}
			}
			
			//m_resendpanel = new Sending_AddressResend(this, controller.get_pas().get_statuscontroller().get_statuscodes(), obj.get_sendproperties().get_resend_refno());				
			m_resendpanel = new Sending_AddressResend(this, list, obj.get_sendproperties().get_resend_refno());
			if(obj.get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_ && obj.get_sendproperties().get_isresend()) {
                m_tabbedpane.addTab(Localization.l("main_tas_panel_new_message") + " (" + Localization.l("main_resend_from_refno") + " " + obj.get_sendproperties().get_resend_refno() + ")", null,m_resendpanel,
                        //"Include addresses for resend (from refno " + obj.get_sendproperties().get_resend_refno() + ")");
                        String.format(Localization.l("main_resend_tas_status_select_tooltip"), obj.get_sendproperties().get_resend_refno()));
            }
			else {
                m_tabbedpane.addTab(Localization.l("main_status_resend") + " (" + Localization.l("main_resend_from_refno") + " " + obj.get_sendproperties().get_resend_refno() + ")", null,
                    m_resendpanel,
                    //"Include addresses for resend (from refno " + obj.get_sendproperties().get_resend_refno() + ")");
                    String.format(Localization.l("main_resend_status_select_tooltip"), obj.get_sendproperties().get_resend_refno()));
            }
		} else {
            m_tabbedpane.addTab(Localization.l("main_sending_address_overview"), null,
								m_addresspanel,
                    Localization.l("main_sending_address_overview_tooltip"));
		}
		// Vi skal kun ha med voice dersom statuskoden er under 8000
		int low = 100000;
		if(m_resendpanel != null) {
			for(int i=0;i<m_resendpanel.get_statuscodes().get_tablelist().getRowCount();i++)
			{
				StatusCode statuscode = (StatusCode)m_resendpanel.get_statuscodes().get_tablelist().getValueAt(i, 0);
				if(statuscode.get_code() < low)
					low = statuscode.get_code();
			}
		}
		if(m_resendpanel == null || low < 8000) {
        m_tabbedpane.addTab(Localization.l("main_sending_settings"), null,
							m_settings,
                Localization.l("main_sending_settings_tooltip"));
		}
		int tmp = obj.get_sendproperties().get_addresstypes();
		if(hasSMS(tmp))
		{
            m_tabbedpane.addTab(Localization.l("main_sending_sms_heading"), null,
								m_sms_broadcast_text_panel,
                    Localization.l("main_sending_sms_heading_tooltip"));
			m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_sms_broadcast_text_panel), true);
		}

		if(obj.get_toolbar().get_cell_broadcast_text().isSelected() || obj.get_toolbar().get_cell_broadcast_voice().isSelected()) {
            m_tabbedpane.addTab(Localization.l("main_status_locationbased_alert"), null,
							m_cell_broadcast_text_panel,
                    Localization.l("main_sending_lba_tooltip"));
            m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_cell_broadcast_text_panel), false);
		}
        m_tabbedpane.addTab(Localization.l("main_sending_finalize_heading"), null,
							m_send,
                Localization.l("main_sending_finalize_heading_tooltip"));
		m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_send), false);
		//m_tabbedpane.setEnabledAt(1, false);
		m_tabbedpane.setEnabledAt(2, false);


        m_btn_next = new JButton(Localization.l("common_wizard_next"));
        m_btn_back = new JButton(Localization.l("common_wizard_back"));
        m_btn_simulation = new JButton(Localization.l("main_sending_simulate"));
		m_btn_simulation.setVisible(PAS.get_pas().get_rightsmanagement().cansimulate());
        m_btn_silent = new JButton(Localization.l("common_silent"));
		m_btn_silent.setVisible(PAS.get_pas().get_rightsmanagement().canlbasilent());
		m_btn_next.setPreferredSize(new Dimension(100, 20));
		m_btn_back.setPreferredSize(new Dimension(100, 20));
		m_btn_next.setActionCommand("act_next");
		m_btn_back.setActionCommand("act_back");
		m_btn_simulation.setPreferredSize(new Dimension(100, 20));
		m_btn_simulation.setActionCommand("act_send_simulation");
		m_btn_simulation.setVisible(false);
		m_btn_silent.setPreferredSize(new Dimension(100, 20));
		m_btn_silent.setActionCommand("act_send_silent");
		m_btn_silent.setVisible(false);
		
		m_btn_next.addActionListener(this);
		m_btn_back.addActionListener(this);
		m_btn_simulation.addActionListener(this);
		m_btn_silent.addActionListener(this);
		m_txt_comstatus.setPreferredSize(new Dimension(220, 16));
		add_controls();
		m_tabbedpane.addChangeListener(this);
		addWindowListener(this);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void setWindowTitle(String s)
	{
        this.setTitle(Localization.l("mainmenu_file_newsending") + " - \"" + s + "\"");
	}
	protected void add_controls() {
		init();
	}
	public void set_comstatus(String sz_text) {
        m_txt_comstatus.setText(Localization.l("main_sending_settings_dl_status") + sz_text);
	}
	public void reset_comstatus() {
        set_comstatus(Localization.l("main_sending_settings_dl_idle"));
	}
	public void start_download_settings() {
        set_comstatus(Localization.l("main_sending_settings_dl_downloading"));
        get_loader().set_totalitems(0, Localization.l("main_sending_settings_loading"));
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_settings_startload");
		actionPerformed(e);
	}
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
		m_btnpane.add_button(m_btn_silent);
		add(m_btnpane, BorderLayout.SOUTH);
		setVisible(true);
		set_next_text();
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_adrcount");
		actionPerformed(e);
		//this.pack();
	}
	
	public void initialize_file_panes() {
		remove_old_filepanes();
		try {
			if(m_settings.get_current_profile().get_soundfiles().size()==0)
				m_files = null;
			else
			{
				m_files = new Sending_Files[m_settings.get_current_profile().get_soundfiles().size()];
			}
			add_filepanes();
		} catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"), "Could not initialize soundfile panes", e, 1);
		}
	}
	public void add_filepanes() {
		SoundFile file;
		if(hasVoice(get_sendobject().get_sendproperties().get_addresstypes()) && m_tabbedpane.indexOfComponent(m_settings) > -1)
		{
			for(int i=0; i < m_settings.get_current_profile().get_soundfiles().size(); i++) {
				try {
					file = (SoundFile)m_settings.get_current_profile().get_soundfiles().get(i);
					m_files[i] = new Sending_Files(PAS.get_pas(), this, file); //(SoundFile)get_sendobject().get_sendproperties().get_bbprofile().get_soundfiles().get(i)
                    m_tabbedpane.insertTab("WAV " + (i+1) + " (" + file.get_modulename() + ")", null,
										m_files[i],
										Localization.l("main_sending_sound_soundfileno") + " " + (i+1), (m_n_fileindex + i));
					m_tabbedpane.setEnabledAt(m_n_fileindex + i, false);
				} catch(Exception e) {
					PAS.get_pas().add_event("ERROR add_filepanes() : " + e.getMessage(), e);
					Error.getError().addError("Sending_Settings","Exception in add_filepanes",e,1);
				}
			}
			this.m_send.add_controls();
		}
	}
	public void remove_old_filepanes() {
		if(m_files==null)
			return;
		for(int i=0; i < m_files.length; i++) {
			if(m_files[i]!=null)
				m_tabbedpane.remove(m_tabbedpane.indexOfComponent(m_files[i]));
		}
	}
	public void set_next_text() {
		if(m_tabbedpane.getSelectedIndex() == m_tabbedpane.getTabCount()-1) {
			m_btn_next.setEnabled(PAS.get_pas().get_rightsmanagement().cansend());
            m_btn_next.setText(Localization.l("main_sending_send"));
			m_btn_next.setActionCommand("act_send");
			m_btn_simulation.setVisible(PAS.get_pas().get_rightsmanagement().cansimulate());
			m_btn_silent.setVisible(PAS.get_pas().get_rightsmanagement().canlbasilent());
			m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex(), true);
		} else {
			m_btn_next.setEnabled(true);
            m_btn_next.setText(Localization.l("common_wizard_next"));
			m_btn_next.setActionCommand("act_next");
			m_btn_simulation.setVisible(false);
			m_btn_silent.setVisible(false);
			if(m_tabbedpane.getSelectedComponent().getClass().equals(Sending_Files.class)) {
				if(((Sending_Files)m_tabbedpane.getSelectedComponent()).get_current_fileinfo() == null) {
					m_btn_next.setEnabled(false);
				}
				else {
					m_btn_next.setEnabled(true);
					//m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex(), true);
				}
			}
			else
				m_btn_next.setEnabled(true);

		}
		if(m_tabbedpane.getSelectedIndex() == 0)
			m_btn_back.setEnabled(false);
		else {
			m_btn_back.setEnabled(true);
		}
	}
	private boolean schedDatePassed() {
		boolean b_dtm_passed = false;
		if(m_settings.m_b_use_scheddatetime) {
			Calendar cal = Calendar.getInstance();
			Day day = (Day)m_settings.m_combo_scheddate.getSelectedItem();
			int l_date = day.get_date();
			int l_time_sched = Integer.parseInt((String)m_settings.m_combo_schedtimehour.getSelectedItem() + "" +(String)m_settings.m_combo_schedtimeminute.getSelectedItem());
	
			String sz_year = String.valueOf(cal.get(Calendar.YEAR));
			String sz_month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			String sz_day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			int l_time_now = Integer.parseInt(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + "" + String.valueOf(cal.get(Calendar.MINUTE)));
				
			int l_now = Integer.parseInt(sz_year + (sz_month.length()<2?"0" + sz_month:sz_month) + (sz_day.length()<2?"0"+sz_day:sz_day));
			
			if(l_now > l_date)
				b_dtm_passed = true;
			if(l_now == l_date && l_time_now > l_time_sched)
				b_dtm_passed = true;
		}
		return b_dtm_passed;
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_next".equals(e.getActionCommand())) {
			boolean movenext = true;
			if(m_tabbedpane.getSelectedComponent().equals(m_settings) && m_settings.m_b_use_scheddatetime){
				if(schedDatePassed()) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_schedule_error"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			if(m_tabbedpane.getSelectedComponent() == m_resendpanel) {
				boolean selected = false;
				for(int i=0;i<m_resendpanel.get_statuscodes().m_tbl_list.getRowCount();++i)
					if(Boolean.valueOf(m_resendpanel.get_statuscodes().m_tbl_list.getValueAt(i, 3).toString()))
						selected = true;
				if(m_resendpanel.get_checked().size() == 0)
					selected= false;
				if(!selected) {
					if(get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_tas_status_select_tooltip"), get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					else {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_status_select_tooltip"), get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					return;
				}
						
			}
			if(m_tabbedpane.getSelectedComponent().equals(m_sms_broadcast_text_panel)) {
				if(!m_send.checkSMSInput())
					return;
				if(m_sms_broadcast_text_panel.validateOADC(m_sms_broadcast_text_panel.get_txt_oadc_text().getText())) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_lba_error_content"));
					return;
				}
			}
			if(m_tabbedpane.getSelectedComponent().equals(m_cell_broadcast_text_panel) && !m_cell_broadcast_text_panel.defaultLanguage()) {
				if((get_sendobject().get_sendproperties().get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_lba_default_lang_error"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					for(int i=m_tabbedpane.indexOfComponent(m_cell_broadcast_text_panel);i<m_tabbedpane.getTabCount();++i){
							m_tabbedpane.setEnabledAt(i, false);
					}
					
					movenext = false;
				}
				m_cell_broadcast_text_panel.set_size_label(m_cell_broadcast_text_panel.get_txt_messagetext().getText(), m_cell_broadcast_text_panel.get_lbl_localsize());
			}
			if(m_tabbedpane.getSelectedIndex() < m_tabbedpane.getTabCount()-1 && movenext) {
				m_tabbedpane.setSelectedComponent(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()+1));
				m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex(), true);
			}
			if(m_tabbedpane.getSelectedComponent() == m_cell_broadcast_text_panel && movenext) {
				m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_cell_broadcast_text_panel), true);
			}
			if(m_tabbedpane.getSelectedComponent().getClass() == Sending_Files.class) {
				((Sending_Files)m_tabbedpane.getSelectedComponent()).get_recorderpanel().getMixer().getMixer().checkError(this);
			}
			//set_next_text();
		} else if("act_back".equals(e.getActionCommand())) {
			if(m_tabbedpane.getSelectedIndex() > 0)
				m_tabbedpane.setSelectedComponent(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()-1));
			//set_next_text();
		} else if("act_adrcount".equals(e.getActionCommand())) {
			m_btn_next.setEnabled(false);
            m_loader.start_progress(0, Localization.l("main_sending_address_count"));
			if(m_tabbedpane.indexOfComponent(m_settings) != -1)
				m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_settings), false);
			if(m_addresspanel!=null)
				m_addresspanel.exec_adrcount(); //wait for this to finish before downloading settings
			else
				start_download_settings(); //we already have statuscode selection. start the settings download
		} else if("act_settings_startload".equals(e.getActionCommand())) {
			//m_xmlsendsettings = new XMLSendSettings(Thread.NORM_PRIORITY, PAS.get_pas(), "PAS_getsendoptions.asp?l_comppk=" + PAS.get_pas().get_userinfo().get_comppk() + "&l_deptpk=" + PAS.get_pas().get_userinfo().get_current_department().get_deptpk(), m_settings, PAS.get_pas().get_httpreq(), get_loader());
			//m_xmlsendsettings.start();	//connection will report to Sending_settings when done. Sending_settings will report back here
			m_xmlsendsettings = new WSSendSettings(m_settings);
			m_xmlsendsettings.start();
		} else if("act_settings_loaded".equals(e.getActionCommand())) {
			get_loader().get_progress().setIndeterminate(false);
            get_loader().set_starttext(Localization.l("common_finished"));
			m_btn_next.setEnabled(true);
			if(m_tabbedpane.indexOfComponent(m_settings) > -1)
				m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_settings), true);
			init_values();
			if(m_resendpanel != null)
				m_resendpanel.get_statuscodes().get_table().setEnabled(true);
		} else if("act_settings_changed".equals(e.getActionCommand())) {

		} else if("act_send".equals(e.getActionCommand())) {
			boolean ready = true;
			
			if(m_sendobject.get_toolbar().get_cell_broadcast_text().isSelected() && m_cell_broadcast_text_panel.validateFields() != null) {
				JOptionPane.showMessageDialog(this,m_cell_broadcast_text_panel.validateFields());
				m_tabbedpane.setSelectedComponent(m_cell_broadcast_text_panel);
				ready = false;
			}
			
			if(ready) {
				
				get_sendobject().get_sendproperties().set_simulation(0);
				
				String message = "";
				
				if(m_sendobject.get_sendproperties().get_isresend()) {
					int count = 0;
					ArrayList<Object> statuslist = m_resendpanel.get_checked();
					ArrayList<Object> ting = PAS.get_pas().get_statuscontroller().get_items();
					//ArrayList<Object> ting = PAS.get_pas().get_statuscontroller().get_items();
					for(int j=0;j<statuslist.size();j++) {
						Object[] obj = (Object[])statuslist.get(j);
						//StatusCode statuscode = (StatusCode)obj[0];
						count +=  Integer.parseInt(obj[2].toString());
						/*for(int i=0;i<ting.size();i++) {
							StatusItemObject item = (StatusItemObject)ting.get(i);
							if(item.get_refno() == m_sendobject.get_sendproperties().get_resend_refno() &&
									item.get_status() == statuscode.get_code()) {
								//count++;
								count+=1;//item.get_adrtype(); //kanskje denne i stedet?
							}
						}*/
					}
                    message = String.format(Localization.l("main_sending_confirm_live_sending"), count);//"Confirm LIVE sending to " + count + " recipients";
				}
				else
				{
					int n_voice = get_addresscount().get_company()+get_addresscount().get_private()+get_addresscount().get_companymobile()+get_addresscount().get_privatemobile();
					int n_sms = get_addresscount().get_companysms()+get_addresscount().get_privatesms();
					if(n_voice + n_sms == 0) {
                        message = Localization.l("main_sending_confirm_live_sending_no_recipients");
                    }
					else
						if(get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                            message = String.format(Localization.l("main_sending_confirm_live_sending"),n_sms);
                        }
						else {
                            message = String.format(Localization.l("main_sending_confirm_live_sending_voice_and_sms"), n_voice, n_sms);//"Confirm LIVE sending\nVoice: " +  n_voice + "\nSMS: " +  n_sms;
                        }
				}
				//message = "Confirm simulated sending to " + get_addresscount().get_total_by_types() + " recipients";
				
				LightPanel panel = new LightPanel();
				panel.add(new JLabel("<html>" + message + "</html>"),panel.m_gridconst);
				panel.set_gridconst(0, panel.inc_panels(), 1, 1);
				StdTextArea confirm = new StdTextArea("",false);
				confirm.setPreferredSize(new Dimension(150,16));
				panel.add(confirm, panel.m_gridconst);
				
				JFrame frame = get_frame();
                int cr = JOptionPane.showConfirmDialog(frame, panel, Localization.l("common_confirm"), JOptionPane.YES_NO_OPTION);
				if(cr == JOptionPane.YES_OPTION && confirm.getText().equals("LIVE")) {
					m_send.actionPerformed(e);
					frame.dispose();
					Variables.getSendController().remove_sending(m_sendobject.get_toolbar());
				}
				else {
					if(cr == JOptionPane.YES_OPTION && !confirm.getText().equals("LIVE")) {
                        JOptionPane.showMessageDialog(frame, String.format(Localization.l("quicksend_alert_dlg_confirm_err"), confirm.getText(), "LIVE"));
                    }
					System.out.println("Sending aborted");
					frame.dispose();
				}
				
			
				/*//if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Confirm live sending to " + get_addresscount().get_total_by_types() + " recipients", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				int n_voice = get_addresscount().get_company()+get_addresscount().get_private()+get_addresscount().get_companymobile()+get_addresscount().get_privatemobile();
				int n_sms = get_addresscount().get_companysms()+get_addresscount().get_privatesms();
				if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Confirm live sending\nVoice: " +  n_voice + "\nSMS: " +  n_sms, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					m_send.actionPerformed(e);
				}*/
			}
		} else if("act_send_simulation".equals(e.getActionCommand())) {
			try
			{
				boolean ready = true;
				if(m_sendobject.get_toolbar().get_cell_broadcast_text().isSelected() && m_cell_broadcast_text_panel.validateFields() != null) {
					JOptionPane.showMessageDialog(this,m_cell_broadcast_text_panel.validateFields());
					m_tabbedpane.setSelectedComponent(m_cell_broadcast_text_panel);
					ready = false;
				} else if(hasVoice(m_sendobject.get_toolbar().get_addresstypes()) && m_settings.get_current_profile() == null) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_settings_error_no_msg_profile"));
					m_tabbedpane.setSelectedComponent(m_settings);
					ready = false;
				} else if(hasVoice(m_sendobject.get_toolbar().get_addresstypes()) && m_settings.get_current_schedprofile() == null) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_settings_error_no_cfg_profile"));
					m_tabbedpane.setSelectedComponent(m_settings);
					ready = false;
				} 
				
				if(ready) {
					get_sendobject().get_sendproperties().set_simulation(1);
					String message = "";
					
					if(m_sendobject.get_sendproperties().get_isresend()) {
						int count = 0;
						ArrayList<Object> statuslist = m_resendpanel.get_checked();
						if(statuslist.size() == 0)
							System.out.println("Statuslisten er 0");
						//ArrayList<Object> ting = PAS.get_pas().get_statuscontroller().get_items();
						for(int j=0;j<statuslist.size();j++) {
							Object[] obj = (Object[])statuslist.get(j);
							count +=  Integer.parseInt(obj[2].toString());
						}
                        message = String.format(Localization.l("main_sending_confirm_simulated_sending"), count);//"Confirm simulated sending to " + count + " recipients";
					}
					else
					{
						int n_voice = get_addresscount().get_company()+get_addresscount().get_private()+get_addresscount().get_companymobile()+get_addresscount().get_privatemobile();
						int n_sms = get_addresscount().get_companysms()+get_addresscount().get_privatesms();
						if(n_voice + n_sms == 0) {
                            message = Localization.l("main_sending_confirm_simulated_sending_no_recipients");
                        }
						else
							if(get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                                message = String.format(Localization.l("main_sending_confirm_simulated_sending"), n_sms);
                            }
							else {
                                message = String.format(Localization.l("main_sending_confirm_simulated_sending_voice_and_sms"), n_voice, n_sms);//"Confirm simulated sending\nVoice: " +  n_voice + "\nSMS: " +  n_sms;
                            }
					}
					LightPanel panel = new LightPanel();
					panel.add(new JLabel("<html>" + message + "</html>"),panel.m_gridconst);
					panel.set_gridconst(0, panel.inc_panels(), 1, 1);
					StdTextArea confirm = new StdTextArea("",false);
					confirm.setPreferredSize(new Dimension(150,16));
					panel.add(confirm, panel.m_gridconst);
					JFrame frame = get_frame();

                    int cr = JOptionPane.showConfirmDialog(frame, panel, Localization.l("common_confirm"), JOptionPane.YES_NO_OPTION);
					if(cr == JOptionPane.YES_OPTION && confirm.getText().equals("SIMULATE")) {
						frame.dispose();
						m_send.actionPerformed(e);
						Variables.getSendController().remove_sending(m_sendobject.get_toolbar());
					}
					else {
						if(cr == JOptionPane.YES_OPTION && !confirm.getText().equals("SIMULATE")) {
                            JOptionPane.showMessageDialog(frame, String.format(Localization.l("quicksend_alert_dlg_confirm_err"), confirm.getText(), "SIMULATE"));
                        }
							
						frame.dispose();
						System.out.println("Sending aborted");
					}
				}
			}
			catch(Exception err)
			{
                Error.getError().addError(Localization.l("common_error"), "Error while executing simulation", err, Error.SEVERITY_ERROR);
			}
		} 
		else if("act_send_silent".equals(e.getActionCommand())) {
			try
			{
				boolean ready = true;
				if(m_sendobject.get_toolbar().get_cell_broadcast_text().isSelected() && m_cell_broadcast_text_panel.validateFields() != null) {
					JOptionPane.showMessageDialog(this,m_cell_broadcast_text_panel.validateFields());
					m_tabbedpane.setSelectedComponent(m_cell_broadcast_text_panel);
					ready = false;
				} else if(hasVoice(m_sendobject.get_toolbar().get_addresstypes()) && m_settings.get_current_profile() == null) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_settings_error_no_msg_profile"));
					m_tabbedpane.setSelectedComponent(m_settings);
					ready = false;
				} else if(hasVoice(m_sendobject.get_toolbar().get_addresstypes()) && m_settings.get_current_schedprofile() == null) {
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_settings_error_no_cfg_profile"));
					m_tabbedpane.setSelectedComponent(m_settings);
					ready = false;
				} 
				
				if(ready) {
					get_sendobject().get_sendproperties().set_simulation(2);
					String message = "";
					
					if(m_sendobject.get_sendproperties().get_isresend()) {
						int count = 0;
						ArrayList<Object> statuslist = m_resendpanel.get_checked();
						if(statuslist.size() == 0)
							System.out.println("Statuslisten er 0");
						//ArrayList<Object> ting = PAS.get_pas().get_statuscontroller().get_items();
						for(int j=0;j<statuslist.size();j++) {
							Object[] obj = (Object[])statuslist.get(j);
							count +=  Integer.parseInt(obj[2].toString());
						}
                        message = String.format(Localization.l("main_sending_confirm_silent_sending"), count);//"Confirm silent sending to " + count + " recipients";
					}
					else
					{
						int n_voice = get_addresscount().get_company()+get_addresscount().get_private()+get_addresscount().get_companymobile()+get_addresscount().get_privatemobile();
						int n_sms = get_addresscount().get_companysms()+get_addresscount().get_privatesms();
						if(n_voice + n_sms == 0) {
                            message = Localization.l("main_sending_confirm_silent_sending_no_recipients");
                        }
						else
							if(get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                                message = String.format(Localization.l("main_sending_confirm_silent_sending"), n_sms);
                            }
							else {
                                message = String.format(Localization.l("main_sending_confirm_silent_sending_voice_and_sms"), n_voice, n_sms);//"Confirm silent sending\nVoice: " +  n_voice + "\nSMS: " +  n_sms;
                            }
					}
					LightPanel panel = new LightPanel();
					panel.add(new JLabel("<html>" + message + "</html>"),panel.m_gridconst);
					panel.set_gridconst(0, panel.inc_panels(), 1, 1);
					StdTextArea confirm = new StdTextArea("",false);
					confirm.setPreferredSize(new Dimension(150,16));
					panel.add(confirm, panel.m_gridconst);
					JFrame frame = get_frame();

                    int cr = JOptionPane.showConfirmDialog(frame, panel, Localization.l("common_confirm"), JOptionPane.YES_NO_OPTION);
					if(cr == JOptionPane.YES_OPTION && confirm.getText().equals("SILENT")) {
						frame.dispose();
						m_send.actionPerformed(e);
						//this.close();
					}
					else {
						if(cr == JOptionPane.YES_OPTION && !confirm.getText().equals("SILENT")) {
                            JOptionPane.showMessageDialog(frame, String.format(Localization.l("quicksend_alert_dlg_confirm_err"), confirm.getText(), "SILENT"));
                        }
							
						frame.dispose();
						System.out.println("Sending aborted");
					}
				}
			}
			catch(Exception err)
			{
                Error.getError().addError(Localization.l("common_error"), "Error while executing silent sending", err, Error.SEVERITY_ERROR);
			}			
		}
		else if("act_finish".equals(e.getActionCommand())) {
			new Thread("Open status thread")
			{
				public void run()
				{
					PAS.get_pas().askAndCloseActiveProject(new no.ums.pas.PAS.IAskCloseStatusComplete() {
						
						@Override
						public void Complete(boolean bStatusClosed) {
							if(bStatusClosed)
							{
								PAS.get_pas().get_statuscontroller().retrieve_statusitems(null, get_sendobject().get_sendproperties().get_projectpk(), get_sendobject().get_sendproperties().get_refno(), true);
								setVisible(false);
							}							
						}
					});
				}
			}.start();
		} else if(Sending_AddressPanel.ADRCOUNT_CALLBACK_ACTION_.equals(e.getActionCommand())) {
			m_addresscount = (AddressCount)e.getSource();
			start_download_settings();
		} else if("act_adrcount_changed".equals(e.getActionCommand())) {
			try
			{
				m_addresspanel.actionPerformed(e);
			}
			catch(Exception err)
			{
				
			}
		}
	}
	
	// Is used to set the location of dialogs
	private JFrame get_frame() {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
		p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
		frame.setLocation(p);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		return frame;
	}
	
	public void init_values() {
		if(get_sendobject().get_sendproperties()!=null) {
			if(get_sendobject().get_sendproperties().get_oadc_number()!=null) {
				if(get_sendobject().get_sendproperties().get_oadc_number().length() > 0)
					m_settings.set_selected_oadc(get_sendobject().get_sendproperties().get_oadc_number());
			}
			/*if(get_sendobject().get_sendproperties().get_profilepk() > -1) {
				System.out.println("SendWindow has the profilepk: " + get_sendobject().get_sendproperties().get_profilepk());
				if(m_settings.set_selected_profile(get_sendobject().get_sendproperties().get_profilepk())==-1)
					get_sendobject().get_sendproperties().set_profilepk(0);
			}*/
			if(get_sendobject().get_sendproperties().get_schedprofilepk()!=null) {
				if(get_sendobject().get_sendproperties().get_schedprofilepk().length() > 0)
					m_settings.set_selected_schedprofile(get_sendobject().get_sendproperties().get_schedprofilepk());
			}
			if(get_sendobject().get_sendproperties().get_validity() > 0)
				m_settings.set_selected_validity(get_sendobject().get_sendproperties().get_validity());
			if(get_sendobject().get_sendproperties().get_cell_broadcast_text() != null)
				m_cell_broadcast_text_panel = get_sendobject().get_sendproperties().get_cell_broadcast_text();
		}
	}
	public void set_values() {
		try {
			get_sendobject().get_sendproperties().set_bbprofile(m_settings.get_current_profile());
			get_sendobject().get_sendproperties().set_refno(m_send.get_refno());
			get_sendobject().get_sendproperties().set_scheddatetime(m_settings.get_scheddatetime());
			get_sendobject().get_sendproperties().set_oadc(m_settings.get_current_oadc());
            get_sendobject().get_sendproperties().set_sendingname(m_settings.get_sendingname(), Localization.l("mainmenu_file_newsending"));
			get_sendobject().get_sendproperties().set_schedprofile(m_settings.get_current_schedprofile());
			get_sendobject().get_sendproperties().set_validity(m_settings.get_current_validity());
			if(get_sendcontroller().get_pas().get_current_project()!=null)
				get_sendobject().get_sendproperties().set_projectpk(get_sendcontroller().get_pas().get_current_project().get_projectpk());
			else
				get_sendobject().get_sendproperties().set_projectpk("");
			get_sendobject().get_sendproperties().set_maxchannels(m_settings.getMaxChannels());
		} catch(Exception e) {
			Error.getError().addError("SendWindow","Exception in set_values",e,1);
		}
	}
	
	public int componentIndex(Component c) {
		for(int i=0;i<m_tabbedpane.getComponentCount();++i) {
			if(m_tabbedpane.getComponent(i).equals(c))
				return i;
		}
		return -1;
	}
	
	public void showSpecifyLanguage() {
		JFrame frame = get_frame();
        JOptionPane.showMessageDialog(frame, Localization.l("main_parm_alert_dlg_specify_default_lang"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
		frame.dispose();
	}
	
	public void stateChanged(ChangeEvent e) {
		// Check if scheddatetime has passed				
		if(this.isVisible() && !m_tabbedpane.getSelectedComponent().equals(m_settings) && schedDatePassed()) {
			m_tabbedpane.setSelectedComponent(m_settings);
            JOptionPane.showMessageDialog(this, Localization.l("main_sending_schedule_error"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(((m_sendobject.get_toolbar().get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0) && 
				((componentIndex(m_cell_broadcast_text_panel)<componentIndex(m_tabbedpane.getSelectedComponent()) && !m_tabbedpane.getSelectedComponent().getClass().equals(Sending_Files.class))
						|| m_tabbedpane.getSelectedComponent().equals(m_send)) &&
				!m_cell_broadcast_text_panel.defaultLanguage()) {
			/*
			int index = -1;
			System.out.println("Selected index: " + m_tabbedpane.getSelectedIndex());
			for(int i=0;i<m_tabbedpane.getTabCount();i++) {
				if(m_tabbedpane.getComponent(i).equals(m_cell_broadcast_text_panel))
					index = i;
			}*/
			//if(index != -1) {
			showSpecifyLanguage();
			m_tabbedpane.setSelectedComponent(m_cell_broadcast_text_panel);
			//}
		}
		Component sel = ((JTabbedPane)e.getSource()).getSelectedComponent();
		if(sel instanceof Sending_Files) //start new instance of recorder
		{
			Sending_Files sf = (Sending_Files)sel;
			try
			{
				sf.get_recorder().get_recorder().startRecording();
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
		else //stop recording threads if running
		{
			if(m_files!=null)
			{
				for(Sending_Files sf : m_files)
				{
					if(sf!=null && sf.get_recorder()!=null && sf.get_recorder().get_recorder()!=null)
						sf.get_recorder().get_recorder().stopRecording();
				}
			}
		}
		
		if(hasSMS(m_sendobject.get_toolbar().get_addresstypes()))
		{
			int sms_index = -1;
			for(int i=0;i<m_tabbedpane.getTabCount();++i) {
				if(m_sms_broadcast_text_panel == m_tabbedpane.getComponentAt(i)) {
					sms_index = i;
				}
			}
			
			if(sms_index != -1 && sms_index < m_tabbedpane.getSelectedIndex()) {
				if(!m_send.checkSMSInput()) {
					return;
				}
			}
		}
		
		if(m_cell_broadcast_text_panel != null && m_cell_broadcast_text_panel.m_popup != null) {
			m_cell_broadcast_text_panel.m_lbl_messagesize.setToolTipText(null);
			m_cell_broadcast_text_panel.m_popup.hide();
		}
		if(m_sms_broadcast_text_panel != null && m_sms_broadcast_text_panel.m_popup != null) {
			m_sms_broadcast_text_panel.m_lbl_messagesize.setToolTipText(null);
			m_sms_broadcast_text_panel.m_popup.hide();
		}
		// Fordi componentListener ødelegger for guien på m_cell_broadcast_text_panel
		if(m_tabbedpane.getSelectedComponent().equals(m_sms_broadcast_text_panel))
				m_sms_broadcast_text_panel.set_size_label(m_sms_broadcast_text_panel.get_txt_messagetext().getText(), m_sms_broadcast_text_panel.get_lbl_localsize());
		else if(m_tabbedpane.getSelectedComponent().equals(m_cell_broadcast_text_panel))
			m_cell_broadcast_text_panel.set_size_label(m_cell_broadcast_text_panel.get_txt_messagetext().getText(), m_cell_broadcast_text_panel.get_lbl_localsize());
		set_next_text();
	}
	public void windowActivated(WindowEvent e) {
		//PAS.get_pas().add_event("Window activated");
		//get_sendcontroller().set_activesending(get_sendobject());
		//get_sendobject().get_toolbar().setActive();
		send_activationevent(true);
	}
	public void windowClosed(WindowEvent e) {
		//send activation event to toolbar
		send_activationevent(false);
		try
		{
			m_sms_broadcast_text_panel.stopMessageLib();
		}
		catch(Exception err)
		{
			
		}
		// her må jeg ta en sjekk på om det er andre sendwindow oppe
		//PAS.get_pas().get_sendcontroller().get_sendings().
		boolean close = true;
		ArrayList<SendObject> sendings = PAS.get_pas().get_sendcontroller().get_sendings();
		for(int i=0; i<sendings.size(); i++) {
			SendObject sending = (SendObject)sendings.get(i);
			try{
				if(!sending.get_sendwindow().equals(this) && sending.get_sendwindow() != null 
						&& sending.get_sendwindow().isVisible())
					close = false;
			} catch(Exception ex) {} // Do nothing just nullpointer which shouldn't happen
		}
		try
		{
			if(this.m_files!=null){
				for(int i=0; i < m_files.length; i++)
				{
					if(m_files[i] != null && m_files[i].get_recorder()!=null && m_files[i].get_recorder().get_recorder()!=null)
						m_files[i].get_recorder().get_recorder().stop_thread();//stopRecording();
				}
			}
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		m_tabbedpane.setSelectedIndex(0);
		if(close)
			PAS.get_pas().get_statuscontroller().set_pause(false);
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
		get_sendobject().get_toolbar().actionPerformed(ae);
	}
	public class BtnPane extends JPanel {
		public static final long serialVersionUID = 1;
		public BtnPane() {
			super();
			setLayout(new FlowLayout());
		}
		public void add_button(Component c) {
			this.add(c);
		}
	}
	
}











