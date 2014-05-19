package no.ums.pas.send;

public class RecipientChannel {
	private String label;
	private int value;
	public RecipientChannel(String label,int value) {
		this.label = label;
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	@Override
	public String toString() {
		return label;
	}
}
