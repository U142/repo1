package no.ums.pas.send.messagelibrary;

import no.ums.pas.PAS;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.tree.UMSTree;
import no.ums.pas.core.ws.WSMessageLibEdit;
import no.ums.pas.send.messagelibrary.tree.MessageLibNode;
import no.ums.pas.sound.SoundRecorderPanel;
import no.ums.pas.sound.SoundTextTemplatePanel;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.ArrayOfUCCMessage;
import no.ums.ws.pas.UBBMESSAGE;
import no.ums.ws.pas.UCCMessage;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageEditPanel extends DefaultPanel implements ComponentListener, KeyListener, ItemListener
{
	protected boolean b_enable_multi_cc = false;
	protected boolean b_editor_mode = false;
	public boolean isEditorMode()
	{
		return b_editor_mode;
	}
	Pattern GSM_Alphabet_Regex = Pattern.compile("[|^{}\\[\\]~\\\\");
	protected ActionListener callback;
	protected MessageLibNode m_msg = null;
	public MessageLibNode getActiveMessage() { return m_msg; }
	private JTabbedPane m_tabbedpane;
	protected JButton btn_new_lang = new JButton(PAS.l("common_new"));
	protected JButton btn_save = new JButton(PAS.l("common_save"));
	protected JButton btn_cancel = new JButton(PAS.l("common_cancel"));
	protected JButton btn_select = new JButton(PAS.l("common_select"));
	protected StdTextLabel lbl_counter = new StdTextLabel("",120);
	private SoundRecorderPanel m_rec;
	//private SoundTTSPanel m_tts;
	//private SoundOpenPanel m_open;
	private SoundTextTemplatePanel m_template;
	
	public MessageEditPanel(ActionListener callback)
	{
		this(callback, false, true);
	}
	
	public MessageEditPanel(ActionListener callback, boolean b_editor_mode, boolean b_enable_multi_cc)
	{
		super();
		this.b_editor_mode = b_editor_mode;
		this.b_enable_multi_cc = b_enable_multi_cc;
		this.callback = callback;
		btn_save.setEnabled(false);
		btn_cancel.setEnabled(!isEditorMode());
		btn_new_lang.setEnabled(false);
		btn_select.setEnabled(false);
		
		addComponentListener(this);
		m_tabbedpane = new JTabbedPane();
		m_tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//Substance 3.3
		m_tabbedpane.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_VERTICAL_ORIENTATION, Boolean.TRUE);
		try {
			/*boolean rec = true;
			m_rec = new SoundRecorderPanel(this, StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), SoundRecorder.RECTYPE_OUTPUTSTREAM); //, parent); //get_parent().get_sendcontroller());
			m_rec.setPreferredSize(new Dimension(100, 100));
			m_rec.showMixer();
			try {
				m_rec.get_recorder().get_recorder().init_oscilliator();
			}
			catch(Exception npe) {
				rec = false;
			}*/
			m_template = new SoundTextTemplatePanel(this);
			
			//m_tabbedpane.addTab(PAS.l("main_sending_audio_type_record"), ImageLoader.load_and_scale_icon("mic_transparent.gif", 15, 15), m_rec, PAS.l("main_sending_audio_type_record_tooltip"));
			m_tabbedpane.addTab(PAS.l("main_sending_text_template"), null, m_template, null);
		}
		catch(Exception e)
		{
			
		}
		
		for(int i=0; i<CountryCodes.getCountryCodes().size();++i)
			cbx_lang.addItem(CountryCodes.getCountryCodes().get(i));
		cbx_lang.addActionListener(this);
		cbx_lang.addItemListener(this);
		btn_save.addActionListener(this);
		btn_cancel.addActionListener(this);
		btn_select.addActionListener(this);
		add_controls();
		init();
		txt_name.addKeyListener(this);
	}
	
	protected StdTextLabel lbl_name = new StdTextLabel(PAS.l("common_name"), 150);
	protected StdTextArea txt_name = new StdTextArea("", false, new Dimension(150,20));
	protected StdTextLabel lbl_lang = new StdTextLabel("Language", 150);
	protected cccombo cbx_lang = new cccombo();
	
	public boolean isActiveMessage(UBBMESSAGE msg)
	{
		if(getActiveMessage()==null || msg==null)
			return false;
		if(getActiveMessage().getMessage().getNMessagepk()==msg.getNMessagepk())
			return true;
		return false;
	}
	
	public void setActiveMessage(MessageLibNode msg)
	{
		m_msg = msg;
		//txt_name.setEnabled(true);
		enableInputs(true);
		
		if(msg==null)
		{
			//this.setVisible(false);
			if(isEditorMode())
				btn_cancel.setEnabled(false);
			btn_save.setEnabled(false);
			btn_select.setEnabled(false);
			txt_name.setText("");
			m_template.setTextMessage("");
			return;
		}
		else
			this.setVisible(true);
		txt_name.setText(msg.getMessage().getSzName());
		txt_name.grabFocus();
		if(!msg.getIsSaved())
		{
			txt_name.setSelectionStart(0);
			txt_name.setSelectionEnd(txt_name.getText().length());
		}
		if(msg.getMessage().getFTemplate()==1)
		{
			m_tabbedpane.setSelectedComponent(m_template);
			m_template.setTextMessage(getTextByCC((CCode)cbx_lang.getSelectedItem()));
		}
		if(!msg.getIsSaved())
		{
			if(isEditorMode())
				btn_cancel.setEnabled(true);
			btn_save.setEnabled(true);
			btn_new_lang.setEnabled(true);
			btn_select.setEnabled(false);
		}
		else
		{
			if(isEditorMode())
				btn_cancel.setEnabled(false);
			btn_save.setEnabled(false);
			btn_new_lang.setEnabled(false);
			//if(msg.isLeaf())
				btn_select.setEnabled(true);
			//else
			//	btn_select.setEnabled(false);
		}
		
		/*else if(msg.getFTemplate()==0)
		{
			m_tabbedpane.setSelectedComponent(m_rec);
			
		}*/
		set_size_label(m_msg.getMessage().getSzMessage(),lbl_counter);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(m_msg!=null) {
			m_msg.setIsSaved(false);
			if(isEditorMode())
				btn_cancel.setEnabled(true);
			btn_save.setEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btn_save)) //save
		{
			if(m_msg!=null)
			{
				//txt_name.setEnabled(false);
				enableInputs(false);
				if(isEditorMode())
					btn_cancel.setEnabled(false);
				btn_save.setEnabled(false);
				saveCurrent((CCode)cbx_lang.getSelectedItem());
				m_msg.getMessage().setSzName(txt_name.getText());
				WSMessageLibEdit save = new WSMessageLibEdit(this, m_msg.getMessage());
				save.start();
			}
		}
		else if(e.getSource().equals(btn_cancel))
		{
			if(isEditorMode())
			{
				m_msg.setIsSaved(true);
				setActiveMessage(getActiveMessage());
			}
			else
			{
				callback.actionPerformed(new ActionEvent("Cancel", ActionEvent.ACTION_PERFORMED, MessageLibDlg.ACT_MESSAGE_SELECTION_CANCELLED));
			}
		}
		else if(e.getSource().equals(btn_select))
		{
			if(m_msg!=null) {
				callback.actionPerformed(new ActionEvent(m_msg, ActionEvent.ACTION_PERFORMED, MessageLibDlg.ACT_MESSAGE_SELECTED));
			}
		}
		else if("act_msg_content_changed".equals(e.getActionCommand()))
		{			
			if(m_msg!=null) {
				if(isEditorMode())
					btn_cancel.setEnabled(true);
				btn_save.setEnabled(true);
				m_msg.setIsSaved(false);
			}
		}
		else if("act_msg_set_count".equals(e.getActionCommand())) {
			Matcher m = GSM_Alphabet_Regex.matcher(m_template.getTextMessage());
			int ext = 0;
			while(m.find() == true)
				++ext;
			set_size_label(m_template.getTextMessage(), lbl_counter);	
		}
		else if("act_messagelib_saved".equals(e.getActionCommand()))
		{
			UBBMESSAGE saved = (UBBMESSAGE)e.getSource();
			if(saved.getNMessagepk()>0)
			{
				m_msg.setNodeObject(saved);
				m_msg.setIsSaved(true);
				
			}
			setActiveMessage(m_msg);
			callback.actionPerformed(new ActionEvent(m_msg, ActionEvent.ACTION_PERFORMED, UMSTree.TREE_NODEVALUE_CHANGED));
		}
	}

	@Override
	public void add_controls() {
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_name, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(txt_name, m_gridconst);
		if(b_enable_multi_cc)
		{
			set_gridconst(0, inc_panels(), 1, 1);
			add(lbl_lang, m_gridconst);
			cbx_lang.setPreferredSize(new Dimension(150,20));
			set_gridconst(1, get_panel(), 1, 1);
			add(cbx_lang, m_gridconst);
		}
		set_gridconst(0, inc_panels(), 3, 1);
		add(m_tabbedpane, m_gridconst);
		set_gridconst(0, inc_panels(), 1, 1);
		if(isEditorMode())
		{
			add(btn_save, m_gridconst);
		}
		else
		{
			add(btn_select, m_gridconst);
		}
		set_gridconst(1, get_panel(), 1, 1);
		add(btn_cancel, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);
		add(lbl_counter, m_gridconst);
	}
	
	public void enableInputs(boolean b)
	{
		m_template.setTextEnabled(b && isEditorMode());
		txt_name.setEnabled(b && isEditorMode());
	}

	@Override
	public void init() {
		setVisible(true);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		if(w<=0 || h<=0)
		{
			super.componentResized(e);
			return;
		}
		//m_tabbedpane.setPreferredSize(new Dimension(w-20, h-80));
		//m_template.setPreferredSize(new Dimension(w-20, h-90));
		m_template.setPreferredSize(new Dimension(w,h-100));
		m_tabbedpane.setPreferredSize(new Dimension(w,h-100));
		m_tabbedpane.invalidate();
		m_template.invalidate();
		//m_tabbedpane.revalidate();
		super.componentResized(e);
	}
	
	public void set_size_label(String text, StdTextLabel activeLabel) {
		
		Matcher m = GSM_Alphabet_Regex.matcher(text);
		int ext = 0;
		while(m.find() == true)
			++ext;
		//System.out.println("Extended chars = " + ext);
		
		activeLabel.setText(String.format(PAS.l("main_message_library_counter"), text.length() + ext));
		
		
			
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if(m_msg != null) {
			if(e.getStateChange() == e.DESELECTED && e.getSource().equals(cbx_lang)) {
				saveCurrent((CCode)cbx_lang.getPreviouslySelected());
			}
			else if(e.getStateChange() == e.SELECTED && e.getSource().equals(cbx_lang)) {
				int ccmsgcount = m_msg.getMessage().getCcmessage().getUCCMessage().size();
				
				for(int i=0;i<ccmsgcount;++i)
					if(m_msg.getMessage().getCcmessage().getUCCMessage().get(i).getLCc() ==((CCode)cbx_lang.getSelectedItem()).getNCCode()) {
						m_template.setTextMessage(m_msg.getMessage().getCcmessage().getUCCMessage().get(i).getSzMessage());
						return;
					}
				
				m_template.setTextMessage(m_msg.getMessage().getSzMessage());
			}
		}
	}
	
	private void saveCurrent(CCode cc) {
		if(m_template.getTextMessage().length()>0) {
			if(b_enable_multi_cc)
			{
				int cccount = m_msg.getMessage().getCcmessage().getUCCMessage().size();
				boolean found = false;
				
				for(int i=0;i<cccount;++i) {
					if(cc.getNCCode() == m_msg.getMessage().getCcmessage().getUCCMessage().get(i).getLCc()){	
						m_msg.getMessage().getCcmessage().getUCCMessage().get(i).setSzMessage(m_template.getTextMessage());
						found = true;
					}
				}
				if(!found && cc.getNCCode() != -1 && !m_msg.getMessage().getSzMessage().equals(m_template.getTextMessage())) {
					UCCMessage ccmsg = new UCCMessage();
					ccmsg.setLCc(cc.getNCCode());
					ccmsg.setSzMessage(m_template.getTextMessage());
					m_msg.getMessage().getCcmessage().getUCCMessage().add(ccmsg);
				}
				else if(!found && cc.getNCCode() == -1) {
					m_msg.getMessage().setSzMessage(m_template.getTextMessage());
				}
			}
			else
			{
				m_msg.getMessage().setSzMessage(m_template.getTextMessage());
				UCCMessage ccmsg = new UCCMessage();
				ccmsg.setLCc(-1);
				ccmsg.setSzMessage(m_template.getTextMessage());
				ArrayOfUCCMessage arr = new ArrayOfUCCMessage();
				arr.getUCCMessage().add(ccmsg);
				/*UBBMESSAGE msg = new UBBMESSAGE();
				msg.setCcmessage(arr);*/
				m_msg.getMessage().setCcmessage(arr);
				//m_msg.getMessage().getCcmessage().getUCCMessage().add(ccmsg);
			}
		}
		else {
			// Remove 
			try
			{
				int cccount = m_msg.getMessage().getCcmessage().getUCCMessage().size();
				for(int i=0;i<cccount;++i) {
					if(cc.getNCCode() == m_msg.getMessage().getCcmessage().getUCCMessage().get(i).getLCc()){	
						m_msg.getMessage().getCcmessage().getUCCMessage().remove(i);
					}
				}
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	private String getTextByCC(CCode cc) {
		if(b_enable_multi_cc)
		{
			try
			{
				int cccount = m_msg.getMessage().getCcmessage().getUCCMessage().size();
				
				for(int i=0;i<cccount;++i) {
					if(cc.getNCCode() == m_msg.getMessage().getCcmessage().getUCCMessage().get(i).getLCc()){	
						return m_msg.getMessage().getCcmessage().getUCCMessage().get(i).getSzMessage();
					}
				}
			}
			catch(Exception e)
			{
				
			}
		}
		return  m_msg.getMessage().getSzMessage();
	}
	
}

class cccombo extends JComboBox {
	public CCode getPreviouslySelected() {
		return (CCode)selectedItemReminder;
	}
}