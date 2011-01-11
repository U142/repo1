package no.ums.pas.core.menus.defines;

import java.awt.event.*;
import javax.swing.*;

import no.ums.pas.PAS;

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

