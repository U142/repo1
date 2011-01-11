package no.ums.pas.importer.gis;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.JComponentCellEditor;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.ums.errorhandling.Error;



class PreviewList extends DefaultPanel implements ComponentListener {
	public static final long serialVersionUID = 1;
	class ComboField extends Object {
		public static final int FIELDID_EMPTY		= 0;
		public static final int FIELDID_MUNICIPALID = 1;
		public static final int FIELDID_STREETID	= 2;
		public static final int FIELDID_HOUSENO		= 4;
		public static final int FIELDID_LETTER		= 8;
		public static final int FIELDID_NAMEFILTER_INCLUSIVE_1 = 16;
		public static final int FIELDID_NAMEFILTER_INCLUSIVE_2 = 32;
		public static final int FIELDSUM_NEEDED		= FIELDID_MUNICIPALID | FIELDID_STREETID | FIELDID_HOUSENO | FIELDID_LETTER;
		
		public final String HEADING_SEARCH_MUNICIPALID [] = new String [] { "KOMMUN" };
		public final String HEADING_SEARCH_STREETID [] = new String [] { "VEJ", "GATENR" };
		public final String HEADING_SEARCH_HOUSENO [] = new String [] { "HUS" };
		public final String HEADING_SEARCH_LETTER [] = new String [] { "BOKSTAV", "BOGSTAV" };
		public final String HEADING_SEARCH_NAMEFILTER_INCLUSIVE_1 [] = new String [] { "FILTER1" };
		public final String HEADING_SEARCH_NAMEFILTER_INCLUSIVE_2 [] = new String [] { "FILTER2" };
		
		private String _sz_name;
		private int _n_id;
		private String [] _sz_search;
		public String toString() { return _sz_name; }
		public int get_id() { return _n_id; }
		public String [] get_search() { return _sz_search; }
		ComboField(int n_id, String sz_name) {
			_sz_name = sz_name;
			_n_id = n_id;
			switch(n_id) {
				case FIELDID_MUNICIPALID:
					_sz_search = HEADING_SEARCH_MUNICIPALID;
					break;
				case FIELDID_STREETID:
					_sz_search = HEADING_SEARCH_STREETID;
					break;
				case FIELDID_HOUSENO:
					_sz_search = HEADING_SEARCH_HOUSENO;
					break;
				case FIELDID_LETTER:
					_sz_search = HEADING_SEARCH_LETTER;
					break;
				case FIELDID_NAMEFILTER_INCLUSIVE_1:
					_sz_search = HEADING_SEARCH_NAMEFILTER_INCLUSIVE_1;
					break;
				case FIELDID_NAMEFILTER_INCLUSIVE_2:
					_sz_search = HEADING_SEARCH_NAMEFILTER_INCLUSIVE_2;
					break;
				default:
					_sz_search = new String[0];
					break;
			}
		}
	}
	class ComboAdrid extends JComboBox {
		public static final long serialVersionUID = 1;
		int m_n_column = -1;
		public void setColumnNo(int i) { m_n_column = i; }
		public int getColumnNo() { return m_n_column; }
		ComboAdrid(ComboField [] fields) {
			super(fields);
		}
		public void set_id(int ID) {
			select(ID);
		}
		public void select(int ID) {
			for(int i=0; i < this.getItemCount(); i++) {
				if(((ComboField)getItemAt(i)).get_id() == ID) {
					setSelectedIndex(i);
					return;
				}
			}
		}
		public boolean search_and_select(String sz_heading) {
			ComboField field;
			String [] search;
			for(int i=0; i < this.getItemCount(); i++) {
				field = (ComboField)this.getItemAt(i);
				search = field.get_search();
				for(int x=0; x < search.length; x++) {
					if(sz_heading.toUpperCase().indexOf(search[x]) >= 0) {
						set_id(field.get_id());
						return true;
					}
				}
			}
			return false;
		}
	}
	
	protected DefaultTableCellRenderer m_renderer = null;
	
	protected PreviewPanel m_parent;
	protected PreviewSearchPanel m_panel;
	public PreviewSearchPanel get_previewpanel() { return m_panel; }
	private GISFile m_gis;
	protected GISFile get_gis() { return m_gis; }
	private boolean m_b_firstline_heading = true;
	public boolean isFirstlineHeading() { return m_b_firstline_heading; }
	protected String [] m_sz_cols;
	private int SHOW_NUM_ROWS_ = 20;
	private int SELECTED_FIELDS_ = 0;
	protected int sum_fields(ComboAdrid combo) {
		deselect(combo);
		SELECTED_FIELDS_ = 0;
		for(int i=0; i < m_field_combos.length; i++) {
			ComboField field = (ComboField)m_field_combos[i].getSelectedItem();
			SELECTED_FIELDS_ |= field.get_id();
		}
		return SELECTED_FIELDS_;
	}
	private void deselect(ComboAdrid combo) {
		if(((ComboField)combo.getSelectedItem()).get_id()==0)
			return;
		for(int i=0; i < m_field_combos.length; i++) {
			ComboField field = (ComboField)m_field_combos[i].getSelectedItem();
			if(field.get_id()==((ComboField)combo.getSelectedItem()).get_id() && !m_field_combos[i].equals(combo)) {
				m_field_combos[i].setSelectedIndex(0);
				m_field_combos[i].grabFocus();
				repaint();
			}
		}
	}
	protected ComboAdrid [] m_field_combos;
	protected ComboField [] m_fields = new ComboField[] { 
			new ComboField(ComboField.FIELDID_EMPTY, ""), 
			new ComboField(ComboField.FIELDID_MUNICIPALID, "Municipal ID"),
			new ComboField(ComboField.FIELDID_STREETID, "Street ID"),
			new ComboField(ComboField.FIELDID_HOUSENO, "House no"),
			new ComboField(ComboField.FIELDID_LETTER, "Letter"),
			new ComboField(ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1, "Name filter1"),
			new ComboField(ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2, "Name filter2")
	};
	
	PreviewList(PreviewPanel parent, GISFile gis, DefaultTableCellRenderer renderer) {
		super();
		if(renderer == null)
			m_renderer = new DefaultTableCellRenderer();
		else
			m_renderer = renderer;
		m_parent = parent;
		m_gis = gis;
		init();
		addComponentListener(this);
		setSize(400, 300);
	}
	PreviewList(PreviewPanel parent, DefaultTableCellRenderer renderer) {
		super();
		if(renderer == null)
			m_renderer = new DefaultTableCellRenderer();
		else
			m_renderer = renderer;
		m_parent = parent;
		m_gis = null;
		addComponentListener(this);
		setSize(400, 300);
	}
	public void add_controls() {
		set_gridconst(0, 0, 1, 1, GridBagConstraints.CENTER);
		add(m_panel, get_gridconst());
		setVisible(true);

	}
	public void reSize(int x, int y) {
		x-=10;
		y-=10;
		setPreferredSize(new Dimension(x, y));
		revalidate();
		resize_children(x, y);
	}
	public void resize_children(int x, int y) {
		setPreferredSize(new Dimension(x - 10, y - 10));
		get_previewpanel().setPreferredSize(new Dimension(x - 20, y - 20));
		get_previewpanel().get_table().setPreferredSize(new Dimension(x - 30, y - 30));
		get_previewpanel().get_table().setSize(new Dimension(x - 30, y - 30));
		revalidate();
		get_previewpanel().revalidate();
		get_previewpanel().get_table().revalidate();
	}
	public void actionPerformed(ActionEvent e) {
		if("act_first_row_has_columnnames".equals(e.getActionCommand())) {
			m_b_firstline_heading = ((Boolean)e.getSource()).booleanValue();
			//get_previewpanel().start_search();
			set_columnnames();
		}
		else if("act_set_fieldid".equals(e.getActionCommand())) {
			ComboField field = (ComboField)((ComboAdrid)e.getSource()).getSelectedItem();
			int n = sum_fields((ComboAdrid)e.getSource());
			boolean b_goto_next;
			if((n & ComboField.FIELDSUM_NEEDED) == ComboField.FIELDSUM_NEEDED)
				b_goto_next = true;
			else
				b_goto_next = false;
			m_parent.actionPerformed(new ActionEvent(new Boolean(b_goto_next), ActionEvent.ACTION_PERFORMED, "act_goto_next_valid"));
		}

	}
	public void init() {
		m_sz_cols = new String[get_gis().get_parser().get_max_columns()];
		int n_width [] = new int[get_gis().get_parser().get_max_columns()];
		boolean b_editable [] = new boolean[get_gis().get_parser().get_max_columns()];
		for(int i=0; i < n_width.length; i++) {
			m_sz_cols[i] = "N/A";
			n_width[i] = 50;
			b_editable[i] = false;
		}
		Dimension dim = new Dimension(400, 300);
		try {
			m_panel = new PreviewSearchPanel(m_sz_cols, n_width, b_editable, dim);
		} catch(Exception e) {
			System.out.println("Error PreviewPanel " + e.getMessage());
			Error.getError().addError("PreviewList","Exception in init",e,1);
		}
		add_controls();
		setVisible(true);
		set_columnnames();
	}
	private void set_columnnames() {
		if(isFirstlineHeading()) {
			try {
				Object [] sz_cols = get_gis().get_parser().get_linedata().toArray(0, get_gis().get_parser().get_max_columns());
				for(int i=0; i < m_sz_cols.length; i++) {
					if(sz_cols.length > i)
						m_sz_cols[i] = sz_cols[i].toString();
					else
						m_sz_cols[i] = "N/A";
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				Error.getError().addError("PreviewList","Exception in set_columnnames",e,1);
			}
		} else {
			for(int i=0; i < m_sz_cols.length; i++) {
				m_sz_cols[i] = "N/A";
			}					
		}
		m_panel.set_columns(m_sz_cols);
		m_panel.start_search();
		determine_cols();		
	}
	private boolean determine_cols() {
		if(isFirstlineHeading())
			if(determine_cols_by_heading())
				return true;
		return determine_cols_by_dataguess();
	}
	private boolean determine_cols_by_heading() {
		if(isFirstlineHeading()) {
			//0	xKOMMUNKODE	xVEJKODE	xADRESSE	xHUSNUMMER	xBOGSTAVSAL	xPOSTNUMMER	STATIONNR	LAVAFGNR
			//adressenr	KOMMUNENR	GATENR	GATENAVN	HUSNR	BOKSTAV	
			LineData.Line data = get_gis().get_parser().get_linedata().get(0);
			String sz_temp;
			for(int i=0; i < data.get_fields().size(); i++) {
				try {
					sz_temp = data.get_fields().get(i).toString();
					m_field_combos[i].search_and_select(sz_temp);
				}catch(Exception e) {
					System.out.println(e.getMessage());
					Error.getError().addError("PreviewList","Exception in determine_cols_by_heading",e,1);
				}
			}
		}
		return false;
	}
	private boolean determine_cols_by_dataguess() {
		//0		xKOMMUNKODE	xVEJKODE	xADRESSE				xHUSNUMMER	xBOGSTAVSAL	xPOSTNUMMER	STATIONNR	LAVAFGNR
		//		461			7235		Sanderumvej	124			5250		955			104
		//adressenr	KOMMUNENR	GATENR	GATENAVN			HUSNR	BOKSTAV	
		//			626			1040	Alv Johnsens Vei	1	 	
		return true;
	}
	protected void clicked(int n_row, int n_col, Object [] obj, Point p) {
		
	}
	
	public class PreviewSearchPanel extends SearchPanelResults {
		public static final long serialVersionUID = 1;
	
		public PreviewSearchPanel(String [] sz_cols, int [] n_width, boolean [] b_editable, Dimension dim) {
			super(sz_cols, n_width, b_editable, dim, ListSelectionModel.SINGLE_SELECTION); 
			m_tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			try
			{
				initialize_combos();
			}
			catch(Exception e)
			{
				
			}
		}
		public void set_columns(String [] sz_columns) {
			super.set_columnnames(sz_columns);
		}
		public void onMouseLClick(int n_row, int n_col, Object [] obj, Point p) {
			clicked(n_row, n_col, obj, p);
		}
		public void onMouseLDblClick(int n_row, int n_col, Object [] obj, Point p) { }
		public void onMouseRClick(int n_row, int n_col, Object [] obj, Point p) {
			//onMouseLClick(n_row, n_col, obj, p);
		}
		public void onMouseRDblClick(int n_row, int n_col, Object [] obj, Point p) { }
		protected void valuesChanged() { }

		public void start_search() {
			Object [] obj;
			clear();
			this.get_table().setDefaultRenderer(JComponent.class, new JComponentCellRenderer());
			this.get_table().setDefaultEditor(JComponent.class, new JComponentCellEditor());
			insert_row(m_field_combos, -1);
			//initialize_combos();
			for(int i=(isFirstlineHeading() ? 1 : 0); 
			i < (SHOW_NUM_ROWS_==-1 ? get_gis().get_parser().get_linedata().get_lines().size() : (get_gis().get_parser().get_linedata().get_lines().size() < SHOW_NUM_ROWS_ ? get_gis().get_parser().get_linedata().get_lines().size() : SHOW_NUM_ROWS_)); i++) {
				try {
					insert_component_row(get_gis().get_parser().get_linedata().toArray(i, get_gis().get_parser().get_max_columns()), -1);
				} catch(Exception e)  {
					Error.getError().addError("PreviewList","Exception in start_search",e,1);
				}
			}
		}
		class JComponentCellRenderer implements TableCellRenderer {
		    public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
		    	return (JComponent)value;
		    }
		}
		public boolean is_cell_editable(int row, int col) { //overridden
			/*if(row==0)
				return true;
			return false;*/
			return editable(row, col);
		}

	    public void set_custom_cellrenderer(TableColumn column, int n_col)
	    {
	    	//if(m_renderer!=null)
	    	if(n_col==5 || n_col==6 || n_col==7 || n_col==13)
	    		column.setCellRenderer(new GISPercentColouredCellRenderer());
	    }

	}

	protected boolean editable(int row, int col) {
		if(row==0)
			return true;
		return false;
	}
	/*private void set_heading_renderer() {
		for(int i=0; i < get_gis().get_parser().get_max_columns(); i++) {
			TableColumn col = get_previewpanel().get_table().getColumnModel().getColumn(i);
			TblCellCombo combo = new TblCellCombo(COMBO_NEEDED_FIELDS_);
			col.setHeaderRenderer(combo);
		}
	}*/
	protected void initialize_combos() {
		try
		{
			m_field_combos = new ComboAdrid[get_gis().get_parser().get_max_columns()];
			for(int i=0; i < m_field_combos.length; i++) {
				m_field_combos[i] = new ComboAdrid(m_fields);
				m_field_combos[i].setColumnNo(i);
				m_field_combos[i].addActionListener(this);
				m_field_combos[i].addActionListener(m_parent);
				m_field_combos[i].setActionCommand("act_set_fieldid");
			}
		}
		catch(Exception e)
		{
			
		}
	}
	private int find_column(int COLUMNTYPE) {
		for(int i=0; i < m_field_combos.length; i++) {
			ComboField field = (ComboField)m_field_combos[i].getSelectedItem();
			if(field.get_id()==COLUMNTYPE) {
				return i;
			}
		}		
		return -1;
	}
	public int get_column_bytype(int COLUMNTYPE) {
		return find_column(COLUMNTYPE);
	}
	public void componentResized(ComponentEvent e) {
		try {
			get_previewpanel().setPreferredSize(new Dimension(getWidth(), getHeight()));
			revalidate();
		} catch(Exception err) {
			
		}
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }		
}
