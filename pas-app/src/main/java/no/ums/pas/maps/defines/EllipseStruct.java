package no.ums.pas.maps.defines;

import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.ZoomLookup;
import no.ums.map.tiled.component.MapModel;
import no.ums.pas.ums.errorhandling.Error;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class EllipseStruct extends ShapeStruct {
	private MapPoint m_p_center = null;
	private MapPoint m_p_corner = null;
	private Ellipse2D m_ellipseshape = null;
	private long m_n_diameter_width_meters = 0;
	private long m_n_diameter_height_meters = 0;
	private int m_n_diameter_width_pix = 0;
	private int m_n_diameter_height_pix = 0;
	public long get_diameter_width_meters() { return m_n_diameter_width_meters; }
	public long get_diameter_height_meters() { return m_n_diameter_height_meters; }
	public int get_diameter_width_pix() { return m_n_diameter_width_pix; }
	public int get_diameter_height_pix() { return m_n_diameter_height_pix; }
	
	public EllipseStruct(Color fill_color, Color border_color) { 
		set_fill_color(fill_color);
		m_border_color = border_color;	
	}
	public EllipseStruct() {
		set_fill_color(new Color(0.0f, 0.0f, 1.0f, 0.2f));
		m_border_color = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	
	
	@Override
	protected void updateCanLock(List<ShapeStruct> restrictionShapes) {
		
	}
	@Override
	protected void calc_area_sqm() {
		m_f_area_sqm = Math.PI * get_diameter_height_meters() * get_diameter_width_meters();
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
	
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance, boolean b_current,
			Dimension dim_map, Navigation nav) {
		return null;
	}
	public MapPoint get_center() {
		return m_p_center;
	}
	public MapPoint get_corner() { return m_p_corner; }

	public void calc_diameters(Navigation nav) {
		try {
			int n_diameter_x = (get_corner().get_x() - get_center().get_x()) * 2;
			int n_diameter_y = (get_corner().get_y() - get_center().get_y()) * 2;
			m_n_diameter_width_meters	= nav.calc_distance(get_corner().get_x(), get_center().get_y(), get_corner().get_x() + n_diameter_x, get_center().get_y());
			m_n_diameter_height_meters	= nav.calc_distance(get_center().get_x(), get_corner().get_y(), get_center().get_x(), get_corner().get_y() + n_diameter_y);
			m_n_diameter_width_pix   	= Math.abs(get_corner().get_x() - get_center().get_x()) * 2;
			m_n_diameter_height_pix		= Math.abs(get_corner().get_y() - get_center().get_y()) * 2;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendPropertiesEllipse","Exception in calc_diameters",e,1);
		}
	}
	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) {
        return m_p_center != null && m_p_corner != null;
    }

	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
                     boolean bFill, int nPenSize, boolean bPaintShapeName) {
		draw(g, mapModel, zoomLookup, bDashed, bFinalized, bEditmode, p, bBorder,bFill, nPenSize, bPaintShapeName, false);
	}
	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
                     boolean bFill, int nPenSize, boolean bPaintShapeName,
                     boolean bHasFocus) {
		Graphics2D g2d = (Graphics2D)g;
        if(get_center()!=null && m_ellipseshape!=null) {
            g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 0.6f));

            final LonLat centerLL = new LonLat(get_center().get_lon(), get_center().get_lat());
            final Point offset = zoomLookup.getPoint(mapModel.getTopLeft());
            final Point center = zoomLookup.getPoint(centerLL);
            final Point corner = zoomLookup.getPoint(new LonLat(get_corner().get_lon(), get_corner().get_lat()));
            final int w = Math.abs(corner.x - center.x) * 2;
            final int h = Math.abs(corner.y - center.y) * 2;

            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3.0f, new float[]{3.0f}, 0.0f));
            g2d.drawLine(center.x-offset.x - w/2, center.y-offset.y + h/2, center.x-offset.x + w/2, center.y-offset.y + h/2);
            g2d.drawLine(center.x-offset.x + w/2, center.y-offset.y + h/2, center.x-offset.x + w/2, center.y-offset.y - h/2);

            double width = zoomLookup.getLonLat(corner.x, corner.y).distanceToInM(zoomLookup.getLonLat(corner.x + w, corner.y));
            double height= zoomLookup.getLonLat(corner.x, corner.y).distanceToInM(zoomLookup.getLonLat(corner.x, corner.y + h));
            g2d.drawString(String.format("%.2fm", width), (center.x-offset.x + w/2) + 5, center.y-offset.y);
            g2d.drawString(String.format("%.2fm", height),center.x-offset.x, center.y-offset.y + h/2 + 17);

            final Ellipse2D.Double ellipse = new Ellipse2D.Double(center.x - offset.x - w/2, center.y - offset.y - h/2, w, h);
            drawShape(g2d, ellipse, nPenSize, bDashed, bFill, bBorder, bPaintShapeName, bHasFocus);
        }
		super.draw_epicentre(g, zoomLookup, mapModel);
	}

    public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed, boolean b_finalized, boolean b_details, Point mousepos) {
		draw(g, mapModel, zoomLookup, !b_finalized, b_finalized, true, null, true, true, 1, false);
	}
	public void set_ellipse(Navigation nav, MapPoint p_center, MapPoint p_corner) {
		m_p_center = p_center;
		m_p_corner = p_corner;		
		recalc_shape(nav);
		finalizeShape();
	}
	public void set_ellipse_center(Navigation nav, MapPoint p_center) {
		set_ellipse(nav, p_center, m_p_corner);
		finalizeShape();
	}
	public void set_ellipse_corner(Navigation nav, MapPoint p_corner) {
		set_ellipse(nav, m_p_center, p_corner);
		finalizeShape();
	}
	public void recalc_shape(Navigation nav) {
		//Ellipse2D.Double height, width, upperleft_x, upperleft_y
		try {
			
			if(m_p_center!=null && m_p_corner!=null && m_p_center.get_mappointpix()!=null && m_p_corner.get_mappointpix()!=null) {
				int height, width, uleft_x, uleft_y;
				height = Math.abs(m_p_corner.get_y() - m_p_center.get_y()) * 2;
				width  = Math.abs(m_p_corner.get_x() - m_p_center.get_x()) * 2;
				uleft_x = m_p_center.get_x() - Math.abs(m_p_corner.get_x() - m_p_center.get_x());
				uleft_y = m_p_center.get_y() - Math.abs(m_p_corner.get_y() - m_p_center.get_y());
				m_ellipseshape = new Ellipse2D.Double(uleft_x, uleft_y, width, height);//height, width, uleft_x, uleft_y);	
				calc_diameters(nav);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendPropertiesEllipse","Exception in recalc_shape",e,1);
		}
	}
	
	@Override
	public void calc_coortopix(Navigation nav)
	{
		if(m_p_center!=null && m_p_corner!=null) {
			m_p_center = new MapPoint(nav, new MapPointLL(m_p_center.get_mappointll()));
			m_p_corner = new MapPoint(nav, new MapPointLL(m_p_corner.get_mappointll()));
			recalc_shape(nav);
			calc_epicentre_coortopix(nav);
			Dimension screen = new Dimension(nav.coor_to_screen(m_p_center.get_lon(), m_p_center.get_lat(), false));
			m_center_pix.set(screen);
		}		
	}

	public NavStruct calc_bounds() {
		NavStruct nav = null;
		if(m_p_center!=null && m_p_corner!=null) {
			try {
				double _lbo, _rbo, _ubo, _bbo;
				_lbo = m_p_center.get_lon() - Math.abs(m_p_corner.get_lon() - m_p_center.get_lon());
				_rbo = m_p_center.get_lon() + Math.abs(m_p_corner.get_lon() - m_p_center.get_lon());
				_ubo = m_p_center.get_lat() + Math.abs(m_p_corner.get_lat() - m_p_center.get_lat());
				_bbo = m_p_center.get_lat() - Math.abs(m_p_corner.get_lat() - m_p_center.get_lat());
				nav = new NavStruct(_lbo, _rbo, _ubo, _bbo);
				m_bounds = nav;
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();				
				Error.getError().addError("SendPropertiesEllipse","Exception in calc_bounds",e,1);
			}
		}
		return nav;
	} 
	public Object clone() throws CloneNotSupportedException {
		EllipseStruct e = new EllipseStruct();
		e.m_border_color = this.m_border_color;
		e.m_ellipseshape = this.m_ellipseshape;
		e.set_fill_color(this.m_fill_color);
		e.m_p_center = this.m_p_center;
		e.m_p_corner = this.m_p_corner;
		return e;
	}
}