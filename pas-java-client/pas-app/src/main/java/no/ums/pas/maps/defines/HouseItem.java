package no.ums.pas.maps.defines;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.controllers.Controller;
import no.ums.pas.core.defines.ComboRow;
import no.ums.pas.send.SendController;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.status.StatusSending;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;


/*
 * HouseItem
 * A list of HouseItem is contained in the class Houses. HouseItem represents a coordinate on map
 */
public class HouseItem extends Object {
	public static final int HOUSE_USEREDIT_NO_		= 1;
	public static final int HOUSE_USEREDIT_			= 2;
	public static final int HOUSE_USEREDIT_PARTIAL_ = 4;
	public static final int HOUSE_NO_INHAB_			= 8;
	public static final int HOUSE_USEREDIT_COOR_	= 16;
	
	double m_f_lon, m_f_lat;
	Dimension m_dim_screencoords;
	/*private String m_sz_gabstreet;
	private String m_sz_gabhouse;
	private String m_sz_gabregion;
	private int m_n_distance = 0;
	public void set_gabstreet(String s) { m_sz_gabstreet = s; }
	public void set_gabhouse(String s) { m_sz_gabhouse = s; }
	public void set_gabregion(String s) { m_sz_gabregion = s; }
	public void set_distance(int n) { m_n_distance = n; }
	public String get_gabstreet() { return m_sz_gabstreet; }
	public String get_gabhouse() { return m_sz_gabhouse; }
	public String get_gabregion() { return m_sz_gabregion; }
	public int get_distance() { return m_n_distance; }*/
	
	boolean m_b_visible = false;
	Color m_active_color;
	int m_n_current_visible_inhab = -1;
	boolean m_b_alert = false;
	boolean m_b_isselected = false;
	boolean m_b_isarmed = false;
	public int HAS_INHABITANT_TYPES_ = 0;
	
	protected int m_n_joined_houses = 0;
	public void addJoinedHouse()
	{
		m_n_joined_houses++;
	}
	public int getJoinedHouses()
	{
		return m_n_joined_houses;
	}

	
	public void set_screencoords(Dimension dim) {
		m_dim_screencoords = dim;
	}
	
	public void modifyScreenCoorsForJoinedHouses(Dimension dim) {
		Dimension delta = dim;
		delta.width -= m_dim_screencoords.width;
		delta.height -= m_dim_screencoords.height;
		delta.width /= (getJoinedHouses()+1);
		delta.height /= (getJoinedHouses()+1);
		m_dim_screencoords.width += delta.width;
		m_dim_screencoords.height += delta.height;
	}
	
	public Dimension get_screencoords() { return m_dim_screencoords; }
	
	public HouseItem(double f_lon, double f_lat)
	{
		m_f_lon = f_lon;
		m_f_lat = f_lat;
		m_subitems = new ArrayList<InhabitantBasics>();
		Color tempCol=new Color(1f,1f,1f,Variables.getAlpha());
//		set_active_color(Color.WHITE);
		set_active_color(tempCol);
	}
	public void set_visible(boolean b_visible) { m_b_visible = b_visible; }
	public boolean isVisible() {
		boolean ret = m_b_visible;
		try
		{
			int filter_refno = ((StatusSending)((ComboRow)PAS.get_pas().get_eastcontent().get_statuspanel().get_combo_filter().getSelectedItem()).getId()).get_refno();
			for(int i=0;i<m_subitems.size();++i)  {
				if(m_subitems.get(i) instanceof StatusItemObject)
				{
					if(filter_refno == ((StatusItemObject)m_subitems.get(i)).get_refno())
					{
						ret = true && m_b_visible;
						return ret;
					}
				}
				else
					return m_b_visible;
			}
			ret = false;
		}
		catch(Exception e)
		{
			
		}
		
		return ret; 
	}
	
	public boolean get_visible() { 
		int filter_refno = -1;
		
		try {
			filter_refno = ((StatusSending)((ComboRow)PAS.get_pas().get_eastcontent().get_statuspanel().get_combo_filter().getSelectedItem()).getId()).get_refno(); 
		} catch (Exception e) { return m_b_visible; } 
		boolean exists = false;
		for(int i=0;i<m_subitems.size();++i)  {
			if(m_subitems.get(i).getClass() == StatusItemObject.class) {
				if(filter_refno == ((StatusItemObject)m_subitems.get(i)).get_refno())
					exists = true;
			}
			else
				return m_b_visible;
		}
		if(exists) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isVisible(Navigation n)
	{
		if(get_lon() >= n.get_lbo() && get_lon() <= n.get_rbo() &&
			get_lat() >= n.get_bbo() && get_lat() <= n.get_ubo())
			return true;
		return false;
	}
	
	public void set_active_color(Color col) { m_active_color = col; }
	Color get_active_color() { return m_active_color; }
	public void reset_current_visible_inhab() { m_n_current_visible_inhab = 0; }
	public void inc_current_visible_inhab(int n_occurances) { m_n_current_visible_inhab += n_occurances; }
	public int get_current_visible_inhab() { return m_n_current_visible_inhab; }
	public void set_alert(boolean b_active) { m_b_alert = b_active; }
	public boolean get_alert() { return m_b_alert; }
	public boolean get_isselected() { return m_b_isselected; }
	public void set_selected(boolean b_selected) { m_b_isselected = b_selected; } 
	public void set_armed(boolean b_armed) { m_b_isarmed = b_armed; }
	public boolean get_armed() { return m_b_isarmed; }
	private int m_n_private_adr = 0;
	private int m_n_company_adr = 0;
	private int m_n_private_mobile_adr = 0;
	private int m_n_company_mobile_adr = 0;
	private int m_n_private_nophone_adr = 0;
	private int m_n_company_nophone_adr = 0;
	private int m_n_private_adr_moved = 0;
	private int m_n_company_adr_moved = 0;
	//private int m_n_mobile_adr_moved = 0;
	private int m_n_fax_adr = 0;
//	private int m_n_visible_adr = 0;
	private int m_n_useredited_adr = 0;
	private int m_n_vulnerable = 0;
	
	/***
	 * @return true if at least one citizen is marked as vulnerable
	 */
	public boolean HasVulnerable()
	{
		return m_n_vulnerable>0;
	}
	
	public int is_useredited() {
		if(m_n_useredited_adr==0)
			return HOUSE_USEREDIT_NO_;
		else if(m_n_useredited_adr > 0 && m_n_useredited_adr < get_inhabitantcount())
			return HOUSE_USEREDIT_PARTIAL_;
		else if(m_n_useredited_adr > 0)
			return HOUSE_USEREDIT_;
		return HOUSE_NO_INHAB_;
	}
	
	public boolean remove_inhabitant(Inhabitant inhab) {
		if(get_inhabitants().remove(inhab)) {
			if((inhab.get_adrtype() & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE) m_n_private_adr--;
			else if((inhab.get_adrtype() & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY) m_n_company_adr--;
			else if((inhab.get_adrtype() & SendController.SENDTO_MOBILE_PRIVATE) == SendController.SENDTO_MOBILE_PRIVATE) m_n_private_mobile_adr--;
			else if((inhab.get_adrtype() & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY) m_n_company_mobile_adr--;
			//else if((inhab.get_adrtype() & HouseController.ADR_TYPES_FAX_) == HouseController.ADR_TYPES_FAX_) m_n_fax_adr--;
			else if((inhab.get_adrtype() & SendController.SENDTO_MOVED_RECIPIENT_PRIVATE) == SendController.SENDTO_MOVED_RECIPIENT_PRIVATE) m_n_private_adr_moved--;
			else if((inhab.get_adrtype() & SendController.SENDTO_MOVED_RECIPIENT_COMPANY) == SendController.SENDTO_MOVED_RECIPIENT_COMPANY) m_n_company_adr_moved--;
			else if((inhab.get_adrtype() & SendController.SENDTO_NOPHONE_PRIVATE) == SendController.SENDTO_NOPHONE_PRIVATE) m_n_private_nophone_adr--;
			else if((inhab.get_adrtype() & SendController.SENDTO_NOPHONE_COMPANY) == SendController.SENDTO_NOPHONE_COMPANY) m_n_company_nophone_adr--;
			//else if((inhab.get_adrtype() & HouseController.ADR_TYPES_COMPANY_MOVED_) == HouseController.ADR_TYPES_COMPANY_MOVED_) m_n_company_adr_moved--;
			//else if((inhab.get_adrtype() & HouseController.ADR_TYPES_MOBILE_MOVED_) == HouseController.ADR_TYPES_MOBILE_MOVED_) m_n_mobile_adr_moved--;
			if(inhab.is_useredited())
				m_n_useredited_adr--;
			return true;
		}
		return false;
	}
	
	public int get_inhabitantcount(int n_adrtype) {
		int ret = 0;
		switch(n_adrtype) {
			case SendController.SENDTO_FIXED_PRIVATE:
			case SendController.SENDTO_FIXED_PRIVATE_ALT_SMS:
			case SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE:
				ret = m_n_private_adr;
				break;
			case SendController.SENDTO_FIXED_COMPANY:
			case SendController.SENDTO_FIXED_COMPANY_ALT_SMS:
			case SendController.SENDTO_FIXED_COMPANY_AND_MOBILE:
				ret = m_n_company_adr;
				break;
			case SendController.SENDTO_MOBILE_PRIVATE:
			case SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED:
				ret = m_n_private_mobile_adr;
				break;
			case SendController.SENDTO_MOBILE_COMPANY:
			case SendController.SENDTO_MOBILE_COMPANY_AND_FIXED:
				ret = m_n_company_mobile_adr;
				break;
			case SendController.SENDTO_NOPHONE_PRIVATE:
				ret = m_n_private_nophone_adr;
				break;
			case SendController.SENDTO_NOPHONE_COMPANY:
				ret = m_n_company_nophone_adr;
				break;
			/*case Controller.ADR_TYPES_FAX_:
				ret = m_n_fax_adr;
				break;*/
			case Controller.ADR_TYPES_SHOW_ALL_:
				ret = m_n_private_adr + m_n_company_adr + m_n_private_mobile_adr + m_n_company_mobile_adr + m_n_fax_adr + m_n_private_nophone_adr + m_n_company_nophone_adr;
				break;
		}
		return ret;
	}
	
	public double get_lon() { return m_f_lon; }
	public double get_lat() { return m_f_lat; }

	ArrayList<InhabitantBasics> m_subitems;
	public ArrayList<InhabitantBasics> get_inhabitants() { return m_subitems; }
	public int get_inhabitantcount() {
		return get_inhabitants().size(); //m_n_private_adr + m_n_company_adr + m_n_private_mobile_adr + m_n_company_mobile_adr; //+ m_n_private_nophone_adr + m_n_company_nophone_adr; //m_subitems.size();
	}
	
	public boolean find_inhabitant(Inhabitant inhab) {
		Inhabitant current;
		for(int i=0; i < get_inhabitants().size(); i++) {
			current = (Inhabitant)get_inhabitants().get(i);
			if(inhab.equals(current))
				return true;
		}
		return false;
	}
	
	void add_itemtohouse(InhabitantBasics item, boolean b_statusmode /*no mobile count*/) { 
		m_subitems.add(item);
		/*if((item.get_adrtype() & HouseController.ADR_TYPES_PRIVATE_) == HouseController.ADR_TYPES_PRIVATE_) m_n_private_adr++;
		else if((item.get_adrtype() & HouseController.ADR_TYPES_COMPANY_) == HouseController.ADR_TYPES_COMPANY_) m_n_company_adr++;
		else if((item.get_adrtype() & HouseController.ADR_TYPES_MOBILE_) == HouseController.ADR_TYPES_MOBILE_) m_n_mobile_adr++;
		else if((item.get_adrtype() & HouseController.ADR_TYPES_FAX_) == HouseController.ADR_TYPES_FAX_) m_n_fax_adr++;
		else if((item.get_adrtype() & HouseController.ADR_TYPES_PRIVATE_MOVED_) == HouseController.ADR_TYPES_PRIVATE_MOVED_) m_n_private_adr_moved++;
		else if((item.get_adrtype() & HouseController.ADR_TYPES_COMPANY_MOVED_) == HouseController.ADR_TYPES_COMPANY_MOVED_) m_n_company_adr_moved++;
		else if((item.get_adrtype() & HouseController.ADR_TYPES_MOBILE_MOVED_) == HouseController.ADR_TYPES_MOBILE_MOVED_) m_n_mobile_adr_moved++;
		*/
		if((item.get_adrtype() & SendController.SENDTO_FIXED_PRIVATE)==SendController.SENDTO_FIXED_PRIVATE) m_n_private_adr++;
		if((item.get_adrtype() & SendController.SENDTO_FIXED_COMPANY)==SendController.SENDTO_FIXED_COMPANY) m_n_company_adr++;
		if(!b_statusmode) {
			if((item.get_adrtype() & SendController.SENDTO_MOBILE_PRIVATE)==SendController.SENDTO_MOBILE_PRIVATE) m_n_private_mobile_adr++;
			if((item.get_adrtype() & SendController.SENDTO_MOBILE_COMPANY)==SendController.SENDTO_MOBILE_COMPANY) m_n_company_mobile_adr++;
			if((item.get_adrtype() & SendController.SENDTO_MOVED_RECIPIENT_PRIVATE)==SendController.SENDTO_MOVED_RECIPIENT_PRIVATE) m_n_private_adr_moved++;
			if((item.get_adrtype() & SendController.SENDTO_MOVED_RECIPIENT_COMPANY)==SendController.SENDTO_MOVED_RECIPIENT_COMPANY) m_n_company_adr_moved++;
			if((item.get_adrtype() & SendController.SENDTO_NOPHONE_PRIVATE)==SendController.SENDTO_NOPHONE_PRIVATE) m_n_private_nophone_adr++;
			if((item.get_adrtype() & SendController.SENDTO_NOPHONE_COMPANY)==SendController.SENDTO_NOPHONE_COMPANY) m_n_company_nophone_adr++;
			/*if((item.get_adrtype() & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) == SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) m_n_private_adr++;
			if((item.get_adrtype() & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) == SendController.SENDTO_FIXED_COMPANY_ALT_SMS) m_n_company_adr++;
			if((item.get_adrtype() & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) == SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) m_n_private_adr++;
			if((item.get_adrtype() & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) == SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) m_n_company_adr++;
			if((item.get_adrtype() & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) == SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) m_n_private_mobile_adr++;
			if((item.get_adrtype() & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) == SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) m_n_company_mobile_adr++;
			*/
		}

		if(item.is_useredited())
			m_n_useredited_adr++;
		if(item.isVulnerable())
			++m_n_vulnerable;
	}
	
	//public int get_visible_adrcount() { return (m_n_visible_adr = m_n_private_adr + m_n_company_adr + m_n_mobile_adr + m_n_fax_adr); }
	public int get_visible_adrcount(int n_showadrtypes) {
		return (
				((n_showadrtypes & SendController.SENDTO_FIXED_PRIVATE) == SendController.SENDTO_FIXED_PRIVATE ? m_n_private_adr : 0) + 
				((n_showadrtypes & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY ? m_n_company_adr : 0) +
				((n_showadrtypes & SendController.SENDTO_MOBILE_PRIVATE) == SendController.SENDTO_MOBILE_PRIVATE ? m_n_private_mobile_adr : 0) +
				((n_showadrtypes & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY ? m_n_company_mobile_adr : 0) +
				((n_showadrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) == SendController.SENDTO_FIXED_PRIVATE_ALT_SMS ? m_n_private_adr + m_n_private_mobile_adr : 0) +
				((n_showadrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) == SendController.SENDTO_FIXED_COMPANY_ALT_SMS ? m_n_company_adr + m_n_company_mobile_adr: 0) +
				((n_showadrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) == SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE ? m_n_private_adr + m_n_private_mobile_adr: 0) +
				((n_showadrtypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) == SendController.SENDTO_FIXED_COMPANY_AND_MOBILE ? m_n_company_adr + m_n_company_mobile_adr: 0) +
				((n_showadrtypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) == SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED ? m_n_private_mobile_adr + m_n_private_adr: 0) +
				((n_showadrtypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) == SendController.SENDTO_MOBILE_COMPANY_AND_FIXED ? m_n_company_mobile_adr + m_n_company_adr: 0)
				
				// +
				//((n_showadrtypes & SendController.SENDTO_NOPHONE_PRIVATE) == SendController.SENDTO_NOPHONE_PRIVATE ? m_n_private_nophone_adr : 0) +
				//((n_showadrtypes & SendController.SENDTO_NOPHONE_COMPANY) == SendController.SENDTO_NOPHONE_COMPANY ? m_n_company_nophone_adr : 0)
				//((n_showadrtypes & HouseController.ADR_TYPES_FAX_) == HouseController.ADR_TYPES_FAX_ ? m_n_fax_adr : 0)
				);
	}
	public Inhabitant get_itemfromhouse(int n_index) { 
		try {
			return (Inhabitant)m_subitems.get(n_index);
		} catch(Exception e) { return null; }	
	}
	public void update_color(int n_code, Color col)
	{
		StatusItemObject current;
		for(int i=0; i < m_subitems.size(); i++)
		{
			current = (StatusItemObject)m_subitems.get(i);
			if(current.get_status() == n_code)
			{
				current.get_parenthouse().set_active_color(col);
				break;
			}
		}
	}
	public int find_statuscode(int n_code)
	{
		int n_ret = 0;
		for(int i=0; i < m_subitems.size(); i++)
		{
			if(((StatusItemObject)m_subitems.get(i)).get_status() == n_code)
				n_ret++;
				//return true; //statuscode is present
		}
		return n_ret;
	}

    @Override
    public int hashCode() {
        int result = 0;
        final int prime = 31;
        result = prime * Double.valueOf(this.get_lat()).hashCode();
        result += prime * Double.valueOf(this.get_lon()).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof HouseItem)) {
            return false;
        } else {
            HouseItem houseItem = (HouseItem) obj;
            return (this.get_lat() == houseItem.get_lat() && this.get_lon() == houseItem.get_lon());
        }


    }
}