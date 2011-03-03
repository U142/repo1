package no.ums.pas.importer.gis;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.ws.WSGisImport;
import no.ums.pas.core.ws.WSGisImport.GisColumnsetStreetid;
import no.ums.pas.importer.gis.PreviewList.ComboAdrid;
import no.ums.pas.importer.gis.PreviewList.ComboField;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class PreviewPanel extends DefaultPanel implements ComponentListener, ChangeListener {
	public static final long serialVersionUID = 1;
	private PreviewList m_previewlist;
	public PreviewList get_previewlist() { return m_previewlist; } 
	private LoadingPanel m_loader;
	public LoadingPanel get_loader() { return m_loader; }
	private JTabbedPane m_tab;
	private GISResultPanel m_resultpanel = null;
	public GISResultPanel get_resultpanel() { return m_resultpanel; }
	private SendObject m_sendobject;
	private ActionListener m_callbackframe;
	
	public PreviewPanel(SendObject so, boolean b_results_only, ActionListener callbackframe) {
		super();
		m_sendobject = so;
		m_callbackframe = callbackframe;
		if(b_results_only) {
			String [] sz_columns = new String[] { "No", "Municipal", "Street ID", "House", "Letter", "Namefilter1", "Namefilter2", "Name", "Phone", "Mobile", "Postcode", "Place", "", "Hit(%)", "Longitude", "Latitude" };
			int [] n_col_width = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30, 16, 16, 20, 20 };
			if(m_resultpanel == null)
				m_resultpanel = new GISResultPanel(this, so.get_sendproperties().typecast_gis().get_gislist(), sz_columns, n_col_width, new Dimension(600, 300));
			m_resultpanel.fill(so.get_sendproperties().typecast_gis().get_gislist(), false);
			addComponentListener(this);
			m_tab = new JTabbedPane();
			if(m_loader==null)
				m_loader = new LoadingPanel("GIS Import", new Dimension(100, 40), true);
			init();

		} else {
			m_loader = new LoadingPanel("GIS Import", new Dimension(100, 40), true);
			so.get_toolbar().showLoader(m_loader);			
		}
	}
	
	public PreviewPanel(GISFile gis, ActionListener callbackframe) {
		super();
		m_callbackframe = callbackframe;
		m_previewlist = new PreviewList(this, gis, null);
		m_loader = new LoadingPanel("GIS Import", new Dimension(100, 40), true);
		
		String [] sz_columns = new String[] { "No", "Municipal", "Street ID", "House", "Letter", "Namefilter1", "Namefilter2", "Name", "Phone", "Mobile", "Postcode", "Place", "", "Hit(%)", "Longitude", "Latitude" };
		int [] n_col_width = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30, 16, 16, 20, 20 };
		if(m_resultpanel==null)
			m_resultpanel = new GISResultPanel(this, gis, sz_columns, n_col_width, new Dimension(400, 300));
		
		addComponentListener(this);
		m_tab = new JTabbedPane();
		init();
	}
	protected void CreateGISResultPanel(GISList gis)
	{
		String [] sz_columns = new String[] { "No", "Municipal", "Street ID", "House", "Letter", "Namefilter1", "Namefilter2", "Name", "Phone", "Mobile", "Postcode", "Place", "", "Hit(%)", "Longitude", "Latitude" };
		int [] n_col_width = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30, 16, 16, 20, 20 };
		m_resultpanel = new GISResultPanel(this, gis, sz_columns, n_col_width, new Dimension(400, 300));
		m_tab = new JTabbedPane();
	}
	public void init() {
		add_controls();
		setVisible(true);
	}

	public void reSize(int x, int y) {
		x-=10;
		y-=10;
		setPreferredSize(new Dimension(x, y));
		m_tab.setPreferredSize(new Dimension(x, y));
		revalidate();
		resize_children(x, y);
	}
	public void resize_children(int x, int y) {
		m_previewlist.reSize(x, y);
		if(get_resultpanel()!=null)
			m_resultpanel.reSize(x, y);
	}
	
	public void actionPerformed(final ActionEvent e) {
		if("act_fetch_addresses".equals(e.getActionCommand())) { //actionevent from PreviewOptions.JButton => PreviewFrame
			int n_col_municipal = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID);
			int n_col_streetid	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID);
			int n_col_houseno	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO);
			int n_col_letter	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER);
			int n_col_namefilter1 = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1);
			int n_col_namefilter2 = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2);
			int n_skiplines		= (get_previewlist().isFirstlineHeading() ? 1 : 0);
			String sz_separator = get_previewlist().get_gis().get_parser().get_separator();
			System.out.println(n_col_municipal + " " + n_col_streetid + " " + n_col_houseno + " " + n_col_letter + " (skip:" + n_skiplines + " sep:" + sz_separator + ")");
			try {
				String sz_sitename;
				try {
					sz_sitename = PAS.get_pas().get_sitename();
				} catch(Exception err) {
					sz_sitename = "http://vb4utv/";
					Error.getError().addError("PreviewPanel","Exception in actionPerformed",err,1);
				}
				File f_post = create_umsgisfile(get_previewlist().get_gis().get_parser().get_file(), 
								  get_previewlist().get_gis().get_parser().get_linedata(), 
								  n_col_municipal, n_col_streetid, n_col_houseno, n_col_letter, n_skiplines, n_col_namefilter1, n_col_namefilter2, sz_separator);
				
				/*HttpPostForm form = new HttpPostForm(sz_sitename + "PAS_gisdata.asp");
				form.setParameter("n_col_municipal", new Integer(n_col_municipal).toString());
				form.setParameter("n_col_streetid",  new Integer(n_col_streetid).toString());
				form.setParameter("n_col_houseno",   new Integer(n_col_houseno).toString());
				form.setParameter("n_col_letter", 	 new Integer(n_col_letter).toString());
				form.setParameter("n_col_namefilter1", new Integer(n_col_namefilter1).toString());
				form.setParameter("n_col_namefilter2", new Integer(n_col_namefilter2).toString());
				form.setParameter("n_skiplines", 	 new Integer(n_skiplines).toString());
				form.setParameter("sz_separator",    sz_separator);
				form.setParameter("file", f_post); //get_previewlist().get_gis().get_parser().get_file());
				XMLGis gis = new XMLGis(null, form, sz_sitename, this, "act_gis_imported", get_loader());
				gis.start();*/
				WSGisImport gis = new WSGisImport(this, "act_gis_imported", m_loader);
				//GisColumnsetStreetid colset = gis.newGisColumnsetStreetid(n_col_municipal, n_col_streetid, n_col_houseno, n_col_letter, n_col_namefilter1, n_col_namefilter2, n_skiplines, sz_separator, f_post);
				GisColumnsetStreetid colset = gis.newGisColumnsetStreetid(0, 1, 2, 3, 4, 5, n_skiplines, sz_separator, f_post);
				gis.setColSet(colset);
				gis.start();
				
				
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("PreviewPanel","Exception in actionPerformed",err,1);
			}
			
		}
		else if("act_autofetch_addresses".equals(e.getActionCommand())) {
			try {
				String sz_sitename;
				try {
					sz_sitename = PAS.get_pas().get_sitename();
				} catch(Exception err) {
					sz_sitename = "http://vb4utv/";
					Error.getError().addError("PreviewPanel","Exception in actionPerformed",err,1);
				}
				File f_post = (File)e.getSource();
				
				/*HttpPostForm form = new HttpPostForm(sz_sitename + "PAS_gisdata.asp");
				form.setParameter("n_col_municipal", new Integer(0).toString());
				form.setParameter("n_col_streetid",  new Integer(1).toString());
				form.setParameter("n_col_houseno",   new Integer(2).toString());
				form.setParameter("n_col_letter", 	 new Integer(3).toString());
				form.setParameter("n_col_namefilter1", new Integer(4).toString());
				form.setParameter("n_col_namefilter2", new Integer(5).toString());
				form.setParameter("n_skiplines", 	 new Integer(0).toString());
				form.setParameter("sz_separator",    "	");
				form.setParameter("file", f_post); //get_previewlist().get_gis().get_parser().get_file());
				//LoadingFrame loader = new LoadingFrame("Parsing GIS", null);
				//loader.set_totalitems(0, "GIS");
				//loader.setVisible(true);
				XMLGis gis = new XMLGis(null, form, sz_sitename, this, "act_gis_imported", get_loader());
				gis.start();*/
				WSGisImport gis = new WSGisImport(this, "act_gis_imported", m_loader);
				GisColumnsetStreetid colset = gis.newGisColumnsetStreetid(0, 1, 2, 3, 4, 5, 0, "	", f_post);
				gis.setColSet(colset);
				gis.start();
				
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("PreviewPanel","Exception in actionPerformed",err,1);
			}
		}
		else if("act_gis_imported".equals(e.getActionCommand())) {
			System.out.println("GIS Download complete");
			final ActionEvent e2 = e;
			boolean b_dofill_results = false;
			if(get_resultpanel() == null)
			{
				CreateGISResultPanel((GISList)e2.getSource());
				b_dofill_results = true;
			}
			try
			{
				m_tab.addTab("Results", null,
								m_resultpanel,
								"GIS Import results");
				final boolean dofill = b_dofill_results;
				//SwingUtilities.invokeLater(new Runnable()
				{
				//	public void run()
					{
						if(m_loader!=null)
						{
							try
							{
								get_resultpanel().fill((GISList)e2.getSource(), dofill);
								if(get_previewlist()!=null)
									get_previewlist().get_gis().get_callback().actionPerformed(e2); // Denne må jeg få med meg ned i else'en
								m_tab.setSelectedComponent(get_resultpanel());
								if(m_sendobject!=null)
								{
									m_sendobject.get_toolbar().hideLoader(m_loader);
									m_sendobject.get_toolbar().set_sendingtype();
									if(dofill)
										m_sendobject.get_toolbar().get_import_callback().actionPerformed(e);
								}
								get_resultpanel().componentResized(null);
							}
							catch(Exception err)
							{
								err.printStackTrace();
								if(m_sendobject == null)
									System.out.println("Error on GIS Import: m_sendobject was null " + err.getMessage() + " \n" + err.getStackTrace().toString());
								else
									System.out.println("Error on GIS Import: " + err.getMessage() + " \n" + err.getStackTrace().toString());
							}
						}
					}
				}
				//});
				//if(get_resultpanel().get_previewpanel().get_tablelist().getRowCount() == 0)
				//	JOptionPane.showMessageDialog(this, "No results, are you on the right department?", "No results", JOptionPane.INFORMATION_MESSAGE);
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
			/*else { // Dette vil bli kjørt ved autofetch_addresses
				//m_sendobject.get_toolbar().remove(m_loader);
				if(m_loader!=null)
				{
					m_sendobject.get_toolbar().hideLoader(m_loader);
				}
				m_sendobject.get_toolbar().set_sendingtype();
				m_sendobject.get_toolbar().get_import_callback().actionPerformed(e);
			}*/
		}
		else if("act_gis_finish".equals(e.getActionCommand())) { //no results (for alert only)
			int n_col_municipal = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID);
			int n_col_streetid	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID);
			int n_col_houseno	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO);
			int n_col_letter	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER);
			int n_col_namefilter1 = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1);
			int n_col_namefilter2 = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2);
			int n_skiplines		= (get_previewlist().isFirstlineHeading() ? 1 : 0);
			String sz_separator = get_previewlist().get_gis().get_parser().get_separator();
			
			File f_post = create_umsgisfile(get_previewlist().get_gis().get_parser().get_file(), 
					  get_previewlist().get_gis().get_parser().get_linedata(), 
					  n_col_municipal, n_col_streetid, n_col_houseno, n_col_letter, n_skiplines, n_col_namefilter1, n_col_namefilter2, sz_separator);
			GISList gislist = create_gislist(f_post, sz_separator);
			get_previewlist().get_gis().get_callback().actionPerformed(new ActionEvent(gislist, ActionEvent.ACTION_PERFORMED, "act_gis_imported"));

		}
		else if("act_set_fieldid".equals(e.getActionCommand())) { //callback from previewlist. send namefilter columns to results
			ComboField field = (ComboField)((ComboAdrid)e.getSource()).getSelectedItem();
			if(field.get_id()==PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1 || field.get_id()==PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2) {
				get_resultpanel().actionPerformed(e);
			}
		}
		else if("act_show_results".equals(e.getActionCommand())) {
			m_tab.addTab("Results", null,
					m_resultpanel,
					"GIS Import results");
			get_resultpanel().fill((GISList)e.getSource(), true);
			get_resultpanel().componentResized(null);
			//get_previewlist().get_gis().get_callback().actionPerformed(e); // Denne må jeg få med meg ned i else'en
			m_tab.setSelectedComponent(get_resultpanel());
		}
		else if("act_update_statistics".equals(e.getActionCommand())) {
			m_callbackframe.actionPerformed(e); //report to frame, it will report to statisticspanel
		}
		else if("act_goto_next_valid".equals(e.getActionCommand())) {
			m_callbackframe.actionPerformed(e);
		}

	}
	public GISList create_gislist(File f, String sz_separator) {
		GISList list = new GISList();
		String sz_line;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			while(1==1) {
				sz_line = br.readLine();
				if(sz_line==null)
					break;
				sz_line = sz_line.replace("\t", "\t ");
				String [] sz_cols = sz_line.split(sz_separator);
				for(int i=0; i < sz_cols.length; i++) {
					sz_cols[i] = sz_cols[i].trim();
					sz_cols[i] = sz_cols[i].replace("&", "&amp;");
					sz_cols[i] = sz_cols[i].replace(">", "&gt;");
					sz_cols[i] = sz_cols[i].replace("<", "&lt;");
					
				}
				list.add(sz_cols);
			}
			br.close();
		} catch(Exception e) {
			
		}
		return list;
	}
	public File create_umsgisfile(File f, LineData linedata, int n_mun, int n_str, int n_hou, int n_let, int n_skip, int n_namefilter1, int n_namefilter2, String sz_sep) {
		System.out.println(f.getName());
		File f_ums;
		try {
			f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + f.getName());
		} catch(Exception e) {
			//f_ums = new File("C:\\Program Files\\UMS Population Alert System\\GIS\\tmp_" + f.getName());
			Error.getError().addError("PreviewPanel","Exception in create_umsgisfile",e,1);
			return null;
		}
		try {
			GISWriter writer = new GISWriter(linedata, f_ums);
			return writer.convert(n_mun, n_str, n_hou, n_let, n_namefilter1, n_namefilter2, n_skip);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("PreviewPanel","Exception in create_umsgisfile",e,1);
		}
		return null;
	}
	public void add_controls() {
		m_tab.addChangeListener(this);
		if(m_sendobject==null) {
			m_tab.addTab("File content", null,
					m_previewlist,
					"GIS Import File");
			/*m_tab.addTab("Results", null,
					m_loader,
					"GIS Import results");*/
			set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
			add(m_tab, get_gridconst());
	/*		set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
			add(m_previewlist, get_gridconst());*/
			set_gridconst(0, 1, 1, 1, GridBagConstraints.NORTH);
			if(m_loader!=null)
				add(m_loader, get_gridconst());
		} else {
			m_tab.addTab("Results", null,
						m_resultpanel,
						"Results");
			set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
			add(m_tab, get_gridconst());
			
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		JTabbedPane pane = (JTabbedPane)e.getSource();
		if(pane.getSelectedComponent().equals(m_resultpanel)) {
			//activate statistics
			m_callbackframe.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_statistics_view"));
		} else if(pane.getSelectedComponent().equals(m_previewlist)) {
			//activate options
			m_callbackframe.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_options_view"));			
		}
	}
	
	public void componentResized(ComponentEvent e) {
		m_tab.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
		if(m_previewlist!=null) {
			m_previewlist.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
			m_previewlist.revalidate();
		}
		if(m_loader!=null)
			m_loader.setPreferredSize(new Dimension(getWidth(), 0));
		if(get_resultpanel()!=null) {
			m_resultpanel.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
			m_resultpanel.revalidate();
		}
		revalidate();
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }	
}