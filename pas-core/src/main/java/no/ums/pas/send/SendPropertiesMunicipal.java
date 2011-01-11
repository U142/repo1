package no.ums.pas.send;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.Municipal;
import no.ums.pas.maps.defines.MunicipalStruct;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.ws.parm.*;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SendPropertiesMunicipal extends SendProperties
{
	public List<Municipal> get_municipals()
	{ 
		return get_shapestruct().typecast_municipal().getMunicipals();
	}
	public SendPropertiesMunicipal(MunicipalStruct shape, SendOptionToolbar toolbar) {
		super(SendProperties.SENDING_TYPE_MUNICIPAL_, toolbar, new Col(Color.black, Color.white));
		set_shapestruct(shape);
	}
	public Municipal newMunicipal(String id, String name)
	{
		return new Municipal(id, name);
	}
	

	
	public void AddMunicipal(Municipal m, boolean b_add)
	{
		get_shapestruct().typecast_municipal().AddMunicipal(m, b_add);
	}
	
	@Override
	public void calc_coortopix()
	{
		get_shapestruct().calc_coortopix(variables.NAVIGATION);
	}


	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		ObjectFactory factory = new ObjectFactory();
		//no.ums.ws.parm.UEllipseDef ell = createWSEllipse();
		//List<UMunicipalDef> mun = new List<UMunicipalDef>();
		//UELLIPSESENDING ms = factory.createUELLIPSESENDING();
		//ms.setEllipse(ell);
		ArrayOfUMunicipalDef arr = factory.createArrayOfUMunicipalDef();
		//List<UMunicipalDef> list = arr.getUMunicipalDef();
		for(int i = 0; i < get_shapestruct().typecast_municipal().getMunicipals().size(); i++)
		{
			UMunicipalDef def = factory.createUMunicipalDef();
			Municipal m = get_shapestruct().typecast_municipal().getMunicipals().get(i);
			def.setSzMunicipalid(m.get_id());
			def.setSzMunicipalname(m.get_name());
			arr.getUMunicipalDef().add(def);
		}
		UMUNICIPALSENDING ms = factory.createUMUNICIPALSENDING();
		ms.setMunicipals(arr);
		
		return super._ExecAdrCount(ms, l, act);
	}

	@Override
	public boolean can_lock() {
		return get_shapestruct().can_lock(null);
	}

	@Override
	public void draw(Graphics g, Point mousepos) {
		
	}

	@Override
	public Color get_color() {
		return null;
	}

	@Override
	public boolean goto_area() {
		try
		{
			ObjectFactory factory = new ObjectFactory();
			ULOGONINFO logon = factory.createULOGONINFO();
			UMUNICIPALSENDING sending = factory.createUMUNICIPALSENDING();
			UMapBounds bounds = factory.createUMapBounds();
			//populate_common((UMAPSENDING)sending, logon, bounds);
			UserInfo info = PAS.get_pas().get_userinfo();
			logon.setLComppk(info.get_comppk());
			logon.setLDeptpk(info.get_current_department().get_deptpk());
			logon.setLUserpk(Long.parseLong(info.get_userpk()));
			logon.setSzCompid(info.get_compid());
			logon.setSzDeptid(info.get_current_department().get_deptid());
			logon.setSzPassword(info.get_passwd());
			logon.setSzStdcc(info.get_current_department().get_stdcc());
			
			sending.setLogoninfo(logon);
			sending.setMapbounds(bounds);
			ArrayOfUMunicipalDef mun = factory.createArrayOfUMunicipalDef();
			if(get_municipals()!=null && !get_isresend())
			{
				for(int i=0; i < get_municipals().size(); i++)
				{
					UMunicipalDef def = factory.createUMunicipalDef();
					def.setSzMunicipalid(get_municipals().get(i).get_id());
					def.setSzMunicipalname(get_municipals().get(i).get_name());
					mun.getUMunicipalDef().add(def);
				}
				sending.setMunicipals(mun);
				URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
				//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
				QName service = new QName("http://ums.no/ws/parm/", "parmws");
				Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
				bounds = myService.getParmwsSoap12().getMapBoundsFromSending(logon, sending);
				NavStruct nav = new NavStruct();
				nav._lbo = bounds.getLBo();
				nav._rbo = bounds.getRBo();
				nav._ubo = bounds.getUBo();
				nav._bbo = bounds.getBBo();
				get_sendobject().get_callback().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			}

			return true;
		} catch (MalformedURLException e) {
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }

	@Override
	protected boolean send() {
		try
		{
			ObjectFactory factory = new ObjectFactory();
			ULOGONINFO logon = factory.createULOGONINFO();
			UMUNICIPALSENDING sending = factory.createUMUNICIPALSENDING();
			UMapBounds bounds = factory.createUMapBounds();
			populate_common((UMAPSENDING)sending, logon, bounds);
	
			//ArrayOfUGisRecord gis = factory.createArrayOfUGisRecord();
			ArrayOfUMunicipalDef mun = factory.createArrayOfUMunicipalDef();
			if(get_municipals()!=null && !get_isresend())
			{
				for(int i=0; i < get_municipals().size(); i++)
				{
					/*for(int j=0; j < get_gislist().get_gisrecord(i).get_inhabitantcount(); j++)
					{
						long l = new Long(get_gislist().get_gisrecord(i).get_inhabitant(j).get_kondmid());
						UGisRecord r = factory.createUGisRecord();
						r.setId(l);
						gis.getUGisRecord().add(r);
					}*/
					UMunicipalDef def = factory.createUMunicipalDef();
					def.setSzMunicipalid(get_municipals().get(i).get_id());
					def.setSzMunicipalname(get_municipals().get(i).get_name());
					//sending.getMunicipals().getUMunicipalDef().add(def);
					mun.getUMunicipalDef().add(def);
				}
				sending.setMunicipals(mun);
				//poly.setGis(gis);
			}
			
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execMunicipalSending(sending);
			parse_sendingresults(response, true);		
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesMunicipal.send() - " + e.getMessage());
			Error.getError().addError("SendPropertiesMunicipal","Exception in send",e,1);
			return false;
		}
		return true;
	}
		

	@Override
	public void set_adrinfo(Object adr) {
		
	}

	@Override
	public void set_color(Color col) {
		
	}

	@Override
	public PolySnapStruct snap_to_point(Point p1, int n_max_distance) {
		return null;
	}
}