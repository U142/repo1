package no.ums.pas.core.menus.defines;

import no.ums.pas.PAS;

import javax.swing.JMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SubstanceMenuItemList extends CheckItemList {
	public static final long serialVersionUID = 1;
	public SubstanceMenuItemList(PAS pas, CheckItem [] items, int n_default_check, JMenu parent, 
		  	  ActionListener callback, String sz_actioncommand, int n_select) {
		super(pas, items, n_default_check, parent, callback, sz_actioncommand);
		init_click(n_select);
		
	}
	public void action(ActionEvent e) {
		//get_pas().get_mainmenu().actionPerformed(e);
	}
	/*public void set_default(CheckItem item) {
		//action(new ActionEvent(((SubstanceMenuItem)item), ActionEvent.ACTION_PERFORMED, get_actioncommand()));
		item.doClick();
	}*/	
}

