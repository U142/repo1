package no.ums.pas.core.controllers;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.Houses;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.send.SendController;
import no.ums.ws.pas.*;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class HouseController extends Controller {
	public static final int HOUSE_DOWNLOAD_IN_PROGRESS_ = 1;
	public static final int HOUSE_DOWNLOAD_FINISHED_	= 2;
	public static final int HOUSE_DOWNLOAD_NO_			= 4;
	public static final int HOUSE_DOWNLOAD_DISABLED_	= 8;
	
	private NavStruct m_nav;	
	private NavStruct get_nav() { return m_nav; }
	//private XMLHouses m_xml;
	//private XMLHouses get_xml() { return m_xml; }
	//private HTTPReq m_http_req;
	//public HTTPReq get_http_req() { return m_http_req; }
	//1=private, 2=company, 4=mobile, 8=fax
	
	private int m_n_max_meters_width = 1000;
	public int get_max_meters_width() { return m_n_max_meters_width; }
	public void set_max_meters_width(int n_meters) { m_n_max_meters_width = n_meters; }
	
	public HouseController() {
		super();
		m_houses = new Houses(false);
		//m_http_req = new HTTPReq(PAS.get_pas().get_sitename()/*PAS.get_pas().get_sitename()*/);
	}
	
	void create_filter() {
		m_nav = new NavStruct(variables.NAVIGATION.get_lbo().doubleValue(), variables.NAVIGATION.get_rbo().doubleValue(),
				variables.NAVIGATION.get_ubo().doubleValue(), variables.NAVIGATION.get_bbo().doubleValue());
	}

	public void show_addresstypes(int n_types) {
		//ADR_TYPES_SHOW_ = 0;
		/*switch(n_types) {
			case 0:
				add_addresstype(ADR_TYPES_PRIVATE_);
				add_addresstype(ADR_TYPES_COMPANY_);
				break;
			case 1:
				add_addresstype(ADR_TYPES_PRIVATE_);
				break;
			case 2:
				add_addresstype(ADR_TYPES_COMPANY_);
				break;
			case 3:
				add_addresstype(ADR_TYPES_MOBILE_);
				break;
			case 4:
				add_addresstype(ADR_TYPES_PRIVATE_);
				add_addresstype(ADR_TYPES_MOBILE_);
				break;
			case 5:
				add_addresstype(ADR_TYPES_COMPANY_);
				add_addresstype(ADR_TYPES_MOBILE_);
				break;
			case 6:
				add_addresstype(ADR_TYPES_PRIVATE_);
				add_addresstype(ADR_TYPES_COMPANY_);
				add_addresstype(ADR_TYPES_MOBILE_);
				break;
		}*/
		ADR_TYPES_SHOW_ |= n_types;
		ADR_TYPES_SHOW_ |= SendController.SENDTO_NOPHONE_COMPANY | SendController.SENDTO_NOPHONE_PRIVATE;
	}
	public void add_addresstype(int n_type) {
		ADR_TYPES_SHOW_ |= n_type;
	}
	public void rem_addresstype(int n_type) {
		ADR_TYPES_SHOW_ &= ~n_type;
	}
	public void set_addresstypes(int n)
	{
		ADR_TYPES_SHOW_ = n;
	}
	public boolean show_adrtype(int n_type) {
		return ((ADR_TYPES_SHOW_ & n_type) == n_type);
	}
	public void start_download(boolean b) {
		/*if(get_http_req().get_running()) {
			PAS.get_pas().add_event("Interrupting house download...", null);
			get_http_req().set_interrupted();
			Timeout time = new Timeout(2, 20);
			while(get_http_req().get_running()) {
				try {
					Thread.sleep(time.get_msec_interval());
					time.inc_timer();
					if(time.timer_exceeded()) {
						System.out.println("Housecontroller wait timed out");
						return;
					}
				} catch(InterruptedException e) { 
					PAS.get_pas().add_event("Waiting for stream to close", e);
				}
			}
			get_http_req().m_b_interrupted = false;
		}*/
		create_filter();
		//if(variables.NAVIGATION.get_mapwidthmeters().intValue() > get_max_meters_width()) {
			//PAS.get_pas().add_event("House visibility exceeded - " + get_max_meters_width() + " / " + variables.NAVIGATION.get_mapwidthmeters().intValue(), null);
		set_visibility_change(true);
		//	return;
		//}
		//String sz_url = "PAS_gethouses_zipped.asp?l_companypk=" + PAS.get_pas().get_userinfo().get_comppk() + "&lbo=" + get_nav()._lbo + "&rbo=" + get_nav()._rbo + "&ubo=" + get_nav()._ubo + "&bbo=" + get_nav()._bbo;
		//m_xml = new XMLHouses(Thread.MAX_PRIORITY, PAS.get_pas(), sz_url, null, new HTTPReq(PAS.get_pas().get_sitename())/*m_http_req*/, this);
		//get_xml().start();
		ObjectFactory of = new ObjectFactory();
		UMapAddressParams searchparams = of.createUMapAddressParams();
		ULOGONINFO logoninfo = of.createULOGONINFO();
		searchparams.setLBo((float)get_nav()._lbo);
		searchparams.setRBo((float)get_nav()._rbo);
		searchparams.setUBo((float)get_nav()._ubo);
		searchparams.setBBo((float)get_nav()._bbo);
		logoninfo.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
		logoninfo.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		try
		{
			//"https://secure.ums2.no/vb4utv/ExecAlert/WS/PAS.asmx?WSDL"
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/PAS.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			
			UAddressList list = new Pasws(wsdl, service).getPaswsSoap12().getAddressList(searchparams, logoninfo);
			ArrayList<Object> outlist = new ArrayList<Object>(0);
			Iterator<UAddress> it = list.getList().getUAddress().iterator();
			while(it.hasNext())
			{
				UAddress a = (UAddress)it.next();
				//int n = new Long(a.getImportid()).intValue();
				//System.out.println("Customadr = " + n);
				Inhabitant inhab = new Inhabitant(); //a.getKondmid(), a.getName(), a.getAddress(), a.getHouseno(), );
				inhab.init(a.getKondmid(), a.getName(), a.getAddress(), new Integer(a.getHouseno()).toString(), a.getLetter(), 
							a.getPostno(), a.getPostarea(), new Integer(a.getRegion()).toString(), a.getBday(), a.getNumber(), 
							a.getMobile(), new Double(a.getLat()).doubleValue(), new Double(a.getLon()).doubleValue(), a.getGno(), a.getBno(), a.getBedrift(), 
							new Long(a.getImportid()).intValue(), a.getStreetid(), a.getXycode(), a.getHasfixed(), a.getHasmobile());
				outlist.add(inhab);
			}
			
			
			//this.actionPerformed(new ActionEvent(outlist, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
			m_items = outlist;
			m_houses.sort_houses(get_items(), false);
			PAS.get_pas().get_drawthread().set_neednewcoors(true);
			set_visibility_change(true);
			PAS.get_pas().actionPerformed(new ActionEvent(new Integer(HouseController.HOUSE_DOWNLOAD_FINISHED_), ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));

		}
		catch(Exception e)
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("Error", "House download", e, 1);
			return;
		}
		
		
		
	}
	
	protected void downloadFinished() 
	{
		
	}
	
	protected void onDownloadFinished() {
		/*if(get_xml().get_items()!=null) {
			m_items = (ArrayList<Object>)get_xml().get_items().clone();
			m_houses.sort_houses(get_items(), false);
			PAS.get_pas().get_drawthread().set_neednewcoors(true);
			set_visibility_change(true);
			PAS.get_pas().actionPerformed(new ActionEvent(new Integer(HouseController.HOUSE_DOWNLOAD_FINISHED_), ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));
		}*/
	}
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_insert_inhabitant".equals(e.getActionCommand())) {
			m_items.add((Inhabitant)e.getSource());
			m_houses.sort_houses(get_items(), false);
			m_houses.set_visible(true);
			m_houses.calcHouseCoords();
			set_visibility_change(true);
			PAS.get_pas().kickRepaint();
		}
		else if("act_delete_inhabitant".equals(e.getActionCommand())) {
			m_houses.remove_inhabitant((Inhabitant)e.getSource());
			m_items.remove((Inhabitant)e.getSource());
			/*m_houses.sort_houses(get_items());
			m_houses.set_visible(true);
			m_houses.calcHouseCoords();*/
			set_visibility_change(true);
			PAS.get_pas().kickRepaint();
			
		}
		super.actionPerformed(e);
	}
	public void set_visibility() {
		get_houses().set_visible(true);
	}
	public void calcHouseCoords()
	{
		if(get_houses()==null)
			return;
		if(get_houses().get_houses()==null)
			return;
		get_houses().calcHouseCoords();
	}
	public void drawItems(Graphics gfx)
	{
		if(get_houses()!=null)// && get_houses().is_housesready())
		{
			if(get_houses().get_houses()==null)
				return;
			set_visibility();
			get_houses().draw_houses(gfx, 0, PAS.get_pas().get_mapproperties().get_border_activated(), PAS.get_pas().get_mapproperties().get_showtext(),
					PAS.get_pas().get_mapproperties().get_fontsize(), ADR_TYPES_SHOW_, null);
		}
	}
	public void check_mouseover(int x, int y) {
		if(PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_show_houses())
			find_houses_bypix(new Dimension(x, y));
	}
	
}

