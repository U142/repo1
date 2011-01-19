package no.ums.pas.maps.defines;

import no.ums.pas.ums.tools.TextFormat;

import java.util.ArrayList;


public class CommonFunc {
	public synchronized static NavStruct calc_bounds(Object [] obj) {
		ShapeStruct [] shapes = new ShapeStruct[obj.length];
		for(int i=0; i < shapes.length; i++) {
			shapes[i] = (ShapeStruct)obj[i];
		}
		return calc_bounds(shapes);
	}
	public synchronized static NavStruct navPadding(NavStruct n, float wanted_padding)
	{
		n._lbo -= wanted_padding;
		n._rbo += wanted_padding;
		n._ubo += wanted_padding;
		n._bbo -= wanted_padding;
		return n;
	}
	public synchronized static NavStruct calc_bounds(ShapeStruct [] shapes) {
		if(shapes.length <= 0)
			return null;
		NavStruct nav[] = new NavStruct[shapes.length];
		for(int i=0; i < shapes.length; i++) {
			if(shapes[i]!=null)
			{
				if(shapes[i].calc_bounds() != null)
					nav[i] = shapes[i].calc_bounds();
			}
		}
		return calc_bounds_from_navstructs(nav);
	}
	private static ArrayList<Double> create_coorlist_lon(NavStruct [] n) {
		ArrayList<Double> list = new ArrayList<Double>();
		for(int i=0; i < n.length; i++) {
			if(n[i] != null) {
				list.add(new Double(n[i]._lbo));
				list.add(new Double(n[i]._rbo));
			}
		}
		return list;
	}
	private static ArrayList<Double> create_coorlist_lat(NavStruct [] n) {
		ArrayList<Double> list = new ArrayList<Double>();
		for(int i=0; i < n.length; i++) {
			if(n[i] != null) {
				list.add(new Double(n[i]._ubo));
				list.add(new Double(n[i]._bbo));
			}
		}
		return list;
	}
	public static NavStruct calc_bounds_from_navstructs(NavStruct [] n) {
		NavStruct nav = null;
		ArrayList<Double> coor_lon = create_coorlist_lon(n);
		ArrayList<Double> coor_lat = create_coorlist_lat(n);
		double lbo = 9999, rbo = -9999, ubo = -9999, bbo = 9999;
		if(coor_lon.size() == 0)
			return null;
		for(int i=0; i < coor_lon.size(); i++) {
			double lon, lat;
			lon = ((Double)coor_lon.get(i)).doubleValue();
			lat = ((Double)coor_lat.get(i)).doubleValue();
			if(lon >= rbo)
				rbo = lon;
			if(lon <= lbo)
				lbo = lon;
			if(lat >= ubo)
				ubo = lat;
			if(lat <= bbo)
				bbo = lat;
		}
		nav = new NavStruct(lbo, rbo, ubo, bbo);
		return nav;
	}
	public static String convert_decimal_to_dms(double degrees) {
		Double d_deg, d_min, d_sectemp;
		Integer d_sec, d_msec;
		String sz_ret;
		d_deg = new Double((int)degrees);
		d_min = new Double(Math.abs(Math.floor(new Double((degrees - d_deg.doubleValue()) * 60).doubleValue())));
		d_sectemp = new Double(Math.abs(((((degrees - d_deg.doubleValue()) - (d_min.doubleValue()/60)) * 60 * 60) * 10000) / 10000));
		d_sec = new Integer(Math.abs(new Double(((int)(d_sectemp.doubleValue()*10000.0))/10000.0).intValue()));
		d_msec = new Integer(Math.abs(new Double((d_sectemp.doubleValue() - d_sec.doubleValue())*100).intValue()));
		sz_ret = Math.abs(d_deg.intValue()) + "°  " + TextFormat.padding(d_min.toString(), '0', 2) + "'  " + TextFormat.padding(d_sec.toString(), '0', 2) + "." + TextFormat.padding(d_msec.toString(), '0', 2) + "''";
		return sz_ret;
	}
	
	public static final int DONT_INTERSECT = 0;
	public static final int COLLINEAR = 1;
	public static final int DO_INTERSECT = 2;
	
	public static int intersect(double x1, double y1, double x2, double y2, 
			double x3, double y3, double x4, double y4, 
								MapPointLL p)
	{	
		double a1, a2, b1, b2, c1, c2;   
		double r1, r2 , r3, r4;   
		double denom, offset, num;   
		  
		  // Compute a1, b1, c1, where line joining points 1 and 2   
		  // is "a1 x + b1 y + c1 = 0".   
		  a1 = y2 - y1;   
		  b1 = x1 - x2;   
		  c1 = (x2 * y1) - (x1 * y2);   
		  
		  // Compute r3 and r4.   
		  r3 = ((a1 * x3) + (b1 * y3) + c1);   
		  r4 = ((a1 * x4) + (b1 * y4) + c1);   
		  
		  if((x1==x3 && y1==y3) || (x2==x4 && y2==y4))
			  return DONT_INTERSECT;
		  if((x1==x4 && y1==y4) || (x2==x3 && y2==y3))
			  return DONT_INTERSECT;
		  
		  // Check signs of r3 and r4. If both point 3 and point 4 lie on   
		  // same side of line 1, the line segments do not intersect.   
		  if ((r3 != 0) && (r4 != 0) && same_sign(r3, r4)){   
		    return DONT_INTERSECT;   
		  }   
		  
		  // Compute a2, b2, c2   
		  a2 = y4 - y3;   
		  b2 = x3 - x4;   
		  c2 = (x4 * y3) - (x3 * y4);   
		  
		  // Compute r1 and r2   
		  r1 = (a2 * x1) + (b2 * y1) + c2;   
		  r2 = (a2 * x2) + (b2 * y2) + c2;   
		  
		  // Check signs of r1 and r2. If both point 1 and point 2 lie   
		  // on same side of second line segment, the line segments do   
		  // not intersect.   
		  if ((r1 != 0) && (r2 != 0) && (same_sign(r1, r2))){   
		    return DONT_INTERSECT;   
		  }   
		  
		  //Line segments intersect: compute intersection point.   
		  denom = (a1 * b2) - (a2 * b1);   
		  
		  if (denom == 0) {   
		    return COLLINEAR;   
		  }   
		  
		  if (denom < 0){    
		    offset = -denom / 2;    
		  }    
		  else {   
		    offset = denom / 2 ;   
		  }   
		  
		  // The denom/2 is to get rounding instead of truncating. It   
		  // is added or subtracted to the numerator, depending upon the   
		  // sign of the numerator.   
		  num = (b1 * c2) - (b2 * c1);   
		  if (num < 0){   
			  p.setLon((num ) / denom);   
		  }    
		  else {   
			  p.setLon((num ) / denom);   
		  }   
		  
		  num = (a2 * c1) - (a1 * c2);   
		  if (num < 0){   
			  p.setLat(( num ) / denom);   
		  }    
		  else {   
			  p.setLat((num ) / denom);   
		  }   
		  
		  // lines_intersect   
		  return DO_INTERSECT;   
		}   
		  
		  
		public static boolean same_sign(double a, double b){   
		  
		  return (( a * b) >= 0);   
		}  	
}

