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
using System.Text.RegularExpressions;

using System.Globalization;
using System.Collections.Generic;

using centric.com.ums.ws.pas;
using centric.com.ums.ws.pas.admin;
using System.ServiceModel;

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
            //txt_activate.Text = DateTime.Now.ToString("dd-MM-yyyy");
            paswsSoapClient pws = new paswsSoapClient();
            pws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);
            centric.com.ums.ws.pas.admin.ULOGONINFO logon = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
            if(logon == null)
                Server.Transfer("logon.aspx");
            
            USYSTEMMESSAGES sysm = pws.GetSystemMessages(Util.convertLogonInfoPas(logon), 1, UBBNEWSLIST_FILTER.IN_BETWEEN_START_END); // jukser til å hente ut alle meldinger

            IEnumerable<UBBNEWS> sorter = sysm.news.newslist.OrderBy(news => news.l_incident_start);

            foreach (UBBNEWS news in sorter)
            {
                if (news.f_active == 1 || (news.l_incident_end>sysm.news.l_timestamp_db || news.l_incident_end==0))
                    lst_messages.Items.Add(new ListItem(Util.padForListBox(news), news.l_newspk.ToString()));
            }

            /*
            for (int i = 0; i < sysm.news.newslist.Length; ++i)
            {
                if (sysm.news.newslist[i].f_active==1)
                    lst_messages.Items.Add(new ListItem(Util.padForListBox(sysm.news.newslist[i]), sysm.news.newslist[i].l_newspk.ToString()));
                    //lst_messages.Items.Add(new ListItem(sysm.news.newslist[i].sz_operatorname + " " + sysm.news.newslist[i].newstext.sz_news + " " + Helper.FormatDate(sysm.news.newslist[i].l_incident_start) + (sysm.news.newslist[i].l_incident_end == 0 ? "" : "-" + Helper.FormatDate(sysm.news.newslist[i].l_incident_end)), sysm.news.newslist[i].l_newspk.ToString()));
            }*/

            PasAdminSoapClient padmin = new PasAdminSoapClient();
            padmin.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
            GetOperatorsResponse res = padmin.doGetOperators(logon);
            if (res.successful)
            {
                foreach (LBAOPERATOR op in res.oplist)
                    ddl_operator.Items.Add(new ListItem(op.sz_operatorname, op.l_operator.ToString()));
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
        sysm.news.newslist[sysm.news.newslist.Length - 1].f_active = 0;
        if(txt_activate.Text.Length>0)
        {
            //IFormatProvider format = new CultureInfo("nb-NO");
            IFormatProvider format = new CultureInfo("nl-NL");
            String ting = txt_activate.Text + " " + ddl_activate_h.SelectedValue + ":" + ddl_activate_m.SelectedValue;
            try
            {
                sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_start = long.Parse(txt_activate.Text.Substring(6, 4) + txt_activate.Text.Substring(3, 2) + txt_activate.Text.Substring(0, 2) + ddl_activate_h.SelectedValue + ddl_activate_m.SelectedValue + "00");
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
        paswsSoapClient ws = new paswsSoapClient();
        ws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);
        centric.com.ums.ws.pas.admin.ULOGONINFO logon = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];

        // Stores the new message and returns it with l_newspk
        sysm.news.newslist[sysm.news.newslist.Length - 1] = ws.UpdateSystemMessage(Util.convertLogonInfoPas(logon), sysm.news.newslist[sysm.news.newslist.Length - 1]);
        sysm.news.newslist[sysm.news.newslist.Length - 1].l_deptpk = 0;
        sysm.news.newslist[sysm.news.newslist.Length - 1].f_active = 1;

        UBBNEWS tsm = (UBBNEWS)Session["edit"];
        if (tsm != null)
            lst_messages.Items.Remove(lst_messages.SelectedItem);
        //lst_messages.Items.Add(new ListItem(sysm.news.newslist[sysm.news.newslist.Length - 1].sz_operatorname + " " + sysm.news.newslist[sysm.news.newslist.Length - 1].newstext.sz_news + " " + Helper.FormatDate(sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_start) + (sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end == 0 ? "" : "-" + Helper.FormatDate(sysm.news.newslist[sysm.news.newslist.Length - 1].l_incident_end)), sysm.news.newslist[sysm.news.newslist.Length - 1].l_newspk.ToString()));
        lst_messages.Items.Add(new ListItem(Util.padForListBox(sysm.news.newslist[sysm.news.newslist.Length - 1]), sysm.news.newslist[sysm.news.newslist.Length - 1].l_newspk.ToString()));
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
        if (lst_messages.SelectedIndex != -1)
        {
            PasAdminSoapClient pasa = new PasAdminSoapClient();
            pasa.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
            centric.com.ums.ws.pas.admin.ULOGONINFO logon = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
            
            DeactivateMessageResponse res = pasa.doDeactivateMessage(Util.convertLogonInfoPasAdmin(logon), long.Parse(lst_messages.SelectedValue));
            if (res.successful)
                lst_messages.Items.Remove(lst_messages.SelectedItem);
        }
                
    }
  
}
