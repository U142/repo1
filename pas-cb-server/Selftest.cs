using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;
using System.Data.Odbc;

namespace pas_cb_server.test
{
    class Selftest
    {
        private static int currentTestRef = 0;
        public static int TestReference
        {
            get
            {
                return currentTestRef;
            }
        }
        public static void TestEnded(int status)
        {
            Log.WriteLog(String.Format("Test alert {0} ended width status {1}", currentTestRef, status), 0);
            currentTestRef = 0;
        }

        public static void NewAlert()
        {
            Settings oUser = Settings.SystemUser();
            cb alert = new cb();
            polypoint pt;

            currentTestRef = Database.GetRefno();

            alert.l_refno = currentTestRef;
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
            msg.l_channel = 1;
            msg.sz_text = "-- self test message --";

            alert.textmessages.Add(msg);

            string filename = String.Format(@"{0}eat\CB_SEND_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());
            Log.WriteLog(String.Format("Initiating self test (New Alert). Filename='{0}'", filename), 0);

            // insert to LBASEND
            if (InsertSending(oUser, alert) != Constant.OK)
            {
                Log.WriteLog(String.Format("Self test failed to insert into database, aborting"), 2);
                return;
            }

            XmlSerializer s = new XmlSerializer(typeof(cb));
            StreamWriter w = new StreamWriter(filename);
            s.Serialize(w, alert);
            w.Close();
        }
        public static void NewAlertPLMN()
        {
            Settings oUser = Settings.SystemUser();
            cb alert = new cb();

            currentTestRef = Database.GetRefno();

            alert.l_refno = currentTestRef;
            alert.operation = "NewAlertPLMN";
            alert.l_comppk = oUser.l_comppk;
            alert.l_deptpk = oUser.l_deptpk;

            alert.textmessages = new List<message>();

            message msg = new message();
            msg.l_channel = 1;
            msg.sz_text = "-- self test message --";

            alert.textmessages.Add(msg);

            string filename = String.Format(@"{0}eat\CB_SEND_{1}.{2}.{3}.xml", Settings.sz_parsepath, alert.l_projectpk, alert.l_refno, Guid.NewGuid().ToString());
            Log.WriteLog(String.Format("Initiating self test (New Alert). Filename='{0}'", filename), 0);

            // insert to LBASEND
            if (InsertSending(oUser, alert) != Constant.OK)
            {
                Log.WriteLog(String.Format("Self test failed to insert into database, aborting"), 2);
                return;
            }

            XmlSerializer s = new XmlSerializer(typeof(cb));
            StreamWriter w = new StreamWriter(filename);
            s.Serialize(w, alert);
            w.Close();
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
                Settings oUser = Settings.SystemUser();
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
                Settings oUser = Settings.SystemUser();
                cb alert = new cb();

                alert.l_refno = currentTestRef;
                alert.operation = "UpdateAlert";
                alert.l_comppk = oUser.l_comppk;
                alert.l_deptpk = oUser.l_deptpk;

                alert.textmessages = new List<message>();
                message msg = new message();
                msg.l_channel = 1;
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

        private static int InsertSending(Settings oUser, cb oAlert)
        {
            string sz_query = "";

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_query, conn);

                conn.Open();
                /*
                                sql = String.Format("INSERT INTO MDVSENDINGINFO(l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, " +
                                                      "l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_group, " +
                                                      "l_removedup, l_type, f_dynacall, l_addresstypes, l_maxchannels) " +
                                                      "VALUES({0}, {1}, {2}, {3}, {4}, {5}, {6}, '{7}', {8}, {9}, {10}, {11}, " +
                                                      "{12}, {13}, {14}, {15}, {16}, {17})",
                                                0,
                                                0,
                                                oAlert.l_refno,
                                                DateTime.Now.ToString("yyyyMMdd"),
                                                DateTime.Now.ToString("HHmm"),
                                                0,
                                                0,
                                                "CB alert", // + land eller multiple
                                                1,
                                                oUser.l_comppk,
                                                oUser.l_deptpk,
                                                0,
                                                5,
                                                1,
                                                Database.MESSAGETYPE,
                                                2,
                                                1048576,
                                                0);
                                cmd.CommandText = sql;
                                cmd.ExecuteNonQuery();
                */
                foreach (message msg in oAlert.textmessages)
                {
                    sz_query = String.Format("sp_cb_ins_lbatext {0}, '{1}', {2}",
                                        oAlert.l_refno,
                                        msg.sz_text.Replace("'","''"),
                                        msg.l_channel);
                    cmd.CommandText = sz_query;
                    cmd.ExecuteNonQuery();
                }
                foreach (Operator op in oUser.operators)
                {
                    /*sz_query = String.Format("INSERT INTO LBASEND(l_refno, l_status, l_response, l_items, l_proc, l_retries, " +
                                         "l_requesttype, sz_jobid, sz_areaid, f_simulate, l_operator, l_type) VALUES({0}, {1}, {2}, {3}, {4}, {5}, " +
                                         "{6}, '{7}', '{8}', {9}, {10}, {11})",
                                         oAlert.l_refno,
                                         100,
                                         -1,
                                         -1,
                                         -1,
                                         0,
                                         1,
                                         "",
                                         "",
                                         0,
                                         op.l_operator,
                                         Database.MESSAGETYPE);*/
                    
                    sz_query = String.Format("sp_cb_ins_lbasend {0}, {1}, {2}",
                                        oAlert.l_refno,
                                        op.l_operator,
                                        Database.MESSAGETYPE);
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
        [XmlAttribute]public double xcord;
        [XmlAttribute]public double ycord;
    }
}
