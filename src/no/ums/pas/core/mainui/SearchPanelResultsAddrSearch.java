package no.ums.pas.core.mainui;

import java.awt.Dimension;
import java.awt.Point;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.ListSelectionModel;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.event.*;
import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.parm.Parmws;
import no.ums.ws.pas.*;


public class SearchPanelResultsAddrSearch extends SearchPanelResults {
	public static final long serialVersionUID = 1;

	private AdrSearchThread m_adrthread;	
	public AdrSearchThread getThread() { return m_adrthread; }
	private ActionListener m_callback;
	private SearchFrame	m_searchframe;
	private double m_f_zoom = 1500;
	protected int m_n_lon_col = 4;
	protected int m_n_lat_col = 3;
	private HTTPReq m_http;
	protected HTTPReq get_http() { return m_http; }
	
	public ActionListener get_callback() { return m_callback; }
	public SearchPanelVals get_vals() { return get_searchframe().get_searchpanelvals(); }
	public SearchFrame get_searchframe() { return m_searchframe; }

	public SearchPanelResultsAddrSearch(SearchFrame frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback)
	{
		super(sz_columns, n_width, null, dim, ListSelectionModel.SINGLE_SELECTION); //new Dimension(800, 200));
		m_searchframe = frm;
		m_callback = callback;
	}
	/*SearchPanelResultsAddrSearch(SearchFrame frm, String [] sz_columns, int [] n_width, Dimension dim, ActionListener callback) {
		this(frm, sz_columns, n_width, dim, callback);
		m_http = http;
	}*/
	protected void valuesChanged() { }

	protected void start_search()
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
	
	public class AdrSearchThread extends Thread
	{
		boolean m_b_issearching;
		
		AdrSearchThread(int n_pri)
		{
			super("AdrSearchThread");
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
		
		public void run()
		{
			
			started();
			
			
			/*String sz_address	= get_vals().get_address();
			//sz_address = sz_address.replaceAll(" ", "%20");
			String sz_no		= get_vals().get_number();
			//sz_no = sz_no.replaceAll(" ", "%20");
			String sz_postno	= get_vals().get_postno();
			String sz_postarea	= get_vals().get_postarea();
			String sz_region	= get_vals().get_region();
			String sz_country	= get_vals().get_country();
			String sz_language = "NO";
			if(sz_country=="Norway")
				sz_language = "NO";
			else if(sz_country=="Sweden")
				sz_language = "SE";
			else if(sz_country=="Denmark")
				sz_language = "DK";
			
			ObjectFactory factory = new ObjectFactory();
			//GabSearch searcher = factory.createGabSearch();
			//GabSearchResponse response = factory.createGabSearchResponse();
			UGabSearchParams params = factory.createUGabSearchParams();
			params.setNCount(10);
			params.setNSort(0);
			params.setSzAddress(sz_address);
			params.setSzCountry(sz_language);
			params.setSzLanguage(sz_language);
			params.setSzNo(sz_no);
			params.setSzPostarea(sz_postarea);
			params.setSzPostno(sz_postno);
			params.setSzPwd("MSG");
			params.setSzRegion(sz_region);
			params.setSzUid("UMS");
			ULOGONINFO logoninfo = factory.createULOGONINFO();
			
			//searcher.setLogoninfo(logoninfo);
			//searcher.setSearchparams(params);
			
			java.net.URL wsdl;*/
			try
			{			
				//wsdl = new java.net.URL("http://localhost/ws/PAS.asmx?WSDL");
				/*wsdl = new java.net.URL(vars.WSDL_PAS);//PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
				QName service = new QName("http://ums.no/ws/pas/", "pasws");
				Pasws myService = new Pasws(wsdl, service);
				UGabSearchResultList response = myService.getPaswsSoap12().gabSearch(params, logoninfo);
				if(response.isBHaserror())
				{
					UMS.ErrorHandling.Error.getError().addError(PAS.l("common_error"), response.getSzErrortext(), new Exception(response.getSzExceptiontext()), 1);
					stopped();
					return;
				}*/
				UGabSearchResultList response = PAS.pasplugin.ADDRESS_SEARCH.onExecSearch(get_vals());
				boolean b_populate = PAS.pasplugin.ADDRESS_SEARCH.onPopulateList(response, get_tablelist());
				
			} 
			catch(Exception e)
			{
				Error.getError().addError(PAS.l("common_error"), "Error populating list", e, Error.SEVERITY_ERROR);
				if(m_tbl_list!=null) {
					m_tbl_list.clear();
				}

			}
			finally
			{				
				stopped();
			}
			

			
			/*String sz_url = get_searchframe().get_pas().get_sitename() + "PAS_adrsearch.asp?sz_userid=UMS&sz_passwd=MSG&n_count=20&n_sort=1&sz_language=" + 
			sz_language + 
			"&sz_address=" + 
			sz_address + 
			"&sz_no=" + 
			sz_no + 
			"&sz_postno=" + 
			sz_postno + 
			"&sz_postarea=" + 
			sz_postarea + 
			"&sz_region=" + 
			sz_region + 
			"&sz_country=" + 
			sz_country;
			Document doc = null;
			try {
				//doc = get_http().get_xml(sz_url, false, null);
				HTTPReq req = new HTTPReq(get_searchframe().get_pas().get_sitename());
				HttpPostForm form = new HttpPostForm(get_searchframe().get_pas().get_sitename() + "PAS_adrsearch.asp");
				form.setParameter("sz_userid", "UMS");
				form.setParameter("sz_passwd", "MSG");
				form.setParameter("n_count", "20");
				form.setParameter("n_sort", "1");
				form.setParameter("sz_language", "sz_language");
				form.setParameter("sz_address", sz_address);
				form.setParameter("sz_no", sz_no);
				form.setParameter("sz_postno", sz_postno);
				form.setParameter("sz_postarea", sz_postarea);
				form.setParameter("sz_region", sz_region);
				form.setParameter("sz_country", sz_country);
				
				//Document db = new Document(form.post());
				DocumentBuilder db = req.createNewDocumentBuilder();
				java.io.InputStream is = form.post();
				//byte [] b = new byte[2048];
				//is.read(b);
				//System.out.println(new String(b));
				doc = db.parse(is);
				
			} catch(Exception e) {
				stopped();
				Error.getError().addError("SearchPanelResultsAddrSearch","Exception in run",e,1);
			}
			if(doc==null)
			{
				try {
					Object[] obj_insert = { "", "<None>" , "", "", "" };
					m_tbl_list.insert_row(obj_insert, -1);
				} catch(Exception e) {
					Error.getError().addError("SearchPanelResultsAddrSearch","Exception in run",e,1);
				}
			}
			else {
				try {
					parseAdrDoc(doc);
				} catch(Exception e) {
					Error.getError().addError("SearchPanelResultsAddrSearch","Exception in run",e,1);
				}
			}*/
		}
		
		/*public void parseAdrDoc(Document doc)
		{
			if(doc==null)
				return;
				
			String[] attrRegion = {"Match", "Name", "Region", "Lon", "Lat" };
			String[] attrStreet = {"Match", "Name", "Region", "Lon", "Lat" };//, "Id"
			String[] attrPost   = {"Match", "Name", "PostNo", "Lon", "Lat" };
			String[] attrHouse  = {"Name", "Lon", "Lat"};
			if(m_tbl_list!=null) {
				m_tbl_list.clear();
			}
			
			NodeList nodesPost, nodesStreet, nodesRegion;
			try {
			    nodesPost = doc.getElementsByTagName( "Post" );
			    nodesStreet = doc.getElementsByTagName( "Street" );
			    nodesRegion = doc.getElementsByTagName("Region");
			} catch(Exception e) {
				Error.getError().addError("SearchPanelResultsAddrSearch","Exception in parseAdrDoc",e,1);
				return;
			}
		    Node current;
		    String[] sz_vals = new String[5];		    

		    if (nodesRegion != null)
		    {
		    	try {
		    		for(int i=0; i < nodesRegion.getLength(); i++) {
		    			current = nodesRegion.item(i);
		    			NamedNodeMap nnm = current.getAttributes();
		    			Node [] attr = get_http().getAttr(doc, nnm, attrRegion);
			    		for(int n_attr=0; n_attr < attr.length; n_attr++) {
			    			sz_vals[n_attr] = attr[n_attr].getNodeValue();
			    		}
			    		try {
							Object[] obj_insert = { sz_vals[0], sz_vals[1], sz_vals[2], sz_vals[3], sz_vals[4] }; //, m_icon_goto };
							get_tablelist().insert_row(obj_insert, -1);
			    		} catch(Exception e) {			    			
				    		Error.getError().addError("SearchPanelResultsAddrSearch","Exception in parseAdrDoc",e,1);		    		
			    		}
		    		}
		    	} catch(Exception e) {
		    		Error.getError().addError("SearchPanelResultsAddrSearch","Exception in parseAdrDoc",e,1);		    		
		    	}
		    }
		    if (nodesPost != null)
		    {
		    	try {
				    for(int i=0; i < nodesPost.getLength(); i++)
				    {
				    	current = nodesPost.item(i);
				    	NamedNodeMap nnm = current.getAttributes();
				    	Node[] attr = get_http().getAttr(doc, nnm, attrPost);
						for(int n_attr=0; n_attr < attr.length; n_attr++)
						{
							sz_vals[n_attr] = attr[n_attr].getNodeValue();
						}		    	
						Object[] obj_insert = { sz_vals[0], sz_vals[1], sz_vals[2], sz_vals[3], sz_vals[4] }; //, m_icon_goto };
						get_tablelist().insert_row(obj_insert, -1);
				    }
		    	} catch(Exception e) {
		    		Error.getError().addError("SearchPanelResultsAddrSearch","Exception in parseAdrDoc",e,1);
		    	}
		    }
		    if (nodesStreet != null)
		    {
		    	Node houseNode;
		    	NodeList list_houses;
		    	NamedNodeMap nnm_house;
			    for(int i=0; i < nodesStreet.getLength(); i++) {
			    	current = nodesStreet.item(i);
			    	NamedNodeMap nnm = current.getAttributes();
			    	Node[] attr = get_http().getAttr(doc, nnm, attrStreet);
				for(int n_attr=0; n_attr < attr.length; n_attr++) {
					sz_vals[n_attr] = attr[n_attr].getNodeValue();
				}		

				try {
					list_houses = current.getChildNodes();
				} catch(Exception e) {
					Error.getError().addError("SearchPanelResultsAddrSearch","Exception in parseAdrDoc",e,1);
					return;
				}
				if(list_houses.getLength() > 0)
				{
					for(int n_houses=0; n_houses < list_houses.getLength(); n_houses++)
					{
						houseNode = list_houses.item(n_houses);
						nnm_house = houseNode.getAttributes();
						if(nnm_house!=null)
						{
							String[] sz_housevals = new String[3];
							sz_housevals[0] = "";
							sz_housevals[1] = "";
							sz_housevals[2] = "";
							if(nnm_house.getNamedItem("Name")!=null)
								sz_housevals[0] = nnm_house.getNamedItem("Name").getNodeValue();
							if(nnm_house.getNamedItem("Lon")!=null)
								sz_housevals[1] = nnm_house.getNamedItem("Lon").getNodeValue();
							if(nnm_house.getNamedItem("Lat")!=null)
								sz_housevals[2] = nnm_house.getNamedItem("Lat").getNodeValue();
							
							if(nnm_house.getLength() > 0)
							{
								try {
									Object [] obj_houseinsert = { sz_vals[0], sz_vals[1] + " " + sz_housevals[0], sz_vals[2], sz_housevals[1], sz_housevals[2] }; //, m_icon_goto };
									get_tablelist().insert_row(obj_houseinsert, -1);
								} catch(Exception e) {
									Error.getError().addError(PAS.l("common_error"),"SearchPanelResultsAddrSearch Exception in parseAdrDoc",e,1);
								}
							}
						}
					}
				}
				else
				{
					try {
						Object[] obj_insert = { sz_vals[0], sz_vals[1], sz_vals[2], sz_vals[3], sz_vals[4] }; //, get_icon_goto() };
						get_tablelist().insert_row(obj_insert, -1);					
					} catch(Exception e) {
						Error.getError().addError(PAS.l("common_error"),"SearchPanelResultsAddrSearch Exception in parseAdrDoc",e,1);
					}
				}
				  	
			    }
		    }		    
		}*/
	
	}	
	
}
