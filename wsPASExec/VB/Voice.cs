using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using System.Data.Odbc;
using System.Data;
using System.IO;
using System.Text;

using libums2_csharp;

namespace com.ums.ws.voice
{
    public class SendVoice
    {
        public SendVoice()
        {
        }
        public SendVoice(SendVoice p)
        {
            this.conn = p.conn;
            this.cmd = p.cmd;
        }

        private OdbcConnection conn;
        private OdbcCommand cmd;
        private string ret = "ting ";

        public Int64 send(ACCOUNT acc, PARAMETERS param, string[] to, string from, VOCFILE[] message, Int64 actionprofilepk)
        {
            SendVoice voice = new SendVoice();
            return voice.send(acc, param, to, from, message, actionprofilepk);
            
            /*
            string sz_sender = from;//"23000050";

            string[] filename;
            Int64 l_refno;
            
            try
            {
                OdbcConnection conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                // Here I have to check the account info
                //if (!checkLogon(acc))
                //    throw new Exception("Could not logon");
                ret = ret + "logon ";

                // Get refno
                l_refno = getRefno();
                if (l_refno == -1)
                    throw new Exception("Error getting refno");
                
                ret = ret + "refno ";
                
                libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
                Int64 l_ting = voice.send(acc, to, from, message, actionprofilepk);

                //UAudio audio = new UAudio();             
                //filename =  audio.generateVoice(message, l_refno, ref cmd, eat, szTTSServerPath);
                //ret = ret + filename[0] + " generate voice ";
                return ret;
                // set options
                //BBDYNARESCHED dynre = new BBDYNARESCHED();

                ret += "RESCHED ";
                if (insertBBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) == -1)
                    throw new Exception("Error inserting dynamic reschedule");
                ret += "VALID ";
                if (insertBBVALID(l_refno) == -1)
                    throw new Exception("Error inserting valid");
                ret += "SENDNUM ";
                if (insertBBSENDNUM(l_refno, sz_sender) == -1)
                    throw new Exception("Error inserting sendnum");
                ret += "ACTIONPROFILE ";
                if (insertBBACTIONPROFILESEND(l_refno, actionprofilepk) == -1)
                    throw new Exception("Error inserting action profile");

                cmd.Dispose();
                conn.Close();

                StreamWriter sw = File.CreateText(eat + "d" + l_refno + ".tmp");
                sw.WriteLine("/MDV");
                sw.WriteLine("/Company=" + acc.sz_compid);
                sw.WriteLine("/Department=" + acc.sz_deptid); // Her må jeg ha noe annet?
                sw.WriteLine("/Pri=" + l_pri);
                sw.WriteLine("/SchedDate=" + DateTime.Now.ToString("yyyyMMdd"));
                sw.WriteLine("/SchedTime=" + DateTime.Now.ToString("HHmm"));
                sw.WriteLine("/Item=" + l_item); // Hvordan bestemmes denne?
                sw.WriteLine("/Name=" + sz_sending_name);
                for (int i = 0; i < filename.Length; ++i)
                    sw.WriteLine("/File=v" + l_refno + "_" + i + ".raw");
                for( int i=0;i < to.Length; ++i)
                    sw.WriteLine("/DCALL NA " + to[i]); //DCALL || SIMU
                //sw.WriteLine("/SIMU NA " + recipient); //DCALL || SIMU
                sw.Close();
                File.Move(eat + "d" + l_refno + ".tmp", eat + "d" + l_refno + ".adr");

                return l_refno.ToString() + " " + ret + 
                    System.IO.Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);
            }
            catch (Exception e)
            {
                //throw e;
                ums.UmsCommon.ULog.write(e.StackTrace);
                return ret + " \n" + e.StackTrace;
            }
             */

        }

        private Int64 getRefno()
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.StoredProcedure;
            cmd.CommandText = "sp_refno_out";

            OdbcDataReader dr = cmd.ExecuteReader();
            Int64 l_refno = -1;
            while (dr.Read())
                l_refno = Int64.Parse(dr[0].ToString());

            dr.Close();
            return l_refno;
        }

        private int insertBBDYNARESCHED(Int64 l_refno, Int64 l_retries, Int64 l_interval, Int64 l_canceltime, Int64 l_canceldate,
            Int64 l_pausetime, Int64 l_pauseinterval)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                     "VALUES(?, ?, ?, ?, ?, ?, ?)";
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            cmd.Parameters.Add("@l_retries", OdbcType.TinyInt).Value = l_retries;
            cmd.Parameters.Add("@l_interval", OdbcType.SmallInt).Value = l_interval;
            cmd.Parameters.Add("@l_canceltime", OdbcType.SmallInt).Value = l_canceltime;
            cmd.Parameters.Add("@l_canceldate", OdbcType.Int).Value = l_canceldate;
            cmd.Parameters.Add("@l_pausetime", OdbcType.SmallInt).Value = l_pausetime;
            cmd.Parameters.Add("@l_pauseinterval", OdbcType.Int).Value = l_pauseinterval;
            int tmp = cmd.ExecuteNonQuery();
            ret += tmp.ToString() + " ";
            return tmp;
            //return cmd.ExecuteNonQuery();
        }

        /*private int insertBBVALID(Int64 l_refno)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBVALID(l_refno, l_valid) VALUES(?, ?)";
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            cmd.Parameters.Add("@l_valid", OdbcType.Int).Value = l_valid;
            int tmp = cmd.ExecuteNonQuery();
            ret += tmp.ToString() + " ";
            return tmp;
            //return cmd.ExecuteNonQuery();
        }*/

        private int insertBBSENDNUM(Int64 l_refno, string sz_sender)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBSENDNUM(l_refno, sz_number) VALUES(?, ?)";
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_sender;
            int tmp = cmd.ExecuteNonQuery();
            ret += tmp.ToString() + " ";
            return tmp;
            //return cmd.ExecuteNonQuery();
        }

        private int insertBBACTIONPROFILESEND(Int64 l_refno, Int64 l_actionprofilepk)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBACTIONPROFILESEND(l_actionprofilepk, l_refno) VALUES(?, ?)";
            cmd.Parameters.Add("@l_actionprofilepk", OdbcType.Int).Value = l_actionprofilepk;
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            int tmp = cmd.ExecuteNonQuery();
            ret += tmp.ToString() + " ";
            return tmp;
            //return cmd.ExecuteNonQuery();
        }

        private bool checkLogon(ACCOUNT acc)
        {
            bool ret = false;

            try
            {
                /*
                string szSQL = String.Format("SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc " +
                                    "FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD " +
                                   "WHERE BU.l_userpk={0} " +
                                     "AND BC.l_comppk={1} " +
                                     "AND BD.l_deptpk={2} " +
                                     "AND BU.l_comppk=BC.l_comppk " +
                                     "AND BC.l_comppk=BD.l_comppk " +
                                     "AND BU.sz_password={3}", acc.l_userpk, acc.l_comppk, acc.l_deptpk, acc.sz_password);
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = szSQL;
                OdbcDataReader dr = cmd.ExecuteReader();
                 */

                cmd.Parameters.Clear();
                cmd.CommandText = "SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc " +
                                    "FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD " +
                                   "WHERE BU.l_userpk=? " +
                                     "AND BC.l_comppk=? " +
                                     "AND BD.l_deptpk=? " +
                                     "AND BU.l_comppk=BC.l_comppk " +
                                     "AND BC.l_comppk=BD.l_comppk " + 
                                     "AND BU.sz_password=?";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.Add("@l_userpk", OdbcType.Int).Value = acc.l_userpk; // This should be BigInt, but is not supported by ayanami
                cmd.Parameters.Add("@l_comppk", OdbcType.Int).Value = acc.l_comppk;
                cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = acc.l_deptpk;
                cmd.Parameters.Add("@sz_password", OdbcType.VarChar, 20).Value = acc.sz_password;
                OdbcDataReader dr = cmd.ExecuteReader();

                if (dr.Read())
                {
                    ret = true;
                }

                dr.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return ret;
        }

        
    }
}
