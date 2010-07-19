package no.ums.pas.core.logon;


import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.maps.defines.*;
import no.ums.ws.pas.*;

/*struct class containing user-info*/
public class DeptInfo extends Object {
	private int m_n_deptpk;
	private String m_sz_deptid;
	private String m_sz_stdcc;
	NavStruct m_nav_init;
	boolean m_b_default_dept;
	private int m_n_deptpri;
	private int m_n_maxalloc;
	private String m_sz_defaultnumber;
	private int m_l_pas; //0=no access, 1=norway db, 2=folkereg db
	
	private UserProfile m_userprofile;
	private List<UMunicipalDef> m_municipals;
	
	public int get_deptpk() { return m_n_deptpk; }
	public String get_deptid() { return m_sz_deptid; }
	public String get_stdcc() { return m_sz_stdcc; }
	public NavStruct get_nav_init() { return m_nav_init; }
	public boolean isDefaultDept() { return m_b_default_dept; }
	public int get_deptpri() { return m_n_deptpri; }
	public int get_maxalloc() { return m_n_maxalloc; }
	public String get_defaultnumber() { return m_sz_defaultnumber; }
	public UserProfile get_userprofile() { return m_userprofile; }
	public String toString() { return m_sz_deptid + " (" + m_userprofile.get_name() + ")"; }
	public List<UMunicipalDef> get_municipals() { return m_municipals; }
	public int get_pas_rights() { return m_l_pas; } //1 = Normal DB, 2 = Folkereg DB, 4 = TAS
	protected List<ShapeStruct> m_restriction_shapestructs = new ArrayList<ShapeStruct>();
	public List<ShapeStruct> get_restriction_shapes() { return m_restriction_shapestructs; }
	
	public void drawRestrictionShapes(Graphics g, Navigation n)
	{
		for(int i=0; i < get_restriction_shapes().size(); i++)
		{
			try
			{
				//if(n.bboxOverlap(get_restriction_shapes().get(i).getFullBBox()))
				get_restriction_shapes().get(i).draw(g, n, false, true, false, null, true, false, 1, true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void CalcCoorRestrictionShapes()
	{
		for(int i=0; i < get_restriction_shapes().size(); i++)
		{
			try
			{
				get_restriction_shapes().get(i).calc_coortopix(variables.NAVIGATION);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public DeptInfo(int n_deptpk, String sz_deptid, String sz_stdcc, NavStruct nav_init, boolean b_default_dept,
			 int n_deptpri, int n_maxalloc, String sz_defaultnumber, UserProfile userprofile,
			 List<UMunicipalDef> municipals, int l_pas, ArrayOfUShape restriction_shapes) {
		m_n_deptpk 	= n_deptpk;
		m_sz_deptid = sz_deptid;
		m_nav_init = nav_init;
		m_sz_stdcc = sz_stdcc;
		m_b_default_dept = b_default_dept;
		m_n_deptpri = n_deptpri;		
		m_n_maxalloc = n_maxalloc;
		m_userprofile = userprofile;
		m_sz_defaultnumber = sz_defaultnumber;
		m_municipals = municipals;
		m_l_pas = l_pas;
		System.out.println("Department=" + m_sz_deptid);
		m_restriction_shapestructs = ConvertUShapes_to_ShapeStructs(restriction_shapes.getUShape());
	}
	
	protected List<ShapeStruct> ConvertUShapes_to_ShapeStructs(List<UShape> s)
	{
		List<ShapeStruct> ret = new ArrayList<ShapeStruct>();
		for(int i=0; i < s.size(); i++)
		{
			UShape ushape = s.get(i);
			ShapeStruct shapestruct = ConvertUShape_to_ShapeStruct(ushape);
			shapestruct.shapeName = m_sz_deptid;
			ret.add(shapestruct);
			if(shapestruct.getClass().equals(PolygonStruct.class))
				shapestruct.typecast_polygon().setCurrentViewMode(PolygonStruct.SHOW_POLYGON_FULL, 0, null);
			shapestruct.set_fill_color(Color.black);

		}
		return ret;
	}
	protected ShapeStruct ConvertUShape_to_ShapeStruct(UShape ushape)
	{
		if(ushape.getClass().equals(UPolygon.class))
		{
			UPolygon upolygon = (UPolygon)ushape;
			PolygonStruct polygonstruct = new PolygonStruct(null, 
					new Color((int)upolygon.getColRed(), 
							(int)upolygon.getColGreen(), 
							(int)upolygon.getColBlue(), 
							(int)upolygon.getColAlpha()), 
							new Color(0,0,0));
			List<UPolypoint> list = upolygon.getMArrayPolypoints().getUPolypoint();
			for(int i=0; i < list.size(); i++)
			{
				polygonstruct.add_coor(list.get(i).getLon(), list.get(i).getLat());
			}
			return polygonstruct;
		}
		else if(ushape.getClass().equals(UEllipse.class))
		{
			UEllipse ell = (UEllipse)ushape;
			EllipseStruct ellipsestruct = new EllipseStruct(
					new Color((int)ell.getColRed(), 
							(int)ell.getColGreen(), 
							(int)ell.getColBlue(), 
							(int)ell.getColAlpha()), 
							new Color(0,0,0));
			ellipsestruct.set_ellipse_center(
					variables.NAVIGATION, 
					new MapPoint(variables.NAVIGATION, new MapPointLL(ell.getLon(), ell.getLat())));
			ellipsestruct.set_ellipse_corner(
					variables.NAVIGATION,
					new MapPoint(variables.NAVIGATION, new MapPointLL(ell.getX(), ell.getY())));
			return ellipsestruct;
					
		}
		else if(ushape.getClass().equals(UBoundingRect.class))
		{
			UBoundingRect upolygon = (UBoundingRect)ushape;
			PolygonStruct polygonstruct = new PolygonStruct(null, 
					new Color((int)upolygon.getColRed(), 
							(int)upolygon.getColGreen(), 
							(int)upolygon.getColBlue(), 
							(int)upolygon.getColAlpha()), 
							new Color(0,0,0));
			polygonstruct.add_coor(upolygon.getLeft(), upolygon.getTop());
			polygonstruct.add_coor(upolygon.getRight(), upolygon.getTop());
			polygonstruct.add_coor(upolygon.getRight(), upolygon.getBottom());
			polygonstruct.add_coor(upolygon.getLeft(), upolygon.getBottom());
			
			//Hide if the restriction area is the whole world
			if(upolygon.getLeft()==-180 && upolygon.getRight()==180 &&
					upolygon.getBottom()==-90 && upolygon.getTop()==90)
				polygonstruct.setHidden(true);
			return polygonstruct;
		}
		return null;
	}
}