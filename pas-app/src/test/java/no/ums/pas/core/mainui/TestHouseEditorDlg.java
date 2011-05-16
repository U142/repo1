package no.ums.pas.core.mainui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointPix;
import no.ums.pas.maps.defines.Navigation;

import org.junit.Test;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class TestHouseEditorDlg implements ActionListener{
	//public void testHouseEditorDlg()
	public static void main(String [] args)
	{
		try
		{
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		InfoPanel info = new InfoPanel();
		HouseEditorDlg panel = new HouseEditorDlg(null, new JFrame(), null, null, null);
		JDialog dlg = new JDialog(new JFrame(), "House Editor Unit test");
		dlg.setSize(new Dimension(500, 800));
		dlg.setLocationRelativeTo(dlg.getRootPane());
		dlg.setLayout(new BorderLayout());
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.getContentPane().add(panel, BorderLayout.CENTER);
		dlg.setModal(true);
		dlg.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
