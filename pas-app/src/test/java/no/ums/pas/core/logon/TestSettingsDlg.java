package no.ums.pas.core.logon;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.TreeSelectionEvent;

import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.view.Settings;
import no.ums.pas.core.logon.view.Settings.ISettingsUpdate;
import no.ums.pas.core.logon.view.SettingsModel;

import org.junit.Test;

public class TestSettingsDlg {
	@Test
	public void testOpenDialog()
	{
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UserInfo ui = new UserInfo();
		UserProfile up = new UserProfile("", "", 2, 2, 2, 2, 2, 2, 2, 2);
		ui.set_current_department(new DeptInfo(0, "", "", null, true, 1, 1, "", up, null, 1, 1, null));
		Variables.setUserInfo(ui);
		new Settings(frm, new ISettingsUpdate() {
			
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
