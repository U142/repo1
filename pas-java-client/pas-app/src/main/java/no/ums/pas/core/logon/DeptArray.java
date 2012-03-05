package no.ums.pas.core.logon;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.ShapeStruct.DETAILMODE;
import no.ums.ws.common.UMunicipalDef;
import no.ums.ws.common.parm.ArrayOfUShape;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class DeptArray extends ArrayList<DeptInfo> {

    private static final Log log = UmsLog.getLogger(DeptArray.class);

	public static final long serialVersionUID = 1;
	public DeptInfo add(int n_deptpk, String sz_deptid, String sz_stdcc, NavStruct nav_init, boolean b_default_dept, 
						int n_deptpri, int n_maxalloc, String sz_defaultnumber, UserProfile m_userprofile,
						List<UMunicipalDef> municipals, int l_pas, int l_langpk, ArrayOfUShape restriction_shapes) {
		DeptInfo dept = new DeptInfo(n_deptpk, sz_deptid, sz_stdcc, nav_init, 
								b_default_dept, n_deptpri, n_maxalloc, 
								sz_defaultnumber, m_userprofile, municipals, l_pas, l_langpk, restriction_shapes);
		super.add(dept);
		return dept;
	}
	
	public DeptInfo getDepartment(int deptpk)
	{
		for(DeptInfo di : this)
		{
			if(di.get_deptpk()==deptpk)
				return di;
		}
		return null;
	}
	
	public List<ShapeStruct> m_combined_shapestruct_list = new ArrayList<ShapeStruct>();;
	public List<ShapeStruct> get_combined_restriction_shape() { return m_combined_shapestruct_list; }
	public void ClearCombinedRestrictionShapelist() {
		for(int i=0; i < this.size(); i++)
		{
			((DeptInfo)this.get(i)).get_restriction_shapes().get(0).combination_id = -1;
		}
		m_combined_shapestruct_list.clear(); 
	}
	
	public enum POINT_DIRECTION
	{
		UP,
		DOWN,
	}
	
	/**
	 * Get all restriction shapes and combine those who are sharing borders
	 */

	public void CreateCombinedRestrictionShape()
	{
		int int_mod = 10000;
		
		Area combined = new Area();
		int n_total_points = 0;
		
		for(int j=0; j < this.size(); j++)
		{
			PolygonStruct poly = (PolygonStruct)((DeptInfo)this.get(j)).get_restriction_shapes().get(0);
            
			if(poly.isHidden() && size()>1 || poly.isObsolete())
				continue;
			Polygon javapoly = new Polygon();
			int c;
			for(c = 0; c < poly.get_size(); c++)
			{
				int x = (int)(poly.get_coor_lon(c % poly.get_size())*int_mod);
				int y = (int)(poly.get_coor_lat(c % poly.get_size())*int_mod);
				javapoly.addPoint(x, y);
				n_total_points++;
			}
			log.debug(poly.get_size() + " points in polygon");
			//log.debug("C = " + c);
			/*if(j==1)
				javapoly.translate(-10, -10);
			else if(j==2)
				javapoly.translate(-10, 10);
			*/
			Area area = new Area(javapoly);
			combined.add(area);
			boolean b_polygonal = combined.isPolygonal();
			//log.debug("Is polygonal:" + b_polygonal);
		}
		log.debug(n_total_points + " points in " + size() + " shapes");

		
		PathIterator it = combined.getPathIterator(new AffineTransform());
		PolygonStruct combined_shapestruct = new PolygonStruct(null, Color.black, new Color(0,0,0,0));
		int point_count = 0;
		n_total_points = 0;
		while(it!=null && !it.isDone())
		{
			double [] coors = new double[2];
			double f_prev_moveto_x = 0;
			double f_prev_moveto_y = 0;
			try
			{
				int pi = it.currentSegment(coors);
				if(pi==PathIterator.SEG_CLOSE)
				{
					//it.next();
					//continue;
				}
				
				//	break;
				if(pi==PathIterator.SEG_LINETO)
				{
					//log.debug("Lineto");
					//combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod);
				}
				else if(pi==PathIterator.SEG_MOVETO)
				{
					//combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod);
				}
				if(pi==PathIterator.SEG_CLOSE)
				{
					log.debug("SEG_CLOSE ("+n_total_points+")");
					
					//combined_shapestruct.add_coor(f_prev_moveto_x, f_prev_moveto_y);
				}
				else if(pi==PathIterator.SEG_MOVETO)
				{
					f_prev_moveto_x = coors[0]/int_mod;
					f_prev_moveto_y = coors[1]/int_mod;
					log.debug("SEG_MOVETO ("+n_total_points+")");
					combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod, true, false);
				}
				else
					combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod, true, false);
								n_total_points++;
				point_count++;
				//log.debug("Lon " + coors[0]/int_mod + " Lat " + coors[1]/int_mod);
			}
			catch(Exception e)
			{
				log.warn(e.getMessage(), e);
				break;
				
			}
		
			it.next();
		}
		log.debug("Combined Pointcount = " + n_total_points);
		combined_shapestruct.finalizeShape();		
		combined_shapestruct.setCurrentViewMode(DETAILMODE.SHOW_POLYGON_FULL, 0, null);

		m_combined_shapestruct_list.add(combined_shapestruct);
	}
	
	/**
	 * Check if two points are close enough (epsilon meters) to be overlapping 
	 */
	protected boolean PointIsOverlapping(MapPointLL p1, MapPointLL p2, double epsilon)
	{
		double dist = Math.sqrt( Math.pow((Math.abs(p1.get_lat() - p2.get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(p1.get_lon() - p2.get_lon()) * 3600 * 30.92 * Math.cos(p2.get_lat()) ),2) );
		
		return (dist < epsilon ? true : false);
	}
	
}