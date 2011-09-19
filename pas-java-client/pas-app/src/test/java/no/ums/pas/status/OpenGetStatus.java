package no.ums.pas.status;

import no.ums.pas.PasApplication;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.UDATAFILTER;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASLOGON;

public class OpenGetStatus {
	public static void main(String[] args) {
		PasApplication pasApp = PasApplication.init("http://localhost:8080/WS/");
		ULOGONINFO logoninfo = new ULOGONINFO();
		logoninfo.setOnetimekey(pasApp.getPaswsSoap().getOneTimeKey());
		logoninfo.setSzCompid("ums");
		logoninfo.setSzUserid("ssa");
		logoninfo.setSzPassword(Utils.encrypt("sa123,1"));
		UPASLOGON logon = pasApp.getPaswsSoap().pasLogon(logoninfo);
		logoninfo.setSessionid(logon.getSessionid());
		logoninfo.setLDeptpk(logon.getDepartments().getUDEPARTMENT().get(0).getLDeptpk());
		logoninfo.setLUserpk(logon.getLUserpk());
		logoninfo.setLComppk(logon.getLComppk());
		pasApp.getPasStatus().getStatusListFiltered(logoninfo, UDATAFILTER.BY_LIVE);
		
		pasApp.shutdown();
	}
}
