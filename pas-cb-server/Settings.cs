using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using umssettings;
using System.Data.Odbc;
using System.Xml;
using System.Xml.Serialization;
using System.IO;
using System.Threading;

namespace pas_cb_server
{
    public class Settings : _settings
    {
        // General (global settings used in the app)
        public static bool debug;
        public static bool running;

        public static string sz_parsepath;
        public static string sz_dumppath;
        public static string sz_dbconn;
        public static int l_cpuaffinity;
        public static int l_statuspollinterval;
        public static int l_retryinterval;
        public static int l_retries;
        public static int l_messagetype;

        // Instanced settings
        public int l_comppk = 0;
        public int l_deptpk = 0;
        public long l_userpk = 0;

        public string sz_compid = "";
        public string sz_deptid = "";
        public string sz_userid = "";
        public string sz_password = "";

        public Operator[] operators;

        // methods
        public static Settings SystemUser()
        {
            Settings ret = new Settings();
            
            try
            {
                ret.l_deptpk = Settings.GetInt("DeptPK");
                ret.l_comppk = Settings.GetInt("CompPK");
                ret.sz_compid = Database.GetCompID(ret.l_comppk);
                ret.sz_deptid = Database.GetDeptID(ret.l_deptpk);
                ret.sz_password = "";
                ret.sz_userid = "system";

                ret.operators = Operator.GetOperators(ret);
            }
            catch (Exception e)
            {
                Log.WriteLog("Error getting systemuser values. " + e.ToString(), "Error getting systemuser values. " + e.Message, 2);
            }

            return ret;
        }
        public static bool SetUserValues(XmlAttributeCollection attr, Settings uv)
        {
            bool bRetval = true;
            try
            {
                // required fields
                if (attr.GetNamedItem("l_deptpk") != null)
                {
                    if (attr.GetNamedItem("l_deptpk").Value != "")
                    {
                        uv.l_deptpk = int.Parse(attr.GetNamedItem("l_deptpk").Value);
                    }
                    else
                    {
                        bRetval = false;
                    }
                }
                else
                {
                    bRetval = false;
                }

                // optional fields
                if (attr.GetNamedItem("l_comppk") != null)
                    if (attr.GetNamedItem("l_comppk").Value != "")
                        uv.l_comppk = int.Parse(attr.GetNamedItem("l_comppk").Value);

                if (attr.GetNamedItem("l_userpk") != null)
                    if (attr.GetNamedItem("l_userpk").Value != "")
                        uv.l_userpk = long.Parse(attr.GetNamedItem("l_userpk").Value);

                if (attr.GetNamedItem("sz_compid") != null)
                    uv.sz_compid = attr.GetNamedItem("sz_compid").Value;
                else if (uv.l_comppk != 0)
                    uv.sz_compid = Database.GetCompID(uv.l_comppk);

                if (attr.GetNamedItem("sz_deptid") != null)
                    uv.sz_deptid = attr.GetNamedItem("sz_deptid").Value;
                else if (uv.l_deptpk != 0)
                    uv.sz_deptid = Database.GetDeptID(uv.l_deptpk);

                if (attr.GetNamedItem("sz_password") != null)
                    uv.sz_password = attr.GetNamedItem("sz_password").Value;

                if (attr.GetNamedItem("sz_userid") != null)
                    uv.sz_userid = attr.GetNamedItem("sz_userid").Value;
                else if (uv.l_userpk != 0)
                    uv.sz_userid = Database.GetUserID(uv.l_userpk);

                uv.operators = Operator.GetOperators(uv);
            }
            catch (Exception e)
            {
                Log.WriteLog("Error reading user values. " + e.ToString(), "Error reading user values. " + e.Message, 2);
                return false;
            }

            return bRetval;
        }
        public static void init()
        {
            // Required values
            Settings.sz_parsepath = add_slash(Settings.GetString("ParsePath"));
            Settings.sz_dbconn = String.Format("DSN={0};UID={1};PWD={2};",
                Settings.GetString("DSN"),
                Settings.GetString("UID"),
                Settings.GetString("PWD"));

            // Optional values
            Settings.debug = Settings.GetValue("Debug", false);
            Settings.sz_dumppath = add_slash(Settings.GetValue("DumpPath", ""));
            Settings.l_statuspollinterval = Settings.GetValue("StatusPollInterval", 60);
            Settings.l_retryinterval = Settings.GetValue("RetryInterval", 60);
            Settings.l_retries = Settings.GetValue("Retries", 2);
            Settings.l_messagetype = Settings.GetValue("MessageType", 7);
            Settings.l_cpuaffinity = Settings.GetValue("CPUAffinity", 0);

            // Init log, default is syslog off / logfile on
            Log.InitLog(
                Settings.GetValue("SyslogApp", "cbserver"),
                Settings.GetValue("SyslogServer", "localhost"),
                Settings.GetValue("SyslogPort", 514),
                Settings.GetValue("Syslog", false),
                Settings.GetValue("LogFileName", "cbserver"),
                Settings.GetValue("LogFile", true));
        }
        public static void start()
        {
            // Write startup info
            Log.WriteLog(String.Format("Debug mode: {0}", Settings.debug), 9);
            Log.WriteLog(String.Format("Dump path: {0}", Settings.sz_dumppath), 9);
            Log.WriteLog(String.Format("Parse path: {0}", Settings.sz_parsepath), 9);

            if (Settings.l_statuspollinterval > 0)
                Log.WriteLog(String.Format("Status poll interval is {0} seconds", Settings.l_statuspollinterval), 9);
            else
                Log.WriteLog(String.Format("Status poll interval is disabled"), 9);

            // Set CPU affinity
            if (Settings.l_cpuaffinity > 0)
                System.Diagnostics.Process.GetCurrentProcess().ProcessorAffinity = (System.IntPtr)Settings.l_cpuaffinity;

            // Start threads
            Settings.running = true; // has to be set before threads start

            Log.WriteLog("Starting keyreader thread", 9);
            new Thread(new ThreadStart(Tools.KeyReaderThread)).Start();

            Log.WriteLog("Starting parser thread", 9);
            new Thread(new ThreadStart(CBParser.CheckFilesThread)).Start();

            if (Settings.l_statuspollinterval > 0)
            {
                Log.WriteLog("Starting status thread", 9);
                new Thread(new ThreadStart(CBStatus.CheckStatusThread)).Start();
            }
        }

        private static string add_slash(string path)
        {
            if (path.Length > 0 && !path.EndsWith(@"\"))
                path += @"\";

            return path;
        }
    }

    public class Operator
    {
        // operator settings
        public int l_operator = 0;
        public string sz_operatorname = "";
        public int l_type = 0; // one2many, tmobile

        // login settings for interface
        public string sz_url = "";
        public string sz_login_id = "";
        public string sz_login_name = "";
        public string sz_login_password = "";
        
        public Version api_version = new Version("2.3");
        public COORDINATESYSTEM coordinate_type = COORDINATESYSTEM.RD;

        // methods
        public static Operator Getoperator(int l_operator)
        {
            string qryOperators = String.Format(@"SELECT
                                        OP.l_operator,
                                        OP.sz_operatorname,
                                        OP.l_type,
                                        OP.sz_url,
                                        OP.sz_user sz_login_id,
                                        OP.sz_name sz_login_name,
                                        OP.sz_password sz_login_password
                                    FROM
                                        LBAOPERATORS OP
                                    WHERE
                                        OP.f_cb=1
                                        AND OP.l_operator={0}"
                , l_operator);

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperators, dbConn);
            OdbcDataReader rsOperators;

            Operator op = new Operator();

            try
            {
                dbConn.Open();

                rsOperators = cmdOperators.ExecuteReader();

                if(rsOperators.Read())
                {

                    op.l_operator = rsOperators.GetInt32(0);
                    op.sz_operatorname = rsOperators.GetString(1);
                    op.l_type = rsOperators.GetInt32(2);
                    op.sz_url = rsOperators.GetString(3);
                    op.sz_login_id = rsOperators.GetString(4);
                    op.sz_login_name = rsOperators.GetString(5);
                    op.sz_login_password = rsOperators.GetString(6);
                }
                rsOperators.Close();
                rsOperators.Dispose();
                cmdOperators.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("GetOperators(Settings s) (exception={0})", e.Message),
                    String.Format("GetOperators(Settings s) (exception={0})", e),
                    2);
            }

            return op;
        }
        public static Operator[] GetOperators(Settings oUser)
        {
            //string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password, OP.f_alertapi, OP.f_statusapi, OP.f_internationalapi, OP.f_statisticsapi FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.l_deptpk.ToString() + " ORDER BY l_operator";
            string qryOperators = String.Format(@"SELECT
                                        OP.l_operator,
                                        OP.sz_operatorname,
                                        OP.l_type,
                                        OP.sz_url,
                                        isnull(OD.sz_userid, OP.sz_user) sz_login_id,
                                        isnull(OD.sz_username, OP.sz_name) sz_login_name,
                                        isnull(OD.sz_userpassword, OP.sz_password) sz_login_password
                                    FROM
                                        LBAOPERATORS OP, 
                                        LBAOPERATORS_X_DEPT OD 
                                    WHERE
                                        OP.f_cb=1
                                        AND OD.l_operator=OP.l_operator 
                                        AND OD.l_deptpk={0}
                                    ORDER BY
                                        OP.l_operator"
                , oUser.l_deptpk.ToString());

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperators, dbConn);
            OdbcDataReader rsOperators;

            List<Operator> Operators = new List<Operator>();

            try
            {
                dbConn.Open();

                rsOperators = cmdOperators.ExecuteReader();

                while (rsOperators.Read())
                {
                    Operator op = new Operator();

                    op.l_operator = rsOperators.GetInt32(0);
                    op.sz_operatorname = rsOperators.GetString(1);
                    op.l_type = rsOperators.GetInt32(2);
                    op.sz_url = rsOperators.GetString(3);
                    op.sz_login_id = rsOperators.GetString(4);
                    op.sz_login_name = rsOperators.GetString(5);
                    op.sz_login_password = rsOperators.GetString(6);

                    Operators.Add(op);
                }
                rsOperators.Close();
                rsOperators.Dispose();
                cmdOperators.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("GetOperators(Settings s) (exception={0})", e.Message),
                    String.Format("GetOperators(Settings s) (exception={0})", e),
                    2);
            }

            return Operators.ToArray();
        }
        public object GetDefaultValues(Type type)
        {
            object ret = new object();

            try
            {
                StreamReader r = new StreamReader(String.Format(@"operators/{0}.xml", sz_operatorname));
                XmlSerializer s = new XmlSerializer(type);

                ret = s.Deserialize(r);

                r.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(String.Format("Failed to get default settings, using standard defaults. {0}", e.Message),2);
            }

            return ret;
        }
    }

    public class Constant
    {
        // main return values for ParseXMLFile
        public const int OK = 0;
        public const int RETRY = -1;
        public const int FAILED = -2;

        // mainstatuses
        // common
        public const int QUEUE = 199;
        public const int PARSING = 200;
        public const int FAILEDRETRY = 290;
        // Alert
        public const int PREPARING = 300;
        public const int PROCESSINGSUB = 305;
        public const int PREPAREDNOCC = 310;
        public const int PREPARED = 311;
        public const int USERCONFIRMED = 320;
        public const int USERCANCELLED = 330;
        public const int SENDING = 340;
        // InternationalAlert
        public const int INTPREPARING = 400;
        public const int INTPROCESSINGSUB = 405;
        public const int INTPREPAREDNOCC = 410;
        public const int INTPREPARED = 411;
        public const int INTUSERCONFIRMED = 420;
        public const int INTUSERCANCELLED = 430;
        public const int INTSENDING = 440;
        // CellBroadcast
        public const int CBPREPARING = 500;
        public const int CBQUEUED = 510;
        public const int CBACTIVE = 540;
        public const int CBPAUSED = 590;

        public const int CANCELLING = 800;
        public const int FINISHED = 1000;
        public const int CANCELLED = 2000;

        // exceptions from AlertiX
        public const int EXC_executeAreaAlert = 42001;
        public const int EXC_prepareAreaAlert = 42002;
        public const int EXC_executeCustomAlert = 42003;
        public const int EXC_prepareCustomAlert = 42004;
        public const int EXC_executePreparedAlert = 42005;
        public const int EXC_cancelPreparedAlert = 42006;
        public const int EXC_executeIntAlert = 42007;
        public const int EXC_prepareIntAlert = 42008;
        public const int EXC_countByCC = 42009;
        public const int EXC_countryDistributionByCc = 42010;
        // errors from AlertiX
        public const int ERR_executeAreaAlert = 42011;
        public const int ERR_prepareAreaAlert = 42012;
        public const int ERR_executeCustomAlert = 42013;
        public const int ERR_prepareCustomAlert = 42014;
        public const int ERR_executePreparedAlert = 42015;
        public const int ERR_cancelPreparedAlert = 42016;
        public const int ERR_executeIntAlert = 42017;
        public const int ERR_prepareIntAlert = 42018;
        public const int ERR_countByCC = 42019;
        public const int ERR_countryDistributionByCc = 42020;
        // errors & exceptions from parsing
        public const int ERR_NOTAG_MSG = 42101;
        public const int ERR_NOATTR_AREA = 42102;
        public const int ERR_NOTAG_POLY = 42103;
        public const int EXC_GetAlertMsg = 42104;
        public const int ERR_NOTAG_CCODE = 42105;
        public const int ERR_EllipseToPoly = 42106;
        // job errors from AlertiX
        public const int ERR_JobError = 42201;

    }

    public enum LBATYPE
    {
        LBAS = 0,
        TAS = 1,
        CB = 2
    }

    public enum COORDINATESYSTEM
    {
        WGS84 = 0,
        UTM31 = 1,
        UTM32 = 2,
        RD = 3
    }
}
