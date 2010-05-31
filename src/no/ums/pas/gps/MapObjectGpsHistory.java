package no.ums.pas.gps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.ListSelectionModel;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.mainui.SearchFrame;
import no.ums.pas.core.mainui.SearchPanelVals;
import no.ums.pas.maps.defines.MapObject;
import no.ums.pas.ums.tools.TextFormat;

import org.w3c.dom.Document;


public class MapObjectGpsHistory extends DefaultPanel {
	public static final long serialVersionUID = 1;
	private MapObjectReg m_reg;
	public MapObjectGpsHistory(MapObjectReg reg) {
		super();
		m_reg = reg;
		add_controls();
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public void add_controls() {

		String [] sz_cols	= { "Date/Time", "Speed", "Course", "ASL", "Battery", "Sat", "Lon", "Lat", "Last Distance", "Street", "Region"  };
		int [] n_width		= { 110, 30, 30, 30, 30, 30, 40, 40, 60, 60, 60 };
		GpsAddrSearch search = new GpsAddrSearch(PAS.get_pas(), sz_cols, n_width);
		setLayout(new BorderLayout());
		add(search, BorderLayout.CENTER);
		init();
	}
	public void init() {
		setPreferredSize(new Dimension(490, 350));
		doLayout();
		setVisible(true);		
	}
	class GpsAddrSearch extends SearchPanelResults {
		public static final long serialVersionUID = 1;
		
		private SearchThread m_adrthread;	
		private SearchFrame	m_searchframe;
		final int m_n_lon_col = 4;
		final int m_n_lat_col = 3;
		
		SearchPanelVals get_vals() { return get_searchframe().get_searchpanelvals(); }
		SearchFrame get_searchframe() { return m_searchframe; }

		GpsAddrSearch(PAS pas, String [] sz_columns, int [] n_width)
		{
			super(sz_columns, n_width, null, new Dimension(400, 300), ListSelectionModel.SINGLE_SELECTION);
			start_search();
		}
		protected void valuesChanged() { }

		protected void start_search()
		{
			//System.out.println("Info: inserting search row");
			m_tbl_list.insert_row(new String[] {"", "Starting searchthread", "" }, -1); //, ""
			m_adrthread = new SearchThread(Thread.MIN_PRIORITY);
			m_adrthread.start();		
		}
		
		protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
		{
		}
		protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
		{
		}
		protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
		{
		}
		protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
		{
		}
		public boolean is_cell_editable(int row, int col) {
			return is_cell_editable(col);
		}
		class SearchThread extends Thread
		{
			boolean m_b_issearching;
			
			SearchThread(int n_pri)
			{
				this.setPriority(n_pri);
			}
			private void started() { m_b_issearching = true; }
			private void stopped() 
			{ 
				m_b_issearching = false; 
				try {
					get_searchframe().get_searchpanelvals().search_stopped();
				} catch(Exception e) {
					
				}
			}
			public boolean get_issearching() { return m_b_issearching; }
			
			public void run()
			{
				started();
				m_tbl_list.clear();
				MapObject obj = m_reg.get_mapobject();
//				for(int i=0; i < obj.get_gpscoors().size(); i++) {
				for(int i=obj.get_gpscoors().size()-1; i >=0; i--) {
 					GPSCoor coor = (GPSCoor)obj.get_gpscoors().get(i);
					String coors [] = new String[] { TextFormat.format_date(coor.get_gpsdate()) + " " + TextFormat.format_time(coor.get_gpstime(), 6), 
							""+coor.get_speed(), coor.get_course()+"", ""+coor.get_asl(), ""+coor.get_battery(), ""+coor.get_satellites(), 
							""+coor.get_lon(),  ""+coor.get_lat(), ""+Math.round(coor.get_distance_to_prev()), coor.get_street(), coor.get_region() };
					m_tbl_list.insert_row(coors, -1);
				}
				
				stopped();
			}
			
			public void parseAdrDoc(Document doc) {
			}
		
		}	
		
	}	
}