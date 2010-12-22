package no.ums.pas.sound;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.xml.namespace.QName;

import no.ums.pas.*;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.ws.vars;
import no.ums.pas.send.*;
import no.ums.pas.send.sendpanels.*;
import no.ums.pas.sound.soundinfotypes.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.pas.ULOGONINFO;

import java.io.*;

import java.net.*;
import java.nio.ByteBuffer;

import no.ums.ws.pas.*;


public class SoundTTSPanel extends DefaultPanel implements FocusListener, KeyListener {
	public static final long serialVersionUID = 1;
	//private SendController m_controller;
	private JTextArea m_text;
	private JScrollPane m_scrollPane;
	private JButton m_btn_convert;
	private SendWindow m_parent;
	private JComboBox m_combo_tts;
	private JComboBox m_combo_txtlib;
	private Sending_Files m_file;
	public Sending_Files get_soundpanel() { return m_file; }
	public SendWindow get_parent() { return m_parent; }
	private SoundRecorderPanel m_playpanel;
	public SoundRecorderPanel get_playpanel() { return m_playpanel; }
	
	public SoundTTSPanel(Sending_Files file, /*SendController controller, */SendWindow parent) {
		super();
		m_parent = parent;
		//m_controller = controller;
		m_file = file;
		m_text = new JTextArea(10, 50);
		m_scrollPane = 
		    new JScrollPane(m_text,
		                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//FontSet 
		m_text.setFont(new Font(null, Font.PLAIN, 11)); //dette hadde noe med substance å gjøre, måtte fjerne den for ikke å få java.lang.NegativeArraySizeException
		m_text.setLineWrap(true);
		m_text.setWrapStyleWord(true);
		m_text.addFocusListener(this);
		m_text.addKeyListener(this);
		m_text.setSelectedTextColor(Color.black);
		m_text.setFocusTraversalKeysEnabled(false);
		m_text.setHighlighter(new DefaultHighlighter());
		//m_text.setPreferredSize(new Dimension(350, 200));
		//m_scrollPane.setPreferredSize(new Dimension(350, 200));
		
		m_text.setEditable(true);		
		m_btn_convert = new JButton(PAS.l("sound_panel_tts_convert"));
		//m_text.setPreferredSize(new Dimension(350,200));
		m_combo_tts = new JComboBox();
		m_combo_tts.setPreferredSize(new Dimension(150, 20));
		m_combo_txtlib = new JComboBox();
		m_combo_txtlib.setPreferredSize(new Dimension(250, 20));

		m_text.invalidate();
		m_btn_convert.setActionCommand("act_tts_convert");
		m_combo_txtlib.setActionCommand("act_txtlib_changed");
		m_btn_convert.addActionListener(this);
		m_combo_txtlib.addActionListener(this);
		m_playpanel = new SoundRecorderPanel(file, StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), SoundRecorder.RECTYPE_FILE);
		m_playpanel.disable_record();
		add_controls();
		populate_tts();
		populate_txtlib();
	}
	
	public void populate_txtlib() {
		try {
			//m_combo_txtlib.addItem((SoundlibFile)new SoundlibFileTxt(new String[]{"true", String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()), "", "", "0"}));
			m_combo_txtlib.addItem((SoundlibFile)new SoundlibFileTxt());
			for(int i=0; i < m_parent.get_txtlib().size(); i++) {
				m_combo_txtlib.addItem(m_parent.get_txtlib().get(i));
			}
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"),"Exception in populate_txtlib",e,1);
		}
	}
	
	public void populate_tts() {
		try {
			for(int i=0; i < m_parent.get_tts().size(); i++) {
				m_combo_tts.addItem(m_parent.get_tts().get(i));
			}
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR populate_tts() " + e.getMessage(), e);
			Error.getError().addError(PAS.l("common_error"),"Exception in populate_txtlib",e,1);
		}
	}
	public void actionPerformed(ActionEvent e) {
		if("act_tts_convert".equals(e.getActionCommand())) {
			start_converter();
		} else if("act_tts_convert_complete".equals(e.getActionCommand())) {
			converter_stopped((String)e.getSource());
			//get_soundpanel().set_soundfiletype(Sending_Files.SOUNDFILE_TYPE_TTS_, (String)e.getSource());
			get_soundpanel().set_soundfiletype(Sending_Files.SOUNDFILE_TYPE_TTS_, new SoundInfoTTS((String)e.getSource(),-1, null));
			
			// Her gjøres den ferdig og reloader parent for å enable next knappen
			get_parent().set_next_text();
		} else if("act_txtlib_changed".equals(e.getActionCommand())) {
			//load text from server
			try {
				SoundlibFileTxt obj = (SoundlibFileTxt)((JComboBox)e.getSource()).getSelectedItem();
				
				if(m_combo_txtlib.getSelectedIndex()<0)
					return;
					
				if(m_combo_txtlib.getSelectedIndex()>0)
				{
					//if(obj.get_messagepk() != "-1") {
					start_progress(PAS.l("sound_panel_tts_retrieving_text_data"));
					obj.load_file(this, "act_download_txtfile_finished");
				}
				else //null selected
				{
					if(m_combo_txtlib.getSelectedIndex() == 0) {
						m_text.setText("");
						m_text.setToolTipText(null);
					}
					//UpdateTextFields();
				}
				//}
			} catch(Exception err) {
				PAS.get_pas().add_event("Error loading TxtLib file " + err.getMessage(), err);
				Error.getError().addError("SoundTTSPanel","Exception in actionPerformed",err,1);
			}
			//stop_progress();
		} else if("act_download_finished".equals(e.getActionCommand())) {
			download_finished();
		} else if("act_download_txtfile_finished".equals(e.getActionCommand())) {
			SoundlibFileTxt obj = (SoundlibFileTxt)e.getSource();
			try {
				PAS.get_pas().add_event("act_download_txtfile_finished", null);
				set_text(obj.get_text());
				set_language(obj.get_langpk());
				m_text.setToolTipText(PAS.l("main_sending_text_template_tooltip"));
				//UpdateTextFields();
				n_current_bracket = -1;
				m_text.requestFocus();
			} catch(Exception err) {
				PAS.get_pas().add_event("Exception caught on SoundTTSPanel.actionPerformed act_download_finished " + err.getMessage(), err);
				Error.getError().addError(PAS.l("common_error"),"Exception in actionPerformed",err,1);
			}			
			stop_progress();
		}
	}
	private void set_language(int n_langpk) {
		TTSLang lang = null;
		for(int i=0; i < m_combo_tts.getItemCount(); i++) {
			lang = (TTSLang)m_combo_tts.getItemAt(i);
			if(lang.get_langpk() == n_langpk) {
				m_combo_tts.setSelectedIndex(i);
				return;
			}
		}
	}
	private void set_text(String sz_text) {
		m_text.setText(sz_text);
	}
	public void start_converter() {
		start_progress(PAS.l("sound_panel_tts_converting_tts"));
		try {
			String sz_data = m_text.getText();
			TTSLang lang = (TTSLang)m_combo_tts.getSelectedItem();
			convert(lang.get_langpk(), lang.get_name(), sz_data);
		} catch(Exception err) {
			PAS.get_pas().add_event("ERROR: act_tts_convert " + err.getMessage(), err);
			Error.getError().addError(PAS.l("common_error"),"Exception in start_converter",err,1);
		}
		m_btn_convert.setEnabled(false);
	}
	String sz_localpath;
	String sz_localfile;
	public void converter_stopped(String sz_file) {
		sz_localpath = StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_);
		sz_localfile = sz_file;
		
		m_playpanel.enable_player(false);
		start_download();
		new Installer().download_and_save(PAS.get_pas().get_sitename() + "audiofiles/" + sz_file, sz_localpath + sz_localfile, true, this, "act_download_finished", this);
		//new Installer().download_and_save("https://secure.ums2.no/vb4utv/audiofiles/" + sz_file, sz_localpath + sz_localfile, true, this, "act_download_finished", this);
	}
	public void download_finished() {
		try {
			stop_progress();
			//if(m_file.get_soundfile().)
			//m_playpanel.initialize_player(sz_localfile, true);
			URL url = new URL(PAS.get_pas().get_sitename() + "audiofiles/" + sz_localfile);
			URLConnection urlConn;
			
			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);
			//urlConn.
			InputStream is = urlConn.getInputStream();
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int r;
			while(true) {
				r = is.read(buffer);
				if(r == -1)
					break;
				out.write(buffer, 0, r);
			}
			ByteBuffer bb = ByteBuffer.wrap(out.toByteArray());
			
			m_playpanel.initialize_player(bb, true);
			
			m_btn_convert.setEnabled(true);
			
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR: SoundTTSPanel.download_finished() " + e.getMessage(), e);
			Error.getError().addError(PAS.l("common_error"),"Exception in download_finished",e,1);
		}
		
	}
	public void start_progress(String sz_text) {
		get_playpanel().enable_player(false);
		get_parent().set_comstatus(sz_text);
		get_parent().get_loader().start_progress(0, sz_text);
	}
	public void stop_progress() {
		get_playpanel().enable_player(true);
		get_parent().reset_comstatus();
		get_parent().get_loader().reset_progress();
	}
	public void start_download() {
		get_parent().set_comstatus(PAS.l("common_downloading"));
		get_parent().get_loader().start_progress(0, PAS.l("common_downloading"));
	}
	public void convert(int n_langpk, String sz_name, String sz_text) {
		PAS.get_pas().add_event("Execute TTS convertion", null);
		TTSConverter tts = new TTSConverter(m_parent.get_sendingid(), get_soundpanel().get_soundfile().get_filenumber(), 
											n_langpk, sz_name, sz_text, this, "act_tts_convert_complete");
		tts.start();
	}
	public void set_ttsdata() {
	}
	public void add_controls() {
		set_gridconst(0, inc_panels(), 2, 1, GridBagConstraints.WEST);
		add(m_combo_tts, m_gridconst);
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_combo_txtlib, m_gridconst);
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_scrollPane, m_gridconst);
		set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.WEST);
		add(m_btn_convert, m_gridconst);
		set_gridconst(0, inc_panels(), 5, 1, GridBagConstraints.WEST);
		add(m_playpanel, m_gridconst);
		init();
	}
	public void init() {
		setVisible(true);
	}
	
	no.ums.pas.ums.tools.UnderlineHighlightPainter painter = new no.ums.pas.ums.tools.UnderlineHighlightPainter(Color.red);
	
	@Override
	public void keyPressed(KeyEvent e) {
		//if(e.getKeyCode()==KeyEvent.VK_TAB)
		if(e.getKeyChar()=='\t')
		{
			e.consume();
			if(e.isShiftDown())
				tabPressedRev();
			else
				tabPressed();
		}
		resetHighLights();
		//m_txt_messagetext.getHighlighter().addHighlight(p0, p1, p)
			
	}
	
	int n_current_bracket = -1;
	int n_highlight_bracket = -1;
	
	public void tabPressedRev()
	{
		
	}
	public void tabPressed()
	{
		Point p = gotoNextBracket(true);
		if(p!=null)
		{
			m_text.setSelectionStart(p.x);
			m_text.setSelectionEnd(p.y+1);
		}
	}
	
	public void gotoPrevBracket()
	{
	}
	
	public Point gotoNextBracket(boolean b_restart)
	{
		int index = (b_restart ? n_current_bracket : n_highlight_bracket);
		String s = m_text.getText();
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
	protected void resetHighLights()
	{
		n_highlight_bracket = -1;
		Point p;
		while((p=gotoNextBracket(false))!=null)
		{
			try
			{
				m_text.getHighlighter().addHighlight(p.x, p.y, painter);
			} 
			catch(Exception e){
				
			}
		}
	}
	
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
	public void keyReleased(KeyEvent e) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}

class TTSConverter extends Thread {
	private int m_n_sendingid;
	private int m_n_dynfile;
	private int m_n_langpk;
	private String m_sz_name;
	private String m_sz_text;
	private String m_sz_action;
	private ActionListener m_callback;
	private String m_sz_serverfile;
	public int get_sendingid() { return m_n_sendingid; }
	public int get_dynfile() { return m_n_dynfile; }
	public int get_langpk() { return m_n_langpk; }
	public String get_name() { return m_sz_name; }
	public String get_text() { return m_sz_text; }
	public String get_action() { return m_sz_action; }
	public ActionListener get_callback() { return m_callback; }
	public String get_serverfile() { return m_sz_serverfile; }
	protected void set_serverfile(String sz_file) { m_sz_serverfile = sz_file; }
	
	TTSConverter(int n_sendingid, int n_dynfile, int n_langpk, String sz_name, String sz_text, ActionListener callback, String sz_action) {
		super();
		m_n_sendingid	= n_sendingid;
		m_n_dynfile		= n_dynfile;
		m_n_langpk		= n_langpk;
		m_sz_name		= sz_name;
		m_sz_text		= sz_text;
		m_callback		= callback;
		m_sz_action		= sz_action;
	}
	public void run() {
		String sz_file = new String("");
		try {
			sz_file = convert();
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"),"Exception in run",e,1);
		}
		get_callback().actionPerformed(new ActionEvent(sz_file, ActionEvent.ACTION_PERFORMED, get_action()));
	}
	public String convert() {
		String sz_filename = null;
		try {
			/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_convert_tts.asp");
			form.setParameter("n_langpk", new Integer(get_langpk()).toString());
			form.setParameter("n_sendingid", new Integer(get_sendingid()).toString());
			form.setParameter("n_dynfile", new Integer(get_dynfile()).toString());
			form.setParameter("sz_text", get_text());
			InputStream is = null;
			sz_filename = parse(form.post());*/
			
			no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
			ObjectFactory of = new ObjectFactory();
			ULOGONINFO logon = of.createULOGONINFO();
			UCONVERTTTSREQUEST ttsreq = of.createUCONVERTTTSREQUEST();
			
			logon.setLComppk(ui.get_comppk());
			logon.setLDeptpk(ui.get_current_department().get_deptpk());
			logon.setLUserpk(Long.parseLong(ui.get_userpk()));
			logon.setSzCompid(ui.get_compid());
			logon.setSzDeptid(ui.get_current_department().get_deptid());
			logon.setSzPassword(ui.get_passwd());
			logon.setSzUserid(ui.get_userid());
			logon.setSessionid(ui.get_sessionid());
			ttsreq.setNDynfile(get_dynfile());
			ttsreq.setNLangpk(get_langpk());
			ttsreq.setSzText(get_text());
			
			java.net.URL wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
			//java.net.URL wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			UCONVERTTTSRESPONSE response = new no.ums.ws.pas.Pasws(wsdl, service).getPaswsSoap12().convertTTS(logon, ttsreq);
			switch(response.getNResponsecode())
			{
			case 0: //OK
				return response.getSzServerFilename();
			}
			
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR TTSConverter.convert() " + e.getMessage(), e);
			Error.getError().addError(PAS.l("common_error"),"Exception in convert",e,1);
			return new String("");
		}
		return sz_filename;	
	}
	/*class PostForm implements Runnable {
		HttpPostForm m_form;
		InputStream m_is;
		public InputStream get_is() { return m_is; }
		PostForm(HttpPostForm form) {
			m_form = form;
		}
		public void run() {
			try {
				m_is = m_form.post();
			} catch(Exception e) {
				Error.getError().addError(PAS.l("common_error"),"Exception in run",e,1);
			}
		}
	}*/
	public String parse(InputStream is) {
		String sz_out = "";
		try {
			byte [] bytes = new byte[1024];
			int n_read = 0;
			int offset = 0;
			int errors = 0;
			while(n_read!=-1) {
				try {
					n_read = is.read(bytes, offset, bytes.length);
				} catch(Exception e) {
					PAS.get_pas().add_event("Error reading TTS inputstream " + e.getMessage(), e);
					Error.getError().addError(PAS.l("common_error"),"Exception in parse",e,1);
					try { Thread.sleep(1000); } catch(InterruptedException err) { }
					errors++;
					if(errors >= 10)
						break;
					else
						continue;
				}
				if(n_read>0) {
					String sz_temp = new String(bytes, offset, n_read);
					sz_out += sz_temp;
				}
				offset += n_read;
			}
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR: parsing TTS InputStream " + e.getMessage(), e);
			Error.getError().addError(PAS.l("common_error"),"Exception in parse",e,1);
			return new String("");
		}
		return sz_out;
	}
}