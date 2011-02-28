package no.ums.pas.ums.tools.colorpicker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicColorChooserUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public abstract class ColorPicker extends JColorChooser
{
	Component m_parent = null;
	String m_sz_title;
	Color m_col_init;
	//JColorChooser m_tcc;
	Color m_selected_color = null;
	ChangeListener m_listener;
	//PAS m_pas;
	JDialog m_dialog;
	public Component get_parent() { return m_parent; }
	
	public ColorPicker(String sz_title, Point p, Color init, Component parent)
	{
		super(init);
		//m_pas = pas;
		//m_tcc = new JColorChooser(init);
		setPreviewPanel(null);
		m_listener = new Change();
		
		m_parent = parent;
		setBounds(p.x, p.y, 50, 50);
		m_sz_title = sz_title;
		m_col_init = init;
		setUI(new BasicColorChooserUI());
		getSelectionModel().addChangeListener(m_listener);
		
	}
	public Color get_selected_color() { return m_selected_color; }
	public void show()
	{
		m_dialog = createDialog(m_parent, m_sz_title, true, this, new OnOk(), new OnCancel());
		m_dialog.setVisible(true);
	}
	class Change implements ChangeListener {
		public void stateChanged(ChangeEvent e)
		{
			m_selected_color = getColor();
			stateEvent(e);
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		m_selected_color = getColor();
		m_dialog.setVisible(false);
		onOk(e);
	}
	class OnOk implements ActionListener {
		public void actionPerformed(ActionEvent e) { onOk(e); }
	}
	class OnCancel implements ActionListener {
		public void actionPerformed(ActionEvent e) { onCancel(e); }
	}
	
	public void onCancel(ActionEvent e)
	{
		m_selected_color = new Color(m_col_init.getRGB(), false);
		stateEvent(new ChangeEvent(this));
		setVisible(false);
	}
	abstract public void onOk(ActionEvent e);
	abstract public void stateEvent(ChangeEvent e);
}

