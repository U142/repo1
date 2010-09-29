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

                //for (int i = 0; i < Settings.l_statuspollinterval; i++)
                //{
                    Thread.Sleep(1000);
//                    if (!CBServer.running)
//                        break;
                //}
            }
            Log.WriteLog("Stopped status thread", 9);
            Interlocked.Decrement(ref Settings.threads);
        }

        public static void CheckStatus()
        {
            OdbcConnection conn = new OdbcConnection();
            OdbcCommand cmd = new OdbcCommand();

            // first status after 1 minute
            string sql1 = string.Format(@"SELECT 
	                LS.l_refno, LS.l_status, LS.sz_jobid, LS.l_operator, LS.l_expires_ts, 0 b_report
                FROM 
	                LBASEND LS 
	                INNER JOIN LBAHISTCELL HC ON LS.l_refno=HC.l_refno AND LS.l_operator=HC.l_operator
                where 
	                LS.l_status in ({0},{1},{2},{3},{4},{5})
	                AND LS.l_started_ts<=CONVERT(NUMERIC,(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),112)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),1,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),4,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),7,2)
                    )) 
                    AND HC.l_timestamp=-1
                    AND LS.l_started_ts is not null"
               , Constant.CBPREPARING
               , Constant.CBQUEUED
               , Constant.CBACTIVE
               , Constant.CBPAUSED
               , Constant.CANCELLING
               , Constant.CBUSERCANCELLED
               , -60);
            
            // status during intervalls
            string sql2 = string.Format(@"SELECT 
	                LS.l_refno, LS.l_status, LS.sz_jobid, LS.l_operator, LS.l_expires_ts, 0 b_report
                FROM 
	                LBASEND LS 
	                INNER JOIN LBAHISTCELL HC ON LS.l_refno=HC.l_refno AND LS.l_operator=HC.l_operator
                where 
	                LS.l_status in ({0},{1},{2},{3},{4},{5})
	                AND HC.l_timestamp<=CONVERT(NUMERIC,(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),112)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),1,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),4,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),7,2)
                    )) 
	                AND LS.l_started_ts is not null
                    AND HC.l_timestamp<>-1"
               , Constant.CBPREPARING
               , Constant.CBQUEUED
               , Constant.CBACTIVE
               , Constant.CBPAUSED
               , Constant.CANCELLING
               , Constant.CBUSERCANCELLED
               , -(Settings.l_statuspollinterval));
            
            // 3 minute status
            string sql3 = string.Format(@"SELECT 
                    LS.l_refno, LS.l_status, LS.sz_jobid, LS.l_operator, LS.l_expires_ts, 1 b_report
                FROM 
                    LBASEND LS 
                    INNER JOIN LBAHISTCELL_REPORT HR ON LS.l_refno=HR.l_refno AND LS.l_operator=HR.l_operator
                where 
                    LS.l_status >= {0}
                    AND LS.l_started_ts<=CONVERT(NUMERIC,(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),112)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),1,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),4,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),DATEADD(SS, {6}, GETDATE()),108),7,2)
                    )) 
                    AND HR.l_timestamp=-1
                    AND LS.l_started_ts is not null"
               , Constant.CBPREPARING
               , Constant.CBQUEUED
               , Constant.CBACTIVE
               , Constant.CBPAUSED
               , Constant.CANCELLING
               , Constant.CBUSERCANCELLED
               , -180);

            // expired status (final status check after sending is finished)
            string sql4 = string.Format(@"SELECT 
                    LS.l_refno, LS.l_status, LS.sz_jobid, LS.l_operator, LS.l_expires_ts, 0 b_report
                FROM 
                    LBASEND LS 
	                INNER JOIN LBAHISTCELL HC ON LS.l_refno=HC.l_refno AND LS.l_operator=HC.l_operator
                where 
                    LS.l_status in ({0},{1},{2},{3},{4},{5})
                    AND LS.l_expires_ts<=CONVERT(NUMERIC,(CONVERT(VARCHAR(10),GETDATE(),112)
                        +SUBSTRING(CONVERT(VARCHAR(10),GETDATE(),108),1,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),GETDATE(),108),4,2)
                        +SUBSTRING(CONVERT(VARCHAR(10),GETDATE(),108),7,2)
                    )) 
                    AND LS.l_expires_ts is not null"
               , Constant.CBPREPARING
               , Constant.CBQUEUED
               , Constant.CBACTIVE
               , Constant.CBPAUSED
               , Constant.CANCELLING
               , Constant.CBUSERCANCELLED);

            /*string sql = String.Format("SELECT l_refno, l_status, sz_jobid, l_operator, l_expires_ts FROM LBASEND where l_status in ({0},{1},{2},{3},{4},{5})"
               , Constant.CBPREPARING
               , Constant.CBQUEUED
               , Constant.CBACTIVE
               , Constant.CBPAUSED
               , Constant.CANCELLING
               , Constant.CBUSERCANCELLED);*/
            string sql = "";
            if (Settings.l_statuspollinterval > 0)
            {
                sql = string.Format("{0} union {1} union {2} union {3} order by LS.l_refno, LS.l_operator", sql1, sql2, sql3, sql4);
            }
            else
            {
                sql = string.Format("{0} union {1} union {2} order by LS.l_refno, LS.l_operator", sql1, sql3, sql4);
            }

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
                    bool b_report = rs_alerts.GetBoolean(5);

                    switch (op.l_type)
                    {
                        case 1: // AlertiX (not supported)
                            Log.WriteLog(String.Format("{0} Status for AlertiX messages not supported (op={1}, job={2})", l_refno, op.sz_operatorname, sz_jobid), 2);
                            break;
                        case 2: // one2many 
                            CB_one2many.GetAlertStatus(l_refno, l_status, int.Parse(sz_jobid), op, l_expires_ts, b_report);
                            break;
                        case 3: // tmobile
                            CB_tmobile.GetAlertStatus(l_refno, l_status, Tools.GetBytes(int.Parse(sz_jobid)), op, l_expires_ts, b_report);
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
