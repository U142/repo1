package no.ums.pas.plugins.centric;

import nl.bzk.services.nl_alert.mapsearch.BoundingBox;
import nl.bzk.services.nl_alert.mapsearch.MapMatches;
import nl.bzk.services.nl_alert.mapsearch.MapSearchResponse;
import nl.bzk.services.nl_alert.mapsearch.MapSearchService;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.mainui.address_search.AddressSearchPanel;
import no.ums.pas.core.mainui.address_search.SearchPanelResultsAddrSearch;
import no.ums.pas.core.mainui.address_search.SearchPanelVals;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.ws.common.parm.UBoundingRect;
import no.ums.ws.pas.*;

import javax.swing.*;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class CentricAddressSearch extends no.ums.pas.pluginbase.defaults.DefaultAddressSearch
{
    private static final Log log = UmsLog.getLogger(CentricAddressSearch.class);

	class UGabResultListItem extends UGabResult
	{

		public String toString()
		{
			return super.getName(); 
		}

        public void setRect(UBoundingRect rect) {
            // TODO: This should not exist, temp method until UgabResult is updated
        }

        public void setScope(int scope) {
            // TODO: This should not exist, temp method until UgabResult is updated
        }
    }
	
	/**
	 * Edit how to search for addresses (e.g. another Web Service)
	 * Must return a UGabSearchResultList
	 * Translate return values into a UGabSearchResultList to return
	 */
	@Override
	public UGabSearchResultList onExecSearch(SearchPanelVals spr) throws Exception
	{
		//PluginLoader.LoadExternalJar(PAS.get_pas().get_codebase(), "NLMapSearch", "ObjectFactory");
		log.debug("CentricAddressSearch.onExecSearch");
		java.net.URL wsdl;
		try
		{			
			//String WSDL_CENTRIC_MAPSEARCH = "http://94.228.135.14/MapSearchService.asmx?WSDL";
			String WSDL_CENTRIC_MAPSEARCH = PAS.get_pas().ADDRESSSEARCH_URL;
			wsdl = new java.net.URL(WSDL_CENTRIC_MAPSEARCH);//PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://www.bzk.nl/services/nl-alert/mapsearch/", "MapSearchService"); //"http://www.bzk.nl/services/nl-alert/mapsearch/"
			MapSearchService myService = new MapSearchService(wsdl, service);
			MapSearchResponse temp_response = myService.
								getMapSearchServiceSoap().
								doMapSearch(
                                        10,
                                        spr.get_number(),
                                        spr.get_address(),
                                        spr.get_postno().replaceAll("\\s",""),
                                        spr.get_postarea(),
                                        spr.get_region());
			List<MapMatches> list = temp_response.getMatches().getMapMatches();
			UGabSearchResultList response = new UGabSearchResultList();
			ArrayOfUGabResult arr_res = new ArrayOfUGabResult();
            for (MapMatches m : list) {
                UGabResultListItem ugabresult = new UGabResultListItem();
                ugabresult.setLat(m.getLat());
                ugabresult.setLon(m.getLon());
                ugabresult.setMatch(m.getMatch());
                ugabresult.setName(m.getName());
                ugabresult.setPostno(m.getName());
                ugabresult.setRegion(m.getRegion());
                UBoundingRect rect = new UBoundingRect();
                BoundingBox bbox = m.getBb();
                if (bbox != null) {
                    rect.setBottom(bbox.getBottom());
                    rect.setLeft(bbox.getLeft());
                    rect.setRight(bbox.getRight());
                    rect.setTop(bbox.getTop());
                    ugabresult.setRect(rect);
                } else {
                    ugabresult.setRect(null);
                }
                ugabresult.setScope(m.getScope());
                switch (m.getScope()) {
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
			log.warn(e.getMessage(), e);
            JOptionPane.showMessageDialog(spr, Localization.l("adrsearch_dlg_general_error"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
			return new UGabSearchResultList();
		}
		
	}
	
	
	@Override
	public SearchPanelVals onCreateSearchPanelVals(AddressSearchPanel panel) throws Exception {
		log.debug("CentricAddressSearch.onCreateSearchPanelVals");
		return new CentricSearchPanelVals(panel);
	}
	
	@Override
	public SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(AddressSearchPanel panel, ActionListener callback)
			throws Exception {
		log.debug("CentricAddressSearch.onCreateSearchPanelResultsAddrSearch");
        String[] sz_columns  = {
                Localization.l("adrsearch_dlg_address"),
                Localization.l("adrsearch_dlg_region"),
                Localization.l("common_lon"),
                Localization.l("common_lat"),};//"icon"
		int[] n_width = { 200, 100, 50, 50 }; //, 16 };
		return new CentricSearchPanelResultsAddrSearch(panel, sz_columns, n_width, new Dimension(100, 100), callback);
	}
	
	@Override
	public boolean onPopulateList(UGabSearchResultList results, TableList list)
	{
		if(list!=null) {
			list.clear();
		}
		if(results.getList()==null || results.getList().getUGabResult()==null)
			return false;
		for (UGabResult result : results.getList().getUGabResult()) {
			Object[] obj_insert = { result, result.getRegion(), new Float(result.getLon()).toString(), new Float(result.getLat()).toString() }; //, m_icon_goto };
			list.insert_row(obj_insert, -1, true);
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

		@Override
		protected void onMouseLDblClick(int nRow, int nCol,
				Object[] rowcontent, Point p) {
			//super.onMouseLDblClick(nRow, nCol, rowcontent, p);
			UGabResult gr = (UGabResult)rowcontent[0];
			UBoundingRect rect = null; // TODO: Comment back in after UGabResult is updated -- gr.getRect();
			//if(rect.getLeft()==rect.getRight() && rect.getTop()==rect.getBottom())
			MapPointLL center = new MapPointLL(gr.getLon(), gr.getLat());
			if(rect == null) {
				rect = new UBoundingRect();
				rect.setLeft(gr.getLon()-0.001);
				rect.setRight(gr.getLon()+0.001);
				rect.setTop(gr.getLat()+0.001);
				rect.setBottom(gr.getLat()-0.001);
			}
			get_callback().actionPerformed(new ActionEvent(center, ActionEvent.ACTION_PERFORMED, "act_set_pinpoint"));
			get_callback().actionPerformed(new ActionEvent(new NavStruct(rect.getLeft(), rect.getRight(), rect.getTop(), rect.getBottom()), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
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
			set_gridconst(1,0,2,1, GridBagConstraints.WEST);
			add(m_val_address, m_gridconst);
			set_gridconst(3,0,1,1, GridBagConstraints.WEST);
			add(m_val_number, m_gridconst);
			
			set_gridconst(0,1,1,1, GridBagConstraints.WEST);
			add(m_txt_postno, m_gridconst);
			set_gridconst(1,1,1,1, GridBagConstraints.WEST);
			add(m_val_postno, m_gridconst);
			set_gridconst(2,1,2,1, GridBagConstraints.EAST);
			add(m_val_postarea, m_gridconst);
			
			set_gridconst(0,2,1,1, GridBagConstraints.WEST);
			add(m_txt_region, m_gridconst);
			set_gridconst(1,2,3,1, GridBagConstraints.WEST);
			add(m_val_region, m_gridconst);
			
			/*set_gridconst(0,3,1,1, GridBagConstraints.WEST);
			add(m_txt_country, m_gridconst);
			set_gridconst(1,3,2,1, GridBagConstraints.WEST);
			add(m_val_country, m_gridconst);*/		
				
			set_gridconst(0,4,4,1, GridBagConstraints.WEST);
			add(m_btn_search, m_gridconst);
		}

	}
}
