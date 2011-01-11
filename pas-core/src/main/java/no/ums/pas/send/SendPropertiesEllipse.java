package no.ums.pas.send;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.core.dataexchange.HttpPostForm;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.*;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.*;
import no.ums.ws.parm.ArrayOfUMapPoint;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.ObjectFactory;
import no.ums.ws.parm.Parmws;
import no.ums.ws.parm.UEllipseDef;
import no.ums.ws.parm.UMapBounds;
import no.ums.ws.parm.UMapPoint;



public class SendPropertiesEllipse extends SendProperties {

	//private EllipseStruct m_ellipse;
	//public EllipseStruct get_ellipse() { return m_ellipse; }
	//public void set_ellipse(EllipseStruct e) { m_ellipse = e; }
	private String sz_ellipse_params [];
	private String sz_ellipse_vals [];
	public String [] get_ellipse_params() { return sz_ellipse_params; }
	public String [] get_ellipse_vals() { return sz_ellipse_vals; }
	private EllipseStruct _get_shapestruct() { return (EllipseStruct)super.get_shapestruct(); }
	
	private PAS get_pas() { return PAS.get_pas(); }

	public SendPropertiesEllipse(EllipseStruct e, SendOptionToolbar parent, Col col_default) {
		super(SendProperties.SENDING_TYPE_CIRCLE_, parent, col_default);
		//m_ellipse = e;
		//set_shapestruct(e);
		set_shapestruct(e);
		set_color(new Color((float)0.5, (float)0.0, (float)0.0, (float)0.2));
	}
	@Override
	public void calc_coortopix()
	{
		get_shapestruct().calc_coortopix(variables.NAVIGATION);
	}

	
	public boolean can_lock() {		
		return get_shapestruct().can_lock(null);
	}
	public Color get_color() { 
		return get_shapestruct().get_fill_color();
	}
	public void set_color(Color col) {
		get_shapestruct().set_fill_color(col);
	}
	public MapPoint get_center() {
		return get_shapestruct().typecast_ellipse().get_center();
	}
	public MapPoint get_corner() {
		return get_shapestruct().typecast_ellipse().get_corner();
	}
	public NavStruct calc_bounds() {
		return get_shapestruct().calc_bounds();
	}
	
	public void draw(Graphics g, Point p) {
		draw(g, !get_sendobject().isActive(), (!get_sendobject().isLocked() && get_sendobject().isActive() ? false : true), p);
	}
	public void draw(Graphics g, boolean b_dashed, boolean b_drawmode, Point lastpoint) {
		if(get_shapestruct()==null)
			return;
		get_shapestruct().draw(g, get_pas().get_navigation(), b_dashed, b_drawmode, get_toolbar().get_parent().isActive(), lastpoint);
	}	

	public boolean goto_area() {
		try {
			NavStruct nav = get_shapestruct().calc_bounds();
			get_sendobject().get_callback().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendPropertiesEllipse","Exception in goto_area",e,1);
		}
		return false;
	}	

	public boolean create_paramvals() {
		/*try {
			sz_polygon_params	= new String[_get_shapestruct().get_size() + 1];
			sz_polygon_vals		= new String[_get_shapestruct().get_size() + 1];
			sz_polygon_params[0]	= "n_polypoints";
			sz_polygon_vals[0]		= new Integer(get_shapestruct().typecast_polygon().get_size()).toString();
			double lon, lat;
			for(int i=1; i < (_get_shapestruct().get_size()) + 1; i++) {
				sz_polygon_params[i]	= "p" + (i-1);
				lon = (double)( Math.round(((Double)_get_shapestruct().get_coors_lon().get(i-1)).doubleValue() * 1000000.0)) / 1000000.0;
				lat = (double)( Math.round(((Double)_get_shapestruct().get_coors_lat().get(i-1)).doubleValue() * 1000000.0)) / 1000000.0;
				//(double)(Math.round(get_lon() * 10000)) / 10000 + ", " + (double)(Math.round(get_lat() * 10000)) / 10000;
				//sz_polygon_vals[i]		= ((Double)get_polygon().get_coors_lon().get(i-1)).toString() + "," + ((Double)get_polygon().get_coors_lat().get(i-1)).toString();
				sz_polygon_vals[i] = new Double(lon).toString() + "," + new Double(lat).toString();
				System.out.println(sz_polygon_vals[i]);
			}
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesPolygon.create_paramvals() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesPolygon","Exception in create_parmvals",e,1);
			return false;
		}*/
		try {
			sz_ellipse_params	= new String[4];
			sz_ellipse_vals		= new String[4];
			sz_ellipse_params[0]	= "center_x";
			sz_ellipse_params[1]	= "center_y";
			sz_ellipse_params[2]	= "radius_x";
			sz_ellipse_params[3]	= "radius_y";
			sz_ellipse_vals[0]		= new Double(_get_shapestruct().get_center().get_lon()).toString();
			sz_ellipse_vals[1]		= new Double(_get_shapestruct().get_center().get_lat()).toString();
			
			double d_width, d_height;
			//Math.
			d_width					= new Float(Math.abs(_get_shapestruct().get_corner().get_lon() - _get_shapestruct().get_center().get_lon())).floatValue();
			d_height				= new Float(Math.abs(_get_shapestruct().get_corner().get_lat() - _get_shapestruct().get_center().get_lat())).floatValue();
			sz_ellipse_vals[2]		= "" + d_width; //new Float( (float)((int)((d_width) * 10000000.0) / 10000000.0)).toString();
			sz_ellipse_vals[3]		= "" + d_height; //new Float( (float)((int)((d_height) * 10000000.0) / 10000000.0)).toString();
		} catch(Exception e) {
			
		}
		return true;
	}
	public boolean send() {
		try {
			/*if(!this.create_paramvals()) {
				return false;
			}*/
			/*HttpPostForm http = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_send.asp");
			populate_common(http);

			XmlWriter xmlWriter = new XmlWriter();
			
			File cb = new File(xmlWriter.generateCBXML(get_cell_broadcast_text())); // Her lager jeg og skriver xmlfilen av cbinfo
			InputStream fis = new FileInputStream(cb);
			
			http.setParameter("cbpoly","cbtemp.xml",fis); // lage xmlwriter som tar inn alert og lager xml av det
			
			if(!get_isresend()) { //we don't need adrinfo if it's a resend
				for(int i=0; i < get_ellipse_params().length; i++) {
					http.setParameter(get_ellipse_params()[i], get_ellipse_vals()[i]);
				}
			}
			InputStream is = http.post();
			// Her kan jeg kanskje slette cbtemp.xml?
			// xmlWriter.deleteZip();
			return Error.getError().addError(is);*/
			ObjectFactory factory = new ObjectFactory();
			no.ums.ws.parm.ULOGONINFO logon = factory.createULOGONINFO();
			no.ums.ws.parm.UELLIPSESENDING poly = factory.createUELLIPSESENDING();
			//UEllipseDef el = factory.createUEllipseDef();
			UMapBounds bounds = factory.createUMapBounds();
			
			 
			populate_common((no.ums.ws.parm.UMAPSENDING)poly, logon, bounds);
			/*UMapPoint center = factory.createUMapPoint();
			UMapPoint radius = factory.createUMapPoint();
			center.setLon((float)_get_shapestruct().get_center().get_lon());
			center.setLat((float)_get_shapestruct().get_center().get_lat());
			radius.setLon(new Float(Math.abs(_get_shapestruct().get_corner().get_lon() - _get_shapestruct().get_center().get_lon())).floatValue());
			radius.setLat(new Float(Math.abs(_get_shapestruct().get_corner().get_lat() - _get_shapestruct().get_center().get_lat())).floatValue());
			el.setCenter(center);
			el.setRadius(radius);*/
			UEllipseDef el = createWSEllipse();
			poly.setEllipse(el);
			
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execEllipseSending(poly);
			parse_sendingresults(response, true);


			return true;
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesPolygon.send() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesPolygon","Exception in send",e,1);
			return false;
		}
		
	}
	protected UEllipseDef createWSEllipse()
	{
		ObjectFactory factory = new ObjectFactory();
		UEllipseDef el = factory.createUEllipseDef();
		UMapPoint center = factory.createUMapPoint();
		UMapPoint radius = factory.createUMapPoint();
		center.setLon((float)_get_shapestruct().get_center().get_lon());
		center.setLat((float)_get_shapestruct().get_center().get_lat());
		radius.setLon(new Float(Math.abs(_get_shapestruct().get_corner().get_lon() - _get_shapestruct().get_center().get_lon())).floatValue());
		radius.setLat(new Float(Math.abs(_get_shapestruct().get_corner().get_lat() - _get_shapestruct().get_center().get_lat())).floatValue());
		el.setCenter(center);
		el.setRadius(radius);
		return el;
	}
	
	public void set_adrinfo(Object o) {

	}

	public PolySnapStruct snap_to_point(Point p, int i) {
		return null;
	}
	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		no.ums.ws.parm.ObjectFactory factory = new no.ums.ws.parm.ObjectFactory();
		no.ums.ws.parm.UEllipseDef ell = createWSEllipse();
		no.ums.ws.parm.UELLIPSESENDING ms = factory.createUELLIPSESENDING();
		ms.setEllipse(ell);
		
		return super._ExecAdrCount(ms, l, act);
	
	}
	

	

}