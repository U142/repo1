package no.ums.pas.core.mainui.address_search;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavPoint;
import no.ums.ws.pas.UGabSearchResultList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SearchPanelResultsAddrSearch extends SearchPanelResults implements ActionListener {
	public static final long serialVersionUID = 1;

	private AdrSearchThread m_adrthread;	
	public AdrSearchThread getThread() { return m_adrthread; }
	private ActionListener m_callback;
	private AddressSearchPanel	m_searchpanel;
	private double m_f_zoom = 1500;
	protected int m_n_lon_col = 4;
	protected int m_n_lat_col = 3;
	private HTTPReq m_http;
	protected HTTPReq get_http() { return m_http; }
	
	public ActionListener get_callback() { return m_callback; }
	public SearchPanelVals get_vals() { return get_searchframe().get_searchpanelvals(); }
	public AddressSearchPanel get_searchframe() { return m_searchpanel; }

	public SearchPanelResultsAddrSearch(AddressSearchPanel frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback, boolean b_enable_sort)
	{
		super(sz_columns, n_width, null, dim, ListSelectionModel.SINGLE_SELECTION, b_enable_sort); //new Dimension(800, 200));
		m_searchpanel = frm;
		m_callback = callback;
	}
	public SearchPanelResultsAddrSearch(AddressSearchPanel frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback)
	{
		this(frm, sz_columns, n_width, dim, callback, true);
		/*super(sz_columns, n_width, null, dim, ListSelectionModel.SINGLE_SELECTION); //new Dimension(800, 200));
		m_searchpanel = frm;
		m_callback = callback;*/
	}
	/*SearchPanelResultsAddrSearch(SearchFrame frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback) {
		this(frm, sz_columns, n_width, dim, callback);
		m_http = http;
	}*/
	protected void valuesChanged() { }

	public void start_search()
	{
		//System.out.println("Info: inserting search row");
		//m_tbl_list.insert_row(new String[] {"", "Starting searchthread", "", "0", "0"}, -1); //, ""
		m_adrthread = new AdrSearchThread(Thread.MIN_PRIORITY);
		m_adrthread.start();		
	}
	public boolean is_cell_editable(int row, int col) {
		return is_cell_editable(col);
	}
	protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		String sz_lon, sz_lat;
		sz_lon = ((String)get_table().getValueAt(n_row, m_n_lon_col)).toString();
		sz_lat = ((String)get_table().getValueAt(n_row, m_n_lat_col)).toString();
		//get_pas().get_navigation().exec_adrsearch(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue(), m_f_zoom);
		MapPointLL center = new MapPointLL(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue());
		get_callback().actionPerformed(new ActionEvent(center, ActionEvent.ACTION_PERFORMED, "act_set_pinpoint"));
		get_callback().actionPerformed(new ActionEvent(new NavPoint(new Double(sz_lat).doubleValue(), new Double(sz_lon).doubleValue(), m_f_zoom), ActionEvent.ACTION_PERFORMED, "act_map_goto_point"));
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	
	public class AdrSearchThread extends WSThread implements ActionListener
	{
		boolean m_b_issearching;
		
		AdrSearchThread(int n_pri)
		{
			super(SearchPanelResultsAddrSearch.this);
			this.setPriority(n_pri);
		}
		private void started() { 
			m_b_issearching = true; 
			setLoading(true);
		}
		private void stopped() 
		{ 
			m_b_issearching = false; 
			setLoading(false);
			get_searchframe().get_searchpanelvals().search_stopped();	
		}
		public boolean get_issearching() { return m_b_issearching; }
		
		@Override
		public void call() throws Exception
		{
			
			started();
			try
			{			
				UGabSearchResultList response = PAS.pasplugin.getAddressSearch().onExecSearch(get_vals());
				boolean b_populate = PAS.pasplugin.getAddressSearch().onPopulateList(response, get_tablelist());
				
			} 
			catch(Exception e)
			{
				//Error.getError().addError(PAS.l("common_error"), "Error populating list", e, Error.SEVERITY_ERROR);
				if(m_tbl_list!=null) {
					m_tbl_list.clear();
				}
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
		public void OnDownloadFinished() {
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}

	}	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	
}
