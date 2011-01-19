package no.ums.pas.importer.gis;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.LetterPairSimilarity;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

class GISPercentColouredCellRenderer extends DefaultTableCellRenderer{
	public static final long serialVersionUID = 1;
   public GISPercentColouredCellRenderer() {
    }

    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);    //---
        /*if(column != 7)
        	return renderer;*/
        Inhabitant inhab = (Inhabitant)table.getValueAt(row, 7);
        //use color on namefilter1 or 2, name and percent (5,6,7,13)
        int namefilterrow = -1;
        if(inhab.get_namesearch_col()==1)
        	namefilterrow = 5;
        else if(inhab.get_namesearch_col()==2)
        	namefilterrow = 6;
        
        if(column != namefilterrow && column != 7) {
        	renderer.setBackground(Color.WHITE);
        	return renderer;
        }
        
        Color col;
        int p = (int)inhab.get_hitpercent();
        int r, g, b = 0, a = 40;
        r = (100 - p)*2;
        g = (p)*2;
        r = (r * 255 / 100);
        g = (g * 255 / 100);
        
        if(r < 0) r = 0;
        if(r > 255) r = 255;
        if(g < 0) g = 0;
        if(g > 255) g = 255;
        
        renderer.setBackground(new Color(r, g, b, a));
        return renderer;
    }
}


public class GISResultPanel extends PreviewList { //SearchPanelResults {

	public static final long serialVersionUID = 1;
	GISList m_gislist;
	GISFile m_gisfile;
	protected GISList get_gislist() { return m_gislist; }
	int [] m_n_width;
	int [] m_n_namefilter = new int[2];
	int n_col_object = 7;
	int n_col_include = 12;
	boolean m_b_run_hittest = true;
	
	public GISResultPanel(PreviewPanel parent, GISList gislist, String [] sz_columns, int [] n_col_width, Dimension dim) {
		super(parent, new GISPercentColouredCellRenderer());
		m_gislist = gislist;
		m_sz_cols = sz_columns;
		m_n_width = n_col_width;
		m_n_namefilter[0] = -1;
		m_n_namefilter[1] = -1;
		init();
	}
	
	public GISResultPanel(PreviewPanel parent, GISFile gisfile, String [] sz_columns, int [] n_col_width, Dimension dim) {
		//super(sz_columns, n_col_width, null, dim);//, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		super(parent, new GISPercentColouredCellRenderer());
		m_gisfile = gisfile;
		m_sz_cols = sz_columns;
		m_n_width = n_col_width;
		m_n_namefilter[0] = -1;
		m_n_namefilter[1] = -1;
		init();
		//get_previewpanel().set
	}
	public void actionPerformed(ActionEvent e) {
		if("act_set_fieldid".equals(e.getActionCommand())) {
			ComboAdrid combo = (ComboAdrid)e.getSource();
			ComboField field = (ComboField)combo.getSelectedItem();
			//ComboField field = (ComboField)((ComboAdrid)e.getSource()).getSelectedItem();
			if(field.get_id()==PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1) {
				m_n_namefilter[0] = combo.getColumnNo(); //field.get_id();
			}
			else if(field.get_id()==PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2) {
				m_n_namefilter[1] = combo.getColumnNo();
			}
			//start_search();
		}
	}
	

	
	protected boolean editable(int row, int col) { //overridden
		if(col==12)
			return true;
		return false;
	}

	public void fill(GISList gislist, boolean b_run_hittest) {
		m_gislist = gislist;
		//m_b_run_hittest = b_run_hittest;
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				start_search();
			}
		});
		//if(m_panel.get_tablelist().getRowCount() == 0)
		//	JOptionPane.showMessageDialog(this, "No results, are you on the right department?", "No results", JOptionPane.INFORMATION_MESSAGE);		
	}
	
	public void init() {
		Dimension dim = new Dimension(400, 300);
		try {
			
			m_panel = new PreviewSearchPanel(m_sz_cols, m_n_width, null, dim);
			add_controls();
			
		} catch(Exception e) {
			System.out.println("Error PreviewPanel " + e.getMessage());
			Error.getError().addError("PreviewList","Exception in init",e,1);
		}
	}
	public void add_controls() {
		set_gridconst(0, 0, 1, 1);
		// fjerner nåværende liste
		try
		{
			for(int i=getComponentCount()-1;i>=0;i--)
				remove(i);
		}
		catch(Exception e)
		{
			
		}
		add(m_panel, get_gridconst());
		setVisible(true);
	}

	public void start_search() {
		//"No", "Municipal", "Street ID", "House", "Letter", "Name", "Phone", "Mobile", "Postcode", "Place"
		try
		{
			int n_threshold = PAS.get_pas().get_settings().getGisDownloadDetailThreshold();
			m_panel.clear();
			if(get_gislist().size()<=n_threshold)
			{
				if(m_parent.get_loader()!=null)
					m_parent.get_loader().set_totalitems(get_gislist().size(), "Executing lexical textfilter");
				GISRecord house;
				int n_recordcount = 0;
				for(int h=0; h < get_gislist().size(); h++) {
					if(m_parent.get_loader()!=null)
						m_parent.get_loader().set_currentitem(h);
					house = (GISRecord)get_gislist().get(h);
					/*String sz_filter1, sz_filter2;
					sz_filter1 = "";
					sz_filter2 = "";*/
					//m_gisfile.get_parser().get_linedata().search_line()
					
					
					for(int i=0; i < house.get_inhabitantcount(); i++) {
						n_recordcount++;
						Inhabitant inhabitant = (Inhabitant)house.get_inhabitant(i);
						int hit = 0;
						Boolean b_include;
						if(m_b_run_hittest) {
							hit = namefilter(inhabitant, house.get_name1(), house.get_name2());
							b_include = hit >= 70;
						} else {
							hit = new Double(inhabitant.get_hitpercent()).intValue();
							b_include = inhabitant.get_included();
						}
						try
						{
							Object [] line = new Object[] {n_recordcount, house.get_municipal(), house.get_streetid(), house.get_houseno(),
															house.get_letter(), house.get_name1(), house.get_name2(), inhabitant, inhabitant.get_number(), inhabitant.get_mobile(),
															inhabitant.get_postno(), inhabitant.get_postarea(), b_include, hit, inhabitant.get_lon(), inhabitant.get_lat()};
							inhabitant.set_included(b_include);
							m_panel.insert_row(line, 0);
						}
						catch(Exception e)
						{
							
						}
					}
				}
				if(m_parent.get_loader()!=null)
				{
					m_parent.get_loader().set_currentitem(get_gislist().size());
					m_parent.get_loader().set_text(PAS.l("common_finished"));
					
				}

			}
			else	
			{
				m_panel.clear();
				m_panel.setToolTipText("No details available<br>The file lines limit for details is " + n_threshold);
			}
			updateStatistics();				
			// Hvis sz_filter1 og 2 er ikke er "" så skal det sorteres på hitprosent
			if(m_parent.get_previewlist()!=null)
			{
				if(!(m_parent.get_previewlist().get_column_bytype(ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1) == -1)
						|| !(m_parent.get_previewlist().get_column_bytype(ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2) == -1))
					m_panel.sort(13); // Dette er hitprosent
			}
			//m_b_run_hittest = false;
		}
		catch(Exception err)
		{
			
		}
		
	}
	private void updateStatistics() {
		m_parent.actionPerformed(new ActionEvent(get_gislist(), ActionEvent.ACTION_PERFORMED, "act_update_statistics"));
	}
	
	protected void clicked(int n_row, int n_col, Object [] obj, Point p) {
		Inhabitant i = (Inhabitant)obj[n_col_object];
		if(n_col==n_col_include) {
			Boolean b = (Boolean)obj[n_col_include];
			i.set_included(b.booleanValue());
			updateStatistics();
		}
		else {
			Variables.getNavigation().exec_adrsearch(i.get_lon(), i.get_lat(), 200);
		}
	}
	
	public int namefilter(Inhabitant inhab, String sz_filter1, String sz_filter2) {

		double n_hit = 0;
		double n_hit1 = -1;
		double n_hit2 = -1;
		if(sz_filter1.length() == 0 && sz_filter2.length() == 0) {
			inhab.set_hitpercent(100);
			return 100;
		}
		//LetterPairSimilarity comp = new LetterPairSimilarity();
		
		if(sz_filter1.length() > 0) {
			n_hit1 = LetterPairSimilarity.compareNames(sz_filter1, inhab.get_adrname(), true)*100;
		}
		if(sz_filter2.length() > 0) {
			n_hit2 = LetterPairSimilarity.compareNames(sz_filter2, inhab.get_adrname(), true)*100;
		}
		if(n_hit1 > n_hit2) inhab.set_namesearch_col(1); else inhab.set_namesearch_col(2);
		inhab.set_hitpercent(Math.max(n_hit1, n_hit2));
		return (int)inhab.get_hitpercent();
		//return (int)Math.max(n_hit1, n_hit2);
		
	}
		


	public  void onMouseLClick(int n_row, int n_col, Object[] rowcontent, Point p) {
		clicked(n_row, n_col, rowcontent, p);
	}


	public void onMouseLDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
		//clicked(n_row, n_col, rowcontent, p);
	}


	public void onMouseRClick(int n_row, int n_col, Object[] rowcontent, Point p) {
		
	}


	public void onMouseRDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
		
	}


	public void valuesChanged() {
		
	}
	public void initialize_combos() {
		m_field_combos = new ComboAdrid[get_gislist().size()];
		for(int i=0; i < m_field_combos.length; i++) {
			m_field_combos[i] = new ComboAdrid(m_fields);
			m_field_combos[i].addActionListener(this);
			m_field_combos[i].addActionListener(m_parent);
			m_field_combos[i].setActionCommand("act_set_fieldid");
		}
	}
	
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }		
	
}