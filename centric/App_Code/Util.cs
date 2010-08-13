using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for Util
/// </summary>
public class Util
{
    public static com.ums.ws.parm.admin.ULOGONINFO convertLogonInfoParmAdmin(com.ums.ws.pas.ULOGONINFO l)
    {
        com.ums.ws.parm.admin.ULOGONINFO logoninfo = new com.ums.ws.parm.admin.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static com.ums.ws.pas.status.ULOGONINFO convertLogonInfoPasStatus(com.ums.ws.pas.ULOGONINFO l)
    {
        com.ums.ws.pas.status.ULOGONINFO logoninfo = new com.ums.ws.pas.status.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static String convertDate(long l_date)
    {
        String date = l_date.ToString();
        if (l_date == 0)
            return "";
        else
            return date.Substring(6,2) + "-" + date.Substring(4, 2) + "-" + date.Substring(0, 4) + " " + date.Substring(8,2) + ":" + date.Substring(10,2);
    }
}
