package no.ums.pas.parm.alert;

import no.ums.pas.PAS;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.sendpanels.Sending_Send;
import no.ums.pas.sound.SoundFile;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.Timeout;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/*
 * verify data and execute sending
 */
public class Sending_Send_Parm_Alert extends Sending_Send {
	public static final long serialVersionUID = 1;
		
	public int get_refno() { return m_n_refno; }
	private JCheckBox m_chk_execute_remote;
	private StdTextLabel m_txt_activate_ext;
	//JButton btn_send;
	public Sending_Send_Parm_Alert(AlertWindow parentwin) {
		super(PAS.get_pas());
		parent = parentwin;
		//btn_send = new JButton("send");
		//btn_send.addActionListener(this);
		//btn_send.setActionCommand("act_send");
		m_txt_refno = new StdTextLabel(PAS.l("common_refno"), 400, 14, true);
		m_txt_adrfile = new StdTextLabel(PAS.l("main_sending_address_file"), 400, 14, true);
		m_txt_activate_ext = new StdTextLabel(PAS.l("main_parm_alert_dlg_activate_for_ext_exec"), true, 180);
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
		m_btn_sendtest.setActionCommand("act_send_test");
		m_btn_sendtest.addActionListener(this);
		m_btn_sendtest.setPreferredSize(new Dimension(50, 16));
		m_chk_execute_remote = new JCheckBox();
		m_chk_execute_remote.setOpaque(false);
		m_chk_execute_remote.setBackground(new Color(0,0,0,Color.TRANSLUCENT));

		add_controls();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}
	
	public void add_controls() {
		this.removeAll();
		m_gridconst.ipadx = 20;
		m_gridconst.ipady = 5;
		int n_max = 6;
		
		/*set_gridconst(1, inc_panels(), 2, 1, GridBagConstraints.WEST);
		add(m_txt_sendtest, m_gridconst);
		set_gridconst(3, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_btn_sendtest, m_gridconst);
*/
		set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.CENTER);
		add(m_lbl_refno, m_gridconst);
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
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.CENTER);
		add(m_txt_activate_ext, m_gridconst);
		set_gridconst(4, get_panel(), 1, 1, GridBagConstraints.CENTER);
		add(m_chk_execute_remote, m_gridconst);
		m_lbl_refno.setVisible(false);
		m_txt_refno.setVisible(false);
		m_lbl_adrfile.setVisible(false);
		m_txt_adrfile.setVisible(false);
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
		
	}
	public void create_upload_indicators() {
		m_txt_wav_upload = new StdTextLabel[parent.get_files().length];
		m_lbl_wav_upload = new JLabel[parent.get_files().length];
		for(int i=0; i < m_txt_wav_upload.length; i++) {
			m_lbl_wav_upload[i] = new JLabel(m_icon_indicator[INDICATOR_RED_]);
			m_txt_wav_upload[i] = new StdTextLabel(PAS.l("common_file") + " - " + parent.get_files()[i].get_soundfile().get_name() + " (" + parent.get_files()[i].get_soundfile().get_modulename() + ")", 400, 14, true);
		}
	}
	public void actionPerformed(ActionEvent e) {
		if("act_send".equals(e.getActionCommand())) {
			parent.get_btn_next().setEnabled(false);
			//parent.get_sendobject().get_sendproperties().set_simulation(false);
			retrieve_refno(true, "act_set_refno");
		} else if("act_send_simulation".equals(e.getActionCommand())) {
			parent.get_btn_next().setEnabled(false);
			//parent.get_sendobject().get_sendproperties().set_simulation(true);
			retrieve_refno(true, "act_set_refno");
		} else if("act_set_refno".equals(e.getActionCommand())) { //wait for refno, then send
			m_n_refno = ((Integer)e.getSource()).intValue();
			m_txt_refno.setText(PAS.l("common_refno") + " - " + m_n_refno);
			parent.set_comstatus(PAS.l("main_sending_refno_retrieved") + " - " + m_n_refno);
			set_refnostatus(INDICATOR_GREEN_);
			if(send()) {
				parent.get_btn_next().setText(PAS.l("common_finish"));
				parent.get_btn_next().setActionCommand("act_finish");
			}
			parent.get_btn_next().setEnabled(true);
		} else if("act_send_test".equals(e.getActionCommand())) {
			String sz_number = m_txt_sendtest.getText();
			if(sz_number.length() < 8) {
				JOptionPane.showMessageDialog(this, PAS.l("main_sending_send_test_warning"));
				return;
			}
			parent.get_btn_next().setEnabled(false);
			retrieve_refno(false, "act_set_testrefno");
		} else if("act_set_testrefno".equals(e.getActionCommand())) {
			String sz_number = m_txt_sendtest.getText();
			m_n_refno = ((Integer)e.getSource()).intValue();
			parent.set_comstatus(PAS.l("main_sending_refno_test_retrieved") + " - " + m_n_refno);
			ArrayList<String> arr_numbers = new ArrayList<String>();
			//arr_numbers.add("98220213");
			arr_numbers.add(sz_number);
			/*if(send_test(arr_numbers)) {
				JOptionPane.showMessageDialog(this, "A test-message has been sent with refno " + get_refno());
			} else {
				JOptionPane.showMessageDialog(this, "Failed to send test-message with refno " + get_refno());
			}*/
			send_test(arr_numbers);
			parent.get_btn_next().setEnabled(true);
		}
	}
	private boolean upload_wavfiles(boolean b_use_indicators) {
		if(parent.get_files()!=null) {			
			for(int i=0; i < parent.get_files().length; i++) {
				try {
					parent.set_comstatus(PAS.l("main_sending_uploading_file") + " - " + parent.get_files()[i].get_soundfile().get_name());
					if(b_use_indicators)
						set_filestatus(i, INDICATOR_YELLOW_);
					SoundFile file = parent.get_files()[i].get_soundfile();
					file.set_bytebuffer(parent.get_files()[i].get_recorder().get_bytebuffer());
					//file.set_fileinfo(parent.get_files()[i].get)
					//PAS.get_pas().add_event("Ready to send wav type " + parent.get_files()[i].get_filetype() + " refno=" + get_refno());
					boolean b_ok = file.send_wav(get_refno(), parent.get_files()[i].get_filetype(), parent.get_files()[i].get_current_fileinfo());
					if(!b_ok) {
						parent.set_comstatus(PAS.l("main_sending_error_uploading_wav_file"));
						if(b_use_indicators)
							set_filestatus(i, INDICATOR_RED_);
						return false;
					}
					if(b_use_indicators)
						set_filestatus(i, INDICATOR_GREEN_);
					parent.set_comstatus(PAS.l("main_sending_uploading_file_complete") + " - " + parent.get_files()[i].get_soundfile().get_name());
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError(PAS.l("common_error"),"Sending_Send Exception in upload_wavfiles",e,1);
				}
			}
		}
		return true;
	}
	private boolean send() {
		//upload wav files
		upload_wavfiles(true);

		//Serverside Script - rename wav files and upload send-data, return new refno
		set_adrfilestatus(INDICATOR_YELLOW_);
		if(send_adrfile()) {
			parent.set_comstatus(PAS.l("main_sending_message_sent"));
			set_adrfilestatus(INDICATOR_GREEN_);
			return true;
		}
		else {
			parent.set_comstatus(PAS.l("main_sending_error_uploading_address_file"));
			set_adrfilestatus(INDICATOR_RED_);
		}
		return false;
		
	}
	private boolean send_test(ArrayList<String> arr_numbers) {
		if(upload_wavfiles(false)) {
			if(send_testadrfile(arr_numbers)) {
				parent.set_comstatus(PAS.l("main_sending_message_sent") + get_refno());
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
				Error.getError().addError(PAS.l("common_error"),"Sending_Send Exception in wait_for_refno",e,1);
			}
			timer.inc_timer();
		}
		if(timer.timer_exceeded()) 
			return false;
		return true;
	}
	/*private boolean retrieve_refno(boolean b_indicator, String sz_callback) {
		parent.set_comstatus("Retrieving refno");
		boolean b = false;
		if(b_indicator)
			set_refnostatus(INDICATOR_YELLOW_);
		try {
			//HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_get_refno.asp");
			//XMLRefno refno = new XMLRefno(form, Thread.NORM_PRIORITY, "PAS_get_refno.asp", this, sz_callback);
			WS
			refno.start();
			b = wait_for_refno();
			if(!b)
				parent.set_comstatus("Failed to retrieve refno...");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("Sending_Send","Exception in retrieve_refno",e,1);
		}
		return b;
	}*/
	private boolean send_adrfile() {
		parent.set_values();
		return parent.get_sendobject().get_sendproperties().send(get_refno());
	}
	private boolean send_testadrfile(ArrayList<String> arr_numbers) {
		parent.set_values();
		return parent.get_sendobject().get_sendproperties().send_test(get_refno(), arr_numbers);
	}
	public JCheckBox get_chk_execute_remote() {
		return m_chk_execute_remote;
	}
	
	public void enableInput(boolean val) {
		m_chk_execute_remote.setEnabled(val);
	}
	
}