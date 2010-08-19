using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Web.UI.HtmlControls;

using com.ums.ws.parm.admin;
using com.ums.ws.pas;

public partial class report_accesspruser : System.Web.UI.Page
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
            com.ums.ws.parm.admin.ULOGONINFO logoninfo = Util.convertLogonInfoParmAdmin(l);
            
            UBBUSER[] ulist = pa.GetUsers(logoninfo);

            for (int i = 0; i < ulist.Length; ++i)
            {
                lst_users.Items.Add(new ListItem(ulist[i].sz_userid, ulist[i].l_userpk.ToString()));
            }
        }
    }

    protected void btn_showClick(object sender, EventArgs e)
    {
        com.ums.ws.pas.ULOGONINFO l = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        com.ums.ws.parm.admin.ULOGONINFO logoninfo = Util.convertLogonInfoParmAdmin(l);

        ParmAdmin pa = new ParmAdmin();
        
        int[] selection = lst_users.GetSelectedIndices();
        UBBUSER[] ulist = new UBBUSER[selection.Length];

        for(int i=0;i<selection.Length;++i) {
            UBBUSER u = new UBBUSER();
            u.l_userpk = long.Parse(lst_users.Items[selection[i]].Value);
            ulist[i] = u;
        }

        CB_USER_REGION_RESPONSE[] response = pa.GetUserRegion(logoninfo, ulist);

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

                header = new HtmlTableRow();
                hc = new HtmlTableCell();
                lbl_header = new Label();
                lbl_header.Text = "&nbsp;";
                hc.Controls.Add(lbl_header);
                header.Cells.Add(hc);
                tbl_output.Rows.Add(header);
            }
        }
        btn_export_to_csv.Visible = true;
    }
    protected void btn_export_to_csv_Click(object sender, EventArgs e)
    {

    }
}
