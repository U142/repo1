﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using com.ums.ws.pas;

public partial class report_useractivity : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        pasws pasws = new pasws();
        ULOGONINFO l = (ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            //UPASLOGON pl = pasws.PasLogon(logoninfo);
            //UDEPARTMENT[] depts = pl.departments;
            /*ParmAdmin pa = new ParmAdmin();
            PAOBJECT[] obj = pa.GetRegions(Util.convertLogonInfo(l));

            for (int i = 0; i < obj.Length; ++i)
            {
                lst_areas.Items.Add(new ListItem(obj[i].sz_name, obj[i].l_objectpk.ToString()));
            }*/
        }
    }
}
