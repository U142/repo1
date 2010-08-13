using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Web.UI.HtmlControls;

using com.ums.ws.pas.status;
using com.ums.ws.pas;

public partial class report_monthly : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        PasStatus ps = new PasStatus();
        com.ums.ws.pas.ULOGONINFO l = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            fillDropDown();
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

    private long createTimestamp()
    {
        String timestamp = "";
        timestamp = ddl_year.SelectedValue + ddl_month.SelectedValue;
        return long.Parse(timestamp.PadRight(14, '0'));
    }

    protected void btn_showClick(object sender, EventArgs e)
    {
        PasStatus pasws = new PasStatus();
        com.ums.ws.pas.ULOGONINFO l = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        CB_MESSAGE_MONTHLY_REPORT_RESPONSE[] res = pasws.GetAllMesagesThisMonth(Util.convertLogonInfoPasStatus(l), createTimestamp());

        tbl_output.Rows.Clear();

        HtmlTableRow header = new HtmlTableRow();
        HtmlTableCell hc = new HtmlTableCell();
        Label lbl_header = new Label();
        lbl_header.Text = "Regional/National/Test:";
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Message";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Username";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "#addressed cells";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Operator performance";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        tbl_output.Rows.Add(header);

        for (int i = 0; i < res.Length; ++i)
        {
            HtmlTableRow row = new HtmlTableRow();
            Label txt = new Label();
            txt.Text = "N/A";
            HtmlTableCell cell = new HtmlTableCell();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            
            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = res[i].sz_text;
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = "N/A";
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = res[i].l_addressedcells.ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = "N/A";
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);
        }

        // System messages this month
        pasws pas = new pasws();
        USYSTEMMESSAGES msg = pas.GetSystemMessagesMonth(l, createTimestamp());
        tbl_sysmessages.Rows.Clear();
        header = new HtmlTableRow();

        lbl_header = new Label();
        lbl_header.Text = "Message";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Operator";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Type";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Activated on";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Deactivated on";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        tbl_sysmessages.Rows.Add(header);

        UBBNEWS[] news = msg.news.newslist;

        for (int i = 0; i < news.Length; ++i)
        {
            // Message
            HtmlTableRow row = new HtmlTableRow();
            Label txt = new Label();
            txt.Text = news[i].newstext.sz_news;
            HtmlTableCell cell = new HtmlTableCell();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            
            // Operator
            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = news[i].sz_operatorname;
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            
            // Type
            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = news[i].l_type.ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            
            // Activated on
            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = Util.convertDate(news[i].l_incident_start);
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            
            // Deactivated on
            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = Util.convertDate(news[i].l_incident_end);
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_sysmessages.Rows.Add(row);
        }
    }
}
