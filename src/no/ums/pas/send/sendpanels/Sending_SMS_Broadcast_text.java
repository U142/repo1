package no.ums.pas.send.sendpanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;



import java.awt.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.JTextComponent;

import no.ums.pas.PAS;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.tree.UMSTree;
import no.ums.pas.core.ws.WSTasResponseNumbers;
import no.ums.pas.parm.alert.AlertWindow;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.messagelibrary.tree.MessageLibNode;
import no.ums.pas.sound.SoundlibFileTxt;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.tools.ExpiryMins;
import no.ums.pas.ums.tools.SmsInReplyNumber;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.UnderlineHighlightPainter;
import no.ums.ws.pas.tas.UTASRESPONSENUMBER;

import org.jvnet.substance.SubstanceLookAndFeel;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Sending_SMS_Broadcast_text extends Sending_Cell_Broadcast_text
{
	

	
	public JComboBox m_combo_expdate;
	public List<UTASRESPONSENUMBER> m_responsenumbers;
		
	public Sending_SMS_Broadcast_text(PAS pas, SendWindow parentwin)
	{
		super(parentwin);
		if(PAS.get_pas().get_sendcontroller().get_activesending() != null && PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
			m_maxSize = 160;
		}
		else
			m_maxSize = 760;
		init_for_sms();
		UpdateTextFields();
		FillSMSTemplates();
		if(parentwin.get_sendobject().get_sendproperties() != null) {
			m_txt_messagetext.setText(parentwin.get_sendobject().get_sendproperties().get_sms_broadcast_message());
			m_txt_oadc_text.setText(parentwin.get_sendobject().get_sendproperties().get_sms_broadcast_oadc());
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
			
			m_lbl_enable_response = new StdTextLabel(PAS.l("main_sending_lba_allow_response") + ":", true, 150);
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
		
		m_combo_expdate = new JComboBox(mins);
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
		
		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL,20);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 7, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_template, m_panel_messages.m_gridconst);
		//m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 7, 1, GridBagConstraints.WEST);
		//m_panel_messages.add(m_combo_templates, m_panel_messages.m_gridconst);
		
		//TEST
		m_panel_messages.set_gridconst(7, m_panel_messages.get_panel(), 7, 1);
		m_panel_messages.add(txt_msglib_search, m_panel_messages.m_gridconst);
		
		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL, 5);

		tree_msglib.setPreferredSize(new Dimension(100, 100));
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 14, 1, GridBagConstraints.WEST);
		m_panel_messages.add(tree_msglib, m_panel_messages.m_gridconst);
		downloadMessageLib();
		//TEST
		
		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL, 10);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), 10, 1, GridBagConstraints.WEST);
		JScrollPane scroll = new JScrollPane(m_txt_messagetext,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(250, 100));
		m_panel_messages.add(scroll, m_panel_messages.m_gridconst);
		//m_panel_messages.add(m_txt_messagetext, m_panel_messages.m_gridconst);
		
		
		m_panel_messages.set_gridconst(10, m_panel_messages.get_panel(), 4, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_messagesize, m_panel_messages.m_gridconst);
		
		JPanel pnl = new JPanel();
		pnl.setOpaque(false);
		pnl.add(m_btn_new);
		pnl.add(m_btn_add);
		pnl.add(m_btn_delete);
		
		m_panel_messages.add(pnl, m_panel_messages.m_gridconst);		
		m_panel_messages.setBorder(BorderFactory.createTitledBorder(PAS.l("common_message_content")));		
		set_gridconst(0, 0, 1, 1);
		add(m_panel_messages, m_gridconst);
		init();
		
		m_txt_messagetext.setPreferredSize(new Dimension(250, 600));
		m_txt_messagetext.addFocusListener(this);
		m_txt_messagetext.setSelectedTextColor(Color.black);
		m_txt_messagetext.setFocusTraversalKeysEnabled(false);
		m_txt_messagetext.setHighlighter(new DefaultHighlighter());
	}
	// Tatt fra Sending_SMS_Broadcast_text.java
	
	
	/*@Override
	public void keyPressed(KeyEvent e) {
		//if(e.getKeyCode()==KeyEvent.VK_TAB)
		//m_txt_messagetext.getHighlighter().addHighlight(p0, p1, p)
		System.out.println("Key pressed: " + e.getKeyChar());	
	}*/
	

	
	@Override
	public void focusGained(FocusEvent e) {
		resetHighLights();
		if(n_current_bracket==-1)
			tabPressed();
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
					if(m_combo_templates.getSelectedIndex() == 0) {
						m_txt_messagetext.setText("");
						m_txt_messagetext.setToolTipText(null);
					}
					UpdateTextFields();
				}
			}
			catch(Exception err){
				err.printStackTrace();
			}
		}
		else if("act_smstemplate_loaded".equals(e.getActionCommand()))
		{
			try
			{
				SoundlibFileTxt s = (SoundlibFileTxt)e.getSource();
				m_txt_messagetext.setText(s.get_text());
				m_txt_messagetext.setToolTipText(PAS.l("main_sending_text_template_tooltip"));
				UpdateTextFields();
				n_current_bracket = -1;
				m_txt_messagetext.requestFocus();
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
		else if("act_allow_response_toggle".equals(e.getActionCommand())) {
			if(m_check_enable_response.isSelected() && m_combo_replynumbers.getItemCount()>0)
				m_combo_replynumbers.setEnabled(true);
			else if(m_combo_replynumbers.getItemCount()==0) {
				m_check_enable_response.setSelected(false);
				m_combo_replynumbers.setEnabled(false);
				JOptionPane.showMessageDialog(this,PAS.l("main_sending_warning_no_response_no"));				
			} else
				m_combo_replynumbers.setEnabled(false);
		}
		else if("act_download_finished".equals(e.getActionCommand())) {
			//m_arr_projects = (ArrayList<Project>)e.getSource();
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
		if(e.getSource().equals(m_combo_expdate)) {
			if(m_combo_expdate.getSelectedItem().equals(""))
				super.m_n_expiry_minutes = 0;
			else
				super.m_n_expiry_minutes = Integer.parseInt(((ExpiryMins)m_combo_expdate.getSelectedItem()).get_minutes());
			
		}
			
	}
}









