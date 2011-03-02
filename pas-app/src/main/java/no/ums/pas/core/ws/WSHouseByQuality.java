package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.*;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;




public class WSHouseByQuality extends WSThread
{
	String action;
	UAddressList m_list;
	ArrayList<Inhabitant> m_items;
	public ArrayList<Inhabitant> get_items() { return m_items; }
	String sz_error = "";
	UMapAddressParamsByQuality m_params;
	
	public WSHouseByQuality(ActionListener callback, String act, UMapAddressParamsByQuality params)
	{
		super(callback);
		action = act;
		m_params = params;
		start();
	}

	@Override
	public void onDownloadFinished() {
		if(m_callback!=null && m_items!=null)
			m_callback.actionPerformed(new ActionEvent(m_items, ActionEvent.ACTION_PERFORMED, action));
			//m_callback.actionPerformed(new ActionEvent(m_inhab, ActionEvent.ACTION_PERFORMED, action));

	}
	
	@Override
	public void call() throws Exception
	{
		m_items = new ArrayList<Inhabitant>();
		ULOGONINFO logon = new ULOGONINFO();
		no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
		logon.setLComppk(ui.get_comppk());
		logon.setLDeptpk(ui.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(ui.get_userpk()));
		logon.setSzCompid(ui.get_compid());
		logon.setSzDeptid(ui.get_current_department().get_deptid());
		logon.setSzUserid(ui.get_userid());
		logon.setSzPassword(ui.get_passwd());
		logon.setSzStdcc(ui.get_current_department().get_stdcc());
		logon.setSessionid(ui.get_sessionid());

		java.net.URL wsdl;
		try
		{	
			wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			//wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			
			
			m_list = new Pasws(wsdl, service).getPaswsSoap12().getAddressListByQuality(logon, m_params);
			ArrayOfUAddress tmp = m_list.getList();
			List<UAddress> adr = tmp.getUAddress();
            for (UAddress u : adr) {
                Inhabitant inhab = new Inhabitant();
                inhab.set_adrname(u.getName());
                inhab.set_adrtype(u.getBedrift());
                inhab.set_birthday(u.getBday());
                inhab.set_bnumber(u.getBno());
                inhab.set_deptpk(new Long(u.getImportid()).intValue());
                inhab.set_gnumber(u.getGno());
                inhab.set_kondmid(u.getKondmid());
                inhab.set_lat(u.getLat());
                inhab.set_letter(u.getLetter());
                inhab.set_lon(u.getLon());
                inhab.set_mobile(u.getMobile());
                inhab.set_no(Integer.toString(u.getHouseno()));
                inhab.set_number(u.getNumber());
                inhab.set_postaddr(u.getAddress());
                inhab.set_postarea(u.getPostarea());
                inhab.set_postno(u.getPostno());
                inhab.set_quality(u.getXycode().charAt(0));
                inhab.set_region(Integer.toString(u.getRegion()));
                inhab.set_streetid(u.getStreetid());

                m_items.add(inhab);
            }
		}
		catch(Exception e)
		{
			//sz_error = e.getMessage();
			//Error.getError().addError("Error occured", "Error downloading houses", e, 1);
			throw e;
		}
		/*finally
		{
			onDownloadFinished();
		}*/
		
	}

	@Override
	protected String getErrorMessage() {
		return "Error downloading houses";
	}
}