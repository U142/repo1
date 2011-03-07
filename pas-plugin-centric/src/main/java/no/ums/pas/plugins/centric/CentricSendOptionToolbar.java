package no.ums.pas.plugins.centric;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.core.project.Project;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.PLMNShape;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.converters.UShapeToShape;
import no.ums.pas.plugins.centric.send.RROComboBox;
import no.ums.pas.plugins.centric.send.RROComboEditor;
import no.ums.pas.plugins.centric.send.RROComboRenderer;
import no.ums.pas.plugins.centric.status.CentricStatusController;
import no.ums.pas.plugins.centric.tools.CentricPrintCtrl;
import no.ums.pas.plugins.centric.ws.WSCentricRRO;
import no.ums.pas.plugins.centric.ws.WSCentricSend;
import no.ums.pas.send.messagelibrary.MessageLibDlg;
import no.ums.pas.ums.tools.*;
import no.ums.ws.common.MDVSENDINGINFOGROUP;
import no.ums.ws.common.UBBMESSAGE;
import no.ums.ws.common.cb.*;
import no.ums.ws.common.parm.UPLMN;
import no.ums.ws.common.parm.UPolygon;
import no.ums.ws.common.parm.UPolypoint;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CentricSendOptionToolbar extends DefaultPanel implements ActionListener, FocusListener, 
																	KeyListener, MouseListener {

	int MAX_MESSAGELENGTH_PR_PAGE = 93;
	int MAX_PAGES = 15;
	int MAX_EVENTNAME_LENGTH = 50;
	int MAX_TOTAL_CHARS = MAX_MESSAGELENGTH_PR_PAGE * MAX_PAGES;
	
	int MAX_TOTAL_AUTHORIZATION_CHARS = 255;
	
	enum MODE
	{
		INITIALIZING,
		MESSAGE_WRITING,
		SHOWING_SUMMARY,
		SHOWING_AUTHORIZATION,
		SENDING,
	}
	
	int m_pages = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StdTextLabel m_lbl_event_name;
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
	private StdTextLabel m_lbl_warning;
		
	private StdTextArea m_txt_event_name;
	private String m_sz_date;
	private StdTextArea m_txt_sender_name;
	private StdTextArea m_txt_date_time;
	private StdTextAreaNoTab m_txt_message;
	private JScrollPane m_txt_messagescroll;
	private JTextArea m_txt_authorization;
	private JScrollPane m_txt_authorizationscroll;
	
	//private JComboBox m_cbx_channel;
	//private JComboBox m_cbx_duration;
	//private JComboBox m_cbx_date;
	//private JComboBox m_cbx_time_hour;
	//private JComboBox m_cbx_time_minute;
	
	private StdTextLabel m_lbl_preview;
	private JTextArea m_txt_preview;
	private JScrollPane m_txt_previewscroll;
	
	private RROComboBox m_cbx_risk;
	private RROComboBox m_cbx_reaction;
	private RROComboBox m_cbx_originator;
	private JComboBox m_cbx_time_hour;
	private JComboBox m_cbx_time_minute;
	
	/*private StdTextLabel m_lbl_schedule;
	private JRadioButton m_rad_immediate;
	private JRadioButton m_rad_schedule;
	private ButtonGroup m_grp_schedule;*/
	
	private JButton m_btn_send;
	private JButton m_btn_address_book; // Add image/icon
	private JButton m_btn_update;
	private JButton m_btn_messagelib;
	private JButton m_btn_send_for_auth;
	
	private JButton m_btn_save_message;
	private JButton m_btn_reset;
	
	public JButton get_reset() { return m_btn_reset; }
	
	private JTextArea m_txt_warning;
	private JScrollPane m_txt_warningscroll;
	
	private JButton m_btn_print;
	private JButton m_btn_cancel;
	
	private Calendar c;
	
	protected MODE current_mode = MODE.MESSAGE_WRITING;
	
	LoadingFrame progress = new LoadingFrame(PAS.l("main_statustext_lba_sending"), null);

    {
        progress = new LoadingFrame(PAS.l("main_statustext_lba_sending"), null);
    }

    protected long n_parent_refno = 0;
	public boolean isResend() { return n_parent_refno>0; }
	public void setParentRefno(long parent_refno)
	{
		n_parent_refno = parent_refno;
	}
	public long getParentRefno()
	{
		return n_parent_refno;
	}
	
	//private CentricStatus m_centricstatus;
	
	private DeptInfo m_dept_send_on_behalf_of = null;
	protected void setDeptSendOnBehalfOf(DeptInfo d)
	{
		m_dept_send_on_behalf_of = d;
		System.out.println("Sending owned by " + d.get_deptid());
	}
	
	/**
	 * Check which dept that should own this sending
	 */
	protected void findDeptSendOnBehalfOf()
	{
		m_dept_send_on_behalf_of = null;
		ShapeStruct mapshape = Variables.getMapFrame().get_active_shape();

		//determine where the painting started
		if(mapshape.getClass().equals(PolygonStruct.class))
		{
			PolygonStruct mappoly = mapshape.typecast_polygon();
			
			DeptArray da = Variables.getUserInfo().get_departments();
			for(int i=0; i < da.size(); i++)
			{
				DeptInfo di = (DeptInfo)da.get(i);
				if(di.get_restriction_shapes().size()<=0)
					continue;
				ShapeStruct s = di.get_restriction_shapes().get(0);
				if(!s.getClass().equals(PolygonStruct.class))
					continue;
				if(!mappoly.isElliptical())
				{
					MapPointLL firstpoint = mappoly.getFirstPoint();
					if(s.pointInsideShape(firstpoint))
					{
						//success
						setDeptSendOnBehalfOf(di);
						break;
					}
				}
				else
				{
					MapPointLL ll = mappoly.getEllipseCenter().get_mappointll();
					if(s.pointInsideShape(ll))
					{
						//success
						setDeptSendOnBehalfOf(di);
						break;
					}
				}
			}
		}
		else if(mapshape.getClass().equals(PLMNShape.class))
		{
			setDeptSendOnBehalfOf(Variables.getUserInfo().get_default_dept());
		}
		if(m_dept_send_on_behalf_of==null)
			setDeptSendOnBehalfOf(Variables.getUserInfo().get_default_dept());
	}
	//private CentricStatusController m_centricstatuscontroller;
	
	public CentricStatusController getStatusController() { return (CentricStatusController)PAS.get_pas().get_statuscontroller(); }
	
	//public void set_centricController(CentricStatusController controller) { m_centricstatuscontroller = controller; }
	
	private long m_projectpk = 0;
	public boolean projectOpen() { return m_projectpk>0; }
	
	public void set_projectpk(long projectpk, String sz_projectname) { 
		if(projectpk <= 0)
		{
			m_txt_event_name.setEnabled(true);
			autoClickButton(m_btn_reset);
			
		}
		else
		{
			m_txt_event_name.setEnabled(false);
			//m_txt_event_name.setText(sz_projectname);
			setEventText(sz_projectname);
		}
		m_projectpk = projectpk;
	}
	
	protected void setEventText(String s)
	{
		m_txt_event_name.setText(s);
		m_txt_event_name.setToolTipText(s);
	}
	
	//public CentricStatusController get_statuscontroller() { return m_centricstatuscontroller; }
	
	public CentricSendOptionToolbar() {
		//super();
		current_mode = MODE.INITIALIZING;
		init();
		
		WSCentricRRO rro = new WSCentricRRO(this, "act_download_risk_reaction_originator_finished");
		try
		{
			rro.call();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public void doInit() {
		init();
	}
	private static int lbl_width = 120;
	private static int input_width = 260;
	private static int btn_width = 110;
	private static int btn_height = 23;
	
	public void init() {
		m_lbl_event_name = new StdTextLabel(PAS.l("main_sending_event_name") + ":",new Dimension(lbl_width,20));
		m_lbl_sender_name = new StdTextLabel(PAS.l("main_sending_lba_sender_text") + ":", new Dimension(lbl_width,20));
		m_lbl_date_time = new StdTextLabel(PAS.l("common_date") + " - " + PAS.l("common_time") + ":", new Dimension(lbl_width,20));
		m_lbl_message = new StdTextLabel(PAS.l("common_message_content") + ":",new Dimension(lbl_width,20));
		/*m_lbl_channel = new StdTextLabel("Channel:",new Dimension(150,20));
		m_lbl_duration = new StdTextLabel("Duration:",new Dimension(150,20));
		m_lbl_date = new StdTextLabel("Date:",new Dimension(150,20));
		m_lbl_time = new StdTextLabel("Time:",new Dimension(150,20));*/
		
		m_lbl_risk = new StdTextLabel(PAS.l("common_risk") + ":",new Dimension(lbl_width,20));
		m_lbl_reaction = new StdTextLabel(PAS.l("common_reaction") + ":",new Dimension(lbl_width,20));
		m_lbl_originator = new StdTextLabel(PAS.l("common_originator") + ":",new Dimension(lbl_width,20));
				
		m_txt_event_name = new StdTextArea("",new Dimension(input_width,20));
		m_txt_event_name.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_event_name.addFocusListener(this);
		m_txt_event_name.addKeyListener(this);
		
		m_txt_sender_name = new StdTextArea("",new Dimension(input_width,20));
		m_txt_sender_name.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_sender_name.addFocusListener(this);
		m_txt_sender_name.addKeyListener(this);
		
		m_txt_date_time = new StdTextArea("",new Dimension(input_width,20));
		m_txt_date_time.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_date_time.addFocusListener(this);
		
		m_sz_date = getFormatedDate();
		m_txt_date_time.setText(m_sz_date);
		m_txt_date_time.addFocusListener(this);
		m_txt_date_time.setEnabled(false);
		
		m_btn_update = new JButton(PAS.l("common_update"));
		m_btn_update.setPreferredSize(new Dimension(btn_width, btn_height));
		m_btn_update.addActionListener(this);
		
		m_txt_message = new StdTextAreaNoTab(this, "",1,1,"[^A-Za-z0-9]");
		m_txt_message.setWrapStyleWord(true);
		m_txt_message.setLineWrap(true);
		m_txt_message.addFocusListener(this);
		m_txt_message.addKeyListener(this);
		
		/*m_txt_message.setFocusTraversalKeysEnabled(true);
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		m_txt_message.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		m_txt_message.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
*/
		//m_txt_message.setPreferredSize(new Dimension(300,200));
		
		m_txt_messagescroll = new JScrollPane(m_txt_message);
		//m_txt_messagescroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_txt_messagescroll.setPreferredSize(new Dimension(input_width,100));
		
		//m_btn_messagelib = new JButton(PAS.l("main_sending_audio_type_library"));
		ImageIcon ico = ImageLoader.load_icon("messagelibrary.png");
		m_btn_messagelib = new JButton(ico);
		m_btn_messagelib.setToolTipText(PAS.l("main_sending_audio_type_library"));
		m_btn_messagelib.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
		m_btn_messagelib.addActionListener(this);
		
		//m_btn_messagelib.setPreferredSize(new Dimension(50, 20));
		
		
		/*m_cbx_channel = new JComboBox(new String[] { "Dutch - Channel 1" });
		m_cbx_channel.setPreferredSize(new Dimension(150,20));
		m_cbx_duration = new JComboBox(new ExpiryMins[]{ new ExpiryMins("30"), new ExpiryMins("60"), new ExpiryMins("120"), new ExpiryMins("360"), new ExpiryMins("720"), new ExpiryMins("1440") });
		m_cbx_duration.setPreferredSize(new Dimension(150,20));
		m_cbx_date = new JComboBox(new String[] { "10.05.2010" });
		m_cbx_time_hour = new JComboBox(new String[] { "15" });
		m_cbx_time_minute = new JComboBox(new String[] { "30" });*/
		
		m_cbx_risk = new RROComboBox(CBRISK.class);
		m_cbx_risk.setPreferredSize(new Dimension(input_width,20));
		m_cbx_risk.addFocusListener(this);
		
		m_cbx_reaction = new RROComboBox(CBREACTION.class);
		m_cbx_reaction.setPreferredSize(new Dimension(input_width,20));
		m_cbx_reaction.addFocusListener(this);
		
		m_cbx_originator = new RROComboBox(CBORIGINATOR.class);
		m_cbx_originator.setPreferredSize(new Dimension(input_width,20));
		m_cbx_originator.addFocusListener(this);
		
		
		m_cbx_originator.setRenderer(new RROComboRenderer(m_cbx_originator));
		m_cbx_reaction.setRenderer(new RROComboRenderer(m_cbx_reaction));
		m_cbx_risk.setRenderer(new RROComboRenderer(m_cbx_risk));
		
		m_cbx_originator.setEditor(new RROComboEditor());
		m_cbx_reaction.setEditor(new RROComboEditor());
		m_cbx_risk.setEditor(new RROComboEditor());
		
		m_cbx_originator.getEditor().getEditorComponent().addFocusListener(this);
		m_cbx_reaction.getEditor().getEditorComponent().addFocusListener(this);
		m_cbx_risk.getEditor().getEditorComponent().addFocusListener(this);

		m_cbx_originator.getEditor().getEditorComponent().addKeyListener(this);
		m_cbx_reaction.getEditor().getEditorComponent().addKeyListener(this);
		m_cbx_risk.getEditor().getEditorComponent().addKeyListener(this);

		m_cbx_risk.setEditable(true);
		m_cbx_reaction.setEditable(true);
		m_cbx_originator.setEditable(true);

		m_cbx_originator.addActionListener(this);
		m_cbx_reaction.addActionListener(this);
		m_cbx_risk.addActionListener(this);
		

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
		m_lbl_preview = new StdTextLabel(PAS.l("common_preview"),new Dimension(150,20));
		m_txt_preview = new JTextArea("",1,1);
		m_txt_preview.setWrapStyleWord(true);
		m_txt_preview.setLineWrap(true);
		m_txt_preview.setEnabled(false);
		
		m_txt_previewscroll = new JScrollPane(m_txt_preview);
		
		m_lbl_pages = new StdTextLabel(PAS.l("common_pages") + " 1/" + MAX_PAGES, 100);
		m_lbl_characters = new StdTextLabel(PAS.l("common_characters") + " 0/" + MAX_TOTAL_CHARS, 200);
		
		m_btn_send = new JButton(PAS.l("main_sending_send"));
		m_btn_send.setPreferredSize(new Dimension(input_width,30));
		m_btn_address_book = new JButton("image"); // Add image/icon
		
		
		
		m_btn_save_message = new JButton(PAS.l("main_sending_save_message_template"));
		m_btn_reset = new JButton(PAS.l("common_reset"));
		m_btn_reset.addActionListener(this);
		m_btn_reset.addFocusListener(this);
		m_btn_send.addActionListener(this);
		m_btn_send.addFocusListener(this);
		m_btn_reset.setPreferredSize(new Dimension(btn_width, btn_height));
		m_btn_save_message.setPreferredSize(new Dimension(input_width/2, btn_height));
		m_btn_send_for_auth = new JButton(PAS.l("main_send_to_address_book_for_authorization"));
		m_btn_send_for_auth.addActionListener(this);
		m_btn_send_for_auth.setEnabled(false);
		
		m_txt_warning = new JTextArea(PAS.l("main_sending_send_warning"),1,1);
		m_txt_warning.setWrapStyleWord(true);
		m_txt_warning.setLineWrap(true);
		//m_txt_warning.setText();
		m_txt_warningscroll = new JScrollPane(m_txt_warning);
		m_txt_warningscroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		//m_btn_close = new JButton(ImageLoader.load_icon("delete_24.png"));
		//m_btn_close.addActionListener(this);
		//m_btn_close.setActionCommand("act_sending_close");
		//m_btn_close.setToolTipText(PAS.l("main_sending_adr_btn_close_sending"));
		
		add_controls();
		m_txt_event_name.setEditable(true);
		m_txt_message.setEditable(true);
		m_txt_sender_name.setEditable(true);
		m_txt_date_time.setEditable(true);
		
		Variables.getMapFrame().addMouseListener(this);
		Variables.getMapFrame().addKeyListener(this);
		setVisible(true);
		//m_btn_reset.doClick();
		autoClickButton(m_btn_reset);
	}
	
	protected void autoClickButton(final JButton b)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				b.doClick();
			}
		});
	}
	
	protected void checkForEnableSendButton()
	{
		switch(current_mode)
		{
		case INITIALIZING: //no checks, wait for the first automated reset click to goto MESSAGE_WRITING mode
			m_btn_send.setEnabled(false);
			break;
		case MESSAGE_WRITING: //if all fields are filled, we may enable send button
			boolean b = checkInputs();
			m_btn_send.setEnabled(b);
			break;
		case SHOWING_SUMMARY: //if training mode is off, we may enable send button
			//m_btn_send.setEnabled(!PAS.TRAINING_MODE);
			if(PAS.TRAINING_MODE) {
                m_btn_send.setToolTipText(PAS.l("mainmenu_trainingmode"));
            }
			else
				m_btn_send.setToolTipText(null);
			break;
		case SHOWING_AUTHORIZATION:
			int n_auth_text = m_txt_authorization.getText().length();
			m_btn_send.setEnabled(n_auth_text>0);
			break;
		case SENDING:
			m_btn_send.setEnabled(false);
			break;
		}
		m_btn_save_message.setEnabled(false);
	}
	
	public void add_controls() {
		ENABLE_GRID_DEBUG = false;
		removeAll();
		reset_panels();
		set_gridconst(0, get_panel(), 1, 1);
		add(m_lbl_event_name, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_event_name, m_gridconst);
		//m_txt_event_name.setEnabled(true);
		m_txt_event_name.setEnabled(!projectOpen());
		
		
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
		set_gridconst(8, get_panel(), 1, 1);
		add(m_btn_messagelib, m_gridconst);
		
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
		
		
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_lbl_pages, m_gridconst);
		
		set_gridconst(2, get_panel(), 7, 1);
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
		m_btn_send.setPreferredSize(new Dimension(input_width,30));
		//set_gridconst(3, get_panel(), 1, 1);
		//add(m_btn_close, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 50);
		
		set_gridconst(8, inc_panels(), 1, 1);
		add(m_btn_reset, m_gridconst);
		
		//set_gridconst(2, get_panel(), 1, 1);
		//add(m_btn_save_message, m_gridconst);
		
		m_txt_previewscroll.setPreferredSize(new Dimension(input_width,100));
		m_txt_event_name.setPreferredSize(new Dimension(input_width, m_lbl_preview.getPreferredSize().height));

		//m_lbl_characters.setPreferredSize(new Dimension(200, m_lbl_characters.getPreferredSize().height));
		updateCharacters();
		revalidate();
		repaint();
	}
	
	private void showSummary() {
		
		findDeptSendOnBehalfOf();
		//if(projectOpen())
			m_txt_event_name.setEnabled(false);
		removeAll();
		reset_panels();
		set_gridconst(0, get_panel(), 1, 1);
		add(m_lbl_event_name, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_event_name, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_preview, m_gridconst);
		set_gridconst(1, get_panel(), 3, 2);
		add(m_txt_previewscroll, m_gridconst);
		set_gridconst(0, inc_panels(), 1, 1);
		if(m_btn_print==null) {
			m_btn_print = new JButton(PAS.l("common_print_summary"));
			m_btn_print.setActionCommand("act_print_summary");
			m_btn_print.addActionListener(this);
		}
		add(m_btn_print, m_gridconst);
		
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_lbl_pages, m_gridconst);
		set_gridconst(2, get_panel(), 7, 1);
		add(m_lbl_characters, m_gridconst);
		
		
		add_spacing(DIR_VERTICAL, 20);
		
		
		//set_gridconst(0, inc_panels(), 8, 1);
		//add(m_txt_warningscroll, m_gridconst);
		m_txt_warning.setEnabled(false);
		Font f = UIManager.getFont("SendingWarningText.font");
		m_txt_warning.setFont(f);
		Color c = UIManager.getColor("SendingWarningText.foreground");
		m_txt_warning.setForeground(c);
		m_txt_warning.setDisabledTextColor(c);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 8, 1);
		//progress.get_progress().setSize(new Dimension(450,25));
		progress.set_totalitems(0, PAS.l("main_statustext_lba_sending"));
		//progress.get_progress().setPreferredSize(new Dimension(450,25));
		progress.stop_and_hide();
		add(progress.get_progress(), m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		if(m_btn_cancel == null) {
			m_btn_cancel = new JButton(PAS.l("common_cancel"));
			m_btn_cancel.addActionListener(this);
		}
		
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		add(m_btn_send, m_gridconst);
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		add(m_btn_save_message, m_gridconst);
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		add(m_btn_send_for_auth, m_gridconst);
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		add(m_btn_cancel, m_gridconst);
		
		
		m_btn_send.setActionCommand("act_goto_authorization");
		int width_left = 550-100;
		progress.get_progress().setPreferredSize(new Dimension(width_left,25));
		//progress.get_progress().setVisible(false);
		m_txt_previewscroll.setPreferredSize(new Dimension(width_left-m_lbl_preview.getPreferredSize().width,250));
		m_txt_warningscroll.setPreferredSize(new Dimension(width_left,70));
		m_txt_event_name.setPreferredSize(new Dimension(width_left-m_lbl_preview.getPreferredSize().width, m_lbl_preview.getPreferredSize().height));

		m_lbl_characters.setPreferredSize(new Dimension(200, m_lbl_characters.getPreferredSize().height));
		m_btn_send.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_cancel.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_save_message.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_send_for_auth.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		updateCharacters();
		revalidate();
		repaint();
		
	}
	
	private void showAuthorization() {
		
		findDeptSendOnBehalfOf();
		//if(projectOpen())
			m_txt_event_name.setEnabled(false);
		removeAll();
		reset_panels();
		set_gridconst(0, get_panel(), 1, 1);
		add(m_lbl_event_name, m_gridconst);
		set_gridconst(1, get_panel(), 7, 1);
		add(m_txt_event_name, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_preview, m_gridconst);
		set_gridconst(1, get_panel(), 3, 2);
		add(m_txt_previewscroll, m_gridconst);
		set_gridconst(0, inc_panels(), 1, 1);
		/*if(m_btn_print==null) {
			m_btn_print = new JButton(PAS.l("common_print_summary"));
			m_btn_print.setActionCommand("act_print_summary");
			m_btn_print.addActionListener(this);
		}
		add(m_btn_print, m_gridconst);
		*/
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_lbl_pages, m_gridconst);
		set_gridconst(2, get_panel(), 7, 1);
		add(m_lbl_characters, m_gridconst);
		
		
		add_spacing(DIR_VERTICAL, 20);
		
		set_gridconst(1, inc_panels(), 7, 1);
		add(m_txt_warningscroll, m_gridconst);
		m_txt_warning.setEnabled(false);
		Font f = UIManager.getFont("SendingWarningText.font");
		m_txt_warning.setFont(f);
		Color c = UIManager.getColor("SendingWarningText.foreground");
		m_txt_warning.setForeground(c);
		m_txt_warning.setDisabledTextColor(c);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 1, 1);
		m_lbl_warning = new StdTextLabel(PAS.get_pas().l("common_show_message_authorization_text"));
		add(m_lbl_warning, m_gridconst);
		
		m_txt_authorization = new JTextArea("",1,1);
		m_txt_authorization.setWrapStyleWord(true);
		m_txt_authorization.setLineWrap(true);
		m_txt_authorization.addKeyListener(this);
		m_txt_authorizationscroll = new JScrollPane(m_txt_authorization);
		set_gridconst(1, get_panel(), 7, 1);
		m_txt_authorizationscroll.setPreferredSize(new Dimension(m_txt_previewscroll.getWidth(),40));
		add(m_txt_authorizationscroll, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 8, 1);
		//progress.get_progress().setSize(new Dimension(450,25));
		progress.set_totalitems(0, PAS.l("main_statustext_lba_sending"));
		//progress.get_progress().setPreferredSize(new Dimension(450,25));
		progress.stop_and_hide();
		add(progress.get_progress(), m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		if(m_btn_cancel == null) {
			m_btn_cancel = new JButton(PAS.l("common_cancel"));
			m_btn_cancel.addActionListener(this);
		}
		
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		add(m_btn_send, m_gridconst);
		//set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		//add(m_btn_save_message, m_gridconst);
		//set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		//add(m_btn_send_for_auth, m_gridconst);
		set_gridconst(1, inc_panels(), 3, 1, GridBagConstraints.EAST);
		add(m_btn_cancel, m_gridconst);
		
		
		m_btn_send.setActionCommand("act_send");
		int width_left = 550-100;
		progress.get_progress().setPreferredSize(new Dimension(width_left,25));
		//progress.get_progress().setVisible(false);
		m_txt_previewscroll.setPreferredSize(new Dimension(width_left-m_lbl_preview.getPreferredSize().width,250));
		m_txt_warningscroll.setPreferredSize(new Dimension(m_txt_previewscroll.getPreferredSize().width,70));
		m_txt_event_name.setPreferredSize(new Dimension(width_left-m_lbl_preview.getPreferredSize().width, m_lbl_preview.getPreferredSize().height));

		m_lbl_characters.setPreferredSize(new Dimension(200, m_lbl_characters.getPreferredSize().height));
		m_btn_send.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_cancel.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_save_message.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_send_for_auth.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		updateCharacters();
		revalidate();
		repaint();
		
	}
	
	public boolean lockSending(boolean b)
	{
		ShapeStruct ps = Variables.getMapFrame().get_active_shape();
		ps.setEditable(!b);
		return b;
	}
	
	public void trainingModeChanged()
	{
		/*if(PAS.TRAINING_MODE)
			m_btn_send.setEnabled(false);
		else
			m_btn_send.setEnabled(true);*/
		checkForEnableSendButton();
		
	}
	
	protected void resetRRO()
	{
		m_cbx_risk.setSelectedItem("");
		m_cbx_reaction.setSelectedItem("");
		m_cbx_originator.setSelectedItem("");
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(m_btn_update)) {
			if(!projectOpen())
			{
				/*if(m_txt_event_name.getText().endsWith(" " + m_sz_date))
					setEventText(m_txt_event_name.getText().substring(0,m_txt_event_name.getText().length()-(m_sz_date.length()+1)));*/
				m_sz_date = getFormatedDate();
				setEventText(PAS.get_pas().get_userinfo().get_organization() + " " + m_sz_date);
				//setEventText(m_txt_event_name.getText());
			}
			else
				m_sz_date = getFormatedDate();
			m_txt_date_time.setText(m_sz_date);
			updatePreviewText();
		}
		else if(e.getSource().equals(m_btn_messagelib))
		{
			MessageLibDlg dlg = new MessageLibDlg(this, PAS.get_pas(), false, false);
			dlg.setResizable(false);
		}
		else if(MessageLibDlg.ACT_MESSAGE_SELECTED.equals(e.getActionCommand()))
		{
			UBBMESSAGE msg = (UBBMESSAGE)e.getSource();
			String s = msg.getSzMessage();
			s = TextFormat.RegExpGsm(s).resultstr;
			m_txt_message.setText(s);
			updatePreviewText();
		}
		if(e.getActionCommand().equals("act_send")) {
			// Check if summary is active, create send object, display send object preview
			trainingModeChanged();
			if(PAS.TRAINING_MODE)
			{
				//m_btn_send.setEnabled(false);
				//return;
			}
			
			CBMESSAGE msg = new CBMESSAGE();
			msg.setSzText(m_txt_preview.getText());
			msg.setLChannel(1); // This is stored in userinfo
			CBMESSAGELIST msglist = new CBMESSAGELIST();
			msglist.getMessage().add(msg);
			
			CBRISK risk = new CBRISK();
			risk.setLPk(-1);
			risk.setSzName(m_cbx_risk.getSelectedItem().toString());
			CBREACTION reaction = new CBREACTION();
			reaction.setLPk(-1);
			reaction.setSzName(m_cbx_reaction.getSelectedItem().toString());
			CBORIGINATOR originator = new CBORIGINATOR();
			originator.setLPk(-1);
			originator.setSzName(m_cbx_originator.getSelectedItem().toString());
			CBMESSAGECONFIRMATION msgconfirm = new CBMESSAGECONFIRMATION();
			msgconfirm.setLPk(-1);
			msgconfirm.setSzName(m_txt_authorization.getText());
			CBSENDBASE operation = null;
					
			
			
			ShapeStruct shape = Variables.getMapFrame().get_active_shape();
			
			if(shape.getClass().equals(PolygonStruct.class))
			{
				operation = new CBALERTPOLYGON();
				UPolygon polygon = new UPolygon();
				PolygonStruct ps = (PolygonStruct) Variables.getMapFrame().get_active_shape();
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
				((CBALERTPOLYGON)operation).setAlertpolygon(polygon);
				if(ps.isElliptical())
					operation.setMdvgroup(MDVSENDINGINFOGROUP.MAP_POLYGONAL_ELLIPSE);
				else
					operation.setMdvgroup(MDVSENDINGINFOGROUP.MAP_POLYGON);
			}
			else if(shape.getClass().equals(PLMNShape.class))
			{
				operation = new CBALERTPLMN();
				operation.setMdvgroup(MDVSENDINGINFOGROUP.MAP_CB_NATIONAL);
				((CBALERTPLMN)operation).setAlertplmn(new UPLMN());
			}
			
			//commons
			operation.setFSimulation(PAS.TRAINING_MODE);
			operation.setLSchedUtc(0);
			operation.setTextmessages(msglist);
			//operation.setSzSender(m_txt_sender_name.getText());
			operation.setLProjectpk(m_projectpk);
			operation.setSzProjectname(m_txt_event_name.getText());
			
			operation.setRisk(risk);
			operation.setReaction(reaction);
			operation.setOriginator(originator);
			operation.setMessageconfirmation(msgconfirm);
			operation.setLParentRefno(n_parent_refno);
			CBMESSAGEPART messagepart = new CBMESSAGEPART();
			messagepart.setLPk(-1);
			messagepart.setSzName(m_txt_message.getText());
			operation.setMessagepart(messagepart);
			if(m_dept_send_on_behalf_of!=null)
			{
				operation.setLDeptpk(m_dept_send_on_behalf_of.get_deptpk());
			}
			CBSENDER sender = new CBSENDER();
			sender.setLPk(-1);
			sender.setSzName(m_txt_sender_name.getText());
			operation.setSender(sender);

			try {
				current_mode = MODE.SHOWING_AUTHORIZATION;
				WSCentricSend send = new WSCentricSend(this, "act_somethingsomething", operation);
				send.start();
			}
			catch(Exception ex) {
				JOptionPane.showMessageDialog(this, PAS.l("common_error" + ": ") + ex.getMessage());
				progress.stop_and_hide();
				m_btn_cancel.setEnabled(true);
				//m_btn_send.setEnabled(true);
				checkForEnableSendButton();
				current_mode = MODE.SHOWING_AUTHORIZATION;
			}
			
			//m_btn_send.setEnabled(false);
			checkForEnableSendButton();
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
			//lock the painting
			PAS.pasplugin.onLockSending(null, true);
			current_mode = MODE.SHOWING_SUMMARY;
			showSummary();
			checkForEnableSendButton();
			//Variables.NAVIGATION.setNavigation(Variables.MAPPANE.get_active_shape().calc_bounds());
			/*PAS.get_pas().actionPerformed(new ActionEvent(Variables.MAPPANE.get_active_shape().calc_bounds(),
										ActionEvent.ACTION_PERFORMED,
										"act_map_goto_area"));*/
			Variables.getNavigation().gotoMap(Variables.getMapFrame().get_active_shape().calc_bounds());
		}
		else if(e.getActionCommand().equals("act_goto_authorization")) {
			PAS.pasplugin.onLockSending(null, true);
			current_mode = MODE.SHOWING_AUTHORIZATION;
			showAuthorization();
			checkForEnableSendButton();
			//Variables.NAVIGATION.setNavigation(Variables.MAPPANE.get_active_shape().calc_bounds());
			/*PAS.get_pas().actionPerformed(new ActionEvent(Variables.MAPPANE.get_active_shape().calc_bounds(),
										ActionEvent.ACTION_PERFORMED,
										"act_map_goto_area"));*/
			//Variables.NAVIGATION.gotoMap(Variables.MAPPANE.get_active_shape().calc_bounds());
		}
		if(e.getActionCommand().equals("act_somethingsomething")){
			//JOptionPane.showMessageDialog(this, PAS.l("common_refno") + ": " + ((CBSENDINGRESPONSE)e.getSource()).getLRefno());
			progress.stop_and_hide();
			m_btn_cancel.setEnabled(true);
			//m_btn_send.setEnabled(true);
			checkForEnableSendButton();
			add_controls();
			current_mode = MODE.MESSAGE_WRITING;
			checkForEnableSendButton();
			onSendFinished(e);
		}
		else if(e.getActionCommand().equals("act_error")) {
			progress.stop_and_hide();
			m_btn_cancel.setEnabled(true);
			current_mode = MODE.SHOWING_SUMMARY;
			checkForEnableSendButton();
			//m_btn_send.setEnabled(true);
		}
		if(e.getSource().equals(m_btn_cancel)) {
			//lock the painting
			
			
			if(current_mode == MODE.SHOWING_SUMMARY) {
				PAS.pasplugin.onLockSending(null, false);
				current_mode = MODE.MESSAGE_WRITING;
				checkForEnableSendButton();
				add_controls();
			}
			else if(current_mode == MODE.SHOWING_AUTHORIZATION){
				current_mode = MODE.SHOWING_SUMMARY;
				checkForEnableSendButton();
				showSummary();
			}
			
			
		}
		if(e.getSource().equals(m_btn_reset)){
			if(!projectOpen())
				setEventText(Variables.getUserInfo().get_organization());
			//m_txt_sender_name.setText(Variables.USERINFO.get_organization());
			m_txt_sender_name.setText("NL-Alert");
			m_txt_message.setText("");
			//m_cbx_risk.setSelectedIndex(0);
			//m_cbx_reaction.setSelectedIndex(0);
			//m_cbx_originator.setSelectedIndex(0);
			//m_txt_preview.setText("");
			resetRRO();
			//updateCharacters();
			Variables.getMapFrame().set_active_shape(new PolygonStruct(Variables.getNavigation().getDimension()));
			Variables.getMapFrame().repaint();
			checkForEnableSendButton();
			current_mode = MODE.MESSAGE_WRITING;
			autoClickButton(m_btn_update);
			// complete default name
			if(m_txt_event_name.isEnabled())
				setEventText(m_txt_event_name.getText() + " " + getFormatedDate());
			updatePreviewText();
			updateCharacters();

		}
		else if("act_download_risk_reaction_originator_finished".equals(e.getActionCommand()))
		{
			CBMESSAGEFIELDS cbm = (CBMESSAGEFIELDS)e.getSource();
			List<CBRISK> risk = cbm.getRiskList().getCBRISK();
			List<CBREACTION> reaction = cbm.getReactionList().getCBREACTION();
			List<CBORIGINATOR> originator = cbm.getOriginatorList().getCBORIGINATOR();
			for(int i=0; i < risk.size(); i++)
				m_cbx_risk.addItem(risk.get(i));
			for(int i=0; i < reaction.size(); i++)
				m_cbx_reaction.addItem(reaction.get(i));
			for(int i=0; i < originator.size(); i++)
				m_cbx_originator.addItem(originator.get(i));
		}
		else if(e.getSource().equals(m_cbx_originator))
		{
			//System.out.println("idx=" + m_cbx_originator.getSelectedIndex() + " " + m_cbx_originator.getSelectedItem().toString());
			if(m_cbx_originator.getSelectedIndex()==-1)
				return;
			if(e.getActionCommand().equals("comboBoxEdited"))
			{
				
			}
			else if(e.getActionCommand().equals("comboBoxChanged"))
			{
				m_cbx_originator.updateEditor();				
			}
			checkForEnableSendButton();
			updatePreviewText(true);
		}
		else if(e.getSource().equals(m_cbx_reaction))
		{
			//System.out.println("idx=" + m_cbx_reaction.getSelectedIndex() + " " + m_cbx_reaction.getSelectedItem().toString());
			if(m_cbx_reaction.getSelectedIndex()==-1)
				return;
			if(e.getActionCommand().equals("comboBoxEdited"))
			{
				
			}
			else if(e.getActionCommand().equals("comboBoxChanged"))
			{
				m_cbx_reaction.updateEditor();				
			}
			checkForEnableSendButton();
			updatePreviewText(true);
		}
		else if(e.getSource().equals(m_cbx_risk))
		{
			//System.out.println("idx=" + m_cbx_risk.getSelectedIndex() + " " + m_cbx_risk.getSelectedItem().toString());
			if(e.getActionCommand().equals("comboBoxEdited"))
			{
				if(m_cbx_risk.getSelectedIndex()==-1)
				{
				}
				
			}
			else if(e.getActionCommand().equals("comboBoxChanged"))
			{
				m_cbx_risk.updateEditor();				
			}
			checkForEnableSendButton();
			updatePreviewText(true);
		}
		else if(e.getActionCommand().equals("act_print_summary")) {
			/*
			int preferred_resolution = 1920;
			int actual_screensize = Toolkit.getDefaultToolkit().getScreenSize().width;
			int width = 800;
			int height = 800;
			String timestamp = String.valueOf(Utils.get_current_datetime());
			
			// Scales to screen resolution
			double temp;
			temp = (double)preferred_resolution/actual_screensize;
			int lablefont = Math.round(((float)actual_screensize/preferred_resolution) * 5);
			int textboxfont = Math.round(((float)actual_screensize/preferred_resolution) * 16);
			width = (int)(((double)actual_screensize/preferred_resolution) * width);
			height = (int)(((double)actual_screensize/preferred_resolution) * height);
			JFrame printFrame = new JFrame();
			
			Container container = printFrame.getContentPane();
			printFrame.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
			
			JLabel message = new JLabel("<html><font size=" + lablefont + ">--- " + PAS.l("main_sending_message_summary") + " " + Variables.USERINFO.get_userid() + " - " + this.m_txt_event_name + " - " + timestamp + " ---</font></html>");
			message.setOpaque(false);
			message.setBackground(new Color(Color.TRANSLUCENT));
			message.setAlignmentX(LEFT_ALIGNMENT);
			printFrame.add(message, container);
			// Space
			JLabel space = new JLabel(" ");
			printFrame.add(space, container);
			// Image goes here
			JPanel pnl = new JPanel();
			pnl.setLayout(new BorderLayout());
			JLabel image = new JLabel();
			Image img;
			
			int actual_width = Variables.DRAW.get_buff_image().getWidth(this);
			int actual_height = Variables.DRAW.get_buff_image().getHeight(this);
			
			if(actual_height < actual_width) {
				if(actual_width<width) {
					width = actual_width;
					height = actual_height;
					img = Variables.DRAW.get_buff_image();
				}
				else {
					double percent_of_actual = ((double)actual_width/(double)width);
					height = (int)((double)actual_height / percent_of_actual);
					img = Variables.DRAW.get_buff_image().getScaledInstance(width, height, Image.SCALE_SMOOTH);
					
				}
			}
			else {
				if(actual_height<height) {
					width = actual_width;
					height = actual_height;
					img = Variables.DRAW.get_buff_image();
				}
				else {
					double percent_of_actual = ((double)actual_height/(double)height);
					width = (int)((double)actual_width / percent_of_actual);
					img = Variables.DRAW.get_buff_image().getScaledInstance(width, height, Image.SCALE_SMOOTH);
					
				}
			}
			*/
			String timestamp = String.valueOf(Utils.get_current_datetime());
			String m_headerfooter = "--- " + PAS.l("main_sending_message_summary") + " " + Variables.getUserInfo().get_userid() + " - " + this.m_txt_event_name + " - " + timestamp + " ---";
			String m_message = m_txt_preview.getText();
			//String m_characters = PAS.l("common_pages") + ": " + m_pages + " - " + Character.toUpperCase(PAS.l("common_characters").charAt(0)) + PAS.l("common_characters").substring(1) + ": " + m_txt_preview.getText().length();
			//String m_characters = PAS.l("common_characters") + " " + PAS.l("common_remaining") + " " + chars_left + "/" + MAX_MESSAGELENGTH_PR_PAGE;
			String m_characters = m_lbl_pages.getText() + " - " + m_lbl_characters.getText();
			/*
			Image img;
			image.setIcon(new ImageIcon(img));
			pnl.add(image,BorderLayout.PAGE_START);
			JTextArea txt = new JTextArea(10,60);
			txt.setWrapStyleWord(true);
			txt.setLineWrap(true);
			txt.setOpaque(false);
			txt.setFocusable(false);
			txt.getCaret().setVisible(false);
			txt.setBackground(new Color(Color.TRANSLUCENT));
			Font font = new Font(message.getFont().getName(), Font.PLAIN, textboxfont);
			txt.setFont(font);
			txt.setText(PAS.l("common_message_content") + ":\n" + m_txt_preview.getText());
			pnl.add(txt, BorderLayout.LINE_START);
			pnl.add(new JLabel("<html><font size=" + lablefont + ">" + PAS.l("common_pages") + ": " + m_pages + " - " + Character.toUpperCase(PAS.l("common_characters").charAt(0)) + PAS.l("common_characters").substring(1) + ": " + m_txt_preview.getText().length() + "</font></html>"), BorderLayout.PAGE_END);
			pnl.setBackground(new Color(Color.TRANSLUCENT));
			pnl.setOpaque(false);
			pnl.setAlignmentX(LEFT_ALIGNMENT);
			printFrame.add(pnl, container);
			message = new JLabel("<html><font size=" + lablefont + ">--- " + PAS.l("main_sending_message_summary") + " " + Variables.USERINFO.get_userid() + " - " + this.m_txt_event_name + " - " + timestamp + " ---</font></html>");
			message.setAlignmentX(LEFT_ALIGNMENT);
			message.setOpaque(false);
			message.setBackground(new Color(Color.TRANSLUCENT));
			printFrame.add(message, container);
			printFrame.pack();
			container.setBackground(Color.WHITE);
			printFrame.setSize(printFrame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height - (int)(Toolkit.getDefaultToolkit().getScreenSize().height*0.05));
			printFrame.setVisible(true);
			BufferedImage imidj = new BufferedImage(printFrame.getHeight(), printFrame.getWidth(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = imidj.createGraphics();
			printFrame.printAll(g);*/
			
			CentricPrintCtrl pctrl = new CentricPrintCtrl(Variables.getDraw().get_buff_image(),m_headerfooter, m_message, m_characters, m_headerfooter);
			pctrl.doPrint();
			//pctrl.print(g);
			
		}

	}
	
	private String getFormatedDate() {
		c = new GregorianCalendar();
		return String.format("%1$td-%1$tm-%1$tY %1$tH:%1$tM", c);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		/*
		if(arg0.getSource().equals(m_txt_event_name)) {
			if(!projectOpen())
			{
				if(m_txt_event_name.getText().endsWith(" " + m_sz_date))
					setEventText(m_txt_event_name.getText().substring(0,m_txt_event_name.getText().length()-(m_sz_date.length()+1)));				
			}
		}*/
		checkForEnableSendButton();
		
		//updateCharacters();
		updatePreviewText();
			
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		/*
		if(arg0.getSource().equals(m_txt_event_name)) {
			if(!projectOpen())
			{
				setEventText(m_txt_event_name.getText() + " " + m_sz_date);
			}
		}*/
		//checkInputs();
		checkForEnableSendButton();
		//updateCharacters();
		updatePreviewText();
	}
	
	protected void updatePreviewText()
	{
		updatePreviewText(false);
	}
	
	protected void updatePreviewText(boolean bRROChanged)
	{
		String sz_totalmessage = "";
		int fieldlengths = 0;
		
		String risk = m_cbx_risk.getEditor().getItem().toString();
		String reaction = m_cbx_reaction.getEditor().getItem().toString();
		String originator = m_cbx_originator.getEditor().getItem().toString();
		if(m_txt_sender_name.getText().length()>0)
			sz_totalmessage += m_txt_sender_name.getText() + " ";
		sz_totalmessage += m_txt_date_time.getText() + "\n";
		fieldlengths += TextFormat.GsmStrLen(sz_totalmessage);//sz_totalmessage.length();
		sz_totalmessage += m_txt_message.getText();
		String szPad = "";
		if(risk.length()>0)
		{
			szPad = "\n" + risk;
			sz_totalmessage += szPad;
			fieldlengths += TextFormat.GsmStrLen(szPad);//szPad.length();
		}
		if(reaction.length()>0)
		{ 
			szPad = "\n" + reaction;
			sz_totalmessage += szPad;
			fieldlengths += TextFormat.GsmStrLen(szPad);//szPad.length();
		}
		if(originator.length()>0)
		{
			szPad = "\n" + originator;
			sz_totalmessage += szPad;
			fieldlengths += TextFormat.GsmStrLen(szPad);//szPad.length();
		}
		int total_len = TextFormat.GsmStrLen(sz_totalmessage);
		if(total_len>MAX_TOTAL_CHARS) //sz_totalmessage.length()
		{
			//sz_totalmessage = sz_totalmessage.substring(0, MAX_TOTAL_CHARS);
			sz_totalmessage = TextFormat.GsmStrMaxLen(sz_totalmessage, MAX_TOTAL_CHARS);
			
		}
		m_txt_preview.setText(sz_totalmessage);
		updateCharacters();
	}
		
	protected int getTextLengthOfFields()
	{
		/*int total = m_txt_sender_name.getText().length() + 
				m_txt_date_time.getText().length() + 
				m_cbx_risk.getEditor().getItem().toString().length() + 
				m_cbx_reaction.getEditor().getItem().toString().length() +
				m_cbx_originator.getEditor().getItem().toString().length();*/
		
		int total = TextFormat.GsmStrLen(m_txt_sender_name.getText()) +
						TextFormat.GsmStrLen(m_txt_date_time.getText()) +
						TextFormat.GsmStrLen(m_cbx_risk.getEditor().getItem().toString()) +
						TextFormat.GsmStrLen(m_cbx_reaction.getEditor().getItem().toString()) +
						TextFormat.GsmStrLen(m_cbx_originator.getEditor().getItem().toString());
		
		//add autogen spaces and linebreak
		String risk = m_cbx_risk.getEditor().getItem().toString();
		String reaction = m_cbx_reaction.getEditor().getItem().toString();
		String originator = m_cbx_originator.getEditor().getItem().toString();

		if(risk.length()>0)
			total++;
		if(reaction.length()>0)
			total++;
		if(originator.length()>0)
			total++;
		if(m_txt_sender_name.getText().length()>0)
			total++;
		return total;
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
		//int MAX_EVENTNAME_LENGTH = 20;
		if(e.getSource().equals(m_txt_event_name)) {
			/*if(!projectOpen())
			{
				if(e.getKeyCode()==e.VK_DELETE || e.getKeyCode()==e.VK_BACK_SPACE || (m_txt_event_name.getSelectedText() != null && m_txt_event_name.getSelectedText().length()>0))
					return;
				if(m_txt_event_name.getText().length() + m_sz_date.length() + 2 > MAX_EVENTNAME_LENGTH)
					setEventText(m_txt_event_name.getText().substring(0,Math.max(0, MAX_EVENTNAME_LENGTH - m_sz_date.length() - 2)));
			}*/
		}
		else if(e.getSource().equals(m_txt_message))
		{
		}
		else if(e.getSource().equals(m_txt_sender_name))
		{
			
		}

	}
	
	protected boolean checkIfInputAllowed(KeyEvent e, int num_chars_selected)
	{
		if(e.getSource().equals(m_txt_message) || 
				e.getSource().equals(m_txt_sender_name) || 
				e.getSource().equals(m_cbx_risk.getEditor().getEditorComponent()) ||
				e.getSource().equals(m_cbx_reaction.getEditor().getEditorComponent()) ||
				e.getSource().equals(m_cbx_originator.getEditor().getEditorComponent())) {
//			String temp = m_txt_message.getText();
			String ch = Character.toString(e.getKeyChar());
			int len_ch = TextFormat.GsmStrLen(ch);

			//RegExpResult res = RegExpGsm(temp);
			//if(!res.valid)
			//	m_txt_message.setText(res.resultstr);

			updatePreviewText();
			int n_total_len = TextFormat.GsmStrLen(m_txt_preview.getText());//m_txt_preview.getText().length();
			if(e.getSource().equals(m_txt_message))// || e.getSource().equals(m_txt_sender_name))
			{
				JTextComponent c = (JTextComponent)e.getSource();
				//int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected;
//				int len_of_message = TextFormat.GsmStrLen(c.getText()); //c.getText().length();
				//if(len_of_message<n_chars_left_for_message)
				{
					/*if(c.getText().length() >= n_chars_left_for_message-1) {
						c.setText(c.getText().substring(0, n_chars_left_for_message-1));
						return false;
					}*/
					int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected - TextFormat.GsmStrLen(m_txt_message.getText());
					
					//if(TextFormat.GsmStrLen(c.getText()) >= n_chars_left_for_message-len_ch) {
					if(n_chars_left_for_message<=len_ch)
					{
						//c.setText(c.getText().substring(0, n_chars_left_for_message-1));
						//c.setText(TextFormat.GsmStrMaxLen(c.getText(), n_chars_left_for_message-len_ch));
						//c.setText(temp);
						e.consume();
						return false;						
					}
					return true;
				}
			}
			else if(e.getSource().equals(m_txt_sender_name))
			{
				if(n_total_len>=MAX_TOTAL_CHARS + num_chars_selected - len_ch)
				{
					int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected - TextFormat.GsmStrLen(m_txt_message.getText());
					//c.setText(c.getText().substring(0, n_chars_left_for_message-1));
					if(n_chars_left_for_message<=len_ch)
					{
						e.consume();
						return false;
					}
					return true;
				}				
			}
			else
			{
				//combobox
				/*if(n_total_len>=MAX_TOTAL_CHARS + num_chars_selected)
				{
					return false;
				}*/
				if(n_total_len>=MAX_TOTAL_CHARS + num_chars_selected - len_ch)
				{
					int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected - TextFormat.GsmStrLen(m_txt_message.getText());
					//c.setText(c.getText().substring(0, n_chars_left_for_message-1));
					if(n_chars_left_for_message<=len_ch)
					{
						e.consume();
						return false;
					}
					return true;
				}				
				
			}
		}
		else if(e.getSource().equals(m_txt_event_name))
		{
//			int n_total_len = m_txt_event_name.getText().length();
			int n_chars_left_for_eventname = MAX_EVENTNAME_LENGTH - m_txt_date_time.getText().length()-1 + num_chars_selected;
//			int len_of_event = m_txt_event_name.getText().length();
			if(m_txt_event_name.getText().length() >= n_chars_left_for_eventname-1) {
				return false;
			}
		}

		return true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		/*if(e.getSource().equals(m_txt_message) || 
				e.getSource().equals(m_txt_sender_name) || 
				e.getSource().equals(m_cbx_risk) ||
				e.getSource().equals(m_cbx_reaction) ||
				e.getSource().equals(m_cbx_originator)) {
			updatePreviewText();
			int n_total_len = m_txt_preview.getText().length();
			if(e.getSource().equals(m_txt_message))
			{
				int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields();
				int len_of_message = m_txt_message.getText().length();
				//if(len_of_message<n_chars_left_for_message)
				{
					if(m_txt_message.getText().length() >= n_chars_left_for_message) {
						m_txt_message.setText(m_txt_message.getText().substring(0, n_chars_left_for_message-1));
					}
				}
			}
			else if(e.getSource().equals(m_txt_sender_name))
			{
			}
		}*/
		if(e.getSource().equals(m_txt_event_name))
		{
			//setEventText(m_txt_event_name.getText());
		}
		else
		{
			updatePreviewText();
			checkForEnableSendButton();
		}

		//checkInputs();
	}
	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getSource().equals(m_txt_message) || 
				e.getSource().equals(m_txt_sender_name) || 
				e.getSource().equals(m_cbx_risk.getEditor().getEditorComponent()) ||
				e.getSource().equals(m_cbx_reaction.getEditor().getEditorComponent()) ||
				e.getSource().equals(m_cbx_originator.getEditor().getEditorComponent())) 
		{
			
			JTextComponent c = (JTextComponent)e.getSource();
			String strText = c.getText();
			TextFormat.RegExpResult res = TextFormat.RegExpGsm(strText + e.getKeyChar());
			if(!res.valid)
			{
				c.setText(res.resultstr);
				e.consume();
			}
			int before = TextFormat.GsmStrLen(m_txt_preview.getText()); //m_txt_preview.getText().length();
			int txt_before = TextFormat.GsmStrLen(m_txt_message.getText()); //m_txt_message.getText().length();
			int char_selection = TextFormat.GsmStrLen(c.getText().substring(c.getSelectionStart(), c.getSelectionEnd())); //c.getSelectionEnd()-c.getSelectionStart();
			//System.out.println("c="+char_selection);
			boolean b = checkIfInputAllowed(e, char_selection);
			int after = TextFormat.GsmStrLen(m_txt_preview.getText()); //m_txt_preview.getText().length();
			//int txt_after = TextFormat.GsmStrLen(c.getText());//c.getText().length();
			int txt_after = TextFormat.GsmStrLen(m_txt_message.getText()) + getTextLengthOfFields() + 1;
//			int number_of_chars = after-before;
			if(!b)
			{
				e.consume();
				//return;
			}
			{
				//remove chars if > max
				if(txt_after>MAX_TOTAL_CHARS)
				{
					try
					{
						int diff = txt_after-(MAX_TOTAL_CHARS);
//						int current_len = c.getText().length();
						String cut = TextFormat.GsmStrMaxLen(c.getText(), txt_before-diff);
						c.setText(cut);
						//c.setText(c.getText().substring(0, current_len-diff));
					}
					catch(Exception err)
					{
						err.printStackTrace();
					}
				}
			}
			//System.out.println("Before="+before+" After="+after);
		}
		else if(e.getSource().equals(m_txt_event_name)) {
			if(!projectOpen())
			{
				/*if(e.getKeyCode()==e.VK_DELETE || e.getKeyCode()==e.VK_BACK_SPACE || (m_txt_event_name.getSelectedText() != null && m_txt_event_name.getSelectedText().length()>0))
					return;
				if(m_txt_event_name.getText().length() + m_sz_date.length() + 2 > MAX_EVENTNAME_LENGTH)
					setEventText(m_txt_event_name.getText().substring(0,Math.max(0, MAX_EVENTNAME_LENGTH - m_sz_date.length() - 2)));*/
				JTextComponent c = (JTextComponent)e.getSource();
				int before = c.getText().length();
				int char_selection = c.getSelectionEnd()-c.getSelectionStart();
				//System.out.println("c="+char_selection);
				boolean b = checkIfInputAllowed(e, char_selection);
				int after = c.getText().length() + m_txt_date_time.getText().length() + 1;
//				int number_of_chars = after-before;
				if(!b)
					e.consume();
				if(after>=MAX_EVENTNAME_LENGTH)
				{
					try
					{
						int diff = after-MAX_EVENTNAME_LENGTH;
//						int current_len = c.getText().length();
						setEventText(c.getText().substring(0, MAX_EVENTNAME_LENGTH - m_txt_date_time.getText().length() - 1));//current_len-diff));
					}
					catch(Exception err)
					{
						
					}
				}
			}
		}
		else if(e.getSource().equals(m_txt_authorization)) {
			if((m_txt_authorization.getText().length() + 1) > MAX_TOTAL_AUTHORIZATION_CHARS)
				e.consume();
		}

	}
	public void onSendFinished(ActionEvent e) {
		// Start update timer
		CBSENDINGRESPONSE response = (CBSENDINGRESPONSE)e.getSource();
		//set_projectpk(response.getLProjectpk(), "");
		//if(m_centricstatuscontroller == null)
		//	m_centricstatuscontroller = new CentricStatusController(e, this);
		//else {
		{
			getStatusController().set_cbsendingresponse(response, true);
			//getStatusController().openStatus((CBSENDINGRESPONSE)e.getSource(), this);
			Project p = new Project();
			p.set_projectpk(Long.toString(response.getLProjectpk()));
			PAS.pasplugin.onOpenProject(p, response.getLRefno());
			((CentricEastContent)PAS.get_pas().get_eastcontent()).flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
			//((CentricEastContent)PAS.get_pas().get_eastcontent()).flip_to(CentricEastContent.PANEL_CENTRICSEND_);
			Variables.getMapFrame().set_active_shape(null);
			Variables.getMapFrame().set_mode(MapFrame.MAP_MODE_PAN);
			autoClickButton(m_btn_reset);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
		//checkInputs();
		checkForEnableSendButton();
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		//checkInputs();
		checkForEnableSendButton();
	}
	private boolean checkInputs() {
		boolean enable_send = true;
		if(m_txt_event_name.getText().length()<1)
			enable_send = false;
		//if(m_txt_sender_name.getText().length()<1)
		//	enable_send = false;
		if(m_txt_message.getText().length()<1)
			enable_send = false;
		//if(Variables.MAPPANE.get_active_shape()==null)
		//	enable_send = false;
		//if(m_cbx_originator.getSelectedItem().toString().length()<=0)
		//	enable_send = false;
		//if(m_cbx_risk.getSelectedItem().toString().length()<=0)
		//	enable_send = false;
		//if(m_cbx_reaction.getSelectedItem().toString().length()<=0)
		//	enable_send = false;
		//else
		{
			if(Variables.getMapFrame().get_active_shape()!=null &&
					!Variables.getMapFrame().get_active_shape().can_lock(Variables.getUserInfo().get_departments().get_combined_restriction_shape()))
				enable_send = false;
		}
		return enable_send;
	}
	
	private void updateCharacters() {
		//int chars = m_txt_preview.getText().length();
		String str = m_txt_preview.getText();
		
		int chars = TextFormat.GsmStrLen(str);
		int pageno = (int)(chars / (MAX_MESSAGELENGTH_PR_PAGE*1.0001)) + 1;
		int chars_left = (pageno)*MAX_MESSAGELENGTH_PR_PAGE - chars;//((chars / pageno));
		int total_chars_left = (MAX_PAGES*MAX_MESSAGELENGTH_PR_PAGE - chars);
		int chars_risk = TextFormat.GsmStrLen(m_cbx_risk.getEditor().getItem().toString());
		int chars_reaction = TextFormat.GsmStrLen(m_cbx_reaction.getEditor().getItem().toString());
		int chars_originator = TextFormat.GsmStrLen(m_cbx_originator.getEditor().getItem().toString());
		m_cbx_risk.setCharsAvailable(total_chars_left + chars_risk); //m_cbx_risk.getEditor().getItem().toString().length());
		m_cbx_reaction.setCharsAvailable(total_chars_left + chars_reaction); //m_cbx_reaction.getEditor().getItem().toString().length());
		m_cbx_originator.setCharsAvailable(total_chars_left + chars_originator); //m_cbx_originator.getEditor().getItem().toString().length());
		/*switch(current_mode)
		{
		case INITIALIZING:
		case MESSAGE_WRITING:
			m_lbl_characters.setText(PAS.l("common_characters") + " " + chars + "/" + MAX_TOTAL_CHARS);
			m_lbl_pages.setText(PAS.l("common_page") + " " + pageno + " (" + MAX_PAGES + ")");
			break;
		case SENDING:
		case SHOWING_SUMMARY:
			//int chars_left = ((chars / MAX_PAGES) % MAX_MESSAGELENGTH_PR_PAGE);
			int chars_left = (pageno)*MAX_MESSAGELENGTH_PR_PAGE - chars;//((chars / pageno));
			
			m_lbl_characters.setText(pageno + " " + PAS.l("common_pages") + " (" + chars_left + " " + PAS.l("common_characters") + " " + PAS.l("common_remaining") + ")");
			break;
		}*/
        m_lbl_pages.setText(Localization.l("common_page") + " " + pageno + "/" + MAX_PAGES);
        m_lbl_characters.setText(Localization.l("common_characters") + " " + Localization.l("common_remaining") + " " + chars_left + "/" + MAX_MESSAGELENGTH_PR_PAGE);
	}
	
	public void fromTemplate(CBSTATUS cb)
	{
		m_txt_event_name.setEnabled(false);
		if(current_mode==MODE.SHOWING_SUMMARY)
			autoClickButton(m_btn_cancel);
		String sz_msgpart = "";
		String sz_originator = "";
		String sz_risk = "";
		String sz_reaction = "";
		String sz_sender = "";
		
		if(cb.getMessagepart()!=null)
			sz_msgpart = cb.getMessagepart().getSzName();
		if(cb.getOriginator()!=null)
			sz_originator = cb.getOriginator().getSzName();
		if(cb.getRisk()!=null)
			sz_risk = cb.getRisk().getSzName();
		if(cb.getReaction()!=null)
			sz_reaction = cb.getReaction().getSzName();
		if(cb.getSender()!=null)
			sz_sender = cb.getSender().getSzName();

		m_txt_message.setText(sz_msgpart);
		m_cbx_originator.setSelectedItem(sz_originator);		
		m_cbx_risk.setSelectedItem(sz_risk);
		m_cbx_reaction.setSelectedItem(sz_reaction);
		m_txt_sender_name.setText(sz_sender);
		setParentRefno(cb.getLRefno());

		checkForEnableSendButton();
		int l_group = (int)cb.getMdv().getLGroup();
		//MDVSENDINGINFOGROUP group = Enum.valueOf(MDVSENDINGINFOGROUP.class, new Long(l_group).toString());
		if(cb.getShape()!=null)
		{
			boolean b_poly_is_elliptical = false;
			switch(l_group)
			{
			case 32:
				b_poly_is_elliptical = true;
				break;
			}
			ShapeStruct shape = UShapeToShape.ConvertUShape_to_ShapeStruct(cb.getShape());
			shape.enableSendColor();
			Variables.getMapFrame().set_active_shape(shape);
			Variables.getMapFrame().setPaintModeBasedOnActiveShape(b_poly_is_elliptical);
		}
		autoClickButton(m_btn_update);
		updatePreviewText();

		
	}
	

}
