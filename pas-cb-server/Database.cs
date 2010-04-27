using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;

namespace pas_cb_server
{
    public class Database
    {
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
    }
}
