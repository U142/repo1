package no.ums.pas.send;


import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.RightsManagement;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.importer.ActionFileLoaded;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.importer.SosiFile;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.PreviewFrame;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.Municipal;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ColorButton;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.UMunicipalDef;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;


public class SendOptionToolbar extends DefaultPanel implements ActionListener, FocusListener {
	
	public static final int SIZE_TXT			= 260;
	public static final int SIZE_BUTTON_ICON	= 21;
	public static final int SIZE_BUTTON_SMALL	= 25;
	public static final int SIZE_BUTTON_MEDIUM	= 35;
	public static final int SIZE_BUTTON_LARGE	= 41;
	public static final int SIZE_BUTTON_XLARGE	= 70;
	public static final int SIZE_BUTTON_XXLARGE	= 90;
	


	
	//PAS m_pas;
	//private PAS get_pas() { return m_pas; }
	public final JPopupMenu menu_smspriv = new JPopupMenu();
	public final JPopupMenu menu_fixedpriv = new JPopupMenu();
	public final JPopupMenu menu_smscomp = new JPopupMenu();
	public final JPopupMenu menu_fixedcomp = new JPopupMenu();
	
	public final ButtonGroup group_smsprivbtn = new ButtonGroup();
	public final ButtonGroup group_fixedprivbtn = new ButtonGroup();
	public final ButtonGroup group_smscompbtn = new ButtonGroup();
	public final ButtonGroup group_fixedcompbtn = new ButtonGroup();
	protected final ButtonGroup [] groups_arr = new ButtonGroup [] { group_smsprivbtn, group_fixedprivbtn, group_smscompbtn, group_fixedcompbtn };

	public final JPopupMenu menu_municipals = new JPopupMenu();
	
	
	SendObject m_parent;
	public SendObject get_parent() { return m_parent; }
	/*set to true by alertgui*/
	public void setIsAlert(boolean b) {
		m_b_is_alert = b;
	}
	public boolean getIsAlert() { return m_b_is_alert; }
	boolean m_b_is_alert = false;
	StdTextArea m_txt_sendname;
	ButtonGroup m_group_sendingtype;
	JToggleButton m_radio_sendingtype_polygon;
	JToggleButton m_radio_sendingtype_ellipse;
	JToggleButton m_radio_sendingtype_polygonal_ellipse;
	JToggleButton m_radio_sendingtype_municipal;
	ButtonGroup m_btngroup_lba;
	public JToggleButton get_radio_polygon() { return m_radio_sendingtype_polygon; }
	public JToggleButton get_radio_ellipse() { return m_radio_sendingtype_ellipse; }
	public JToggleButton get_radio_polygonal_ellipse() { return m_radio_sendingtype_polygonal_ellipse; }
	public JToggleButton get_radio_municipal() { return m_radio_sendingtype_municipal; }
	JButton m_btn_goto;
	ToggleAddresstype m_btn_adrtypes_private_fixed;
	ToggleAddresstype m_btn_adrtypes_company_fixed;
	ToggleAddresstype m_btn_adrtypes_private_mobile;
	ToggleAddresstype m_btn_adrtypes_company_mobile;
	ToggleAddresstype m_btn_adrtypes_nophone_private;
	ToggleAddresstype m_btn_adrtypes_nophone_company;
	ToggleAddresstype m_btn_adrtypes_cell_broadcast_text;
	ToggleAddresstype m_btn_adrtypes_cell_broadcast_voice;
	ToggleAddresstype m_btn_adrtypes_nofax;
	public ToggleAddresstype get_cell_broadcast_text() { return m_btn_adrtypes_cell_broadcast_text; }
	public ToggleAddresstype get_cell_broadcast_voice() { return m_btn_adrtypes_cell_broadcast_voice; }
	JToggleButton m_btn_finalize;
	SendingColorPicker m_colorpicker;
	ColorButton m_btn_color;
	public JButton m_btn_send;
	public JButton m_btn_close;
	public JButton m_btn_open;
	private int m_n_addresstypes = 0;
	ActionListener m_callback;
	protected ActionListener get_callback() { return m_callback; }
	protected ActionListener m_alert_callback;
	protected ActionListener report_addresschanges() { return m_alert_callback; }
	public void setReportAddressChanges(ActionListener a) { m_alert_callback = a; }
	
	ImageIcon m_icon_polygon_no_edit = null;
	ImageIcon m_icon_polygon_edit = null;
	ImageIcon m_icon_gis_no_edit = null;
	ImageIcon m_icon_gis_edit = null;
	
	
	StdTextLabel m_lbl_addresstypes_private = new StdTextLabel("", 9, false);
	StdTextLabel m_lbl_addresstypes_company = new StdTextLabel("", 9, false);
	StdTextLabel m_lbl_addresstypes_lba = new StdTextLabel("", 9, false);
	
	private PreviewFrame m_gis_preview = null;
	public void setPreviewFrame(PreviewFrame f) { m_gis_preview = f; }
	
	public enum ADRGROUPS
	{
		PRIVATE,
		COMPANY,
		LBA,
		NOFAX,
	}
	
	public int get_addresstypes() { return m_n_addresstypes; }
	private void click_addresstypes(boolean pf, boolean cf, boolean pm, boolean cm, boolean np, int nofax) {
		m_btn_adrtypes_private_fixed.setSelected(pf);
		m_btn_adrtypes_company_fixed.setSelected(cf);
		m_btn_adrtypes_private_mobile.setSelected(pm);
		m_btn_adrtypes_company_mobile.setSelected(cm);
		m_btn_adrtypes_nophone_private.setSelected(np);
		m_btn_adrtypes_nofax.setSelected((nofax==1 ? true : false));
	}
	protected String gen_adrtypes_text(int n_adrtypes, ADRGROUPS group)
	{
		String ret = "<html>";
		/*if(n_adrtypes<=0)
			ret += "<font color='red'>- No addresstypes selected</br></font>";
		else*/
		{
			String temp = "";
			String sz_font = "<font size='1'>";
			
			if(group==ADRGROUPS.PRIVATE)
			{
				if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_fixed") + "<br>";
				if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_fixed_alt_sms")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_fixed_and_mobile")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_mobile")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_mobile_and_fixed")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_SMS_PRIVATE) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_sms")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_sms_alt_fixed")+"<br>";
				if(temp.length()>0) //add heading
				{
					if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0)
						temp += sz_font + "- " + PAS.l("main_sending_adr_option_using_blocklist");
				}
				ret += "<font size='3'><b>" + PAS.l("common_adr_private") + "</b></font><br>";
				ret += temp; //add text
			}

			if(group==ADRGROUPS.COMPANY)
			{
				if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_fixed") + "<br>";
				if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_fixed_alt_sms")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_fixed_and_mobile")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_mobile")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_mobile_and_fixed")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_SMS_COMPANY) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_sms")+"<br>";
				if((m_n_addresstypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0)
					temp += sz_font + "- "+PAS.l("main_sending_adr_option_sms_alt_fixed")+"<br>";
				if(temp.length()>0) //add heading
				{
					if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0)
						temp += sz_font + "- " + PAS.l("main_sending_adr_option_using_blocklist");
				}
				ret += "<font size='3'><b>" + PAS.l("common_adr_company") + "</b></font><br>";
				ret += temp;
			}

			if(group==ADRGROUPS.LBA)
			{
				if((m_n_addresstypes & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0)
					temp += sz_font + "- " + PAS.l("main_sending_adr_option_location_based_sms");
				if(temp.length()>0)
					ret += "<font size='3'><b>" + PAS.l("main_sending_adr_option_location_based") + "</b></font><br>";
				ret += temp;
			}
			if(group==ADRGROUPS.NOFAX)
			{
				if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0)
					temp += sz_font + "- " + PAS.l("main_sending_adr_option_using_blocklist");
				ret += temp;
			}
		}
		ret += "</html>";
		return ret;
	}
	public void init_addresstypes(int n_adrtypes)
	{
		//set_addresstypes(n_adrtypes);
		_SelectAddressSelection(m_n_addresstypes);
		
	}
	public void set_addresstypes(int n_adrtypes) {
		boolean b = false;
		m_n_addresstypes = n_adrtypes;
		if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE ||
			(m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) == SendController.SENDTO_FIXED_PRIVATE_ALT_SMS ||
			(m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) == SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE)
			m_btn_adrtypes_private_fixed.setSelected(true);
		else
			m_btn_adrtypes_private_fixed.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE) == SendController.SENDTO_MOBILE_PRIVATE ||
			(m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) == SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED ||
			(m_n_addresstypes & SendController.SENDTO_SMS_PRIVATE) == SendController.SENDTO_SMS_PRIVATE ||
			(m_n_addresstypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) == SendController.SENDTO_SMS_PRIVATE_ALT_FIXED)
			m_btn_adrtypes_private_mobile.setSelected(true);
		else
			m_btn_adrtypes_private_mobile.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY ||
			(m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) == SendController.SENDTO_FIXED_COMPANY_ALT_SMS ||
			(m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) == SendController.SENDTO_FIXED_COMPANY_AND_MOBILE)
			m_btn_adrtypes_company_fixed.setSelected(true);
		else
			m_btn_adrtypes_company_fixed.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY ||
			(m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) == SendController.SENDTO_MOBILE_COMPANY_AND_FIXED ||
			(m_n_addresstypes & SendController.SENDTO_SMS_COMPANY) == SendController.SENDTO_SMS_COMPANY ||
			(m_n_addresstypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) == SendController.SENDTO_SMS_COMPANY_ALT_FIXED)
			m_btn_adrtypes_company_mobile.setSelected(true);
		else
			m_btn_adrtypes_company_mobile.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT &&
				can_lba())
		{
			m_btn_adrtypes_cell_broadcast_text.setSelected(true);
			//m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
		}
		else
		{
			//if(m_btn_adrtypes_cell_broadcast_text.isSelected())
			//	m_btn_adrtypes_cell_broadcast_text.doClick();
			m_btn_adrtypes_cell_broadcast_text.setSelected(false);
			m_n_addresstypes &= ~SendController.SENDTO_CELL_BROADCAST_TEXT;
			
		}
		if((m_n_addresstypes & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE)
		{
			m_btn_adrtypes_cell_broadcast_voice.setSelected(true);
			//m_btn_adrtypes_cell_broadcast_text.setSelected(false);
		}
		else
			m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
		
		if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) == SendController.SENDTO_USE_NOFAX_COMPANY)
		{
			m_btn_adrtypes_nofax.setSelected(true);
		}
		else
			m_btn_adrtypes_nofax.setSelected(false);
		
		/*if((m_n_addresstypes & SendController.SENDTO_NOPHONE_PRIVATE) == SendController.SENDTO_NOPHONE_PRIVATE)
			m_btn_adrtypes_nophone_private.setSelected(true);
		else*/
			m_btn_adrtypes_nophone_private.setSelected(false);
		/*if((m_n_addresstypes & SendController.SENDTO_NOPHONE_COMPANY) == SendController.SENDTO_NOPHONE_COMPANY)
			m_btn_adrtypes_nophone_company.setSelected(true);
		else*/
			m_btn_adrtypes_nophone_company.setSelected(false);
				
			if(report_addresschanges()!=null)
				report_addresschanges().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
			m_lbl_addresstypes_private.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.PRIVATE));
			m_lbl_addresstypes_company.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.COMPANY));
			m_lbl_addresstypes_lba.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.LBA));
			
		//_SelectAddressSelection(m_n_addresstypes);			
	}
	
	public static final int BTN_SENDINGTYPE_POLYGON_	= 1;
	public static final int BTN_SENDINGTYPE_ELLIPSE_ 	= 2;
	public static final int BTN_ADRTYPES_PRIVATE_		= 4;
	public static final int BTN_ADRTYPES_COMPANY_		= 8;
	public static final int BTN_ADRTYPES_NOPHONE_		= 16;
	public static final int BTN_COLORPICKER_			= 32;
	public static final int BTN_FINALIZE_				= 64;
	public static final int BTN_SEND_					= 128;
	public static final int BTN_CLOSE_					= 256;
	public static final int BTN_OPEN_					= 512;
	public static final int BTN_ACTIVATE_				= 1024;
	public static final int BTN_CENTER_ON_MAP_			= 2048;
	public static final int TXT_SENDINGNAME_			= 4096;
	public static final int BTN_CELL_BROADCAST_			= 8192;
	public static final int BTN_CELL_BROADCAST_VOICE_	= 16384;
	public static final int TXT_RECIPIENTTYPES_			= 32768;
	public static final int BTN_ADRTYPES_NOFAX_			= 65536;
	public static final int BTN_SENDINGTYPE_MUNICIPAL_	= 1 << 17;
	public static final int BTN_SENDINGTYPE_POLYGONAL_ELLIPSE_ = 1 << 18; 
	
	public static final int COMPONENTS_ALL_ = BTN_SENDINGTYPE_POLYGON_ | BTN_SENDINGTYPE_ELLIPSE_ | BTN_ADRTYPES_PRIVATE_ |
												BTN_ADRTYPES_COMPANY_ | BTN_ADRTYPES_NOPHONE_ | BTN_COLORPICKER_ | BTN_FINALIZE_ | 
												BTN_SEND_ | BTN_CLOSE_ | BTN_OPEN_ | BTN_ACTIVATE_ | BTN_CENTER_ON_MAP_ | TXT_SENDINGNAME_ | 
												BTN_CELL_BROADCAST_ | BTN_CELL_BROADCAST_VOICE_ | BTN_ADRTYPES_NOFAX_ | 
												TXT_RECIPIENTTYPES_ | BTN_SENDINGTYPE_MUNICIPAL_;
	
	private void hide_buttons() {
		show_buttonsbyadrtype(PAS.get_pas().get_rightsmanagement().addresstypes());
		switch(get_parent().get_sendproperties().get_sendingtype()) {
			case SendProperties.SENDING_TYPE_POLYGON_:
				break;
			case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
				//show_buttons(BTN_SENDINGTYPE_POLYGON_ | BTN_SENDINGTYPE_ELLIPSE_ | BTN_CELL_BROADCAST_ | BTN_CELL_BROADCAST_VOICE_, false);
				m_radio_sendingtype_polygon.setEnabled(false);
				m_radio_sendingtype_ellipse.setEnabled(false);
				m_radio_sendingtype_municipal.setEnabled(false);
				m_radio_sendingtype_polygonal_ellipse.setEnabled(false);
				
				m_btn_adrtypes_cell_broadcast_text.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_text.setSelected(false);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
				break;
			case SendProperties.SENDING_TYPE_MUNICIPAL_:
				m_btn_adrtypes_cell_broadcast_text.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_text.setSelected(false);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_voice.setSelected(false);				
				break;
		}
	}
	public void showLoader(Component c) {
		show_buttons(COMPONENTS_ALL_, false);
		c.setPreferredSize(new Dimension(PAS.get_pas().get_eastwidth()-40, 40));
		c.setSize(PAS.get_pas().get_eastwidth()-40, 40);
		set_gridconst(0, 0, 100, 3);
		add(c, m_gridconst);
	}
	public void hideLoader(Component c) {
		remove(c);
		show_buttons(COMPONENTS_ALL_, true);
	}
	
	public void show_buttonsbyadrtype(long ADR) {
		m_btn_adrtypes_nophone_private.setVisible((ADR & SendController.SENDTO_NOPHONE_PRIVATE) == SendController.SENDTO_NOPHONE_PRIVATE);
		m_btn_adrtypes_nophone_company.setVisible((ADR & SendController.SENDTO_NOPHONE_COMPANY) == SendController.SENDTO_NOPHONE_COMPANY);
			
		m_btn_adrtypes_private_fixed.setVisible((ADR & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE);
		m_btn_adrtypes_private_mobile.setVisible((ADR & SendController.SENDTO_MOBILE_PRIVATE) == SendController.SENDTO_MOBILE_PRIVATE);

		m_btn_adrtypes_company_fixed.setVisible((ADR & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY);
		m_btn_adrtypes_company_mobile.setVisible((ADR & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY);

		m_btn_adrtypes_cell_broadcast_text.setVisible((ADR & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT);
		m_btn_adrtypes_cell_broadcast_voice.setVisible((ADR & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE);
	}
	
	public void show_buttons(int FLAGS, boolean b_show) {
		if((FLAGS & BTN_SENDINGTYPE_POLYGON_) == BTN_SENDINGTYPE_POLYGON_) {
			this.m_radio_sendingtype_polygon.setVisible(b_show);
		}
		if((FLAGS & BTN_SENDINGTYPE_ELLIPSE_) == BTN_SENDINGTYPE_ELLIPSE_) {
			this.m_radio_sendingtype_ellipse.setVisible(b_show);
		}
		if((FLAGS & BTN_SENDINGTYPE_POLYGONAL_ELLIPSE_) == BTN_SENDINGTYPE_POLYGONAL_ELLIPSE_) {
			this.m_radio_sendingtype_polygonal_ellipse.setVisible(b_show);
		}
		if((FLAGS & BTN_SENDINGTYPE_MUNICIPAL_) == BTN_SENDINGTYPE_MUNICIPAL_)
		{
			this.m_radio_sendingtype_municipal.setVisible(b_show);
		}
		if((FLAGS & BTN_ADRTYPES_PRIVATE_) == BTN_ADRTYPES_PRIVATE_) {
			this.m_btn_adrtypes_private_fixed.setVisible(b_show);
			this.m_btn_adrtypes_private_mobile.setVisible(b_show);
		}
		if((FLAGS & BTN_ADRTYPES_COMPANY_) == BTN_ADRTYPES_COMPANY_) {
			this.m_btn_adrtypes_company_fixed.setVisible(b_show);
			this.m_btn_adrtypes_company_mobile.setVisible(b_show);
		}
		if((FLAGS & BTN_ADRTYPES_NOPHONE_) == BTN_ADRTYPES_NOPHONE_) {
			this.m_btn_adrtypes_nophone_private.setVisible(b_show);
			this.m_btn_adrtypes_nophone_company.setVisible(b_show);
		}
		if((FLAGS & BTN_COLORPICKER_) == BTN_COLORPICKER_) {
			this.m_btn_color.setVisible(b_show);
		}
		if((FLAGS & BTN_FINALIZE_) == BTN_FINALIZE_) {
			this.m_btn_finalize.setVisible(b_show);
		}
		if((FLAGS & BTN_SEND_) == BTN_SEND_) {
			this.m_btn_send.setVisible(b_show);
		}
		if((FLAGS & BTN_CLOSE_) == BTN_CLOSE_) {
			this.m_btn_close.setVisible(b_show);
		}
		if((FLAGS & BTN_OPEN_) == BTN_OPEN_) {
			this.m_btn_open.setVisible(b_show);
		}
		if((FLAGS & BTN_ACTIVATE_) == BTN_ACTIVATE_) {
			this.m_radio_activate.setVisible(b_show);
		}
		if((FLAGS & BTN_CENTER_ON_MAP_) == BTN_CENTER_ON_MAP_) {
			this.m_btn_goto.setVisible(b_show);
		}
		if((FLAGS & TXT_SENDINGNAME_) == TXT_SENDINGNAME_) {
			this.m_txt_sendname.setVisible(b_show);
		}
		if((FLAGS & BTN_CELL_BROADCAST_) == BTN_CELL_BROADCAST_) {
			this.m_btn_adrtypes_cell_broadcast_text.setVisible(b_show);
		}
		if((FLAGS & BTN_CELL_BROADCAST_VOICE_) == BTN_CELL_BROADCAST_VOICE_) {
			this.m_btn_adrtypes_cell_broadcast_voice.setVisible(b_show);
		}
		if((FLAGS & TXT_RECIPIENTTYPES_) == TXT_RECIPIENTTYPES_) {
			this.m_lbl_addresstypes_company.setVisible(b_show);
			this.m_lbl_addresstypes_private.setVisible(b_show);
			this.m_lbl_addresstypes_lba.setVisible(b_show);
		}
		if((FLAGS & BTN_ADRTYPES_NOFAX_) == BTN_ADRTYPES_NOFAX_) {
			this.m_btn_adrtypes_nofax.setVisible(b_show);
		}
	}
	
	public void gen_addresstypes() {

		int TYPES = 0;
		if(m_btn_adrtypes_nophone_private.isSelected() && m_btn_adrtypes_nophone_private.isVisible()) TYPES |= m_btn_adrtypes_nophone_private.get_adrtype();
		if(m_btn_adrtypes_nophone_company.isSelected() && m_btn_adrtypes_nophone_company.isVisible()) TYPES |= m_btn_adrtypes_nophone_company.get_adrtype();
		if(m_btn_adrtypes_cell_broadcast_text.isSelected() && m_btn_adrtypes_cell_broadcast_text.isVisible()) TYPES |= m_btn_adrtypes_cell_broadcast_text.get_adrtype();
		if(m_btn_adrtypes_cell_broadcast_voice.isSelected() && m_btn_adrtypes_cell_broadcast_voice.isVisible() && !m_btn_adrtypes_cell_broadcast_text.isSelected()) TYPES |= m_btn_adrtypes_cell_broadcast_voice.get_adrtype();
		
		if(m_btn_adrtypes_nofax.isSelected()) TYPES |= m_btn_adrtypes_nofax.get_adrtype();
		
		Enumeration<AbstractButton> en = group_smsprivbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if(it.isSelected())
				TYPES |= i;
		}
		en = group_fixedprivbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if(it.isSelected())
				TYPES |= i;
		}
		en = group_smscompbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if(it.isSelected())
				TYPES |= i;
		}
		en = group_fixedcompbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if(it.isSelected())
				TYPES |= i;
		}

        //disable buttons if certain criterias are met
		try
		{
			_EnableAddressSelection(0, true, group_smsprivbtn); //enable all
			_EnableAddressSelection(0, true, group_fixedprivbtn); //enable all
			_EnableAddressSelection(0, true, group_smscompbtn); //enable all
			_EnableAddressSelection(0, true, group_fixedcompbtn); //enable all
			EnableAddressSelection(false, group_fixedprivbtn, group_smsprivbtn);
			EnableAddressSelection(false, group_smsprivbtn, group_fixedprivbtn);
			EnableAddressSelection(false, group_fixedcompbtn, group_smscompbtn);
			EnableAddressSelection(false, group_smscompbtn, group_fixedcompbtn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		set_addresstypes(TYPES);
		System.out.println("Addresstypes = " + TYPES);
		
	}
	
	protected int EnableAddressSelection(boolean b, ButtonGroup btngroup_to_check, ButtonGroup btngroup_to_disable)
	{
		int ret = 0;
		Enumeration<AbstractButton> en = btngroup_to_disable.getElements();
		en = btngroup_to_check.getElements();
		while(en.hasMoreElements())
		{
			CheckItem c = (CheckItem)en.nextElement();
			if(c.isSelected())
			{
				int val = ((Integer)c.get_value2()).intValue();
				for(int x = 0; x < 32; x++)
				{
					if(((1 << x) & val) > 0)
						ret &= ~_EnableAddressSelection((1 << x), false, btngroup_to_disable);
				}
			}
		}		
		return ret;
	}
	private void _SelectAddressSelection(int n) //in addresstypes to check
	{
		Enumeration<AbstractButton> btn;
		for(int i=0; i < groups_arr.length; i++)
		{
			btn = groups_arr[i].getElements();
			CheckItem itm;
			while(btn.hasMoreElements())
			{
				itm = (CheckItem)btn.nextElement();
				int value = ((Integer)itm.get_value()).intValue();
				if((value & n)==value)
					itm.setSelected(true);
				//else
				//	itm.setSelected(false);
			}
			
		}
		
	}
	private int _EnableAddressSelection(int n, boolean b, ButtonGroup btngroup_to_disable)
	{
		int ret = 0;
		Enumeration<AbstractButton> btn;
		btn = btngroup_to_disable.getElements();
		CheckItem itm;
		while(btn.hasMoreElements())
		{
			itm = (CheckItem)btn.nextElement();
			int value = ((Integer)itm.get_value()).intValue();
			if((n & value) > 0 || n==0)
			{
				if(itm.isSelected())
					ret |= value;
				if(!b)
				{
					int remainder = (value & ~n);
					boolean b_found_remainder = false;
					/*if(remainder>0 && itm.isSelected()) //combination of two or more
					{
						Enumeration<AbstractButton> find_alternative = btngroup.getElements();
						while(find_alternative.hasMoreElements())
						{
							CheckItem itm2 = ((CheckItem)find_alternative.nextElement());
							int value2 = 0;
							value2 = ((Integer)itm2.get_value()).intValue();
							if((remainder & value2) > 0)
							{
								itm2.setSelected(true);
								b_found_remainder = true;
								break;
							}
						}
					}*/
					if(!b_found_remainder && !b && itm.isSelected())
						btngroup_to_disable.getElements().nextElement().setSelected(true);
					
				}
				//if(n!=0)
					itm.setEnabled(b);

			}
				
		}
		return ret;
	}
	private int m_n_initsendnumber = 0;
	public SendingColorPicker get_colorpicker() { return m_colorpicker; }
	public ColorButton get_colorbutton() { return m_btn_color; }
	private JRadioButton m_radio_activate;
	public int get_sendingid() { 
		return m_n_initsendnumber;
	}
	private GridBagLayout m_gridbag;
	
	public void addSeparator()
	{
		this.add_spacing(DIR_HORIZONTAL, 10);
	}
	public void addSeparator(int i)
	{
		add_spacing(DIR_HORIZONTAL, i);
	}
	public SendOptionToolbar(/*PAS pas, */SendObject sendobject, ActionListener callback, int n_sendnumber) {
		super();
		//m_pas = pas;
		set_import_callback(this);
		m_parent = sendobject;
		m_n_initsendnumber = n_sendnumber;
		m_callback = callback;
		//m_gridlayout = new GridBagLayout();
		//m_gridconst  = new GridBagConstraints();
		m_btngroup_lba = new ButtonGroup();
		init();
		this.setSize(340, 25);
		try {
			//this.setFloatable(true);
			this.setVisible(true);
			this.setFocusable(true);
		} catch(Exception e) {
			//PAS.get_pas().add_event("ERROR SendOptionToolbar.constructor - " + e.getMessage());
			Error.getError().addError("SendOptionToolbar","Exception in SendOptionToolbar",e,1);
		}
		//this.setOrientation(JToolBar.HORIZONTAL);
		//setLayout((m_gridbag = new GridBagLayout()));
		setLayout(this.getLayout());
		//m_gridbag = this;
		//setLayout(this);
		//this.setFloatable(false);
		//this.setRollover(true);
		//setBorderPainted(true);
	}
	public void close() {
		
		setVisible(false);
	}
	public void set_sendingtype() {
		set_sendingicons();
		hide_buttons();
		lock_sending(false);
		set_addresstypes(get_addresstypes());
	}
	protected void resetMunicipals()
	{
		for(int i=0; i < menu_municipals.getComponentCount(); i++)
		{
			Component c = menu_municipals.getComponent(i);
			if(c.getClass().equals(MunicipalCheckbox.class))
				((MunicipalCheckbox)c).setSelected(false);
		}
	}
	private void set_sendingicons() {
		switch(get_parent().get_sendproperties().get_sendingtype()) {
			case SendProperties.SENDING_TYPE_POLYGON_:
				m_radio_activate.setIcon(m_icon_polygon_no_edit);
				m_radio_activate.setSelectedIcon(m_icon_polygon_edit);
				//m_radio_activate.setActionCommand("act_sendingtype_polygon");
				m_radio_activate.setEnabled(false);
				break;
			case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
				m_radio_activate.setIcon(m_icon_gis_edit);
				m_radio_activate.setSelectedIcon(m_icon_gis_edit);
				//m_radio_activate.setEnabled(false);
				m_radio_activate.setActionCommand("act_preview_gislist");
				m_radio_activate.setEnabled(true);
				break;
		}
		switch(get_parent().get_sendproperties().get_sendingtype()) {
			case SendProperties.SENDING_TYPE_POLYGON_:
			case SendProperties.SENDING_TYPE_CIRCLE_:
			case SendProperties.SENDING_TYPE_POLYGONAL_ELLIPSE_:
				m_radio_sendingtype_polygon.setEnabled(true);
				m_radio_sendingtype_ellipse.setEnabled(true);
				m_radio_sendingtype_municipal.setEnabled(true);
				m_radio_sendingtype_polygonal_ellipse.setEnabled(true);
				m_radio_activate.setEnabled(true);
				break;
			case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
				m_radio_sendingtype_polygon.setEnabled(false);
				m_radio_sendingtype_ellipse.setEnabled(false);
				m_radio_sendingtype_municipal.setEnabled(false);
				m_radio_sendingtype_polygonal_ellipse.setEnabled(false);
				m_radio_activate.setEnabled(false);
				break;				
		}
	}
	public void init() {
		try {
		m_txt_sendname = new StdTextArea(String.format(PAS.l("main_sending_init_sendingname"), m_n_initsendnumber), false);
		this.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(m_txt_sendname.getText(), TitledBorder.TOP));
		m_txt_sendname.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e){				
			}
			public void keyReleased(KeyEvent e){
				setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(m_txt_sendname.getText()));
				//TitledBorder b = (TitledBorder)getBorder();
				//b.setTitle(m_txt_sendname.getText());
			}
			public void keyTyped(KeyEvent e){
			}
		});
		set_size(m_txt_sendname, SIZE_TXT, 20);
		m_group_sendingtype = new ButtonGroup();

		if(PAS.icon_version==2)
			m_radio_sendingtype_polygon = new JToggleButton(ImageLoader.load_icon("send_polygon_24.png"), true);
		else
			m_radio_sendingtype_polygon = new JToggleButton(ImageLoader.load_icon("send_polygon.gif"), true);
		if(PAS.icon_version==2)
			m_radio_sendingtype_ellipse	= new JToggleButton(ImageLoader.load_icon("send_ellipse_24.png"));
		else
			m_radio_sendingtype_ellipse	= new JToggleButton(ImageLoader.load_icon("send_ellipse.gif"));
		m_radio_sendingtype_polygonal_ellipse = new JToggleButton(ImageLoader.load_icon("send_ellipse_24.png"));
		if(PAS.icon_version==2)
			m_radio_sendingtype_municipal = new JToggleButton(ImageLoader.load_icon("send_municipal_24.png"));
		else
			m_radio_sendingtype_municipal = new JToggleButton(ImageLoader.load_icon("send_municipal.png"));
			
		if(PAS.icon_version==2)
			m_btn_goto = new JButton(ImageLoader.load_icon("find_24.png"));
		else
			m_btn_goto = new JButton(ImageLoader.load_icon("gnome-searchtool_16x16.jpg"));
		//m_btn_goto = new JButton(ImageLoader.load_icon("search_32.png"));
		
		if(PAS.icon_version==2)
			m_btn_adrtypes_private_fixed = new ToggleAddresstype(ImageLoader.load_icon("phone_24.png"), false, SendController.SENDTO_FIXED_PRIVATE);
		else
			m_btn_adrtypes_private_fixed = new ToggleAddresstype(ImageLoader.load_icon("category_phonecalls_16.png"), false, SendController.SENDTO_FIXED_PRIVATE);
		
		if(PAS.icon_version==2)
			m_btn_adrtypes_company_fixed = new ToggleAddresstype(ImageLoader.load_icon("phone_24.png"), false, SendController.SENDTO_FIXED_COMPANY);
		else
			m_btn_adrtypes_company_fixed = new ToggleAddresstype(ImageLoader.load_icon("voice.gif"), false, SendController.SENDTO_FIXED_COMPANY);
		
		if(PAS.icon_version==2)
			m_btn_adrtypes_private_mobile  = new ToggleAddresstype(ImageLoader.load_icon("mobile_24.png"), false, SendController.SENDTO_MOBILE_PRIVATE);
		else
			m_btn_adrtypes_private_mobile  = new ToggleAddresstype(ImageLoader.load_icon("gsm.gif"), false, SendController.SENDTO_MOBILE_PRIVATE);
		
		if(PAS.icon_version==2)
			m_btn_adrtypes_company_mobile = new ToggleAddresstype(ImageLoader.load_icon("mobile_24.png"), false, SendController.SENDTO_MOBILE_COMPANY);
		else
			m_btn_adrtypes_company_mobile = new ToggleAddresstype(ImageLoader.load_icon("gsm.gif"), false, SendController.SENDTO_MOBILE_COMPANY);
		
		m_btn_adrtypes_nophone_private = new ToggleAddresstype(ImageLoader.load_icon("inhab_private_no_phone.png"), false, SendController.SENDTO_NOPHONE_PRIVATE);
		m_btn_adrtypes_nophone_company = new ToggleAddresstype(ImageLoader.load_icon("inhab_company_no_phone.png"), false, SendController.SENDTO_NOPHONE_COMPANY);
		
		if(PAS.icon_version==2)
			m_btn_adrtypes_cell_broadcast_text = new ToggleAddresstype(ImageLoader.load_icon("lba_24.png"), false, SendController.SENDTO_CELL_BROADCAST_TEXT);
		else
			m_btn_adrtypes_cell_broadcast_text = new ToggleAddresstype(ImageLoader.load_icon("cell_broadcast_text.png"), false, SendController.SENDTO_CELL_BROADCAST_TEXT);
		m_btn_adrtypes_cell_broadcast_voice = new ToggleAddresstype(ImageLoader.load_icon("cell_broadcast_voice.png"), false, SendController.SENDTO_CELL_BROADCAST_VOICE);
		if(PAS.icon_version==2)
			m_btn_adrtypes_nofax = new ToggleAddresstype(ImageLoader.load_icon("flag_red_24.png"), false, SendController.SENDTO_USE_NOFAX_COMPANY);
		else
			m_btn_adrtypes_nofax = new ToggleAddresstype(ImageLoader.load_icon("flag_red_16.gif"), false, SendController.SENDTO_USE_NOFAX_COMPANY);
		//m_btngroup_lba.add(m_btn_adrtypes_cell_broadcast_text);
		//m_btngroup_lba.add(m_btn_adrtypes_cell_broadcast_voice);
		m_btn_color = new ColorButton(new Color((float)1.0, (float)0.0, (float)0.0), new Dimension(SIZE_BUTTON_ICON, SIZE_BUTTON_ICON));
		
		if(PAS.icon_version==2)
			m_btn_finalize = new JToggleButton(ImageLoader.load_icon("lock_24.png"), false);
		else
			m_btn_finalize = new JToggleButton(ImageLoader.load_icon("lock.gif"), false);
		if(PAS.icon_version==2)
			m_btn_send = new JButton(ImageLoader.load_icon("outbox_24.png"));
		else
			m_btn_send = new JButton(ImageLoader.load_icon("sendmail.gif"));
		if(PAS.icon_version==2)
			m_btn_close = new JButton(ImageLoader.load_icon("delete_24.png"));
		else
			m_btn_close = new JButton(ImageLoader.load_icon("no.gif"));
		//m_radio_activate.setDisabledSelectedIcon(load_icon("no.gif"));
		//m_radio_activate.setSelectedIcon(load_icon("yes.gif"));
		
		if(PAS.icon_version==2)
			m_icon_polygon_no_edit = ImageLoader.load_icon("brush_disabled_16.png");
		else
			m_icon_polygon_no_edit = ImageLoader.load_icon("no_edit.gif");
		if(PAS.icon_version==2)
			m_icon_polygon_edit = ImageLoader.load_icon("brush_16.png");
		else
			m_icon_polygon_edit = ImageLoader.load_icon("edit.gif");
		m_icon_gis_no_edit = ImageLoader.load_icon("no_edit_gis_import.png");
		m_icon_gis_edit = ImageLoader.load_icon("edit_gis_import.png");		
		
		m_radio_activate = new JRadioButton(m_icon_polygon_no_edit, true);
		//set_sendingicons();
		//m_radio_activate.setSelectedIcon(m_icon_polygon_edit);
		
		if(PAS.icon_version==2)
			m_btn_open = new JButton(ImageLoader.load_icon("folder_open_24.png"));		
		else
			m_btn_open = new JButton(ImageLoader.load_icon("open.gif"));

		m_radio_sendingtype_polygon.setToolTipText(PAS.l("main_sending_type_polygon"));
		m_radio_sendingtype_ellipse.setToolTipText(PAS.l("main_sending_type_ellipse"));
		m_radio_sendingtype_polygonal_ellipse.setToolTipText(PAS.l("main_sending_type_ellipse") + " (polygonal)");
		m_radio_sendingtype_municipal.setToolTipText(PAS.l("main_sending_type_municipal"));
		m_btn_goto.setToolTipText(PAS.l("main_status_show_map_of_sending"));
		m_btn_adrtypes_private_fixed.setToolTipText(PAS.l("main_sending_adr_btn_fixed_private_tooltip"));
		m_btn_adrtypes_private_mobile.setToolTipText(PAS.l("main_sending_adr_btn_mobile_private_tooltip"));
		m_btn_adrtypes_company_fixed.setToolTipText(PAS.l("main_sending_adr_btn_fixed_company_tooltip"));
		m_btn_adrtypes_company_mobile.setToolTipText(PAS.l("main_sending_adr_btn_mobile_company_tooltip"));
		m_btn_adrtypes_nophone_private.setToolTipText(PAS.l("main_sending_adr_btn_nophone_private_tooltip"));
		m_btn_adrtypes_nophone_company.setToolTipText(PAS.l("main_sending_adr_btn_nophone_company_tooltip"));
		m_btn_adrtypes_cell_broadcast_text.setToolTipText(PAS.l("main_sending_adr_btn_lba_text_tooltip"));
		m_btn_adrtypes_cell_broadcast_voice.setToolTipText(PAS.l("main_sending_adr_btn_lba_voice_tooltip"));
		m_btn_adrtypes_nofax.setToolTipText(PAS.l("main_sending_adr_btn_company_blocklist_tooltip"));
		m_btn_finalize.setToolTipText(PAS.l("main_sending_adr_btn_lock_tooltip"));
		m_btn_color.setToolTipText(PAS.l("common_color"));
		m_btn_send.setToolTipText(PAS.l("main_sending_send"));
		m_btn_close.setToolTipText(PAS.l("main_sending_adr_btn_close_sending"));
		m_btn_open.setToolTipText(PAS.l("mainmenu_file_import"));
		
		m_group_sendingtype.add(m_radio_sendingtype_polygon);
		m_group_sendingtype.add(m_radio_sendingtype_ellipse);
		m_group_sendingtype.add(m_radio_sendingtype_polygonal_ellipse);
		m_group_sendingtype.add(m_radio_sendingtype_municipal);
		set_size(m_radio_sendingtype_polygon, SIZE_BUTTON_LARGE);
		set_size(m_radio_sendingtype_ellipse, SIZE_BUTTON_LARGE);
		set_size(m_radio_sendingtype_polygonal_ellipse, SIZE_BUTTON_LARGE);
		set_size(m_radio_sendingtype_municipal, SIZE_BUTTON_LARGE);
		set_size(m_btn_goto, SIZE_BUTTON_LARGE);
		
		set_size(m_btn_adrtypes_private_fixed, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_private_mobile, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_company_fixed, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_company_mobile,  SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_nophone_private, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_nophone_company, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_cell_broadcast_text, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_cell_broadcast_voice, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_nofax, SIZE_BUTTON_LARGE);
		
		set_size(m_btn_color, SIZE_BUTTON_ICON, SIZE_BUTTON_ICON);
		set_size(m_btn_send, SIZE_BUTTON_LARGE);
		set_size(m_btn_open, SIZE_BUTTON_LARGE);
		set_size(m_radio_activate, SIZE_BUTTON_SMALL);
		set_size(m_btn_finalize, SIZE_BUTTON_LARGE);
		set_size(m_btn_close, SIZE_BUTTON_LARGE);
		
		m_radio_sendingtype_polygon.addActionListener(this);
		m_radio_sendingtype_ellipse.addActionListener(this);
		m_radio_sendingtype_polygonal_ellipse.addActionListener(this);
		m_radio_sendingtype_municipal.addActionListener(this);
		m_btn_goto.addActionListener(this);
		m_btn_adrtypes_private_fixed.addActionListener(this);
		m_btn_adrtypes_company_fixed.addActionListener(this);
		m_btn_adrtypes_private_mobile.addActionListener(this);
		m_btn_adrtypes_company_mobile.addActionListener(this);
		m_btn_adrtypes_nophone_private.addActionListener(this);
		m_btn_adrtypes_nophone_company.addActionListener(this);
		m_btn_adrtypes_cell_broadcast_text.addActionListener(this);
		m_btn_adrtypes_cell_broadcast_voice.addActionListener(this);
		m_btn_adrtypes_nofax.addActionListener(this);
		
				
		m_btn_color.addActionListener(this);
		m_radio_activate.addActionListener(this);
		m_btn_send.addActionListener(this);
		m_btn_finalize.addActionListener(this);
		m_btn_close.addActionListener(this);
		m_btn_open.addActionListener(this);
		ActionEvent e = new ActionEvent(m_radio_activate, ActionEvent.ACTION_PERFORMED, "act_register_activation_btn");
		//if(get_parent().get_sendcontroller() != null)
			get_callback().actionPerformed(e);

		m_btn_color.setActionCommand("act_open_colorpicker");
		m_radio_activate.setActionCommand("act_activate_sending");
		m_btn_goto.setActionCommand("act_find_sending");
		m_btn_send.setActionCommand("act_send_one");
		m_radio_sendingtype_polygon.setActionCommand("act_sendingtype_polygon");
		m_radio_sendingtype_ellipse.setActionCommand("act_sendingtype_ellipse");
		m_radio_sendingtype_polygonal_ellipse.setActionCommand("act_sendingtype_polygonal_ellipse");
		m_radio_sendingtype_municipal.setActionCommand("act_sendingtype_municipal");
		m_btn_adrtypes_private_fixed.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_company_fixed.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_private_mobile.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_company_mobile.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nophone_private.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nophone_company.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_cell_broadcast_text.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_cell_broadcast_voice.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nofax.setActionCommand("act_set_addresstypes");
		m_btn_close.setActionCommand("act_sending_close");
		m_btn_open.setActionCommand("act_open_polygon");
		
		m_btn_adrtypes_cell_broadcast_text.setSelected(false);
		m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
		m_btn_adrtypes_nofax.setSelected(false);
		
		//System.out.println(PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_cellbroadcast_rights());
		if(PAS.get_pas() != null && !PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_rights_management().cell_broadcast())
			show_buttons(SendOptionToolbar.BTN_CELL_BROADCAST_,false);
		
		} catch(Exception e) {
			//PAS.get_pas().add_event("Error in SendOptionToolbar.init(): " + e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendOptionToolbar","Exception in init",e,1);
		}
		add_controls();
		
		//Substance 3.3
		Color c = SubstanceLookAndFeel.getActiveColorScheme().getMidColor();
		
		//Substance 5.2
		//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getMidColor();
		
		//PRIVATE MOBILE
		group_smsprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_none"), new Integer(0), new Integer(0), true, PAS.l("main_sending_adr_sel_private_mobile_none_tooltip"), c));
		group_smsprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_voice"), new Integer(SendController.SENDTO_MOBILE_PRIVATE), new Integer(SendController.SENDTO_FIXED_PRIVATE_ALT_SMS | SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE), true, PAS.l("main_sending_adr_sel_private_mobile_voice_tooltip"), c));
		group_smsprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_sms"), new Integer(SendController.SENDTO_SMS_PRIVATE), new Integer(SendController.SENDTO_FIXED_PRIVATE_ALT_SMS), false, PAS.l("main_sending_adr_sel_private_mobile_sms_tooltip"), c));
		group_smsprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_voice_and_sms"), new Integer(SendController.SENDTO_MOBILE_PRIVATE|SendController.SENDTO_SMS_PRIVATE), new Integer(SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE | SendController.SENDTO_FIXED_PRIVATE_ALT_SMS), false, PAS.l("main_sending_adr_sel_private_mobile_voice_and_sms_tooltip"), c));
		group_smsprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), new Integer(SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED), new Integer(SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_FIXED_PRIVATE_ALT_SMS | SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE), false, PAS.l("main_sending_adr_sel_private_mobile_voice_and_fixed_tooltip"), c));
		group_smsprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_sms_or_fixed"), new Integer(SendController.SENDTO_SMS_PRIVATE_ALT_FIXED), new Integer(SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_FIXED_PRIVATE_ALT_SMS | SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE), false, PAS.l("main_sending_adr_sel_private_mobile_sms_or_fixed_tooltip"), c));
		
		//PRIVATE FIXED
		group_fixedprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_none"), new Integer(0), new Integer(0), true, PAS.l("main_sending_adr_sel_private_fixed_none_tooltip"), c));
		group_fixedprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_voice"), new Integer(SendController.SENDTO_FIXED_PRIVATE), new Integer(SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED | SendController.SENDTO_SMS_PRIVATE_ALT_FIXED), true, PAS.l("main_sending_adr_sel_private_fixed_voice_tooltip"), c));
		group_fixedprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_and_mobile"), new Integer(SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE), new Integer(SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED | SendController.SENDTO_SMS_PRIVATE_ALT_FIXED | SendController.SENDTO_MOBILE_PRIVATE), true, PAS.l("main_sending_adr_sel_private_fixed_and_mobile_tooltip"), c));
		group_fixedprivbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_alt_sms"), new Integer(SendController.SENDTO_FIXED_PRIVATE_ALT_SMS), new Integer(SendController.SENDTO_SMS_PRIVATE | SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED | SendController.SENDTO_SMS_PRIVATE_ALT_FIXED | SendController.SENDTO_MOBILE_PRIVATE), false, PAS.l("main_sending_adr_sel_private_fixed_alt_sms_tooltip"), c));

		
		//COMPANY MOBILE
		group_smscompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_none"), new Integer(0), new Integer(0), true, PAS.l("main_sending_adr_sel_company_mobile_none_tooltip"), Color.yellow));
		group_smscompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_voice"), new Integer(SendController.SENDTO_MOBILE_COMPANY), new Integer(SendController.SENDTO_FIXED_COMPANY_ALT_SMS | SendController.SENDTO_FIXED_COMPANY_AND_MOBILE), true, PAS.l("main_sending_adr_sel_private_mobile_voice_tooltip"), c));
		group_smscompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_sms"), new Integer(SendController.SENDTO_SMS_COMPANY), new Integer(SendController.SENDTO_FIXED_COMPANY_ALT_SMS), false, PAS.l("main_sending_adr_sel_private_mobile_sms_tooltip"), c));
		group_smscompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_voice_and_sms"), new Integer(SendController.SENDTO_MOBILE_COMPANY|SendController.SENDTO_SMS_COMPANY), new Integer(SendController.SENDTO_FIXED_COMPANY_AND_MOBILE | SendController.SENDTO_FIXED_COMPANY_ALT_SMS), false, PAS.l("main_sending_adr_sel_private_mobile_voice_and_sms_tooltip"), c));
		group_smscompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), new Integer(SendController.SENDTO_MOBILE_COMPANY_AND_FIXED), new Integer(SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_FIXED_COMPANY_ALT_SMS | SendController.SENDTO_FIXED_COMPANY_AND_MOBILE), false, PAS.l("main_sending_adr_sel_private_mobile_voice_and_fixed_tooltip"), c));
		group_smscompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_mobile_sms_or_fixed"), new Integer(SendController.SENDTO_SMS_COMPANY_ALT_FIXED), new Integer(SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_FIXED_COMPANY_ALT_SMS | SendController.SENDTO_FIXED_COMPANY_AND_MOBILE), false, PAS.l("main_sending_adr_sel_private_mobile_sms_or_fixed_tooltip"), c));
		
		//COMPANY FIXED
		group_fixedcompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_none"), new Integer(0), new Integer(0), true, PAS.l("main_sending_adr_sel_company_fixed_none_tooltip"), c));
		group_fixedcompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_voice"), new Integer(SendController.SENDTO_FIXED_COMPANY), new Integer(SendController.SENDTO_MOBILE_COMPANY_AND_FIXED | SendController.SENDTO_SMS_COMPANY_ALT_FIXED), true, PAS.l("main_sending_adr_sel_private_fixed_voice_tooltip"), c));
		group_fixedcompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_and_mobile"), new Integer(SendController.SENDTO_FIXED_COMPANY_AND_MOBILE), new Integer(SendController.SENDTO_MOBILE_COMPANY_AND_FIXED | SendController.SENDTO_SMS_COMPANY_ALT_FIXED | SendController.SENDTO_MOBILE_COMPANY), true, PAS.l("main_sending_adr_sel_private_fixed_and_mobile_tooltip"), c));
		group_fixedcompbtn.add(new CheckItem(PAS.l("main_sending_adr_sel_private_fixed_alt_sms"), new Integer(SendController.SENDTO_FIXED_COMPANY_ALT_SMS), new Integer(SendController.SENDTO_MOBILE_COMPANY | SendController.SENDTO_SMS_COMPANY | SendController.SENDTO_MOBILE_COMPANY_AND_FIXED | SendController.SENDTO_SMS_COMPANY_ALT_FIXED | SendController.SENDTO_MOBILE_COMPANY), false, PAS.l("main_sending_adr_sel_private_mobile_sms_or_fixed_tooltip"), c));
	
		add_adrtypemenus(m_btn_adrtypes_private_mobile, menu_smspriv, group_smsprivbtn, "      Private Mobile Phones");
		add_adrtypemenus(m_btn_adrtypes_private_fixed, menu_fixedpriv, group_fixedprivbtn, "      Private Fixed Phones");
		add_adrtypemenus(m_btn_adrtypes_company_mobile, menu_smscomp, group_smscompbtn, "      Company Mobile Phones");
		add_adrtypemenus(m_btn_adrtypes_company_fixed, menu_fixedcomp, group_fixedcompbtn, "      Company Fixed Phones");
		//_SelectAddressSelection(m_n_addresstypes);
		
		if(PAS.get_pas() != null)
			add_municipals(m_radio_sendingtype_municipal, menu_municipals, "Select municipals");
		
		lock_sending(false);
	}
	/*private ImageIcon load_icon(String sz_filename) {
		URL url_icon = null;
		ImageIcon icon = null;
		try { 
			url_icon = new URL(get_pas().get_sitename() + "/java_pas/images/" + sz_filename);
			icon = new ImageIcon(url_icon);
		} catch(MalformedURLException e) {
			
		}
		return icon;
	}*/
	
	public void set_size(java.awt.Component c, int n_width) {
		//c.setPreferredSize(new Dimension(n_width, 30));
		set_size(c, n_width, 30);
	}
	public void set_size(java.awt.Component c, int n_width, int n_height) {
		c.setPreferredSize(new Dimension(n_width, n_height));		
	}
	
	protected void add_municipals(final AbstractButton btn, final JPopupMenu pop, String sz_label)
	{
		StdTextLabel lbl = new StdTextLabel(sz_label, 14, true);
		//lbl.setIcon(UMS.Tools.ImageLoader.load_icon("no.gif"));

		//Substance 3.3
		lbl.setBackground(SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor());
		
		//Substance 5.2
		//lbl.setBackground(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraDarkColor());

		
		pop.add(lbl);
		SendPropertiesMunicipal mun;
		MunicipalCheckbox chk;
		DeptInfo dept = PAS.get_pas().get_userinfo().get_current_department();
		for(int i = 0; i < dept.get_municipals().size(); i++)
		{
			UMunicipalDef mu = dept.get_municipals().get(i);
			chk = new MunicipalCheckbox(new Municipal(mu.getSzMunicipalid(), mu.getSzMunicipalname()));
			chk.addActionListener(this);
			chk.setActionCommand("act_sendingtype_municipal");
			pop.add(chk);
		}
		//pop.add(new JButton("close"));
		
		
		btn.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent evt) {
	        	try
	        	{
	        		//Thread.sleep(60);
	        	}catch(Exception e) {}
	        	//super.mousePressed(evt);
	        	//btn.doClick();
	        	btn.setSelected(true);
	        	pop.setLightWeightPopupEnabled(true);
	        	
	        	/*if(!pop.isVisible())
	        	{
	        		pop.show(evt.getComponent(), 0, btn.getHeight());
	        		pop.setVisible(true);
	        	}
	        	else
	        		pop.setVisible(false);*/
				Point componentLocation = btn.getLocationOnScreen();
				if(!pop.isShowing())
					pop.show(null, componentLocation.x, componentLocation.y + btn.getHeight());
				else
					pop.setVisible(false);
	        	
	        }
	        public void mouseReleased(MouseEvent evt) {
	        }
	    });
		/*btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Point componentLocation = btn.getLocationOnScreen();
				//pop.setPreferredSize(new Dimension(150, 500));
				//pop.setMinimumSize(new Dimension(150, 100));
				//pop.setPreferredSize(new Dimension(150, pop.getHeight()));
				if(!pop.isShowing())
					pop.show(null, componentLocation.x, componentLocation.y + btn.getHeight());
				else
					pop.setVisible(false);
			}
		});*/

	}
	
	public class MunicipalCheckbox extends JCheckBox
	{
		//protected String sz_munid;
		//protected String sz_munname;
		protected Municipal mun;
		public Municipal getMunicipal() { return mun; }
		public MunicipalCheckbox(Municipal m)
		{
			super(m.get_id() + "     " + m.get_name());
			mun = m;
			//this.sz_munid = sz_munid;
			//this.sz_munname = sz_munname;
		}
		@Override
		public String toString()
		{
			return mun.get_id() + "     " + mun.get_name();
		}
	}
	protected void add_adrtypemenus(final AbstractButton btn, final JPopupMenu pop, ButtonGroup group, String sz_label)
	{
		StdTextLabel lbl = new StdTextLabel(sz_label, 14, true);
		//Substance 3.3
		lbl.setBackground(SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor());
		
		//Substance 5.2
		//lbl.setBackground(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraDarkColor());
		
		
		//lbl.setBorder(UMS.Tools.TextFormat.CreateStdBorder(""));
		pop.add(lbl);
		java.util.Enumeration<AbstractButton> e = group.getElements();
		while(e.hasMoreElements())
		{
			AbstractButton b = e.nextElement();
			b.addActionListener(this);
			b.setActionCommand("act_set_addresstypes");
			pop.add(b);
		}
		btn.addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent evt) {
	            //if (evt.isPopupTrigger()) {
	        	actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	        	if(!get_parent().isLocked())
	        		pop.show(evt.getComponent(), 0, btn.getHeight());
	            //}
	        }
	        public void mouseReleased(MouseEvent evt) {
	        	//btn.setSelected(false);
	            //if (evt.isPopupTrigger()) {
	        	//pop.show(evt.getComponent(), 0, btn.getHeight());
	        	
	            //}
	        }
	    });
	}
	
	public void add_controls() {
		DefaultPanel.ENABLE_GRID_DEBUG = false;
		JLabel m_place_holder;
		add_spacing(DIR_VERTICAL, 5);
		//inc_panels();
		this.reset_xpanels();
		this.set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.WEST);
		add(m_radio_activate, m_gridconst);
		set_gridconst(inc_xpanels(), get_panel(), 11, 1, GridBagConstraints.WEST);
		add(m_txt_sendname, m_gridconst);
		inc_xpanels2(9);
		//addSeparator();
		//inc_xpanels2();
		//addSeparator();
		//inc_xpanels2();
		add_spacing(DIR_HORIZONTAL, 30);
		inc_xpanels2();
		m_gridconst.anchor = GridBagConstraints.CENTER;
		add(m_btn_color, m_gridconst);
		inc_xpanels2();
		//this.addSeparator();
		//inc_panels2();
		add(m_radio_sendingtype_polygon, m_gridconst);
		inc_xpanels2();
		add(m_radio_sendingtype_ellipse, m_gridconst);
		//inc_xpanels2();
		//add(m_radio_sendingtype_polygonal_ellipse, m_gridconst);
		inc_xpanels2();
		add(m_radio_sendingtype_municipal, m_gridconst);
		inc_xpanels2();
		//addSeparator();
		add(m_btn_open, m_gridconst);
		inc_xpanels2();
		add_spacing(DIR_VERTICAL, 15);
		
		//this.addSeparator();
		//this.set_gridconst(get_xpanel(), inc_panels(), 1, 1, GridBagConstraints.WEST);
		this.reset_xpanels();
		inc_panels2();
		inc_xpanels2();
	
		add(m_btn_adrtypes_private_fixed, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(5);
		inc_xpanels2();
		add(m_btn_adrtypes_private_mobile, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(15);
		inc_xpanels2();
		add(m_btn_adrtypes_company_fixed, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(5);
		inc_xpanels2();
		add(m_btn_adrtypes_company_mobile, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(15);
		inc_xpanels2();
		add(m_btn_adrtypes_nofax, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(15);
		/*inc_xpanels2();
		add(m_btn_adrtypes_nophone_private, m_gridconst);
		//addSeparator();
		inc_xpanels2();
		add(m_btn_adrtypes_nophone_company, m_gridconst);*/
		//addSeparator();
		inc_xpanels2();
		//inc_xpanels2();
		add(m_btn_adrtypes_cell_broadcast_text, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		//inc_xpanels2();
		//inc_xpanels2(2);
		add_spacing(DIR_HORIZONTAL, 30);
		inc_xpanels2();
		//inc_panels2();
		//add(m_btn_adrtypes_cell_broadcast_voice, m_gridconst);
		//inc_xpanels2();
		
		m_btn_adrtypes_nophone_private.setSelected(false);
		m_btn_adrtypes_nophone_company.setSelected(false);
		m_btn_adrtypes_nophone_private.setEnabled(false);
		m_btn_adrtypes_nophone_company.setEnabled(false);
		//m_btn_adrtypes_cell_broadcast_text.setEnabled(false);

		//this.addSeparator();
		//add_spacing(DIR_VERTICAL, 5);
		add(m_btn_goto, m_gridconst);
		inc_xpanels2();
		//addSeparator();
		
		//this.addSeparator();
		
		//this.addSeparator();
		add(m_btn_finalize, m_gridconst);
		inc_xpanels2();
		add(m_btn_send, m_gridconst);
		inc_xpanels2();
		//this.addSeparator();
		add(m_btn_close, m_gridconst);
		inc_xpanels2();
		inc_panels2();
		set_gridconst(1, get_panel(), 8, 1, GridBagConstraints.NORTHWEST);	
		add(m_lbl_addresstypes_private, m_gridconst);
		set_gridconst(5, get_panel(), 8, 1, GridBagConstraints.NORTHWEST);
		add(m_lbl_addresstypes_company, m_gridconst);
		set_gridconst(10, get_panel(), 8, 1, GridBagConstraints.NORTHWEST);
		add(m_lbl_addresstypes_lba, m_gridconst);
		init_addresstypes(0); //SendController.SENDTO_ALL);
		add_spacing(DIR_VERTICAL, 5);

		doLayout();
	}
	public void setActive() {
		//ActionEvent e = new ActionEvent(get_parent(), ActionEvent.ACTION_PERFORMED, "act_activate_sending");
		//actionPerformed(e);
		if(!get_parent().get_sendproperties().getClass().equals(SendPropertiesGIS.class)) {
			System.out.println(m_radio_activate.getActionCommand());
			m_radio_activate.doClick();
		}
	}
	
	public void setActiveShape(ShapeStruct s) {
		if(s.get_fill_color()!=null)
			get_parent().get_sendproperties().set_color(s.get_fill_color());
		//get_parent().get_sendproperties().typecast_poly().set_polygon(p);
		get_parent().get_sendproperties().set_shapestruct(s);
		lock_sending(false);
		
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_open_colorpicker".equals(e.getActionCommand())) {
			m_colorpicker = new SendingColorPicker("Color",
					new Point(0,0), get_parent().get_sendproperties().get_shapestruct().get_fill_color(), this);			
			get_colorpicker().show();
		}
		else if("act_set_color".equals(e.getActionCommand())) {
			/*get_colorbutton().setBackground((Color)e.getSource());
			get_colorbutton().setForeground((Color)e.getSource());*/
			get_colorbutton().setBg((Color)e.getSource());
			//((SendPropertiesPolygon)get_parent().get_sendproperties()).get_polygon().set_fill_color((Color)e.getSource());
			Color c = (Color)e.getSource();
			get_parent().get_sendproperties().get_shapestruct().set_fill_color((Color)e.getSource());
			get_parent().get_sendproperties().set_color((Color)e.getSource());
			//PAS.get_pas().kickRepaint();
			get_callback().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_kick_repaint"));
		}
		else if("act_activate_sending".equals(e.getActionCommand())) {
			e.setSource(get_parent());
			//get_parent().get_sendcontroller().actionPerformed(e);
			get_callback().actionPerformed(e);
		}
		else if("act_send_one".equals(e.getActionCommand())) {
			try {
				//this.gen_addresstypes();
				
				System.out.println("Addressfilter: " + this.get_addresstypes());
				e.setSource(get_parent());
				//e.setSource(this);
				switch(get_parent().get_sendproperties().get_sendingtype()) {
					case SendProperties.SENDING_TYPE_POLYGON_:
						try {
							//get_parent().set_sendproperties(new SendPropertiesPolygon(new PolygonStruct(get_pas()), this));
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New polygon sending");
						} catch(Exception err) {
							err.printStackTrace();
							//PAS.get_pas().add_event("ERROR New Sending : " + err.getMessage());
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
						}
						break;
					case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
						try {
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New GIS sending");
						} catch(Exception err) {
							err.printStackTrace();
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
						}
						break;
					case SendProperties.SENDING_TYPE_CIRCLE_:
						try {
							//get_parent().set_sendproperties(new SendPropertiesEllipse(this));
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New ellipse sending");
						} catch(Exception err) {
							err.printStackTrace();
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
						}
						break;
					case SendProperties.SENDING_TYPE_MUNICIPAL_:
						try {
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New Municipal sending");							
						} catch(Exception err) {
							err.printStackTrace();
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);							
						}
				}
				//get_parent().get_sendcontroller().actionPerformed(e);
				get_callback().actionPerformed(e);
			} catch(Exception err) {
				//PAS.get_pas().add_event("ERROR: act_send_one - " + err.getMessage());
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}
		}
		else if("act_set_addresstypes".equals(e.getActionCommand())) {
			/*if(m_btn_adrtypes_cell_broadcast_voice.isSelected() || m_btn_adrtypes_cell_broadcast_text.isSelected()) {
				if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_voice) && ((!m_btn_adrtypes_cell_broadcast_voice.isSelected() && !m_btn_adrtypes_cell_broadcast_text.isSelected()) || (!m_btn_adrtypes_cell_broadcast_voice.isSelected() && m_btn_adrtypes_cell_broadcast_text.isSelected()))) {
					m_group_sendingtype.remove(m_btn_adrtypes_cell_broadcast_voice);
					m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
					m_group_sendingtype.add(m_btn_adrtypes_cell_broadcast_voice);
				} else {
					//m_btn_adrtypes_cell_broadcast_voice.setSelected(true);				
				}
				if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_text) && ((!m_btn_adrtypes_cell_broadcast_text.isSelected() && !m_btn_adrtypes_cell_broadcast_voice.isSelected()) || (!m_btn_adrtypes_cell_broadcast_text.isSelected() && m_btn_adrtypes_cell_broadcast_voice.isSelected()))) {
					m_group_sendingtype.remove(m_btn_adrtypes_cell_broadcast_text);
					m_btn_adrtypes_cell_broadcast_text.setSelected(false);
					m_group_sendingtype.add(m_btn_adrtypes_cell_broadcast_text);
				} else {
					//m_btn_adrtypes_cell_broadcast_text.setSelected(true);				
				}
			}*/
			if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_voice) && m_btn_adrtypes_cell_broadcast_text.isSelected())
				m_btn_adrtypes_cell_broadcast_text.setSelected(false);
			if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_text) && m_btn_adrtypes_cell_broadcast_voice.isSelected())
				m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
				
			gen_addresstypes();
			if(get_parent().isActive()) {
				//if(((ToggleAddresstype)e.getSource()).isSelected())
					get_callback().actionPerformed(e);
					//PAS.get_pas().get_housecontroller().add_addresstype(((ToggleAddresstype)e.getSource()).get_adrtype());
				//else
					//PAS.get_pas().get_housecontroller().rem_addresstype(((ToggleAddresstype)e.getSource()).get_adrtype());
				//PAS.get_pas().kickRepaint();
			}
			//if(report_addresschanges()!=null)
			//	report_addresschanges().actionPerformed(e);
		}
		else if("act_find_sending".equals(e.getActionCommand())) {
			System.out.println("SendOptionToolbar.act_find_sending");
			get_parent().goto_area();
		}
		else if("act_lock".equals(e.getActionCommand())) {
			lock_sending(true);
		}
		else if("act_unlock".equals(e.getActionCommand())) {
			lock_sending(false);
		}
		else if("act_sendwindow_activated".equals(e.getActionCommand())) {
			//window is opened. Disable unlock feature and send button
			enableEdit(false);
			setActive();
		}
		else if("act_sendwindow_closed".equals(e.getActionCommand())) {
			//window is closed. Enable unlock feature and send button
			enableEdit(true);
		}
		else if("act_sending_close".equals(e.getActionCommand())) {
			//PAS.get_pas().add_event("Destroy sending");
			destroy_sending();
		}
		else if("act_open_polygon".equals(e.getActionCommand())) {
			import_polygon();
		}
		else if("act_sosi_parsing_complete".equals(e.getActionCommand())) {
			ArrayList sendings_found = (ArrayList)e.getSource();
			for(int i=0; i < sendings_found.size(); i++) {
				SendObject obj = (SendObject)sendings_found.get(i);
				if(obj==null)
					continue;
				if(obj.get_sendproperties()==null)
					continue;
				if(obj.get_sendproperties().get_sendingname()==null)
					continue;
				if(i==0)
					this.get_parent().get_sendproperties().set_shapestruct(obj.get_sendproperties().get_shapestruct());
				else
					//PAS.get_pas().get_sendcontroller().add_sending(obj);
					PAS.get_pas().actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_add_sending"));
				System.out.println("Adding sending " + obj.get_sendproperties().get_sendingname());
			
			}
			try {
				PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_center_all_polygon_sendings"));
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("ImportPolygon","Exception in actionPerformed",err,1);
			}

		}
		else if("act_set_shape".equals(e.getActionCommand())) {
			try
			{
				ShapeStruct shape = (ShapeStruct)e.getSource();
				get_parent().get_sendproperties().set_shapestruct(shape);
			}
			catch(Exception err)
			{
				
			}
		}
		else if("act_polygon_imported".equals(e.getActionCommand())) {
			ActionFileLoaded event = (ActionFileLoaded)e;
			SosiFile sosi = (SosiFile)e.getSource();
			//PAS.get_pas().get_drawthread().set_suspended(true);
			try {
				//get_parent().get_sendproperties().typecast_poly().set_shapestruct(new PolygonStruct(PAS.get_pas().get_mappane().get_dimension(), sosi.get_flater().get_current_flate().get_polygon()));
				get_parent().get_sendproperties().typecast_poly().set_shapestruct((PolygonStruct)sosi.get_flater().get_current_flate().get_polygon().clone());
				get_parent().get_sendproperties().typecast_poly().set_polygon_color();
			} catch(Exception err) {
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}
			try {
				String sz_description = sosi.toString();
				get_parent().get_sendproperties().set_sendingname((sosi.get_flater().get_current_flate().get_name().length() > 0 ? sosi.get_flater().get_current_flate().get_name() : sosi.get_flater().get_current_flate().get_objecttype()), sz_description);
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}
			/*ActionEvent e_lock = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_lock");
			actionPerformed(e_lock);*/
			//this.m_btn_finalize.doClick();
			//PAS.get_pas().get_drawthread().set_suspended(false);
			//PAS.get_pas().kickRepaint();
			//variables.NAVIGATION.gotoMap(sosi.get_bounding());
			/*variables.NAVIGATION.gotoMap(sosi.get_polygon().calc_bounds());
			try {
				PAS.get_pas().kickRepaint();
				//Thread.sleep(2000);
			} catch(Exception err) {
				
			}*/
			
		}
		else if("act_register_gis_previewframe".equals(e.getActionCommand())) {
			setPreviewFrame((PreviewFrame)e.getSource());
		}
		else if("act_set_municipals".equals(e.getActionCommand())) {
			//this.actionPerformed(new ActionEvent("Municipal", ActionEvent.ACTION_PERFORMED, "act_sendingtype_municipal"));
			MunicipalCheckbox chk = (MunicipalCheckbox)e.getSource();
			get_parent().get_sendproperties().typecast_municipal().AddMunicipal(chk.getMunicipal(), chk.isSelected());
		}
		else if("act_gis_imported".equals(e.getActionCommand())) {
			//PAS.get_pas().add_event("act_gis_imported", null);
			
			final GISList list = (GISList)e.getSource();
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					get_parent().set_type(SendProperties.SENDING_TYPE_GEMINI_STREETCODE_);
					get_parent().get_sendproperties().typecast_gis().set_gislist(list);
					get_parent().get_sendproperties().set_shapestruct(new GISShape(list));
					try {
						get_parent().get_sendproperties().goto_area();
					} catch(Exception err) {
						
					}
					System.out.println("SendOptionToolbar: " + list.size() + " lines");
				}
			});
		}
		else if("act_sendingtype_polygon".equals(e.getActionCommand())) {
			try
			{
				get_parent().set_type(SendProperties.SENDING_TYPE_POLYGON_);
				get_parent().get_sendproperties().typecast_poly();	
				
				get_callback().actionPerformed(new ActionEvent(new Integer(no.ums.pas.maps.MapFrame.MAP_MODE_SENDING_POLY), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
				get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
				
				resetMunicipals();
				//enableEdit(false);
				//setActive();
			}
			catch(Exception err) { }
		}
		else if("act_sendingtype_ellipse".equals(e.getActionCommand())) {
			try
			{
				get_parent().set_type(SendProperties.SENDING_TYPE_CIRCLE_);
				get_callback().actionPerformed(new ActionEvent(new Integer(no.ums.pas.maps.MapFrame.MAP_MODE_SENDING_ELLIPSE), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
				//if(get_callback()!=null)
				get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
				resetMunicipals();
			}
			catch(Exception err) { }
		}
		else if("act_sendingtype_polygonal_ellipse".equals(e.getActionCommand())) {
			try
			{
				get_parent().set_type(SendProperties.SENDING_TYPE_POLYGONAL_ELLIPSE_);
				get_callback().actionPerformed(new ActionEvent(new Integer(no.ums.pas.maps.MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
				//if(get_callback()!=null)
				get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
				resetMunicipals();				
			}
			catch(Exception err) { }
		}
		else if("act_sendingtype_municipal".equals(e.getActionCommand())) {
			get_parent().set_type(SendProperties.SENDING_TYPE_MUNICIPAL_);
			get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
			if(e.getSource().getClass().equals(MunicipalCheckbox.class))
			{
				MunicipalCheckbox chk = (MunicipalCheckbox)e.getSource();
				get_parent().get_sendproperties().typecast_municipal().AddMunicipal(chk.getMunicipal(), chk.isSelected());
			}

        	//PAS.get_pas().kickRepaint();
		}
		else if("act_preview_gislist".equals(e.getActionCommand())) {
			if(m_gis_preview==null)
				m_gis_preview = new PreviewFrame(this.get_parent());
			else if(!this.getIsAlert())
				m_gis_preview.setVisible(true);
		}
	}
	public void set_sendingname(String sz_name, String sz_description) {
		m_txt_sendname.setText(sz_name);
		m_txt_sendname.setToolTipText(sz_description);
		setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(m_txt_sendname.getText()));
		
	}
	private ActionListener m_import_callback;
	public void set_import_callback(ActionListener a) { m_import_callback = a; }
	public ActionListener get_import_callback() { return m_import_callback; }
	public void import_polygon() {
		new ImportPolygon(m_import_callback, "act_polygon_imported", this.getIsAlert(), m_parent.get_sendwindow());
		// If the code gets to this line, everything should be ok and I can set the status enabling the user to go to the next step
	}
	public void destroy_sending() {
		if(JOptionPane.showConfirmDialog(this, PAS.l("main_sending_adr_btn_close_sending"), PAS.l("common_are_you_sure"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			get_parent().destroy_sending(); //sendobject
	}
	public void enableEdit(boolean b) {
		enableLocking(b);
		enableSendbtn(b);
	}
	public void enableLocking(boolean b) {
		m_btn_finalize.setEnabled(b);
	}
	void enableSendbtn(boolean b) {
		m_btn_send.setEnabled(b);
	}
	public ToggleAddresstype get_adrtype_private_fixed() { return m_btn_adrtypes_private_fixed; }
	public ToggleAddresstype get_adrtype_company_fixed() { return m_btn_adrtypes_company_fixed; }
	public ToggleAddresstype get_adrtype_private_mobile() { return m_btn_adrtypes_private_mobile; }
	public ToggleAddresstype get_adrtype_company_mobile() { return m_btn_adrtypes_company_mobile; }
	public ToggleAddresstype get_adrtype_cell_broadcast_text() { return m_btn_adrtypes_cell_broadcast_text; }
	public ToggleAddresstype get_adrtype_cell_broadcast_voice() { return m_btn_adrtypes_cell_broadcast_voice; }
	public ToggleAddresstype get_adrtype_nofax() { return m_btn_adrtypes_nofax; }
	public JButton get_btn_open() { return m_btn_open; }
	public ColorButton get_btn_color() { return m_btn_color; }
	public JToggleButton get_btn_finalize() { return m_btn_finalize; }
	
	public boolean can_lba()
	{
		boolean b_can_lba = false;
		try
		{
		
			RightsManagement rights = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_rights_management();
			if(!rights.cell_broadcast())
				return false;
			switch(get_parent().get_sendproperties().get_sendingtype())
			{
			case SendProperties.SENDING_TYPE_POLYGON_:
			case SendProperties.SENDING_TYPE_CIRCLE_:
				b_can_lba = true;
				break;
			}
		}
		catch(Exception e)
		{
			
		}
		return b_can_lba;
	}
	
	public void lock_sending(boolean b) {
		if(get_parent().setLocked(b)) {
			//RightsManagement rights = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_rights_management();
			m_btn_send.setEnabled(b);
			m_btn_adrtypes_private_fixed.setEnabled(!b);
			m_btn_adrtypes_company_fixed.setEnabled(!b);
			m_btn_adrtypes_private_mobile.setEnabled(!b);
			m_btn_adrtypes_company_mobile.setEnabled(!b);
			//m_btn_adrtypes_nophone_private.setEnabled(!b);
			//m_btn_adrtypes_nophone_company.setEnabled(!b);
			/*boolean b_can_lba = false;
			try
			{
				switch(get_parent().get_sendproperties().get_sendingtype())
				{
				case SendProperties.SENDING_TYPE_POLYGON_:
				case SendProperties.SENDING_TYPE_CIRCLE_:
					b_can_lba = true;
				}
			}
			catch(Exception e)
			{
				
			}
			if(!b_can_lba)
			{
				if(m_btn_adrtypes_cell_broadcast_text.isSelected())
					m_btn_adrtypes_cell_broadcast_text.doClick();
				if(m_btn_adrtypes_cell_broadcast_voice.isSelected())
					m_btn_adrtypes_cell_broadcast_voice.doClick();				
			}
			if(!b && rights.cell_broadcast())
			{
				if(b_can_lba)
				{
					m_btn_adrtypes_cell_broadcast_text.setEnabled(!b);
					m_btn_adrtypes_cell_broadcast_voice.setEnabled(!b);
				}
				else
				{
				}
			}
			else
			{
				m_btn_adrtypes_cell_broadcast_text.setEnabled(!b);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(!b);				
			}*/
			if(can_lba())
			{
				m_btn_adrtypes_cell_broadcast_text.setEnabled(!b);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(!b);				
			}
			else
			{
				m_btn_adrtypes_cell_broadcast_text.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(false);
			}

			m_btn_color.setEnabled(!b);
			m_radio_sendingtype_polygon.setEnabled(!b);
			m_radio_sendingtype_ellipse.setEnabled(!b);
			m_radio_sendingtype_polygonal_ellipse.setEnabled(!b);
			m_radio_sendingtype_municipal.setEnabled(!b);
			m_txt_sendname.setEditable(!b);
			m_btn_open.setEnabled(!b);
			m_btn_adrtypes_nofax.setEnabled(!b);
			
			//test of auto expand polygon
			//can be removed
			/*if(this.get_parent().get_sendproperties()!=null)
			{
				PolygonStruct poly = (PolygonStruct)this.get_parent().get_sendproperties().get_shapestruct();
				if(poly!=null)
				{
					NavStruct nav = poly.calc_bounds();
					double weight_x = nav._lbo + (nav._rbo - nav._lbo) / 2;
					double weight_y = nav._bbo + (nav._ubo - nav._bbo) / 2;
					int points = poly.get_size();
					float expand = 0.1f;
					int expandtype = 1;
					PolygonStruct ret = new Utils().expandPolygon(PAS.get_pas().get_mapsize(), poly, weight_x, weight_y, points, expand, expandtype);
					get_parent().get_sendproperties().set_shapestruct(ret);
				}
			}*/
		} else {
			//PAS.get_pas().add_event("Cannot lock sending at this time", null);
			m_btn_finalize.setSelected(false);
		}
		if(get_parent().isLocked())
			m_btn_finalize.setActionCommand("act_unlock");
		else
			m_btn_finalize.setActionCommand("act_lock");
	}
	
	public synchronized void focusGained(FocusEvent e) {
		
	}
	public synchronized void focusLost(FocusEvent e) {
		
	}
	
}