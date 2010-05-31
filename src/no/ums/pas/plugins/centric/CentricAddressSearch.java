package no.ums.pas.plugins.centric;

import java.awt.*;
import java.awt.event.*;
import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.pluginbase.PAS_Scripting;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.mainui.*;
import no.ums.ws.pas.*;
import no.ums.pas.*;


public class CentricAddressSearch extends no.ums.pas.pluginbase.defaults.DefaultAddressSearch
{
	
	/**
	 * Edit how to search for addresses (e.g. another Web Service)
	 * Must return a UGabSearchResultList
	 */
	/*@Override
	public UGabSearchResultList onExecSearch(SearchPanelVals spr) throws Exception
	{
		System.out.println("CentricAddressSearch.onExecSearch");
		return null;
	}*/
	
	
	@Override
	public SearchPanelVals onCreateSearchPanelVals(SearchFrame frame) throws Exception {
		System.out.println("CentricAddressSearch.onCreateSearchPanelVals");
		return new CentricSearchPanelVals(frame);
	}
	
	@Override
	public SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(SearchFrame frame, ActionListener callback)
			throws Exception {
		System.out.println("CentricAddressSearch.onCreateSearchPanelResultsAddrSearch");
        String[] sz_columns  = {
				PAS.l("adrsearch_dlg_address"),
				PAS.l("adrsearch_dlg_region"),
				PAS.l("common_lon"),
				PAS.l("common_lat"),};//"icon"
		int[] n_width = { 200, 100, 50, 50 }; //, 16 };
		return new CentricSearchPanelResultsAddrSearch(frame, sz_columns, n_width, new Dimension(800, 200), callback);
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
		
		public CentricSearchPanelResultsAddrSearch(SearchFrame frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback)
		{
			super(frm, sz_columns, n_width, dim, callback);
			super.m_n_lon_col = 3;
			super.m_n_lat_col = 2;
		}
	}
	
	public class CentricSearchPanelVals extends SearchPanelVals
	{
		public CentricSearchPanelVals(SearchFrame frm)
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
