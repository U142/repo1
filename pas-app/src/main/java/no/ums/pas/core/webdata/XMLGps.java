package no.ums.pas.core.webdata;

import no.ums.pas.PAS;
import no.ums.pas.gps.GPSCoor;
import no.ums.pas.gps.GPSEvent;
import no.ums.pas.maps.defines.MapObject;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;

public class XMLGps extends XMLThread {
	public XMLGps(int n_pri, PAS pas, String sz_url, JFrame parent_frame)
	{
		super(n_pri, pas, sz_url, parent_frame, pas.get_eastcontent().get_gps_loadingpanel(), "Downloading GPS data...", true, 
			  pas.get_httpreq());
		
	}
	public void parseDoc(Document doc)
	{
		if(doc==null)
			return;
		/*parse header*/
		//get_pas().add_event("Inflating and parsing GPS list");
		NodeList list_itemlist = doc.getElementsByTagName("MAPOBJECTS");
		if (list_itemlist == null)
			return;
		Node current = list_itemlist.item(0);
		if(current==null)
			return;
		NamedNodeMap nnm_itemlist = current.getAttributes();
		if(nnm_itemlist==null)
			return;
		Node node_records = nnm_itemlist.getNamedItem("records");
		if(node_records==null)
			return;
		String sz_records;
		sz_records = node_records.getNodeValue();
		//get_pas().add_event("Records: " + sz_records);
		get_loadingpanel().start_progress(new Integer(sz_records).intValue(), "Inflating and parsing GPS list");
		/*parse items*/
		NodeList list_items = current.getChildNodes(); //mapobjects
		if(list_items==null)
			return;
		Node node_item;
		NamedNodeMap nnm_items;
		
/*
  <MAPOBJECT l_objectpk="1500000000000042" sz_name="Per Hansen" l_comppk="2" l_deptpk="1" l_lon="0" l_lat="0" l_picturepk="2" l_resourcepk="-1" l_unitpk="-1" l_timeinterval="-1" l_moveinterval="-1" l_gsmno="98222222" l_lastdate="-1" l_lasttime="-1" l_lastspeed="" l_lastcourse="" l_lastsatellites="" l_lastasl="" l_lastbattery="" f_satfix="" sz_picname="GSM" sz_iconfile="1500000000000042.gif">
*/
		String[] sz_itemattr = { "l_objectpk", "sz_name", "l_comppk", "l_deptpk", "l_lon", "l_lat", "l_picturepk", "l_resourcepk", "l_unitpk", "l_timeinterval", "l_moveinterval", "l_gsmno", "l_lastdate", "l_lasttime", "l_lastspeed", "l_lastcourse", "l_lastsatellites", "l_lastasl", "l_lastbattery", "f_satfix", "sz_picname", "sz_iconfile", "sz_street", "sz_region", "l_gsmno2", "l_manufacturer", "l_usertype", "sz_imei", "sz_simid", "f_online", "l_serverport" };
		String[] sz_values;
		MapObject mapobj;
		int n_gpscoors_added = 0;
		double f_percent = 0;
		int n_show_percent = 0;
		double f_percent_pr_item;
		int n_record_count = new Integer(sz_records).intValue();
		if(n_record_count > 0)
			f_percent_pr_item = ((double)30 / n_record_count);
		else
			f_percent_pr_item = 100;
		
		
		for(int n_items=0; n_items < list_items.getLength(); n_items++)
		{
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null)
				continue;
			sz_values = new String[sz_itemattr.length];
			//get_pas().add_event("length: "+ sz_itemattr.length);
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++)
			{
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				}
				catch(Exception e)
				{
					sz_values[n_attr] = new String("");
					//Error.getError().addError("XMLGps","Exception in parseDoc",e,1);
				}
			}
			try {
				mapobj = get_pas().get_gpscontroller().get_mapobjects().add(sz_values);
				//mapobj = new MapObject()
			} catch(Exception e) { 
				String sz_temp = new String();
				for(int z=0; z<sz_itemattr.length; z++)
					sz_temp += ", " + new String(nnm_items.getNamedItem(sz_itemattr[z]).getNodeValue());
				get_pas().add_event("PARSEERROR: " + e.getMessage() + " " + sz_temp, e);
				Error.getError().addError("XMLGps","Exception in parseDoc",e,1);
				continue;
			}
			
			/*
			 * mapobj now contains the MapObject data. now check if there's any Dynamic coordinates
			 * <GPSCOOR l_lon="0" l_lat="0" l_date="-1" l_time="-1" l_speed="0" l_course="0" l_satellites="0" l_asl="0" l_battery="0" /> 
			 */
			NodeList list_gps;
			list_gps = node_item.getChildNodes();
			int n_gps_count = 0;
			n_gpscoors_added = 0;
			
			if(list_gps!=null)
			{
				Node node_gps;
				NamedNodeMap nnm_gps;
				GPSCoor gps = null;
				String[] sz_gpsattr = { "l_lon", "l_lat", "l_date", "l_time", "l_speed", "l_course", "l_satellites", "l_asl", "l_battery", "sz_street", "sz_region"  };
				for(int n_gps=0; n_gps < list_gps.getLength(); n_gps++)
				{
					node_gps = list_gps.item(n_gps);
					nnm_gps  = node_gps.getAttributes();
					if(nnm_gps==null)
						continue;
					sz_values = new String[sz_gpsattr.length];
					for(int n_gpsattr=0; n_gpsattr < sz_gpsattr.length; n_gpsattr++)
					{
						try {
							sz_values[n_gpsattr] = new String(nnm_gps.getNamedItem(sz_gpsattr[n_gpsattr]).getNodeValue());
						} catch(Exception e)
						{
							sz_values[n_gpsattr] = new String("");
							Error.getError().addError("XMLGps","Exception in parseDoc",e,1);
						}
					}
					try {
						//newest comes first
						if((gps = mapobj.add_gpscoor(sz_values)) != null)
						{
							mapobj.set_position(gps.get_lon(), gps.get_lat());
							n_gpscoors_added++;
							//gps.set_prev(prev);
							//prev = gps;
							//get_pas().add_event("dist: " + gps.get_distance_to_prev());
						}
					} catch(Exception e) { 
						System.out.println(e.getMessage());
						e.printStackTrace();
						Error.getError().addError("XMLGps","Exception in parseDoc",e,1);
					}
					get_loadingpanel().inc_currentitem();
					f_percent += f_percent_pr_item;
					if((int)f_percent > n_show_percent)
					{
						n_show_percent = (int)(f_percent);
						get_loadingpanel().set_currentoverallitem(n_show_percent);
					}
					
				}
			}
			if(n_gpscoors_added==0)
			{
				get_loadingpanel().inc_currentitem();
				f_percent += f_percent_pr_item;
				if((int)f_percent > n_show_percent)
				{
					n_show_percent = (int)(f_percent);
					get_loadingpanel().set_currentoverallitem(n_show_percent);
				}
				mapobj.recalc_screencoords(false);
			}
			else
				mapobj.recalc_screencoords(true);
		}
		parseEvents(doc);
		//get_pas().add_event("GPS Coors added: " + n_gpscoors_added);
		onDownloadFinished();
	}
	public void parseEvents(Document doc) {
		NodeList list_itemlist = doc.getElementsByTagName("EVENTLIST");
		if (list_itemlist == null)
			return;
		Node current = list_itemlist.item(0);
		if(current==null)
			return;
		/*NamedNodeMap nnm_itemlist = current.getAttributes();
		if(nnm_itemlist==null)
			return;
		Node node_records = nnm_itemlist.getNamedItem("records");
		if(node_records==null)
			return;
		String sz_records;
		sz_records = node_records.getNodeValue();*/
		/*parse items*/
		NodeList list_items = current.getChildNodes(); //mapobjects
		if(list_items==null)
			return;
		Node node_item;
		NamedNodeMap nnm_items;
		
		GPSEvent eventobj;
		
		//String[] sz_itemattr = { "l_objectpk", "sz_name", "l_comppk", "l_deptpk", "l_lon", "l_lat", "l_picturepk", "l_resourcepk", "l_unitpk", "l_timeinterval", "l_moveinterval", "l_gsmno", "l_lastdate", "l_lasttime", "l_lastspeed", "l_lastcourse", "l_lastsatellites", "l_lastasl", "l_lastbattery", "f_satfix", "sz_picname", "sz_iconfile", "sz_street", "sz_region", "l_gsmno2", "l_manufacturer", "l_usertype", "sz_imei", "sz_simid", "f_online", "l_serverport" };
		String [] sz_itemattr = { "l_eventpk", "l_objectpk", "l_cmd", "l_dir", "l_param1", "l_param2", "sz_param1", "sz_param2", "l_answered", "l_pri", "l_date", "l_time", "l_updatedate", "l_updatetime", "l_msgpk" };
		String[] sz_values;
		//System.out.println("list_items.getLength() = "+ list_items.getLength());
		for(int n_items=0; n_items < list_items.getLength(); n_items++)
		{
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null)
				continue;
			System.out.println("attr");
			sz_values = new String[sz_itemattr.length];
			//get_pas().add_event("length: "+ sz_itemattr.length);
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++)
			{
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					sz_values[n_attr] = new String("");
					Error.getError().addError("XMLGps","Exception in parseEvents",e,1);
				}
			}
			try {
				eventobj = get_pas().get_gpscontroller().get_gpsevents().add(sz_values);
				//mapobj = get_pas().get_gpscontroller().get_mapobjects().add(sz_values);
				//mapobj = new MapObject()
			} catch(Exception e) { 
				String sz_temp = new String();
				for(int z=0; z<sz_itemattr.length; z++)
					sz_temp += ", " + new String(nnm_items.getNamedItem(sz_itemattr[z]).getNodeValue());
				get_pas().add_event("PARSEERROR: " + e.getMessage() + " " + sz_temp, e);
				Error.getError().addError("XMLGps","Exception in parseEvents",e,1);
				continue;
			}
		}		
	}
	public void onDownloadFinished()
	{
		try{
			get_pas().get_gpscontroller().onDownloadFinished();
		}
		catch(Exception e) { Error.getError().addError("XMLGps","Exception in onDownloadFinished",e,1); }
	}	
}