package no.ums.pas.core.logon;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import no.ums.pas.core.logon.view.PasswordUpdate;
import no.ums.pas.core.logon.view.PasswordUpdateCtrl;
import no.ums.pas.core.logon.view.PasswordUpdateModel;
import no.ums.pas.core.logon.view.Settings;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordResult;
import no.ums.pas.ums.tools.Utils;


public class ShowPasswordChangeDlg {
	public static void main(String[] args) {
		/*final PasswordUpdate dlg = new PasswordUpdate(new PasswordUpdate.PasswordUpdateComplete() {
			
			@Override
			public PasswordResult onOk(PasswordUpdateModel model) {
				//save new password
				String shaOld = Utils.encrypt(model.getOldpassword());
				String shaNew = Utils.encrypt(model.getNewpassword());
				String shaNewRepeat = Utils.encrypt(model.getRepeatnewpassword());
				if(!shaNew.equals(shaNewRepeat))
				{
					JOptionPane.showMessageDialog(null, "new and repeat doesn't match");
				}
				return PasswordResult.OK;
			}
			
			@Override
			public void onCancel(PasswordUpdateModel model) {
				//do nothing
			}
		});
		dlg.setModal(true);
		dlg.setVisible(true);*/
		UserInfo ui = new UserInfo();
		ui.set_passwd(Utils.encrypt("æøåÆØÅ"));
		new PasswordUpdateCtrl(ui).ShowGUI(true);
		System.exit(0);
	}
}
