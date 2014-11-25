package no.ums.pas.send.sendpanels;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.ws.vars;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.RecipientChannel;
import no.ums.pas.send.SendProperties;
import no.ums.pas.sound.SoundFile;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.Timeout;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;


/*
 * verify data and execute sending
 */
public class Sending_Send extends DefaultPanel {

    private static final Log log = UmsLog.getLogger(Sending_Send.class);

	public static final long serialVersionUID = 1;
	public static final int INDICATOR_RED_ = 0;
	public static final int INDICATOR_YELLOW_ = 1;
	public static final int INDICATOR_GREEN_ = 2;
	
	protected SendWindow parent = null;
	protected int m_n_refno = -1;
	protected StdTextLabel m_txt_refno;
	protected StdTextLabel m_txt_wav_upload[];
	protected JLabel m_lbl_wav_upload[];
	protected JLabel m_lbl_refno;
	protected JLabel m_lbl_adrfile;
	protected StdTextLabel m_txt_adrfile;
	protected ImageIcon m_icon_indicator[] = new ImageIcon[3];
	protected JButton m_btn_sendtest = new JButton(Localization.l("main_sending_send_test"));

    {
        m_btn_sendtest = new JButton(Localization.l("main_sending_send_test"));
    }
    
    protected JButton m_btn_sendtest_voice = new JButton(Localization.l("main_sending_send_test_voice"));
    protected JButton m_btn_sendtest_sms = new JButton(Localization.l("main_sending_send_test_sms"));

    protected StdTextArea m_txt_sendtest = new StdTextArea("", false, 75);
	protected SendWindow get_parent() { return parent; }
	
	private String sendTestType="";
	protected StdTextLabel m_lbl_test_recipient;
	protected StdTextLabel m_lbl_voice_profile;
	protected JComboBox schedProfileCombo;
	class ReshedProfile
	{
		private int val;
		private String label;

		public ReshedProfile(String label,int val) {
			this.label = label;
			this.val = val;
		}
		public int getVal() {
			return val;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}
	
	public int get_refno() { return m_n_refno; }
	//JButton btn_send;
	public Sending_Send(PAS pas){
		super();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}

	
	public Sending_Send(PAS pas, SendWindow parentwin) {
		super();
		parent = parentwin;
		//btn_send = new JButton("send");
		//btn_send.addActionListener(this);
		//btn_send.setActionCommand("act_send");
        m_txt_refno = new StdTextLabel(Localization.l("common_refno"), 400, 14, true);
        m_txt_adrfile = new StdTextLabel(Localization.l("main_sending_address_file"), 400, 14, true);
		if(PAS.icon_version==2)
		{
			m_icon_indicator[0] = ImageLoader.load_icon("status_red_16.png");
			m_icon_indicator[1] = ImageLoader.load_icon("status_yellow_16.png");
			m_icon_indicator[2] = ImageLoader.load_icon("status_green_16.png");			
		}
		else
		{
			m_icon_indicator[0] = ImageLoader.load_icon("circle_red.gif");
			m_icon_indicator[1] = ImageLoader.load_icon("circle_yellow.gif");
			m_icon_indicator[2] = ImageLoader.load_icon("circle_green.gif");
		}
		m_lbl_adrfile	= new JLabel(m_icon_indicator[INDICATOR_RED_]);
		m_lbl_refno		= new JLabel(m_icon_indicator[INDICATOR_RED_]);

		m_lbl_test_recipient = new StdTextLabel(Localization.l("main_sending_send_test_recipient"));
		m_lbl_voice_profile = new StdTextLabel(Localization.l("main_sending_send_test_voice_resched_profile"));
		schedProfileCombo = new JComboBox();
		initReshedProfileList();
		m_lbl_test_recipient.setPreferredSize(new Dimension(150, 30));
		m_txt_sendtest.setPreferredSize(new Dimension(112, 17));
		m_lbl_voice_profile.setPreferredSize(new Dimension(150, 30));
		schedProfileCombo.setPreferredSize(new Dimension(112, 17));

		m_btn_sendtest.setActionCommand("act_send_test");
		m_btn_sendtest.addActionListener(this);
		m_btn_sendtest.setPreferredSize(new Dimension(70, 17));
        m_btn_sendtest.setToolTipText(Localization.l("main_sending_send_test_tooltip"));
        
        m_btn_sendtest_voice.setActionCommand("act_send_test");
        m_btn_sendtest_voice.addActionListener(this);
        m_btn_sendtest_voice.setPreferredSize(new Dimension(112, 17));
        m_btn_sendtest_voice.setToolTipText(Localization.l("main_sending_send_test_tooltip"));
        m_btn_sendtest_sms.setActionCommand("act_send_test");
        m_btn_sendtest_sms.addActionListener(this);
        m_btn_sendtest_sms.setPreferredSize(new Dimension(112, 17));
        m_btn_sendtest_sms.setToolTipText(Localization.l("main_sending_send_test_tooltip"));
        
		add_controls();
	}

	private void initReshedProfileList()
	{
		ReshedProfile oneTry = new ReshedProfile(Localization.l("main_sending_send_test_voice_resched_profile_1try"), 1);
		ReshedProfile alertUserProfile = new ReshedProfile(Localization.l("main_sending_send_test_voice_resched_profile_alert"), 0);
		schedProfileCombo.addItem(oneTry);
		schedProfileCombo.addItem(alertUserProfile);
	}
	public void add_controls() {
		this.removeAll();
		m_gridconst.ipadx = 25;
		m_gridconst.ipady = 5;
		int n_max = 6;

		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.WEST);
		add(m_lbl_test_recipient, m_gridconst);
		set_gridconst(4, get_panel(), 3, 1, GridBagConstraints.WEST);
		add(m_txt_sendtest, m_gridconst);

		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.WEST);
		add(m_lbl_voice_profile, m_gridconst);
		set_gridconst(4, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(schedProfileCombo, m_gridconst);

//		set_gridconst(1, inc_panels(), 2, 1, GridBagConstraints.WEST);
//		add(m_txt_sendtest, m_gridconst);
//		set_gridconst(3, get_panel(), 1, 1, GridBagConstraints.WEST);
//		add(m_btn_sendtest, m_gridconst);
		
//		set_gridconst(1, inc_panels(), 2, 1, GridBagConstraints.WEST);
//		add(m_txt_sendtest, m_gridconst);
		set_gridconst(3, inc_panels(), 1, 1, GridBagConstraints.WEST);
		add(m_btn_sendtest_voice, m_gridconst);
		set_gridconst(4, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_btn_sendtest_sms, m_gridconst);

		//set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.CENTER);
		//add(m_lbl_refno, m_gridconst);
		set_gridconst(1, get_panel(), 5, 1, GridBagConstraints.CENTER);
		add(m_txt_refno, m_gridconst);
		if(parent.get_files()!=null && parent.get_files().length > 0) {
			create_upload_indicators();
			for(int i=0; i < m_txt_wav_upload.length; i++) {
				set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.CENTER);
				add(m_lbl_wav_upload[i], m_gridconst);
				set_gridconst(1, get_panel(), 5, 1, GridBagConstraints.CENTER);
				add(m_txt_wav_upload[i], m_gridconst);
			}
		}
		set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.CENTER);
		add(m_lbl_adrfile, m_gridconst);
		set_gridconst(1, get_panel(), 5, 1, GridBagConstraints.CENTER);
		add(m_txt_adrfile, m_gridconst);
		
		//set_gridconst(0, inc_panels(), 2, 1, GridBagConstraints.CENTER);
		//add(btn_send, m_gridconst);
		init();
	}
	private void set_filestatus(int n_fileno, int n_status) {
		m_lbl_wav_upload[n_fileno].setIcon(m_icon_indicator[n_status]);
	}
	private void set_refnostatus(int n_status) {
		m_lbl_refno.setIcon(m_icon_indicator[n_status]);
	}
	private void set_adrfilestatus(int n_status) {
		m_lbl_adrfile.setIcon(m_icon_indicator[n_status]);
	}
	public void init() {
		m_btn_sendtest_voice.setEnabled(parent.hasVoice(parent.m_sendobject.get_toolbar().get_addresstypes()));
		m_btn_sendtest_sms.setEnabled(parent.hasSMS(parent.m_sendobject.get_toolbar().get_addresstypes()));
		schedProfileCombo.setEnabled(parent.hasVoice(parent.m_sendobject.get_toolbar().get_addresstypes()));
	}
	public void create_upload_indicators() {
		m_txt_wav_upload = new StdTextLabel[parent.get_files().length];
		m_lbl_wav_upload = new JLabel[parent.get_files().length];
		for(int i=0; i < m_txt_wav_upload.length; i++) {
			m_lbl_wav_upload[i] = new JLabel(m_icon_indicator[INDICATOR_RED_]);
            m_txt_wav_upload[i] = new StdTextLabel(Localization.l("common_file") + " - " + parent.get_files()[i].get_soundfile().get_name() + " (" + parent.get_files()[i].get_soundfile().get_modulename() + ")", 400, 14, true);
		}
	}
	public boolean checkSMSInput() {
		boolean istas = false;
		if(parent.get_sendobject().get_sendproperties() != null && parent.get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
			istas=true;
		}
		if(parent.m_sms_broadcast_text_panel.get_txt_messagetext().getText().length() < 1) {
            JOptionPane.showMessageDialog(this, Localization.l("main_sending_warning_empty_sms"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			parent.m_tabbedpane.setSelectedComponent(parent.m_sms_broadcast_text_panel);
			return false;
		} else if (parent.m_sms_broadcast_text_panel.get_txt_oadc_text().getText().length() < 1 && !istas) {
            JOptionPane.showMessageDialog(this, Localization.l("main_sending_warning_empty_sms_oadc"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			parent.m_tabbedpane.setSelectedComponent(parent.m_sms_broadcast_text_panel);
			return false;
		} else if(parent.m_sms_broadcast_text_panel.get_gsmsize(parent.m_sms_broadcast_text_panel.get_txt_messagetext().getText())>parent.m_sms_broadcast_text_panel.m_maxSize) {
            JOptionPane.showMessageDialog(this, String.format(Localization.l("main_sending_warning_sms_too_long"),parent.m_sms_broadcast_text_panel.m_maxSize));
			parent.m_tabbedpane.setSelectedComponent(parent.m_sms_broadcast_text_panel);
			return false;
		}
		return true;
	}
	public void actionPerformed(ActionEvent e) {
		if("act_send".equals(e.getActionCommand())) {
			boolean b_continue = true;
			//if(parent.hasSMS(parent.m_sendobject.get_toolbar().get_addresstypes()) && !(parent.m_sendobject.get_sendproperties().get_isresend() && parent.m_sendobject.get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_)) {
			if(parent.hasSMS(parent.m_sendobject.get_toolbar().get_addresstypes())) {
				b_continue = checkSMSInput();
			}
			if(parent.m_resendpanel != null && parent.m_resendpanel.get_statuscodes().m_tbl_list.getRowCount()>0) {
				boolean selected = false;
				for(int i=0;i<parent.m_resendpanel.get_statuscodes().m_tbl_list.getRowCount();++i)
					if(Boolean.valueOf(parent.m_resendpanel.get_statuscodes().m_tbl_list.getValueAt(i, 3).toString()))
						selected = true;
				if(!selected) {
					b_continue = false;
					if(parent.get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_tas_status_select_tooltip"), parent.get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					else {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_status_select_tooltip"), parent.get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					parent.m_tabbedpane.setSelectedComponent(parent.m_resendpanel);
					return;
				}	
			}
			if(b_continue) {	
				parent.get_btn_next().setEnabled(false);
				//parent.get_sendobject().get_sendproperties().set_simulation(false);
				retrieve_refno(true, "act_set_refno");
			}
		} else if("act_send_simulation".equals(e.getActionCommand())) {
			boolean b_continue = true;
			if(parent.hasSMS(parent.m_sendobject.get_toolbar().get_addresstypes())) {
				b_continue = checkSMSInput();
			}
			if(parent.m_resendpanel != null && parent.m_resendpanel.get_statuscodes().m_tbl_list.getRowCount()>0) {
				boolean selected = false;
				for(int i=0;i<parent.m_resendpanel.get_statuscodes().m_tbl_list.getRowCount();++i)
					if(Boolean.valueOf(parent.m_resendpanel.get_statuscodes().m_tbl_list.getValueAt(i, 3).toString()))
						selected = true;
				if(!selected) {
					b_continue = false;
					if(parent.get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_tas_status_select_tooltip"), parent.get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					else {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_status_select_tooltip"), parent.get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					parent.m_tabbedpane.setSelectedComponent(parent.m_resendpanel);
					return;
				}
						
			}
			if(b_continue) {	
				parent.get_btn_next().setEnabled(false);
				//parent.get_sendobject().get_sendproperties().set_simulation(true);
				retrieve_refno(true, "act_set_refno");
			}
		} 
		else if("act_send_silent".equals(e.getActionCommand())) {
			boolean b_continue = true;
			if(parent.hasSMS(parent.m_sendobject.get_toolbar().get_addresstypes())) {
				b_continue = checkSMSInput();
			}
			if(parent.m_resendpanel != null && parent.m_resendpanel.get_statuscodes().m_tbl_list.getRowCount()>0) {
				boolean selected = false;
				for(int i=0;i<parent.m_resendpanel.get_statuscodes().m_tbl_list.getRowCount();++i)
					if(Boolean.valueOf(parent.m_resendpanel.get_statuscodes().m_tbl_list.getValueAt(i, 3).toString()))
						selected = true;
				if(!selected) {
					b_continue = false;
					if(parent.get_sendobject().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_tas_status_select_tooltip"), parent.get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					else {
                        JOptionPane.showMessageDialog(this, String.format(Localization.l("main_resend_status_select_tooltip"), parent.get_sendobject().get_sendproperties().get_resend_refno()), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
                    }
					parent.m_tabbedpane.setSelectedComponent(parent.m_resendpanel);
					return;
				}
						
			}
			if(b_continue) {	
				parent.get_btn_next().setEnabled(false);
				//parent.get_sendobject().get_sendproperties().set_simulation(true);
				retrieve_refno(true, "act_set_refno");
			}			
		}
		else if("act_set_refno".equals(e.getActionCommand())) { //wait for refno, then send
			m_n_refno = ((Integer)e.getSource()).intValue();
            m_txt_refno.setText(Localization.l("common_refno") + " - " + m_n_refno);
            parent.set_comstatus(Localization.l("main_sending_refno_retrieved") + " - " + m_n_refno);
			set_refnostatus(INDICATOR_GREEN_);
			if(send()) {
				/*
				parent.get_btn_next().setText("Finish");
				parent.get_btn_next().setActionCommand("act_finish");
				parent.get_btn_next().doClick();*/
			}
			if(PAS.get_pas().get_rightsmanagement().cansend())
				parent.get_btn_next().setEnabled(true);
		} else if("act_send_test".equals(e.getActionCommand())) {
			boolean b_continue = true;
			
			if(e.getSource().equals(m_btn_sendtest_voice))
				sendTestType="voice";
			else if(e.getSource().equals(m_btn_sendtest_sms))
				sendTestType="sms";
			
			String sz_number = m_txt_sendtest.getText();
			if(sz_number.length() < 8) {
                JOptionPane.showMessageDialog(this, Localization.l("main_sending_send_test_warning"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				return;
			}
			if(parent.hasSMS(parent.m_sendobject.get_toolbar().get_addresstypes())) {
				if(parent.m_sms_broadcast_text_panel.get_txt_messagetext().getText().length() < 1 && b_continue) {
					b_continue = false;
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_warning_empty_sms"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					parent.m_tabbedpane.setSelectedComponent(parent.m_sms_broadcast_text_panel);
				} else if (parent.m_sms_broadcast_text_panel.get_txt_oadc_text().getText().length() < 1 && b_continue) {
					b_continue = false;
                    JOptionPane.showMessageDialog(this, Localization.l("main_sending_warning_empty_sms_oadc"), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
					parent.m_tabbedpane.setSelectedComponent(parent.m_sms_broadcast_text_panel);
				} else if(parent.m_sms_broadcast_text_panel.get_gsmsize(parent.m_sms_broadcast_text_panel.get_txt_messagetext().getText())>parent.m_sms_broadcast_text_panel.m_maxSize) {
					b_continue = false;
                    JOptionPane.showMessageDialog(this, String.format(Localization.l("main_sending_warning_sms_too_long"),parent.m_sms_broadcast_text_panel.m_maxSize));
					parent.m_tabbedpane.setSelectedComponent(parent.m_sms_broadcast_text_panel);
				}
			}
			if(b_continue) {	
				parent.get_btn_next().setEnabled(false);
				retrieve_refno(false, "act_set_testrefno");
			}
		} else if("act_set_testrefno".equals(e.getActionCommand())) {
			String sz_number = m_txt_sendtest.getText();
			m_n_refno = ((Integer)e.getSource()).intValue();
            parent.set_comstatus(Localization.l("main_sending_refno_test_retrieved") + " - " + m_n_refno);
			ArrayList<String> arr_numbers = new ArrayList<String>();
			//arr_numbers.add("98220213");
			arr_numbers.add(sz_number);
			/*if(send_test(arr_numbers)) {
				JOptionPane.showMessageDialog(this, "A test-message has been sent with refno " + get_refno());
			} else {
				JOptionPane.showMessageDialog(this, "Failed to send test-message with refno " + get_refno());
			}*/
			send_test(arr_numbers);
			//parent.get_btn_next().setEnabled(true);
			parent.set_next_text();
		}
	}
	private boolean upload_wavfiles(boolean b_use_indicators) {
		if(parent.get_files()!=null) {			
			for(int i=0; i < parent.get_files().length; i++) {
				try {
					if(parent.get_files()[i]!=null && parent.get_files()[i].get_soundfile()!=null)
					{
                        parent.set_comstatus(Localization.l("main_sending_uploading_file") + " - " + parent.get_files()[i].get_soundfile().get_name());
						if(b_use_indicators)
							set_filestatus(i, INDICATOR_YELLOW_);
						SoundFile file = parent.get_files()[i].get_soundfile();
						
						if(parent.get_files()[i].get_recorder() != null)
							file.set_bytebuffer(parent.get_files()[i].get_recorder().get_bytebuffer());
						else
							file.set_bytebuffer(parent.get_files()[i].get_soundfile().get_bytebuffer()); // Kanskje ikke beste fiksen, men det virker ihvertfall nå
						
						//file.set_fileinfo(parent.get_files()[i].get)
						//PAS.get_pas().add_event("Ready to send wav type " + parent.get_files()[i].get_filetype() + " refno=" + get_refno());
						boolean b_ok = file.send_wav(get_refno(), parent.get_files()[i].get_filetype(), parent.get_files()[i].get_current_fileinfo());
						if(!b_ok) {
                            parent.set_comstatus(Localization.l("main_sending_error_uploading_wav_file"));
							if(b_use_indicators)
								set_filestatus(i, INDICATOR_RED_);
							return false;
						}
						if(b_use_indicators)
							set_filestatus(i, INDICATOR_GREEN_);
                        parent.set_comstatus(Localization.l("main_sending_uploading_file_complete") + " - " + parent.get_files()[i].get_soundfile().get_name());
					}
				} catch(Exception e) {
					log.debug(e.getMessage());
					log.warn(e.getMessage(), e);
					Error.getError().addError("Sending_Send","Exception in upload_wavfiles",e,1);
					return false;
				}
			}
		}
		return true;
	}
	private boolean send() {
		//upload wav files
		if(!upload_wavfiles(true))
		{
			JOptionPane.showMessageDialog(this, Localization.l("main_sending_error_uploading_wav_file"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}

		//Serverside Script - rename wav files and upload send-data, return new refno
		set_adrfilestatus(INDICATOR_YELLOW_);
		if(send_adrfile()) {
            parent.set_comstatus(Localization.l("main_sending_message_sent"));
			set_adrfilestatus(INDICATOR_GREEN_);
			return true;
		}
		else {
            parent.set_comstatus(Localization.l("main_sending_error_uploading_address_file"));
			set_adrfilestatus(INDICATOR_RED_);
		}
		return false;
		
	}
	private boolean send_test(ArrayList<String> arr_numbers) {
		if(upload_wavfiles(false)) {
			if(send_testadrfile(arr_numbers)) {
                parent.set_comstatus(Localization.l("main_sending_message_sent") + " " + get_refno());
				return true;
			}
		}
		return false;
	}

	private boolean wait_for_refno() {
		Timeout timer = new Timeout(30, 200);
		while(m_n_refno < 0 && !timer.timer_exceeded()) {
			try {
				Thread.sleep(timer.get_msec_interval());
			} catch(InterruptedException e) {
				Error.getError().addError("Sending_Send","Exception in wait_for_refno",e,1);
			}
			timer.inc_timer();
		}
		if(timer.timer_exceeded()) 
			return false;
		return true;
	}
	protected boolean retrieve_refno(boolean b_indicator, String sz_callback) {
        parent.set_comstatus(Localization.l("main_sending_refno_retrieving"));
		boolean b = false;
		if(b_indicator)
			set_refnostatus(INDICATOR_YELLOW_);
		try {
			/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_get_refno.asp");
			XMLRefno refno = new XMLRefno(form, Thread.NORM_PRIORITY, "PAS_get_refno.asp", this, sz_callback);
			refno.start();
			b = wait_for_refno();*/
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/PAS.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			ULOGONINFO logon = new ULOGONINFO();
			no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
			logon.setLComppk(ui.get_comppk());
			logon.setLDeptpk(ui.get_current_department().get_deptpk());
			logon.setLUserpk(Long.parseLong(ui.get_userpk()));
			logon.setSzCompid(ui.get_compid());
			logon.setSzDeptid(ui.get_current_department().get_deptid());
			logon.setSzUserid(ui.get_userid());
			logon.setLDeptpri(ui.get_current_department().get_deptpri());
			logon.setSzPassword(ui.get_passwd());
			logon.setSessionid(ui.get_sessionid());

			try
			{
				m_n_refno = (int)new Pasws(wsdl, service).getPaswsSoap12().getRefno(logon);
				b = true;
				ActionEvent e = new ActionEvent(new Integer(m_n_refno), ActionEvent.ACTION_PERFORMED, sz_callback);
				//ActionEvent e = new ActionEvent(new Integer(m_n_refno), ActionEvent.ACTION_PERFORMED, "act_set_refno");
				actionPerformed(e);
			}
			catch(Exception e)
			{
				log.warn(e.getMessage(), e);
                parent.set_comstatus(Localization.l("main_sending_error_retrieving_refno"));
			}
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("Sending_Send","Exception in retrieve_refno",e,1);
		}
		return b;
	}
	private boolean send_adrfile() {
		parent.set_values();
		return parent.get_sendobject().get_sendproperties().send(get_refno());
	}
	private boolean send_testadrfile(ArrayList<String> arr_numbers) {
		parent.set_values();
		return parent.get_sendobject().get_sendproperties().send_test(get_refno(), arr_numbers,sendTestType,((ReshedProfile)schedProfileCombo.getSelectedItem()).getVal());
	}
}