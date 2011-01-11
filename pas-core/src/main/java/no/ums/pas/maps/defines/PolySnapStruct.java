package no.ums.pas.maps.defines;

import java.awt.*;

public class PolySnapStruct extends UMSMapObject {
	private boolean m_b_activesending;
	private int m_n_polygonindex;
	private PolygonStruct m_polystruct;
	private boolean m_b_islastpoint;
	public boolean isActive() { return m_b_activesending; }
	public int get_polyindex() { return m_n_polygonindex; }
	public PolygonStruct get_polygon() { return m_polystruct; }
	public boolean isLastPoint() { return m_b_islastpoint; }
	public void set_pos(Point p, MapPoint mp) {
		super.set_pos(p);
		get_polygon().set_at(get_polyindex(), mp.get_lon(), mp.get_lat());
	}
	
	public PolySnapStruct(Point p, boolean activesending, int n_polygonindex, PolygonStruct poly, boolean b_islastpoint) {
		super(p);
		m_b_activesending = activesending;
		m_n_polygonindex = n_polygonindex;
		m_polystruct = poly;
		m_b_islastpoint = b_islastpoint;
	}
}
