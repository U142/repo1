package no.ums.pas.core.logon.view;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordResult;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordUpdateComplete;
import no.ums.pas.ums.tools.Utils;



/**
 * 
 * @author Modda
 *
 * GUI for setting a new password.
 * Run ShowGUI to show dialog.
 */
public class PasswordUpdateCtrl implements PasswordUpdateComplete {

	private final UserInfo userinfo;
	private final PasswordUpdate dlg = new PasswordUpdate(this);
	
	
	public PasswordUpdateCtrl(UserInfo ui)
	{
		userinfo = ui;
	}
	
	public void ShowGUI(boolean modal, JComponent parent)
	{		
		dlg.setLocationRelativeTo(parent);
		dlg.setModal(modal);
		dlg.setVisible(true);
	}
	
	@Override
	public PasswordResult onValidation(PasswordUpdateModel bean) {
		//save new password
		String shaOld = Utils.encrypt(bean.getOldpassword());
		String shaNew = Utils.encrypt(bean.getNewpassword());
		String shaNewRepeat = Utils.encrypt(bean.getRepeatnewpassword());
		if(bean.getOldpassword()==null || bean.getOldpassword().length()<=0)
		{
			return PasswordResult.PASSWORD_EMPTY;
		}
		if(!userinfo.get_passwd().equals(shaOld))
		{
			return PasswordResult.WRONG_PASSWORD;
		}
		if(bean.getNewpassword()==null || bean.getNewpassword().length()<=0)
		{
			return PasswordResult.PASSWORD_EMPTY;
		}
		if(bean.getRepeatnewpassword()==null || bean.getRepeatnewpassword().length()<=0)
		{
			return PasswordResult.PASSWORD_EMPTY;
		}
		if(!shaNew.equals(shaNewRepeat))
		{
			return PasswordResult.PASSWORD_MISMATCH;
		}
		return PasswordResult.OK;
	}

	@Override
	public void onCancel(PasswordUpdateModel bean) {
		System.out.println("user clicked cancel");
	}

	@Override
	public void onAfterValidation(PasswordResult result) {
		System.out.println(result.toString());
		switch(result)
		{
		case OK: //save changes
			break;
		case PASSWORD_EMPTY:
			JOptionPane.showMessageDialog(dlg, PAS.l("mainmenu_update_password_empty_password"));
			break;
		case PASSWORD_MISMATCH:
			JOptionPane.showMessageDialog(dlg, PAS.l("mainmenu_update_password_password_mismatch"));
			break;
		case WRONG_PASSWORD:
			JOptionPane.showMessageDialog(dlg, PAS.l("mainmenu_update_password_wrong_password"));
			break;
		}
	}

	
}
