using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using IWshRuntimeLibrary;
using System.Web.Services.Protocols;
using System.Threading;
using System.IO;
using System.Reflection;

using pasinvoke.no.ums2.secure;
using pasinvoke.pas;
using pasinvoke.src;

namespace pasinvoke
{
    public partial class StoreShortcut : Form
    {
        private PAEVENT eventVO;
        private ToolTip cbx_toolTip;
        private pasinvoke.no.ums2.secure.ULOGONINFO logon;
        private String sessionid;
        private bool workComplete = false;

        public StoreShortcut()
        {
            InitializeComponent();
            Assembly asm = Assembly.GetExecutingAssembly();
            lbl_version_number.Text = asm.GetName().Version.ToString();
        }

        private void btn_saveshortcut_Click(object sender, EventArgs e)
        {
            bool ready = true;
            if (ready && txt_user.Text.Length < 1)
            {
                ready = false;
                lbl_message.Text = "Cannot create shortcut without username";
                txt_user.Focus();
            }
            if (ready && txt_company.Text.Length < 1)
            {
                ready = false;
                lbl_message.Text = "Cannot create shortcut without company";
                txt_company.Focus();
            }
            if (ready)
            {
                WshShellClass shell = new WshShellClass();
                try
                {
                    //IWshShortcut shortcut = (IWshShortcut)shell.CreateShortcut("C:\\Documents and Settings\\" + Environment.UserName + "\\Desktop\\invoke " + eventVO.sz_name + ".lnk");
                    IWshShortcut shortcut = (IWshShortcut)shell.CreateShortcut(Environment.GetFolderPath(Environment.SpecialFolder.Desktop) + "\\invoke " + eventVO.sz_name + ".lnk");
                    shortcut.TargetPath = Environment.CurrentDirectory + "\\pasinvoke.exe";//"C:\\Program Files\\UMS Population Alert System\\PAS Invoke\\pasinvoke.exe";
                    shortcut.Arguments = txt_user.Text + " " + txt_company.Text + " " + txt_department.Text + " " + eventVO.l_eventpk + " " + logon.l_userpk + " " + logon.l_comppk + " " + logon.l_deptpk + " \"" + eventVO.sz_name + "\"" + " \"live\"";
                    shortcut.Description = "Invoking PAS event";
                    shortcut.IconLocation = Environment.CurrentDirectory + "\\pas.ico";
                    shortcut.Save();
                    lbl_message.Text = eventVO.sz_name + " shortcut created";
                }
                catch (Exception ex)
                {
                    lbl_message.Text = cbx_events.SelectedItem.ToString() + " shortcut creationg failed: " + ex.Message;
                }
            }
        }

        private void cbx_events_SelectedIndexChanged(object sender, EventArgs e)
        {
            eventVO = (PAEVENT)cbx_events.SelectedItem;
            if(cbx_toolTip != null)
                cbx_toolTip.RemoveAll();
            cbx_toolTip = new ToolTip();
            PAALERT[] alerts = eventVO.alerts;
            string tooltip = "";
            for (int i = 0; i < alerts.Length; ++i)
                if (i + 1 < alerts.Length)
                    tooltip += alerts[i].l_alertpk + ": " + alerts[i].sz_name + "\n";
                else
                    tooltip += alerts[i].l_alertpk + ": " + alerts[i].sz_name;

            cbx_toolTip.SetToolTip(cbx_events, tooltip);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            bool ready = true;
            if (ready && txt_user.Text.Length < 1)
            {
                ready = false;
                MessageBox.Show("Cannot retrieve events without username");
                txt_user.Focus();
            }
            else if (ready && txt_company.Text.Length < 1)
            {
                ready = false;
                 MessageBox.Show("Cannot retrieve events without company");
                txt_company.Focus();
            }
            else if (ready && txt_department.Text.Length < 1)
            {
                ready = false;
                MessageBox.Show("Cannot retrieve events without department id");
                txt_department.Focus();
            }
            else if (ready && txt_password.Text.Length < 1)
            {
                ready = false;
                MessageBox.Show("Cannot retrieve events without password");
                txt_password.Focus();
            }
            if (ready)
            {
                progressBar1.Style = ProgressBarStyle.Marquee;
                progressBar1.MarqueeAnimationSpeed = 50;
                workComplete = false;          
                
                if(!backgroundWorker1.IsBusy)
                    backgroundWorker1.RunWorkerAsync();
                
                //workComplete = true;
                //progressBar1.MarqueeAnimationSpeed = 0;
                //backgroundWorker1.RunWorkerCompleted;                
            }
            else
                lbl_message.ForeColor = Color.Red;
        }

        public Button get_btn_saveshortcut()
        {
            return btn_saveshortcut;
        }
        public Button get_btn_send()
        {
            return btn_send;
        }
        public Label get_lbl_message(){
            return lbl_message;
        }

        private void btn_send_Click(object sender, EventArgs e)
        {
            btn_send.Enabled = false;
            // Web service send!
            lbl_message.Text = "";
            logon.sessionid = sessionid;
            Send send = new Send(logon);
            try
            {
                send.send(((PAEVENT)cbx_events.SelectedItem).l_eventpk);
                SendingStatusDialog ssd = new SendingStatusDialog(send, logon, ((PAEVENT)cbx_events.SelectedItem).sz_name);
                ssd.ShowDialog(this);
            }
            catch(Exception ex)
            {
                progressBar1.MarqueeAnimationSpeed = 0;
                progressBar1.Style = ProgressBarStyle.Blocks;
                MessageBox.Show(ex.Message);
            }

            btn_send.Enabled = true;
            //_parmws.ExecEventV3(long.Parse(vo.pk.Substring(1)),
            //_pasws.PasLogon(
        }

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            backgroundWorker1.ReportProgress(0);
            PAEVENT[] paevent = new PAEVENT[0];
            try
            {
                parmws _parmws = new parmws();
                _parmws.Url = "https://secure.ums.no/pas/ws_pasqa/ws/ExternalExec.asmx";
                //_parmws.Endpoint.Address = new System.ServiceModel.EndpointAddress("http://localhost:8080/WS/ExternalExec.asmx");
                //_parmws.Endpoint.Binding = new System.ServiceModel.BasicHttpBinding();
                paswsSoapClient _pasws = new paswsSoapClient();
                _pasws.Endpoint.Address = new System.ServiceModel.EndpointAddress("https://secure.ums.no/pas/ws_pasqa/ws/pas.asmx");
                //_pasws.Endpoint.Address = new System.ServiceModel.EndpointAddress("http://localhost:8080/WS/pas.asmx");
                //_pasws.Endpoint.Binding = new System.ServiceModel.BasicHttpBinding();
                pas.ULOGONINFO paslogin = new pas.ULOGONINFO();
                paslogin.sz_userid = txt_user.Text.ToUpper();
                //login.l_userpk = 1;
                paslogin.sz_compid = txt_company.Text.ToUpper();
                //login.l_comppk = 2;
                //login.sz_deptid = "TEST";
                paslogin.sz_deptid = txt_department.Text.ToUpper();
                paslogin.sz_password = Utils.CreateSHA512Hash(txt_password.Text);
                paslogin.onetimekey = _pasws.GetOneTimeKey();

                /*progressBar1.Style = ProgressBarStyle.Marquee;
                progressBar1.Invoke((MethodInvoker)delegate
                {
                    progressBar1.MarqueeAnimationSpeed = 50;
                });
                */


                UPASLOGON pl = _pasws.PasLogon(paslogin);

                UDEPARTMENT department = new UDEPARTMENT();
                foreach (UDEPARTMENT dept in pl.departments)
                {
                    if(txt_department.Text.ToUpper().Equals(dept.sz_deptid))
                        department = dept;
                }

                pasinvoke.no.ums2.secure.ULOGONINFO logoninfo = new pasinvoke.no.ums2.secure.ULOGONINFO();
                sessionid = pl.sessionid;
                logoninfo.sessionid = sessionid;
                logoninfo.sz_userid = pl.sz_userid;
                logoninfo.sz_compid = pl.sz_compid;
                logoninfo.sz_deptid = paslogin.sz_deptid;
                logoninfo.sz_password = paslogin.sz_password;
                logoninfo.jobid = paslogin.jobid;

                logoninfo.l_comppk = pl.l_comppk;
                logoninfo.l_userpk = pl.l_userpk;
                logoninfo.l_deptpk = department.l_deptpk;
                logoninfo.onetimekey = paslogin.onetimekey;
                logoninfo.sz_stdcc = paslogin.sz_stdcc;

                logon = logoninfo;

                paevent = _parmws.GetEventList(logon);
                e.Result = paevent;
            }
            catch (SoapException sexlol)
            {
                e.Result = sexlol;
            }
            catch (Exception ex)
            {
                e.Result = ex;
            }
            
            return;
        }



        void ExecEventV3Completed(object sender, ExecEventV3CompletedEventArgs args)
        {
            SendingStatusDialog.setExecResponse(args.Result);
        }

        private void backgroundWorker1_ProgressChanged(object sender, ProgressChangedEventArgs e) 
        {
            
        }
        private void backgroundWorker1_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            try
            {

                object o = e.Result;

                if (o.GetType() == typeof(PAEVENT[]))
                {

                    PAEVENT[] paevent = (PAEVENT[])e.Result;
                    cbx_events.DataSource = paevent;
                    cbx_events.DisplayMember = "sz_name";

                    if (cbx_events.Items.Count < 1)
                        throw new Exception("No events returned");

                    btn_saveshortcut.Enabled = true;
                    btn_send.Enabled = true;
                    cbx_events.Enabled = true;
                    lbl_message.ForeColor = Color.Green;
                    lbl_message.Text = "Events retrieved";
                }
                else
                {
                    throw (Exception)o;
                }
                /*
                Progress p = new Progress(ref _parmws, ref login, ref cbx_events, ref btn_saveshortcut, ref lbl_message);
                Thread th = new Thread(new ThreadStart(p.progressStart));*/
                /*for (int i = 0; i < paevent.Length; ++i)
                {
                    cbx_events.DataBindings.Add(paevent[i].sz_name, paevent[i], paevent[i].ToString()); 
                }*/
                //

            }
            catch (SoapException sexlol)
            {
                MessageBox.Show(sexlol.Message);
                //lbl_message.ForeColor = Color.Red;
                //lbl_message.Text = sexlol.Message;
            }
            catch (Exception ex)
            {
                if(ex.Message.Contains("ULogonFailedException"))
                    MessageBox.Show("Invalid user information");
                else
                    MessageBox.Show(ex.Message);
                //lbl_message.ForeColor = Color.Red;
                //lbl_message.Text = ex.Message;
            }
            progressBar1.MarqueeAnimationSpeed = 0;
            progressBar1.Style = ProgressBarStyle.Blocks;
            Console.WriteLine("Ferdig");
        }

        private void txt_password_TextChanged(object sender, EventArgs e)
        {
            txt_password.KeyDown += (txt_password_KeyDown, args) => {
                if (args.KeyCode == Keys.Enter)
                {
                    btn_getevents.PerformClick();
                }
            };
        }
    }
    public class Progress
    {
        parmws _parmws;
        pasinvoke.no.ums2.secure.ULOGONINFO login;
        ComboBox cbx_events;
        StoreShortcut parent;
        Button btn_saveshortcut;
        Label lbl_message;

        public Progress(ref parmws _parmws, ref pasinvoke.no.ums2.secure.ULOGONINFO login, ref ComboBox cbx_events, ref Button btn_saveshortcut, ref Label lbl_message)
        {
            this._parmws = _parmws;
            this.login = login;
            this.cbx_events = cbx_events;
            this.btn_saveshortcut = btn_saveshortcut;
            this.lbl_message = lbl_message;
        }
        public void progressStart()
        {
            cbx_events.DataSource = _parmws.GetEventList(login);
            cbx_events.DisplayMember = "sz_name";
            if (cbx_events.Items.Count < 1)
                throw new Exception("No events returned");
            
            btn_saveshortcut.Enabled = true;
            parent.get_btn_send().Enabled = true;
            cbx_events.Enabled = true;
            lbl_message.ForeColor = Color.Green;
            lbl_message.Text = "Events retrieved";

        }
    }
}
