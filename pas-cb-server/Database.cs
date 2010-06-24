using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;

namespace pas_cb_server
{
    public class Database
    {
        /*// 7 = CB, 6 = TCS, 5 = TAS, 4 = PAS
        private static int MESSAGETYPEfield = umssettings._settings.GetValue("MessageType", 7);
        public static int MESSAGETYPE
        {
            get
            {
                return MESSAGETYPEfield;
            }
        }*/

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

        public static int SetSendingStatus(Operator op, int l_refno, int l_status)
        {
            string szQuery;

            szQuery = "UPDATE LBASEND SET l_status=? WHERE l_refno=? AND l_operator=?";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);

            try
            {
                cmd.Parameters.Add("status", OdbcType.Int).Value = l_status;
                cmd.Parameters.Add("refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = op.l_operator;

                dbConn.Open();
                cmd.ExecuteNonQuery();

                dbConn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return Constant.OK;
        }
        public static int SetSendingStatus(Operator op, int l_refno, int l_status, string sz_jobid)
        {
            string szQuery;

            szQuery = "UPDATE LBASEND SET l_status=?, sz_jobid=? WHERE l_refno=? AND l_operator=?";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);

            try
            {
                cmd.Parameters.Add("status", OdbcType.Int).Value = l_status;
                cmd.Parameters.Add("jobid", OdbcType.VarChar).Value = sz_jobid;
                cmd.Parameters.Add("refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = op.l_operator;

                dbConn.Open();
                cmd.ExecuteNonQuery();

                dbConn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return Constant.OK;
        }
        public static int UpdateTries(int lReference, int lTempStatus, int lEndStatus, int lResponse, int lOperator, LBATYPE Type)
        {
            //int lMaxTries = 1; // Is really retries so 4 will give 5 tries total
            /*int lRetries = Settings.GetValue("Retries", 2);*/
            int lRetVal = 0;
            string szQuery = "";

            if (Type == LBATYPE.LBAS)
            {
                szQuery = "sp_lba_upd_sendtries " + lReference.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + Settings.l_retries.ToString() + ", " + lResponse.ToString() + ", " + lOperator.ToString();
            }
            else if (Type == LBATYPE.TAS)
            {
                szQuery = "sp_tas_upd_sendtries " + lReference.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + Settings.l_retries.ToString() + ", " + lResponse.ToString() + ", " + lOperator.ToString();
            }
            else if (Type == LBATYPE.CB)
            {
                szQuery = "sp_lba_upd_sendtries " + lReference.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + Settings.l_retries.ToString() + " , " + lResponse.ToString() + ", " + lOperator.ToString();
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

        public static int GetHandle(Operator op)
        {
            return (int)Database.ExecuteScalar("sp_cb_gethandle " + op.l_operator);
        }
        public static string GetJobID(Operator op, int l_refno)
        {
            string ret = "";
            string szQuery;

            szQuery = String.Format("SELECT sz_jobid FROM LBASEND WHERE l_refno={0} AND l_operator={1}"
                , l_refno
                , op.l_operator);

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
                    String.Format("Database.GetJobID (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetJobID (exception={0}) (sql={1})", e, szQuery),
                    2);
            }

            return ret;
        }
        public static int GetRefno()
        {
            return (int)Database.ExecuteScalar("sp_refno_out");
        }

        private static object ExecuteScalar(string sz_query)
        {
            object ret = null;

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_query, conn);

                conn.Open();
                ret = cmd.ExecuteScalar();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.ExecuteScalar (exception={0}) (sql={1})", e.Message, sz_query),
                    String.Format("Database.ExecuteScalar (exception={0}) (sql={1})", e, sz_query),
                    2);
            }

            return ret;
        }
    }
}
