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
            //txt_activate.Attributes.Add("readonly","readonly");
            //txt_deactivate.Attributes.Add("readonly", "readonly");
            pasws pws = new pasws();
            pws.Url = ConfigurationSettings.AppSettings["Pas"];
            com.ums.ws.pas.admin.ULOGONINFO logon = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
            if (logon == null)
                Server.Transfer("logon.aspx");
            USYSTEMMESSAGES sysm = pws.GetAllSystemMessages(Util.convertLogonInfoPas(logon));

            IEnumerable<UBBNEWS> sorter = sysm.news.newslist.OrderBy(n => n.l_incident_start);

            foreach (UBBNEWS n in sorter)
            {
                lst_messages.Items.Add(new ListItem(Util.padForListBox(n), n.l_newspk.ToString()));
            }
            /*
            for (int i = 0; i < sysm.news.newslist.Length; ++i)
            {
                //lst_messages.Items.Add(new ListItem(sysm.news.newslist[i].sz_operatorname + " " + sysm.news.newslist[i].newstext.sz_news + " " + Helper.FormatDate(sysm.news.newslist[i].l_incident_start) + (sysm.news.newslist[i].l_incident_end == 0 ? "" : "-" + Helper.FormatDate(sysm.news.newslist[i].l_incident_end)), sysm.news.newslist[i].l_newspk.ToString()));
                lst_messages.Items.Add(new ListItem(Util.padForListBox(sysm.news.newslist[i]), sysm.news.newslist[i].l_newspk.ToString()));
            }*/

            UBBNEWS news = (UBBNEWS)Session["edit"];
            if (news != null)
            {
                lst_messages.SelectedValue = news.l_newspk.ToString();
                lst_messages_selectedindex(this, new EventArgs());
            }

            PasAdmin padmin = new PasAdmin();
            padmin.Url = ConfigurationSettings.AppSettings["PasAdmin"];
            GetOperatorsResponse res = padmin.doGetOperators(logon);
            if (res.successful)
            {
                foreach (LBAOPERATOR op in res.oplist)
                    ddl_operator.Items.Add(new ListItem(op.sz_operatorname, op.l_operator.ToString()));
            }

            Session.Remove("edit");
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
        com.ums.ws.pas.admin.ULOGONINFO logon;

        if (lst_messages.SelectedIndex != -1)
            news = getMessage(long.Parse(lst_messages.SelectedValue));

        if (news != null)
        {
            logon = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
            news.l_deptpk = 0;
            news.l_userpk = logon.l_userpk;
            news.l_type = long.Parse(ddl_type.SelectedValue);
            news.l_operator = int.Parse(ddl_operator.SelectedValue);
            news.l_incident_start = long.Parse(txt_activate.Text.Substring(6, 4) + txt_activate.Text.Substring(3, 2) + txt_activate.Text.Substring(0, 2) + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
            //
            if (txt_deactivate.Text.Length == 0 || txt_deactivate.Text.Length == 1)
                news.l_incident_end = 0;
            else
                news.l_incident_end = long.Parse(txt_deactivate.Text.Substring(6, 4) + txt_deactivate.Text.Substring(3, 2) + txt_deactivate.Text.Substring(0, 2) + ddl_deactivate_h.SelectedValue + ddl_deactivate_m.SelectedValue + "00");
            news.newstext.sz_news = txt_message.Text;

            pasws pws = new pasws();
            pws.Url = ConfigurationSettings.AppSettings["Pas"];

            news = pws.UpdateSystemMessage(Util.convertLogonInfoPas(logon), news);
            ddl_operator.SelectedValue = news.l_operator.ToString();
            news.sz_operatorname = ddl_operator.Items[ddl_operator.SelectedIndex].Text;

            Session["edit"] = news;
            int selectedindex = lst_messages.SelectedIndex;
            lst_messages.Items.RemoveAt(selectedindex);
            lst_messages.Items.Insert(selectedindex, new ListItem(Util.padForListBox(news), news.l_newspk.ToString()));
            lst_messages.SelectedIndex = selectedindex;
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
               
                // Activate minutes
                int minutes = 0;
                int hour = 0;
                if (messages.news.newslist[i].l_incident_start.ToString().Length >= 12)
                {
                    minutes = int.Parse(messages.news.newslist[i].l_incident_start.ToString().Substring(10, 2));
                    minutes = (int)Math.Round((double)minutes / 5.0) * 5;
                    if (minutes == 60)
                        ddl_activate_m.SelectedIndex = 0;
                    else
                        ddl_activate_m.SelectedValue = minutes.ToString();
                }
                else
                    ddl_activate_m.SelectedIndex = 0;
                
                // Activate hours
                if (messages.news.newslist[i].l_incident_start.ToString().Length >= 10)
                {
                    hour = int.Parse(messages.news.newslist[i].l_incident_start.ToString().Substring(8, 2));
                    if (minutes == 60)
                        hour++;
                    if (hour == 24)
                        ddl_activate_h.SelectedIndex = 0;
                    else
                        ddl_activate_h.SelectedValue = hour.ToString();
                }
                else
                    ddl_activate_h.SelectedIndex = 0;

                // Activate date
                if (messages.news.newslist[i].l_incident_start.ToString().Length >= 8)
                {
                    DateTime date = new DateTime(int.Parse(messages.news.newslist[i].l_incident_start.ToString().Substring(0, 4)), int.Parse(messages.news.newslist[i].l_incident_start.ToString().Substring(4, 2)), int.Parse(messages.news.newslist[i].l_incident_start.ToString().Substring(6, 2)));

                    if (hour == 24)
                    {
                        date.AddDays(1);
                        txt_activate.Text = date.ToString("dd-MM-yyyy");
                    }
                    else
                        txt_activate.Text = date.ToString("dd-MM-yyyy");
                }
                else
                    txt_activate.Text = "";

                // Deactivate minutes
                minutes = 0;
                hour = 0;
                if (messages.news.newslist[i].l_incident_end.ToString().Length >= 12)
                {
                    minutes = int.Parse(messages.news.newslist[i].l_incident_end.ToString().Substring(10, 2));
                    minutes = (int)Math.Round((double)minutes / 5.0) * 5;
                    if (minutes == 60)
                        ddl_deactivate_m.SelectedIndex = 0;
                    else
                        ddl_deactivate_m.SelectedValue = minutes.ToString();
                }
                else
                    ddl_deactivate_m.SelectedIndex = 0;
                
                // Deactivate hours
                if (messages.news.newslist[i].l_incident_end.ToString().Length >= 10)
                {
                    hour = int.Parse(messages.news.newslist[i].l_incident_end.ToString().Substring(8, 2));
                    if (minutes == 60)
                        hour++;
                    if (hour == 24)
                        ddl_deactivate_h.SelectedIndex = 0;
                    else
                        ddl_deactivate_h.SelectedValue = hour.ToString();
                }
                else
                    ddl_deactivate_h.SelectedIndex = 0;

                // Deactivate date
                if (messages.news.newslist[i].l_incident_end.ToString().Length >= 8)
                {
                    DateTime date = new DateTime(int.Parse(messages.news.newslist[i].l_incident_end.ToString().Substring(0, 4)), int.Parse(messages.news.newslist[i].l_incident_end.ToString().Substring(4, 2)), int.Parse(messages.news.newslist[i].l_incident_end.ToString().Substring(6, 2)));

                    if (hour == 24)
                    {
                        date = date.AddDays(1);
                        txt_deactivate.Text = date.ToString("dd-MM-yyyy");
                    }
                    else
                        txt_deactivate.Text = date.ToString("dd-MM-yyyy");
                }
                else
                    txt_deactivate.Text = "";

                if (messages.news.newslist[i].f_active == 1) //active egentlig f_active
                {
                    txt_message.Attributes.Remove("onFocus");
                    activate_validate.Enabled = true;
                    ddl_operator.Enabled = true;
                    ddl_type.Enabled = true;
                    ddl_activate_h.Enabled = true;
                    ddl_activate_m.Enabled = true;
                    CalendarExtender1.Enabled = true;
                    //txt_activate.Enabled = true;
                    ddl_deactivate_h.Enabled = true;
                    ddl_deactivate_m.Enabled = true;
                    //txt_deactivate.Enabled = true;
                }
                else // Only allow changes to deactivate
                {
                    txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
                    activate_validate.Enabled = false;
                    ddl_operator.Enabled = false;
                    ddl_type.Enabled = false;
                    ddl_activate_h.Enabled = false;
                    ddl_activate_m.Enabled = false;
                    CalendarExtender1.Enabled = false;
                    //txt_activate.Enabled = false;
                    ddl_deactivate_h.Enabled = true;
                    ddl_deactivate_m.Enabled = true;
                    //txt_deactivate.Enabled = true;
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
