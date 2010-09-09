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

        public static void NewAlert() 
        {
            LBAParameter oParams = new LBAParameter();
            Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);
            cb alert = new cb();
            polypoint pt;

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
            msg.l_channel = oParams.l_channelno;
            msg.sz_text = "Polygon alert text";

            alert.textmessages.Add(msg);

            currentTestRef = CBTest.NewAlert(alert);
            Log.WriteLog(String.Format("Self test message sent with refno: {0}.", currentTestRef), 0);
        }
        public static void NewAlertPLMN() 
        {
            LBAParameter oParams = new LBAParameter();
            Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);
            cb alert = new cb();

            alert.operation = "NewAlertPLMN";
            alert.l_comppk = oUser.l_comppk;
            alert.l_deptpk = oUser.l_deptpk;

            alert.textmessages = new List<message>();

            message msg = new message();
            msg.l_channel = oParams.l_channelno;
            msg.sz_text = "Nationwide alert text";

            alert.textmessages.Add(msg);

            currentTestRef = CBTest.NewAlert(alert);
            Log.WriteLog(String.Format("Self test message (nationwide) sent with refno: {0}.", currentTestRef), 0);
        }
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
                //Log.WriteLog(String.Format("Initiating self test (Kill Alert). Filename='{0}'", filename), 0);

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
                //Log.WriteLog(String.Format("Initiating self test (Update Alert). Filename='{0}'", filename), 0);

                XmlSerializer s = new XmlSerializer(typeof(cb));
                StreamWriter w = new StreamWriter(filename);
                s.Serialize(w, alert);
                w.Close();
            }
        }
    }

    class CBTest
    {
        private static DateTime? ts_nexttest = null;

        public static void RandomTestThread()
        {
            // Get ts_nexttest from database when starting up
            ts_nexttest = Database.GetNextTest();

            while (CBServer.running)
            {
                if (ts_nexttest.HasValue)
                {
                    // next test has a time, check if it's time to send
                    if (ts_nexttest.Value.CompareTo(DateTime.Now) < 0)
                    {
                        LBAParameter oParams = new LBAParameter();
                        Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);

                        cb oAlert = new cb();
                        message msg = new message();

                        msg.l_channel = oParams.l_testchannelno;
                        msg.sz_text = Settings.GetValue("TestMessageText", "test");

                        oAlert.l_comppk = oUser.l_comppk;
                        oAlert.l_deptpk = oUser.l_deptpk;
                        oAlert.l_projectpk = 0;
                        oAlert.operation = "NewAlertTest";
                        oAlert.l_validity = 5;

                        oAlert.textmessages = new List<message>();
                        oAlert.textmessages.Add(msg);

                        // test time is now, or has passed, send test and generate new time
                        ts_nexttest = Database.GenerateNextTestTime();
                        Log.WriteLog(String.Format("Random test message sent with refno: {0}, next message scheduled for {1}.", NewAlert(oAlert, 70), ts_nexttest), 0);
                    }
                }
                else
                {
                    // next test is not set yet, check if it exists in the database
                    ts_nexttest = Database.GetNextTest();
                    if (!ts_nexttest.HasValue)
                    {
                        // no time set in database either, generate new
                        ts_nexttest = Database.GenerateNextTestTime();
                    }
                }
                Thread.Sleep(1000);
            }
            Log.WriteLog("Stopped randomtest thread", 9);
            Interlocked.Decrement(ref Settings.threads);
        }
        public static void HeartbeatThread()
        {
            while (CBServer.running)
            {
                if (CBServer.running) // check if server is still running
                {
                    // sleep for l_heartbeatinterval minutes
                    for (int i = 0; i < Settings.l_heartbeatinterval * 60; i++)
                    {
                        if (!CBServer.running) break; // exit sleep loop if server is stopped

                        Thread.Sleep(1000);
                    }

                    /* 
                     * send heartbeat message (run after sleep, so it doesn't fire 
                     * immediately when starting up, but waits for the interval.
                     * Should prevent duplicate messages when restarting server, or
                     * if server crashes and restarts
                     */

                    if (CBServer.running)
                    {
                        LBAParameter oParams = new LBAParameter();
                        Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);

                        cb oAlert = new cb();
                        message msg = new message();

                        msg.l_channel = oParams.l_heartbeat;
                        msg.sz_text = Settings.GetValue("HeartBeatMessageText", "heartbeat");

                        oAlert.l_comppk = oUser.l_comppk;
                        oAlert.l_deptpk = oUser.l_deptpk;
                        oAlert.l_projectpk = 0;
                        oAlert.operation = "NewAlertHeartbeat";
                        oAlert.l_validity = 5;

                        oAlert.textmessages = new List<message>();
                        oAlert.textmessages.Add(msg);

                        Log.WriteLog(String.Format("New heartbeat message sent with refno: {0}, next message in {1} minutes.", NewAlert(oAlert), Settings.l_heartbeatinterval), 0);
                    }
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
        public static int NewAlert(cb alert)
        {
            return NewAlert(alert, null);
        }
        public static int NewAlert(cb alert, int? l_sendinginfo_type)
        {
            LBAParameter oParams = new LBAParameter();
            Settings oUser = Settings.SystemUser(oParams.l_deptpk, oParams.l_comppk);

            int l_testref = 0;
            l_testref  = Database.GetRefno();

            alert.l_refno = l_testref;
            string filename = String.Format(@"{0}eat\CB_SEND_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());

            // insert to LBASEND
            if (InsertSending(oUser, alert, l_sendinginfo_type) != Constant.OK)
            {
                Log.WriteLog(String.Format("Test failed to insert into database, aborting"), 2);
                return -1;
            }

            XmlSerializer s = new XmlSerializer(typeof(cb));
            StreamWriter w = new StreamWriter(filename);
            s.Serialize(w, alert);
            w.Close();

            return l_testref;
        }

        private static int InsertSending(Settings oUser, cb oAlert, int? l_sendinginfo_type)
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
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.InsertSending (exception={0}) (sql={1})", e.Message, sz_query),
                    String.Format("Database.InsertSending (exception={0}) (sql={1})", e, sz_query),
                    2);
            }
            if (l_sendinginfo_type.HasValue)
            {
                return InsertSendingInfo(oAlert, l_sendinginfo_type.Value);
            }

            return Constant.OK;
        }
        private static int InsertSendingInfo(cb oAlert, int l_sendinginfo_type)
        {
            string sz_query = "";

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_query, conn);

                conn.Open();

                sz_query = String.Format("INSERT INTO MDVSENDINGINFO(sz_fields, sz_sepused, l_namepos, l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_removedup, l_group, sz_groups, l_type, f_dynacall, l_addresstypes, l_userpk, f_lowres, l_maxchannels) VALUES('','',NULL,0,0,{0},0,0,0,0,'Alert {0}',0,{1},{2},0,0,{3},NULL,{4},0,0,0,NULL,0)"
                    , oAlert.l_refno
                    , oAlert.l_comppk
                    , oAlert.l_deptpk
                    , 16 // group
                    , l_sendinginfo_type);

                cmd.CommandText = sz_query;
                cmd.ExecuteNonQuery();

                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.InsertSendinginfo (exception={0}) (sql={1})", e.Message, sz_query),
                    String.Format("Database.InsertSendinginfo (exception={0}) (sql={1})", e, sz_query),
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
