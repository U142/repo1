package no.ums.pas.maps.defines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;


public abstract class ShapeStruct extends Object implements Cloneable {
	
	public static final int SHAPE_UNKNOWN = -1;
	public static final int SHAPE_ELLIPSE = 0;
	public static final int SHAPE_POLYGON = 1;
	public static final int SHAPE_GISIMPORT = 2;
	public static final int SHAPE_MUNICIPAL = 9;
	
	public double POINT_PRECISION = 10000.0;
	
	protected boolean b_can_lock = false;
	protected void setCanLock(boolean b) { b_can_lock = b; }
	protected abstract void updateCanLock(List<ShapeStruct> restrictionShapes);
	
	public void setPrecision(double d) {
		POINT_PRECISION = d;
	}
	
	/*public static final int SHOW_POLYGON_FULL					= 1;
	public static final int SHOW_POLYGON_SIMPLIFIED_PRPIXELS	= 2;
	public static final int SHOW_POLYGON_SIMPLIFIED_PRMETERS	= 3;
*/
	public enum DETAILMODE
	{
		SHOW_POLYGON_FULL,
		SHOW_POLYGON_SIMPLIFIED_PRPIXELS,
		SHOW_POLYGON_SIMPLIFIED_PRMETERS,
	}
	
	public void enableSendColor()
	{
		set_fill_color(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.2));
		m_border_color = new Color((float)0.0, (float)0.0, (float)0.0, (float)1.0);
	}
	public void enableStatusColor()
	{
		set_fill_color(new Color((float)1.0, (float)0.0, (float)0.0, (float)0.2));
		m_border_color = new Color((float)0.0, (float)0.0, (float)0.0, (float)1.0);
	}

	protected boolean m_b_hidden = false;
	protected boolean m_b_obsolete = false;
	protected double m_f_area_sqm = 0;
	protected boolean m_b_editable = true;
	
	/**
	 * set if the shape is locked for further editing
	 * @param b true to lock this shape
	 */
	public void setEditable(boolean b)
	{
		m_b_editable = b;
		System.out.println("Shape setEditable="+m_b_editable);
	}
	
	/**
	 * If sending is "locked", the shape should not be editable
	 * @return true if editable
	 */
	public boolean isEditable()
	{
		return m_b_editable;
	}
	
	protected DETAILMODE m_n_current_show_mode = DETAILMODE.SHOW_POLYGON_SIMPLIFIED_PRPIXELS;
	public void setDetailMode(DETAILMODE nmode)
	{
		m_n_current_show_mode = nmode;
	}

	protected double get_area_sqm()
	{
		return m_f_area_sqm;
	}
	protected abstract void calc_area_sqm();
	public void setHidden(boolean b)
	{
		m_b_hidden = b;
	}
	public boolean isHidden()
	{
		return m_b_hidden;
	}
	public void setObsolete(boolean b)
	{
		m_b_obsolete = b;
	}
	public boolean isObsolete()
	{
		return m_b_obsolete;
	}
	public int numParts = 0;
	public List<Integer> parts = new ArrayList<Integer>();
	public int combination_id = -1;

	protected NavStruct m_bounds = new NavStruct();
	protected MapPointLL m_center = new MapPointLL(0,0);
	protected MapPointPix m_center_pix = new MapPointPix(0, 0);
	public void SetBounds(double lbo, double rbo, double ubo, double bbo)
	{
		m_bounds._lbo = lbo;
		m_bounds._rbo = rbo;
		m_bounds._bbo = bbo;
		m_bounds._ubo = ubo;
		m_center.setLon((rbo - lbo)/2);
		m_center.setLat((ubo - bbo)/2);
	} 
	
	public String toString()
	{
		return new Long(shapeID).toString();
	}
	public long shapeID = 0;
	public void setShapeId(long n) { shapeID = n; }
	public String shapeName = "";
	private ImageIcon m_icon_epicentre;
	private MapPoint m_p_epicentre = null;
	public MapPoint get_epicentre() { return m_p_epicentre; }
	public void set_epicentre(MapPoint p) { m_p_epicentre = p; }
	protected void draw_epicentre(Graphics g) {
		if(get_epicentre()!=null) {
			g.drawImage(m_icon_epicentre.getImage(), get_epicentre().get_x(), get_epicentre().get_y(), m_icon_epicentre.getIconWidth(), m_icon_epicentre.getIconHeight(), null);
		}
	}
	/** mark as finished to prepare bounds*/
	public void finalizeShape()
	{
		try
		{
			calc_area_sqm();
			calc_bounds();
			calc_coortopix(variables.NAVIGATION);
			//can_lock(variables.USERINFO.get_departments().get_combined_restriction_shape());
			updateCanLock(variables.USERINFO.get_departments().get_combined_restriction_shape());
		}
		catch(Exception e)
		{
			
		}
	}
	
	protected void calc_epicentre_coortopix(Navigation n) {
		if(get_epicentre()!=null) {
			get_epicentre().recalc_pix(n);
		}
	}
	
	public abstract boolean pointInsideShape(MapPointLL ll);
	
	public abstract NavStruct getFullBBox();
	
	public int getType() {
		if(this.getClass().equals(PolygonStruct.class)) {
			return SHAPE_POLYGON;
		}
		else if(this.getClass().equals(EllipseStruct.class)) {
			return SHAPE_ELLIPSE;
		}
		else if(this.getClass().equals(UnknownShape.class)) {
			return SHAPE_UNKNOWN;
		}
		else if(this.getClass().equals(GISShape.class)) {
			return SHAPE_GISIMPORT;
		}
		else if(this.getClass().equals(MunicipalStruct.class)) {
			return SHAPE_MUNICIPAL;
		}
		return SHAPE_UNKNOWN;
	}
	
	protected Color m_fill_color = new Color(0, 0, 200, 200);
	protected Color m_border_color;
	protected Color m_text_color = new Color(0,0,0,200);
	protected Color m_text_bg_color = new Color(0,0,0,100);
	public PolygonStruct typecast_polygon() { return (PolygonStruct)this; }
	public EllipseStruct typecast_ellipse() { return (EllipseStruct)this; }
	public MunicipalStruct typecast_municipal() { return (MunicipalStruct)this; }
	public GISShape typecast_gis() { return (GISShape)this; }
	public TasStruct typecast_tas() { return (TasStruct)this; }

	public ShapeStruct() {
		m_icon_epicentre = ImageLoader.load_icon("epicentre_pinpoint.png");
	}
	public ShapeStruct(DETAILMODE mode, double precision) {
		this();
		setDetailMode(mode);
		POINT_PRECISION = precision;
	}
	public Color get_border_color() { return m_border_color; }
	public void set_border_color(Color col) { m_border_color = col; }
	public abstract boolean can_lock(List<ShapeStruct> restrictionShapes);
	public abstract void draw(Graphics g, Navigation nav, boolean b_dashed, boolean b_finalized, boolean b_editmode, Point p);
	public abstract void draw(Graphics g, Navigation nav, boolean b_dashed, boolean b_finalized, boolean b_editmode, 
						Point p, boolean b_border, boolean b_fill, int pensize, boolean bPaintShapeName);
	public abstract NavStruct calc_bounds();
	public abstract PolySnapStruct snap_to_point(Point p1, int n_max_distance, boolean b_current,
			Dimension dim_map, Navigation nav);
	public abstract void calc_coortopix(Navigation n);
	public abstract Object clone() throws CloneNotSupportedException;
	
	public Color get_fill_color() { return m_fill_color; }
	public void set_fill_color(Color col) { 
		//m_fill_color = new Color((float)col.getRed(), (float)col.getGreen(), (float)col.getRed(), (float)0.2);
		try {
			float[] comp = new float[3];
			comp = col.getRGBColorComponents(comp);
			m_fill_color = new Color(comp[0], comp[1], comp[2], (float)0.2);
			create_texpaint(6);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("ShapeStruct","Exception in set_fill_color",e,1);
		}
	}
	
	public Color get_text_color() { return m_text_color; }
	public void set_text_color(Color col) {
		m_text_color = new Color(col.getRed(), col.getGreen(), col.getBlue(), 200);
	}
	public Color get_text_bg_color() { return m_text_bg_color; }
	public void set_text_bg_color(Color col) {
		m_text_bg_color = col;
	}
	

	BufferedImage m_buff_image = null;
	TexturePaint m_tex_paint = null;
	public BufferedImage skravering(int h, Rectangle rek, Color col1, Color col2) {
		BufferedImage buf=new BufferedImage(h,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D gg = buf.createGraphics();
		gg.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		gg.setColor(col2); 
		gg.fill(rek);
		gg.setColor(col1);
		gg.drawLine(0,0,h-1,h-1);
		gg.drawLine(h-1,0,0,h-1);
		return buf; 
	}	
	
	
	protected void create_texpaint(int h) {
		Rectangle rek = new Rectangle(0, 0, h, h);
		m_buff_image = skravering(h, rek, get_fill_color(), new Color(0.0f, 0.0f, 0.0f, 0.0f));
		m_tex_paint = new TexturePaint(m_buff_image,rek);
	}

}