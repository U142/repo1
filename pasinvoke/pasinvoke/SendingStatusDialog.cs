using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using IWshRuntimeLibrary;
using System.Threading;
using pasinvoke.pas.status;
using System.IO;
using System.IO.Compression;

using System.Xml.Serialization;
using System.Xml;
using pasinvoke.src;
using pasinvoke.no.ums2.secure;

namespace pasinvoke
{
    public partial class SendingStatusDialog : Form
    {
        private static ExecResponse execResponse;
        private Send send;
        private pasinvoke.no.ums2.secure.ULOGONINFO logon;
        public int sleep = 10000;
        private System.Threading.Timer statusrefresh;
        private UStatusItemSearchParams search;
        private pas.status.ULOGONINFO logonstatus;
        private int counter = 0;
        private int refreshrate = 10000;
        private Dictionary<long, String> sendingInfo = new Dictionary<long, String>();
        private Dictionary<long, String> sendingType = new Dictionary<long, String>();

        public String getType(long type) {
            try
            {
                return sendingType[type];
            }
            catch (Exception)
            {
                return "Sending type " + type;
            }
        }
        public String getSendingStatus(long status)
        {
            try
            {
                return sendingInfo[status];
            }
            catch (Exception)
            {
                return "Status code " + status;
            }
        }
        
        private void initializeSendingInfoAndType()
        {
            sendingInfo.Add(-2, "General LBA Error");
            sendingInfo.Add(1, "Parsing Queue");
            sendingInfo.Add(2, "Parsing");
            sendingInfo.Add(3, "Writing");
            sendingInfo.Add(4, "Parse queue");
            sendingInfo.Add(5, "Parsing");
            sendingInfo.Add(6, "Send queue");
            sendingInfo.Add(7, "Finished");
            sendingInfo.Add(100, "LBA initialised");
            sendingInfo.Add(41100, "WS-error: Could not publish LBA file");
            sendingInfo.Add(199, "Sent to LBA Server");
            sendingInfo.Add(200, "Parsing LBA");
            sendingInfo.Add(290, "Service not available, retrying");
            sendingInfo.Add(300, "Preparing");
            sendingInfo.Add(305, "Processing");
            sendingInfo.Add(310, "Prepared");
            sendingInfo.Add(311, "Count complete");
            sendingInfo.Add(320, "Confirmed by user");
            sendingInfo.Add(330, "Cancelled by user");
            sendingInfo.Add(340, "Sending");

            sendingInfo.Add(400, "Preparing");
            sendingInfo.Add(405, "Processing");
            sendingInfo.Add(410, "Prepared");
            sendingInfo.Add(411, "Count complete");
            sendingInfo.Add(420, "Confirmed by user");
            sendingInfo.Add(430, "Cancelled by user");
            sendingInfo.Add(440, "Sending");

            sendingInfo.Add(500, "Preparing");
            sendingInfo.Add(505, "Processing");
            sendingInfo.Add(510, "Prepared");
            sendingInfo.Add(511, "Count complete");
            sendingInfo.Add(520, "Confirmed by user");
            sendingInfo.Add(530, "Cancelled by user");
            sendingInfo.Add(540, "Sending");
            sendingInfo.Add(590, "Paused");

            sendingInfo.Add(800, "Cancel in progress");
            sendingInfo.Add(1000, "Finished");
            sendingInfo.Add(2000, "Cancelled");
            sendingInfo.Add(2001, "Cancelled by user or system");
            sendingInfo.Add(2002, "Cancelled");

            sendingInfo.Add(42001, "Exception in AreaAlert (LBA Server)");
            sendingInfo.Add(42002, "Exception in Prepare AreaAlert(LBA Server)");
            sendingInfo.Add(42003, "Exception in Execute CustomAlert (LBA Server)");
            sendingInfo.Add(42004, "Exception in Prepare CustomAlert (LBA Server)");
            sendingInfo.Add(42005, "Exception in Execute PreparedAlert (LBA Server)");
            sendingInfo.Add(42006, "Exception in Cancel PreparedAlert (LBA Server)");

            sendingInfo.Add(42007, "Exception in IntAlert (LBA Server)");
            sendingInfo.Add(42008, "Exception in IntAlert (LBA Server)");
            
            sendingInfo.Add(42011, "Failed to Execute AreaAlert (LBA Server)");
            sendingInfo.Add(42012, "Failed to Prepare AreaAlert (LBA Server)");
            sendingInfo.Add(42013, "Failed to Execute CustomAlert (LBA Server)");
            sendingInfo.Add(42014, "Failed to Prepare CustomAlert (LBA Server)");
            sendingInfo.Add(42015, "Failed to Execute PreparedAlert (LBA Server)");
            sendingInfo.Add(42016, "Failed to Cancel PreparedAlert (LBA Server)");

            sendingInfo.Add(42017, "Failed to Execute IntAlert (Operator)");
            sendingInfo.Add(42018, "Failed to Prepare IntAlert (Operator)");
	
            sendingInfo.Add(42101, "Missing tag textmessages");
            sendingInfo.Add(42102, "Missing attribute areaname");
            sendingInfo.Add(42103, "Missing tag polygon/ellipse");
            sendingInfo.Add(42104, "Get Alert Message Exception");
            sendingInfo.Add(42105, "Get Alert Message Failed (missing ccode tag)");
            
            sendingInfo.Add(42201, "Job failed (Operator)");

            sendingType.Add(1, "Voice");
            sendingType.Add(2, "SMS");
            sendingType.Add(3, "Mail");
            sendingType.Add(4, "LBA");
            sendingType.Add(5, "TAS");
        }
        public SendingStatusDialog()
        {
            InitializeComponent();
            initializeSendingInfoAndType();
        }
        public SendingStatusDialog(Send send, no.ums2.secure.ULOGONINFO logon, String eventname)
        {
            InitializeComponent();
            initializeSendingInfoAndType();

            this.Text += " - " + eventname;

            execResponse = send.execResponse;
            this.logon = logon;
            this.send = send;

            dgv_status.ColumnCount = 7;
            dgv_status.Columns[0].Name = "Refno";
            dgv_status.Columns[1].Name = "Name";
            dgv_status.Columns[2].Name = "Channel";
            dgv_status.Columns[3].Name = "Sendingstatus";
            dgv_status.Columns[4].Name = "Total items";
            dgv_status.Columns[5].Name = "Processed";
            dgv_status.Columns[6].Name = "Percent";
            dgv_status.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;

            PasStatusSoapClient passtatus = new PasStatusSoapClient();
            System.Windows.Forms.Application.DoEvents();
            statusrefresh = new System.Threading.Timer(updateStatus, null, 0, refreshrate);

        }

        private void SendingStatusDialog_Load(object sender, EventArgs e)
        {

            UStatusItemSearchParams search;
        }

        private void SendingStatusDialog_Shown(object sender, EventArgs e)
        {

        }

        private void updateStatus(Object x)
        {
            counter++;
            logonstatus = new pas.status.ULOGONINFO();
            logonstatus.jobid = logon.jobid;
            logonstatus.l_altservers = logon.l_altservers;
            logonstatus.l_comppk = logon.l_comppk;
            logonstatus.l_deptpk = logon.l_deptpk;
            logonstatus.l_deptpri = logon.l_deptpri;
            logonstatus.l_priserver = logon.l_priserver;
            logonstatus.l_userpk = logon.l_userpk;
            logonstatus.onetimekey = logon.onetimekey;
            logonstatus.sessionid = logon.sessionid;
            logonstatus.sz_compid = logon.sz_compid;
            logonstatus.sz_deptid = logon.sz_deptid;
            logonstatus.sz_password = logon.sz_password;
            logonstatus.sz_stdcc = logon.sz_stdcc;
            logonstatus.sz_userid = logon.sz_userid;

            if (!this.IsHandleCreated && !this.IsDisposed) return;

            if (!backgroundWorker1.IsBusy)
            {
                progressBar1.Invoke(new activateProgressbarCallback(this.activateProgressbar), true);
                backgroundWorker1.RunWorkerAsync();
            }
        }

        private void activateProgressbar(Boolean activate)
        {
            if (activate)
            {
                progressBar1.MarqueeAnimationSpeed = 10;
                progressBar1.Style = ProgressBarStyle.Marquee;
            }
            else
            {
                progressBar1.MarqueeAnimationSpeed = 0;
                progressBar1.Style = ProgressBarStyle.Blocks;
            }
        }

        public delegate void activateProgressbarCallback(Boolean activate);

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            List<MDVSENDINGINFO> mdvlist;

            if (send.execResponse == null)
            {
                e.Result = new List<MDVSENDINGINFO>();
                return;
            }

            execResponse = send.execResponse;

            long[] refno = new long[execResponse.arr_alertresults.Length];
            for (int i = 0; i < execResponse.arr_alertresults.Length; ++i)
            {
                refno[i] = execResponse.arr_alertresults[i].l_refno;
            }
            search = new UStatusItemSearchParams();
            long ppk;

            if (long.TryParse(execResponse.l_projectpk, out ppk))
            {
                search._l_projectpk = long.Parse(execResponse.l_projectpk);
                search._l_refno_filter = refno;
            }
            else
            {
                e.Result = null;
                return;
            }

            PasStatusSoapClient ps = new PasStatusSoapClient();
            ps.Endpoint.Address = new System.ServiceModel.EndpointAddress("https://secure.ums.no/pas/ws_pasqa/ws/PasStatus.asmx");
            //ps.Endpoint.Binding = new System.ServiceModel.BasicHttpBinding();

            try
            {
                byte[] fjols = ps.GetStatusItems(logonstatus, search);
                string filename = System.IO.Path.GetRandomFileName();

                MemoryStream ms = new MemoryStream(fjols);

                byte[] buffer = new byte[fjols.Length];

                GZipStream ds = new GZipStream(ms, CompressionMode.Decompress);
                ReadAllBytesFromStream(ds, ref buffer);
                XmlDataDocument xmldoc = new XmlDataDocument();
                ms = new MemoryStream(buffer);
                xmldoc.Load(ms);
                ms.Flush();
                ms.Close();

                mdvlist = Utils.Deserialize(xmldoc);
            }
            catch (Exception ex)
            {
                mdvlist = new List<MDVSENDINGINFO>();
            }
            e.Result = mdvlist;
        }

        private void backgroundWorker1_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Result != null)
            {
                dgv_status.Invoke(new UpdateStatusCallback(this.updateStatus), (List<MDVSENDINGINFO>)e.Result);
            }
            if (send.execResponse != null)
            {
                dgv_status.Invoke(new UpdateEventAndProjectPK(this.updateEventAndProjectPk), send);
            }
            progressBar1.Invoke(new activateProgressbarCallback(this.activateProgressbar), false);
            backgroundWorker1.Dispose();
        }

        private void backgroundWorker1_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {

        }

        private void updateStatus(List<MDVSENDINGINFO> mdvlist)
        {
            dgv_status.Rows.Clear();
            foreach (MDVSENDINGINFO mdvsi in mdvlist)
            {               
                dgv_status.Rows.Add(new object[] { mdvsi.l_refno, 
                    mdvsi.sz_sendingname,
                    getType(mdvsi.l_type),
                    getSendingStatus(mdvsi.l_sendingstatus),
                    mdvsi.l_totitem,
                    mdvsi.l_processed,
                    ((mdvsi.l_processed > 0 && mdvsi.l_totitem > 0) ? (int)(((double)mdvsi.l_processed / (double)mdvsi.l_totitem) * 100) : 0) });
            }
            if (mdvlist.Count < 1 && counter > 1)
            {
                lbl_info.Text = String.Format("No status items found, retrying in {0} seconds", refreshrate / 1000);
            }
            else if (counter < 2)
            {
                lbl_info.Text = "Status being prepared, please wait";
            }
            else
            {
                lbl_info.Text = "";
            }

        }

        public delegate void UpdateStatusCallback(List<MDVSENDINGINFO> mdvlist);

        public delegate void UpdateEventAndProjectPK(Send send);

        private void updateEventAndProjectPk(Send send)
        {
            lbl_projectpk.Text = send.execResponse.l_projectpk;
        }

        public static int ReadAllBytesFromStream(Stream stream, ref byte[] buffer)
        {
            // Use this method is used to read all bytes from a stream.
            int offset = 0;
            int totalCount = 0;
            while (true)
            {
                if (totalCount + 100 > buffer.Length)
                    Array.Resize<byte>(ref buffer, buffer.Length * 2);
                int bytesRead = stream.Read(buffer, offset, 100);
                if (bytesRead == 0)
                {
                    break;
                }
                offset += bytesRead;
                totalCount += bytesRead;
            }
            Array.Resize<byte>(ref buffer, totalCount);
            return totalCount;
        }

        public static void setExecResponse(ExecResponse er)
        {
            execResponse = er;
        }


    }
   

   
}
