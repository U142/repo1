using System;
using System.Data.Odbc;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using umssettings;
using umswhitelists;

namespace umsalertix
{
    public class Settings : _settings
    {
        // General (global settings used in the app)
        public static string sz_parsepath;
        public static string sz_dbconn;
        public static string sz_dbconn_whitelist;
        public static string sz_executemode;
        public static int l_validity;
        public static int l_statistics_timer;

        // which threads to run
        public static bool tr_parser;
        public static bool tr_statistics;
        public static bool tr_stat_cccount;
        public static bool tr_stat_ccsend;
        public static bool tr_stat_jobcount;
        public static bool tr_stat_jobsend;

        // Instanced settings
        public int l_comppk = 0;
        public int l_deptpk = 0;
        public long l_userpk = 0;

        public string sz_compid = "";
        public string sz_deptid = "";
        public string sz_userid = "";
        public string sz_password = "";

        public Operator[] operators;
    }

    public class WhiteListConfig : _whitelists { }

    public class Operator
    {
        public int l_cc_from = 47;
        public string sz_iso_from = "NO";
        
        public int l_operator = 0;
        public string sz_operatorname = "";
        public string sz_url = "";
        public string sz_user = "";
        public string sz_password = "";

        public bool b_alertapi = false;
        public bool b_statusapi = false;
        public bool b_internationalapi = false;
        public bool b_statisticsapi = false;

        public static Operator[] GetOperators()
        {
            string qryOperators = "SELECT l_operator, sz_operatorname, sz_url, sz_user, sz_password, f_alertapi, f_statusapi, f_internationalapi, f_statisticsapi FROM LBAOPERATORS WHERE f_internationalapi=1 ORDER BY l_operator";

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
                    op.sz_url = rsOperators.GetString(2);
                    op.sz_user = rsOperators.GetString(3);
                    op.sz_password = rsOperators.GetString(4);
                    op.b_alertapi = rsOperators.GetInt16(5) == 1 ? true : false;
                    op.b_statusapi = rsOperators.GetInt16(6) == 1 ? true : false;
                    op.b_internationalapi = rsOperators.GetInt16(7) == 1 ? true : false;
                    op.b_statisticsapi = rsOperators.GetInt16(8) == 1 ? true : false;
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
                    String.Format("GetOperator() (exception={0})", e.Message),
                    String.Format("GetOperator() (exception={0})", e),
                    2);
            }

            return Operators.ToArray();
        }
        public static Operator[] GetOperators(Settings oUser)
        {
            string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password, OP.f_alertapi, OP.f_statusapi, OP.f_internationalapi, OP.f_statisticsapi FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.l_deptpk.ToString() + " ORDER BY l_operator";

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
                    op.sz_url = rsOperators.GetString(2);
                    op.sz_user = rsOperators.GetString(3);
                    op.sz_password = rsOperators.GetString(4);
                    op.b_alertapi = rsOperators.GetInt16(5) == 1 ? true : false;
                    op.b_statusapi = rsOperators.GetInt16(6) == 1 ? true : false;
                    op.b_internationalapi = rsOperators.GetInt16(7) == 1 ? true : false;
                    op.b_statisticsapi = rsOperators.GetInt16(8) == 1 ? true : false;

                    Operators.Add(op);
                }
                rsOperators.Close();
                rsOperators.Dispose();
                cmdOperators.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch(Exception e)
            {
                Log.WriteLog(
                    String.Format("GetOperators(Settings s) (exception={0})", e.Message),
                    String.Format("GetOperators(Settings s) (exception={0})", e),
                    2);
            }

            return Operators.ToArray();
        }
        public static Operator GetOperator(int l_operator)
        {
            Operator oRet = new Operator();

            string qryOperator = "SELECT l_operator, sz_operatorname, sz_url, sz_user, sz_password, f_alertapi, f_statusapi, f_internationalapi, f_statisticsapi FROM LBAOPERATORS WHERE l_operator=" + l_operator.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdOperator = new OdbcCommand(qryOperator, dbConn);
            OdbcDataReader rsOperator;

            try
            {
                dbConn.Open();

                rsOperator = cmdOperator.ExecuteReader();

                while (rsOperator.Read())
                {
                    oRet.l_operator = rsOperator.GetInt32(0);
                    oRet.sz_operatorname = rsOperator.GetString(1);
                    oRet.sz_url = rsOperator.GetString(2);
                    oRet.sz_user = rsOperator.GetString(3);
                    oRet.sz_password = rsOperator.GetString(4);
                    oRet.b_alertapi = rsOperator.GetInt16(5) == 1 ? true : false;
                    oRet.b_statusapi = rsOperator.GetInt16(6) == 1 ? true : false;
                    oRet.b_internationalapi = rsOperator.GetInt16(7) == 1 ? true : false;
                    oRet.b_statisticsapi = rsOperator.GetInt16(8) == 1 ? true : false;
                }
                rsOperator.Close();
                rsOperator.Dispose();
                cmdOperator.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("GetOperators(int i) (exception={0})", e.Message),
                    String.Format("GetOperators(int i) (exception={0})", e),
                    2);
            }

            return oRet;
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

        public const int CANCELLING = 800;
        public const int FINISHED = 1000;
        public const int CANCELLED = 2000;

        // exceptions from cellvision
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
        // errors from cellvision
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
        // job errors from CellVision
        public const int ERR_JobError = 42201;
    }

    public enum LBATYPE
    {
        LBAS = 0,
        TAS = 1
    }
}