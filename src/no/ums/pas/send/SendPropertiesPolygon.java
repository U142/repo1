package no.ums.pas.send;

import java.awt.*;
import java.io.*;
import java.net.URL;

import java.awt.event.*;

import javax.xml.namespace.QName;

import no.ums.pas.*;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.*;
import no.ums.pas.maps.defines.*;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.parm.ArrayOfUMapPoint;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.ObjectFactory;
import no.ums.ws.parm.Parmws;
import no.ums.ws.parm.ULOGONINFO;
import no.ums.ws.parm.ULocationBasedAlert;
import no.ums.ws.parm.UMapBounds;
import no.ums.ws.parm.UMapPoint;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UAddressList;
import no.ums.ws.pas.UMAPSENDING;


//import sun.nio.cs.ext.ISCII91;

public class SendPropertiesPolygon extends SendProperties {
	//private PolygonStruct m_poly;
	//public PolygonStruct get_polygon() { return m_poly; }
	//public void set_polygon(PolygonStruct p) { m_poly = p; } 
	//private SendObject get_sendobject() { return parent.get_parent(); }
	private PolygonStruct _get_shapestruct() { return (PolygonStruct)super.get_shapestruct(); }
	private String sz_polygon_params [];
	private String sz_polygon_vals [];
	public String [] get_polygon_params() { return sz_polygon_params; }
	public String [] get_polygon_vals() { return sz_polygon_vals; }
	private PAS get_pas() { return PAS.get_pas(); }
	public void set_polygon_color() {
		get_shapestruct().set_fill_color(m_col_default.get_fill());
		get_shapestruct().set_border_color(m_col_default.get_border());
	}
	
	@Override
	public void calc_coortopix()
	{
		get_shapestruct().calc_coortopix(PAS.get_pas().get_navigation());
	}


	public SendPropertiesPolygon(PolygonStruct poly, SendOptionToolbar parent, Col col_default) {
		super(SendProperties.SENDING_TYPE_POLYGON_, parent, col_default);
		set_shapestruct(poly);
		set_shapestruct(poly);
		get_shapestruct().set_border_color(col_default.get_border());
		get_shapestruct().set_fill_color(col_default.get_fill());
	}
	public void set_adrinfo(Object poly) {
		set_shapestruct((PolygonStruct)poly);
	}
	public boolean can_lock() {
		return get_shapestruct().can_lock();
	}
	public void set_color(Color c) {
		get_shapestruct().set_fill_color(c);
	}
	public Color get_color() { 
		return get_shapestruct().get_fill_color();
	}
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance) {
		Point p2 = null;
		PolySnapStruct snapat = null;
		long n_distance = 0;
		boolean b_current = false;
		if(get_sendobject().isActive())
			b_current = true;
		snapat = get_shapestruct().snap_to_point(p1, n_max_distance, b_current, get_pas().get_mapsize(), get_pas().get_navigation());
		return snapat;
	}
	public boolean create_paramvals() {
		try {
			
			/*sz_polygon_params	= new String[_get_shapestruct().get_size() + 1];
			sz_polygon_vals		= new String[_get_shapestruct().get_size() + 1];			
			sz_polygon_params[0]	= "n_polypoints";
			sz_polygon_vals[0]		= new Integer(_get_shapestruct().get_size()).toString();
			double lon, lat;
			for(int i=1; i < (_get_shapestruct().get_size()) + 1; i++) {
				sz_polygon_params[i]	= "p" + (i-1);
				lon = (double)( Math.round(((Double)_get_shapestruct().get_coors_lon().get(i-1)).doubleValue() * 1000000.0)) / 1000000.0;
				lat = (double)( Math.round(((Double)_get_shapestruct().get_coors_lat().get(i-1)).doubleValue() * 1000000.0)) / 1000000.0;
				//(double)(Math.round(get_lon() * 10000)) / 10000 + ", " + (double)(Math.round(get_lat() * 10000)) / 10000;
				//sz_polygon_vals[i]		= ((Double)get_polygon().get_coors_lon().get(i-1)).toString() + "," + ((Double)get_polygon().get_coors_lat().get(i-1)).toString();
				sz_polygon_vals[i] = new Double(lon).toString() + "," + new Double(lat).toString();
				System.out.println(sz_polygon_vals[i]);
			}*/
			sz_polygon_params	= new String[_get_shapestruct().get_show_size() + 1];
			sz_polygon_vals		= new String[_get_shapestruct().get_show_size() + 1];
			sz_polygon_params[0]	= "n_polypoints";
			sz_polygon_vals[0]		= new Integer(_get_shapestruct().get_show_size()).toString();
			double lon, lat;
			int i;
			for(i=1; i < (_get_shapestruct().get_show_size()) + 1; i++) {
				sz_polygon_params[i]	= "p" + (i-1);
				lon = (double)( Math.round(((Double)_get_shapestruct().get_coors_show_lon().get(i-1)).doubleValue() * 1000000.0)) / 1000000.0;
				lat = (double)( Math.round(((Double)_get_shapestruct().get_coors_show_lat().get(i-1)).doubleValue() * 1000000.0)) / 1000000.0;
				sz_polygon_vals[i] = new Double(lon).toString() + "," + new Double(lat).toString();
				System.out.println(sz_polygon_vals[i]);
			}

			System.out.println("Points : " + _get_shapestruct().get_show_size());
			
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesPolygon.create_paramvals() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesPolygon","Exception in create_parmvals",e,1);
			return false;
		}
		return true;
	}
	protected final boolean send() {

		try {
			/*if(!this.create_paramvals()) {
				return false;
			}*/

			ObjectFactory factory = new ObjectFactory();
			no.ums.ws.parm.ULOGONINFO logon = factory.createULOGONINFO();
			no.ums.ws.parm.UPOLYGONSENDING poly = factory.createUPOLYGONSENDING();
			UMapBounds bounds = factory.createUMapBounds();
			 
			populate_common((no.ums.ws.parm.UMAPSENDING)poly, logon, bounds);
			
			ArrayOfUMapPoint points;
			if(!get_isresend())
				points = createWSPolygon();
			else
				points = null;
			poly.setPolygonpoints(points);
			
			
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execPolygonSending(poly);
			parse_sendingresults(response, true);
			return true;
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesPolygon.send() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesPolygon","Exception in send",e,1);
			return false;
		}
		
		//return true;
	}
	
	protected ArrayOfUMapPoint createWSPolygon()
	{
		ObjectFactory factory = new ObjectFactory();
		ArrayOfUMapPoint points = factory.createArrayOfUMapPoint();

		double lon, lat;
		for(int i=0; i < (_get_shapestruct().get_show_size()); i++) {
			lon = ( Math.round(((Double)_get_shapestruct().get_coors_show_lon().get(i)).floatValue() * 1000000.0)) / 1000000.0;
			lat = ( Math.round(((Double)_get_shapestruct().get_coors_show_lat().get(i)).floatValue() * 1000000.0)) / 1000000.0;
			UMapPoint p = new UMapPoint();
			p.setLat((float)lat);
			p.setLon((float)lon);
			points.getUMapPoint().add(p);
		}		
		
		return points;
	}
	
	public void draw(Graphics g, Point mousepos) {
		draw(g, !get_sendobject().isActive(), (!get_sendobject().isLocked() && get_sendobject().isActive() ? false : true), mousepos);
	}
	public void draw(Graphics g, boolean b_dashed, boolean b_drawmode, Point lastpoint) {
		if(get_shapestruct()==null)
			return;
		if(PAS.get_pas() != null)
			get_shapestruct().draw(g, get_pas().get_navigation(), b_dashed, b_drawmode, get_toolbar().get_parent().isActive(), lastpoint);
		else
			get_shapestruct().draw(g, get_toolbar().get_parent().get_navigation(), b_dashed, b_drawmode, get_toolbar().get_parent().isActive(), lastpoint);
		if(get_toolbar().get_parent().isActive()) {
			/*try {
				draw_last_line(g);
			} catch(Exception e) {
				get_pas().add_event("Error: SendObject.draw.draw_last_line(g)", e);
			}*/
		}
	}
	void draw_last_line(Graphics g, Point lastpoint) {
		if(get_pas()==null || get_shapestruct()==null || lastpoint==null || get_pas().get_navigation()==null)
			return;
		if(lastpoint==null || _get_shapestruct().get_size() < 1)
			return;
		if(get_pas().get_mappane().get_mode()==MapFrame.MAP_MODE_SENDING_POLY) {
			try {
				MapPointPix p1 = new MapPointPix(get_shapestruct().typecast_polygon().get_pix_int_x()[_get_shapestruct().get_size()-1], _get_shapestruct().get_pix_int_y()[_get_shapestruct().get_size()-1]);
				MapPointPix p2 = new MapPointPix(lastpoint.x, lastpoint.y);
				Long n_dist = new Long(get_pas().get_navigation().calc_distance(p1, p2));				
				String sz_distance = n_dist.longValue() + "m";
				g.setColor(new Color((float)0.2, (float)0.2, (float)0.2, (float)1.0));
				g.drawLine(_get_shapestruct().get_pix_int_x()[_get_shapestruct().get_size()-1], _get_shapestruct().get_pix_int_y()[_get_shapestruct().get_size()-1], lastpoint.x, lastpoint.y);
				g.drawString(sz_distance, lastpoint.x + 10, lastpoint.y);
			} catch(Exception e) {
				Error.getError().addError("SendPropertiesPolygon","Exception in draw_last_line",e,1);
			}
		}
	}
	public boolean goto_area() {
		NavStruct nav = get_shapestruct().calc_bounds();
		if(nav==null)
			return false;
		get_sendobject().get_callback().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
		return true;
	}
	public void insert_at(PolySnapStruct at) {
		_get_shapestruct().set_activepoint(at);
	}
	public void remove_at(PolySnapStruct at) {
		_get_shapestruct().remove_at(at.get_polyindex());
	}
	public void move_at(PolySnapStruct at) {
		_get_shapestruct().move_at(at.get_polyindex());
	}
	public void reverse_polypoints() {
		_get_shapestruct().reverse_coor_order();
	}
	
	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		no.ums.ws.parm.ObjectFactory factory = new no.ums.ws.parm.ObjectFactory();
		no.ums.ws.parm.ArrayOfUMapPoint points = createWSPolygon();
		no.ums.ws.parm.UPOLYGONSENDING ms = factory.createUPOLYGONSENDING();
		ms.setPolygonpoints(points);
		
		return super._ExecAdrCount(ms, l, act);
		
	}
	
	
}