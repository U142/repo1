package no.ums.pas.core.logon;

public class DeptCategory {

	private String label;
	private int value;
	
	public DeptCategory(int value,String label) {
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return ""+this.label;
	}
}