package no.ums.pas.core.logon.view;

import javax.swing.JOptionPane;

import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordResult;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordUpdateComplete;
import no.ums.pas.ums.tools.Utils;

import org.jdesktop.beansbinding.IBeanCtrl;


public class PasswordUpdateCtrl implements PasswordUpdateComplete {

	private final UserInfo userinfo;
	public PasswordUpdateCtrl(UserInfo ui)
	{
		userinfo = ui;
	}
	
	@Override
	public PasswordResult onOk(PasswordUpdateModel bean) {
		//save new password
		String shaOld = Utils.encrypt(bean.getOldpassword());
		String shaNew = Utils.encrypt(bean.getNewpassword());
		String shaNewRepeat = Utils.encrypt(bean.getRepeatnewpassword());
		if(!userinfo.get_passwd().equals(shaOld))
		{
			return PasswordResult.WRONG_PASSWORD;
		}
		if(!shaNew.equals(shaNewRepeat))
		{
			return PasswordResult.PASSWORD_MISMATCH;
		}
		return PasswordResult.OK;
	}

	@Override
	public void onCancel(PasswordUpdateModel bean) {
		
	}

	
}
