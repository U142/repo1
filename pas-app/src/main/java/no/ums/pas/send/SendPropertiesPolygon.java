package no.ums.pas.send;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.UMapBounds;
import no.ums.ws.common.UMapPoint;
import no.ums.ws.common.parm.ArrayOfUMapPoint;
import no.ums.ws.common.parm.UPOLYGONSENDING;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.Parmws;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


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
		get_shapestruct().calc_coortopix(Variables.getNavigation());
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
		return get_shapestruct().can_lock(null);
	}
	public void set_color(Color c) {
		get_shapestruct().set_fill_color(c);
	}
	public Color get_color() { 
		return get_shapestruct().get_fill_color();
	}
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance) {
		boolean b_current = get_sendobject().isActive();
		return get_shapestruct().snap_to_point(p1, n_max_distance, b_current, get_pas().get_mapsize(), get_pas().get_navigation());
	}
	public boolean create_paramvals() {
		try {			
			sz_polygon_params	= new String[_get_shapestruct().get_show_size() + 1];
			sz_polygon_vals		= new String[_get_shapestruct().get_show_size() + 1];
			sz_polygon_params[0]	= "n_polypoints";
			sz_polygon_vals[0]		= Integer.toString(_get_shapestruct().get_show_size());
			double lon, lat;
			for(int i=1; i <= (_get_shapestruct().get_show_size()); i++) {
				sz_polygon_params[i]	= "p" + (i-1);
				lon = (double)( Math.round((Double) _get_shapestruct().get_coors_show_lon().get(i - 1) * 1000000.0)) / 1000000.0;
				lat = (double)( Math.round((Double) _get_shapestruct().get_coors_show_lat().get(i - 1) * 1000000.0)) / 1000000.0;
				sz_polygon_vals[i] = Double.toString(lon) + "," + Double.toString(lat);
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
			ULOGONINFO logon = new ULOGONINFO();
			UPOLYGONSENDING poly = new UPOLYGONSENDING();
			UMapBounds bounds = new UMapBounds();
			 
			populate_common(poly, logon, bounds);
			
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
		} 
		catch(SOAPFaultException e)
		{
			PAS.pasplugin.onSoapFaultException(Variables.getUserInfo(), e);
			return false;
		}
		catch(Exception e) {
			set_last_error("ERROR SendPropertiesPolygon.send() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesPolygon","Exception in send",e,1);
			return false;
		}
		
		//return true;
	}
	
	protected ArrayOfUMapPoint createWSPolygon()
	{
		ArrayOfUMapPoint points = new ArrayOfUMapPoint();

		double lon, lat;
		for(int i=0; i < (_get_shapestruct().get_size()); i++) {
			lon = ( Math.round(_get_shapestruct().get_coors_lon().get(i).floatValue() * 1000000.0)) / 1000000.0;
			lat = ( Math.round(_get_shapestruct().get_coors_lat().get(i).floatValue() * 1000000.0)) / 1000000.0;
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
			get_shapestruct().draw(g, get_pas().get_mappane().getMapModel(), get_pas().get_mappane().getZoomLookup(), b_dashed, b_drawmode, get_toolbar().get_parent().isActive(), lastpoint, true, true, 1, true, !b_drawmode);
		else
			get_shapestruct().draw(g, Variables.getMapFrame().getMapModel(), Variables.getMapFrame().getZoomLookup(), b_dashed, b_drawmode, get_toolbar().get_parent().isActive(), lastpoint);
		if(get_toolbar().get_parent().isActive()) {
			/*try {
				draw_last_line(g);
			} catch(Exception e) {
				get_pas().add_event("Error: SendObject.draw.draw_last_line(g)", e);
			}*/
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
		_get_shapestruct().move_at();
	}
	public void reverse_polypoints() {
		_get_shapestruct().reverse_coor_order();
	}
	
	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		ArrayOfUMapPoint points = createWSPolygon();
		UPOLYGONSENDING ms = new UPOLYGONSENDING();
		ms.setPolygonpoints(points);

		return super._ExecAdrCount(ms, l, act);
		
	}
	
	
}