using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Threading;
using System.Net;
using System.Data.Odbc;
using UMSAlertiX.AlertiX3;

namespace UMSAlertiX
{
    public class UMSAlertiXStatus
    {
        UMSAlertiXController oController;

        public void SetController(ref UMSAlertiXController objController)
        {
            oController = objController;
        }

        public void UpdateJobStatus() // checks status of jobs
        {
            string szJobQuery = "SELECT s.l_refno, s.sz_jobid, s.l_operator, s.l_status FROM LBASEND s INNER JOIN LBAOPERATORS o ON o.l_operator=s.l_operator AND o.sz_version='3.0' WHERE s.l_status=300 OR s.l_status=305 OR (s.l_status=310 AND s.l_items=0)";

            OdbcConnection dbConn = new OdbcConnection(oController.dsn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            AlertixWsApiService aStatus = new AlertixWsApiService();
            AlertInfo aJobResponse = new AlertInfo();
            string idJob;

            int lRetVal;
            int lRefNo;
            int lOperator;
            int lStatus;

            dbConn.Open();
            while (oController.running)
            {
                rsJobs = cmdJobs.ExecuteReader();
                while (rsJobs.Read() && oController.running)
                {
                    if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2))|| rsJobs.IsDBNull(3)) // required values missing from LBASEND
                    {
                        lRefNo = rsJobs.GetInt32(0);
                        idJob = rsJobs.GetString(1);
                        lOperator = rsJobs.GetInt32(2);
                        lStatus = rsJobs.GetInt32(3);
                        //Console.WriteLine("Checking refno={0} job={1} operator={2} status={3}", lRefNo.ToString(), idJob.value, lOperator.ToString(), lStatus.ToString());
                        
                        Operator op = oController.GetOperator(lOperator);

                        aStatus.Url = op.sz_url + oController.statusapi;

                        NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                        Uri uri = new Uri(aStatus.Url);

                        ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                        aStatus.Credentials = objAuth;
                        aStatus.PreAuthenticate = true;

                        try
                        {
                            aJobResponse = aStatus.getAlertInfo(idJob);
                        }
                        catch (Exception e)
                        {
                            oController.log.WriteLog(e.ToString(), e.Message);
                        }

                        if (aJobResponse.resultSpecified)// && aJobResponse.jobStatusSpecified)
                        {
                            if (aJobResponse.result == Result.SUCCESS)
                            {
                                switch (aJobResponse.alertDetails.state)
                                {
                                    case AlertState.NO_SUBSCRIBERS:
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status NO_SUBSCRIBERS");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case AlertState.SCHEDULED:
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status PREPARED (" + aJobResponse.alertDetails.subscriberCount.ToString() + " items)");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + aJobResponse.alertDetails.subscriberCount.ToString() + ", l_proc=0, l_status=310 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case AlertState.SUBMITTING:
                                    case AlertState.COMPLETED:
                                    case AlertState.SUBMITTED:
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status SENDING (" + aJobResponse.alertDetails.subscriberCount.ToString() + " items)");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + aJobResponse.alertDetails.subscriberCount.ToString() + ", l_proc=0, l_status=340 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case AlertState.ERROR:
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status ERROR");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_status=42003 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case AlertState.PREPARING:
                                        break;
                                    default:
                                        break;
                                }
                            }
                            else
                            {
                                oController.log.WriteLog(aJobResponse.result.ToString() + " - " + idJob + " - " + aJobResponse.description);
                            }
                        }
                        else
                        {
                            Console.WriteLine("jobResponse.result not specified");
                        }
                    }
                }
                rsJobs.Close();
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!oController.running)
                        break;
                }
            }
            dbConn.Close();
            oController.threads--;
            Console.WriteLine("# Stopped Job Status thread", 1);
        }

        public void UpdateCCStatus() // updates jobs with country code status
        {
            string szJobQuery = "SELECT s.l_refno, s.sz_jobid, s.l_operator, s.l_expires_ts FROM LBASEND s INNER JOIN LBAOPERATORS o ON o.l_operator=s.l_operator AND o.sz_version='3.0' where s.l_status=340 or s.l_status=310";

            OdbcConnection dbConn = new OdbcConnection(oController.dsn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            string idJob;

            int lRefNo;
            int lOperator;
            dbConn.Open();

            while (oController.running)
            {
                rsJobs = cmdJobs.ExecuteReader();
                while (rsJobs.Read() && oController.running)
                {
                    if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2)))
                    {
                        DateTime expires_ts = new DateTime();
                        bool bExpired = false;

                        lRefNo = rsJobs.GetInt32(0);
                        idJob = rsJobs.GetString(1);
                        lOperator = rsJobs.GetInt32(2);
                        if (!rsJobs.IsDBNull(3))
                            if (DateTime.TryParseExact(rsJobs.GetDecimal(3).ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out expires_ts))
                                if (expires_ts < DateTime.Now)
                                    bExpired = true;

                        //Console.WriteLine("Checking status for refno={0} jobid={1} operator={2}", lRefNo.ToString(), idJob.value, lOperator.ToString());

                        UpdateCCStatus(idJob, lRefNo, lOperator, bExpired);
                    }
                }
                rsJobs.Close();
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!oController.running)
                        break;
                }
            }
            dbConn.Close();
            oController.threads--;
            Console.WriteLine("# Stopped CC Status thread", 1);
        }

        private bool UpdateCCStatus(string idJob, int lRefNo, int lOperator)
        {
            return UpdateCCStatus(idJob, lRefNo, lOperator, false);
        }

        private bool UpdateCCStatus(string idJob, int lRefNo, int lOperator, bool bExpired)
        {
            AlertixWsApiService aStatus = new AlertixWsApiService();
            AlertStatusByCountry aStatusResponse = new AlertStatusByCountry();
            Operator op = oController.GetOperator(lOperator);

//            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            aStatus.Url = op.sz_url + oController.statusapi;

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aStatus.Url);

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aStatus.Credentials = objAuth;
            aStatus.PreAuthenticate = true;

            bool bReturn = true;

            int lRetVal;
            int cc;
            
            int ccDelivered;
            int ccExpired;
            int ccFailed;
            int ccUnknown;

            int ccSubmitted;
            int ccQueued;
            
            int ccSubscribers;
            
            int lItems = 0;
            int lProc = 0;
            int lCountSub = 0;
            int lStatus = 0;

            int lPrevProc = 0;

            try
            {
                oController.GetSendingProc(lRefNo, lOperator, ref lItems, ref lPrevProc, ref lStatus);
                aStatusResponse = aStatus.getAlertStatusByCountry(idJob);
            }
            catch (Exception e)
            {
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            if (aStatusResponse == null)
            {
                // getAlertStatusByCountryCode didn't fail, but returned NULL, update database with something
                oController.log.WriteLog("jobid: " + idJob + " statusResponse is NULL (status=" + lStatus.ToString() + ")");
            }
            else if (aStatusResponse.countryDeliveryStatuses != null)
            {
                foreach (CountryDeliveryStatus ccStatus in aStatusResponse.countryDeliveryStatuses)
                {
                    cc = oController.getCCFromISO(ccStatus.country);

                    ccDelivered = ccStatus.deliveryStatus.deliveredSuccessfully;
                    ccExpired = ccStatus.deliveryStatus.deliveryExpired;
                    ccFailed = ccStatus.deliveryStatus.deliveryFailed + ccStatus.deliveryStatus.failedSubmission;
                    ccUnknown = ccStatus.deliveryStatus.deliveryUnknown;

                    if (bExpired)
                    {
                        ccExpired += ccStatus.deliveryStatus.submitted;
                        ccExpired += ccStatus.deliveryStatus.processing;
                        ccSubmitted = 0;
                        ccQueued = 0;
                    }
                    else
                    {
                        ccSubmitted = ccStatus.deliveryStatus.submitted;
                        ccQueued = ccStatus.deliveryStatus.processing;
                    }
                    
                    ccSubscribers = ccStatus.deliveryStatus.totalCount;
                    lCountSub += ccSubscribers;
                    lProc += ccDelivered + ccExpired + ccFailed + ccUnknown;

                    lRetVal = oController.ExecDB("sp_upd_status_lba_cc " + lRefNo.ToString() + ", " + lOperator.ToString() + ", " + cc.ToString() + ", " + ccDelivered.ToString() + ", " + ccExpired.ToString() + ", " + ccFailed.ToString() + ", " + ccUnknown.ToString() + ", " + ccSubmitted.ToString() + ", " + ccQueued.ToString() + ", " + ccSubscribers.ToString(), oController.dsn);
                }

                if (lProc == lItems && lStatus == 340 && lItems > 0) // done sending
                {
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status done (" + lItems.ToString() + " recipients)");
                    lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + lItems.ToString() + ", l_proc=" + lProc.ToString() + ", l_status=1000 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                }
                else if (lItems == lCountSub && lStatus == 310 && lItems > 0) // got all ccode statuses from prepared sending (skip those with 0 items)
                {
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Got all cc for prepared sending (" + lItems.ToString() + " recipients)");
                    lRetVal = oController.ExecDB("UPDATE LBASEND set l_status=311 WHERE l_refno=" + lRefNo.ToString() + " AND l_status=310 AND l_operator=" + lOperator.ToString(), oController.dsn);
                }
                else // sending in progress
                {
                    if (lItems == 0)
                    {
                        AlertInfo aJobResponse = new AlertInfo();
                        try
                        {
                            aJobResponse = aStatus.getAlertInfo(idJob);
                        }
                        catch (Exception e)
                        {
                            oController.log.WriteLog(e.ToString(), e.Message);
                        }
                        if (aJobResponse.resultSpecified)// && aJobResponse.jobStatusSpecified)
                        {
                            if (aJobResponse.result == Result.SUCCESS)
                            {
                                lItems = aJobResponse.alertDetails.subscriberCount;
                                if (lItems > 0)
                                {
                                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status adjusting items (" + lItems.ToString() + " subscribers)");
                                    lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + lItems.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                }
                            }
                            else
                            {
                                oController.log.WriteLog(aJobResponse.result.ToString() + " - " + idJob + " - " + aJobResponse.description);
                            }
                        }
                        else
                        {
                            Console.WriteLine("jobResponse.result not specified");
                        }
                    }

                    if (lPrevProc < lProc && lProc > 0)
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status update (" + lProc.ToString() + " of " + lItems.ToString() + " delivered)");
                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_proc=" + lProc.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                    }
                }
            }
            else if (aStatusResponse.resultSpecified && (lStatus == 310 || lStatus == 340)) // if status is not 310 or 340 the message has most likely been cancelled by the user, ignore it
            {
                switch (aStatusResponse.result)
                {
                    case Result.NO_SUBSCRIBERS:
                        // set to completed
                        oController.log.WriteLog("jobid: " + idJob + " error (" + aStatusResponse.result.ToString() + ") (" + aStatusResponse.description + ")");
                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000, l_response=" + (int)aStatusResponse.result + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob + "'", oController.dsn);
                        break;
                    default:
                        // set to cancelled
                        oController.log.WriteLog("jobid: " + idJob + " error (" + aStatusResponse.result.ToString() + ") (" + aStatusResponse.description + ")");
                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_status=2000, l_response=" + (int)aStatusResponse.result + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob + "'", oController.dsn);
                        break;
                }
            }
            else
            {
                oController.log.WriteLog("jobid: " + idJob + " no action taken (status=" + lStatus.ToString() + ")");
            }
            return bReturn;
        }
    }
}
