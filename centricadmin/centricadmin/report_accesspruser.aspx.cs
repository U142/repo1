using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Web.UI.HtmlControls;

using centric.com.ums.ws.parm.admin;
using centric.com.ums.ws.pas;
using System.ServiceModel;

public partial class report_accesspruser : System.Web.UI.Page
{    
    protected void Page_Load(object sender, EventArgs e)
    {
        paswsSoapClient pasws = new paswsSoapClient();
        pasws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);

        centric.com.ums.ws.pas.admin.ULOGONINFO l = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            //UPASLOGON pl = pasws.PasLogon(logoninfo);
            //UDEPARTMENT[] depts = pl.departments;
            ParmAdminSoapClient pa = new ParmAdminSoapClient();
            pa.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["ParmAdmin"]);
            centric.com.ums.ws.parm.admin.ULOGONINFO logoninfo = Util.convertLogonInfoParmAdmin(l);
            
            UBBUSER[] ulist = pa.GetUsers(logoninfo);
            IEnumerable<UBBUSER> sorter = ulist.OrderBy(user => user.sz_userid);
            foreach (UBBUSER user in sorter)
            {
                if (user.l_deptpk != int.Parse(ConfigurationManager.AppSettings["admin_department"]))
                    lst_users.Items.Add(new ListItem(user.sz_userid, user.l_userpk.ToString()));
            }
        }
    }

    protected void btn_showClick(object sender, EventArgs e)
    {
        centric.com.ums.ws.pas.admin.ULOGONINFO l = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        centric.com.ums.ws.parm.admin.ULOGONINFO logoninfo = Util.convertLogonInfoParmAdmin(l);

        ParmAdminSoapClient pa = new ParmAdminSoapClient();
        pa.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["ParmAdmin"]);
        
        int[] selection = lst_users.GetSelectedIndices();
        UBBUSER[] ulist = new UBBUSER[selection.Length];

        for(int i=0;i<selection.Length;++i) {
            UBBUSER u = new UBBUSER();
            u.l_userpk = long.Parse(lst_users.Items[selection[i]].Value);
            ulist[i] = u;
        }

        CB_USER_REGION_RESPONSE[] response = pa.GetUserRegion(logoninfo, ulist);

        Session["region_response"] = response;

        for(int i=0; i< response.Length; ++i) {
            
            if (response[i].regionlist.Length > 0)
            {
                HtmlTableRow header = new HtmlTableRow();
                HtmlTableCell hc = new HtmlTableCell();
                Label lbl_header = new Label();
                lbl_header.Text = "User:";
                hc.Controls.Add(lbl_header);
                header.Cells.Add(hc);

                Label htxt = new Label();
                //TextBox htxt = new TextBox();
                htxt.Text = response[i].user.sz_userid;
                hc = new HtmlTableCell();
                hc.Controls.Add(htxt);
                header.Cells.Add(hc);
                tbl_output.Rows.Add(header);

                for (int j = 0; j < response[i].regionlist.Length; ++j)
                {
                    if (response[i].regionlist[j].l_deptpk != int.Parse(ConfigurationManager.AppSettings["admin_department"]))
                    {
                        HtmlTableRow row = new HtmlTableRow();
                        Label lbldesc = new Label();
                        lbldesc.Text = "Area:";
                        HtmlTableCell cell = new HtmlTableCell();
                        cell.Controls.Add(lbldesc);
                        row.Cells.Add(cell);
                        cell = new HtmlTableCell();
                        Label txt = new Label();
                        txt.Text = response[i].regionlist[j].sz_name;
                        cell.Controls.Add(txt);
                        row.Cells.Add(cell);
                        tbl_output.Rows.Add(row);
                    }
                }

                header = new HtmlTableRow();
                hc = new HtmlTableCell();
                lbl_header = new Label();
                lbl_header.Text = "&nbsp;";
                hc.Controls.Add(lbl_header);
                header.Cells.Add(hc);
                tbl_output.Rows.Add(header);
            }
        }
        if(response.Length>0)
            btn_export_to_csv.Visible = true;
    }
    
    protected void btn_export_to_csv_Click(object sender, EventArgs e)
    {
        Util.WriteAccessPerUserToCSV((CB_USER_REGION_RESPONSE[])Session["region_response"]);
    }
}
