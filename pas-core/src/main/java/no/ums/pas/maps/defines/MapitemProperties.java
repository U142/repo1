package no.ums.pas.maps.defines;


public class MapitemProperties {
	private int m_n_pixradius;
	private int m_n_epsilon = 0; //max diff in meters to assign addresses to the same house
	private boolean m_b_showtext;
	private int m_n_fontsize;
	private int m_n_iconsize_percent = 100;
	private boolean m_b_borderactivated = false;
	
	public int get_pixradius() { return m_n_pixradius; }
	public int get_epsilon() { return m_n_epsilon; }
	public boolean get_showtext() { return m_b_showtext; }
	public int get_fontsize() { return m_n_fontsize; }
	public int get_iconsize_percent() { return m_n_iconsize_percent; }
	public boolean get_border_activated() { return m_b_borderactivated; }
	
	public void set_properties(int n_pixradius, boolean b_border, boolean b_showtext, int n_fontsize)
	{
		m_n_pixradius = n_pixradius;
		m_b_showtext  = b_showtext;
		m_n_fontsize  = n_fontsize;
		m_b_borderactivated = b_border;
	}
	public void set_iconproperties(int n_percent)
	{
		m_n_iconsize_percent = n_percent;
	}
		
	public MapitemProperties()
	{
	}
	
	
	public void set_zoom(int n_meters)
	{
		if(n_meters > 10000) {
			set_properties(1, false, false, 0);
			set_iconproperties(30);
		}
		else if(n_meters > 5000) {
			set_properties(2, false, false, 0);
			set_iconproperties(40);
		}
		else if(n_meters > 3000) {
			set_properties(4, true, false, 0);
			set_iconproperties(50);
		}
		else if(n_meters > 2000) {
			set_properties(5, true, false, 6);
			set_iconproperties(70);
		}
		else if(n_meters > 1000) {
			set_properties(7, true, true, 9);
			set_iconproperties(80);
		}
		else if(n_meters > 500) {
			set_properties(9, true, true, 9);
			set_iconproperties(90);
		}
		else {
			set_properties(10, true, true, 10);
			set_iconproperties(100);
		}
		//get_pas().add_event("Iconsize at " + get_iconsize_percent() + "%");
	}
	
}