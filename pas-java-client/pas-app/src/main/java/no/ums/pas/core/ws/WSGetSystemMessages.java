package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.ws.common.UBBNEWSLISTFILTER;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.USYSTEMMESSAGES;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.event.ActionListener;
import java.net.URL;

public class WSGetSystemMessages extends WSThread
{
	USYSTEMMESSAGES ret;
	long n_dbtimestamp = 0;
	public USYSTEMMESSAGES getSystemMessages() { return ret; }

    // Fix for bug APP-357
    private final int maxErrors = 5;
    private int currentErrorNumber = 0;

	public WSGetSystemMessages(ActionListener callback)
	{
		super(callback);
	}
	@Override
	public void call() throws Exception {
		try
		{
            try {
                ULOGONINFO logon = new ULOGONINFO();
                logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
                logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
                logon.setJobid("0");
                logon.setLAltservers(0);
                logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
                logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
                logon.setLDeptpri(0);
                logon.setLPriserver(0);
                logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
                logon.setSzDeptid("");
                logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
                logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
                logon.setSzStdcc("");
                URL wsdl = new URL(vars.WSDL_PAS);
                QName service = new QName("http://ums.no/ws/pas/", "pasws");
                ret = new Pasws(wsdl, service).getPaswsSoap12().getSystemMessages(logon, n_dbtimestamp, UBBNEWSLISTFILTER.IN_BETWEEN_START_END);
                n_dbtimestamp = ret.getNews().getLTimestampDb();
                currentErrorNumber = 0;
            } catch (Exception e) {
                currentErrorNumber++;
                if(currentErrorNumber >= maxErrors) {
                    throw e;
                }
            }
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	@Override
	protected String getErrorMessage() {
		return "Error accessing system messages from server";
	}

	@Override
	public void onDownloadFinished() {
		//log.debug("System messages downloaded");
	}
	
}