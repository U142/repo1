package no.ums.pas.core.mainui.address_search;

import java.util.Locale;

import javax.swing.JFrame;

import no.ums.pas.localization.Localization;

public class ShowAddressSearch {
	public static void main(String [] args)
	{
		Localization.INSTANCE.setLocale(new Locale("no", "NO"));
		AddressSearchCtrl dlg = new AddressSearchCtrl();
		dlg.showGUI();
		dlg.getDlg().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
