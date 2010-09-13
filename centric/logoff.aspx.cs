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
        pasws ws = new pasws();
        ws.PasLogoff(Util.convertLogonInfoPas((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"]));
        Session.RemoveAll();
        Server.Transfer("logon.aspx");
    }
}
