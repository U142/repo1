package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.Settings;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASUISETTINGS;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.net.URL;

public class WSSaveUI extends WSThread
{
	public WSSaveUI(ActionListener callback)
	{
		super(callback);
	}

	@Override
	public void onDownloadFinished() {
		
	}

	@Override
	public void call() throws Exception {
		try
		{
			//PAS.get_pas().setSubstanceChanges();
			//PAS.pasplugin.onUserChangedLookAndFeel(PAS.get_pas().get_settings());
			ULOGONINFO logon = new ULOGONINFO();
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
			UPASUISETTINGS ui = new UPASUISETTINGS();
			MailAccount account = PAS.get_pas().get_userinfo().get_mailaccount();
			Settings settings = PAS.get_pas().get_settings();
			String layerlist = "";
			//Element layer;
			for(int i=0; i < settings.getSelectedWmsLayers().size(); i++)
			{
				if(i>0)
					layerlist+=",";
				layerlist+=settings.getSelectedWmsLayers().get(i);
			}
			
			if(account!=null)
			{
				ui.setSzEmailName(account.get_displayname());
				ui.setSzEmail(account.get_mailaddress());
				ui.setSzEmailserver(account.get_mailserver());
				ui.setLMailport(account.get_port());
			}
			else
			{
				ui.setSzEmailName("");
				ui.setSzEmail("");
				ui.setSzEmailserver("");
				ui.setLMailport(25);
			}
			ui.setBAutostartParm(settings.parm());
			ui.setBAutostartFleetcontrol(settings.fleetcontrol());
			ui.setSzLanguageid(settings.getLanguage());
			ui.setLLbaUpdatePercent(settings.getLbaRefresh());
			ui.setLMapserver(settings.getMapServer().ordinal());
			ui.setSzWmsSite(settings.getWmsSite());
			ui.setSzWmsFormat(settings.getSelectedWmsFormat());
			ui.setSzWmsLayers(layerlist);
			ui.setLDragMode((settings.getPanByDrag() ? 1 : 0));
			ui.setLZoomMode((settings.getZoomFromCenter() ? 0 : 1));
			
			ui.setSzSkinClass(settings.getSkinClassName());
			ui.setSzButtonshaperClass(settings.getButtonShaperClassname());
			ui.setSzGradientClass(settings.getGradientClassname());
			ui.setSzThemeClass(settings.getThemeClassName());
			ui.setSzTitleClass(settings.getTitlePainterClassname());
			ui.setSzWatermarkClass(settings.getWatermarkClassName());
			ui.setLGisMaxForDetails(500);
			ui.setSzWmsUsername(settings.getWmsUsername());
			ui.setSzWmsPassword(settings.getWmsPassword());
			
			if((PAS.get_pas().getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH)
				ui.setBWindowFullscreen(true);
			else
				ui.setBWindowFullscreen(false);
			ui.setLWinposX(PAS.get_pas().getLocation().x);
			ui.setLWinposY(PAS.get_pas().getLocation().y);
			ui.setLWinWidth(PAS.get_pas().getWidth());
			ui.setLWinHeight(PAS.get_pas().getHeight());
			ui.setFMapinitBbo(0);
			ui.setFMapinitLbo(0);
			ui.setFMapinitRbo(0);
			ui.setFMapinitUbo(0);
			try
			{
				URL wsdl = new URL(vars.WSDL_PAS);
				QName service = new QName("http://ums.no/ws/pas/", "pasws");
				//projectresponse = new Pasws(wsdl, service).getPaswsSoap12().uCreateProject(logon, projectrequest);
				boolean b_ret = new Pasws(wsdl, service).getPaswsSoap12().savePasUiSettings(logon, ui);
			}
			catch(Exception e)
			{
				//Error.getError().addError(PAS.l("common_error"), "Error saving UI settings", e, Error.SEVERITY_ERROR);
				throw e;
			}
			finally
			{
				//onDownloadFinished();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}

	}

	@Override
	protected String getErrorMessage() {
		return "Error saving UI settings";
	}
}