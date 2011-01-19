package no.ums.pas.core.ws;

import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UPASUISETTINGS;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class WSGetVisualSettings extends WSThread
{
	protected static UPASUISETTINGS visuals = new UPASUISETTINGS();
	public static UPASUISETTINGS getVisuals()
	{
		return WSGetVisualSettings.visuals;
	}
	@Override
	public void onDownloadFinished() {
		m_callback.actionPerformed(new ActionEvent(visuals, ActionEvent.ACTION_PERFORMED, "act_visualsettings_downloaded"));
	}
	@Override
	public void call() throws Exception {
		try
		{
			no.ums.ws.pas.ObjectFactory of = new no.ums.ws.pas.ObjectFactory();
			no.ums.ws.pas.ULOGONINFO logon = of.createULOGONINFO();
			logon.setSzCompid(sz_compid.toUpperCase());
			logon.setSzUserid(sz_userid.toUpperCase());
			logon.setJobid("0");
			logon.setLAltservers(0);
			logon.setLComppk(0);
			logon.setLDeptpk(0);
			logon.setLDeptpri(0);
			logon.setLPriserver(0);
			logon.setLUserpk(0);
			logon.setSzDeptid("");
			logon.setSzPassword("");
			logon.setSzStdcc("");
			URL wsdl = new URL(vars.WSDL_PAS);
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			synchronized(WSGetVisualSettings.visuals)
			{
				WSGetVisualSettings.visuals = new Pasws(wsdl, service).getPaswsSoap12().loadLanguageAndVisuals(logon);
			}

		}
		catch(Exception e)
		{
			visuals = new UPASUISETTINGS();
			visuals.setInitialized(false);
			e.printStackTrace();
			throw e;
		}
		/*finally
		{
			onDownloadFinished();
		}*/
		
	}
	
	@Override
	protected String getErrorMessage() {
		return "Unable to load Visual Settings";
	}

	protected String sz_userid;
	protected String sz_compid;
	public WSGetVisualSettings(ActionListener callback, String userid, String compid)
	{
		super(callback);
		sz_userid = userid;
		sz_compid = compid;
		
	}
	
}