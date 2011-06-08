package no.ums.pas.send;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.common.UEllipseDef;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.UMapBounds;
import no.ums.ws.common.UMapPoint;
import no.ums.ws.common.parm.UELLIPSESENDING;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.ObjectFactory;
import no.ums.ws.parm.Parmws;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;



public class SendPropertiesEllipse extends SendProperties {

    private static final Log log = UmsLog.getLogger(SendPropertiesEllipse.class);

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
		get_shapestruct().calc_coortopix(Variables.getNavigation());
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
		get_shapestruct().draw(g, get_pas().get_mappane().getMapModel(), get_pas().get_mappane().getZoomLookup(), b_dashed, b_drawmode, get_toolbar().get_parent().isActive(), lastpoint);
	}	

	public boolean goto_area() {
		try {
			NavStruct nav = get_shapestruct().calc_bounds();
			get_sendobject().get_callback().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			return true;
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendPropertiesEllipse","Exception in goto_area",e,1);
		}
		return false;
	}	

	public boolean create_paramvals() {
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
			ObjectFactory factory = new ObjectFactory();
			ULOGONINFO logon = new ULOGONINFO();
			UELLIPSESENDING poly = new UELLIPSESENDING();
			//UEllipseDef el = factory.createUEllipseDef();
			UMapBounds bounds = new UMapBounds();
			
			 
			populate_common(poly, logon, bounds);
			UEllipseDef el = createWSEllipse();
			poly.setEllipse(el);
			
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execEllipseSending(poly);
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
		
	}
	protected UEllipseDef createWSEllipse()
	{
		UEllipseDef el = new UEllipseDef();
		UMapPoint center = new UMapPoint();
		UMapPoint radius = new UMapPoint();
		center.setLon((float)_get_shapestruct().get_center().get_lon());
		center.setLat((float)_get_shapestruct().get_center().get_lat());
		radius.setLon(Math.abs(_get_shapestruct().get_corner().get_lon() - _get_shapestruct().get_center().get_lon()));
		radius.setLat(Math.abs(_get_shapestruct().get_corner().get_lat() - _get_shapestruct().get_center().get_lat()));
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
		UEllipseDef ell = createWSEllipse();
		UELLIPSESENDING ms = new UELLIPSESENDING();
		ms.setEllipse(ell);

		return super._ExecAdrCount(ms, l, act);
	
	}
	

	

}