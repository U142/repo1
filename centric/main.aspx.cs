using System;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using System.Globalization;
using System.Collections.Generic;

using com.ums.ws.pas;
using com.ums.ws.pas.admin;

public partial class main : System.Web.UI.Page 
{
    private USYSTEMMESSAGES messages;

    protected void Page_Load(object sender, EventArgs e)
    {
        USYSTEMMESSAGES messages = (USYSTEMMESSAGES)Session["messages"];
        if (messages == null)
            messages = new USYSTEMMESSAGES();

        if (!IsPostBack)
        {
            //txt_activate.Attributes.Add("readonly", "readonly");
            //txt_deactivate.Attributes.Add("readonly", "readonly");

            pasws pws = new pasws();
            com.ums.ws.pas.ULOGONINFO logon = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
            if(logon == null)
                Server.Transfer("logon.aspx");
            USYSTEMMESSAGES sysm = pws.GetSystemMessages(logon,0);
             
            for (int i = 0; i < sysm.news.newslist.Length; ++i)
            {
                lst_messages.Items.Add(new ListItem(sysm.news.newslist[i].sz_operatorname + " " + sysm.news.newslist[i].newstext.sz_news + " " + Helper.FormatDate(sysm.news.newslist[i].l_incident_start) + (sysm.news.newslist[i].l_incident_end == 0 ? "" : "-" + Helper.FormatDate(sysm.news.newslist[i].l_incident_end)), sysm.news.newslist[i].l_newspk.ToString()));
            }
            Session["messages"] = sysm;
        }
    }
    protected void btn_activate_message_Click(object sender, EventArgs e)
    {
        USYSTEMMESSAGES sysm = (USYSTEMMESSAGES)Session["messages"];
        if (sysm == null)
            sysm = new USYSTEMMESSAGES();

        // If it doesn't contain messages create a new newslist
        if (sysm.news.newslist == null || sysm.news.newslist.Length == 0)
        {
            sysm.news.newslist = new UBBNEWS[1];
            sysm.news.newslist[sysm.news.newslist.Length - 1] = new UBBNEWS();
        }
        else // If it does contain messages, create a bigger list to make room for the new message
        {
            UBBNEWS[] news = new UBBNEWS[sysm.news.newslist.Length + 1];
            for (int i = 0; i < sysm.news.newslist.Length; ++i)
                news[i] = sysm.news.newslist[i];
            sysm.news.newslist = news;
            sysm.news.newslist[sysm.news.newslist.Length - 1] = new UBBNEWS();
        }

        UBBNEWSTEXT newstext = new UBBNEWSTEXT();
        newstext.sz_news = txt_message.Text;
        sysm.news.newslist[sysm.news.newslist.Length - 1].newstext = newstext;
        sysm.news.newslist[sysm.news.newslist.Length-1].l_operator = int.Parse(ddl_operator.SelectedItem.Value);
        sysm.news.newslist[sysm.news.newslist.Length - 1].sz_operatorname = ddl_operator.SelectedItem.Text;
        sysm.news.newslist[sysm.news.newslist.Length - 1].l_type = int.Parse(ddl_type.SelectedItem.Value);
        sysm.news.newslist[sysm.news.newslist.Length - 1].f_active = 1;
        if(txt_activate.Text.Length>0)
        {
            //IFormatProvider format = new CultureInfo("nb-NO");
            IFormatProvider format = new CultureInfo("nl-NL");
            String ting = txt_activate.Text + " " + ddl_activate_h.SelectedValue + ":" + ddl_activate_m.SelectedValue;
            try
            {
                sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_start = long.Parse(txt_activate.Text.Substring(6, 4) + txt_activate.Text.Substring(3, 2) + txt_activate.Text.Substring(6, 2) + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
            }
            catch (Exception ex)
            {
                // skriv ut i validate
            }

            
        }
        if (txt_deactivate.Text.Length > 0)
        {
            //sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end = long.Parse(txt_activate.Text + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
            sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end = long.Parse(txt_deactivate.Text.Substring(6, 4) + txt_deactivate.Text.Substring(3, 2) + txt_deactivate.Text.Substring(0, 2) + ddl_deactivate_h.SelectedValue + ddl_deactivate_m.SelectedValue + "00");
            //IFormatProvider format = new CultureInfo("nb-NO");
            /*IFormatProvider format = new CultureInfo("nl-NL");
            String ting = txt_deactivate.Text + " " + ddl_deactivate_h.SelectedValue + ":" + ddl_deactivate_m.SelectedValue;
            try
            {
                sm.DateDeactivate = DateTime.Parse(txt_deactivate.Text + " " + ddl_deactivate_h.SelectedValue + ":" + ddl_deactivate_m.SelectedValue, format, DateTimeStyles.None);
            }
            catch (Exception ex)
            {
                // skriv ut i validate
            }*/
        }
        pasws ws = new pasws();
        com.ums.ws.pas.ULOGONINFO logon = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];

        // Stores the new message and returns it with l_newspk
        sysm.news.newslist[sysm.news.newslist.Length - 1] = ws.UpdateSystemMessage(logon, sysm.news.newslist[sysm.news.newslist.Length - 1]);
        sysm.news.newslist[sysm.news.newslist.Length - 1].l_deptpk = logon.l_deptpk;

        UBBNEWS tsm = (UBBNEWS)Session["edit"];
        if (tsm != null)
            lst_messages.Items.Remove(lst_messages.SelectedItem);
        lst_messages.Items.Add(new ListItem(sysm.news.newslist[sysm.news.newslist.Length - 1].sz_operatorname + " " + sysm.news.newslist[sysm.news.newslist.Length - 1].newstext.sz_news + " " + Helper.FormatDate(sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_start) + (sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end == 0 ? "" : "-" + Helper.FormatDate(sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end)), sysm.news.newslist[sysm.news.newslist.Length - 1].l_newspk.ToString()));
        //messages.Remove(tsm);
        //messages.Add(sm);
        Session["messages"] = sysm;
        reset();
        Session.Remove("edit");
    }

    protected void btn_edit_Click(object sender, EventArgs e)
    {
        UBBNEWS news = null;

        if(lst_messages.SelectedIndex != -1)
            news = getMessage(long.Parse(lst_messages.SelectedValue));

        if (news != null)
        {
            Session["edit"] = news;
            Server.Transfer("systemmessages.aspx");
            /*
           
            ddl_operator.SelectedValue = news.l_operator.ToString();
            ddl_type.SelectedValue = news.l_type.ToString();
            txt_message.Text = news.newstext.sz_news;

            if (news.l_incident_start == 0)
            {
                txt_activate.Text = "";
                ddl_activate_h.SelectedValue = "00";
                ddl_activate_m.SelectedValue = "00";
            }
            else
            {
                txt_activate.Text = Helper.FormatDate(news.l_incident_start).Substring(0,10);
                ddl_activate_h.SelectedValue = news.l_incident_start.ToString().Substring(8,2);
                ddl_activate_m.SelectedValue = news.l_incident_start.ToString().Substring(10,2);
            }

            if (news.l_incident_end == 0)
            {
                txt_deactivate.Text = "";
                ddl_deactivate_h.SelectedValue = "00";
                ddl_deactivate_m.SelectedValue = "00";
            }
            else
            {
                txt_deactivate.Text = Helper.FormatDate(news.l_incident_end).Substring(0, 10);
                ddl_deactivate_h.SelectedValue = news.l_incident_end.ToString().Substring(8, 2);
                ddl_deactivate_m.SelectedValue = news.l_incident_end.ToString().Substring(10, 2);
            }*/
        }

    }
    private void reset()
    {
        txt_message.Text = "";
        ddl_operator.SelectedIndex = 0;
        ddl_type.SelectedIndex = 0;

        txt_activate.Text = "";
        ddl_activate_h.SelectedValue = "00";
        ddl_activate_m.SelectedValue = "00";

        txt_deactivate.Text = "";
        ddl_deactivate_h.SelectedValue = "00";
        ddl_deactivate_m.SelectedValue = "00";
    }
    protected void lst_messages_selectedindex(object sender, EventArgs e)
    {
        USYSTEMMESSAGES messages = (USYSTEMMESSAGES)Session["messages"];

        int id = int.Parse(lst_messages.SelectedValue);
        
        for (int i = 0; i < messages.news.newslist.Length; ++i)
        {
            if (id == messages.news.newslist[i].l_newspk)
                txt_view_message.Text = messages.news.newslist[i].newstext.sz_news;
        }
    }

    private UBBNEWS getMessage(long id)
    {

        USYSTEMMESSAGES messages = (USYSTEMMESSAGES)Session["messages"];

        for (int i = 0; i < messages.news.newslist.Length; ++i)
        {
            if (id == messages.news.newslist[i].l_newspk)
                return messages.news.newslist[i];
        }
        
        return null;
        
    }

    protected void btn_deactivate_Click(object sender, EventArgs e)
    {
        PasAdmin pasa = new PasAdmin();
        com.ums.ws.pas.ULOGONINFO logon = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        DeactivateMessageResponse res = pasa.doDeactivateMessage(Util.convertLogonInfoPasAdmin(logon), long.Parse(lst_messages.SelectedValue));
        if (res.successful)
            lst_messages.Items.Remove(lst_messages.SelectedItem);
                
    }
}
