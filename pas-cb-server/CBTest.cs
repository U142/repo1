using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;
using System.Data.Odbc;
using System.Threading;

namespace pas_cb_server.cb_test
{
    class CBSelftest
    {
        private static int currentTestRef = 0;
        private static int TestReference
        {
            get
            {
                return currentTestRef;
            }
        }
        private static void TestEnded(int status)
        {
            Log.WriteLog(String.Format("Test alert {0} ended width status {1}", currentTestRef, status), 0);
            currentTestRef = 0;
        }

        public static void NewAlert() { }
        public static void NewAlertPLMN() { }
        public static void KillAlert()
        {
            if (currentTestRef == 0)
            {
                Log.WriteLog("No test alerts to kill.", 9);
                Console.Write("Enter refno to kill: ");
                currentTestRef = int.Parse(Console.ReadLine());
            }
            else
            {
                LBAParameter oParams = new LBAParameter();
                Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);
                cb alert = new cb();

                alert.l_refno = currentTestRef;
                alert.operation = "KillAlert";
                alert.l_comppk = oUser.l_comppk;
                alert.l_deptpk = oUser.l_deptpk;

                string filename = String.Format(@"{0}eat\CB_KILL_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());
                Log.WriteLog(String.Format("Initiating self test (Kill Alert). Filename='{0}'", filename), 0);

                XmlSerializer s = new XmlSerializer(typeof(cb));
                StreamWriter w = new StreamWriter(filename);
                s.Serialize(w, alert);
                w.Close();

                currentTestRef = 0;
            }
        }
        public static void UpdateAlert()
        {
            if (currentTestRef == 0)
            {
                Log.WriteLog("No test alerts to kill.", 9);
            }
            else
            {
                LBAParameter oParams = new LBAParameter();
                Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);
                cb alert = new cb();

                alert.l_refno = currentTestRef;
                alert.operation = "UpdateAlert";
                alert.l_comppk = oUser.l_comppk;
                alert.l_deptpk = oUser.l_deptpk;

                alert.textmessages = new List<message>();
                message msg = new message();
                msg.l_channel = oParams.l_testchannelno;
                msg.sz_text = "-- self test message - UPDATED --";

                alert.textmessages.Add(msg);

                string filename = String.Format(@"{0}eat\CB_UPDATE_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());
                Log.WriteLog(String.Format("Initiating self test (Update Alert). Filename='{0}'", filename), 0);

                XmlSerializer s = new XmlSerializer(typeof(cb));
                StreamWriter w = new StreamWriter(filename);
                s.Serialize(w, alert);
                w.Close();
            }
        }
    }

    class CBTest
    {
        public static void RandomTestThread()
        {
            while (CBServer.running)
            {
                Thread.Sleep(1000);
            }
            Log.WriteLog("Stopped randomtest thread", 9);
            Interlocked.Decrement(ref Settings.threads);
        }

        public static void HeartbeatThread()
        {
            while (CBServer.running)
            {
                // sleep for l_heartbeatinterval minutes
                for (int i = 0; i < Settings.l_heartbeatinterval * 60; i++)
                {
                    if (!CBServer.running) break; // exit sleep loop if server is stopped

                    Thread.Sleep(1000);
                }

                if (CBServer.running) // check if server is still running
                {
                    /* 
                     * send heartbeat message (run after sleep, so it doesn't fire 
                     * immediately when starting up, but waits for the interval.
                     * Should prevent duplicate messages when restarting server, or
                     * if server crashes and restarts
                     */

                    LBAParameter oParams = new LBAParameter();
                    Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);

                    cb oAlert = new cb();
                    message msg = new message();

                    msg.l_channel = oParams.l_heartbeat;
                    msg.sz_text = "<heartbeat message>";

                    oAlert.l_comppk = oUser.l_comppk;
                    oAlert.l_deptpk = oUser.l_deptpk;
                    oAlert.l_projectpk = 0;
                    oAlert.l_refno = Database.GetRefno();
                    oAlert.operation = "NewAlertHeartbeat";
                    oAlert.l_validity = 5;

                    oAlert.textmessages = new List<message>();
                    oAlert.textmessages.Add(msg);

                }
            }
            Log.WriteLog("Stopped heartbeat thread", 9);
            Interlocked.Decrement(ref Settings.threads);
        }
        public static int CreateXML(Settings oUser, cb oAlert)
        {
            int ret = 0;

            return ret;
        }
        public static int NewAlert()
        {
            LBAParameter oParams = new LBAParameter();
            Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);
            cb alert = new cb();
            polypoint pt;

            int l_testref = 0;
            l_testref = Database.GetRefno();

            alert.l_refno = l_testref;
            alert.operation = "NewAlertPolygon";
            alert.l_comppk = oUser.l_comppk;
            alert.l_deptpk = oUser.l_deptpk;

            alert.alertpolygon = new List<polypoint>();
            alert.textmessages = new List<message>();

            // Downtown Amsterdam
            pt = new polypoint();
            pt.xcord = 4.79133;
            pt.ycord = 52.51956;
            alert.alertpolygon.Add(pt);

            pt = new polypoint();
            pt.xcord = 4.73395;
            pt.ycord = 52.29474;
            alert.alertpolygon.Add(pt);

            pt = new polypoint();
            pt.xcord = 5.05612;
            pt.ycord = 52.30121;
            alert.alertpolygon.Add(pt);

            pt = new polypoint();
            pt.xcord = 5.04533;
            pt.ycord = 52.51956;
            alert.alertpolygon.Add(pt);

            message msg = new message();
            msg.l_channel = oParams.l_testchannelno;
            msg.sz_text = "-- Self Test Message --\r\nThis message is longer than one page, should be automatically split into several pages and padded. Also the text is being split between words if possible.";

            alert.textmessages.Add(msg);

            string filename = String.Format(@"{0}eat\CB_SEND_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());
            Log.WriteLog(String.Format("Initiating self test (New Alert). Filename='{0}'", filename), 0);

            // insert to LBASEND
            if (InsertSending(oUser, alert) != Constant.OK)
            {
                Log.WriteLog(String.Format("Self test failed to insert into database, aborting"), 2);
                return -1;
            }

            XmlSerializer s = new XmlSerializer(typeof(cb));
            StreamWriter w = new StreamWriter(filename);
            s.Serialize(w, alert);
            w.Close();

            return l_testref;
        }
        public static int NewAlertPLMN()
        {
            LBAParameter oParams = new LBAParameter();
            Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);
            cb alert = new cb();

            int l_testref = 0;
            l_testref  = Database.GetRefno();

            alert.l_refno = l_testref;
            alert.operation = "NewAlertPLMN";
            alert.l_comppk = oUser.l_comppk;
            alert.l_deptpk = oUser.l_deptpk;

            alert.textmessages = new List<message>();

            message msg = new message();
            msg.l_channel = oParams.l_testchannelno;
            msg.sz_text = "-- self test message --";

            alert.textmessages.Add(msg);

            string filename = String.Format(@"{0}eat\CB_SEND_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());
            Log.WriteLog(String.Format("Initiating self test (New Alert). Filename='{0}'", filename), 0);

            // insert to LBASEND
            if (InsertSending(oUser, alert) != Constant.OK)
            {
                Log.WriteLog(String.Format("Self test failed to insert into database, aborting"), 2);
                return -1;
            }

            XmlSerializer s = new XmlSerializer(typeof(cb));
            StreamWriter w = new StreamWriter(filename);
            s.Serialize(w, alert);
            w.Close();

            return l_testref;
        }

        private static int InsertSending(Settings oUser, cb oAlert)
        {
            string sz_query = "";

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_query, conn);

                conn.Open();
                foreach (message msg in oAlert.textmessages)
                {
                    sz_query = String.Format("sp_cb_ins_lbatext {0}, '{1}', {2}",
                                        oAlert.l_refno,
                                        msg.sz_text.Replace("'", "''"),
                                        msg.l_channel);
                    cmd.CommandText = sz_query;
                    cmd.ExecuteNonQuery();
                }
                foreach (Operator op in oUser.operators)
                {
                    sz_query = String.Format("sp_cb_ins_lbasend {0}, {1}, {2}",
                                        oAlert.l_refno,
                                        op.l_operator,
                                        Settings.l_messagetype);
                    cmd.CommandText = sz_query;
                    cmd.ExecuteNonQuery();
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.InsertSending (exception={0}) (sql={1})", e.Message, sz_query),
                    String.Format("Database.InsertSending (exception={0}) (sql={1})", e, sz_query),
                    2);
            }

            return Constant.OK;
        }
    }

    [XmlRoot]
    public class cb
    {
        [XmlAttribute]public string operation;
        [XmlAttribute]public int l_refno;
        [XmlAttribute]public int l_projectpk = 0;

        [XmlAttribute]public int l_comppk;
        [XmlAttribute]public int l_deptpk;
        [XmlAttribute]public int l_userpk = 0;
        [XmlAttribute]public string sz_password = "";

        [XmlAttribute]public int l_sched_utc = 0;
        [XmlAttribute]public int l_validity = 10;

        public List<message> textmessages;// = new List<message>();
        public List<polypoint> alertpolygon;// = new List<polypoint>();
    }
    public class message
    {
        [XmlAttribute]public int l_channel;
        [XmlAttribute]public string sz_text;
    }
    public class polypoint
    {
        [XmlAttribute("lon")]public double xcord;
        [XmlAttribute("lat")]public double ycord;
    }
}
