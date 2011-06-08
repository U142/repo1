package no.ums.pas.maps.defines;


import no.ums.log.Log;
import no.ums.log.UmsLog;

public class MapPoint {

    private static final Log log = UmsLog.getLogger(MapPoint.class);

	MapPointLL m_ll;
	MapPointPix m_pix;
	public int get_x() { return m_pix.get_x(); }
	public int get_y() { return m_pix.get_y(); }
	public double get_lon() { return m_ll.get_lon(); }
	public double get_lat() { return m_ll.get_lat(); }
	public MapPointLL get_mappointll() { return m_ll; }
	public MapPointPix get_mappointpix() { return m_pix; }
	public MapPoint(Navigation nav, MapPointLL point_ll) {
		m_ll = point_ll;
		try
		{
			m_pix = new MapPointPix(nav.coor_to_screen(m_ll.get_lon(), m_ll.get_lat(), false));
		}
		catch(Exception e)
		{
			m_pix = null;
		}
	}
	public MapPoint(Navigation nav, MapPointPix pix) {
		m_pix = pix;
		try
		{
			m_ll  = new MapPointLL(nav.screen_to_coor(m_pix.get_x(), m_pix.get_y()));
		}
		catch(Exception e)
		{
			m_ll = null;
		}
	}
	public String toString() {
		try {
			return (double)(Math.round(get_lon() * 100000)) / 100000 + ", " + (double)(Math.round(get_lat() * 100000)) / 100000;
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		return "No Point";
	}
	public void recalc_pix(Navigation n) {
		m_pix = new MapPointPix(n.coor_to_screen(get_lon(), get_lat(), false));
	}
}