using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using com.ums.ws.pas;

public partial class logon : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        if (!IsPostBack)
        {
            txt_company.Text = "UMS";
            txt_user.Text = "MH";
        }


    }

    protected void btn_ok_Click(object sender, EventArgs e)
    {
        // log på, etterpå lagre ulogon i session, da vil jeg alltid kunne bruke den på alle ws kall
        pasws pws = new pasws();
        ULOGONINFO l = new ULOGONINFO();
        l.sz_compid = txt_company.Text;
        l.sz_userid = txt_user.Text;
        l.sz_password = Helper.CreateSHA512Hash(txt_password.Text);
        txt_password.Text = "";

        try
        {
            UPASLOGON pl = pws.PasLogon(l);
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
        }
        catch (Exception ex)
        {
            // Verifyting
        }
    }
}
