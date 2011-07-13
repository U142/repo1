package no.ums.pas.maps.defines;

import no.ums.map.tiled.ZoomLookup;
import no.ums.map.tiled.component.MapModel;
import no.ums.ws.common.ULBACOUNTRY;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TasStruct extends ShapeStruct
{
	protected List<ULBACOUNTRY> countries = new ArrayList<ULBACOUNTRY>(0);
	public List<ULBACOUNTRY> getCountryList() { return countries; }
	public void AddCountry(ULBACOUNTRY m, boolean b_add) {
		ULBACOUNTRY temp = null;
		for(int i=0; i < countries.size() && temp==null; i++)
		{
			if(countries.get(i)!=null)
			{
				if(countries.get(i).equals(m))
					temp = countries.get(i);
			}
		}
		if(b_add)
		{
			if(temp==null)
				countries.add(m);
		}
		else
		{
			countries.remove(m);
		}
	}
	public void ClearMunicipalList() { countries.clear(); }
	public TasStruct()
	{
		super();
	}

	@Override
	protected void calc_area_sqm() {
		
	}
	@Override
	public boolean pointInsideShape(MapPointLL ll) {
		return true;
	}
	@Override
	public NavStruct getFullBBox()
	{
		return m_bounds;
	}

	@Override
	public NavStruct calc_bounds() {
		return m_bounds; 
	}

	@Override
	public void calc_coortopix(Navigation n) {
		
	}

	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) {
		if(countries.size()>0)
			return true;
		return false;
	}
	@Override
	protected void updateCanLock(List<ShapeStruct> restrictionShapes) {
		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		TasStruct ret = new TasStruct();
		ret.m_border_color = this.m_border_color;
		ret.countries = this.countries;
		ret.m_fill_color = this.m_fill_color;
		ret.m_tex_paint = this.m_tex_paint;
		return ret;
	}

	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed,
                     boolean b_finalized, boolean b_editmode, Point p) {
		
	}
	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
                     boolean bFill, int nPenSize, boolean bPaintShapeName) {
		
	}


	@Override
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance,
			boolean b_current, Dimension dim_map, Navigation nav) {
		return null;
	}
	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed,
                     boolean b_finalized, boolean b_editmode, Point p, boolean b_border,
                     boolean b_fill, int pensize, boolean bPaintShapeName,
                     boolean bHasFocus) {
		// TODO Auto-generated method stub
		
	}
}