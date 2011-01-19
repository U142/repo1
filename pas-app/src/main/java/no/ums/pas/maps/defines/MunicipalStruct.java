package no.ums.pas.maps.defines;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MunicipalStruct extends ShapeStruct
{
	protected List<Municipal> municipals = new ArrayList<Municipal>(0);
	public List<Municipal> getMunicipals() { return municipals; }
	public void AddMunicipal(Municipal m, boolean b_add) {
		Municipal temp = null;
		for(int i=0; i < municipals.size() && temp==null; i++)
		{
			if(municipals.get(i)!=null)
			{
				if(municipals.get(i).equals(m))
					temp = municipals.get(i);
			}
		}
		if(b_add)
		{
			if(temp==null)
				municipals.add(m);
		}
		else
		{
			municipals.remove(m);
		}
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

	public void ClearMunicipalList() { municipals.clear(); }
	public MunicipalStruct()
	{
		super();
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
		if(municipals.size()>0)
			return true;
		return false;
	}
	@Override
	protected void updateCanLock(List<ShapeStruct> restrictionShapes) {
		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		MunicipalStruct ret = new MunicipalStruct();
		ret.m_border_color = this.m_border_color;
		ret.municipals = this.municipals;
		ret.m_fill_color = this.m_fill_color;
		ret.m_tex_paint = this.m_tex_paint;
		return ret;
	}

	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int nPenSize, boolean bPaintShapeName) {
		
	}

	@Override
	public void draw(Graphics g, Navigation nav, boolean b_dashed,
			boolean b_finalized, boolean b_editmode, Point p) {
		
	}

	@Override
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance,
			boolean b_current, Dimension dim_map, Navigation nav) {
		return null;
	}
}