package no.ums.pas.maps.defines;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.ums.tools.Timeout;

import javax.annotation.Nonnull;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;

//import Core.Controllers.*;

/*
 * Houses
 * Contains a list of HouseItem. Parses an inhabitantlist to make a distinct list of coordinates.
 */
public class Houses {

    private static final Log log = UmsLog.getLogger(Houses.class);

	private NavStruct m_bounds = null;
	private ArrayList<HouseItem> m_houses = null;
	private ArrayList<HouseItem> m_houses_paint = null;
	final private LonLatComparator m_comp_lonlat = new LonLatComparator();
	private boolean m_b_housesready;
	public NavStruct get_bounds() { return m_bounds; } 
	
	public ArrayList<HouseItem> get_houses() { return m_houses; }
	public ArrayList<HouseItem> get_paint_houses() { return m_houses_paint; }
	protected NavStruct m_fullbounds = null;
	
	public NavStruct getFullBBox()
	{
		return m_fullbounds;
	}
	
	protected boolean b_joinhouses = false;
	protected int nJoinHousesPixelLimit = 20;
	public void setJoinHouses(boolean bJoin, int pixelLimit)
	{
		b_joinhouses = bJoin;
		nJoinHousesPixelLimit = pixelLimit;
	}
	
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
	}
	
	public boolean is_housesready() { return m_b_housesready; }
	
	public class StatusComparator implements Comparator<Object>
	{
		public final int compare ( Object a, Object b ) /*objects of type StatusItemObject*/
		{
            if (a == b) {
                return 0;
            }
            else if (a == null) {
                return 1;
            }
            else if (b == null) {
                return -1;
            }
            StatusItemObject obj1 = (StatusItemObject)a;
            StatusItemObject obj2 = (StatusItemObject)b;
            return ComparisonChain
            		.start()
            		.compare(obj1.get_lat(), obj2.get_lat())
            		.compare(obj1.get_lon(), obj2.get_lon())
            		.compare(obj1.get_refno() + "_" + obj1.get_item(), obj2.get_refno() + "_" + obj2.get_item())
            		.result();
		}
	}
	
	public class LonLatComparator implements Comparator<Object>
	{
		public final int compare ( Object a, Object b ) /*objects of type StatusItemObject*/
		{
            if (a == b) {
                return 0;
            }
            else if (a == null) {
                return 1;
            }
            else if (b == null) {
                return -1;
            }

            Inhabitant obj1 = (Inhabitant)a;
            Inhabitant obj2 = (Inhabitant)b;
            return ComparisonChain
                    .start()
                    .compare(obj1.get_lat(), obj2.get_lat())
                    .compare(obj1.get_lon(), obj2.get_lon())
                    .compare(obj1.get_kondmid(), obj2.get_kondmid())
                    .result();
		}
	}
	public class Compare_lon implements Comparator<Object>
	{
		public final int compare( Object a, Object b)
		{
			InhabitantBasics obj1 = (InhabitantBasics)a;
			InhabitantBasics obj2 = (InhabitantBasics)b;
			if(obj1==null || obj2==null)
				return 0;
			if(obj1.get_lon() > obj2.get_lon())
				return 1;
			else if(obj1.get_lon() == obj2.get_lon())
				return 0;
			else
				return -1;			
		}
		
	}
	
	private void add_house(@Nonnull HouseItem house)
	{
		m_houses.add(Preconditions.checkNotNull(house, "house cannot be null"));
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
	
	protected Inhabitant get_min_valid_coor_index(Iterable<Inhabitant> inhabitants) {
		Iterator<Inhabitant> it = inhabitants.iterator();
		return it.hasNext() ? it.next() : null;
	}
	
	public NavStruct calc_bbox_by_stddev(SortedSet<Inhabitant> l)
	{
		if(l.size()==0)
		{
			return new NavStruct();
		}
		else if(l.size()==1) //make a point navstruct
		{
			return new NavStruct(l.first().m_f_lon, l.first().m_f_lon, l.first().m_f_lat, l.first().m_f_lat);
		}
		
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
		int i = 0;
		
		for(InhabitantBasics b : l)
		{
			//to solve map zoom out issue, ignore recipients with 0 lat,lon value while calculating zoom level
			if(b.get_lat()==0 || b.get_lon()==0)
				continue;
			sum_lat += b.get_lat();
			sum_lon += b.get_lon();
			++i;
		}
		
		mean_lat = sum_lat/i;
		mean_lon = sum_lon/i;
		
		int factor = 300;
		
		for(InhabitantBasics b : l)
		{
			if(b.get_lat()==0 || b.get_lon()==0)
				continue;
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
	
	AtomicBoolean sortReady = new AtomicBoolean(true);
	
	/*
	 * Parse all statusitems and create distinct houseitems. 
	 * needs only to be done at every statusload/update
	 */
	public void sort_houses(SortedSet<Inhabitant> inhabitants, boolean b_statusmode)
	{
		if(!sortReady.compareAndSet(true, false))
		{
			System.out.println("previous sort_houses not ready yet");
			return;
		}
		if(inhabitants==null) {
			m_b_housesready = true;
			sortReady.set(true);
			return;
		}
		m_houses = new ArrayList<HouseItem>();
		if(inhabitants.size() <= 0) {
			m_b_housesready = true;
			sortReady.set(true);
			return;
		}
		try
		{
			double f_prev_lon = 0.0;
			double f_prev_lat = 0.0;
			HouseItem prev_item = null;
			InhabitantBasics current;
	        for (InhabitantBasics inhabitant : inhabitants) {
	            current = (InhabitantBasics) inhabitant;
	
	            /* lon and/or lat is different, this is a new house
	                * add the house to the list, and add current inhabitant
	                * */
	            if(current==null)
	            {
	            	log.error("current==null");
	            	continue;
	            }
	            if (f_prev_lon != current.get_lon() || f_prev_lat != current.get_lat()) {
	                HouseItem item = new HouseItem(current.get_lon(), current.get_lat());
	                item.add_itemtohouse(current, b_statusmode);
	                current.set_parenthouse(item);
	                add_house(item);
	                prev_item = item;
	                item.HAS_INHABITANT_TYPES_ |= current.get_adrtype();
	
	            }
	            /* lon and lat is equal, add a new inhabitant to the house*/
	            else {
	                if (prev_item != null) {
	                    prev_item.add_itemtohouse(current, b_statusmode);
	                    current.set_parenthouse(prev_item);
	                    prev_item.HAS_INHABITANT_TYPES_ |= current.get_adrtype();
	                }
	            }
	            f_prev_lon = current.get_lon();
	            f_prev_lat = current.get_lat();
	        }
			m_bounds = calc_bbox_by_stddev(inhabitants);
			m_b_housesready = true;
		}
		finally
		{
			sortReady.set(true);
		}
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
			
			if (current == null) {
                log.error("Encountered null house at index "+i, new Exception("Test stack trace"));
                continue;
            }
			final Point topLeft = Variables.getMapFrame().getZoomLookup().getPoint(Variables.getMapFrame().getMapModel().getTopLeft());

			Point point = Variables.getMapFrame().getZoomLookup().getPoint(new LonLat(current.get_lon(), current.get_lat()));
			Dimension p = new Dimension(point.x - topLeft.x, point.y - topLeft.y);
			current.set_screencoords(p);
			if(current.get_screencoords().width < -nJoinHousesPixelLimit*3 ||
				current.get_screencoords().width > nJoinHousesPixelLimit*3+Variables.getMapFrame().getWidth() ||
				current.get_screencoords().height < -nJoinHousesPixelLimit*3 ||
				current.get_screencoords().height > nJoinHousesPixelLimit*3+Variables.getMapFrame().getHeight())
			{
				current.set_visible(false);
				continue;
			}
			current.set_visible(true);
			
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
					int threshold = nJoinHousesPixelLimit;
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
			else
				m_houses_paint = m_houses;
		}		
		//m_houses = m_houses_paint;
	}
	public void draw_houses(Graphics gfx, int n_alertborder, boolean b_border, boolean b_showtext, int n_fontsize,
					int n_addresstypes, Color col_override) {
		ArrayList<HouseItem> arraylist = get_houses();
		if(b_joinhouses)
			arraylist = get_paint_houses();
		
		if(arraylist==null)
			return;

		HouseItem current;
		int n_houses = 0;
		for(int i=0; i < arraylist.size(); i++)
		{
			current = (HouseItem)arraylist.get(i);
			
			//check visible statuscodes
			if(current.get_screencoords() != null && current.isVisible())// && (current.HAS_INHABITANT_TYPES_ & n_addresstypes)>0)
			{
				drawItem(current, gfx, Math.min(15, PAS.get_pas().get_mapproperties().get_pixradius() + (int)(1+current.getJoinedHouses()*0.1)), b_border, n_alertborder, 
						 b_showtext, n_fontsize, n_addresstypes, col_override, current.HasVulnerable() ? Color.red : Color.black, current.HasVulnerable() ? 2 : 1);
				n_houses++;
			}
		}
	}
	public void drawItem(HouseItem house, Graphics gfx, int n_size, boolean b_border, int n_alertborder, boolean b_showtext,
						 int n_fontsize, int n_addresstypes, Color col_override, Color col_border_override, int borderWidth)
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
			{
				gfx.setColor(col_border_override);
				gfx.drawRect(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
			}
		}
		else
		{
			gfx.fillOval(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
			gfx.setColor(Color.black);
			Graphics2D gfx2d = (Graphics2D)gfx;
			Stroke revertStroke = gfx2d.getStroke();
			Stroke singleStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			Stroke dualstroke = new BasicStroke(borderWidth+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			if(house.get_armed()) {				
				gfx2d.setStroke(dualstroke);
			}
			else
			{
				gfx2d.setStroke(singleStroke);
			}
			if(b_border)
			{
				gfx.setColor(col_border_override);
				gfx.drawOval(house.get_screencoords().width - n_border/2, house.get_screencoords().height - n_border/2, n_border, n_border);
			}
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
