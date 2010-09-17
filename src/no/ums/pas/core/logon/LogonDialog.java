package no.ums.pas.core.logon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JToolTip;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/*Substance 3.3
import org.jvnet.substance.utils.params.PropertiesFileParamReader;
*/
import no.ums.pas.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.ws.WSPowerup;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.localization.LocalizationFinder;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.UPOWERUPRESPONSE;

import org.opengis.coverage.grid.Grid;



import java.awt.event.*;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*dialog box*/
public class LogonDialog extends JFrame implements WindowListener, ComponentListener, ActionListener { //JDialog { //implements ComponentListener {
	public static final long serialVersionUID = 1;
	private LogonInfo m_logoninfo = null;
	public LogonInfo get_logoninfo() { return m_logoninfo; }
	private LogonPanel m_panel;
	public LogonPanel get_logonpanel() { return m_panel; }
	private Logon m_logon;
	public void setMaxLogonTries(int i)
	{
		try
		{
			m_logon.setMaxTries(i);
			System.out.println("set max logontries to " + i);
		}
		catch(Exception e)
		{
			
		}
	}
	private String m_sz_errortext;
	private boolean m_b_logonproc_start = false;
	protected String wantedlanguage = "";
	protected String selectedlanguage = "";
	public void setLoading(boolean b)
	{
		try
		{
			m_panel.m_nslist.setLoading(b);
		}
		catch(Exception e) { }
	}

	
	public void restart() { 
		m_b_logonproc_start = false;
		m_panel.m_txt_passwd.setText("");
		enableInput(true);
		m_panel.m_txt_passwd.grabFocus();
	}
	public boolean get_logonproc_start() { return m_b_logonproc_start; }
	public void set_errortext(String s) { 
		set_errortext(s, true);
	}
	
	public void set_errortext(String s, boolean b_error)
	{
		m_sz_errortext = s;
		m_panel.m_lbl_errormsg.setText(s);
		if(b_error)
			m_panel.m_lbl_errormsg.setForeground(Color.RED);
		else
			m_panel.m_lbl_errormsg.setForeground(new Color(0,140,0));
	}
	
	public void fillNSInfo() {
		
		m_panel.m_nslist.start_search();
	}
	protected void set_response(boolean b_quit) {
		m_logon.set_response(b_quit);
		set_errortext(PAS.l("common_contacting_server"), false);
	}
	protected void enableInput(boolean b) {
		m_panel.m_txt_userid.setEnabled(b);
		m_panel.m_txt_compid.setEnabled(b);
		m_panel.m_txt_passwd.setEnabled(b);
		m_panel.m_btn_submit.setEnabled(b);
	}
	
	public void windowActivated(WindowEvent e) {
		if(m_logoninfo.get_compid().length()>0 && m_logoninfo.get_userid().length()>0) {
			m_panel.m_txt_passwd.requestFocusInWindow();
			m_panel.m_txt_passwd.selectAll();
		}
		else if(m_logoninfo.get_userid().length()>0) {
			m_panel.m_txt_compid.requestFocusInWindow();
			m_panel.m_txt_compid.selectAll();
		}
		
	}
	public void windowClosed(WindowEvent e) {
	}
	public void windowClosing(WindowEvent e) {
		set_response(true); //for exiting		
	}
	public void windowDeactivated(WindowEvent e) {
	}
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) {
		
	}
	
	public LogonDialog(Logon logon, JFrame owner, boolean b_modal, 
			LogonInfo logoninfo, String wantedlanguage,
			boolean b_request_newsession) {
		super(PAS.l("logon_heading"));
		this.setResizable(false);
		this.setIconImage(PAS.get_pas().getIconImage());
		this.addWindowListener(this);
		this.wantedlanguage = wantedlanguage;
		m_logon = logon;
		setAlwaysOnTop(true);
		m_logoninfo = logoninfo;
		setName("Logon");
		//setTitle("Logon to UMS PAS");
		//setTitle(PAS.l("logon_heading"));
		int n_width  = 500;//310;
		int n_height = 330; //230;
		
		
		setBounds((PAS.get_pas().get_screensize().width/2 - n_width/2), PAS.get_pas().get_screensize().height/2 - n_height/2, n_width, n_height);
		this.setLocationRelativeTo(PAS.get_pas());
		if(b_request_newsession)
			this.setAlwaysOnTop(true);
		m_panel = new LogonPanel(PAS.get_pas());
		m_panel.m_txt_passwd.addComponentListener(this);
		//m_panel.add_controls();
		PAS.pasplugin.onLogonAddControls(m_panel);
		add_panel();
		
		try {
			if(m_logoninfo.get_userid().length()>0){
				m_panel.m_txt_userid.setText(m_logoninfo.get_userid());
			}
			if(m_logoninfo.get_compid().length()>0) {
				System.out.println("Default Compid=" + m_logoninfo.get_compid());
				m_panel.m_txt_compid.setText(m_logoninfo.get_compid());
			}
		} catch(Exception e) {
		}
		m_panel.init();
		if(b_request_newsession)
		{
			m_panel.m_txt_userid.setEditable(false);
			m_panel.m_txt_compid.setEditable(false);
		}
		try
		{
			new WSPowerup(this);
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{ 
		/*powerup command finished, WebService is active*/
		if("act_powerup".equals(e.getActionCommand()))
		{
			WSPowerup powerup = (WSPowerup)e.getSource();
			PAS.pasplugin.onAfterPowerUp(this, powerup);
		}
	}
	
	void add_panel() {
		getContentPane().add(m_panel, BorderLayout.CENTER);
	}
	void set_logoninfo(String sz_userid, String sz_compid, String sz_passwd, 
				String language) {
		m_logoninfo = new LogonInfo(sz_userid, sz_compid, sz_passwd, language);
	}
	public void componentHidden(ComponentEvent e) {
	}
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		m_panel.m_nslist.setPreferredSize(new Dimension(getWidth()-10, 150));
		m_panel.m_nslist.revalidate();
		m_panel.revalidate();
	}
	public void componentShown(ComponentEvent e) {
	}

	public void focusGained(FocusEvent e) {
		
	}
	public void focusLost(FocusEvent e) {
		
	}

	public class LanguageCombo extends StdTextLabel
	{
		String languageid;
		public String getLanguageid() { return languageid; }
		public LanguageCombo(String sz_language, String languageid)
		{
			super(sz_language);
			this.languageid = languageid; 
			//load icon
			try
			{
				setIcon(no.ums.pas.ums.tools.ImageLoader.load_icon("nationalities_flags/" + languageid + ".gif"));
			}
			catch(Exception e)
			{
				
			}
		}
		public String toString(){
			return getText();
		}
	}
	
	public class LogonPanel extends DefaultPanel implements KeyListener, FocusListener { //implements ActionListener {
	
		public static final long serialVersionUID = 1;
		private StdTextArea	m_txt_userid;
		private StdTextArea m_txt_compid;
		private JPasswordField m_txt_passwd;
		private StdTextLabel m_lbl_userid;
		private StdTextLabel m_lbl_compid;
		private StdTextLabel m_lbl_passwd;
		private JButton		m_btn_submit;
		private StdTextLabel m_lbl_errormsg;
		private NSList m_nslist;
		private StdTextLabel m_lbl_language;
		private JComboBox m_combo_language;
		
		public StdTextArea getUserId() { return m_txt_userid; }
		public StdTextArea getCompId() { return m_txt_compid; }
		public JPasswordField getPasswd() { return m_txt_passwd; }
		public StdTextLabel getLblUserId() { return m_lbl_userid; }
		public StdTextLabel getLblCompId() { return m_lbl_compid; }
		public StdTextLabel getLblPasswd() { return m_lbl_passwd; }
		public NSList getNSList() { return m_nslist; }
		public StdTextLabel getLblLanguage() { return m_lbl_language; }
		public JComboBox getLanguageCombo() { return m_combo_language; }
		public JButton getBtnSubmit() { return m_btn_submit; }
		public StdTextLabel getLblError() { return m_lbl_errormsg; }
		
		public LogonPanel(PAS pas) {
			super();
			m_txt_userid = new StdTextArea("", false);
			m_txt_compid = new StdTextArea("", false);
			m_txt_passwd = new JPasswordField("") {
				public JToolTip createToolTip()
				{
					tooltip = super.createToolTip();
					if(tooltip!=null)
						tooltip.setBackground(Color.yellow);			
					return tooltip;
				}
			};
			m_lbl_userid = new StdTextLabel(PAS.l("logon_userid"));
			m_lbl_compid = new StdTextLabel(PAS.l("logon_company"));
			m_lbl_passwd = new StdTextLabel(PAS.l("logon_password"));
			m_lbl_language = new StdTextLabel(PAS.l("common_language"));
			m_lbl_errormsg = new StdTextLabel("");
			m_combo_language = new JComboBox();
			m_lbl_errormsg.setForeground(Color.RED);
			m_btn_submit = new JButton(PAS.l("common_submit"));
			m_txt_userid.setPreferredSize(new Dimension(100, 15));
			m_txt_compid.setPreferredSize(new Dimension(100, 15));
			m_txt_passwd.setPreferredSize(new Dimension(100, 15));
			m_combo_language.setPreferredSize(new Dimension(100,15));
			m_lbl_userid.setPreferredSize(new Dimension(100, 15));
			m_lbl_compid.setPreferredSize(new Dimension(100, 15));
			m_lbl_passwd.setPreferredSize(new Dimension(100, 15));
			m_lbl_language.setPreferredSize(new Dimension(100, 15));
			//m_btn_submit.setPreferredSize(new Dimension(100, 15));
			m_lbl_errormsg.setPreferredSize(new Dimension(200, 15));
			m_btn_submit.setActionCommand(ENABLE);
			m_btn_submit.setActionCommand("act_logon");
			m_btn_submit.addActionListener(this);
			m_lbl_userid.enableInputMethods(false);
			m_lbl_compid.enableInputMethods(false);
			m_lbl_passwd.enableInputMethods(false);
			m_nslist = new NSList(new String [] { PAS.l("common_date") + "/" + PAS.l("common_time"), PAS.l("common_domain"), PAS.l("common_ip"), PAS.l("common_location"), "" }, new int [] { 80, 40, 40, 80, 20 }, new Dimension(380, 100));
			this.addKeyListener(this);
			m_txt_userid.addKeyListener(this);
			m_txt_compid.addKeyListener(this);
			m_txt_passwd.addKeyListener(this);
			this.addFocusListener(this);
			m_txt_passwd.addFocusListener(this);
			m_combo_language.setRenderer(new ListCellRenderer() {
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					return (StdTextLabel)value;
				}
			});
			m_combo_language.addActionListener(this);
			m_combo_language.setActionCommand("act_language_changed");
			
			try
			{
				ArrayList<Locale> loc = LocalizationFinder.getAvailableLangfiles();
				Hashtable<String, String> l = new Hashtable<String, String>();
				if(loc!=null)
				{
					for(int i=0; i < loc.size(); i++)
					{
						Locale locale = loc.get(i);
						l.put(locale.getLanguage() + "_" + locale.getCountry(), locale.getDisplayLanguage());
					}
				}
				else
					l.put("en_GB", "English");
				Enumeration en = l.keys();
				while(en.hasMoreElements())
				{
					String key = (String)en.nextElement();
					String value = l.get(key);
					LanguageCombo item = new LanguageCombo(value, key);
					m_combo_language.addItem(item);
					String temp = wantedlanguage;
					boolean b_use = temp.equalsIgnoreCase(key);
					if(b_use)
					{
						System.out.println("language " + key);
						m_combo_language.setSelectedItem(item);
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Error");
			}
			System.out.println("LOGON");
			
		}
		
		public void add_controls()
		{
			int verticalspacing = 2;
			m_gridconst.fill = GridBagConstraints.HORIZONTAL;

			set_gridconst(3, inc_panels(), 1, 1, GridBagConstraints.WEST);
			add(m_lbl_language, m_gridconst);
			set_gridconst(5, get_panel(), 1, 1, GridBagConstraints.WEST);
			add(m_combo_language, m_gridconst);

			add_spacing(DIR_VERTICAL, verticalspacing);
			
			set_gridconst(3,inc_panels(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_lbl_userid, m_gridconst);
			set_gridconst(5,get_panel(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_txt_userid, m_gridconst);
			add_spacing(DIR_VERTICAL, verticalspacing);

			set_gridconst(3,inc_panels(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_lbl_compid, m_gridconst);
			set_gridconst(5,get_panel(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_txt_compid, m_gridconst);

			add_spacing(DIR_VERTICAL, verticalspacing);

			set_gridconst(3,inc_panels(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_lbl_passwd, m_gridconst);
			set_gridconst(5,get_panel(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_txt_passwd, m_gridconst);

			add_spacing(DIR_VERTICAL, verticalspacing);
			
			
			set_gridconst(5,inc_panels(),1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_btn_submit, m_gridconst);			
			set_gridconst(0,inc_panels(),7,1, GridBagConstraints.WEST);
			add(m_lbl_errormsg, m_gridconst);
			set_gridconst(0,inc_panels(),7,1);
			add(m_nslist, m_gridconst);
		}
		public void init() {
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("act_logon")) {
				enableInput(false);
				m_b_logonproc_start = true;
				try
				{
					String sha = encrypt(new String(m_txt_passwd.getPassword()));
					char[] s = m_txt_passwd.getPassword();
					int l = sha.length();
					set_logoninfo(m_txt_userid.getText(), m_txt_compid.getText(), sha/*new String(m_txt_passwd.getPassword())*/, selectedlanguage);
					m_txt_passwd.setText("");
					set_response(false);
				}
				catch(Exception err)
				{
					Error.getError().addError(PAS.l("common_error"), "Error while encrypting password", err, Error.SEVERITY_ERROR);
				}
			}
			else if(e.getActionCommand().equals("act_language_changed"))
			{
				LanguageCombo lang = (LanguageCombo)m_combo_language.getSelectedItem();
				selectedlanguage = lang.getLanguageid();
				
			}
		}
		
		public String encrypt(String pw)
			throws Exception
		{
			String sha = "";
			try
			{
				MessageDigest md = MessageDigest.getInstance("SHA-512");
				md.update(pw.getBytes());
				byte[] mb = md.digest();
	            for (int i = 0; i < mb.length; i++) {
	                byte temp = mb[i];
	                String s = Integer.toHexString(new Byte(temp));
	                while (s.length() < 2) {
	                    s = "0" + s;
	                }
	                s = s.substring(s.length() - 2);
	                sha += s;
	            }
			}
			catch(Exception e)
			{
				throw e;
			}
			return sha;
		}
		
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
					m_panel.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_logon"));
					break;
			}
			if(e.getSource().equals(m_txt_passwd))
			{
				try
				{
					//java.awt.Toolkit.getDefaultToolkit().setLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK, false);
					if(e.getKeyCode()==java.awt.event.KeyEvent.VK_CAPS_LOCK)
						checkForCapsLock();
				}
				catch(Exception err)
				{
					
				}
			}
		
		}
		private JToolTip tooltip;
		public void keyReleased(KeyEvent e) { 
			//if(e.getKeyCode()==java.awt.event.KeyEvent.VK_CAPS_LOCK)
			//	checkForCapsLock();
		}
		public void keyTyped(KeyEvent e) { 
		}
		boolean b_tooltip_visible = false;
		protected void checkForCapsLock()
		{
			try
			{
				boolean b = java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
				if(b)
				{
					m_txt_passwd.setToolTipText(PAS.l("logon_caps_lock_warning"));
					javax.swing.Action toolTipAction = m_txt_passwd.getActionMap().get("postTip");
					if (toolTipAction != null)
					{
						ActionEvent postTip = new ActionEvent(m_txt_passwd, ActionEvent.ACTION_PERFORMED, "");
						toolTipAction.actionPerformed( postTip );
					}				
				}
				else if(!b)
				{
					javax.swing.Action toolTipAction = m_txt_passwd.getActionMap().get("hideTip");
					m_txt_passwd.setToolTipText("");
					ActionEvent postTip = new ActionEvent(m_txt_passwd, ActionEvent.ACTION_PERFORMED, "");
					toolTipAction.actionPerformed( postTip );
				}
				b_tooltip_visible = b;
				tooltip.setVisible(b);
			}
			catch(Exception e)
			{
				//System.out.println(e.getMessage());
			}
		}
		@Override
		public void focusGained(FocusEvent e) {
			try
			{
				checkForCapsLock();
				if(e.getComponent().equals(m_txt_passwd))
					PAS.get_pas().LoadVisualSettings(PAS.get_pas().get_pasactionlistener(), m_txt_userid.getText(), m_txt_compid.getText(), true);
				//if(e.getSource().equals(m_txt_passwd))
				//	java.awt.Toolkit.getDefaultToolkit().setLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK, false);
			}
			catch(Exception err)
			{
				
			}
		}
		@Override
		public void focusLost(FocusEvent e) {
		}		
	}
	
	public class NSList extends SearchPanelResults {
		public static final long serialVersionUID = 1;
		public NSList(String [] sz_columns, int [] n_width, Dimension dim) {
			super(sz_columns, n_width, null, dim);
			//this.set_custom_cellrenderer()
		}

		protected void start_search() {
			if(m_logon.get_userinfo()==null)
				return;
			if(m_logon.get_userinfo().get_nslookup()==null)
				return;
			ArrayList<no.ums.pas.core.logon.UserInfo.NSLookup> arrns = m_logon.get_userinfo().get_nslookup();
			for(int i=0; i < arrns.size(); i++) {
				UserInfo.NSLookup ns = (UserInfo.NSLookup)arrns.get(i);
				Object [] o = new Object [] { ns.get_lastdatetime(), ns, ns.get_ip(), ns.get_location(), ns.get_ssuccess() };
				this.insert_row(o, i);
			}
		}
	    public void set_custom_cellrenderer(TableColumn column, final int n_col)
	    {
	    	//if(n_col==4)
	    	{
	    		column.setCellRenderer(new DefaultTableCellRenderer() {
	    			public static final long serialVersionUID = 1;
	    		    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    		        Component renderer =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);    //---
	    		        UserInfo.NSLookup ns = (UserInfo.NSLookup)m_tbl_list.getValueAt(row, 1);//(UserInfo.NSLookup)value;
    		        	//renderer.setFont(PAS.f().getTitleFont());
    		        	if(n_col==4)
    		        	{
		    		        if(ns.get_success().booleanValue()) {
		    		        	//FontSet renderer.setFont(new Font("Arial", Font.PLAIN, 11));
		    		        	renderer.setForeground(new Color(0, 190, 0));
		    		        	//renderer.setBackground(new Color(250, 255, 250));
		    		        }
		    		        else {
		    		        	//FontSet renderer.setFont(new Font("Arial", Font.BOLD, 11));
		    		        	renderer.setForeground(new Color(220, 0, 0));
		    		        	//renderer.setBackground(new Color(255, 250, 250));
		    		        }
    		        	}
	    		        return renderer;
	    		    }
	    		});
	    	}
	    }
		protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}

		protected void onMouseLDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}

		protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}

		protected void onMouseRDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}

		protected void valuesChanged() {
			
		}

		public boolean is_cell_editable(int row, int col) {
			return false;
		}
	}


}