using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Data.Odbc;

namespace pas_cb_server
{
    class CBStatus
    {
        public static void CheckStatusThread()
        {
            while (CBServer.running)
            {
                CheckStatus();

                for (int i = 0; i < Settings.l_statuspollinterval; i++)
                {
                    Thread.Sleep(1000);
                    if (!CBServer.running)
                        break;
                }
            }
            Log.WriteLog("Stopped status thread", 9);
            Interlocked.Decrement(ref Settings.threads);
        }

        public static void CheckStatus()
        {
            OdbcConnection conn = new OdbcConnection();
            OdbcCommand cmd = new OdbcCommand();

            string sql = String.Format("SELECT l_refno, l_status, sz_jobid, l_operator, l_expires_ts FROM LBASEND where l_status in ({0},{1},{2},{3},{4},{5})"
               , Constant.CBPREPARING
               , Constant.CBQUEUED
               , Constant.CBACTIVE
               , Constant.CBPAUSED
               , Constant.CANCELLING
               , Constant.USERCANCELLED);

            try
            {
                conn.ConnectionString = Settings.sz_dbconn;
                cmd.Connection = conn;
                cmd.CommandText = sql;

                conn.Open();
                OdbcDataReader rs_alerts = cmd.ExecuteReader();
                while (rs_alerts.Read())
                {
                    int l_refno = rs_alerts.GetInt32(0);
                    int l_status = rs_alerts.GetInt32(1);
                    string sz_jobid = rs_alerts.GetString(2);
                    Operator op = Operator.Getoperator(rs_alerts.GetInt32(3));
                    decimal l_expires_ts = rs_alerts.GetDecimal(4);

                    switch (op.l_type)
                    {
                        case 1: // AlertiX (not supported)
                            Log.WriteLog(String.Format("{0} Status for AlertiX messages not supported (op={1}, job={2})", l_refno, op.sz_operatorname, sz_jobid), 2);
                            break;
                        case 2: // one2many
                            CB_one2many.GetAlertStatus(l_refno, l_status, int.Parse(sz_jobid), op, l_expires_ts);
                            break;
                        case 3: // tmobile
                            CB_tmobile.GetAlertStatus(l_refno, l_status, BitConverter.GetBytes(int.Parse(sz_jobid)), op, l_expires_ts);
                            break;
                        default:
                            Log.WriteLog(String.Format("{0} Unkown operator type (op={1}, job={2})", l_refno, op.sz_operatorname, sz_jobid), 2);
                            break;
                    }
                }
                rs_alerts.Close();
                conn.Close();
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Statuscheck failed: {0}", e.Message), 
                    String.Format("Statuscheck failed: {0}", e), 
                    2);
            }
        }
    }
}
