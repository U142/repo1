package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.send.*;
import no.ums.pas.sound.SoundlibFile;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.OpenBrowser;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.calendarutils.SchedCalendar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/*
 * download : profiles, oadc and schedprofiles
 */
public class Sending_Settings extends DefaultPanel implements KeyListener {
	public static final long serialVersionUID = 1;

	protected SendWindow parent = null;
	public SendWindow get_parent() { return parent; }
	protected StdTextLabel m_lbl_profiles = new StdTextLabel(PAS.l("main_sending_settings_msg_profile") + ":");
	protected StdTextLabel m_lbl_schedprofiles = new StdTextLabel(PAS.l("main_sending_settings_config_profile") + ":");
	protected StdTextLabel m_lbl_oadc = new StdTextLabel(PAS.l("main_sending_settings_origin_number") + ":");
	protected StdTextLabel m_lbl_validity = new StdTextLabel(PAS.l("main_sending_settings_validity"));
	protected JComboBox m_combo_validity = new JComboBox(new String [] { "1", "2", "3", "4", "5", "6", "7" } );
	protected StdTextLabel m_lbl_sendname;
	protected StdTextArea m_txt_sendname;
	protected StdTextLabel m_lbl_scheddate = new StdTextLabel(PAS.l("common_date") + "/" + PAS.l("common_time") + ":");
	protected JRadioButton m_radio_sendnow = new JRadioButton(PAS.l("main_sending_settings_schedule_now"), true);
	protected JRadioButton m_radio_sched = new JRadioButton(PAS.l("main_sending_settings_schedule_later"));
	
	protected StdTextLabel m_lbl_requesttype = new StdTextLabel(PAS.l("main_status_locationbased_alert_short"));
	protected JRadioButton m_radio_requesttype_0 = new JRadioButton(PAS.l("main_sending_settings_lba_send_now"), true);
	protected JRadioButton m_radio_requesttype_1 = new JRadioButton(PAS.l("main_sending_settings_lba_send_confirm"));
	protected ButtonGroup btn_group_requesttype = new ButtonGroup();
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource().equals(m_txt_sendname))
		{
			parent.setWindowTitle(m_txt_sendname.getText());
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	protected JSlider m_slider_maxchannels = new JSlider();
	protected JButton m_channels_plus = new JButton();
	protected JButton m_channels_minus = new JButton();
	
	protected StdTextLabel m_lbl_maxchannels = new StdTextLabel(PAS.l("main_sending_settings_max_voice_channels") + ":");
	protected StdTextLabel m_lbl_showmaxchannels = new StdTextLabel("");
	protected int m_n_maxchannels = 0;
	protected int m_n_requesttype = 0;
	
	public int getMaxChannels() { return m_n_maxchannels; }
	//public int getRequestType() { return m_n_requesttype; }
	public void setMaxChannels(int n) { 
		m_n_maxchannels = n;
		m_slider_maxchannels.setValue(n);
	}
	public void setRequestType(int n) { 
		m_n_requesttype = n;
		switch(m_n_requesttype)
		{
		case 0:
			m_radio_requesttype_0.doClick();
			break;
		case 1:
			m_radio_requesttype_1.doClick();
			break;
		}
		
	}
	
	protected JButton m_btn_showprofile;
	
	protected ButtonGroup m_btngroup_sched = new ButtonGroup();
	protected JComboBox m_combo_scheddate;
	protected JComboBox m_combo_schedtimehour;
	protected JComboBox m_combo_schedtimeminute;
	protected JComboBox m_combo_profiles;
	protected JComboBox m_combo_oadc;
	protected JComboBox m_combo_schedprofiles;
	protected BBProfile m_current_profile = null;
	protected BBSchedProfile m_current_schedprofile = null;
	protected OADC m_current_oadc = null;
	public BBProfile get_current_profile() { return m_current_profile; }
	public BBSchedProfile get_current_schedprofile() { return m_current_schedprofile; }
	public OADC get_current_oadc() { return m_current_oadc; }
	public String get_sendingname() { return m_txt_sendname.getText(); }
	public int get_current_validity() { return new Integer((String)m_combo_validity.getSelectedItem()).intValue(); }
	protected SchedDateTime m_scheddatetime = null;
	protected boolean m_b_use_scheddatetime = false;
	public boolean get_use_schedddatetime() { return m_b_use_scheddatetime; }
	public SchedDateTime get_scheddatetime() {
		if(get_use_schedddatetime())
			return m_scheddatetime;
		else
			return new SchedDateTime();
	}
	
	public Sending_Settings(PAS pas){
		super();
		m_radio_sendnow.setOpaque(false);
		m_radio_sched.setOpaque(false);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}
	
	
	public Sending_Settings(PAS pas, SendWindow parentwin) {
		super();
		parent = parentwin;
		m_scheddatetime = new SchedDateTime();
		m_combo_profiles = new JComboBox();
		m_combo_profiles.setPreferredSize(new Dimension(200, 20));
		m_combo_profiles.addActionListener(this);
		m_combo_profiles.setActionCommand("act_profile_changed");
		m_btn_showprofile = new JButton(PAS.l("main_sending_settings_show_msg_profile"));
		m_btn_showprofile.addActionListener(this);
		m_btn_showprofile.setActionCommand("act_profile_show");
		m_combo_oadc = new JComboBox();
		m_combo_oadc.setPreferredSize(new Dimension(200, 20));
		m_combo_oadc.addActionListener(this);
		m_combo_oadc.setActionCommand("act_oadc_changed");
		m_combo_schedprofiles = new JComboBox();
		m_combo_schedprofiles.setPreferredSize(new Dimension(200, 20));
		m_combo_schedprofiles.addActionListener(this);
		m_combo_schedprofiles.setActionCommand("act_schedprofile_changed");
		m_lbl_sendname = new StdTextLabel(PAS.l("common_sendingname") + ":");
		int common_width = 170;
		m_lbl_sendname.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_profiles.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_schedprofiles.setPreferredSize(new Dimension(common_width, 20));
		m_lbl_maxchannels.setPreferredSize(new Dimension(common_width, 20));
		m_txt_sendname = new StdTextArea(get_parent().get_sendobject().get_sendproperties().get_sendingname(), false);
		m_txt_sendname.setPreferredSize(new Dimension(200, 20));
		m_txt_sendname.addKeyListener(this);
		m_combo_validity.setPreferredSize(new Dimension(50, 20));
		m_btngroup_sched.add(m_radio_sendnow);
		m_btngroup_sched.add(m_radio_sched);
		SchedCalendar sched = new SchedCalendar(8);
		m_combo_scheddate = new JComboBox(sched.get_days());
		m_combo_schedtimehour = new JComboBox(sched.get_hours());
		m_combo_schedtimeminute = new JComboBox(sched.get_minutes());
		m_combo_scheddate.setPreferredSize(new Dimension(100, 16));
		m_combo_schedtimehour.setPreferredSize(new Dimension(40, 16));
		m_combo_schedtimeminute.setPreferredSize(new Dimension(40, 16));
		enable_sched(false);
		m_combo_scheddate.setActionCommand("act_scheddate_changed");
		m_combo_schedtimehour.setActionCommand("act_schedhour_changed");
		m_combo_schedtimeminute.setActionCommand("act_schedminute_changed");
		m_combo_scheddate.addActionListener(this);
		m_combo_schedtimehour.addActionListener(this);
		m_combo_schedtimeminute.addActionListener(this);
		m_radio_sendnow.setActionCommand("act_sched_disable");
		m_radio_sched.setActionCommand("act_sched_enable");
		m_radio_sendnow.addActionListener(this);
		m_radio_sched.addActionListener(this);
		
		
		add_controls();
		init_common();
 		if(parent.hasVoice(parent.m_sendobject.get_toolbar().get_addresstypes()))
			add_common_controls();
		
		
	}
	
	protected void init_common()
	{
		m_slider_maxchannels.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider)e.getSource();
				Integer i = new Integer(slider.getValue());
				m_lbl_showmaxchannels.setText(i.toString());
				m_n_maxchannels = i.intValue();
				if(parent.m_sendobject.get_sendproperties() != null && parent.m_sendobject.get_sendproperties().get_maxchannels() == 1 && parent.m_sendobject.get_sendproperties().get_maxchannels() == PAS.get_pas().get_userinfo().get_current_department().get_maxalloc())
					parent.m_sendobject.get_sendproperties().set_maxchannels(i.intValue());
			}
			
		});
		m_slider_maxchannels.setBackground(new Color(255,255,255, Color.TRANSLUCENT));
		m_slider_maxchannels.setOpaque(false);
		m_slider_maxchannels.setMinimum(1);
		
		int maxalloc;
		//if(m_n_maxchannels>0)
		//	maxalloc = m_n_maxchannels;
		//else
		maxalloc = PAS.get_pas().get_userinfo().get_current_department().get_maxalloc();
		m_slider_maxchannels.setMaximum(maxalloc);
		m_slider_maxchannels.setValue(maxalloc);
		
		if(parent.m_sendobject.get_sendproperties() != null)
			m_slider_maxchannels.setValue(parent.m_sendobject.get_sendproperties().get_maxchannels());
		
		/*
		m_radio_requesttype_0.addActionListener(this);
		m_radio_requesttype_1.addActionListener(this);
		m_radio_requesttype_0.setActionCommand("act_requesttype_0");
		m_radio_requesttype_1.setActionCommand("act_requesttype_1");
		btn_group_requesttype.add(m_radio_requesttype_0);
		btn_group_requesttype.add(m_radio_requesttype_1);
		setRequestType(m_n_requesttype);
		*/
		
		m_channels_plus = new JButton("+");
		m_channels_minus = new JButton("-");
		m_channels_plus.addActionListener(this);
		m_channels_minus.addActionListener(this);
		m_channels_plus.setActionCommand("inc_channels");
		m_channels_minus.setActionCommand("dec_channels");
		m_channels_minus.setPreferredSize(new Dimension(46,22));
		m_channels_plus.setPreferredSize(new Dimension(46,22));
	}
	protected void add_common_controls()
	{
		int n_width = 10;
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_maxchannels, m_gridconst);
		set_gridconst(5, get_panel(), 4, 1, GridBagConstraints.WEST);
		add(m_slider_maxchannels, m_gridconst);
		set_gridconst(10, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_channels_minus, m_gridconst);
		set_gridconst(11, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_channels_plus, m_gridconst);
		set_gridconst(12, get_panel(), 1, 1, GridBagConstraints.WEST);
		add(m_lbl_showmaxchannels, m_gridconst);
		
	}
	
	public void add_controls() {
		int n_width = 10;
		int n_addrtypes = parent.m_sendobject.get_toolbar().get_addresstypes();
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_lbl_sendname, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
		add(m_txt_sendname, m_gridconst);
		
		// if only cell broadcast is selected then don't show these fields
		System.out.println("Addresstypes: " + parent.m_sendobject.get_toolbar().get_addresstypes());
		if(parent.m_sendobject.get_toolbar().get_addresstypes() != 256) { // Her må jeg finne Statiske for BTN_CELLBROADCAST
			if(parent.hasVoice(n_addrtypes)) {
				set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
				add(m_lbl_profiles, m_gridconst);
				set_gridconst(5, get_panel(),5, 1, GridBagConstraints.WEST);
				add(m_combo_profiles, m_gridconst);
				set_gridconst(9, get_panel(), 4, 1, GridBagConstraints.WEST);
				add(m_btn_showprofile, m_gridconst);
			
				set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
				add(m_lbl_schedprofiles, m_gridconst);
				set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
				add(m_combo_schedprofiles, m_gridconst);
			
				set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
				add(m_lbl_oadc, m_gridconst);
				set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
				add(m_combo_oadc, m_gridconst);
			}
		}
		//
		if(parent.hasVoice(n_addrtypes)) {
			set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
			add(m_lbl_validity, m_gridconst);
			set_gridconst(5, get_panel(), 2, 1, GridBagConstraints.WEST);
			add(m_combo_validity, m_gridconst);
			set_gridconst(7, get_panel(), 3, 1, GridBagConstraints.WEST);
			add(new StdTextLabel(PAS.l("common_days")), m_gridconst);
		
			set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
			add(m_radio_sendnow, m_gridconst);
			set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
			add(m_radio_sched, m_gridconst);
			
			set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
			add(m_lbl_scheddate, m_gridconst);
			set_gridconst(5, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_scheddate, m_gridconst);
			set_gridconst(6, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_schedtimehour, m_gridconst);
			//set_gridconst(7, get_panel(), 1, 1, GridBagConstraints.WEST);
			//add(new StdTextArea(":"), m_gridconst);
			set_gridconst(8, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_schedtimeminute, m_gridconst);
		}
		else if(parent.hasSMS(n_addrtypes)) {
			set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
			add(m_radio_sendnow, m_gridconst);
			set_gridconst(5, get_panel(), 5, 1, GridBagConstraints.WEST);
			add(m_radio_sched, m_gridconst);

			set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
			add(m_lbl_scheddate, m_gridconst);
			set_gridconst(5, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_scheddate, m_gridconst);
			set_gridconst(6, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_schedtimehour, m_gridconst);
			//set_gridconst(7, get_panel(), 1, 1, GridBagConstraints.WEST);
			//add(new StdTextArea(":"), m_gridconst);
			set_gridconst(8, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_schedtimeminute, m_gridconst);
			
		}

		init();
	}
	public void init() {
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if("act_settings_loaded".equals(e.getActionCommand())) {
			get_parent().set_comstatus(PAS.l("main_sending_settings_dl_complete"));
			populate_controls();
			parent.actionPerformed(e);
			
		} else if("act_profile_changed".equals(e.getActionCommand())) {
			m_current_profile = (BBProfile)m_combo_profiles.getSelectedItem();
			System.out.println("Profile changed to: " + m_current_profile.toString());
			get_parent().initialize_file_panes();
			//get_pas().add_event("selected profile: " + m_current_profile.get_profilename());
			String n_default_sched = m_current_profile.get_reschedpk();
			BBSchedProfile sched = null;
			try
			{
				sched = find_schedprofile(m_current_schedprofile.get_reschedpk());
			}
			catch(Exception err) {	Error.getError().addError("Sending_Settings","Exception trying to set Schedule Profile",err,1);	}
			
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
		} else if("act_profile_show".equals(e.getActionCommand()) && get_current_profile() != null) {
			try {
				OpenBrowser.browse(PAS.get_pas().VB4_URL + "/PAS_msg_profile_dlg.asp?f_setreadonly=True&lProfilePk="+m_current_profile.get_profilepk()+"&l_deptpk="+PAS.get_pas().get_userinfo().get_default_dept().get_deptpk()+"&usr="+PAS.get_pas().get_userinfo().get_userid()+"&cmp="+PAS.get_pas().get_userinfo().get_compid()+"&pas="+PAS.get_pas().get_userinfo().get_passwd());
				//new OpenBrowser().showDocument(new java.net.URL(PAS.get_pas().VB4_URL + "PAS_msg_profile_dlg.asp?f_setreadonly=True&lProfilePk="+m_current_profile.get_profilepk()+"&l_deptpk="+PAS.get_pas().get_userinfo().get_default_dept().get_deptpk()+"&usr="+PAS.get_pas().get_userinfo().get_userid()+"&cmp="+PAS.get_pas().get_userinfo().get_compid()+"&pas="+PAS.get_pas().get_userinfo().get_passwd()));
			} catch(Exception err) {
				javax.swing.JOptionPane.showMessageDialog(this, "Error opening web browser", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
				Error.getError().addError("Sending_Settings","Exception in actionPerformed",err,1);
			}
		} else if("act_oadc_changed".equals(e.getActionCommand())) {
			m_current_oadc = (OADC)m_combo_oadc.getSelectedItem();
		} else if("act_schedprofile_changed".equals(e.getActionCommand())) {
			m_current_schedprofile = (BBSchedProfile)m_combo_schedprofiles.getSelectedItem();
		} else if("act_scheddate_changed".equals(e.getActionCommand())) {
			int n_new_date = ((SchedCalendar.Day)m_combo_scheddate.getItemAt(m_combo_scheddate.getSelectedIndex())).get_date();
			m_scheddatetime.set_date(n_new_date);
		} else if("act_schedhour_changed".equals(e.getActionCommand())) {
			int n_new_hour = new Integer(m_combo_schedtimehour.getSelectedItem().toString()).intValue();
			m_scheddatetime.set_hour(n_new_hour);
		} else if("act_schedminute_changed".equals(e.getActionCommand())) {
			int n_new_minute = new Integer(m_combo_schedtimeminute.getSelectedItem().toString()).intValue();
			m_scheddatetime.set_minute(n_new_minute);
		} else if("act_sched_disable".equals(e.getActionCommand())) {
			enable_sched(false);
		} else if("act_sched_enable".equals(e.getActionCommand())) {
			enable_sched(true);
		} /*else if("act_requesttype_0".equals(e.getActionCommand())) {
			m_n_requesttype = 0;
		} else if("act_requesttype_1".equals(e.getActionCommand())) {
			m_n_requesttype = 1;
		}*/ else if("inc_channels".equals(e.getActionCommand())) {
			m_slider_maxchannels.setValue(m_slider_maxchannels.getValue()+1);
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
		populate_txtlib();
		populate_wavlib();
		populate_ttslang();
		populate_profiles();
		populate_oadc();
		populate_schedprofiles();
		populate_smstemplates();
		set_default_oadc();
	}
	
	protected void set_default_oadc() {
		String number;
		String test;
		for(int i=0;i<m_combo_oadc.getItemCount();++i) {
			number = m_combo_oadc.getItemAt(i).toString().substring(0,m_combo_oadc.getItemAt(i).toString().indexOf(" "));
			//number = m_combo_oadc.getItemAt(i).toString().trim();
			//test = PAS.get_pas().get_userinfo().get_current_department().get_defaultnumber().trim();
			if(number.equals(PAS.get_pas().get_userinfo().get_current_department().get_defaultnumber().trim())) {
				m_combo_oadc.setSelectedIndex(i);
				return;
			}
		}
		if(m_combo_oadc.getItemCount()>0)
			m_combo_oadc.setSelectedIndex(0);
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
		final ArrayList<BBProfile> profiles = get_parent().get_settingsloader().get_profiles();
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					for(int i=0; i < profiles.size(); i++) {
						BBProfile current = profiles.get(i);
						m_combo_profiles.addItem(current);
					}
					//System.out.println("Trying to set profile: " + parent.m_sendobject.get_sendproperties().get_profilepk());
					if(parent.m_sendobject != null && parent.m_sendobject.get_sendproperties() != null && parent.m_sendobject.get_sendproperties().get_profilepk() > -1) {
						BBProfile selectedProfile = null;
						//System.out.println("At least I'm inside");
						BBProfile temp = null;
						int profilepk;
						for(int i=0;i<m_combo_profiles.getItemCount();++i) {
							temp = (BBProfile)m_combo_profiles.getItemAt(i);
							profilepk = ((BBProfile)m_combo_profiles.getItemAt(i)).get_profilepk();
							if(profilepk == parent.m_sendobject.get_sendproperties().get_profilepk())
								selectedProfile = (BBProfile)m_combo_profiles.getItemAt(i);
						}
						if(selectedProfile != null) {
							m_combo_profiles.setSelectedItem(selectedProfile);
							//System.out.println("Profile set: " + selectedProfile.toString());
						}
						//System.out.println("The profile should now be: " + m_combo_profiles.getSelectedItem().toString());
					}
				}
			});
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	protected void populate_smstemplates() {
		ArrayList<SoundlibFile> smstemp = get_parent().get_settingsloader().get_smstemplatelib();
		this.get_parent().set_smstemplates(smstemp);
		this.get_parent().get_sms_broadcast_text().addComboTemplatesActionListener();
	}
	protected void populate_oadc() {
		ArrayList<OADC> oadcs = get_parent().get_settingsloader().get_oadcnumbers();
		if(oadcs==null)
			return;
		for(int i=0; i < oadcs.size(); i++) {
			OADC oadc = (OADC)oadcs.get(i);
			if(i==0)
				m_current_oadc = oadc;
			m_combo_oadc.addItem(oadc);
		}
	}
	private void populate_schedprofiles() {
		ArrayList<BBSchedProfile> schedprofiles = get_parent().get_settingsloader().get_schedprofiles();
		for(int i=0; i < schedprofiles.size(); i++) {
			BBSchedProfile sched = (BBSchedProfile)schedprofiles.get(i);
			m_combo_schedprofiles.addItem(sched);
		}
	}
	private void populate_ttslang() {
		ArrayList<TTSLang> ttslang = get_parent().get_settingsloader().get_ttslang();
		this.get_parent().set_tts(ttslang);
	}
	
	private int findProfile(int n_profilepk) {
		for(int i=0; i < m_combo_profiles.getItemCount(); i++) {
			BBProfile p = (BBProfile)m_combo_profiles.getItemAt(i);
			int pro = p.get_profilepk();
			if(pro==n_profilepk)
				return i;
		}
		return -1;
	}
	private int findSchedProfile(String sz_schedprofile) {
		for(int i=0; i < m_combo_schedprofiles.getItemCount(); i++) {
			BBSchedProfile p = (BBSchedProfile)m_combo_schedprofiles.getItemAt(i);
			if(p.get_reschedpk().equals(sz_schedprofile))
				return i;
		}
		return -1;
	}
	private int findOadc(String sz_number) {
		for(int i=0; i < m_combo_oadc.getItemCount(); i++) {
			OADC o = (OADC)m_combo_oadc.getItemAt(i);
			if(o.get_number().equals(sz_number)) {
				return i;
			}
		}
		return -1;
	}
	private int findValidity(int n_days) {
		for(int i=0; i < m_combo_validity.getItemCount(); i++) {
			Integer d = new Integer((String)m_combo_validity.getItemAt(i));
			if(d.intValue()==n_days) {
				return i;
			}
		}
		return -1;
	}
	public int set_selected_profile(int n_profilepk) {
		try {
			int i = findProfile(n_profilepk);
			if(i>=0) {
				m_combo_profiles.setSelectedIndex(i);
				actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_profile_changed"));
				return i;
			} else {
				m_combo_profiles.setSelectedIndex(0);
				actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_profile_changed"));
				return 0;
			}
		} catch(Exception e) {
			//Error.getError().addError("Alert profile not found", "Could not find the loaded profile in profilelist", e, Error.SEVERITY_ERROR);
			return -1;
		}
	}
	public void set_selected_schedprofile(String sz_profilepk) {
		try {
			m_combo_schedprofiles.setSelectedIndex(findSchedProfile(sz_profilepk));
		} catch(Exception e) {
			Error.getError().addError("Schedule profile not found", "Could not find the loaded schedule profile in list", e, Error.SEVERITY_ERROR);
		}
	}
	public void set_selected_oadc(String sz_number) {
		try {
			m_combo_oadc.setSelectedIndex(findOadc(sz_number));
		} catch(Exception e) {
			Error.getError().addError("Origin number not found", "Could not find the loaded origin number in list", e, Error.SEVERITY_ERROR);
		}
	}
	public void set_selected_validity(int n_days) {
		try {
			m_combo_validity.setSelectedIndex(findValidity(n_days));
		} catch(Exception e) {
			Error.getError().addError("Message validity not found", "The loaded validity is not eligible", e, Error.SEVERITY_ERROR);
		}
	}
	public void set_name(String sz_name){
		m_txt_sendname.setText(sz_name);
	}
}
