package no.ums.pas.send.sendpanels;

import javax.swing.JOptionPane;

import org.jdesktop.beansbinding.AbstractBean;

public class SendingResultsModel extends AbstractBean {
	
	private boolean answer;
	private String resText;


	
	boolean statusNotOpened = true;
	boolean statusOpened = false;
	

	public boolean getStatusNotOpened() {
		return statusNotOpened;
	}
	
	public boolean getStatusOpened() {
		return !statusNotOpened;
	}
	
	public void setStatusOpened(boolean statusOpened) {
		final boolean oldValue = this.statusOpened;
		this.statusOpened = statusOpened;
		update("statusOpened", oldValue, this.statusOpened);		
	}

	public void setStatusNotOpened(boolean statusNotOpened) {
		final boolean oldValue = this.statusNotOpened;
		this.statusNotOpened = statusNotOpened;
		update("statusNotOpened", oldValue, this.statusNotOpened);
		setStatusOpened(!statusNotOpened);
	}
	
	public SendingResultsModel() {
	}
	
	
	public boolean getAnswer() {
		return answer;
	}


	public void setAnswer(boolean answer) {
		Object oldValue = this.answer;
		this.answer = answer;
		update("answer", oldValue, answer);
	}


	public String getResText() {
		return resText;
	}


	public void setNoOption() {
		Object oldValue = getAnswer();
		this.answer = false;
		update("answer", oldValue, JOptionPane.NO_OPTION);
	}
	
	public void setYesOption() {
		Object oldValue = getAnswer();
		this.answer = true;
		update("answer", oldValue, JOptionPane.YES_OPTION);
	}
	
	public void setOkOption() {
		Object oldValue = getAnswer();
		this.answer = true;
		update("answer", oldValue, JOptionPane.OK_OPTION);
	}
	
	public void setResText(String resultText) {
		Object oldValue = this.resText;
		this.resText = resultText;
		update("resText", oldValue, this.resText);
	}
}
