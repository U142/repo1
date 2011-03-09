package no.ums.pas.core.logon.view;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.namespace.QName;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordResult;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordUpdateComplete;
import no.ums.pas.core.ws.vars;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.UPASSWORDUPDATEREQUEST;
import no.ums.ws.common.UPASSWORDUPDATERESULT;
import no.ums.ws.pas.Pasws;



/**
 * 
 * @author Modda
 *
 * GUI for setting a new password.
 * Run ShowGUI to show dialog.
 */
public class PasswordUpdateCtrl implements PasswordUpdateComplete {

    private static final Log log = UmsLog.getLogger(PasswordUpdateCtrl.class);

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
	public void onAfterValidation(PasswordResult result, final PasswordUpdateModel bean) {
		switch(result)
		{
		case OK: //save changes
			try
			{
				new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						dlg.setEnabled(false);
						updatePassword(bean);
						dlg.setEnabled(true);				
						return Boolean.TRUE;
					}
					
				}.execute();
			}
			finally
			{
			}
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

	private void updatePassword(PasswordUpdateModel bean)
	{
		ULOGONINFO logon = new ULOGONINFO();
		logon.setLComppk(userinfo.get_comppk());
		logon.setLDeptpk(userinfo.get_current_department().get_deptpk());
		logon.setLUserpk(new Long(userinfo.get_userpk()));
		logon.setSzPassword(userinfo.get_passwd());
		logon.setSzUserid(userinfo.get_userid());
		logon.setSzCompid(userinfo.get_compid());
		logon.setSessionid(userinfo.get_sessionid());
		
		try
		{
			URL wsdl = new URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			UPASSWORDUPDATEREQUEST req = new UPASSWORDUPDATEREQUEST();
			req.setShaNew(Utils.encrypt(bean.getNewpassword()));
			req.setShaNewRepeat(Utils.encrypt(bean.getRepeatnewpassword()));
			req.setShaOld(Utils.encrypt(bean.getOldpassword()));
			req.setNewPassword(bean.getNewpassword());
			
			UPASSWORDUPDATERESULT res = new Pasws(wsdl, service).getPaswsSoap12().updatePassword(logon, req);
			switch(res)
			{
			case FAILED:
				break;
			case OK:
				userinfo.set_passwd(Utils.encrypt(bean.getNewpassword()));
				JOptionPane.showMessageDialog(dlg, PAS.l("mainmenu_update_password_ok"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
				dlg.setVisible(false);
				break;
			case TOO_WEAK:
				JOptionPane.showMessageDialog(dlg,PAS.l("mainmenu_update_password_too_weak"), PAS.l("common_error"), JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
		catch(Exception e)
		{
			log.error("Error updating password", e);
		}
		finally
		{
			//reset all info
			bean.setNewpassword("");
			bean.setOldpassword("");
			bean.setRepeatnewpassword("");
		}
	}
	
}
