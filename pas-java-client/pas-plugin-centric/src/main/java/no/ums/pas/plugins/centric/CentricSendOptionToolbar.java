package no.ums.pas.plugins.centric;

import no.ums.log.Log;
import no.ums.log.UmsLog;
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
import no.ums.pas.plugins.centric.ws.WSLBAParameters;
import no.ums.pas.send.messagelibrary.MessageLibDlg;
import no.ums.pas.ums.tools.*;
import no.ums.ws.common.MDVSENDINGINFOGROUP;
import no.ums.ws.common.UBBMESSAGE;
import no.ums.ws.common.ULBAPARAMETER;
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

    private static final Log log = UmsLog.getLogger(CentricSendOptionToolbar.class);

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
    public String getEventName() { return m_txt_event_name.getText(); }
	private StdTextLabel m_lbl_sender_name;
	private StdTextLabel m_lbl_date_time;
	private StdTextLabel m_lbl_message;

	private StdTextLabel m_lbl_risk;
	private StdTextLabel m_lbl_reaction;
	private StdTextLabel m_lbl_originator;
	private StdTextLabel m_lbl_pages;
	private StdTextLabel m_lbl_characters;
	private StdTextLabel m_lbl_warning;
	private JLabel m_lbl_shape_error_text;
		
	private StdTextArea m_txt_event_name;
	private String m_sz_date;
	private StdTextArea m_txt_sender_name;
	private StdTextArea m_txt_date_time;
	private StdTextAreaNoTab m_txt_message;
	private JScrollPane m_txt_messagescroll;
	private JTextArea m_txt_authorization;
	private JScrollPane m_txt_authorizationscroll;

	private StdTextLabel m_lbl_preview;
	private JTextArea m_txt_preview;
	private JScrollPane m_txt_previewscroll;
	
	private RROComboBox m_cbx_risk;
	private RROComboBox m_cbx_reaction;
	private RROComboBox m_cbx_originator;
	private JComboBox m_cbx_time_hour;
	private JComboBox m_cbx_time_minute;
	
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

    protected MODE current_mode = MODE.MESSAGE_WRITING;
	
	LoadingFrame progress = new LoadingFrame(Localization.l("main_statustext_lba_sending"), null);

    {
        progress = new LoadingFrame(Localization.l("main_statustext_lba_sending"), null);
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
	
	public void setShapeErrorText(String s)
	{
		m_lbl_shape_error_text.setText(s);
	}
	
	private DeptInfo m_dept_send_on_behalf_of = null;
	protected void setDeptSendOnBehalfOf(DeptInfo d)
	{
		m_dept_send_on_behalf_of = d;
		log.debug("Sending owned by " + d.get_deptid());
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
            for (Object aDa : da) {
                DeptInfo di = (DeptInfo) aDa;
                if (di.get_restriction_shapes().size() <= 0)
                    continue;
                ShapeStruct s = di.get_restriction_shapes().get(0);
                if (!s.getClass().equals(PolygonStruct.class))
                    continue;
                if (!mappoly.isElliptical()) {
                    MapPointLL firstpoint = mappoly.getFirstPoint();
                    if (s.pointInsideShape(firstpoint)) {
                        //success
                        setDeptSendOnBehalfOf(di);
                        break;
                    }
                } else {
                    MapPointLL ll = mappoly.getEllipseCenter().get_mappointll();
                    if (s.pointInsideShape(ll)) {
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

	public CentricStatusController getStatusController() { return (CentricStatusController)PAS.get_pas().get_statuscontroller(); }

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
			setEventText(sz_projectname);
		}
		m_projectpk = projectpk;
	}
	
	protected void setEventText(String s)
	{
		m_txt_event_name.setText(s);
		m_txt_event_name.setToolTipText(s);
	}
	
	public CentricSendOptionToolbar() {
		//super();
		current_mode = MODE.INITIALIZING;


        WSLBAParameters lbap = new WSLBAParameters(this, "act_download_lba_parameters_finished");
        try
        {
            lbap.runNonThreaded();
        }
        catch(Exception e)
        {
            log.warn(e.getMessage(), e);
        }
        init();

		WSCentricRRO rro = new WSCentricRRO(this, "act_download_risk_reaction_originator_finished");
		try
		{
			rro.runNonThreaded();
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}

	}
	public void doInit() {
		init();
	}

    private static int input_width = 260;

    public void init() {
        int lbl_width = 120;
        m_lbl_event_name = new StdTextLabel(Localization.l("main_sending_event_name") + ":",new Dimension(lbl_width,20));
        m_lbl_sender_name = new StdTextLabel(Localization.l("main_sending_lba_sender_text") + ":", new Dimension(lbl_width,20));
        m_lbl_date_time = new StdTextLabel(Localization.l("common_date") + " - " + Localization.l("common_time") + ":", new Dimension(lbl_width,20));
        m_lbl_message = new StdTextLabel(Localization.l("common_message_content") + ":",new Dimension(lbl_width,20));

        m_lbl_risk = new StdTextLabel(Localization.l("common_risk") + ":",new Dimension(lbl_width,20));
        m_lbl_reaction = new StdTextLabel(Localization.l("common_reaction") + ":",new Dimension(lbl_width,20));
        m_lbl_originator = new StdTextLabel(Localization.l("common_originator") + ":",new Dimension(lbl_width,20));
				
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

        m_btn_update = new JButton(Localization.l("common_update"));
        int btn_width = 110;
        int btn_height = 23;
        m_btn_update.setPreferredSize(new Dimension(btn_width, btn_height));
		m_btn_update.addActionListener(this);
		
		m_txt_message = new StdTextAreaNoTab(this, "",1,1,"[^A-Za-z0-9]");
		m_txt_message.setWrapStyleWord(true);
		m_txt_message.setLineWrap(true);
		m_txt_message.addFocusListener(this);
		m_txt_message.addKeyListener(this);
		
		m_txt_messagescroll = new JScrollPane(m_txt_message);
		m_txt_messagescroll.setPreferredSize(new Dimension(input_width,100));
		
		ImageIcon ico = ImageLoader.load_icon("messagelibrary.png");
		m_btn_messagelib = new JButton(ico);
        m_btn_messagelib.setToolTipText(Localization.l("main_sending_audio_type_library"));
		m_btn_messagelib.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
		m_btn_messagelib.addActionListener(this);

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
		
		//Preview ting
        m_lbl_preview = new StdTextLabel(Localization.l("common_preview"),new Dimension(150,20));
		m_txt_preview = new JTextArea("",1,1);
		m_txt_preview.setWrapStyleWord(true);
		m_txt_preview.setLineWrap(true);
		m_txt_preview.setEnabled(false);
		
		m_txt_previewscroll = new JScrollPane(m_txt_preview);

        m_lbl_pages = new StdTextLabel(Localization.l("common_pages") + " 1/" + MAX_PAGES, 100);
        m_lbl_characters = new StdTextLabel(Localization.l("common_characters") + " 0/" + MAX_TOTAL_CHARS, 200);

        m_btn_send = new JButton(Localization.l("main_sending_send"));
		m_btn_send.setPreferredSize(new Dimension(input_width,30));
		m_btn_address_book = new JButton("image"); // Add image/icon


        m_btn_save_message = new JButton(Localization.l("main_sending_save_message_template"));
        m_btn_reset = new JButton(Localization.l("common_reset"));
		m_btn_reset.addActionListener(this);
		m_btn_reset.addFocusListener(this);
		m_btn_send.addActionListener(this);
		m_btn_send.addFocusListener(this);
	    m_btn_reset.setPreferredSize(new Dimension(btn_width, btn_height));
		m_btn_save_message.setPreferredSize(new Dimension(input_width/2, btn_height));
        m_btn_send_for_auth = new JButton(Localization.l("main_send_to_address_book_for_authorization"));
		m_btn_send_for_auth.addActionListener(this);
		m_btn_send_for_auth.setEnabled(false);

        m_txt_warning = new JTextArea(Localization.l("main_sending_send_warning"),1,1);
		m_txt_warning.setWrapStyleWord(true);
		m_txt_warning.setLineWrap(true);
		m_txt_warningscroll = new JScrollPane(m_txt_warning);
		m_txt_warningscroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add_controls();
		m_txt_event_name.setEditable(true);
		m_txt_message.setEditable(true);
		m_txt_sender_name.setEditable(true);
		m_txt_date_time.setEditable(true);
		
		Variables.getMapFrame().addMouseListener(this);
		Variables.getMapFrame().addKeyListener(this);
		setVisible(true);
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
			if(PAS.TRAINING_MODE) {
                m_btn_send.setToolTipText(Localization.l("mainmenu_trainingmode"));
           }
			else
				m_btn_send.setToolTipText(null);
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

        set_gridconst(1, inc_panels(), 2, 1);
		add(m_btn_send, m_gridconst);
		m_btn_send.setActionCommand("act_goto_summary");
		m_btn_send.setPreferredSize(new Dimension(input_width,30));

		m_lbl_shape_error_text = new JLabel("");
		set_gridconst(0, inc_panels(), 8, 1, GridBagConstraints.WEST);
		add(m_lbl_shape_error_text, m_gridconst);

		add_spacing(DIR_VERTICAL, 50);


        set_gridconst(1, inc_panels(), 1, 1);
        add(m_btn_reset, m_gridconst);

        set_gridconst(2, get_panel(), 1, 1);
        add(m_btn_save_message, m_gridconst);

        m_btn_save_message.setPreferredSize(new Dimension(m_btn_send.getPreferredSize().width/2, m_btn_reset.getPreferredSize().height));
        m_btn_reset.setPreferredSize(new Dimension(m_btn_send.getPreferredSize().width/2, m_btn_reset.getPreferredSize().height));
		m_txt_previewscroll.setPreferredSize(new Dimension(input_width,100));
		m_txt_event_name.setPreferredSize(new Dimension(input_width, m_lbl_preview.getPreferredSize().height));

		updateCharacters();
		revalidate();
		repaint();
	}
	
	private void showSummary() {
		
		findDeptSendOnBehalfOf();
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
            m_btn_print = new JButton(Localization.l("common_print_summary"));
			m_btn_print.setActionCommand("act_print_summary");
			m_btn_print.addActionListener(this);
		}
		add(m_btn_print, m_gridconst);
		
		set_gridconst(1, inc_panels(), 1, 1);
		add(m_lbl_pages, m_gridconst);
		set_gridconst(2, get_panel(), 7, 1);
		add(m_lbl_characters, m_gridconst);
		
		
		add_spacing(DIR_VERTICAL, 20);
		
		
		m_txt_warning.setEnabled(false);
		Font f = UIManager.getFont("SendingWarningText.font");
		m_txt_warning.setFont(f);
		Color c = UIManager.getColor("SendingWarningText.foreground");
		m_txt_warning.setForeground(Color.RED);
		m_txt_warning.setDisabledTextColor(Color.RED);
        m_txt_warning.setText(Localization.l("main_sending_send_warning"));
		add_spacing(DIR_VERTICAL, 5);
		
		set_gridconst(0, inc_panels(), 8, 1);
        progress.set_totalitems(0, Localization.l("main_statustext_lba_sending"));
		progress.stop_and_hide();
		add(progress.get_progress(), m_gridconst);
		
		add_spacing(DIR_VERTICAL, 5);
		
		if(m_btn_cancel == null) {
            m_btn_cancel = new JButton(Localization.l("common_cancel"));
			m_btn_cancel.addActionListener(this);
		}

        set_gridconst(0, inc_panels(), 3, 1);
        add(m_txt_warningscroll, m_gridconst);

        set_gridconst(0, inc_panels(), 1, 1);
        add(m_btn_cancel, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(m_btn_send, m_gridconst);

		
		
		m_btn_send.setActionCommand("act_send");
		int width_left = 550-100;
		progress.get_progress().setPreferredSize(new Dimension(width_left,25));
		m_txt_previewscroll.setPreferredSize(new Dimension(width_left-m_lbl_preview.getPreferredSize().width,250));
		m_txt_warningscroll.setPreferredSize(new Dimension(width_left,70));
		m_txt_event_name.setPreferredSize(new Dimension(width_left-m_lbl_preview.getPreferredSize().width, m_lbl_preview.getPreferredSize().height));

		m_lbl_characters.setPreferredSize(new Dimension(200, m_lbl_characters.getPreferredSize().height));
		m_btn_send.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width, m_btn_send.getPreferredSize().height));
		m_btn_cancel.setPreferredSize(new Dimension(m_txt_event_name.getPreferredSize().width/2, m_btn_send.getPreferredSize().height));
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
				m_sz_date = getFormatedDate();
				setEventText(PAS.get_pas().get_userinfo().get_organization() + " " + m_sz_date);
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
			msgconfirm.setSzName("");
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
				WSCentricSend send = new WSCentricSend(this, "act_exec_cb_operation", operation);
				send.start();
			}
			catch(Exception ex) {
                JOptionPane.showMessageDialog(this, Localization.l("common_error" + ": ") + ex.getMessage(), Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
				progress.stop_and_hide();
				m_btn_cancel.setEnabled(true);
				checkForEnableSendButton();
				current_mode = MODE.SHOWING_AUTHORIZATION;
			}
			
			checkForEnableSendButton();
			m_btn_cancel.setEnabled(false);
			
			try
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
                        progress.set_totalitems(0, Localization.l("main_statustext_lba_sending"));
						progress.get_progress().setVisible(true);
					}
				});
			}
			catch(Exception ex)
			{
				log.error(ex.getMessage(), ex);
			}
		}
		else if(e.getActionCommand().equals("act_goto_summary")) {
			//lock the painting
			PAS.pasplugin.onLockSending(null, true);
			current_mode = MODE.SHOWING_SUMMARY;
			showSummary();
			checkForEnableSendButton();
			Variables.getNavigation().gotoMap(Variables.getMapFrame().get_active_shape().calc_bounds());
		}
		if(e.getActionCommand().equals("act_exec_cb_operation")){
			progress.stop_and_hide();
			m_btn_cancel.setEnabled(true);
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

			m_txt_sender_name.setText("NL-Alert");
			m_txt_message.setText("");

			resetRRO();

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
            for (CBRISK aRisk : risk) m_cbx_risk.addItem(aRisk);
            for (CBREACTION aReaction : reaction) m_cbx_reaction.addItem(aReaction);
            for (CBORIGINATOR anOriginator : originator) m_cbx_originator.addItem(anOriginator);
		}
		else if(e.getSource().equals(m_cbx_originator))
		{
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
			if(e.getActionCommand().equals("comboBoxChanged"))
			{
				m_cbx_risk.updateEditor();				
			}
			checkForEnableSendButton();
			updatePreviewText(true);
		}
		else if(e.getActionCommand().equals("act_print_summary")) {

			String timestamp = String.valueOf(Utils.get_current_datetime());
            String m_headerfooter = "--- " + Localization.l("main_sending_message_summary") + " " + Variables.getUserInfo().get_userid() + " - " + this.m_txt_event_name + " - " + timestamp + " ---";
			String m_message = m_txt_preview.getText();
			String m_characters = m_lbl_pages.getText() + " - " + m_lbl_characters.getText();

			CentricPrintCtrl pctrl = new CentricPrintCtrl(Variables.getDraw().get_buff_image() ,m_headerfooter, m_message, m_characters, m_headerfooter);
			//CentricPrintCtrl pctrl = new CentricPrintCtrl(Variables.getDraw().get_buff_image(),m_headerfooter, m_message, m_characters, m_headerfooter);
			pctrl.doPrint();
		}
        if("act_download_lba_parameters_finished".equals(e.getActionCommand())) {
            ULBAPARAMETER lbap = (ULBAPARAMETER)e.getSource();
            MAX_MESSAGELENGTH_PR_PAGE = lbap.getLPagesize();
            MAX_PAGES = lbap.getLMaxpages();
            MAX_TOTAL_CHARS = MAX_MESSAGELENGTH_PR_PAGE * MAX_PAGES;
        }
	}
	
	private String getFormatedDate() {
        Calendar c = new GregorianCalendar();
		return String.format("%1$td-%1$tm-%1$tY %1$tH:%1$tM", c);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		checkForEnableSendButton();
		updatePreviewText();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		checkForEnableSendButton();
		updatePreviewText();
	}
	
	protected void updatePreviewText()
	{
		updatePreviewText(false);
	}
	
	protected void updatePreviewText(boolean bRROChanged)
	{
		String sz_totalmessage = "";

        String risk = m_cbx_risk.getEditor().getItem().toString();
		String reaction = m_cbx_reaction.getEditor().getItem().toString();
		String originator = m_cbx_originator.getEditor().getItem().toString();
		if(m_txt_sender_name.getText().length()>0)
			sz_totalmessage += m_txt_sender_name.getText() + " ";
		sz_totalmessage += m_txt_date_time.getText() + "\n";
        sz_totalmessage += m_txt_message.getText();
		String szPad;
		if(risk.length()>0)
		{
			szPad = "\n" + risk;
			sz_totalmessage += szPad;
        }
		if(reaction.length()>0)
		{ 
			szPad = "\n" + reaction;
			sz_totalmessage += szPad;
        }
		if(originator.length()>0)
		{
			szPad = "\n" + originator;
			sz_totalmessage += szPad;
        }
		int total_len = TextFormat.GsmStrLen(sz_totalmessage);
		if(total_len>MAX_TOTAL_CHARS) //sz_totalmessage.length()
		{
			sz_totalmessage = TextFormat.GsmStrMaxLen(sz_totalmessage, MAX_TOTAL_CHARS);
			
		}
		m_txt_preview.setText(sz_totalmessage);
		updateCharacters();
	}
		
	protected int getTextLengthOfFields()
	{
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
		if(w<=1 || h<=1) {
        }

	}
	@Override
	public void keyPressed(KeyEvent e) {

	}
	
	protected boolean checkIfInputAllowed(KeyEvent e, int num_chars_selected)
	{
		if(e.getSource().equals(m_txt_message) || 
				e.getSource().equals(m_txt_sender_name) || 
				e.getSource().equals(m_cbx_risk.getEditor().getEditorComponent()) ||
				e.getSource().equals(m_cbx_reaction.getEditor().getEditorComponent()) ||
				e.getSource().equals(m_cbx_originator.getEditor().getEditorComponent())) {
			String ch = Character.toString(e.getKeyChar());
			int len_ch = TextFormat.GsmStrLen(ch);

			updatePreviewText();
			int n_total_len = TextFormat.GsmStrLen(m_txt_preview.getText());//m_txt_preview.getText().length();
			if(e.getSource().equals(m_txt_message))// || e.getSource().equals(m_txt_sender_name))
			{
                int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected - TextFormat.GsmStrLen(m_txt_message.getText());

                if(n_chars_left_for_message<=len_ch)
                {
                    e.consume();
                    return false;
                }
                return true;
			}
			else if(e.getSource().equals(m_txt_sender_name))
			{
				if(n_total_len>=MAX_TOTAL_CHARS + num_chars_selected - len_ch)
				{
					int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected - TextFormat.GsmStrLen(m_txt_message.getText());

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
				if(n_total_len>=MAX_TOTAL_CHARS + num_chars_selected - len_ch)
				{
					int n_chars_left_for_message = MAX_TOTAL_CHARS - getTextLengthOfFields() + num_chars_selected - TextFormat.GsmStrLen(m_txt_message.getText());

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
			int n_chars_left_for_eventname = MAX_EVENTNAME_LENGTH - m_txt_date_time.getText().length()-1 + num_chars_selected;
			if(m_txt_event_name.getText().length() >= n_chars_left_for_eventname-1) {
				return false;
			}
		}

		return true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource().equals(m_txt_event_name))
		{
		}
		else
		{
			updatePreviewText();
			checkForEnableSendButton();
		}
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
			
			int selectionStart = c.getSelectionStart();
			int selectionEnd = c.getSelectionEnd();
			
			String strText = c.getText();
			TextFormat.RegExpResult res = TextFormat.RegExpGsm(strText + e.getKeyChar());
			if(!res.valid)
			{
				c.setText(res.resultstr);
				c.setSelectionStart(selectionStart);
				c.setSelectionEnd(selectionEnd);
				e.consume();
			}
            int txt_before = TextFormat.GsmStrLen(m_txt_message.getText()); //m_txt_message.getText().length();
			int char_selection = TextFormat.GsmStrLen(c.getText().substring(c.getSelectionStart(), c.getSelectionEnd())); //c.getSelectionEnd()-c.getSelectionStart();

			boolean b = checkIfInputAllowed(e, char_selection);
			int after = TextFormat.GsmStrLen(m_txt_preview.getText()); //m_txt_preview.getText().length();

			int txt_after = TextFormat.GsmStrLen(m_txt_message.getText()) + getTextLengthOfFields() + 1;

			if(!b)
			{
				e.consume();
			}
			{
				if(txt_after>MAX_TOTAL_CHARS)
				{
					try
					{
						int diff = txt_after-(MAX_TOTAL_CHARS);
						String cut = TextFormat.GsmStrMaxLen(c.getText(), txt_before-diff);
						c.setText(cut);
						c.setSelectionStart(selectionStart);
						c.setSelectionEnd(selectionEnd);
					}
					catch(Exception err)
					{
						log.warn(err.getMessage(), err);
					}
				}
			}
		}
		else if(e.getSource().equals(m_txt_event_name)) {
			if(!projectOpen())
			{

				JTextComponent c = (JTextComponent)e.getSource();
                int char_selection = c.getSelectionEnd()-c.getSelectionStart();
				boolean b = checkIfInputAllowed(e, char_selection);
				int after = c.getText().length() + m_txt_date_time.getText().length() + 1;
				if(!b)
					e.consume();
				if(after>=MAX_EVENTNAME_LENGTH)
				{
					try
					{
                        setEventText(c.getText().substring(0, MAX_EVENTNAME_LENGTH - m_txt_date_time.getText().length() - 1));//current_len-diff));
					}
					catch(Exception err)
					{
						log.warn(err.getMessage(), err);
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

        getStatusController().set_cbsendingresponse(response, true);
        Project p = new Project();
        p.set_projectpk(Long.toString(response.getLProjectpk()));
        PAS.pasplugin.onOpenProject(p, response.getLRefno());
        PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
        Variables.getMapFrame().set_active_shape(null);
        Variables.getMapFrame().set_mode(MapFrame.MapMode.PAN);
        autoClickButton(m_btn_reset);
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
		if(m_txt_message.getText().length()<1)
			enable_send = false;

        if(Variables.getMapFrame().get_active_shape()!=null &&
                !Variables.getMapFrame().get_active_shape().can_lock(Variables.getUserInfo().get_departments().get_combined_restriction_shape()))
            enable_send = false;

		return enable_send;
	}
	
	private void updateCharacters() {
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
