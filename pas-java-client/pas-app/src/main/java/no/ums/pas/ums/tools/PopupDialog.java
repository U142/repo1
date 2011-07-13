package no.ums.pas.ums.tools;

import no.ums.pas.PAS;

import javax.swing.JFrame;
import java.awt.Point;

public final class PopupDialog {
	public synchronized static JFrame get_frame() {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
		p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
		frame.setLocation(p);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		return frame;
	}
}
