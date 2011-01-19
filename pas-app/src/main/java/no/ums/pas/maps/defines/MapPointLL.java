package no.ums.pas.maps.defines;

public class MapPointLL implements Comparable<MapPointLL> {
	private int m_point_reference;
	private double m_measurement_reference;
	private MapPointLL m_degree_distance = null;
	private double m_lon;
	private double m_lat;
	public MapPointLL(double lon, double lat) {
		m_lon = lon;
		m_lat = lat;
	}
	public MapPointLL(double lon, double lat, int pointref)
	{
		this(lon, lat);
		m_point_reference = pointref;
	}
	public MapPointLL(MapPointLL ll) {
		m_lon = ll.get_lon();
		m_lat = ll.get_lat();
	}
	public void setPointReference(int ref)
	{
		m_point_reference = ref;
	}
	/**
	 * May be used as reference to where in a polygon the point is.
	 * @return
	 */
	public int getPointReference()
	{
		return m_point_reference;
	}
	public double getMeasurementReference()
	{
		return m_measurement_reference;
	}
	public MapPointLL getDegreeDistance()
	{
		return m_degree_distance;
	}
	public void setLon(double l)
	{
		m_lon = l;
	}
	public void setLat(double l)
	{
		m_lat = l;
	}
	public void setMeasurementReference(double d)
	{
		m_measurement_reference = d;
	}
	public void setDegreeDistance(double x, double y)
	{
		m_degree_distance = new MapPointLL(Math.abs(x), Math.abs(y));
	}
	public double get_lon() { return m_lon; }
	public double get_lat() { return m_lat; }
	public String toString() {
		try {
			return (double)(Math.round(get_lon() * 10000)) / 10000 + ", " + (double)(Math.round(get_lat() * 10000)) / 10000;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return "No Point";
	}
	@Override
	public int compareTo(MapPointLL o) {
		if(getMeasurementReference()>o.getMeasurementReference())
			return 1;
		return -1;
	}
	
}