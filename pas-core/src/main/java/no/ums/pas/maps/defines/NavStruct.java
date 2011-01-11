package no.ums.pas.maps.defines;


public class NavStruct {
	public double _lbo, _rbo, _ubo, _bbo;
	public NavStruct(double lbo, double rbo, double ubo, double bbo)
	{
		_lbo = lbo; _rbo = rbo; _ubo = ubo; _bbo = bbo;
	}
	public NavStruct() {
		this(-9999, -9999, -9999, -9999);
	}
	public String toString() {
		return "lbo=" + _lbo + " ubo=" + _ubo + " rbo=" + _rbo + " bbo=" + _bbo;
	}
	public boolean pointInside(MapPointLL ll)
	{
		if(ll.get_lon()>=_lbo && ll.get_lon()<=_rbo && ll.get_lat()>=_bbo && ll.get_lat()<=_ubo)
			return true;
		return false;
	}
	/*public double _get_lbo() {return _lbo;}
	public double _get_rbo() {return _rbo;}
	public double _get_ubo() {return _ubo;}
	public double _get_bbo() {return _bbo;}*/
	
}