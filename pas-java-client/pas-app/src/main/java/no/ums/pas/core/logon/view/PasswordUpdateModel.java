package no.ums.pas.core.logon.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.ums.pas.core.logon.view.PasswordUpdate.PasswordStrength;
import no.ums.pas.localization.Localization;

import org.jdesktop.beansbinding.AbstractBean;


public class PasswordUpdateModel extends AbstractBean {
	public PasswordUpdateModel()
	{
		updatePasswordStrength();
	}
	private String oldpassword;
	private String newpassword;
	private String repeatnewpassword;
	private String strength = "0";

	public PasswordStrength updatePasswordStrength()
	{
		PasswordStrength ret = PasswordStrength.TOO_WEAK;
		String s = (getNewpassword()!=null ? getNewpassword() : "");
		String strength = Localization.l("mainmenu_update_password_too_weak");
		Pattern pEnough = Pattern.compile("[(?=.{6,}).*]");
		Pattern pMedium = Pattern.compile("^(?=.{7,})(((?=.*[A-Z])(?=.*[a-z]))|((?=.*[A-Z])(?=.*[0-9]))|((?=.*[a-z])(?=.*[0-9]))).*$");
		Pattern pStrong = Pattern.compile("^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$");
		Matcher m = pEnough.matcher(s);
		if(m.find())
		{
			strength = Localization.l("mainmenu_update_password_weak");
			ret = PasswordStrength.WEAK;
		}
		m = pMedium.matcher(s);
		if(m.find())
		{
			strength = Localization.l("mainmenu_update_password_medium");
			ret = PasswordStrength.MEDIUM;
		}
		m = pStrong.matcher(s);
		if(m.find())
		{
			strength = Localization.l("mainmenu_update_password_strong");
			ret = PasswordStrength.STRONG;
		}
		
		setStrength(strength);
		return ret;
	}
	
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		String oldValue = this.strength;
		this.strength = strength;
		update("strength", oldValue, this.strength);
	}
	public String getOldpassword() {
		return oldpassword;
	}
	public void setOldpassword(String oldpassword) {
		String oldValue = this.oldpassword;
		this.oldpassword = oldpassword;
		update("oldpassword", oldValue, this.oldpassword);
	}
	public String getNewpassword() {
		
		return newpassword;
	}
	public void setNewpassword(String newpassword) {
		String oldValue = this.newpassword;
		this.newpassword = newpassword;
		update("newpassword", oldValue, this.newpassword);
	}
	public String getRepeatnewpassword() {
		return repeatnewpassword;
	}
	public void setRepeatnewpassword(String repeatnewpassword) {
		String oldValue = this.repeatnewpassword;
		this.repeatnewpassword = repeatnewpassword;
		update("repeatnewpassword", oldValue, this.repeatnewpassword);
	}
	
	
}
