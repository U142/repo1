package no.ums.pas.plugins.centric;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import no.ums.pas.core.variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.PreviewFrame;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.plugins.centric.ws.WSCentricSend;
import no.ums.pas.PAS;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.SendOptionToolbar.MunicipalCheckbox;
import no.ums.pas.ums.tools.ExpiryMins;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.calendarutils.DateTime;
import no.ums.ws.parm.CBALERTPOLYGON;
import no.ums.ws.parm.CBMESSAGE;
import no.ums.ws.parm.CBMESSAGELIST;
import no.ums.ws.parm.CBOPERATIONBASE;
import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.parm.UPolygon;
import no.ums.ws.parm.UPolypoint;

public class CentricSendOptionToolbar extends DefaultPanel implements ActionListener, FocusListener, KeyListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StdTextLabel m_lbl_alert_name;
	private StdTextLabel m_lbl_sender_name;
	private StdTextLabel m_lbl_date_time;
	private StdTextLabel m_lbl_message;
	//private StdTextLabel m_lbl_channel;
	//private StdTextLabel m_lbl_duration;
	//private StdTextLabel m_lbl_date;
	//private StdTextLabel m_lbl_time;
	private StdTextLabel m_lbl_risk;
	private StdTextLabel m_lbl_reaction;
	private StdTextLabel m_lbl_originator;
	private StdTextLabel m_lbl_pages;
	private StdTextLabel m_lbl_characters;
	
	private StdTextArea m_txt_alert_name;
	private String m_sz_date;
	private StdTextArea m_txt_sender_name;
	private StdTextArea m_txt_date_time;
	private JTextArea m_txt_message;
	private JScrollPane m_txt_messagescroll;
	
	//private JComboBox m_cbx_channel;
	//private JComboBox m_cbx_duration;
	//private JComboBox m_cbx_date;
	//private JComboBox m_cbx_time_hour;
	//private JComboBox m_cbx_time_minute;
	
	private StdTextLabel m_lbl_preview;
	private JTextArea m_txt_preview;
	private JScrollPane m_txt_previewscroll;
	
	private JComboBox m_cbx_risk;
	private JComboBox m_cbx_reaction;
	private JComboBox m_cbx_originator;
	private JComboBox m_cbx_time_hour;
	private JComboBox m_cbx_time_minute;
	
	/*private StdTextLabel m_lbl_schedule;
	private JRadioButton m_rad_immediate;
	private JRadioButton m_rad_schedule;
	private ButtonGroup m_grp_schedule;*/
	
	private JButton m_btn_send;
	private JButton m_btn_address_book; // Add image/icon
	private JButton m_btn_update;
	
	private JButton m_btn_save_message;
	private JButton m_btn_reset;
	
	private JTextArea m_txt_warning;
	private JScrollPane m_txt_warningscroll;
	
	private JButton m_btn_print;
	private JButton m_btn_cancel;
	
	private Calendar c;
	
	LoadingFrame progress = new LoadingFrame(PAS.l("main_statustext_lba_sending"), null);
	
	private CentricStatus m_centricstatus;
	
	public CentricSendOptionToolbar() {
		//super();
		init();
	}
	public void doInit() {
		init();
	}
	public void init() {
		m_lbl_alert_name = new StdTextLabel(PAS.l("projectdlg_projectname") + ":",new Dimension(150,20));
		m_lbl_sender_name = new StdTextLabel(PAS.l("main_sending_lba_sender_text") + ":", new Dimension(150,20));
		m_lbl_date_time = new StdTextLabel(PAS.l("common_date") + " - " + PAS.l("common_time") + ":", new Dimension(150,20));
		m_lbl_message = new StdTextLabel(PAS.l("common_message_content") + ":",new Dimension(150,20));
		/*m_lbl_channel = new StdTextLabel("Channel:",new Dimension(150,20));
		m_lbl_duration = new StdTextLabel("Duration:",new Dimension(150,20));
		m_lbl_date = new StdTextLabel("Date:",new Dimension(150,20));
		m_lbl_time = new StdTextLabel("Time:",new Dimension(150,20));*/
		
		m_lbl_risk = new StdTextLabel(PAS.l("common_risk") + ":",new Dimension(150,20));
		m_lbl_reaction = new StdTextLabel(PAS.l("common_reaction") + ":",new Dimension(150,20));
		m_lbl_originator = new StdTextLabel(PAS.l("common_originator") + ":",new Dimension(150,20));
				
		m_txt_alert_name = new StdTextArea("",new Dimension(300,20));
		m_txt_alert_name.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_alert_name.addFocusListener(this);
		m_txt_alert_name.addKeyListener(this);
		
		m_txt_sender_name = new StdTextArea("",new Dimension(300,20));
		m_txt_sender_name.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_sender_name.addFocusListener(this);
		m_txt_sender_name.addKeyListener(this);
		
		m_txt_date_time = new StdTextArea("",new Dimension(300,20));
		m_txt_date_time.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_date_time.addFocusListener(this);
		
		m_sz_date = getFormatedDate();
		m_txt_date_time.setText(m_sz_date);
		m_txt_date_time.addFocusListener(this);
		m_txt_date_time.setEnabled(false);
		
		m_btn_update = new JButton(PAS.l("common_update"));
		m_btn_update.addActionListener(this);
		
		m_txt_message = new JTextArea("",10,10);
		m_txt_message.setWrapStyleWord(true);
		m_txt_message.setLineWrap(true);
		m_txt_message.addFocusListener(this);
		m_txt_message.addKeyListener(this);
		//m_txt_message.setPreferredSize(new Dimension(300,200));
		
		m_txt_messagescroll = new JScrollPane(m_txt_message);
		//m_txt_messagescroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_messagescroll.setPreferredSize(new Dimension(300,100));
		
		/*m_cbx_channel = new JComboBox(new String[] { "Dutch - Channel 1" });
		m_cbx_channel.setPreferredSize(new Dimension(150,20));
		m_cbx_duration = new JComboBox(new ExpiryMins[]{ new ExpiryMins("30"), new ExpiryMins("60"), new ExpiryMins("120"), new ExpiryMins("360"), new ExpiryMins("720"), new ExpiryMins("1440") });
		m_cbx_duration.setPreferredSize(new Dimension(150,20));
		m_cbx_date = new JComboBox(new String[] { "10.05.2010" });
		m_cbx_time_hour = new JComboBox(new String[] { "15" });
		m_cbx_time_minute = new JComboBox(new String[] { "30" });*/
		
		m_cbx_risk = new JComboBox();
		/*m_cbx_risk.setRenderer(new DefaultListCellRenderer() { 

            JLabel lbl = new JLabel(""); 
            
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
                    if(value.getClass().equals(CB_MESSAGE_FIELDS_BASE.class)) 
                    { 
                            CB_MESSAGE_FIELDS_BASE base = (CB_MESSAGE_FIELDS_BASE)value; 
                            lbl.setText(base.getSzName()); 
                            return lbl; 
                    } 
                    return super.getListCellRendererComponent(list, value, index, isSelected, 
                                    cellHasFocus); 
            } 
            
		});*/
		m_cbx_risk.setPreferredSize(new Dimension(300,20));
		m_cbx_risk.addFocusListener(this);
		
		m_cbx_reaction = new JComboBox();
		/*m_cbx_reaction.setRenderer(new DefaultListCellRenderer() { 

            JLabel lbl = new JLabel(""); 
            
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
                    if(value.getClass().equals(CB_MESSAGE_FIELDS_BASE.class)) 
                    { 
                            CB_MESSAGE_FIELDS_BASE base = (CB_MESSAGE_FIELDS_BASE)value; 
                            lbl.setText(base.getSzName()); 
                            return lbl; 
                    } 
                    return super.getListCellRendererComponent(list, value, index, isSelected, 
                                    cellHasFocus); 
            } 
            
		});*/
		m_cbx_reaction.setPreferredSize(new Dimension(300,20));
		m_cbx_reaction.addFocusListener(this);
		
		m_cbx_originator = new JComboBox();
		/*m_cbx_originator.setRenderer(new DefaultListCellRenderer() { 

            JLabel lbl = new JLabel(""); 
            
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
                    if(value.getClass().equals(CB_MESSAGE_FIELDS_BASE.class)) 
                    { 
                            CB_MESSAGE_FIELDS_BASE base = (CB_MESSAGE_FIELDS_BASE)value; 
                            lbl.setText(base.getSzName()); 
                            return lbl; 
                    } 
                    return super.getListCellRendererComponent(list, value, index, isSelected, 
                                    cellHasFocus); 
            } 
            
		});*/
		m_cbx_originator.setPreferredSize(new Dimension(300,20));
		m_cbx_originator.addFocusListener(this);
		
		/*
		m_lbl_immediate = new StdTextLabel("Immidiately");
		m_lbl_schedule = new StdTextLabel("Schedule");
		m_rad_immediate = new JRadioButton();
		m_rad_schedule = new JRadioButton();
		m_grp_schedule = new ButtonGroup();
		m_grp_schedule.add(m_rad_immediate);
		m_grp_schedule.add(m_rad_schedule);
		*/
		
		//Preview ting
		m_lbl_preview = new StdTextLabel("Preview:",new Dimension(150,20));
		m_txt_preview = new JTextArea("",10,10);
		m_txt_preview.setWrapStyleWord(true);
		m_txt_preview.setLineWrap(true);
		m_txt_preview.setEnabled(false);
		
		m_txt_previewscroll = new JScrollPane(m_txt_preview);
		m_txt_previewscroll.setPreferredSize(new Dimension(300,100));
		
		m_lbl_pages = new StdTextLabel(PAS.l("common_page") + " 1/25");
		m_lbl_characters = new StdTextLabel(PAS.l("common_characters") + " 0/92", 150);
		
		m_btn_send = new JButton(PAS.l("main_sending_send"));
		m_btn_send.setPreferredSize(new Dimension(300,30));
		m_btn_address_book = new JButton("image"); // Add image/icon
		
		m_btn_save_message = new JButton("Save message");
		m_btn_reset = new JButton(PAS.l("common_reset"));
		m_btn_reset.addActionListener(this);
		
		m_btn_send.addActionListener(this);
		//m_btn_close = new JButton(ImageLoader.load_icon("delete_24.png"));
		//m_btn_close.addActionListener(this);
		//m_btn_close.setActionCommand("act_sending_close");
		//m_btn_close.setToolTipText(PAS.l("main_sending_adr_btn_close_sending"));
		
		add_controls();
		m_txt_alert_name.setEditable(true);
		m_txt_message.setEditable(true);
		m_txt_sender_name.setEditable(true);
		m_txt_date_time.setEditable(true);
		
		variables.MAPPANE.addMouseListener(this);
		variables.MAPPANE.addKeyListener(this);
		setVisible(true);
	}
	
	public void add_controls() {
		ENABLE_GRID_DEBUG = false;
		removeAll();
		reset_panels();
		set_gridconst(0, get_panel(), 1, 1);
		add(m_lbl_alert_name, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_alert_name, m_gridconst);
		m_txt_alert_name.setEnabled(true);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_sender_name, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_sender_name, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_date_time, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_date_time, m_gridconst);
		set_gridconst(8, get_panel(), 1, 1);
		add(m_btn_update, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 10);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_message, m_gridconst);
		set_gridconst(1, get_panel(), 7, 3);
		add(m_txt_messagescroll, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(8, inc_panels(), 1, 1);
		//add(m_btn_address_book, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 10);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_risk, m_gridconst);
		set_gridconst(1, get_panel(), 3, 1);
		add(m_cbx_risk, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_reaction, m_gridconst);
		set_gridconst(1, get_panel(), 3, 1);
		add(m_cbx_reaction, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_originator, m_gridconst);
		set_gridconst(1, get_panel(), 3, 1);
		add(m_cbx_originator, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_preview, m_gridconst);
		set_gridconst(1, get_panel(), 3, 1);
		add(m_txt_previewscroll, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		/*
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_lbl_pages, m_gridconst);
		*/
		set_gridconst(2, get_panel(), 1, 1);
		add(m_lbl_characters, m_gridconst);
				
		/*
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_rad_immediate, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);		
		add(m_lbl_immediate, m_gridconst);
		set_gridconst(3, get_panel(), 2, 1);
		add(m_rad_schedule, m_gridconst);
		set_gridconst(4, get_panel(), 2, 1);
		add(m_lbl_schedule, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_date,m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(m_cbx_date,m_gridconst);
		
		add_spacing(DIR_HORIZONTAL, 5);
		
		set_gridconst(3, get_panel(), 2, 1);
		add(m_lbl_time, m_gridconst);
		set_gridconst(4, get_panel(), 1, 1);
		add(m_cbx_time_hour, m_gridconst);
		set_gridconst(5, get_panel(), 2, 1);
		add(m_cbx_time_minute, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 15);*/
		
		set_gridconst(1, inc_panels(), 2, 1);
		add(m_btn_send, m_gridconst);
		m_btn_send.setActionCommand("act_goto_summary");
		m_btn_send.setEnabled(false);
		//set_gridconst(3, get_panel(), 1, 1);
		//add(m_btn_close, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 50);
		
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_btn_reset, m_gridconst);
		
		set_gridconst(2, get_panel(), 1, 1);
		//add(m_btn_save_message, m_gridconst);
		revalidate();
		repaint();
	}
	
	private void showSummary() {
		
		removeAll();
		reset_panels();
		
		set_gridconst(0, get_panel(), 1, 1);
		add(m_lbl_alert_name, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_alert_name, m_gridconst);
		m_txt_alert_name.setEnabled(false);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_preview, m_gridconst);
		set_gridconst(1, get_panel(), 3, 2);
		add(m_txt_previewscroll, m_gridconst);
		set_gridconst(0, inc_panels(), 1, 1);
		if(m_btn_print==null)
			m_btn_print = new JButton(PAS.l("common_print_summary"));
		add(m_btn_print, m_gridconst);
		
		
		add_spacing(DIR_VERTICAL, 5);
		
		if(m_txt_warning == null) {
			m_txt_warning = new JTextArea("",10,10);
			m_txt_warningscroll = new JScrollPane(m_txt_warning);
			m_txt_warningscroll.setPreferredSize(new Dimension(450,60));
		}
		
		set_gridconst(0, inc_panels(), 8, 1);
		add(m_txt_warningscroll, m_gridconst);
		m_txt_warning.setEnabled(false);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 8, 1);
		//progress.get_progress().setSize(new Dimension(450,25));
		progress.set_totalitems(0, PAS.l("main_statustext_lba_sending"));
		progress.get_progress().setPreferredSize(new Dimension(450,25));
		progress.stop_and_hide();
		add(progress.get_progress(), m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		if(m_btn_cancel == null) {
			m_btn_cancel = new JButton(PAS.l("common_cancel"));
			m_btn_cancel.addActionListener(this);
		}
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_btn_cancel, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_btn_send, m_gridconst);
		if(PAS.TRAINING_MODE)
			m_btn_send.setEnabled(false);
		else
			m_btn_send.setEnabled(true);
		
		m_btn_send.setActionCommand("act_send");
		revalidate();
		repaint();
		
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(m_btn_update)) {
			if(m_txt_alert_name.getText().endsWith(" " + m_sz_date))
				m_txt_alert_name.setText(m_txt_alert_name.getText().substring(0,m_txt_alert_name.getText().length()-(m_sz_date.length()+1)));
			m_sz_date = getFormatedDate();
			m_txt_alert_name.setText(m_txt_alert_name.getText() + " " + m_sz_date);
			m_txt_date_time.setText(m_sz_date);
		}
		if(e.getActionCommand().equals("act_send")) {
			// Check if summary is active, create send object, display send object preview
			
			CBMESSAGE msg = new CBMESSAGE();
			msg.setSzText(m_txt_preview.getText());
			msg.setLChannel(991); // This is stored in userinfo
			CBMESSAGELIST msglist = new CBMESSAGELIST();
			msglist.getMessage().add(msg);
			
			//sending.n_deptpk = PAS.get_pas().get_userinfo().get_current_department().get_deptpk();
			//sending.n_userpk = PAS.get_pas().get_userinfo().get_userpk();
			//sending.n_comppk = PAS.get_pas().get_userinfo().get_comppk();
			CBALERTPOLYGON poly = new CBALERTPOLYGON();
			
			//poly.setLProjectpk(Long.parseLong(PAS.get_pas().get_current_project().get_projectpk()));
			//poly.setLValidity(value);
			poly.setLSchedUtc(c.getTimeInMillis());
			poly.setTextmessages(msglist);
			
			UPolygon polygon = new UPolygon();
			PolygonStruct ps = (PolygonStruct)variables.MAPPANE.get_active_shape();
			if(ps == null || ps.get_coors_lat().size()<3) {
				JOptionPane.showMessageDialog(this, "Alert area is missing");
				return;
			}
			for(int i=0;i<ps.get_coors_lat().size();++i) {
				UPolypoint pp = new UPolypoint();
				pp.setLat(ps.get_coor_lat(i));
				pp.setLon(ps.get_coor_lon(i));
				polygon.getPolypoint().add(pp);
			}
			poly.setAlertpolygon(polygon);
			try {
				WSCentricSend send = new WSCentricSend(this, "act_somethingsomething", poly);
				send.start();
			}
			catch(Exception ex) {
				JOptionPane.showMessageDialog(this, PAS.l("common_error" + ": ") + ex.getMessage());
				progress.stop_and_hide();
				m_btn_cancel.setEnabled(true);
				m_btn_send.setEnabled(true);
			}
			
			m_btn_send.setEnabled(false);
			m_btn_cancel.setEnabled(false);
			
			try
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						progress.set_totalitems(0, PAS.l("main_statustext_lba_sending"));
						progress.get_progress().setVisible(true);
					}
				});
			}
			catch(Exception ex)
			{
				
			}
		}
		else if(e.getActionCommand().equals("act_goto_summary")) {
			showSummary();
		}
		if(e.getActionCommand().equals("act_somethingsomething")){
			JOptionPane.showMessageDialog(this, PAS.l("common_refno") + ": " + ((CBSENDINGRESPONSE)e.getSource()).getLRefno());
			progress.stop_and_hide();
			m_btn_cancel.setEnabled(true);
			m_btn_send.setEnabled(true);
			m_btn_reset.doClick();
			add_controls();
			onSendFinished(e);
		}
		if(e.getSource().equals(m_btn_cancel)) {
			add_controls();
		}
		if(e.getSource().equals(m_btn_reset)){
			m_btn_update.doClick();
			m_txt_alert_name.setText("");
			m_txt_sender_name.setText("");
			m_txt_message.setText("");
			//m_cbx_risk.setSelectedIndex(0);
			//m_cbx_reaction.setSelectedIndex(0);
			//m_cbx_originator.setSelectedIndex(0);
			m_txt_preview.setText("");
			updateCharacters();
			variables.MAPPANE.set_active_shape(new PolygonStruct(variables.NAVIGATION.getDimension()));
			variables.MAPPANE.repaint();
		}
	}
	
	private String getFormatedDate() {
		c = new GregorianCalendar();
		return String.format("%1$td-%1$tm-%1$tY %1$tH:%1$tM", c);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource().equals(m_txt_alert_name)) {
			if(m_txt_alert_name.getText().endsWith(" " + m_sz_date))
				m_txt_alert_name.setText(m_txt_alert_name.getText().substring(0,m_txt_alert_name.getText().length()-(m_sz_date.length()+1)));				
		}
			
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource().equals(m_txt_alert_name)) {
			m_txt_alert_name.setText(m_txt_alert_name.getText() + " " + m_sz_date);
		}
		m_txt_preview.setText(m_txt_sender_name.getText() + " " + m_txt_date_time.getText() + "\n" +
				m_txt_message.getText());
		checkInputs();
		updateCharacters();
	}
	
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);

		final int w = getWidth();
		final int h = getHeight();
		if(w<=1 || h<=1)
			return;
		/*
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					revalidate();
					//address_search.get_searchpanelresults().setPreferredSize(new Dimension(w, 200));
					//address_search.get_searchpanelresults().get_scrollpane().setPreferredSize(new Dimension(w, 200));
					//address_search.get_searchpanelresults().setPreferredSize(new Dimension(w, 200));
					//address_search.get_searchpanelresults().get_scrollpane().setPreferredSize(new Dimension(w-10, 150));
					//address_search.get_searchpanelresults().setSize(new Dimension(100,100));
					//address_search.revalidate();
					//address_search.get_searchpanelresults().get_scrollpane().revalidate();
				}
			});
		}
		catch(Exception err)
		{
			
		}*/

	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		final int max = 92;
		if(e.getSource() == m_txt_message || e.getSource() == m_txt_sender_name) {
			if(m_txt_message.getText().length() > ((max-2) - (m_txt_sender_name.getText().length() + m_txt_date_time.getText().length()))) {
				m_txt_preview.setText(m_txt_sender_name.getText() + " " + m_txt_date_time.getText() + "\n" +
					m_txt_message.getText().substring(0,((max-2) - (m_txt_sender_name.getText().length() + m_txt_date_time.getText().length()))));
				m_txt_message.setText(m_txt_message.getText().substring(0,((max-2) - (m_txt_sender_name.getText().length() + m_txt_date_time.getText().length())))); // -2 because of " " and "/n"
			}
			else
				m_txt_preview.setText(m_txt_sender_name.getText() + " " + m_txt_date_time.getText() + "\n" + m_txt_message.getText());
				
			updateCharacters();
		}
		checkInputs();
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		return;
	}
	public void onSendFinished(ActionEvent e) {
		if(m_centricstatus == null)
			m_centricstatus = new CentricStatus((CBSENDINGRESPONSE)e.getSource());
		else
			m_centricstatus.set_cbsendingresponse((CBSENDINGRESPONSE)e.getSource());
			
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		// update status ting med CBSendingresponse?
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
		checkInputs();
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		checkInputs();
	}
	private void checkInputs() {
		boolean enable_send = true;
		if(m_txt_alert_name.getText().length()<1)
			enable_send = false;
		if(m_txt_sender_name.getText().length()<1)
			enable_send = false;
		if(m_txt_message.getText().length()<1)
			enable_send = false;
		if(variables.MAPPANE.get_active_shape()==null || ((PolygonStruct)variables.MAPPANE.get_active_shape()).get_coors_lat().size()<3)
			enable_send = false;
		
		if(enable_send)
			m_btn_send.setEnabled(true);
		else
			m_btn_send.setEnabled(false);
	}
	
	private void updateCharacters() {
		m_lbl_characters.setText(PAS.l("common_characters") + " " + m_txt_preview.getText().length() + "/92");
	}
}
