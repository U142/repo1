package no.ums.pas.core.defines;

import java.awt.event.ActionEvent;

import no.ums.pas.*;
import no.ums.pas.ums.tools.StdTextLabel;

public class LightPanel extends DefaultPanel {
	public static final long serialVersionUID = 1;
	public static final int DIR_HORIZONTAL	= 0;
	public static final int DIR_VERTICAL	= 1;

	//final PAS m_pas;
	//public final PAS get_pas() { return m_pas; }
	int m_n_panels = 0;
	public int inc_panels() { m_n_panels++; return m_n_panels; }
	public int get_panel() { return m_n_panels; }
	//private JTextArea m_txt_spacing_horizontal = new JTextArea("");
	private StdTextLabel m_txt_spacing   = new StdTextLabel("");

	public LightPanel()
	{
		super();
	}
	
	/*public LightPanel(PAS pas) {
		super();
	}*/
	
	public void actionPerformed(ActionEvent e) {
		
	}
	public void add_controls() {
		
	}
	public void init() {
		
	}
	
}