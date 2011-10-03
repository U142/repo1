package no.ums.pas.maps.defines;

import no.ums.pas.send.SendController;

import java.awt.Dimension;


public class InhabitantBasics
{
	public Inhabitant toInhabitant()
	{
		Inhabitant inhab = new Inhabitant();
		inhab.arrayindex = this.arrayindex;
		inhab.m_b_is_useredited = this.m_b_is_useredited;
		inhab.m_f_lat = this.m_f_lat;
		inhab.m_f_lon = this.m_f_lon;
		inhab.m_dim_screencoor = this.m_dim_screencoor;
		inhab.m_n_adrtype = this.m_n_adrtype;
		inhab.m_n_hasfixed = this.m_n_hasfixed;
		inhab.m_n_hasmobile = this.m_n_hasmobile;
		inhab.m_n_inhabitant_type = this.m_n_inhabitant_type;
		inhab.m_n_vulnerable = this.m_n_vulnerable;
		inhab.m_parent_house = this.m_parent_house;
		inhab.m_sz_kondmid = this.m_sz_kondmid;
		return inhab;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_sz_kondmid == null) ? 0 : m_sz_kondmid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InhabitantBasics other = (InhabitantBasics) obj;
		if (m_sz_kondmid == null) {
			if (other.m_sz_kondmid != null)
				return false;
		} else if (!m_sz_kondmid.equals(other.m_sz_kondmid))
			return false;
		return true;
	}
	public static final int INHABITANT_PRIVATE = 0;
	public static final int INHABITANT_COMPANY = 1;

	protected String m_sz_kondmid;
	protected double m_f_lon = 0;
	protected double m_f_lat = 0;
	protected int m_n_hasfixed = 0;
	protected int m_n_hasmobile = 0;
	protected int m_n_inhabitant_type; //bedrift
	protected int arrayindex;
	protected int m_n_vulnerable = 0;
	HouseItem m_parent_house;
	private boolean m_b_included = true; //For GIS-import manual edit and namefilter
	public void set_included(boolean b) { m_b_included = b; }
	public boolean get_included() { return m_b_included; }


	public String get_kondmid() { return m_sz_kondmid; }
	public int get_hasfixed() { return m_n_hasfixed; }
	public int get_hasmobile() { return m_n_hasmobile; }
	public boolean hasfixed() { return (get_hasfixed()==1 ? true : false); }
	public boolean hasmobile() { return (get_hasmobile()==1 ? true : false); }
	public void set_kondmid(String s) { m_sz_kondmid = s; }
	public boolean isVulnerable() { return m_n_vulnerable > 0;}
	public int getVulnerable() { return m_n_vulnerable; }

	public double get_lon() { return m_f_lon; }
	public double get_lat() { return m_f_lat; }
	public void set_lon(double d) { m_f_lon = d; }
	public void set_lat(double d) { m_f_lat = d; }
	public void set_parenthouse(HouseItem parent) { m_parent_house = parent; }
	public HouseItem get_parenthouse() { return m_parent_house; }
	public int get_inhabitanttype() { return m_n_inhabitant_type; }
	public boolean bedrift() { return (get_inhabitanttype()==1 ? true : false);  }
	public int get_adrtype() { return m_n_adrtype; }
	protected int m_n_adrtype;
	protected boolean m_b_is_useredited = false;
	public void set_useredited() { m_b_is_useredited = true; }
	public boolean is_useredited() { return m_b_is_useredited; }

	public InhabitantBasics()
	{
		
	}
	public InhabitantBasics(String kondmid, double lon, double lat, int n_hasfixed, 
						int n_hasmobile, int bedrift, int arrayindex, int n_vulnerable)
	{
		m_sz_kondmid = kondmid;
		m_f_lon = lon;
		m_f_lat = lat;
		m_n_hasfixed = n_hasfixed;
		m_n_hasmobile = n_hasmobile;
		m_n_adrtype = bedrift;
		m_n_vulnerable = n_vulnerable;
		if(bedrift==10 || bedrift==12) //manual address (private/mobile)
			m_n_inhabitant_type = INHABITANT_PRIVATE;
		else if(bedrift==11) //manual address (company)
			m_n_inhabitant_type = INHABITANT_COMPANY;
		else
			m_n_inhabitant_type = bedrift;

		this.arrayindex = arrayindex;
		m_n_adrtype = 0;
		if(bedrift==0 && n_hasfixed==1) m_n_adrtype |= SendController.SENDTO_FIXED_PRIVATE;
		if(bedrift==0 && n_hasmobile==1) m_n_adrtype |= SendController.SENDTO_MOBILE_PRIVATE;
		if(bedrift==1 && n_hasfixed==1) m_n_adrtype |= SendController.SENDTO_FIXED_COMPANY;
		if(bedrift==1 && n_hasmobile==1) m_n_adrtype |= SendController.SENDTO_MOBILE_COMPANY;
		if(bedrift==0 && (n_hasfixed==0 && n_hasmobile==0)) m_n_adrtype = SendController.SENDTO_NOPHONE_PRIVATE;
		if(bedrift==1 && (n_hasfixed==0 && n_hasmobile==0)) m_n_adrtype = SendController.SENDTO_NOPHONE_COMPANY;
		if(bedrift==10 || bedrift==12) m_n_adrtype = SendController.SENDTO_MOVED_RECIPIENT_PRIVATE;
		if(bedrift==11) m_n_adrtype = SendController.SENDTO_MOVED_RECIPIENT_COMPANY;

	}
	Dimension m_dim_screencoor;
	public Dimension get_dim_screencoor() { return m_dim_screencoor; }
	public void set_dim_screencoor(Dimension dim) { m_dim_screencoor = dim; }

}