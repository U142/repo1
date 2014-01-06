package no.ums.pas.send;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Houses;
import no.ums.pas.maps.defines.Houses.LonLatComparator;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.common.UGisRecord;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.UMapBounds;
import no.ums.ws.common.parm.ArrayOfUGisRecord;
import no.ums.ws.common.parm.UGISSENDING;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.Parmws;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class SendPropertiesGIS extends SendProperties {

    private static final Log log = UmsLog.getLogger(SendPropertiesGIS.class);

	private GISList m_gislist;
	public GISList get_gislist() { return m_gislist; }
	private Color m_col_housecolor = new Color(0, 0, 200);
	public void set_color(Color col) {
		m_col_housecolor = col;
	}
	public Color get_color() {
		return m_col_housecolor;
	}
	
	public void set_gislist(GISList list) {
		m_gislist = list;
		m_houses = new Houses(true);
		m_houses.setJoinHouses(true, 10);
		m_inhabitants = new TreeSet<Inhabitant>(m_houses.new LonLatComparator());
		m_inhabitants.clear();
		for(int i=0; i < list.size(); i++) {
			for(int j=0; j < list.get_gisrecord(i).get_inhabitantcount(); j++) {
				Inhabitant bas = list.get_gisrecord(i).get_inhabitant(j).toInhabitant();
				m_inhabitants.add(bas);
			}
		}
		m_houses.sort_houses(m_inhabitants, false);
		m_houses.set_visible(true);
	}
	SortedSet<Inhabitant> m_inhabitants = new TreeSet<Inhabitant>(); 
	private Houses m_houses;
	public Houses get_houses() { return m_houses; }
	@Override
	public void calc_coortopix()
	{
		try
		{
			if(get_houses()!=null)
			{
				get_houses().calcHouseCoords();
			}
			get_shapestruct().calc_coortopix(Variables.getNavigation());
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
	}

	public SendPropertiesGIS(SendOptionToolbar toolbar) {
		super(SendProperties.SENDING_TYPE_GEMINI_STREETCODE_, toolbar, new Col(Color.black, Color.white));
	}
	
	public int get_adrcount(int n_type) {
		int ret = 0;
		HouseItem house;
		if(m_houses == null)
			m_houses = PAS.get_pas().get_statuscontroller().get_houses();
		
		for(int i=0; i < get_houses().get_houses().size(); i++) {
			house = (HouseItem)get_houses().get_houses().get(i);
			ret += house.get_inhabitantcount(n_type);
		}
		return ret;
	}
	
	public boolean can_lock() {
		return true;
	}
	public void draw(Graphics g, Point mousepos) {
		/*Inhabitant inhab;
		for(int i=0; i < get_gislist().size(); i++) {
			for(int j=0; j < get_gislist().get_gisrecord(i).get_inhabitantcount(); j++) {
				inhab = get_gislist().get_gisrecord(i).get_inhabitant(j);
			}
		}*/
		//log.debug("SendPropertiesGIS.draw");
		try {
			//if(get_houses().get_houses().size()<1000)
			{
				//get_houses().calcHouseCoords();
				Color c = new Color(get_color().getRed(), get_color().getGreen(), get_color().getBlue(), 128);
				get_houses().draw_houses(g, 0, true, true, 10, get_addresstypes_bitwise(), c);
			}
		} catch(Exception e) {
			//Error.getError().addError("Error in painting", "Could not paint GIS houses", e, 2);
		}
	}
	public boolean goto_area() {
		try {
			if(get_houses().get_bounds()!=null) {
				PAS.get_pas().actionPerformed(new ActionEvent(get_houses().get_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				return true;
			}
		} catch(Exception e) {
			
		}
		return false;
	}
	public boolean create_paramvals() {
		try {
			/*sz_polygon_params	= new String[get_polygon().get_size() + 1];
			sz_polygon_vals		= new String[get_polygon().get_size() + 1];
			sz_polygon_params[0]	= "n_polypoints";
			sz_polygon_vals[0]		= new Integer(get_polygon().get_size()).toString();
			for(int i=1; i < (get_polygon().get_size()) + 1; i++) {
				sz_polygon_params[i]	= "p" + (i-1);
				sz_polygon_vals[i]		= ((Double)get_polygon().get_coors_lon().get(i-1)).toString() + "," + ((Double)get_polygon().get_coors_lat().get(i-1)).toString();
			}*/
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesGIS.create_paramvals() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesGIS","Exception in create_paramvals",e,1);
			return false;
		}
		return true;
	}	
	protected final boolean send() {
		try {
			ULOGONINFO logon = new ULOGONINFO();
			UGISSENDING poly = new UGISSENDING();
			//UEllipseDef el = factory.createUEllipseDef();
			UMapBounds bounds = new UMapBounds();
			if(!get_isresend()) {
				bounds.setLBo(m_gislist.GetBounds()._lbo);
				bounds.setRBo(m_gislist.GetBounds()._rbo);
				bounds.setUBo(m_gislist.GetBounds()._ubo);
				bounds.setBBo(m_gislist.GetBounds()._bbo);
			}
		
			populate_common(poly, logon, bounds);

			ArrayOfUGisRecord gis = new ArrayOfUGisRecord();
			int inhabIncluded = 0;
			if(get_gislist()!=null && !get_isresend())
			{
				for(int i=0; i < get_gislist().size(); i++)
				{
					for(int j=0; j < get_gislist().get_gisrecord(i).get_inhabitantcount(); j++)
					{
						InhabitantBasics inhab = get_gislist().get_gisrecord(i).get_inhabitant(j);
						long l = new Long(inhab.get_kondmid());
						
						/*
						 * check for addresstypes. 
						 * Check for private/company, fixed/mobile combinations.
						 * To avoid sending more data to server than needed
						 */
						boolean SMSPrivate = (get_addresstypes() & SendController.CHECK_PRIVATE_SMS_INCLUDED) > 0;
						boolean SMSCompany = (get_addresstypes() & SendController.CHECK_COMPANY_SMS_INCLUDED) > 0;
						boolean FixPrivate = (get_addresstypes() & SendController.CHECK_PRIVATE_FIXED_INCLUDED) > 0;
						boolean FixCompany = (get_addresstypes() & SendController.CHECK_COMPANY_FIXED_INCLUDED) > 0;
						boolean MobPrivate = (get_addresstypes() & SendController.CHECK_PRIVATE_MOBILE_INCLUDED) > 0;
						boolean MobCompany = (get_addresstypes() & SendController.CHECK_COMPANY_MOBILE_INCLUDED) > 0;
						boolean OnlyVulner = (get_addresstypes() & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) > 0;
						boolean OnlyHead   = (get_addresstypes() & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) > 0;
						
						//check if at least one of these statements are correct to include address in server object
						boolean bDoInclude = (SMSPrivate && inhab.hasmobile() && !inhab.bedrift()) |
											(SMSCompany && inhab.hasmobile() && inhab.bedrift()) |
											(FixPrivate && inhab.hasfixed() && !inhab.bedrift()) |
											(FixCompany && inhab.hasfixed() && inhab.bedrift()) | 
											(MobPrivate && inhab.hasmobile() && !inhab.bedrift()) |
											(MobCompany && inhab.hasmobile() && inhab.bedrift());
						/*
						 * Check addresstypes. If "only send to vulnerable citizens" is selected,
						 * add only inhabitants marked as vulnerable.
						 */
						boolean bVulnerable = get_gislist().get_gisrecord(i).get_inhabitant(j).isVulnerable();
						if((OnlyVulner && bVulnerable && bDoInclude) ||
							(!OnlyVulner && bDoInclude))
						{
							UGisRecord r = new UGisRecord();
							r.setId(l);
							gis.getUGisRecord().add(r);
							++inhabIncluded;
						}
					}
				}
				poly.setGis(gis);
			}
			log.info("Send GIS: %d inhabitants included in GIS-list based on channels and private/company", inhabIncluded);
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execGisSending(poly);
			parse_sendingresults(response, true);

			
		} 
		catch(SOAPFaultException e)
		{
			PAS.pasplugin.onSoapFaultException(Variables.getUserInfo(), e);
			return false;
		}		
		catch(Exception e) {
			set_last_error("ERROR SendPropertiesGIS.send() - " + e.getMessage());
			Error.getError().addError("SendPropertiesGIS","Exception in send",e,1);
			return false;
		}
		
		return true;
	}
	public void set_adrinfo(Object obj) {
		
	}
	public PolySnapStruct snap_to_point(Point p, int n) {
		return null;
	}
	protected File create_addressfile() {
		File f_path = new File(StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_));
		//File f = null;
		File f;
		try {
			f = File.createTempFile("send", ".ums", f_path);
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			return null;
		}
		try {
			if(f!=null) {
				write_addressfile(f);
				return f;
			}
		} catch(Exception e) {
			log.debug(e.getMessage());
			Error.getError().addError("SendPropertiesGIS","Exception in create_addressfile",e,1);
			log.warn(e.getMessage(), e);
		}
		
		return null;
	}
	protected boolean write_addressfile(File f) {
		try {
			FileWriter write = new FileWriter(f);
			HouseItem house;
			Inhabitant inhab;
			for(int i=0; i < get_houses().get_houses().size(); i++) {
				house = (HouseItem)get_houses().get_houses().get(i);
				//for(int j=0; j < house.get_inhabitantcount(); j++) {
				for(int j=0; j < house.get_inhabitants().size(); j++) {
					inhab = house.get_itemfromhouse(j);
					if(inhab==null) {
						//Error.getError().addError("Inhabitant error", "NULL inhabitant found (number " + j + ") on house number " + i, 1, 1);
						log.debug("NULL inhabitant found (number " + j + ") on house number " + i);
					}
					else if(inhab.get_kondmid().length() > 0 && inhab.get_included()) {
						write.write("/KONDMID=" + inhab.get_kondmid() + "\r\n");
						write.write("/ADRID=" + "\r\n");
					}
				}
			}
			write.close();
			return true;
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("SendPropertiesGIS","Exception in write_addressfile",e,1);
		}
		return false;
	}

	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		return false;
	}
	
	
}