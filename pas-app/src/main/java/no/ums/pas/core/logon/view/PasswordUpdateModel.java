package no.ums.pas.core.logon.view;

import org.jdesktop.beansbinding.AbstractBean;


public class PasswordUpdateModel extends AbstractBean {
	private String oldpassword;
	private String newpassword;
	private String repeatnewpassword;
	
	
	
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
