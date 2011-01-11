package no.ums.pas.maps.defines;

import no.ums.pas.gps.GPSCoor;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class MapObject extends Object implements Cloneable {
	
	private String m_l_objectpk = new String("");
	private String m_sz_name = new String("");
	private int m_l_comppk;
	private int m_l_deptpk;
	private double m_l_lon = 0;
	private double m_l_lat = 0;
	private String m_l_picturepk = new String("");
	private String m_l_resourcepk = new String("");
	private String m_l_unitpk = new String("");
	private int m_l_timeinterval = 0;
	private int m_l_moveinterval = 0;
	private String m_sz_gsmno = new String("");
	private int m_l_lastdate;
	private int m_l_lasttime;
	private int m_l_lastspeed;
	private int m_l_lastcourse;
	private int m_l_lastsatellites;
	private int m_l_lastasl;
	private int m_l_lastbattery;
	private boolean m_f_satfix;
	private boolean m_f_dynamic = true;
	private String m_sz_iconname = null;
	private String m_sz_iconfile = null;
	ArrayList<GPSCoor> m_gpscoors = null;
	private ImageIcon m_icon;
	MapObjectList m_objectlist;
	boolean m_b_visible = true;
	boolean m_b_alert = false;
	private Color m_trail_color = Color.red;
	private boolean m_b_trail = false;
	private boolean m_b_follow = false;
	private int m_n_trail_minutes = 0;
	private boolean m_b_added_to_list = false;
	private boolean m_b_hasposition = false;
	private boolean m_b_dirty = false;
	private boolean m_b_new = false;
	private String m_sz_street = new String("");
	private String m_sz_region = new String("");
	private int m_n_manufacturer = 0;
	private int m_n_usertype = 0;
	private String m_sz_gsmno2 = new String("");
	private int m_n_carrier_status = 0; //0=offline, 1
	private int m_n_online_status = 0;
	private String m_sz_imei;
	private String m_sz_simid;
	private int m_n_serverport = 0;
	
	public void set_objectpk(String sz_objectpk) { m_l_objectpk = sz_objectpk; }
	public void set_name(String sz_name) { m_sz_name = sz_name; }
	public void set_commpk(int n_comppk) { m_l_comppk = n_comppk; }
	public void set_deptpk(int n_deptpk) { m_l_deptpk = n_deptpk; }
	public void set_picturepk(String sz_picturepk) { m_l_picturepk = sz_picturepk; }
	public void set_resourcepk(String sz_resourcepk) { m_l_resourcepk = sz_resourcepk; }
	public void set_unitpk(String sz_unitpk) { m_l_unitpk = sz_unitpk; }
	public void set_timeinterval(int n_timeinterval) { m_l_timeinterval = n_timeinterval; }
	public void set_moveinterval(int n_moveinterval) { m_l_moveinterval = n_moveinterval; }
	public void set_gsmno(String sz_gsmno) { m_sz_gsmno = sz_gsmno; }
	public void set_iconname(String sz_iconname) { m_sz_iconname = sz_iconname; }
	public void set_iconfile(String sz_iconfile) { m_sz_iconfile = sz_iconfile; }
	public void set_icon(ImageIcon icon) { m_icon = icon; }
	public void set_street(String sz_street) { m_sz_street = sz_street; }
	public void set_region(String sz_region) { m_sz_region = sz_region; }
	public void set_manufacturer(int n) { m_n_manufacturer = n; }
	public void set_usertype(int n) { m_n_usertype = n; }
	public void set_gsmno2(String sz_gsm) { m_sz_gsmno2 = sz_gsm; }
	public void set_carrier_status(int n_status) { m_n_carrier_status = n_status; }
	public void set_online_status(int n_status) { m_n_online_status = n_status; }
	public void set_imei(String sz_imei) { m_sz_imei = sz_imei; }
	public void set_simid(String sz_simid) { m_sz_simid = sz_simid; }
	public void set_serverport(int n_port) { m_n_serverport = n_port; }
	
	public boolean isNew() { return m_b_new; }
	private MapObjectList get_objectlist() { return m_objectlist; }
	private Dimension m_dim_screencoords;
	public Dimension get_screencoords() { return m_dim_screencoords; }
	public void set_screencoords(Dimension dim) { m_dim_screencoords = dim; }
	public boolean get_visible() { return m_b_visible; }
	public void set_visible(boolean b_visible) { m_b_visible = b_visible; }
	public boolean get_alert() { return m_b_alert; }
	public void set_alert(boolean b_alert) { m_b_alert = b_alert; }
	public boolean get_follow() { return m_b_follow; }
	public void set_follow(boolean b_follow) { m_b_follow = b_follow; }
	public Color get_trail_color() { return m_trail_color; }
	public void set_trail_color(Color col) { m_trail_color = col; }
	public boolean get_trail() { return m_b_trail; }
	public int get_trail_minutes() { return m_n_trail_minutes; }
	public boolean isDirty() { return m_b_dirty; }
	public void set_trail_minutes(int n_minutes) {
		if(n_minutes > 0)
			m_n_trail_minutes = n_minutes;
	}
	public void set_trail(boolean b_trail) {
		m_b_trail = b_trail;
	}
	public void set_added_to_list() { m_b_added_to_list = true; }
	public boolean get_added_to_list() { return m_b_added_to_list; }
	public boolean get_hasposition() { return m_b_hasposition; }
	public String toString() { return m_sz_name; }
	public void set_position(double d_lon, double d_lat) {
		if(d_lon!=0 && d_lat!=0)
		{
			m_l_lon = d_lon;
			m_l_lat = d_lat;
			m_b_hasposition = true;
		}
	}
	
	public void recalc_screencoords(boolean b_include_gpscoords)
	{
		get_objectlist().calcGpsCoords(this, b_include_gpscoords);
	}
	
	public MapObject(MapObjectList objectlist) {
		m_objectlist = objectlist;
		m_b_new = true;
	}
	/*MapObject(MapObject obj) {
		return obj.clone();
	}*/
	public MapObject Clone() {
		try {
			return (MapObject)super.clone();
		} catch(CloneNotSupportedException e) {
			Error.getError().addError("MapObject","Exception in Clone",e,1);
			return null;
		}
	}
	
	public MapObject(MapObjectList objectlist, String l_objectpk, String sz_name, String l_comppk, String l_deptpk, String l_lon, 
			  String l_lat, String l_picturepk, String l_resourcepk, String l_unitpk, String l_timeinterval,
			  String l_moveinterval, String sz_gsmno, String l_lastdate, String l_lasttime, String l_lastspeed,
			  String l_lastcourse, String l_lastsatellites, String l_lastasl, String l_lastbattery, String f_satfix,
			  String sz_iconname, String sz_iconfile, String sz_street, String sz_region, String sz_gsmno2, 
			  String l_manufacturer, String l_usertype, String sz_imei, String sz_simid, String l_online, String l_serverport)
	{
		m_objectlist = objectlist;
		if (sz_gsmno.length() < 8)
			m_f_dynamic = false;
		else
		{
			m_gpscoors = new ArrayList<GPSCoor>();
			m_f_dynamic = true;
		}
	
		set_properties(l_objectpk, sz_name, l_comppk, l_deptpk, l_lon, l_lat, l_picturepk, l_resourcepk,
					   l_unitpk, l_timeinterval, l_moveinterval, sz_gsmno, l_lastdate, l_lasttime,
					   l_lastspeed, l_lastcourse, l_lastsatellites, l_lastasl, l_lastbattery, f_satfix,
					   sz_iconname, sz_iconfile, sz_street, sz_region, sz_gsmno2, l_manufacturer, l_usertype,
					   sz_imei, sz_simid, l_online, l_serverport);
	}
	public void set_properties(String l_objectpk, String sz_name, String l_comppk, String l_deptpk, String l_lon, 
			  String l_lat, String l_picturepk, String l_resourcepk, String l_unitpk, String l_timeinterval,
			  String l_moveinterval, String sz_gsmno, String l_lastdate, String l_lasttime, String l_lastspeed,
			  String l_lastcourse, String l_lastsatellites, String l_lastasl, String l_lastbattery, String f_satfix,
			  String sz_iconname, String sz_iconfile, String sz_street, String sz_region, String sz_gsmno2, 
			  String l_manufacturer, String l_usertype, String sz_imei, String sz_simid, String l_online,
			  String l_serverport)
	{
		m_l_objectpk	= l_objectpk;
		m_sz_name		= sz_name;
		m_l_comppk		= new Integer(l_comppk).intValue();
		m_l_deptpk		= new Integer(l_deptpk).intValue();
		double d_lon, d_lat;
		d_lon = new Double(l_lon).doubleValue();
		d_lat = new Double(l_lat).doubleValue();
		if(d_lon != 0 && d_lat != 0)
		{
			m_l_lon			= d_lon;
			m_l_lat			= d_lat;
			//get_objectlist().get_pas().add_event("m_l_lon = " + m_l_lon + " , m_l_lat = " + m_l_lat);
			m_b_hasposition = true;
		}
		m_l_picturepk	= l_picturepk;
		m_l_resourcepk	= l_resourcepk;
		m_l_unitpk		= l_unitpk;
		m_l_timeinterval= new Integer(l_timeinterval).intValue();
		m_l_moveinterval= new Integer(l_moveinterval).intValue();
		m_sz_gsmno		= sz_gsmno;
		m_l_lastdate	= new Integer(l_lastdate).intValue();
		m_l_lasttime	= new Integer(l_lasttime).intValue();
		m_l_lastspeed	= new Integer(l_lastspeed).intValue();
		m_l_lastcourse	= new Integer(l_lastcourse).intValue();
		m_l_lastsatellites = new Integer(l_lastsatellites).intValue();
		m_l_lastasl		= new Integer(l_lastasl).intValue();
		m_l_lastbattery = new Integer(l_lastbattery).intValue();
		m_sz_street = sz_street;
		m_sz_region = sz_region;
		m_sz_gsmno2 = sz_gsmno2;
		m_n_manufacturer = new Integer(l_manufacturer).intValue();
		m_n_usertype = new Integer(l_usertype).intValue();
		m_sz_imei		= sz_imei;
		m_sz_simid		= sz_simid;
		m_n_online_status= new Integer(l_online).intValue();
		m_n_serverport	= new Integer(l_serverport).intValue();

		if(m_sz_iconfile==null || !m_sz_iconfile.equals(sz_iconfile))
		{
			m_sz_iconname	= sz_iconname;
			m_sz_iconfile	= sz_iconfile;
			load_icon();
		}
		
		if (new Integer(f_satfix).intValue() == 1)
			m_f_satfix = true;
		else
			m_f_satfix = false;
	}
	public boolean load_icon()
	{
		String sz_icon_filename = get_objectlist().get_pas().get_sitename() + "images/map/user_objects/" + get_iconfile();
		try {
			URL url_icon = new URL(sz_icon_filename);
			//set_icon(java.awt.Toolkit.getDefaultToolkit().createImage(url_icon));
			//get_objectlist().get_pas().add_event("Icon loaded: " + url_icon.toString());
			set_icon(url_icon);
			return true;
		} catch(Exception e) { 
			get_objectlist().get_pas().add_event("load_icon() ERROR: " + e.getMessage(), e);
			Error.getError().addError("MapObject","Exception in load_icon",e,1);
			return false;
		}
	}
	public String get_objectpk() { return m_l_objectpk; }
	public String get_name() { return m_sz_name; }
	public int get_comppk() { return m_l_comppk; }
	public int get_deptpk() { return m_l_deptpk; }
	public double get_lon() { return m_l_lon; }
	public double get_lat() { return m_l_lat; }
	public void set_lon(double l) { m_l_lon = l; }
	public void set_lat(double l) { m_l_lat = l; }
	public String get_picturepk() { return m_l_picturepk; }
	public String get_resourcepk() { return m_l_resourcepk; }
	public String get_unitpk() { return m_l_unitpk; }
	public int get_timeinterval() { return m_l_timeinterval; }
	public int get_moveinterval() { return m_l_moveinterval; }
	public String get_gsmno() { return m_sz_gsmno; }
	public int get_lastdate() { return m_l_lastdate; }
	public int get_lasttime() { return m_l_lasttime; }
	public int get_lastspeed() { return m_l_lastspeed; }
	public int get_lastcourse() { return m_l_lastcourse; }
	public int get_lastsatellites() { return m_l_lastsatellites; }
	public int get_lastasl() { return m_l_lastasl; }
	public int get_lastbattery() { return m_l_lastbattery; }
	public boolean get_satfix() { return m_f_satfix; }
	public boolean get_dynamic() { return m_f_dynamic; }
	public void set_dynamic(boolean b) { m_f_dynamic = b; }
	public String get_iconname() { return m_sz_iconname; }
	public String get_iconfile() { return m_sz_iconfile; }
	public String get_street() { return m_sz_street; }
	public String get_region() { return m_sz_region; }
	public String get_gsmno2() { return m_sz_gsmno2; }
	public int get_manufacturer() { return m_n_manufacturer; }
	public int get_usertype() { return m_n_usertype; }
	public int get_onlinestatus() { return m_n_online_status; }
	public int get_carrierstatus() { return m_n_carrier_status; }
	private GPSCoor m_prevcoor = null;

	public ArrayList<GPSCoor> get_gpscoors() { return m_gpscoors; }
	public ImageIcon get_icon() { return m_icon; }
	public void set_icon(URL icon) { 
		try {
			m_icon = new ImageIcon(icon);
		} catch(Exception e) {
			Error.getError().addError("MapObject","Exception in set_icon",e,1);
		}
	}

	public GPSCoor add_gpscoor(String sz_values[])
	{
		return add_gpscoor(sz_values[0], sz_values[1], sz_values[2], sz_values[3], sz_values[4], sz_values[5], 
					sz_values[6], sz_values[7], sz_values[8], sz_values[9], sz_values[10]);
	}
	private GPSCoor add_gpscoor(String l_lon, String l_lat, String l_gpsdate, String l_gpstime,
							   String l_speed, String l_course, String l_satellites, String l_asl,
							   String l_battery, String sz_street, String sz_region)
	{
		if(!get_dynamic())
			return null;
		if(find_existing_gpscoor(new Integer(l_gpsdate).intValue(), new Integer(l_gpstime).intValue()))
			return null;
		GPSCoor gps = new GPSCoor(get_objectpk(), l_lon, l_lat, l_gpsdate, l_gpstime, l_speed, l_course, 
								  l_satellites, l_asl, l_battery, sz_street, sz_region);
		gps.set_prev(m_prevcoor);
		m_prevcoor = gps;
		//return add_gpscoor(gps);
		if(add_gpscoor(gps))
			return gps;
		return null;
	}
	
	private boolean find_existing_gpscoor(int l_gpsdate, int l_gpstime)
	{
		GPSCoor current = null;
		for(int i=get_gpscoors().size()-1; i >= 0; i--) //newest first
		{
			current = (GPSCoor)get_gpscoors().get(i);
			if(current.get_gpsdate() == l_gpsdate)
			{
				if(current.get_gpstime() == l_gpstime)
					return true;
			}
		}
		return false;
	}
	
	/*
	 * add newest coordinates first
	 */
	private boolean add_gpscoor(GPSCoor gps) {
		//get_gpscoors().add(0, gps);
		get_gpscoors().add(gps);
		return true;		
	}

	
	
	public void draw_trail(Graphics g)
	{
		if(get_gpscoors()==null)
			return;
		Graphics2D g2 = (Graphics2D)g;
		
		Stroke revertStroke = g2.getStroke();
		Stroke trailStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2.setStroke(trailStroke);
		
		GPSCoor current = null, prev = null;
		g.setClip(0, 0, get_objectlist().get_pas().get_mappane().get_dimension().width, get_objectlist().get_pas().get_mappane().get_dimension().height);
		double d_distance;
		for(int i=get_gpscoors().size()-1; i >= 0 ; i--) //newest first
		{
			try
			{
				current = (GPSCoor)get_gpscoors().get(i);
				if(i==get_gpscoors().size()-1)
					prev = current;
				d_distance = calc_distance(current, prev);
				Calendar now = Utils.get_now();				
				if(get_objectlist().get_pas().get_gpscontroller().get_if_trailed(now, current.get_gpsdate(), current.get_gpstime()))
				{
					/*if((current.get_distance_to_prev() > get_objectlist().get_pas().get_gpscontroller().get_epsilon_meters() || 
						current.get_distance_to_prev()==-1) && 
						current.get_lon() != 0 && current.get_lat() != 0 ||
						i==0 || i==get_gpscoors().size()-1)*/
					if((d_distance > get_objectlist().get_pas().get_gpscontroller().get_epsilon_meters() || i==get_gpscoors().size()-1 || i==0) &&  
					   current.get_lon() != 0 && current.get_lat() != 0)
					{
						//Draw line from prev to current
						//Color col_edge = new Color(get_trail_color());
						if(current.get_screencoords() != null && prev.get_screencoords() != null)
						{
							g2.setColor(get_trail_color());
							if(d_distance < 1000) { //else start new trail for this object
								g2.drawLine(current.get_screencoords().width, current.get_screencoords().height, prev.get_screencoords().width, prev.get_screencoords().height);
								draw_arrow(g2, 
										   new Point(current.get_screencoords().width, current.get_screencoords().height), 
										   new Point(prev.get_screencoords().width, prev.get_screencoords().height),
										   get_objectlist().get_pas().get_gpscontroller().get_arrowsize());
							}
							//g2.fillOval(prev.get_screencoords().width - 2, prev.get_screencoords().height - 2, 4, 4);
							g2.fillOval(prev.get_screencoords().width - 2, prev.get_screencoords().height - 2, 4, 4);
							g2.setColor(Color.black);
							g2.drawOval(prev.get_screencoords().width - 2, prev.get_screencoords().height - 2, 4, 4);
						}
						prev = current;
					}
					else
						continue;
				}
				else
					break; //no more lines to draw
			}
			catch(Exception e) { Error.getError().addError("MapObject","Exception in draw_trail",e,1); }
			prev = (GPSCoor)get_gpscoors().get(i);
		}
		g2.setStroke(revertStroke);
	}
	public double calc_distance(GPSCoor p1, GPSCoor p2) {
		double ret = 0;
		if(p1!=null && p2!=null)
		{
			try {
				double y1r = (p2.get_lat() * Math.PI * 2) / 360;
				double d_dist = Math.sqrt( Math.pow((Math.abs(p2.get_lat() - p1.get_prev().get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(p2.get_lon() - p1.get_prev().get_lon()) * 3600 * 30.92 * Math.cos(y1r) ),2) );
				ret = d_dist;
			} catch(Exception e) {
				ret = 0;
			}
		}
		return ret;
	}
	public void draw_arrow(Graphics g, Point p1, Point p2, double d_size)
	{
		//poly.addPoint()
		draw_arrow(g, p1, p2, d_size, 60);
	}
	private void draw_arrow(Graphics g, Point p1, Point p2, double d_arrowsize, int n_fromend)
	{
		double slopy , cosy , siny;
		double Par = d_arrowsize;  //length of Arrow (>)
		slopy = Math.atan2( ( p1.y - p2.y ),
			( p1.x - p2.x ) );
		cosy = Math.cos( slopy );
		siny = Math.sin( slopy ); //need math.h for these functions

		long n_mid_x, n_mid_y;
		n_mid_x = (long)((p1.x)+(p2.x-p1.x)*n_fromend/100);
		n_mid_y = (long)((p1.y)+(p2.y-p1.y)*n_fromend/100);
		Polygon poly = new Polygon();
		poly.addPoint((int)(n_mid_x + ( Par * cosy - ( Par / 2.0 * siny ) )), (int)(n_mid_y + ( Par * siny + ( Par / 2.0 * cosy ) ) ));
		poly.addPoint((int)(n_mid_x + ( Par * cosy + Par / 2.0 * siny )), (int)(n_mid_y - ( Par / 2.0 * cosy - Par * siny ) ));
		poly.addPoint((int)n_mid_x, (int)n_mid_y);
		g.fillPolygon(poly);
		
	}
}
