package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.LightPanel;
import no.ums.pas.core.defines.tree.UMSTree;
import no.ums.pas.core.defines.tree.UMSTreeNode;
import no.ums.pas.send.SendController;
import no.ums.pas.send.messagelibrary.MessageLibTreePanel;
import no.ums.pas.send.messagelibrary.tree.MessageLibNode;
import no.ums.pas.ums.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.Collator;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import sun.awt.AWTCharset.Encoder;


public class Sending_Cell_Broadcast_text extends DefaultPanel implements ActionListener, KeyListener, ItemListener, ComponentListener, FocusListener {
	public static final long serialVersionUID = 1;
	
	Pattern GSM_Alphabet_Regex = Pattern.compile("[|^{}\\[\\]~\\\\]");
	public int m_maxSize = 500;
	public int m_maxSafe = 160;
	private final int m_oadc_maxSize = 11;
	
	protected SendWindow parent = null;
	public SendWindow get_parent() { return parent; }
	public void set_parent(SendWindow parent) { this.parent = parent; }
	protected StdTextLabel m_lbl_expirydate = new StdTextLabel(PAS.l("main_sending_lba_expirytime") + ":");
	protected StdTextLabel m_lbl_messagename = new StdTextLabel(PAS.l("main_sending_lba_messagename") + ":");
	protected StdTextArea m_txt_messagename;
	public StdTextArea get_txt_messagename() { return m_txt_messagename; }
	public StdTextLabel m_lbl_enable_response;
	public JCheckBox m_check_enable_response;
	public JCheckBox get_allow_response() { return m_check_enable_response; }
	public JComboBox m_combo_replynumbers;
	public JComboBox get_replynumbers() { return m_combo_replynumbers; }
	protected StdTextLabel m_lbl_messagetext = new StdTextLabel(PAS.l("main_sending_lba_message") + ":");
	protected StdTextLabel m_lbl_area = new StdTextLabel(PAS.l("main_sending_lba_area") + ":");
	protected StdTextLabel m_lbl_messagesize = new StdTextLabel("(0 " + PAS.l("common_x_of_y") + " " + m_maxSize + ")");
	public JLabel get_lbl_localsize() { return m_lbl_messagesize; }
	protected StdTextLabel m_lbl_internationalsize = new StdTextLabel("(0 " + PAS.l("common_x_of_y") + " " + m_maxSize + ")");
	public JLabel get_lbl_internationalsize() { return m_lbl_internationalsize; }
	public StdTextLabel m_lbl_oadc_text = new StdTextLabel(PAS.l("main_sending_lba_sender_text") + ":"); // 11
	public StdTextLabel get_lbl_oadc_text() { return m_lbl_oadc_text; }
	public StdTextArea m_txt_oadc_text;
	public StdTextArea get_txt_oadc_text() { return m_txt_oadc_text; }
	public JList m_lst_cc;
	protected JScrollPane listScrollPane;
	protected DefaultListModel listModel;
	protected JComboBox m_cbx_messages;
	public JComboBox get_cbx_messages() { return m_cbx_messages; }
	protected JButton m_btn_new = new JButton(PAS.l("common_new"));
	protected JButton m_btn_add = new JButton(PAS.l("common_add"));
	protected JButton m_btn_delete = new JButton(PAS.l("common_delete"));
	protected int m_n_expiry_minutes = 0;
	public int get_expiryMinutes() { return m_n_expiry_minutes; }
	public void set_expiryMinutes(int n) { 
		m_n_expiry_minutes = n;
		if(m_n_expiry_minutes<=0){
			for(int i=0;i<m_combo_expdate.getItemCount();++i)
				if(((ExpiryMins)m_combo_expdate.getItemAt(i)).get_minutes().equals("60"))
					m_combo_expdate.setSelectedIndex(i);
		}
		else {
			for(int i1=0;i1<m_combo_expdate.getItemCount();++i1)
				if(((ExpiryMins)m_combo_expdate.getItemAt(i1)).get_minutes().equals(String.valueOf(n)))
					m_combo_expdate.setSelectedIndex(i1);
			//m_combo_expdate.setSelectedItem(new Integer(n).toString());
		}
	}
	public int n_focuscounter=0;
	public JComboBox m_combo_templates = new JComboBox();
	public StdTextLabel m_lbl_template = new StdTextLabel(PAS.l("main_sending_text_template") + ":");
	protected MessageLibTreePanel tree_msglib;
	protected StdSearchArea txt_msglib_search;

	// Expiry date
	protected JComboBox m_combo_expdate;
	
	protected ScrollPane local_scroll;
	protected JTextArea m_txt_messagetext;
	public JTextArea get_txt_messagetext() { return m_txt_messagetext; }
	private JScrollPane m_sp_messagetext;
	protected JComboBox m_combo_area;
	public JComboBox get_combo_area() { return m_combo_area; }
	protected LightPanel m_panel_messages;
	protected LightPanel m_panel_area;
	
	public JScrollPane getTextScroller() { return m_sp_messagetext; }
	public JTextArea getTextArea() { return m_txt_messagetext;  }
	
	protected StdTextLabel m_lbl_requesttype = new StdTextLabel(PAS.l("main_status_locationbased_alert_short"));
	protected JRadioButton m_radio_requesttype_0 = new JRadioButton(PAS.l("main_sending_settings_lba_send_now"), true);
	protected JRadioButton m_radio_requesttype_1 = new JRadioButton(PAS.l("main_sending_settings_lba_send_confirm"));
	protected ButtonGroup btn_group_requesttype = new ButtonGroup();
	
	protected UnderlineHighlightPainter painter = new UnderlineHighlightPainter(Color.red);

	
	public int get_requesttype() {
		if(m_radio_requesttype_0.isSelected())
				return 0;
		else
			return 1;
	}
	public void set_requesttype(int n_requesttype) {
		if(n_requesttype==0)
			m_radio_requesttype_0.setSelected(true);
		else
			m_radio_requesttype_1.setSelected(true);
	}
	protected int m_n_requesttype = 0;
	
	public int getRequestType() { return m_n_requesttype; }
	
	private Sending_Cell_Broadcast_text() {
		this(PAS.get_pas());
		addCellFocusListener();
	}
	
	//use this as super in sms_Broadcast_text constructor
	public Sending_Cell_Broadcast_text(SendWindow parentwin) {
		this(PAS.get_pas());
		parent = parentwin;
	}
	
	//use this for LBA dialogs
	public Sending_Cell_Broadcast_text(PAS pas, SendWindow parentwin) {
		this();
		parent = parentwin;
	}
	
	/*public Sending_Cell_Broadcast_text(PAS pas, AlertWindow parentwin) {
		this();
		parent = parentwin;
	}*/

	
	private Sending_Cell_Broadcast_text(PAS pas) {
		super();
		//m_panel_messages	= new DefaultPanel(pas);
		//m_panel_area		= new DefaultPanel(pas);
		
//		this.setPreferredSize(new Dimension(810, 610));
		m_panel_messages = new LightPanel();
		//m_panel_area	 = new LightPanel();
		//this.addComponentListener(this);
		
		m_lbl_messagename.setPreferredSize(new Dimension(150, 70));
		m_lbl_messagename.setAlignmentX(StdTextLabel.TOP_ALIGNMENT);
		
		//m_lst_cc = new List();
		//m_lst_cc.setMultipleMode(true);
		listModel = new DefaultListModel();
		m_lst_cc = new JList(listModel); //data has type Object[]
		m_lst_cc.setSelectionModel(new DefaultListSelectionModel()
									{
									public static final long serialVersionUID = 1;
										public void setSelectionInterval(int index0, int index1) {
											if (isSelectedIndex(index0))
												super.removeSelectionInterval(index0, index1);
											else
												super.addSelectionInterval(index0, index1);
											}
									}
		);
		m_lst_cc.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//ListSelectionModel.SINGLE_SELECTION);
		m_lst_cc.setLayoutOrientation(JList.VERTICAL);
		m_lst_cc.setVisibleRowCount(-1);
        listScrollPane = new JScrollPane(m_lst_cc);
        listScrollPane.setPreferredSize(new Dimension(160, 70));
		
		//m_lst_cc.setVisibleRowCount(-1);		
		Set forwardkeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set newForwardKeys = new HashSet(forwardkeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		//m_txt_messagename.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
		
		m_lbl_messagetext.setPreferredSize(new Dimension(150, 70));
		m_lbl_messagetext.setAlignmentX(StdTextLabel.TOP_ALIGNMENT);
		
		m_txt_messagetext = new JTextArea(35,30);
		m_sp_messagetext = new JScrollPane(m_txt_messagetext);
		m_sp_messagetext.setPreferredSize(new Dimension(200, 70));
		m_txt_messagetext.setLineWrap(true);
		m_txt_messagetext.setWrapStyleWord(true);
		
		m_txt_messagetext.setPreferredSize(new Dimension(200, 70));
		m_txt_messagetext.addKeyListener(this);
		m_txt_messagetext.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
		
		m_txt_messagetext.setText(PAS.l("main_sending_lba_message"));
		
		m_cbx_messages = new JComboBox();
		m_cbx_messages.addItemListener(this);
		//m_cbx_messages.setVisible(false);
		
		m_lbl_area.setPreferredSize(new Dimension(150, 20));
		m_combo_area = new JComboBox();
		m_combo_area.setPreferredSize(new Dimension(200, 20));
		m_combo_area.addItem(new Area("5", "Hellesylt (ID 5)"));
		m_combo_area.addItem(new Area("6", "Geiranger (ID 6)"));
		m_combo_area.addItem(new Area("7", "Karmøy (ID 7)"));
		m_combo_area.addItem(new Area("4", "Sandnes (ID 4)"));
		m_combo_area.addItem(new Area("3", "Skøyen (ID 3)"));
		m_combo_area.addItem(new Area("1", "Test 1 (ID 1)"));
		m_combo_area.addItem(new Area("2", "Test 2 (ID 2)"));
		
		m_lbl_oadc_text.setPreferredSize(new Dimension(150, 20));
		
		m_btn_add.addActionListener(this);
		m_btn_new.addActionListener(this);
		m_btn_delete.addActionListener(this);
		
		String comp_name = PAS.get_pas().get_userinfo().get_compid();
		
		if(comp_name.length() > 11)
			comp_name = comp_name.substring(0,11);
		
		m_txt_messagename = new StdTextArea("", false);
		m_txt_messagename.setPreferredSize(new Dimension(200,20));
		m_txt_messagename.addKeyListener(this);
		
		m_txt_oadc_text = new StdTextArea(comp_name, false);
		m_txt_oadc_text.setPreferredSize(new Dimension(200, 20));
		m_txt_oadc_text.addKeyListener(this);
		m_combo_templates.setPreferredSize(new Dimension(200, 20));
		//m_combo_templates.addActionListener(this); Is now added in Sending_Settings populate_smstemplates to avoid action performed when downloading
		m_combo_templates.setActionCommand("act_smstemplate_changed");
		
		//tree_msglib = new MessageLibTreePanel(Sending_Cell_Broadcast_text.this, UMSTree.TREEMODE.SELECTION_ONLY,10, false);
		//tree_msglib.getTree().setIconSize(TREEICONSIZE.SMALL);
		txt_msglib_search = new StdSearchArea("", false, "Search");
		txt_msglib_search.addActionListener(this);
		txt_msglib_search.setPreferredSize(new Dimension(150, 20));
		
		m_radio_requesttype_0.addActionListener(this);
		m_radio_requesttype_1.addActionListener(this);
		m_radio_requesttype_0.setActionCommand("act_requesttype_0");
		m_radio_requesttype_1.setActionCommand("act_requesttype_1");
		btn_group_requesttype.add(m_radio_requesttype_0);
		btn_group_requesttype.add(m_radio_requesttype_1);
		//setRequestType(m_n_requesttype);
		
		add_controls();
	}
	
	public void addComboTemplatesActionListener() {
		m_combo_templates.addActionListener(this);
	}
	
	public void addCellFocusListener()
	{
		m_txt_messagetext.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if(n_focuscounter == 0) {
					m_txt_messagetext.setText("");
					keyReleased(new KeyEvent(m_txt_messagetext,KeyEvent.KEY_RELEASED, 10, 0, 0));
				}
				++n_focuscounter;
			}

			@Override
			public void focusLost(FocusEvent e) {
				
			}
			
		});		
	}
	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}
	
	public String validateFields() {
		
		//if(get_txt_messagetext().getText().length() < 1)
		//	return "Cell Broadcast requires a message";
		if(get_txt_oadc_text().getText().length() < 1)
			return PAS.l("main_sending_lba_error_need_sender_text");
		else if(validateSenderText())
			return PAS.l("main_sending_lba_error_content");

		return null;
	}
	
	public boolean defaultLanguage() {
		for(int i=0;i<m_cbx_messages.getItemCount();i++) {
			CBMessage cbm = (CBMessage)m_cbx_messages.getItemAt(i);
			for(int j=0;j<cbm.getCcodes().size();j++) {
				if(((CCode)cbm.getCcodes().get(j)).getCCode().equals("-1"))
					return true;
			}
		}
		return false;
	}
	
	private boolean validateSenderText() {
		for(int i=0;i<m_cbx_messages.getItemCount();i++) {
			if(validateString(((CBMessage)m_cbx_messages.getItemAt(i)).getCboadc())) {
				m_cbx_messages.setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}
	
	protected boolean validateMessageText() {
		// Her vet jeg ikke om jeg trenger validering
		return false;
	}
	
	private boolean validateString(String text) {
		Pattern p = Pattern.compile("[\\W&&[^/-]]");
		Matcher m = p.matcher(text);
		return m.find();
	}
	
	public boolean validateOADC(String text) {
		Pattern p = Pattern.compile("[\\W]");
		Matcher m = p.matcher(text);
		return m.find();
	}
	
	public void add_controls() {
		int n_width = 21;
		
		m_panel_messages.setOpaque(false);
		//m_panel_area.setOpaque(false);
		m_panel_messages.setBackground(new Color(255,255,255,1));
		//m_panel_area.setBackground(new Color(255,255,255,1));
		m_panel_messages.setPreferredSize(new Dimension(550, 340));
		//m_panel_area.setPreferredSize(new Dimension(500, 100));
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), n_width/2, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_expirydate, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3, m_panel_messages.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		//SchedCalendar exp_cal = new SchedCalendar(14);
		//String[] mins = new String[]{ new String(""), new String("30"), new String("60"), new String("120"), new String("360"), new String("720"), new String("1440") };
		ExpiryMins[] mins = new ExpiryMins[]{ new ExpiryMins(""), new ExpiryMins("30"), new ExpiryMins("60"), new ExpiryMins("120"), new ExpiryMins("360"), new ExpiryMins("720"), new ExpiryMins("1440") };
		
		m_combo_expdate = new JComboBox(mins);
		m_combo_expdate.addItemListener(this);
		//m_combo_exptimehour = new JComboBox(exp_cal.get_hours());
		//m_combo_exptimeminute = new JComboBox(exp_cal.get_minutes());
		m_panel_messages.add(m_combo_expdate, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3 + n_width/3, m_panel_messages.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		//m_panel_messages.add(new JLabel("Minutes"), m_panel_messages.m_gridconst);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), n_width/2, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_messagename, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/2, m_panel_messages.get_panel(), n_width/2, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_txt_messagename, m_panel_messages.m_gridconst);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), n_width/2, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_oadc_text, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/2, m_panel_messages.get_panel(), n_width/2, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_txt_oadc_text, m_panel_messages.m_gridconst);
		
		m_panel_messages.add_spacing(DefaultPanel.DIR_VERTICAL,20);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		m_panel_messages.add(listScrollPane, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3, m_panel_messages.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_sp_messagetext, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3 + n_width/3, m_panel_messages.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_messagesize, m_panel_messages.m_gridconst);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_cbx_messages, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3, m_panel_messages.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		
		m_panel_messages.set_gridconst(0, m_panel_messages.inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		m_panel_messages.add(m_lbl_requesttype, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3, m_panel_messages.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		JPanel pnl_lba = new JPanel();
		pnl_lba.setOpaque(false);
		pnl_lba.add(m_radio_requesttype_0);
		pnl_lba.add(m_radio_requesttype_1);
		m_panel_messages.add(pnl_lba, m_panel_messages.m_gridconst);
		m_panel_messages.set_gridconst(n_width/3, m_panel_messages.inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		
		JPanel pnl = new JPanel();
		pnl.setOpaque(false);
		pnl.add(m_btn_new);
		pnl.add(m_btn_add);
		pnl.add(m_btn_delete);
		
		m_panel_messages.add(pnl, m_panel_messages.m_gridconst);
		//m_panel_messages.add(lba_conf, m_panel_messages.m_gridconst);
//		set_gridconst(n_width/3 + n_width/3, get_panel(), n_width/3, 1, GridBagConstraints.WEST);
//		add(m_btn_add, m_gridconst);
		
		//m_panel_area.set_gridconst(0, m_panel_area.inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		//m_panel_area.add(m_lbl_area, m_panel_area.m_gridconst);
		//m_panel_area.set_gridconst(n_width/2, m_panel_area.get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		//m_panel_area.add(m_combo_area, m_panel_area.m_gridconst);
		
		
		
		m_panel_messages.setBorder(BorderFactory.createTitledBorder(PAS.l("main_sending_lba_heading_messages")));
		//m_panel_area.setBorder(BorderFactory.createTitledBorder("Area"));
		
		set_gridconst(0, 0, 1, 1);
		add(m_panel_messages, m_gridconst);
		//set_gridconst(0, 1, 1, 1);
		//add(m_panel_area, m_gridconst);
		
		init();
	}
	
	public void downloadMessageLib()
	{
		if(tree_msglib!=null)
			tree_msglib.getTree().startUpdater(true);
	}
	public void stopMessageLib()
	{
		if(tree_msglib!=null)
			tree_msglib.getTree().stopUpdater();
	}
	
	public void setSendingType(int nType) {
		if((nType & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE)
			m_panel_messages.setVisible(false);
		else if((nType & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT)
			m_panel_messages.setVisible(true);
			
	}
	
	public void init() {
		//CountryCodes.load();
		ArrayList<CCode> ccs = CountryCodes.getCountryCodes();
		
		for(int i=0;i<ccs.size();i++) {
			CCode cc = (CCode)ccs.get(i);
			//if(cc.isVisible())
				//m_lst_cc
				//m_lst_cc.add(cc.getCountry(),i);
			if(cc.isVisible())
				listModel.addElement(cc);
		}
		m_btn_delete.setEnabled(false);
		setVisible(true);
	}
	
	public ArrayList<Object> getMessages() {
		ArrayList<Object> al = new ArrayList<Object>();
		
		for(int i=0;i<m_cbx_messages.getItemCount();i++) {
			al.add(m_cbx_messages.getItemAt(i));
		}
		return al;
	}
	
	public void setMessages(ArrayList<Object> messages) {
		Iterator<Object> it = messages.iterator();
		while(it.hasNext()) {
			CBMessage cbm = (CBMessage)it.next();			
			m_cbx_messages.addItem(cbm);
			Iterator <CCode>it2 = cbm.getCcodes().iterator();
			while(it2.hasNext())
				removeFromCCodes(it2.next());
		}
	}
	
	private boolean removeFromCCodes(CCode cc) {
		return listModel.removeElement(cc);
	}
	
	private void removeCCodes(CBMessage cbm) {
		Iterator it = cbm.getCcodes().iterator();
		
		while(it.hasNext()) {
			removeFromCCodes((CCode)it.next());
		}
	}
	
	private void addToCCodes(CBMessage cbm) {
		Iterator it = cbm.getCcodes().iterator();
		while(it.hasNext())
			listModel.addElement(it.next());
		sort_cclist();
	}
	private boolean checkInput(Point p, JFrame frame) {
		if(m_lst_cc.isSelectionEmpty()) {
			p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
			frame.setLocation(p);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(frame, PAS.l("main_sending_lba_error_country_or_default"));
			frame.dispose();
			return false;
		}
		else if(m_txt_messagename.getText().length()<1)	{
			p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
			frame.setLocation(p);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(frame, PAS.l("main_sending_lba_error_no_name"));
			frame.dispose();
			return false;
		}
		else if(m_txt_oadc_text.getText().length()<1) {
			p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
			frame.setLocation(p);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(frame, PAS.l("main_sending_lba_error_content"));
			frame.dispose();
			return false;
		}
		else if(m_txt_messagetext.getText().length()<1) {
			p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
			frame.setLocation(p);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(frame, PAS.l("main_sending_lba_error_no_content"));
			frame.dispose();
			return false;
		}
		return true;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_btn_add && m_btn_add.getText().equals("Save")) {
			JFrame frame = new JFrame();
			frame.setUndecorated(true);
			Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
			
			if(checkInput(p, frame))
			{
				CBMessage cbm = (CBMessage)m_cbx_messages.getItemAt(m_cbx_messages.getSelectedIndex());
				// Oppdater endringene
				cbm.setMessage(m_txt_messagetext.getText());
				cbm.setMessageName(m_txt_messagename.getText());
				cbm.setCboadc(m_txt_oadc_text.getText());
				cbm.getCcodes().clear();
				cbm.setCcodes(getCCodes());
				resetTextFields();
				// nullstill messagetext og navn
			}
		}
		else if(e.getSource() == m_btn_add) {
			JFrame frame = new JFrame();
			frame.setUndecorated(true);
			Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
			
			if(checkInput(p, frame))
			{
				CBMessage cbm = new CBMessage(m_txt_messagename.getText(), getCCodes(), m_txt_messagetext.getText(), m_txt_oadc_text.getText());
				if(!m_cbx_messages.isVisible())
					m_cbx_messages.setVisible(true);
				m_cbx_messages.addItem(cbm);
				m_btn_new.doClick();
			}
			
		}
		else if(e.getSource() == m_btn_new) {
			// tilbakestill alt og nullstill messagename og messagetext + deselect alle ccodes
			for(int i=0;i<m_cbx_messages.getItemCount();i++) {
				removeCCodes((CBMessage)m_cbx_messages.getItemAt(i));
			}
			resetTextFields();
			
		}
		else if(e.getSource() == m_btn_delete) {
			m_cbx_messages.removeItemListener(this);
			m_cbx_messages.removeItemAt(m_cbx_messages.getSelectedIndex());
			m_cbx_messages.addItemListener(this);
			resetTextFields();
			for(int i=0;i<listModel.getSize();i++) {
				if(m_lst_cc.isSelectedIndex(i))
					m_lst_cc.setSelectedIndex(i);
			}
		}
		else if("act_requesttype_0".equals(e.getActionCommand()))
			m_n_requesttype = 0;
		else if("act_requesttype_1".equals(e.getActionCommand()))
			m_n_requesttype = 1;
		else if(UMSTree.TREE_SELECTION_CHANGED.equals(e.getActionCommand()))
		{
			try
			{
				MessageLibNode node = (MessageLibNode)e.getSource();
				final String msg = node.getMessage().getSzMessage();
				SwingUtilities.invokeLater(new Runnable() { 
					public void run() {
						m_txt_messagetext.setText(msg);
						m_txt_messagetext.setToolTipText(PAS.l("main_sending_text_template_tooltip"));
						UpdateTextFields();
						n_current_bracket = -1;
						m_txt_messagetext.grabFocus();
					}
				});
			}
			catch(Exception err)
			{
				
			}

		}
		else if(StdSearchArea.ACTION_SEARCH_CLEARED.equals(e.getActionCommand()))
		{
			if(tree_msglib!=null)
			{
				tree_msglib.getTree().getTreeRenderer().setSearchString("");
				tree_msglib.getTree().searchTreeNode("");
			}
		}
		else if(StdSearchArea.ACTION_SEARCH_UPDATED.equals(e.getActionCommand()))
		{
			if(tree_msglib!=null)
			{
				tree_msglib.getTree().getTreeRenderer().setSearchString((String)e.getSource());
				List<UMSTreeNode> l = tree_msglib.getTree().searchTreeNode((String)e.getSource());
				for(int i=0; i < l.size(); i++)
				{
					tree_msglib.getTree().scrollPathToVisible(tree_msglib.getTree().getPath(l.get(i)));
				}
			}
		}


			
	}
	
	private void resetTextFields() {
		m_txt_messagename.setText("");
		m_txt_messagetext.setText(PAS.l("main_sending_lba_message"));
		n_focuscounter=0;
		m_txt_oadc_text.setText(PAS.get_pas().get_userinfo().get_compid());
		UpdateTextFields();
		m_cbx_messages.setSelectedIndex(-1);
		m_btn_delete.setEnabled(false);
		m_btn_add.setText("Add");
	}
	protected void UpdateTextFields() {
		set_size_label(m_txt_messagetext.getText(), m_lbl_messagesize);		
	}
	
	protected void FillSMSTemplates() {
		try
		{
			if(get_parent()==null)
				return;
			m_combo_templates.removeAllItems();
			m_combo_templates.addItem(new String (""));
			if(get_parent().get_smstemplates()!=null)
			{
				for(int i=0; i < get_parent().get_smstemplates().size(); i++)
				{
					m_combo_templates.addItem(get_parent().get_smstemplates().get(i));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	public ArrayList<CCode> getCCodes() {
		/*Object selected[] = m_lst_cc.getSelectedObjects();*/
		//listModel.get
		Object selected [] = m_lst_cc.getSelectedValues();
		ArrayList<CCode> al = new ArrayList<CCode>();
		
		for(int i=0;i<selected.length;i++) {
			al.add((CCode)selected[i]);
			listModel.removeElement((CCode)selected[i]);
		}
		return al;
	}
	
	private void selectAddedCCodes(CBMessage cbm) {
		Iterator it = cbm.getCcodes().iterator();
		int indices[] = new int[cbm.getCcodes().size()];
		int i=0;
		
		while(it.hasNext()) {
			indices[i] = listModel.indexOf(it.next());
			i++;
		}
		
		m_lst_cc.setSelectedIndices(indices);
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public void set_size_label(String text, JLabel activeLabel) {
		
		/*Matcher m = GSM_Alphabet_Regex.matcher(text);
		int ext = 0;
		while(m.find() == true)
			++ext;
			*/
		//System.out.println("Extended chars = " + ext);
		
		activeLabel.setText("(" + get_gsmsize(text) + " " + PAS.l("common_x_of_y") + " " + m_maxSize + ")");
		
		if(text.length() > m_maxSafe) {
			activeLabel.setForeground(Color.RED);
			//activeLabel.setFont(new Font(null,Font.BOLD, parent.getFont().getSize()));
			activeLabel.setToolTipText(m_maxSafe + " " + PAS.l("main_sending_lba_messagetextlabel"));
		}
		else {
			activeLabel.setForeground(PAS.get_pas().getForeground());
			//activeLabel.setFont(new Font(null, parent.getFont().getStyle(), parent.getFont().getSize()));
			activeLabel.setToolTipText(null);
		}
			
	}
	
	public int get_gsmsize(String text) {
		Matcher m = GSM_Alphabet_Regex.matcher(text);
		int ext = 0;
		while(m.find() == true)
			++ext;
		return text.length()+ext;
	}

	public void keyPressed(KeyEvent e) {
//		int location = e.getKeyLocation(); 
//		if(e.getSource() == m_txt_localtext && location == KeyEvent.VK_TAB)
//			m_txt_internationaltext
		if(e.getKeyChar()=='\t')
		{
			e.consume();
			if(e.isShiftDown())
				tabPressedRev();
			else
				tabPressed();
		}
		resetHighLights();

	}
	int n_current_bracket = -1;
	int n_highlight_bracket = -1;
	
	public void tabPressedRev()
	{
		Point p = gotoPrevBracket(true);
		if(p!=null)
		{
			m_txt_messagetext.setSelectionStart(p.x);
			m_txt_messagetext.setSelectionEnd(p.y+1);
		}
	}
	public void tabPressed()
	{
		Point p = gotoNextBracket(true);
		if(p!=null)
		{
			m_txt_messagetext.setSelectionStart(p.x);
			m_txt_messagetext.setSelectionEnd(p.y+1);
		}
	}
	
	public Point gotoPrevBracket(boolean b_restart)
	{
		int index = (b_restart ? n_current_bracket : n_highlight_bracket);
		String s = m_txt_messagetext.getText();
		int end = s.lastIndexOf(']', (index>0 ? index-1 : m_txt_messagetext.getText().length()));
	if(end<0 && b_restart)
		{
			end = s.lastIndexOf(']', m_txt_messagetext.getText().length()); //Restart
		}
		if(end<=m_txt_messagetext.getText().length())
		{
			int start = s.lastIndexOf('[', end);
			if(end>start)
			{
				if(b_restart)
					n_current_bracket = start;
				else
					n_highlight_bracket = start;
				return new Point(start, end);
				//m_txt_messagetext.setSelectionStart(start);
				//m_txt_messagetext.setSelectionEnd(end+1);
			}
		}
		return null;
	}
	
	public Point gotoNextBracket(boolean b_restart)
	{
		int index = (b_restart ? n_current_bracket : n_highlight_bracket);
		String s = m_txt_messagetext.getText();
		int start = s.indexOf('[', (index>=0 ? index+1 : 0));
		if(start<0 && b_restart)
		{
			start = s.indexOf('[', 0); //Restart
		}
		if(start>=0)
		{
			int end = s.indexOf(']', start);
			if(end>start)
			{
				if(b_restart)
					n_current_bracket = start;
				else
					n_highlight_bracket = start;
				return new Point(start, end);
				//m_txt_messagetext.setSelectionStart(start);
				//m_txt_messagetext.setSelectionEnd(end+1);
			}
		}
		return null;
	}
	public void resetHighLights()
	{
		n_highlight_bracket = -1;
		Point p;
		while((p=gotoNextBracket(false))!=null)
		{
			try
			{
				m_txt_messagetext.getHighlighter().addHighlight(p.x, p.y, painter);
			} 
			catch(Exception e){
				
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		
//		if(e.getSource() == m_txt_messagename && m_txt_messagename.getText().length() > m_maxSize) {
//			m_txt_messagename.setText(m_txt_messagename.getText().substring(0,160));
//			set_size_label(m_txt_messagename.getText(), m_lbl_messagesize);
//		
//		} else if (e.getSource() == m_txt_messagename)
//			set_size_label(m_txt_messagename.getText(), m_lbl_messagesize);
		
		Matcher m = GSM_Alphabet_Regex.matcher(m_txt_messagetext.getText());
		int ext = 0;
		while(m.find() == true)
			++ext;
		
		if(e.getSource() == m_txt_messagetext && (m_txt_messagetext.getText().length()+ext) > m_maxSize) {
			m_txt_messagetext.setText(m_txt_messagetext.getText().substring(0,(m_maxSize-ext)));
			set_size_label(m_txt_messagetext.getText(), m_lbl_messagesize);
		}  else if (e.getSource() == m_txt_messagetext)
			set_size_label(m_txt_messagetext.getText(), m_lbl_messagesize);
		
		if(e.getSource() == m_txt_oadc_text && m_txt_oadc_text.getText().length()>11) {
			m_txt_oadc_text.setText(m_txt_oadc_text.getText().substring(0,m_oadc_maxSize));
		}
	}
	public void itemStateChanged(ItemEvent e) {
//		if(e.getSource() == m_cbx_messages && e.getStateChange() == ItemEvent.SELECTED && 
//				parent.get_tabbedpane().getSelectedComponent().equals(this)) {
		if(e.getSource() == m_cbx_messages && e.getStateChange() == ItemEvent.SELECTED) {
			// Først må jeg passe på at ingen av ccodes ligger igjen
			for(int i=0;i<m_cbx_messages.getItemCount();i++) {
				removeCCodes((CBMessage)m_cbx_messages.getItemAt(i));
			}
			CBMessage cbm = (CBMessage)m_cbx_messages.getItemAt(m_cbx_messages.getSelectedIndex());
			
			// Når du velger en melding fra messages skal denne populate'e de andre inputene.
			addToCCodes(cbm);
			// inkludert å legge til de tilhørene ccodes og velge dem i listen og forandre add knappen til save
			selectAddedCCodes(cbm);
			
			m_txt_messagename.setText(cbm.getMessageName());
			m_txt_messagetext.setText(cbm.getMessage());
			m_txt_oadc_text.setText(cbm.getCboadc());
			set_size_label(cbm.getMessage(),m_lbl_messagesize);
			m_btn_add.setText("Save");
			m_btn_delete.setEnabled(true);
		}
		if(e.getSource() == m_combo_expdate) {
			if((((ExpiryMins)m_combo_expdate.getSelectedItem()).get_minutes()).equals(""))
				m_n_expiry_minutes = 0;
			else
				m_n_expiry_minutes = Integer.parseInt(((ExpiryMins)m_combo_expdate.getSelectedItem()).get_minutes());
			
		}
	}
	
	public void sort_cclist() {
		DefaultListModel dlm = listModel;//(DefaultListModel) m_lst_cc.getModel();
		int numItems = dlm.getSize();
		CCode[] a = new CCode[numItems];
		for (int i=0;i<numItems;i++){
			a[i] = (CCode)dlm.getElementAt(i);
		}
		sortArray(Collator.getInstance(),a);
		//Locale loc = Locale.NORWEGIAN;
	    //sortArray(Collator.getInstance(loc), (String[])a);
	    for (int i=0;i<numItems;i++) {
	    	dlm.setElementAt(a[i], i);
	    }
	}
	
	public void sortArray(Collator collator, CCode[] strArray) {
		if (strArray.length == 1) return;
		/*for (int i = 0; i < strArray.length; i++) {
			for (int j = i + 1; j < strArray.length; j++) {
				if( collator.compare(strArray[i], strArray[j] ) > 0 ) {
					tmp = strArray[i];
					strArray[i] = strArray[j];
					strArray[j] = tmp;
				}
			}
		} */
		//Arrays.sort(strArray);
/*		Collections.sort(listModel, new Comparator() {
			public final int compare ( Object a, Object b )
			{
				return ((a.toString().toLowerCase().
	                    compareTo((b.toString().toLowerCase()))));
			}
		});	*/	
	}	
	public void componentResized(ComponentEvent e) {
		/*System.out.println(getWidth() + " " + getHeight());
		this.setSize(getWidth() - 10, getHeight()-10);
		m_panel_area.setSize(getWidth()- 20, (getHeight()-20)/2);
		m_panel_messages.setSize(getWidth()- 20, (getHeight()-20)/2);
		m_panel_area.revalidate();
		m_panel_messages.revalidate();
		revalidate();*/
		if(getWidth()<=0 || getHeight()<=0)
		{
			super.componentResized(e);
			return;
		}
		m_panel_messages.setPreferredSize(new Dimension(getWidth(), getHeight()));
		if(tree_msglib!=null)
			tree_msglib.setPreferredSize(new Dimension(getWidth()-40, 120));
		//m_sp_messagetext.setPreferredSize(new Dimension(getWidth()-100, 150));
		super.componentResized(e);
	}
	public void componentMoved(ComponentEvent e) {
		
	}
	public void componentShown(ComponentEvent e) {
		downloadMessageLib();
	}
	public void componentHidden(ComponentEvent e) {
		
	}
	@Override
	public void focusGained(FocusEvent e) {
		
	}
	@Override
	public void focusLost(FocusEvent e) {
		
	}

}
