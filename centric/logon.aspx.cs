using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Configuration;

using com.ums.ws.pas.admin;

public partial class logon : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        if (!IsPostBack)
        {                                   
            txt_company.Text = ConfigurationSettings.AppSettings["company"];
        }


    }

    protected void btn_ok_Click(object sender, EventArgs e)
    {
        // log på, etterpå lagre ulogon i session, da vil jeg alltid kunne bruke den på alle ws kall
        PasAdmin pws = new PasAdmin();
        pws.Url = ConfigurationSettings.AppSettings["PasAdmin"];
        ULOGONINFO l = new ULOGONINFO();
        l.sz_compid = txt_company.Text.Replace("'","''");
        l.sz_userid = txt_user.Text.Replace("'", "''");
        l.sz_password = Helper.CreateSHA512Hash(txt_password.Text.Replace("'", "''"));
        txt_password.Text = "";
        lbl_error.ForeColor = System.Drawing.Color.Red;

        try
        {
            PasLogonResponse res = pws.doPasLogon(l);
            if (res.successful)
            {
                UPASLOGON pl = res.logon;
                l.sessionid = pl.sessionid;
                l.l_comppk = pl.l_comppk;
                l.l_userpk = pl.l_userpk;
                for (int i = 0; i < pl.departments.Length; ++i)
                    if (pl.departments[i].f_default)
                        l.l_deptpk = pl.departments[i].l_deptpk;
                if (pl.f_granted)
                {
                    Session["logoninfo"] = l;
                    Server.Transfer("main.aspx");
                }
                else
                {
                    lbl_error.Text = "Wrong username or password";
                }
            }
            else
            {
                lbl_error.Text = res.reason;
            }
        }
        catch (Exception ex)
        {
            // Verifyting
            lbl_error.Text = ex.Message;
        }
    }
}
