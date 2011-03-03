package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.common.UDATAFILTER;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.status.PasStatus;
import no.ums.ws.pas.status.UStatusListItem;
import no.ums.ws.pas.status.UStatusListResults;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
//import no.ums.ws.pas.*;

public class WSGetStatusList extends WSThread
{
	private ArrayList<StatusListObject> m_arr_statusobjects;
	
	public WSGetStatusList(ActionListener callback)
	{
		super(callback);
	}

	@Override
	public void onDownloadFinished() {
		if(m_arr_statusobjects==null)
		{
			m_arr_statusobjects = new ArrayList<StatusListObject>();
		}
		try{
			m_callback.actionPerformed(new ActionEvent(m_arr_statusobjects, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
		}
		catch(Exception e) { Error.getError().addError("XMLGetStatusList","Exception in onDownloadFinished",e,1); }			
		
	}

	@Override
	public void call() throws Exception {
		ULOGONINFO logon = new ULOGONINFO();
		WSFillLogoninfo.fill(logon, PAS.get_pas().get_userinfo());
        URL wsdl = new URL(vars.WSDL_PASSTATUS);
        QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");

        final UDATAFILTER filter;
        if(PAS.TRAINING_MODE)
            filter = UDATAFILTER.BY_SIMULATION;
        else
            filter = UDATAFILTER.BY_LIVE;
        UStatusListResults response = new PasStatus(wsdl, service).getPasStatusSoap12().getStatusListFiltered(logon, filter);
        m_arr_statusobjects = new ArrayList<StatusListObject>();

        for(int i=0; i < response.getList().getUStatusListItem().size(); i++)
        {
            UStatusListItem item = response.getList().getUStatusListItem().get(i);
            StatusListObject obj = new StatusListObject(item.getNRefno(), item.getNSendingtype(), item.getNTotitem(),
                    item.getNAltjmp(), item.getNCreatedate(), item.getNCreatetime(), item.getSzSendingname(),
                    item.getNSendingstatus(), item.getNGroup(), item.getNType(), item.getNDeptpk(),
                    item.getSzDeptid(), Long.toString(item.getNProjectpk()), item.getSzProjectname(), Long.toString(item.getNCreatetimestamp()),
                    Long.toString(item.getNUpdatetimestamp()));
            m_arr_statusobjects.add(obj);
        }
	}

	@Override
	protected String getErrorMessage() {
		return "Unable to open statuslist";
	}

}