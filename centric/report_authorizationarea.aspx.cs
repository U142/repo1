using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using com.ums.ws.pas;
using com.ums.ws.parm.admin;

public partial class report_authorizationarea : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        pasws pasws = new pasws();
        com.ums.ws.pas.ULOGONINFO l = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            //UPASLOGON pl = pasws.PasLogon(logoninfo);
            //UDEPARTMENT[] depts = pl.departments;
            ParmAdmin pa = new ParmAdmin();
            PAOBJECT[] obj = pa.GetRegions(Util.convertLogonInfoParmAdmin(l));

            for (int i = 0; i < obj.Length; ++i)
            {
                lst_areas.Items.Add(new ListItem(obj[i].sz_name, obj[i].l_objectpk.ToString()));
            }
        }
    }
    protected void btn_showClick(object sender, EventArgs e)
    {
    }
}
