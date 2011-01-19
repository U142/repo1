package no.ums.pas.ums.tools;

import javax.swing.*;
import java.awt.*;

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
	public Color getBackground() {
		return m_override;
	}
	public void paint(java.awt.Graphics g) {
		super.paint(g);
		if(m_override!=null && m_dim!=null) {
			g.setColor(m_override);
			g.fillOval(4, 4, m_dim.width-8,m_dim.height-8);
		}
	}
	public void setBg(Color col) {
		super.setBackground(col);
		super.setForeground(col);
		m_override = col;
		repaint();
	}
}