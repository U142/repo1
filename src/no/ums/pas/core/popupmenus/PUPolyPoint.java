package no.ums.pas.core.popupmenus;

import javax.swing.*;

import no.ums.pas.*;

import java.awt.event.*;

public class PUPolyPoint extends PUMenu {
	public static final long serialVersionUID = 1;
	ActionListener m_actionlistener;
	JMenuItem item_insert	= new JMenuItem("Insert point");
	JMenuItem item_delete	= new JMenuItem("Delete point");
	JMenuItem item_move		= new JMenuItem("Move point");
	JMenuItem item_reverse	= new JMenuItem("Reverse order");	
	public PUPolyPoint(PAS pas, String sz_name, ActionListener actionlistener) {
		super(pas, sz_name);
		m_actionlistener = actionlistener;

		item_insert.setActionCommand("act_insat_polypoint");
		item_delete.setActionCommand("act_delat_polypoint");
		item_move.setActionCommand("act_moveat_polypoint");
		item_reverse.setActionCommand("act_reverse_polypoints");
		item_insert.setMnemonic('i');
		item_delete.setMnemonic('d');
		item_move.setMnemonic('m');
		item_reverse.setMnemonic('r');
		item_insert.addActionListener(m_actionlistener);
		item_delete.addActionListener(m_actionlistener);
		item_move.addActionListener(m_actionlistener);
		item_reverse.addActionListener(m_actionlistener);
		add(item_insert);
		add(item_delete);
		add(item_move);
		add(item_reverse);
		set_layout();
		super.pack();
	}
	public void enable_insert(boolean b) {
		item_insert.setEnabled(b);
	}

}