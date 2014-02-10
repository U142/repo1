package no.ums.pas.core.defines;


import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.importer.csv.csvexporter;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ColorButton;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;



public abstract class SearchPanelResults extends JPanel implements ComponentListener, TableModelListener //JPanel 
{

    private static final Log log = UmsLog.getLogger(SearchPanelResults.class);

	/*@Override
	public void actionPerformed(ActionEvent e) {
		
	}*/
	private GridLayout	m_gridlayout;
	public JTable m_tbl;
	public TableList m_tbl_list;
	private AdressTblListener m_listener;
	protected JScrollPane scrollPane;
	//public ImageIcon m_icon_goto;
	private boolean [] m_b_editable;
	public JScrollPane get_scrollpane() { return scrollPane; }
	protected TableSorter sorter;
	public TableSorter get_tablesorter() {
    	return sorter;
    }
	
	
	StdTextLabel lbl_loading = new StdTextLabel("");
    private JLabel m_lbl_no_results = new JLabel(PAS.l("common_search_no_results"));
	boolean b_loading = false;
	ImageIcon ico = null;
	
	public void setLoading(final boolean b)
	{
		if(b)
		{
			setNoResults(false);
		}
		b_loading = b;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				lbl_loading.setVisible(b);
			}
		});
		//scrollPane.setVisible(!b);
	
		//scrollPane.setComponentZOrder(lbl_loading, 20);
		//lbl_loading.setComponentZOrder(scrollPane, 1);
		/*if(b)
			add(lbl_loading, BorderLayout.CENTER);
		else
			remove(lbl_loading);*/
		
	}
	
	public void setNoResults(final boolean b)
	{
		if(b)
		{
			setLoading(false);
		}
		m_lbl_no_results.setVisible(b);
	}
	
	public void CreateOnMouseOver()
	{
		m_tbl.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int idx = m_tbl.rowAtPoint(e.getPoint());
				//m_tbl.changeSelection(idx, -1, false, true);
			}
			
		});
		
	}
	public SearchPanelResults(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension panelDimension)
	{
		this(sz_columns, n_width, b_editable, panelDimension, ListSelectionModel.SINGLE_SELECTION);
		//m_tbl.changeSelection(rowIndex, columnIndex, toggle, extend)
	}
	JLayeredPane layer = new JLayeredPane();
	public JLayeredPane getLayeredPane() { return layer; }

	public SearchPanelResults(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension panelDimension, int model)
	{
		this(sz_columns, n_width, b_editable, panelDimension, model, true);
	}
	
	public final Color DEFAULT_SELECTION_COLOR = new JTable().getSelectionBackground();
	public final Color ALTERNATING_BG_COLOR_1 = new JTable().getBackground();
	public final Color ALTERNATING_BG_COLOR_2 = SystemColor.control;
	public final Color FOREGROUND_SELECTION_COLOR = new JTable().getSelectionForeground();
	public final Color FOREGROUND_COLOR = new JTable().getForeground();
	
	public Color getBgColorForRow(int row)
	{
		boolean bSelected = get_table().isRowSelected(row);
		if (row % 2 == 0 && !bSelected) {
			return ALTERNATING_BG_COLOR_1;
		}
		else if(bSelected)
		{
			return DEFAULT_SELECTION_COLOR;
		}
		else
		{
			return ALTERNATING_BG_COLOR_2;
		}
	}
	
	public Color getFgColorForRow(int row)
	{
		return (get_table().isRowSelected(row) ? FOREGROUND_SELECTION_COLOR : FOREGROUND_COLOR);
	}
	

	
	@Override
	public void tableChanged(TableModelEvent e) {
		
	}

	public SearchPanelResults(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension panelDimension, int model, boolean b_enable_sort)
	{
	        //super(new GridLayout(1,0));
			super();
			setLayout(new GridLayout(1,1));
			//setLayout(new BorderLayout());
			addComponentListener(this);
	        m_tbl_list = new TableList(sz_columns, n_width);
			sorter = new TableSorter(m_tbl_list);	        		
	        
	        m_tbl = new JTable(sorter) {
				@Override
				public Component prepareRenderer(TableCellRenderer renderer,
						int row, int column) {
					Component comp = super.prepareRenderer(renderer, row, column);
					comp.setBackground(SearchPanelResults.this.getBgColorForRow(row));
					comp.setForeground(SearchPanelResults.this.getFgColorForRow(row));
					return super.prepareRenderer(renderer, row, column);
				}
	        	
	        };
	        m_tbl.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					Point p = e.getPoint();
					int row = m_tbl.rowAtPoint(p);
					int column = m_tbl.getColumnModel().getColumnIndexAtX(p.x);
					Object o = m_tbl.getValueAt(row, 0);
					if(o instanceof TooltipItem)
					{
						TooltipItem tip = (TooltipItem)o;
						m_tbl.setToolTipText(tip.toTooltipString(column));
					}
					//super.mouseMoved(e);
				}
	        	
			});
	        if(b_enable_sort)
	        {
		        m_tbl.getTableHeader().setDefaultRenderer(m_tbl.getTableHeader().getDefaultRenderer());
		        SwingUtilities.invokeLater(new Runnable() {
		        	public void run()
		        	{
                        sorter.setTableHeader(m_tbl.getTableHeader());
		        	}
		        });
	        	/*m_tbl.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
	        		
					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						//Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						//		row, column);
						this.setText(value.toString());
						this.setHorizontalAlignment(SwingConstants.CENTER);
						return this;
					}
	        		
	        	});
	        	SwingUtilities.invokeLater(new Runnable() {
		        	public void run()
		        	{
			        	sorter.setTableHeader(m_tbl.getTableHeader());
		        	}
	        	});*/
	        }

	        
			m_b_editable = b_editable;
        	m_tbl.setSelectionMode(model);
        	m_tbl.setCellSelectionEnabled(false);
        	m_tbl.setRowSelectionAllowed(true);
        	m_tbl.setColumnSelectionAllowed(true);
        	

	        //setBounds(0,0,panelDimension.width, panelDimension.height);
	        //m_tbl.setPreferredSize(new Dimension(panelDimension.width, panelDimension.height));
	        lbl_loading.setBounds(0, 0, panelDimension.width, panelDimension.height);
	        m_tbl.setPreferredScrollableViewportSize(panelDimension); //new Dimension(800, 200));
	        //Create the scroll pane and add the table to it.
	        try
	        {
	        	ico = ImageLoader.load_icon("bigrotation2.gif");
    			lbl_loading.setIcon(ico);
    			lbl_loading.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
    			m_lbl_no_results.setHorizontalAlignment(SwingConstants.CENTER);
    			m_lbl_no_results.setVerticalAlignment(SwingConstants.CENTER);
    			m_lbl_no_results.setFont(m_lbl_no_results.getFont().deriveFont(25.0f));
    			m_lbl_no_results.setVisible(false);
    			m_lbl_no_results.setOpaque(false);
    			m_lbl_no_results.setForeground(new Color(0,0,0,100));    			
	        }
	        catch(Exception e) { }
	        try
	        {
		        scrollPane = new JScrollPane(m_tbl);
		        
		        //Add the scroll pane to this panel.
		        //set_gridconst(0, 0, 1, 1, GridBagConstraints.CENTER);
		        //add(lbl_loading, get_gridconst());
		        //set_gridconst(0, 1, 1, 1, GridBagConstraints.CENTER);
		        
		        add(layer);
		        
		        layer.add(scrollPane, 1,0);//, get_gridconst());
		        //scrollPane.setBounds(0, 0, 300, 200);
		        //add(lbl_loading, new Integer(1));
		        //add(lbl_loading, BorderLayout.WEST);
		        //lbl_loading.setVisible(false);
		        layer.add(lbl_loading, 2,0);
		        layer.add(m_lbl_no_results, 3, 0);
		        m_tbl_list.initialize();
		        setLoading(false);
		        
		        m_listener = new AdressTblListener();
	        	TableColumnModel colSM = m_tbl.getColumnModel();
	        	colSM.addColumnModelListener(m_listener);
		        ListSelectionModel rowSM = m_tbl.getSelectionModel();
		        rowSM.addListSelectionListener(m_listener);
		        colSM.setColumnSelectionAllowed(true);
		        m_tbl.addMouseListener(m_listener);
				m_tbl.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				m_tbl_list.addTableModelListener(this);
	        }
	        catch(Exception e)
	        {
	        	
	        }
	        //CreateOnMouseOver();
			/*final JTableHeader tableHeader = m_tbl.getTableHeader();
			tableHeader.addMouseListener( new MouseAdapter() {
			  public void mouseClicked(MouseEvent e) {
			    int x = e.getX();
			    int y = e.getY();
			    int columnIndex = tableHeader.columnAtPoint( new Point(x,y) );
			    sort( columnIndex );
			  }
			} );*/
	        
	}
	public void set_columnnames(String [] sz_columns) {
    	m_tbl_list.columnNames = sz_columns;
    	m_tbl_list.fireTableStructureChanged();
    	sorter.fireTableStructureChanged();
	}
	/*public SearchPanelResults(String [] sz_columns, Integer [] n_width, Boolean [] b_editable, Dimension dim, int n_model) {
		//this(sz_columns, width, editable, dim, n_model);
	}*/

	//HTTPReq get_http() { return new HTTPReq(get_pas()); }/*return get_pas().get_httpreq(); }*/
	//public HTTPReq get_http() { return get_pas().get_httpreq(); }

	public TableList get_tablelist() { return m_tbl_list; }
	public JTable get_table() { return m_tbl; }
	//ImageIcon get_icon_goto() { return m_icon_goto; }
	
	protected abstract void start_search();
	
	protected abstract void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p);
	protected abstract void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p);
	protected abstract void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p);
	protected abstract void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p);
	protected abstract void valuesChanged();
	public void sort(int n_col) {
		//log.debug("sort by column " + n_col);
		sorter.setSortingStatus(n_col, TableSorter.ASCENDING);
	}
	
	/***
	 * 
	 * @param n_col column number
	 * @param DIRECTION TableSorter.ASCENDING = 1, TableSorter.DESCENDING = -1, TableSorter.NOT_SORTED = 0
	 */
	public void sort(int n_col, int DIRECTION)
	{
		sorter.setSortingStatus(n_col, DIRECTION);
	}
	
	void prepare_controls()
	{
		
	}
	public void add_controls()
	{
		
	}	
	public void init()
	{
	}
	public void insert_row(Object [] data, int n_index)
	{
		this.insert_row(data, n_index, true);
	}
	public void insert_row(Object [] data, int n_index, boolean bAutoUpdate)
	{
		get_tablelist().insert_row(data, n_index, bAutoUpdate);
	}
	public void insert_component_row(Object [] data, int n_index) {
		get_tablelist().insert_component_row(data, n_index);
	}
	public void remove_row(Object data) {
		try
		{
			get_tablelist().remove_row(data);
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
	}
	public void edit_row(Object [] data, int n_index, boolean [] update_cols) {
		int x = m_tbl.getSelectedRow();
		for(int i=0; i < data.length; i++)
		{
			if(update_cols==null ||update_cols[i]==true) 
				get_tablelist().setValueAt(data[i], n_index, i);
		}
		m_tbl.getSelectionModel().setSelectionInterval(x, x);
	}
	int n_selection = -1;
	public void pushSelection()
	{
		n_selection = m_tbl.getSelectedRow();
	}
	public void popSelection()
	{
		if(n_selection>=0)
			m_tbl.getSelectionModel().setSelectionInterval(n_selection, n_selection);
	}
	public void setValueAt(Object data, int idx, int col)
	{
		try
		{
			get_tablelist().setValueAt(data, idx, col);
		}
		catch(Exception e)
		{
			
		}
	}
	public void clear()
	{
		get_tablelist().clear();
		//this.repaint();
	}
    public void set_custom_cellrenderer(TableColumn column, int n_col)
    {
    	
    }
    
	public void SetColumnClass(int n_column, Class type)
	{
		TableColumn column = m_tbl.getColumnModel().getColumn(n_column);
		column.setCellRenderer(newTableCellRenderer(type));
	}
	public void SetRowClass(int n_column, TableCellRenderer renderer)
	{
		TableColumn column = m_tbl.getColumnModel().getColumn(n_column);
		column.setCellRenderer(renderer);
	}
	
	public class TableDateFormatter
	{
		long date;
		public TableDateFormatter(long n)
		{
			this.date = n;
		}
		public String toString(){
			return no.ums.pas.ums.tools.TextFormat.format_datetime(this.date);
		}
		public long getDate() { return date; }
	}
	public TableDateFormatter newTableDateFormatter(long date)
	{
		return new TableDateFormatter(date);
	}
	
	protected DefaultTableCellRenderer newTableCellRenderer(final Class<Object> type)
	{
		return new DefaultTableCellRenderer() {
			public static final long serialVersionUID = 1; 
			public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				//return type.getClass().cast(value);//;
				if(JProgressBar.class.equals(type))
				{
					return (JProgressBar)value;
				}
				else if(JLabel.class.equals(type))
				{
					return (JLabel)value;
				}
				else if(type.equals(Inhabitant.class))
				{
					
					Inhabitant inhab = (Inhabitant)value;
					JLabel ret;
					ret = inhab.isVulnerable() ? new JLabel(inhab.get_adrname(), ImageFetcher.getIcon("bandaid_16.png"), JLabel.LEFT) : new JLabel(inhab.get_adrname());
					ret.setOpaque(true);
					ret.setBackground(getBgColorForRow(row));
					ret.setForeground(inhab.isVulnerable() ? Color.red : getFgColorForRow(row));
					
					return ret;
				}
				else if(no.ums.pas.core.mainui.OpenStatuscodes.StatusCodeRenderer.class.equals(type))
					return (no.ums.pas.core.mainui.OpenStatuscodes.StatusCodeRenderer)value;
				else if(StdTextArea.class.equals(type))
						return (StdTextArea)value;
				else if(StdTextLabel.class.equals(type))
						return (StdTextLabel)value;
				else if(JTextField.class.equals(type))
					return (JTextField)value;
				else if(JTextArea.class.equals(type))
					return (JTextArea)value;
				return (DefaultTableCellRenderer)value;
			}
		};
	}
   
    public int find(int n_column, int n_value) {
    	for(int i=0; i < m_tbl_list.getRowCount(); i++) {
    		int n_temp = -1;
    		try {
    			n_temp = new Integer((String)m_tbl_list.getValueAt(i, n_column)).intValue();
    		} catch(Exception e) {
    			Error.getError().addError(Localization.l("common_error"),"Exception in find",e,1);
    		}
    		if(n_temp == n_value) {
    			return i;
    		}
    	}
    	return -1;
    }
    public int find(Object o, int n_col, Class expect) {
    	for(int i=0; i < m_tbl_list.getRowCount(); i++) {
    		try {
    			if(expect.cast(o).equals(expect.cast(m_tbl_list.getValueAt(i, n_col)))) 
    				return i;
    		} catch(Exception e) {
    			/*log.debug(e.getMessage());
    			log.warn(e.getMessage(), e);
    			Error.getError().addError("SearchPanelResults","Exception in find",e,1);*/
    		}
    	}
    	return -1;
    }
    

    
    public boolean delete_row(int row)
    {
    	try
    	{
    		int modelRow = get_table().convertRowIndexToModel(row);
    		get_tablelist().remove_row(modelRow);
	    	get_tablelist().fireTableRowsDeleted(modelRow, modelRow);
	    	return true;
    	}
    	catch(Exception e)
    	{
    		log.warn(e.getMessage(), e);
    	}
    	return false;
    }
    public boolean delete_row(Object o, int n_objcol, Class expect) {
    	try {
    		int i = find(o, n_objcol, expect);
    		if(i >= 0) {
            	for(int x=0; x < m_tbl_list.m_data.length; x++)
            	{
            		try {
	            		((ArrayList<Object>)m_tbl_list.m_data[x]).remove(i);
	            		m_tbl_list.fireTableRowsDeleted(i, i);
            		} catch(Exception e) {
            			log.debug(e.getMessage());
            			log.warn(e.getMessage(), e);
            			Error.getError().addError(Localization.l("common_error"),"Exception in delete_row",e,1);
            		}
            	}

	    		sorter.fireTableDataChanged();
	    		m_tbl_list.fireTableDataChanged();
	    		return true;
    		}
    		return false;
    	} catch(Exception e) {
    		log.debug(e.getMessage());
    		log.warn(e.getMessage(), e);
    		Error.getError().addError(Localization.l("common_error"),"Exception in delete_row",e,1);
    	}
    	return false;
    }
	public boolean is_cell_editable(int col) {
    	if(m_b_editable==null)
        	return false;
    	else
    		return m_b_editable[col];
	}
    public abstract boolean is_cell_editable(int row, int col);
	
    
    public void export(boolean b_headers) {
    	String sz_path = exportFileSelect();
    	if(sz_path!=null) {
    		exportToFile(this.sorter, sz_path, b_headers);
    	}
    }
	public void exportToFile(TableSorter table, String filepath, boolean writeTableHeaders){
		File exportFile = new File(filepath);
		try {
			FileWriter f = new FileWriter(exportFile);
			BufferedWriter b = new BufferedWriter(f);
			PrintWriter p = new PrintWriter(b);
			
			if(writeTableHeaders){
				for(int i=0;i<table.getColumnCount();i++) {
					if(table.getColumnClass(i).equals(javax.swing.ImageIcon.class))
						p.print("IMG\t");
					else if(table.getColumnClass(i).equals(JButton.class))
						;// Hvis det er knapp skal det ikke gjøres noe
					else if(table.getColumnClass(i).equals(JCheckBox.class))
						p.print(table.getColumnName(i) + "\t");
					else if(table.getColumnClass(i).equals(JRadioButton.class))
						p.print(table.getColumnName(i) + "\t");
					else if(table.getColumnClass(i).equals(ColorButton.class))
						p.print(table.getColumnName(i) + "\t");
					else
						p.print(table.getColumnName(i) + "\t");
				}
				p.print("\n");
			}
			for(int i=0;i<table.getRowCount();i++){
				for(int j=0;j<table.getColumnCount();j++) {
					if(table.getColumnClass(j).equals(javax.swing.ImageIcon.class))
						p.print("img\t");
					else if(table.getColumnClass(j).equals(JButton.class))
						;// Hvis det er knapp skal det ikke gjøres noe
					else if(table.getColumnClass(j).equals(JCheckBox.class))
						if(((JCheckBox)table.getValueAt(i,j)).isSelected())
							p.print("X");
						else
							p.print("\t");
					else if(table.getColumnClass(j).equals(JRadioButton.class))
						if(((JRadioButton)table.getValueAt(i,j)).isSelected())
							p.print("X");
						else
							p.print("\t");
					else if(table.getColumnClass(j).equals(ColorButton.class)){
						Color c = ((ColorButton)table.getValueAt(i,j)).getBackground();
						p.print(c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "\t");
					}
					else
						p.print(table.getValueAt(i,j).toString() + "\t");
					
				}
				p.print("\n");
			}
			p.close();
			b.close();
			f.close();
			exportFile = null;
		}
		catch(IOException ioe){
			log.warn(ioe.getMessage(), ioe);
			Error.getError().addError(Localization.l("common_error"),"IOException in exportToFile",ioe,1);
		}
		catch(Exception e){
			log.warn(e.getMessage(), e);
			Error.getError().addError(Localization.l("common_error"),"Exception in exportToFile",e,1);
		}
	}
	public void exportToCSV() {
		try{
			csvexporter csve = new csvexporter(this,"");
			csve.addExcludeClass(JProgressBar.class);
			
			ArrayList<String> headers = new ArrayList<String>();
			boolean add;
			
			if(m_tbl_list.getRowCount() > 0) {
				// Have to find the position of excluded class
				for(int j=0;j<m_tbl_list.getRowContent(0).length;++j){
					add=true;
					for(int i=0;i<csve.getExcluded().size();++i)
					{
						if(csve.getExcluded().containsKey(m_tbl_list.getRowContent(0)[j].getClass().getName()))
							add=false;
					}
					if(add)
						headers.add(m_tbl_list.columnNames[j]);
				}
				
				csve.addLine(headers.toArray());
				for(int i=0;i<m_tbl_list.getRowCount();++i){
					csve.addLine(m_tbl_list.getRowContent(i));
				}
			}
			csve.Save();
		}
		catch(Exception e){
			return;
		}
	}
	public String exportFileSelect() {
		JFileChooser fchExportLocation = new JFileChooser();
		if(fchExportLocation.showDialog(this, "Save")==JFileChooser.APPROVE_OPTION)
			return fchExportLocation.getSelectedFile().toString();
		return null;
	}
	
	
	public class AdressTblListener implements ListSelectionListener, TableColumnModelListener, MouseListener
	{
		int m_n_selectedindex = -1;
		int m_n_selectedcolumn = -1;
		Component m_current_component = null;
	
		public void valueChanged(ListSelectionEvent e)
		{
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if (lsm.isSelectionEmpty()) {
				m_n_selectedindex = -1;
			}
			else
			{
		        //set_rowbgcolor(java.awt.Color.WHITE);
				m_n_selectedindex = lsm.getMinSelectionIndex();
	        	//ListSelectionModel lsm = jt.getSelectionModel();
	        	//lsm.setSelectionInterval(0, m_n_selectedindex);
				
		        //set_rowbgcolor(java.awt.Color.LIGHT_GRAY);
		        
				//lsm.clearSelection();
				m_tbl.getColumnModel().getSelectionModel().setSelectionInterval(0, m_tbl.getColumnCount()-1);
			}
		}
		public void columnAdded(TableColumnModelEvent e) { }
		public void columnMarginChanged(ChangeEvent e) { }
		public void columnMoved(TableColumnModelEvent e) { }
		public void columnRemoved(TableColumnModelEvent e) { }
		public synchronized void columnSelectionChanged(ListSelectionEvent e) {
			//m_n_selectedcolumn = e.getLastIndex();
			//TableColumnModel tcm = (TableColumnModel)e.getSource();
			//tcm.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			//tcm.getSelectionModel().addSelectionInterval(0, m_tbl.getColumnCount()-1);
			//PAS.get_pas().add_event("columnSelectionChanged " + e.getFirstIndex() + " / " + e.getLastIndex());
		}
		
		public synchronized void mousePressed(java.awt.event.MouseEvent e) { }
		public synchronized void mouseReleased(java.awt.event.MouseEvent e) {
			int idx = m_tbl.rowAtPoint(e.getPoint());
			int idxcol = m_tbl.columnAtPoint(e.getPoint());
			setSelected(idx, idxcol);

			switch(e.getButton())
			{
				case MouseEvent.BUTTON1:
					if(e.getClickCount()==1 || m_n_selectedcolumn == 3 || m_n_selectedcolumn == 4) // Dette er litt jalla, men må nesten ha det for at den skal få med seg despoklikkingen til Randi (altså ikke hoppe over til double click)
					{
						if(m_n_selectedindex>=0) {
							onMouseLClick(m_n_selectedindex, m_n_selectedcolumn, sorter.getRowContent(m_n_selectedindex), new Point(e.getX(), e.getY()));
						}
					}
					else if(e.getClickCount()==2) //double click
					{
						if(m_n_selectedindex>=0)
						{
							select(e);
							onMouseLDblClick(m_n_selectedindex, m_n_selectedcolumn, sorter.getRowContent(m_n_selectedindex), new Point(e.getX(), e.getY()));
						}
					}
					break;
				case MouseEvent.BUTTON3:
					if(e.getClickCount()==1)
					{
						if(m_n_selectedindex>=0) {
							//m_tbl.getComponentAt(e.getX(), e.getY())
							onMouseRClick(m_n_selectedindex, m_n_selectedcolumn, sorter.getRowContent(m_n_selectedindex), new Point(e.getX(), e.getY()));
						}
						
					}
					else if(e.getClickCount()==2)
					{
						if(m_n_selectedindex>=0) {
							onMouseRDblClick(m_n_selectedindex, m_n_selectedcolumn, sorter.getRowContent(m_n_selectedindex), new Point(e.getX(), e.getY()));
						}
					}
					break;
			}
			if(m_n_selectedindex>=0) {
				//m_tbl.setColumnSelectionInterval(0, m_tbl.getColumnCount()-1);
			}
		}
		public synchronized void mouseExited(java.awt.event.MouseEvent e) { }	
		public synchronized void mouseEntered(java.awt.event.MouseEvent e) { 
			//get_pas().add_event("Mouse entered");
		}	
		public void select(MouseEvent e) {
			//JTable theList = (JTable)e.getSource();
			//theList.getro
			/*JList theList = (JList) e.getSource();
			if (theList.getSelectedIndex() == -1) {
				int index = theList.locationToIndex(e.getPoint());
				if (index == -1)
					return;
				else
					theList.setSelectedIndex(index);
			} else {
				//if ( theList.locationToIndex(e.getPoint()) !=  theList.getSelectedIndex())
				//	return;
				m_n_selectedindex = theList.getSelectedIndex();
			}*/
		}
	    public void setSelected(int index, int colidx) {
	    	if(index != m_n_selectedindex) {
		        //m_tbl.changeSelection(index,-1,false,false);
		        m_n_selectedindex = index;
	    	}
	    	if(colidx != m_n_selectedcolumn) {
	    		m_n_selectedcolumn = colidx;
	    	}
	    	//m_tbl.setRowSelectionInterval(0,m_n_selectedindex);
	    	//m_tbl.setColumnSelectionInterval(0, m_tbl.getColumnCount());
	    	//m_tbl.changeSelection(m_n_selectedindex, -1, true, true);
	    }
	    public void set_rowbgcolor(java.awt.Color col) {
	        if(m_n_selectedindex>=0) {
	        	/*for(int i=0; i < m_tbl.getColumnCount(); i++) {
	        		m_tbl.getCellRenderer(m_n_selectedindex, i).getTableCellRendererComponent()
	        	}*/
	        	
	        }	
	    }

		public void mouseClicked(java.awt.event.MouseEvent e) { }	
	}
	
	public class TableList extends DefaultTableModel
	{
		public static final long serialVersionUID = 1;
		int m_n_columns;
		public JTable getJTable() { return m_tbl; }
		
		public TableList(String [] sz_columns, int [] n_width)
		{
            log.info(sz_columns.length);
			m_n_columns = sz_columns.length;
			m_data = new ArrayList[m_n_columns];
			for(int i=0; i < m_n_columns; i++)
			{
				m_data[i] = new ArrayList<Object>();
			}
			columnNames = sz_columns;
			columnWidth = n_width;
		}
        private String[] columnNames;
        private int[] columnWidth;
        private ArrayList<Object>[] m_data;
	
        
		public void initialize()
		{
			TableColumn column = null;
			for (int i = 0; i < m_n_columns; i++) {
			    column = m_tbl.getColumnModel().getColumn(i);
			    column.setPreferredWidth(columnWidth[i]);
		    	set_custom_cellrenderer(column,  i);
			    	//column.setCellRenderer(new TblCellColor());
			    
			}		
			
		}
        public void insert_row(Object[] data, int n_index, boolean bAutoUpdateList)
        {
        	int idx;
        	if(n_index>=0)
        		idx = n_index;
        	else
        		idx = m_data[0].size();
        	for(int i=0; i < data.length; i++)
        	{
        		m_data[i].add(idx, data[i]);
        	}
        	if(bAutoUpdateList)
        	{
        		fireTableDataChanged();
        	}
        }
        public void insert_component_row(Object[] data, int n_index) {
        	int idx;
        	if(n_index>=0)
        		idx = n_index;
        	else
        		idx = m_data[0].size();
        	for(int i=0; i < data.length; i++)
        	{
        		m_data[i].add(idx, new javax.swing.JLabel(data[i].toString()));
        	}
        	fireTableDataChanged();
        }
        
        public void remove_row(int n)
        {
			for(int j=0;j<m_data.length;++j) {
				m_data[j].remove(n);
			}
        }
        
        public void remove_row(Object data) {
        	for(int i=0;i<m_data[0].size();++i) {
        		if(m_data[0].get(i).toString().equals(data.toString())){
        			for(int j=0;j<m_data.length;++j) {
        				m_data[j].remove(i);
        			}
        		}
        	}
        	fireTableDataChanged();
        }
        public void clear()
        {
        	int n_rows = this.getRowCount();
/*        	for(int i=0; i < m_n_columns; i++)
        	{
        		m_data[i].clear();
        	}	*/
        	
        	/*for(int i=0; i < m_data.length; i++)
        	{
        		m_data[i].clear();
        	}*/
        	int i;
        	for(i=0; i < this.getRowCount(); i++) {
        		m_data[i].clear();
        	}
        	//this.
        	//if(i > 0)
        	//	this.fireTableRowsDeleted(0, n_rows-1);
        	//fireTableCellUpdated(0, 0);
        	//fireTableDataChanged();
        	fireTableDataChanged();
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
        	return (m_data!=null ? m_data[0].size() : 0);
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	try {
        		//return sorter.getValueAt(row, col);
        		return m_data[col].get(row);
        	} catch(Exception e) { 
        		//Error.getError().addError("SearchPanelResults","Exception in getValueAt",e,1);
        		return null;
        	}
        }
        public Object [] getRowContent(int row) {
        	Object [] ret = new Object[m_n_columns];//new String[m_n_columns];
        	for(int i=0; i < m_n_columns; i++)
        	{
        		try {
        			ret[i] = getValueAt(row, i);//.toString();
        		}
        		catch(Exception e) {
        			Error.getError().addError(Localization.l("common_error"),"Exception in getRowContent",e,1);
        			ret[i] = null;
        		}
        	}
        	return ret;
        }
        public Class getColumnClass(int c) {
        	try {
        		return getValueAt(0, c).getClass();
        	} catch(Exception e) {
        		return null;
        	}
        }
        public boolean isCellEditable(int row, int col) {
        	return is_cell_editable(row, col);
        }
        public void setValueAt(Object value, int row, int col) {
            //data[row][col] = value;
            m_data[col].set(row, value);
            fireTableCellUpdated(row, col);

        }
        public void fireTableDataChanged() {
        	valuesChanged();
        	super.fireTableDataChanged();
        	//sorter.fireTableDataChanged();
        }
	
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		if(w<=1 || h<=1)
		{
			return;
		}
		//log.debug(layer.getWidth() + " " + layer.getHeight());
		scrollPane.setSize(layer.getWidth(), layer.getHeight());
		int x = ico.getIconWidth(); int y = ico.getIconHeight();
		lbl_loading.setBounds(layer.getWidth()/2-x/2, layer.getHeight()/2-y/2, x, y);
		m_lbl_no_results.setSize(new Dimension(layer.getWidth(), layer.getHeight()));
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}

	/*@Override
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);
	}*/


}

