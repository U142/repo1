using System;
using System.Data.Odbc;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace umsalertix
{
    class Database
    {
        public static int Execute(string sz_sql)
        {
            int lRetVal = 0;

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(sz_sql, dbConn);

            try
            {
                dbConn.Open();
                lRetVal = cmd.ExecuteNonQuery();
                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.Execute (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.Execute (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }

            return lRetVal;
        }

        public static int GetRequestType(int l_refno)
        {
            int lRetVal = 0;
            string szQuery;

            szQuery = "SELECT l_requesttype FROM LBASEND WHERE l_refno=" + l_refno.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsRequestType;

            try
            {
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
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetRequestType (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetRequestType (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return lRetVal;
        }
        /*
        public static int GetSendingStatus(int l_refno)
        {
            int lRetVal = 0;
            string szQuery;

            szQuery = "SELECT l_status FROM LBASEND WHERE l_refno=" + l_refno.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsRequestType;

            try
            {
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
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetSendingStatus (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetSendingStatus (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return lRetVal;
        }
        */
        public static string GetCompID(int l_comppk) 
        {
            string ret = "";
            string szQuery;

            szQuery = "SELECT sz_compid FROM BBCOMPANY WHERE l_comppk=" + l_comppk.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rs;

            try
            {
                dbConn.Open();
                rs = cmd.ExecuteReader();

                if (rs.Read())
                    if (!rs.IsDBNull(0))
                        ret = rs.GetString(0);

                rs.Close();
                rs.Dispose();
                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetCompID (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetCompID (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return ret;
        }

        public static string GetDeptID(int l_deptpk)
        {
            string ret = "";
            string szQuery;

            szQuery = "SELECT sz_deptid FROM BBDEPARTMENT WHERE l_deptpk=" + l_deptpk.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rs;

            try
            {
                dbConn.Open();
                rs = cmd.ExecuteReader();

                if (rs.Read())
                    if (!rs.IsDBNull(0))
                        ret = rs.GetString(0);

                rs.Close();
                rs.Dispose();
                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetDeptID (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetDeptID (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return ret;
        }

        public static string GetUserID(long l_userpk)
        {
            string ret = "";
            string szQuery;

            szQuery = "SELECT sz_userid FROM BBUSER WHERE l_userpk=" + l_userpk.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rs;

            try
            {
                dbConn.Open();
                rs = cmd.ExecuteReader();

                if (rs.Read())
                    if (!rs.IsDBNull(0))
                        ret = rs.GetString(0);

                rs.Close();
                rs.Dispose();
                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetUserID (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetUserID (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return ret;
        }

        public static string GetJobIDCount(int l_requestpk, int l_operator)
        {
            string szRetVal = "";
            string szQuery;

            szQuery = "SELECT sz_jobid FROM LBATOURISTCOUNTREQ WHERE l_requestpk=" + l_requestpk.ToString() + " AND l_operator=" + l_operator.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsSendingStatus;

            try
            {
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
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetJobIDCount (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetJobIDCount (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return szRetVal.Trim();
        }
        
        public static string GetJobIDSend(int l_refno, int l_operator)
        {
            string szRetVal = "";
            string szQuery;

            szQuery = "SELECT sz_jobid FROM LBASEND WHERE l_refno=" + l_refno.ToString() + " AND l_operator=" + l_operator.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsSendingStatus;

            try
            {
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
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetJobIDSend (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetJobIDSend (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return szRetVal.Trim();
        }

        public static HashSet<int> GetReqCountries(int l_requestpk)
        {
            HashSet<int> ret = new HashSet<int>();
            string sz_sql = String.Format("SELECT l_cc_to FROM LBATOURISTCOUNTREQ_COUNTRIES WHERE l_requestpk={0}", l_requestpk);

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(sz_sql, dbConn);

            try
            {
                dbConn.Open();
                OdbcDataReader rsCC = cmd.ExecuteReader();

                while (rsCC.Read())
                {
                    ret.Add(rsCC.GetInt32(0));
                }
                rsCC.Close();

                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetReqCountries (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.GetReqCountries (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }

            return ret;
        }

        public static HashSet<int> GetSendCountries(int l_refno)
        {
            HashSet<int> ret = new HashSet<int>();
            string sz_sql = String.Format("SELECT l_cc_to FROM LBASEND_COUNTRIES WHERE l_refno={0}", l_refno);

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(sz_sql, dbConn);

            try
            {
                dbConn.Open();
                OdbcDataReader rsCC = cmd.ExecuteReader();

                while (rsCC.Read())
                {
                    ret.Add(rsCC.GetInt32(0));
                }
                rsCC.Close();

                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetSendCountries (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.GetSendCountries (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }

            return ret;
        }

        public static HashSet<int> GetCountryCodes()
        {
            HashSet<int> ret = new HashSet<int>();
            
            string sz_sql = String.Format("SELECT l_cc FROM LBACOUNTRIES WHERE l_cc>0");

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(sz_sql, dbConn);

            try
            {
                dbConn.Open();
                OdbcDataReader rsCC = cmd.ExecuteReader();

                while (rsCC.Read())
                {
                    ret.Add(rsCC.GetInt32(0));
                }
                rsCC.Close();

                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetCountryCodes (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.GetCountryCodes (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }

            return ret;
        }

        public static bool GetSendingProc(int lRefNo, int lOperator, ref int lItems, ref int lProc, ref int lStatus)
        {
            string szQuery;

            szQuery = "SELECT l_items, l_proc, l_status FROM LBASEND where l_refno=" + lRefNo.ToString() + " and l_operator=" + lOperator.ToString();

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsProcessed;

            try
            {
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
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetSendingProc (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetSendingProc (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return true;
        }

        public static int GetRequestPK()
        {
            int lRetVal = 0;
            string szQuery;

            szQuery = "sp_tas_requestpk";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rs;

            try
            {
                dbConn.Open();
                rs = cmd.ExecuteReader();

                if (rs.Read())
                    if (!rs.IsDBNull(0))
                        lRetVal = rs.GetInt32(0);

                rs.Close();
                rs.Dispose();
                cmd.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetRequestPK (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetRequestPK (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return lRetVal;
        }

        public static int UpdateTries(int lReference, int lTempStatus, int lEndStatus, int lResponse, int lOperator, LBATYPE Type)
        {
            int lMaxTries = 1; // Is really retries so 4 will give 5 tries total
            int lRetVal = 0;
            string szQuery = "";

            if (Type == LBATYPE.LBAS)
            {
                szQuery = "sp_lba_upd_sendtries " + lReference.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + lMaxTries.ToString() + ", " + lResponse.ToString() + ", " + lOperator.ToString();
            }
            else if (Type == LBATYPE.TAS)
            {
                szQuery = "sp_tas_upd_sendtries " + lReference.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + lMaxTries.ToString() + ", " + lResponse.ToString() + ", " + lOperator.ToString();
            }

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsRequestType;

            try
            {
                dbConn.Open();
                rsRequestType = cmd.ExecuteReader();

                if (rsRequestType.Read())
                    if (!rsRequestType.IsDBNull(0))
                        lRetVal = rsRequestType.GetInt32(0);

                cmd.Dispose();
                dbConn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.UpdateTries (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.UpdateTries (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return lRetVal;
        }

        public static void SetListState(Decimal listid, QueueState state)
        {
            string sql = "";

            try
            {
                sql = String.Format("UPDATE WHITELISTS SET l_state={0} WHERE l_whitelistid={1}"
                    , (int)state
                    , listid); 
                
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.SetListState (exception={0}) (sql={1})", e.Message, sql),
                    String.Format("Database.SetListState (exception={0}) (sql={1})", e, sql),
                    2);
            }
        }
        public static void SetListState(Decimal listid, int status, Operator op)
        {
            string sql = "";

            try
            {
                sql = String.Format("UPDATE WHITELISTS_X_OPERATOR SET l_status={0} WHERE l_whitelistid={1} AND l_operator={2}"
                    , status
                    , listid
                    , op.l_operator);

                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.SetListState (exception={0}) (sql={1})", e.Message, sql),
                    String.Format("Database.SetListState (exception={0}) (sql={1})", e, sql),
                    2);
            }
        }

        public static void DeleteWhitelist(Decimal listid)
        {
            string sql = "";

            try
            {
                sql = String.Format("sp_tcs_del_whitelist {0}", listid);

                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.DeleteWhitelist (exception={0}) (sql={1})", e.Message, sql),
                    String.Format("Database.DeleteWhitelist(exception={0}) (sql={1})", e, sql),
                    2);
            }
        }
        public static string GetListName(string listname, int department, int company)
        {
            string ret = "";
            decimal listid = 0;
            string sql = String.Format("sp_tcs_get_whitelistbyname ?,?,?");

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("sz_name", OdbcType.VarChar, 50).Value = listname;
                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = department;
                cmd.Parameters.Add("l_comppk", OdbcType.Int).Value = company;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    if (!rs.IsDBNull(0))
                        listid = rs.GetDecimal(0);
                }
                rs.Close();
                conn.Close();

                ret = String.Format("TCS-id{0}-c{1}-d{2}", listid, company, department);
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetWhitelistName (exception={0}) (sql={1})", e.Message, sql),
                    String.Format("Database.GetWhitelistName (exception={0}) (sql={1})", e, sql),
                    2);
            }

            return ret;
        }
    }
}
