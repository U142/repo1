package no.ums.pas.send;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.MunicipalStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.send.sendpanels.SendWindow;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SendObject extends Object {

    private static final Log log = UmsLog.getLogger(SendObject.class);

	//PAS m_pas = null;
	//private PAS get_pas() { return m_pas; }
	String m_sz_name;
	String m_sz_projectpk;
	public String get_name() { return m_sz_name; }
	public String toString() { 
		if(get_toolbar()!=null)
			return Integer.toString(this.get_toolbar().get_sendingid());
		else
			return "-1";
	}
/*	private Point m_current_mousepos = null;
	public Point get_current_mousepos() { return m_current_mousepos; }
	public void set_current_mousepoint(Point p) {
		m_current_mousepos = p;
	}*/
	
	private SendOptionToolbar m_toolbar = null;
	public SendOptionToolbar get_toolbar() { return m_toolbar; }
	//private SendController m_sendcontroller;
	//public SendController get_sendcontroller() { return m_sendcontroller; }
	private boolean m_b_active = true;
	public boolean isActive() { return m_b_active; }
	public void setActive(boolean b) { m_b_active = b; }
	private SendProperties m_sendproperties = null;
	public SendProperties get_sendproperties() { return m_sendproperties; }
	private ActionListener m_callback = null;
	protected ActionListener get_callback() { return m_callback; }
	public void set_sendproperties(SendProperties sp) { m_sendproperties = sp; } 
	private boolean m_b_locked = false;
	public boolean isLocked() { return m_b_locked; }
	private SendWindow m_sendwindow = null;
	public void set_sendwindow(SendWindow win) { m_sendwindow = win; }
	public SendWindow get_sendwindow() { return m_sendwindow; }
	private Navigation m_navigation = null;
	protected Navigation get_navigation() { return m_navigation; }
	private Col m_default_color = new Col(new Color(1.0f, 0.0f, 0.0f, 0.2f), new Color(1.0f, 0.0f, 0.0f, 0.9f));
	public void activate_sendwindow() {
		if(m_sendwindow!=null) {
			if(m_sendwindow.isVisible())
				m_sendwindow.setVisible(true);
		}
	}
	
	private boolean m_import_more_flag;
	public boolean get_import_more_flag() { return m_import_more_flag; }
	public void set_import_more_flag(boolean m_import_more_flag) { this.m_import_more_flag = m_import_more_flag; }

	public SendObject(String sz_name, int n_type, int n_send_id, ActionListener callback, Navigation nav) { //SendController controller) {
		super();
		
		//m_pas = pas;
		//m_sendcontroller = controller;
		m_callback = callback;
		m_navigation = nav;
		/*try {
			m_sendcontroller = PAS.get_pas().get_sendcontroller();
		} catch(Exception e) {
			
		}*/
		//init(sz_name, sz_projectpk);
		try {
			if(PAS.get_pas() != null && PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 5)
				m_toolbar = new SendOptionToolbar(this, callback, n_send_id);
			else
				m_toolbar = new SendOptionToolbar(this, callback, n_send_id);
			if(PAS.pasplugin != null) // admin map
				PAS.pasplugin.onAddSendOptionToolbar(m_toolbar);
		} catch(Exception e) {
			//PAS.get_pas().add_event("Error SendObject() : " + e.getMessage(), e);
			//PAS.get_pas().printStackTrace(e.getStackTrace());
			Error.getError().addError("SendObject","Exception in SendObject",e,1);
			log.warn(e.getMessage(), e);
		}
		//set_type(n_type);
		switch(n_type) {
		case SendProperties.SENDING_TYPE_POLYGON_:
			//get_toolbar().m_radio_sendingtype_polygon.doClick();
			get_toolbar().actionPerformed(new ActionEvent(this,Event.ACTION_EVENT,"act_sendingtype_polygon"));
			break;
		case SendProperties.SENDING_TYPE_CIRCLE_:
			get_toolbar().m_radio_sendingtype_ellipse.doClick();
			break;
		case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
			m_sendproperties = new SendPropertiesGIS(m_toolbar);
			break;
		case SendProperties.SENDING_TYPE_MUNICIPAL_:
			get_toolbar().m_radio_sendingtype_municipal.doClick();
			break;
		}
		try {
			//PAS.get_pas().get_eastcontent().get_generalpanel().add(m_toolbar);
		} catch(Exception e) {
			Error.getError().addError("SendObject","Exception in SendObject",e,1);
		}
		//((BasicToolBarUI) m_toolbar.getUI()).setFloating(true, new Point(get_pas().get_mappane().get_dimension().width, 50));
	}
	public SendObject(PAS pas, ActionListener callback) {
		//init("No name", "-1");
		m_callback = callback;
	}
	public void set_type(int n_type) {
		SendProperties tmp = null;
		switch(n_type) {
		case SendProperties.SENDING_TYPE_POLYGON_:
			log.debug("SendingType=Polygon");
			if(m_sendproperties!=null)
				tmp = m_sendproperties;
			m_sendproperties = new SendPropertiesPolygon(new PolygonStruct(get_navigation().getDimension()), m_toolbar, new Col(m_default_color));
			if(tmp!=null)
				m_sendproperties.CopyCommons(tmp);
			get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.SENDING_POLY, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			break;
		case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
			log.debug("SendingType=Gemini streetcode");
			try {
				if(m_sendproperties!=null)
					tmp = m_sendproperties;
				if(m_sendproperties==null || !m_sendproperties.getClass().equals(SendPropertiesGIS.class)) { //get_shapestruct().getClass().equals(GISShape.class)) {
					m_sendproperties = new SendPropertiesGIS(m_toolbar);
				}
				if(tmp!=null)
					m_sendproperties.CopyCommons(tmp);
			} catch(Exception e) {
			}
			get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.SENDING_POLY, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			break;
		case SendProperties.SENDING_TYPE_CIRCLE_:
			log.debug("SendingType=Ellipse");
			if(m_sendproperties!=null)
				tmp = m_sendproperties;
			m_sendproperties = new SendPropertiesEllipse(new EllipseStruct(), m_toolbar, m_default_color);
			if(tmp!=null)
				m_sendproperties.CopyCommons(tmp);
			get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.SENDING_ELLIPSE, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			break;
		case SendProperties.SENDING_TYPE_MUNICIPAL_:
			log.debug("SendingType=Municipal");
			
			if(m_sendproperties!=null && m_sendproperties.getClass().equals(SendPropertiesMunicipal.class))
			{
				
			}
			else
			{
				if(m_sendproperties!=null)
					tmp = m_sendproperties;
				m_sendproperties = new SendPropertiesMunicipal(new MunicipalStruct(), m_toolbar);
				if(tmp!=null)
					m_sendproperties.CopyCommons(tmp);
			}
			//get_callback().actionPerformed(new ActionEvent(new Integer(MapFrame.MAP_MODE_SENDING_POLY), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			break;
		case SendProperties.SENDING_TYPE_TAS_COUNTRY_:
			{
				log.debug("SendingType=TAS Country");
				//m_sendproperties = new SendPropertiesPolygon(new PolygonStruct(get_navigation().getDimension()), m_toolbar, new Col(m_default_color));
				m_sendproperties = new SendPropertiesTAS(m_toolbar);
				
			}
        case SendProperties.SENDING_TYPE_HOUSESELECT_ALERT_:
            // Using GIS sending as basis for selecting houses
            log.debug("SendingType=House select");
            try {
                if(m_sendproperties!=null)
                    tmp = m_sendproperties;
                if(m_sendproperties==null || !m_sendproperties.getClass().equals(SendPropertiesGIS.class)) { //get_shapestruct().getClass().equals(GISShape.class)) {
                    m_sendproperties = new SendPropertiesGIS(m_toolbar);
                }
                if(tmp!=null)
                    m_sendproperties.CopyCommons(tmp);
            } catch(Exception e) {
            }
            get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.HOUSESELECT_ALERT, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));

		}
		get_toolbar().set_sendingtype();
		get_sendproperties().set_sendingname(m_toolbar.m_txt_sendname.getText(), "");
		
	}
	public void destroy_all() {
		try {
			if(get_sendwindow()!=null) {
				get_sendwindow().close();
			}
		} catch(Exception e) {
			Error.getError().addError("SendObject","Exception in destroy_all",e,1);
		}
		get_toolbar().close();
	}
	/*
	void init(String sz_name, String sz_projectpk) {
		//m_pas			= pas;
		m_sz_name		= sz_name;
		m_sz_projectpk	= sz_projectpk;
		//m_current_mousepos = new Point();
	}*/
	public void draw(Graphics g, Point mousepos) {
		get_sendproperties().draw(g, mousepos);
	}
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance) {
		return get_sendproperties().snap_to_point(p1, n_max_distance);
	}
	public void goto_area() {
		//if(!get_sendproperties().goto_area())
		log.debug("SendObject.goto_area()");
		get_sendproperties().goto_area();
			//PAS.get_pas().add_event("Could not execute goto_area()", null);
	}
	public boolean setLocked(boolean b) {
		if(b) { //try to lock
			if(!get_sendproperties().can_lock()) {
				return false;
			}
			if(m_toolbar.get_addresstypes()<1)
				return false;
		} else { //try to unlock
			
		}
		m_b_locked = b;
		//PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_ZOOM);
		//PAS.get_pas().get_mainmenu().set_pan();
		//PAS.get_pas().kickRepaint();
        if (b) {
            get_callback().actionPerformed(new ActionEvent(MapFrame.MapMode.PAN, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
        } else {
            get_callback().actionPerformed(new ActionEvent(Variables.getMapFrame().get_prev_mode(), ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
        }
		
		return true;
	}
	protected void destroy_sending() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_sending_close");
		get_callback().actionPerformed(e);
	}
}