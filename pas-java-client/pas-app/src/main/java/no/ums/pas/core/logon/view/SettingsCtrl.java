package no.ums.pas.core.logon.view;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.WmsLayer;
import no.ums.pas.core.logon.WmsLayerTree;
import no.ums.pas.core.logon.view.Settings.ISettingsUpdate;
import no.ums.pas.core.ws.WSSaveUI;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.maps.MapLoader;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendOptionToolbar;
import org.geotools.data.ows.Layer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsCtrl implements ISettingsUpdate {
    private static final Log log = UmsLog.getLogger(SettingsCtrl.class);
    
    private final Settings dlg;

    public SettingsCtrl(Component parent, boolean modal, 
    		no.ums.pas.core.logon.Settings settings, MailAccount mailaccount,
    		UserInfo userinfo)
    {
    	dlg = new Settings(null, this);
    	dlg.setIconImage(ImageFetcher.getImage("pas_appicon_16.png"));

        initializeGui(settings, mailaccount, userinfo);
    	dlg.initValues();
    	dlg.populateDeptCategory(settings.getDeptCategory());
    	dlg.setLocationRelativeTo(parent);
    	dlg.setVisible(true);
    }
    
    public void initializeGui(no.ums.pas.core.logon.Settings s, MailAccount a, UserInfo userinfo)
    {
    	dlg.getToggleLba().setSelected(false);
    	dlg.getToggleBlocklist().setSelected(false);
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
    	dlg.settingsModel1.setNewSendingAutoChannel(s.getN_newsending_autochannel());
    	dlg.settingsModel1.setNewSendingAutoShape(s.getN_autoselect_shapetype());
    	switch(s.getN_autoselect_shapetype())
    	{
    	case SendOptionToolbar.BTN_SENDINGTYPE_POLYGON_:
    		dlg.getTogglePolygon().doClick();
    		break;
    	case SendOptionToolbar.BTN_SENDINGTYPE_ELLIPSE_:
    		dlg.getToggleEllipse().doClick();
    		break;
    	case SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_:
    		break;
    	case SendOptionToolbar.BTN_OPEN_:
    		dlg.getToggleImport().doClick();
    		break;
    	}
    	
    	onMapWmsSelected(s.getMapServer()==MAPSERVER.WMS);
    	if(s.getMapServer()==MAPSERVER.WMS)
    	{
    		dlg.getBtnMapWmsOpen().doClick();
    	}
    	
    	//check if user may use parm on one or more departments
    	boolean b_enable_parm = false;
    	boolean b_enable_lba = false;
    	boolean b_enable_vulnerable = false;
    	boolean b_enable_headofhousehold = false;
    	for(DeptInfo di : userinfo.get_departments())
    	{
    		if(di.get_userprofile().get_parm_rights()>=1)
    		{
    			b_enable_parm = true;
    		}
    		long adrtypes = di.get_userprofile().get_addresstypes();
    		if((adrtypes & SendController.SENDTO_CELL_BROADCAST_TEXT)>0)
    		{
    			b_enable_lba = true;
    		}
    		if((adrtypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS)>0)
    		{
    			b_enable_vulnerable = true;
    		}
    		if((adrtypes & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD)>0)
    		{
    			b_enable_headofhousehold = true;
    		}
    	}
    	if(!b_enable_parm)
    	{
    		dlg.settingsModel1.setAutoStartParm(b_enable_parm);
    		dlg.getChkAutoStartParm().setEnabled(b_enable_parm);
    	}
    	dlg.getPnlLBA().setVisible(b_enable_lba);
    	dlg.getToggleLba().setVisible(b_enable_lba);
    	dlg.getToggleVulnerable().setVisible(b_enable_vulnerable);
    	dlg.getToggleHeadOfHousehold().setVisible(b_enable_headofhousehold);

    }
    
	@Override
	public void onMoveLayerUp(final WmsLayerTree tree) {
		Layer l = tree.getSelectedLayer();
		tree.moveNodeUp();
	}

	@Override
	public void onMoveLayerDown(final WmsLayerTree tree) {
		tree.moveNodeDown();
	}

	@Override
	public void onCancel() {
		log.debug("Cancel");
		dlg.setVisible(false);
	}

	@Override
	public void onOk(WmsLayerTree tree, SettingsModel model) {
		log.debug("Save");
		
		no.ums.pas.core.logon.Settings s = Variables.getSettings();
		MailAccount ma = PAS.get_pas().get_userinfo().get_mailaccount();
		s.setUsername(model.getUsername());
		s.setCompany(model.getCompanyid());
		s.setParm(model.getAutoStartParm());
		boolean b_reload_map = 
			s.setMapServer((model.getMapSiteDefault() ? MAPSERVER.DEFAULT : MAPSERVER.WMS)) |
			s.setWmsSite(model.getWmsUrl()) | 
			s.setWmsUsername(model.getWmsUsername()) |
			s.setWmsPassword(model.getWmsPassword());
		s.setPanByDrag(model.getPanByDrag());
		s.setZoomFromCenter(model.getZoomFromCenter());
		s.setLbaRefresh(Integer.parseInt(model.getLbaupdate().toString()));
		try{
			s.setDeptCategory((model.getDeptCategory()));
		} catch(Exception e)	{

			log.error("DeptGroupCategory exception",e);
		}
		s.setAddressTypes(model.getAddressTypes());
		s.setSelectedWmsFormat(model.getWmsImageFormat());
		s.setN_newsending_autochannel(model.getNewSendingAutoChannel());
		s.setN_autoselect_shapetype(model.getNewSendingAutoShape());
		
		ma.set_accountname(model.getEmailAddress());
		ma.set_displayname(model.getEmailDisplayName());
		ma.set_mailaddress(model.getEmailAddress());
		ma.set_mailserver(model.getEmailServer());
		
		List<String> selected_layers;
		if(s.getMapServer()==MAPSERVER.WMS && tree.getModel().getRoot()!=null) // m_wms_list.m_tbl_list.getRowCount() > 0)
		{
			selected_layers = new ArrayList<String>();
			List<WmsLayer> layers = tree.getLayers();
			for(WmsLayer l : layers)
			{
				selected_layers.add(l.toString());
			}
			b_reload_map |= s.setWmsLayers(layers);
		}
		else //keep the old ones
		{
			selected_layers = PAS.get_pas().get_settings().getSelectedWmsLayers();
		}
		dlg.getBtnSave().setEnabled(false);
		final boolean final_reload_map = b_reload_map;
		new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				new WSSaveUI(null).runNonThreaded();
				new XmlWriter().saveSettings(true);
				return "OK";
			}

			@Override
			protected void done() {
				super.done();
				dlg.getBtnSave().setEnabled(true);
				dlg.setVisible(false);
				if(final_reload_map)
				{
					Variables.getMapFrame().signalWmsLayersChanged();
					Variables.getNavigation().reloadMap();
				}
			}
		
		}.execute();
	}

	@Override
	public void onOpenWmsSite(final WmsLayerTree tree, final String wmsUrl, final String wmsUser,
			final String wmsPassword, final JComboBox imageformats) {
		log.debug("onOpenWmsSite");
		new SwingWorker<DefaultTreeModel, List<Layer>>() {
			List<Layer> layers;
			no.ums.pas.core.logon.Settings s = PAS.get_pas().get_settings();
			
			@Override
			protected DefaultTreeModel doInBackground() throws Exception {
				dlg.getBtnSave().setEnabled(false);
				dlg.getBtnMapWmsOpen().setEnabled(false);
				String current_url = wmsUrl;
				String new_url = wmsUrl;
				boolean b_new_url = false;
				if(!current_url.equals(new_url))
					b_new_url = true;
				MapLoader l = new MapLoader(null);
				try
				{
					l.testWmsUrl(new_url, wmsUser, wmsPassword.toCharArray());
					layers = l.getCapabilitiesTest().getLayerList();
				}
				catch(Exception e)
				{
					//dlg.getTxtMapWms().grabFocus();
					dlg.onWmsLayerlistLoaded(false, e);
					throw e;
				}
				
				imageformats.removeAllItems();
				List<String> formats = l.m_wms_formats;
				int select_index = 0;
				for(int i=0; i < formats.size(); i++)
				{
					imageformats.addItem(formats.get(i));
					if(s.getSelectedWmsFormat().equals(formats.get(i)))
						select_index = i;
				}
				if(select_index>=0)
					imageformats.setSelectedIndex(select_index);
				
				tree.populate(layers, s.getWmsLayers(), b_new_url, null);
				dlg.onWmsLayerlistLoaded(true, null);
				return tree.getModel();
			}

			@Override
			protected void done() {
				dlg.getBtnSave().setEnabled(true);
				dlg.getBtnMapWmsOpen().setEnabled(true);
				PAS.pasplugin.onWmsLayerListLoaded(layers, s.getSelectedWmsLayers());
				super.done();
			}
			
		}.execute();

	}

	@Override
	public void onWmsLayerSelect(WmsLayerTree tree, TreeSelectionEvent e) {
		log.debug("onWmsLayerSelect");
		
	}

	@Override
	public void onMapWmsSelected(boolean b) {
		dlg.getTxtMapWms().setEnabled(b);
		dlg.getTxtMapWmsUser().setEnabled(b);
		dlg.getTxtMapWmsPassword().setEnabled(b);
		dlg.getTreeWMS().setEnabled(b);
		dlg.getBtnMapWmsOpen().setEnabled(b);
		dlg.getComboMapWmsImg().setEnabled(b);
		dlg.getBtnMoveDown().setEnabled(b);
		dlg.getBtnMoveUp().setEnabled(b);
	}

	@Override
	public void onNewSendingAutoShape(AbstractButton value) {
		
	}
	

}
