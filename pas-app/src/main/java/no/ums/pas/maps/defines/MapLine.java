package no.ums.pas.maps.defines;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MapLine extends ShapeStruct
{
	@Override
	public NavStruct calc_bounds() {
		return null;
	}
	@Override
	public NavStruct getFullBBox()
	{
		return m_bounds;
	}

	@Override
	protected void calc_area_sqm() {
		
	}
	@Override
	public boolean pointInsideShape(MapPointLL ll) {
		return true;
	}
	@Override
	public void calc_coortopix(Navigation n) {
		
	}
	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) {
		return false;
	}
	@Override
	protected void updateCanLock(List<ShapeStruct> restrictionShapes) {
		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return null;
	}
	@Override
	public void draw(Graphics g, Navigation nav, boolean b_dashed,
			boolean b_finalized, boolean b_editmode, Point p) {
		
	}
	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int nPenSize, boolean bPaintShapeName) {
		
	}

	@Override
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance,
			boolean b_current, Dimension dim_map, Navigation nav) {
		return null;
	}
	protected List<MapPointF> pts = new ArrayList<MapPointF>();
	public int numPoints;
	

	public MapLine()
	{
		
	}
	public void addPoint(MapPointF p)
	{
		pts.add(p);
	}
}