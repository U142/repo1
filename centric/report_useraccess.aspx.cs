using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Web.UI.HtmlControls;

using com.ums.ws.pas;
using com.ums.ws.parm.admin;

public partial class report_useraccess : System.Web.UI.Page
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
        int[] selection = lst_areas.GetSelectedIndices();
        UBBUSER[] ulist;
        ParmAdmin pa = new ParmAdmin();

        tbl_output.Rows.Clear();
        
        for (int i = 0; i < selection.Length; ++i)
        {
            ulist = pa.GetAccessPermissions(long.Parse(lst_areas.Items[selection[i]].Value));
            if (ulist.Length > 0)
            {
                HtmlTableRow header = new HtmlTableRow();
                HtmlTableCell hc = new HtmlTableCell();
                Label lbl_header = new Label();
                lbl_header.Text = "Area:";
                hc.Controls.Add(lbl_header);
                header.Cells.Add(hc);

                Label htxt = new Label();
                //TextBox htxt = new TextBox();
                htxt.Text = lst_areas.Items[selection[i]].Text;
                hc = new HtmlTableCell();
                hc.Controls.Add(htxt);
                header.Cells.Add(hc);
                tbl_output.Rows.Add(header);

                for (int j = 0; j < ulist.Length; ++j)
                {
                    HtmlTableRow row = new HtmlTableRow();
                    Label lbldesc = new Label();
                    lbldesc.Text = "User:";
                    HtmlTableCell cell = new HtmlTableCell();
                    cell.Controls.Add(lbldesc);
                    row.Cells.Add(cell);
                    cell = new HtmlTableCell();
                    Label txt = new Label();
                    txt.Text = ulist[j].sz_userid;
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
    }
}
