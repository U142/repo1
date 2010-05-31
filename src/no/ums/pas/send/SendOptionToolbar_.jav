package Send;

import PAS.*;

import javax.swing.*;

import UMS.ErrorHandling.Error;
import UMS.Tools.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

import Importer.*;
import Maps.Defines.*;
import Importer.GIS.*;

public class SendOptionToolbar extends JToolBar implements ActionListener, FocusListener {
	
	public static final int SIZE_TXT			= 100;
	public static final int SIZE_BUTTON_ICON	= 20;
	public static final int SIZE_BUTTON_SMALL	= 25;
	public static final int SIZE_BUTTON_MEDIUM	= 35;
	public static final int SIZE_BUTTON_LARGE	= 50;
	public static final int SIZE_BUTTON_XLARGE	= 70;
	public static final int SIZE_BUTTON_XXLARGE	= 90;
	


	
	//PAS m_pas;
	//private PAS get_pas() { return m_pas; }
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
	public JToggleButton get_radio_polygon() { return m_radio_sendingtype_polygon; }
	public JToggleButton get_radio_ellipse() { return m_radio_sendingtype_ellipse; }
	JButton m_btn_goto;
	ToggleAddresstype m_btn_adrtypes_private_fixed;
	ToggleAddresstype m_btn_adrtypes_company_fixed;
	ToggleAddresstype m_btn_adrtypes_private_mobile;
	ToggleAddresstype m_btn_adrtypes_company_mobile;
	ToggleAddresstype m_btn_adrtypes_nophone_private;
	ToggleAddresstype m_btn_adrtypes_nophone_company;
	ToggleAddresstype m_btn_adrtypes_cell_broadcast_text;
	public ToggleAddresstype get_cell_broadcast_text() { return m_btn_adrtypes_cell_broadcast_text; }
	JToggleButton m_btn_finalize;
	SendingColorPicker m_colorpicker;
	ColorButton m_btn_color;
	JButton m_btn_send;
	JButton m_btn_close;
	JButton m_btn_open;
	private int m_n_addresstypes = 0;
	ActionListener m_callback;
	protected ActionListener get_callback() { return m_callback; }
	
	ImageIcon m_icon_polygon_no_edit = null;
	ImageIcon m_icon_polygon_edit = null;
	ImageIcon m_icon_gis_no_edit = null;
	ImageIcon m_icon_gis_edit = null;
	
	private PreviewFrame m_gis_preview = null;
	public void setPreviewFrame(PreviewFrame f) { m_gis_preview = f; }
	
	public int get_addresstypes() { return m_n_addresstypes; }
	private void click_addresstypes(boolean pf, boolean cf, boolean pm, boolean cm, boolean np) {
		m_btn_adrtypes_private_fixed.setSelected(pf);
		m_btn_adrtypes_company_fixed.setSelected(cf);
		m_btn_adrtypes_private_mobile.setSelected(pm);
		m_btn_adrtypes_company_mobile.setSelected(cm);
		m_btn_adrtypes_nophone_private.setSelected(np);
	}
	public void set_addresstypes(int n_adrtypes) {
		boolean b = false;
		m_n_addresstypes = n_adrtypes;
		if((m_n_addresstypes & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE)
			m_btn_adrtypes_private_fixed.setSelected(true);
		else
			m_btn_adrtypes_private_fixed.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_MOBILE_PRIVATE) == SendController.SENDTO_MOBILE_PRIVATE)
			m_btn_adrtypes_private_mobile.setSelected(true);
		else
			m_btn_adrtypes_private_mobile.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY)
			m_btn_adrtypes_company_fixed.setSelected(true);
		else
			m_btn_adrtypes_company_fixed.setSelected(false);
		if((m_n_addresstypes & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY)
			m_btn_adrtypes_company_mobile.setSelected(true);
		else
			m_btn_adrtypes_company_mobile.setSelected(false);
		/*if((m_n_addresstypes & SendController.SENDTO_NOPHONE_PRIVATE) == SendController.SENDTO_NOPHONE_PRIVATE)
			m_btn_adrtypes_nophone_private.setSelected(true);
		else*/
			m_btn_adrtypes_nophone_private.setSelected(false);
		/*if((m_n_addresstypes & SendController.SENDTO_NOPHONE_COMPANY) == SendController.SENDTO_NOPHONE_COMPANY)
			m_btn_adrtypes_nophone_company.setSelected(true);
		else*/
			m_btn_adrtypes_nophone_company.setSelected(false);
				
		
		/*if(m_n_addresstypes==-1)
			m_btn_finalize.setEnabled(false);
		else
			m_btn_finalize.setEnabled(true);
		switch(get_addresstypes()) {
			case 0:
				click_addresstypes(true, true, false);
				break;
			case 1:
				click_addresstypes(true, false, false);
				break;
			case 2:
				click_addresstypes(false, true, false);
				break;
			case 3:
				click_addresstypes(false, false, true);
				break;
			case 4:
				click_addresstypes(true, false, true);
				break;
			case 5:
				click_addresstypes(false, true, true);
				break;
			case 6:
				click_addresstypes(true, true, true);
				break;
		}*/
/*
	if lAddressTypes=0 then
		szSqlAddressTypesFilter = " AND (BEDRIFT=0 OR BEDRIFT=1)"
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_PRIVATE_ & " / " & GETADR_MAP_ADDRESSTYPES_COMPANY_
	elseif lAddressTypes=1 then
		szSqlAddressTypesFilter = " AND (BEDRIFT=0)"
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_PRIVATE_
	elseif lAddressTypes=2 then
		szSqlAddressTypesFilter = " AND (BEDRIFT=1)"
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_COMPANY_
	elseif lAddressTypes=3 then
		szSqlAddressTypesFilter = " AND (BEDRIFT=2)"
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_MOBILE_
	elseif lAddressTypes=4 then
		szSqlAddressTypesFilter = " AND (BEDRIFT=0 OR BEDRIFT=2)"
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_PRIVATE_ & " / " & GETADR_MAP_ADDRESSTYPES_MOBILE_
	elseif lAddressTypes=5 then
		szSqlAddressTypesFilter = " AND (BEDRIFT=1 OR BEDRIFT=2)"
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_COMPANY_ & " / " & GETADR_MAP_ADDRESSTYPES_MOBILE_
	elseif lAddressTypes=6 then
		szSqlAddressTypesFilter = ""
		szAddressTypesShow = GETADR_MAP_ADDRESSTYPES_PRIVATE_ & " / " & GETADR_MAP_ADDRESSTYPES_COMPANY_ & " / " & GETADR_MAP_ADDRESSTYPES_MOBILE_
	end if
 */
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
	
	public static final int COMPONENTS_ALL_ = BTN_SENDINGTYPE_POLYGON_ | BTN_SENDINGTYPE_ELLIPSE_ | BTN_ADRTYPES_PRIVATE_ |
												BTN_ADRTYPES_COMPANY_ | BTN_ADRTYPES_NOPHONE_ | BTN_COLORPICKER_ | BTN_FINALIZE_ | 
												BTN_SEND_ | BTN_CLOSE_ | BTN_OPEN_ | BTN_ACTIVATE_ | BTN_CENTER_ON_MAP_ | TXT_SENDINGNAME_ | BTN_CELL_BROADCAST_;
	
	private void hide_buttons() {
		switch(get_parent().get_sendproperties().get_sendingtype()) {
			case SendProperties.SENDING_TYPE_POLYGON_:
				break;
			case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
				show_buttons(BTN_SENDINGTYPE_POLYGON_ | BTN_SENDINGTYPE_ELLIPSE_, false);
				break;
		}
	}
	public void showLoader(Component c) {
		show_buttons(COMPONENTS_ALL_, false);
		c.setPreferredSize(new Dimension(getWidth(), getHeight()));
		c.setSize(getWidth(), getHeight());
		add(c);
	}
	public void hideLoader(Component c) {
		remove(c);
		show_buttons(COMPONENTS_ALL_, true);
	}
	
	public void show_buttons(int FLAGS, boolean b_show) {
		if((FLAGS & BTN_SENDINGTYPE_POLYGON_) == BTN_SENDINGTYPE_POLYGON_) {
			this.m_radio_sendingtype_polygon.setVisible(b_show);
		}
		if((FLAGS & BTN_SENDINGTYPE_ELLIPSE_) == BTN_SENDINGTYPE_ELLIPSE_) {
			this.m_radio_sendingtype_ellipse.setVisible(b_show);
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
	}
	
	private void gen_addresstypes() {
		/*boolean b_private = false, b_company = false, b_mobile = false;
		if(m_btn_adrtypes_private.isSelected()) {
			b_private = true;
		}
		if(m_btn_adrtypes_company.isSelected()) {
			b_company = true;
		}
		if(m_btn_adrtypes_mobile.isSelected()) {
			b_mobile = true;
		}
		if(b_private && b_company && b_mobile) set_addresstypes(6);
		else if(b_mobile && b_company && !b_private) set_addresstypes(5);
		else if(b_private && b_mobile && !b_company) set_addresstypes(4);
		else if(b_mobile && !b_private && !b_company) set_addresstypes(3);
		else if(b_company && !b_mobile && !b_private) set_addresstypes(2);
		else if(b_private && !b_mobile && !b_company) set_addresstypes(1);
		else if(b_private && b_company && !b_mobile) set_addresstypes(0);
		else set_addresstypes(-1);*/
		int TYPES = 0;
		if(m_btn_adrtypes_private_fixed.isSelected()) TYPES |= m_btn_adrtypes_private_fixed.get_adrtype();
		if(m_btn_adrtypes_company_fixed.isSelected()) TYPES |= m_btn_adrtypes_company_fixed.get_adrtype();
		if(m_btn_adrtypes_private_mobile.isSelected()) TYPES |= m_btn_adrtypes_private_mobile.get_adrtype();
		if(m_btn_adrtypes_company_mobile.isSelected()) TYPES |= m_btn_adrtypes_company_mobile.get_adrtype();
		if(m_btn_adrtypes_nophone_private.isSelected()) TYPES |= m_btn_adrtypes_nophone_private.get_adrtype();
		if(m_btn_adrtypes_nophone_company.isSelected()) TYPES |= m_btn_adrtypes_nophone_company.get_adrtype();
		if(m_btn_adrtypes_cell_broadcast_text.isSelected()) TYPES |= m_btn_adrtypes_cell_broadcast_text.get_adrtype();
		set_addresstypes(TYPES);
		System.out.println("Addresstypes = " + TYPES);
		
	}
	//SendProperties m_sendproperties;
	//int m_n_sendingtype = SendProperties.SENDING_TYPE_POLYGON_;
	//public int get_sendingtype() { return m_n_sendingtype; }
	private int m_n_initsendnumber = 0;
	public SendingColorPicker get_colorpicker() { return m_colorpicker; }
	public ColorButton get_colorbutton() { return m_btn_color; }
	private JRadioButton m_radio_activate;
	//public SendProperties get_sendproperties() { return m_sendproperties; }
	public int get_sendingid() { 
		return m_n_initsendnumber;
	}
	private GridBagLayout m_gridbag;
	
	
	public SendOptionToolbar(/*PAS pas, */SendObject sendobject, ActionListener callback, int n_sendnumber) {
		super();
		//m_pas = pas;
		set_import_callback(this);
		m_parent = sendobject;
		m_n_initsendnumber = n_sendnumber;
		m_callback = callback;
		//m_gridlayout = new GridBagLayout();
		//m_gridconst  = new GridBagConstraints();
		init();
		this.setSize(340, 25);
		try {
			this.setFloatable(true);
			this.setVisible(true);
			this.setFocusable(true);
		} catch(Exception e) {
			//PAS.get_pas().add_event("ERROR SendOptionToolbar.constructor - " + e.getMessage());
			Error.getError().addError("SendOptionToolbar","Exception in SendOptionToolbar",e,1);
		}
		this.setOrientation(JToolBar.HORIZONTAL);
		setLayout((m_gridbag = new GridBagLayout()));
		//this.setFloatable(false);
		//this.setRollover(true);
		setBorderPainted(true);
	}
	public void close() {
		
		setVisible(false);
	}
	public void set_sendingtype() {
		set_sendingicons();
		hide_buttons();
	}
	private void set_sendingicons() {
		switch(get_parent().get_sendproperties().get_sendingtype()) {
			case SendProperties.SENDING_TYPE_POLYGON_:
				m_radio_activate.setIcon(m_icon_polygon_no_edit);
				m_radio_activate.setSelectedIcon(m_icon_polygon_edit);
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
				m_radio_sendingtype_polygon.setEnabled(true);
				m_radio_sendingtype_ellipse.setEnabled(true);
				break;
			case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
				m_radio_sendingtype_polygon.setEnabled(false);
				m_radio_sendingtype_ellipse.setEnabled(false);
				break;				
		}
	}
	public void init() {
		try {
		m_txt_sendname = new StdTextArea("Sending - part " + m_n_initsendnumber, false);
		set_size(m_txt_sendname, SIZE_TXT);
		m_group_sendingtype = new ButtonGroup();

		m_radio_sendingtype_polygon = new JToggleButton(ImageLoader.load_icon("send_polygon.gif"), true);
		m_radio_sendingtype_ellipse	= new JToggleButton(ImageLoader.load_icon("send_ellipse.gif"));
		m_btn_goto = new JButton(ImageLoader.load_icon("gnome-searchtool_16x16.jpg"));
		m_btn_adrtypes_private_fixed = new ToggleAddresstype(ImageLoader.load_icon("category_phonecalls_16.png"), false, SendController.SENDTO_FIXED_PRIVATE);
		m_btn_adrtypes_company_fixed = new ToggleAddresstype(ImageLoader.load_icon("voice.gif"), false, SendController.SENDTO_FIXED_COMPANY);
		m_btn_adrtypes_private_mobile  = new ToggleAddresstype(ImageLoader.load_icon("gsm.gif"), false, SendController.SENDTO_MOBILE_PRIVATE);
		m_btn_adrtypes_company_mobile = new ToggleAddresstype(ImageLoader.load_icon("gsm.gif"), false, SendController.SENDTO_MOBILE_COMPANY);
		m_btn_adrtypes_nophone_private = new ToggleAddresstype(ImageLoader.load_icon("inhab_private_no_phone.png"), false, SendController.SENDTO_NOPHONE_PRIVATE);
		m_btn_adrtypes_nophone_company = new ToggleAddresstype(ImageLoader.load_icon("inhab_company_no_phone.png"), false, SendController.SENDTO_NOPHONE_COMPANY);
		m_btn_adrtypes_cell_broadcast_text = new ToggleAddresstype(ImageLoader.load_icon("cell_broadcast_text.png"), false, SendController.SENDTO_CELL_BROADCAST_TEXT);
		m_btn_color = new ColorButton(new Color((float)1.0, (float)0.0, (float)0.0), new Dimension(SIZE_BUTTON_ICON, 20));
		m_btn_finalize = new JToggleButton(ImageLoader.load_icon("lock.gif"), false); 
		m_btn_send = new JButton(ImageLoader.load_icon("sendmail.gif"));
		m_btn_close = new JButton(ImageLoader.load_icon("no.gif"));
		//m_radio_activate.setDisabledSelectedIcon(load_icon("no.gif"));
		//m_radio_activate.setSelectedIcon(load_icon("yes.gif"));
		
		m_icon_polygon_no_edit = ImageLoader.load_icon("no_edit.gif");
		m_icon_polygon_edit = ImageLoader.load_icon("edit.gif");
		m_icon_gis_no_edit = ImageLoader.load_icon("no_edit_gis_import.png");
		m_icon_gis_edit = ImageLoader.load_icon("edit_gis_import.png");		
		
		m_radio_activate = new JRadioButton(m_icon_polygon_no_edit, true);
		//set_sendingicons();
		//m_radio_activate.setSelectedIcon(m_icon_polygon_edit);
		
		
		m_btn_open = new JButton(ImageLoader.load_icon("open.gif"));

		m_radio_sendingtype_polygon.setToolTipText("Polygon");
		m_radio_sendingtype_ellipse.setToolTipText("Ellipse");
		m_btn_goto.setToolTipText("Center this sending on map");
		m_btn_adrtypes_private_fixed.setToolTipText("Fixed Private Recipients");
		m_btn_adrtypes_private_mobile.setToolTipText("Mobile Private Recipients");
		m_btn_adrtypes_company_fixed.setToolTipText("Fixed Company Recipients");
		m_btn_adrtypes_company_mobile.setToolTipText("Mobile Company Recipients");
		m_btn_adrtypes_nophone_private.setToolTipText("Private Recipients without phone");
		m_btn_adrtypes_nophone_company.setToolTipText("Company Recipients without phone");
		m_btn_adrtypes_cell_broadcast_text.setToolTipText("Cell Broadcast text message");
		m_btn_finalize.setToolTipText("Finalize");
		m_btn_color.setToolTipText("Color");
		m_btn_send.setToolTipText("Send");
		m_btn_close.setToolTipText("Close sending");
		m_btn_open.setToolTipText("Open polygon");
		
		m_group_sendingtype.add(m_radio_sendingtype_polygon);
		m_group_sendingtype.add(m_radio_sendingtype_ellipse);
		set_size(m_radio_sendingtype_polygon, SIZE_BUTTON_ICON);
		set_size(m_radio_sendingtype_ellipse, SIZE_BUTTON_ICON);
		set_size(m_btn_goto, SIZE_BUTTON_ICON);
		
		set_size(m_btn_adrtypes_private_fixed, SIZE_BUTTON_ICON);
		set_size(m_btn_adrtypes_private_mobile, SIZE_BUTTON_ICON);
		set_size(m_btn_adrtypes_company_fixed, SIZE_BUTTON_ICON);
		set_size(m_btn_adrtypes_company_mobile,  SIZE_BUTTON_ICON);
		set_size(m_btn_adrtypes_nophone_private, SIZE_BUTTON_ICON);
		set_size(m_btn_adrtypes_nophone_company, SIZE_BUTTON_ICON);
		set_size(m_btn_adrtypes_cell_broadcast_text, SIZE_BUTTON_ICON);
		
		set_size(m_btn_color, SIZE_BUTTON_ICON);
		set_size(m_btn_send, SIZE_BUTTON_ICON);
		set_size(m_btn_open, SIZE_BUTTON_ICON);
		set_size(m_radio_activate, SIZE_BUTTON_SMALL);
		set_size(m_btn_finalize, SIZE_BUTTON_ICON);
		set_size(m_btn_close, SIZE_BUTTON_ICON);
		
		m_radio_sendingtype_polygon.addActionListener(this);
		m_radio_sendingtype_ellipse.addActionListener(this);
		m_btn_goto.addActionListener(this);
		m_btn_adrtypes_private_fixed.addActionListener(this);
		m_btn_adrtypes_company_fixed.addActionListener(this);
		m_btn_adrtypes_private_mobile.addActionListener(this);
		m_btn_adrtypes_company_mobile.addActionListener(this);
		m_btn_adrtypes_nophone_private.addActionListener(this);
		m_btn_adrtypes_nophone_company.addActionListener(this);
		m_btn_adrtypes_cell_broadcast_text.addActionListener(this);
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
		m_btn_adrtypes_private_fixed.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_company_fixed.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_private_mobile.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_company_mobile.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nophone_private.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_nophone_company.setActionCommand("act_set_addresstypes");
		m_btn_adrtypes_cell_broadcast_text.setActionCommand("act_set_addresstypes");
		m_btn_close.setActionCommand("act_sending_close");
		m_btn_open.setActionCommand("act_open_polygon");
		} catch(Exception e) {
			//PAS.get_pas().add_event("Error in SendOptionToolbar.init(): " + e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendOptionToolbar","Exception in init",e,1);
		}
		add_controls();
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
		c.setPreferredSize(new Dimension(n_width, 20));
	}
	public void add_controls() {
		add(m_radio_activate);
		this.addSeparator();
		//m_radio_activate.doClick();
		add(m_btn_open);
		
		add(m_txt_sendname);
		this.addSeparator();
		
		add(m_radio_sendingtype_polygon);
		add(m_radio_sendingtype_ellipse);

		this.addSeparator();
		add(m_btn_adrtypes_private_fixed);
		add(m_btn_adrtypes_private_mobile);
		add(m_btn_adrtypes_company_fixed);
		add(m_btn_adrtypes_company_mobile);
		add(m_btn_adrtypes_nophone_private);
		add(m_btn_adrtypes_nophone_company);
		add(m_btn_adrtypes_cell_broadcast_text);
		m_btn_adrtypes_nophone_private.setSelected(false);
		m_btn_adrtypes_nophone_company.setSelected(false);
		m_btn_adrtypes_nophone_private.setEnabled(false);
		m_btn_adrtypes_nophone_company.setEnabled(false);
		//m_btn_adrtypes_cell_broadcast_text.setEnabled(false);

		this.addSeparator();
		add(m_btn_goto);
		
		this.addSeparator();
		add(m_btn_color);
		
		this.addSeparator();
		add(m_btn_finalize);
		add(m_btn_send);
		this.addSeparator();
		add(m_btn_close);
		set_addresstypes(SendController.SENDTO_ALL);
		
		doLayout();
	}
	public void setActive() {
		//ActionEvent e = new ActionEvent(get_parent(), ActionEvent.ACTION_PERFORMED, "act_activate_sending");
		//actionPerformed(e);
		if(!get_parent().get_sendproperties().getClass().equals(SendPropertiesGIS.class))
			m_radio_activate.doClick();
	}
	
	public void setActiveShape(ShapeStruct s) {
		if(s.get_fill_color()!=null)
			get_parent().get_sendproperties().set_color(s.get_fill_color());
		//get_parent().get_sendproperties().typecast_poly().set_polygon(p);
		get_parent().get_sendproperties().set_shapestruct(s);
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
				this.gen_addresstypes();
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
				}
				//get_parent().get_sendcontroller().actionPerformed(e);
				get_callback().actionPerformed(e);
			} catch(Exception err) {
				//PAS.get_pas().add_event("ERROR: act_send_one - " + err.getMessage());
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}
		}
		else if("act_set_addresstypes".equals(e.getActionCommand())) {
			gen_addresstypes();
			if(get_parent().isActive()) {
				//if(((ToggleAddresstype)e.getSource()).isSelected())
					get_callback().actionPerformed(e);
					//PAS.get_pas().get_housecontroller().add_addresstype(((ToggleAddresstype)e.getSource()).get_adrtype());
				//else
					//PAS.get_pas().get_housecontroller().rem_addresstype(((ToggleAddresstype)e.getSource()).get_adrtype());
				//PAS.get_pas().kickRepaint();
			}
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
				if(i==0)
					this.get_parent().get_sendproperties().set_shapestruct(obj.get_sendproperties().get_shapestruct());
				else
					PAS.get_pas().actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_add_sending"));
				System.out.println("Adding sending " + obj.get_name());
			
			}
			try {
				PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_center_all_polygon_sendings"));
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("ImportPolygon","Exception in actionPerformed",err,1);
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
			//PAS.get_pas().get_navigation().gotoMap(sosi.get_bounding());
			/*PAS.get_pas().get_navigation().gotoMap(sosi.get_polygon().calc_bounds());
			try {
				PAS.get_pas().kickRepaint();
				//Thread.sleep(2000);
			} catch(Exception err) {
				
			}*/
			
		}
		else if("act_register_gis_previewframe".equals(e.getActionCommand())) {
			setPreviewFrame((PreviewFrame)e.getSource());
		}
		else if("act_gis_imported".equals(e.getActionCommand())) {
			//PAS.get_pas().add_event("act_gis_imported", null);
			GISList list = (GISList)e.getSource();
			get_parent().set_type(SendProperties.SENDING_TYPE_GEMINI_STREETCODE_);
			get_parent().get_sendproperties().typecast_gis().set_gislist(list);
			get_parent().get_sendproperties().set_shapestruct(new GISShape(list));
			try {
				get_parent().get_sendproperties().goto_area();
			} catch(Exception err) {
				
			}
			System.out.println("SendOptionToolbar: " + list.size() + " lines");
		}
		else if("act_sendingtype_polygon".equals(e.getActionCommand())) {
			get_parent().set_type(SendProperties.SENDING_TYPE_POLYGON_);
			//get_callback().actionPerformed(new ActionEvent(new Integer(Maps.MapFrame.MAP_MODE_SENDING_POLY), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			//if(get_callback()!=null)
			get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
		}
		else if("act_sendingtype_ellipse".equals(e.getActionCommand())) {
			get_parent().set_type(SendProperties.SENDING_TYPE_CIRCLE_);
			//get_callback().actionPerformed(new ActionEvent(new Integer(Maps.MapFrame.MAP_MODE_SENDING_ELLIPSE), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			//if(get_callback()!=null)
			get_callback().actionPerformed(new ActionEvent(get_parent().get_sendproperties().get_shapestruct(), ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));
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
	}
	private ActionListener m_import_callback;
	public void set_import_callback(ActionListener a) { m_import_callback = a; }
	public ActionListener get_import_callback() { return m_import_callback; }
	public void import_polygon() {
		new ImportPolygon(m_import_callback, "act_polygon_imported", this.getIsAlert());
	}
	public void destroy_sending() {
		if(JOptionPane.showConfirmDialog(this, "Close Sending", "Are you sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
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
	public void lock_sending(boolean b) {
		if(get_parent().setLocked(b)) {
			m_btn_send.setEnabled(b);
			m_btn_adrtypes_private_fixed.setEnabled(!b);
			m_btn_adrtypes_company_fixed.setEnabled(!b);
			m_btn_adrtypes_private_mobile.setEnabled(!b);
			m_btn_adrtypes_company_mobile.setEnabled(!b);
			//m_btn_adrtypes_nophone_private.setEnabled(!b);
			//m_btn_adrtypes_nophone_company.setEnabled(!b);
			m_btn_adrtypes_cell_broadcast_text.setEnabled(!b);
			m_btn_color.setEnabled(!b);
			m_radio_sendingtype_polygon.setEnabled(!b);
			m_radio_sendingtype_ellipse.setEnabled(!b);
			m_txt_sendname.setEditable(!b);
			m_btn_open.setEnabled(!b);
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