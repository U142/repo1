package no.ums.pas.send;


import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.AreaController;
import no.ums.pas.area.AreaController.AreaSource;
import no.ums.pas.area.constants.PredefinedAreaConstants;
import no.ums.pas.area.main.MainAreaController;
import no.ums.pas.area.server.AreaServerCon;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.ChannelType;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.laf.ULookAndFeel;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.RightsManagement;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.importer.ActionFileLoaded;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.importer.SosiFile;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.PreviewFrame;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.Municipal;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendController.ISendingAdded;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ColorButton;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.UMunicipalDef;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class SendOptionToolbar extends DefaultPanel implements ActionListener, FocusListener {

    private static final Log log = UmsLog.getLogger(SendOptionToolbar.class);

	public static final int SIZE_TXT			= 210;
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

//	public final JPopupMenu menu_municipals = new JPopupMenu();
	public final MunicipalJPopupMenu menu_municipals = new MunicipalJPopupMenu(PAS.get_pas());
	
	
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
    JToggleButton m_radio_sendingtype_house_select;

	ButtonGroup m_btngroup_lba;
	public JToggleButton get_radio_polygon() { return m_radio_sendingtype_polygon; }
	public JToggleButton get_radio_ellipse() { return m_radio_sendingtype_ellipse; }
	public JToggleButton get_radio_polygonal_ellipse() { return m_radio_sendingtype_polygonal_ellipse; }
	public JToggleButton get_radio_municipal() { return m_radio_sendingtype_municipal; }
    public JToggleButton get_radio_sendingtype_house_select() { return m_radio_sendingtype_house_select; }
	JButton m_btn_goto;
	public ToggleAddresstype m_btn_adrtypes_private_fixed;
	public ToggleAddresstype m_btn_adrtypes_company_fixed;
	public ToggleAddresstype m_btn_adrtypes_private_mobile;
	public ToggleAddresstype m_btn_adrtypes_company_mobile;
	public ToggleAddresstype m_btn_adrtypes_nophone_private;
	public ToggleAddresstype m_btn_adrtypes_nophone_company;
	public ToggleAddresstype m_btn_adrtypes_cell_broadcast_text;
	public ToggleAddresstype m_btn_adrtypes_cell_broadcast_voice;
	public ToggleAddresstype m_btn_adrtypes_nofax;
	public ToggleAddresstype m_btn_adrtypes_vulnerable;
	public ToggleAddresstype m_btn_adrtypes_headofhousehold;
	
//	public ToggleAddresstype get_cell_broadcast_text() { return m_btn_adrtypes_cell_broadcast_text; }
	public ToggleAddresstype get_cell_broadcast_voice() { return m_btn_adrtypes_cell_broadcast_voice; }
	JToggleButton m_btn_finalize;
	SendingColorPicker m_colorpicker;
	ColorButton m_btn_color;
	public JButton m_btn_send;
	public JButton m_btn_close;
	public JButton m_btn_open;
	private JButton btnSaveArea;
	private int m_n_addresstypes = 0;
	ActionListener m_callback;
	public JButton getBtnSaveArea() {
		return btnSaveArea;
	}
	protected ActionListener get_callback() { return m_callback; }
	public void setCallback(ActionListener c) { m_callback = c; }
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
	
	private JCheckBox chkAddressBased = null;
	private JCheckBox chkLocationBased = null;
	private JTabbedPane recipientTab;
	private JPanel privateReceipientPanel;
	private JPanel companyReceipientPanel;
	private StdTextLabel lblSelectPrivateRecipients = new StdTextLabel("", 9, false);
	private StdTextLabel lblSelectCompanyRecipients = new StdTextLabel("", 9, false);
	private JCheckBox chkResident = null;
	private JCheckBox chkPropertyOwnerPrivate = null;
	private JCheckBox chkPropertyOwnerVacation = null;
	private JComboBox comboPrivateRecipientChannel = null;
	private JComboBox comboCompanyRecipientChannel = null;
	private JLabel lblSelectArea = null;
	private JComboBox comboAreaList = null;
	public JCheckBox getChkLocationBased() { return chkLocationBased; }
	
	private PreviewFrame m_gis_preview = null;
	public void setPreviewFrame(PreviewFrame f) { m_gis_preview = f; }
	
	public enum ADRGROUPS
	{
		PRIVATE,
		COMPANY,
		LBA,
		NOFAX,
		VULNERABLE,
		HEAD_OF_HOUSEHOLD,
		ABAS
	}
	
	public int get_addresstypes() { return m_n_addresstypes; }
	private void click_addresstypes(boolean pf, boolean cf, boolean pm, boolean cm, boolean np, int nofax, int vulnerable) {
		m_btn_adrtypes_private_fixed.setSelected(pf);
		m_btn_adrtypes_company_fixed.setSelected(cf);
		m_btn_adrtypes_private_mobile.setSelected(pm);
		m_btn_adrtypes_company_mobile.setSelected(cm);
		m_btn_adrtypes_nophone_private.setSelected(np);
		m_btn_adrtypes_nofax.setSelected((nofax==1 ? true : false));
		m_btn_adrtypes_vulnerable.setSelected(vulnerable == 1);
	}
	public String gen_adrtypes_text(int n_adrtypes, ADRGROUPS group)
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
				if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_fixed") + "<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_fixed_alt_sms") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_fixed_and_mobile") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_mobile") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_mobile_and_fixed") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_SMS_PRIVATE) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_sms") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_sms_alt_fixed") +"<br>";
                }
				if(temp.length()>0) //add heading
				{
					if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0) {
                        temp += sz_font + "- " + Localization.l("main_sending_adr_option_using_blocklist")+"<br>";
                    }
					if((m_n_addresstypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS)> 0) {
						temp += sz_font + "- " + Localization.l("main_sending_adr_btn_vulnerable_citizens")+"<br>";
					}
					if((m_n_addresstypes & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) > 0) {
						temp += sz_font + "- " + Localization.l("main_sending_adr_btn_head_of_household")+"<br>";
					}
						 
				}
				if(temp.length()==0)
				{
					//temp += sz_font + "- " + Localization.l("common_none");
				}
                ret += "<font size='3'><b>" + Localization.l("common_adr_private") + "</b></font><br>";
				ret += temp; //add text
			}

			if(group==ADRGROUPS.COMPANY)
			{
				if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_fixed") + "<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_fixed_alt_sms") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_fixed_and_mobile") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_mobile") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_mobile_and_fixed") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_SMS_COMPANY) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_sms") +"<br>";
                }
				if((m_n_addresstypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0) {
                    temp += sz_font + "- "+ Localization.l("main_sending_adr_option_sms_alt_fixed") +"<br>";
                }
				if(temp.length()>0) //add heading
				{
					if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0) {
                        temp += sz_font + "- " + Localization.l("main_sending_adr_option_using_blocklist")+"<br>";
                    }
					if((m_n_addresstypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS)> 0) {
						temp += sz_font + "- " + Localization.l("main_sending_adr_btn_vulnerable_citizens") +"<br>";
					}
				}
                ret += "<font size='3'><b>" + Localization.l("common_adr_company") + "</b></font><br>";
				ret += temp;
			}

			if(group==ADRGROUPS.ABAS)
			{
				ret += "<font size='3'><b>" + Localization.l("main_sending_adr_option_address_based") + "</b></font><br>";
				
				if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_option_using_blocklist");
                }
				ret += temp;
			}
			if(group==ADRGROUPS.LBA)
			{
				if((m_n_addresstypes & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_option_location_based_sms");
                }
//				if(temp.length()>0) {
                    ret += "<font size='3'><b>" + Localization.l("main_sending_adr_option_location_based") + "</b></font><br>";
//                }
				ret += temp;
			}
			if(group==ADRGROUPS.NOFAX)
			{
				if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_option_using_blocklist");
                }
				ret += temp;
			}
			if(group==ADRGROUPS.VULNERABLE)
			{
				if((m_n_addresstypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_btn_vulnerable_citizens");
                }
				ret += temp;				
			}
			if(group==ADRGROUPS.HEAD_OF_HOUSEHOLD)
			{
				if((m_n_addresstypes & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) > 0) {
					temp += sz_font + "- " + Localization.l("main_sending_adr_btn_head_of_household");
				}
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
	
	public void remove_addresstypes(int n_adrtypes) {
		set_addresstypes(get_addresstypes() & ~n_adrtypes);
	}
	
	public void add_addresstypes(int n_adrtypes) {
		set_addresstypes(get_addresstypes() | n_adrtypes); 
	}
	public void set_addresstypes(int n_adrtypes) {
		boolean b = false;
		m_n_addresstypes = n_adrtypes;
		if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE ||
			(m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) == SendController.SENDTO_FIXED_PRIVATE_ALT_SMS ||
			(m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) == SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE)
		{
			m_btn_adrtypes_private_fixed.setSelected(true);
		}
		else
		{
			m_btn_adrtypes_private_fixed.setSelected(false);
		}
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
			chkLocationBased.setSelected(true);
		else
		{
			chkLocationBased.setSelected(false);
			m_n_addresstypes &= ~SendController.SENDTO_CELL_BROADCAST_TEXT;
			
		}
		if((m_n_addresstypes & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE)
		{
			m_btn_adrtypes_cell_broadcast_voice.setSelected(true);
		}
		else
			m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
		
		if((m_n_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) == SendController.SENDTO_USE_NOFAX_COMPANY)
		{
			m_btn_adrtypes_nofax.setSelected(true);
		}
		else
			m_btn_adrtypes_nofax.setSelected(false);
		
		/*if((m_n_addresstypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) == SendController.SENDTO_ONLY_VULNERABLE_CITIZENS)
		{
			m_btn_vulnerable.setSelected(true);
		}*/
		m_btn_adrtypes_vulnerable.setSelected((m_n_addresstypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) == SendController.SENDTO_ONLY_VULNERABLE_CITIZENS);
		m_btn_adrtypes_headofhousehold.setSelected((m_n_addresstypes & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) == SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD);
		
		m_btn_adrtypes_nophone_private.setSelected(false);
		m_btn_adrtypes_nophone_company.setSelected(false);
		
		boolean bEnableAbas = IsAbasChannelSelected();
		m_btn_adrtypes_nofax.setEnabled(bEnableAbas);
		if(!bEnableAbas && m_btn_adrtypes_nofax.isSelected())
			m_btn_adrtypes_nofax.setSelected(false);
		m_btn_adrtypes_vulnerable.setEnabled(bEnableAbas);
		m_btn_adrtypes_headofhousehold.setEnabled(bEnableAbas);
				
		if(report_addresschanges()!=null)
			report_addresschanges().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
		m_lbl_addresstypes_private.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.PRIVATE));
		m_lbl_addresstypes_company.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.COMPANY));
		m_lbl_addresstypes_lba.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.LBA));	
		
		changeVisibilityOfABASPanel(chkAddressBased.isSelected());

	}
	public void populateABASPanelData(int types)
	{
//		System.out.println("SendoptionToolbar populateABASPanelData called types="+types);
		if (types==0)
		{
			changeVisibilityOfABASPanel(chkAddressBased.isSelected());
			return;
		}
		
		if((SendController.SENDTO_CELL_BROADCAST_TEXT & types)==SendController.SENDTO_CELL_BROADCAST_TEXT)
		{
			chkLocationBased.setText(gen_adrtypes_text(types, ADRGROUPS.LBA));
			chkLocationBased.setSelected(true);
		}
		if((SendController.SENDTO_USE_ABAS_RECIPIENTS & types)==SendController.SENDTO_USE_ABAS_RECIPIENTS)
		{
			chkAddressBased.setText(gen_adrtypes_text(types, ADRGROUPS.ABAS));
			chkAddressBased.setSelected(true);
		}
		if((SendController.SENDTO_USE_NOFAX_COMPANY & types)==SendController.SENDTO_USE_NOFAX_COMPANY)
			m_btn_adrtypes_nofax.setSelected(true);
		if((SendController.SENDTO_ONLY_VULNERABLE_CITIZENS & types)== SendController.SENDTO_ONLY_VULNERABLE_CITIZENS)
			m_btn_adrtypes_vulnerable.setSelected(true);
		if((SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD & types) == SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD)
			m_btn_adrtypes_headofhousehold.setSelected(true);
		
		changeVisibilityOfABASPanel(chkAddressBased.isSelected());
		
		for(int i=1;i<comboPrivateRecipientChannel.getItemCount();i++)
		{
			int value = ((RecipientChannel)comboPrivateRecipientChannel.getItemAt(i)).getValue();
			if((value & types)==value)
				comboPrivateRecipientChannel.setSelectedIndex(i);
		}
		for(int i=1;i<comboCompanyRecipientChannel.getItemCount();i++)
		{
			int value = ((RecipientChannel)comboCompanyRecipientChannel.getItemAt(i)).getValue();
			if((value & types)==value)
				comboCompanyRecipientChannel.setSelectedIndex(i);
		}
		if((SendController.RECIPTYPE_PRIVATE_RESIDENT & types)==SendController.RECIPTYPE_PRIVATE_RESIDENT)
			chkResident.setSelected(true);
		if((SendController.RECIPTYPE_PRIVATE_OWNER_HOME & types)==SendController.RECIPTYPE_PRIVATE_OWNER_HOME)
			chkPropertyOwnerPrivate.setSelected(true);
		if((SendController.RECIPTYPE_PRIVATE_OWNER_VACATION & types)==SendController.RECIPTYPE_PRIVATE_OWNER_VACATION)
			chkPropertyOwnerVacation.setSelected(true);
	}
	
	public boolean IsAbasChannelSelected()
	{
		/*return m_btn_adrtypes_private_fixed.isSelected() | 
				m_btn_adrtypes_private_mobile.isSelected() |
				m_btn_adrtypes_company_fixed.isSelected() | 
				m_btn_adrtypes_company_mobile.isSelected();	*/
		boolean flag= (((RecipientChannel)comboPrivateRecipientChannel.getSelectedItem()).getValue() > 0 | 
				((RecipientChannel)comboCompanyRecipientChannel.getSelectedItem()).getValue() > 0 |
				chkResident.isSelected() |
				chkPropertyOwnerPrivate.isSelected() |
				chkPropertyOwnerVacation.isSelected() )& chkAddressBased.isSelected();	
		return flag;
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
	public static final int BTN_ADRTYPES_VULNERABLE_	= 1 << 19;
	public static final int BTN_ADRTYPES_HEADOFHOUSEHOLD_ = 1 << 20;
    public static final int BTN_SENDINGTYPE_HOUSE_SELECT_ALERT = 1 << 21;
	
	public static final int COMPONENTS_ALL_ = BTN_SENDINGTYPE_POLYGON_ | BTN_SENDINGTYPE_ELLIPSE_ | BTN_ADRTYPES_PRIVATE_ |
												BTN_ADRTYPES_COMPANY_ | BTN_ADRTYPES_NOPHONE_ | BTN_COLORPICKER_ | BTN_FINALIZE_ | 
												BTN_SEND_ | BTN_CLOSE_ | BTN_OPEN_ | BTN_ACTIVATE_ | BTN_CENTER_ON_MAP_ | TXT_SENDINGNAME_ | 
												BTN_CELL_BROADCAST_ | BTN_CELL_BROADCAST_VOICE_ | BTN_ADRTYPES_NOFAX_ | 
												TXT_RECIPIENTTYPES_ | BTN_SENDINGTYPE_MUNICIPAL_ | BTN_ADRTYPES_VULNERABLE_ | BTN_ADRTYPES_HEADOFHOUSEHOLD_ |
                                                BTN_SENDINGTYPE_HOUSE_SELECT_ALERT;
	
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
				chkLocationBased.setEnabled(false);
				chkLocationBased.setSelected(false);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
				break;
			case SendProperties.SENDING_TYPE_MUNICIPAL_:
				m_btn_adrtypes_cell_broadcast_text.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_text.setSelected(false);
				chkLocationBased.setEnabled(false);
				chkLocationBased.setSelected(false);
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
		log.debug("in show_buttonsbyadrtype ADR="+ADR);
		show_buttonsbyadrtype(ADR, m_btn_adrtypes_private_fixed, m_btn_adrtypes_private_mobile, 
				m_btn_adrtypes_company_fixed, m_btn_adrtypes_company_mobile, chkLocationBased,chkPropertyOwnerPrivate,chkPropertyOwnerVacation);
		if(m_btn_adrtypes_private_fixed.isVisible() || m_btn_adrtypes_private_mobile.isVisible())
			m_lbl_addresstypes_private.setVisible(true);
		else
			m_lbl_addresstypes_private.setVisible(false);
		
		if(m_btn_adrtypes_company_fixed.isVisible() || m_btn_adrtypes_company_mobile.isVisible())
			m_lbl_addresstypes_company.setVisible(true);
		else
			m_lbl_addresstypes_company.setVisible(false);
		
		//boolean b = (ADR & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) == SendController.SENDTO_ONLY_VULNERABLE_CITIZENS;
		m_btn_adrtypes_cell_broadcast_voice.setVisible((ADR & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE);
		
		m_btn_adrtypes_vulnerable.setVisible(PAS.get_pas().get_rightsmanagement().only_vulnerable_subscribers() &&
				(ADR & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) == SendController.SENDTO_ONLY_VULNERABLE_CITIZENS);
		m_btn_adrtypes_headofhousehold.setVisible(PAS.get_pas().get_rightsmanagement().only_head_of_household() &&
				(ADR & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) == SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD);
	}
	
	public void show_buttonsbyadrtype(long ADR, AbstractButton btn_private_fixed, AbstractButton btn_private_mobile,
								AbstractButton btn_company_fixed, AbstractButton btn_company_mobile,
								JCheckBox chk_lba_text,
								JCheckBox chk_property_owner_private, JCheckBox chk_property_owner_vacation)
	{
		btn_private_fixed.setVisible((ADR & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE);
		btn_private_mobile.setVisible((ADR & SendController.SENDTO_MOBILE_PRIVATE) == SendController.SENDTO_MOBILE_PRIVATE);
		btn_company_fixed.setVisible((ADR & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY);
		btn_company_mobile.setVisible((ADR & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY);		
//		btn_lba_text.setVisible((ADR & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT);
		chk_lba_text.setVisible((ADR & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT);
		chk_property_owner_private.setVisible((ADR & SendController.RECIPTYPE_PRIVATE_OWNER_HOME) == SendController.RECIPTYPE_PRIVATE_OWNER_HOME);
		chk_property_owner_vacation.setVisible((ADR & SendController.RECIPTYPE_PRIVATE_OWNER_VACATION) == SendController.RECIPTYPE_PRIVATE_OWNER_VACATION);
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
			this.chkLocationBased.setVisible(b_show);
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
		//only make access to vulnerable citizens adrtype if LBA-text is not enabled.
		if((FLAGS & BTN_ADRTYPES_VULNERABLE_) == BTN_ADRTYPES_VULNERABLE_) {
			this.m_btn_adrtypes_vulnerable.setVisible(b_show);
		}
		
		if((FLAGS & BTN_ADRTYPES_HEADOFHOUSEHOLD_) == BTN_ADRTYPES_HEADOFHOUSEHOLD_) {
			this.m_btn_adrtypes_headofhousehold.setVisible(b_show);
		}

        if((FLAGS & BTN_SENDINGTYPE_HOUSE_SELECT_ALERT) == BTN_SENDINGTYPE_HOUSE_SELECT_ALERT) {
            this.m_radio_sendingtype_house_select .setVisible(b_show);
        }
	}

	public boolean adrGroupRepresented(ButtonGroup bg)
	{
		Enumeration<AbstractButton> en = bg.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if(i>0 && it.isSelected())
				return true;
		}
		
		return false;
	}
	
	public void initSelections()
	{
		int TYPES = get_addresstypes();
		Enumeration<AbstractButton> en = group_smsprivbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if((TYPES & i) == i)
				it.setSelected(true);
		}
		en = group_fixedprivbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if((TYPES & i) == i)
				it.setSelected(true);
		}
		en = group_smscompbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if((TYPES & i) == i)
				it.setSelected(true);
		}
		en = group_fixedcompbtn.getElements();
		while(en.hasMoreElements())
		{
			CheckItem it = (CheckItem)en.nextElement();
			int i = ((Integer)it.get_value()).intValue();
			if((TYPES & i) == i)
				it.setSelected(true);
		}
		disableAddressMenus();
		actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}
	
	public int customizeSelections(int TYPES)
	{
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
		disableAddressMenus();
		
		
		return TYPES;
	}
	
	protected void disableAddressMenus()
	{
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
			log.warn(e.getMessage(), e);
		}		
	}
	
	public void gen_addresstypes() {

		int TYPES = 0;
		if(m_btn_adrtypes_nophone_private.isSelected() && m_btn_adrtypes_nophone_private.isVisible()) 
			TYPES |= m_btn_adrtypes_nophone_private.get_adrtype();
		if(m_btn_adrtypes_nophone_company.isSelected() && m_btn_adrtypes_nophone_company.isVisible()) 
			TYPES |= m_btn_adrtypes_nophone_company.get_adrtype();
//		if(m_btn_adrtypes_cell_broadcast_text.isSelected() && m_btn_adrtypes_cell_broadcast_text.isVisible()) 
//			TYPES |= m_btn_adrtypes_cell_broadcast_text.get_adrtype();
//		if(m_btn_adrtypes_cell_broadcast_voice.isSelected() && m_btn_adrtypes_cell_broadcast_voice.isVisible() && !m_btn_adrtypes_cell_broadcast_text.isSelected()) 
		if(m_btn_adrtypes_cell_broadcast_voice.isSelected() && m_btn_adrtypes_cell_broadcast_voice.isVisible() && !chkLocationBased.isSelected()) 
			TYPES |= m_btn_adrtypes_cell_broadcast_voice.get_adrtype();
		
		if(chkLocationBased.isSelected()) 
			TYPES |= m_btn_adrtypes_cell_broadcast_text.get_adrtype();
		
		if(chkAddressBased.isSelected())
		{
			TYPES |= SendController.SENDTO_USE_ABAS_RECIPIENTS;
			
			if(IsAbasChannelSelected())
			{
				TYPES |= ((RecipientChannel)comboPrivateRecipientChannel.getSelectedItem()).getValue();
				TYPES |= ((RecipientChannel)comboCompanyRecipientChannel.getSelectedItem()).getValue();
				
				if(chkResident.isSelected())
					TYPES |= SendController.RECIPTYPE_PRIVATE_RESIDENT;
				if(chkPropertyOwnerPrivate.isSelected())
					TYPES |= SendController.RECIPTYPE_PRIVATE_OWNER_HOME;
				if(chkPropertyOwnerVacation.isSelected())
					TYPES |= SendController.RECIPTYPE_PRIVATE_OWNER_VACATION;
			}
		}
		
		if(m_btn_adrtypes_nofax.isSelected() && IsAbasChannelSelected()) {
			TYPES |= m_btn_adrtypes_nofax.get_adrtype();
		}
		//only make this option available if the button is visible
		if(m_btn_adrtypes_vulnerable.isSelected() && m_btn_adrtypes_vulnerable.isVisible()) 
			TYPES |= m_btn_adrtypes_vulnerable.get_adrtype();
		if(m_btn_adrtypes_headofhousehold.isSelected() && m_btn_adrtypes_headofhousehold.isVisible())
			TYPES |= m_btn_adrtypes_headofhousehold.get_adrtype();
		//commented as per new combo box for recipient channels
//		TYPES = customizeSelections(TYPES);
		set_addresstypes(TYPES);
		log.debug("Addresstypes = " + TYPES);
		
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
				for(int x = 0; x <= 32; x++)
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
					if(!b_found_remainder && !b && itm.isSelected())
						btngroup_to_disable.getElements().nextElement().setSelected(true);
					
				}
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
		set_import_callback(this);
		m_parent = sendobject;
		m_n_initsendnumber = n_sendnumber;
		m_callback = callback;
		m_btngroup_lba = new ButtonGroup();
		init();
		this.setSize(550, 200);
		try {
			this.setVisible(true);
			this.setFocusable(true);
		} catch(Exception e) {
			Error.getError().addError("SendOptionToolbar","Exception in SendOptionToolbar",e,1);
		}
		setLayout(this.getLayout());
		if(PAS.get_pas()!=null)
		{
			PAS.get_pas().get_mappane().addActionListener(this);
		}
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
		
		Component [] municipalMenus = menu_municipals.getComponents();
		for(int i=0; i < municipalMenus.length; i++)
		{
			Component c = municipalMenus[i];
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
            m_txt_sendname = new StdTextArea(String.format(Localization.l("main_sending_init_sendingname"), m_n_initsendnumber), false);
		this.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(m_txt_sendname.getText(), TitledBorder.TOP));
		m_txt_sendname.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e){				
			}
			public void keyReleased(KeyEvent e){
				try
				{
					setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(m_txt_sendname.getText()));
					get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "");
					PAS.get_pas().kickRepaint();
				}
				catch(Exception err)
				{
					log.warn(err.getMessage(), err);
				}
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
            m_radio_sendingtype_house_select = new JToggleButton(ImageLoader.load_icon("send_house_select2.png"));
        else
            m_radio_sendingtype_house_select = new JToggleButton(ImageLoader.load_icon("send_house_select2.png"));
			
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
		
		m_btn_adrtypes_vulnerable = new ToggleAddresstype(ImageLoader.load_icon("bandaid_24.png"), false, SendController.SENDTO_ONLY_VULNERABLE_CITIZENS);
		
		m_btn_adrtypes_headofhousehold = new ToggleAddresstype(ImageLoader.load_icon("HeadOfHousehold_24.png"), false, SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD);
		
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
		
		btnSaveArea = new JButton(ImageLoader.load_icon("save_area.png"));

		if(PAS.icon_version==2)
			m_icon_polygon_edit = ImageLoader.load_icon("brush_16.png");
		else
			m_icon_polygon_edit = ImageLoader.load_icon("edit.gif");

		if(PAS.icon_version==2)
			m_icon_polygon_no_edit = ImageFetcher.makeGrayscale("brush_16.png");//ImageLoader.load_icon("brush_disabled_16.png");
		else
			m_icon_polygon_no_edit = ImageLoader.load_icon("no_edit.gif");

		m_icon_gis_no_edit = ImageLoader.load_icon("no_edit_gis_import.png");
		m_icon_gis_edit = ImageLoader.load_icon("edit_gis_import.png");		
		
		m_radio_activate = new JRadioButton(m_icon_polygon_no_edit, true);
		//set_sendingicons();
		//m_radio_activate.setSelectedIcon(m_icon_polygon_edit);
		
		if(PAS.icon_version==2)
			m_btn_open = new JButton(ImageLoader.load_icon("folder_open_24.png"));		
		else
			m_btn_open = new JButton(ImageLoader.load_icon("open.gif"));

            m_radio_sendingtype_polygon.setToolTipText(Localization.l("main_sending_type_polygon"));
            m_radio_sendingtype_ellipse.setToolTipText(Localization.l("main_sending_type_ellipse"));
            m_radio_sendingtype_polygonal_ellipse.setToolTipText(Localization.l("main_sending_type_ellipse") + " (polygonal)");
            m_radio_sendingtype_municipal.setToolTipText(Localization.l("main_sending_type_municipal"));
            m_radio_sendingtype_house_select.setToolTipText(Localization.l("main_sending_type_house_select"));
            m_btn_goto.setToolTipText(Localization.l("main_status_show_map_of_sending"));
            m_btn_adrtypes_private_fixed.setToolTipText(Localization.l("main_sending_adr_btn_fixed_private_tooltip"));
            m_btn_adrtypes_private_mobile.setToolTipText(Localization.l("main_sending_adr_btn_mobile_private_tooltip"));
            m_btn_adrtypes_company_fixed.setToolTipText(Localization.l("main_sending_adr_btn_fixed_company_tooltip"));
            m_btn_adrtypes_company_mobile.setToolTipText(Localization.l("main_sending_adr_btn_mobile_company_tooltip"));
            m_btn_adrtypes_nophone_private.setToolTipText(Localization.l("main_sending_adr_btn_nophone_private_tooltip"));
            m_btn_adrtypes_nophone_company.setToolTipText(Localization.l("main_sending_adr_btn_nophone_company_tooltip"));
            m_btn_adrtypes_cell_broadcast_text.setToolTipText(Localization.l("main_sending_adr_btn_lba_text_tooltip"));
            m_btn_adrtypes_cell_broadcast_voice.setToolTipText(Localization.l("main_sending_adr_btn_lba_voice_tooltip"));
            m_btn_adrtypes_nofax.setToolTipText(Localization.l("main_sending_adr_btn_company_blocklist_tooltip"));
            m_btn_adrtypes_vulnerable.setToolTipText(Localization.l("main_sending_adr_btn_vulnerable_citizens_tooltip"));
            m_btn_adrtypes_headofhousehold.setToolTipText(Localization.l("main_sending_adr_btn_head_of_household_tooltip"));
            m_btn_finalize.setToolTipText(Localization.l("main_sending_adr_btn_lock_tooltip"));
            m_btn_color.setToolTipText(Localization.l("common_color"));
            m_btn_send.setToolTipText(Localization.l("main_sending_prepare"));
            m_btn_close.setToolTipText(Localization.l("main_sending_adr_btn_close_sending"));
            m_btn_open.setToolTipText(Localization.l("mainmenu_file_import"));
            btnSaveArea.setToolTipText(Localization.l("main_sending_adr_btn_save_predefined_area"));

		m_group_sendingtype.add(m_radio_sendingtype_polygon);
		m_group_sendingtype.add(m_radio_sendingtype_ellipse);
		m_group_sendingtype.add(m_radio_sendingtype_polygonal_ellipse);
		m_group_sendingtype.add(m_radio_sendingtype_municipal);
        m_group_sendingtype.add(m_radio_sendingtype_house_select);
		set_size(m_radio_sendingtype_polygon, SIZE_BUTTON_LARGE);
		set_size(m_radio_sendingtype_ellipse, SIZE_BUTTON_LARGE);
		set_size(m_radio_sendingtype_polygonal_ellipse, SIZE_BUTTON_LARGE);
		set_size(m_radio_sendingtype_municipal, SIZE_BUTTON_LARGE);
        set_size(m_radio_sendingtype_house_select, SIZE_BUTTON_LARGE);
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
		set_size(m_btn_adrtypes_vulnerable, SIZE_BUTTON_LARGE);
		set_size(m_btn_adrtypes_headofhousehold, SIZE_BUTTON_LARGE);
		
		set_size(m_btn_color, SIZE_BUTTON_ICON, SIZE_BUTTON_ICON);
		set_size(m_btn_send, SIZE_BUTTON_LARGE);
		set_size(m_btn_open, SIZE_BUTTON_LARGE);
		set_size(m_radio_activate, SIZE_BUTTON_SMALL);
		set_size(m_btn_finalize, SIZE_BUTTON_LARGE);
		set_size(m_btn_close, SIZE_BUTTON_LARGE);
		set_size(btnSaveArea, SIZE_BUTTON_LARGE);
		
		m_radio_sendingtype_polygon.addActionListener(this);
		m_radio_sendingtype_ellipse.addActionListener(this);
		m_radio_sendingtype_polygonal_ellipse.addActionListener(this);
		m_radio_sendingtype_municipal.addActionListener(this);
        m_radio_sendingtype_house_select.addActionListener(this);
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
		m_btn_adrtypes_vulnerable.addActionListener(this);
		m_btn_adrtypes_headofhousehold.addActionListener(this);
		
				
		m_btn_color.addActionListener(this);
		m_radio_activate.addActionListener(this);
		m_btn_send.addActionListener(this);
		m_btn_finalize.addActionListener(this);
		m_btn_close.addActionListener(this);
		m_btn_open.addActionListener(this);
		btnSaveArea.addActionListener(this);
		ActionEvent e = new ActionEvent(m_radio_activate, ActionEvent.ACTION_PERFORMED, "act_register_activation_btn");
		//if(get_parent().get_sendcontroller() != null)
		if(get_callback()!=null)
			get_callback().actionPerformed(e);

		m_btn_color.setActionCommand("act_open_colorpicker");
		m_radio_activate.setActionCommand("act_activate_sending");
		m_btn_goto.setActionCommand("act_find_sending");
		m_btn_send.setActionCommand("act_send_one");
		m_radio_sendingtype_polygon.setActionCommand("act_sendingtype_polygon");
		m_radio_sendingtype_ellipse.setActionCommand("act_sendingtype_ellipse");
		m_radio_sendingtype_polygonal_ellipse.setActionCommand("act_sendingtype_polygonal_ellipse");
		m_radio_sendingtype_municipal.setActionCommand("act_sendingtype_municipal");
        m_radio_sendingtype_house_select.setActionCommand("act_sendingtype_house_select");
		m_btn_adrtypes_private_fixed.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_company_fixed.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_private_mobile.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_company_mobile.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nophone_private.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nophone_company.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_cell_broadcast_text.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_cell_broadcast_voice.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nofax.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_vulnerable.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_headofhousehold.setActionCommand("act_set_addresstypes");
		m_btn_close.setActionCommand("act_sending_close");
		m_btn_open.setActionCommand("act_open_polygon");
		btnSaveArea.setActionCommand("act_save_predefined_area");
		lblSelectArea = new JLabel(Localization.l("main_sending_adr_option_select_area"));

		comboAreaList = new JComboBox();
		log.debug("after creating areaListCombo 1");
		ArrayList<AreaVO> areaList = Variables.getAreaList();
		if(areaList == null || areaList.size()==0)
		{
			AreaServerCon areaSyncUtility = new AreaServerCon();
			areaSyncUtility.execute(null, "fetch");
			try
			{
				areaSyncUtility.join();
				areaList = areaSyncUtility.getAreaList();
				log.debug("in sendoptiontollbar areaList size="+areaList.size());
			}
			catch(Exception ex)
			{}
		}
		resetAreaList();
		comboAreaList.setToolTipText(Localization.l("main_sending_adr_btn_select_area_tooltip"));
		comboAreaList.setEditable(true);
		comboAreaList.setSelectedItem("");
		comboAreaList.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() { 
			public void keyReleased(KeyEvent e) {
				log.debug("1key entered=" + e.getKeyCode());
				if(e.getKeyChar()==KeyEvent.VK_ENTER)
				{
					//areaListCombo.setFocusable(false);
					comboAreaList.hidePopup();
					if(comboAreaList.getSelectedItem() instanceof AreaVO)
					{
						AreaVO selectedArea = (AreaVO) comboAreaList.getSelectedItem();
						log.debug("selected object is321 = "+selectedArea);
						autoSelectShapeFromPredefinedArea(selectedArea.getM_shape());
					}
				}
				else if(e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT)
				{
					//do nothing when up or down keys are pressed
				}
				//add validation logic here, to decide which keys are allowed
				else if (e.getKeyCode() != 38 && e.getKeyCode() != 40 && e.getKeyCode() != 10) 
				{
	                String a = comboAreaList.getEditor().getItem().toString();
	                comboAreaList.removeAllItems();
	                int st = 0;
//	                comboAreaList.addItem(new String(a));

	                ArrayList<AreaVO> areaList = Variables.getAreaList();
	                for (int i = 0; i < areaList.size(); i++) {
	                    if (areaList.get(i).getName().toUpperCase().startsWith(a.toUpperCase()))
	                    {
	                    	comboAreaList.addItem(areaList.get(i));
	                    	st++;
	                    }
	                }
	                comboAreaList.getEditor().setItem(new String(a));
//	                comboAreaList.setEditable(true);
//	                comboAreaList.setFocusable(true);
	                comboAreaList.hidePopup();
	                if (st != 0) { comboAreaList.showPopup(); }
	                PAS.get_pas().kickRepaint();
	            }
        }});

		comboAreaList.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED)
				{
					if(comboAreaList.getSelectedItem() instanceof AreaVO)
					{
						AreaVO selectedArea = (AreaVO) comboAreaList.getSelectedItem();
						//log.debug("comboAreaList item changed="+selectedArea + ";e.getStateChange()=" + e.getStateChange());
						autoSelectShapeFromPredefinedArea(selectedArea.getM_shape());
					}
				}
			}
		});

		recipientTab = new JTabbedPane();
		
		chkLocationBased = new JCheckBox(gen_adrtypes_text(0, ADRGROUPS.LBA));
		chkAddressBased = new JCheckBox(gen_adrtypes_text(0, ADRGROUPS.ABAS));
		
		privateReceipientPanel = new JPanel();
		lblSelectPrivateRecipients.setText(Localization.l("main_sending_adr_sel_recipients"));
		//lblSelectCompanyRecipients.setText(Localization.l("main_sending_adr_sel_recipients"));
        //lblSelectCompanyRecipients.setText(Localization.l("main_sending_adr_sel_channels"));
		lblSelectCompanyRecipients.setText(Localization.l("main_sending_adr_sel_recipients_company"));
   		chkResident = new JCheckBox(Localization.l("main_sending_adr_sel_residents"));
		chkPropertyOwnerPrivate = new JCheckBox(Localization.l("main_sending_adr_sel_property_owner_private"));
		chkPropertyOwnerVacation = new JCheckBox(Localization.l("main_sending_adr_sel_property_owner_vacation"));
		
		Font font = new Font(null,Font.BOLD,12);
		chkResident.setFont(font);
		chkPropertyOwnerPrivate.setFont(font);
		chkPropertyOwnerVacation.setFont(font);
		
		comboPrivateRecipientChannel = new JComboBox();
		initRecipientChannel(ChannelType.PRIVATE,comboPrivateRecipientChannel);
		
		comboCompanyRecipientChannel = new JComboBox();
		initRecipientChannel(ChannelType.COMPANY,comboCompanyRecipientChannel);
		
//		privateReceipientPanel.setLayout(new GridLayout(4, 1));
		privateReceipientPanel.setLayout(new GridBagLayout());
		GridBagConstraints privateRecipientConstraint = new GridBagConstraints();
		
		privateRecipientConstraint.fill = GridBagConstraints.HORIZONTAL;
		privateRecipientConstraint.gridx=0;
		privateRecipientConstraint.gridy=0;
		privateRecipientConstraint.ipadx=60;
		privateRecipientConstraint.ipady=40;
		privateReceipientPanel.add(lblSelectPrivateRecipients,privateRecipientConstraint);
		privateRecipientConstraint.fill = GridBagConstraints.HORIZONTAL;
		privateRecipientConstraint.gridx=1;
		privateRecipientConstraint.gridy=0;
		privateRecipientConstraint.ipadx=0;
		privateRecipientConstraint.ipady=0;
		privateReceipientPanel.add(comboPrivateRecipientChannel,privateRecipientConstraint);
		
		privateRecipientConstraint.fill = GridBagConstraints.HORIZONTAL;
		privateRecipientConstraint.gridx=0;
		privateRecipientConstraint.gridy=1;
		privateRecipientConstraint.gridwidth=2;
		privateReceipientPanel.add(chkResident,privateRecipientConstraint);
		
		privateRecipientConstraint.fill = GridBagConstraints.HORIZONTAL;
		privateRecipientConstraint.gridx=0;
		privateRecipientConstraint.gridy=2;
		privateRecipientConstraint.gridwidth=2;
		privateReceipientPanel.add(chkPropertyOwnerPrivate,privateRecipientConstraint);
		
		privateRecipientConstraint.fill = GridBagConstraints.HORIZONTAL;
		privateRecipientConstraint.gridx=0;
		privateRecipientConstraint.gridy=3;
		privateRecipientConstraint.gridwidth=2;
		privateReceipientPanel.add(chkPropertyOwnerVacation,privateRecipientConstraint);
		
		recipientTab.addTab(Localization.l("common_adr_private"), null, privateReceipientPanel, Localization.l("common_adr_private"));
		
		companyReceipientPanel = new JPanel();
		
		companyReceipientPanel.setLayout(new GridBagLayout());
		GridBagConstraints companyRecipientConstraint = new GridBagConstraints();
		
		companyRecipientConstraint.fill = GridBagConstraints.NORTHWEST;
		companyRecipientConstraint.gridx=0;
		companyRecipientConstraint.gridy=0;
		companyRecipientConstraint.ipadx=40;
		companyRecipientConstraint.ipady=30;
		companyRecipientConstraint.anchor=GridBagConstraints.FIRST_LINE_START;
		companyReceipientPanel.add(lblSelectCompanyRecipients,companyRecipientConstraint);
		companyRecipientConstraint.fill = GridBagConstraints.NORTH;
		companyRecipientConstraint.gridx=1;
		companyRecipientConstraint.gridy=0;
		companyRecipientConstraint.ipadx=0;
		companyRecipientConstraint.ipady=0;
		companyRecipientConstraint.insets=new Insets(10, 0, 90, 0);
		companyReceipientPanel.add(comboCompanyRecipientChannel,companyRecipientConstraint);
		
		recipientTab.addTab(Localization.l("common_adr_company"), null, companyReceipientPanel, Localization.l("common_adr_company"));
		
		chkLocationBased.addActionListener(this);
		chkAddressBased.addActionListener(this);
		comboPrivateRecipientChannel.addActionListener(this);
		comboCompanyRecipientChannel.addActionListener(this);
		chkResident.addActionListener(this);
		chkPropertyOwnerPrivate.addActionListener(this);
		chkPropertyOwnerVacation.addActionListener(this);
		
		chkLocationBased.setActionCommand("act_set_addresstypes");
		chkAddressBased.setActionCommand("act_set_addresstypes");
		comboPrivateRecipientChannel.setActionCommand("act_set_addresstypes");
		comboCompanyRecipientChannel.setActionCommand("act_set_addresstypes");
		chkResident.setActionCommand("act_set_addresstypes");
		chkPropertyOwnerPrivate.setActionCommand("act_set_addresstypes");
		chkPropertyOwnerVacation.setActionCommand("act_set_addresstypes");
		
		m_btn_adrtypes_cell_broadcast_text.setSelected(false);
		chkLocationBased.setSelected(false);
		m_btn_adrtypes_cell_broadcast_voice.setSelected(false);
		m_btn_adrtypes_nofax.setSelected(false);
		
		//log.debug(PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_cellbroadcast_rights());
		if(PAS.get_pas() != null && !PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_rights_management().cell_broadcast())
			show_buttons(SendOptionToolbar.BTN_CELL_BROADCAST_,false);
		
		} catch(Exception e) {
			//PAS.get_pas().add_event("Error in SendOptionToolbar.init(): " + e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("SendOptionToolbar","Exception in init",e,1);
		}
		//TODO: check for access to send only to vulnerable citizens
		if(PAS.get_pas() != null && PAS.get_pas().get_rightsmanagement().only_vulnerable_subscribers())
		{
			show_buttons(SendOptionToolbar.BTN_ADRTYPES_VULNERABLE_, true);
		}
		if(PAS.get_pas() != null && PAS.get_pas().get_rightsmanagement().only_head_of_household())
		{
			show_buttons(SendOptionToolbar.BTN_ADRTYPES_HEADOFHOUSEHOLD_, true);
		}
		add_controls();
		
		//Substance 3.3
		//Color c = SubstanceLookAndFeel.getActiveColorScheme().getMidColor();
		Color c = SystemColor.window;
		
		//Substance 5.2
		//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getMidColor();
		
		//PRIVATE MOBILE
        group_smsprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_none"), 0, 0, true, Localization.l("main_sending_adr_sel_private_mobile_none_tooltip"), c));
        group_smsprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_voice"), SendController.SENDTO_MOBILE_PRIVATE, SendController.SENDTO_FIXED_PRIVATE_ALT_SMS | SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE, true, Localization.l("main_sending_adr_sel_private_mobile_voice_tooltip"), c));
		if(Variables.getUserInfo().get_current_department().get_userprofile().get_sms() == 1) {
            group_smsprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_sms"), SendController.SENDTO_SMS_PRIVATE, SendController.SENDTO_FIXED_PRIVATE_ALT_SMS, false, Localization.l("main_sending_adr_sel_private_mobile_sms_tooltip"), c));
            group_smsprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_voice_and_sms"), SendController.SENDTO_MOBILE_PRIVATE | SendController.SENDTO_SMS_PRIVATE, SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE | SendController.SENDTO_FIXED_PRIVATE_ALT_SMS, false, Localization.l("main_sending_adr_sel_private_mobile_voice_and_sms_tooltip"), c));
		}
        group_smsprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED, SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_FIXED_PRIVATE_ALT_SMS | SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE, false, Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed_tooltip"), c));
		if(Variables.getUserInfo().get_current_department().get_userprofile().get_sms() == 1) {
            group_smsprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_sms_or_fixed"), SendController.SENDTO_SMS_PRIVATE_ALT_FIXED, SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_FIXED_PRIVATE_ALT_SMS | SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE, false, Localization.l("main_sending_adr_sel_private_mobile_sms_or_fixed_tooltip"), c));
        }
		
		//PRIVATE FIXED
        group_fixedprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_none"), 0, 0, true, Localization.l("main_sending_adr_sel_private_fixed_none_tooltip"), c));
        group_fixedprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_voice"), SendController.SENDTO_FIXED_PRIVATE, SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED | SendController.SENDTO_SMS_PRIVATE_ALT_FIXED, true, Localization.l("main_sending_adr_sel_private_fixed_voice_tooltip"), c));
        group_fixedprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_and_mobile"), SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE, SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED | SendController.SENDTO_SMS_PRIVATE_ALT_FIXED | SendController.SENDTO_MOBILE_PRIVATE, true, Localization.l("main_sending_adr_sel_private_fixed_and_mobile_tooltip"), c));
		if(Variables.getUserInfo().get_current_department().get_userprofile().get_sms() == 1) {
            group_fixedprivbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_alt_sms"), SendController.SENDTO_FIXED_PRIVATE_ALT_SMS, SendController.SENDTO_SMS_PRIVATE | SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED | SendController.SENDTO_SMS_PRIVATE_ALT_FIXED | SendController.SENDTO_MOBILE_PRIVATE, false, Localization.l("main_sending_adr_sel_private_fixed_alt_sms_tooltip"), c));
        }

		
		//COMPANY MOBILE
        group_smscompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_none"), 0, 0, true, Localization.l("main_sending_adr_sel_company_mobile_none_tooltip"), c));
        group_smscompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_voice"), SendController.SENDTO_MOBILE_COMPANY, SendController.SENDTO_FIXED_COMPANY_ALT_SMS | SendController.SENDTO_FIXED_COMPANY_AND_MOBILE, true, Localization.l("main_sending_adr_sel_private_mobile_voice_tooltip"), c));
		if(Variables.getUserInfo().get_current_department().get_userprofile().get_sms() == 1) {
            group_smscompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_sms"), SendController.SENDTO_SMS_COMPANY, SendController.SENDTO_FIXED_COMPANY_ALT_SMS, false, Localization.l("main_sending_adr_sel_private_mobile_sms_tooltip"), c));
            group_smscompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_voice_and_sms"), SendController.SENDTO_MOBILE_COMPANY | SendController.SENDTO_SMS_COMPANY, SendController.SENDTO_FIXED_COMPANY_AND_MOBILE | SendController.SENDTO_FIXED_COMPANY_ALT_SMS, false, Localization.l("main_sending_adr_sel_private_mobile_voice_and_sms_tooltip"), c));
		}
        group_smscompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), SendController.SENDTO_MOBILE_COMPANY_AND_FIXED, SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_FIXED_COMPANY_ALT_SMS | SendController.SENDTO_FIXED_COMPANY_AND_MOBILE, false, Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed_tooltip"), c));
		if(Variables.getUserInfo().get_current_department().get_userprofile().get_sms() == 1) {
            group_smscompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_mobile_sms_or_fixed"), SendController.SENDTO_SMS_COMPANY_ALT_FIXED, SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_FIXED_COMPANY_ALT_SMS | SendController.SENDTO_FIXED_COMPANY_AND_MOBILE, false, Localization.l("main_sending_adr_sel_private_mobile_sms_or_fixed_tooltip"), c));
        }
		
		//COMPANY FIXED
        group_fixedcompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_none"), 0, 0, true, Localization.l("main_sending_adr_sel_company_fixed_none_tooltip"), c));
        group_fixedcompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_voice"), SendController.SENDTO_FIXED_COMPANY, SendController.SENDTO_MOBILE_COMPANY_AND_FIXED | SendController.SENDTO_SMS_COMPANY_ALT_FIXED, true, Localization.l("main_sending_adr_sel_private_fixed_voice_tooltip"), c));
        group_fixedcompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_and_mobile"), SendController.SENDTO_FIXED_COMPANY_AND_MOBILE, SendController.SENDTO_MOBILE_COMPANY_AND_FIXED | SendController.SENDTO_SMS_COMPANY_ALT_FIXED | SendController.SENDTO_MOBILE_COMPANY, true, Localization.l("main_sending_adr_sel_private_fixed_and_mobile_tooltip"), c));
		if(Variables.getUserInfo().get_current_department().get_userprofile().get_sms() == 1) {
            group_fixedcompbtn.add(new CheckItem(Localization.l("main_sending_adr_sel_private_fixed_alt_sms"), SendController.SENDTO_FIXED_COMPANY_ALT_SMS, SendController.SENDTO_MOBILE_COMPANY | SendController.SENDTO_SMS_COMPANY | SendController.SENDTO_MOBILE_COMPANY_AND_FIXED | SendController.SENDTO_SMS_COMPANY_ALT_FIXED | SendController.SENDTO_MOBILE_COMPANY, false, Localization.l("main_sending_adr_sel_private_mobile_sms_or_fixed_tooltip"), c));

		}
	
		add_adrtypemenus(m_btn_adrtypes_private_mobile, menu_smspriv, group_smsprivbtn, Localization.l("main_sending_adr_sel_heading_private_mobile"));
		add_adrtypemenus(m_btn_adrtypes_private_fixed, menu_fixedpriv, group_fixedprivbtn, Localization.l("main_sending_adr_sel_heading_private_fixed"));
		add_adrtypemenus(m_btn_adrtypes_company_mobile, menu_smscomp, group_smscompbtn, Localization.l("main_sending_adr_sel_heading_company_mobile"));
		add_adrtypemenus(m_btn_adrtypes_company_fixed, menu_fixedcomp, group_fixedcompbtn, Localization.l("main_sending_adr_sel_heading_company_fixed"));
		//_SelectAddressSelection(m_n_addresstypes);
		
		if(PAS.get_pas() != null)
			add_municipals(m_radio_sendingtype_municipal, menu_municipals, Localization.l("main_sending_type_municipal_select"));
		
		lock_sending(false);
	}
	
	private void resetAreaList()
	{
		ArrayList<AreaVO> areaList = Variables.getAreaList();
		comboAreaList.removeAllItems();
		comboAreaList.addItem("");
		for (int i = 0; i < areaList.size(); i++) {
			comboAreaList.addItem(areaList.get(i));
        }
		log.debug("in sendoptiontollbar areaListCombo size="+comboAreaList.getItemCount());
		comboAreaList.setSelectedItem("");
	}

	private boolean isAnyABASChannelSelected()
	{
		return ((((RecipientChannel)comboPrivateRecipientChannel.getSelectedItem()).getValue() > 0) |(((RecipientChannel)comboCompanyRecipientChannel.getSelectedItem()).getValue() > 0));
	}

	private boolean isAnyABASRecipientTypeSelected()
	{
		return ( chkResident.isSelected() | chkPropertyOwnerVacation.isSelected() | chkPropertyOwnerPrivate.isSelected());
	}
	private void changeVisibilityOfABASPanel(boolean b)
	{
		if(b && !chkAddressBased.isSelected())
			return;
		lblSelectPrivateRecipients.setEnabled(b);
		comboPrivateRecipientChannel.setEnabled(b);
		chkResident.setEnabled(b);
		chkPropertyOwnerPrivate.setEnabled(b);
		chkPropertyOwnerVacation.setEnabled(b);
		lblSelectCompanyRecipients.setEnabled(b);
		comboCompanyRecipientChannel.setEnabled(b);
		recipientTab.setVisible(b);
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
	
	private ArrayList<RecipientChannel> getPrivateRecipientList()
	{
		ArrayList<RecipientChannel> recipientChannelList = new ArrayList<RecipientChannel>();
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice"), SendController.SENDTO_FIXED_PRIVATE));//voiceFixedChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice"), SendController.SENDTO_MOBILE_PRIVATE));//voiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_option_sms"), SendController.SENDTO_SMS_PRIVATE));//smsChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_and_sms"), SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_SMS_PRIVATE));//voiceFixedAndSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile_voice"), SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_MOBILE_PRIVATE));//voiceFixedAndVoiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile"), SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE));//voiceFixedAndVoiceMobilePriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED));//voiceMobileAndVoiceFixedPriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_or_sms"), SendController.SENDTO_FIXED_PRIVATE_ALT_SMS));//voiceFixedOrSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_sms_or_fixed_voice"), SendController.SENDTO_SMS_PRIVATE_ALT_FIXED));//smsOrvoiceFixedChannel
		return recipientChannelList;
	}
	private ArrayList<RecipientChannel> getCompanyRecipientList()
	{
		ArrayList<RecipientChannel> recipientChannelList = new ArrayList<RecipientChannel>();
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice"), SendController.SENDTO_FIXED_COMPANY));//voiceFixedChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice"), SendController.SENDTO_MOBILE_COMPANY));//voiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_option_sms"), SendController.SENDTO_SMS_COMPANY));//smsChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_and_sms"), SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_SMS_COMPANY));//voiceFixedAndSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile_voice"), SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_MOBILE_COMPANY));//voiceFixedAndVoiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile"), SendController.SENDTO_FIXED_COMPANY_AND_MOBILE));//voiceFixedAndVoiceMobilePriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), SendController.SENDTO_MOBILE_COMPANY_AND_FIXED));//voiceMobileAndVoiceFixedPriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_or_sms"), SendController.SENDTO_FIXED_COMPANY_ALT_SMS));//voiceFixedOrSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_sms_or_fixed_voice"), SendController.SENDTO_SMS_COMPANY_ALT_FIXED));//smsOrvoiceFixedChannel
		return recipientChannelList;
	}
	private void initRecipientChannel(ChannelType channelType,JComboBox comboRecipientChannel)
	{
		ArrayList<RecipientChannel> recipientChannelList = null;	
		
		switch (channelType) {
		case PRIVATE:
			recipientChannelList = getPrivateRecipientList();
			break;
		case COMPANY:
			recipientChannelList = getCompanyRecipientList();
			break;
		}
		
		comboRecipientChannel.addItem(new RecipientChannel(Localization.l("main_sending_adr_sel_channels"),0));
		for(RecipientChannel channel : recipientChannelList)
		{
			comboRecipientChannel.addItem(channel);
		}
	}
	
	public void set_size(java.awt.Component c, int n_width) {
		//c.setPreferredSize(new Dimension(n_width, 30));
		set_size(c, n_width, 30);
	}
	public void set_size(java.awt.Component c, int n_width, int n_height) {
		c.setPreferredSize(new Dimension(n_width, n_height));		
	}
	
	protected void add_municipals(final AbstractButton btn, final MunicipalJPopupMenu pop, String sz_label)
	{
		//StdTextLabel lbl = new StdTextLabel(sz_label, 14, true);
//		JLabel lbl = new JLabel("<html><b>"+sz_label+"</b></html>");
//		lbl.setOpaque(true);
//		lbl.setBackground(SystemColor.controlDkShadow);
//		lbl.setForeground(SystemColor.controlHighlight);
//		lbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//		
//		pop.add(lbl);
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
		
		
		btn.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent evt) {
	        	popMunicipals();
	        	
	        }
	        public void mouseReleased(MouseEvent evt) {
	        }
	    });
	}
	
	public void popMunicipals()
	{
		m_radio_sendingtype_municipal.setSelected(true);
    	menu_municipals.setLightWeightPopupEnabled(true);
    	
		Point componentLocation = m_radio_sendingtype_municipal.getLocationOnScreen();
		if(!menu_municipals.isShowing())
			menu_municipals.show(null, componentLocation.x, componentLocation.y + m_radio_sendingtype_municipal.getHeight());
		else
			menu_municipals.setVisible(false);		
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
	public void add_adrtypemenus(final AbstractButton btn, final JPopupMenu pop, ButtonGroup group, String sz_label)
	{
		JLabel lbl = new JLabel(sz_label);
		lbl.setOpaque(true);
		lbl.setBackground(SystemColor.controlDkShadow);
		lbl.setForeground(SystemColor.controlHighlight);
		lbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));//(SystemColor.controlDkShadow, 3));
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
	        	if(get_parent()!=null && !get_parent().isLocked() && btn.isEnabled())
	        		pop.show(evt.getComponent(), 0, btn.getHeight());
	        	else if(get_parent()==null)
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
		this.reset_xpanels();
		this.set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.WEST);
		add(m_radio_activate, m_gridconst);
		set_gridconst(inc_xpanels(), get_panel(), 16, 1, GridBagConstraints.WEST);
		add(m_txt_sendname, m_gridconst);
		inc_xpanels2(11);
		//add_spacing(DIR_HORIZONTAL, 15);
		inc_xpanels2(2);
		//addSeparator(15);
		inc_xpanels2();
		m_gridconst.anchor = GridBagConstraints.CENTER;
		add(m_btn_color, m_gridconst);
		inc_xpanels2();
		add(m_radio_sendingtype_polygon, m_gridconst);
		inc_xpanels2();
		add(m_radio_sendingtype_ellipse, m_gridconst);
		inc_xpanels2();
		add(m_radio_sendingtype_municipal, m_gridconst);
		inc_xpanels2();
        add(m_radio_sendingtype_house_select, m_gridconst);
        inc_xpanels2();
		add(m_btn_open, m_gridconst);
		inc_xpanels2();
		add_spacing(DIR_VERTICAL, 15);

		this.reset_xpanels();
		int groupSpacing=5;
		
		inc_panels2();
		inc_xpanels2();
//		add_spacing(DIR_VERTICAL, 15);
		add_spacing(DIR_VERTICAL, 15);
		m_place_holder = new JLabel("");
//		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		m_gridconst.anchor = GridBagConstraints.CENTER;
		set_gridconst(0, get_panel(), 30, 1, GridBagConstraints.NORTHWEST);
		add(lblSelectArea,m_gridconst);
		inc_xpanels2();
		addSeparator(groupSpacing);
//		add_spacing(DIR_VERTICAL, 15);
		set_gridconst(5, get_panel(), 60, 1, GridBagConstraints.NORTHWEST);
		comboAreaList.setPreferredSize(new Dimension(300, 23));
		add(comboAreaList,m_gridconst);
		inc_xpanels2();
		set_gridconst(11, get_panel(), 10, 1, GridBagConstraints.EAST);
		add(btnSaveArea, m_gridconst);

		this.reset_xpanels();
		add_spacing(DIR_VERTICAL, 15);
		inc_panels2();
		//inc_xpanels2();
	
		m_place_holder = new JLabel("");
//		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
//		add(m_place_holder,m_gridconst);
//		addSeparator(2);
//		inc_xpanels2();
		///add(m_btn_adrtypes_abas, m_gridconst);
		m_gridconst.gridwidth=12;
		add(chkLocationBased, m_gridconst);
		
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(groupSpacing);
		inc_xpanels2();
        ///add(m_btn_adrtypes_cell_broadcast_text, m_gridconst);
        
		//simply by not adding below component we can bypass the code written for its event handling, instead of removing it.
		///add(m_btn_adrtypes_private_fixed, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(2);
		inc_xpanels2();
		//simply by not adding below component we can bypass the code written for its event handling, instead of removing it.
		///add(m_btn_adrtypes_private_mobile, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(groupSpacing);
		inc_xpanels2();
		//simply by not adding below component we can bypass the code written for its event handling, instead of removing it.
		///add(m_btn_adrtypes_company_fixed, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		addSeparator(2);
		inc_xpanels2();
		//simply by not adding below component we can bypass the code written for its event handling, instead of removing it.
		///add(m_btn_adrtypes_company_mobile, m_gridconst);
		
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);
		inc_xpanels2();
		addSeparator(2);
		inc_xpanels2();


		add(m_btn_adrtypes_vulnerable, m_gridconst);

		inc_xpanels2();
		addSeparator(2);
		inc_xpanels2();
		add(m_btn_adrtypes_headofhousehold, m_gridconst);
        m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);

		
		addSeparator(groupSpacing);
		
		inc_xpanels2();
        add(m_btn_adrtypes_nofax, m_gridconst);
		m_place_holder = new JLabel("");
		m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		add(m_place_holder,m_gridconst);

		inc_xpanels2(1);
		//m_place_holder = new JLabel("");
		//m_place_holder.setPreferredSize(new Dimension(SIZE_BUTTON_LARGE,SIZE_BUTTON_LARGE));
		//add_spacing(DIR_HORIZONTAL, 10);
		
		m_btn_adrtypes_nophone_private.setSelected(false);
		m_btn_adrtypes_nophone_company.setSelected(false);
		m_btn_adrtypes_nophone_private.setEnabled(false);
		m_btn_adrtypes_nophone_company.setEnabled(false);
		add(m_btn_goto, m_gridconst);
		inc_xpanels2();
		add(m_btn_finalize, m_gridconst);
		inc_xpanels2();
		add(m_btn_send, m_gridconst);
		inc_xpanels2();
		add(m_btn_close, m_gridconst);
		inc_xpanels2();
//		add(btnSaveArea, m_gridconst);
		inc_xpanels2();
		inc_panels2();
		set_gridconst(0, get_panel(), 12, 1, GridBagConstraints.NORTHWEST);	
		//simply by not adding below component we can bypass the code written for its event handling, instead of removing it. 
//		add(m_lbl_addresstypes_private, m_gridconst);
//		add(lblAbasAdrTypes, m_gridconst);
		add(chkAddressBased, m_gridconst);
//		set_gridconst(4, get_panel(), 8, 1, GridBagConstraints.NORTHWEST);
		//simply by not adding below component we can bypass the code written for its event handling, instead of removing it. 
		///add(m_lbl_addresstypes_company, m_gridconst);
		///set_gridconst(8, get_panel(), 8, 1, GridBagConstraints.NORTHWEST);
		//add(m_lbl_addresstypes_lba, m_gridconst);
		
//		inc_panels2();
//		addSeparator(2);

//		inc_xpanels2();
//		addSeparator(2);
//		inc_xpanels2();

//		add_spacing(DIR_VERTICAL, 15);
//		inc_panels2();
//		inc_panels2();
//		set_gridconst(0, get_panel(), 50, 1, GridBagConstraints.NORTHWEST);

//		inc_xpanels2();
//		inc_panels2();
//		inc_xpanels2();


		inc_panels2();
		add_spacing(DIR_VERTICAL, 15);
		add_spacing(DIR_VERTICAL, 15);
		
//		userOptions.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
		recipientTab.setUI(ULookAndFeel.newUTabbedPaneUI(recipientTab, new ULookAndFeel.TabCallback() {

			@Override
			public boolean CloseButtonClicked(JComponent c) {
				return false;
			}

			@Override
			public void CloseButtonHot(JComponent c) {
			}
			}));
		recipientTab.setPreferredSize(new Dimension(520, 180));
		set_gridconst(0, get_panel(), 220, 1, GridBagConstraints.SOUTHWEST);
		add(recipientTab,m_gridconst);

		doLayout();
	}
	public void setActive() {
		//ActionEvent e = new ActionEvent(get_parent(), ActionEvent.ACTION_PERFORMED, "act_activate_sending");
		//actionPerformed(e);
		if(!get_parent().get_sendproperties().getClass().equals(SendPropertiesGIS.class)) {
			log.debug(m_radio_activate.getActionCommand());
			//m_radio_activate.doClick();
		}
	}
	private void autoSelectShapeFromPredefinedArea(ShapeStruct shape)
	{
		ShapeStruct s = null;
		try
		{
			if(shape!=null)
			{
				if(shape instanceof PolygonStruct)
				{
					s = (PolygonStruct)shape.clone();
//					m_radio_sendingtype_polygon.doClick();
					m_radio_sendingtype_polygon.setSelected(true);
//					log.debug("doclick of polygon toggle button called12");
					get_parent().set_type(SendProperties.SENDING_TYPE_POLYGON_);
					get_parent().get_sendproperties().typecast_poly();
					get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.SENDING_POLY, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
					get_callback().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
	//				this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_sendingtype_polygon"));

//					PAS.get_pas().get_mappane().get_actionhandler().addAction("act_add_polypoint","");
				}
				else if (shape instanceof EllipseStruct)
				{
					s = (EllipseStruct)shape.clone();
//					m_radio_sendingtype_ellipse.doClick();
					m_radio_sendingtype_ellipse.setSelected(true);
//					log.debug("doclick of ellipse toggle button called12");
					get_parent().set_type(SendProperties.SENDING_TYPE_CIRCLE_);
					get_callback().actionPerformed(new ActionEvent(no.ums.pas.maps.MapFrame.MapMode.SENDING_ELLIPSE, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
					get_callback().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
	//				this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_sendingtype_ellipse"));

//					PAS.get_pas().get_mappane().get_actionhandler().addAction("act_set_ellipse_corner","");
				}
				s.shapeName = m_txt_sendname.getText();
				PAS.get_pas().kickRepaint();
				setActiveShape(s);
			}
		}
		catch (Exception e) {
		}
		canFinalize();
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
			m_colorpicker = new SendingColorPicker(UIManager.getString("ColorChooser.titleText"),
					new Point(0,0), get_parent().get_sendproperties().get_shapestruct().get_fill_color(), this);		
			m_colorpicker.show();
			//get_colorpicker().showDialog(component, title, get_parent().get_sendproperties().get_shapestruct().get_fill_color())
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
				
				log.debug("Addressfilter: " + this.get_addresstypes());
				e.setSource(get_parent());
				//e.setSource(this);
				switch(get_parent().get_sendproperties().get_sendingtype()) {
					case SendProperties.SENDING_TYPE_POLYGON_:
						try {
							//get_parent().set_sendproperties(new SendPropertiesPolygon(new PolygonStruct(get_pas()), this));
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New polygon sending");
						} catch(Exception err) {
							log.warn(err.getMessage(), err);
							//PAS.get_pas().add_event("ERROR New Sending : " + err.getMessage());
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
						}
						break;
					case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
						try {
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New GIS sending");
						} catch(Exception err) {
							log.warn(err.getMessage(), err);
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
						}
						break;
					case SendProperties.SENDING_TYPE_CIRCLE_:
						try {
							//get_parent().set_sendproperties(new SendPropertiesEllipse(this));
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New ellipse sending");
						} catch(Exception err) {
							log.warn(err.getMessage(), err);
							Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
						}
						break;
					case SendProperties.SENDING_TYPE_MUNICIPAL_:
						try {
							get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "New Municipal sending");							
						} catch(Exception err) {
							log.warn(err.getMessage(), err);
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
			/*if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_voice) && m_btn_adrtypes_cell_broadcast_text.isSelected())
				m_btn_adrtypes_cell_broadcast_text.setSelected(false);
			if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_text) && m_btn_adrtypes_cell_broadcast_voice.isSelected())
				m_btn_adrtypes_cell_broadcast_voice.setSelected(false);*/
			if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_voice))
				m_btn_adrtypes_cell_broadcast_voice.toggleSelection();
//			if(e.getSource().equals(m_btn_adrtypes_cell_broadcast_text))
//				m_btn_adrtypes_cell_broadcast_text.toggleSelection();
			else if(e.getSource().equals(m_btn_adrtypes_nofax))
				m_btn_adrtypes_nofax.toggleSelection();
			else if(e.getSource().equals(m_btn_adrtypes_vulnerable))
				m_btn_adrtypes_vulnerable.toggleSelection();
			else if(e.getSource().equals(m_btn_adrtypes_headofhousehold))
				m_btn_adrtypes_headofhousehold.toggleSelection();
			else if(e.getSource().equals(chkAddressBased))
				changeVisibilityOfABASPanel(chkAddressBased.isSelected());
			gen_addresstypes();
			
			chkLocationBased.setText(gen_adrtypes_text(m_n_addresstypes,ADRGROUPS.LBA));
			chkAddressBased.setText(gen_adrtypes_text(m_n_addresstypes, ADRGROUPS.ABAS));
			
			if(get_parent()!=null && get_parent().isActive()) {
				get_callback().actionPerformed(e);
			}
			else if(get_callback()!=null)
				get_callback().actionPerformed(e);
		}
		else if("act_init_addresstypes".equals(e.getActionCommand())) {
			get_callback().actionPerformed(e);
		}
		else if("act_find_sending".equals(e.getActionCommand())) {
			log.debug("SendOptionToolbar.act_find_sending");
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
		else if("act_save_predefined_area".equals(e.getActionCommand())) {
			log.debug("act_save_predefined_area event called from predefinede area1");
//			btnSaveArea.setEnabled(false);

			if(!PAS.get_pas().get_eastcontent().lockFocusToAreasTab())
			{
				try
				{
					MainAreaController mainAreaController = PAS.get_pas().getPredefinedAreaController();
					AreaController areaCtrl = null;
					if(mainAreaController==null)
					{
						PAS.get_pas().initPredefinedAreaController();
						mainAreaController = PAS.get_pas().getPredefinedAreaController();
					}
					areaCtrl = mainAreaController.getAreaCtrl();
					if (areaCtrl == null) {
						areaCtrl = new AreaController(mainAreaController, mainAreaController.getMapNavigation());
					}
	//				AreaVO area = null;
					areaCtrl.setEditMode(false);
					areaCtrl.createNewArea(this, null, false,AreaSource.NEW_ALERT);
					areaCtrl.setActiveShape(get_parent().get_sendproperties().get_shapestruct());
				}
				catch(Exception ex)
				{
					log.error("error",ex);
				}
			}
		}
		else if("act_save_predefined_area_cancel".equals(e.getActionCommand())) {
//			log.debug("in sendoptiontoolbar act_save_predefined_area_cancel called");
		}
		else if("act_save_predefined_area_complete".equals(e.getActionCommand())) {
			this.btnSaveArea.setEnabled(false);
//			log.debug("in sendoptiontoolbar act_save_predefined_area_complete called");
			setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(m_txt_sendname.getText()));
			get_parent().get_sendproperties().set_sendingname(m_txt_sendname.getText(), "");
			PAS.get_pas().kickRepaint();
		}
		else if("act_sosi_parsing_complete".equals(e.getActionCommand())) {
			@SuppressWarnings("unchecked")
			List<SendObject> sendings_found = (List<SendObject>)e.getSource();
			final int wait_for_sendings = sendings_found.size(); //dont wait for the first one
			ISendingAdded icallback_sendingadded = new ISendingAdded() {
				int sendings = 0;
				@Override
				public void sendingAdded(SendObject obj) {
					++sendings;
					if(wait_for_sendings==this.sendings)
					{
						PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_center_all_polygon_sendings"));
					}
				}
			};
			for(int i=0; i < sendings_found.size(); i++) {
				SendObject obj = sendings_found.get(i);
				if(obj==null)
					continue;
				if(obj.get_sendproperties()==null)
					continue;
				if(obj.get_sendproperties().get_sendingname()==null)
				{
					obj.get_sendproperties().set_sendingname("Unknown Sendingname", "");
				}
				/*if(i==0)
				{
					this.get_parent().get_sendproperties().set_shapestruct(obj.get_sendproperties().get_shapestruct());
					set_sendingtype();
					this.get_parent().get_sendproperties().set_sendingname(obj.get_sendproperties().get_sendingname(), "");
				}
				else*/
				{
					Variables.getSendController().add_sending(obj, false, false, icallback_sendingadded);
				}
					//Variables.getSendController().remove_sending(this);
					//PAS.get_pas().actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_add_sending"));
				log.debug("Adding sending " + obj.get_sendproperties().get_sendingname());
			
			}
			try {
				//PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_center_all_polygon_sendings"));
			} catch(Exception err) {
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
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
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}
			/*ActionEvent e_lock = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_lock");
			actionPerformed(e_lock);*/
			//this.m_btn_finalize.doClick();
			//PAS.get_pas().get_drawthread().set_suspended(false);
			//PAS.get_pas().kickRepaint();
			//Variables.NAVIGATION.gotoMap(sosi.get_bounding());
			/*Variables.NAVIGATION.gotoMap(sosi.get_polygon().calc_bounds());
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

					SendObject activeSendObject = get_parent().get_sendproperties().get_sendobject();
					boolean m_import_more_flag = activeSendObject.get_import_more_flag();
					if(m_import_more_flag) {
						activeSendObject.set_import_more_flag(false);
						GISList prevGISList = activeSendObject.get_sendproperties().typecast_gis().get_gislist();
						if(prevGISList!=null){
							prevGISList.addAll(list);
						}
						else
						{
							log.debug("SendOptionToolbar: new gis list is null");
							prevGISList = list;
						}
						get_parent().get_sendproperties().typecast_gis().set_gislist(prevGISList);
						get_parent().get_sendproperties().set_shapestruct(new GISShape(prevGISList));
						log.debug("SendOptionToolbar: new gis list size=" + prevGISList.size() + " lines");
					}
					else
					{
						get_parent().get_sendproperties().typecast_gis().set_gislist(list);
						get_parent().get_sendproperties().set_shapestruct(new GISShape(list));
					}
					//get_parent().get_sendproperties().typecast_gis().set_gislist(list);
					//get_parent().get_sendproperties().set_shapestruct(new GISShape(list));

					try {
						get_parent().get_sendproperties().goto_area();
					} catch(Exception err) {
						
					}
					log.debug("SendOptionToolbar: " + list.size() + " lines");
				}
			});
		}
		else if("act_sendingtype_polygon".equals(e.getActionCommand())) {
			try
			{
				get_parent().set_type(SendProperties.SENDING_TYPE_POLYGON_);
				get_parent().get_sendproperties().typecast_poly();	
				
				get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.SENDING_POLY, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
				get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
				
				resetMunicipals();
				resetAreaList();
				//enableEdit(false);
				//setActive();
			}
			catch(Exception err) { }
		}
		else if("act_sendingtype_ellipse".equals(e.getActionCommand())) {
			try
			{
				get_parent().set_type(SendProperties.SENDING_TYPE_CIRCLE_);
				get_callback().actionPerformed(new ActionEvent(no.ums.pas.maps.MapFrame.MapMode.SENDING_ELLIPSE, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
				//if(get_callback()!=null)
				get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
				resetMunicipals();
				resetAreaList();
			}
			catch(Exception err) { }
		}
		else if("act_sendingtype_polygonal_ellipse".equals(e.getActionCommand())) {
			try
			{
				get_parent().set_type(SendProperties.SENDING_TYPE_POLYGONAL_ELLIPSE_);
				get_callback().actionPerformed(new ActionEvent(no.ums.pas.maps.MapFrame.MapMode.SENDING_ELLIPSE_POLYGON, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
				//if(get_callback()!=null)
				get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
				resetMunicipals();
				resetAreaList();
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
			resetAreaList();
        	//PAS.get_pas().kickRepaint();
		}
		else if("act_preview_gislist".equals(e.getActionCommand())) {
			if(m_gis_preview==null)
				m_gis_preview = new PreviewFrame(this.get_parent());
			else if(!this.getIsAlert())
				m_gis_preview.setVisible(true);
			resetAreaList();
		}
        else if("act_sendingtype_house_select".equals(e.getActionCommand())) {

            boolean resetSelection = Variables.getMapFrame().get_prev_mode() != MapFrame.MapMode.HOUSESELECT_ALERT ||
                    (Variables.getMapFrame().get_prev_mode() == MapFrame.MapMode.HOUSESELECT_ALERT &&
                    Variables.getMapFrame().get_mode() == MapFrame.MapMode.HOUSESELECT_ALERT);

            if (resetSelection) {
                GISList list = new GISList();
                get_parent().set_type(SendProperties.SENDING_TYPE_GEMINI_STREETCODE_);
                get_parent().get_sendproperties().typecast_gis().set_gislist(list);
                get_parent().get_sendproperties().set_shapestruct(new GISShape(list));

                PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_clear_selected_houses"));
            }
            get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.HOUSESELECT_ALERT, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
            resetAreaList();
            //PAS.get_pas().kickRepaint();
        }
		canFinalize();
	}
	
	/**
	 * To be able to finalize, a shape must be lockable and at least one type of address-type needs to be selected
	 * @return
	 */
	public boolean canFinalize()
	{

		// Hengelåsen skal være visuelt og praktisk deaktivert frem til område og kanal er valgt
		boolean bCanFinalize = !(get_parent() != null && (!get_parent().get_sendproperties().can_lock() || (get_parent().get_sendproperties().get_addresstypes() == 0 || get_parent().get_sendproperties().get_addresstypes() == SendController.SENDTO_USE_NOFAX_COMPANY /* Den skal ikke kunne sende kun nofax */)));
		bCanFinalize = bCanFinalize && (!chkAddressBased.isSelected() || (isAnyABASChannelSelected() && isAnyABASRecipientTypeSelected()));
		m_btn_finalize.setEnabled(bCanFinalize);
		enableSaveArea(bCanFinalize);
		return bCanFinalize;
	}
	private void enableSaveArea(boolean bCanFinalize)
	{
		boolean isPredefinedArea = false;
		boolean isShapeValid = false;
		if (comboAreaList!=null)
			isPredefinedArea = (comboAreaList.getSelectedItem().toString().trim().length() !=0);
//		log.debug("isPredefinedArea="+isPredefinedArea);

		//check if the shape is polygon or ellipse
		try
		{
			if(get_parent().get_sendproperties().get_shapestruct() instanceof PolygonStruct || get_parent().get_sendproperties().get_shapestruct() instanceof EllipseStruct)
				isShapeValid=true;
		} catch(NullPointerException npe){ }

		//check for predefined areas's access
		boolean hasPredefinedAreaAccess = false;
		long storedareas = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_storedareas();
		int phonebook = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().getPhonebook();
//    	log.debug("in enableSaveArea storedareas=" + storedareas + ";phonebook="+phonebook);
		if((storedareas > 0) && (phonebook > PredefinedAreaConstants.READ_ONLY))
			hasPredefinedAreaAccess = true;

		bCanFinalize = bCanFinalize && !(isPredefinedArea) && isShapeValid && hasPredefinedAreaAccess;
		btnSaveArea.setEnabled(bCanFinalize);
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
        if(JOptionPane.showConfirmDialog(this, Localization.l("main_sending_adr_btn_close_sending"), Localization.l("common_are_you_sure"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
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
//	public ToggleAddresstype get_adrtype_cell_broadcast_text() { return m_btn_adrtypes_cell_broadcast_text; }
	public ToggleAddresstype get_adrtype_cell_broadcast_voice() { return m_btn_adrtypes_cell_broadcast_voice; }
	public ToggleAddresstype get_adrtype_nofax() { return m_btn_adrtypes_nofax; }
	public ToggleAddresstype get_adrtype_vulnerable() { return m_btn_adrtypes_vulnerable; }
	public ToggleAddresstype get_adrtype_headofhousehold() { return m_btn_adrtypes_headofhousehold; }
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
			if(get_parent()==null)
				return true;
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
		if(get_parent()!=null && get_parent().setLocked(b)) {
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
//				m_btn_adrtypes_cell_broadcast_text.setEnabled(!b);
				chkLocationBased.setEnabled(!b);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(!b);				
			}
			else
			{
//				m_btn_adrtypes_cell_broadcast_text.setEnabled(false);
				chkLocationBased.setEnabled(false);
				m_btn_adrtypes_cell_broadcast_voice.setEnabled(false);
			}

			m_btn_color.setEnabled(!b);
			m_radio_sendingtype_polygon.setEnabled(!b);
			m_radio_sendingtype_ellipse.setEnabled(!b);
			m_radio_sendingtype_polygonal_ellipse.setEnabled(!b);
			m_radio_sendingtype_municipal.setEnabled(!b);
            m_radio_sendingtype_house_select.setEnabled(!b);
			m_txt_sendname.setEditable(!b);
			m_btn_open.setEnabled(!b);
			m_btn_adrtypes_nofax.setEnabled(!b);
			m_btn_adrtypes_vulnerable.setEnabled(!b);
			m_btn_adrtypes_headofhousehold.setEnabled(!b);
			
			chkAddressBased.setEnabled(!b);
			changeVisibilityOfABASPanel(!b);

			lblSelectArea.setEnabled(!b);
			comboAreaList.setEnabled(!b);
			
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
		if(get_parent()!=null && get_parent().isLocked())
			m_btn_finalize.setActionCommand("act_unlock");
		else
			m_btn_finalize.setActionCommand("act_lock");
	}
	
	public synchronized void focusGained(FocusEvent e) {
		
	}
	public synchronized void focusLost(FocusEvent e) {
		
	}
	public void shapeColorChanged(Color c)
	{
		//remove alpha
		Color setColor = new Color(c.getRed(), c.getGreen(), c.getBlue());
		m_btn_color.setBg(setColor);
	}
	
	class MunicipalJPopupMenu extends JPopupMenu implements ActionListener
	{
		private static final long	serialVersionUID	= 1;
		private JPanel				panelTopBar			= new JPanel();
		private JLabel 				heading 			= null;
		private JButton				close				= null;
		private JPanel				panelMenus			= new JPanel();
		private JScrollPane			scroll				= null;
		private JFrame				jframe				= null;

		public MunicipalJPopupMenu(JFrame jframe) {
			super();
			this.jframe = jframe;
			this.setLayout(new BorderLayout());
			GridLayout gridLayout = new GridLayout(0, 2);
			panelMenus.setLayout(gridLayout);
			panelMenus.setBackground(UIManager.getColor("MenuItem.background"));
			//		panelMenus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			init(jframe);
		}
		
		private void init(JFrame jframe) {
			super.removeAll();
			scroll = new JScrollPane();
			scroll.setViewportView(panelMenus);
			scroll.setBorder(null);
			scroll.setMinimumSize(new Dimension(50, 40));

			scroll.setMaximumSize(new Dimension(scroll.getMaximumSize().width, 
							this.getToolkit().getScreenSize().height
			- this.getToolkit().getScreenInsets(jframe.getGraphicsConfiguration()).top
			- this.getToolkit().getScreenInsets(jframe.getGraphicsConfiguration()).bottom - 10));
			
			heading = new JLabel("<html><b>"+Localization.l("main_sending_type_municipal_select")+"</b></html>");
			heading.setOpaque(true);
			heading.setBackground(SystemColor.controlDkShadow);
			heading.setForeground(SystemColor.controlHighlight);
			heading.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			close = new JButton(ImageLoader.load_icon("delete_16.png"));
			close.setToolTipText(Localization.l("common_close"));
			close.addActionListener(this);
			panelTopBar.setLayout(new BorderLayout());
			panelTopBar.add(heading,BorderLayout.CENTER);
			panelTopBar.add(close,BorderLayout.EAST);
			
			super.add(panelTopBar, BorderLayout.NORTH);
			super.add(scroll, BorderLayout.CENTER);
		}
		
		public void show(Component invoker, int x, int y) {
			init(jframe);
			panelMenus.validate();
			int maxsize = scroll.getMaximumSize().height;
			int realsize = panelMenus.getPreferredSize().height;

			int sizescroll = 0;

			if (maxsize < realsize) {
				sizescroll = scroll.getVerticalScrollBar().getPreferredSize().width;
			}
			
			scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width + sizescroll + 20, 

					scroll.getPreferredSize().height));
			this.pack();
//			this.setInvoker(invoker);
			if (sizescroll != 0) {
				//Set popup size only if scrollbar is visible
				this.setPopupSize(new Dimension(scroll.getPreferredSize().width + 20, 

									scroll.getMaximumSize().height - 20));
			}
			//        this.setMaximumSize(scroll.getMaximumSize());
//			Point invokerOrigin = invoker.getLocationOnScreen();
//			this.setLocation((int) invokerOrigin.getX() + x, (int) invokerOrigin.getY() + y);
			this.setLocation(x, y);
			this.setVisible(true);
		}
		
		public void hidemenu() {
			if (this.isVisible()) {
				this.setVisible(false);
			}
		}

		public void add(MunicipalCheckbox menuItem) {
			//		menuItem.setMargin(new Insets(0, 20, 0 , 0));
			if (menuItem == null) {
				return;
			}
			panelMenus.add(menuItem);
		}
		
		public Component[] getComponents() {
			return panelMenus.getComponents();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			hidemenu();
		}
	}	
}