package no.ums.pas.send;

import javax.swing.*;

public class ToggleAddresstype extends JToggleButton {
	public static final long serialVersionUID = 1;
	private int m_n_adrtype;
	public int get_adrtype() { return m_n_adrtype; }
	public ToggleAddresstype(ImageIcon icon, boolean b, int n_adrtype) {
		super(icon, b);
		m_n_adrtype = n_adrtype;
	}
}