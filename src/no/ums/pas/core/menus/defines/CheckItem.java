package no.ums.pas.core.menus.defines;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.*;

/*class ToolTipMng extends ToolTipManager
{
	public ToolTipMng()
	{
	}
}*/


public class CheckItem extends JCheckBoxMenuItem {
	public static final long serialVersionUID = 1;
	private Object m_value, m_value2;
	public Object get_value() { return m_value; }
	public Object get_value2() { return m_value2; }
	public void set_value(Object value) { m_value = value; }
	public void set_value2(Object value) { m_value2 = value; }
	private java.awt.Color tooltip_bg = null;
	
	public CheckItem(String sz_text, Object value, boolean b_init_select) {
		super(sz_text);
		set_value(value);
		this.setSelected(b_init_select);
	}
	public CheckItem(String sz_text, Object value, boolean b_init_select, String sz_tooltip) {
		this(sz_text, value, b_init_select, sz_tooltip, null);
	}
	public CheckItem(String sz_text, Object value, boolean b_init_select, String sz_tooltip, java.awt.Color tooltip_bg) {
		super(sz_text);
		set_value(value);
		this.setSelected(b_init_select);
		this.setToolTipText("<html>" + sz_tooltip + "</html>");
		this.tooltip_bg = tooltip_bg;
	}
	public CheckItem(String sz_text, Object value, Object value2, boolean b_init_select, String sz_tooltip, java.awt.Color tooltip_bg) {
		this(sz_text, value, b_init_select, sz_tooltip, tooltip_bg);
		set_value2(value2);
	}
	public JToolTip createToolTip()
	{
		JToolTip tip = super.createToolTip();
		if(tooltip_bg!=null)
			tip.setBackground(tooltip_bg);
		//tip.setBackground(java.awt.Color.YELLOW);
		//tip.set
		return tip;
	}
	public CheckItem(String sz_text, Object value, boolean b_init_select, Icon ico) {
		super(sz_text);
		set_value(value);
		this.setSelected(b_init_select);
		if(ico!=null)
			this.setIcon(ico);
	}
}