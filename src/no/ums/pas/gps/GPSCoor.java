package no.ums.pas.gps;

import java.awt.Dimension;

public class GPSCoor extends Object {
	private String m_l_objectpk;
	private double m_l_lon;
	private double m_l_lat;
	private int m_l_gpsdate;
	private int m_l_gpstime;
	private int m_l_speed;
	private int m_l_course;
	private int m_l_satellites;
	private int m_l_asl;
	private int m_l_battery;
	private Dimension m_dim_screencoords;
	private boolean m_b_deprecated = false;
	private double m_n_distance_to_prev = -1;
	private GPSCoor m_prev = null;
	private String m_sz_street;
	private String m_sz_region;

	
	public GPSCoor(String l_objectpk, String l_lon, String l_lat, String l_gpsdate, String l_gpstime,
			String l_speed, String l_course, String l_satellites, String l_asl, String l_battery,
			String sz_street, String sz_region)
	{
		set_properties(l_objectpk, l_lon, l_lat, l_gpsdate, l_gpstime, l_speed, l_course, l_satellites, 
					   l_asl, l_battery, sz_street, sz_region);
	}
	private void set_properties(String l_objectpk, String l_lon, String l_lat, String l_gpsdate,
								String l_gpstime, String l_speed, String l_course, String l_satellites,
								String l_asl, String l_battery, String sz_street, String sz_region)
	{
		m_l_objectpk	= l_objectpk;
		m_l_lon			= new Double(l_lon).doubleValue();
		m_l_lat			= new Double(l_lat).doubleValue();
		m_l_gpsdate		= new Integer(l_gpsdate).intValue();
		m_l_gpstime		= new Integer(l_gpstime).intValue();
		m_l_speed		= new Integer(l_speed).intValue();
		m_l_course		= new Integer(l_course).intValue();
		m_l_satellites	= new Integer(l_satellites).intValue();
		m_l_asl			= new Integer(l_asl).intValue();
		m_l_battery		= new Integer(l_battery).intValue();
		m_sz_street		= sz_street;
		m_sz_region		= sz_region;
		if(m_l_lon == 0 || m_l_lat == 0)
			set_deprecated(true);
	}
	public void calc_distance()
	{
		if(get_prev()==null)
			set_distance_to_prev(-1);
		else
		{
			double y1r = (get_lat() * Math.PI * 2) / 360;
			double d_dist = Math.sqrt( Math.pow((Math.abs(get_lat() - get_prev().get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(get_lon() - get_prev().get_lon()) * 3600 * 30.92 * Math.cos(y1r) ),2) );
			set_distance_to_prev(d_dist);
			/*
 * 	Dim y1r
	y1r = (lat1*3.141592*2)/360
	Dim nHeight, nWidth, nHeightMeters, nWidthMeters, nLength
	nHeight = abs(lat2-lat1)
	nWidth = abs(lon2-lon1)
	nHeightMeters = nHeight*3600*30.92
	nWidthMeters  = nWidth*3600*30.92*cos(y1r)
	nLength = sqr((nWidthMeters*nWidthMeters)+(nHeightMeters*nHeightMeters))
 */
		}
	}
	public void set_deprecated(boolean b) { m_b_deprecated = b; }
	public boolean get_deprecated() { return m_b_deprecated; }
	public void set_distance_to_prev(double d_meters) { m_n_distance_to_prev = d_meters; }
	public double get_distance_to_prev() { return m_n_distance_to_prev; }
	public Dimension get_screencoords() { return m_dim_screencoords; }
	public void set_screencoords(Dimension dim) { m_dim_screencoords = dim; }
	public void set_prev(GPSCoor c) { 
		m_prev = c;
		calc_distance();
	}
	public GPSCoor get_prev() { return m_prev; }
	
	public String get_objectpk() { return m_l_objectpk; }
	public double get_lon() { return m_l_lon; }
	public double get_lat() { return m_l_lat; }
	public int get_gpsdate() { return m_l_gpsdate; }
	public int get_gpstime() { return m_l_gpstime; }
	public int get_speed() { return m_l_speed; }
	public int get_course() { return m_l_course; }
	public int get_satellites() { return m_l_satellites; }
	public int get_asl() { return m_l_asl; }
	public int get_battery() { return m_l_battery; }
	public String get_street() { return m_sz_street; }
	public String get_region() { return m_sz_region; }
}