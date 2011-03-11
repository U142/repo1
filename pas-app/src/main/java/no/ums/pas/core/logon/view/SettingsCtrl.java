package no.ums.pas.core.logon.view;

import java.awt.Component;
import java.util.List;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;

import org.geotools.data.ows.Layer;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.view.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.logon.WmsLayerTree;
import no.ums.pas.core.logon.view.Settings.SettingsUpdate;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.maps.MapLoader;

public class SettingsCtrl implements SettingsUpdate {
    private static final Log log = UmsLog.getLogger(SettingsCtrl.class);
    
    private final Settings dlg;

    public SettingsCtrl(Component parent, boolean modal, no.ums.pas.core.logon.Settings settings, MailAccount mailaccount)
    {
    	dlg = new Settings(null, this);
    	dlg.setIconImage(ImageFetcher.getImage("pas_appicon_16.png"));
    	initializeGui(settings, mailaccount);
    	dlg.setLocationRelativeTo(parent);
    	dlg.setModal(modal);
    	dlg.setVisible(true);
    }
    
    public void initializeGui(no.ums.pas.core.logon.Settings s, MailAccount a)
    {
    	dlg.settingsModel1.setAutoStartParm(s.parm());
    	dlg.settingsModel1.setCompanyid(s.getCompany());
    	dlg.settingsModel1.setEmailAddress(a.get_mailaddress());
    	dlg.settingsModel1.setEmailDisplayName(a.get_displayname());
    	dlg.settingsModel1.setEmailServer(a.get_mailserver());
    	dlg.settingsModel1.setLbaupdate(Integer.toString(s.getLbaRefresh()));
    	dlg.settingsModel1.setMapSiteDefault(s.getMapServer()==MAPSERVER.DEFAULT);
    	dlg.settingsModel1.setMapSiteWms(s.getMapServer()==MAPSERVER.WMS);
    	dlg.settingsModel1.setPanByClick(!s.getPanByDrag());
    	dlg.settingsModel1.setPanByDrag(s.getPanByDrag());
    	dlg.settingsModel1.setUsername(s.getUsername());
    	dlg.settingsModel1.setWmsImageFormat(s.getSelectedWmsFormat());
    	dlg.settingsModel1.setWmsPassword(s.getWmsPassword());
    	dlg.settingsModel1.setWmsUrl(s.getWmsSite());
    	dlg.settingsModel1.setWmsUsername(s.getWmsUsername());
    	dlg.settingsModel1.setZoomFromCenter(s.getZoomFromCenter());
    	dlg.settingsModel1.setZoomFromCorner(!s.getZoomFromCenter());
    	onMapWmsSelected(s.getMapServer()==MAPSERVER.WMS);
    	if(s.getMapServer()==MAPSERVER.WMS)
    	{
    		dlg.getBtnMapWmsOpen().doClick();
    	}
    }
    
	@Override
	public void onCancel() {
		System.out.println("Cancel");
		dlg.setVisible(false);
	}

	@Override
	public void onOk(WmsLayerTree tree, SettingsModel model) {
		System.out.println("Save");
	}

	@Override
	public void onOpenWmsSite(final WmsLayerTree tree, final String wmsUrl, final String wmsUser,
			final String wmsPassword, final JComboBox imageformats) {
		System.out.println("onOpenWmsSite");
		new SwingWorker<DefaultTreeModel, List<Layer>>() {
			List<Layer> layers;
			no.ums.pas.core.logon.Settings s = PAS.get_pas().get_settings();
			
			@Override
			protected DefaultTreeModel doInBackground() throws Exception {
				dlg.getBtnSave().setEnabled(false);
				String current_url = wmsUrl;
				String new_url = wmsUrl;
				boolean b_new_url = false;
				if(!current_url.equals(new_url))
					b_new_url = true;
				MapLoader l = new MapLoader(null, null);
				l.testWmsUrl(new_url, wmsUser, wmsPassword.toCharArray());
				layers = l.getCapabilitiesTest().getLayerList();
				
				imageformats.removeAllItems();
				List<String> formats = l.m_wms_formats;
				int select_index = 0;
				for(int i=0; i < formats.size(); i++)
				{
					imageformats.addItem(formats.get(i));
					if(s.getSelectedWmsFormat().equals(formats.get(i)))
						select_index = i;
				}
				imageformats.setSelectedIndex(select_index);
				
				tree.populate(layers, s.getSelectedWmsLayers(), b_new_url, null);
				return tree.getModel();
			}

			@Override
			protected void done() {
				dlg.getBtnSave().setEnabled(true);
				PAS.pasplugin.onWmsLayerListLoaded(layers, s.getSelectedWmsLayers());
				super.done();
			}
			
		}.execute();

	}

	@Override
	public void onWmsLayerSelect(WmsLayerTree tree, TreeSelectionEvent e) {
		System.out.println("onWmsLayerSelect");
		
	}

	@Override
	public void onMapWmsSelected(boolean b) {
		dlg.getTxtMapWms().setEnabled(b);
		dlg.getTxtMapWmsUser().setEnabled(b);
		dlg.getTxtMapWmsPassword().setEnabled(b);
		dlg.getTreeWMS().setEnabled(b);
		dlg.getBtnMapWmsOpen().setEnabled(b);
		dlg.getComboMapWmsImg().setEnabled(b);
	}
	

}
