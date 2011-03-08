package no.ums.pas.core.logon;

import javax.swing.JDialog;
import javax.swing.JFrame;

import no.ums.pas.core.logon.view.Settings;

public class ShowSettingsDlg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Settings s = new Settings(frm);
		s.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		s.settingsModel1.setLbaupdate("20");
		s.setVisible(true);
		System.exit(0);
	}

}
