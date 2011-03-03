package no.ums.pas.send.messagelibrary;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Dimension;


public class MsgTree extends JDialog
{
	public MsgTree(JFrame parent)
	{
		super(parent);
		//setPreferredSize(new Dimension(300, 300));
		Dimension ul = no.ums.pas.ums.tools.Utils.screendlg_upperleft(300, 300);
		setBounds(ul.width, ul.height, 300, 300);
		setVisible(true);
	}
}