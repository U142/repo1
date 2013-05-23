package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.WmsLayer;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASUISETTINGS;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;

public class WSSaveUI extends WSThread
{
    private static final Log log = UmsLog.getLogger(WSSaveUI.class);

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
			final Settings settings = PAS.get_pas().get_settings();
			String layerlist = "";
            // TODO: Make a proper sort method if needed. This one adds a random layer
			Collections.sort(settings.getWmsLayers(), new Comparator<WmsLayer>() {
				public int compare(WmsLayer w1, WmsLayer w2)
				{
					if(w1.checked.booleanValue())
						return -1;
					if(w2.checked.booleanValue())
						return 1;
					return settings.getWmsLayers().indexOf(w1) - settings.getWmsLayers().indexOf(w2);
				}
			});

			for(WmsLayer l : settings.getWmsLayers())
			{
				if(l.checked) {
					layerlist+=l.toString() + ",";
                }
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
			ui.setLSendingAutochannel(settings.getN_newsending_autochannel());
			ui.setLSendingAutoshape(settings.getN_autoselect_shapetype());
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
			log.warn(e.getMessage(), e);
			throw e;
		}

	}

	@Override
	protected String getErrorMessage() {
		return "Error saving UI settings";
	}
}
