package no.ums.pas.maps.defines;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

public class UnknownShape extends ShapeStruct {
	public UnknownShape() {
		
	}
	public PolySnapStruct snap_to_point(Point p, int i, boolean b, Dimension d, Navigation n) {
		return null;
	}
	public void draw(Graphics g, Navigation n, boolean b1, boolean b2, boolean b3, Point p) {
		draw_epicentre(g);
	}
	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int nPenSize, boolean bPaintShapeName) {
		
	}

	public Object clone() { return null; }
	
	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) { return false; }
	
	public void calc_coortopix(Navigation n) {
		calc_epicentre_coortopix(n);
	}
	public NavStruct calc_bounds() {
		return null;
	}
	@Override
	public NavStruct getFullBBox()
	{
		return m_bounds;
	}
	@Override
	public boolean pointInsideShape(MapPointLL ll) {
		return true;
	}
	@Override
	protected void calc_area_sqm() {
		
	}

}