package no.ums.pas.maps.defines;

import java.awt.*;
//import PAS.*;
import java.awt.event.*;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.ums.tools.CoorConverter;
import no.ums.ws.pas.tas.UMapPoint;


public class Navigation {
	/*public class NavStruct {
		public double _lbo, _rbo, _ubo, _bbo;
		NavStruct(double lbo, double rbo, double ubo, double bbo)
		{
			_lbo = lbo; _rbo = rbo; _ubo = ubo; _bbo = bbo;
		}
		NavStruct() {
			this(-9999, -9999, -9999, -9999);
		}
		public String toString() {
			return "lbo=" + _lbo + " ubo=" + _ubo + " rbo=" + _rbo + " bbo=" + _bbo;
		}
		
	}*/
	public enum EARTH_DIRECTION
	{
		LONGITUDE,
		LATITUDE,
	}
	public class Pan {
		public static final int EAST = 6;
		public static final int WEST = 4;
		public static final int SOUTH = 2;
		public static final int NORTH = 8;
		public static final int SOUTHEAST = 3;
		public static final int SOUTHWEST = 1;
		public static final int NORTHEAST = 9;
		public static final int NORTHWEST = 7;
	}
	public class Zoom {
		public static final int ZOOMOUT = 1;
		public static final int ZOOMIN  = 2;
	}
	
	public static NavStruct NAV_WORLD = new NavStruct(-180, 180, 90, -90);

	/*public class MapPoint {
		MapPointLL m_ll;
		MapPointPix m_pix;
		public int get_x() { return m_pix.get_x(); }
		public int get_y() { return m_pix.get_y(); }
		public double get_lon() { return m_ll.get_lon(); }
		public double get_lat() { return m_ll.get_lat(); }
		public MapPointLL get_mappointll() { return m_ll; }
		public MapPointPix get_mappointpix() { return m_pix; }
		public MapPoint(Navigation nav, MapPointLL point_ll) {
			m_ll = point_ll;
			m_pix = new MapPointPix(nav.coor_to_screen(m_ll.get_lon(), m_ll.get_lat(), false));
		}
		public MapPoint(Navigation nav, MapPointPix pix) {
			m_pix = pix;
			m_ll  = new MapPointLL(nav.screen_to_coor(m_pix.get_x(), m_pix.get_y()));
		}
	}

	public class MapPointLL {
		private double m_lon;
		private double m_lat;
		public MapPointLL(double lon, double lat) {
			m_lon = lon;
			m_lat = lat;
		}
		public MapPointLL(MapPointLL ll) {
			m_lon = ll.get_lon();
			m_lat = ll.get_lat();
		}
		public double get_lon() { return m_lon; }
		public double get_lat() { return m_lat; }
	}
	public class MapPointPix {
		private Dimension m_dim;
		public MapPointPix(int x, int y) {
			m_dim = new Dimension(x, y);
		}
		public MapPointPix(Dimension dim) {
			m_dim = new Dimension(dim);
		}
		public int get_x() { return m_dim.width; }
		public int get_y() { return m_dim.height; }
	}
	*/
	
	
	
	
	Double m_f_lbo, m_f_rbo, m_f_ubo, m_f_bbo;
	double m_f_nav_lbo, m_f_nav_rbo, m_f_nav_ubo, m_f_nav_bbo;
	public Double get_lbo() { return m_f_lbo; }
	public Double get_rbo() { return m_f_rbo; }
	public Double get_ubo() { return m_f_ubo; }
	public Double get_bbo() { return m_f_bbo; }
	
	//PAS m_pas;
	Dimension m_dimension;
	Double m_f_widthprpix, m_f_heightprpix;
	Double m_f_mapwidthmeters, m_f_mapheightmeters;
	Double m_f_zoom_multiplier;
	ActionListener m_callback = null;

	public Navigation(ActionListener callback, int x, int y)
	{
		//m_pas = pas;
		m_callback = callback;
		m_dimension = new Dimension(x, y);
		m_f_nav_lbo = 10.913889554439;
		m_f_nav_rbo = 10.928453845561;
		m_f_nav_ubo = 59.9476233814255;
		m_f_nav_bbo = 59.9659090185745;
		m_f_zoom_multiplier = new Double(1.2);
	}
	
	public void set_dimension(Dimension d) {
		m_dimension = d;
	}
	private void load_map() {
		if(m_callback!=null)
			m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_loadmap"));
	}
	
	public boolean bboxOverlap(NavStruct test) {
		if(test==null)
			return false;
		if(test._rbo < m_f_lbo || test._lbo > m_f_rbo || test._ubo < m_f_bbo || test._bbo > m_f_ubo)
			return false;
		return true;
		
	}
	
	public boolean bboxEntirelyVisible(NavStruct test) {
		if(test==null)
			return false;
		if(test._lbo > m_f_lbo && test._rbo < m_f_rbo && test._ubo < m_f_ubo && test._bbo > m_f_nav_bbo)
			return true;
		return false;
	}
	
	double get_zoom_multiplier() { return m_f_zoom_multiplier.doubleValue(); }
	
	public void setNavigation(double lbo, double rbo, double ubo, double bbo)
	{
		//if(Math.abs(lbo)>=180 || Math.abs(rbo)>=180 || Math.abs(ubo)>
		if(Math.abs(lbo)>180)
			lbo = 180 * Math.signum(lbo);
		if(Math.abs(rbo)>180)
			rbo = 180 * Math.signum(rbo);
		if(Math.abs(ubo)>90)
			ubo = 90 * Math.signum(ubo);
		if(Math.abs(bbo)>90)
			bbo = 90 * Math.signum(bbo);
		m_f_nav_lbo = lbo; m_f_nav_rbo = rbo; m_f_nav_ubo = ubo; m_f_nav_bbo = bbo;
	}
	public String toString() {
		return "lbo: " + m_f_nav_lbo + " rbo: " + m_f_nav_rbo + " ubo: " + m_f_nav_ubo + " bbo: " + m_f_nav_bbo;
	}
	public void setNavigation(NavStruct nav) {
		if(nav!=null)
			setNavigation(nav._lbo, nav._rbo, nav._ubo, nav._bbo);
	}
	public void gotoMap(NavStruct nav) {
		Dimension dim;
		setNavigation(nav);
		dim = getDimensionFromBounds(nav);
		if(too_small(nav))
			exec_zoom_in(dim, dim);
		else
			load_map();
		
	}
	public void reloadMap()
	{
		load_map();
	}
	
	public Dimension getDimensionFromBounds(NavStruct nav) {		
		double x;
		double y;
		x = (nav._rbo + nav._lbo) / 2;
		y = (nav._ubo + nav._bbo) / 2;
		
		return coor_to_screen(x,y,false);
	}
	
	public boolean too_small(NavStruct nav) {
		int n_minzoom = 60;

		MapPoint mp1 = new MapPoint(variables.NAVIGATION, new MapPointLL(nav._rbo, nav._ubo));
		MapPoint mp2 = new MapPoint(variables.NAVIGATION, new MapPointLL(nav._lbo, nav._bbo));
		
		if(calc_distance(mp1, mp2) < n_minzoom)
			return true;
		else
			return false;
	}
	
	public boolean pointVisible(UMapPoint p)
	{
		if(p.getLat() > m_f_bbo && p.getLat() < m_f_ubo && p.getLon() > m_f_lbo && p.getLon() < m_f_rbo)
			return true;
		return false;
	}
	
	public void exec_zoom_in(Dimension dim_start, Dimension dim_stop) {
		double f_centerpoint_x, f_centerpoint_y;
		int n_minzoom = 60;
		//System.out.println("mapwidth = " + m_f_mapwidthmeters + " " + calc_distance(dim_stop.width, dim_stop.height, dim_start.width, dim_stop.height));
		
		double d_deltax = Math.abs(dim_stop.width-dim_start.width);
//		double d_deltay = Math.abs(dim_stop.height-dim_start.height);
		double d_dist = calc_distance(0, 0, (int)d_deltax, 0);
		System.out.println(d_dist);
		if(d_dist < n_minzoom) { //mapwidth of n_minzoom meters
			//return Math.round(Math.sqrt( Math.pow((Math.abs(px2 - px1) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth()), 2) + Math.pow((Math.abs(py2 - py1) * get_mapheightmeters().doubleValue() / m_dimension.getHeight()), 2)));
			//double f_factor = 1;
/*			Math.round(Math.sqrt( Math.pow((Math.abs(dim_stop.width - dim_start.width) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth()) * d_percent, 2) + Math.pow((Math.abs(dim_stop.height - dim_start.height) * get_mapheightmeters().doubleValue() / m_dimension.getHeight()) * d_percent, 2))) = 100
			Math.pow((Math.abs(dim_stop.width - dim_start.width) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth()) * d_percent, 2) + Math.pow((Math.abs(dim_stop.height - dim_start.height) * get_mapheightmeters().doubleValue() / m_dimension.getHeight()) * d_percent, 2)) = Math.sqrt(100);
			Math.abs(dim_stop.width - dim_start.width) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth() * d_percent + Math.abs(dim_stop.height - dim_start.height) * get_mapheightmeters().doubleValue() / m_dimension.getHeight() * d_percent = 100
			Math.abs(dim_stop.width - dim_start.width) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth() + Math.abs(dim_stop.height - dim_start.height) * get_mapheightmeters().doubleValue() / m_dimension.getHeight() = 100/d_percent*/
			//float f_factor = (float)(Math.abs(dim_stop.width - dim_start.width) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth() / 100 + Math.abs(dim_stop.height - dim_start.height) * get_mapheightmeters().doubleValue() / m_dimension.getHeight() / 100);
			//System.out.println("f_percent = " + f_factor + "  width=" + (dim_stop.width-dim_start.width) * f_factor);
			//Dimension dstart = new Dimension(Math.round(dim_start.width / f_factor), Math.round(dim_start.height / f_factor));
			//Dimension dstop  = new Dimension(Math.round(dim_stop.width / f_factor), Math.round(dim_stop.height / f_factor));
			int centerx = (dim_start.width);
			int centery = (dim_start.height);
			//calc pixels for X meters
			//int deltax = m_f_widthprpix * Xpix
			double d_metersprpix_x = m_f_mapwidthmeters.doubleValue() / m_dimension.width;
			double d_metersprpix_y = m_f_mapheightmeters.doubleValue() / m_dimension.height;
			/*double n_metersx = n_minzoom * m_f_widthprpix.doubleValue();
			double n_metersy = (n_minzoom * m_f_heightprpix.doubleValue()) * (m_f_mapwidthmeters.doubleValue() / m_f_mapheightmeters.doubleValue());
			double n_pixelsx = n_metersx / m_f_widthprpix.doubleValue();
			double n_pixelsy = n_metersy / m_f_heightprpix.doubleValue();*/
			
		
			//double d_pixelsx = 1 / (m_f_widthprpix.doubleValue() * 40);
			//double d_pixelsy = 1 / (m_f_heightprpix.doubleValue() * 40);
			
			double d_pixelsx = (n_minzoom / d_metersprpix_x) + 5;
			double d_pixelsy = (n_minzoom / d_metersprpix_y) + 5;
			
			//System.out.println(d_pixelsx + " " + d_pixelsy);
			Dimension dstart = new Dimension((int)(centerx), (int)(centery));
			Dimension dstop  = new Dimension((int)(centerx + d_pixelsx), (int)(centery + d_pixelsy));
			//m_f_widthprpix * X = n_minzoom
			//System.out.println(calc_distance(dstop.width, dstop.height, dstart.width, dstop.height));
			
			//System.out.println(dstart.width + " " + dstart.height + " " + dstop.width + " " + dstop.height);
			exec_zoom_in(dstart, dstop);
			return;
			//System.out.println(dstart.width + " " + dstart.height + " " + dstop.width + " " + dstop.height);
			//use quickzoom
			//int n_centerx = Math.abs((dim_stop.width + dim_start.width)/2);
			//int n_centery = Math.abs((dim_stop.height + dim_start.height)/2);
		}
		
		f_centerpoint_x = calc_centerpoint_x(dim_start.width);
		f_centerpoint_y = calc_centerpoint_y(dim_start.height);
		
		int n_delta_x, n_delta_y;
		n_delta_x = Math.abs(dim_start.width - dim_stop.width);
		n_delta_y = Math.abs(dim_start.height - dim_stop.height);
		double f_delta_x, f_delta_y;
		f_delta_x = n_delta_x * m_f_widthprpix.doubleValue();
		f_delta_y = n_delta_y * m_f_heightprpix.doubleValue();
		
		double lbo, rbo, ubo, bbo;
		lbo = f_centerpoint_x - f_delta_x;
		rbo = f_centerpoint_x + f_delta_x;
		ubo = f_centerpoint_y + f_delta_y;
		bbo = f_centerpoint_y - f_delta_y;
		setNavigation(lbo, rbo, ubo, bbo);
		load_map();
	}
	public void exec_zoom_out(Dimension dim_start)
	{
		double f_centerpoint_x, f_centerpoint_y;
		f_centerpoint_x = calc_centerpoint_x(dim_start.width);
		f_centerpoint_y = calc_centerpoint_y(dim_start.height);
		
		double f_delta_x, f_delta_y;
		f_delta_x = m_f_rbo.doubleValue() - m_f_lbo.doubleValue();
		f_delta_y = m_f_ubo.doubleValue() - m_f_bbo.doubleValue();
		double lbo, rbo, ubo, bbo;
		lbo = f_centerpoint_x - f_delta_x * get_zoom_multiplier();
		rbo = f_centerpoint_x + f_delta_x * get_zoom_multiplier();
		ubo = f_centerpoint_y + f_delta_y * get_zoom_multiplier();
		bbo = f_centerpoint_y - f_delta_y * get_zoom_multiplier();
		setNavigation(lbo, rbo, ubo, bbo);
		load_map();
	}
	public void exec_quickzoom(int ZOOMDIR) {
		double zoompercent = 0.50;
		switch(ZOOMDIR) {
			case Zoom.ZOOMIN:
				Dimension start, stop;
				start  = new Dimension((int)(m_dimension.width / 2), (int)(m_dimension.height / 2));
				stop = new Dimension((int)((m_dimension.width / 2) + ((m_dimension.width / 2 * zoompercent))), (int)((m_dimension.height / 2) + ((m_dimension.height / 2 * zoompercent))));
				//m_pas.add_event(uleft.width + " " + uleft.height + " / " + bright.width + " / " + bright.height);
				exec_zoom_in(start, stop);
				break;
			case Zoom.ZOOMOUT:
				exec_zoom_out(new Dimension(m_dimension.width / 2, m_dimension.height / 2));
				break;
		}
	}

	public void exec_pan_direction(int DIRECTION) {
		Dimension dim_pan;
		int x = (m_dimension.width / 2), y = (m_dimension.height / 2);
		int b_x = 0, b_y = 0; //0=no change, 1=add, -1=neg
		switch(DIRECTION) {
			case Pan.EAST:
				b_x = 1;
				break;
			case Pan.WEST:
				b_x = -1;
				break;
			case Pan.NORTH:
				b_y = -1;
				break;
			case Pan.SOUTH:
				b_y = 1;
				break;
			case Pan.NORTHEAST:
				b_x = 1;
				b_y = -1;
				break;
			case Pan.NORTHWEST:
				b_x = -1;
				b_y = -1;
				break;
			case Pan.SOUTHEAST:
				b_x = 1;
				b_y = 1;
				break;
			case Pan.SOUTHWEST:
				b_x = -1;
				b_y = 1;
				break;
		}
		if(b_x == 1)
			x += (m_dimension.width / 2);
		else if(b_x == -1)
			x -= (m_dimension.width / 2);
		if(b_y == 1)
			y += (m_dimension.height / 2);
		else if(b_y == -1)
			y -= (m_dimension.height / 2);
		dim_pan = new Dimension(x, y);
		exec_pan(dim_pan);
	}
	
	public void exec_pan(Dimension dim_start)
	{
		double f_centerpoint_x, f_centerpoint_y;
		f_centerpoint_x = calc_centerpoint_x(dim_start.width);
		f_centerpoint_y = calc_centerpoint_y(dim_start.height);
		double f_delta_x, f_delta_y;
		f_delta_x = m_f_rbo.doubleValue() - m_f_lbo.doubleValue();
		f_delta_y = m_f_ubo.doubleValue() - m_f_bbo.doubleValue();
		double lbo, rbo, ubo, bbo;
		lbo = f_centerpoint_x - f_delta_x / 2;
		rbo = f_centerpoint_x + f_delta_x / 2;
		ubo = f_centerpoint_y + f_delta_y / 2;
		bbo = f_centerpoint_y - f_delta_y / 2;
		setNavigation(lbo, rbo, ubo, bbo);
		load_map();
	}
	public void exec_adrsearch(double f_lon, double f_lat, double f_zoom)
	{
		double lbo, rbo, ubo, bbo;
		if(f_lon==0.0 || f_lat==0.0)
			return;
		
		double f_dy = f_zoom / 3600 / 30.92;
		ubo = f_lat + f_dy;
		bbo = f_lat - f_dy;		
		
		//calc meters to degrees E/W
		double y1r = bbo * Math.PI * 2 / 360;
		double f_dx = f_zoom / 3600 / 30.92 / Math.cos(y1r);
		lbo = f_lon - f_dx / 2;
		rbo = f_lon + f_dx / 2;
		setNavigation(lbo, rbo, ubo, bbo);
		if(PAS.get_pas().get_mainmenu().get_selectmenu().get_view_searchpinpoint())
			PAS.get_pas().get_mappane().set_pinpoint(new MapPointLL(f_lon, f_lat));
		load_map();
		
	}
	
	public synchronized long calc_distance(MapPoint p1, MapPoint p2) {
		return calc_distance(p1.get_mappointpix(), p2.get_mappointpix());
	}
	/**
	 * 
	 * @param p point specifying lon/lat, based on origo 0,0
	 * @return distance in pixels based on origo 0,0 to p
	 */
	public synchronized int calc_pix_distance(MapPointLL p)
	{
		float x = (float)(p.get_lon() / get_widthprpix());
		float y = (float)(p.get_lat() / get_heightprpix());
		return (int)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	public synchronized long calc_distance(MapPointPix p1, MapPointPix p2) {
		if(p1==null || p2==null)
			return 0;		
		return calc_distance(p1.get_x(), p1.get_y(), p2.get_x(), p2.get_y());
	}
	public synchronized long calc_distance(int px1, int py1, int px2, int py2) {
		return Math.round(Math.sqrt( Math.pow((Math.abs(px2 - px1) * get_mapwidthmeters().doubleValue() / m_dimension.getWidth()), 2) + Math.pow((Math.abs(py2 - py1) * get_mapheightmeters().doubleValue() / m_dimension.getHeight()), 2)));
	}
	public synchronized long calc_pix_distance(int px1, int py1, int px2, int py2) {
		return Math.round(Math.sqrt( Math.pow(Math.abs(px2 - px1), 2) + Math.pow(Math.abs(py2 - py1), 2) ) );
	}
	
	public synchronized Dimension coor_to_screen(double f_lon, double f_lat, boolean b_mustbevisible)
	{
		/*create screen coors. if coor is out of bounds, return null*/
		
		if(b_mustbevisible)
		{
			if( f_lon < m_f_lbo.doubleValue() || f_lon > m_f_rbo.doubleValue() || f_lat < m_f_bbo.doubleValue() || f_lat > m_f_ubo.doubleValue() )
				return null;
		}
		
		Dimension dim_screen = new Dimension();
		//dim_screen.width = (int)((f_lat - m_f_lbo.doubleValue()) / m_f_widthprpix.doubleValue());
		//dim_screen.height= (int)(m_dimension.height - ((f_lon - m_f_bbo.doubleValue()) / m_f_heightprpix.doubleValue()));
		dim_screen.width = (int)((f_lon - m_f_lbo.doubleValue()) / m_f_widthprpix.doubleValue());
		dim_screen.height= (int)((m_dimension.height) - ((f_lat - m_f_bbo.doubleValue()) / m_f_heightprpix.doubleValue()));
		return dim_screen;
	}
	public synchronized MapPointLL screen_to_coor(int n_x, int n_y) {
		double f_lon, f_lat;
		f_lon =  getHeaderLBO().doubleValue() + n_x * get_widthprpix().doubleValue();
		f_lat =  getHeaderUBO().doubleValue() - n_y * get_heightprpix().doubleValue();
		MapPointLL ll = new MapPointLL(f_lon, f_lat);
		return ll;
	}
	
	public synchronized MapPointLL pix_to_ll(int pix_x, int pix_y) {
		return new MapPointLL(pix_x * get_widthprpix().doubleValue(), pix_y * get_heightprpix().doubleValue());
	}
	
	public double calc_centerpoint_x(int x) { return m_f_lbo.doubleValue() + x * m_f_widthprpix.doubleValue(); }
	public double calc_centerpoint_y(int y) { return m_f_bbo.doubleValue() + (m_dimension.height - y) * m_f_heightprpix.doubleValue(); }
	
	/** <b>ONLY to be called from load_map</b>*/
	public synchronized void setHeaderBounds(float lbo, float rbo, float ubo, float bbo)
	{
		m_f_lbo = (double)lbo;
		m_f_rbo = (double)rbo;
		m_f_ubo = (double)ubo;
		m_f_bbo = (double)bbo;
		calc();
		m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_download_houses"));
	}
	/** <b>ONLY to be called from load_map</b>*/
	public synchronized void setHeaderBounds(double lbo, double rbo, double ubo, double bbo) {
		setHeaderBounds((float)lbo, (float)rbo, (float)ubo, (float)bbo);
	}
	
	/** <b>ONLY to be called from load_map</b>*/
	public synchronized void setHeaderBounds(String lBo, String rBo, String uBo, String bBo)
	{
		
		Double f_l = new Double(lBo);
		Double f_r = new Double(rBo);
		Double f_u = new Double(uBo);
		Double f_b = new Double(bBo);
		
		m_f_lbo = f_l;
		m_f_rbo = f_r;
		m_f_ubo = f_u;
		m_f_bbo = f_b;
		calc();
		m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_download_houses"));
		//m_pas.add_event("set_neednewcoors(true);");
		//m_pas.download_houses();
		/*m_pas.get_housecontroller().start_download(true);
		m_pas.get_drawthread().set_neednewcoors(true);*/
	}

	void calc()
	{
		calc_prpix();
		calc_mapmeters();
	}	
	public void calc_prpix()
	{
		m_f_widthprpix = new Double((m_f_rbo.doubleValue() - m_f_lbo.doubleValue()) / m_dimension.width);
		m_f_heightprpix= new Double((m_f_ubo.doubleValue() - m_f_bbo.doubleValue()) / m_dimension.height);
	}
	public void calc_mapmeters()
	{
		//double y1r = m_f_bbo.doubleValue() * Math.PI * 2 / 360;
		double y1r = 0;
		if(m_f_bbo>0 && m_f_ubo>0)
			y1r = m_f_bbo.doubleValue() * Math.PI * 2 / 360;
		else if(m_f_bbo<0 && m_f_ubo<0)
			y1r = m_f_ubo.doubleValue() * Math.PI * 2 / 360;
		m_f_mapwidthmeters = new Double((m_f_rbo.doubleValue() - m_f_lbo.doubleValue()) * 3600 * 30.92 * Math.cos(y1r));
		//System.out.println(m_f_mapwidthmeters);
		m_f_mapheightmeters= new Double((m_f_ubo.doubleValue() - m_f_bbo.doubleValue()) * 3600 * 30.92);
		//m_pas.get_mapproperties().set_zoom(m_f_mapwidthmeters.intValue());
		set_zoom();
	}
	public static synchronized MapPointF distance_xy_M(double lbo, double rbo, double ubo, double bbo)
	{
		MapPointF r = new MapPointF();
		r.x = (rbo - lbo) * 3600.0 * 30.92 * Math.cos(bbo * Math.PI / 180.0);
		r.y = (ubo - bbo) * 3600.0 * 30.92;
		return r;
	}
	protected static synchronized NavStruct extend_to_aspect(double lbo, double rbo, double ubo, double bbo, Dimension d, EARTH_DIRECTION extend_dir, double wanted_aspect)
	{
		NavStruct ret = new NavStruct();
		ret._bbo = bbo;
		ret._lbo = lbo;
		ret._rbo = rbo;
		ret._ubo = ubo;
		if(extend_dir==EARTH_DIRECTION.LONGITUDE)
		{
			double newdist = wanted_aspect * d.width;
			double newdistll = newdist / 3600.0 / 30.92 / Math.cos(bbo * Math.PI / 180.0);
			ret._lbo = (lbo+rbo) / 2.0 - newdistll / 2;
			ret._rbo = (lbo+rbo) / 2.0 + newdistll / 2;
		}
		else if(extend_dir==EARTH_DIRECTION.LATITUDE)
		{
			double newdist = wanted_aspect * d.height;
			double newdistll = newdist / 3600.0 / 30.92;
			ret._ubo = (ubo+bbo) / 2.0 + newdistll/2;
			ret._bbo = (ubo+bbo) / 2.0 - newdistll/2;
		}
		return ret;
	}
	public static synchronized NavStruct preserve_aspect(double lbo, double rbo, double ubo, double bbo, java.awt.Dimension d)
	{
		NavStruct r = new NavStruct();
		MapPointF distance = distance_xy_M(lbo, rbo, ubo, bbo);
		MapPointF aspect = new MapPointF(distance.x / d.width, distance.y / d.height);
		System.out.println("Aspect: " + aspect);
		if(aspect.x > aspect.y)
		{
			/*
			 * e.g. query
			 * distance.x = 1000m, distance.y = 500m
			 * dimension.x = 200px, dimension.y = 200px
			 * aspect.x = 1000/200 = 5.0, aspect.y = 500/200 = 2.5
			 * extend latitude to fulfill aspect of 5.0 
			 */
			r = extend_to_aspect(lbo, rbo, ubo, bbo, d, EARTH_DIRECTION.LATITUDE, aspect.x);
		}
		else if(aspect.x < aspect.y)
			r = extend_to_aspect(lbo, rbo, ubo, bbo, d, EARTH_DIRECTION.LONGITUDE, aspect.y);
		else
		{
			r._bbo = bbo;
			r._lbo = lbo;
			r._rbo = rbo;
			r._ubo = ubo;
		}
		return r;
	}
	private void set_zoom() {
		m_callback.actionPerformed(new ActionEvent(m_f_mapwidthmeters, ActionEvent.ACTION_PERFORMED, "act_setzoom"));
	}
	
	public double getNavLBO() { return m_f_nav_lbo; }
	public double getNavRBO() { return m_f_nav_rbo; }
	public double getNavUBO() { return m_f_nav_ubo; }
	public double getNavBBO() { return m_f_nav_bbo; }
	public Double getHeaderLBO() { return m_f_lbo; }
	public Double getHeaderRBO() { return m_f_rbo; }
	public Double getHeaderUBO() { return m_f_ubo; }
	public Double getHeaderBBO() { return m_f_bbo; }
	public Dimension getDimension() { return m_dimension; }
	public Double get_widthprpix() { return m_f_widthprpix; }
	public Double get_heightprpix() { return m_f_heightprpix; }
	public Double get_mapwidthmeters() { return m_f_mapwidthmeters; }
	public Double get_mapheightmeters() { return m_f_mapheightmeters; }
	public double getDeltaLon() { return m_f_nav_rbo - m_f_nav_lbo; }
	public double getDeltaLat() { return m_f_nav_ubo - m_f_nav_bbo; }
}