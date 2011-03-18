package no.ums.pas.core.mainui.address_search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.mainui.address_search.AddressSearchCountry;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.AddressSearchListHeader;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.AddressSearchListItem;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.IAddressSearch;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavPoint;
import no.ums.pas.pluginbase.defaults.DefaultAddressSearch;
import no.ums.ws.pas.UGabSearchResultList;

public class AddressSearchCtrl implements IAddressSearch, ActionListener {
	private AddressSearchDlg dlg = null;
	public AddressSearchDlg getDlg() { return dlg; }
	public AddressSearchCtrl()
	{
		
	}
	
	public void showGUI()
	{
		if(dlg == null) {
			dlg = new AddressSearchDlg(this, Variables.getMapFrame());
		}
		int defaultCC = 47;
		try
		{
			defaultCC = Integer.valueOf(Variables.getUserInfo().get_current_department().get_stdcc());
		}
		catch(Exception e)
		{
			//ignore, for test purposes
		}
		List<AddressSearchCountry> countries = new ArrayList<AddressSearchCountry>();
		countries.add(dlg.newAddressSearchCountry(47, Localization.l("common_cc_country_47")));
		countries.add(dlg.newAddressSearchCountry(46, Localization.l("common_cc_country_46")));
		countries.add(dlg.newAddressSearchCountry(45, Localization.l("common_cc_country_45")));
		dlg.setCountries(countries, defaultCC);
		dlg.setVisible(true);
	}

	@Override
	public boolean onSearch(AddressSearchModel m) {
		System.out.println("User clicked search");
		//kjør ws
		AdrSearchThread ws = new AdrSearchThread(m, this, 1);
		ws.run();
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
		private void started() { 
			m_b_issearching = true; 
			//setLoading(true);
		}
		private void stopped() 
		{ 
			m_b_issearching = false; 
			//setLoading(false);
			//get_searchframe().get_searchpanelvals().search_stopped();	
		}
		public boolean get_issearching() { return m_b_issearching; }
		
		@Override
		public void call() throws Exception
		{
			
			started();
			try
			{	
				UGabSearchResultList response = PAS.pasplugin.getAddressSearch().onExecSearch(m.getAddress(),m.getHouse(), m.getPostno(), m.getPlace(), m.getRegion(), (AddressSearchCountry)m.getCountry());
				dlg.fillResults(response);
			} 
			catch(Exception e)
			{
				throw e;

			}
			finally
			{				
				stopped();
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
		System.out.println("User select");
		String sz_lon, sz_lat;
		sz_lon = String.valueOf(s.getLat());
		sz_lat = String.valueOf(s.getLon());
		//get_pas().get_navigation().exec_adrsearch(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue(), m_f_zoom);
		MapPointLL center = new MapPointLL(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue());
		PAS.get_pas().get_pasactionlistener().actionPerformed(new ActionEvent(center, ActionEvent.ACTION_PERFORMED, "act_set_pinpoint"));
		PAS.get_pas().get_pasactionlistener().actionPerformed(new ActionEvent(new NavPoint(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue(), 1500), ActionEvent.ACTION_PERFORMED, "act_map_goto_point"));
		return false;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
