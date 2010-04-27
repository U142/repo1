using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using umssettings;
using System.Data.Odbc;
using System.Xml;

namespace pas_cb_server
{
    public class Settings : _settings
    {
        // General (global settings used in the app)
        public static string sz_parsepath;
        public static string sz_dbconn;

        // Instanced settings
        public int l_comppk = 0;
        public int l_deptpk = 0;
        public long l_userpk = 0;

        public string sz_compid = "";
        public string sz_deptid = "";
        public string sz_userid = "";
        public string sz_password = "";

        public Operator[] operators;

        // methods
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
                else if (uv.l_userpk != 0)
                    uv.sz_userid = Database.GetUserID(uv.l_userpk);

                uv.operators = Operator.GetOperators(uv);
            }
            catch (Exception e)
            {
                Log.WriteLog("Error reading user values. " + e.ToString(), "Error reading user values. " + e.Message, 2);
                return false;
            }

            return bRetval;
        }
    }

    public class Operator
    {
        // operator settings
        public int l_operator = 0;
        public string sz_operatorname = "";
        public int l_type = 0; // one2many, tmobile

        // login settings for interaface
        public string sz_url = "";
        public int l_port = 0;
        public string sz_login_id = "";
        public string sz_login_name = "";
        public string sz_login_password = "";

        // methods
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
                    op.sz_login_id = rsOperators.GetString(3);
                    op.sz_login_password = rsOperators.GetString(4);
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
                    op.sz_login_id = rsOperators.GetString(3);
                    op.sz_login_password = rsOperators.GetString(4);

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
                    oRet.sz_login_id = rsOperator.GetString(3);
                    oRet.sz_login_password = rsOperator.GetString(4);
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
}
