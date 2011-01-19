package no.ums.pas.core.popupmenus;

import no.ums.pas.PAS;

import javax.swing.*;
import java.awt.*;


public class PUMenu extends JPopupMenu {
	public static final long serialVersionUID = 1;
	int m_n_item = -1;
	//int m_n_id = -1;
	Object m_obj_id = null;
	private PAS m_pas;
	PAS get_pas() { return m_pas; }
	public void set_item(int n_item) { m_n_item = n_item; }
	public void set_id(Object obj_id) { m_obj_id = obj_id; }
	int get_item() { return m_n_item; }
	public Object get_id() { return m_obj_id; }
	Font m_font = null;
	public void set_font(Font f) { m_font = f; }
	
	
	
	public PUMenu(PAS pas)
	{
		this(pas, "");
	}
	public PUMenu(PAS pas, String sz_name)
	{
		super(sz_name);
		m_pas = pas;
		m_font = new Font("Arial", Font.BOLD, 10);
		set_font(m_font);
	}
	public void pop(Component comp, Point p)
	{
		try
		{
			this.show(comp, p.x, p.y);
		}
		catch(Exception e)
		{
			
		}
	}
	protected void set_layout() {
		for(int i=0; i < this.getSubElements().length; i++) {
			//FontSet ((JMenuItem)getSubElements()[i]).setFont(m_font);
		}
	}
}







