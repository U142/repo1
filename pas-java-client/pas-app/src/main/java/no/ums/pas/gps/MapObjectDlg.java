package no.ums.pas.gps;

import no.ums.pas.PAS;
import no.ums.pas.maps.defines.MapObject;

import javax.swing.JDialog;
import java.awt.BorderLayout;


public class MapObjectDlg extends JDialog {
	public static final long serialVersionUID = 1;
	private PAS m_pas;
	public PAS get_pas() { return m_pas; }
	
	public MapObjectDlg(PAS pas, MapObject obj) {
		super(pas);
		m_pas = pas;
		setLayout(new BorderLayout());
		setModal(false);
		setAlwaysOnTop(true);
		setBounds(500, 500, 500, 450);
		add(new MapObjectReg(this, pas, obj));
		setTitle("Map Object Registration");
		//setResizable(false);
		
		setVisible(true);
		
	}
	
}
