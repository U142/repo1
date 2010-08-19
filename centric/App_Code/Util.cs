using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data;
using System.IO;
using System.Text;
using com.ums.ws.pas.status;
using com.ums.ws.pas;
using com.ums.ws.parm.admin;

/// <summary>
/// Summary description for Util
/// </summary>
public class Util
{
    public static com.ums.ws.parm.admin.ULOGONINFO convertLogonInfoParmAdmin(com.ums.ws.pas.ULOGONINFO l)
    {
        com.ums.ws.parm.admin.ULOGONINFO logoninfo = new com.ums.ws.parm.admin.ULOGONINFO();
        logoninfo.l_deptpk = l.l_deptpk;
        logoninfo.l_userpk = l.l_userpk;
        logoninfo.l_comppk = l.l_comppk;
        logoninfo.sz_password = l.sz_password;
        logoninfo.sessionid = l.sessionid;
        return logoninfo;
    }

    public static com.ums.ws.pas.status.ULOGONINFO convertLogonInfoPasStatus(com.ums.ws.pas.ULOGONINFO l)
    {
        com.ums.ws.pas.status.ULOGONINFO logoninfo = new com.ums.ws.pas.status.ULOGONINFO();
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
        else
            return date.Substring(6,2) + "-" + date.Substring(4, 2) + "-" + date.Substring(0, 4) + " " + date.Substring(8,2) + ":" + date.Substring(10,2);
    }

    private static void AddComma(string value, StringBuilder stringBuilder)
    {
        stringBuilder.Append(value.Replace(',', ' '));
        stringBuilder.Append(", ");
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
        AddComma("N/A", stringBuilder);
        AddComma(monthlyreport.sz_text, stringBuilder);
        AddComma("N/A", stringBuilder);
        AddComma(monthlyreport.l_addressedcells.ToString(), stringBuilder);
        AddComma(monthlyreport.l_performance.ToString(), stringBuilder);
        HttpContext.Current.Response.Write(stringBuilder.ToString());
        HttpContext.Current.Response.Write(Environment.NewLine);
    }

    private static void WriteMonthlyReportColumnName()
    {
        string columnNames = "Regional/National/Test, Message, Username, #addressed cells, Operator performance";
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
        AddComma(performance.sz_operatorname, stringBuilder);
        AddComma(performance.l_performance.ToString(), stringBuilder);
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
        AddComma(sysmessage.newstext.sz_news, stringBuilder);
        AddComma(sysmessage.sz_operatorname, stringBuilder);
        AddComma(sysmessage.l_type.ToString(), stringBuilder);
        AddComma(sysmessage.l_incident_start.ToString(), stringBuilder);
        AddComma(sysmessage.l_incident_end.ToString(), stringBuilder);
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
            WriteAccessPerUser(reg);
        }
        HttpContext.Current.Response.End();
    }
    private static void WriteAccessPerUser(CB_USER_REGION_RESPONSE reg)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < reg.regionlist.Length; ++i)
        {
            AddComma(reg.user.sz_userid, stringBuilder);
            AddComma(reg.regionlist[i].sz_name, stringBuilder);
        }
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
    public static void WriteUsersPerAccessPermissionToCSV(UBBUSER[] userlist, string areaname)
    {
        string attachment = "attachment; filename=AccessPermissions.csv";
        HttpContext.Current.Response.Clear();
        HttpContext.Current.Response.ClearHeaders();
        HttpContext.Current.Response.ClearContent();
        HttpContext.Current.Response.AddHeader("content-disposition", attachment);
        HttpContext.Current.Response.ContentType = "text/csv";
        HttpContext.Current.Response.AddHeader("Pragma", "public");
        WriteAccessPerUserColumnName();
        foreach (UBBUSER user in userlist)
        {
            WriteUsersPerAccessPermission(user, areaname);
        }
        HttpContext.Current.Response.End();
    }
    private static void WriteUsersPerAccessPermission(UBBUSER user, string areaname)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AddComma(areaname, stringBuilder);
        AddComma(user.sz_name, stringBuilder);
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
