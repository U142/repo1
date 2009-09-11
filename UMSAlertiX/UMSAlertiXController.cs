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

        public Operator[] Operators;

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
/*        public string wsuser
        {
            get
            {
                return szWSUser;
            }
            set
            {
                szWSUser = value;
            }
        }
        public string wspass
        {
            get
            {
                return szWSPass;
            }
            set
            {
                szWSPass = value;
            }
        }*/
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

            cmd.Dispose();
            dbConn.Close();

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

            cmd.Dispose();
            dbConn.Close();

            return lRetVal;
        }

        public int GetSendingProc(int lRefNo, int lOperator)
        {
            string szQuery;
            int lRetVal = 0;

            szQuery = "SELECT l_proc FROM LBASEND where l_refno=" + lRefNo.ToString() + " and l_operator=" + lOperator.ToString();

            OdbcConnection dbConn = new OdbcConnection(szDBConn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsProcessed;

            dbConn.Open();
            rsProcessed = cmd.ExecuteReader();

            if (rsProcessed.Read())
                if (!rsProcessed.IsDBNull(0))
                    lRetVal = rsProcessed.GetInt32(0);

            cmd.Dispose();
            dbConn.Close();

            return lRetVal;
        }

        public Operator GetOperator(int l_operator)
        {
            Operator oRet = new Operator();

            foreach (Operator op in Operators)
            {
                if (l_operator == op.l_operator)
                {
                    oRet = op;
                    return oRet;
                }
            }
            return oRet;
        }

        public Operator[] GetOperators(int l_refno)
        {
            int lCount=0;
            // Select operators from LBAOPERATORS joina med LBASEND (use status=200 or 290 to only pick up sendings that failed)
            string qryOperatorCount = "SELECT COUNT(l_operator) FROM LBASEND WHERE (l_status=200 or l_status=290) AND l_refno=" + l_refno.ToString();
            string qryOperators = "SELECT l_operator from LBASEND WHERE (l_status=200 or l_status=290) AND l_refno=" + l_refno.ToString();

            OdbcConnection dbConn = new OdbcConnection(dsn);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperatorCount, dbConn);
            OdbcDataReader rsOperators;

            dbConn.Open();

            int lOperatorCount = (int)cmdOperators.ExecuteScalar();

            cmdOperators.CommandText = qryOperators;

            rsOperators = cmdOperators.ExecuteReader();
            Operator[] retOperators = new Operator[lOperatorCount];
            while (rsOperators.Read())
            {
                retOperators[lCount] = GetOperator(rsOperators.GetInt32(0));
                lCount++;
            }
            return retOperators;
        }

        public Operator[] GetOperators()
        {
            return Operators;
        }

        public void InitOperators()
        {
            int lCount = 0;

            string qryOperatorCount = "SELECT COUNT(l_operator) FROM LBAOPERATORS";
            string qryOperators = "SELECT l_operator, sz_operatorname, sz_url, sz_user, sz_password FROM LBAOPERATORS ORDER BY l_operator";

            OdbcConnection dbConn = new OdbcConnection(dsn);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperatorCount, dbConn);
            OdbcDataReader rsOperators;

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
        }
    }

    public class Constant
    {
        public const int OK = 0;

        public const int ERR_CANCEL = -1;
        public const int ERR_GENERAL = -2;

        public const int ERR_NOTAG_LBA = -2001;
        public const int ERR_NOTAG_ALERTPOLY = -2002;
        public const int ERR_NOMSG = -2003;
        public const int ERR_NOREFNO = -2004;
    }

    public class Operator
    {
        public int l_operator=0;
        public string sz_operatorname="";
        public string sz_url="";
        public string sz_user="";
        public string sz_password="";
    }
}