using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Configuration;
using centric.com.ums.ws.pas;
using System.ServiceModel;

public partial class parameters : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        inclogons_range.MaximumValue = Int32.MaxValue.ToString();
        //autologoff_range.MaximumValue = Int32.MaxValue.ToString();
        rng_channel.MaximumValue = Int32.MaxValue.ToString();
        rng_second_channel.MaximumValue = Int32.MaxValue.ToString();
        rng_heartbeat.MaximumValue = Int32.MaxValue.ToString();
        rng_interval.MaximumValue = Int32.MaxValue.ToString();
        //rng_repetitions.MaximumValue = Int32.MaxValue.ToString();
        rng_test_channel.MaximumValue = Int32.MaxValue.ToString();

        if (!IsPostBack)
        {
            // web service load settings
            paswsSoapClient pws = new paswsSoapClient();
            pws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);

            centric.com.ums.ws.pas.admin.ULOGONINFO logon = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
            if (logon == null)
                Server.Transfer("logon.aspx");
            ULBAPARAMETER param = pws.GetCBParameters(Util.convertLogonInfoPas(logon));

            txt_inclogons.Text = param.l_incorrect.ToString();
            //txt_autologoff.Text = param.l_autologoff.ToString();
            txt_adminemail.Text = param.sz_adminemail;
            txt_channel.Text = param.l_channelno.ToString();
            txt_test_channel.Text = param.l_test_channelno.ToString();
            txt_heartbeat.Text = param.l_heartbeat.ToString();
            txt_interval.Text = param.l_interval.ToString();
            txt_duration.Text = param.l_duration.ToString();
            txt_pagesize.Text = param.l_pagesize.ToString();
            txt_maxpages.Text = param.l_maxpages.ToString();
            txt_second_channel.Text = param.l_second_channelno.ToString();
            chk_second_channel.Checked = param.b_second_channel_active;
        }
    }
    protected void btn_save_Click(object sender, EventArgs e)
    {
        // web service call
        ULBAPARAMETER param = new ULBAPARAMETER();

        param.l_incorrect = int.Parse(txt_inclogons.Text);
        //param.l_autologoff = int.Parse(txt_autologoff.Text);
        param.sz_adminemail = txt_adminemail.Text;
        param.l_channelno = int.Parse(txt_channel.Text);
        param.l_test_channelno = int.Parse(txt_test_channel.Text);
        param.l_heartbeat = int.Parse(txt_heartbeat.Text);
        param.l_interval = int.Parse(txt_interval.Text);
        //param.l_repetition = int.Parse(txt_repetitions.Text);
        param.l_duration = int.Parse(txt_duration.Text);
        param.l_pagesize = int.Parse(txt_pagesize.Text);
        param.l_maxpages = int.Parse(txt_maxpages.Text);

        paswsSoapClient pws = new paswsSoapClient();
        pws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);
        centric.com.ums.ws.pas.admin.ULOGONINFO logon = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        if (logon == null)
            Server.Transfer("logon.aspx");
        param.l_deptpk = logon.l_deptpk;
        param.l_comppk = logon.l_comppk;
        param.l_second_channelno = int.Parse(txt_second_channel.Text);
        param.b_second_channel_active = chk_second_channel.Checked;
        pws.updateCBParameters(Util.convertLogonInfoPas(logon), param);
    }
}
