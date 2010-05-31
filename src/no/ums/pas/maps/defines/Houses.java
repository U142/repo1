package no.ums.pas.maps.defines;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import no.ums.pas.PAS;
import no.ums.pas.ums.tools.Timeout;

//import Core.Controllers.*;

/*
 * Houses
 * Contains a list of HouseItem. Parses an inhabitantlist to make a distinct list of coordinates.
 */
public class Houses {
	private NavStruct m_bounds = null;
	private ArrayList<HouseItem> m_houses = null;
	private ArrayList<HouseItem> m_houses_paint = null;
	private Compare_lat m_comp_lat;
	private Compare_lon m_comp_lon;
	//private Controller m_controller;
	private boolean m_b_housesready;
//	private void set_housesready(boolean b_ready) { m_b_housesready = b_ready; }
	public NavStruct get_bounds() { return m_bounds; } 
	
	//private Controller get_controller() { return m_controller; }
	public ArrayList<HouseItem> get_houses() { return m_houses; }
	public ArrayList<HouseItem> get_paint_houses() { return m_houses_paint; }
	protected NavStruct m_fullbounds = null;
	
	public NavStruct getFullBBox()
	{
		return m_fullbounds;
	}
	
	protected boolean b_joinhouses = false;
	
	public int size() {
		if(get_houses()!=null)
			return get_houses().size();
		else
			return 0;
	}
	
	public Houses(boolean b_joinhouses)
	{
		this.b_joinhouses = b_joinhouses;
		m_b_housesready= false;
		//m_controller = controller;
		m_comp_lat = new Compare_lat();		
		m_comp_lon = new Compare_lon();
	}
	
	public boolean is_housesready() { return m_b_housesready; }
	
	public class Compare_lat implements Comparator<Object>
	{
		public final int compare ( Object a, Object b ) /*objects of type StatusItemObject*/
		{
			InhabitantBasics obj1 = (InhabitantBasics)a;
			InhabitantBasics obj2 = (InhabitantBasics)b;
			/*if(obj1.get_lat() > obj2.get_lat() || obj1.get_lon() > obj2.get_lon())
				return 1;
			else if(obj1.get_lat() == obj2.get_lat() && obj1.get_lon() == obj2.get_lon())
				return 0;
			else
				return -1;*/
			if(obj1.get_lat() > obj2.get_lat())
				return 1;
			else if(obj1.get_lat() == obj2.get_lat())
				return 0;
			else
				return -1;
		}
	}
	public class Compare_lon implements Comparator<Object>
	{
		public final int compare( Object a, Object b)
		{
			InhabitantBasics obj1 = (InhabitantBasics)a;
			InhabitantBasics obj2 = (InhabitantBasics)b;
			if(obj1.get_lon() > obj2.get_lon())
				return 1;
			else if(obj1.get_lon() == obj2.get_lon())
				return 0;
			else
				return -1;			
		}
		
	}
	
	private void add_house(HouseItem house)
	{
		m_houses.add(house);
	}
	public void update_statuscolor(int n_code, Color col)
	{
		HouseItem current;
		if(get_houses()==null)
			return;
		for(int i=0; i < get_houses().size(); i++)
		{
			current = (HouseItem)get_houses().get(i);
			current.update_color(n_code, col);
		}
	}
	public void set_visible(boolean b) {
		if(get_houses()==null)
			return;
		HouseItem current;
		for(int i=0; i < get_houses().size(); i++) {
			current = (HouseItem)get_houses().get(i);
			current.set_visible(b);
		}
	}
	
	public HouseItem find_inhabitants_house(Inhabitant inhab) {
		HouseItem house = null;
		for(int i=0; i < get_houses().size(); i++) {
			house = (HouseItem)get_houses().get(i);
			if(house.find_inhabitant(inhab))
				return house;
		}
		
		return null;
	}
	
	public boolean remove_inhabitant(Inhabitant inhab) {
		HouseItem house = find_inhabitants_house(inhab);
		if(house!=null) {
			/*house.get_inhabitants().remove(inhab);
			if(house.get_inhabitantcount() == 0) {
				
			}*/
			if(house.remove_inhabitant(inhab))
				return true;
		}
		return false;
	}
	
	protected int get_min_valid_coor_index(ArrayList<Object> inhabitants) {
		int ret = -1;
		for(int i=0; i < inhabitants.size(); i++) {
			InhabitantBasics in = (InhabitantBasics)inhabitants.get(i);
			if(in.get_lon()>0.0 && in.get_lat()>0.0) {
				ret = i;
				break;
			}
		}
		//if(i == inhabitants.size())
		//	return -1;
		return ret;
	}
	
	public NavStruct calc_bbox_by_stddev(ArrayList<Object> l)
	{
		if(l.size()<=2)
			return new NavStruct();
		
		//double mid_lat = 0, mid_lon = 0;
		double sum_lat = 0;
		double sum_lon = 0;
		double mean_lat = 0;
		double mean_lon = 0;
		double sum_deviations_lat = 0;
		double sum_deviations_lon = 0;
		double deviation_lat = 0;
		double deviation_lon = 0;
		NavStruct nav = new NavStruct();
		int i;
		
		for(i=0; i < l.size(); i++)
		{
			InhabitantBasics b = (InhabitantBasics)l.get(i);
			sum_lat += b.get_lat();
			sum_lon += b.get_lon();
		}
		mean_lat = sum_lat/i;
		mean_lon = sum_lon/i;
		
		int factor = 300;
		for(i=0; i < l.size(); i++)
		{
			InhabitantBasics b = (InhabitantBasics)l.get(i);
			sum_deviations_lat += Math.pow(mean_lat*factor - b.get_lat()*factor,2);
			sum_deviations_lon += Math.pow(mean_lon*factor - b.get_lon()*factor,2);
		}

		deviation_lat = sum_deviations_lat / (i-1);
		deviation_lon = sum_deviations_lon / (i-1);
		
		nav._lbo = mean_lon - deviation_lon/factor;
		nav._rbo = mean_lon + deviation_lon/factor;
		nav._bbo = mean_lat - deviation_lat/factor;
		nav._ubo = mean_lat + deviation_lat/factor;
		
		return nav;
	}
	
	/*
	 * Parse all statusitems and create distinct houseitems. 
	 * needs only to be done at every statusload/update
	 */
	public void sort_houses(ArrayList<Object> inhabitants, boolean b_statusmode)
	{
		//if(get_controller().get_items()==null)
		if(inhabitants==null) {
			m_b_housesready = true;
			return;
		}
		m_houses = new ArrayList<HouseItem>();
		if(inhabitants.size() <= 0) {
			m_b_housesready = true;
			return;
		}
		//Collections.sort(get_controller().get_items(), m_comp_lat);
		//Collections.sort(get_controller().get_items(), m_comp_lon);
		Collections.sort(inhabitants, m_comp_lat);
		Collections.sort(inhabitants, m_comp_lon);
		try {
			//Inhabitant inhab_ul = (Inhabitant)inhabitants.get(0);
			int idx = get_min_valid_coor_index(inhabitants);
			if(idx==-1) { //error
				m_bounds = null;
			} else {
				InhabitantBasics inhab_ul = (InhabitantBasics)inhabitants.get(idx);
				InhabitantBasics inhab_lr = (InhabitantBasics)inhabitants.get(inhabitants.size()-1);
				//m_bounds = new NavStruct(inhab_ul.get_lon()-0.05, inhab_lr.get_lon()+0.05, inhab_ul.get_lat()+0.05, inhab_lr.get_lat()-0.05);
				m_fullbounds = m_bounds;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		double f_prev_lon = 0.0;
		double f_prev_lat = 0.0;
		HouseItem prev_item = null;
		InhabitantBasics current;
		//for(int i=0; i < get_controller().get_items().size(); i++)
		for(int i=0; i < inhabitants.size(); i++)
		{
			//current = (Inhabitant)get_controller().get_items().get(i);
			current = (InhabitantBasics)inhabitants.get(i);
			
			/* lon and/or lat is different, this is a new house
			 * add the house to the list, and add current inhabitant
			 * */
			
			if(f_prev_lon != current.get_lon() || f_prev_lat != current.get_lat())
			{
				HouseItem item = new HouseItem(current.get_lon(), current.get_lat());
				item.add_itemtohouse(current, b_statusmode);
				current.set_parenthouse(item);
				add_house(item);
				prev_item = item;
				item.HAS_INHABITANT_TYPES_ |= current.get_adrtype();
				
			}
			/* lon and lat is equal, add a new inhabitant to the house*/
			else
			{
				if(prev_item!=null)
				{
					prev_item.add_itemtohouse(current, b_statusmode);
					current.set_parenthouse(prev_item);
					prev_item.HAS_INHABITANT_TYPES_ |= current.get_adrtype();
				}
			}
			f_prev_lon = current.get_lon();
			f_prev_lat = current.get_lat();
		}
		m_bounds = calc_bbox_by_stddev(inhabitants);
		//get_controller().get_pas().add_event("sort_houses: " + get_controller().get_items().size());
		m_b_housesready = true;
	}
	
	public void calcHouseCoords() {
		HouseItem current;
		m_houses_paint = new ArrayList<HouseItem>();
		Hashtable<Dimension, HouseItem> hash_points = new Hashtable<Dimension, HouseItem>();
		if(get_houses()==null)
			return;
		for(int i=0; i < get_houses().size(); i++)
		{
			current = get_houses().get(i);
			Dimension p = PAS.get_pas().get_navigation().coor_to_screen(current.get_lon(), current.get_lat(), true);
			current.set_screencoords(p);
			
			if(b_joinhouses)
			{
				if(p==null)
					continue;
				
				HouseItem houseitem = hash_points.get(p);
				if(houseitem!=null) //point is reserved
				{
					String s = "";
					for(int inhab=0; inhab < current.get_inhabitants().size(); inhab++)
					{
						InhabitantBasics obj_inhab = current.get_inhabitants().get(inhab);
						if(obj_inhab!=null)
							houseitem.add_itemtohouse(obj_inhab, false);
					}
					houseitem.addJoinedHouse();
					houseitem.modifyScreenCoorsForJoinedHouses(p);
				}
				else //point is free, make a reservation
				{
					HouseItem house_paint = new HouseItem(current.m_f_lon, current.m_f_lat);
					int threshold = 50;
					for(int x = p.width - threshold; x < p.width + threshold; x++)
					{
						for(int y = p.height - threshold; y < p.height + threshold; y++)
						{
							Dimension dim = new Dimension(x, y);
							hash_points.put(dim, house_paint);
							
						}
					}
					house_paint.set_screencoords(p);
					house_paint.set_visible(true);
					m_houses_paint.add(house_paint);
					for(int inhab=0; inhab < current.get_inhabitants().size(); inhab++)
					{
						InhabitantBasics obj_inhab = current.get_inhabitants().get(inhab);
						if(obj_inhab!=null)
							house_paint.add_itemtohouse(obj_inhab, false);
					}
					
				}
			}
		}		
	}
	public void draw_houses(Graphics gfx, int n_alertborder, boolean b_border, boolean b_showtext, int n_fontsize,
					int n_addresstypes, Color col_override) {
		Timeout time = new Timeout(1, 20);
		while(1==1)
		{
			if(is_housesready())
				break;
				//break;
			time.inc_timer();
			if(time.timer_exceeded())
				return;
			try { Thread.sleep(time.get_msec_interval()); } catch(InterruptedException e) { }
		}
		ArrayList<HouseItem> arraylist = get_houses();
		if(b_joinhouses)
			arraylist = get_paint_houses();
		
		if(arraylist==null)
			return;
		//if(get_controller().get_visibility_change())
		{
			//get_controller().set_visibility();
		}

		HouseItem current;
//		boolean b_show = false;
		int n_houses = 0;
		for(int i=0; i < arraylist.size(); i++)
		{
			current = (HouseItem)arraylist.get(i);
			//check visible statuscodes
			//PAS.get_pas().add_event(current.HAS_INHABITANT_TYPES_ + " & " + n_addresstypes + " = " + (current.HAS_INHABITANT_TYPES_ & n_addresstypes));
			if(current.get_screencoords() != null && current.get_visible())// && (current.HAS_INHABITANT_TYPES_ & n_addresstypes)>0)
			{
				//get_pas().add_event("drawItems: " + ((HouseItem)get_houses().get_houses().get(i)).get_screencoords().width);
				drawItem(current, gfx, Math.min(15, PAS.get_pas().get_mapproperties().get_pixradius() + (int)(1+current.getJoinedHouses()*0.1)), b_border, n_alertborder, 
						 b_showtext, n_fontsize, n_addresstypes, col_override);
				n_houses++;
				//n_totalvisiblehouses++;
				//n_totalvisiblestatusitems+=current.get_inhabitantcount();
			}
		}
	}
	public void drawItem(HouseItem house, Graphics gfx, int n_size, boolean b_border, int n_alertborder, boolean b_showtext,
						 int n_fontsize, int n_addresstypes, Color col_override)
	{
		int n_border = n_size;
		if(house.get_alert()) {
			n_border = n_size + n_alertborder;//house.get_itemfromhouse(0).get_status());
			b_border = true; //force borders on alert items
		}
		
		if(col_override!=null)
			gfx.setColor(col_override);
		else
			gfx.setColor(house.get_active_color());
		//gfx.fillOval(house.get_screencoords().width - n_size/2, house.get_screencoords().height - n_size/2, n_size, n_size);
		if(house.get_isselected())
		{
			gfx.fillRect(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
			gfx.setColor(Color.black);
			if(b_border)
				gfx.drawRect(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
		}
		else
		{
			gfx.fillOval(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
			gfx.setColor(Color.black);
			Graphics2D gfx2d = (Graphics2D)gfx;
			Stroke revertStroke = gfx2d.getStroke();
			Stroke dualstroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			if(house.get_armed()) {				
				gfx2d.setStroke(dualstroke);
			}
			if(b_border)
				gfx.drawOval(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
			if(house.get_armed()) {
			}
			n_border+=4;
			gfx.setColor(Color.red);
			gfx2d.setStroke(dualstroke);
			if(house.is_useredited()==HouseItem.HOUSE_USEREDIT_PARTIAL_) {
				gfx.drawLine(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, house.get_screencoords().width, house.get_screencoords().height);
			}
			if(house.is_useredited()==HouseItem.HOUSE_USEREDIT_) {
				gfx.drawLine(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, house.get_screencoords().width + n_border/2, house.get_screencoords().height + n_border/2);
			}
			gfx.setColor(Color.black);
			gfx2d.setStroke(revertStroke);
		}
		if(b_showtext)
		{
			gfx.setColor(Color.black);
			gfx.setFont(new Font(null, Font.PLAIN, n_fontsize));
			//FontSet gfx.setFont(new Font("Arial", Font.PLAIN, n_fontsize));
			/*if(house.get_current_visible_inhab() > -1)
				gfx.drawString(" " + house.get_current_visible_inhab() + "/" + house.get_inhabitantcount(), house.get_screencoords().width + n_size/2, house.get_screencoords().height + n_size/2);
			else
				gfx.drawString(" " + house.get_visible_adrcount(n_addresstypes) + "/" + house.get_inhabitantcount(), house.get_screencoords().width + n_size/2, house.get_screencoords().height + n_size/2);*/
			//if(n_size>5)
				gfx.drawString(" " + house.get_inhabitantcount(), house.get_screencoords().width + n_size/2, house.get_screencoords().height + n_size/2);
		}
	}
	
	
}
