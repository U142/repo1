package no.ums.pas.core.menus.defines;

import no.ums.pas.PAS;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RadioItemList extends CheckItemList {
	public static final long serialVersionUID = 1;
	public RadioItemList(PAS pas, CheckItem [] items, int n_default_check, JMenu parent, 
			  	  ActionListener callback, String sz_actioncommand) {
		super(pas, items, n_default_check, parent, callback, sz_actioncommand);
	}
	
	public void action(ActionEvent e) {
		get_pas().get_mainmenu().actionPerformed(e);
	}
	/*public void set_default(CheckItem item) {
	}*/
}