using System;
using System.Collections.Generic;
using System.Text;
using System.Data.Odbc;
using UMSAlertiX.AlertiXAreaApi;

namespace UMSAlertiX
{
    // Controller class to handle console input and run flag
    public class UMSAlertiXController
    {
        private bool bRun = true;
        private string szDBConn;
        private int lThreadsRunning=0;
        private string szWSStatusAPI = "";
        private string szWSAlertAPI = "";
        private string szWSAreaAPI = "";
        private string szParsePath = "";
        private int lMessageValidity = 240; // minutes
        private int lAffinity = 1;
        private UMSAlertiXLog oLog;
        private UMSAlertiXWebServer oWebServer;

        public UMSAlertiXLog log
        {
            get
            {
                return oLog;
            }
            set
            {
                oLog = value;
            }
        }

        public UMSAlertiXWebServer webserver
        {
            get
            {
                return oWebServer;
            }
            set
            {
                oWebServer = value;
            }
        }

        public int message_validity
        {
            get
            {
                return lMessageValidity;
            }
            set
            {
                lMessageValidity = value;
            }
        }

        public int affinity
        {
            get
            {
                return lAffinity;
            }
            set
            {
                lAffinity = value;
            }
        }

        public int threads
        {
            get
            {
                return lThreadsRunning;
            }
            set
            {
                lThreadsRunning = value;
            }
        }

        public string dsn
        {
            get
            {
                return szDBConn;
            }
            set
            {
                szDBConn = value;
            }
        }

        public bool running
        {
            get 
            { 
                return bRun; 
            }
            set 
            { 
                bRun = value; 
            }
        }

        public string statusapi
        {
            get
            {
                return szWSStatusAPI;
            }
            set
            {
                szWSStatusAPI = value;
            }
        }
        public string alertapi
        {
            get
            {
                return szWSAlertAPI;
            }
            set
            {
                szWSAlertAPI = value;
            }
        }
        public string areaapi
        {
            get
            {
                return szWSAreaAPI;
            }
            set
            {
                szWSAreaAPI = value;
            }
        }

        public string parsepath
        {
            get
            {
                return szParsePath;
            }
            set
            {
                szParsePath = value;
                if(!szParsePath.EndsWith("\\"))
                    szParsePath+="\\";
            }
        }

        public void GetKey()
        {
            while (bRun)
            {
                switch (Console.ReadLine().ToLower())
                {
                    case "h":
                    case "?":
                    case "help":
                        Console.WriteLine("# List of commands:", 1);
                        Console.WriteLine("#", 1);
                        Console.WriteLine("#   h, help\tthis page", 1);
                        Console.WriteLine("#   q, quit\tstop parser", 1);
                        break;
                    case "q":
                    case "quit":
                        Console.WriteLine("-- Shutting down --");
                        bRun = false;
                        break;
                    default:
                        Console.WriteLine("# Uknown command, try \"help\" for more info.", 1);
                        break;
                }
            }
            threads--;
            Console.WriteLine("# Stopped Watcher thread", 1);
            oWebServer.Stop();
            threads--;
        }

        public int ExecDB(string szQuery, string szConn)
        {
            int lRetVal = 0;

            OdbcConnection dbConn = new OdbcConnection(szDBConn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);

            dbConn.Open();
            lRetVal = cmd.ExecuteNonQuery();
            cmd.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return lRetVal;
        }

        public int GetRequestType(int lRefNo)
        {
            int lRetVal = 0;
            string szQuery;

            szQuery = "SELECT l_requesttype FROM LBASEND WHERE l_refno=" + lRefNo.ToString();

            OdbcConnection dbConn = new OdbcConnection(szDBConn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsRequestType;

            dbConn.Open();
            rsRequestType = cmd.ExecuteReader();

            if (rsRequestType.Read())
                if (!rsRequestType.IsDBNull(0))
                    lRetVal = rsRequestType.GetByte(0);

            rsRequestType.Close();
            rsRequestType.Dispose();
            cmd.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return lRetVal;
        }

        public int GetSendingStatus(int lRefNo)
        {
            int lRetVal = 0;
            string szQuery;

            szQuery = "SELECT l_status FROM LBASEND WHERE l_refno=" + lRefNo.ToString();

            OdbcConnection dbConn = new OdbcConnection(szDBConn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsSendingStatus;

            dbConn.Open();
            rsSendingStatus = cmd.ExecuteReader();

            if (rsSendingStatus.Read())
                if (!rsSendingStatus.IsDBNull(0))
                    lRetVal = rsSendingStatus.GetInt32(0);

            rsSendingStatus.Close();
            rsSendingStatus.Dispose();
            cmd.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return lRetVal;
        }

        public string GetJobID(int l_refno, int l_operator)
        {
            string szRetVal = "";
            string szQuery;

            szQuery = "SELECT sz_jobid FROM LBASEND WHERE l_refno=" + l_refno.ToString() + " AND l_operator=" + l_operator.ToString();

            OdbcConnection dbConn = new OdbcConnection(szDBConn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsSendingStatus;

            dbConn.Open();
            rsSendingStatus = cmd.ExecuteReader();

            if (rsSendingStatus.Read())
                if (!rsSendingStatus.IsDBNull(0))
                    szRetVal = rsSendingStatus.GetString(0);

            rsSendingStatus.Close();
            rsSendingStatus.Dispose();
            cmd.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return szRetVal.Trim();
        }

        public bool GetSendingProc(int lRefNo, int lOperator, ref int lItems, ref int lProc, ref int lStatus)
        {
            string szQuery;

            szQuery = "SELECT l_items, l_proc, l_status FROM LBASEND where l_refno=" + lRefNo.ToString() + " and l_operator=" + lOperator.ToString();

            OdbcConnection dbConn = new OdbcConnection(szDBConn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsProcessed;

            dbConn.Open();
            rsProcessed = cmd.ExecuteReader();

            if (rsProcessed.Read())
            {
                if (!rsProcessed.IsDBNull(0))
                    lItems = rsProcessed.GetInt32(0);
                if (!rsProcessed.IsDBNull(1))
                    lProc = rsProcessed.GetInt32(1);
                if (!rsProcessed.IsDBNull(2))
                    lStatus = rsProcessed.GetInt32(2);
            }
            rsProcessed.Close();
            rsProcessed.Dispose();
            cmd.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return true;
        }

        public Operator GetOperator(int l_operator)
        {
            Operator oRet = new Operator();

            string qryOperator = "SELECT l_operator, sz_operatorname, sz_url, sz_user, sz_password FROM LBAOPERATORS WHERE sz_version='1.0' AND l_operator=" + l_operator.ToString();

            OdbcConnection dbConn = new OdbcConnection(dsn);
            OdbcCommand cmdOperator = new OdbcCommand(qryOperator, dbConn);
            OdbcDataReader rsOperator;

            dbConn.Open();

            rsOperator = cmdOperator.ExecuteReader();

            while (rsOperator.Read())
            {
                oRet.l_operator = rsOperator.GetInt32(0);
                oRet.sz_operatorname = rsOperator.GetString(1);
                oRet.sz_url = rsOperator.GetString(2);
                oRet.sz_user = rsOperator.GetString(3);
                oRet.sz_password = rsOperator.GetString(4);
            }
            rsOperator.Close();
            rsOperator.Dispose();
            cmdOperator.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return oRet;
        }

        public Operator[] GetAlertOperators(long l_alertpk)
        {
            int lCount = 0;

            string qryOperatorCount = "SELECT COUNT(OP.l_operator) FROM LBAOPERATORS OP, PAALERT_LBA PA WHERE OP.sz_version='1.0' AND PA.l_operator=OP.l_operator AND PA.l_alertpk=" + l_alertpk.ToString();
            string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password FROM LBAOPERATORS OP, PAALERT_LBA PA WHERE OP.sz_version='1.0' AND PA.l_operator=OP.l_operator AND PA.l_alertpk=" + l_alertpk.ToString() + " ORDER BY l_operator";

            OdbcConnection dbConn = new OdbcConnection(dsn);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperatorCount, dbConn);
            OdbcDataReader rsOperators;

            Operator[] Operators;

            dbConn.Open();

            int lOperatorCount = (int)cmdOperators.ExecuteScalar();

            cmdOperators.CommandText = qryOperators;

            rsOperators = cmdOperators.ExecuteReader();
            Operators = new Operator[lOperatorCount];

            while (rsOperators.Read())
            {
                Operators[lCount] = new Operator();

                Operators[lCount].l_operator = rsOperators.GetInt32(0);
                Operators[lCount].sz_operatorname = rsOperators.GetString(1);
                Operators[lCount].sz_url = rsOperators.GetString(2);
                Operators[lCount].sz_user = rsOperators.GetString(3);
                Operators[lCount].sz_password = rsOperators.GetString(4);

                lCount++;
            }
            rsOperators.Close();
            rsOperators.Dispose();
            cmdOperators.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return Operators;
        }

        public Operator[] GetOperators(ref UserValues oUser)
        {
            int lCount = 0;

            string qryOperatorCount = "SELECT COUNT(OP.l_operator) FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OP.sz_version='1.0' AND OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.l_deptpk.ToString();
            string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OP.sz_version='1.0' AND OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.l_deptpk.ToString() + " ORDER BY l_operator";

            OdbcConnection dbConn = new OdbcConnection(dsn);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperatorCount, dbConn);
            OdbcDataReader rsOperators;

            Operator[] Operators;

            dbConn.Open();

            int lOperatorCount = (int)cmdOperators.ExecuteScalar();

            cmdOperators.CommandText = qryOperators;

            rsOperators = cmdOperators.ExecuteReader();
            Operators = new Operator[lOperatorCount];

            while (rsOperators.Read())
            {
                Operators[lCount] = new Operator();

                Operators[lCount].l_operator = rsOperators.GetInt32(0);
                Operators[lCount].sz_operatorname = rsOperators.GetString(1);
                Operators[lCount].sz_url = rsOperators.GetString(2);
                Operators[lCount].sz_user = rsOperators.GetString(3);
                Operators[lCount].sz_password = rsOperators.GetString(4);

                lCount++;
            }
            rsOperators.Close();
            rsOperators.Dispose();
            cmdOperators.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            return Operators;
        }
    }

    public class Constant
    {
        // main return values for ParseXMLFile
        public const int OK     = 0;
        public const int RETRY  = -1;
        public const int FAILED = -2;

        // exceptions from cellvision
        public const int EXC_executeAreaAlert       = 42001;
        public const int EXC_prepareAreaAlert       = 42002;
        public const int EXC_execureCustomAlert     = 42003;
        public const int EXC_prepareCustomAlert     = 42004;
        public const int EXC_executePreparedAlert   = 42005;
        public const int EXC_cancelPreparedAlert    = 42006;
        // errors from cellvision
        public const int ERR_executeAreaAlert       = 42011;
        public const int ERR_prepareAreaAlert       = 42012;
        public const int ERR_executeCustomAlert     = 42013;
        public const int ERR_prepareCustomAlert     = 42014;
        public const int ERR_executePreparedAlert   = 42015;
        public const int ERR_cancelPreparedAlert    = 42016;
        // errors & exceptions from parsing
        public const int ERR_NOTAG_MSG              = 42101;
        public const int ERR_NOATTR_AREA            = 42102;
        public const int ERR_NOTAG_POLY             = 42103;
        public const int EXC_GetAlertMsg            = 42104;
        public const int ERR_NOTAG_CCODE            = 42105;
        public const int ERR_EllipseToPoly          = 42106;
    }

    public class Operator
    {
        public int l_operator=0;
        public string sz_operatorname="";
        public string sz_url="";
        public string sz_user="";
        public string sz_password="";
    }

    public class UserValues
    {
        public int l_comppk=0;
        public int l_deptpk=0;
        public long l_userpk=0;

        public string sz_compid="";
        public string sz_deptid="";
        public string sz_userid="";
        public string sz_password="";

        public Operator[] operators;
    }
}