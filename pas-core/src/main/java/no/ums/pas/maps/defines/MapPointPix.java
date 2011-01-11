package no.ums.pas.maps.defines;

import java.awt.*;

public class MapPointPix {
	private Dimension m_dim;
	public MapPointPix(int x, int y) {
		m_dim = new Dimension(x, y);
	}
	public MapPointPix(Dimension dim) {
		m_dim = new Dimension(dim);
	}
	public void set(Dimension d) {
		m_dim = d;
	}

	public int get_x() { return m_dim.width; }
	public int get_y() { return m_dim.height; }
	public void set_x(int x) { m_dim.width = x; }
	public void set_y(int y) { m_dim.height = y; }
}
