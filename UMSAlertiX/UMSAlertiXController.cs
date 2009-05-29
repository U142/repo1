using System;
using System.Collections.Generic;
//using System.Linq;
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
        private string szWSUser = "";
        private string szWSPass = "";
        private string szParsePath = "";
        private int lMessageValidity = 240; // minutes
        private UMSAlertiXLog oLog;
//        public OdbcConnection dbConn;

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
//                dbConn = new OdbcConnection(szDBConn);
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
        public string wsuser
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

//			lRetVal = (int)cmd.ExecuteScalar();

            cmd.Dispose();
            dbConn.Close();

            return lRetVal;
        }

        public int GetSendingProc(int lRefNo)
        {
            string szQuery;
            int lRetVal = 0;

            szQuery = "SELECT l_proc FROM LBASEND where l_refno=" + lRefNo.ToString();

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

 /*       public void CheckSocket()
        {
        }*/
    }
}