package no.ums.pas.maps.defines;

public class NavPoint extends Object {
	private double _lon;
	private double _lat;
	private double _zoom;
	
	public NavPoint(double lon, double lat, double zoom) {
		_lon = lon;
		_lat = lat;
		_zoom = zoom;
	}
	public double get_lon() { return _lon; }
	public double get_lat() { return _lat; }
	public double get_zoom() { return _zoom; }
	
}