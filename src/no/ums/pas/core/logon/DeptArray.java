package no.ums.pas.core.logon;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.util.*;

import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.ShapeStruct.DETAILMODE;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.pas.*;

public class DeptArray extends ArrayList<Object> {
	public static final long serialVersionUID = 1;
	public DeptInfo add(int n_deptpk, String sz_deptid, String sz_stdcc, NavStruct nav_init, boolean b_default_dept, 
						int n_deptpri, int n_maxalloc, String sz_defaultnumber, UserProfile m_userprofile,
						List<UMunicipalDef> municipals, int l_pas, ArrayOfUShape restriction_shapes) {
		DeptInfo dept = new DeptInfo(n_deptpk, sz_deptid, sz_stdcc, nav_init, 
								b_default_dept, n_deptpri, n_maxalloc, 
								sz_defaultnumber, m_userprofile, municipals, l_pas, restriction_shapes);
		super.add(dept);
		return dept;
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
		/*Polygon shape1 = new Polygon();
		Polygon shape2 = new Polygon();
		shape1.addPoint(4, 58);
		shape1.addPoint(4, 59);
		shape1.addPoint(6, 59);
		shape1.addPoint(6, 58);
		shape2.addPoint(4, 58);
		shape2.addPoint(5, 60);
		shape2.addPoint(7, 60);
		shape2.addPoint(6, 58);
		
		Area area1 = new Area(shape1);
		Area area2 = new Area(shape2);*/
		
		/*if(size()==1)
		{
			m_combined_shapestruct_list.add((PolygonStruct)((DeptInfo)this.get(0)).get_restriction_shapes().get(0));
			return;
		}*/

		
		int int_mod = 10000;
		
		Area combined = new Area();
		int n_total_points = 0;
		
		for(int j=0; j < this.size(); j++)
		{
			PolygonStruct poly = (PolygonStruct)((DeptInfo)this.get(j)).get_restriction_shapes().get(0);
			if(poly.isHidden() && size()>1)
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
			System.out.println(poly.get_size() + " points in polygon");
			//System.out.println("C = " + c);
			/*if(j==1)
				javapoly.translate(-10, -10);
			else if(j==2)
				javapoly.translate(-10, 10);
			*/
			Area area = new Area(javapoly);
			combined.add(area);
			boolean b_polygonal = combined.isPolygonal();
			//System.out.println("Is polygonal:" + b_polygonal);
		}
		System.out.println(n_total_points + " points in " + size() + " shapes");

		//temp
		//test ellipse
		/*Ellipse2D.Double ell = new Ellipse2D.Double();
		ell.x = 6.22478;
		ell.y = 58.84978;
		ell.width = 0.8;
		ell.height = 0.2;
		PolygonStruct poly_ellipse = new PolygonStruct(null);
		Utils.ConvertEllipseToPolygon(ell.x, ell.y, ell.x+ell.width, ell.y+ell.height, 40, 360, poly_ellipse);

		Polygon javapoly = new Polygon();
		int c;
		for(c = 0; c < poly_ellipse.get_size(); c++)
		{
			int x = (int)(poly_ellipse.get_coor_lon(c % poly_ellipse.get_size())*int_mod);
			int y = (int)(poly_ellipse.get_coor_lat(c % poly_ellipse.get_size())*int_mod);
			javapoly.addPoint(x, y);
		}
		Area ellipse_area = new Area(javapoly);
		combined.intersect(ellipse_area);*/
		//temp
		
		
		//combined.add(area1);
		//combined.add(area2);
		
		
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
					//System.out.println("Lineto");
					//combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod);
				}
				else if(pi==PathIterator.SEG_MOVETO)
				{
					//combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod);
				}
				if(pi==PathIterator.SEG_CLOSE)
				{
					System.out.println("SEG_CLOSE ("+n_total_points+")");
					
					//combined_shapestruct.add_coor(f_prev_moveto_x, f_prev_moveto_y);
				}
				else if(pi==PathIterator.SEG_MOVETO)
				{
					f_prev_moveto_x = coors[0]/int_mod;
					f_prev_moveto_y = coors[1]/int_mod;
					System.out.println("SEG_MOVETO ("+n_total_points+")");
					combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod, true);
				}
				else
					combined_shapestruct.add_coor(coors[0]/int_mod, coors[1]/int_mod, true);
								n_total_points++;
				point_count++;
				//System.out.println("Lon " + coors[0]/int_mod + " Lat " + coors[1]/int_mod);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				break;
				
			}
		
			it.next();
		}
		System.out.println("Combined Pointcount = " + n_total_points);
		combined_shapestruct.setCurrentViewMode(DETAILMODE.SHOW_POLYGON_FULL, 0, null);

		m_combined_shapestruct_list.add(combined_shapestruct);
	}
	
	/**
	 * Get all restriction shapes and combine those who are sharing borders
	 */
	/*public void CreateCombinedRestrictionShape(PolygonStruct combined_shapestruct, ShapeStruct next, int start_at_point, POINT_DIRECTION direction, int combo_id)
	{
		if(combined_shapestruct==null)
		{
			combined_shapestruct = new PolygonStruct(null, Color.black, new Color(0,0,0,0));
			m_combined_shapestruct_list.add(combined_shapestruct);
		}
		if(combo_id<=0)
			combo_id = 1;
		if(start_at_point<0)
			start_at_point = 0;
		//start at polygon 0
		//loop points in polygon 0
		//add every point to the combined polygon
		//check every point if it's overlapping
		//if yes, jump into next polygon at the polypoint number
		
		//int combo_id = 1;
		//for(int i=0; i < this.size(); i++)
		{
			
			PolygonStruct source_polygon;
			if(next==null)
			{
				List<ShapeStruct> l = ((DeptInfo)this.get(0)).get_restriction_shapes();
				source_polygon = (PolygonStruct)l.get(0);
			}
			else
				source_polygon = (PolygonStruct)next;
			
			if(source_polygon.combination_id<=0) //only combine polygons that are not already combined
			{
				source_polygon.combination_id = combo_id;
				PolygonStruct next_poly = null;
				boolean b_found_valid_next = false;
				for(int j=0; j < this.size(); j++)
				{
					next_poly = (PolygonStruct)((DeptInfo)this.get(j)).get_restriction_shapes().get(0);
					if(next_poly==source_polygon) //don't compare a polygon to itself
						continue;
					if(next_poly.combination_id<0 || next_poly.combination_id==source_polygon.combination_id)
					{
						b_found_valid_next = true;
						break;
					}
				}
				//start parsing from the first polypoint
				//for(int source_points = 0; source_points < source_polygon.get_size(); source_points++)
				boolean b_cont = true;
				int source_points = 0;
				if(direction==POINT_DIRECTION.DOWN)
					source_points = source_polygon.get_size()-1;
				while(b_cont)
				{
					if(direction==POINT_DIRECTION.UP)
					{
						if(source_points >= source_polygon.get_size())
							break;
					}
					else
					{
						if(source_points<0)
							break;
					}
					//firstly, add the point
					MapPointLL source_point = new MapPointLL(source_polygon.get_coor_lon(((source_points + start_at_point) % source_polygon.get_size())), source_polygon.get_coor_lat(((source_points + start_at_point) % source_polygon.get_size())));
					//if(next_poly!=null && b_found_valid_next) //only check point-to-point if there are more polygons to be tested
					{
						for(int dest_points = 0; dest_points < next_poly.get_size(); dest_points++)
						{
							MapPointLL dest_point = new MapPointLL(next_poly.get_coor_lon(dest_points), next_poly.get_coor_lat(dest_points));
							if(PointIsOverlapping(source_point, dest_point, 50))
							{
								//source_polygon.setPointIsAdded(source_points, true);
								//next_poly.setPointIsAdded(dest_points, true);
								//check next point, if we're going UP or DOWN on the next polygon
								
								
								CreateCombinedRestrictionShape(combined_shapestruct, next_poly, dest_points+1, POINT_DIRECTION.UP, combo_id);
							}
						}
					}
					boolean b_added = source_polygon.getIsAdded(source_points);// || next_poly.getIsAdded(dest_points);
					//if(!b_added)
					{
						combined_shapestruct.add_coor(source_point.get_lon(), source_point.get_lat());
						source_polygon.setPointIsAdded(source_points, true);						
					}
					if(direction==POINT_DIRECTION.UP)
						source_points ++;
					else
						source_points --;

					
				}
			}
		}
	}*/
	
	/**
	 * Check if two points are close enough (epsilon meters) to be overlapping 
	 */
	protected boolean PointIsOverlapping(MapPointLL p1, MapPointLL p2, double epsilon)
	{
		double dist = Math.sqrt( Math.pow((Math.abs(p1.get_lat() - p2.get_lat()) * 3600 * 30.92),2) + Math.pow((Math.abs(p1.get_lon() - p2.get_lon()) * 3600 * 30.92 * Math.cos(p2.get_lat()) ),2) );
		
		return (dist < epsilon ? true : false);
	}
	
}