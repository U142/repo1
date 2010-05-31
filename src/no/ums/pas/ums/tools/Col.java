package no.ums.pas.ums.tools;

import java.awt.Color;

public class Col {
	private Color m_col_border;
	private Color m_col_fill;
	public Color get_border() { return m_col_border; }
	public Color get_fill() { return m_col_fill; }
	public Col() {
		this(new Color((float)0.6, (float)0.6, (float)0.6, (float)0.3), new Color((float)0.6, (float)0.6, (float)0.6, (float)0.6));
	}
	public Col(Color border, Color fill) {
		m_col_border= border;
		m_col_fill	= fill;
	}
	public Col(Col col) {
		this(col.get_border(), col.get_fill());
	}
}