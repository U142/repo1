package no.ums.pas.gps;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.maps.defines.MapObject;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;


public class MapObjectReg extends DefaultPanel {
	public static final long serialVersionUID = 1;
	JTabbedPane m_tabbedpane;
	MapObjectInfopane m_infopanel;
	MapObjectPicturepane m_picturepanel;
	MapObjectGpsSetup m_gpssetup;
	MapObjectGpsHistory m_gpshistory;
	MapObjectEventHistory m_eventhistory;
	MapObjectButtonpane m_buttonpane;
	MapObject m_obj = null;
	MapObject m_undo = null;
	public MapObject get_mapobject() { return m_obj; }
	private JDialog m_dlg;
	public JDialog get_dlg() { return m_dlg; }
	
	public MapObjectReg(JDialog dlg, PAS pas, MapObject obj) {
		super();
		m_dlg = dlg;
		m_obj = obj;
		if(m_obj==null) { 
			m_obj = new MapObject(PAS.get_pas().get_gpscontroller().get_mapobjects()); //parent objectlist
			m_obj.set_lon(Variables.getNavigation().calc_centerpoint_x(PAS.get_pas().get_mappane().get_dimension().width / 2));
			m_obj.set_lat(Variables.getNavigation().calc_centerpoint_y(PAS.get_pas().get_mappane().get_dimension().height / 2));
		}
		else
		{
			m_undo = m_obj.Clone();
			if(m_undo==null)
				PAS.get_pas().add_event("m_undo failed", null);
		}
		setLayout(new BorderLayout());
		m_tabbedpane = new JTabbedPane();
		m_infopanel = new MapObjectInfopane(this);
		m_picturepanel = new MapObjectPicturepane(this);
		m_gpshistory = new MapObjectGpsHistory(this);
		m_eventhistory = new MapObjectEventHistory(this);
		m_gpssetup = new MapObjectGpsSetup(this);
		m_buttonpane = new MapObjectButtonpane(this);
		
		m_tabbedpane.setPreferredSize(new Dimension(490, 350));
		m_tabbedpane.addTab("Information", null,
                			m_infopanel,
                			"Map Object information");
		m_tabbedpane.addTab("Icon manager", null,
							m_picturepanel,
			    			"Icon");

		add_controls();
		int n_mode = MapObjectInfopane.MODE_DYNAMIC_;
		if(!m_obj.get_dynamic())
			n_mode = MapObjectInfopane.MODE_STATIC_;
		set_mode(n_mode);
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}
	public void add_controls() {
        add(m_tabbedpane, BorderLayout.NORTH);	
        add(m_buttonpane, BorderLayout.SOUTH);
        init();
	}
	public void init() {
		setVisible(true);
	}
	public void undo() {
		//m_obj = m_undo;
		//m_obj.set_icon(m_undo.get_icon());
	}
	public void set_mode(int n_mode) {
		boolean b_show_gps = true;
		if(n_mode==MapObjectInfopane.MODE_DYNAMIC_) {
			//show gps stuff
			
		} else if(n_mode==MapObjectInfopane.MODE_STATIC_) {
			//hide gps stuff
			b_show_gps = false;
		}
		if(b_show_gps)
			m_tabbedpane.addTab("GPS Setup", null, m_gpssetup, "GPS Device Setup");
		else {
			if(m_tabbedpane.indexOfComponent(m_gpssetup)>=0)
				m_tabbedpane.removeTabAt(m_tabbedpane.indexOfComponent(m_gpssetup));
		}
		
		//hide gps stuff unconditionally if the object is new
		if(get_mapobject().isNew()) {
			b_show_gps = false;
		}
		if(b_show_gps) {
			m_tabbedpane.addTab("GPS History", null, m_gpshistory, "GPS History");
			m_tabbedpane.addTab("Events", null, m_eventhistory, "Event History");
		} else {
			if(m_tabbedpane.indexOfComponent(m_gpshistory)>=0)
				m_tabbedpane.removeTabAt(m_tabbedpane.indexOfComponent(m_gpshistory));
			if(m_tabbedpane.indexOfComponent(m_eventhistory)>=0)
				m_tabbedpane.removeTabAt(m_tabbedpane.indexOfComponent(m_eventhistory));			
		}
	}
	
	public boolean update_data(boolean b_to_vars) {
		if(b_to_vars) { //data from controls to vars
			m_infopanel.update_data(true);
			return verify_data();
		} else { //data from vars to controls
			return true;
		}
	}
	public boolean verify_data() {
		boolean b_ret = true;
		try {
			if(get_mapobject().get_name().length()<=0) {
				PAS.get_pas().add_event("get_name length 0", null);
				b_ret = false;
			}
			if(get_mapobject().get_dynamic() && get_mapobject().get_gsmno().length() < 8) {
				PAS.get_pas().add_event("get_gsmno length < 8", null);
				b_ret = false;
			}
			if(get_mapobject().get_picturepk().length()<=0) {
				PAS.get_pas().add_event("get_picturepk length 0", null);
				b_ret = false;
			}
		} catch(Exception e) {
			PAS.get_pas().add_event("Error verify_data() " + e.getMessage(), e);
		}
		return b_ret;
	}
}