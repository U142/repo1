package no.ums.pas.ums.tools;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ColorButton extends JButton {
	public static final long serialVersionUID = 1;
	private Color m_override = null;
	private Dimension m_dim = null;
	public ColorButton(Color override, Dimension dim) {
		super("");
		m_dim = dim;
		m_override = override;
		//setBackground(override);
		
	}
	//public Color getBackground() {
	//	return m_override;
	//}
	public void paint(java.awt.Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		super.paint(g2d);
		if(m_override!=null && m_dim!=null) {
			g2d.setColor(m_override);
			g2d.fillOval(4, 4, getWidth()-8,getHeight()-8);
		}
	}
	public void setBg(Color col) {
		//super.setBackground(col);
		//super.setForeground(col);
		m_override = col;
		repaint();
	}
}