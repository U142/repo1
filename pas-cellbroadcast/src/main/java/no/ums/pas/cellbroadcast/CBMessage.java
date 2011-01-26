package no.ums.pas.cellbroadcast;

import java.util.ArrayList;

public class CBMessage {

	private ArrayList<CCode> ccodes;
	private String messageName;
	private String message;
	private String cboadc;
	
	public CBMessage(String messagename, ArrayList<CCode> ccodes, String message, String cboadc) {
		messageName = messagename;
		this.ccodes = ccodes;
		this.message = message;
		this.cboadc = cboadc;
	}
	
    public ArrayList<CCode> getCcodes() {
		return ccodes;
	}

	public void setCcodes(ArrayList<CCode> ccodes) {
		this.ccodes = ccodes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String toString() {
		return messageName;
	}

	public String getCboadc() {
		return cboadc;
	}

	public void setCboadc(String cboadc) {
		this.cboadc = cboadc;
	}
	
}
