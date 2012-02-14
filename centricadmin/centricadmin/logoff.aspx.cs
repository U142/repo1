using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using centric.com.ums.ws.pas;
using centric.com.ums.ws.pas.admin;

using System.Configuration;
using System.ServiceModel;

public partial class logoff : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        try
        {

            PasAdminSoapClient pa = new PasAdminSoapClient();
            pa.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
            pa.doSetOccupied((centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.PREDEFINEDTEXT, false);
            pa.doSetOccupied((centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.RESTRICTIONAREA, false);
        }
        catch (Exception err)
        {
            lbl_err.Text = err.Message;
        }
        try
        {
            paswsSoapClient ws = new paswsSoapClient();
            ws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);
            bool success = ws.PasLogoff(Util.convertLogonInfoPas((centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"]));
            Server.Transfer("logon.aspx");
        }
        catch (Exception err)
        {
            lbl_err.Text = err.Message;
        }
        Session.RemoveAll();
    }

    protected void btn_gotologon_Click(object sender, EventArgs e)
    {
        Server.Transfer("logon.aspx");
    }
}
