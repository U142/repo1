using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;
using System.Configuration;

using com.ums.ws.pas.admin;

public partial class report_useractivity : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        ULOGONINFO l = (ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            fillDropDown();
            PasAdmin pa = new PasAdmin();

            GetUsersResponse res = pa.doGetUsers(l);
            Hashtable users = new Hashtable();
            lst_users.Items.Add(new ListItem("Administrator","-1"));
            UBBUSER admin = new UBBUSER();
            admin.l_userpk = (long)-1;
            admin.sz_userid = "Administrator";
            users.Add((long)-1, admin);
            if (res.successful)
            {
                foreach(UBBUSER user in res.user)
                {
                    users.Add(user.l_userpk, user);
                    lst_users.Items.Add(new ListItem(user.sz_userid, user.l_userpk.ToString()));
                }
            }
            foreach (ListItem item in lst_users.Items)
            {
                item.Selected = true;
            }
            Session["users"] = users;
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
    private void fillDropDown()
    {
        /*
        System.Globalization.CultureInfo CI = new System.Globalization.CultureInfo("en-us", true);
        System.Threading.Thread.CurrentThread.CurrentUICulture = CI;
        DateTime month = Convert.ToDateTime("1/1/2000");
        
        for (int i = 0; i < 12; i++)
        {
            DateTime NextMont = month.AddMonths(i);
            ListItem list = new ListItem();
            list.Text = NextMont.ToString("MMMM");
            list.Value = NextMont.Month.ToString().PadLeft(2,'0');
            ddl_month.Items.Add(list);
        }
        */
        DateTime now = DateTime.Now;
        for (int i = 2010; i <= now.Year; i++)
        {
            ListItem list = new ListItem();
            list.Text = i.ToString();
            list.Value = i.ToString();
            ddl_year.Items.Add(list);
        }
    }

    protected void btn_show_click(object sender, EventArgs e)
    {
        ULOGONINFO l = (ULOGONINFO)Session["logoninfo"];
        PasAdmin pasa = new PasAdmin();
        Hashtable users = (Hashtable)Session["users"];

        int[] indices = lst_users.GetSelectedIndices();
        List<UBBUSER> selectedusers = new List<UBBUSER>();
        foreach (int i in indices)
        {
            long ting = long.Parse(lst_users.Items[i].Value);
            selectedusers.Add((UBBUSER)users[long.Parse(lst_users.Items[i].Value)]);
        }

        GetUserActivityResponse res = pasa.doGetUserActivity(l, createTimestamp(), selectedusers.ToArray());
        if (res.successful)
        {
            if (res.log.Length > 0)
            {
                Session["loglist"] = res.log;
                btn_export.Visible = true;
                // Do lots of parsing
                HtmlTableRow header = new HtmlTableRow();
                HtmlTableCell hc = new HtmlTableCell();
                Label lbl_header = new Label();
                lbl_header.Text = "Log id";
                hc.Controls.Add(lbl_header);
                header.Cells.Add(hc);

                Label htxt = new Label();
                //TextBox htxt = new TextBox();
                htxt.Text = "Username";
                hc = new HtmlTableCell();
                hc.Controls.Add(htxt);
                header.Cells.Add(hc);

                htxt = new Label();
                //TextBox htxt = new TextBox();
                htxt.Text = "Operation";
                hc = new HtmlTableCell();
                hc.Controls.Add(htxt);
                header.Cells.Add(hc);                

                htxt = new Label();
                //TextBox htxt = new TextBox();
                htxt.Text = "Timestamp";
                hc = new HtmlTableCell();
                hc.Controls.Add(htxt);
                header.Cells.Add(hc);

                htxt = new Label();
                //TextBox htxt = new TextBox();
                htxt.Text = "Description";
                hc = new HtmlTableCell();
                hc.Controls.Add(htxt);
                header.Cells.Add(hc);
                
                tbl_output.Rows.Add(header);

                String[] tmp = ConfigurationSettings.AppSettings["hide"].Split(',');
                HashSet<short> hide = new HashSet<short>();
                for (int i = 0; i < tmp.Length; ++i)
                    hide.Add(short.Parse(tmp[i]));

                for (int j = 0; j < res.log.Length; ++j)
                {
                    if (!hide.Contains(res.log[j].l_operation))
                    {
                        HtmlTableRow row = new HtmlTableRow();
                        Label lbldesc = new Label();
                        lbldesc.Text = res.log[j].l_id.ToString();
                        HtmlTableCell cell = new HtmlTableCell();
                        cell.Controls.Add(lbldesc);

                        row.Cells.Add(cell);
                        cell = new HtmlTableCell();
                        lbldesc = new Label();
                        if (res.log[j].l_userpk == -1)
                            lbldesc.Text = "Administrator";
                        else
                            lbldesc.Text = ((UBBUSER)users[res.log[j].l_userpk]).sz_userid;
                        cell.Controls.Add(lbldesc);
                        row.Cells.Add(cell);

                        cell = new HtmlTableCell();
                        lbldesc = new Label();
                        lbldesc.Text = ConfigurationSettings.AppSettings[res.log[j].l_operation.ToString()];
                        cell.Controls.Add(lbldesc);
                        row.Cells.Add(cell);

                        cell = new HtmlTableCell();
                        lbldesc = new Label();
                        lbldesc.Text = res.log[j].l_timestamp.ToString();
                        cell.Controls.Add(lbldesc);
                        row.Cells.Add(cell);

                        cell = new HtmlTableCell();
                        lbldesc = new Label();
                        lbldesc.Text = res.log[j].sz_desc;
                        cell.Controls.Add(lbldesc);
                        row.Cells.Add(cell);

                        tbl_output.Rows.Add(row);
                    }
                }
            }
            else
            {
                btn_export.Visible = false;
            }
        }
    }

    protected void btn_export_click(object sender, EventArgs e)
    {
        UPASLOG[] loglist = (UPASLOG[])Session["loglist"];
        Hashtable users = (Hashtable)Session["users"];
        Util.WriteUserActivityMonthlyToCSV(loglist, users);
    }

    protected void btn_deselect_click(object sender, EventArgs e)
    {
        lst_users.SelectedIndex = -1;
    }

    private long createTimestamp()
    {
        String timestamp = "";
        timestamp = ddl_year.SelectedValue + ddl_month.SelectedValue;
        return long.Parse(timestamp.PadRight(14, '0'));
    }

}
