package no.ums.pas.maps.defines;

import java.awt.Point;

public abstract class UMSMapObject extends Object {
	private Point m_p;
	private boolean m_b_ismoving = false;
	public Point p() { return m_p; }
	public void set_pos(Point p) { 
		m_p = p;
	}
	abstract public void set_pos(Point p, MapPoint mp);
	public boolean isMoving() { return m_b_ismoving; }
	public void setMoving(boolean b) { m_b_ismoving = b; }
	public UMSMapObject(Point p) {
		super();
		m_p = p;
	}
	
	
}



