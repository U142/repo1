package no.ums.pas.core.logon;

import no.ums.pas.core.logon.view.PasswordUpdateCtrl;
import no.ums.pas.core.logon.view.PasswordUpdateModel;
import no.ums.pas.core.logon.view.PasswordUpdate.PasswordResult;
import no.ums.pas.ums.tools.Utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PasswordUpdateCtrlTest {
	UserInfo ui = new UserInfo();

	@Before
	public void onStart()
	{
		ui.set_passwd(Utils.encrypt("testæøåÆØÅ123"));
	}
	@Test
	public void testWrongPassword()
	{
		PasswordUpdateCtrl ctrl = new PasswordUpdateCtrl(ui);
		PasswordUpdateModel model = new PasswordUpdateModel();
		model.setOldpassword("testæøå12");
		assertThat(ctrl.onValidation(model), equalTo(PasswordResult.WRONG_PASSWORD));
	}
	
	@Test
	public void testWrongRepeat()
	{
		PasswordUpdateCtrl ctrl = new PasswordUpdateCtrl(ui);
		PasswordUpdateModel model = new PasswordUpdateModel();
		
		model.setOldpassword("testæøåÆØÅ123");
		model.setNewpassword("testæøå");
		model.setRepeatnewpassword("ÆØÅ");
		assertThat(ctrl.onValidation(model), equalTo(PasswordResult.PASSWORD_MISMATCH));
	}
	
	@Test
	public void testOkPassword()
	{
		PasswordUpdateCtrl ctrl = new PasswordUpdateCtrl(ui);
		PasswordUpdateModel model = new PasswordUpdateModel();
		
		model.setOldpassword("testæøåÆØÅ123");
		model.setNewpassword("'\"#¤%&/()=?`\\**");
		model.setRepeatnewpassword("'\"#¤%&/()=?`\\**");
		assertThat(ctrl.onValidation(model), equalTo(PasswordResult.OK));		
	}
}
