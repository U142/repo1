package no.ums.pas.core.logon;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.TreeSelectionEvent;

import no.ums.pas.core.logon.view.Settings;
import no.ums.pas.core.logon.view.Settings.SettingsUpdate;
import no.ums.pas.core.logon.view.SettingsModel;

import org.junit.Test;

public class TestSettingsDlg {
	@Test
	public void testOpenDialog()
	{
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Settings(frm, new SettingsUpdate() {
			
			@Override
			public void onMoveLayerUp(WmsLayerTree tree) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMoveLayerDown(WmsLayerTree tree) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onOk(WmsLayerTree tree, SettingsModel model) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onWmsLayerSelect(WmsLayerTree tree, TreeSelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onOpenWmsSite(WmsLayerTree tree, String wmsUrl,
					String wmsUser, String wmsPassword, JComboBox imageformats) {
				
			}


			@Override
			public void onMapWmsSelected(boolean b) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
