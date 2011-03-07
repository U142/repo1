package no.ums.pas.maps.defines;

import no.ums.pas.importer.gis.GISList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;


public class GISShape extends ShapeStruct {
	GISList m_gislist;
	public GISList get_gislist() { return m_gislist; }
	protected NavStruct m_fullbounds = null;
	
	public GISShape(GISList list) {
		super();
		m_gislist = list;
		this.set_fill_color(new Color(0, 0, 200));
	}
	
	@Override
	public boolean pointInsideShape(MapPointLL ll) {
		return true;
	}

	@Override
	protected void calc_area_sqm() {
		
	}

	public NavStruct calc_bounds() {
//		GISRecord gisr;
//		for(int i=0;i<m_gislist.size();i++) {
//			gisr = (GISRecord)m_gislist.get(i);
//		}
		return m_bounds;
	}
	@Override
	public NavStruct getFullBBox()
	{
		return m_fullbounds;
	}

	public void calc_coortopix(Navigation n) {
	}	
	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) {
		return true;
	}

	@Override
	protected void updateCanLock(List<ShapeStruct> restrictionShapes) {
		
	}

	protected void calc_epicentre_coortopix(Navigation n) {
		super.calc_epicentre_coortopix(n);
	}

	public Object clone() throws CloneNotSupportedException {
		//return null;
		try {
			GISShape s = new GISShape((GISList)get_gislist().clone());
			s.m_border_color = this.m_border_color;
			s.set_fill_color(this.m_fill_color);
			return s;
		} catch(Exception e) {
			no.ums.pas.ums.errorhandling.Error.getError().addError("Could not clone GISShape", "Unexpected error", e, 1);
			return null;
		}
	}

	protected void create_texpaint(int h) {
		super.create_texpaint(h);
	}

	protected void draw_epicentre(Graphics g) {
		super.draw_epicentre(g);
	}

	public void draw(Graphics g, Navigation nav, boolean b_dashed, boolean b_finalized, boolean b_editmode, Point p) {
		
	}
	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int nPenSize, boolean bPaintShapeName) {
		
	}

	public PolySnapStruct snap_to_point(Point p1, int n_max_distance, boolean b_current, Dimension dim_map, Navigation nav) {
		return null;
	}
	
}