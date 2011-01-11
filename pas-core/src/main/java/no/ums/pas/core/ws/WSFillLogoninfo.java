package no.ums.pas.core.ws;

//import no.ums.ws.parm.admin.*;

import no.ums.pas.core.logon.UserInfo;
import no.ums.ws.pas.status.ULOGONINFO;

public class WSFillLogoninfo
{
	public static void fill(ULOGONINFO l, UserInfo ui)
	{
		l.setSzCompid(ui.get_compid());
		l.setSzUserid(ui.get_userid());
		l.setSzDeptid(ui.get_current_department().get_deptid());
		l.setSzPassword(ui.get_passwd());
		l.setLComppk(ui.get_comppk());
		l.setLDeptpk(ui.get_current_department().get_deptpk());
		l.setLUserpk(new Long(ui.get_userpk()));
		l.setSessionid(ui.get_sessionid());

	}
}