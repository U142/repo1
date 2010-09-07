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

        public static int InsertHistCell(int l_refno, int l_operator)
        {
            Database.ExecuteNonQuery(
                String.Format("sp_cb_ins_cellhist {0},{1}", 
                l_refno, 
                l_operator));
            return Constant.OK;
        }
        public static int UpdateHistCell(int l_refno, int l_operator, float l_successpercentage)
        {
            return UpdateHistCell(l_refno, l_operator, l_successpercentage, 0, 0, 0, 0, 0, 0);
        }
        public static int UpdateHistCell(int l_refno, int l_operator, float l_successpercentage, int l_2gtotal, int l_2gok)
        {
            return UpdateHistCell(l_refno, l_operator, l_successpercentage, l_2gtotal, l_2gok, 0, 0, 0, 0);
        }
        public static int UpdateHistCell(int l_refno, int l_operator, float l_successpercentage, int l_2gtotal, int l_2gok, int l_3gtotal, int l_3gok)
        {
            return UpdateHistCell(l_refno, l_operator, l_successpercentage, l_2gtotal, l_2gok, l_3gtotal, l_3gok, 0, 0);
        }
        public static int UpdateHistCell(int l_refno, int l_operator, float l_successpercentage, int l_2gtotal, int l_2gok, int l_3gtotal, int l_3gok, int l_4gtotal, int l_4gok)
        {
            string sz_sql = "exec sp_cb_upd_cellhist ?,?,?,?,?,?,?,?,?";

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_sql, conn);

                cmd.Parameters.Add("refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = l_operator;
                cmd.Parameters.Add("2gtot", OdbcType.Int).Value = l_2gtotal;
                cmd.Parameters.Add("2gok", OdbcType.Int).Value = l_2gok;
                cmd.Parameters.Add("3gtot", OdbcType.Int).Value = l_3gtotal;
                cmd.Parameters.Add("3gok", OdbcType.Int).Value = l_3gok;
                cmd.Parameters.Add("4gtot", OdbcType.Int).Value = l_4gtotal;
                cmd.Parameters.Add("4gok", OdbcType.Int).Value = l_4gok;
                cmd.Parameters.Add("success", OdbcType.Double).Value = (double)l_successpercentage;

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.UpdateHistCell (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.UpdateHistCell (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }
            return Constant.OK;
        }
        public static int GetSendingStatus(Operator op, int l_refno, out bool expired)
        {
            string szQuery;
            int ret = Constant.FAILED;
            expired = false;

            szQuery = "SELECT l_status, l_expires_ts FROM LBASEND WHERE l_refno=? AND l_operator=?";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);

            try
            {
                cmd.Parameters.Add("refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = op.l_operator;

                dbConn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    DateTime ts_expires;
                    string sz_ts = "";

                    if (!rs.IsDBNull(0))
                        ret = rs.GetInt32(0);
                    if (!rs.IsDBNull(1))
                        sz_ts = rs.GetDecimal(1).ToString();

                    ts_expires = DateTime.ParseExact(sz_ts, "yyyyMMddHHmmss", System.Globalization.DateTimeFormatInfo.InvariantInfo);
                    if (DateTime.Compare(ts_expires, DateTime.Now) >= 0) // message has expired
                        expired = true;
                }

                dbConn.Close();

                return ret;
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetSendingStatus (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.GetSendingStatus (exception={0}) (sql={1})", e, szQuery),
                    2);
                return ret;
            }
        }
        public static int SetSendingStatus(Operator op, int l_refno, int l_status)
        {
            string szQuery;
            int ret = 0;

            szQuery = "UPDATE LBASEND SET l_status=? WHERE l_refno=? AND l_operator=?";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);

            try
            {
                cmd.Parameters.Add("status", OdbcType.Int).Value = l_status;
                cmd.Parameters.Add("refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = op.l_operator;

                dbConn.Open();
                ret = cmd.ExecuteNonQuery();

                dbConn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e, szQuery),
                    2);
                return Constant.FAILED;
            }

            return Constant.OK;
        }
        public static int SetSendingStatus(Operator op, int l_refno, int l_status, string sz_jobid, string sz_expires)
        {
            string szQuery;
            int ret = 0;

            szQuery = "UPDATE LBASEND SET l_status=?, sz_jobid=?, l_started_ts=?, l_expires_ts=? WHERE l_refno=? AND l_operator=?";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);

            try
            {
                cmd.Parameters.Add("status", OdbcType.Int).Value = l_status;
                cmd.Parameters.Add("jobid", OdbcType.VarChar).Value = sz_jobid;
                cmd.Parameters.Add("started", OdbcType.Decimal).Value = DateTime.Now.ToString("yyyyMMddHHmmss");
                cmd.Parameters.Add("endtime", OdbcType.Decimal).Value = sz_expires;
                cmd.Parameters.Add("refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = op.l_operator;

                dbConn.Open();
                ret = cmd.ExecuteNonQuery();

                dbConn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e.Message, szQuery),
                    String.Format("Database.SetSendingStatus (exception={0}) (sql={1})", e, szQuery),
                    2);
                return Constant.FAILED;
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
                lRetVal = Constant.FAILED;
            }

            return lRetVal;
        }

        public static int GetHandle(Operator op)
        {
            object handle = Database.ExecuteScalar("sp_cb_gethandle " + op.l_operator);
            if(handle!=null)
                return (int)handle;
            else
                return 0;

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
                return null;
            }

            return ret;
        }
        public static DateTime GetCreateTime(Operator op, int l_refno)
        {
            DateTime ret = new DateTime();
            Decimal ts = 0;
            string szQuery;

            szQuery = String.Format("SELECT l_created_ts FROM LBASEND WHERE l_refno={0} AND l_operator={1}"
                , l_refno
                , op.l_operator);

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rs;

            dbConn.Open();
            rs = cmd.ExecuteReader();

            if (rs.Read())
                if (!rs.IsDBNull(0))
                    ts = rs.GetDecimal(0);

            rs.Close();
            rs.Dispose();
            cmd.Dispose();
            dbConn.Close();
            dbConn.Dispose();

            if (ts.ToString().Length == 14)
            {
                ret = DateTime.ParseExact(ts.ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture);
            }
            else
                throw new Exception("Could not get created timestamp");

            return new DateTime(ret.Ticks, DateTimeKind.Local);
        }
        public static int GetRefno()
        {
            object refno = Database.ExecuteScalar("sp_refno_out");
            if (refno != null)
                return (int)refno;
            else
                return 0;
        }
        public static bool VerifyRefno(int l_refno, int l_operator)
        {
            bool ret = true;
            string sz_query = String.Format("SELECT l_refno FROM LBASEND WHERE l_refno={0} and l_operator={1}", l_refno, l_operator);

            object refno = Database.ExecuteScalar(sz_query);
            if (refno == null)
                return false;
            else if ((int)refno != l_refno)
                return false;

            return ret;
        }

        private static void ExecuteNonQuery(string sz_sql)
        {
            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_sql, conn);

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.ExecuteNonQuery (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.ExecuteNonQuery (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }
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
                conn.Close();
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
