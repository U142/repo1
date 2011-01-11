package no.ums.pas.send;

import no.ums.pas.PAS;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Houses;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.parm.*;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;

public class SendPropertiesGIS extends SendProperties {
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
		m_inhabitants = new ArrayList<Object>();
		for(int i=0; i < list.size(); i++) {
			for(int j=0; j < list.get_gisrecord(i).get_inhabitantcount(); j++) {
				m_inhabitants.add(list.get_gisrecord(i).get_inhabitant(j));
			}
		}
		m_houses.sort_houses(m_inhabitants, false);
		m_houses.set_visible(true);
	}
	ArrayList<Object> m_inhabitants = null;
	private Houses m_houses;
	public Houses get_houses() { return m_houses; }
	@Override
	public void calc_coortopix()
	{
		try
		{
			get_houses().calcHouseCoords();
			get_shapestruct().calc_coortopix(variables.NAVIGATION);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
		//System.out.println("SendPropertiesGIS.draw");
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
		/*if(!this.create_paramvals()) {
			return false;
		}*/
		try {
			/*HttpPostForm http = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_send.asp");
			populate_common(http);
			//create addressfile and attach
			if(!get_isresend()) { //we don't need adrinfo if it's a resend
				File f = create_addressfile();
				if(f!=null) {
					http.setParameter("file", f);
					//InputStream is = http.post();
				}
				else {
					Error.getError().addError("Error", "Could not create a temporary addressfile", -1, 1);
					return false;
				}
			}
			try {				
				//f.delete();
				InputStream is = http.post();
				Error.getError().addError(is);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("SendPropertiesGIS","Exception in send",e,1);
			}*/
			ObjectFactory factory = new ObjectFactory();
			no.ums.ws.parm.ULOGONINFO logon = factory.createULOGONINFO();
			no.ums.ws.parm.UGISSENDING poly = factory.createUGISSENDING();
			//UEllipseDef el = factory.createUEllipseDef();
			UMapBounds bounds = factory.createUMapBounds();
			if(!get_isresend()) {
				bounds.setLBo(m_gislist.GetBounds()._lbo);
				bounds.setRBo(m_gislist.GetBounds()._rbo);
				bounds.setUBo(m_gislist.GetBounds()._ubo);
				bounds.setBBo(m_gislist.GetBounds()._bbo);
			}
		
			populate_common((no.ums.ws.parm.UMAPSENDING)poly, logon, bounds);

			ArrayOfUGisRecord gis = factory.createArrayOfUGisRecord();
			if(get_gislist()!=null && !get_isresend())
			{
				for(int i=0; i < get_gislist().size(); i++)
				{
					for(int j=0; j < get_gislist().get_gisrecord(i).get_inhabitantcount(); j++)
					{
						long l = new Long(get_gislist().get_gisrecord(i).get_inhabitant(j).get_kondmid());
						UGisRecord r = factory.createUGisRecord();
						r.setId(l);
						gis.getUGisRecord().add(r);
					}
				}
				poly.setGis(gis);
			}
			
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execGisSending(poly);
			parse_sendingresults(response, true);

			
		} catch(Exception e) {
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
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		try {
			if(f!=null) {
				write_addressfile(f);
				return f;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			Error.getError().addError("SendPropertiesGIS","Exception in create_addressfile",e,1);
			e.printStackTrace();
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
						System.out.println("NULL inhabitant found (number " + j + ") on house number " + i);
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
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SendPropertiesGIS","Exception in write_addressfile",e,1);
		}
		return false;
	}

	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		return false;
	}
	
	
}