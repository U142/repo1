using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using com.ums.ws.pas;
using com.ums.ws.pas.admin;

using System.Configuration;

public partial class logoff : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        try
        {
            
            PasAdmin pa = new PasAdmin();
            pa.Url = ConfigurationSettings.AppSettings["PasAdmin"];
            pa.doSetOccupied((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.PREDEFINEDTEXT, false);
            pa.doSetOccupied((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.RESTRICTIONAREA, false);
        }
        catch (Exception err)
        {
        }
        try
        {
            pasws ws = new pasws();
            ws.Url = ConfigurationSettings.AppSettings["Pas"];
            bool success = ws.PasLogoff(Util.convertLogonInfoPas((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"]));
        }
        catch (Exception err)
        {

        }
        Session.RemoveAll();
        Server.Transfer("logon.aspx");
    }
}
