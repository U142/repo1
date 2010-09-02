package no.ums.pas.maps.defines;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.ShapeStruct;

public class PLMNShape extends ShapeStruct
{

	@Override
	protected void calc_area_sqm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NavStruct calc_bounds() {
		return new NavStruct(5, 8, 54, 50);
	}

	@Override
	public void calc_coortopix(Navigation n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean can_lock() {
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p, boolean bBorder,
			boolean bFill, int pensize, boolean bPaintShapeName) {
		Font oldFont = g.getFont();
		Color oldColor = g.getColor();
		int x = variables.MAPPANE.getWidth()/2;
		int y = variables.MAPPANE.getHeight()/2;
		g.setFont(new Font("Arial", Font.BOLD, 40));
		String sOutput = PAS.l("main_sending_type_national");
		int borderline = 10;
		int height = g.getFontMetrics().getHeight();
		int width = g.getFontMetrics().stringWidth(sOutput);
		int maxascent = g.getFontMetrics().getMaxAscent();
		int wHalf = width/2;
		int hHalf = height/2;
		g.setColor(new Color(0, 0, 0, 200));
		g.drawRoundRect(x-wHalf-borderline, y-maxascent - borderline, width + borderline*2, height + borderline*2, 10, 10);
		g.setColor(new Color(255, 255, 255, 200));
		g.fillRoundRect(x-wHalf-borderline, y-maxascent - borderline, width + borderline*2, height + borderline*2, 10, 10);
		g.setColor(new Color(255, 0, 0, 200));
		g.drawString(sOutput, x - wHalf, y);
		
		g.setColor(oldColor);
		g.setFont(oldFont);
	}

	@Override
	public void draw(Graphics g, Navigation nav, boolean bDashed,
			boolean bFinalized, boolean bEditmode, Point p) {
		draw(g, nav, bDashed, bFinalized, bEditmode, p, false, false, 1, false);
	}

	@Override
	public NavStruct getFullBBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean pointInsideShape(MapPointLL ll) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PolySnapStruct snap_to_point(Point p1, int nMaxDistance,
			boolean bCurrent, Dimension dimMap, Navigation nav) {
		// TODO Auto-generated method stub
		return null;
	}
	
}