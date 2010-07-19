package no.ums.pas.plugins.centric;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import nl.bzk.services.nl_alert.mapsearch.*;
import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.pluginbase.PAS_Scripting;
import no.ums.pas.pluginbase.PluginLoader;
import no.ums.pas.core.variables;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.mainui.*;
import no.ums.pas.core.mainui.address_search.AddressSearchPanel;
import no.ums.pas.core.mainui.address_search.SearchPanelResultsAddrSearch;
import no.ums.pas.core.mainui.address_search.SearchPanelVals;
import no.ums.pas.core.ws.vars;
import no.ums.ws.pas.*;
import no.ums.pas.*;


public class CentricAddressSearch extends no.ums.pas.pluginbase.defaults.DefaultAddressSearch
{
	
	/**
	 * Edit how to search for addresses (e.g. another Web Service)
	 * Must return a UGabSearchResultList
	 * Translate return values into a UGabSearchResultList to return
	 */
	@Override
	public UGabSearchResultList onExecSearch(SearchPanelVals spr) throws Exception
	{
		//PluginLoader.LoadExternalJar(PAS.get_pas().get_codebase(), "NLMapSearch", "ObjectFactory");
		System.out.println("CentricAddressSearch.onExecSearch");
		nl.bzk.services.nl_alert.mapsearch.ObjectFactory of = new nl.bzk.services.nl_alert.mapsearch.ObjectFactory();
		java.net.URL wsdl;
		try
		{			
			String WSDL_CENTRIC_MAPSEARCH = "http://www.centric.nl/services/nl-alert/mapsearch/MapSearchService.asmx?WSDL";
			wsdl = new java.net.URL(WSDL_CENTRIC_MAPSEARCH);//PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://www.bzk.nl/services/nl-alert/mapsearch/", "MapSearchService");
			MapSearchService myService = new MapSearchService(wsdl, service);
			MapSearchResponse temp_response = myService.
								getMapSearchServiceSoap().
								doMapSearch(
										10, 
										spr.get_number(), 
										spr.get_address(), 
										spr.get_postno(),
										spr.get_postarea(),
										spr.get_region());
			List<MapMatches> list = temp_response.getMatches().getMapMatches();
			UGabSearchResultList response = new UGabSearchResultList();
			List<UGabResult> results = new ArrayList<UGabResult>();
			ArrayOfUGabResult arr_res = new ArrayOfUGabResult();
			for(int i=0; i < list.size(); i++)
			{
				MapMatches m = list.get(i);
				UGabResult ugabresult = new UGabResult();
				ugabresult.setLat(m.getLat());
				ugabresult.setLon(m.getLon());
				ugabresult.setMatch(m.getMatch());
				ugabresult.setName(m.getName());
				ugabresult.setPostno(m.getName());
				ugabresult.setRegion(m.getRegion());
				switch(m.getScope())
				{
				default:
					ugabresult.setType(GABTYPE.HOUSE);
					break;
				}
				arr_res.getUGabResult().add(ugabresult);
				
			}
			response.setBHaserror(false);
			response.setList(arr_res);
			/*Pasws myService = new Pasws(wsdl, service);
			UGabSearchResultList response = myService.getPaswsSoap12().gabSearch(params, logoninfo);
			if(response.isBHaserror())
			{
				no.ums.pas.ums.errorhandling.Error.getError().addError(PAS.l("common_error"), response.getSzErrortext(), new Exception(response.getSzExceptiontext()), 1);
				throw new Exception(response.getSzErrortext());
			}*/
			return response;
		} 
		catch(Exception e)
		{
			throw e;
		}
		
	}
	
	
	@Override
	public SearchPanelVals onCreateSearchPanelVals(AddressSearchPanel panel) throws Exception {
		System.out.println("CentricAddressSearch.onCreateSearchPanelVals");
		return new CentricSearchPanelVals(panel);
	}
	
	@Override
	public SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(AddressSearchPanel panel, ActionListener callback)
			throws Exception {
		System.out.println("CentricAddressSearch.onCreateSearchPanelResultsAddrSearch");
        String[] sz_columns  = {
				PAS.l("adrsearch_dlg_address"),
				PAS.l("adrsearch_dlg_region"),
				PAS.l("common_lon"),
				PAS.l("common_lat"),};//"icon"
		int[] n_width = { 200, 100, 50, 50 }; //, 16 };
		return new CentricSearchPanelResultsAddrSearch(panel, sz_columns, n_width, new Dimension(100, 100), callback);
	}
	
	@Override
	public boolean onPopulateList(UGabSearchResultList results, TableList list)
	{
		if(list!=null) {
			list.clear();
		}
		java.util.Iterator it = results.getList().getUGabResult().iterator();
		while(it.hasNext())
		{
			UGabResult result = (UGabResult)it.next();
			Object[] obj_insert = { result.getName(), result.getRegion(), new Float(result.getLon()).toString(), new Float(result.getLat()).toString() }; //, m_icon_goto };
			list.insert_row(obj_insert, -1);
		}

		return true;
	}

	
	public class CentricSearchPanelResultsAddrSearch extends SearchPanelResultsAddrSearch
	{
		protected int m_n_lon_col = 3;
		protected int m_n_lat_col = 2;
		
		public CentricSearchPanelResultsAddrSearch(AddressSearchPanel frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback)
		{
			super(frm, sz_columns, n_width, dim, callback, false);
			super.m_n_lon_col = 3;
			super.m_n_lat_col = 2;
		}
	}
	
	public class CentricSearchPanelVals extends SearchPanelVals
	{
		public CentricSearchPanelVals(AddressSearchPanel frm)
		{
			super(frm);
		}
		@Override
		public void add_controls()
		{
			set_gridconst(0,0,1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
			add(m_txt_address, m_gridconst);
			set_gridconst(1,0,1,1, GridBagConstraints.WEST);
			add(m_val_address, m_gridconst);
			set_gridconst(2,0,1,1, GridBagConstraints.WEST);
			add(m_val_number, m_gridconst);
			
			set_gridconst(0,1,1,1, GridBagConstraints.WEST);
			add(m_txt_postno, m_gridconst);
			set_gridconst(1,1,2,1, GridBagConstraints.WEST);
			add(m_val_postno, m_gridconst);
			set_gridconst(1,1,2,1, GridBagConstraints.EAST);
			add(m_val_postarea, m_gridconst);
			
			set_gridconst(0,2,2,1, GridBagConstraints.WEST);
			add(m_txt_region, m_gridconst);
			set_gridconst(1,2,2,1, GridBagConstraints.WEST);
			add(m_val_region, m_gridconst);
			
			/*set_gridconst(0,3,1,1, GridBagConstraints.WEST);
			add(m_txt_country, m_gridconst);
			set_gridconst(1,3,2,1, GridBagConstraints.WEST);
			add(m_val_country, m_gridconst);*/		
				
			set_gridconst(0,4,3,1, GridBagConstraints.WEST);
			add(m_btn_search, m_gridconst);
		}

	}
}
