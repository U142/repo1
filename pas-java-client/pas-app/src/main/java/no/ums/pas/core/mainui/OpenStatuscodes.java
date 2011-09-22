package no.ums.pas.core.mainui;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.defines.TblCellColor;
import no.ums.pas.core.popupmenus.PUStatusList;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.send.SendProperties;
import no.ums.pas.status.StatusCode;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.status.StatusSending;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.colorpicker.ColorPickerTable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.undo.UndoManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.SortedSet;


public class OpenStatuscodes extends SearchPanelResults {

    private static final Log log = UmsLog.getLogger(OpenStatuscodes.class);

	public static final long serialVersionUID = 1;
	private PAS m_pas;
	private StatuscodeFrame m_statusframe;
	public StatuscodeFrame get_statusframe() { return m_statusframe; }
	//private int n_refno_column = 1;
	private int n_col_show = 3;
	private int n_col_anim = 4;
	private int n_col_color = 5;
	private int n_col_code = 0;
	private int n_col_text = 1;
	private int n_col_hits = 2;
	public PAS get_pas() { return m_pas; }
	private PUStatusList m_popup_statuslist = null;
	public PUStatusList get_popup_statuslist() { return m_popup_statuslist; }
	protected StatusSending m_filter = null;
	public StatusSending getFilter() { return m_filter; }
	
	public OpenStatuscodes(PAS pas, StatuscodeFrame statusframe, String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim)
	{
		super(sz_columns, n_width, b_editable, dim/*new Dimension(800, 200)*/, ListSelectionModel.SINGLE_SELECTION);
		m_pas = pas;
		m_statusframe = statusframe;
		
		m_popup_statuslist = new PUStatusList(get_pas(), "Status");
		setRenderers();
		super.sort(0);
		//super.sorter.setColumnComparator(StatusCode.class, StatusCode);
	}
	
	
	protected void valuesChanged() { }
	
	public void setRenderers()
	{
		super.SetRowClass(0, new StatusCodeRenderer());
		super.SetRowClass(1, new StatusCodeRenderer());
		super.SetRowClass(2, new StatusCodeRenderer());
		super.SetRowClass(3, new StatusCodeRenderer());
		super.SetRowClass(4, new StatusCodeRenderer());
		//super.SetRowClass(3, new StatusCodeRenderer());
		//super.SetRowClass(4, new StatusCodeRenderer());
		super.SetRowClass(5, new TblCellColor());
		//super.SetRowClass(2, BoldRenderer.class);
		//super.SetRowClass(1, BoldRenderer.class);
		
	}
	public class StatusCodeRenderer extends JLabel implements TableCellRenderer
	{
		public static final long serialVersionUID = 1;
		public JCheckBox chk = new JCheckBox();
		//DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
		public StatusCodeRenderer()
		{
			super();
		}
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			StatusCode code = (StatusCode)sorter.getRowContent(row)[0];
			this.setOpaque(true);

			if(isSelected)
			{
				this.setBackground(DEFAULT_SELECTION_COLOR);
				this.setForeground(FOREGROUND_SELECTION_COLOR);
				//this.setForeground(new java.awt.Color(255,0,0));
			}
			else
			{
				//this.setBackground(m_tbl.getBackground());
				//this.setForeground(m_tbl.getForeground());
			}
			if(value instanceof Boolean) //checkbox
			{
				if(code.get_code()>=-1002 && code.get_code()<=-1000)
				{
					this.setText("");
				}
				else
				{
					chk.setSelected((Boolean)value);
					chk.setAlignmentX(Component.CENTER_ALIGNMENT);
					chk.setHorizontalAlignment(SwingConstants.CENTER);
					return chk;
				}
			}
			else
			{			
				this.setText(value.toString());
			}
			return this;
		}
	}


	public boolean is_cell_editable(int row, int col) {
		return is_cell_editable(col);
	}	
	protected synchronized void start_search(StatusSending filter) {
		m_filter = filter;
		start_search();
	}
	
	Hashtable<StatusCode, Boolean> hashStatusCodesShow = new Hashtable<StatusCode, Boolean>();
	
	
    protected synchronized void start_search()
	{
		pushSelection();
		StatusCode current;
		StatuscodeFrame frame = get_statusframe();
		StatusController cont = frame.get_controller();
		for(int i=0; i < get_statusframe().get_controller().get_statuscodes().size(); i++)
		{
			current = get_statusframe().get_controller().get_statuscodes()._get(i);
			boolean visible = true;
			boolean visibleInList = false;
			boolean animated = true;
			for(int j=0;j<this.get_table().getRowCount();j++) {
				if(((StatusCode)this.get_table().getValueAt(j, 0)).get_code() == current.get_code()) {
					visible = (Boolean)this.get_table().getValueAt(j, 3);
					if(current.get_code() <= -1000 && current.get_code() >= -1002) {
						visible = true;
						setValueAt(false, j, 3);
						//set_cell_editable(3, false);
					}
					visibleInList = true;
				}
			}
			visible = isStatusVisibleChecked(current);
			current.set_visible(visible);
			
			try {
				int hits = 0;
				if(!current.get_reserved()) {
					hits = count_statusitems(current.get_code());
					current.set_current_count(hits);
				}
				else
					hits = current.get_current_count();
				Object colType = null;
				if(current.get_code()<=-1000 && current.get_code()>=-1002)
				{ 
					colType = "";
				}

				if(!current.get_addedtolist()) {
					Object visibleType = new Boolean(visible);
					Object animType = new Boolean(false);
					insert_row(new Object[] { current, current.get_status(), hits, visibleType, animType, (colType!=null ? "" : current.get_color())}, -1);
					current.set_addedtolist();
				}
				else { //update text and count
					int n_row = find(n_col_code, current.get_code());
					if(n_row>=0) {
						setValueAt(current.get_status(), n_row, n_col_text);
						setValueAt(hits, n_row, n_col_hits);
						setValueAt((colType!=null ? "" : current.get_color()), n_row, n_col_color);
					}
				}									  
				if(current.get_current_count() < 1
						|| (m_filter != null && (m_filter.hasLBA() || m_filter.get_sendingtype() == 5) && current.get_code() < -1000) // Dette fjerner queue og sending dersom LBA er valgt
						|| (m_filter != null && (m_filter.get_percentage() == (float)100 && current.get_code() < -1000))) { // Dette fjerner queue og sending dersom filteret er på en sending som er 100 prosent
					current.set_visible(false);
					remove_row(current);
					current.set_removedfromlist();
				}
				get_pas().get_statuscontroller().show_statuscode(current.get_code(), current.get_visible());
			} catch(Exception e) {
				log.debug(e.getMessage());
				log.warn(e.getMessage(), e);
				Error.getError().addError("OpenStatuscodes","Exception in start_search",e,1);
			}
		}
		sorter.fireTableDataChanged();
		get_pas().get_statuscontroller().refresh_search_houses();
		popSelection();
	}
    
    private boolean isStatusVisibleChecked(StatusCode code)
    {
    	if(!hashStatusCodesShow.containsKey(code))
    		hashStatusCodesShow.put(code, true); //a new statuscode should always be true
		return hashStatusCodesShow.get(code);
    }
    
    public int find(int n_col, int code) {
    	for(int i=0; i < m_tbl_list.getRowCount(); i++) {
    		int n_temp = -1;
    		try {
    			n_temp = ((StatusCode) m_tbl_list.getValueAt(i, n_col)).get_code();
    		} catch(Exception e) {
    			Error.getError().addError("SearchPanelResults","Exception in find",e,1);
    		}
    		if(n_temp == code) {
    			return i;
    		}
    	}
    	return -1;
    }
	int count_statusitems(int n_code)
	{
		int n_ret = 0;
		SortedSet<Inhabitant> objects = get_statusframe().get_controller().get_items();
		if(objects==null)
			return n_ret;
		for(Inhabitant current : objects)
		{
			StatusItemObject obj = (StatusItemObject)current;
			if(m_filter!=null)
			{
				//filter is activated. if refno's are noe equal, continue without counting
				if(m_filter.get_refno()!=obj.get_refno())
					continue;
			}
			if(obj.get_status() == n_code)
				n_ret++;
		}
		return n_ret;
	}
	protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		if(this.get_table().getValueAt(n_row, 3) instanceof Boolean)
		{
			Boolean b_show = (Boolean)this.get_table().getValueAt(n_row, 3);
			StatusCode code = (StatusCode)rowcontent[n_col_code];
			int n_code = code.get_code();
			hashStatusCodesShow.put(code, b_show);
			
			try
			{
				Boolean b_alert = (Boolean)get_table().getValueAt(n_row, 4);
				get_pas().get_statuscontroller().activate_alertborder(n_code, b_alert.booleanValue());
				get_pas().get_statuscontroller().show_statuscode(n_code, b_show.booleanValue());
			}
			catch(Exception e)
			{
				
			}
			PAS.get_pas().kickRepaint();
		}
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		if(n_col == n_col_color && get_table().getValueAt(n_row, 5) instanceof Color) {
			//int n_code = new Integer((String)rowcontent[n_col_code]).intValue();
			//ColorPickerTable pick = new ColorPickerTable(get_pas(), "Select Color", p, (Color)get_tablelist().getValueAt(n_row, 5), this, get_tablelist(), new Integer(n_code), n_row);
			ColorPickerTable pick = new ColorPickerTable(get_pas(), "Select Color", p, (Color)get_table().getValueAt(n_row, 5), this, get_table(), (StatusCode)get_table().getValueAt(n_row, 0), n_row);
			pick.show();
		}
		//get_pas().add_event("ColorPicker: " + pick.get_selected_color().toString());
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		try {
			m_tbl.getSelectionModel().setSelectionInterval(n_row, n_row);
			get_popup_statuslist().set_item(n_row);
			get_popup_statuslist().set_id(new Integer(rowcontent[n_col_code].toString()));
			get_popup_statuslist().pop(this.m_tbl, p);
			//get_pas().add_event("Rightclick: open popup menu");
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("OpenStatuscodes","Exception in onMouseRClick",e,1);
		}

	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	void onDownloadFinished()
	{
		
	}
}
