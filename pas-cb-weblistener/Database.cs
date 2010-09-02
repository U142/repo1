using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.Odbc;
using pas_cb_server.tmobile;

namespace pas_cb_weblistener
{
    public class lol
    {
        private static bool _sveinedrid = true;
        public static bool sveinedrid()
        {
            return _sveinedrid;
        }
    }
    public class Database
    {
        public static int GetHandle(int l_operator)
        {
            object handle = Database.ExecuteScalar("sp_cb_gethandle " + l_operator);
            if (handle != null)
                return (int)handle;
            else
                return 0;

        }
        public static int GetRefno(string sz_jobid, int l_operator)
        {
            int ret = 0;
            string sz_sql = "SELECT l_refno FROM LBASEND WHERE sz_jobid=? AND l_operator=?";

            try
            {
                OdbcConnection conn = new OdbcConnection(Settings.sz_dbconn);
                OdbcCommand cmd = new OdbcCommand(sz_sql, conn);

                cmd.Parameters.Add("jobid", OdbcType.VarChar).Value = sz_jobid;
                cmd.Parameters.Add("operator", OdbcType.Int).Value = l_operator;

                conn.Open();
                
                object refno = cmd.ExecuteScalar();
                if (refno != null)
                    ret = (int)refno;
                
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Database.GetRefno (exception={0}) (sql={1})", e.Message, sz_sql),
                    String.Format("Database.GetRefno (exception={0}) (sql={1})", e, sz_sql),
                    2);
            }

            return ret;
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
