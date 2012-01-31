using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Configuration;
using System.Web.UI.HtmlControls;

using com.ums.ws.pas.status;
using com.ums.ws.pas;
using com.ums.ws.pas.admin;
using System.ServiceModel;

public partial class report_monthly : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        PasStatus ps = new PasStatus();
        ps.Url = ConfigurationManager.AppSettings["PasStatus"];
        com.ums.ws.pas.admin.ULOGONINFO l = ( com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            btn_messages_month.Visible = false;
            btn_performance_month.Visible = false;
            btn_sysmessages_month.Visible = false;

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
        com.ums.ws.pas.admin.ULOGONINFO l = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];

        PasAdminSoapClient pa = new PasAdminSoapClient();
        pa.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
        GetTotalNumberOfMessagesResponse tnmres = pa.doGetTotalNumberOfMessages(l, createTimestamp());
        Session["totalMessages"] = tnmres;
        HtmlTableRow header = new HtmlTableRow();
        HtmlTableCell hc = new HtmlTableCell();
        Label lbl_header = new Label();

        if (tnmres.successful)
        {
            lbl_header.Text = "Total number of events";
            hc.Controls.Add(lbl_header);
            header.Cells.Add(hc);

            lbl_header = new Label();
            lbl_header.Text = "Total number of regional messages sent";
            hc = new HtmlTableCell();
            hc.Controls.Add(lbl_header);
            header.Cells.Add(hc);

            lbl_header = new Label();
            lbl_header.Text = "Total number of national messages sent";
            hc = new HtmlTableCell();
            hc.Controls.Add(lbl_header);
            header.Cells.Add(hc);

            lbl_header = new Label();
            lbl_header.Text = "Total number of test messages sent";
            hc = new HtmlTableCell();
            hc.Controls.Add(lbl_header);
            header.Cells.Add(hc);

            tbl_total_messages.Rows.Add(header);

            HtmlTableRow row = new HtmlTableRow();
            Label txt = new Label();
            txt.Text = tnmres.total_events.ToString();
            HtmlTableCell cell = new HtmlTableCell();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = tnmres.total_regional.ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = tnmres.total_national.ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = tnmres.total_test.ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_total_messages.Rows.Add(row);

            btn_messages_total.Visible = true;
        }
        else
            btn_messages_total.Visible = false;

        PasStatus pasws = new PasStatus();
        pasws.Url = ConfigurationManager.AppSettings["PasStatus"];

        CB_MESSAGE_MONTHLY_REPORT_RESPONSE[] res = pasws.GetAllMesagesThisMonth(Util.convertLogonInfoPasStatus(l), createTimestamp());
        
        Session["messages_month"] = res;

        tbl_output.Rows.Clear();

        header = new HtmlTableRow();
        hc = new HtmlTableCell();
        lbl_header = new Label();
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
        lbl_header.Text = "Operator";
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
            txt.Text = Util.sendingType(res[i]);
            HtmlTableCell cell = new HtmlTableCell();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            
            cell = new HtmlTableCell();
            cell.Attributes.Add("style", "WORD-BREAK:BREAK-ALL;");
            txt = new Label();
            txt.Text = Server.HtmlEncode(res[i].sz_text);
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = res[i].sz_userid;
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = res[i].sz_operatorname;
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
            txt.Text = Math.Round(res[i].l_performance,1).ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_output.Rows.Add(row);
        }

        if(res.Length > 0)
            btn_messages_month.Visible = true;
        else
            btn_messages_month.Visible = false;

        // System operator performance this month
        res = pasws.GetOperatorPerformanceThisMonth(Util.convertLogonInfoPasStatus(l), createTimestamp());
        
        Session["performance_month"] = res;

        tbl_operatorperformance.Rows.Clear();
        header = new HtmlTableRow();

        lbl_header = new Label();
        lbl_header.Text = "Operator";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "Performance";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        tbl_operatorperformance.Rows.Add(header);

        for (int i = 0; i < res.Length; ++i)
        {
            HtmlTableRow row = new HtmlTableRow();
            Label txt = new Label();
            txt.Text = res[i].sz_operatorname;
            HtmlTableCell cell = new HtmlTableCell();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);

            cell = new HtmlTableCell();
            txt = new Label();
            txt.Text = Math.Round(res[i].l_performance, 1).ToString();
            cell.Controls.Add(txt);
            row.Cells.Add(cell);
            tbl_operatorperformance.Rows.Add(row);
        }

        if (res.Length > 0)
            btn_performance_month.Visible = true;
        else
            btn_performance_month.Visible = false;

        // System messages this month
        paswsSoapClient pas = new paswsSoapClient();
        pas.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);

        USYSTEMMESSAGES msg = pas.GetSystemMessagesMonth(Util.convertLogonInfoPas(l), createTimestamp());
        
        Session["sysmessage_month"] = msg;

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
            cell.Attributes.Add("style", "WORD-BREAK:BREAK-ALL;");
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
            txt.Text = Util.sysMessageType(news[i].l_type);
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

        if (news.Length > 0)
            btn_sysmessages_month.Visible = true;
        else
            btn_sysmessages_month.Visible = false;
    }

    protected void btn_messages_total_month_Click(object sender, EventArgs e)
    {
        GetTotalNumberOfMessagesResponse tnmres = (GetTotalNumberOfMessagesResponse) Session["totalMessages"];
        Util.WriteMonthlyTotalReportToCSV(tnmres.total_events, tnmres.total_regional, tnmres.total_national, tnmres.total_test);
    }

    protected void btn_messages_month_Click(object sender, EventArgs e)
    {
        Util.WriteMonthlyReportToCSV((CB_MESSAGE_MONTHLY_REPORT_RESPONSE[])Session["messages_month"]);
    }
    
    protected void btn_performance_month_Click(object sender, EventArgs e)
    {
        Util.WriteMonthlyPerformanceToCSV((CB_MESSAGE_MONTHLY_REPORT_RESPONSE[])Session["performance_month"]);
    }
    
    protected void btn_sysmessages_month_Click(object sender, EventArgs e)
    {
        Util.WriteMonthlySystemMessagesToCSV((USYSTEMMESSAGES)Session["sysmessage_month"]);
    }

    
}
