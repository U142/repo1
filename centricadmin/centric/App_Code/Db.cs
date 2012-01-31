using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using System.Data.Odbc;
using System.Collections.Generic;

/// <summary>
/// Summary description for Db
/// </summary>
public class Db
{
    private OdbcConnection oSQLConn;
    private OdbcCommand oc;

    public Db()
	{
		
	}
    public OdbcConnection getConnection()
    {
        OdbcConnection oSQLConn;
        /*
        try
        {*/
        //oSQLConn = new OdbcConnection("DSN=" + Common.DATABASE.sz_dsn + ";" + "UID=" + Common.DATABASE.sz_uid + ";PWD=" + Common.DATABASE.sz_pwd + ";");            
        // Brukt lang nok tid på dette, hardkoder    
        oSQLConn = new OdbcConnection("DSN=" + ConfigurationManager.AppSettings["sz_db_dsn"] +
                                     ";UID=" + ConfigurationManager.AppSettings["sz_db_uid"] +
                                     ";PWD=" + ConfigurationManager.AppSettings["sz_db_pwd"]);

        return oSQLConn;
        /*}
        catch (Exception e)
        {
            return null;
        }*/
    }
    public List<PredefinedText> getPredefinedText()
    {
        oSQLConn = getConnection();
        oSQLConn.Open();

        oc = new OdbcCommand();
        oc.Connection = oSQLConn;
        oc.CommandText = "SELECT mc.sz_text, m.l_messagepk, m.sz_name, m.l_parentpk FROM BBMESSAGES m,BBMESSAGE_SMSCONTENT mc where m.l_messagepk=mc.l_messagepk AND l_deptpk=?";
        oc.CommandType = CommandType.Text;
        oc.Parameters.Add("@deptpk", OdbcType.Int).Value = 1;
        OdbcDataReader dr = oc.ExecuteReader();

        List<PredefinedText> pdt = new List<PredefinedText>();

        while (dr.Read())
        {
            pdt.Add(new PredefinedText(dr.GetString(0), dr.GetInt32(1), dr.GetString(2), dr.GetInt32(3)));
        }
        dr.Close();
        dr.Dispose();
        oSQLConn.Close();
        oSQLConn.Dispose();
        
        return pdt;
    }

    public List<PredefinedArea> getPredefinedArea()
    {
        oSQLConn = getConnection();
        oSQLConn.Open();

        oc = new OdbcCommand();
        oc.Connection = oSQLConn;
        oc.CommandText = "SELECT mc.sz_text, m.l_messagepk, m.sz_name, m.l_parentpk FROM BBMESSAGES m, BBMESSAGE_SMSCONTENT mc where m.l_messagepk=mc.l_messagepk AND l_deptpk=?";
        oc.CommandType = CommandType.Text;
        oc.Parameters.Add("@deptpk", OdbcType.Int).Value = 1;
        OdbcDataReader dr = oc.ExecuteReader();

        List<PredefinedArea> pda = new List<PredefinedArea>();

        while (dr.Read())
        {
            pda.Add(new PredefinedArea(dr.GetString(0), dr.GetInt32(3), (int)dr.GetDecimal(1)));
        }
        dr.Close();
        dr.Dispose();
        oSQLConn.Close();
        oSQLConn.Dispose();

        return pda;
    }

    public int addPredefinedText(PredefinedText pdt)
    {
        /*oSQLConn = getConnection();
        oSQLConn.Open();

        oc = new OdbcCommand();
        oc.Connection = oSQLConn;
        oc.CommandText = "sp_ins_messages ?,?,?,?,?,?,?,?,?,?";
        "@l_deptpk int, @l_type smallint, @sz_name varchar(50), @sz_description varchar(255), @l_langpk numeric(18,0), @f_template tinyint, @sz_filename varchar(255), " +
        "@l_ivrcode int, @l_categorypk numeric(18,0),  @l_parentpk numeric(18,0), @messagepk numeric(18,0)=-1";
        oc.CommandType = CommandType.StoredProcedure;
        oc.Parameters.Add("@deptpk", OdbcType.Int).Value = 1;
         */

        Random random = new Random();

        return random.Next(1000, 99999);

    }

    public int addPredefinedArea(PredefinedArea pda)
    {
        /*oSQLConn = getConnection();
        oSQLConn.Open();

        oc = new OdbcCommand();
        oc.Connection = oSQLConn;
        oc.CommandText = "sp_ins_messages ?,?,?,?,?,?,?,?,?,?";
        "@l_deptpk int, @l_type smallint, @sz_name varchar(50), @sz_description varchar(255), @l_langpk numeric(18,0), @f_template tinyint, @sz_filename varchar(255), " +
        "@l_ivrcode int, @l_categorypk numeric(18,0),  @l_parentpk numeric(18,0), @messagepk numeric(18,0)=-1";
        oc.CommandType = CommandType.StoredProcedure;
        oc.Parameters.Add("@deptpk", OdbcType.Int).Value = 1;
         */

        Random random = new Random();

        return random.Next(1000, 99999);

    }
}
