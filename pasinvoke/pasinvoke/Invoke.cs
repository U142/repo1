using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Web.Services.Protocols;
using System.Security.Cryptography;
using System.Reflection;
using System.ServiceModel;

using pasinvoke.pas;
using pasinvoke.no.ums2.secure;
using pasinvoke.src;

namespace pasinvoke
{
    public partial class Invoke : Form
    {
        private string sz_user;
        private string sz_company;
        private string sz_department;
        private string sz_pk;
        private string sz_eventname;
        private long l_userpk;       
        private int l_comppk;
        private int l_deptpk;
        private string sz_sendMode;

        public Invoke()
        {
            InitializeComponent();
            lbl_status.Size = new System.Drawing.Size(280, 80);
            lbl_status.AutoSize = false;

        }
        public Invoke(string user, string company, string department, string pk, long userpk, int comppk, int deptpk, string eventname, string sendmode = "live")
        {
            InitializeComponent();

            Assembly asm = Assembly.GetExecutingAssembly();
            lbl_version_number.Text = asm.GetName().Version.ToString();

            sz_user = user;
            sz_company = company;
            sz_department = department;
            sz_pk = pk;
            sz_eventname = eventname;
            l_userpk = userpk;
            l_comppk = comppk;
            l_deptpk = deptpk;
            sz_sendMode = sendmode;

            if (sz_sendMode.ToLower().Equals("simulate"))
            {
                lbl_simualte.Visible = true;
            }

            lbl_status.Text = "Current event is: " + eventname;

            if (user.Length < 1)
            {
                // Something is wrong
            }
            else
            {
                txt_user.Text = sz_user;
                txt_company.Text = sz_company;
                txt_department.Text = sz_department;
            }


        }

        private void InvokeDialog_Load(object sender, EventArgs e)
        {

        }

        private void textBox1_TextChanged(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                btn_send.PerformClick();
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            // Web service send!
            btn_send.Enabled = false;
            lbl_status.Text = "";


            pas.paswsSoapClient _pasws = new pas.paswsSoapClient();
            _pasws.Endpoint.Address = new System.ServiceModel.EndpointAddress("https://secure.ums.no/pas/ws_pasqa/ws/pas.asmx");
            //_pasws.Endpoint.Binding = new System.ServiceModel.BasicHttpBinding();

            pas.ULOGONINFO paslogin = new pas.ULOGONINFO();
            paslogin.sz_userid = sz_user.ToUpper();
            paslogin.sz_compid = sz_company.ToUpper();
            paslogin.sz_deptid = sz_department.ToUpper();
            paslogin.sz_password = Utils.CreateSHA512Hash(txt_password.Text);
            paslogin.onetimekey = _pasws.GetOneTimeKey();

            pas.UPASLOGON pl = _pasws.PasLogon(paslogin);

            pasinvoke.no.ums2.secure.ULOGONINFO logon = new pasinvoke.no.ums2.secure.ULOGONINFO();
            logon.sz_compid = sz_company.ToUpper();
            logon.l_comppk = l_comppk;
            logon.sz_deptid = sz_department.ToUpper();
            logon.l_deptpk = l_deptpk;
            logon.sz_userid = sz_user.ToUpper();
            logon.l_userpk = l_userpk;
            logon.sz_password = Utils.CreateSHA512Hash(txt_password.Text);
            logon.onetimekey = _pasws.GetOneTimeKey();
            logon.sessionid = pl.sessionid;

            try
            {
                Send send = new Send(logon);
                send.send(long.Parse(sz_pk), sz_sendMode);
                SendingStatusDialog ssd = new SendingStatusDialog(send, logon, sz_eventname);
                ssd.ShowDialog(this);
            }
            catch (EndpointNotFoundException)
            {
                MessageBox.Show("Could not connect with web service");
            }
            catch (SoapException)
            {
                MessageBox.Show("Problem with web service call");
            }
            catch (Exception)
            {
                MessageBox.Show("Could not send message, please check password");
            }
            btn_send.Enabled = true;
        }
    }
}
