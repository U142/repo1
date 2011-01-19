package no.ums.pas.maps.defines;

import java.awt.*;
import java.util.List;


public class PointStruct extends ShapeStruct
{
	public PointStruct()
	{
		super();
	}

	@Override
	protected void calc_area_sqm() {
		
	}

	@Override
	public NavStruct calc_bounds() {
		return null;
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
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int pensize, boolean bPaintShapeName) {
		
	}

	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p) {		
	}

	@Override
	public NavStruct getFullBBox() {
		return null;
	}

	@Override
	public boolean pointInsideShape(MapPointLL ll) {
		return false;
	}

	@Override
	public PolySnapStruct snap_to_point(Point p1, int nMaxDistance,
			boolean bCurrent, Dimension dimMap, Navigation nav) {
		return null;
	}
	
}