package no.ums.pas.core.controllers;

/*
 * 
 * @author Morten Helvig - Unified Messaging Systems AS
 * GPSController
 * 		* MapObjectList (extends ArrayList, contains an array of MapObject)
 * 		* MapObject (if dynamic, contains an array of GPSCoor order by date, time)
 * 		* GPSCoor (contains lon/lat/date/time/speed/course/sat/asl/batt
 *
 */

import no.ums.pas.PAS;
import no.ums.pas.core.webdata.XMLGps;
import no.ums.pas.gps.GPSCmd;
import no.ums.pas.gps.GPSCoor;
import no.ums.pas.gps.GPSEventList;
import no.ums.pas.gps.GpsSetupReturnCode;
import no.ums.pas.gps.MapObjectDlg;
import no.ums.pas.maps.defines.MapObject;
import no.ums.pas.maps.defines.MapObjectList;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Utils;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Calendar;

//import no.ums.pas.core.dataexchange.HttpGPSChannel;


public class GPSController extends Controller{
	private int m_n_trail_minutes = 10;
	private int m_n_epsilon_meters = 15;
	private double m_n_arrowsize = 10.0;
	private int m_n_simulation_date = -1;
	private int m_n_simulation_time = -1;
	private boolean m_b_run_simulation = false;

	MapObjectList m_mapobjects;
	GPSEventList m_gpsevents;
	XMLGps m_xmlgps;
	public XMLGps get_xmlgps() { return m_xmlgps; }
	public MapObjectList get_mapobjects() { return m_mapobjects; }
	public GPSEventList get_gpsevents() { return m_gpsevents; }
	
	public int get_trail_minutes() { return m_n_trail_minutes; }
	public int get_epsilon_meters() { return m_n_epsilon_meters; }
	public void set_epsilon_meters(int n_meters) { m_n_epsilon_meters = n_meters; }
	public void set_arrowsize(double d_size) { m_n_arrowsize = d_size; }
	public double get_arrowsize() { return m_n_arrowsize; }
	public int get_simulation_date() { return m_n_simulation_date; }
	public int get_simulation_time() { return m_n_simulation_time; }
	public void start_simulation(boolean b_start) { m_b_run_simulation = b_start; }
	public boolean get_run_simulation() { return m_b_run_simulation; }
	public boolean isSimulationSet() { 
		if(m_n_simulation_date > 0)
			return true;
		else
			return false; 
	}
	private MapObject m_mapobj_follow_me = null;

	public void set_follow_me(MapObject obj, boolean b_follow) {
		if(m_mapobj_follow_me!=null) //delete old follow me
		{
			try {
				m_mapobj_follow_me.set_follow(false);
			} catch(Exception e) { Error.getError().addError("GPSController","Exception in set_follow_me",e,1); }
			m_mapobj_follow_me = null;
		}
		if(b_follow) //create new follow me
		{
			m_mapobj_follow_me = obj;
			try {
				//only needs update if this is a new object
				if( get_follow_me()==null || (get_follow_me()!=null && get_follow_me().equals(obj)) )
				{
					obj.set_follow(true);
					PAS.get_pas().add_event("\"Follow me\" enabled on " + obj.get_name(), null);
				}
			} catch(Exception e) { 
				m_mapobj_follow_me = null;
				Error.getError().addError("GPSController","Exception in set_follow_me",e,1);
			} //error: not a valid object
		}
	}
	public MapObject get_follow_me() { return m_mapobj_follow_me; }
	
	public void close() {
		
	}
	
	
	public GPSController()
	{
		super();
		m_mapobjects = new MapObjectList(PAS.get_pas());
		m_gpsevents  = new GPSEventList();
	}
	public synchronized void start_download(boolean b_auto)
	{
		if(get_updates_in_progress()) {
			if(!b_auto)
				PAS.get_pas().add_event("GPS: Error, updates already in progress...", null);
			return;
		}
		set_updates_in_progress(true);
		//String sz_url = "PAS_getgpslist.asp?l_companypk=2";
		String sz_url = "PAS_getgpslist_zipped.asp?l_companypk=" + PAS.get_pas().get_userinfo().get_comppk() + "&l_datefilter=" + get_filter_date() + "&l_timefilter=" + get_filter_time() + "&l_eventpkfilter=" + get_gpsevents().get_eventpk_filter();
		m_xmlgps = new XMLGps(Thread.MAX_PRIORITY, PAS.get_pas(), sz_url, null);
		get_xmlgps().start();
	}
	public void set_trail_minutes(int n_minutes) { 
		m_n_trail_minutes = n_minutes;
		PAS.get_pas().kickRepaint();
	}
	public boolean get_if_trailed(Calendar now, int n_date, int n_time) {
		if(get_trail_minutes()==-1)
			return true;
		Calendar check;
		try {
			check = Utils.create_date(n_date, n_time);
			if(Utils.get_minute_difference(check, now) < get_trail_minutes())
				return true;
		} catch(Exception e) { 
			Error.getError().addError("GPSController","Exception in get_if_trailed",e,1);
			PAS.get_pas().add_event(e.getMessage(), e);
		}
		return false;
	}
	
	/*
	 * parse mapobjects with gps enabled, and retrieve the highest date/time
	 */
	void create_filter()
	{
		MapObject current = null;
		int n_current_date = get_filter_date();
		int n_current_time = get_filter_time();
		int n_idx_lastcoor = -1;
		int n_temp_date, n_temp_time;
		for(int i=0; i < get_mapobjects().size(); i++)
		{
			current = (MapObject)get_mapobjects().get(i);
			if(current.get_gpscoors()!=null)
			{
				n_idx_lastcoor = current.get_gpscoors().size()-1;
				//get last coor of each object
				if(n_idx_lastcoor >= 0)
				{
					n_temp_date = ((GPSCoor)current.get_gpscoors().get(n_idx_lastcoor)).get_gpsdate();
					n_temp_time = ((GPSCoor)current.get_gpscoors().get(n_idx_lastcoor)).get_gpstime();
					if( ( n_temp_date > n_current_date) || 
						( n_temp_date == n_current_date && n_temp_time > n_current_time ) )
					{
						n_current_date = n_temp_date;
						n_current_time = n_temp_time;
					}
				}
			}
		}
		set_timefilter(n_current_date, n_current_time);
	}
	
	/*
	 * draw
	 */
	public void drawItems(Graphics g)
	{
		get_mapobjects().draw(g);
	}
	public void goto_object(String l_objectpk, int n_zoom)
	{
		get_mapobjects().goto_object(l_objectpk, n_zoom);
	}
	
	/*
	 * new map means new pixel coordinates
	 */
	public void calcGpsCoords()
	{
		get_mapobjects().calcGpsCoords();
	}
	
	/*
	 * The GPS download thread is finished. Update objects and show.
	 */
	public void onDownloadFinished()
	{
		create_filter();
		PAS.get_pas().get_gpsframe().fill();
		PAS.get_pas().get_eastcontent().get_gpseventpanel().fill(get_gpsevents());
		//get_pas().get_drawthread().set_neednewcoors(true);
		PAS.get_pas().kickRepaint();
		set_lastupdate();
		set_updates_in_progress(false);
//		get_pas().get_eastcontent().flip_to(EastContent.PANEL_GPS_LIST_);
	}
	public void set_visibility() {
		
	}
	public void reg_mapobj(MapObject obj) {
		new MapObjectDlg(PAS.get_pas(), obj);
	}
	public void get_current_position(String sz_objectpk) {
		//HttpGPSChannel.post_form(PAS.get_pas().get_sitename() + "PAS_gpssetup.asp", sz_objectpk, this, PAS.get_pas().get_eastcontent().get_gps_loadingpanel(), GPSCmd.CMD_GETCOOR, 10, 0, "", "");
	}
	public void gps_shutdown(String sz_objectpk) {
		//HttpGPSChannel.post_form(PAS.get_pas().get_sitename() + "PAS_gpssetup.asp", sz_objectpk, this, PAS.get_pas().get_eastcontent().get_gps_loadingpanel(), GPSCmd.CMD_GPS_SHUTDOWN, 0, 0, "", "");
	}
	public void gps_query_battery(String sz_objectpk) {
		//HttpGPSChannel.post_form(PAS.get_pas().get_sitename() + "PAS_gpssetup.asp", sz_objectpk, this, PAS.get_pas().get_eastcontent().get_gps_loadingpanel(), GPSCmd.CMD_BAT_VOLTAGE, 0, 0, "", "");		
	}
	public void actionPerformed(ActionEvent e) {
		if("act_gps_answer".equals(e.getActionCommand())) {
			GpsSetupReturnCode ret = (GpsSetupReturnCode)e.getSource();
			String sz_msg = "Return value:" + ret.get_answertext() + " (" + ret.get_text() + ")";
			//JOptionPane.showMessageDialog(PAS.get_pas(), sz_msg);
			System.out.println(sz_msg);
			if(ret.get_answercode()==GPSCmd.STATUS_FINAL_OK) {
				this.start_download(true);
				this.goto_object(ret.get_objectpk(), 500);
			}
			return; //no need for more checking
		}
		super.actionPerformed(e);
	}
}