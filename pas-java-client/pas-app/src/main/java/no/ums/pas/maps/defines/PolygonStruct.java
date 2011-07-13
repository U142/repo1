package no.ums.pas.maps.defines;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.ZoomLookup;
import no.ums.map.tiled.component.MapModel;
import no.ums.pas.core.Variables;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class PolygonStruct extends ShapeStruct {

    private static final Log log = UmsLog.getLogger(PolygonStruct.class);

    enum PolyType {
		NORMAL,
		ELLIPSE_PARENT,
		ELLIPSE_RECALCULATED,
	}
	
	public PolyType polytype = PolyType.NORMAL;
	public PolyType getPolyType() { return polytype; }
	private ArrayList<Double> m_coor_lon, m_coor_lat, m_coor_show_lon = null, m_coor_show_lat = null;
	private ArrayList<Integer> m_coor_pointref = null;
	private ArrayList<Double> m_ellipse_coor_lon = new ArrayList<Double>(), m_ellipse_coor_lat = new ArrayList<Double>();
	private ArrayList<Boolean> m_b_isadded = new ArrayList<Boolean>();
	private Hashtable<Integer, String> hash_coors_added = new Hashtable<Integer, String>(); 
	private int [] m_int_x;
	private int [] m_int_y;

    private Dimension m_dim_mapsize;
	public Dimension get_mapsize() { return m_dim_mapsize; }
	
	public PolygonStruct() {
		this(DETAILMODE.SHOW_POLYGON_FULL, 10000.0);
	}
	
	public PolygonStruct(DETAILMODE mode, double precision) {
		this(new Dimension());
		setDetailMode(mode);
		POINT_PRECISION = precision;
	}

	public PolygonStruct(Dimension dim_mapsize) {
		set_fill_color(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.2));
		m_border_color = new Color((float)0.0, (float)0.0, (float)0.0, (float)1.0);
		m_coor_lon = new ArrayList<Double>();
		m_coor_lat = new ArrayList<Double>();
		m_coor_pointref = new ArrayList<Integer>();
		m_dim_mapsize = dim_mapsize;
		shapeName = "POLY";
	}
	public PolygonStruct(Dimension dim_mapsize, Color fill_color, Color border_color) { 
		set_fill_color(fill_color);
		m_border_color = border_color;	
		m_coor_lon = new ArrayList<Double>();
		m_coor_lat = new ArrayList<Double>();		
		m_coor_pointref = new ArrayList<Integer>();
		m_dim_mapsize = dim_mapsize;
		shapeName = "POLY";
	}
	public PolygonStruct(Dimension dim_mapsize, PolygonStruct p) {
		set_fill_color(p.m_fill_color);
		m_border_color = p.m_border_color;
		m_coor_lon = p.m_coor_lon;
		m_coor_lat = p.m_coor_lat;
		m_coor_pointref = p.m_coor_pointref;
		m_b_isadded = p.m_b_isadded;
		m_dim_mapsize = dim_mapsize;
		shapeName = "POLY";
		
	}

    public MapPointLL getLastPoint() {
		if(m_coor_lon.size()>0) {
			return new MapPointLL(m_coor_lon.get(m_coor_lon.size()-1), m_coor_lat.get(m_coor_lat.size()-1), m_coor_pointref.get(m_coor_pointref.size()-1));
		}
		return null;
	}
	public MapPointLL getFirstPoint() {
		if(m_coor_lon.size()>0) {
			return new MapPointLL(m_coor_lon.get(0), m_coor_lat.get(0), m_coor_pointref.get(0));
		}
		return null;
	}

    @Override
	protected void calc_area_sqm() {
		double sqm = 0;
		m_f_area_sqm = 0;
		if(get_size()<3) {
			m_f_area_sqm = 0;
			return;
		}
		
		for(int i=0; i < get_size(); i++) {
			int next = ((i+1) % get_size());
			sqm += (get_coor_lon(i)*get_coor_lat(next) - get_coor_lon(next)*get_coor_lat(i));
		}
		sqm /= 2.0;
		m_f_area_sqm = sqm;

	}

    public double distanceBetweenPoints(MapPointLL p1, MapPointLL p2) {
        return Math.sqrt( Math.pow((Math.abs(p1.get_lat() - p2.get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(p1.get_lon() - p2.get_lon()) * 3600 * 30.92 * Math.cos(p2.get_lat()) ),2) );
	}
	
	public MapPointLL findNearestPolypoint(MapPointLL p1) {
		if(m_coor_lat.size()==0)
			return null;
		//check distance to all points and return the closest one
		MapPointLL ret = new MapPointLL(0,0);
		int closest_index = 0;
		double closest = 0;
		for(int i=0; i < m_coor_lon.size(); i++) {
			double dist = Math.sqrt( Math.pow((Math.abs(p1.get_lat() - m_coor_lat.get(i)) * 3600 * 30.92),2) + Math.pow((Math.abs(p1.get_lon() - m_coor_lon.get(i)) * 3600 * 30.92 * Math.cos(m_coor_lat.get(i)) ),2) );
			if(i==0) {
                closest = dist;
            }
			if(dist<closest) {
				closest = dist;
				closest_index = i;
			}
		}
		ret.setLat(m_coor_lat.get(closest_index));
		ret.setLon(m_coor_lon.get(closest_index));
		ret.setDegreeDistance(m_coor_lon.get(closest_index) - p1.get_lon(), m_coor_lat.get(closest_index) - p1.get_lat());
		ret.setPointReference(closest_index);
		ret.setMeasurementReference(closest);
		return ret;
	}

    public boolean FollowRestrictionLines(MapPointLL reference_point, MapPointLL p1, MapPointLL p2, PolygonStruct restrict,
			boolean add_first_point, boolean add_last_point, boolean b_force_follow_border) {
		int startat = p1.getPointReference();
		int stopat = p2.getPointReference();
		
		//test - measure distance from start to stop and choose the shortest one
		float dist1 = 0;
		float dist2 = 0;

		int xx = startat;
		while(1==1)
		{
			int n_1 = (xx % restrict.get_size());
			int n_2 = ((xx+1) % restrict.get_size());
			MapPointLL mp1 = new MapPointLL(restrict.get_coor_lon(n_1), restrict.get_coor_lat(n_1));
			MapPointLL mp2 = new MapPointLL(restrict.get_coor_lon(n_2), restrict.get_coor_lat(n_2));
			dist1 += distanceBetweenPoints(mp1, mp2);//Math.sqrt( Math.pow((Math.abs(mp1.get_lat() - mp2.get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(mp1.get_lon() - mp2.get_lon()) * 3600 * 30.92 * Math.cos(mp2.get_lat()) ),2) );
			int num_points = restrict.get_size();
			xx = ((xx+1) % num_points);
			if(xx==stopat)
				break;			
		}
		xx = stopat;
		while(1==1)
		{
			int n_1 = (xx % restrict.get_size());
			int n_2 = ((xx+1) % restrict.get_size());
			MapPointLL mp1 = new MapPointLL(restrict.get_coor_lon(n_1), restrict.get_coor_lat(n_1));
			MapPointLL mp2 = new MapPointLL(restrict.get_coor_lon(n_2), restrict.get_coor_lat(n_2));
			dist2 += distanceBetweenPoints(mp1, mp2);
			xx = Math.abs(((xx+1) % restrict.get_size()));
			if(xx==startat)
				break;	
		}
		int iterations;
		int dir;

		int _start = startat;
		int _stop = stopat;

		if(_start>_stop) {
			iterations = restrict.get_size() - _start + _stop;
		}
		else
			iterations = Math.abs(_stop - _start);
		
		int it = _start;
		int count = 0;
		int modulus = restrict.get_size()-1;
		if(b_force_follow_border) {
			//walk 1. way
			while(true)
			{
				int n_1 = (it % modulus);
				int n_2 = ((it+1) % modulus);
				if(n_1<0)
					n_1 = restrict.get_size()-1;
				if(n_2<0)
					n_2 = restrict.get_size()-1;
				MapPointLL mp1 = new MapPointLL(restrict.get_coor_lon(n_1), restrict.get_coor_lat(n_1));
				MapPointLL mp2 = new MapPointLL(restrict.get_coor_lon(n_2), restrict.get_coor_lat(n_2));
				dist1 += distanceBetweenPoints(mp1, mp2);
				it++;
				count++;
				if(count>=iterations)
					break;
			}
			
			if(_start>_stop) {
				iterations = Math.abs(_start - _stop);
			}
			else {
				iterations = restrict.get_size() - _stop + _start;
			}
			it = _start;
			count = 0;
			while(true)
			{
				int n_1 = (it % modulus);
				//int n_2 = ((it + (_stop<_start ? -1 : 1)) % restrict.get_size());
				int n_2 = ((it-1) % modulus);
				if(n_1<0)
					n_1 = restrict.get_size()+n_1;
				if(n_2<0)
					n_2 = restrict.get_size()+n_2;
				MapPointLL mp1 = new MapPointLL(restrict.get_coor_lon(n_1), restrict.get_coor_lat(n_1));
				MapPointLL mp2 = new MapPointLL(restrict.get_coor_lon(n_2), restrict.get_coor_lat(n_2));
				dist2 += distanceBetweenPoints(mp1, mp2);

				it--;
				count++;
				if(count>=iterations)
					break;
			}
			if(dist1>dist2) {
				dir = -1;
			}
			else {
				dir = 1;
			}
			log.debug("dist1 = " + dist1 + " dist2 = " + dist2);
		}
		else
		{
			double distp1 = distanceBetweenPoints(p1, reference_point);
			double distp2 = distanceBetweenPoints(p2, reference_point);
			if(distp1<distp2) {
				startat = p2.getPointReference();
				stopat  = p1.getPointReference();
			}
			if(dist1>dist2) {
				dir = -1;
			} else {
				dir = 1;
			}
		}
		_start = startat;
		_stop = stopat;

		if(_start>_stop && dir>0)
			iterations = restrict.get_size() - _start + _stop;
		else if(_start<_stop && dir<0)
			iterations = restrict.get_size() - _stop + _start;
		else
			iterations = Math.abs(_stop - _start);
		if(b_force_follow_border)
			iterations++;

		if(add_first_point)
		{
			this.add_coor(p1.get_lon(), p1.get_lat());
		}

		count = 0;
		it = _start;
		it += dir*(add_first_point && dir>0 ? 1 : 0);
		while(true)
		{
			int n_1 = (it % restrict.get_size());
			if(n_1<0)
				n_1 = restrict.get_size()+n_1;
			this.add_coor(restrict.get_coor_lon(n_1), restrict.get_coor_lat(n_1), true, POINT_PRECISION, false);			
			count++;
			it+=dir;
			if(count >= iterations)
				break;
		}
		if(add_last_point)
		{
			this.add_coor(p2.get_lon(), p2.get_lat(), true, POINT_PRECISION, true);
		}
		else
			this.finalizeShape();

		return true;
	}
	/**
	 * Check if line p1-p2 intersects with any poly-lines
	 */
	public List<MapPointLL> LineIntersect(MapPointLL p1, MapPointLL p2, boolean b_exclude_tangent_points)
	{
		return LineIntersect(p1, p2, 0, b_exclude_tangent_points);
	}
	
	public List<MapPointLL> LineIntersect(MapPointLL p1, MapPointLL p2, int startat, 
			boolean b_exclude_tangent_points)
	{
		return LineIntersect(p1, p2, startat, null, b_exclude_tangent_points);
	}
	
	/**
	 * Check if line p1-p2 intersects with any poly-lines
	 */
	public List<MapPointLL> LineIntersect(MapPointLL p1, MapPointLL p2, int startat, MapPointLL distance_reference, 
									boolean b_exclude_tangent_points)
	{
		List<MapPointLL> ret = new ArrayList<MapPointLL>();
		for(int i=startat; i < startat+m_coor_lat.size(); i++)
		{
			int real_idx = (i % (m_coor_lat.size()));
			int real_next_idx = ((i+1) % (m_coor_lat.size()));
			Double y1 = m_coor_lat.get(real_idx);
			Double x1 = m_coor_lon.get(real_idx);
			Double y2 = m_coor_lat.get(real_next_idx);
			Double x2 = m_coor_lon.get(real_next_idx);
			MapPointLL intersect = new MapPointLL(0, 0);
			int DOINTERSECT = CommonFunc.intersect(x1, y1, x2, y2, p1.get_lon(), p1.get_lat(), p2.get_lon(), p2.get_lat(), intersect);
			
			//check if the line starts or ends where the intersection is
			if(DOINTERSECT == CommonFunc.DO_INTERSECT && b_exclude_tangent_points)
			{
				double epsilon = 1/POINT_PRECISION;
				if( (Math.abs(intersect.get_lat()-p1.get_lat())<epsilon && Math.abs(intersect.get_lon()-p1.get_lon())<epsilon) || 
						(Math.abs(intersect.get_lat()-p2.get_lat())<epsilon && Math.abs(intersect.get_lon()-p2.get_lon())<epsilon) )
				{
					DOINTERSECT = CommonFunc.DONT_INTERSECT;
				}
			}
			switch(DOINTERSECT)
			{
			case CommonFunc.COLLINEAR:
				break;
			case CommonFunc.DO_INTERSECT:
				//check end points
					
				intersect.setPointReference(real_idx);
				if(intersect.getPointReference()!=p1.getPointReference())
				{
					if(distance_reference!=null)
					{
						intersect.setMeasurementReference(distanceBetweenPoints(intersect, distance_reference));
					}
					ret.add(intersect);
				}
				break;
			case CommonFunc.DONT_INTERSECT:
				break;
			}
		}
		return ret;
	}
	
	@Override
	public boolean pointInsideShape(MapPointLL p) {
		int modificator = 1;
        if (m_coor_lon == null || m_coor_lat == null)
            return false;
        if (get_size() < 3)
            return false;
        if(m_bounds==null)
        	calc_bounds();
        if(p==null)
        	return false;
        if(!m_bounds.pointInside(p))
        	return false;
        int counter = 0;
        double xinters;
        
        double p1x = m_coor_lon.get(0)*modificator;
        double p1y = m_coor_lat.get(0)*modificator;
        double p2x, p2y;
        //double useprecision = POINT_PRECISION * 10000;
        double useprecision = 1000000000d;
        for (int i = 1; i <= m_coor_lon.size(); i++)
        {
            p2x = m_coor_lon.get((i % m_coor_lon.size()))*modificator;
            p2y = m_coor_lat.get((i % m_coor_lat.size()))*modificator;
            if (p.get_lat()*modificator > Math.min(p1y, p2y))
            {
                if (p.get_lat()*modificator <= Math.max(p1y, p2y))
                {
                    if (p.get_lon()*modificator <= Math.max(p1x, p2x))
                    {
                    	//log.debug(Math.abs(p1y - p2y) + " prec = " + 1.0d/useprecision);
                    	
                        if (Math.abs(p1y - p2y) > 1.0d/useprecision)
                    	//if(p1y!=p2y)
                        {
                            xinters = (p.get_lat()*modificator - p1y) * (p2x - p1x) / (p2y - p1y) + p1x;
                            if (Math.abs(p1x - p2x) < 1.0d/useprecision || p.get_lon()*modificator <= xinters)
                            //if(p1x==p2x || p.get_lon()*modificator <= xinters)
                                counter++;
                        }
                    }
                }
            }
            p1x = p2x;
            p1y = p2y;
        }
        return counter % 2 != 0;
    }

	@Override
	public NavStruct getFullBBox()
	{
		return m_bounds;
	}
	public void add_coor(Double lon, Double lat) {
		this.add_coor(lon, lat, false, POINT_PRECISION, true);
	}
	
	public void add_coor(Double lon, Double lat, boolean b_allow_duplicates)
	{
		this.add_coor(lon, lat, b_allow_duplicates, POINT_PRECISION, true);
	}
	
	public void add_coor(Double lon, Double lat, boolean b_allow_duplicates, boolean auto_finalize)
	{
		add_coor(lon, lat, b_allow_duplicates, POINT_PRECISION, auto_finalize);
	}
	
	public void add_coor(Double lon, Double lat, double precision) {
		add_coor(lon, lat, false, precision, true);
	}
	
	public void add_coor(Double lon, Double lat, boolean b_allow_duplicates, double precision, boolean auto_finalize) {
		add_coor(lon, lat, -1, b_allow_duplicates, precision, auto_finalize);
	}

	public void add_coor(Double lon, Double lat, int pointref, boolean b_allow_duplicates, double precision, boolean auto_finalize) {
		if(!isEditable())
			return;
		int index = get_size();
		double dlon = ((int)(lon * precision))/precision;
		double dlat = ((int)(lat * precision))/precision;
		String id = dlon+"_"+dlat;
		if(!b_allow_duplicates && hash_coors_added.contains(id) && !isElliptical())
			return;
		//if(hash_coors_added.contains(id))
		//	log.debug("contains point");
		m_coor_lon.add(dlon);
		m_coor_lat.add(dlat);
		m_coor_pointref.add(pointref);
		m_b_isadded.add(false);
		hash_coors_added.put(index, id);
		if(auto_finalize)
			finalizeShape();
    }
	public void set_activepoint(PolySnapStruct at) {
		//at.get_polyindex() set this as last point
		ArrayList<Double> lat = new ArrayList<Double>(get_coors_lat().size());
		ArrayList<Double> lon = new ArrayList<Double>(get_coors_lon().size());
		int n_current_index = (at.get_polyindex() + 1);// % (get_coors_lon().size() - 1);

		for(int i=0; i < get_coors_lat().size(); i++) {
			//PAS.get_pas().add_event("set_activepoint at index " + at.get_polyindex() + " cur " + n_current_index);
			lat.add(get_coors_lat().get(n_current_index));
			lon.add(get_coors_lon().get(n_current_index));
			n_current_index = (n_current_index + 1) % (get_coors_lon().size());
		}
		m_coor_lat = lat;
		m_coor_lon = lon;
	}
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance, boolean b_current,
			Dimension dim_map, Navigation nav) {
		Point p2;
		PolySnapStruct snapat = null;
		long n_distance = 0;
		if(get_pix_int_x()==null || get_pix_int_y()==null)
			return null;
		//if(get_sendobject().get_toolbar().get_parent().isActive())
		for(int j=0; j < /*(b_current ? get_polygon().get_pix_int_x().length-1 :*/ get_pix_int_x().length; j++) {
			if(get_pix_int_x()[j]>=0 && get_pix_int_x()[j]<=dim_map.width &&
					get_pix_int_y()[j]>=0 && get_pix_int_y()[j]<=dim_map.height) {
				p2 = new Point(get_pix_int_x()[j], get_pix_int_y()[j]);
				n_distance = nav.calc_pix_distance(p1.x, p1.y, p2.x, p2.y);
				if(n_distance <= n_max_distance) {
					snapat = new PolySnapStruct(p2, b_current, j, this, (b_current && j==get_pix_int_x().length-1 ? true : false));
					return snapat; //prioritize active sending
				}
			}
		}
		return snapat;
	}

	public void reverse_coor_order() {
		ArrayList<Double> rev_lat = new ArrayList<Double>(get_coors_lat().size());
		ArrayList<Double>rev_lon = new ArrayList<Double>(get_coors_lon().size());
		ArrayList<Integer> rev_ref = new ArrayList<Integer>(get_coors_pointref().size());
		for(int i=get_coors_lat().size()-2; i >= 0; i--) {
			rev_lat.add(get_coors_lat().get(i));
			rev_lon.add(get_coors_lon().get(i));
			rev_ref.add(get_coors_pointref().get(i));
		}
		rev_lat.add(get_coors_lat().get(get_coors_lat().size()-1));
		rev_lon.add(get_coors_lon().get(get_coors_lon().size()-1));
		rev_ref.add(get_coors_pointref().get(get_coors_pointref().size()-1));
		m_coor_lat = rev_lat;
		m_coor_lon = rev_lon;
		m_coor_pointref = rev_ref;
	}
	public void remove_at(int n_index) {
		if(n_index>=0)
		{
			m_coor_lon.remove(n_index);
			m_coor_lat.remove(n_index);
			m_coor_pointref.remove(n_index);
			hash_coors_added.remove(n_index);
			if(m_b_isadded.size()>n_index && n_index>=0 && m_b_isadded.size()>0)
			{
				m_b_isadded.remove(n_index);
			}
			finalizeShape();
		}
	}
	public void move_at() {
		
	}
	public void set_at(int n_index, double lon, double lat) {
		m_coor_lon.set(n_index, lon);
		m_coor_lat.set(n_index, lat);
		hash_coors_added.put(n_index, lon+"_"+lat);
		finalizeShape();
	}
	public NavStruct calc_bounds() {
        double lbo = 9999, rbo = -9999, ubo = -9999, bbo = 9999;
		if(m_coor_lon.size() == 0)
			return null;
		double lon=0, lat=0;
		double next_lon = 0, next_lat = 0;
		double total_lon = 0;
		double total_lat = 0;
		for(int i=0; i < m_coor_lon.size(); i++) {
			/*if(lon == ((Double)arr_use_lon.get(i)).doubleValue() && lat == ((Double)arr_use_lat.get(i)).doubleValue())
				log.debug("break");*/
			lon = m_coor_lon.get(i);
			lat = m_coor_lat.get(i);
			if(lon >= rbo)
				rbo = lon;
			if(lon <= lbo)
				lbo = lon;
			if(lat >= ubo)
				ubo = lat;
			if(lat <= bbo)
				bbo = lat;
			
			next_lon = m_coor_lon.get(((i+1) % get_size()));
			next_lat = m_coor_lat.get(((i+1) % get_size()));
			total_lon += ((lon+next_lon)*(lon*next_lat-next_lon*lat));
			total_lat += ((lat+next_lat)*(lon*next_lat-next_lon*lat));
		}
		calc_area_sqm();
		total_lon *= 1/(6*m_f_area_sqm);
		total_lat *= 1/(6*m_f_area_sqm);
		NavStruct nav = new NavStruct(lbo, rbo, ubo, bbo);
		m_bounds = nav;
		m_center.setLon(total_lon);
		m_center.setLat(total_lat);
		return nav;
	}
	public void rem_last_coor() {
		remove_at(this.get_size()-1);
	}
	public void calc_coortopix(Navigation nav) {
		if(m_b_recalcing)
			return;
		m_b_recalcing = true;

		Dimension screen;
		try
		{
			m_int_x = new int[get_coors_lat().size()];
			m_int_y = new int[get_coors_lon().size()];
			for(int i=0; i < get_coors_lat().size(); i++)
			{
				screen = new Dimension(nav.coor_to_screen(get_coors_lon().get(i),
                        get_coors_lat().get(i),
										    false));
				m_int_x[i] = screen.width;
				m_int_y[i] = screen.height;
			}
		}
		catch(Exception e)
		{
			
		}
		try
		{
			calc_show_coortopix(nav);
			calc_epicentre_coortopix(nav);
			calc_bounds();
			screen = new Dimension(nav.coor_to_screen(m_center.get_lon(), m_center.get_lat(), false));
			m_center_pix.set(screen);
        }
		catch(Exception e)
		{
			
		}
		try
		{
			if(ellipse_polygon!=null)
				ellipse_polygon.calc_coortopix(nav);
			if(isElliptical())
			{
				if(m_p_center!=null)
					m_p_center.recalc_pix(nav);
				if(m_p_corner!=null)
					m_p_corner.recalc_pix(nav);
			}
		}
		catch(Exception e)
		{
			
		}
		calc_bounds();
		m_b_recalcing = false;

	}
	private int m_n_lod = 5;
	private int m_n_lod_meters = 5;
	public void set_lod(int n_level_of_detail) { 
		m_n_lod = n_level_of_detail;
		//calc_show_coortopix(nav);		
	}
	public void set_lod_meters(int n_meters) {
		m_n_lod_meters = n_meters;
	}
	
	public void setCurrentViewMode(DETAILMODE SHOW, int n_lod, Navigation nav) {
		switch(SHOW) 
		{
		case SHOW_POLYGON_SIMPLIFIED_PRMETERS:
			set_lod_meters(n_lod);
			break;
		case SHOW_POLYGON_SIMPLIFIED_PRPIXELS:
			set_lod(n_lod);
			break;
		}
		m_n_current_show_mode = SHOW;
		calc_coortopix(nav);
	}
	
	boolean m_b_recalcing = false;
	public void calc_show_coortopix(Navigation nav) {
		if(m_int_x.length <= 0) {
            return;
        }
		try {
			if(m_coor_show_lon!=null)
				m_coor_show_lon.clear();
			if(m_coor_show_lat!=null)
				m_coor_show_lat.clear();
	
			long n_distance;
			int n_lod = m_n_lod;
			int n_current_point = 0;
			ArrayList<Integer> indexbuffer = new ArrayList<Integer>();
			indexbuffer.add(n_current_point);
			int i=0;
			for(i=0; i < m_int_x.length-2; i++) {
				switch(this.m_n_current_show_mode) {
					case SHOW_POLYGON_FULL:
						n_current_point = i+1;
						indexbuffer.add(n_current_point);
						break;
					case SHOW_POLYGON_SIMPLIFIED_PRPIXELS:
						n_distance = nav.calc_pix_distance(m_int_x[n_current_point], m_int_y[n_current_point], m_int_x[i+1], m_int_y[i+1]);
						if(n_distance > n_lod) { //add next point (i+1)
							n_current_point = i+1;
							indexbuffer.add(n_current_point);
						} else if(m_int_x.length <= 50) {
							indexbuffer.add(n_current_point);
						}
						break;
					case SHOW_POLYGON_SIMPLIFIED_PRMETERS:
						n_distance = nav.calc_distance(m_int_x[n_current_point], m_int_y[n_current_point], m_int_x[i+1], m_int_y[i+1]);
						
						if(n_distance > this.m_n_lod_meters) {
							n_current_point = i+1;
							indexbuffer.add(n_current_point);
						} else if(m_int_x.length <= 50) {
							n_current_point = i+1;
							indexbuffer.add(n_current_point);
						}
						break;
				}
			}
			//force last point into indexbuffer
			if(m_int_x.length>1) {
				indexbuffer.add(i + 1);
			}
            int[] m_show_int_x = new int[indexbuffer.size()];
            int[] m_show_int_y = new int[indexbuffer.size()];
			m_coor_show_lon = new ArrayList<Double>(indexbuffer.size());
			m_coor_show_lat = new ArrayList<Double>(indexbuffer.size());
	
			for(i=0; i < indexbuffer.size(); i++) {
				m_show_int_x[i] = m_int_x[indexbuffer.get(i)];
				m_show_int_y[i] = m_int_y[indexbuffer.get(i)];
                m_coor_show_lon.add(this.get_coors_lon().get(indexbuffer.get(i)));
                m_coor_show_lat.add(this.get_coors_lat().get(indexbuffer.get(i)));
			}
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
		}
    }

    public ArrayList<Double> get_coors_lon() { return m_coor_lon; }
	public ArrayList<Double> get_coors_lat() { return m_coor_lat; }
	public ArrayList<Double> get_coors_show_lon() { return m_coor_show_lon; }
	public ArrayList<Double> get_coors_show_lat() { return m_coor_show_lat; }
	public ArrayList<Integer> get_coors_pointref() { return m_coor_pointref; } 
	public int [] get_pix_int_x() { return m_int_x; }
	public int [] get_pix_int_y() { return m_int_y; }

    public int get_size() { return get_coors_lat().size(); }
	public int get_ellipse_size() { return m_ellipse_coor_lat.size(); }
	public int get_show_size() { return get_coors_show_lon().size(); }//get_show_pix_int_x().length; }
	public double get_coor_lon(int idx) {
        return get_coors_lon().size() > idx ? get_coors_lon().get(idx) : 0.0;
	}
	public double get_coor_lat(int idx) {
        return get_coors_lat().size() > idx ? get_coors_lat().get(idx) : 0.0;
	}
	public double get_ellipse_coor_lon(int idx) {
		return m_ellipse_coor_lon.get(idx);
	}
	public double get_ellipse_coor_lat(int idx) {
		return m_ellipse_coor_lat.get(idx);
	}

    private LonLat getLonLat(int index) {
        return new LonLat(get_coor_lon(index), get_coor_lat(index));
    }

    private void drawEditLines(Graphics2D g, ZoomLookup zoomLookup, Point topLeft, Point editPoint) {
        if (get_size() > 0) {
            final LonLat pointLonLat = zoomLookup.getLonLat(topLeft.x + editPoint.x, topLeft.y + editPoint.y);
            final LonLat lastLonLat = getLonLat(get_size() - 1);
            final Point lastPoint = zoomLookup.getPoint(lastLonLat);
            final String distToEnd = String.format("%.2fm", pointLonLat.distanceToInM(lastLonLat));

            g.setColor(new Color(0.2f, 0.2f, 0.2f, 1.0f));
            g.drawLine(lastPoint.x-topLeft.x, lastPoint.y-topLeft.y, editPoint.x, editPoint.y);
            g.drawString(distToEnd, editPoint.x + 10, editPoint.y+10);
            if (get_size() > 1) {
                float [] Dashes = {5.0F, 20.0F, 5.0F, 20.0F};
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, Dashes, 0.F));
                final Point firstPoint = zoomLookup.getPoint(getLonLat(0));
                g.drawLine(firstPoint.x - topLeft.x, firstPoint.y - topLeft.y, editPoint.x, editPoint.y);
            }
        }
    }
	
	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
                     boolean bFill, int nPenSize, boolean bPaintShapeName,
                     boolean bHasFocus) {
		if(m_b_recalcing || isHidden()) {
            return;
        }
		if(bEditmode && ellipse_polygon!=null) {
            ellipse_polygon.draw(g, mapModel, zoomLookup, true, true, true, p, bBorder, false, 1, false);
		}
		
		Graphics2D g2d = (Graphics2D)g;
		if(get_size() >= 1) {
			try {

                final Path2D path = new Path2D.Double();
                final Point topLeft = zoomLookup.getPoint(mapModel.getTopLeft());
                Point lastPoint = null;
                for (int i=0; i<get_size(); i++) {
                    final Point lineTo = zoomLookup.getPoint(new LonLat(get_coor_lon(i), get_coor_lat(i)));
                    if (lastPoint == null) {
                        lastPoint = lineTo;
                        path.moveTo(lineTo.x - topLeft.x, lineTo.y - topLeft.y);
                    } else if (lineTo.x != lastPoint.x && lineTo.y != lastPoint.y) {
                        lastPoint = lineTo;
                        path.lineTo(lineTo.x - topLeft.x, lineTo.y - topLeft.y);
                    }
                }
                if(!bFinalized && Variables.getMapFrame().getMouseInsideCanvas() && !isElliptical()) {
                    drawEditLines(g2d, zoomLookup, topLeft, p);
                }
                if (g.getClipBounds().intersects(path.getBounds())) {
                    if (!bEditmode) {
                        path.closePath();
                    }

                    drawShape(g2d, path, nPenSize, bDashed, bFill, bBorder, bPaintShapeName, bHasFocus);
                }
			} catch(Exception e) {
				Error.getError().addError("PolyStruct","Exception in draw",e,1);
			}
		}

		super.draw_epicentre(g, zoomLookup, mapModel);
	}

	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
                     boolean bFill, int nPenSize, boolean bPaintShapeName) {
		draw(g, mapModel, zoomLookup, bDashed, bFinalized, bEditmode, p, bBorder, bFill, nPenSize, bPaintShapeName, false);
	}

	
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed, boolean b_finalized, boolean b_editmode, Point p) {
		draw(g, mapModel, zoomLookup, b_dashed, b_finalized, b_editmode, p, true, true, 1, false);
	}
	
	protected void updateCanLock(List<ShapeStruct> restrictionShapes)
	{
		if(getPolyType()==PolyType.ELLIPSE_PARENT)
		{
			if(m_p_center!=null && m_p_corner!=null && m_p_center.get_x()!=m_p_corner.get_x() && m_p_center.get_y()!=m_p_corner.get_y())
			{
				if(get_size()>3)
				{
					setCanLock(true);
					return;
				}
			}
		}
		else if(getPolyType()==PolyType.NORMAL)
		{
			if(get_size() >= 3) {
				if(restrictionShapes!=null && restrictionShapes.size()>0)
				{
					//also check if all points are inside poly and no polyline crosses restriction area
					PolygonStruct restriction = (PolygonStruct)restrictionShapes.get(0);
					MapPointLL p1 = getLastPoint();
					MapPointLL p2 = getFirstPoint();
					//List<MapPointLL> intersects;
					List<MapPointLL> intersects = restriction.LineIntersect(p1, p2, 0, true);
					if(intersects.size()>0)
					{
						setCanLock(false);
						return;
					}

					MapPointLL midpoint = new MapPointLL((p2.get_lon()+p1.get_lon())/2.0, (p2.get_lat()+p1.get_lat())/2.0);
					boolean b_midpoint_inside = restriction.pointInsideShape(midpoint);
					if(!b_midpoint_inside)
					{
						setCanLock(false);
						return;
					}

				}
				setCanLock(true);
				return;
			}
		}
		setCanLock(false);
	}
	
	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) {
		updateCanLock(restrictionShapes);
		return b_can_lock;
	}
	
	MapPoint m_p_center;
	MapPoint m_p_corner;
	
	public MapPoint getEllipseCenter()
	{
		return m_p_center;
	}
	public MapPoint getEllipseCorner()
	{
		return m_p_corner;
	}
	
	public void set_ellipse(Navigation nav, MapPoint p_center, MapPoint p_corner) {
		if(!isEditable())
			return;
		m_p_center = p_center;
		m_p_corner = p_corner;
		if(m_p_center==null || m_p_corner==null)
			return;
		recalc_shape(nav);
		finalizeShape();
		polytype = PolyType.ELLIPSE_PARENT;
	}
	public void set_ellipse_center(Navigation nav, MapPoint p_center) {
		if(!isEditable())
			return;
		//ellipse_polygon = null; //reset
		set_ellipse(nav, p_center, p_center);
		polytype = PolyType.ELLIPSE_PARENT;
		//finalize();
	}
	public void set_ellipse_corner(Navigation nav, MapPoint p_corner) {
		if(!isEditable())
			return;
		set_ellipse(nav, m_p_center, p_corner);
		polytype = PolyType.ELLIPSE_PARENT;
		//recalc_shape(nav);
		//finalize();
	}
	public boolean isElliptical()
	{
		return (ellipse_polygon!=null ? true : false);
	}
	PolygonStruct ellipse_polygon = null;
	public void recalc_shape(Navigation nav)
	{
		m_b_recalcing = true;
		m_ellipse_coor_lat.clear();
		m_ellipse_coor_lon.clear();
		
		
		if(ellipse_polygon==null)
		{
			ellipse_polygon = new PolygonStruct();
			ellipse_polygon.polytype = PolyType.ELLIPSE_PARENT;
		}
		Utils.ConvertEllipseToPolygon(
				m_p_center.get_lon(), 
				m_p_center.get_lat(), 
				m_p_corner.get_lon(), 
				m_p_corner.get_lat(), 
				50, 0, 
				POINT_PRECISION,
				ellipse_polygon);
		this.m_ellipse_coor_lat = ellipse_polygon.get_coors_lat();
		this.m_ellipse_coor_lon = ellipse_polygon.get_coors_lon();
		//log.debug("points="+m_ellipse_coor_lat.size());
		ellipse_polygon.set_border_color(this.get_border_color());
		ellipse_polygon.set_fill_color(this.get_fill_color());
        m_b_recalcing = false;
	}
	
	public enum POLY_FOLLOW_RESTRICT
	{
		INCLUDE_ENDPOINTS,
		INCLUDE_START,
		INCLUDE_END,
		INCLUDE_BETWEEN,
	};
	
	protected void followRestrictionLines(PolygonStruct poly, PolygonStruct restrict, 
			int start, int stop, int dir)
	{
		if(dir>=0)
			dir = 1;
		else 
			dir = -1;
		if(start==stop)
			return;
		int cur = start;
		while(1==1)
		{
			int idx = (cur % restrict.get_size());
			MapPointLL ll = new MapPointLL(restrict.get_coor_lon(idx), restrict.get_coor_lat(idx));
			poly.add_coor(ll.get_lon(), ll.get_lat(), true, POINT_PRECISION, false);
			if(idx==stop)
				break;
			cur+=dir;
			if(cur<=0)
				cur = restrict.get_size();
		}
		poly.finalizeShape();

	}
	
	protected void followRestrictionLines(PolygonStruct poly, PolygonStruct restrict, 
											int start, int stop)
	{
		if(start==stop)
			return;
		int cur = start;
		while(1==1)
		{
			int idx = (cur % restrict.get_size());
			MapPointLL ll = new MapPointLL(restrict.get_coor_lon(idx), restrict.get_coor_lat(idx));
			poly.add_coor(ll.get_lon(), ll.get_lat(), true, POINT_PRECISION, false);
			if(idx==stop)
				break;
			cur++;
		}
		poly.finalizeShape();
	}
	protected void followRestrictionLines(PolygonStruct poly, PolygonStruct restrict, POLY_FOLLOW_RESTRICT r,
											int start, int stop)
	{
		if(start==stop)
			return;
		int n_start = start;
		int n_end = stop;
		switch(r)
		{
		case INCLUDE_BETWEEN:
			n_start+=1;
			if(n_start!=n_end)
				n_end-=1;
			break;
		case INCLUDE_START:
			n_end-=1;
			break;
		case INCLUDE_END:
			n_start+=1;
			break;
		case INCLUDE_ENDPOINTS:
			break;
		}
		if(n_start>n_end)
			n_end+=restrict.get_size();
		for(int i=n_start; i <= n_end; i++)
		{
			int idx = (i % restrict.get_size());
			MapPointLL ll = new MapPointLL(restrict.get_coor_lon(idx), restrict.get_coor_lat(idx));
			poly.add_coor(ll.get_lon(), ll.get_lat(), true, POINT_PRECISION, false);
		}
		poly.finalizeShape();
	}
	
	int RestrictionStage = 0;
	protected void iterateEllipse(PolygonStruct newpoly,
								PolygonStruct restrict,
								int n_ell_point,
								MapPointLL current_point,
								boolean b_iteration_outside,
								boolean b_marked_as_finalized)
	{
		//if(RestrictionStage>5)
		//	return;
		boolean b_cur_inside = false;
		if(b_marked_as_finalized)
			return;
		int n_size = get_ellipse_size();
		if(n_size==0)
			return;
		if(n_ell_point>n_size)
			return;
		if(current_point==null)
		{
			current_point = new MapPointLL(get_ellipse_coor_lon(n_ell_point), get_ellipse_coor_lat(n_ell_point));
			current_point.setPointReference(-1);
			if(!restrict.pointInsideShape(current_point))
				b_iteration_outside = true;
		}
		if(current_point.getPointReference()>0)
			b_cur_inside = true;
		else
			b_cur_inside = restrict.pointInsideShape(current_point);
		
		MapPointLL last_added_point = newpoly.getLastPoint();
		MapPointLL first_added_point = newpoly.getFirstPoint();

		//if(newpoly.get_size()==45)
		//	log.debug("break");
		if(n_ell_point==n_size && !b_cur_inside && first_added_point!=null && last_added_point!=null)
		{
			//newpoly.followRestrictionLines(newpoly, restrict, last_added_point.getPointReference()+1, first_added_point.getPointReference());
			newpoly.followRestrictionLines(newpoly, restrict, last_added_point.getPointReference(), first_added_point.getPointReference(), first_added_point.getPointReference()-last_added_point.getPointReference());
			RestrictionStage++;
			b_marked_as_finalized = true;
			//newpoly.FollowRestrictionLines(first_added_point, last_added_point, first_added_point, restrict, false, false, true);
			return;
		}
		else if(n_ell_point==n_size && first_added_point==null)
		{
			//entire ellipse is tested, no points found. 
			//Either the ellipse is outside the restriction area
			//or ellipse is covering the entire restriction area (all points are inside)
			if(restrict.pointInsideShape(newpoly.getFirstPoint()))
				newpoly.followRestrictionLines(newpoly, restrict, 0, restrict.get_size()-1);
			RestrictionStage++;
			b_marked_as_finalized = true;
			return;
		}
		else if(n_ell_point==n_size && first_added_point!=null)
		{
			//newpoly.followRestrictionLines(newpoly, restrict, last_added_point.getPointReference(), first_added_point.getPointReference(), first_added_point.getPointReference()-last_added_point.getPointReference());
			RestrictionStage++;
			b_marked_as_finalized = true;
			return;	
		}
		
		
		int n_next_ell_point = ((n_ell_point+1) % n_size);
		MapPointLL next_point = new MapPointLL(get_ellipse_coor_lon(n_next_ell_point), get_ellipse_coor_lat(n_next_ell_point));
		int n_start_intersect = (current_point.getPointReference() > 0 ? current_point.getPointReference() : 0);
		List<MapPointLL> intersects = restrict.LineIntersect(current_point, next_point, n_start_intersect, false);
		for(int i=0; i < intersects.size(); i++)
		{
			MapPointLL ll = intersects.get(i);
			double dist = distanceBetweenPoints(current_point, ll);
			intersects.get(i).setMeasurementReference(dist);
		}
		Collections.sort(intersects);

		
		boolean b_restrict_used = false;
		//iterate
		if(last_added_point!=null && 
				last_added_point.getPointReference()>0 && 
				current_point.getPointReference()>0 &&
				!b_iteration_outside)// && intersects.size()==0)
		{
			newpoly.followRestrictionLines(newpoly, restrict, last_added_point.getPointReference(), current_point.getPointReference(), current_point.getPointReference()-last_added_point.getPointReference());
			RestrictionStage++;
			//newpoly.followRestrictionLines(newpoly, restrict, last_added_point.getPointReference(), current_point.getPointReference());
			newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), current_point.getPointReference(), true, POINT_PRECISION, false);
			//newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), current_point.getPointReference(), true, POINT_PRECISION, false);
			b_restrict_used = true;
		}

		//add the point
		if(b_cur_inside && !b_restrict_used)
		{
			newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), current_point.getPointReference(), true, POINT_PRECISION, false);
		}

		
		if(intersects.size()>0)
		{
			current_point = intersects.get(0);
			current_point.setPointReference(intersects.get(0).getPointReference());
			b_iteration_outside = !b_iteration_outside;
		}
		else
		{
			current_point = next_point;
			n_ell_point++;
		}
		
		iterateEllipse(newpoly, restrict, n_ell_point, current_point, b_iteration_outside, b_marked_as_finalized);
	}
	
	private void sortIntersectsByDistance()
	{
		
	}
	@Deprecated
	protected void iterateEllipse(PolygonStruct newpoly,
								PolygonStruct restrict,
								int n_ell_point,
								boolean b_is_inside,
								MapPointLL current_point,
								int n_entered_polygon_at_idx,
								int n_left_polygon_at_idx,
								int n_total_iterations,
								int n_first_entered_polygon_at_idx,
								int n_first_left_polygon_at_idx)
	{
		boolean b_back_inside = false;
		boolean b_point_is_a_intersection = false;
		int n_size = get_ellipse_size();
		if(n_size<=0)
			return;
		
		if(n_ell_point>=n_size)
		{
			//we're done iterating. If the result polygon is 0, check if ellipse covers the restriction polygon.
			//check if at least one point in the restriction polygon is inside the ellipse.
			MapPointLL ll = restrict.getFirstPoint();
			if(newpoly.get_size()==0 && ellipse_polygon.pointInsideShape(ll))
			{
				followRestrictionLines(newpoly, restrict, POLY_FOLLOW_RESTRICT.INCLUDE_ENDPOINTS, 0, restrict.get_size());
			}
			return;
		}
		boolean b_cur_inside = false;
		if(current_point==null)
		{
			current_point = new MapPointLL(get_ellipse_coor_lon(n_ell_point), get_ellipse_coor_lat(n_ell_point));
			current_point.setPointReference(-1);
			b_cur_inside = restrict.pointInsideShape(current_point);
		}
		else
		{
			b_point_is_a_intersection = true;
			b_cur_inside = true; //this is an intersection point, always defined as inside
		}
		if(n_total_iterations<=0)
		{
			n_total_iterations = 0;
			b_is_inside = b_cur_inside;
		}

		int n_next_ell_point = ((n_ell_point+1) % n_size);
		MapPointLL next_point = new MapPointLL(get_ellipse_coor_lon(n_next_ell_point), get_ellipse_coor_lat(n_next_ell_point));
		boolean b_next_inside = restrict.pointInsideShape(next_point);
		int n_start_intersect = 0;
		if(n_left_polygon_at_idx>=0)
			n_start_intersect = n_left_polygon_at_idx;
		else if(n_entered_polygon_at_idx>=0)
			n_start_intersect = n_entered_polygon_at_idx;
		List<MapPointLL> intersects = restrict.LineIntersect(current_point, next_point, n_start_intersect, false);
		//if(intersects.size()>0)
		//	log.debug("break");
		MapPointLL first_intersect = null;
		int num_intersects = intersects.size();
		//if(!b_point_is_a_intersection && num_intersects>0)

		if(num_intersects>0)
		{
			//check first intersect distance, if intersect point~=current point, don't use this intersect point
			double epsilon = 10.0;
			MapPointLL p1 = intersects.get(0);
			double dist = Math.sqrt( Math.pow((Math.abs(p1.get_lat() - current_point.get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(p1.get_lon() - current_point.get_lon()) * 3600 * 30.92 * Math.cos(current_point.get_lat()) ),2) );
			//log.debug("dist="+dist);
			if(dist<epsilon && num_intersects>=1)
			{
				first_intersect = intersects.get(0);
				b_point_is_a_intersection = true;
			}
			else if(dist<epsilon && num_intersects==0 && b_point_is_a_intersection)
			{
				b_point_is_a_intersection = true;				
			}
			else
			{
				first_intersect = intersects.get(0);
				b_point_is_a_intersection = false;
			}
		}
		

		if(b_point_is_a_intersection)
		{
			//if(num_intersects>0)
			//	num_intersects --;
		}
		
		if(n_total_iterations>0)
		{
			//if(!b_is_inside && intersects.size()>0)
			//if(b_cur_inside && n_left_polygon_at_idx>=0)
			if(n_left_polygon_at_idx>=0 && num_intersects>0)
				b_back_inside = true;
		}
		
		if(n_ell_point+1==n_size && !b_back_inside)
		{
			if(first_intersect!=null && first_intersect.getPointReference()>=0 && n_entered_polygon_at_idx>=0)
			{
				if(b_cur_inside)
					newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), true, POINT_PRECISION, false);
				if(num_intersects>0)
					newpoly.add_coor(first_intersect.get_lon(), first_intersect.get_lat(), true, POINT_PRECISION, false);
				
				followRestrictionLines(newpoly, restrict, POLY_FOLLOW_RESTRICT.INCLUDE_END, first_intersect.getPointReference(), n_entered_polygon_at_idx);
				return;
			}
			else if(n_first_entered_polygon_at_idx>=0 && n_left_polygon_at_idx>=0) //we end at the outside, make a line from last intersect to first intersect
			{
				if(b_cur_inside)
					newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), true, POINT_PRECISION, false);
				followRestrictionLines(newpoly, restrict, POLY_FOLLOW_RESTRICT.INCLUDE_END, 
						n_left_polygon_at_idx, n_first_entered_polygon_at_idx );
				return;
			}
		}


		
		if(b_back_inside && first_intersect!=null)
		{
			if(b_cur_inside)
				newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), true, POINT_PRECISION, false);
			if(n_left_polygon_at_idx!=first_intersect.getPointReference())
			{
				followRestrictionLines(newpoly, restrict, POLY_FOLLOW_RESTRICT.INCLUDE_END, n_left_polygon_at_idx, first_intersect.getPointReference());
				//n_left_polygon_at_idx = -1;
			}
			newpoly.add_coor(first_intersect.get_lon(), first_intersect.get_lat(), true, POINT_PRECISION, false);
		}
		else if(b_cur_inside && intersects.size()==0)
		{
			newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), true, POINT_PRECISION, false);
		}
		else if(!b_back_inside && num_intersects>0)
		{
			if(b_cur_inside)
				newpoly.add_coor(current_point.get_lon(), current_point.get_lat(), true, POINT_PRECISION, false);
			newpoly.add_coor(first_intersect.get_lon(), first_intersect.get_lat(), true, POINT_PRECISION, false);			
		}

		if(num_intersects==0)
		{
			n_ell_point ++;//n_next_ell_point;
			current_point = null; //use ellipse's points
			//b_is_inside = b_cur_inside;
		}
		else if(num_intersects>0 || (b_point_is_a_intersection && intersects.size()>1)) //we're going out or coming back inside
		{
			if(b_cur_inside && n_left_polygon_at_idx==-1)
			{
				n_left_polygon_at_idx = first_intersect.getPointReference();
				n_entered_polygon_at_idx = -1;
				if(n_first_left_polygon_at_idx<0)
					n_first_left_polygon_at_idx = n_left_polygon_at_idx;
			}
			else
			{
				n_entered_polygon_at_idx = first_intersect.getPointReference();
				n_left_polygon_at_idx = -1;
				if(n_first_entered_polygon_at_idx<0)
					n_first_entered_polygon_at_idx = n_entered_polygon_at_idx;
			}
			b_is_inside = !b_is_inside;//b_cur_inside;
			//b_is_inside = true;
			current_point = first_intersect;
		}
		iterateEllipse(newpoly, restrict, n_ell_point, b_is_inside, 
					current_point, n_entered_polygon_at_idx, 
					n_left_polygon_at_idx, ++n_total_iterations,
					n_first_entered_polygon_at_idx,
					n_first_left_polygon_at_idx);
	}
	@Deprecated
	protected void iterateEllipse(PolygonStruct newpoly, 
								PolygonStruct restrict, 
								int n_ell_point, 
								boolean b_is_currently_inside,
								MapPointLL prev_intersect_out,
								MapPointLL prev_intersect_in)
	{
		int n_size = get_ellipse_size();
		if(n_size<=0)
			return;
		if(n_ell_point>=n_size)
			return;
		MapPointLL p1 = new MapPointLL(get_ellipse_coor_lon(n_ell_point), get_ellipse_coor_lat(n_ell_point));
		//if(prev_intersect!=null)
		//	p1 = prev_intersect;
		boolean b_cur_inside = restrict.pointInsideShape(p1);
		int n_next_ell_point = ((n_ell_point+1) % n_size);
		MapPointLL p2 = new MapPointLL(get_ellipse_coor_lon(n_next_ell_point), get_ellipse_coor_lat(n_next_ell_point));
		boolean b_next_inside = restrict.pointInsideShape(p2);
		List<MapPointLL> intersects = restrict.LineIntersect(p1, p2, false);
		
		//if both are inside, check if there are any line intersections (going outside temporarily)
		if(b_cur_inside && b_next_inside && intersects.size()==0)
		{
			//add point and continue
			newpoly.add_coor(p1.get_lon(), p1.get_lat());
			iterateEllipse(newpoly, restrict, ++n_ell_point, b_next_inside, prev_intersect_out, prev_intersect_in);
		}
		else if(b_cur_inside && b_next_inside && intersects.size()>0) //should be pairs of intersection
		{

			for(int i=0; i < intersects.size()-1; i+=2)
			{
				newpoly.add_coor(intersects.get(i).get_lon(), intersects.get(i).get_lat());
				if(intersects.get(i).getPointReference()!=intersects.get(i+1).getPointReference())
				{
					newpoly.FollowRestrictionLines(intersects.get(i+1), 
							intersects.get(i), intersects.get(i+1), 
													restrict, false, false, false);
				}
			}
			for(int i=0; i < intersects.size(); i++)
			{
				newpoly.add_coor(intersects.get(i).get_lon(), intersects.get(i).get_lat());
			}
			
			iterateEllipse(newpoly, restrict, ++n_ell_point, b_next_inside, prev_intersect_out, prev_intersect_in);
		}
		else if(!b_cur_inside && b_next_inside) //we're coming back in
		{
			for(int i=0; i < intersects.size(); i++)
			{
				prev_intersect_in = intersects.get(i);
				if(prev_intersect_out!=null && intersects.get(i).getPointReference()!=prev_intersect_out.getPointReference())
				{
					int next_ref = prev_intersect_out.getPointReference()+1;//intersects.get(0).getPointReference()+1;
					MapPointLL ll_temp = new MapPointLL(restrict.get_coor_lon(next_ref), restrict.get_coor_lat(next_ref));
					ll_temp.setPointReference(next_ref);
					newpoly.FollowRestrictionLines(ll_temp, 
							prev_intersect_in, ll_temp, 
													restrict, false, false, false);
				}
				newpoly.add_coor(intersects.get(i).get_lon(), intersects.get(i).get_lat());

			}
			newpoly.add_coor(p2.get_lon(), p2.get_lat());
			iterateEllipse(newpoly, restrict, ++n_ell_point, b_next_inside, prev_intersect_out, prev_intersect_in);
		}
		else if(b_cur_inside && !b_next_inside) //we're going out
		{
			//newpoly.FollowRestrictionLines(p2, p1, p2, restrict, false, false, true);
			newpoly.add_coor(p1.get_lon(), p1.get_lat());
			if(intersects.size()==1)
			{
				newpoly.add_coor(intersects.get(0).get_lon(), intersects.get(0).get_lat());
				prev_intersect_out = intersects.get(0);
			}
			else if(intersects.size()>1)
			{
				//first intersection leads out, next leads back in
				boolean b_in = true;
				for(int i=0; i < intersects.size() ; i++)
				{
					newpoly.add_coor(intersects.get(i).get_lon(), intersects.get(i).get_lat());
					if(b_in && i+1<intersects.size()-1)
					{
						newpoly.FollowRestrictionLines(intersects.get(i+1), 
								intersects.get(i), intersects.get(i+1), 
														restrict, false, false, false);
					}
					else if(i+1<intersects.size()-1)
					{
						newpoly.add_coor(intersects.get(i+1).get_lon(), intersects.get(i+1).get_lat());
					}
					if(b_in) b_in = false; else b_in = true;
				}
			}
			if(n_ell_point+1==n_size)
			{
				if(prev_intersect_out!=null && intersects.get(0).getPointReference()!=prev_intersect_out.getPointReference())
				{
					newpoly.FollowRestrictionLines(intersects.get(0), 
							prev_intersect_out, intersects.get(0), 
													restrict, false, false, false);
				}
			}
			iterateEllipse(newpoly, restrict, ++n_ell_point, b_next_inside, prev_intersect_out, prev_intersect_in);			
		}
		else if(!b_cur_inside && !b_next_inside && intersects.size()>0)
		{
			for(int i=0; i < intersects.size(); i++)
			{
				newpoly.add_coor(intersects.get(i).get_lon(), intersects.get(i).get_lat());
			}
			prev_intersect_out = intersects.get(intersects.size()-1);
			iterateEllipse(newpoly, restrict, ++n_ell_point, b_next_inside, prev_intersect_out, prev_intersect_in);

		}
		else if(!b_cur_inside && !b_next_inside)
		{
			//newpoly.FollowRestrictionLines(p2, p1, p2, restrict, false, false, true);
			iterateEllipse(newpoly, restrict, ++n_ell_point, b_next_inside, prev_intersect_out, prev_intersect_in);
		}
				
	}
	
	public void ellipseToRestrictionlines(PolygonStruct restrict)
	{
		PolygonStruct newpoly = new PolygonStruct();
		newpoly.polytype = PolyType.ELLIPSE_RECALCULATED;
		//iterateEllipse(newpoly, restrict, 0, false, null, -1, -1, -1,-1,-1);
		RestrictionStage = 0;
		iterateEllipse(newpoly, restrict, 0, null, false, false);
		newpoly.finalizeShape();
		this.m_coor_lat = newpoly.m_coor_lat;
		this.m_coor_lon = newpoly.m_coor_lon;
		this.m_coor_pointref = newpoly.m_coor_pointref;
        calc_coortopix(Variables.getNavigation());
	}
	
	
 
	@Override
	public void set_fill_color(Color col) {
		super.set_fill_color(col);
		if(ellipse_polygon!=null)
			ellipse_polygon.set_fill_color(col);
	}

	public Object clone() throws CloneNotSupportedException {
		PolygonStruct c;
		try {
			c = new PolygonStruct(get_mapsize());//(PolygonStruct)super.clone();
			c.m_coor_lat = (ArrayList<Double>)m_coor_lat.clone(); //new ArrayList();
			c.m_coor_lon = (ArrayList<Double>)m_coor_lon.clone(); //new ArrayList();
			c.m_coor_pointref = (ArrayList<Integer>)m_coor_pointref.clone();
			c.m_int_x = m_int_x;
			c.m_int_y = m_int_y;
			c.set_fill_color(new Color(m_fill_color.getRed(), m_fill_color.getGreen(), m_fill_color.getBlue(), m_fill_color.getAlpha()));
			c.m_border_color = new Color(m_border_color.getRed(), m_border_color.getGreen(), m_border_color.getBlue(), m_border_color.getAlpha());
			c.hash_coors_added = hash_coors_added;
		} catch(Exception e) {
			Error.getError().addError("PolyStruct","Exception in clone",e,1);
			throw new CloneNotSupportedException("Could not clone Navigation class");
		}
		return c;
	}
}