package no.ums.pas.core.menus.defines;

import no.ums.pas.PAS;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;


public abstract class CheckItemList extends ArrayList<Object> implements ItemListener, ActionListener {
	private ButtonGroup m_group;
	private ButtonGroup get_group() { return m_group; }
	private Object m_selected_value = null;
	protected JMenu m_parent = null;
	//private int m_n_selected_index = -1;
	private String m_sz_actioncommand;
	private void set_selected_value(Object value) { m_selected_value = value; }
	public Object get_selected_value() { return m_selected_value; }
	private CheckItem m_current_selection = null;
	private void set_current_selection(CheckItem item) { m_current_selection = item; }
	protected CheckItem get_current_selection() { return m_current_selection; }
	private PAS m_pas;
	PAS get_pas() { return m_pas; }
	String get_actioncommand() { return m_sz_actioncommand; } 
	
	
	public CheckItemList(PAS pas, CheckItem [] items, int n_default_check, JMenu parent, 
				  ActionListener callback, String sz_actioncommand) {
		super();
		m_pas = pas;
		m_sz_actioncommand = sz_actioncommand;
		for(int i=0; i < items.length; i++) {
			items[i].addActionListener(callback);
			add(items[i]);
		}
		m_parent = parent;
		add_items_to_menu();
	}
	public void init_click(int n_index) {
		//checked(n_index);
		//set_default((CheckItem)get(n_index));
		//actionPerformed(new ActionEvent())
		//((CheckItem)get(n_index)).doClick();
		((CheckItem)get(n_index)).setSelected(true);
		//ActionEvent e = new ActionEvent(get_current_selection(), ActionEvent.ACTION_PERFORMED, get_actioncommand());
		//get_pas().actionPerformed(e);
		
	}
	
	private void add_items_to_menu() {
		m_group = new ButtonGroup();
		for(int i=0; i < size(); i++)
		{
			CheckItem current = (CheckItem)get(i);
			m_parent.add(current);
			get_group().add(current);
			current.setActionCommand(get_actioncommand());
			current.addItemListener(this);
			if(current.getActionListeners().length == 0)
				current.addActionListener(this);
		}
	}	
	void add(CheckItem item) {
		super.add((Object)item);
	}
	protected void select(int n_index) {
		
	}
	protected void select(CheckItem item) {
		get_group().setSelected(item.getModel(), true);
	}
	private void checked(CheckItem item) {
		set_current_selection(item);
		set_selected_value(item.get_value());
	}
	void checked(int n_item) {
		for(int i=0; i < size(); i++)
		{
			if(n_item==i)
			{
				get_group().setSelected(((CheckItem)get(i)).getModel(), true);
				set_current_selection((CheckItem)get(i));
			}
		}
	}
	void return_changed() {

	}
	public synchronized void itemStateChanged(ItemEvent e)
	{
		if(e.getStateChange()==ItemEvent.SELECTED)
			checked((CheckItem)e.getItem());
	}
	public synchronized void actionPerformed(ActionEvent e)
	{
		action(e);
	}
	public synchronized void enable_all(boolean b_enable) {
		Enumeration<AbstractButton> e = get_group().getElements();
		while(e.hasMoreElements()) {
			((CheckItem)e.nextElement()).setEnabled(b_enable);
		}
	}
	public abstract void action(ActionEvent e);
	public void set_default(CheckItem item) {
		item.doClick();
	}
}