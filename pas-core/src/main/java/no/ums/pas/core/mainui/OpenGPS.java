package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.popupmenus.PUGPSList;
import no.ums.pas.maps.defines.MapObject;
import no.ums.pas.maps.defines.MapObjectList;
import no.ums.pas.maps.defines.MapObjectVars;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.TextFormat;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class OpenGPS extends SearchPanelResults implements ComponentListener {
	public static final long serialVersionUID = 1;
	private PAS m_pas;
	private GPSFrame m_frame;
	public GPSFrame get_frame() { return m_frame; }
	public PAS get_pas() { return m_pas; }
	private PUGPSList m_popup_gpslist;
	public PUGPSList get_popup_gpslist() { return m_popup_gpslist; }
	
/*	private int m_col_objectpk = 0;
	private int m_col_name = 2;
	private int m_col_type = 3;
	private int m_col_date = 4;
	private int m_col_time = 5;
	private int m_col_trail = 6;
	private int m_col_show = 7;
	private int m_col_alert = 8;
	private int m_col_follow = 9;
	private int m_col_street = 10;
	private int m_col_region = 11;*/
	private final int m_col_dynamic = 0;
	private final int m_col_object = 1;
	private final int m_col_type = 2;
	private final int m_col_date = 3;
	private final int m_col_time = 4;
	private final int m_col_trail = 5;
	private final int m_col_show = 6;
	private final int m_col_alert = 7;
	private final int m_col_follow = 8;
	private final int m_col_street = 9;
	private final int m_col_region = 10;	
	
	private int m_n_filter_dynamic = 1 | 2; //1 = all, 0 = static, 1 = dynamic
	private int m_n_filter_active = 1 | 2; //1 = all, 0 = offline, 1 = online
	private int m_n_filter_status = 1 | 2 | 4; //1 = all, 0 = off duty, 1 = available, 2 = occupied
	private int m_n_filter_carrier = 1 | 2 | 4 | 8; //1 = all, 0 = universal, 1 = person, 2 = dog, 3 = car
	private String m_sz_filter_name = "";
	
	
	
	public OpenGPS(PAS pas, GPSFrame frame, String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim)
	{
		super(sz_columns, n_width, b_editable, dim/*new Dimension(800, 200)*/, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		m_pas = pas;
		m_frame = frame;
		setPreferredSize(dim);
		setMinimumSize(new Dimension(10, 10));
		setMaximumSize(new Dimension(500, 1200));
		m_popup_gpslist = new PUGPSList(m_pas, "Map Objects");
		//m_tbl.pre
		//m_tbl.setDefaultRenderer()
		//addComponentListener(this);
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		//System.out.println("OpenGPS resized");
		//setPreferredSize(new Dimension(getWidth(), getHeight()));
		revalidate();
	}
	public void componentShown(ComponentEvent e) { }
	
	public synchronized void add_textfilter(int n_filtertype, String sz_filter) {
		switch(n_filtertype) {
			case MapObjectVars.FILTER_TYPE_NAME_:
				m_sz_filter_name = sz_filter;
				break;
		}
		clear();
		start_search(true);
	}
	public boolean is_cell_editable(int row, int col) {
		return is_cell_editable(col);
	}	
	public synchronized void flip_filter(int n_filtertype, int n_filter, boolean b_on) {
		try {
			switch(n_filtertype) {
				case MapObjectVars.FILTER_TYPE_DYNAMIC_:
					if(!b_on)
						m_n_filter_dynamic &= ~n_filter;
					else 
						m_n_filter_dynamic |= n_filter;
					break;
				case MapObjectVars.FILTER_TYPE_ONLINE_STATUS_:
					if(!b_on)
						m_n_filter_active &= ~n_filter;
					else
						m_n_filter_active |= n_filter;
					break;
				case MapObjectVars.FILTER_TYPE_STATUS_:
					if(!b_on)
						m_n_filter_status &= ~n_filter;
					else
						m_n_filter_status |= n_filter;
					break;
				case MapObjectVars.FILTER_TYPE_USERTYPE_:
					if(!b_on)
						m_n_filter_carrier &= ~n_filter;
					else
						m_n_filter_carrier |= n_filter;
					break;
			}
		} catch(Exception e) {
			Error.getError().addError("OpenGPS","SecurityException in flip_filter",e,1);
		}
		try {
			clear();
		} catch(Exception e) {
			Error.getError().addError("OpenGPS","SecurityException in flip_filter",e,1);
		}
		try {
			start_search(true);
		} catch(Exception e) {
			Error.getError().addError("OpenGPS","SecurityException in flip_filter",e,1);
		}
	}
	
	
    public void set_custom_cellrenderer(TableColumn column, int n_col)
    {
    	if(n_col==this.m_col_object)
    		column.setCellRenderer(new ColouredCellRenderer());
    	/*if(n_col==5)
    	{
    		column.setCellRenderer(new TblCellColor());
    	}*/
    }
    public class ColouredCellRenderer extends javax.swing.table.DefaultTableCellRenderer{
    	public static final long serialVersionUID = 1;
       private Color col_static;
        private Color col_offline;
        private Color col_online;
        private Color col_satfix;
        public ColouredCellRenderer() {
            col_static = new Color(222, 236, 246);
            col_offline = new Color(244, 193, 193);
            col_online = new Color(244, 244, 193);
            col_satfix = new Color(193, 244, 193);
        }

        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component renderer =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);    //---
            MapObject obj = (MapObject)get_tablelist().getValueAt(row, m_col_object);
            Color col;
            if(!obj.get_dynamic()) {
            	col = col_static;
            } else if(obj.get_dynamic() && obj.get_onlinestatus()==0) {
            	col = col_offline;
            } else if(obj.get_dynamic() && obj.get_onlinestatus()==1 && !obj.get_satfix()) {
            	col = col_online;
            } else if(obj.get_dynamic() && obj.get_onlinestatus()==1 && obj.get_satfix()) {
            	col = col_satfix;
            } else
            	col = Color.BLACK;
            renderer.setBackground(col);
            return renderer;
        }
    }
    public void valuesChanged() {
    	/*for(int i=0; i < m_tbl.getColumnCount(); i++) {
    		//m_tbl.getDefaultRenderer(ColouredCellRenderer.class).notifyAll();
    	}*/
    }
    
	public synchronized void start_search(boolean b_refresh) {
		MapObjectList list = get_pas().get_gpscontroller().get_mapobjects();
		MapObject current;
		int n_success = 0;
		for(int i=0; i < list.size(); i++)
		{
			try {
				current = (MapObject)list.get(i);
				if((b_refresh || (!b_refresh && !current.get_added_to_list())))
				{
					if(check_filter(current)) {
						//"l_objectpk", "Dynamic", "Name", "Date", "Time", "Trail", "Show", "Alert", "Follow"
						try {
							insert_row(new Object[] { new Boolean(current.get_dynamic()), current, MapObjectVars.GPS_UNIT_USERTYPE_[1][current.get_usertype()], TextFormat.format_date(current.get_lastdate()), TextFormat.format_time(current.get_lasttime(),6), (current.get_dynamic() ? new Boolean(false) : new Boolean(false)), new Boolean(current.get_visible()), new Boolean(current.get_alert()), new Boolean(current.get_follow()), current.get_street(), current.get_region() }, -1 );
							n_success ++;
							current.set_added_to_list();
						} catch(Exception e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
							Error.getError().addError("OpenGPS","Exception in start_search",e,1);
						}
					}
				}
				else
					set_rowvalues(current);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("OpenGPS","Exception in start_search",e,1);
			}
		}		
	}
	public boolean check_filter(MapObject obj) {
		if(obj.get_dynamic() && (m_n_filter_dynamic & 2)!=2)
			return false;
		else if(!obj.get_dynamic() && (m_n_filter_dynamic & 1)!=1)
			return false;
		int n_usertype = new Integer(MapObjectVars.GPS_UNIT_USERTYPE_[2][obj.get_usertype()]).intValue();
		if((m_n_filter_carrier & n_usertype) != n_usertype)
			return false;
		int n_carrierstatus = new Integer(obj.get_carrierstatus()).intValue();
		if((m_n_filter_carrier & n_carrierstatus) != n_carrierstatus)
			return false;
		int n_onlinestatus = new Integer(obj.get_onlinestatus()).intValue();
		if((m_n_filter_active & n_onlinestatus) != n_onlinestatus)
			return false;
		if(m_sz_filter_name.length()>0) {
			if(obj.get_name().toUpperCase().lastIndexOf(m_sz_filter_name.toUpperCase())<0)
				return false;
		}
		return true;
	}
    
	public synchronized void start_search()
	{
		start_search(false);
	}
	private synchronized int find_row(int n_search_col, Object obj_search_for)
	{
		for(int i=0; i < get_tablelist().getRowCount(); i++)
		{
			if(obj_search_for.equals(((MapObject)get_tablelist().getValueAt(i, n_search_col)).get_objectpk()))
				return i;
		}
		return -1;
	}
	public synchronized boolean set_rowvalues(MapObject obj)
	{
		int n_row = find_row(m_col_object, obj.get_objectpk());
		if(n_row < 0)
			return false;
		//current.get_name(), TextFormat.format_date(current.get_lastdate()), TextFormat.format_time(current.get_lasttime(),6)
		get_tablelist().setValueAt(obj, n_row, m_col_object);
		get_tablelist().setValueAt(MapObjectVars.GPS_UNIT_USERTYPE_[1][obj.get_usertype()], n_row, m_col_type);
		get_tablelist().setValueAt(TextFormat.format_date(obj.get_lastdate()), n_row, m_col_date);
		get_tablelist().setValueAt(TextFormat.format_time(obj.get_lasttime(),6), n_row, m_col_time);
		get_tablelist().setValueAt(obj.get_street(), n_row, m_col_street);
		get_tablelist().setValueAt(obj.get_region(), n_row, m_col_region);
		
		return true;
	}
	public void fireTableDataChanged() {
		repaint();
	}
	protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		//String l_objectpk = new String();
		MapObject obj = null;
		boolean b_needrepaint = false;
		try {
			//l_objectpk = ((MapObject)get_tablelist().getValueAt(n_row, m_col_object)).get_objectpk();
			obj = (MapObject)get_tablelist().getValueAt(n_row, m_col_object);
			//PAS.get_pas().add_event("object clicked " + obj.get_name());
			set_iteminfo(n_row, rowcontent[m_col_object]);
		} catch(Exception e) { Error.getError().addError("OpenGPS","Exception in onMouseLClick",e,1); }
		try {
			boolean b = false;
			try {
				//b = ((Boolean)get_tablelist().getValueAt(n_row, n_col)).booleanValue();
			} catch(Exception e) { 
				//Error.getError().addError("OpenGPS","Exception in onMouseLClick",e,1);
			}
			switch(n_col) {
				case m_col_trail:
					b = ((Boolean)get_tablelist().getValueAt(n_row, n_col)).booleanValue();					
					obj.set_trail(b);
					b_needrepaint = true;
					break;
				case m_col_show:
					b = ((Boolean)get_tablelist().getValueAt(n_row, n_col)).booleanValue();
					obj.set_visible(b);
					b_needrepaint = true;
					break;
				case m_col_alert:
					b = ((Boolean)get_tablelist().getValueAt(n_row, n_col)).booleanValue();
					obj.set_alert(b);
					b_needrepaint = true;
					break;
				case m_col_follow:
					b = ((Boolean)get_tablelist().getValueAt(n_row, n_col)).booleanValue();
					obj.set_follow(b);
					break;
			}
		} catch(Exception e) { 
			Error.getError().addError("OpenGPS","Exception in onMouseLClick",e,1);
			return;
		}
		if(b_needrepaint) {
			PAS.get_pas().kickRepaint();
		}
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		String l_objectpk = ((MapObject)get_tablelist().getValueAt(n_row, m_col_object)).get_objectpk();
		set_iteminfo(n_row, ((MapObject)rowcontent[m_col_object]).get_objectpk());
		get_popup_gpslist().get_actionlistener().actionPerformed(new ActionEvent(get_popup_gpslist().get_actionlistener(), ActionEvent.ACTION_PERFORMED, "act_find"));
		
	}
	void set_iteminfo(int n_row, Object id) {
		get_popup_gpslist().set_item(n_row);
		get_popup_gpslist().set_id(id);		
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		//onMouseLClick(n_row, n_col, rowcontent, p);
		//super.findComponentAt(p.x, p.y).se
		try {
			set_iteminfo(n_row, ((MapObject)rowcontent[m_col_object]).get_objectpk());
			get_popup_gpslist().pop(this.m_tbl, p);
		} catch(Exception e) { 
			get_pas().add_event(e.getMessage(), e);
			Error.getError().addError("OpenGPS","Exception in onMouseRClick",e,1);
		}
	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	void onDownloadFinished()
	{
		
	}	
}
