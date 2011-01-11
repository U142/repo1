package no.ums.pas.maps.defines;

import no.ums.pas.PAS;
import no.ums.pas.gps.GPSCoor;
import no.ums.pas.ums.tools.TextFormat;

import java.awt.*;
import java.util.ArrayList;


public class MapObjectList extends ArrayList<MapObject> {
	public static final long serialVersionUID = 1;
	PAS m_pas;
	String m_sz_listname;
	int m_n_last_update_date;
	int m_n_last_update_time;
	public PAS get_pas() { return m_pas; }
	
	public MapObjectList(PAS pas) {
		super();
		m_pas = pas;
		m_n_last_update_date = 0;
		m_n_last_update_time = 0;
	}
	public MapObject add(String [] sz_values) {
		return add(sz_values[0], sz_values[1], sz_values[2], sz_values[3], sz_values[4], sz_values[5],
				   sz_values[6], sz_values[7], sz_values[8], sz_values[9], sz_values[10], sz_values[11],
				   sz_values[12], sz_values[13], sz_values[14], sz_values[15], sz_values[16], sz_values[17],
				   sz_values[18], sz_values[19], sz_values[20], sz_values[21], sz_values[22], sz_values[23],
				   sz_values[24], sz_values[25], sz_values[26], sz_values[27], sz_values[28], sz_values[29],
				   sz_values[30]);
	}
	private MapObject add(String l_objectpk, String sz_name, String l_comppk, String l_deptpk, String l_lon, 
			  		String l_lat, String l_picturepk, String l_resourcepk, String l_unitpk, String l_timeinterval,
					String l_moveinterval, String sz_gsmno, String l_lastdate, String l_lasttime, String l_lastspeed,
					String l_lastcourse, String l_lastsatellites, String l_lastasl, String l_lastbattery, String f_satfix,
					String sz_iconname, String sz_iconfile, String sz_street, String sz_region, String sz_gsmno2,
					String sz_manufacturer, String sz_usertype, String sz_imei, String sz_simid, String f_online,
					String l_serverport) {
		MapObject obj = check_existing(l_objectpk);
		if(obj==null) { //INSERT
			obj = new MapObject(this, l_objectpk, sz_name, l_comppk, l_deptpk, l_lon, l_lat, l_picturepk,
										  l_resourcepk, l_unitpk, l_timeinterval, l_moveinterval, sz_gsmno, l_lastdate,
										  l_lasttime, l_lastspeed, l_lastcourse, l_lastsatellites, l_lastasl,
										  l_lastbattery, f_satfix, sz_iconname, sz_iconfile, sz_street, sz_region,
										  sz_gsmno2, sz_manufacturer, sz_usertype, sz_imei, sz_simid, f_online, 
										  l_serverport);
			//obj.load_icon();
			add(obj);
		}
		else { //UPDATE
			obj.set_properties(l_objectpk, sz_name, l_comppk, l_deptpk, l_lon, l_lat, l_picturepk, 
							   l_resourcepk, l_unitpk, l_timeinterval, l_moveinterval, sz_gsmno, 
							   l_lastdate, l_lasttime, l_lastspeed, l_lastcourse, l_lastsatellites, 
							   l_lastasl, l_lastbattery, f_satfix, sz_iconname, sz_iconfile, sz_street, sz_region,
							   sz_gsmno2, sz_manufacturer, sz_usertype, sz_imei, sz_simid, f_online, l_serverport);
			//get_pas().get_gpsframe().get_panel().set_rowvalues(obj);
		}
		return obj;
	}
	
	/*
	 * set the last update date/time, to use as filter for next update
	 */
	public void set_last_gpstime(String sz_date, String sz_time)
	{
		int n_date = new Integer(sz_date).intValue();
		int n_time = new Integer(sz_time).intValue();
		if((m_n_last_update_date == n_date && n_time > m_n_last_update_time) || n_date > m_n_last_update_date)
		{
			m_n_last_update_date = n_date;
			m_n_last_update_time = n_time;
		}
	}
	
	/*
	 * check if object already exists when getting gps updates
	 */
	private MapObject check_existing(String l_objectpk)
	{
		MapObject ret = null;
		MapObject current = null;
		for(int i=0; i < size(); i++)
		{
			current = (MapObject)get(i);
			if(current.get_objectpk().equals(l_objectpk))
			{
				ret = current;
				break;
			}
		}
		return ret;
	}
	@Override
	public boolean add(MapObject obj) {
		return super.add(obj);
	}
	public void remove(MapObject obj) {
		super.remove(obj);
	}
	public void clear() {
		super.clear();
	}
	
	public void goto_object(String l_objectpk, int n_zoom)
	{
		MapObject obj = find(l_objectpk);
		if(obj!=null)
			get_pas().get_navigation().exec_adrsearch(obj.get_lon(), obj.get_lat(), n_zoom);
		
	}
	public void set_trail(MapObject obj, boolean b_trail)
	{
		if(obj!=null)
		{
			if((b_trail && !obj.get_trail()) || !b_trail && obj.get_trail()) //change
			{
				obj.set_trail(b_trail);
				if(b_trail) //change
					get_pas().add_event("Trail activated on object " + obj.get_name(), null);
				else
					get_pas().add_event("Trail deactivated on object " + obj.get_name(), null);
			}
		}
	}
	public void set_alert(MapObject obj, boolean b_alert)
	{
		if(obj!=null)
			obj.set_alert(b_alert);
	}
	public void set_visible(MapObject obj, boolean b_visible)
	{
		if(obj!=null)
			obj.set_visible(b_visible);
	}
	public void set_follow(MapObject obj, boolean b_follow)
	{
		if(obj!=null)
		{
			get_pas().get_gpscontroller().set_follow_me(obj, b_follow);
		}
	}
	public void set_objectproperties(String l_objectpk, boolean b_trail, boolean b_alert, 
									 boolean b_visible, boolean b_follow)
	{
		MapObject current = find(l_objectpk);
		if(current!=null)
		{
			set_trail(current, b_trail);
			set_alert(current, b_alert);
			set_visible(current, b_visible);
			set_follow(current, b_follow);
			get_pas().kickRepaint();
		}
		else
		{
			get_pas().add_event("No object found with objectpk " + l_objectpk, null);
		}
	}
	
	public MapObject find(String l_objectpk)
	{
		MapObject current;
		for(int i=0; i < size(); i++)
		{
			current = (MapObject)get(i);
			if(current.get_objectpk().equals(l_objectpk))
				return current;
		}
		return null;
	}
	
	/*
	 * calc new screencoords for both MapObject and their respective GPSCoor's
	 */
	public void calcGpsCoords() {
		MapObject current;
//		GPSCoor gps;
		for(int i=0; i < size(); i++) {
			current = (MapObject)get(i);
			calcGpsCoords(current, true);
		}
	}
	public void calcGpsCoords(MapObject obj, boolean b_gpscoords) {
		obj.set_screencoords(get_pas().get_navigation().coor_to_screen(obj.get_lon(), obj.get_lat(), false));
		if(b_gpscoords && obj.get_gpscoors()!=null)
		{
			GPSCoor gps;
			for(int j=obj.get_gpscoors().size()-1; j >= 0; j--)
			{
				gps = (GPSCoor)obj.get_gpscoors().get(j);
				gps.set_screencoords(get_pas().get_navigation().coor_to_screen(gps.get_lon(), gps.get_lat(), false));
			}
		}		
	}
	/*
	 * draw
	 */
	public void draw(Graphics g)
	{
		MapObject current;
		double n_percent = new Double((double)get_pas().get_mapproperties().get_iconsize_percent()/100).doubleValue();
		double f_width, f_height;
//		int n_width, n_height;

		//draw trails first
		for(int i=0; i < size(); i++) {
			current = (MapObject)get(i);
			if(current.get_trail())
			{
				g.setColor(current.get_trail_color());
				current.draw_trail(g);
			}
		}
		//draw objects
		for(int i=0; i < size(); i++) {
			current = (MapObject)get(i);
			//draw object
			if(current.get_screencoords()!=null) //visible
			{
				f_width = ((double)current.get_icon().getIconWidth() * n_percent);
				f_height = ((double)current.get_icon().getIconHeight() * n_percent);
				g.drawImage(current.get_icon().getImage(), current.get_screencoords().width - (int)f_width/2, current.get_screencoords().height - (int)f_height/2, (int)Math.round(f_width), (int)Math.round(f_height), null);
			}			
		}
		for(int i=0; i < size(); i++) {
			current = (MapObject)get(i);
			if(current.get_screencoords()!=null && current.get_trail())
				draw_objdetails(g, current);
		}
	}
	public void draw_objdetails(Graphics g, MapObject obj)
	{
		Dimension quad = new Dimension();
		Point p = new Point();
		p.x = obj.get_screencoords().width;
		p.y = obj.get_screencoords().height;
		quad.width = 125;
		if(obj.get_dynamic()) quad.height = 48; else quad.height = 24;
		g.setColor(Color.black);
		g.drawRect(p.x, p.y, quad.width, quad.height);
		g.setColor(new Color((float)1.0, (float)1.0, (float)1.0, (float)0.6));
		g.fillRect(p.x, p.y, quad.width, quad.height);
		g.setColor(Color.black);
		int n_lineno = 0; int n_linedist = 12; int n_xpad = 3;
		//FontSet g.setFont(new Font("Arial", Font.PLAIN, n_linedist - 2));
		g.drawString(obj.get_name(), p.x + n_xpad, p.y + (n_linedist - 2) + (n_lineno++ * n_linedist));
		//g.drawString(obj.get_gsmno(), p.x + 5, p.y + (n_linedist - 2) + (n_lineno++ * n_linedist));
		g.drawString(obj.get_street().substring(0, (obj.get_street().length()<18 ? obj.get_street().length() : 18)), p.x + n_xpad, p.y + (n_linedist - 2) + (n_lineno++ * n_linedist));
		if(obj.get_dynamic()) {
			g.drawString(TextFormat.format_date(obj.get_lastdate()) + " " + TextFormat.format_time(obj.get_lasttime(), 6), p.x + n_xpad, p.y + (n_linedist - 2) + (n_lineno++ * n_linedist));
			g.drawString(((double)obj.get_lastbattery()/1000) + "V", p.x + n_xpad, p.y + (n_linedist - 2) + (n_lineno++ * n_linedist));
		}
	}
	
	public MapObject get_object(int i) { return (MapObject)super.get(i); }
	MapObjectList get_mapobjects() { return this; }
}