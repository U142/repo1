package no.ums.pas.core.logon;

import javax.swing.JFrame;

import no.ums.pas.core.logon.view.Settings;

import org.junit.Test;

public class TestSettingsDlg {
	@Test
	public void testOpenDialog()
	{
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Settings(frm);
	}
}
