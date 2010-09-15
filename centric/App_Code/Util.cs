using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Web;
using System.Data;
using System.IO;
using System.Text;
using com.ums.ws.pas.status;
using com.ums.ws.pas;
using com.ums.ws.parm.admin;
using com.ums.ws.pas.admin;
using System.Configuration;
using System.Text.RegularExpressions;

/// <summary>
/// Summary description for Util
/// </summary>
public class Util
{
    public static readonly Regex GSM_Alphabet_Regex = new Regex("[^a-zA-Z0-9 .∆_ΦΓΛΩΠΨΣΘΞ@£$¥èéùìòÇØøÅåÆæßÉÄÖÑÜ§¿äöñüà+,/:;<=>?¡|^€{}*!#¤%&'()\r\n\\\\\\[\\]\"~-]");

    public static CheckAccessResponse setOccupied(com.ums.ws.pas.admin.ULOGONINFO l, ACCESSPAGE page, Boolean f_lock) {
        PasAdmin pa = new PasAdmin();
        CheckAccessResponse ares = pa.doSetOccupied(l, page, f_lock);
        return ares;
    }
    public static String sysMessageType(long type)
    {
        switch (type)
        {
            case 0:
                return "Planned outage";
            case 1:
                return "Unplanned outage";
            case 2:
                return "Other";
            default:
                return "Unknown";
        }
    }

    public static String userType(long type)
    {
        switch (type)
        {
            case 2:
                return "Regional";
            case 3:
                return "Super Regional";
            case 5:
                return "National";
            case 7: 
                return "Administrator";
            default:
                return "Unknown";
        }
    }

    public static String sendingType(CB_MESSAGE_MONTHLY_REPORT_RESPONSE res)
    {
        if(res.l_type == 70)
            return "Test";
        else if(res.l_type == 7 && res.l_group == 16)
            return "National";
        else if (res.l_type == 7 && (res.l_group == 2 || res.l_group == 8 || res.l_group == 32))
            return "Regional";
        else
            return "Unknown";
    }

    public static String padForListBox(UBBNEWS news)
    {
        String listboxthingy = "";

        listboxthingy = news.sz_operatorname.PadRight(8, '#') + " " + Helper.FormatDate(news.l_incident_start) + (news.l_incident_end.ToString().Length > 1 ? "-" + Helper.FormatDate(news.l_incident_end) : "".PadRight(17, '#')) + " " + news.newstext.sz_news;


        return HttpUtility.HtmlDecode(listboxthingy.Replace("#", "&nbsp;"));
    }

    public static com.ums.ws.parm.admin.ULOGONINFO convertLogonInfoParmAdmin(com.ums.ws.pas.admin.ULOGONINFO l)
    {
        com.ums.ws.parm.admin.ULOGONINFO logoninfo = new com.ums.ws.parm.admin.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static com.ums.ws.pas.status.ULOGONINFO convertLogonInfoPasStatus(com.ums.ws.pas.admin.ULOGONINFO l)
    {
        com.ums.ws.pas.status.ULOGONINFO logoninfo = new com.ums.ws.pas.status.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static com.ums.ws.pas.admin.ULOGONINFO convertLogonInfoPasAdmin(com.ums.ws.pas.admin.ULOGONINFO l)
    {
        com.ums.ws.pas.admin.ULOGONINFO logoninfo = new com.ums.ws.pas.admin.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static com.ums.ws.pas.ULOGONINFO convertLogonInfoPas(com.ums.ws.pas.admin.ULOGONINFO l)
    {
        com.ums.ws.pas.ULOGONINFO logoninfo = new com.ums.ws.pas.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static String convertDate(long l_date)
    {
        String date = l_date.ToString();
        if (l_date == 0)
            return "";
        else if (l_date.ToString().Length == 8)
            return date.Substring(6, 2) + "-" + date.Substring(4, 2) + "-" + date.Substring(0, 4);
        else
            return date.Substring(6, 2) + "-" + date.Substring(4, 2) + "-" + date.Substring(0, 4) + " " + date.Substring(8, 2) + ":" + date.Substring(10, 2);
    }

    private static void AddComma(string value, StringBuilder stringBuilder)
    {
        value = value.Replace('\n', ' ');
        value = value.Replace('\r', ' ');
        stringBuilder.Append(value.Replace(',', '.'));
        stringBuilder.Append(", ");
    }

    private static void AddLast(string value, StringBuilder stringBuilder)
    {
        value = value.Replace('\n', ' ');
        value = value.Replace('\r', ' ');
        stringBuilder.Append(value.Replace(',', '.'));
    }

    /*********************************
     ***** Monthly total messages ****
     *********************************/
    public static void WriteMonthlyTotalReportToCSV(long total_events, long total_regional, long total_national, long total_test)
    {
        string attachment = "attachment; filename=MonthlyTotalReport.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteMonthlyTotalReportColumnName();
        WriteMonthlyTotalReport(total_events, total_regional, total_national, total_test);
        
        HttpContext.Current.Response.End();
    }

    private static void WriteMonthlyTotalReport(long total_events, long total_regional, long total_national, long total_test)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma(total_events.ToString(), stringBuilder);
        AddComma(total_regional.ToString(), stringBuilder);
        AddComma(total_national.ToString(), stringBuilder);
        AddLast(total_test.ToString(), stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteMonthlyTotalReportColumnName()
    {
        string columnNames = "Total number of events, Total number of regional messages sent, Total number of national messages sent, Total number of test messages sent";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    /*************************
     ***** Monthly report ****
     *************************/
    public static void WriteMonthlyReportToCSV(CB_MESSAGE_MONTHLY_REPORT_RESPONSE[] monthlyreport)
    {
        string attachment = "attachment; filename=MonthlyReport.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteMonthlyReportColumnName();
        foreach (CB_MESSAGE_MONTHLY_REPORT_RESPONSE report in monthlyreport)
        {
            WriteMonthlyReport(report);
        }
        HttpContext.Current.Response.End();
    }

    private static void WriteMonthlyReport(CB_MESSAGE_MONTHLY_REPORT_RESPONSE monthlyreport)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma("\"" + Util.sendingType(monthlyreport) + "\"", stringBuilder);
        AddComma("\"" + monthlyreport.sz_text + "\"", stringBuilder);
        AddComma("\"" + monthlyreport.sz_userid + "\"", stringBuilder);
        AddComma("\"" + monthlyreport.sz_operatorname + "\"", stringBuilder);
        AddComma(monthlyreport.l_addressedcells.ToString(), stringBuilder);
        AddLast(Math.Round(monthlyreport.l_performance,1).ToString(), stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteMonthlyReportColumnName()
    {
        string columnNames = "Regional/National/Test, Message, Username, Operator, #addressed cells, Operator performance";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    /*************************
     ** Monthly performance **
     *************************/
    private static void WriteMonthlyPerformanceColumnName()
    {
        string columnNames = "Operator, Performance";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    public static void WriteMonthlyPerformanceToCSV(CB_MESSAGE_MONTHLY_REPORT_RESPONSE[] MonthlyPerformance)
    {
        string attachment = "attachment; filename=MonthlyPerformance.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteMonthlyPerformanceColumnName();
        foreach (CB_MESSAGE_MONTHLY_REPORT_RESPONSE report in MonthlyPerformance)
        {
            WriteMonthlyPerformance(report);
        }
        HttpContext.Current.Response.End();
    }

    private static void WriteMonthlyPerformance(CB_MESSAGE_MONTHLY_REPORT_RESPONSE performance)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma("\"" + performance.sz_operatorname + "\"", stringBuilder);
        AddLast(Math.Round(performance.l_performance, 1).ToString(), stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }
    
    /*************************
     ***** Sysmessages *******
     *************************/
    public static void WriteMonthlySystemMessagesToCSV(USYSTEMMESSAGES sysmessages)
    {
        string attachment = "attachment; filename=SysmessageReport.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteSystemMessagesColumnName();
        foreach (UBBNEWS sysmessage in sysmessages.news.newslist)
        {
            WriteMonthlySystemMessages(sysmessage);
        }
        HttpContext.Current.Response.End();
    }
    private static void WriteMonthlySystemMessages(UBBNEWS sysmessage)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma("\"" + sysmessage.newstext.sz_news + "\"", stringBuilder);
        AddComma("\"" + sysmessage.sz_operatorname + "\"", stringBuilder);
        AddComma("\"" + Util.sysMessageType(sysmessage.l_type) + "\"", stringBuilder);
        AddComma("\"" + Util.convertDate(sysmessage.l_incident_start) + "\"", stringBuilder);
        AddLast("\"" + Util.convertDate(sysmessage.l_incident_end) + "\"", stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteSystemMessagesColumnName()
    {
        string columnNames = "Message, Operator, Type, Activated on, Deactivated on";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    /*******************************
     * Monthly user activity       *
     *******************************/
    public static void WriteUserActivityMonthlyToCSV(UPASLOG[] loglist, Hashtable users)
    {
        string attachment = "attachment; filename=UserActivity.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteUserActivityMonthlyColumnName();
        
        String[] tmp = ConfigurationSettings.AppSettings["hide"].Split(',');
        HashSet<short> hide = new HashSet<short>();
        for (int i = 0; i < tmp.Length; ++i)
            hide.Add(short.Parse(tmp[i]));

        foreach (UPASLOG log in loglist)
        {
            if(!hide.Contains(log.l_operation))
                WriteUserActivityMonthly(log, users);
        }
        HttpContext.Current.Response.End();
    }
    private static void WriteUserActivityMonthly(UPASLOG log, Hashtable users)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma(log.l_id.ToString(), stringBuilder);
        if (log.l_userpk == -1)
            AddComma("\"Administrator\"", stringBuilder);
        else
            if (log.l_userpk != 0)
                AddComma("\"" + ((com.ums.ws.pas.admin.UBBUSER)users[log.l_userpk]).sz_userid + "\"", stringBuilder);
            else
                AddComma("\"Unknown\"", stringBuilder);
        AddComma("\"" + ConfigurationSettings.AppSettings[log.l_operation.ToString()] + "\"", stringBuilder);
        AddComma(log.l_timestamp.ToString(), stringBuilder);
        AddLast("\"" + log.sz_desc + "\"", stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteUserActivityMonthlyColumnName()
    {
        string columnNames = "Log id, Username, Operation, Timestamp, Description";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    /*******************************
     * Access permissions per user *
     *******************************/
    public static void WriteAccessPerUserToCSV(CB_USER_REGION_RESPONSE[] region)
    {
        string attachment = "attachment; filename=AccessPermissions.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteAccessPerUserColumnName();
        foreach (CB_USER_REGION_RESPONSE reg in region)
        {
            foreach(PAOBJECT obj in reg.regionlist)
                if (obj.l_deptpk != int.Parse(ConfigurationSettings.AppSettings["admin_department"]))
                    WriteAccessPerUser(obj, reg);
        }
        HttpContext.Current.Response.End();
    }
    private static void WriteAccessPerUser(PAOBJECT obj, CB_USER_REGION_RESPONSE res)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma("\"" + res.user.sz_userid + "\"", stringBuilder);
        AddLast("\"" + obj.sz_name + "\"", stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteAccessPerUserColumnName()
    {
        string columnNames = "Username, Authorization area name,";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    /*******************************
     * Users per access permission *
     *******************************/
    public static void WriteUsersPerAccessPermissionToCSV(List<com.ums.ws.parm.admin.UBBUSER[]> userlist, String[] areaname)
    {
        string attachment = "attachment; filename=AccessPermissions.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteUsersPerAccessPermissionColumnName();
        for(int i=0; i<areaname.Length;++i)
        {
            foreach (com.ums.ws.parm.admin.UBBUSER user in userlist[i])
                WriteUsersPerAccessPermission(user, areaname[i]);
        }
        HttpContext.Current.Response.End();
    }
    private static void WriteUsersPerAccessPermission(com.ums.ws.parm.admin.UBBUSER user, string areaname)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma("\"" + areaname + "\"", stringBuilder);
        AddLast("\"" + user.sz_userid + "\"", stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteUsersPerAccessPermissionColumnName()
    {
        string columnNames = "Authorization area name, Username";
        HttpContext.Current.Response.Write(columnNames);
        HttpContext.Current.Response.Write(Environment.NewLine);
    }


}
