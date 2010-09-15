using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using com.ums.ws.pas;
using com.ums.ws.pas.admin;

public partial class logoff : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        try
        {
            
            PasAdmin pa = new PasAdmin();
            pa.doSetOccupied((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.PREDEFINEDTEXT, false);
            pa.doSetOccupied((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.RESTRICTIONAREA, false);
        }
        catch (Exception err)
        {
        }
        try
        {
            pasws ws = new pasws();
            bool success = ws.PasLogoff(Util.convertLogonInfoPas((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"]));
        }
        catch (Exception err)
        {

        }
        Session.RemoveAll();
        Server.Transfer("logon.aspx");
    }
}
