package no.ums.pas.core.logon;

import javax.swing.JDialog;
import javax.swing.JFrame;

import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.view.Settings;
import no.ums.pas.core.logon.view.SettingsCtrl;

public class ShowSettingsDlg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Settings s = new Settings(frm);
		no.ums.pas.core.logon.Settings settings = new no.ums.pas.core.logon.Settings();
		MailAccount mail = new MailAccount();
		SettingsCtrl s = new SettingsCtrl(null, true, settings, mail);
		System.exit(0);
	}

}
