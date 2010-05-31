package no.ums.pas.parm.exception;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import no.ums.pas.PAS;


public class ParmException extends Exception{
	public static final long serialVersionUID = 1;
	public ParmException(){
		
	}
	
	public ParmException(String msg){
		super(msg);
		/*JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Point p = UMS.Tools.Utils.get_dlg_location_centered(0,0);
		p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
		frame.setLocation(p);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(frame,msg,"Warning!",JOptionPane.ERROR_MESSAGE);
		frame.dispose();*/
	}
}
