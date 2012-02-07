package no.ums.pas.plugins.centric.address_search;

import no.ums.pas.PAS;
import no.ums.pas.core.mainui.address_search.AddressSearchCtrl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import no.ums.pas.core.Variables;
import no.ums.pas.core.mainui.address_search.AddressSearchCountry;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.AddressSearchListHeader;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.AddressSearchListItem;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.IAddressSearch;
import no.ums.pas.core.mainui.address_search.AddressSearchModel;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavPoint;
import no.ums.pas.pluginbase.defaults.DefaultAddressSearch;
import no.ums.ws.pas.UGabSearchResultList;

public class CentricAddressSearchCtrl extends AddressSearchCtrl {
	private CentricAddressSearchDlg dlg = null;
	private CentricAddressSearchDlg getDlg() { return dlg; }
	public CentricAddressSearchCtrl()
	{
		
	}
	
	public void showGUI()
	{
		if(dlg == null) {
			dlg = new CentricAddressSearchDlg(this, Variables.getMapFrame());
		}
		dlg.setVisible(true);
	}

	@Override
	public boolean onSearch(AddressSearchModel m) {
		//kjør ws
		AdrSearchThread ws = new AdrSearchThread(m, this, 1);
		ws.start();
		return false;
	}

	public class AdrSearchThread extends WSThread implements ActionListener
	{
		boolean m_b_issearching;
		AddressSearchModel m;
		
		AdrSearchThread(AddressSearchModel m, ActionListener callback , int n_pri)
		{
			super(callback);
			this.m = m;
			this.setPriority(n_pri);
		}
		
		private void isRunning(boolean b)
		{
			m_b_issearching = b; 
			getDlg().getBtnSearch().setEnabled(!b);			
		}
		
		public boolean get_issearching() { return m_b_issearching; }
		
		@Override
		public void call() throws Exception
		{
			
			isRunning(true);
			try
			{	
				UGabSearchResultList response = PAS.pasplugin.getAddressSearch().onExecSearch(m.getAddress(),m.getHouse(), m.getPostno(), m.getPlace(), m.getRegion(), (AddressSearchCountry)m.getCountry());
				//dlg.fillResults(response);
			} 
			catch(Exception e)
			{
				throw e;

			}
			finally
			{				
				isRunning(false);
			}
		}
		@Override
		protected String getErrorMessage() {
			return "Error populating list";
		}
		@Override
		public void onDownloadFinished() {
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}

	}	
	
	@Override
	public boolean onAddressSelect(AddressSearchListItem s) {
		String sz_lon, sz_lat;
		sz_lon = String.valueOf(s.getLat());
		sz_lat = String.valueOf(s.getLon());
		MapPointLL center = new MapPointLL(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue());
		int zoomLevel = 100;
		switch(s.getType())
		{
		case HOUSE:
			zoomLevel = 100;
			break;
		case STREET:
			zoomLevel = 400; 
			break;
		case POST:
			zoomLevel = 1500;
			break;
		case REGION:
			zoomLevel = 5000;
			break;
		}
		PAS.get_pas().get_pasactionlistener().actionPerformed(new ActionEvent(center, ActionEvent.ACTION_PERFORMED, "act_set_pinpoint"));
		PAS.get_pas().get_pasactionlistener().actionPerformed(new ActionEvent(new NavPoint(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue(), zoomLevel), ActionEvent.ACTION_PERFORMED, "act_map_goto_point"));
		return false;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}

