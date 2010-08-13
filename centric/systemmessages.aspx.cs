﻿using System;
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

public partial class systemmessages : System.Web.UI.Page
{
    private USYSTEMMESSAGES messages;

    protected void Page_Load(object sender, EventArgs e)
    {
        messages = (USYSTEMMESSAGES)Session["messages"];
        if (messages == null)
            messages = new USYSTEMMESSAGES();

        if (!IsPostBack)
        {
            pasws pws = new pasws();
            ULOGONINFO logon = (ULOGONINFO)Session["logoninfo"];
            if (logon == null)
                Server.Transfer("logon.aspx");
            USYSTEMMESSAGES sysm = pws.GetAllSystemMessages(logon);

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
        sysm.news.newslist[sysm.news.newslist.Length - 1].l_operator = int.Parse(ddl_operator.SelectedItem.Value);
        sysm.news.newslist[sysm.news.newslist.Length - 1].l_type = int.Parse(ddl_type.SelectedItem.Value);
        if (txt_activate.Text.Length > 0)
        {
            //IFormatProvider format = new CultureInfo("nb-NO");
            IFormatProvider format = new CultureInfo("nl-NL");
            String ting = txt_activate.Text + " " + ddl_activate_h.SelectedValue + ":" + ddl_activate_m.SelectedValue;
            try
            {
                sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_start = long.Parse(txt_activate.Text + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
            }
            catch (Exception ex)
            {
                // skriv ut i validate
            }


        }
        if (txt_deactivate.Text.Length > 0)
        {
            sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end = long.Parse(txt_activate.Text + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
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
        UBBNEWS tsm = (UBBNEWS)Session["edit"];
        if (tsm != null)
            lst_messages.Items.Remove(lst_messages.SelectedItem);
        lst_messages.Items.Add(new ListItem(sysm.news.newslist[sysm.news.newslist.Length - 1].sz_operatorname + " " + sysm.news.newslist[sysm.news.newslist.Length - 1].newstext.sz_news + " " + Helper.FormatDate(sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_start) + (sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end == 0 ? "" : "-" + Helper.FormatDate(sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end)), sysm.news.newslist[sysm.news.newslist.Length - 1].l_newspk.ToString()));
        //messages.Remove(tsm);
        //messages.Add(sm);
        Session["messages"] = messages;
        reset();
        Session.Remove("edit");
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {
        UBBNEWS news = null;
        ULOGONINFO logon;

        if (lst_messages.SelectedIndex != -1)
            news = getMessage(long.Parse(lst_messages.SelectedValue));

        if (news != null)
        {
            logon = (ULOGONINFO)Session["logoninfo"];
            news.l_deptpk = logon.l_deptpk;
            news.l_userpk = logon.l_userpk;
            news.l_type = long.Parse(ddl_type.SelectedValue);
            news.l_operator = int.Parse(ddl_operator.SelectedValue);
            news.l_incident_start = long.Parse(txt_activate.Text + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
            news.l_incident_end = long.Parse(txt_deactivate.Text + ddl_deactivate_h.SelectedValue + ddl_deactivate_m.SelectedValue + "00");
            news.newstext.sz_news = txt_message.Text;

            pasws pws = new pasws();
            news = pws.UpdateSystemMessage(logon, news);

            Session["edit"] = news;

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
                txt_activate.Text = Helper.FormatDate(news.l_incident_start).Substring(0, 10);
                ddl_activate_h.SelectedValue = news.l_incident_start.ToString().Substring(8, 2);
                ddl_activate_m.SelectedValue = news.l_incident_start.ToString().Substring(10, 2);
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
            }
          
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
        int id = int.Parse(lst_messages.SelectedValue);

        for (int i = 0; i < messages.news.newslist.Length; ++i)
        {
            if (id == messages.news.newslist[i].l_newspk)
            {
                txt_message.Text = messages.news.newslist[i].newstext.sz_news;
                ddl_operator.SelectedValue = messages.news.newslist[i].l_operator.ToString();
                ddl_type.SelectedValue = messages.news.newslist[i].l_type.ToString();

                if (messages.news.newslist[i].l_incident_start.ToString().Length >= 8)
                    txt_activate.Text = messages.news.newslist[i].l_incident_start.ToString().Substring(0, 8);
                else
                     txt_activate.Text = "";
                if (messages.news.newslist[i].l_incident_start.ToString().Length >= 10)
                    ddl_activate_h.SelectedValue = messages.news.newslist[i].l_incident_start.ToString().Substring(8, 2);
                else
                    ddl_activate_h.SelectedIndex = 0;
                if (messages.news.newslist[i].l_incident_start.ToString().Length >= 12)
                    ddl_activate_m.SelectedValue = messages.news.newslist[i].l_incident_start.ToString().Substring(10, 2);
                else
                    ddl_activate_m.SelectedIndex = 0;
                if (messages.news.newslist[i].l_incident_end.ToString().Length >= 8)
                    txt_deactivate.Text = messages.news.newslist[i].l_incident_end.ToString().Substring(0, 8);
                else
                    txt_deactivate.Text = "";
                if(messages.news.newslist[i].l_incident_end.ToString().Length >= 10)
                    ddl_deactivate_h.SelectedValue = messages.news.newslist[i].l_incident_end.ToString().Substring(8, 2);
                else
                    ddl_deactivate_h.SelectedIndex = 0;
                if (messages.news.newslist[i].l_incident_end.ToString().Length >= 12)
                    ddl_deactivate_m.SelectedValue = messages.news.newslist[i].l_incident_end.ToString().Substring(10, 2);
                else
                    ddl_deactivate_m.SelectedIndex = 0;

                if (messages.news.l_timestamp_db < messages.news.newslist[i].l_incident_end || messages.news.newslist[i].l_incident_end == 0) //active
                {
                    txt_message.Enabled = true;
                    ddl_operator.Enabled = true;
                    ddl_type.Enabled = true;
                    ddl_activate_h.Enabled = true;
                    ddl_activate_m.Enabled = true;
                    txt_activate.Enabled = true;
                    ddl_deactivate_h.Enabled = true;
                    ddl_deactivate_m.Enabled = true;
                    txt_deactivate.Enabled = true;
                }
                else // Only allow changes to deactivate
                {
                    txt_message.Enabled = false;
                    ddl_operator.Enabled = false;
                    ddl_type.Enabled = false;
                    ddl_activate_h.Enabled = false;
                    ddl_activate_m.Enabled = false;
                    txt_activate.Enabled = false;
                    ddl_deactivate_h.Enabled = true;
                    ddl_deactivate_m.Enabled = true;
                    txt_deactivate.Enabled = true;
                }
            }
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
}
