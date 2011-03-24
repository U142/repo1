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
        public static bool live;
        public static long threads = 0;

        public static string sz_parsepath;
        public static string sz_dumppath;
        public static string sz_dbconn;
        public static int l_cpuaffinity;
        public static int l_statuspollinterval;
        public static int l_heartbeatinterval;
        public static int l_retryinterval;
        public static int l_retries;
        public static int l_messagetype;
        public static int l_linktestinterval;

        public static int l_testmessagetype;
        public static int l_testmessagegroup;

        // Instanced settings
        public int l_comppk = 0;
        public int l_deptpk = 0;
        public long l_userpk = -1;

        public string sz_compid = "";
        public string sz_deptid = "";
        public string sz_userid = "";
        public string sz_password = "";

        public Operator[] operators;

        // methods
        public static Settings SystemUser(int l_deptpk, int l_comppk)
        {
            Settings ret = new Settings();
            
            try
            {
                //ret.l_deptpk = Settings.GetInt("DeptPK");
                //ret.l_comppk = Settings.GetInt("CompPK");
                ret.l_deptpk = l_deptpk;
                ret.l_comppk = l_comppk;
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
                else if (uv.l_userpk != -1)
                    uv.sz_userid = Database.GetUserID(uv.l_userpk);

                uv.operators = Operator.GetOperators(uv);
                
                /*// Get default values
                foreach (Operator op in uv.operators)
                {
                    switch (op.l_type)
                    {
                        case 1: // AlertiX (not supported)
                            break;
                        case 2: // one2many
                            op.def_values = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
                            break;
                        case 3: // tmobile
                            op.def_values = (CB_tmobile_defaults)op.GetDefaultValues(typeof(CB_tmobile_defaults));
                            break;
                        default:
                            break;
                    }
                }*/
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
            // Check that all sub-paths exist (throws exception if one is missing
            verify_parsefolder();

            Settings.sz_dbconn = String.Format("DSN={0};UID={1};PWD={2};",
                Settings.GetString("DSN"),
                Settings.GetString("UID"),
                Settings.GetString("PWD"));
            
            // Optional values
            Settings.debug = Settings.GetValue("Debug", false);
            Settings.live = Settings.GetValue("Live", true);
            Settings.sz_dumppath = add_slash(Settings.GetValue("DumpPath", exec_folder()));
            Settings.l_statuspollinterval = Settings.GetValue("StatusPollInterval", 60); // seconds
            Settings.l_heartbeatinterval = Settings.GetValue("HeartBeatInterval", 10); // minutes
            Settings.l_retryinterval = Settings.GetValue("RetryInterval", 60);
            Settings.l_retries = Settings.GetValue("Retries", 2);
            Settings.l_messagetype = Settings.GetValue("MessageType", 7);
            Settings.l_cpuaffinity = Settings.GetValue("CPUAffinity", 0);
            Settings.l_testmessagegroup = Settings.GetValue("TestMessageGroup", 16);
            Settings.l_testmessagetype = Settings.GetValue("TestMessageType", 70);
            Settings.l_linktestinterval = Settings.GetValue("LinkTestInterval", 60); // disabled default

            // Init log, default is syslog off / logfile on
            Log.InitLog(
                Settings.GetValue("SyslogApp", "cbserver"),
                Settings.GetValue("SyslogServer", "localhost"),
                Settings.GetValue("SyslogPort", 514),
                Settings.GetValue("Syslog", false),
                add_slash(Settings.GetValue("LogFilePath", exec_folder())),
                Settings.GetValue("LogFileName", "cbserver"),
                Settings.GetValue("LogFile", true),
                Settings.GetValue("LogDatabase", true));
        }

        private static string add_slash(string path)
        {
            if (path.Length > 0 && !path.EndsWith(@"\"))
                path += @"\";

            if (!Directory.Exists(path))
                throw new Exception(String.Format("Could not find path {0}", path));

            return path;
        }
        private static void verify_parsefolder()
        {
            string[] subfolders = { "eat", "retry", "failed", "done" };
            foreach (string folder in subfolders)
            {
                if (!Directory.Exists(Settings.sz_parsepath + folder))
                    throw new Exception(String.Format("Could not find path {0}", Settings.sz_parsepath + folder));
            }
        }

        public static string exec_folder()
        {
            try
            {
                return add_slash(System.IO.Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location));
            }
            catch
            {
                return "";
            }
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

        //public object def_values = null;

        public Version api_version = null;
        public COORDINATESYSTEM coordinate_type = COORDINATESYSTEM.WGS84;

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
                                        OP.sz_password sz_login_password,
                                        isnull(OP.sz_version, '1.0') sz_version,
                                        isnull(OP.l_coordinatetype, 0) l_coordinatetype
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
                    op.api_version = new Version(rsOperators.GetString(7));
                    op.coordinate_type = (COORDINATESYSTEM)rsOperators.GetInt32(8);
                }
                rsOperators.Close();
                dbConn.Close();
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
        public static Operator[] GetOperatorsDept(Settings oUser)
        {
            //string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password, OP.f_alertapi, OP.f_statusapi, OP.f_internationalapi, OP.f_statisticsapi FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.l_deptpk.ToString() + " ORDER BY l_operator";
            string qryOperators = String.Format(@"SELECT
                                        OP.l_operator,
                                        OP.sz_operatorname,
                                        OP.l_type,
                                        OP.sz_url,
                                        isnull(OD.sz_userid, OP.sz_user) sz_login_id,
                                        isnull(OD.sz_username, OP.sz_name) sz_login_name,
                                        isnull(OD.sz_userpassword, OP.sz_password) sz_login_password,
                                        isnull(OP.sz_version, '1.0') sz_version,
                                        isnull(OP.l_coordinatetype, 0) l_coordinatetype
                                    FROM
                                        LBAOPERATORS OP, 
                                        LBAOPERATORS_X_DEPT OD 
                                    WHERE
                                        OP.f_cb=1
                                        AND OP.f_active=1
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
                    op.api_version = new Version(rsOperators.GetString(7));
                    op.coordinate_type = (COORDINATESYSTEM)rsOperators.GetInt32(8);

                    Operators.Add(op);
                }
                rsOperators.Close();
                dbConn.Close();
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
        public static Operator[] GetOperators(Settings oUser)
        {
            //string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password, OP.f_alertapi, OP.f_statusapi, OP.f_internationalapi, OP.f_statisticsapi FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.l_deptpk.ToString() + " ORDER BY l_operator";
            string qryOperators = String.Format(@"SELECT
                                        OP.l_operator,
                                        OP.sz_operatorname,
                                        OP.l_type,
                                        OP.sz_url,
                                        OP.sz_user sz_login_id,
                                        OP.sz_name sz_login_name,
                                        OP.sz_password sz_login_password,
                                        '1.0' sz_version,
                                        isnull(l_coordinatetype,0) l_coordinatetype
                                    FROM
                                        LBAOPERATORS OP
                                    WHERE
                                        OP.f_cb=1
                                        AND OP.f_active=1
                                   ORDER BY
                                        OP.l_operator"
                //CHANGED: 0 l_coordinatetype
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
                    op.api_version = new Version(rsOperators.GetString(7));
                    op.coordinate_type = (COORDINATESYSTEM)rsOperators.GetInt32(8);

                    Operators.Add(op);
                }
                rsOperators.Close();
                dbConn.Close();
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
                StreamReader r = new StreamReader(String.Format(@"{0}operators/{1}.xml", Settings.exec_folder() ,sz_operatorname));
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
        public const int DELETE = -100;

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
        public const int CBUSERCONFIRMED = 520;
        public const int CBUSERCANCELLED = 530;
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

    public class LBAParameter 
    {
        // populate when creating to make sure the latest information is used
        public LBAParameter()
        {
            string qry = String.Format(@"SELECT l_comppk, l_deptpk, l_channelno, l_test_channelno, l_heartbeat, l_interval FROM LBAPARAMETER");

            try
            {
                OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(qry, dbConn);

                dbConn.Open();

                OdbcDataReader rs = cmd.ExecuteReader();

                if (rs.Read())
                {
                    this.l_comppk = rs.GetInt32(0);
                    this.l_deptpk = rs.GetInt32(1);
                    this.l_channelno = rs.GetInt32(2);
                    this.l_testchannelno = rs.GetInt32(3);
                    this.l_heartbeat = rs.GetInt32(4);
                    this.l_repetitioninterval = rs.GetInt32(5);
                }
                // close
                rs.Close();
                dbConn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("LBAParameter constructor failed (exception={0})", e.Message),
                    String.Format("LBAParameter constructor failed (exception={0})", e),
                    2);
            }
        }

        public int l_comppk;
        public int l_deptpk;

        public int l_channelno;
        public int l_testchannelno;
        public int l_heartbeat;
        public int l_repetitioninterval;
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
