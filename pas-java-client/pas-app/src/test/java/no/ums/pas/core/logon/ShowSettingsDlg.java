package no.ums.pas.core.logon;

import javax.swing.JDialog;
import javax.swing.JFrame;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
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
		UserInfo ui = new UserInfo();
		UserProfile up = new UserProfile("", "", 2, 2, 2, 2, 2, 2, 2097471, 2, 0, 0, 0, 0,
							0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
		DeptInfo di = new DeptInfo(0, "", "", null, true, 1, 1, "", up, null, 1, 1, null);
		ui.get_departments().add(di);
		ui.set_current_department(di);
		Variables.setUserInfo(ui);
		no.ums.pas.core.logon.Settings settings = new no.ums.pas.core.logon.Settings();
		MailAccount mail = new MailAccount();
		Variables.setSettings(settings);
		SettingsCtrl s = new SettingsCtrl(null, true, settings, mail, ui);
		System.exit(0);
	}

}
