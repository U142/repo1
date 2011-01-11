package no.ums.pas.core.mainui;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.gps.*;


public class GPSEventPanel extends DefaultPanel implements ComponentListener {
	public static final long serialVersionUID = 1;
	private GPSEvents m_events = null;
	public GPSEventPanel(PAS pas, Dimension size) {
		super();
		m_events = new GPSEvents(new String [] { "Time", "Object", "Status", "Event" }, new int [] { 60, 30, 40, 200 }, new boolean [] { false, false, false, false }, new Dimension(300, 300)); 
		setPreferredSize(size);
		add_controls();
		addComponentListener(this);
	}
	public void add_controls() {
		//m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, 0, 1, 1);
		add(m_events, get_gridconst());
		init();
	}
	public void init() {
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		//System.out.println(getWidth() + ", " + getHeight());
		m_events.setPreferredSize(new Dimension(getWidth(), getHeight()));
		revalidate();
	}
	public void fill(GPSEventList list) {
		//System.out.println("Fill GPSEvent");
		m_events.start_search();
	}
	public void componentShown(ComponentEvent e) { }

}

class GPSEvents extends SearchPanelResults implements ComponentListener {
	public static final long serialVersionUID = 1;
	private int _n_last_index = 0;
	private void inc_last_index() { _n_last_index ++; }
	protected int get_last_index() { return _n_last_index; }
	
	GPSEvents(String [] _cols, int [] _width, boolean [] _editable, Dimension dim) {
		super(_cols, _width, _editable, dim, ListSelectionModel.SINGLE_SELECTION);
		addComponentListener(this);
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		revalidate();
	}
	public void componentShown(ComponentEvent e) { }
	public boolean is_cell_editable(int row, int col) {
		return false;
	}
	public void onMouseLClick(int row, int col, Object [] obj, Point p) {
		
	}
	public void onMouseRClick(int row, int col, Object [] obj, Point p) {
		
	}
	public void onMouseLDblClick(int row, int col, Object [] obj, Point p) {
		
	}
	public void onMouseRDblClick(int row, int col, Object [] obj, Point p) {
		
	}
	protected void valuesChanged() { }

	public void start_search() {
		//m_tbl_list.clear();
		GPSEventList list = PAS.get_pas().get_gpscontroller().get_gpsevents();
		GPSEvent event;
		Object [] data;
		//System.out.println("listsize="+list.size());
		String sz_obj;
		for(int i=get_last_index(); i < list.size(); i++) {
			event = (GPSEvent)list.get(i);
			sz_obj = PAS.get_pas().get_gpscontroller().get_mapobjects().find(event.get_objectpk()).get_name();
			data = new Object [] { event, sz_obj, GpsSetupReturnCode.create_returntext(event.get_answered()), event.get_statement() }; 
			insert_row(data, 0);
			inc_last_index();
		}
	}

	
}