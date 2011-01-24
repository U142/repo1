package no.ums.pas.maps.defines;

import no.ums.pas.ums.errorhandling.Error;

import java.awt.*;
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
		set_fill_color(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.2));
		m_border_color = new Color((float)0.0, (float)0.0, (float)0.0, (float)1.0);
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
		if(m_p_center != null && m_p_corner != null)
			return true;
		return false;
	}

	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int nPenSize, boolean bPaintShapeName) {		
		draw(g, nav, !bEditmode, bFinalized, true, null);
	}
	public void draw(Graphics g, Navigation nav, boolean b_dashed, boolean b_active, boolean b_islocked) {
		draw(g, nav, !b_active, (!b_islocked && b_active ? false : true), true, null);
	}
	public void draw(Graphics g, Navigation nav, boolean b_dashed, boolean b_finalized, boolean b_details, Point mousepos) {
		Graphics2D g2d = (Graphics2D)g;
		calc_coortopix(nav);
		Stroke stroke_revert = g2d.getStroke();
		try {
			if(get_center()!=null && m_ellipseshape!=null) {
				if(b_details) {
					g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 0.6f));
					g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3.0f, new float[]{ 3.0f }, 0.0f));
					g2d.drawLine((get_center().get_x() - get_diameter_width_pix()/2), (get_center().get_y() + get_diameter_height_pix()/2), (get_center().get_x() + get_diameter_width_pix()/2), (get_center().get_y() + get_diameter_height_pix()/2));
					g2d.drawLine((get_center().get_x() + get_diameter_width_pix()/2), (get_center().get_y() + get_diameter_height_pix()/2), (get_center().get_x() + get_diameter_width_pix()/2), (get_center().get_y() - get_diameter_height_pix()/2));
					//FontSet g2d.setFont(new Font("Arial", Font.BOLD, 11));
					String sz_width = m_n_diameter_width_meters + "m";
					String sz_height= m_n_diameter_height_meters + "m";
					g2d.drawString(sz_height, (get_center().get_x() + get_diameter_width_pix()/2) + 5, get_center().get_y());
					g2d.drawString(sz_width,get_center().get_x(), (get_center().get_y() + get_diameter_height_pix()/2) + 17);
				}
				if(b_dashed)
					g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3.0f, new float[]{ 3.0f }, 0.0f));
				else
					g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				
				if(b_dashed)
					g2d.setPaint(m_tex_paint);
				else
					g2d.setColor(get_fill_color());
				g2d.fill(m_ellipseshape);
				g2d.setColor(get_border_color());
				g2d.draw(m_ellipseshape);
				g2d.setStroke(stroke_revert);
			}
		} catch(Exception e) {
			g2d.setStroke(stroke_revert);
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendPropertiesEllipse","Exception in draw",e,1);
		}
		super.draw_epicentre(g);
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
			if(m_p_center!=null && m_p_corner!=null) {
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
	public void calc_coortopix(Navigation nav) {
		if(m_p_center!=null && m_p_corner!=null) {
			m_p_center = new MapPoint(nav, new MapPointLL(m_p_center.get_mappointll()));
			m_p_corner = new MapPoint(nav, new MapPointLL(m_p_corner.get_mappointll()));
			recalc_shape(nav);
			calc_epicentre_coortopix(nav);
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