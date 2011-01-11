package Send.SendPanels;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import UMS.ErrorHandling.Error;
import UMS.Tools.*;
import PAS.*;
import Send.*;
import Sound.*;
import Core.WebData.*;
import Core.MainUI.*;


public class SendWindow extends JDialog implements ActionListener, ChangeListener, WindowListener {
	SendController m_sendcontroller;
	public SendController get_sendcontroller() { return m_sendcontroller; }
	protected JTabbedPane m_tabbedpane;
	protected Sending_AddressResend m_resendpanel;
	protected Sending_AddressPanel m_addresspanel;
	protected Sending_Settings m_settings;
	protected Sending_Cell_Broadcast_text m_cell_broadcast_text_panel;
	protected Sending_Files m_files[] = null;
	protected int m_n_files = 0;
	protected Sending_Send m_send = null;
	protected int m_n_fileindex = 2;
	protected SendObject m_sendobject;
	protected JButton m_btn_next;
	protected JButton m_btn_back;
	protected JButton m_btn_simulation;
	protected StdTextLabel m_txt_comstatus = new StdTextLabel("");
	protected LoadingPanel m_loader = null;
	public LoadingPanel get_loader() { return m_loader; }
	protected BtnPane m_btnpane = new BtnPane();
	protected XMLSendSettings m_xmlsendsettings;
	public XMLSendSettings get_settingsloader() { return m_xmlsendsettings; }
	public SendObject get_sendobject() { return m_sendobject; }
	public int get_num_files() { return m_n_files; }
	public Sending_Files[] get_files() { return m_files; }
	public JButton get_btn_next() { return m_btn_next; }
	protected ArrayList m_ttslang = null;
	public void set_tts(ArrayList tts) { m_ttslang = tts; }
	protected ArrayList m_txtlib = null;
	protected ArrayList m_soundlib = null;
	public void set_txtlib(ArrayList txt) { m_txtlib = txt; }
	public void set_soundlib(ArrayList snd) { m_soundlib = snd; }
	public ArrayList get_tts() { return m_ttslang; }
	public ArrayList get_txtlib() { return m_txtlib; }
	public ArrayList get_soundlib() { return m_soundlib; }
	protected AddressCount m_addresscount = null;
	public AddressCount get_addresscount() { return m_addresscount; }
	protected Image imgbg = null;
	public Image get_bgimg() { return imgbg; }

	//private String m_sz_sendingid;
	//public String get_sendingid() { return m_sz_sendingid; }
	protected void generate_sendingid() {
	}
	public int get_sendingid() {
		return get_sendobject().get_toolbar().get_sendingid();
	}
	
	
	public SendWindow(SendController controller) {
		super(controller.get_pas());
	}
	
	public boolean load_background() {
		try {
			imgbg = ImageLoader.load_icon("warninglivesending.png").getImage();
			imgbg = ImageLoader.makeTransparent(imgbg, 0.55f);
			return true;
		} catch(Exception e) {
			Error.getError().addError("Error in ImageLoader", "Could not load warning background image", e, Error.SEVERITY_WARNING);
			return false;
		}
	}
	
	public SendWindow(SendController controller, SendObject obj) {
		super(controller.get_pas());
		m_sendobject = obj;
		//register window with toolbar
		m_sendobject.set_sendwindow(this);
		//generate_sendingid();
		load_background();
		try {
			this.setAlwaysOnTop(false);
			this.setModal(false);
		} catch(Exception e) {
			Error.getError().addError("SendingWindow","Exception in SendWindow",e,1);
		}
		this.setTitle("New sending - \"" + m_sendobject.get_sendproperties().get_sendingname() + "\"");
		setLayout(new BorderLayout());
		m_sendcontroller = controller;
		int n_width = 600, n_height = 450;
		//setBounds((controller.get_pas().getWidth() / 2) - (n_width/2), (controller.get_pas().getHeight() / 2) - (n_height/2), n_width, n_height);
		Dimension d = Utils.screendlg_upperleft(n_width, n_height);
		setBounds(d.width, d.height, n_width, n_height);
		
		if(!obj.get_sendproperties().get_isresend()) {
			switch(obj.get_sendproperties().get_sendingtype()) {
				case SendProperties.SENDING_TYPE_POLYGON_:
					m_addresspanel = new Sending_AddressPanelPolygon(controller.get_pas(), this);
					break;
				case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
					m_addresspanel = new Sending_AddressPanelGIS(controller.get_pas(), this);
					break;
				case SendProperties.SENDING_TYPE_CIRCLE_:
					m_addresspanel = new Sending_AddressPanelEllipse(controller.get_pas(), this);
					break;
			}
		}
		m_loader = new LoadingPanel("", new Dimension(100, 20));
		reset_comstatus();

		
	
		
		m_settings = new Sending_Settings(controller.get_pas(), this);
		m_cell_broadcast_text_panel = new Sending_Cell_Broadcast_text(controller.get_pas(), this);
		m_send = new Sending_Send(controller.get_pas(), this);
		m_tabbedpane = new JTabbedPane();
		m_tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		if(obj.get_sendproperties().get_isresend()) {
			m_addresscount = new AddressCount();
			m_resendpanel = new Sending_AddressResend(this, controller.get_pas().get_statuscontroller().get_statuscodes());				
			m_tabbedpane.addTab("Resend (from refno " + obj.get_sendproperties().get_resend_refno() + ")", null,
					m_resendpanel,
					"Include addresses for resend (from refno " + obj.get_sendproperties().get_resend_refno() + ")");
		} else {		
			m_tabbedpane.addTab("Addresses", null,
								m_addresspanel,
								"Selected addresses");
		}
		m_tabbedpane.addTab("Settings", null,
							m_settings,
							"Settings");
		if(obj.get_toolbar().get_cell_broadcast_text().isSelected()) {
			m_tabbedpane.addTab("Cell Broadcast", null, 
							m_cell_broadcast_text_panel,
							"Configure Cell Broadcast");
		}
		m_tabbedpane.addTab("Finalize", null,
							m_send,
							"Finalize sending");
		m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_send), false);
		//m_tabbedpane.setEnabledAt(1, false);
		m_tabbedpane.setEnabledAt(2, false);
		
		m_btn_next = new JButton("Next>>");
		m_btn_back = new JButton("<<Back");
		m_btn_simulation = new JButton("Simulate");
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
		m_tabbedpane.addChangeListener(this);
		addWindowListener(this);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	protected void add_controls() {
		init();
	}
	public void set_comstatus(String sz_text) {
		m_txt_comstatus.setText("Status: " + sz_text);
	}
	public void reset_comstatus() {
		set_comstatus("Idle...");
	}
	public void start_download_settings() {
		set_comstatus("Downloading settings...");
		get_loader().set_totalitems(0, "Loading settings...");
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
				m_files = new Sending_Files[m_settings.get_current_profile().get_soundfiles().size()];
			add_filepanes();
		} catch(Exception e) {
			Error.getError().addError("Dialog error", "Could not initialize soundfile panes", e, 1);
		}
	}
	public void add_filepanes() {
		SoundFile file;
		for(int i=0; i < m_settings.get_current_profile().get_soundfiles().size(); i++) {
			try {
				file = (SoundFile)m_settings.get_current_profile().get_soundfiles().get(i);
				m_files[i] = new Sending_Files(PAS.get_pas(), this, file); //(SoundFile)get_sendobject().get_sendproperties().get_bbprofile().get_soundfiles().get(i)
				m_tabbedpane.insertTab("WAV " + (i+1) + " (" + file.get_modulename() + ")", null, 
									m_files[i],
									"Soundfile number " + (i+1), (m_n_fileindex + i));
				m_tabbedpane.setEnabledAt(m_n_fileindex + i, false);
			} catch(Exception e) {
				PAS.get_pas().add_event("ERROR add_filepanes() : " + e.getMessage(), e);
				Error.getError().addError("Sending_Settings","Exception in add_filepanes",e,1);
			}
		}
		this.m_send.add_controls();
	}
	public void remove_old_filepanes() {
		if(m_files==null)
			return;
		for(int i=0; i < m_files.length; i++) {
			m_tabbedpane.remove(m_tabbedpane.indexOfComponent(m_files[i]));
		}
	}
	public void set_next_text() {
		if(m_tabbedpane.getSelectedIndex() == m_tabbedpane.getTabCount()-1) {
			m_btn_next.setText("Send");
			m_btn_next.setActionCommand("act_send");
			m_btn_simulation.setVisible(true);
			m_tabbedpane.setEnabledAt(m_tabbedpane.getSelectedIndex(), true);
		} else {
			m_btn_next.setText("Next>>");
			m_btn_next.setActionCommand("act_next");
			m_btn_simulation.setVisible(false);
		}
		if(m_tabbedpane.getSelectedIndex() == 0)
			m_btn_back.setEnabled(false);
		else {
			m_btn_back.setEnabled(true);
		}
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
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_next".equals(e.getActionCommand())) {
			if(m_tabbedpane.getSelectedIndex() < m_tabbedpane.getTabCount()-1) {
				m_tabbedpane.setSelectedComponent(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()+1));
			}
			if(m_tabbedpane.getSelectedComponent() == m_cell_broadcast_text_panel) {
				m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_cell_broadcast_text_panel), true);
			}
			//set_next_text();
		} else if("act_back".equals(e.getActionCommand())) {
			if(m_tabbedpane.getSelectedIndex() > 0)
				m_tabbedpane.setSelectedComponent(m_tabbedpane.getComponentAt(m_tabbedpane.getSelectedIndex()-1));
			//set_next_text();
		} else if("act_adrcount".equals(e.getActionCommand())) {
			m_btn_next.setEnabled(false);
			m_loader.start_progress(0, "Addresscount");
			m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_settings), false);
			if(m_addresspanel!=null)
				m_addresspanel.exec_adrcount(); //wait for this to finish before downloading settings
			else
				start_download_settings(); //we already have statuscode selection. start the settings download
		} else if("act_settings_startload".equals(e.getActionCommand())) {
			m_xmlsendsettings = new XMLSendSettings(Thread.NORM_PRIORITY, PAS.get_pas(), "PAS_getsendoptions.asp?l_comppk=" + PAS.get_pas().get_userinfo().get_comppk() + "&l_deptpk=" + PAS.get_pas().get_userinfo().get_current_department().get_deptpk(), m_settings, PAS.get_pas().get_httpreq(), get_loader());
			m_xmlsendsettings.start();	//connection will report to Sending_settings when done. Sending_settings will report back here
		} else if("act_settings_loaded".equals(e.getActionCommand())) {
			m_btn_next.setEnabled(true);
			m_tabbedpane.setEnabledAt(m_tabbedpane.indexOfComponent(m_settings), true);
			init_values();
		} else if("act_settings_changed".equals(e.getActionCommand())) {

		} else if("act_send".equals(e.getActionCommand())) {
			get_sendobject().get_sendproperties().set_simulation(false);
			if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Confirm live sending to " + get_addresscount().get_total_by_types() + " recipients", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				m_send.actionPerformed(e);
			}
		} else if("act_send_simulation".equals(e.getActionCommand())) {
			get_sendobject().get_sendproperties().set_simulation(true);
			if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Confirm simulated sending to " + get_addresscount().get_total_by_types() + " recipients", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				m_send.actionPerformed(e);
			}
		} else if("act_finish".equals(e.getActionCommand())) {
			PAS.get_pas().get_statuscontroller().retrieve_statusitems(null, get_sendobject().get_sendproperties().get_projectpk(), get_sendobject().get_sendproperties().get_refno(), true);
			this.setVisible(false);
		} else if(Sending_AddressPanel.ADRCOUNT_CALLBACK_ACTION_.equals(e.getActionCommand())) {
			m_addresscount = (AddressCount)e.getSource();
			start_download_settings();
		}
	}
	public void init_values() {
		if(get_sendobject().get_sendproperties()!=null) {
			if(get_sendobject().get_sendproperties().get_oadc_number()!=null) {
				if(get_sendobject().get_sendproperties().get_oadc_number().length() > 0)
					m_settings.set_selected_oadc(get_sendobject().get_sendproperties().get_oadc_number());
			}
			if(get_sendobject().get_sendproperties().get_profilepk() > -1) {
				if(m_settings.set_selected_profile(get_sendobject().get_sendproperties().get_profilepk())==-1)
					get_sendobject().get_sendproperties().set_profilepk(0);
			}
			if(get_sendobject().get_sendproperties().get_schedprofilepk()!=null) {
				if(get_sendobject().get_sendproperties().get_schedprofilepk().length() > 0)
					m_settings.set_selected_schedprofile(get_sendobject().get_sendproperties().get_schedprofilepk());
			}
			if(get_sendobject().get_sendproperties().get_validity() > 0)
				m_settings.set_selected_validity(get_sendobject().get_sendproperties().get_validity());
		}
	}
	public void set_values() {
		try {
			get_sendobject().get_sendproperties().set_bbprofile(m_settings.get_current_profile());
			get_sendobject().get_sendproperties().set_refno(m_send.get_refno());
			get_sendobject().get_sendproperties().set_scheddatetime(m_settings.get_scheddatetime());
			get_sendobject().get_sendproperties().set_oadc(m_settings.get_current_oadc());
			get_sendobject().get_sendproperties().set_sendingname(m_settings.get_sendingname(), "New sending");
			get_sendobject().get_sendproperties().set_schedprofile(m_settings.get_current_schedprofile());
			get_sendobject().get_sendproperties().set_validity(m_settings.get_current_validity());
			if(get_sendcontroller().get_pas().get_current_project()!=null)
				get_sendobject().get_sendproperties().set_projectpk(get_sendcontroller().get_pas().get_current_project().get_projectpk());
			else
				get_sendobject().get_sendproperties().set_projectpk("");
		} catch(Exception e) {
			Error.getError().addError("SendWindow","Exception in set_values",e,1);
		}
	}
	public void stateChanged(ChangeEvent e) {
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
		public BtnPane() {
			super();
			setLayout(new FlowLayout());
		}
		public void add_button(Component c) {
			this.add(c);
		}
	}
	
}











