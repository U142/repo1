package no.ums.pas.maps.defines;

import no.ums.map.tiled.ZoomLookup;
import no.ums.map.tiled.component.MapModel;
import no.ums.pas.core.Variables;
import no.ums.pas.localization.Localization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class PLMNShape extends ShapeStruct
{
	public PLMNShape()
	{
		super();
		set_fill_color(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.2));
		m_border_color = new Color((float)0.0, (float)0.0, (float)0.0, (float)1.0);
		set_text_color(get_fill_color());
	}
	
	@Override
	protected void calc_area_sqm() {
		
	}

	@Override
	public NavStruct calc_bounds() {
		return new NavStruct(3.12167, 7.29843, 53.4049, 51.01873);
		//return new NavStruct(5, 8, 54, 50);
	}

	@Override
	public void calc_coortopix(Navigation n) {
		
	}

	@Override
	public boolean can_lock(List<ShapeStruct> restrictionShapes) {
		return true;
	}
	@Override
	protected void updateCanLock(List<ShapeStruct> restrictionShapes) {
		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return null;
	}

	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
                     boolean bFill, int pensize, boolean bPaintShapeName) {
		if(m_b_hidden)
			return;
		Graphics2D g2d = (Graphics2D)g;
		Font oldFont = g.getFont();
		Color oldColor = g.getColor();
		int x = Variables.getMapFrame().getWidth()/2;
		int y = Variables.getMapFrame().getHeight()/2;
		g.setFont(new Font("Arial", Font.BOLD, 40));
		String sOutput = "";
        sOutput = Localization.l("main_sending_type_national") + "\n" + shapeName;
		String[] arr = sOutput.split("\n");

		int borderline = 10;
		int lines = arr.length;
		int height = g.getFontMetrics().getHeight();
		int widths [] = new int[lines];
		int maxwidth = 0;
		for(int i=0; i < lines; i++)
		{
			int width = g.getFontMetrics().stringWidth(arr[i]);
			if(width>maxwidth)
				maxwidth = width;
			widths[i] = width;
		}
		int maxascent = g.getFontMetrics().getMaxAscent();
		int wHalf = maxwidth/2;
		int hHalf = height/2;
		//g.setColor(new Color(0, 0, 0, 200));
		//g.setColor(new Color(255, 255, 255, 200));
		g.setColor(get_text_bg_color());
		g.fillRoundRect(x-wHalf-borderline, y-maxascent - borderline, maxwidth + borderline*2, height*lines + borderline*2, 10, 10);

		g.setColor(Color.black);
		g.drawRoundRect(x-wHalf-borderline, y-maxascent - borderline, maxwidth + borderline*2, height*lines + borderline*2, 10, 10);

		//g.setColor(new Color(255, 0, 0, 200));
		g.setColor(get_text_color());
		for(int i=0; i < lines; i++)
		{
			g.drawString(arr[i], x - widths[i]/2, y + height*i);
		}
		
		g.setColor(oldColor);
		g.setFont(oldFont);
	}

	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean bDashed,
                     boolean bFinalized, boolean bEditmode, Point p) {
		draw(g, mapModel, zoomLookup, bDashed, bFinalized, bEditmode, p, false, false, 1, false);
	}

	@Override
	public NavStruct getFullBBox() {
		return calc_bounds();
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

	@Override
	public void draw(Graphics g, MapModel mapModel, ZoomLookup zoomLookup, boolean b_dashed,
                     boolean b_finalized, boolean b_editmode, Point p, boolean b_border,
                     boolean b_fill, int pensize, boolean bPaintShapeName,
                     boolean bHasFocus) {
		// TODO Auto-generated method stub
		
	}
	
}