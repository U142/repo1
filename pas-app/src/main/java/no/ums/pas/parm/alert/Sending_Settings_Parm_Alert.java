package no.ums.pas.parm.alert;

import com.google.common.base.Supplier;
import no.ums.pas.PAS;
import no.ums.pas.send.BBProfile;
import no.ums.pas.send.BBSchedProfile;
import no.ums.pas.send.OADC;
import no.ums.pas.send.SchedDateTime;
import no.ums.pas.send.TTSLang;
import no.ums.pas.send.sendpanels.Sending_Settings;
import no.ums.pas.send.sendpanels.ShowProfileAction;
import no.ums.pas.sound.SoundlibFile;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;



/*
 * download : profiles, oadc and schedprofiles
 */
public class Sending_Settings_Parm_Alert extends Sending_Settings {
	public static final long serialVersionUID = 1;
	
	private AlertWindow alertparent = null;
	public AlertWindow get_alert_parent() { return alertparent; }
	public BBProfile get_current_profile() { return m_current_profile; }
	public BBSchedProfile get_current_schedprofile() { return m_current_schedprofile; }
	public JTextField get_sending_name() { return m_txt_sendname; } 
	private StdTextLabel m_lbl_validity_days;

	public Sending_Settings_Parm_Alert(AlertWindow parentwin) {
		super(PAS.get_pas());
		alertparent = parentwin;
		parent = parentwin;
		m_scheddatetime = new SchedDateTime();
		m_combo_profiles = new JComboBox();
		m_combo_profiles.setPreferredSize(new Dimension(200, 20));
		m_combo_profiles.setActionCommand("act_profile_changed");
		m_btn_showprofile = new JButton(new ShowProfileAction(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return m_current_profile.get_profilepk();
            }
        }));
		m_combo_oadc = new JComboBox();
		m_combo_oadc.setPreferredSize(new Dimension(200, 20));
		m_combo_oadc.addActionListener(this);
		m_combo_oadc.setActionCommand("act_oadc_changed");
		m_combo_schedprofiles = new JComboBox();
		m_combo_schedprofiles.setPreferredSize(new Dimension(200, 20));
		m_combo_schedprofiles.addActionListener(this);
		m_combo_schedprofiles.setActionCommand("act_schedprofile_changed");
		m_lbl_sendname = new StdTextLabel(PAS.l("main_parm_alert_dlg_name") + ":");
		int common_width = 150;
		m_lbl_sendname.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_profiles.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_schedprofiles.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_oadc.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_validity.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_maxchannels.setPreferredSize(new Dimension(common_width, 20));
		//m_txt_sendname = new StdTextArea(get_parent().get_sendobject().get_sendproperties().get_sendingname(), false);
		m_txt_sendname = new StdTextArea("", false);
		m_txt_sendname.setPreferredSize(new Dimension(200, 20));
		m_combo_validity.setPreferredSize(new Dimension(50, 20));
		m_lbl_validity_days = new StdTextLabel("Day(s)");
		m_radio_sendnow.addActionListener(this);
		m_radio_sched.addActionListener(this);
		
		m_radio_sendnow.addActionListener(this);
		m_radio_sched.addActionListener(this);
		/*m_radio_requesttype_0.addActionListener(this);
		m_radio_requesttype_1.addActionListener(this);
		btn_group_requesttype.add(m_radio_requesttype_0);
		btn_group_requesttype.add(m_radio_requesttype_1);*/
		
		add_controls();
		init_common();
		add_common_controls();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}
	public void toggleVoiceSettings(boolean enable) {
		m_lbl_profiles.setVisible(enable);
		m_combo_profiles.setVisible(enable);
		m_lbl_schedprofiles.setVisible(enable);
		m_combo_schedprofiles.setVisible(enable);
		m_btn_showprofile.setVisible(enable);
		m_lbl_oadc.setVisible(enable);
		m_combo_oadc.setVisible(enable);
		m_lbl_validity.setVisible(enable);
		m_combo_validity.setVisible(enable);
		m_lbl_validity_days.setVisible(enable);
		m_lbl_maxchannels.setVisible(enable);
		m_slider_maxchannels.setVisible(enable);
		m_channels_minus.setVisible(enable);
		m_channels_plus.setVisible(enable);
		m_lbl_showmaxchannels.setVisible(enable);
	}
	public void add_controls() {
		int n_width = 10;
		//int n_addrtypes = parent..get_addresstypes(); 
		
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_sendname, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
		add(m_txt_sendname, m_gridconst);
		
		
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_profiles, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
		add(m_combo_profiles, m_gridconst);
		set_gridconst(9, get_panel(), 3, 1, GridBagConstraints.WEST);
		add(m_btn_showprofile, m_gridconst);
		
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_schedprofiles, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
		add(m_combo_schedprofiles, m_gridconst);

		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_oadc, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
		add(m_combo_oadc, m_gridconst);
		
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_validity, m_gridconst);
		set_gridconst(5, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_combo_validity, m_gridconst);
		set_gridconst(6, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_lbl_validity_days, m_gridconst);

		add_spacing(DIR_VERTICAL, 10);
		/*set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_requesttype, m_gridconst);
		set_gridconst(5, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_radio_requesttype_0, m_gridconst);
		set_gridconst(6, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_radio_requesttype_1, m_gridconst);
		*/
		init();
	}
	public void init() {
		setVisible(true);
	}
	protected void add_common_controls()
	{
		int n_width = 10;
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_maxchannels, m_gridconst);
		set_gridconst(5, get_panel(), 4, 1, GridBagConstraints.WEST);
		add(m_slider_maxchannels, m_gridconst);
		set_gridconst(9, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_channels_minus, m_gridconst);
		set_gridconst(10, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_channels_plus, m_gridconst);
		set_gridconst(11, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_lbl_showmaxchannels, m_gridconst);
		
	}
	public void actionPerformed(ActionEvent e) {
		if("act_settings_loaded".equals(e.getActionCommand())) {
			get_parent().set_comstatus(PAS.l("main_sending_settings_dl_complete"));
			populate_controls();
			parent.actionPerformed(e);
			//set_selected_oadc(PAS.get_pas().get_userinfo().get_current_department().get_defaultnumber());
			m_combo_profiles.addActionListener(this);			
		} else if("act_profile_changed".equals(e.getActionCommand())) {
			m_current_profile = (BBProfile)m_combo_profiles.getSelectedItem();
			//get_pas().add_event("selected profile: " + m_current_profile.get_profilename());
			String n_default_sched = m_current_profile.get_reschedpk();
			BBSchedProfile sched = find_schedprofile(n_default_sched);
			if(sched!=null)
				m_combo_schedprofiles.setSelectedItem(sched);
			parent.actionPerformed(e);
			try
			{
				m_combo_profiles.setToolTipText(m_current_profile.get_profilename() + " (" + m_current_profile.get_soundfiles().size() + ")");
			}
			catch(Exception err)
			{
				
			}
			//PAS.get_pas().add_event("Opening " + PAS.get_pas().get_sitename());
		} else if("act_oadc_changed".equals(e.getActionCommand())) {
			m_current_oadc = (OADC)m_combo_oadc.getSelectedItem();
		} else if("act_schedprofile_changed".equals(e.getActionCommand())) {
			m_current_schedprofile = (BBSchedProfile)m_combo_schedprofiles.getSelectedItem();
			parent.actionPerformed(e);
		} /*else if("act_requesttype_0".equals(e.getActionCommand())) {
			m_n_requesttype = 0;
		} else if("act_requesttype_1".equals(e.getActionCommand())) {
			m_n_requesttype = 1;
		}*/ else if("inc_channels".equals(e.getActionCommand())) {
			m_slider_maxchannels.setValue(m_slider_maxchannels.getValue() + 1);
		} else if("dec_channels".equals(e.getActionCommand())) {
			m_slider_maxchannels.setValue(m_slider_maxchannels.getValue()-1);
		}

		
	}
	public void enable_sched(boolean b_enable) {
		m_combo_scheddate.setEnabled(b_enable);
		m_combo_schedtimehour.setEnabled(b_enable);
		m_combo_schedtimeminute.setEnabled(b_enable);
		if(b_enable) {
			actionPerformed(new ActionEvent(m_combo_scheddate, ActionEvent.ACTION_PERFORMED, "act_scheddate_changed"));
			actionPerformed(new ActionEvent(m_combo_schedtimehour, ActionEvent.ACTION_PERFORMED, "act_schedhour_changed"));
			actionPerformed(new ActionEvent(m_combo_schedtimeminute, ActionEvent.ACTION_PERFORMED, "act_schedminute_changed"));			
		}
		m_b_use_scheddatetime = b_enable;
	}
	public BBSchedProfile find_schedprofile(String n_profilepk) {
		BBSchedProfile ret = null;
		for(int i=0; i < m_combo_schedprofiles.getItemCount(); i++) {
			ret = (BBSchedProfile)m_combo_schedprofiles.getItemAt(i);
			if(ret.get_reschedpk().equals(n_profilepk))
				break;
			else
				ret = null;
		}
		return ret;
	}
	private void populate_controls() {
		populate_ttslang();
		populate_profiles();
		populate_oadc();
		populate_schedprofiles();
		populate_txtlib();
		populate_wavlib();
		populate_smstemplates();
		set_default_oadc();
	}
	private void populate_txtlib() {
		ArrayList<SoundlibFile> txtlib = get_parent().get_settingsloader().get_txtlib();
		this.get_parent().set_txtlib(txtlib);
	}
	private void populate_wavlib() {
		ArrayList<SoundlibFile> wavlib = get_parent().get_settingsloader().get_soundlib();
		this.get_parent().set_soundlib(wavlib);
	}
	private void populate_profiles() {
		ArrayList<BBProfile> profiles = get_parent().get_settingsloader().get_profiles();
		for(int i=0; i < profiles.size(); i++) {
			BBProfile current = (BBProfile)profiles.get(i);
			if(i==0)
				m_current_profile = current;
			m_combo_profiles.addItem(current);
		}
	}
	
	private void populate_schedprofiles() {
		ArrayList<BBSchedProfile> schedprofiles = get_parent().get_settingsloader().get_schedprofiles();
		for(int i=0; i < schedprofiles.size(); i++) {
			BBSchedProfile sched = (BBSchedProfile)schedprofiles.get(i);
			if(i==0)
				m_current_schedprofile = sched;
			m_combo_schedprofiles.addItem(sched);
		}
	}
	private void populate_ttslang() {
		ArrayList<TTSLang> ttslang = get_parent().get_settingsloader().get_ttslang();
		this.get_parent().set_tts(ttslang);
	}
	public void enableInput(boolean val) {
		m_txt_sendname.setEnabled(val);
		m_combo_profiles.setEnabled(val);
		m_combo_oadc.setEnabled(val);
		//m_combo_scheddate.setEnabled(val);
		m_combo_schedprofiles.setEnabled(val);
		//m_combo_schedtimehour.setEnabled(val);
		//m_combo_schedtimeminute.setEnabled(val);
		m_combo_validity.setEnabled(val);
		m_channels_plus.setEnabled(val);
		m_channels_minus.setEnabled(val);
		m_slider_maxchannels.setEnabled(val);
		
	}

}