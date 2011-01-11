package no.ums.pas.send;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.pas.ums.tools.SmsInReplyNumber;
import no.ums.ws.parm.*;
import no.ums.ws.pas.status.USMSINSTATS;
import no.ums.ws.pas.tas.ULBACOUNTRY;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;


public class SendPropertiesTAS extends SendProperties
{
	List<ULBACOUNTRY> m_country = null;
	List<USMSINSTATS> m_smsinstats = null;
	boolean m_b_allow_resonse = false;
	
	public SendPropertiesTAS(SendOptionToolbar toolbar)
	{ 
		super(SendProperties.SENDING_TYPE_TAS_COUNTRY_, toolbar, new Col(Color.red, Color.red));
	}
	
	public void setSmsInStats(List<USMSINSTATS> smsinstats) {
		m_smsinstats = smsinstats;
	}
	
	public List<USMSINSTATS> getSmsInStats() {
		return m_smsinstats;
	}
	
	public List<ULBACOUNTRY> getCountry() {
		return m_country; 
	}
	public void setCountryList(List<ULBACOUNTRY> c)
	{
		m_country = c;
		String sendto = "";
		if(c.size()==1)
			sendto = c.get(0).getSzName();
		else if(c.size()>1)
			sendto = PAS.l("main_tas_send_multiple_countries") + " (" + c.size() + ")";
		set_sendingname(PAS.l("main_tas_title") + " - " + sendto, "");
		get_sendobject().get_sendproperties().get_toolbar().lock_sending(true);
	}
	
	@Override
	public void calc_coortopix()
	{
		get_shapestruct().calc_coortopix(variables.NAVIGATION);
	}

	@Override
	public boolean can_lock() {
		return (m_country!=null ? true : false);
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
		return false;
	}

	@Override
	public boolean PerformAdrCount(ActionListener l, String act) {
		return false;
	}

	@Override
	protected boolean send() {
		try {
			ObjectFactory factory = new ObjectFactory();
			no.ums.ws.parm.ULOGONINFO logon = factory.createULOGONINFO();
			no.ums.ws.parm.UTASSENDING tas = factory.createUTASSENDING();
			UMapBounds bounds = factory.createUMapBounds();
			if(!super.get_isresend()) {
				
				//bounds.setLBo(m_country.get(0).getBounds().getLBo());
				//bounds.setRBo(m_country.get(0).getBounds().getRBo());
				//bounds.setBBo(m_country.get(0).getBounds().getBBo());
				//bounds.setUBo(m_country.get(0).getBounds().getUBo());
				
				NavStruct [] nav_list = new NavStruct[m_country.size()];
				for(int i=0; i < m_country.size(); i++)
				{
					no.ums.ws.pas.tas.UMapBounds b = m_country.get(i).getBounds();
					nav_list[i] = new NavStruct(b.getLBo(),b.getRBo(), b.getUBo(), b.getBBo());
				}
				NavStruct nav_final = CommonFunc.calc_bounds_from_navstructs(nav_list);
				bounds.setLBo(nav_final._lbo);
				bounds.setRBo(nav_final._rbo);
				bounds.setUBo(nav_final._ubo);
				bounds.setBBo(nav_final._bbo);
			}
			else {
				tas.setBResend(true);
			}
			System.out.println("TAS objid før populate common: " + System.identityHashCode(this));
			populate_common((no.ums.ws.parm.UMAPSENDING)tas, logon, bounds);
			
			/*ArrayOfUMapPoint points;
			if(!get_isresend())
				points = createWSPolygon();
			else
				points = null;
			poly.setPolygonpoints(points);*/
			ArrayOfULBACOUNTRY arr = factory.createArrayOfULBACOUNTRY();
			for(int i=0; i < m_country.size(); i++)
			{
				no.ums.ws.parm.ULBACOUNTRY c = factory.createULBACOUNTRY();
				c.setSzName(m_country.get(i).getSzName());
				c.setLCc(m_country.get(i).getLCc());
				c.setLContinentpk(m_country.get(i).getLContinentpk());
				c.setLIsoNumeric(m_country.get(i).getLIsoNumeric());
				c.setSzIso(m_country.get(i).getSzIso());
				arr.getULBACOUNTRY().add(c);
			}
			tas.setCountrylist(arr);
			tas.setBAllowResponse(super.get_sms_broadcast_text().get_allow_response().isSelected());
			m_b_allow_resonse = super.get_sms_broadcast_text().get_allow_response().isSelected();
			if(m_b_allow_resonse)
				tas.setSzResponseNumber(((SmsInReplyNumber)super.get_sms_broadcast_text().get_replynumbers().getSelectedItem()).get_replynumber());
			//URL wsdl = new URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			Parmws myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
			ExecResponse response = myService.getParmwsSoap12().execTasSending(tas);
			parse_sendingresults(response, true);
			PAS.get_pas().get_current_project().set_saved();
			return true;
		} catch(Exception e) {
			set_last_error("ERROR SendPropertiesTAS.send() - " + e.getMessage());
			PAS.get_pas().add_event(get_last_error(), e);
			Error.getError().addError("SendPropertiesTAS","Exception in send",e,1);
			return false;
		}
		
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
	
	public boolean get_allow_response() {
		return m_b_allow_resonse;
	}
	
	public void set_allow_response(boolean allow_response) {
		m_b_allow_resonse = allow_response;
	}
}