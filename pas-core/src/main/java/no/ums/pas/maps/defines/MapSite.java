package no.ums.pas.maps.defines;



public class MapSite extends Object {
	private int m_n_mapsite;
	private String m_sz_portrayal;
	private String m_sz_mapname;
	public MapSite(String sz_mapname, int n_mapsite, String sz_portrayal) {
		m_sz_mapname = sz_mapname;
		m_n_mapsite = n_mapsite;
		m_sz_portrayal = sz_portrayal;
	}
	public String toString() {
		return m_sz_mapname;
	}
	public int get_mapsite() {
		return m_n_mapsite;
	}
	public String get_portrayal() { 
		return m_sz_portrayal;
	}
	public String get_mapname() {
		return m_sz_mapname;
	}
}