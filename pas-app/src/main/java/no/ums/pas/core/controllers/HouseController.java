package no.ums.pas.core.controllers;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.ZoomLookup;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.core.Variables;
import no.ums.pas.core.menus.ViewOptions;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.HouseDownloadCache;
import no.ums.pas.maps.defines.Houses;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.send.SendController;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UAddress;
import no.ums.ws.pas.UAddressList;
import no.ums.ws.pas.UMapAddressParams;

import javax.xml.namespace.QName;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class HouseController extends Controller {
	public static final int HOUSE_DOWNLOAD_IN_PROGRESS_ = 1;
	public static final int HOUSE_DOWNLOAD_FINISHED_	= 2;
	public static final int HOUSE_DOWNLOAD_NO_			= 4;
	public static final int HOUSE_DOWNLOAD_DISABLED_	= 8;

    private static final Log log = UmsLog.getLogger(HouseController.class);

	private NavStruct m_nav;	
	private NavStruct get_nav() { return m_nav; }
	private boolean bShow = true;
	
	//private XMLHouses m_xml;
	//private XMLHouses get_xml() { return m_xml; }
	//private HTTPReq m_http_req;
	//public HTTPReq get_http_req() { return m_http_req; }
	//1=private, 2=company, 4=mobile, 8=fax
	
	public boolean isShow() {
		return bShow;
	}
	private void setShow(boolean bShow) {
		this.bShow = bShow;
	}
	private int m_n_max_meters_width = 1000;
	public int get_max_meters_width() { return m_n_max_meters_width; }
	public void set_max_meters_width(int n_meters) { m_n_max_meters_width = n_meters; }
	
	public HouseController() {
		super();
		m_houses = new Houses(false);
		//m_http_req = new HTTPReq(PAS.get_pas().get_sitename()/*PAS.get_pas().get_sitename()*/);
	}
	
	void create_filter() {
		m_nav = new NavStruct(Variables.getNavigation().get_lbo().doubleValue(), Variables.getNavigation().get_rbo().doubleValue(),
				Variables.getNavigation().get_ubo().doubleValue(), Variables.getNavigation().get_bbo().doubleValue());
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

    private final ZoomLookup stdZoom = new ZoomLookup(17, 256);
    private final HouseDownloadCache cache = new HouseDownloadCache();
    
	public void start_download(final boolean b) {
		create_filter();
		set_visibility_change(true);
		try
		{
            m_items = new ArrayList<Object>();
            for (final Point point : stdZoom.getTiles(new LonLat(get_nav()._lbo, get_nav()._ubo), new LonLat(get_nav()._rbo, get_nav()._bbo))) {
                for (UAddress a : cache.getHouseInfos(stdZoom.getZoomLevel(), point.y, point.x)) {
                    m_items.add(new Inhabitant(a));
                }
            }

			m_houses.sort_houses(get_items(), false);
			PAS.get_pas().get_drawthread().set_neednewcoors(true);
			set_visibility_change(true);
			PAS.get_pas().actionPerformed(new ActionEvent(HouseController.HOUSE_DOWNLOAD_FINISHED_, ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));
		}
		catch(Exception e)
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("Error", "House download", e, 1);
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
			if(!m_items.contains((Inhabitant)e.getSource())) {
				m_items.add((Inhabitant)e.getSource());
			}
			else {
				m_items.set(m_items.indexOf((Inhabitant)e.getSource()),(Inhabitant)e.getSource());
			}
			
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
	
	@Override
	public void set_visibility() {
		set_visibility(true);
	}
	public void set_visibility(boolean b) {
		setShow(b);
		get_houses().set_visible(b);
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
		if(isShow())
		{
			if(get_houses()!=null)
			{
				if(get_houses().get_houses()==null)
					return;
				set_visibility();
				get_houses().draw_houses(gfx, 0, PAS.get_pas().get_mapproperties().get_border_activated(), PAS.get_pas().get_mapproperties().get_showtext(),
						PAS.get_pas().get_mapproperties().get_fontsize(), ADR_TYPES_SHOW_, null);
			}
		}
	}
	public void check_mouseover(int x, int y) {
		if(isShow())
		{
			if(ViewOptions.TOGGLE_HOUSES.isSelected())
				find_houses_bypix(new Dimension(x, y));
		}
	}
}

