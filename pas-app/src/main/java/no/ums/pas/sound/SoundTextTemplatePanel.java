package no.ums.pas.sound;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.send.sendpanels.Sending_Cell_Broadcast_text;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class SoundTextTemplatePanel extends DefaultPanel implements KeyListener
{
	ActionListener callback;
	JTextArea m_txt_messagetext;
	private JScrollPane m_sp_messagetext;
	Sending_Cell_Broadcast_text text_editor;
	public String getTextMessage() {
		return text_editor.getTextArea().getText();
	}
	public void setTextEnabled(boolean b)
	{
		text_editor.get_txt_messagetext().setEnabled(b);
	}
	public void setTextMessage(String s)
	{
		if(s==null)
			s = "";
		text_editor.getTextArea().setText(s);
		text_editor.resetHighLights();
		text_editor.getTextArea().addKeyListener(this);
	}
	
	public SoundTextTemplatePanel(ActionListener callback)
	{
		super();
		addComponentListener(this);
		this.callback = callback;
		text_editor = new Sending_Cell_Broadcast_text(null);
		text_editor.getTextArea().addKeyListener(text_editor);
		//text_editor.addCellFocusListener();
		text_editor.getTextArea().setHighlighter(new DefaultHighlighter());
		
		m_txt_messagetext = new JTextArea(10, 35);
		m_txt_messagetext = new JTextArea(35,30);
		m_sp_messagetext = new JScrollPane(m_txt_messagetext);
		m_sp_messagetext.setPreferredSize(new Dimension(200, 70));
		m_txt_messagetext.setLineWrap(true);
		m_txt_messagetext.setWrapStyleWord(true);

		add_controls();
		init();
		setTextMessage(null);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {
		set_gridconst(0, 0, 1, 1);
		add(text_editor.getTextScroller(), m_gridconst);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		text_editor.getTextScroller().setPreferredSize(new Dimension(getWidth(), getHeight()));
		super.componentResized(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_msg_content_changed"));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_msg_set_count"));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}