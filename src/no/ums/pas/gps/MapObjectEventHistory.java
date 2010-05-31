package no.ums.pas.gps;

import java.awt.event.ActionEvent;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;

public class MapObjectEventHistory extends DefaultPanel {
	public static final long serialVersionUID = 1;
	private MapObjectReg m_reg;
	public MapObjectEventHistory(MapObjectReg reg) {
		super();
		m_reg = reg;
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public void add_controls() {
		
	}
	public void init() {
		setVisible(true);		
	}	
}
