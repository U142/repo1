package no.ums.pas.send.sendpanels;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.ws.WSTasResponseNumbers;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendProperties;
import no.ums.pas.sound.SoundlibFileTxt;
import no.ums.pas.ums.tools.ExpiryMins;
import no.ums.pas.ums.tools.SmsInReplyNumber;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.tas.UTASRESPONSENUMBER;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.PopupFactory;
import javax.swing.text.DefaultHighlighter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.List;

public class Sending_SMS_Broadcast_text extends Sending_Cell_Broadcast_text
{
	
    private static final Log log = UmsLog.getLogger(Sending_SMS_Broadcast_text.class);
	
	public List<UTASRESPONSENUMBER> m_responsenumbers;
	
	public Sending_SMS_Broadcast_text(PAS pas, SendWindow parentwin)
	{
		super(parentwin);
		if(PAS.get_pas().get_sendcontroller().get_activesending() != null && PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
			m_maxSize = 160;
		}
		else
			m_maxSize = 760;
		String sz_default_oadc = PAS.get_pas().get_userinfo().get_default_oadc();
		init_for_sms();
		UpdateTextFields();
		FillSMSTemplates();
		if(parentwin.get_sendobject().get_sendproperties() != null) {
			m_txt_messagetext.setText(parentwin.get_sendobject().get_sendproperties().get_sms_broadcast_message());
			if(parentwin.get_sendobject().get_sendproperties().get_sms_broadcast_oadc().trim().length()>0)
				m_txt_oadc_text.setText(parentwin.get_sendobject().get_sendproperties().get_sms_broadcast_oadc());
			else
				m_txt_oadc_text.setText(sz_default_oadc);				
		}
		else {
			//m_txt_oadc_text.setText(PAS.get_pas().get_userinfo().get_current_department().get_defaultnumber());
			m_txt_oadc_text.setText(sz_default_oadc);
		}
		addComponentListener(this);
	}
	protected void init_for_sms()
	{
		this.m_btn_add.setVisible(false);
		this.m_btn_delete.setVisible(false);
		this.m_btn_new.setVisible(false);
		this.m_cbx_messages.setVisible(false);
		this.m_combo_area.setVisible(false);
		this.m_lst_cc.setVisible(false);
		this.m_txt_messagename.setVisible(false);
		this.m_lbl_messagename.setVisible(false);
		this.listScrollPane.setVisible(false);
	}
	public void add_controls()
	{
		//int n_width = 21;
		
		m_panel_messages.setOpaque(false);
		m_panel_messages.setBackground(new Color(255,255,255,1));
		m_panel_messages.setPreferredSize(new Dimension(520, 270));

		m_check_enable_response = new JCheckBox();
		m_check_enable_response.addActionListener(this);
		m_check_enable_response.setActionCommand("act_allow_response_toggle");
		
		m_combo_replynumbers = new JComboBox();
		m_combo_replynumbers.setEnabled(false);
		
		if(PAS.get_pas().get_sendcontroller().get_activesending() != null && PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_ && 
				!PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_isresend() ) {

            m_lbl_enable_response = new StdTextLabel(Localization.l("main_sending_lba_allow_response") + ":", true, 150);
			m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 7, 1, GridBagConstraints.WEST);
			m_panel_messages.add(m_lbl_enable_response, m_panel_messages.m_gridconst);
			m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 1, 1, GridBagConstraints.WEST);
			m_panel_messages.add(m_check_enable_response, m_panel_messages.m_gridconst);
			m_panel_messages.set_gridconst(8, m_panel_messages.get_panel(), 1, 1, GridBagConstraints.WEST);
			m_panel_messages.add(m_combo_replynumbers, m_panel_messages.m_gridconst);
			m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL, 10);
			new WSTasResponseNumbers(this).start();
		}
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 7, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_expirydate, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 7, 1, GridBagConstraints.WEST);
		ExpiryMins[] mins;
		if(PAS.get_pas().get_sendcontroller().get_activesending() != null && PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_)
			mins = new ExpiryMins[]{ new ExpiryMins("30"), new ExpiryMins("60"), new ExpiryMins("120"), new ExpiryMins("360"), new ExpiryMins("720"), new ExpiryMins("1440"), new ExpiryMins("4320") };
		else
			mins = new ExpiryMins[] { new ExpiryMins("4320") };
		
		m_combo_expdate = new JComboBox<ExpiryMins>(mins);
		m_combo_expdate.setSelectedIndex(m_combo_expdate.getItemCount()-1);
		super.m_n_expiry_minutes = Integer.parseInt(((ExpiryMins)m_combo_expdate.getItemAt(m_combo_expdate.getSelectedIndex())).get_minutes());
		m_combo_expdate.addItemListener(this);
		m_panel_messages.add(m_combo_expdate, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(14, m_panel_messages.get_panel(), 7, 1, GridBagConstraints.WEST);
		//m_panel_messages.add(new JLabel("Minutes"), m_panel_messages.m_gridconst);

		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL, 10);
	
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 7, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_oadc_text, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 7, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_txt_oadc_text, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(14, m_panel_messages.get_panel(), 1, 1);
		m_panel_messages.add(btn_set_default_oadc, m_panel_messages.m_gridconst);
		
		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL,20);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 7, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_template, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 7, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_combo_templates, m_panel_messages.m_gridconst);

		//TEST
		if(tree_msglib!=null)
		{
			m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 7, 1);
			m_panel_messages.add(txt_msglib_search, m_panel_messages.m_gridconst);
			
			m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL, 5);

			tree_msglib.setPreferredSize(new Dimension(100, 100));
			m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 14, 1, GridBagConstraints.WEST);
			m_panel_messages.add(tree_msglib, m_panel_messages.m_gridconst);
			downloadMessageLib();
		}
		//TEST
		
		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL, 10);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 15, 1, GridBagConstraints.WEST);
		JScrollPane scroll = new JScrollPane(m_txt_messagetext,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(350, 100));
		m_panel_messages.add(scroll, m_panel_messages.m_gridconst);
		//m_panel_messages.add(m_txt_messagetext, m_panel_messages.m_gridconst);
		
		
		m_panel_messages.set_gridconst(14, m_panel_messages.get_panel(), 1, 1, GridBagConstraints.WEST);
        m_panel_messages.setPadding(10, 0);
		m_panel_messages.add(m_lbl_messagesize, m_panel_messages.m_gridconst);
		
		JPanel pnl = new JPanel();
		pnl.setOpaque(false);
		pnl.add(m_btn_new);
		pnl.add(m_btn_add);
		pnl.add(m_btn_delete);
		
		m_panel_messages.add(pnl, m_panel_messages.m_gridconst);
        m_panel_messages.setBorder(BorderFactory.createTitledBorder(Localization.l("common_message_content")));
		set_gridconst(0, 0, 1, 1);
		add(m_panel_messages, m_gridconst);
		init();
		
		//m_txt_messagetext.setPreferredSize(new Dimension(350, 600));
		m_txt_messagetext.addFocusListener(this);
		m_txt_messagetext.setSelectedTextColor(Color.black);
		m_txt_messagetext.setFocusTraversalKeysEnabled(false);
		m_txt_messagetext.setHighlighter(new DefaultHighlighter());
		
		m_tooltip = m_lbl_messagesize.createToolTip();
        m_tooltip.setTipText("<html> " + m_maxSafe + " " + Localization.l("main_sending_sms_messagetextlabel"));
		//activeLabel.setToolTipText(m_maxSafe + " " + PAS.l("main_sending_lba_messagetextlabel"));
		popupFactory = PopupFactory.getSharedInstance();
	}

	@Override
	public void focusGained(FocusEvent e) {
		resetHighLights();
		if(n_current_bracket==-1)
			tabPressed();
		//set_size_label(get_txt_messagetext().getText(), get_lbl_localsize());
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		if(n_current_bracket==-1)
			tabPressed();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if("act_smstemplate_changed".equals(e.getActionCommand()))
		{
			try
			{
				if(m_combo_templates.getSelectedIndex()<0)
					return;
					
				if(m_combo_templates.getSelectedItem().getClass().equals(SoundlibFileTxt.class))
				{
					SoundlibFileTxt s = (SoundlibFileTxt)m_combo_templates.getSelectedItem();
					s.load_file(this, "act_smstemplate_loaded");
				}
				else //null selected
				{
					UpdateTextFields();
				}
			}
			catch(Exception err){
				log.warn(err.getMessage(), err);
			}
		}
		else if("act_smstemplate_loaded".equals(e.getActionCommand()))
		{
			try
			{
				SoundlibFileTxt s = (SoundlibFileTxt)e.getSource();
				m_txt_messagetext.setText(s.get_text());
                m_txt_messagetext.setToolTipText(Localization.l("main_sending_text_template_tooltip"));
				UpdateTextFields();
				n_current_bracket = -1;
				m_txt_messagetext.requestFocus();
			}
			catch(Exception err)
			{
				log.warn(err.getMessage(), err);
			}
		}
		else if("act_allow_response_toggle".equals(e.getActionCommand())) {
			if(m_check_enable_response.isSelected() && m_combo_replynumbers.getItemCount()>0)
				m_combo_replynumbers.setEnabled(true);
			else if(m_combo_replynumbers.getItemCount()==0) {
				m_check_enable_response.setSelected(false);
				m_combo_replynumbers.setEnabled(false);
                JOptionPane.showMessageDialog(this, Localization.l("main_sending_warning_no_response_no"));
			} else
				m_combo_replynumbers.setEnabled(false);
		}
		else if("act_download_finished".equals(e.getActionCommand())) {
            m_responsenumbers = (List<UTASRESPONSENUMBER>)e.getSource();
			for(int i=0;i<m_responsenumbers.size();++i) {
				m_combo_replynumbers.addItem(new SmsInReplyNumber(m_responsenumbers.get(i).getSzResponsenumber(),m_responsenumbers.get(i).getNRefno(),m_responsenumbers.get(i).getNTimestamp()));
			}
		}
		super.actionPerformed(e);
	}
	
	
	public void setExpiry(int expiry) {
		if(expiry == 0)
			m_combo_expdate.setSelectedIndex(0);
		else
			m_combo_expdate.setSelectedItem(new String(String.valueOf(expiry)));
	}
	
	public void set_allow_response(boolean allow_response) {
		m_check_enable_response.setSelected(allow_response);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		super.itemStateChanged(e);
		set_size_label(m_txt_messagetext.getText(),m_lbl_messagesize);
		if(e.getSource().equals(m_combo_expdate)) {
			if(m_combo_expdate.getSelectedItem().equals(""))
				super.m_n_expiry_minutes = 0;
			else
				super.m_n_expiry_minutes = Integer.parseInt(((ExpiryMins)m_combo_expdate.getSelectedItem()).get_minutes());
			
		}
	}
	
	public void enableInput(boolean val) {
		m_combo_expdate.setEnabled(val);
		m_txt_oadc_text.setEnabled(val);
		m_combo_templates.setEnabled(val);
		m_txt_messagetext.setEnabled(val);
	}
	
	public void set_size_label(String text, JLabel activeLabel) {
        final int gsmsize = get_gsmsize(text);
		final int parts = (gsmsize <= m_maxSafe) ? 1 : (int) Math.ceil(gsmsize / (m_maxSafe-8d));
        String formattedText = String.format(Localization.l("main_sending_sms_size_info"), gsmsize, (m_maxSize- gsmsize), parts);
//        activeLabel.setText("<html>(" + get_gsmsize(text) + "<br /> " + (m_maxSize-get_gsmsize(text)) + " <br /> " + part + ")</html>");
        activeLabel.setText(formattedText);
        activeLabel.setForeground((parts == 1) ? PAS.get_pas().getForeground() : Color.RED);
    }
}
