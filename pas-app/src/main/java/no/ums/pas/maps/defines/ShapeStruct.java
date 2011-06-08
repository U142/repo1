package no.ums.pas.maps.defines;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.ZoomLookup;
import no.ums.map.tiled.component.MapModel;
import no.ums.pas.core.Variables;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public abstract class ShapeStruct extends Object implements Cloneable {


    private static final Log log = UmsLog.getLogger(ShapeStruct.class);
	
	public static final int SHAPE_UNKNOWN = -1;
	public static final int SHAPE_ELLIPSE = 0;
	public static final int SHAPE_POLYGON = 1;
	public static final int SHAPE_GISIMPORT = 2;
	public static final int SHAPE_MUNICIPAL = 9;
	
	public double POINT_PRECISION = 100000.0;
	
	protected boolean b_can_lock = false;
	protected void setCanLock(boolean b) { b_can_lock = b; }
	protected abstract void updateCanLock(List<ShapeStruct> restrictionShapes);

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
		log.debug("Shape setEditable="+m_b_editable);
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
		return Long.toString(shapeID);
	}
	public long shapeID = 0;
	public void setShapeId(long n) { shapeID = n; }
	public String shapeName = "";
	private ImageIcon m_icon_epicentre;
	private MapPoint m_p_epicentre = null;
	public MapPoint get_epicentre() { return m_p_epicentre; }
	public void set_epicentre(MapPoint p) { m_p_epicentre = p; }
	protected void draw_epicentre(Graphics g, ZoomLookup zoomLookoup, MapModel mapModel) {
		if(get_epicentre()!=null) {
            final Point offset = zoomLookoup.getPoint(mapModel.getTopLeft());
            final Point center = zoomLookoup.getPoint(new LonLat(get_epicentre().get_lon(), get_epicentre().get_lat()));

            final int w = m_icon_epicentre.getIconWidth();
            final int h = m_icon_epicentre.getIconHeight();
            g.drawImage(m_icon_epicentre.getImage(), center.x-offset.x-w/2, center.y-offset.y-h/2, w, h, null);
		}
	}
	/** mark as finished to prepare bounds*/
	public void finalizeShape() {
        calc_area_sqm();
        calc_bounds();
//        calc_coortopix(Variables.getNavigation());
        if (Variables.getUserInfo() != null) {
            updateCanLock(Variables.getUserInfo().get_departments().get_combined_restriction_shape());
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

	public boolean hasValidBounds() {
		return m_bounds._lbo>-180 && m_bounds._rbo<180 && m_bounds._bbo>-90 && m_bounds._ubo<90;
	}

	
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
	public abstract void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed, boolean b_finalized, boolean b_editmode, Point p);
	public abstract void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed, boolean b_finalized, boolean b_editmode,
                              Point p, boolean b_border, boolean b_fill, int pensize, boolean bPaintShapeName);
	public abstract void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed, boolean b_finalized, boolean b_editmode,
                              Point p, boolean b_border, boolean b_fill, int pensize, boolean bPaintShapeName, boolean bHasFocus);

    protected void drawShape(Graphics2D g2d, Shape shape, int penSize, boolean dashed, boolean fill, boolean border, boolean showName, boolean focused) {
        Color col_dot = new Color(get_fill_color().getRed(), get_fill_color().getGreen(), get_fill_color().getBlue());
        Stroke oldStroke = g2d.getStroke();
        Paint oldPaint = g2d.getPaint();
        if (dashed) {
            g2d.setStroke(new BasicStroke(penSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 30.0f, new float[]{3.0f, 3.0f}, 0.0f));
            g2d.setPaint(m_tex_paint);
        } else {
            g2d.setStroke(new BasicStroke(penSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2d.setColor(get_fill_color());
        }
        if (fill) {
            g2d.fill(shape);
        }
        if (border) {
            g2d.setColor(col_dot);
            g2d.draw(shape);
        }
        if (showName) {
            paintShapeName(g2d, new Point((int) shape.getBounds().getCenterX(), (int) shape.getBounds().getCenterY()), focused);
        }
        g2d.setStroke(oldStroke);
        g2d.setPaint(oldPaint);
    }

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
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
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

	public void paintShapeName(Graphics g, boolean bIsActive, boolean bHasFocus) {
		if(m_center_pix.get_x()<=0 && m_center_pix.get_y()<=0)
			return;
        paintShapeName(g, new Point(m_center_pix.get_x(), m_center_pix.get_y()), bHasFocus);
	}

    private static final Color TEXT_BG = new Color(SystemColor.control.getRed(), SystemColor.control.getGreen(), SystemColor.control.getBlue(), 70);

    public void paintShapeName(Graphics g, Point center, boolean bHasFocus) {
		Font f = new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 10);
		FontMetrics fm = g.getFontMetrics(f);
		int width = fm.stringWidth(this.shapeName);
		int height = fm.getHeight();
        int factor = 5;

		g.setColor(TEXT_BG);
		g.fillRoundRect(center.x-width/2-factor, center.y-height/2-factor, width+factor*2, height+factor, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(center.x-width/2-factor, center.y-height/2-factor, width+factor*2, height+factor, 10, 10);

		g.setFont(f);
		g.setColor((bHasFocus ? SystemColor.textText : SystemColor.controlDkShadow));
		g.drawString(this.shapeName, center.x-width/2, center.y+2);
	}
}