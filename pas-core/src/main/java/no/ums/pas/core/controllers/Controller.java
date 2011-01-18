/*
 * 
 * @author Morten Helvig - Unified Messaging Systems AS
 * GPSController
 * 		* MapObjectList (extends ArrayList, contains an array of MapObject)
 * 		* MapObject (if dynamic, contains an array of GPSCoor order by date, time)
 * 		* GPSCoor (contains lon/lat/date/time/speed/course/sat/asl/batt
 *
 */

package no.ums.pas.core.controllers;

import no.ums.pas.PAS;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Houses;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.send.SendController;
import no.ums.pas.ums.tools.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;



public abstract class Controller implements ActionListener {
	
	/*public static final int ADR_TYPES_PRIVATE_ = 1;
	public static final int ADR_TYPES_COMPANY_ = 2;
	public static final int ADR_TYPES_MOBILE_  = 4;
	public static final int ADR_TYPES_FAX_     = 8;
	public static final int ADR_TYPES_PRIVATE_MOVED_ = 16;
	public static final int ADR_TYPES_COMPANY_MOVED_ = 32;
	public static final int ADR_TYPES_MOBILE_MOVED_  = 64;
	public static final int ADR_TYPES_SHOW_ALL_ = ADR_TYPES_PRIVATE_ | ADR_TYPES_COMPANY_ | ADR_TYPES_MOBILE_ | ADR_TYPES_FAX_;*/
	public static final int ADR_TYPES_SHOW_ALL_ = SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_MOBILE_PRIVATE | SendController.SENDTO_MOBILE_COMPANY | SendController.SENDTO_MOVED_RECIPIENT_PRIVATE | SendController.SENDTO_MOVED_RECIPIENT_COMPANY | SendController.SENDTO_NOPHONE_PRIVATE | SendController.SENDTO_NOPHONE_COMPANY;
	public int ADR_TYPES_SHOW_ = ADR_TYPES_SHOW_ALL_;
	public int get_addresstypes_bitwise() { return ADR_TYPES_SHOW_; }
	
	//PAS m_pas;
	private boolean m_b_auto_update = false;
	private int m_n_trail_minutes = 10;
	protected int m_n_autoupdate_seconds = 10;
	private boolean m_b_updates_in_progress = false;
	private long m_n_lastupdate = 0;
	private Calendar cal = Calendar.getInstance();
	private int m_n_filter_date = -2;
	private int m_n_filter_time = -2;
	private boolean m_b_pause = false;
	private boolean m_b_closed = false;
	
	public boolean isClosed() { return m_b_closed; }
	public void setClosed() { m_b_closed = true; }
	
	private boolean m_b_visibility_change = false;

	//public PAS get_pas() { return m_pas; }

	public boolean get_autoupdate() { return m_b_auto_update; }
	public int get_autoupdate_seconds() { return m_n_autoupdate_seconds; }
	public int get_autoupdate_millisecs() { return get_autoupdate_seconds() * 1000; }
	public void set_autoupdate(boolean b_auto) { m_b_auto_update = b_auto; }
	public void set_autoupdate_seconds(int n_seconds) { m_n_autoupdate_seconds = n_seconds; }
	public void set_updates_in_progress(boolean b_inprogress) { 
		m_b_updates_in_progress = b_inprogress;
	}
	public boolean get_updates_in_progress() { return m_b_updates_in_progress; }
	public synchronized void set_lastupdate() { m_n_lastupdate = Utils.get_now().getTimeInMillis(); }
	public long get_lastupdate() { return m_n_lastupdate; }
	public void set_timefilter(int n_date, int n_time) {
		m_n_filter_date = n_date;
		m_n_filter_time = n_time;
		//get_pas().add_event("GPSFilter: " + n_date + " / " + n_time);
	}
	public int get_filter_date() { return m_n_filter_date; }
	public int get_filter_time() { return m_n_filter_time; }
	ArrayList <Object>m_items = null;
	Houses m_houses = null;
	public Houses get_houses() { return m_houses; }
	public ArrayList<Object> get_items() { return m_items; }
	public boolean get_pause() { return m_b_pause; }
	public void set_pause(boolean pause) { m_b_pause = pause; }
	
	Controller() {
		cal.add(Calendar.DATE, -2);
		m_n_filter_date = new Integer(new java.text.SimpleDateFormat("yyyyMMdd").format(cal.getTime())).intValue();
		m_n_filter_time = new Integer(new java.text.SimpleDateFormat("HHmmss").format(cal.getTime())).intValue();;	
		
	}
	
	public boolean check_needupdate() {
		if(get_autoupdate())
		{
			long n_since_last = Utils.get_now().getTimeInMillis() - get_lastupdate();
			if(n_since_last > get_autoupdate_millisecs()) // && !m_b_pause)
			{
				if(!get_updates_in_progress()) {
					set_lastupdate();
					//get_pas().add_event("GPS: Autoupdate in progress");
					start_download(true);
					return true;
				}
			}
		}
		return false;
	}

	abstract void create_filter();
	
	public abstract void start_download(boolean b);
	public abstract void set_visibility();
	public boolean get_visibility_change() { return m_b_visibility_change; }
	void set_visibility_change(boolean b) { 
		m_b_visibility_change = b;
		//PAS.get_pas().kickRepaint();
	}
	ArrayList<Object> arr_found = new ArrayList<Object>();
	public ArrayList<Object> get_found() { return arr_found; }
	ArrayList<Object> arr_found_houses = new ArrayList<Object>();
	public ArrayList<Object> get_found_houses() { return arr_found_houses; }
	
	boolean b_draw_inhabitant_details = false;
	public boolean get_draw_inhabitant_details() { return b_draw_inhabitant_details; }
	private Dimension m_mouse;
	public synchronized void find_houses_bypix(Dimension dim) {
		m_mouse = dim;
		//arr_found.clear();
		ArrayList <Object>arr_temp = new ArrayList<Object>();
		ArrayList <Object>arr_temp_houses = new ArrayList<Object>();
		
		if(get_houses()!=null) {
			/*while(1==1) {
				if(get_houses().is_housesready())
					break;
			}*/
			//get_pas().get_drawthread().set_suspended(true);
			HouseItem current;
			int n_radius;
			int n_count = 0;
			int n_total_inhabitants = 0;
			Object [] data;
			for(int i=0; i < get_houses().size(); i++) {
				current = (HouseItem)get_houses().get_houses().get(i);
				
				//current.set_selected(false);
				if(current.get_screencoords() != null && current.get_visible()) {
					n_radius = PAS.get_pas().get_mapproperties().get_pixradius();
					if(dim.width >= (current.get_screencoords().width - n_radius) && dim.width <= (current.get_screencoords().width + n_radius) &&
					   dim.height >= (current.get_screencoords().height - n_radius) && dim.height <= (current.get_screencoords().height + n_radius)) { //user click inside house-area
						Inhabitant obj_inhab;
						current.set_armed(true);
						arr_temp_houses.add(current);
						for(int inhab=0; inhab < current.get_inhabitants().size(); inhab++) {
							obj_inhab = (Inhabitant)current.get_itemfromhouse(inhab);
							if(obj_inhab!=null)  {
								//if((ADR_TYPES_SHOW_ & obj_inhab.get_adrtype()) == obj_inhab.get_adrtype()) {
								//if((obj_inhab.get_adrtype() & ADR_TYPES_SHOW_) != 0) {
									if(!obj_inhab.get_number().equals("") || !obj_inhab.get_mobile().equals("")) {
										arr_temp.add(obj_inhab);
										n_count++;
									}
								//}
							}
						}
						//get_pas().add_event("found house with" + current.get_inhabitantcount() + " inhabitants");
						//if(!current.get_isselected())
						//	current.set_selected(true);
					}
					else
						current.set_armed(false);
				}
			}
			if(n_count > 0) {
				b_draw_inhabitant_details = true;
			}
			else
			{
				b_draw_inhabitant_details = false;
				PAS.get_pas().get_mappane().setToolTipText("", false);
			}
				
		}
		boolean b_needrepaint = false;
		if(arr_temp.equals(arr_found)) {
			//return;
		} else {
			arr_found = (ArrayList<Object>)arr_temp.clone();
			b_needrepaint = true;
		}
		if(arr_temp_houses.equals(arr_found_houses)) {
			//return;
		} else {
			arr_found_houses = (ArrayList<Object>)arr_temp_houses.clone();
			b_needrepaint = true;
		}
		
		if(b_needrepaint)
			PAS.get_pas().kickRepaint();
	}

	public void draw_details(Graphics g)
	{

		Inhabitant cur;
		String sz_showname, sz_showaddr;
		boolean b_showtooltip = false;
		String sz = "<html>";
		sz += "<table>";
		/*sz += "<tr style=\"font-size:6px; font-face:Arial;\">";
		sz += "<td></td><td style=\"width:80px;\"></td><td align=center style=\"width:60px;\"><img src=\"PASIcons/voice.gif\"></td><td align=center style=\"width:60px;\"><img src=\"PASIcons/gsm.gif\"></td>";
		sz += "</tr>";*/

		for(int i=0; i < arr_found.size(); i++) {
			b_showtooltip = true;
			cur = (Inhabitant)arr_found.get(i);
			//if((ADR_TYPES_SHOW_ & cur.get_adrtype()) == cur.get_adrtype()) {
				if(cur.get_adrname().length() < 25)
					sz_showname = cur.get_adrname();
				else
					sz_showname = cur.get_adrname().substring(0, 25) + "..";
				if(cur.get_postaddr().length() < 20)
					sz_showaddr = cur.get_postaddr();
				else
					sz_showaddr = cur.get_postaddr().substring(0, 20) + "..";
				
				//g.drawString(sz_showname + " " + cur.get_number(), p.x + n_xpad - n_compensate_left, p.y + (n_linedist - 2) + (n_lineno++ * n_linedist) - n_compensate_up);
				//n_x = p.x + n_xpad - n_compensate_left;
				//n_y = p.y + (n_linedist - 2) + (n_lineno++ * n_linedist) - n_compensate_up;
				/*g.drawString(sz_showname, n_x, n_y);
				g.drawString(sz_showaddr, n_x + 130, n_y);
				g.drawString(String.format("%1$-10s %2$-10s", cur.get_number(), cur.get_mobile()), n_x + 260, n_y);
				*/
				//this.setToolTipText("test");
				//tt.setTipText("Test");
				//tt.setBounds(p.x - n_compensate_left, p.y - n_compensate_up, quad.width, quad.height);
				//tt.setVisible(true);
				sz += "<tr style=\"font-size:9px; font-face:Arial;\">";
				sz += "<td><b>" + sz_showname + "</td><td style=\"width:90px;\">" + sz_showaddr + "</td><td align=center style=\"width:60px;\">" + cur.get_number() + "</td><td align=center style=\"width:60px;\">" + cur.get_mobile() + "</b></td>";
				sz += "</tr>";
			//}
		}
		sz += "</table>";
		sz+="</html>";
		String sz_oldtext = "";
		try
		{
			 sz_oldtext = PAS.get_pas().get_mappane().getToolTipText();
		}
		catch(Exception e)
		{
			
		}
		if(!sz.equals(sz_oldtext))
			PAS.get_pas().get_mappane().setToolTipText(sz, b_showtooltip);
		
		//PAS.get_pas().get_mappane().gett
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_download_finished".equals(e.getActionCommand())) {
			onDownloadFinished();
		}
	}
	protected abstract void onDownloadFinished(); 
}